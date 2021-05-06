package com.tower.mapper.my;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tower.entity.Salvage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xxxx
 * @since 2021-03-25
 */
public interface MySalvageMapper extends BaseMapper<Salvage> {

    Integer selectTotalSalvage(@Param("userId")int userId);

    Integer selectTotalGet(@Param("userId")int userId);

    void settlement();

    void getSalvage(@Param("rewardIds") List<Integer> rewardIds);
}
