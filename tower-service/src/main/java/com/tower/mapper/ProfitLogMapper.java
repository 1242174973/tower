package com.tower.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tower.entity.ProfitLog;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xxxx
 * @since 2021-04-05
 */
public interface ProfitLogMapper extends BaseMapper<ProfitLog> {

    Double selectUserProfitByDay(@Param("userId") int userId,@Param("startTime") String startTime,@Param("stopTime")  String stopTime);
    Double selectUserProfitByDayAllStatus(@Param("userId") int userId,@Param("startTime") String startTime,@Param("stopTime")  String stopTime);


}

