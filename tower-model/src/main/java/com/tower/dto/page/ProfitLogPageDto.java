package com.tower.dto.page;

import com.tower.dto.ProfitLogDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author 梦-屿-千-寻
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class ProfitLogPageDto extends PageDto<ProfitLogDto>{
    private String search;
}
