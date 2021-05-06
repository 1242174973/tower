package com.tower.dto.page;

import com.tower.dto.SignInDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author xxxx
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class SignInPageDto extends PageDto<SignInDto>{
    private String search;
}
