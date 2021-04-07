package com.tower.dto.page;

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
 * @author 梦屿千寻
 * @since 2021-04-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ProfitRebateLog对象", description="")
public class ProfitRebateLogDto implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "盈亏返利记录")
      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "玩家id")
    private Integer userId;

    @ApiModelProperty(value = "返利金额")
    private Double rebateCoin;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;


}
