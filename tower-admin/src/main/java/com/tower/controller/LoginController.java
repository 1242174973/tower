package com.tower.controller;

import com.tower.dto.LoginUserDto;
import com.tower.dto.ResponseDto;
import com.tower.dto.UserDto;
import com.tower.service.UserService;
import com.tower.utils.JsonUtils;
import com.tower.utils.RedisOperator;
import com.tower.utils.UuidUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author 梦-屿-千-寻
 * @date 2021/3/19 17:42
 */
@RestController
@RequestMapping("/login")
@Slf4j
public class LoginController {

    @Resource
    private RedisOperator redisOperator;

    @Resource
    private UserService userService;

    @PostMapping("/login")
    public ResponseDto<LoginUserDto> login(@RequestBody UserDto userDto){
        log.info("用户登录开始");
        userDto.setPassword(DigestUtils.md5DigestAsHex(userDto.getPassword().getBytes()));
        ResponseDto<LoginUserDto> responseDto = new ResponseDto<>();

        // 根据验证码token去获取缓存中的验证码，和用户输入的验证码是否一致
        String imageCode =  redisOperator.get(userDto.getImageCodeToken());
        log.info("从redis中获取到的验证码：{}", imageCode);
        if (StringUtils.isEmpty(imageCode)) {
            responseDto.setSuccess(false);
            responseDto.setMessage("验证码已过期");
            log.info("用户登录失败，验证码已过期");
            return responseDto;
        }
        if (userDto.getImageCode()==null||!imageCode.toLowerCase().equals(userDto.getImageCode().toLowerCase())) {
            responseDto.setSuccess(false);
            responseDto.setMessage("验证码不对");
            log.info("用户登录失败，验证码不对");
            return responseDto;
        } else {
            // 验证通过后，移除验证码
            redisOperator.del(userDto.getImageCodeToken());
        }

        LoginUserDto loginUserDto = userService.login(userDto);
        String token = UuidUtil.getShortUuid();
        loginUserDto.setToken(token);
        redisOperator.set(token, JsonUtils.objectToJson(loginUserDto), 3600*24);
        responseDto.setContent(loginUserDto);
        return responseDto;
    }
}
