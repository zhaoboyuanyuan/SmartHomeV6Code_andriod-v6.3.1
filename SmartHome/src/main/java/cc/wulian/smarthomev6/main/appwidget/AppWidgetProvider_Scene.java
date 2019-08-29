package cc.wulian.smarthomev6.main.appwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by zbl on 2017/9/13.
 * 桌面插件 场景
 */

public class AppWidgetProvider_Scene extends AppWidgetProvider {

    public static final int APP_WIDGET_TYPE = 1;

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        WLog.i("AppWidget scene onEnabled");
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        WLog.i("AppWidget scene onDisabled");
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        WLog.i("AppWidget scene onDeleted");
//        for (int appWidgetId : appWidgetIds) {
//            Preference.getPreferences().deleteAppWidgets(appWidgetId);
//        }
    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
        WLog.i("AppWidget scene onRestored");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //处理默认的几种广播
        super.onReceive(context, intent);
        //处理自定义广播
//        updateScene(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
//        super.onUpdate(context, appWidgetManager, appWidgetIds);
        WLog.i("AppWidget scene onUpdate");
//        Preference.getPreferences().saveAppWidgets(appWidgetIds, APP_WIDGET_TYPE);
        AppWidgetTool.updateScene(MainApplication.getApplication());
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        WLog.i("AppWidget scene onAppWidgetOptionsChanged");
    }

}
