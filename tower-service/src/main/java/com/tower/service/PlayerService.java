package com.tower.service;

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
public interface PlayerService extends IService<Player> {

    void resetAward();
}
