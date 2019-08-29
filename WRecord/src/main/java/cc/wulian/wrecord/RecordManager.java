package cc.wulian.wrecord;

import android.app.Activity;
import android.text.TextUtils;
import android.util.ArrayMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cc.wulian.wrecord.record.IRecord;
import cc.wulian.wrecord.record.Record;
import cc.wulian.wrecord.record.app.AppRecord;
import cc.wulian.wrecord.record.page.PageMap;
import cc.wulian.wrecord.record.page.PageRecord;
import cc.wulian.wrecord.record.page.PageRecordCreateListener;
import cc.wulian.wrecord.utils.Preferences;
import cc.wulian.wrecord.utils.WLog;

/**
 * Created by Veev on 2017/8/3
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    RecordManager
 */

public class RecordManager {
    private static final String TAG = "WRecord_Manager";

    private static int MAX_COUNT = 20;

    private List<Record> mRecordList;

    public RecordManager() {
        mRecordList = new ArrayList<>();
        for (Object o : Preferences.get(C.pref.name_record_record).getAll().values()) {
            if (o instanceof String) {
                Record record = Record.parseInJsonStr((String) o);
                mRecordList.add(record);
//                WLog.i(TAG, "recordAppStart: " + record);
            }
        }

        registerActivityLifecycleCallbacks();
    }

    public void setMaxCount(int count) {
        MAX_COUNT = count;
    }

    private WActivityLifecycleListener mLifecycleListener = new WActivityLifecycleListener();

    private void registerActivityLifecycleCallbacks() {
        WRecord.getApp().registerActivityLifecycleCallbacks(mLifecycleListener);
    }

    public void fragmentStart(Object fragment, Activity activity) {
        mLifecycleListener.onFragmentStart(fragment, activity);
    }

    public void fragmentEnd(Object fragment, Activity activity) {
        mLifecycleListener.onFragmentEnd(fragment, activity);
    }

    // *********************************************************************************************
    // ************************************ PageRecord *********************************************
    // *********************************************************************************************

    // 记录页面停留时间
    private Map<Activity, Long> mPageStayMap = new ArrayMap<>();
    // source 页面
    private Map<Activity, String> mPageSourceMap = new ArrayMap<>();

    // 页面打点类型
    private int pageRecordType = PAGE_STAY;
    // 进入页面打点
    public static final int PAGE_START = 1;
    // 离开页面打点
    public static final int PAGE_END = 2;
    // 页面停留时间打点
    public static final int PAGE_STAY = 4;
    // 是否只记录map中的页面
    private boolean isOnlyRecordMap = false;
    // 记录创建时的监听
    private PageRecordCreateListener mPageRecordCreateListener;

    public void setPageRecordCreateListener(PageRecordCreateListener pageRecordCreateListener) {
        mPageRecordCreateListener = pageRecordCreateListener;
    }

    public void interceptPageCreate(Activity activity, PageRecord record) {
        if (mPageRecordCreateListener != null) {
            mPageRecordCreateListener.onCreate(activity, record);
        }
    }

    /**
     * 设置页面别名
     * @param map               页面别名映射
     *                          key:    页面报名+类名
     *                          value:  别名
     * @param onlyRecordMap     是否只记录这个映射中的页面, 默认否
     */
    public void setPageMap(Map<String, String> map, boolean onlyRecordMap) {
        PageMap.setPageMap(map);
        isOnlyRecordMap = onlyRecordMap;
    }

    /**
     * 记录activity的开始时间
     */
    public void pageStart(Activity activity) {
        mPageStayMap.put(activity, System.currentTimeMillis());

        if ((pageRecordType & PAGE_START) == PAGE_START) {
            recordPageStart(activity);
        }
    }

    public void setPageSource(Activity activity, String source) {
        mPageSourceMap.put(activity, source);
    }

    /**
     * 记录activity的开始时间
     * @param isRecordStart     是否记录页面开始
     *                          true    记录
     *                          false   不记录
     */
    public void pageStart(Activity activity, boolean isRecordStart) {
        mPageStayMap.put(activity, System.currentTimeMillis());

        if (isRecordStart) {
            recordPageStart(activity);
        }
    }

    /**
     * 获取activity的停留时间
     */
    public long getPageStayTime(Activity activity) {
        Long l = mPageStayMap.get(activity);
        if (l == null) {
            return 0;
        }
        return System.currentTimeMillis() - mPageStayMap.get(activity);
    }

    /**
     * 记录Activity的结束时间, 返回并清空停留时间
     */
    public long pageEnd(Activity activity, String target) {
        if ((pageRecordType & PAGE_END) == PAGE_END) {
            recordPageEnd(activity, target);
        }
        if ((pageRecordType & PAGE_STAY) == PAGE_STAY) {
            recordPageStay(activity, target);
        }

        mPageSourceMap.remove(activity);
        return getPageStayTime(activity);
    }

    /**
     * 设置页面记录类型 此设置为全局设置
     * @param type      页面的记录类型
     *                  {@link RecordManager#PAGE_START}
     *                  {@link RecordManager#PAGE_END}
     *                  {@link RecordManager#PAGE_STAY}
     *                  传入要记录的页面事件即可
     *                  如果要记录多个事件, 传入  PAGE_START | PAGE_END
     */
    public void setPageRecordType(int type) {
        pageRecordType = type;
    }

    private void recordPageStart(Activity activity) {
        String name = PageMap.getNameFromMap(activity.getClass().getName());
        if (TextUtils.isEmpty(name)) {
            if (isOnlyRecordMap) {
                return;
            }
            name = activity.getClass().getName();
            PageMap.put(name, name);
        }

        IRecord iRecord = new PageRecord(name, mPageSourceMap.get(activity), null, null, C.record.page_start);
        interceptPageCreate(activity, (PageRecord) iRecord);
        Record record = createRecord("C1", iRecord);
        put(record);

        WLog.json(TAG, "PageStart: " + record.toJsonStr());
    }

    private void recordPageEnd(Activity activity, String target) {
        String name = PageMap.getNameFromMap(activity.getClass().getName());
        if (TextUtils.isEmpty(name)) {
            if (isOnlyRecordMap) {
                return;
            }
            name = activity.getClass().getName();
            PageMap.put(name, name);
        }

        IRecord iRecord = new PageRecord(name, mPageSourceMap.get(activity), target, null, C.record.page_end);
        interceptPageCreate(activity, (PageRecord) iRecord);
        Record record = createRecord("C1", iRecord);
        put(record);

        WLog.json(TAG, "PageEnd: " + name);
    }

    private void recordPageStay(Activity activity, String target) {
        String name = PageMap.getNameFromMap(activity.getClass().getName());
        if (TextUtils.isEmpty(name)) {
            if (isOnlyRecordMap) {
                return;
            }
            name = activity.getClass().getName();
            PageMap.put(name, name);
        }

        long stayTime = getPageStayTime(activity);
        IRecord iRecord = new PageRecord(name, mPageSourceMap.get(activity), target, "" + stayTime, C.record.page_stay);
        interceptPageCreate(activity, (PageRecord) iRecord);
        Record record = createRecord("C1", iRecord);
        put(record);

        WLog.json(TAG, "PageStay: " + record.toJsonStr());
    }

    // *********************************************************************************************
    // ************************************* AppRecord *********************************************
    // *********************************************************************************************

    // APP 打点类型
    private int appRecordType = APP_START | APP_STAY;
    // 进入 APP 打点
    public static final int APP_START = 1;
    // 离开 APP 打点
    public static final int APP_EXIT = 2;
    // APP 停留时间打点
    public static final int APP_STAY = 4;

    public void setAppRecordType(int type) {
        appRecordType = type;
    }

    public void appStart(boolean isRecordStart) {
        Preferences.get(C.pref.name_record_app).put(C.pref.key_app_start, System.currentTimeMillis());

        if (isRecordStart) {
            recordAppStart();
        }
    }

    public void appStart() {
        Preferences.get(C.pref.name_record_app).put(C.pref.key_app_start, System.currentTimeMillis());

        if ((appRecordType & APP_START) == APP_START) {
            recordAppStart();
        }
    }

    public void appExit() {
        Preferences.get(C.pref.name_record_app).put(C.pref.key_app_exit, System.currentTimeMillis());

        if ((appRecordType & APP_EXIT) == APP_EXIT) {
            recordAppExit();
        }
        if ((appRecordType & APP_STAY) == APP_STAY) {
            recordAppStay();
        }
    }

    public long getAppStayTime() {
        long lastStart = Preferences.get(C.pref.name_record_app).getLong(C.record.app_start);
        long time = System.currentTimeMillis();
        long delta = time - lastStart;
        return delta;
    }

    private void recordAppStart() {
        long lastExit = Preferences.get(C.pref.name_record_app).getLong(C.pref.key_app_exit);
        long time = System.currentTimeMillis();
        long delta = time - lastExit;
        if (lastExit == -1L) {
            // 首次启动
            WLog.i(TAG, "首次启动");
        }

        if (delta > 30 * 1000) {
            Record record = createRecord("A1", AppRecord.AppStartRecord(C.record.app_start));
            put(record);

            WLog.json(TAG, "StartApp: " + record.toJsonStr());
        }
    }

    private void recordAppStay() {
        long lastStart = Preferences.get(C.pref.name_record_app).getLong(C.pref.key_app_start);
        long time = System.currentTimeMillis();
        long delta = time - lastStart;

        Record record = createRecord("A2", AppRecord.AppStayRecord(C.record.app_stay, delta));
        put(record);
        // App 退到后台, 上传记录
        post();

        WLog.json(TAG, "StayApp: " + record.toJsonStr());
    }

    private void recordAppExit() {
        Record record = createRecord("A1", AppRecord.AppExitRecord(C.record.app_exit));
        put(record);

        WLog.json(TAG, "ExitApp: " + record.toJsonStr());
    }


    // *********************************************************************************************
    // *************************************    Base   *********************************************
    // *********************************************************************************************

    public void postSuccess(List<Record> list) {
        WLog.i(TAG, "上传成功: " + list);
        Preferences.get().put(C.pref.key_last_post, System.currentTimeMillis());
    }

    public void postSuccess(JSONArray array) {
        WLog.i(TAG, "上传成功: " + array);
        Preferences.get().put(C.pref.key_last_post, System.currentTimeMillis());
    }

    public void postFailure(List<Record> list) {
        WLog.i(TAG, "上传失败: " + list);
        WLog.i(TAG, "Count Now: " + mRecordList.size());

        for (Record r : list) {
            Preferences.get(C.pref.name_record_record).put(r.key(), r.toJsonStr());
            mRecordList.add(0, r);
        }
        WLog.i(TAG, "Count All: " + mRecordList.size());
    }

    public void postFailure(JSONArray array) {
        WLog.i(TAG, "上传失败: " + array);
        WLog.i(TAG, "Count Now: " + mRecordList.size());

        for (int i = 0, count = array.length(); i < count; i++) {
            JSONObject object = array.optJSONObject(i);
            Record r = Record.parseInJson(object);
            Preferences.get(C.pref.name_record_record).put(r.key(), r.toJsonStr());
            mRecordList.add(0, r);
        }

        WLog.i(TAG, "Count All: " + mRecordList.size());
    }

    private PostHandle mPostHandle;

    public void setPostHandle(PostHandle handle) {
        mPostHandle = handle;
    }

    /**
     * 上传记录
     */
    private void post() {
        List<Record> list = new ArrayList<>();
        int i = 0;
        for (Record record : mRecordList) {
            list.add(record);
            Preferences.get(C.pref.name_record_record).remove(record.key());
            if (i ++ >= MAX_COUNT) {
                break;
            }
        }

        mRecordList.removeAll(list);

        postOut(list);
    }

    /**
     * 交给调用的app 去上传
     */
    private void postOut(List<Record> list) {
        try {
            mPostHandle.onRecord(list);

            JSONArray array = new JSONArray();
            for (Record r : list) {
                array.put(r.toJson());
            }
            mPostHandle.onRecord(array);
        } catch (Exception e) {
            e.printStackTrace();
            WLog.e(TAG, "Post Record Error: " + e.getMessage());
        }
    }

    private void checkRecordList() {
        if (mRecordList.size() >= MAX_COUNT) {
            post();
        }
    }

    /**
     * 将记录加入到列表
     */
    private void put(Record record) {
        Preferences.get(C.pref.name_record_record).put(record.key(), record.toJsonStr());
        mRecordList.add(record);

        checkRecordList();

        WLog.i(TAG, "Record Count: " + mRecordList.size());
    }

    /**
     * 创建 记录
     */
    private Record createRecord(String type, IRecord t) {
        Record record = new Record(type, System.currentTimeMillis(), t);
        return record;
    }
}
