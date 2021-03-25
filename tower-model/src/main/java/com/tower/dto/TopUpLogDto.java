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
@ApiModel(value = "TopUpLog对象", description = "")
public class TopUpLogDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "充值记录")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @ApiModelProperty(value = "充值信息ID")
    private Integer topUpId;

    @ApiModelProperty(value = "订单号")
    private String orderId;

    @ApiModelProperty(value = "玩家ID")
    private Integer userId;

    @ApiModelProperty(value = "充值金额")
    private BigDecimal topUpMoney;

    @ApiModelProperty(value = "实际到账")
    private BigDecimal remit;

    @ApiModelProperty(value = "实际上分")
    private BigDecimal coin;

    @ApiModelProperty(value = "当前状态（0、审核、1通过，上分中，2上分成功，10失败）")
    private Integer state;

    @ApiModelProperty(value = "审核说明")
    private String audit;

    @ApiModelProperty(value = "审核人")
    private Integer auditId;

    @ApiModelProperty(value = "充值银行")
    private String bankCardName;

    @ApiModelProperty(value = "充值银行卡号")
    private String bankCardNum;

    @ApiModelProperty(value = "汇款人")
    private String payee;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "审核时间")
    private LocalDateTime auditTime;


}
