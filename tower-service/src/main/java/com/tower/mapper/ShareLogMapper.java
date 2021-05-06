package com.tower.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tower.entity.ShareLog;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xxxx
 * @since 2021-04-05
 */
public interface ShareLogMapper extends BaseMapper<ShareLog> {
    /**
     * @param userId    玩家id
     * @param startTime 开始时间
     * @param stopTime  结束时间
     * @return 分享金额
     */
    Double selectUserShareByDay(@Param("userId") int userId,@Param("startTime")  String startTime,@Param("stopTime") String stopTime);
    /**
     * @param userId    玩家id
     * @param startTime 开始时间
     * @param stopTime  结束时间
     * @return 分享金额
     */
    Double selectUserYieldByDay(@Param("userId")int userId,@Param("startTime") String startTime,@Param("stopTime") String stopTime);
}
