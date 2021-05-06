package com.tower.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tower.entity.ProfitRebateLog;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xxxx
 * @since 2021-04-07
 */
public interface ProfitRebateLogMapper extends BaseMapper<ProfitRebateLog> {

    Double selectUserProfitByDay(@Param("userId") int userId,@Param("startTime") String startTime,@Param("stopTime") String stopTime);
}
