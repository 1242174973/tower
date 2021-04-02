package com.tower.dto.page.game;

import com.tower.dto.AgentTeamDto;
import com.tower.dto.page.PageDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * @author 梦-屿-千-寻
 * @date 2021/4/2 15:12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "代理首页数据", description = "")
public class AgentTeamPageDto extends PageDto<AgentTeamDto> {
    @ApiModelProperty(value = "周期 1今日、2昨日、10本周期、20上周期", required = true)
    private int period;
}
