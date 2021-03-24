package com.tower.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tower.entity.ChallengeReward;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 梦屿千寻
 * @since 2021-03-24
 */
public interface ChallengeRewardService extends IService<ChallengeReward> {
    /**
     * 查询有关自己的所有返利
     * @param userId  xx
     * @return 查询有关自己的所有返利
     */
    Integer selectTotalRebate(int userId);
    /** 查询有关自己已经领取的所有返利
     * @param userId  xx
     * @return 查询有关自己已经领取的所有返利
     */
    int selectTotalGet(int userId);
}
