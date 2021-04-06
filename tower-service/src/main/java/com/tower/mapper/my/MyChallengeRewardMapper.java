package com.tower.mapper.my;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tower.entity.ChallengeReward;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 梦屿千寻
 * @since 2021-03-24
 */
public interface MyChallengeRewardMapper extends BaseMapper<ChallengeReward> {

    Integer selectTotalRebate(@Param("userId")int userId);

    Integer selectTotalGet(@Param("userId")int userId);

    void settlement();

    void getChallengeReward(@Param("rewardIds") List<Integer> rewardIds);
}
