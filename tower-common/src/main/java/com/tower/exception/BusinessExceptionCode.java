package com.tower.exception;

/**
 * 错误描述
 *
 * @author Administrator
 */
public enum BusinessExceptionCode {
    //账号
    ID("id"),
    SIGN_IN("签到"),
    TOTAL_SIGN_IN("总签到"),
    MONEY("余额"),
    SAFE_BOX("保险柜余额"),
    EXPERIENCE("经验"),
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
    LOGIN_USER_ERROR("用户名错误"),
    LOGIN_PASSWORD_ERROR("密码错误"),
    TURRET_NAME("炮塔名字"),
    MONSTER_NAME("怪物名字"),
    MULTIPLE("倍数"),
    MAX_BET("最大下注"),
    RATES("出现概率"),
    AWARD("奖励"),
    AWARD_TYPE("奖励类型"),
    DAY("第几天"),
    LOGIN_NAME("登录名"),
    NAME("名称"),
    TODAY_WITHDRAW_MONEY("今日可提现金额"),
    TOTAL_WITHDRAW_MONEY("每日可提现金额"),
    TODAY_WITHDRAW_SIZE("今日可提现次数"),
    TOTAL_WITHDRAW_SIZE("每日可提现次数"),
    SERVICE_CHARGE("提现手续费"),
    TYPE("类型"),
    MODEL("模式"),
    PAYEE("持卡人"),
    TOP_UP_MONEY("充值金额"),
    TOP_UP_ID("充值信息ID"),
    RECEPTION_ID("接收人"),
    COIN("分数"),
    USER_ID("玩家id"),
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
