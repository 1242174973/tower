package com.tower.dto.page;

import com.tower.dto.ProfitLogDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author xxxx
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class ProfitLogPageDto extends PageDto<ProfitLogDto>{
    private String search;
}
