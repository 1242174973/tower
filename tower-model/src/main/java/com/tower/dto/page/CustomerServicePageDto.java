package com.tower.dto.page;

import com.tower.dto.CustomerServiceDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author 梦-屿-千-寻
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class CustomerServicePageDto extends PageDto<CustomerServiceDto>{
    private String search;
}
