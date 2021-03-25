package com.tower.service.my.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tower.entity.Salvage;
import com.tower.mapper.my.MySalvageMapper;
import com.tower.service.my.MySalvageService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 梦屿千寻
 * @since 2021-03-25
 */
@Service
public class MySalvageServiceImpl extends ServiceImpl<MySalvageMapper, Salvage> implements MySalvageService {

    @Override
    public Integer selectTotalSalvage(int userId) {
        return baseMapper.selectTotalSalvage(userId);
    }

    @Override
    public Integer selectTotalGet(int userId) {
        return baseMapper.selectTotalGet(userId);
    }


}
