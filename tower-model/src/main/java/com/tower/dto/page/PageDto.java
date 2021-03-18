package com.tower.dto.page;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 梦-屿-千-寻
 * @date2021/3/18 14:31
 */
@Data
public class PageDto<T> {
    @ApiModelProperty(value = "第几页")
    private int page;
    @ApiModelProperty(value = "每页几条")
    private int size;
    @ApiModelProperty(value = "总条数")
    private int total;
    @ApiModelProperty(value = "数据")
    private List<T> list;

}
