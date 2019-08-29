package cc.wulian.smarthomev6.support.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.appwidget.AppWidgetTool;
import cc.wulian.smarthomev6.main.device.cateye.CateyeRingActivity;
import cc.wulian.smarthomev6.main.device.device_Bn.DevBnCallToAnswerActivity;
import cc.wulian.smarthomev6.main.device.device_CG27.CG27WaitingAnswerActivity;
import cc.wulian.smarthomev6.main.device.device_bc.DevBcCallToAnswerActivity;
import cc.wulian.smarthomev6.main.device.eques.EquesRingActivity;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesRingPicBean;
import cc.wulian.smarthomev6.main.device.penguin.PenguinDetailActivity;
import cc.wulian.smarthomev6.main.device.safeDog.SafeDogSecurityActivity;
import cc.wulian.smarthomev6.main.home.HomeActivity;
import cc.wulian.smarthomev6.main.message.alarm.BcAlarmActivity;
import cc.wulian.smarthomev6.main.message.alarm.CylincamAlarmActivity;
import cc.wulian.smarthomev6.main.message.alarm.EquesHistoryAlarmActivity;
import cc.wulian.smarthomev6.main.message.alarm.ICamHistoryAlarmActivity;
import cc.wulian.smarthomev6.main.message.alarm.MessageAlarmListActivity;
import cc.wulian.smarthomev6.main.message.log.MessageLogListActivity;
import cc.wulian.smarthomev6.main.mine.CustomerServiceTitleActivity;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.SsoApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.sso.RegisterDeviceBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.bean.CameraOtherBindBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.EntranceGuardCallBean;
import cc.wulian.smarthomev6.support.event.AlarmMediaPushEvent;
import cc.wulian.smarthomev6.support.event.AlarmPushEvent;
import cc.wulian.smarthomev6.support.event.CameraBindEvent;
import cc.wulian.smarthomev6.support.event.CameraHouseKeeperEvent;
import cc.wulian.smarthomev6.support.event.CateyeDoorbellEvent;
import cc.wulian.smarthomev6.support.event.ChangeLockNameEvent;
import cc.wulian.smarthomev6.support.event.CustomerServiceEvent;
import cc.wulian.smarthomev6.support.event.DoorbellButtonEvent;
import cc.wulian.smarthomev6.support.event.DoorbellEvent;
import cc.wulian.smarthomev6.support.event.EntranceGuardCallEvent;
import cc.wulian.smarthomev6.support.event.GatewayStateChangedEvent;
import cc.wulian.smarthomev6.support.event.GetSceneListEvent;
import cc.wulian.smarthomev6.support.event.LanguageChangeEvent;
import cc.wulian.smarthomev6.support.event.LoginEvent;
import cc.wulian.smarthomev6.support.event.NetStateEvent;
import cc.wulian.smarthomev6.support.event.NetworkInfoEvent;
import cc.wulian.smarthomev6.support.event.SceneInfoEvent;
import cc.wulian.smarthomev6.support.event.SortSceneEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.TTSTool;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.CameraUtil;
import cc.wulian.smarthomev6.support.utils.DialogUtil;
import cc.wulian.smarthomev6.support.utils.Logger;
import cc.wulian.smarthomev6.support.utils.NetworkUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.VibratorUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by Administrator on 2017/2/14 0014
 */

public class MainService extends Service {
    private static final String TAG = "MainService";
    private static final String NOTIFICATION_ALARM_CHANNEL_ID = "alarm";
    private static final String NOTIFICATION_ALARM_CHANNEL_NAME = "alarm_message";
    private Preference preference = Preference.getPreferences();

    private volatile static MainService mainService = null;

    private BroadcastReceiver netStateReceiver;
    private BroadcastReceiver languageStateReceiver;
    private BroadcastReceiver appwidgetSceneReceiver;
    private WLDialog dialog;
    private Handler handler;
    private Runnable netRunnable;

    public static final MainService getMainService() {
        return mainService;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Logger.debug("obBind Service");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mainService = this;
        Logger.debug("onCreate service");
        EventBus.getDefault().register(this);
        setApi26Notification();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_ALARM_CHANNEL_ID);
        PendingIntent contentIntent = PendingIntent.getActivities(this, 0,
                new Intent[]{new Intent(this, HomeActivity.class)}, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_launcher_nofi)         //设置状态栏里面的图标（小图标）
                .setWhen(System.currentTimeMillis())        //设置时间发生时间
                .setAutoCancel(false)                        //设置可以清除
                .setContentTitle(getString(R.string.app_name))    //设置下拉列表里的标题
                .setContentText(getString(R.string.app_name));     //设置上下文内容
        Notification notification = builder.build();
        startForeground(1, notification);

//        if (preference.isLogin() && Preference.ENTER_TYPE_ACCOUNT.equals(preference.getUserEnterType())) {
//            if (netStateReceiver == null) {
//            }
//        }
        registerNetStateReceiver();
        registerLanguageStateReceiver();
        registerAppwidgetSceneReceiver();
    }

    @Override
    public void onDestroy() {
//        ((MainApplication) getApplication()).getMqttManager().disconnect();
        if (netStateReceiver != null) {
            unregisterReceiver(netStateReceiver);
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        mainService = null;
    }

    public void registerNetStateReceiver() {
        handler = new Handler();
        netRunnable = new Runnable() {
            @Override
            public void run() {
                if (!NetworkUtil.isNetworkAvailable(MainApplication.getApplication())) {
                    ToastUtil.singleLong(getString(R.string.Network_Not_Available));
                }
            }
        };
        netStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                StringBuilder sb = new StringBuilder();
                for (String text : intent.getExtras().keySet()) {
                    sb.append(text + ":" + intent.getExtras().get(text));
                    sb.append(",");
                }
                EventBus.getDefault().post(new NetStateEvent(sb.toString()));
                //网络状态变化时尝试连接局域网中的网关
                Bundle bundle = intent.getExtras();
//                String networkInfo = bundle.get("networkInfo").toString();
                NetworkInfo networkInfo = (NetworkInfo) bundle.get("networkInfo");
                if (networkInfo != null) {
                    EventBus.getDefault().post(new NetworkInfoEvent(networkInfo));
                    if (networkInfo.isConnected()) {
                        handler.removeCallbacks(netRunnable);
                        if (preference.isLogin() && Preference.ENTER_TYPE_ACCOUNT.equals(preference.getUserEnterType())) {
                            ((MainApplication) getApplication()).getMqttManager().reconnectCloud();
//                        ((MainApplication) getApplication()).getMqttManager().connectGatewayInCloud();
                        }
                    } else {
                        handler.postDelayed(netRunnable, 2000);

                    }

                }
            }
        };
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(netStateReceiver, intentFilter);
    }


    public void registerLanguageStateReceiver() {
        languageStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED)) {
                    WLog.i("Language has bean Changed.");
                    MainApplication.getApplication().getLocalInfo(false);
                    SsoApiUnit ssoApiUnit = new SsoApiUnit(getMainService());
                    ssoApiUnit.doRegisterDevice(new SsoApiUnit.SsoApiCommonListener<RegisterDeviceBean>() {
                        @Override
                        public void onSuccess(RegisterDeviceBean bean) {
                            WLog.i("Language Changed save.");
                            Preference.getPreferences().saveLanguage(MainApplication.getApplication().getLocalInfo().appLang);
                        }

                        @Override
                        public void onFail(int code, String msg) {
                        }
                    });
                    EventBus.getDefault().post(new LanguageChangeEvent());
//                    MainApplication.getApplication().logout(false);
//                    Intent intentHome = new Intent(MainApplication.getApplication(), SplashActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intentHome);
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_LOCALE_CHANGED);
        registerReceiver(languageStateReceiver, intentFilter);
    }

    public void registerAppwidgetSceneReceiver() {
        appwidgetSceneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(AppWidgetTool.ACTION_APPWIGET_SCENE_ON_CLICK)) {
                    int position = intent.getIntExtra("position", 0);
//                    ToastUtil.single("scene position:" + position);
                    if (!"3".equals(Preference.getPreferences().getGatewayRelationFlag())) {
                        AppWidgetTool.onSceneItemClick(context, position);
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(AppWidgetTool.ACTION_APPWIGET_SCENE_ON_CLICK);
        registerReceiver(appwidgetSceneReceiver, intentFilter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CustomerServiceEvent event) {
//        if (!TextUtils.isEmpty(event.msg) && preference.getAlarmPush()) {
        if (!TextUtils.isEmpty(event.msg)) {
            setApi26Notification();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_ALARM_CHANNEL_ID);
            Intent intent = new Intent(this, CustomerServiceTitleActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivities(this, 0,
                    new Intent[]{intent}, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(contentIntent)
                    .setSmallIcon(R.drawable.ic_launcher_nofi)         //设置状态栏里面的图标（小图标）
                    // .setLargeIcon(BitmapFactory.decodeResource(resource, R.mipmap.ic_launcher)) //下拉下拉列表里面的图标（大图标）
                    .setTicker(event.msg)                //设置状态栏的显示的信息
                    .setWhen(System.currentTimeMillis())        //设置时间发生时间
                    .setAutoCancel(true)                        //设置可以清除
                    .setContentTitle(getString(R.string.OnlineService))    //设置下拉列表里的标题
                    .setContentText(event.msg);     //设置上下文内容
            Notification notification = builder.build();
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(event.msg.hashCode(), notification);
            if (preference.getKeyAlarmVoice()) {
                String message = event.msg;
                TTSTool.getInstance().addTTSReadTask(message);
            }
            if (preference.getAlarmShake()) {//震动
                VibratorUtil.notificationVibration();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(AlarmPushEvent event) {//消息推送
//        if (!TextUtils.isEmpty(event.msg) && preference.getAlarmPush()) {
        if (!TextUtils.isEmpty(event.msg)) {
            setApi26Notification();
            Class<?> clazz = null;
            int notificationId = 0;
            //根据alarmCode结尾是1还是2，跳转到报警列表或者日志列表
            Intent intent = null;
            if (!TextUtils.isEmpty(event.alarmCode) && event.alarmCode.endsWith("2")) {
                intent = new Intent(this, MessageLogListActivity.class);
            } else if ("0109061".equals(event.alarmCode)) {
                intent = new Intent(this, SafeDogSecurityActivity.class);
                intent.putExtra("devId", event.deviceId);
            } else {
                intent = new Intent(this, MessageAlarmListActivity.class);
            }
            //设置通知ID
            if (TextUtils.isEmpty(event.alarmCode)) {
                notificationId = (int) (System.currentTimeMillis());
            } else {
                notificationId = event.alarmCode.hashCode();
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_ALARM_CHANNEL_ID);

            PendingIntent contentIntent = PendingIntent.getActivities(this, 0,
                    new Intent[]{intent}, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(contentIntent)
                    .setSmallIcon(R.drawable.ic_launcher_nofi)         //设置状态栏里面的图标（小图标）
                    // .setLargeIcon(BitmapFactory.decodeResource(resource, R.mipmap.ic_launcher)) //下拉下拉列表里面的图标（大图标）
                    .setTicker(event.msg)                //设置状态栏的显示的信息
                    .setWhen(System.currentTimeMillis())        //设置时间发生时间
                    .setAutoCancel(true)                        //设置可以清除
                    .setContentTitle(getString(R.string.Message_Center_AlarmMessage))    //设置下拉列表里的标题
                    .setContentText(event.msg);     //设置上下文内容
            Notification notification = builder.build();
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(notificationId, notification);
            if (preference.getKeyAlarmVoice()) {
                String message = event.msg;
                TTSTool.getInstance().addTTSReadTask(message);
            }
            if (preference.getAlarmShake()) {//震动
                VibratorUtil.notificationVibration();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final AlarmMediaPushEvent event) {//带图片的报警消息推送
        ImageLoader.getInstance().loadImage(event.picUrl, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                showMediaPushNotification(event, loadedImage);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                //加载图片失败,弹出普通警告通知
                onEvent(new AlarmPushEvent(event.alarmCode, event.msg, event.deviceId));
            }
        });
    }


    private void showMediaPushNotification(AlarmMediaPushEvent event, Bitmap bitmap) {
        if (!TextUtils.isEmpty(event.msg)) {
            setApi26Notification();
            int notificationId = 0;
            //根据alarmCode结尾是1还是2，跳转到报警列表或者日志列表
            Intent intent = null;
            String type = event.type;
            if ("CMICA1".equals(type) || "CMICA2".equals(type) || "CMICA3".equals(type) || "CMICA5".equals(type) || "CMICA6".equals(type)) {
                intent = new Intent(this, ICamHistoryAlarmActivity.class);
                intent.putExtra("deviceID", event.deviceId);
                intent.putExtra("type", type);
            } else if ("CMICY1".equals(type)) {
                intent = new Intent(this, EquesHistoryAlarmActivity.class);
                intent.putExtra("deviceID", event.deviceId);
                intent.putExtra("type", type);
            } else if ("Bc".equals(type) || "Bn".equals(type)) {
                intent = new Intent(this, BcAlarmActivity.class);
                intent.putExtra("deviceID", event.deviceId);
                intent.putExtra("msgType", BcAlarmActivity.TYPE_ALL);
            } else if ("CMICA4".equals(type)) {
                intent = new Intent(this, CylincamAlarmActivity.class);
                intent.putExtra("deviceID", event.deviceId);
                intent.putExtra("type", type);
            } else {
                return;
            }
            //设置通知ID
            if (TextUtils.isEmpty(event.alarmCode)) {
                notificationId = (int) (System.currentTimeMillis());
            } else {
                notificationId = event.alarmCode.hashCode();
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_ALARM_CHANNEL_ID);

            PendingIntent contentIntent = PendingIntent.getActivities(this, 0,
                    new Intent[]{intent}, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(contentIntent)
                    .setSmallIcon(R.drawable.ic_launcher_nofi)         //设置状态栏里面的图标（小图标）
                    .setLargeIcon(bitmap)                              //下拉下拉列表里面的图标（大图标）
                    .setTicker(event.msg)                              //设置状态栏的显示的信息
                    .setWhen(System.currentTimeMillis())               //设置时间发生时间
                    .setAutoCancel(true)                               //设置可以清除
                    .setContentTitle(getString(R.string.Message_Center_AlarmMessage))    //设置下拉列表里的标题
                    .setContentText(event.msg)     //设置上下文内容
                    .setDefaults(NotificationCompat.DEFAULT_ALL);
            android.support.v4.app.NotificationCompat.BigPictureStyle style = new android.support.v4.app.NotificationCompat.BigPictureStyle();
            style.setBigContentTitle(getString(R.string.Message_Center_AlarmMessage));
            style.setSummaryText(event.msg);
            style.bigPicture(bitmap);
            builder.setStyle(style);

            Notification notification = builder.build();
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(notificationId, notification);
            if (preference.getKeyAlarmVoice()) {
                String message = event.msg;
                TTSTool.getInstance().addTTSReadTask(message);
            }
            if (preference.getAlarmShake()) {//震动
                VibratorUtil.notificationVibration();
            }
        }
    }


    /**
     * Android8.0通知适配
     */
    private void setApi26Notification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = NOTIFICATION_ALARM_CHANNEL_ID;
            String channelName = NOTIFICATION_ALARM_CHANNEL_NAME;
            int importance = NotificationManager.IMPORTANCE_LOW;
            createNotificationChannel(channelId, channelName, importance);
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        channel.enableVibration(false);
        channel.setVibrationPattern(new long[]{0});
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCateyeDoorbellEvent(CateyeDoorbellEvent event) {
        if (event.cateyeDoorbellBean != null) {
            Device device = MainApplication.getApplication().getDeviceCache().get(event.cateyeDoorbellBean.devID);
            if (device != null && !device.type.equals("Bc")) {
                String bucket = ApiConstant.BUCKET;
                String region = ApiConstant.REGION;
                if (event.cateyeDoorbellBean.extData1 != null) {
                    bucket = event.cateyeDoorbellBean.extData1.bucket == null ? bucket : event.cateyeDoorbellBean.extData1.bucket;
                    region = event.cateyeDoorbellBean.extData1.region == null ? region : event.cateyeDoorbellBean.extData1.region;
                }
                CateyeRingActivity.start(this, device.devID, event.cateyeDoorbellBean.extData, bucket, region);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(ChangeLockNameEvent event) {
        if (event != null && event.devID != null) {
            // TODO: 2017/7/14 70开锁时弹框干掉入口
//            LockDialogActivity.start(this, event.devID);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDoorbellEvent(DoorbellEvent event) {
        if (event.doorbellBean != null && !StringUtil.isNullOrEmpty(event.data)) {
            Device device = MainApplication.getApplication().getDeviceCache().get(event.doorbellBean.devID);
            if (device != null && (TextUtils.equals(device.type, "Bc") || TextUtils.equals(device.type, "Bn"))) {
                String cameraid = "";
                String pictureURL = "";
                String bucket = "";
                String region = "";
                try {
                    JSONObject jsonObject = new JSONObject(event.data);
                    if (!jsonObject.isNull("endpoints")) {
                        JSONArray endpoints = jsonObject.getJSONArray("endpoints");
                        JSONArray clusters = ((JSONObject) endpoints.get(0)).getJSONArray("clusters");
                        JSONArray attributes = ((JSONObject) clusters.get(0)).getJSONArray("attributes");
                        String attributeValue = ((JSONObject) attributes.get(0)).getString("attributeValue");
                        int attributeId = ((JSONObject) attributes.get(0)).getInt("attributeId");
                        if (attributeId == 32776) {
                            if (attributeValue.length() > 2) {
                                String strPre = attributeValue.substring(0, 2);
                                if (TextUtils.equals(strPre, "16")) {
                                    cameraid = attributeValue.substring(2);
                                }
                            }
                        } else if (attributeId == 32775) {
                            pictureURL = jsonObject.getString("extData");
                            JSONObject extData1 = jsonObject.getJSONObject("extData1");
                            bucket = extData1.optString("bucket");
                            region = extData1.optString("region");
                            String strPre = attributeValue.substring(0, 2);
                            if (TextUtils.equals(strPre, "06") || TextUtils.equals(strPre, "05")) {
                                cameraid = attributeValue.substring(2);
                            }
                        }
                    } else {
                        if (jsonObject.has("extData")) {
                            pictureURL = jsonObject.getString("extData");
                        }
                        if (jsonObject.has("extData1")) {
                            JSONObject extData1 = jsonObject.getJSONObject("extData1");
                            bucket = extData1.optString("bucket", null);
                            region = extData1.optString("region", null);
                        }
                        String allcameralID = jsonObject.optString("cameraID");
                        String strPre = allcameralID.substring(0, 2);
                        if (TextUtils.equals(strPre, "06") || TextUtils.equals(strPre, "16")) {
                            cameraid = allcameralID.substring(2);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (!TextUtils.isEmpty(cameraid) && !DevBcCallToAnswerActivity.isReceived && TextUtils.equals(device.type, "Bc")) {
                    bucket = bucket == null ? ApiConstant.BUCKET : bucket;
                    region = region == null ? ApiConstant.REGION : region;
                    DevBcCallToAnswerActivity.start(this, device.devID, pictureURL, cameraid, bucket, region);
                } else if (!TextUtils.isEmpty(cameraid) && !DevBnCallToAnswerActivity.isReceived && TextUtils.equals(device.type, "Bn")) {
                    bucket = bucket == null ? ApiConstant.BUCKET : bucket;
                    region = region == null ? ApiConstant.REGION : region;
                    DevBnCallToAnswerActivity.start(this, device.devID, pictureURL, cameraid, bucket, region);
                }
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEquesDoorbellEvent(EquesRingPicBean event) {
        if (event != null) {
            Device device = MainApplication.getApplication().getDeviceCache().get(event.uid);
            if (device != null) {
                EquesRingActivity.start(this, device.devID, device.isOnLine(), event.fid);
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSceneUpdated(SceneInfoEvent event) {
        AppWidgetTool.updateScene(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSceneReport(GetSceneListEvent event) {
        AppWidgetTool.updateScene(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSortSceneEvent(SortSceneEvent event) {
        AppWidgetTool.updateScene(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginEvent(LoginEvent event) {
        AppWidgetTool.updateScene(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGatewayStateChangedEvent(GatewayStateChangedEvent event) {
        AppWidgetTool.updateScene(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCameraBindByOtherUserEvent(CameraBindEvent event) {
        showBindByOtherUserDialog(event.cameraOtherBindBean);
    }

    private void showBindByOtherUserDialog(CameraOtherBindBean bean) {
        if (!CameraUtil.isAppIsInBackground(this)) {
            dialog = DialogUtil.showOtherUserBindTips(this, new WLDialog.MessageListener() {
                @Override
                public void onClickPositive(View var1, String msg) {
                    dialog.dismiss();
                }

                @Override
                public void onClickNegative(View var1) {

                }
            }, String.format(getString(R.string.Bound_Dialog), bean.user, bean.deviceName));
            dialog.show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDoorbellBtnCall(DoorbellButtonEvent event) {
        List<Device> deviceList = MainApplication.getApplication().getDeviceCache().getWifiDevices();
        for (Device device :
                deviceList) {
            if (device.devID.startsWith("cmic21")) {
                CameraUtil.jumpToCamera(getApplicationContext(), device);
                return;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHousekeeperCameraCall(CameraHouseKeeperEvent event) {
        List<Device> deviceList = MainApplication.getApplication().getDeviceCache().getWifiDevices();
        for (Device device :
                deviceList) {
            if (TextUtils.equals(device.devID, event.cameraHouseKeeperBean.devID)) {
                CameraUtil.jumpToCamera(getApplicationContext(), device);
                return;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEntranceGuardCallEvent(EntranceGuardCallEvent event) {
        if (event != null) {
            EntranceGuardCallBean.ExtData bean = JSON.parseObject(event.entranceGuardCallBean.getExtData(), EntranceGuardCallBean.ExtData.class);
            String deviceId = bean.getCommunityId() + bean.getUc();
            Device device = MainApplication.getApplication().getDeviceCache().get(deviceId);
            if (device != null) {
                CameraUtil.jumpToCamera(this,bean.getCommunityId(), bean.getUc());
//                CG27WaitingAnswerActivity.start(this, bean.getCommunityId(), bean.getUc());
            }
        }
    }

}
