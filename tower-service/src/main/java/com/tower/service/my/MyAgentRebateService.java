package com.tower.service.my;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tower.entity.AgentRebate;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author xxxx
 * @since 2021-04-02
 */
public interface MyAgentRebateService extends IService<AgentRebate> {
    /**
     * 查询预计奖励
     *
     * @param playerId 玩家id
     * @param period   周期
     * @return 预计奖励
     */
    double selectExpectedReward(int playerId, String period);

    /**
     * 查询返利
     *
     * @param playerId  玩家id
     * @param startTime 周期
     * @return 预计奖励
     */
    double selectUserRewardByDay(int playerId, String startTime, String stopTime);
}
