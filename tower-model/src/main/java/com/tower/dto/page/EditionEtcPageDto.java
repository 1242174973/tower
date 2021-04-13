package com.tower.dto.page;

import com.tower.dto.EditionEtcDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author 梦-屿-千-寻
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class EditionEtcPageDto extends PageDto<EditionEtcDto>{
    private String search;
}
