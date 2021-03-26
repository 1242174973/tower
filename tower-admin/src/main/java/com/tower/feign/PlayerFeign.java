package com.tower.feign;

import com.tower.entity.Player;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author 梦-屿-千-寻
 * @date 2021/3/26 16:32
 */
@FeignClient("tower-game")
@RequestMapping("/feign/player")
public interface PlayerFeign {
    /**
     * 服务提供者提供的接口，也就是自己可以通过这个接口获取想要的数据
     *
     * @param player 玩家信息
     */
    @PostMapping("/save")
    void save(@RequestBody Player player);
}
