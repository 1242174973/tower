package com.tower.dto.page;

import com.tower.dto.WithdrawLogDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author 梦-屿-千-寻
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class WithdrawLogPageDto extends PageDto<WithdrawLogDto>{
    private String search;
}
