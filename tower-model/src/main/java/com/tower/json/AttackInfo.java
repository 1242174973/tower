package com.tower.json;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author 梦-屿-千-寻
 * @date 2021/3/29 16:16
 */
@Data
public class AttackInfo {
    private String ver;
    private Integer monsterId;
    private String monsterName;
    private Integer fearWeaponId;
    private String attackProb;
    private List<String> picUrlList;
    private Integer rewardCoin;
    private List<String> rewardGiftsList;
    private Long serverTime;
    private Boolean isFreeze;
}
