package cc.wulian.wrecord.record.app;

import android.util.ArrayMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import cc.wulian.wrecord.record.IRecord;
import cc.wulian.wrecord.C;

/**
 * Created by Veev on 2017/8/8
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    AppRecord
 */

public class AppRecord implements IRecord {

    public String stay_time;
    public String action;

    private AppRecord() {}

    private AppRecord(String action, String stay_time) {
        this.action = action;
        this.stay_time = stay_time;
    }

    public static AppRecord AppStartRecord(String action) {
        return new AppRecord(C.record.app_start, null);
    }

    public static AppRecord AppExitRecord(String action) {
        return new AppRecord(C.record.app_exit, null);
    }

    public static AppRecord AppStayRecord(String action, long stay_time) {
        return new AppRecord(C.record.app_stay, "" + stay_time);
    }

    public static AppRecord parseInJson(JSONObject json) {
        AppRecord record = new AppRecord();
        record.stay_time = json.optString("stay_time");
        record.action = json.optString("action");
        return record;
    }

    public static AppRecord parseInJsonStr(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);
            return parseInJson(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new AppRecord();
    }

    @Override
    public String toJsonStr() {
        return toJson().toString();
    }

    @Override
    public JSONObject toJson() {
        Map<String, String> map = new ArrayMap<>();
        map.put("stay_time", stay_time);
        map.put("action", action);
        JSONObject json = new JSONObject(map);
        return json;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AppRecord{");
        sb.append("stay_time='").append(stay_time).append('\'');
        sb.append(", action='").append(action).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
