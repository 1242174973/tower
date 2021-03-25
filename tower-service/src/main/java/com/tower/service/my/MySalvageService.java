package com.tower.service.my;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tower.entity.Salvage;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 梦屿千寻
 * @since 2021-03-25
 */
public interface MySalvageService extends IService<Salvage> {
    /**
     * 查询总救援金
     * @param userId 玩家id
     * @return 总救援金
     */
    Integer selectTotalSalvage(int userId);
    /**
     * 查询总已领取救援金
     * @param userId 玩家id
     * @return 已领取救援金
     */
    Integer selectTotalGet(int userId);

}
