package com.tower.enums;

/**
 * @Author: xxxx
 * @Date: 2021/2/17 13:01
 * @Version 1.0
 */
public enum DynamicTypeEnum {
    NULL(-1,"未知"),
    LOCAL(0,"同城"),
    NEWEST(1,"最新"),
    ANIME(2,"动漫"),
    FICTION(3,"小说"),
    LIVE(4,"生活"),
    ISSUE(101,"我发布的"),
    COMMENT(102,"我评论的"),
    BROWSE(103,"我浏览的"),
    PRAISE(104,"我点赞的"),
    ;
    private Integer code;
    private String desc;
    DynamicTypeEnum(int code, String desc){
        this.code=code;
        this.desc=desc;
    }
    public static DynamicTypeEnum getDynamicType(int code){
        for (DynamicTypeEnum value : values()) {
            if(value.getCode().equals(code)){
                return value;
            }
        }
        return DynamicTypeEnum.NULL;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
