package com.tower.dto.page;

import com.tower.dto.NoticeDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author 梦-屿-千-寻
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class NoticePageDto extends PageDto<NoticeDto>{
    private String search;
}
