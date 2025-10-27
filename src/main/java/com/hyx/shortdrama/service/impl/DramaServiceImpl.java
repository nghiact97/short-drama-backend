package com.hyx.shortdrama.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyx.shortdrama.common.ErrorCode;
import com.hyx.shortdrama.constant.CacheConstant;
import com.hyx.shortdrama.exception.BusinessException;
import com.hyx.shortdrama.mapper.DramaMapper;
import com.hyx.shortdrama.model.dto.drama.DramaQueryRequest;
import com.hyx.shortdrama.model.entity.Drama;
import com.hyx.shortdrama.model.vo.DramaVO;
import com.hyx.shortdrama.service.DramaService;
import com.hyx.shortdrama.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户评论服务实现
 */
@Service
@Slf4j
public class DramaServiceImpl extends ServiceImpl<DramaMapper, Drama> implements DramaService {
    // TODO 该文件为自动生成，在使用前需要修改

    @Resource
    private UserService userService;

    /**
     * 校验数据
     */
    @Override
    public void validDrama(Drama drama, boolean add) {
        if (drama == null) return;
        // 最基本的校验，按需扩展
        if (add) {
            // 标题可选非空策略，这里仅限制长度
        }
        if (drama.getTitle() != null && drama.getTitle().length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Tiêu đề không được vượt quá 80 ký tự");
        }
        if (drama.getDescription() != null && drama.getDescription().length() > 1024) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Mô tả không được vượt quá 1024 ký tự");
        }
        if (drama.getCategory() != null && drama.getCategory().length() > 128) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Danh mục không được vượt quá 128 ký tự");
        }
    }

    /**
     * 获取查询条件
     *
     * @param req
     * @return
     */
    @Override
    public QueryWrapper<Drama> getQueryWrapper(DramaQueryRequest req) {
        QueryWrapper<Drama> qw = new QueryWrapper<>();
        if (req == null) {
            return qw.orderByDesc("orderNum").orderByDesc("id");
        }
        Long id = req.getId();
        Long notId = req.getNotId();
        String title = req.getTitle();
        String searchText = req.getSearchText();
        Long userId = req.getUserId();

        qw.ne(notId != null, "id", notId);
        qw.eq(id != null, "id", id);
        qw.eq(userId != null, "userId", userId);
        qw.like(title != null && !title.isEmpty(), "title", title);

        // 同时搜索标题和描述
        if (searchText != null && !searchText.trim().isEmpty()) {
            qw.and(wrapper -> wrapper
                    .like("title", searchText.trim()))
                    .or()
                    .like("description", searchText.trim());
        }

        // 排序（默认）
        qw.orderByDesc("orderNum").orderByDesc("id");
        return qw;
    }

    /**
     * 获取剧集封装（用于Service层内部调用）
     *
     * @param drama
     * @return
     */
    @Override
    public DramaVO getDramaVO(Drama drama) {
        if (drama == null) {
            return null;
        }
        // 对象转封装类（不包含用户相关状态信息）
        return DramaVO.objToVo(drama);
    }

    /**
     * 获取剧集封装
     *
     * @param drama
     * @param request
     * @return
     */
    @Override
    public DramaVO getDramaVO(Drama drama, HttpServletRequest request) {
        // 对象转封装类
        DramaVO dramaVO = DramaVO.objToVo(drama);

        // TODO: 如果需要，可以在这里添加用户相关的状态信息
        // 比如当前用户是否收藏了这个剧集等

        return dramaVO;
    }

    /**
     * 分页获取剧集封装
     *
     * @param dramaPage
     * @param request
     * @return
     */
    @Override
    public Page<DramaVO> getDramaVOPage(Page<Drama> dramaPage, HttpServletRequest request) {
        List<Drama> dramaList = dramaPage.getRecords();
        Page<DramaVO> dramaVOPage = new Page<>(dramaPage.getCurrent(), dramaPage.getSize(), dramaPage.getTotal());
        if (CollUtil.isEmpty(dramaList)) {
            return dramaVOPage;
        }
        // 对象列表 => 封装对象列表
        List<DramaVO> dramaVOList = dramaList.stream().map(DramaVO::objToVo).collect(Collectors.toList());

        dramaVOPage.setRecords(dramaVOList);
        return dramaVOPage;
    }


    @Override
    @Cacheable(
            cacheNames = CacheConstant.DRAMA_LIST,
            key = "T(com.hyx.shortdrama.utils.CacheKeyBuilders).dramaList(#category,#current,#pageSize)",
            condition = "@cacheSwitch.enabled",
            sync = true
    )
    public Page<DramaVO> listPublic(long current, long pageSize, String category, HttpServletRequest request) {
        QueryWrapper<Drama> qw = new QueryWrapper<Drama>().eq("status", 1);
        if (category != null && !category.isEmpty()) {
            qw.eq("category", category);
        }
        qw.orderByDesc("orderNum").orderByDesc("id");
        Page<Drama> page = this.page(new Page<>(current, pageSize), qw);
        return getDramaVOPage(page, request);
    }

    @Override
    @Cacheable(
            cacheNames = CacheConstant.DRAMA_DETAIL,
            key = "T(com.hyx.shortdrama.utils.CacheKeyBuilders).dramaDetail(#id)",
            condition = "@cacheSwitch.enabled",
            sync = true
    )
    public DramaVO getDramaDetail(long id, HttpServletRequest request) {
        Drama drama = this.getById(id);
        if (drama == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return getDramaVO(drama, request);
    }

    @Override
    @Cacheable(
            cacheNames = CacheConstant.DRAMA_SEARCH,
            key = "T(com.hyx.shortdrama.utils.CacheKeyBuilders).dramaSearch(#searchText,#category,#current,#pageSize)",
            condition = "@cacheSwitch.enabled && #pageSize <= T(com.hyx.shortdrama.constant.CommonConstant).PAGE_SIZE_LIMIT && #searchText != null && #searchText.length() <= 50",
            sync = true
    )
    public Page<DramaVO> search(String searchText, long current, long pageSize, String category, HttpServletRequest request) {
        DramaQueryRequest dramaQueryRequest = new DramaQueryRequest();
        dramaQueryRequest.setSearchText(searchText);
        dramaQueryRequest.setCurrent((int) current);
        dramaQueryRequest.setPageSize((int) pageSize);

        QueryWrapper<Drama> queryWrapper = getQueryWrapper(dramaQueryRequest);
        queryWrapper.eq("status", 1);
        if (category != null && !category.isEmpty()) {
            queryWrapper.eq("category", category);
        }
        Page<Drama> dramaPage = this.page(new Page<>(current, pageSize), queryWrapper);
        return getDramaVOPage(dramaPage, request);
    }

}
