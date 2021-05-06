package com.tower.json;

import lombok.Data;

import java.util.List;

/**
 * @author xxxx
 * @date 2021/3/29 16:49
 */
@Data
public class DataLog {
    private String ver;
    private Integer limit;
    private List<MonsterInfo> monsterInfosList;
    private Long serverTime;
    private Boolean isFreeze;
}
