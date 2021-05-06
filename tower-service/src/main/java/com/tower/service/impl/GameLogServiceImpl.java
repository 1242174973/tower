package com.tower.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tower.entity.GameLog;
import com.tower.mapper.GameLogMapper;
import com.tower.service.GameLogService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xxxx
 * @since 2021-04-06
 */
@Service
public class GameLogServiceImpl extends ServiceImpl<GameLogMapper, GameLog> implements GameLogService {

}
