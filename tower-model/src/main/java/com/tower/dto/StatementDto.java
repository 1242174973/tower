package com.tower.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author 梦-屿-千-寻
 * @date 2021/4/5 16:07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "报表信息", description = "")
public class StatementDto {

    @ApiModelProperty(value = "直系活跃人数")
    private int activeNum;

    @ApiModelProperty(value = "直系新增人数")
    private int newNum;

    @ApiModelProperty(value = "直系总人数")
    private int totalNum;

    @ApiModelProperty(value = "其他活跃人数")
    private int otherActiveNum;

    @ApiModelProperty(value = "其他新增人数")
    private int otherNewNum;

    @ApiModelProperty(value = "其他总人数")
    private int otherTotalNum;

    @ApiModelProperty(value = "本人流水返利")
    private double myRebate;

    @ApiModelProperty(value = "下级流水返利")
    private double lowerRebate;

    @ApiModelProperty(value = "已领取上周期亏盈代理返利")
    private double profitRebate;

    @ApiModelProperty(value = "本人投注流水")
    private double myBetWater;

    @ApiModelProperty(value = "下级投注流水")
    private double lowerBetWater;

    @ApiModelProperty(value = "本人盈亏")
    private double myProfit;

    @ApiModelProperty(value = "下级盈亏")
    private double lowerProfit;

    @ApiModelProperty(value = "本人充值")
    private double myTopUp;

    @ApiModelProperty(value = "下级充值")
    private double lowerTopUp;

    @ApiModelProperty(value = "本人福利")
    private double myWelfare;

    @ApiModelProperty(value = "下级福利")
    private double lowerWelfare;

    @ApiModelProperty(value = "周期")
    private int period;

    @ApiModelProperty(value = "本周期团队盈亏总和")
    private double totalProfit;

}
