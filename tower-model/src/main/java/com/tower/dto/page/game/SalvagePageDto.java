package com.tower.dto.page.game;

import com.tower.dto.SalvageDto;
import com.tower.dto.page.PageDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xxxx
 * @date 2021/3/24 16:51
 */
@Data
public class SalvagePageDto extends PageDto<SalvageDto> {
    @ApiModelProperty(value = "年")
    private int year;
    @ApiModelProperty(value = "月")
    private int month;
    @ApiModelProperty(value = "总救援金")
    private int totalSalvage;
    @ApiModelProperty(value = "已领取救援金")
    private int totalGet;
}
