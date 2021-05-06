package com.tower.dto.page;

import com.tower.dto.RoleDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author xxxx
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class RolePageDto extends PageDto<RoleDto>{
    private String search;
}
