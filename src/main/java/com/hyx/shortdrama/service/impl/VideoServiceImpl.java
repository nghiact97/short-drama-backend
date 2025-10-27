package com.hyx.shortdrama.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyx.shortdrama.common.ErrorCode;
import com.hyx.shortdrama.constant.CacheConstant;
import com.hyx.shortdrama.constant.CommonConstant;
import com.hyx.shortdrama.exception.BusinessException;
import com.hyx.shortdrama.mapper.VideoMapper;
import com.hyx.shortdrama.model.dto.video.VideoQueryRequest;
import com.hyx.shortdrama.model.entity.Video;
import com.hyx.shortdrama.model.vo.VideoVO;
import com.hyx.shortdrama.service.VideoService;
import com.hyx.shortdrama.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {

    @Override
    public void validVideo(Video video, boolean add) {
        if (video == null) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        if (add) {
            if (StringUtils.isBlank(video.getVideoUrl())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "Địa chỉ video không được để trống");
            }
        }
        if (StringUtils.isNotBlank(video.getTitle()) && video.getTitle().length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Tiêu đề không được vượt quá 80 ký tự");
        }
        if (StringUtils.isNotBlank(video.getDescription()) && video.getDescription().length() > 1024) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Mô tả không được vượt quá 1024 ký tự");
        }
    }

    @Override
    public QueryWrapper<Video> getQueryWrapper(VideoQueryRequest request) {
        QueryWrapper<Video> qw = new QueryWrapper<>();
        if (request == null) return qw;
        Long id = request.getId();
        Long notId = request.getNotId();
        String title = request.getTitle();
        Integer status = request.getStatus();
        Long userId = request.getUserId();
        Long dramaId = request.getDramaId();
        Integer episodeNumber = request.getEpisodeNumber();

        qw.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        qw.eq(ObjectUtils.isNotEmpty(id), "id", id);
        qw.eq(ObjectUtils.isNotEmpty(status), "status", status);
        qw.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        qw.eq(ObjectUtils.isNotEmpty(dramaId), "dramaId", dramaId);
        qw.like(StringUtils.isNotBlank(title), "title", title);

        String sortField = request.getSortField();
        String sortOrder = request.getSortOrder();

        // 默认按 orderNum desc, id desc；如果指定了剧集，推荐按 episodeNumber 正序、id 正序
        if (ObjectUtils.isNotEmpty(dramaId)) {
            qw.orderByAsc("episodeNumber").orderByAsc("id");
        } else if (SqlUtils.validSortField(sortField)) {
            qw.orderBy(true, CommonConstant.SORT_ORDER_ASC.equals(sortOrder), sortField);
        } else {
            qw.orderByDesc("orderNum").orderByDesc("id");
        }
        return qw;
    }

    @Override
    public VideoVO getVideoVO(Video video, HttpServletRequest request) {
        return VideoVO.objToVo(video);
    }

    @Override
    public Page<VideoVO> getVideoVOPage(Page<Video> page, HttpServletRequest request) {
        Page<VideoVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream().map(VideoVO::objToVo).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    @Cacheable(
            cacheNames = CacheConstant.VIDEO_EPISODES,
            key = "T(com.hyx.shortdrama.utils.CacheKeyBuilders).videoEpisodes(#dramaId)",
            condition = "@cacheSwitch.enabled",
            sync = true
    )
    public List<VideoVO> listEpisodes(long dramaId, HttpServletRequest request) {
        QueryWrapper<Video> qw = new QueryWrapper<Video>()
                .eq("status", 1)
                .eq("dramaId", dramaId)
                .orderByAsc("episodeNumber")
                .orderByAsc("id");
        List<Video> list = this.list(qw);
        return list.stream().map(v -> getVideoVO(v, request)).collect(Collectors.toList());
    }

    @Override
    @Cacheable(
            cacheNames = CacheConstant.VIDEO_FEED,
            key = "T(com.hyx.shortdrama.utils.CacheKeyBuilders).videoFeed(#dramaId,#current,#pageSize)",
            condition = "@cacheSwitch.enabled && #pageSize <= T(com.hyx.shortdrama.constant.CommonConstant).FEED_SIZE_LIMIT",
            sync = true
    )
    public Page<VideoVO> feed(long current, long pageSize, Long dramaId, HttpServletRequest request) {
        QueryWrapper<Video> qw = new QueryWrapper<Video>().eq("status", 1);
        if (dramaId != null) {
            qw.eq("dramaId", dramaId).orderByAsc("episodeNumber").orderByAsc("id");
        } else {
            qw.orderByDesc("orderNum").orderByDesc("id");
        }
        Page<Video> page = this.page(new Page<>(current, pageSize), qw);
        return getVideoVOPage(page, request);
    }
}
