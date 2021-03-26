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
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author 梦屿千寻
 * @since 2021-03-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Salvage对象", description="")
public class SalvageDto implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "救助金")
      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "玩家id")
    private Integer userId;

    @ApiModelProperty(value = "盈亏")
    private Double profit;

    @ApiModelProperty(value = "救助金")
    private BigDecimal salvage;

    @ApiModelProperty(value = "已领取")
    private BigDecimal getSalvage;

    @ApiModelProperty(value = "状态 0 未结算  1 已结算  2 已领取")
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "昨日救助金")
    private BigDecimal yesterdaySalvage;



}
