package com.tower.mapper.my;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tower.entity.ChallengeReward;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 梦屿千寻
 * @since 2021-03-24
 */
public interface MyChallengeRewardMapper extends BaseMapper<ChallengeReward> {

    Integer selectTotalRebate(int userId);

    Integer selectTotalGet(int userId);

    void settlement();
}
