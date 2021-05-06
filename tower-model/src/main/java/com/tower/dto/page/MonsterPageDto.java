package com.tower.dto.page;

import com.tower.dto.MonsterDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author xxxx
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class MonsterPageDto extends PageDto<MonsterDto>{
    private String search;
}
