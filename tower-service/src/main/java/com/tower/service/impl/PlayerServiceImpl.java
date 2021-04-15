package com.tower.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tower.entity.Player;
import com.tower.mapper.PlayerMapper;
import com.tower.service.PlayerService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 梦屿千寻
 * @since 2021-03-16
 */
@Service
public class PlayerServiceImpl extends ServiceImpl<PlayerMapper, Player> implements PlayerService {

    @Override
    public void resetAward() {
        baseMapper.resetAward();
    }
}
