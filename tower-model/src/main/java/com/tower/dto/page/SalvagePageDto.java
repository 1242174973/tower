package com.tower.dto.page;

import com.tower.dto.SalvageDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author 梦-屿-千-寻
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class SalvagePageDto extends PageDto<SalvageDto>{
    private String search;
}
