package com.tower.enums;

/**
 * @author xxxx
 * @date 2021/3/26 10:42
 */
public enum AuditType {
    //未结算
    AUDIT(0,"审核中"),
    REMITTANCE(1,"汇款中"),
    SUCCESS(2,"成功"),
    ERROR(10,"失败"),
    ;
    private int code;
    private String desc;

    AuditType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
