package cc.wulian.smarthomev6.support.core.apiunit.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;


/**
 * 通用的应答bean
 */

public class ResponseBean<T> implements Serializable {
    public String requestId;
    public String resultCode;
    public String resultDesc;
    @JSONField(name = "data")
    public T data;

    public int getResultCode() {
        int code = -1;
        try {
            code = Integer.parseInt(resultCode);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return code;
    }

    public boolean isSuccess() {
        int code = -1;
        try {
            code = Integer.parseInt(resultCode);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return code == 0;
    }
}
