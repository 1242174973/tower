package com.tower.exception;

/**
 * @author 梦-屿-千-寻
 * @date2021/3/17 10:22
 */
public class ServerException extends BaseException {
    private static final long serialVersionUID = -4615177415004625406L;
    private String msgExt;
    private String msgCode;

    public ServerException(String msg) {
        super(msg);
    }

    public ServerException(Throwable e) {
        super(e);
    }

    public ServerException(String msg, Throwable e) {
        super(msg, e);
    }

    public ServerException(String msg, Throwable e, boolean enableSuppression, boolean writableStackTrace) {
        super(msg, e, enableSuppression, writableStackTrace);
    }

    public ServerException setMsgExt(String msgExt) {
        this.msgExt = msgExt;
        return this;
    }

    public ServerException setMsgCode(String msgCode) {
        this.msgCode = msgCode;
        return this;
    }

    public String getMsgCode() {
        return msgCode;
    }

    public String getMsgExt() {
        return msgExt;
    }
}
