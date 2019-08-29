package cc.wulian.smarthomev6.utils;

import static junit.framework.Assert.assertTrue;

/**
 * Created by 赵永健 on 2018/1/23.
 */
public class Result {
    private StringBuilder builder;
    private Result result;
    private boolean success;

    public Result() {
        this.builder = new StringBuilder();
    }

    public Result(boolean success) {
        this.builder = new StringBuilder();
        this.success = success;
    }


    public boolean isSuccess(){
        return true;
    }

    public int getData(){
        return 1;
    }


    public Result fail(String message){
        assertTrue(message,false);
        Result re=new Result();
        return re;
    }

    public Result success(int index){
        Result re=new Result();
        return re;
    }

}
