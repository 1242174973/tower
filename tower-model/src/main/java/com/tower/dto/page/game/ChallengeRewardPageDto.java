package com.tower.dto.page.game;

import com.tower.dto.ChallengeRewardDto;
import com.tower.dto.page.PageDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xxxx
 * @date 2021/3/24 16:51
 */
@Data
public class ChallengeRewardPageDto  extends PageDto<ChallengeRewardDto> {
    @ApiModelProperty(value = "年")
    private int year;
    @ApiModelProperty(value = "月")
    private int month;
    @ApiModelProperty(value = "总返利")
    private int totalRebate;
    @ApiModelProperty(value = "已领取返利")
    private int totalGet;
}
