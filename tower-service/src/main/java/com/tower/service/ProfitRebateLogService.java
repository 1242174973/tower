package com.tower.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tower.entity.ProfitRebateLog;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xxxx
 * @since 2021-04-07
 */
public interface ProfitRebateLogService extends IService<ProfitRebateLog> {

    double selectUserProfitByDay(int userId, String startTime, String stopTime);
}
