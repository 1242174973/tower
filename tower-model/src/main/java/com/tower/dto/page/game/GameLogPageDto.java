package com.tower.dto.page.game;

import com.tower.dto.GameLogDto;
import com.tower.dto.WelfareLogDto;
import com.tower.dto.page.PageDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xxxx
 * @date2021/3/18 14:34
 */
@Data
public class GameLogPageDto extends PageDto<GameLogDto> {
    @ApiModelProperty(value = "奖励类型 0 全部   1签到 ")
    private int model;
    @ApiModelProperty(value = "最近天数 0 近三天 1今天 2昨天 ")
    private int recentDay;
}
