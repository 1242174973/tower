package com.tower.dto.page;

import com.tower.dto.TransferLogDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author xxxx
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class TransferLogPageDto extends PageDto<TransferLogDto>{
    private String search;
}
