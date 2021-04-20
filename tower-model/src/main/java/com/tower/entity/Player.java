package com.tower.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author 梦屿千寻
 * @since 2021-03-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "Player对象", description = "")
public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "玩家ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "密码盐")
    private String salt;

    @ApiModelProperty(value = "是否代理")
    private Integer isAgent;

    @ApiModelProperty(value = "账号（用户名）")
    private String account;

    @ApiModelProperty(value = "头像")
    private String pic;

    @ApiModelProperty(value = "vip等级")
    private Integer vip;

    @ApiModelProperty(value = "余额")
    private BigDecimal money;

    @ApiModelProperty(value = "保险柜余额")
    private BigDecimal safeBox;

    @ApiModelProperty(value = "返利比例")
    private BigDecimal rebate;

    @ApiModelProperty(value = "税点")
    private BigDecimal tax;

    @ApiModelProperty(value = "总奖励")
    private BigDecimal totalAward;

    @ApiModelProperty(value = "预计奖励")
    private BigDecimal expectedAward;

    @ApiModelProperty(value = "可提现奖励")
    private BigDecimal canAward;

    @ApiModelProperty(value = "保险柜密码")
    private String safeBoxPassword;

    @ApiModelProperty(value = "签到天数")
    private Integer signIn;

    @ApiModelProperty(value = "总签到天数")
    private Integer totalSignIn;

    @ApiModelProperty(value = "推广码")
    private String spread;

    @ApiModelProperty(value = "上级玩家ID")
    private Integer superId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;


    @ApiModelProperty(value = "vip升级经验")
    private Integer experience;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "签到时间")
    private LocalDateTime signInTime;

}
