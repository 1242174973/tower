package com.tower.dto.page;

import com.tower.dto.EditionEtcDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author xxxx
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class EditionEtcPageDto extends PageDto<EditionEtcDto>{
    private String search;
}
