package com.tower.dto.page;

import com.tower.dto.TopUpLogDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author xxxx
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class TopUpLogPageDto extends PageDto<TopUpLogDto>{
    private String search;
}
