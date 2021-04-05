package com.tower.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tower.entity.ShareLog;
import com.tower.mapper.ShareLogMapper;
import com.tower.service.ShareLogService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 梦屿千寻
 * @since 2021-04-05
 */
@Service
public class ShareLogServiceImpl extends ServiceImpl<ShareLogMapper, ShareLog> implements ShareLogService {

    @Override
    public double selectUserShareByDay(int userId, String startTime, String stopTime) {
        return baseMapper.selectUserShareByDay(userId,startTime,stopTime);
    }

    @Override
    public double selectUserYieldByDay(int userId, String startTime, String stopTime) {
        return baseMapper.selectUserYieldByDay(userId,startTime,stopTime);
    }
}
