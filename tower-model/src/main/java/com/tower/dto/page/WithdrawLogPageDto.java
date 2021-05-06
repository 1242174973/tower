package com.tower.dto.page;

import com.tower.dto.WithdrawLogDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author xxxx
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class WithdrawLogPageDto extends PageDto<WithdrawLogDto>{
    private String search;
}
