package com.tower.service.my;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tower.entity.ChallengeReward;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 梦屿千寻
 * @since 2021-03-24
 */
public interface MyChallengeRewardService extends IService<ChallengeReward> {
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

    /**
     * 结算
     */
    void settlement();

    /**
     *  添加该玩家今天的记录
     * @param id 玩家id
     */
    void insertToday(int id);

    /**
     * 领取奖励
     * @param rewardIds 奖励id
     */
    void getChallengeReward(List<Integer> rewardIds);
}
