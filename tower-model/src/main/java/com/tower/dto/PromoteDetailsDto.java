package com.tower.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author xxxx
 * @date 2021/4/5 11:33
 */
@Data
@ApiModel(value = "推广明细对象", description = "")
public class PromoteDetailsDto {

    @ApiModelProperty(value = "总奖励")
    private double totalAward;

    @ApiModelProperty(value = "流水返利")
    private double rebate;

    @ApiModelProperty(value = "负亏盈返利")
    private double exhibitRebate;

    @ApiModelProperty(value = "上级分享")
    private double superShare;

    @ApiModelProperty(value = "分享下级")
    private double shareLower;

    @ApiModelProperty(value = "分享日期")
    private String createTime;
}
