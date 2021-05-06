package com.tower.dto.page;

import com.tower.dto.ProfitRebateLogDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author xxxx
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class ProfitRebateLogPageDto extends PageDto<ProfitRebateLogDto>{
    private String search;
}
