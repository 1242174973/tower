package com.tower.feign;

import com.tower.entity.Player;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author xxxx
 * @date 2021/3/26 16:32
 */
@FeignClient("tower-game-dev")
@RequestMapping("/feign/player")
public interface PlayerFeign {
    /**
     * 服务提供者提供的接口，也就是自己可以通过这个接口获取想要的数据
     *
     * @param player 玩家信息
     */
    @PostMapping("/save")
    void save(@RequestBody Player player);

    /**
     * 根据玩家id获得玩家信息
     *
     * @param id 玩家id
     * @return 玩家信息
     */
    @GetMapping("/playerInfo/{id}")
    Player playerInfo(@PathVariable int id);

    /**
     * 根据玩家id获得玩家信息
     *
     * @return 玩家信息
     */
    @GetMapping("/getStatus")
    int getStatus();

    /**
     * 根据玩家id获得玩家信息
     */
    @GetMapping("/setStatus/{status}")
    void setStatus(@PathVariable int status);

    /**
     * 根据玩家id获得玩家信息
     *
     * @param playerId 玩家id
     * @return 返回玩家
     */
    @GetMapping("/getPlayer/{playerId}")
    Player getPlayer(@PathVariable int playerId);

    /**
     * 清空数据
     *
     * @param password 密码
     */
    @GetMapping("/removeAll/{password}")
    void removeAll(@PathVariable String password);

    /** 清空数据
     */
    @GetMapping("/removePlayer/{id}")
    void removePlayer(@PathVariable int id);
}
