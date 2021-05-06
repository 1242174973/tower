package com.tower.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author xxxx
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

    @ApiModelProperty(value = "创建时间")
    private String createTime;
}
