package com.tower.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tower.entity.ShareLog;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author xxxx
 * @since 2021-04-05
 */
public interface ShareLogService extends IService<ShareLog> {
    /**
     * @param userId    玩家id
     * @param startTime 开始时间
     * @param stopTime  结束时间
     * @return 分享金额
     */
    double selectUserShareByDay(int userId, String startTime, String stopTime);
    /**
     * @param userId    玩家id
     * @param startTime 开始时间
     * @param stopTime  结束时间
     * @return 分享金额
     */
    double selectUserYieldByDay(int userId, String startTime, String stopTime);
}
