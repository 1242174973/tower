package com.tower.json;

import lombok.Data;

/**
 * @author 梦-屿-千-寻
 * @date 2021/3/29 16:41
 */
@Data
public class StartInfo {
    private String ver;
    private String gameToken;
    private long startTime;
    private long awardTime;
    private long endTime;
    private int status;
    private int kugouId;
    private int mode;
    private String gameName;
    private int leftFightSeconds;
    private int animationDelayBase;
    private String stage;
}
