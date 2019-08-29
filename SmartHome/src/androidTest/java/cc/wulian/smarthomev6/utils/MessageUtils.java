package cc.wulian.smarthomev6.utils;


import cc.wulian.smarthomev6.app.StartApp;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by 赵永健 on 2017/5/8.
 *
 * 封装错误信息的相关操作
 */
public class MessageUtils {
    public static void append(String message){
          assertEquals(message, true, false);
//        StartApp.append(null==message ? "" : message);
    }

    public static boolean isEmpty(){
        return StartApp.builder.toString().isEmpty();
    }

}
