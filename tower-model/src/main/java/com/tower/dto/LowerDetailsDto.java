package com.tower.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author xxxx
 * @date 2021/4/5 14:01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "下级详情", description = "")
public class LowerDetailsDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "玩家id")
    private int userId;

    @ApiModelProperty(value = "积分代理返点")
    private double rebate;

    @ApiModelProperty(value = "活跃人数")
    private int activeNum;

    @ApiModelProperty(value = "新增人数")
    private int newNum;

    @ApiModelProperty(value = "总人数")
    private int totalNum;

    @ApiModelProperty(value = "个人详情")
    private LowerDetails myDetails;

    @ApiModelProperty(value = "下级详情")
    private LowerDetails lowerDetails;

    @ApiModel(value = "下级详情", description = "")
    @Data
    public static class LowerDetails implements Serializable {

        private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "推广返利")
        private double rebate;

        @ApiModelProperty(value = "充值金额")
        private double topUp;

        @ApiModelProperty(value = "积分")
        private double coin;

        @ApiModelProperty(value = "福利金额")
        private double welfare;

        @ApiModelProperty(value = "盈亏金额")
        private double profit;
    }
}
