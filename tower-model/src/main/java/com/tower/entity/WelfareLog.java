package com.tower.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author 梦屿千寻
 * @since 2021-03-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "WelfareLog对象", description = "")
public class WelfareLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "福利记录表")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "玩家Id")
    private Integer userId;

    @ApiModelProperty(value = "福利值")
    private BigDecimal welfare;

    @ApiModelProperty(value = "福利类型")
    private Integer welfareType;

    @ApiModelProperty(value = "获取方式")
    private Integer mode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "获取时间")
    private LocalDateTime createTime;


}
