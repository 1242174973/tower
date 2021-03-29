package com.tower.core.game;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author 梦-屿-千-寻
 * @date 2021/3/29 17:23
 */
@Component
@Data
public class TowerGame {
    /**
     * 开始时间
     */
    private long startTime;
    /**
     * 进攻时间
     */
    private long awardTime;
    /**
     * 结束时间
     */
    private long endTime;
    /**
     * 是否已经进攻
     */
    private boolean award;

    /**
     * 怪物id
     */
    private Integer monsterId;

    /**
     * 游戏初始化
     */
    public void init() {
        setAward(true);
    }

    /**
     * 发送开始游戏
     */
    public void gameStart() {

    }

    /**
     * 出怪
     */
    public void award() {
    }
}
