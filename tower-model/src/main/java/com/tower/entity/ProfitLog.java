package com.tower.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
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
 * @author xxxx
 * @since 2021-04-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ProfitLog对象", description="")
public class ProfitLog implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "代理盈亏记录")
      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "游戏期数")
    private String orderId;

    @ApiModelProperty(value = "代理id")
    private Integer userId;

    @ApiModelProperty(value = "盈亏分")
    private Double profitCoin;

    @ApiModelProperty(value = "状态（0未结算、1已结算）")
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "产生时间")
    private LocalDateTime createTime;


}
