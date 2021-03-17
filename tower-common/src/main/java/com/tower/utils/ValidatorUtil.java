package com.tower.utils;

import org.springframework.util.StringUtils;
import com.tower.exception.ValidatorException;

public class ValidatorUtil {

    /**
     * 空校验（null or ""）
     */
    public static void require(Object str, String fieldName) throws ValidatorException {
        if (StringUtils.isEmpty(str)) {
            throw new ValidatorException(fieldName);
        }
    }
    /**
     * 断言判断，false则抛出异常
     */
    public static void assertParam(boolean param, String fieldName) {
        if (!param) {
            throw new ValidatorException(fieldName);
        }
    }
    /**
     * 长度校验
     */
    public static void length(String str, String fieldName, int min, int max) {
        if (StringUtils.isEmpty(str)) {
            return;
        }
        int length = 0;
        if (!StringUtils.isEmpty(str)) {
            length = str.length();
        }
        if (length < min || length > max) {
            throw new ValidatorException(fieldName);
        }
    }
}
