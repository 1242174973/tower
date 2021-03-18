package com.tower.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tower.entity.User;
import com.tower.mapper.UserMapper;
import com.tower.service.UserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 梦屿千寻
 * @since 2021-03-18
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
