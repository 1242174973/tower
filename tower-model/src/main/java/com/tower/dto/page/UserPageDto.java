package com.tower.dto.page;

import com.tower.dto.UserDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author xxxx
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class UserPageDto extends PageDto<UserDto>{
    private String search;
}
