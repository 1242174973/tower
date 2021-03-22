package com.tower.dto.page;

import com.tower.dto.MonsterDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author 梦-屿-千-寻
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class MonsterPageDto extends PageDto<MonsterDto>{
    private String search;
}
