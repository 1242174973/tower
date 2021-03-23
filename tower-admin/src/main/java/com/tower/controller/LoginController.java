package com.tower.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tower.dto.LoginUserDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.UserDto;
import com.tower.entity.User;
import com.tower.exception.BusinessException;
import com.tower.service.UserService;
import com.tower.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author 梦-屿-千-寻
 * @date 2021/3/19 17:42
 */
@RestController
@RequestMapping("/login")
@Slf4j
@Api(value = "登录", tags = "登录相关请求")
public class LoginController {

    @Resource
    private RedisOperator redisOperator;

    @Resource
    private UserService userService;



    @PostMapping("/login")
    @ApiOperation(value = "登录请求", notes = "登录请求")
    public ResponseDto<UserDto> login(@RequestBody UserDto userDto){
        log.info("用户登录开始");
        userDto.setPassword(DigestUtils.md5DigestAsHex(userDto.getPassword().getBytes()));
        assertCode(userDto);

        LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getLoginName,userDto.getLoginName());
        User mysqlUser = userService.getOne(lambdaQueryWrapper);

        BusinessUtil.assertParam(mysqlUser!=null,"用户没找到");
        BusinessUtil.assertParam(mysqlUser.getPassword().equals(userDto.getPassword()),"密码错误");
        ResponseDto<UserDto> responseDto = new ResponseDto<>();
        String token = UuidUtil.getShortUuid();
        userDto.setToken(token);
        userDto.setName(mysqlUser.getName());
        redisOperator.set(token, JsonUtils.objectToJson(mysqlUser), 3600*24);
        responseDto.setContent(userDto);
        return responseDto;
    }
    public void assertCode(UserDto userDto){
        // 根据验证码token去获取缓存中的验证码，和用户输入的验证码是否一致
        String imageCode =  redisOperator.get(userDto.getImageCodeToken());
        log.info("从redis中获取到的验证码：{}", imageCode);
        BusinessUtil.assertParam(imageCode!=null,"验证码已过期");
        BusinessUtil.assertParam(imageCode.toLowerCase().equals(userDto.getImageCode()),"验证码错误");
        // 验证通过后，移除验证码
        redisOperator.del(userDto.getImageCodeToken());
    }

    /**
     * 退出登录
     */
    @GetMapping("/logout/{token}")
    public ResponseDto logout(@PathVariable String token) {
        ResponseDto responseDto = new ResponseDto();
        redisOperator.del(token);
        log.info("从redis中删除token:{}", token);
        return responseDto;
    }

    /**
     * 修改密码
     */
    @PostMapping("/updatePassword/{oldPassword}/{newPassword}")
    public ResponseDto updatePassword(@PathVariable String oldPassword, @PathVariable String newPassword, User user) {
        LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getLoginName,user.getLoginName());
        User mysqlUser = userService.getOne(lambdaQueryWrapper);
        if (mysqlUser == null || !mysqlUser.getPassword().equals(DigestUtils.md5DigestAsHex(oldPassword.getBytes()))) {
            throw new BusinessException("旧密码错误");
        }
        mysqlUser.setPassword(DigestUtils.md5DigestAsHex(newPassword.getBytes()));
        userService.saveOrUpdate(mysqlUser);
        return new ResponseDto();
    }

}
