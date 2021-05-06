package com.tower.dto.page;

import com.tower.dto.CustomerServiceDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author xxxx
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class CustomerServicePageDto extends PageDto<CustomerServiceDto>{
    private String search;
}
