package com.tower.exception;

public class BusinessException extends RuntimeException{


    public BusinessException (BusinessExceptionCode code) {
        super(code.getDesc());
    }
    public BusinessException (String message) {
        super(message);
    }

    /**
     * 不写入堆栈信息，提高性能
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
