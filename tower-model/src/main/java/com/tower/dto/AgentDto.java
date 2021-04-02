package com.tower.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author 梦-屿-千-寻
 * @date 2021/4/2 11:25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="代理首页数据", description="")
public class AgentDto implements Serializable {
    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "预计奖励")
    private BigDecimal expectedReward;

    @ApiModelProperty(value = "流水代理")
    private BigDecimal rebate;

    @ApiModelProperty(value = "盈亏代理")
    private BigDecimal tax;

    @ApiModelProperty(value = "新增人数")
    private Integer newNum;

    @ApiModelProperty(value = "历史总奖励")
    private BigDecimal totalAward;

    @ApiModelProperty(value = "可提现金额")
    private BigDecimal canAward;
}
