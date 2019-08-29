package cc.wulian.wrecord;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.text.TextUtils;

import cc.wulian.wrecord.record.page.PageMap;
import cc.wulian.wrecord.utils.WLog;

/**
 * Created by Veev on 2017/8/8
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    WActivityLifecycleListener
 */

public class WActivityLifecycleListener implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = "WRecord_lifecycle";

    private Activity lastStopActivity, lastStartActivity;

    private int startActivityCount = 0;

    public void onFragmentStart(Object fragment, Activity activity) {

    }

    public void onFragmentEnd(Object fragment, Activity activity) {

    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        // WLog.i(TAG, "onActivityCreated: " + activity.getClass().getName());
    }

    @Override
    public void onActivityStarted(Activity activity) {
        // WLog.i(TAG, "onActivityStarted: " + activity.getClass().getName());

        lastStartActivity = activity;

        WRecord.pageStart(activity);

        if (startActivityCount == 0) {
            WLog.i(TAG, "App: 启动了");
            WRecord.appStart();
        }

        startActivityCount ++;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        // WLog.i(TAG, "onActivityResumed: " + activity.getClass().getName());
    }

    @Override
    public void onActivityPaused(Activity activity) {
        // WLog.i(TAG, "onActivityPaused: " + activity.getClass().getName());
    }

    @Override
    public void onActivityStopped(Activity activity) {
        // WLog.i(TAG, "onActivityStopped: " + activity.getClass().getName());

        lastStopActivity = activity;

        if (lastStartActivity != null) {
            WRecord.setPageSource(lastStartActivity, PageMap.getNameByPackage(activity.getClass().getName()));
        }

        WRecord.pageEnd(activity, lastStartActivity == null ? null : PageMap.getNameByPackage(lastStartActivity.getClass().getName()));
        startActivityCount --;

        if (startActivityCount == 0) {
            WLog.i(TAG, "App: 退到后台");
            WRecord.appExit();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        // WLog.i(TAG, "onActivitySaveInstanceState: " + activity.getClass().getName());
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        // WLog.i(TAG, "onActivityDestroyed: " + activity.getClass().getName());
    }
}
