package com.tower.dto.page;

import com.tower.dto.ExtracLogDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author 梦-屿-千-寻
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class ExtracLogPageDto extends PageDto<ExtracLogDto>{
    private String search;
}
