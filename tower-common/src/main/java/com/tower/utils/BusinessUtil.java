package com.tower.utils;

import com.tower.exception.BusinessException;
import com.tower.exception.BusinessExceptionCode;
import org.springframework.util.StringUtils;

/**
 * @author 梦-屿-千-寻
 * @date2021/3/16 15:24
 */
public class BusinessUtil {

    /**
     * 空校验（null or ""）
     */
    public static void require(Object str, BusinessExceptionCode code) throws BusinessException {
        if (StringUtils.isEmpty(str)) {
            throw new BusinessException(code.getDesc()+"不能为空");
        }
    }
    /**
     * 空校验（null or ""）
     */
    public static void require(Object str, String message) throws BusinessException {
        if (StringUtils.isEmpty(str)) {
            throw new BusinessException(message);
        }
    }
    /**
     * 断言判断，false则抛出异常
     */
    public static void assertParam(boolean param, String message) {
        if (!param) {
            throw new BusinessException(message);
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
            throw new BusinessException(code.getDesc()+"长度在"+min+"-"+max);
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
        if (length != min ) {
            throw new BusinessException(code.getDesc()+"长度是"+min);
        }
    }
}
