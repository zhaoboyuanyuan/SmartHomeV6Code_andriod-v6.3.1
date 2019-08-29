package cc.wulian.wrecord;

/**
 * Created by Veev on 2017/8/8
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    常量
 */
public interface C {
    interface pref {
        String name_record = "WRecord";
        // 记录 app 启动相关
        String name_record_app = "WRecord_App";
        // 持久化 record
        String name_record_record = "WRecord_Record";

        String key_app_exit = "key_app_exit";
        String key_app_start = "key_app_start";
        String key_last_post = "key_last_post";
    }

    interface record {
        String page_start = "PAGE_START";
        String page_end = "PAGE_END";
        String page_stay = "PAGE_STAY";

        String app_start = "APP_START";
        String app_exit = "APP_EXIT";
        String app_stay = "APP_STAY";
    }
}
