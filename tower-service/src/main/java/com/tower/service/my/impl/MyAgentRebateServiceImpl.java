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
 * @author xxxx
 * @since 2021-04-02
 */
@Service
public class MyAgentRebateServiceImpl extends ServiceImpl<MyAgentRebateMapper, AgentRebate> implements MyAgentRebateService {

    @Override
    public double selectExpectedReward(int playerId, String period) {
        Double v = baseMapper.selectExpectedReward(playerId, period);
        return v==null?0:v;
    }

    @Override
    public double selectUserRewardByDay(int playerId, String startTime, String stopTime) {
        Double v = baseMapper.selectUserRewardByDay(playerId, startTime, stopTime);
        return v==null?0:v;
    }
}
