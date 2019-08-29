package cc.wulian.wrecord;

import org.json.JSONArray;

import java.util.List;

import cc.wulian.wrecord.record.Record;

/**
 * Created by Veev on 2017/8/10
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    PostHandle
 */

public interface PostHandle {
    void onRecord(List<Record> list);
    void onRecord(JSONArray array);
}
