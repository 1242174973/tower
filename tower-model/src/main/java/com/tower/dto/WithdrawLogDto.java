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
 * @since 2021-03-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="WithdrawLog对象", description="")
public class WithdrawLogDto implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "提现记录表")
      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "玩家id")
    private Integer userId;

    @ApiModelProperty(value = "订单号")
    private String order;

    @ApiModelProperty(value = "提现金额")
    private BigDecimal withdrawMoney;

    @ApiModelProperty(value = "手续费金额")
    private BigDecimal serviceCharge;

    @ApiModelProperty(value = "实际汇款")
    private BigDecimal remit;

    @ApiModelProperty(value = "当前状态（0审核，1通过，汇款中，3汇款成功，4失败）")
    private Integer state;

    @ApiModelProperty(value = "审核说明")
    private String audit;

    @ApiModelProperty(value = "审核id")
    private Integer auditId;

    @ApiModelProperty(value = "提现银行")
    private String bankCardName;

    @ApiModelProperty(value = "银行卡号")
    private String bankCardNum;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "审核时间")
    private LocalDateTime auditTime;


}
