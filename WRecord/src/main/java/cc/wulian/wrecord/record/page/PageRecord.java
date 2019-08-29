package cc.wulian.wrecord.record.page;

import android.util.ArrayMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import cc.wulian.wrecord.record.IRecord;
import cc.wulian.wrecord.C;

/**
 * Created by Veev on 2017/8/4
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    PageRecord
 */

public class PageRecord implements IRecord {

    public String pid;
    public String source;
    public String target;
    public String stay_time;
    public String action;

    private PageRecord() {}

    public PageRecord(String pid, String source, String target, String stay_time, String action) {
        this.pid = pid;
        this.source = source;
        this.target = target;
        this.stay_time = stay_time;
        this.action = action;
    }

    public static PageRecord PageStartRecord(String pid) {
        return new PageRecord(pid, null, null, null, C.record.page_start);
    }

    public static PageRecord PageEndRecord(String pid) {
        return new PageRecord(pid, null, null, null, C.record.page_end);
    }

    public static PageRecord PageStayRecord(String pid, long stay_time) {
        return new PageRecord(pid, null, null, "" + stay_time, C.record.page_stay);
    }

    public static PageRecord parseInJson(JSONObject json) {
        PageRecord record = new PageRecord();
        record.pid = json.optString("pid");
        record.source = json.optString("source");
        record.target = json.optString("target");
        record.stay_time = json.optString("stay_time");
        record.action = json.optString("action");
        return record;
    }

    public static PageRecord parseInJsonStr(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);
            return parseInJson(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new PageRecord();
    }

    @Override
    public JSONObject toJson() {
        Map<String, String> map = new ArrayMap<>();
        map.put("pid", pid);
        map.put("source", source);
        map.put("target", target);
        map.put("stay_time", stay_time);
        map.put("action", action);
        JSONObject json = new JSONObject(map);
        return json;
    }

    @Override
    public String toJsonStr() {
        return toJson().toString();
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PageRecord{");
        sb.append("pid='").append(pid).append('\'');
        sb.append(", source='").append(source).append('\'');
        sb.append(", target='").append(target).append('\'');
        sb.append(", stay_time='").append(stay_time).append('\'');
        sb.append(", action='").append(action).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
