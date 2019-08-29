package cc.wulian.wrecord;

import org.json.JSONArray;

import java.util.List;

import cc.wulian.wrecord.record.Record;

/**
 * Created by Veev on 2017/8/10
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    PostHandleAdapter
 */

public abstract class PostHandleAdapter implements PostHandle {
    @Override
    public void onRecord(List<Record> list) {

    }

    @Override
    public void onRecord(JSONArray array) {

    }
}
