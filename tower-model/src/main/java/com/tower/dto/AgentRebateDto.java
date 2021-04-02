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
 * @since 2021-04-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="AgentRebate对象", description="")
public class AgentRebateDto implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "下级流水返利")
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "代理id")
    private Integer agentUserId;

    @ApiModelProperty(value = "玩家id")
    private Integer userId;

    @ApiModelProperty(value = "流水")
    private BigDecimal challenge;

    @ApiModelProperty(value = "返利")
    private BigDecimal rebate;

    @ApiModelProperty(value = "状态（0未结算，1已结算，2已领取）")
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "产生时间")
    private LocalDateTime createTime;


}
