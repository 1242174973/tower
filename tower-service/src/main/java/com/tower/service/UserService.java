package com.tower.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tower.dto.LoginUserDto;
import com.tower.dto.UserDto;
import com.tower.entity.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 梦屿千寻
 * @since 2021-03-18
 */
public interface UserService extends IService<User> {
    /**
     * 登录
     * @param userDto user登录对象
     * @return 登录成功对象
     */
    LoginUserDto login(UserDto userDto);
}
