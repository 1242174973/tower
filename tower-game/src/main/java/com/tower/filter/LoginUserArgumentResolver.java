package com.tower.filter;

import com.tower.entity.User;
import com.tower.utils.JsonUtils;
import com.tower.utils.RedisOperator;
import com.tower.variable.RedisVariable;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author: 梦-屿-千-寻
 * @Date: 2021/2/2 12:16
 * @Version 1.0
 */

@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Resource
    private RedisOperator redisOperator;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> clazz = parameter.getParameterType();
        return clazz == User.class;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest webRequest, WebDataBinderFactory webDataBinderFactory) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        assert request != null;
        String token = request.getHeader("token");
        // 然后根据token获取用户登录信息
        User user = null;
        if (token != null) {
            // 这边拿到的 token  前往redis获得用户信息返回
            user = JsonUtils.jsonToPojo(redisOperator.hget(RedisVariable.USER_INFO , token), User.class);
        }
        return user;
    }
}
