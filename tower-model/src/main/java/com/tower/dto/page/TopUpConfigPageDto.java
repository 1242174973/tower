package com.tower.dto.page;

import com.tower.dto.TopUpConfigDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author 梦-屿-千-寻
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class TopUpConfigPageDto extends PageDto<TopUpConfigDto>{
    private String search;
}
