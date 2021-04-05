package com.tower.service.my.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tower.entity.AgentRebate;
import com.tower.mapper.AgentRebateMapper;
import com.tower.mapper.my.MyAgentRebateMapper;
import com.tower.service.AgentRebateService;
import com.tower.service.my.MyAgentRebateService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 梦屿千寻
 * @since 2021-04-02
 */
@Service
public class MyAgentRebateServiceImpl extends ServiceImpl<MyAgentRebateMapper, AgentRebate> implements MyAgentRebateService {

    @Override
    public double selectExpectedReward(int playerId, String period) {
        return baseMapper.selectExpectedReward(playerId,period);
    }

    @Override
    public double selectUserRewardByDay(int playerId, String startTime, String stopTime) {
        return  baseMapper.selectUserRewardByDay(playerId,startTime,stopTime);
    }
}
