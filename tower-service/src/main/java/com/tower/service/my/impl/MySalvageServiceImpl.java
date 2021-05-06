package com.tower.service.my.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tower.entity.Salvage;
import com.tower.mapper.my.MySalvageMapper;
import com.tower.service.my.MySalvageService;
import com.tower.utils.DateUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xxxx
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

    @Override
    public void settlement() {
        baseMapper.settlement();
    }

    @Override
    public void insertToday(int id) {
        LambdaQueryWrapper<Salvage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Salvage::getUserId, id)
                .ge(Salvage::getCreateTime, DateUtils.getDate(0))
                .lt(Salvage::getCreateTime, DateUtils.getDate(1));
        Salvage salvage = getOne(queryWrapper);
        if (salvage == null) {
            salvage = new Salvage().setCreateTime(LocalDateTime.now()).setUserId(id)
                    .setStatus(0).setProfit(BigDecimal.ZERO.doubleValue())
                    .setGetSalvage(BigDecimal.ZERO).setSalvage(BigDecimal.ZERO);
            save(salvage);
        }
    }

    @Override
    public void getSalvage(List<Integer> rewardIds) {
        baseMapper.getSalvage(rewardIds);
    }


}
