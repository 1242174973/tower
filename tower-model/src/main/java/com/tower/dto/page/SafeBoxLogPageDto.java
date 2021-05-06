package com.tower.dto.page;

import com.tower.dto.SafeBoxLogDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author xxxx
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class SafeBoxLogPageDto extends PageDto<SafeBoxLogDto>{
    private String search;
}
