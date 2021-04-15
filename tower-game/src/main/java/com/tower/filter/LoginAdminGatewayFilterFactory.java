package com.tower.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: 梦-屿-千-寻
 * @Date: 2021/2/2 12:15
 * @Version 1.0
 */
@Component
public class LoginAdminGatewayFilterFactory extends WebMvcConfigurationSupport {
    /**
     * 登录拦截
     */
    @Resource
    private LoginInterceptor loginInterceptor;

    /**
     * 获得token中的user
     */
    @Resource
    private LoginUserArgumentResolver loginUserArgumentResolver;

    /**
     * 使用token 是注入 user
     *
     * @param argumentResolvers
     */
    @Override
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(loginUserArgumentResolver);
    }

    /**
     * 跳过拦截和选择拦截的请求
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/account/**",
                        "/feign/**",
                        "/editionEtc/**",
                        "/customer/**",
                        "/file/imageUpload",
                        "/kaptcha/image-code/**",
                        "/v2/api-docs");
    }

    @Override
    protected void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*")
                .allowCredentials(true).allowedMethods("*").maxAge(3600);
    }

    /**
     * 最重要的一步：在此处指明你在拦截器中排除拦截的静态资源路径指向的是classpath下static路径
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .addResourceLocations("classpath:/templates/")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
                .addResourceLocations("classpath:/META-INF/resources/");
    }
}
