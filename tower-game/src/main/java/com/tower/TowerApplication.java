package com.tower;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author 梦-屿-千-寻
 * @date2021/3/16 14:38
 */
@SpringBootApplication
@MapperScan("com.tower.mapper")
public class TowerApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(TowerApplication.class, args);
        GameServer bean = context.getBean(GameServer.class);
        bean.start();
    }
}
