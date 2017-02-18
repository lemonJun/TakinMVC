package lemon.mvc.mvc.bind;

/**
 * WF自实现的错误对象实体
 * @author lemon
 *
 */
public class ObjectError {

    private String objectName;

    private String errorMessage;

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String toString() {
        return "Error in object '" + this.objectName + "': " + errorMessage;
    }
}
