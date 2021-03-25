package com.tower.mapper.my;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tower.entity.Salvage;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 梦屿千寻
 * @since 2021-03-25
 */
public interface MySalvageMapper extends BaseMapper<Salvage> {

    Integer selectTotalSalvage(int userId);

    Integer selectTotalGet(int userId);

    void settlement();

    void getSalvage(List<Integer> rewardIds);
}
