package com.tower.dto.page;

import com.tower.dto.ExtracLogDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author xxxx
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class ExtracLogPageDto extends PageDto<ExtracLogDto>{
    private String search;
}
