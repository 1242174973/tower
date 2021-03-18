package com.tower.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author 梦-屿-千-寻
 * @date2021/3/16 14:45
 */
@Data
public class PlayerDto {
    @ApiModelProperty(value = "玩家ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "图片验证码")
    private String picCode;

    @ApiModelProperty(value = "验证码token")
    private String codeToken;

    @ApiModelProperty(value = "密码")
    private String password;

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

    @ApiModelProperty(value = "token")
    private String token;
}
