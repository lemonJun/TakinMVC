package com.takin.mvc.mvc.bind;

/**
 * 校验和数据绑定时产生错误的存储实体
 * 
 * @author lemon
 *
 */
public class CheckedError {

    /**
     * 错误来源：绑定，校验
     */
    private ErrorType errorType;

    /**
     * 绑定对象的名称
     */
    private String targetName;

    /**
     * 错误消息
     */
    private String message;

    public CheckedError(ErrorType errorType, String targetName, String message) {
        super();
        this.errorType = errorType;
        this.targetName = targetName;
        this.message = message;
    }

    /**
     * @return the targetName
     */
    public String getTargetName() {
        return targetName;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the errorType
     */
    public ErrorType getErrorType() {
        return errorType;
    }

    @Override
    public String toString() {
        return "ValidateError [targetName=" + targetName + ", message=" + message + "]";
    }

    public enum ErrorType {
        BIND, VALIDATE
    }
}
