package com.tower.dto.page;

import com.tower.dto.UserBankCardDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author xxxx
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class UserBankCardPageDto extends PageDto<UserBankCardDto>{
    private String search;
}
