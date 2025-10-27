package com.hyx.shortdrama.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hyx.shortdrama.annotation.AuthCheck;
import com.hyx.shortdrama.common.BaseResponse;
import com.hyx.shortdrama.common.DeleteRequest;
import com.hyx.shortdrama.common.ErrorCode;
import com.hyx.shortdrama.common.ResultUtils;
import com.hyx.shortdrama.constant.CacheConstant;
import com.hyx.shortdrama.constant.CommonConstant;
import com.hyx.shortdrama.constant.UserConstant;
import com.hyx.shortdrama.exception.BusinessException;
import com.hyx.shortdrama.exception.ThrowUtils;
import com.hyx.shortdrama.model.dto.drama.DramaAddRequest;
import com.hyx.shortdrama.model.dto.drama.DramaEditRequest;
import com.hyx.shortdrama.model.dto.drama.DramaQueryRequest;
import com.hyx.shortdrama.model.dto.drama.DramaUpdateRequest;
import com.hyx.shortdrama.model.entity.Drama;
import com.hyx.shortdrama.model.entity.User;
import com.hyx.shortdrama.model.vo.DramaVO;
import com.hyx.shortdrama.model.vo.VideoVO;
import com.hyx.shortdrama.service.DramaService;
import com.hyx.shortdrama.service.UserService;
import com.hyx.shortdrama.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户评论接口
 *
 */
@RestController
@RequestMapping("/drama")
@Slf4j
public class DramaController {
    // TODO 该文件为自动生成，在使用前需要修改

    @Resource
    private DramaService dramaService;

    @Resource
    private UserService userService;

    @Resource
    private VideoService videoService;


    @GetMapping("/list")
    public BaseResponse<Page<DramaVO>> listPublic(@RequestParam(defaultValue = "1") long current,
                                                  @RequestParam(defaultValue = "10") long pageSize,
                                                  @RequestParam(required = false) String category,
                                                  HttpServletRequest request) {
        ThrowUtils.throwIf(pageSize > CommonConstant.PAGE_SIZE_LIMIT, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(dramaService.listPublic(current, pageSize, category, request));
    }

    @GetMapping("/{id}")
    public BaseResponse<DramaVO> getByPath(@PathVariable("id") long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(dramaService.getDramaDetail(id,  request));
    }

    @GetMapping("/{id}/episodes")
    public BaseResponse<List<VideoVO>> listEpisodes(@PathVariable("id") long id,
                                                              HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(videoService.listEpisodes(id,  request));
    }

    /**
     * 搜索剧集
     *
     * @param searchText 搜索关键词
     * @param current 当前页
     * @param pageSize 页面大小
     * @param category 分类筛选
     * @param request HTTP请求
     * @return 搜索结果
     */
    @GetMapping("/search")
    public BaseResponse<Page<DramaVO>> searchDramas(@RequestParam String searchText,
                                                    @RequestParam(defaultValue = "1") long current,
                                                    @RequestParam(defaultValue = "10") long pageSize,
                                                    @RequestParam(required = false) String category,
                                                    HttpServletRequest request){
        ThrowUtils.throwIf(pageSize > CommonConstant.PAGE_SIZE_LIMIT, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(searchText == null || searchText.trim().isEmpty(),
                ErrorCode.PARAMS_ERROR, "Vui lòng nhập từ khóa tìm kiếm");

        return ResultUtils.success(dramaService.search(searchText, current, pageSize, category, request));
    }


    // region 增删改查（自动生成）

    /**
     * 创建
     *
     * @param dramaAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheConstant.DRAMA_LIST, allEntries = true),
            @CacheEvict(cacheNames = CacheConstant.DRAMA_SEARCH, allEntries = true)
    })
    public BaseResponse<Long> addDrama(@RequestBody DramaAddRequest dramaAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(dramaAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        Drama drama = new Drama();
        BeanUtils.copyProperties(dramaAddRequest, drama);
        // 数据校验
        dramaService.validDrama(drama, true);
        // todo 填充默认值
        User loginUser = userService.getLoginUser(request);
        drama.setUserId(loginUser.getId());
        if (drama.getStatus() == null)drama.setStatus(1);
        if (drama.getOrderNum() == null)drama.setOrderNum(0);
        if (drama.getUserId() == null)drama.setUserId(0L);
        // 写入数据库
        boolean result = dramaService.save(drama);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newDramaId = drama.getId();
        return ResultUtils.success(newDramaId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheConstant.DRAMA_DETAIL, key = "#deleteRequest.id", condition = "#deleteRequest != null && #deleteRequest.id > 0"),
            @CacheEvict(cacheNames = CacheConstant.VIDEO_EPISODES, key = "#deleteRequest.id", condition = "#deleteRequest != null && #deleteRequest.id > 0"),
            @CacheEvict(cacheNames = CacheConstant.DRAMA_LIST, allEntries = true),
            @CacheEvict(cacheNames = CacheConstant.DRAMA_SEARCH, allEntries = true)
    })
    public BaseResponse<Boolean> deleteDrama(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Drama oldDrama = dramaService.getById(id);
        ThrowUtils.throwIf(oldDrama == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldDrama.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = dramaService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新（仅管理员可用）
     *
     * @param dramaUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheConstant.DRAMA_DETAIL,   key = "#dramaUpdateRequest.id", condition = "#dramaUpdateRequest != null && #dramaUpdateRequest.id > 0"),
            @CacheEvict(cacheNames = CacheConstant.VIDEO_EPISODES, key = "#dramaUpdateRequest.id", condition = "#dramaUpdateRequest != null && #dramaUpdateRequest.id > 0"),
            @CacheEvict(cacheNames = CacheConstant.DRAMA_LIST,     allEntries = true),
            @CacheEvict(cacheNames = CacheConstant.DRAMA_SEARCH,   allEntries = true)
    })
    public BaseResponse<Boolean> updateDrama(@RequestBody DramaUpdateRequest dramaUpdateRequest) {
        if (dramaUpdateRequest == null || dramaUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        Drama drama = new Drama();
        BeanUtils.copyProperties(dramaUpdateRequest, drama);
        // 数据校验
        dramaService.validDrama(drama, false);
        // 判断是否存在
        long id = dramaUpdateRequest.getId();
        Drama oldDrama = dramaService.getById(id);
        ThrowUtils.throwIf(oldDrama == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = dramaService.updateById(drama);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<DramaVO> getDramaVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Drama drama = dramaService.getById(id);
        ThrowUtils.throwIf(drama == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(dramaService.getDramaVO(drama, request));
    }

    /**
     * 分页获取列表（仅管理员可用）
     *
     * @param dramaQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Drama>> listDramaByPage(@RequestBody DramaQueryRequest dramaQueryRequest) {
        long current = dramaQueryRequest.getCurrent();
        long size = dramaQueryRequest.getPageSize();
        // 查询数据库
        Page<Drama> dramaPage = dramaService.page(new Page<>(current, size),
                dramaService.getQueryWrapper(dramaQueryRequest));
        return ResultUtils.success(dramaPage);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param dramaQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<DramaVO>> listDramaVOByPage(@RequestBody DramaQueryRequest dramaQueryRequest,
                                                               HttpServletRequest request) {
        long current = dramaQueryRequest.getCurrent();
        long size = dramaQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Drama> dramaPage = dramaService.page(new Page<>(current, size),
                dramaService.getQueryWrapper(dramaQueryRequest));
        // 获取封装类
        return ResultUtils.success(dramaService.getDramaVOPage(dramaPage, request));
    }

    /**
     * 分页获取当前登录用户创建的列表
     *
     * @param dramaQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<DramaVO>> listMyDramaVOByPage(@RequestBody DramaQueryRequest dramaQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(dramaQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        dramaQueryRequest.setUserId(loginUser.getId());
        long current = dramaQueryRequest.getCurrent();
        long size = dramaQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Drama> dramaPage = dramaService.page(new Page<>(current, size),
                dramaService.getQueryWrapper(dramaQueryRequest));
        // 获取封装类
        return ResultUtils.success(dramaService.getDramaVOPage(dramaPage, request));
    }

    /**
     * 编辑（给用户使用）
     *
     * @param dramaEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editDrama(@RequestBody DramaEditRequest dramaEditRequest, HttpServletRequest request) {
        if (dramaEditRequest == null || dramaEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        Drama drama = new Drama();
        BeanUtils.copyProperties(dramaEditRequest, drama);
        // 数据校验
        dramaService.validDrama(drama, false);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = dramaEditRequest.getId();
        Drama oldDrama = dramaService.getById(id);
        ThrowUtils.throwIf(oldDrama == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldDrama.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = dramaService.updateById(drama);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion
}
