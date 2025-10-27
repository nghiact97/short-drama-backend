package com.hyx.shortdrama.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName(value = "video")
@Data
public class Video implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String title;
    private String description;

    private String videoUrl;
    private String coverUrl;

    private Integer durationSec;
    private Integer orderNum;
    private Integer status; // 0-下线 1-上线

    private Long userId;
    
    // 新增字段：剧集相关（可为空，保持向后兼容）
    private Long dramaId;       // 所属剧集id
    private Integer episodeNumber; // 集数（第几集）

    private Date createTime;
    private Date updateTime;

    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
