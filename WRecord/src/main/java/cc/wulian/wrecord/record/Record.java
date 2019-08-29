package cc.wulian.wrecord.record;

import android.util.ArrayMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import cc.wulian.wrecord.record.app.AppRecord;
import cc.wulian.wrecord.record.page.PageRecord;

/**
 * Created by Veev on 2017/8/3
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    Record
 */

public class Record implements IRecord {

    public String type;
    public String time;
    public String uid;
    public IRecord data;

    public String key() {
        return type + "_" + time;
    }

    public Record() {}

    public Record(String type, long time, IRecord data) {
        this.type = type;
        this.time = time + "";
        this.data = data;
    }

    public static Record parseInJson(JSONObject json) {
        Record record = new Record();
        record.type = json.optString("type");
        record.time = json.optString("time");
        record.uid = json.optString("uid");
        JSONObject object = json.optJSONObject("data");
        switch (record.type) {
            case "A1":
            case "A2":
                record.data = AppRecord.parseInJson(object);
                break;
            case "C1":
                record.data = PageRecord.parseInJson(object);
                break;
        }
        return record;
    }

    public static Record parseInJsonStr(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);
            return parseInJson(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new Record();
    }

    @Override
    public String toJsonStr() {
        return toJson().toString();
    }

    @Override
    public JSONObject toJson() {
        Map<String, String> map = new ArrayMap<>();
        map.put("type", type);
        map.put("time", time);
        map.put("uid", uid);
        JSONObject json = new JSONObject(map);
        try {
            json.put("data", data.toJson());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Record{");
        sb.append("type='").append(type).append('\'');
        sb.append(", time='").append(time).append('\'');
        sb.append(", uid='").append(uid).append('\'');
        sb.append(", data=").append(data.toString());
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Record record = (Record) o;

        if (type != null ? !type.equals(record.type) : record.type != null) return false;
        return time != null ? time.equals(record.time) : record.time == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (time != null ? time.hashCode() : 0);
        return result;
    }
}
