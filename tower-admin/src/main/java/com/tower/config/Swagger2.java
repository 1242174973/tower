package com.tower.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author 梦-屿-千-寻
 * @date2021/1/16 16:42
 */
@Configuration
@EnableSwagger2
public class Swagger2 {

//    http://localhost:8088/swagger-ui.html     原路径
//    http://localhost:8088/doc.html            原路径

    /**
     * 配置swagger2核心配置 docket
     */
    @Bean
    public Docket createRestApi() {
        // 指定api类型为swagger2
        return new Docket(DocumentationType.SWAGGER_2)
                // 用于定义api文档汇总信息
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors
                        // 指定controller包
                        .basePackage("com.tower.controller"))
                // 所有controller
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                // 文档页标题
                .title("平台接口api")
                .contact(new Contact("jun",
                        "https://www.jun.com",
                        // 联系人信息
                        "abc@jun.com"))
                // 详细信息
                .description("api文档")
                // 文档版本号
                .version("1.0.1")
                // 网站地址
                .termsOfServiceUrl("https://www.xxx.com")
                .build();
    }

}
