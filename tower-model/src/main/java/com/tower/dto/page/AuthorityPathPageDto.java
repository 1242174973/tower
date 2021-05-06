package com.tower.dto.page;

import com.tower.dto.AuthorityPathDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author xxxx
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class AuthorityPathPageDto extends PageDto<AuthorityPathDto>{
    private String search;
}
