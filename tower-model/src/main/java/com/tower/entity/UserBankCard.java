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
@ApiModel(value="UserBankCard对象", description="")
public class UserBankCard implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "银行卡绑定信息")
      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "玩家id")
    private Integer userId;

    @ApiModelProperty(value = "银行卡持卡人")
    private String bankCardName;

    @ApiModelProperty(value = "银行卡号")
    private String bankCardNum;

    @ApiModelProperty(value = "银行")
    private String bank;

    @ApiModelProperty(value = "支行")
    private String subBranch;

    @ApiModelProperty(value = "省")
    private String province;

    @ApiModelProperty(value = "市")
    private String city;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;


}
