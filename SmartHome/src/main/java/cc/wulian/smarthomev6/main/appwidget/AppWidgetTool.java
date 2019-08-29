package cc.wulian.smarthomev6.main.appwidget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.SceneInfo;
import cc.wulian.smarthomev6.main.home.scene.SceneManager;
import cc.wulian.smarthomev6.main.login.SigninActivity;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.BitmapUtil;
import cc.wulian.smarthomev6.support.utils.DrawableUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by zbl on 2017/9/14.
 */

public class AppWidgetTool {
    public static final String ACTION_APPWIGET_SCENE_ON_CLICK = "ACTION_APPWIGET_SCENE_ON_CLICK";

    public static void updateAppWidgets(Context context) {
        updateScene(context);
    }

    private static RemoteViews remoteViews;
    private static AppWidgetManager appWidgetManager;
    private static int[] scene_appWidgetIds;
    private static Bitmap origin;
    private static Bitmap newBitmap;
    private static Canvas canvas;
    private static int originWidth;
    private static int originheight;
    private static Matrix matrix = null;
    private static Paint paint;
    private static long lastTime;
    private static Handler mHandler;
    private static ValueAnimator mLoadingAnimator;
    private static int clickPosition = -1;//记录点击位置，防止同时点击
    //场景///////////////////////////////////////////////////////

    private static final int[] scene_item_ids = {
            R.id.layout_scene_item0,
            R.id.layout_scene_item1,
            R.id.layout_scene_item2,
            R.id.layout_scene_item3,
            R.id.layout_scene_item4,
            R.id.layout_scene_item5,
            R.id.layout_scene_item6,
            R.id.layout_scene_item7
    };
    private static final int[] scene_icon_ids = {
            R.id.iv_scene_icon0,
            R.id.iv_scene_icon1,
            R.id.iv_scene_icon2,
            R.id.iv_scene_icon3,
            R.id.iv_scene_icon4,
            R.id.iv_scene_icon5,
            R.id.iv_scene_icon6,
            R.id.iv_scene_icon7
    };
    private static final int[] loading_layout_ids = {
            R.id.loading_layout0,
            R.id.loading_layout1,
            R.id.loading_layout2,
            R.id.loading_layout3,
            R.id.loading_layout4,
            R.id.loading_layout5,
            R.id.loading_layout6,
            R.id.loading_layout7
    };
    private static final int[] loading_icon_ids = {
            R.id.loading_icon0,
            R.id.loading_icon1,
            R.id.loading_icon2,
            R.id.loading_icon3,
            R.id.loading_icon4,
            R.id.loading_icon5,
            R.id.loading_icon6,
            R.id.loading_icon7
    };
    private static final int[] scene_name_ids = {
            R.id.tv_scene_name0,
            R.id.tv_scene_name1,
            R.id.tv_scene_name2,
            R.id.tv_scene_name3,
            R.id.tv_scene_name4,
            R.id.tv_scene_name5,
            R.id.tv_scene_name6,
            R.id.tv_scene_name7
    };

    public static void updateScene(final Context context) {
        WLog.i("widgetUpdateScene");
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.view_appwidget_scene);
        long intval = System.currentTimeMillis() - lastTime;
        if (intval > 1000) {
            clickPosition = -1;
        } else {
            if (mHandler == null) {
                mHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 1) {
                            updateScene(context);
                        }
                    }
                };
            }
            mHandler.removeMessages(1);
            mHandler.sendEmptyMessageDelayed(1, 1000 - intval);
            return;
        }
        ComponentName componentName = new ComponentName(context, AppWidgetProvider_Scene.class);

        appWidgetManager = AppWidgetManager.getInstance(context);

        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);
        ArrayList<Integer> id_list = new ArrayList<>();
        for (int appWidgetId : appWidgetIds) {
            AppWidgetProviderInfo info = appWidgetManager.getAppWidgetInfo(appWidgetId);
            if (info.previewImage == R.drawable.appwidget_scene_shortcut) {
                id_list.add(appWidgetId);
            }
        }
        if (id_list.size() == 0) {
            return;
        }
        int size = id_list.size();
        scene_appWidgetIds = new int[size];
        for (int i = 0; i < size; i++) {
            scene_appWidgetIds[i] = id_list.get(i);
        }
        for (int i = 0; i < scene_item_ids.length; i++) {
            Intent intent = new Intent(ACTION_APPWIGET_SCENE_ON_CLICK);
            intent.putExtra("position", i);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(scene_item_ids[i], pendingIntent);
        }
        SceneManager sceneManager = new SceneManager(context);
        List<SceneInfo> sceneInfos = sceneManager.acquireScene();
        //场景
        if (sceneInfos != null) {
//            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.view_appwidget_scene);
            int itemsCount = Math.min(scene_item_ids.length, sceneInfos.size());
            for (int i = 0; i < itemsCount; i++) {
                SceneInfo info = sceneInfos.get(i);
                remoteViews.setViewVisibility(loading_layout_ids[i], View.GONE);
                remoteViews.setViewVisibility(scene_item_ids[i], View.VISIBLE);
//                remoteViews.setImageViewResource(scene_icon_ids[i], sceneManager.getSceneIconResIdQuick(context, info.getIcon()));
                Bitmap bitmap = DrawableUtil.drawableToBitmap(SceneManager.getSceneIconQuick(context, info.getIcon()));
                remoteViews.setImageViewBitmap(scene_icon_ids[i], BitmapUtil.changeBitmapColor(bitmap, 0xff535353));
                if (TextUtils.equals(info.getStatus(), "2")) {
                    remoteViews.setTextViewText(scene_name_ids[i], info.getName() + context.getString(R.string.Home_Scene_IsOpen));
                    remoteViews.setTextColor(scene_name_ids[i], context.getResources().getColor(R.color.v6_text_green));
                    remoteViews.setImageViewBitmap(scene_icon_ids[i], BitmapUtil.changeBitmapColor(bitmap, 0xff5dc604));
                } else {
                    remoteViews.setTextViewText(scene_name_ids[i], info.getName());
                    remoteViews.setTextColor(scene_name_ids[i], context.getResources().getColor(R.color.v6_text_gray_dark));
                    remoteViews.setImageViewBitmap(scene_icon_ids[i], BitmapUtil.changeBitmapColor(bitmap, 0xff535353));
                }
            }
            //全部按钮
//            remoteViews.setViewVisibility(scene_item_ids[itemsCount], View.VISIBLE);
//            remoteViews.setImageViewResource(scene_icon_ids[itemsCount], R.drawable.scene_icon_all);
//            remoteViews.setTextViewText(scene_name_ids[itemsCount], context.getString(R.string.Home_Scene_All));
//            remoteViews.setTextColor(scene_name_ids[itemsCount], context.getResources().getColor(R.color.v6_text_gray_dark));
            //剩余按钮隐藏
            for (int i = itemsCount; i < scene_item_ids.length; i++) {
                remoteViews.setViewVisibility(scene_item_ids[i], View.INVISIBLE);
            }
            //刷新显示
            appWidgetManager.updateAppWidget(scene_appWidgetIds, remoteViews);
//            appWidgetManager.updateAppWidget(componentName, remoteViews);
        }
    }

    public static void onSceneItemClick(final Context context, final int position) {
        if (clickPosition != -1) {
            return;
        }
        Preference preference = Preference.getPreferences();
        clickPosition = position;
        if (preference.isLogin()) {
            if (StringUtil.isNullOrEmpty(preference.getCurrentGatewayID())) {
//                List<Activity> activityList = MainApplication.getApplication().getActivities();
//                if(activityList!=null && activityList.size()>0){
//                    Intent intent = new Intent(context,activityList.get(0).getClass());
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(intent);
//                    WLDialog.Builder builder = new WLDialog.Builder(activityList.get(0));
//                    builder.setTitle(context.getString(R.string.Home_Scene_NoGateway_Tips_Title))
//                            .setMessage(context.getString(R.string.Home_Scene_NoGateway_Tips_Message))
//                            .setPositiveButton(context.getString(R.string.Home_Scene_NoGateway_Tips_Ok))
//                            .setNegativeButton(context.getString(R.string.Cancel))
//                            .setListener(new WLDialog.MessageListener() {
//                                @Override
//                                public void onClickPositive(View var1, String msg) {
//                                    Intent intent = new Intent(context, GatewayListActivity.class);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    context.startActivity(intent);
//                                }
//
//                                @Override
//                                public void onClickNegative(View var1) {
//
//                                }
//                            })
//                            .create()
//                            .show();
//                }
                ToastUtil.show(R.string.Home_Scene_NoGateway_Tips_Message);
            } else {
                if (preference.getCurrentGatewayState().equals("0")) {
                    ToastUtil.show(R.string.Gateway_Offline);
                    return;
                } else {
                    SceneManager sceneManager = new SceneManager(context);
                    List<SceneInfo> sceneInfos = sceneManager.acquireScene();
                    int listSize = sceneInfos.size();
//                    if ((listSize < scene_item_ids.length && position == listSize) || position == scene_item_ids.length - 1) {
//                        //跳转到全部
//                        Intent intent = new Intent(context, AllSceneActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(intent);
//                    } else {
                    // 切换场景
                    WLog.i("luzx", "click");
                    lastTime = System.currentTimeMillis();//记录item点击时间
                    remoteViews.setViewVisibility(loading_layout_ids[position], View.VISIBLE);
                    if (mLoadingAnimator == null) {
                        mLoadingAnimator = ValueAnimator.ofFloat(0, 360 * 16);
                        mLoadingAnimator.setInterpolator(new LinearInterpolator());
                        mLoadingAnimator.setDuration(10000);
                    }
                    mLoadingAnimator.removeAllUpdateListeners();
                    mLoadingAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            WLog.i("luzx", "onAnimationEnd");
                            super.onAnimationEnd(animation);
                            long intval = System.currentTimeMillis() - lastTime;
                            if (intval > 1000) {
//                                    remoteViews.setViewVisibility(loading_layout_ids[position], View.GONE);
//                                    //刷新显示
//                                    appWidgetManager.updateAppWidget(scene_appWidgetIds, remoteViews);
                                clickPosition = -1;
                            }
                        }
                    });
                    mLoadingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            if (origin == null) {
                                origin = BitmapFactory.decodeResource(context.getResources(), R.drawable.scene_loading);
                                originWidth = origin.getWidth();
                                originheight = origin.getHeight();
                            }
                            if (matrix == null) {
                                matrix = new Matrix();
                            }
                            // 设置画笔，消除锯齿
                            if (paint == null) {
                                paint = new Paint();
                                paint.setAntiAlias(true);
                            }
                            // 创建一个和原图一样大小的图片
                            if (newBitmap == null) {
                                newBitmap = Bitmap.createBitmap(origin.getWidth(),
                                        origin.getHeight(), origin.getConfig());
                            }
                            if (canvas == null) {
                                canvas = new Canvas(newBitmap);
                            }
                            // 根据原图的中心位置旋转
                            matrix.setRotate((float) animation.getAnimatedValue(), originWidth / 2,
                                    originheight / 2);
                            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                            canvas.drawBitmap(origin, matrix, paint);
                            remoteViews.setImageViewBitmap(loading_icon_ids[position], newBitmap);
                            if ((float) animation.getAnimatedValue() == 360 * 16) {
                                long intval = System.currentTimeMillis() - lastTime;
                                if (intval > 1000) {
                                    remoteViews.setViewVisibility(loading_layout_ids[position], View.GONE);
                                }
                            }
                            //刷新显示
                            appWidgetManager.updateAppWidget(scene_appWidgetIds, remoteViews);
                        }
                    });
                    mLoadingAnimator.start();
                    //刷新显示
                    appWidgetManager.updateAppWidget(scene_appWidgetIds, remoteViews);
                    sceneManager.toggleScene(sceneInfos.get(position));
//                    }
                }
            }
        } else {
            Intent intent = new Intent(context, SigninActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
