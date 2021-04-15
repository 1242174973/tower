package com.tower.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tower.entity.Player;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 梦屿千寻
 * @since 2021-03-16
 */
public interface PlayerMapper extends BaseMapper<Player> {

    void resetAward();
}
