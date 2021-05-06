package com.tower.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashSet;
import java.util.List;

/**
 * @author xxxx
 * @date2021/3/19 17:52
 */
@Data
public class LoginUserDto {
    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "登陆名")
    private String loginName;

    @ApiModelProperty(value = "昵称")
    private String name;

    @ApiModelProperty(value = "登录凭证")
    private String token;



}
