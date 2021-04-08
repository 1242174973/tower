package com.tower.dto.page;

import com.tower.dto.ProfitRebateLogDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author 梦-屿-千-寻
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class ProfitRebateLogPageDto extends PageDto<ProfitRebateLogDto>{
    private String search;
}
