package com.tower.mapper.my;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tower.entity.Player;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author 梦屿千寻
 * @since 2021-03-16
 */
public interface MyPlayerMapper extends BaseMapper<Player> {
    /**
     * 重置玩家提现次数和额度
     */
    void resetPlayerWithdraw();
}
