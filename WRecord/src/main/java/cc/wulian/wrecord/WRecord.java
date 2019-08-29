package cc.wulian.wrecord;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import org.json.JSONArray;

import java.util.List;
import java.util.Map;

import cc.wulian.wrecord.record.Record;
import cc.wulian.wrecord.record.page.PageRecordCreateListener;

/**
 * Created by Veev on 2017/8/4
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    WRecord
 */

public class WRecord {

    private static Application mApp;
    private static RecordManager sManager;
    public static boolean sCouldLog = false;

    public static void init(Application app) {
        mApp = app;
        sManager = new RecordManager();
    }

    public static Context getContext() {
        checkInit();
        return mApp;
    }

    public static Application getApp() {
        checkInit();
        return mApp;
    }

    public static void setLog(boolean couldLog) {
        WRecord.sCouldLog = couldLog;
    }

    private static void checkInit() {
        if (mApp == null) {
            throw new IllegalStateException("WRecord must be init before use it");
        }
    }

    public static void setRecordCount(int count) {
        sManager.setMaxCount(count);
    }

    public static void fragmentStart(Object fragment, Activity activity) {
        sManager.fragmentStart(fragment, activity);
    }

    public static void fragmentEnd(Object fragment, Activity activity) {
        sManager.fragmentEnd(fragment, activity);
    }

    public static void setPostHandle(PostHandle handle) {
        sManager.setPostHandle(handle);
    }

    public static void postSuccess(List<Record> list) {
        sManager.postSuccess(list);
    }

    public static void postSuccess(JSONArray array) {
        sManager.postSuccess(array);
    }

    public static void postFailure(List<Record> list) {
        sManager.postFailure(list);
    }

    public static void postFailure(JSONArray array) {
        sManager.postFailure(array);
    }

    public static void setPageRecordCreateListener(PageRecordCreateListener pageRecordCreateListener) {
        sManager.setPageRecordCreateListener(pageRecordCreateListener);
    }

    public static void setPageRecordType(int type) {
        sManager.setPageRecordType(type);
    }

    public static void setPageMap(Map<String, String> map) {
        setPageMap(map, false);
    }

    public static void setPageMap(Map<String, String> map, boolean onlyRecordMap) {
        sManager.setPageMap(map, onlyRecordMap);
    }

    public static void pageStart(Activity activity) {
        sManager.pageStart(activity);
    }

    public static void setPageSource(Activity activity, String source) {
        sManager.setPageSource(activity, source);
    }

    public static void pageEnd(Activity activity, String target) {
        sManager.pageEnd(activity, target);
    }

    public static long getPageStayTime(Activity activity) {
        return sManager.getPageStayTime(activity);
    }

    public static void appStart() {
        sManager.appStart();
    }

    public static void appExit() {
        sManager.appExit();
    }

    public static long getAppStayTime() {
        return sManager.getAppStayTime();
    }

}
