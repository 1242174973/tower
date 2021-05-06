package com.tower.config;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xxxx
 * @date 2021/4/14 15:43
 */
@Data
public class VipConfig {

    public VipConfig() {
        map = new HashMap<>();
        map.put(1, 100);
        map.put(2, 2000);
        map.put(3, 20000);
        map.put(4, 50000);
        map.put(5, 100000);
        map.put(6, 300000);
        map.put(7, 600000);
        map.put(8, 1000000);
        map.put(9, 2000000);
        map.put(10, 5000000);
    }
    private Map<Integer, Integer> map;

}
