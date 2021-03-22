package com.tower.filter;

import com.tower.exception.BusinessException;
import com.tower.utils.RedisOperator;
import com.tower.variable.RedisVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: 梦-屿-千-寻
 * @Date: 2021/2/2 12:15
 * @Version 1.0
 */
@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(LoginInterceptor.class);

    @Resource
    private RedisOperator redisOperator;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        // 从 http 请求头中取出 token
        String token = request.getHeader("token");
        LOG.info("后台登录验证开始，token：{}", token);
        if (token == null || token.isEmpty()) {
            LOG.info("token为空，请求被拦截---{}",request.getRequestURI());
            throw new BusinessException("请先登录");
        }
        String object = redisOperator.get(token);
        if (object == null) {
            LOG.warn("token无效，请求被拦截---{}",request.getRequestURI());
            throw new BusinessException("请重新登录");
        } else {
            // 这边拿到的 用户信息 判断权限
//            User user= JsonUtils.jsonToPojo(object, User.class);
//            String requestURI = request.getRequestURI();
//            if(!canAuthority(user.getTgAuthorities(),requestURI)){
//                LOG.info( "权限不够，请求被拦截" );
//                return false;
//            }
            LOG.info("已登录：{}", object);
            return true;
        }
    }

    /*private boolean canAuthority(List<TGAuthority> tgAuthorities, String requestURI) {
        if(tgAuthorities==null||tgAuthorities.size()<=0){
            return false;
        }
        for (TGAuthority tgAuthority : tgAuthorities) {
            if(requestURI.equals(tgAuthority.getPath())){
                return true;
            }
            if(tgAuthority.getPath()!=""&&requestURI.contains(tgAuthority.getPath()+"/")){
                return true;
            }
        }
        return false;
    }*/

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

}
