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
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author xxxx
 * @since 2021-03-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "Monster对象", description = "")
public class Monster implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "怪物表")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "怪物昵称")
    private String monsterName;

    @ApiModelProperty(value = "炮塔昵称")
    private String turretName;

    @ApiModelProperty(value = "倍数")
    private Integer multiple;

    @ApiModelProperty(value = "最大下注")
    private Integer maxBet;


    @ApiModelProperty(value = "概率（千分之）")
    private Integer rates;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;


}
