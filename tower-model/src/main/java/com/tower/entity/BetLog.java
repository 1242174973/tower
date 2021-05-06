package com.tower.entity;

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
 * @author xxxx
 * @since 2021-04-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BetLog对象", description = "")
public class BetLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "下注记录")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "记录id")
    private String orderId;

    @ApiModelProperty(value = "玩家id")
    private Integer userId;

    @ApiModelProperty(value = "1号下注分数")
    private BigDecimal oneBet;

    @ApiModelProperty(value = "2号下注分数")
    private BigDecimal twoBet;

    @ApiModelProperty(value = "3号下注分数")
    private BigDecimal threeBet;

    @ApiModelProperty(value = "4号下注分数")
    private BigDecimal fourBet;

    @ApiModelProperty(value = "5号下注分数")
    private BigDecimal fiveBet;

    @ApiModelProperty(value = "6号下注分数")
    private BigDecimal sixBet;

    @ApiModelProperty(value = "7号下注分数")
    private BigDecimal sevenBet;

    @ApiModelProperty(value = "8号下注分数")
    private BigDecimal eightBet;

    @ApiModelProperty(value = "中奖分数")
    private BigDecimal resultCoin;

    @ApiModelProperty(value = "中奖怪物")
    private Integer resultMonster;

    @ApiModelProperty(value = "状态（1、未开奖、2、已开奖 3、撤销下注）")
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "下注时间")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "开奖时间")
    private LocalDateTime resultTime;


}
