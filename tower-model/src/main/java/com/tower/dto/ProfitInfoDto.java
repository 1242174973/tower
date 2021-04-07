package com.tower.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 梦-屿-千-寻
 * @date 2021/4/7 14:14
 */
@Data
public class ProfitInfoDto {

    @ApiModelProperty(value = "充值金额")
    private double topUp;

    @ApiModelProperty(value = "流水金额")
    private double water;

    @ApiModelProperty(value = "福利金额")
    private double welfare;

    @ApiModelProperty(value = "推广返利")
    private double rebate;

    @ApiModelProperty(value = "盈利金额")
    private double profit;
}
