package com.tower.service.my.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tower.entity.Player;
import com.tower.mapper.my.MyPlayerMapper;
import com.tower.service.my.MyPlayerService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xxxx
 * @since 2021-03-16
 */
@Service
public class MyPlayerServiceImpl extends ServiceImpl<MyPlayerMapper, Player> implements MyPlayerService {

    @Override
    public void resetPlayerWithdraw() {
        baseMapper.resetPlayerWithdraw();
    }
}
