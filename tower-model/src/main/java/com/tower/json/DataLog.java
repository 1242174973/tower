package com.tower.json;

import lombok.Data;

import java.util.List;

/**
 * @author 梦-屿-千-寻
 * @date 2021/3/29 16:49
 */
@Data
public class DataLog {
    private String ver;
    private int limit;
    private List<MonsterInfo> monsterInfosList;
    private long serverTime;
    private boolean isFreeze;
}
