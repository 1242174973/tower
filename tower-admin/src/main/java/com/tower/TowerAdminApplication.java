package com.tower;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author 梦-屿-千-寻
 * @date2021/3/16 14:38
 */
@SpringBootApplication
@EnableFeignClients
public class TowerAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(TowerAdminApplication.class, args);
    }
}
