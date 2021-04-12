package com.tower.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tower.entity.ProfitRebateLog;
import com.tower.mapper.ProfitRebateLogMapper;
import com.tower.service.ProfitRebateLogService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 梦屿千寻
 * @since 2021-04-07
 */
@Service
public class ProfitRebateLogServiceImpl extends ServiceImpl<ProfitRebateLogMapper, ProfitRebateLog> implements ProfitRebateLogService {

    @Override
    public double selectUserProfitByDay(int userId, String startTime, String stopTime) {
        Double v = baseMapper.selectUserProfitByDay(userId, startTime, stopTime);
        return v==null?0.0:v;
    }
}
