package com.tower.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @author xxxx
 * @date2021/3/16 14:44
 */
@ApiModel("返回信息")
public class ResponseDto<T> implements Serializable {

    private static final long serialVersionUID = -6814382603612799610L;

    @ApiModelProperty("业务上的成功或失败")
    private boolean success = true;

    @ApiModelProperty("返回码")
    private String code;

    @ApiModelProperty("返回信息")
    private String message;

    @ApiModelProperty("返回泛型数据，自定义类型")
    private T content;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ResponseDto{");
        sb.append("success=").append(success);
        sb.append(", code='").append(code).append('\'');
        sb.append(", message='").append(message).append('\'');
        sb.append(", content=").append(content);
        sb.append('}');
        return sb.toString();
    }
}
