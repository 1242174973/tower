package com.tower.exception;

/**
 * 错误描述
 *
 * @author Administrator
 */
public enum BusinessExceptionCode {
    //账号
    ACCOUNT("账号"),
    NICK_NAME("昵称"),
    PIC_CODE("图片验证码"),
    CODE_TOKEN("图片token"),
    PASSWORD("密码"),
    SPREAD("推广码"),
    OLD_PASSWORD("旧密码"),
    NEW_PASSWORD("新密码"),
    TOKEN("token"),
    VIP_LEVEL("vip等级"),
    BANK_CARD_NAME("持卡人"),
    BANK_CARD_NUM("银行卡号"),
    BANK("银行"),
    SUB_BRANCH("支行"),
    PROVINCE("省"),
    CITY("市"),
    ;
    /**
     * 描述
     */
    private String desc;

    /**
     * 错误描述
     *
     * @param desc 错误描述
     */
    BusinessExceptionCode(String desc) {
        this.desc = desc;
    }

    /**
     * 错误描述
     *
     * @return
     */
    public String getDesc() {
        return desc;
    }
}
