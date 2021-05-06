package com.tower.dto.page;

import com.tower.dto.NoticeDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author xxxx
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class NoticePageDto extends PageDto<NoticeDto>{
    private String search;
}
