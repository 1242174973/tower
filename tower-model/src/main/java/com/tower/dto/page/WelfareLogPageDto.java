package com.tower.dto.page;

import com.tower.dto.WelfareLogDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author 梦-屿-千-寻
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class WelfareLogPageDto extends PageDto<WelfareLogDto>{
    private String search;
}
