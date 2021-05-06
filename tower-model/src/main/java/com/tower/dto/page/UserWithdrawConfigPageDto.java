package com.tower.dto.page;

import com.tower.dto.UserWithdrawConfigDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author xxxx
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class UserWithdrawConfigPageDto extends PageDto<UserWithdrawConfigDto>{
    private String search;
}
