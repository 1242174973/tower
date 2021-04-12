package com.tower.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author 梦屿千寻
 * @since 2021-04-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="EditionEtc对象", description="")
public class EditionEtcDto implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "版本信息")
      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "平台类型(1:IOS,2:Android)")
    private Integer platform;

    @ApiModelProperty(value = "强制更新版本")
    private Integer forceVersion;

    @ApiModelProperty(value = "最新版")
    private Integer newest;

    @ApiModelProperty(value = "升级描述")
    private String description;

    @ApiModelProperty(value = "下载地址")
    private String url;

    @ApiModelProperty(value = "二维码地址")
    private String dimensional;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "版本更新时间")
    private LocalDateTime createTime;


}
