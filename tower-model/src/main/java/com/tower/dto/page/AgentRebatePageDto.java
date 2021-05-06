package com.tower.dto.page;

import com.tower.dto.AgentRebateDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author xxxx
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class AgentRebatePageDto extends PageDto<AgentRebateDto>{
    private String search;
}
