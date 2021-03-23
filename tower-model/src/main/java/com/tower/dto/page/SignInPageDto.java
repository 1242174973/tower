package com.tower.dto.page;

import com.tower.dto.SignInDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author 梦-屿-千-寻
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class SignInPageDto extends PageDto<SignInDto>{
    private String search;
}
