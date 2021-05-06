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
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author xxxx
 * @since 2021-03-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "TopUpConfig对象", description = "")
public class TopUpConfigDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "充值配置")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "充值类型（1、官方、2支付宝、银联）")
    private Integer type;

    @ApiModelProperty(value = "转账方式")
    private String model;

    @ApiModelProperty(value = "银行卡名")
    private String bankCardName;

    @ApiModelProperty(value = "收款人")
    private String payee;

    @ApiModelProperty(value = "银行卡号")
    private String bankCardNum;

    @ApiModelProperty(value = "支行")
    private String subBranch;

    @ApiModelProperty(value = "最小充值")
    private Integer minTopUp;

    @ApiModelProperty(value = "最大充值")
    private Integer maxTopUp;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;


}
