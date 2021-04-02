package com.tower.mapper.my;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tower.entity.AgentRebate;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 梦屿千寻
 * @since 2021-04-02
 */
public interface MyAgentRebateMapper extends BaseMapper<AgentRebate> {

    /**
     * 查询预计奖励
     * @param playerId 玩家id
     * @param period 周期
     * @return 预计奖励
     */
    double selectExpectedReward(@Param("playerId") int playerId,@Param("period") String period);
}
