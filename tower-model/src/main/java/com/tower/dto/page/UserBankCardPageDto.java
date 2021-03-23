package com.tower.dto.page;

import com.tower.dto.UserBankCardDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author 梦-屿-千-寻
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class UserBankCardPageDto extends PageDto<UserBankCardDto>{
    private String search;
}
