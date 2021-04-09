package com.tower.dto.page;

import com.tower.dto.RoleDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author 梦-屿-千-寻
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class RolePageDto extends PageDto<RoleDto>{
    private String search;
}
