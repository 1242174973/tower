package com.tower.dto.page;

import com.tower.dto.SalvageDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author xxxx
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class SalvagePageDto extends PageDto<SalvageDto>{
    private String search;
}
