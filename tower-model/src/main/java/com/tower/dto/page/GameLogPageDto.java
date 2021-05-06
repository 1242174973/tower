package com.tower.dto.page;

import com.tower.dto.GameLogDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author xxxx
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class GameLogPageDto extends PageDto<GameLogDto>{
    private String search;
}
