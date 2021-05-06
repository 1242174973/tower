package com.tower.dto.page;

import com.tower.dto.BetLogDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author xxxx
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class BetLogPageDto extends PageDto<BetLogDto>{
    private String search;
}
