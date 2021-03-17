package com.tower.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author 梦-屿-千-寻
 * @date2021/3/17 17:09
 */
@Data
@ApiModel(value="Monster对象", description="")
public class MonsterDto {
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

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
}
