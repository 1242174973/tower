package com.tower.utils;

import com.tower.exception.BusinessExceptionCode;
import com.tower.exception.ServerException;
import org.springframework.util.StringUtils;

/**
 * @author xxxx
 * @date2021/3/16 15:24
 */
public class ServerUtil {

    /**
     * 空校验（null or ""）
     */
    public static void require(Object str, BusinessExceptionCode code) throws ServerException {
        if (StringUtils.isEmpty(str)) {
            throw new ServerException(code.getDesc() + "不能为空");
        }
    }

    /**
     * 断言判断，false则抛出异常
     */
    public static void assertParam(boolean param, String message) {
        if (!param) {
            throw new ServerException(message);
        }
    }

    /**
     * 长度校验
     */
    public static void length(String str, BusinessExceptionCode code, int min, int max) {
        if (StringUtils.isEmpty(str)) {
            return;
        }
        int length = 0;
        if (!StringUtils.isEmpty(str)) {
            length = str.length();
        }
        if (length < min || length > max) {
            throw new ServerException(code.getDesc() + "长度在" + min + "-" + max);
        }
    }

    /**
     * 长度校验
     */
    public static void length(String str, BusinessExceptionCode code, int min) {
        if (StringUtils.isEmpty(str)) {
            return;
        }
        int length = 0;
        if (!StringUtils.isEmpty(str)) {
            length = str.length();
        }
        if (length != min) {
            throw new ServerException(code.getDesc() + "长度是" + min);
        }
    }
}
