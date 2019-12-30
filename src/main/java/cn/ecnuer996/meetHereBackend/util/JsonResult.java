package cn.ecnuer996.meetHereBackend.util;

/**
 * @author LuoChengLing
 */
public class JsonResult<T> {

    private T result;
    private int code;
    private String message;

    public static final int SUCCESS=200;
    public static final int FAIL=400;

    /**
     * 若没有数据返回，默认状态码SUCCESS，提示信息为“操作成功！”
     */
    public JsonResult(){
        this.code=JsonResult.SUCCESS;
        this.message="操作成功！";
    }

    /**
     * 若没有数据返回，可以人为指定状态码和信息
     * @param code
     * @param message
     */
    public JsonResult(int code, String message){
        this.code=code;
        this.message=message;
    }

    /**
     * 有数据返回，返回默认的code和message
     * @param result
     */
    public JsonResult(T result){
        this.result=result;
        this.code=SUCCESS;
        this.message="操作成功！";
    }

    /**
     * 有数据返回，指定返回的message
     * @param result
     * @param message
     */
    public JsonResult(T result,String message){
        this.result=result;
        this.code=SUCCESS;
        this.message=message;
    }

    // get和set方法

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
