package com.tower.dto.page;

import com.tower.dto.UserWithdrawConfigDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author 梦-屿-千-寻
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class UserWithdrawConfigPageDto extends PageDto<UserWithdrawConfigDto>{
    private String search;
}
