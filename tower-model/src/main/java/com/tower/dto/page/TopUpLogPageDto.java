package com.tower.dto.page;

import com.tower.dto.TopUpLogDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author 梦-屿-千-寻
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class TopUpLogPageDto extends PageDto<TopUpLogDto>{
    private String search;
}
