package com.tower.dto.page;

import com.tower.dto.GameLogDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author 梦-屿-千-寻
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class GameLogPageDto extends PageDto<GameLogDto>{
    private String search;
}
