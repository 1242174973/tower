package com.tower.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author xxxx
 * @date 2021/4/2 15:12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "代理首页数据", description = "")
public class AgentTeamDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "团队id")
    private int teamId;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "流水返利")
    private BigDecimal rebate;

    @ApiModelProperty(value = "流水返利金额")
    private BigDecimal rebateMoney;

    @ApiModelProperty(value = "团队人数")
    private int teamNum;

    @ApiModelProperty(value = "团队盈亏")
    private BigDecimal profit;

    @ApiModelProperty(value = "团队流水")
    private BigDecimal water;
}
