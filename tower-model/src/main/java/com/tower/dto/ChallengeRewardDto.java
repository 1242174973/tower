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
 * @since 2021-03-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ChallengeReward对象", description = "")
public class ChallengeRewardDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "流水返利")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "玩家ID")
    private Integer userId;

    @ApiModelProperty(value = "挑战账目")
    private BigDecimal challenge;

    @ApiModelProperty(value = "返利")
    private BigDecimal rebate;

    @ApiModelProperty(value = "已领取")
    private BigDecimal getRebate;

    @ApiModelProperty(value = "状态（0未结算，1已结算，2已领取）")
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "总返利")
    private Integer totalRebate;

}
