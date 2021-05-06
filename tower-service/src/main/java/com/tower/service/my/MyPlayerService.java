package com.tower.service.my;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tower.entity.Player;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xxxx
 * @since 2021-03-16
 */
public interface MyPlayerService extends IService<Player> {
    /**
     * 重置玩家重置提现次数和额度
     */
    void resetPlayerWithdraw();
}
