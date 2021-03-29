package com.tower.entity;

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
 * @author 梦屿千寻
 * @since 2021-03-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="AttackLog对象", description="")
public class AttackLog implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "攻击id")
      private Integer id;

    @ApiModelProperty(value = "期号")
    private String orderId;

    @ApiModelProperty(value = "版本")
    private String ver;

    @ApiModelProperty(value = "怪物ID")
    private Integer monsterId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;


}
