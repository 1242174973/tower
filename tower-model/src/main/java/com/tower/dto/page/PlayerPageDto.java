package com.tower.dto.page;

import com.tower.dto.PlayerDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author xxxx
 * @date 2021/3/22 9:56
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PlayerPageDto extends PageDto<PlayerDto>{
    private String search;
}
