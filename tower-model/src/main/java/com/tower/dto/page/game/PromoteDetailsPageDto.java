package com.tower.dto.page.game;

import com.tower.dto.PromoteDetailsDto;
import com.tower.dto.page.PageDto;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author 梦屿千寻
 * @since 2021-04-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "PromoteDetails对象", description = "")
public class PromoteDetailsPageDto extends PageDto<PromoteDetailsDto> {

}
