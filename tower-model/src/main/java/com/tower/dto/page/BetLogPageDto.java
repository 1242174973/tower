package com.tower.dto.page;

import com.tower.dto.BetLogDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author 梦-屿-千-寻
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class BetLogPageDto extends PageDto<BetLogDto>{
    private String search;
}
