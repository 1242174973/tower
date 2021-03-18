package com.tower.dto.page;

import com.tower.dto.WelfareLogDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 梦-屿-千-寻
 * @date2021/3/18 14:34
 */
@Data
public class WelFareLogPageDto extends PageDto<WelfareLogDto> {
    @ApiModelProperty(value = "奖励类型 0 全部   1签到 ")
    private int model;
    @ApiModelProperty(value = "最近天数 0 近三天 1今天 2昨天 ")
    private int recentDay;
}
