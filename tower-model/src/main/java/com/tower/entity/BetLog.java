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
 * @author 梦屿千寻
 * @since 2021-03-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="BetLog对象", description="")
public class BetLog implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "下注记录")
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "记录id")
    private String orderId;

    @ApiModelProperty(value = "玩家id")
    private Integer userId;

    @ApiModelProperty(value = "下注怪物id")
    private Integer betMonsterId;

    @ApiModelProperty(value = "下注分数")
    private BigDecimal betCoin;

    @ApiModelProperty(value = "结果怪物id")
    private Integer resultMonsterId;

    @ApiModelProperty(value = "结果输赢分数")
    private BigDecimal resultCoin;

    @ApiModelProperty(value = "状态（1、未开奖、2、已开奖 3、撤销下注）")
    private Integer status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "下注时间")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "开奖时间")
    private LocalDateTime resultTime;


}
