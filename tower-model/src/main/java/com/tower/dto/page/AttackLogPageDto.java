package com.tower.dto.page;

import com.tower.dto.AttackLogDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author 梦-屿-千-寻
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class AttackLogPageDto extends PageDto<AttackLogDto>{
    private String search;
}
