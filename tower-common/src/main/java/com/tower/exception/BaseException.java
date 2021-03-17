package com.tower.exception;

/**
 * @author 梦-屿-千-寻
 * @date2021/3/17 10:22
 */
public class BaseException extends RuntimeException {
    private static final long serialVersionUID = 3928924179697332043L;


    public BaseException(String msg) {
        super(msg);
    }

    public BaseException(Throwable e) {
        super(e);
    }

    public BaseException(String msg, Throwable e) {
        super(msg, e);
    }

    public BaseException(String msg, Throwable e, boolean enableSuppression, boolean writableStackTrace) {
        super(msg, e, enableSuppression, writableStackTrace);
    }
}
