package com.tower.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tower.entity.ProfitLog;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author xxxx
 * @since 2021-04-05
 */
public interface ProfitLogService extends IService<ProfitLog> {
    /**
     * 查询盈亏返利
     *
     * @param userId    玩家id
     * @param startTime 开始时间
     * @param stopTime  结束时间
     * @return 盈亏返利
     */
    double selectUserProfitByDay(int userId, String startTime, String stopTime);
    /**
     * 查询盈亏返利
     *
     * @param userId    玩家id
     * @param startTime 开始时间
     * @param stopTime  结束时间
     * @return 盈亏返利
     */
    double selectUserProfitByDayAllStatus(int userId, String startTime, String stopTime);
}
