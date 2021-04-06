package com.tower.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tower.entity.ProfitLog;
import com.tower.mapper.ProfitLogMapper;
import com.tower.service.ProfitLogService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 梦屿千寻
 * @since 2021-04-05
 */
@Service
public class ProfitLogServiceImpl extends ServiceImpl<ProfitLogMapper, ProfitLog> implements ProfitLogService {

    @Override
    public double selectUserProfitByDay(int userId, String startTime, String stopTime) {
        Double v = baseMapper.selectUserProfitByDay(userId, startTime, stopTime);
        return v == null ? 0 : v;
    }
}