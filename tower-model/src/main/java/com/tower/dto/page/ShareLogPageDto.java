package com.tower.dto.page;

import com.tower.dto.ShareLogDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author 梦-屿-千-寻
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class ShareLogPageDto extends PageDto<ShareLogDto>{
    private String search;
}
