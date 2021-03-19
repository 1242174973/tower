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
 * @author 梦屿千寻
 * @since 2021-03-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserWithdrawConfig对象", description="")
public class UserWithdrawConfig implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "玩家提现配置")
      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "玩家ID")
    private Integer userId;

    @ApiModelProperty(value = "每日可提现总次数")
    private Integer totalWithdrawSize;

    @ApiModelProperty(value = "今日可提现总次数")
    private Integer todayWithdrawSize;

    @ApiModelProperty(value = "每日可提现金额")
    private Double totalWithdrawMoney;

    @ApiModelProperty(value = "今日可提现金额")
    private Double todayWithdrawMoney;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;


}
