package com.tower.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2021-03-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SafeBoxLog对象", description="")
public class SafeBoxLog implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "记录值")
      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "玩家ID")
    private Integer userId;

    @ApiModelProperty(value = "存取分")
    private Integer withdraw;

    @ApiModelProperty(value = "存取时间")
    private LocalDateTime createTime;


}
