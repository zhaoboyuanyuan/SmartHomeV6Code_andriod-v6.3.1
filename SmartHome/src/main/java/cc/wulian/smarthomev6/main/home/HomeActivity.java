package cc.wulian.smarthomev6.main.home;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.model.GuidePage;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.ShapeBadgeItem;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.AdvInfoBean;
import cc.wulian.smarthomev6.entity.AdvInfoListBean;
import cc.wulian.smarthomev6.entity.LocalInfo;
import cc.wulian.smarthomev6.entity.SkinBean;
import cc.wulian.smarthomev6.entity.SkinListBean;
import cc.wulian.smarthomev6.main.account.RegisterMailActivity;
import cc.wulian.smarthomev6.main.account.RegisterPhoneActivity;
import cc.wulian.smarthomev6.main.application.BaseActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.DeviceFragment;
import cc.wulian.smarthomev6.main.explore.ExploreFragment;
import cc.wulian.smarthomev6.main.home.voicecontrol.VoiceControlActivity;
import cc.wulian.smarthomev6.main.login.GatewayLoginActivity;
import cc.wulian.smarthomev6.main.login.SigninActivity;
import cc.wulian.smarthomev6.main.mine.MineFragment;
import cc.wulian.smarthomev6.main.mine.gatewaycenter.ConfirmGatewayPasswordActivity;
import cc.wulian.smarthomev6.main.mine.gatewaycenter.GatewayBindActivity;
import cc.wulian.smarthomev6.main.mine.gatewaycenter.GatewayListActivity;
import cc.wulian.smarthomev6.main.smart.SmartFragment;
import cc.wulian.smarthomev6.main.welcome.AdvActivity;
import cc.wulian.smarthomev6.support.core.apiunit.AdApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.SkinApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.VersionInfoApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceIsPushBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.FileBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.WulianNoticeBean;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.DeviceForbiddenBean;
import cc.wulian.smarthomev6.support.customview.BindGatewayPop;
import cc.wulian.smarthomev6.support.customview.EmptyGWBindPop;
import cc.wulian.smarthomev6.support.customview.LoginAndRegisterPop;
import cc.wulian.smarthomev6.support.customview.WulianNoticePop;
import cc.wulian.smarthomev6.support.event.ConfirmPasswordEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GatewayInfoEvent;
import cc.wulian.smarthomev6.support.event.GatewayPasswordChangedEvent;
import cc.wulian.smarthomev6.support.event.GatewayStateChangedEvent;
import cc.wulian.smarthomev6.support.event.GetGatewayListEvent;
import cc.wulian.smarthomev6.support.event.HomeDeviceWidgetChangeEvent;
import cc.wulian.smarthomev6.support.event.KeyboardEvent;
import cc.wulian.smarthomev6.support.event.LoginPopDismissEvent;
import cc.wulian.smarthomev6.support.event.LoginSuccessEvent;
import cc.wulian.smarthomev6.support.event.MQTTRegisterEvent;
import cc.wulian.smarthomev6.support.event.MultiGatewayEvent;
import cc.wulian.smarthomev6.support.event.NewFlagsChangedEvent;
import cc.wulian.smarthomev6.support.event.NotifyInputGatewayPasswordEvent;
import cc.wulian.smarthomev6.support.event.ShareDeviceStatusChangedEvent;
import cc.wulian.smarthomev6.support.event.SkinChangedEvent;
import cc.wulian.smarthomev6.support.tools.FlagsTool;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.RegularTool;
import cc.wulian.smarthomev6.support.tools.SystemDownloadManager;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.DialogUtil;
import cc.wulian.smarthomev6.support.utils.FileUtil;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;
import cc.wulian.smarthomev6.support.utils.MD5Util;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.VersionUtil;
import cc.wulian.smarthomev6.support.utils.WLog;
import cc.wulian.wrecord.WRecord;

import static cc.wulian.smarthomev6.support.event.KeyboardEvent.KEYBOARD_HIDE;
import static cc.wulian.smarthomev6.support.event.KeyboardEvent.KEYBOARD_SHOW;
import static cc.wulian.smarthomev6.support.tools.Preference.DEFAULT_SKIN_ID;
import static cc.wulian.smarthomev6.support.tools.Preference.ENTER_TYPE_ACCOUNT;


public class HomeActivity extends BaseActivity implements BottomNavigationBar.OnTabSelectedListener {

    public static final int LOGIN_STATE = 1;
    public static final int INSTALL_PACKAGES_REQUESTCODE = 101;
    private static final String VIRTUAL_GATEWAY_ID = "000000000000";
    private Context context;
    private ArrayList<Fragment> fragments;
    private HomeFragment homeFragment;
    private DeviceFragment deviceFragment;
    private ExploreFragment exploreFragment;
    private SmartFragment smartFragment;
    private MineFragment mineFragment;
    private FragmentManager fragmentManager;
    private WLDialog enterGatewayPasswordDialog;
    private DeviceApiUnit deviceApiUnit;
    private AdApiUnit adApiUnit;
    private BottomNavigationBar bottomNavigationBar;
    private ProgressDialogManager progressDialogManager;
    private Dialog mDialog;
    private ShapeBadgeItem mineBadgeItemnew;
    private ImageView btn_voice;
    private View decorView;
    // 当前界面的可是部分
    private Rect windowVisibleDisplayFrame;
    // 键盘状态
    private int keyBoardState = KEYBOARD_HIDE;
    // 屏幕底部, 不为0, 应为部分设备有虚拟按键
    private int screenBottom;
    private int lastPosition;
    private int currentPosition;
    private String currentPassword;
    private String forbiddenDevice;
    private boolean isChina = true;
    private static boolean isFirst = true;
    private List<String> gatewayList;

    private DownloadCompleteReceiver mDownloadCompleteReceiver;
    private SystemDownloadManager mSystemDownloadManager;
    private LoginAndRegisterPop loginAndRegisterPop;
    private BindGatewayPop bindGatewayPop;
    private WulianNoticePop wlNoticePop;
    private Handler popupHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    showLoginAndRegisterPop(currentPosition);
                    getWulianNoticeData(currentPosition);
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                Class decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
                Field field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor");
                field.setAccessible(true);
                field.setInt(getWindow().getDecorView(), Color.TRANSPARENT);  //改为透明
            } catch (Exception e) {
            }
        }
        setContentView(R.layout.activity_home);//透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(Color.TRANSPARENT);
            }
        }
        context = this;
        isChina = LanguageUtil.isChina();
        progressDialogManager = ProgressDialogManager.getDialogManager();
        deviceApiUnit = new DeviceApiUnit(this);
        adApiUnit = new AdApiUnit(this);
        initView();
        updateSkin();
//        showNewbieGuide();

        if (isFirst) {
            isFirst = false;
            startGuideOrAdv();
        }
        checkVersion();


        EventBus.getDefault().register(this);
        if (preference.getIsNeedDefaultSkin()) {
            FileUtil.delAllFile(FileUtil.getSkinPath());
            preference.setNeedDefaultSkin(false);
            preference.saveCurrentSkin(DEFAULT_SKIN_ID);
        }
        checkExistHolidaySkin();
        decorView = getWindow().getDecorView();

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        screenBottom = outMetrics.heightPixels;
        windowVisibleDisplayFrame = new Rect();
        findViewById(R.id.bottom_navigation_bar).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //获取当前界面可视部分
                decorView.getWindowVisibleDisplayFrame(windowVisibleDisplayFrame);
                // 无遮挡, 键盘消失
                if (screenBottom == windowVisibleDisplayFrame.bottom) {
                    if (keyBoardState == KEYBOARD_SHOW) {
                        keyBoardState = KEYBOARD_HIDE;

                        WLog.i(TAG, "onGlobalLayout: 键盘消失了");
                        EventBus.getDefault().post(new KeyboardEvent(KEYBOARD_HIDE));
                    }
                } else {
                    // 有遮挡, 键盘出现
                    if (keyBoardState == KEYBOARD_HIDE) {
                        keyBoardState = KEYBOARD_SHOW;

                        WLog.i(TAG, "onGlobalLayout: 键盘弹出来了");
                        EventBus.getDefault().post(new KeyboardEvent(KEYBOARD_SHOW, screenBottom - windowVisibleDisplayFrame.bottom));
                    }
                }
            }
        });
    }

    private void showNewbieGuide() {
        FrameLayout container = (FrameLayout) bottomNavigationBar.findViewById(com.ashokvarma.bottomnavigation.R.id.bottom_navigation_bar_container);
        LinearLayout view = (LinearLayout) container.getChildAt(1);
        View smartView;
        int layoutResId;
        if (isChina) {
            layoutResId = R.layout.view_guide_smart_cn;
        } else {
            layoutResId = R.layout.view_guide_smart_en;
        }
        smartView = view.getChildAt(view.getChildCount() - 2);

        NewbieGuide.with(this)
                .setLabel("smart")
                .alwaysShow(false)
                .addGuidePage(GuidePage.newInstance()
                        .addHighLight(smartView)
                        .setLayoutRes(layoutResId))
                .show();
    }

    @Override
    public boolean enableSwipeBack() {
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        popupHandler.sendEmptyMessageDelayed(0, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void startGuideOrAdv() {
        int currentVersion = VersionUtil.getVersionCode(this);
        int oldVersion = preference.getCurrentVersion();
        if (currentVersion > oldVersion) {
            //启动引导页
            preference.setCurrentVersion(currentVersion);
//            if (LanguageUtil.isChina()) {
//                startActivity(new Intent(this, GuideActivity.class));
//            }
        } else {
            //启动广告页
            startAdvertisement();
        }
    }

    private void startAdvertisement() {
        adApiUnit.getAdvList(new AdApiUnit.AdvListener() {
            @Override
            public void onGetCache(AdvInfoListBean bean) {
                if (bean.advInfo != null && bean.advInfo.size() > 0) {
                    int size = bean.advInfo.size();
                    int index = preference.getAdvIndex();
                    index += 1;
                    if (index >= size) {
                        index = 0;
                    }
                    preference.saveAdvIndex(index);
                    AdvInfoBean advInfoBean = bean.advInfo.get(index);
                    File imageFile = ImageLoader.getInstance().getDiskCache().get(advInfoBean.imageUrl);
                    if (imageFile != null && imageFile.exists() && imageFile.length() > 0) {
                        AdvActivity.start(HomeActivity.this, advInfoBean);
                    }
                }
            }

            @Override
            public void onSuccess(AdvInfoListBean bean) {
                if (bean.advInfo != null && bean.advInfo.size() > 0) {
                    for (AdvInfoBean advInfoBean : bean.advInfo) {
                        ImageLoader.getInstance().loadImage(advInfoBean.imageUrl, null);
                    }
                }
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    private void checkVersion() {
        VersionInfoApiUnit versionUnit = new VersionInfoApiUnit(this);
        versionUnit.doGetNewestVersion(new VersionInfoApiUnit.VersionUpdateCommonListener<FileBean>() {
            @Override
            public void onSuccess(final FileBean bean) {
                if (bean == null || bean.appVO == null) {
                    return;
                }
                if (bean.appVO.versionCode > VersionUtil.getVersionCode(context)) {
                    mDialog = DialogUtil.showMsgListDialog(context, false, getString(R.string.Updatereminder_Versionupdate), bean.appVO.content,
                            getResources().getString(R.string.Update_Now),
                            getResources().getString(R.string.Talk_Later),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    view.setEnabled(false);
                                    mDialog.dismiss();
                                    if (mDownloadCompleteReceiver == null) {
                                        mDownloadCompleteReceiver = new DownloadCompleteReceiver();
                                        IntentFilter intentFilter = new IntentFilter();
                                        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                                        registerReceiver(mDownloadCompleteReceiver, intentFilter);
                                    }
                                    if (mSystemDownloadManager == null) {
                                        mSystemDownloadManager = new SystemDownloadManager(context);
                                    }
                                    mSystemDownloadManager.downloadApk(bean.appVO.url, "/wulian/SmartHome-" + bean.appVO.versionName + ".apk");
                                }
                            },
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mDialog.dismiss();
                                }
                            });
                }
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    private void checkExistHolidaySkin() {
        final List<SkinBean> skinList = new ArrayList<>();
        final long currentTime = System.currentTimeMillis() / 1000;
        final SkinApiUnit skinApiUnit = new SkinApiUnit(this);
        skinApiUnit.getSkinList(new SkinApiUnit.CommonListener<SkinListBean>() {
            @Override
            public void onSuccess(SkinListBean bean) {
                boolean isExistHolidaySkin = false;
                if (bean.appThemeVOs != null && bean.appThemeVOs.size() > 0) {
                    for (final SkinBean skinBean : bean.appThemeVOs) {
                        setSkinBeanStatus(skinBean);
                        if (skinBean.endTime / 1000 == 0) {//一直可用的皮肤
                            skinList.add(skinBean);
                        }
                        if (skinBean.startTime / 1000 < currentTime && skinBean.endTime / 1000 > currentTime) {
                            String skinId = preference.getCurrentSkin();
                            WLog.i("SkinLog", "有节假日皮肤时上次使用的皮肤" + skinId);
                            isExistHolidaySkin = true;
                            skinApiUnit.downloadSkin(skinBean.themeId, skinBean.themeUrl, new SkinApiUnit.CommonListener<File>() {
                                @Override
                                public void onSuccess(File bean) {
                                    skinBean.status = 1;
                                    if (preference.getAutoChangeSkin()) {
                                        unzipSkinPackage(skinBean);
                                    }
                                }

                                @Override
                                public void onFail(int code, String msg) {
                                    ToastUtil.show(R.string.Infraredrelay_Downloadfailed_Title);
                                }
                            });
                        }
                    }
                    if (!isExistHolidaySkin) {
                        //不存在节假日皮肤时需要开启自动换肤功能
                        preference.setAutoChangeSkin(true);
                        WLog.i("SkinLog", "不存在节假日皮肤时，使用缓存中保存的常规皮肤" + preference.getCommonSkin());
                        for (SkinBean skinBean : skinList) {
                            if (skinBean.themeId.equals(preference.getCommonSkin())) {
                                unzipSkinPackage(skinBean);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    private void unzipSkinPackage(final SkinBean skinBean) {
        SkinManager.unzipSkinPackage(skinBean.themeId, new SkinManager.ZipCallback() {
            @Override
            public void onFinish() {
                skinBean.status = 2;
                Preference.getPreferences().saveCurrentSkin(skinBean.themeId);
                EventBus.getDefault().post(new SkinChangedEvent());
            }

            @Override
            public void onFail() {
//                ToastUtil.show(R.string.Setting_Fail);
            }
        });
    }


    private void setSkinBeanStatus(SkinBean skinBean) {
        String currentSkinId = Preference.getPreferences().getCurrentSkin();
        if (TextUtils.equals(currentSkinId, skinBean.themeId)) {
            skinBean.status = 2;
        } else if (SkinManager.isSkinPackageExists(skinBean.themeId)) {
            skinBean.status = 1;
        } else {
            skinBean.status = 0;
        }
    }

    class DownloadCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            WLog.i("luzx", "onReceive:" + intent.getAction());
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                //通过downloadId去查询下载的文件名
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                long downloadedId = Preference.getPreferences().getDownloadId("downloadId");
                if (downloadId == downloadedId) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(downloadId);
                    Cursor myDownload = manager.query(query);
                    if (myDownload.moveToFirst()) {
                        int fileUriIdx = myDownload.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                        String fileUri = myDownload.getString(fileUriIdx);
                        if (!TextUtils.isEmpty(fileUri)) {
                            String filePath = Uri.parse(fileUri).getPath();
                            if (mSystemDownloadManager == null) {
                                mSystemDownloadManager = new SystemDownloadManager(HomeActivity.this);
                            }
                            mSystemDownloadManager.installAPK(filePath);
                        }
                    }
                    myDownload.close();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == INSTALL_PACKAGES_REQUESTCODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //先判断是否有安装未知来源应用的权限
                if (getPackageManager().canRequestPackageInstalls()) {
                    mSystemDownloadManager.installAPK(null);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
//        EventBus.getDefault().unregister(this);
        if (mDialog != null) {
            mDialog.dismiss();
        }
        if (mDownloadCompleteReceiver != null) {
            unregisterReceiver(mDownloadCompleteReceiver);
        }
        super.onDestroy();
    }

    protected void initView() {
        homeFragment = new HomeFragment();
        deviceFragment = new DeviceFragment();
        exploreFragment = new ExploreFragment();
        smartFragment = new SmartFragment();
        mineFragment = new MineFragment();
        fragmentManager = getSupportFragmentManager();
        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        mineBadgeItemnew = new ShapeBadgeItem()
                .setShape(ShapeBadgeItem.SHAPE_OVAL)
                .setShapeColorResource(R.color.new_flags)
                .setSizeInDp(this, 9, 9)
                .setEdgeMarginInDp(this, 5)
                .setHideOnSelect(false);
        fragments = getFragments();
        setDefaultFragment();
        bottomNavigationBar.setTabSelectedListener(HomeActivity.this);
        btn_voice = (ImageView) findViewById(R.id.btn_voice);
        btn_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preference.getUserEnterType().equals(Preference.ENTER_TYPE_ACCOUNT)) {
                    Intent intent = new Intent(HomeActivity.this, VoiceControlActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.bottomtotop_in, 0);
                } else {
                    ToastUtil.show(getString(R.string.voice_assistant));
                }
            }
        });
        btn_voice.setVisibility(isChina ? View.VISIBLE : View.GONE);

        updateNewFlag();
    }

    private void updateSkin() {

        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setImageViewDrawable(btn_voice, SkinResouceKey.BITMAP_BOTTOM_NAVIGATION_VOICE_N);
        String[] keys = {
                SkinResouceKey.BITMAP_BOTTOM_NAVIGATION_HOME_S,
                SkinResouceKey.BITMAP_BOTTOM_NAVIGATION_HOME_N,
                SkinResouceKey.BITMAP_BOTTOM_NAVIGATION_DEVICE_S,
                SkinResouceKey.BITMAP_BOTTOM_NAVIGATION_DEVICE_N,
                SkinResouceKey.BITMAP_BOTTOM_NAVIGATION_FIND_S,
                SkinResouceKey.BITMAP_BOTTOM_NAVIGATION_DIND_N,
                SkinResouceKey.BITMAP_BOTTOM_NAVIGATION_MINE_S,
                SkinResouceKey.BITMAP_BOTTOM_NAVIGATION_MINE_N
        };
        Drawable[] drawables = new Drawable[keys.length];
        boolean isResourceExists = true;
        for (int i = 0; i < keys.length; i++) {
            drawables[i] = skinManager.getDrawable(keys[i]);
            if (drawables[i] == null) {
                isResourceExists = false;
                break;
            }
        }
        int currentSelectedPosition = bottomNavigationBar.getCurrentSelectedPosition();
        if (isResourceExists) {
            bottomNavigationBar.clearAll();
            bottomNavigationBar.addItem(new BottomNavigationItem(drawables[0], getString(R.string.Bottom_Navigation_Home)).setInactiveIcon(drawables[1]))
                    .addItem(new BottomNavigationItem(drawables[2], getString(R.string.Bottom_Navigation_Device)).setInactiveIcon(drawables[3]));
            if (isChina) {
                findViewById(R.id.icon_voice_assistant_bg).setVisibility(View.VISIBLE);
                bottomNavigationBar.addItem(new BottomNavigationItem(R.color.transparent, "").setInactiveIconResource(R.color.transparent));
            }
            bottomNavigationBar.addItem(new BottomNavigationItem(drawables[4], getString(R.string.Smart_smart)).setInactiveIcon(drawables[5]))
                    .addItem(new BottomNavigationItem(drawables[6], getString(R.string.Bottom_Navigation_Mine)).setInactiveIcon(drawables[7]).setBadgeItem(mineBadgeItemnew))
                    .setFirstSelectedPosition(0);
            String activeColor = skinManager.getColorString(SkinResouceKey.COLOR_BOTTOM_NAVIGATION_TEXT_S);
            if (activeColor == null) {
                bottomNavigationBar.setActiveColor(R.color.v6_green_dark);
            } else {
                bottomNavigationBar.setActiveColor(activeColor);
            }
            String inactiveColor = skinManager.getColorString(SkinResouceKey.COLOR_BOTTOM_NAVIGATION_TEXT_N);
            if (inactiveColor != null) {
                bottomNavigationBar.setInActiveColor(inactiveColor);
            }
            bottomNavigationBar.initialise();
        } else {
            bottomNavigationBar.clearAll();
            bottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.home_column_home_select, getString(R.string.Bottom_Navigation_Home)).setInactiveIconResource(R.drawable.home_column_home_nor))
                    .addItem(new BottomNavigationItem(R.drawable.home_column_device_select, getString(R.string.Bottom_Navigation_Device)).setInactiveIconResource(R.drawable.home_column_device_nor));
            if (isChina) {
                findViewById(R.id.icon_voice_assistant_bg).setVisibility(View.VISIBLE);
                bottomNavigationBar.addItem(new BottomNavigationItem(R.color.transparent, "").setInactiveIconResource(R.color.transparent));
            }
            bottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.home_column_explore_select, getString(R.string.Smart_smart)).setInactiveIconResource(R.drawable.home_column_explore_nor))
                    .addItem(new BottomNavigationItem(R.drawable.home_column_mine_select, getString(R.string.Bottom_Navigation_Mine)).setInactiveIconResource(R.drawable.home_column_mine_nor).setBadgeItem(mineBadgeItemnew))
                    .setActiveColor(R.color.v6_green_dark)
                    .setFirstSelectedPosition(0)
                    .initialise();
        }
        if (currentSelectedPosition > 0) {
            bottomNavigationBar.selectTab(currentSelectedPosition, false);
        }
    }

    private void updateNewFlag() {
        if (FlagsTool.getFlag(FlagsTool.NEW_HOMEMINE_MASK)) {
            mineBadgeItemnew.hide();
        } else {
            mineBadgeItemnew.show();
        }
    }

    /**
     * 设置默认的
     */
    private void setDefaultFragment() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.layFrame, homeFragment, "home");
        transaction.commit();
        recordFragment(0);
    }

    private ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(homeFragment);
        fragments.add(deviceFragment);
//        fragments.add(exploreFragment);
        fragments.add(smartFragment);
        fragments.add(mineFragment);
        return fragments;
    }

    private int tabSelected = 0;

    public String getTabSelected() {
        return getTabSelected(tabSelected);
    }

    private String getTabSelected(int position) {
        return fragments.get(position).getTag();
    }

    private void recordFragment(int position) {
        WRecord.pageEnd(this, getTabSelected(position));
        tabSelected = position;
        WRecord.pageStart(this);
    }

    private int saveLastPosition = -1;

    @Override
    public void onTabSelected(int position) {
        lastPosition = currentPosition;
        currentPosition = position;
        DeviceForbiddenBean deviceForbiddenBean = null;
        forbiddenDevice = mainApplication.forbiddenDevice;
        if (!TextUtils.isEmpty(forbiddenDevice)) {
            deviceForbiddenBean = JSON.parseObject(forbiddenDevice, DeviceForbiddenBean.class);
        }
        Log.i(TAG, "forbiddenDevice: " + forbiddenDevice);
        if (position != 0 && homeFragment != null) {
            homeFragment.hideGifView();
        }
        if (isChina) {
            if (position == 2) {
                return;
            }
            if (position > 1) {//中间加了一个tab的空位
                position -= 1;
            }
        }
        if (fragments != null) {
            if (position < fragments.size()) {
                showLoginAndRegisterPop(position);
                showGoToBindPop(position);
                getWulianNoticeData(position);
                FragmentTransaction ft = fragmentManager.beginTransaction();
                Fragment fragment = fragments.get(position);
                String tag = fragment.getClass().getSimpleName();
                switch (position) {
                    case 0:
                        if (fragment.isAdded()) {
                            ft.show(fragment);
                        } else {
                            ft.add(R.id.layFrame, fragment, tag);
                        }
                        recordFragment(position);
                        break;
                    case 1:
                        if (Preference.getPreferences().isLogin()) {
                            if (deviceForbiddenBean != null) {
                                if (TextUtils.equals(preference.getCurrentGatewayID(), deviceForbiddenBean.getGwID())) {
                                    if (deviceForbiddenBean.getType() == 0 && deviceForbiddenBean.getStatus() == 0) {
                                        DialogUtil.showDeviceForbiddenDialog(HomeActivity.this, getString(R.string.device_gateway_Disable));
                                        bottomNavigationBar.selectTab(0);
                                        return;
                                    }
                                }
                            }
                            if (fragment.isAdded()) {
                                ft.show(fragment);
                            } else {
                                ft.add(R.id.layFrame, fragment, tag);
                            }
                            // 切换fragment 记录
                            recordFragment(position);
                        } else {
                            startActivityForResult(new Intent(this, SigninActivity.class), LOGIN_STATE);
                            return;
                        }
                        break;
                    case 2:
                        if (fragment == smartFragment) {
                            if (!Preference.getPreferences().isLogin()) {
                                startActivityForResult(new Intent(this, SigninActivity.class), LOGIN_STATE);
                                return;
                            } else {
                                if (TextUtils.isEmpty(preference.getCurrentGatewayID())) {
                                    showPopWindow();
                                    return;
                                } else {
                                    String relationFlag = preference.getGatewayRelationFlag();
                                    if (!TextUtils.isEmpty(relationFlag) && TextUtils.equals(relationFlag, "3")) {
                                        ToastUtil.show(R.string.Smart_no_set);
                                        return;
                                    }
                                    if (deviceForbiddenBean != null) {
                                        if (TextUtils.equals(preference.getCurrentGatewayID(), deviceForbiddenBean.getGwID())) {
                                            if (deviceForbiddenBean.getType() == 0 && deviceForbiddenBean.getStatus() == 0) {
                                                DialogUtil.showDeviceForbiddenDialog(HomeActivity.this, getString(R.string.device_gateway_Disable));
                                                bottomNavigationBar.selectTab(0);
                                                return;
                                            }
                                        }
                                    }
                                    if (fragment.isAdded()) {
                                        ft.show(fragment);
                                    } else {
                                        ft.add(R.id.layFrame, fragment, tag);
                                    }
                                    recordFragment(position);
                                }
                            }
                        }
                        break;
                    case 3:
                        if (fragment.isAdded()) {
                            ft.show(fragment);
                        } else {
                            ft.add(R.id.layFrame, fragment, tag);
                        }
                        recordFragment(position);
                        break;
                }
                if (saveLastPosition != -1 && saveLastPosition != position) {
                    ft.hide(fragments.get(saveLastPosition));
                }
                saveLastPosition = position;
                ft.commitAllowingStateLoss();
            }
        }

    }

    @Override
    public void onTabUnselected(int position) {
    }

    @Override
    public void onTabReselected(int position) {


    }


    private void showPopWindow() {
        final EmptyGWBindPop popupWindow = new EmptyGWBindPop(this);
        popupWindow.setOnPopClickListener(new EmptyGWBindPop.onPopClickListener() {
            @Override
            public void virtualGateway() {
                bindVirtualGateway();
                bottomNavigationBar.selectTab(0);
            }

            @Override
            public void bindGateway() {
                startActivity(new Intent(HomeActivity.this, GatewayListActivity.class));
                bottomNavigationBar.selectTab(lastPosition);
            }

            @Override
            public void cancel() {
                popupWindow.dismiss();
                bottomNavigationBar.selectTab(lastPosition);
            }
        });
        popupWindow.showAtLocation(findViewById(R.id.activity_home),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popupWindow.backgroundAlpha((Activity) HomeActivity.this, 1f);
            }
        });
    }

    private void showLoginAndRegisterPop(int position) {
        View view = findViewById(R.id.layout_bottom_bar);
        if (!Preference.getPreferences().isLogin()) {
            if (loginAndRegisterPop == null) {
                loginAndRegisterPop = new LoginAndRegisterPop(this);
            }
            loginAndRegisterPop.setOnPopClickListener(new LoginAndRegisterPop.onPopClickListener() {
                @Override
                public void goToLogin() {
                    startActivity(new Intent(HomeActivity.this, SigninActivity.class));
                }

                @Override
                public void goToRegister() {
                    if (LanguageUtil.isChina()) {
                        startActivity(new Intent(HomeActivity.this, RegisterPhoneActivity.class));
                    } else {
                        startActivity(new Intent(HomeActivity.this, RegisterMailActivity.class));
                    }
                }
            });
            if (!loginAndRegisterPop.isShowing() && position == 0) {
                loginAndRegisterPop.showUp2(view);
            } else if (loginAndRegisterPop.isShowing() && position != 0) {
                loginAndRegisterPop.dismiss();
            }
            loginAndRegisterPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    loginAndRegisterPop.backgroundAlpha(HomeActivity.this, 1f);
                }
            });
        }

    }

    private void showGoToBindPop(int position) {
        if (TextUtils.equals(preference.getUserEnterType(), Preference.ENTER_TYPE_ACCOUNT)) {
            View view = findViewById(R.id.layout_bottom_bar);
            gatewayList = preference.getCurrentGatewayList();
            if (gatewayList == null || gatewayList.size() == 0) {
                if (bindGatewayPop == null) {
                    bindGatewayPop = new BindGatewayPop(this);
                }
                bindGatewayPop.setOnPopClickListener(new BindGatewayPop.onPopClickListener() {
                    @Override
                    public void goToBind() {
                        GatewayBindActivity.start(HomeActivity.this, "", false);
                    }
                });
                if (!bindGatewayPop.isShowing() && position == 0) {
                    bindGatewayPop.showUp2(view);
                } else if (bindGatewayPop.isShowing() && position != 0) {
                    bindGatewayPop.dismiss();
                }
                bindGatewayPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        bindGatewayPop.backgroundAlpha(HomeActivity.this, 1f);
                    }
                });
            } else {
                if (bindGatewayPop != null) {
                    bindGatewayPop.dismiss();
                }
            }
        }

    }

    private void getWulianNoticeData(final int position) {
        if (position == 0) {
            DataApiUnit dataApiUnit = new DataApiUnit(this);
            dataApiUnit.doGetWulianNotice(new DataApiUnit.DataApiCommonListener<WulianNoticeBean>() {
                @Override
                public void onSuccess(WulianNoticeBean bean) {
                    if (bean != null && bean.getNotices() != null) {
                        int index = bean.getNotices().get(0).getIndex();
                        if (preference.getWulianNoticeId() != index) {
                            showWulianNoticePop(bean.getNotices().get(0), position);
                        }
                    }
                }

                @Override
                public void onFail(int code, String msg) {
                    ToastUtil.show(msg);
                }
            });
        } else if (wlNoticePop != null && wlNoticePop.isShowing()) {
            wlNoticePop.dismiss();
        }
    }

    private void showWulianNoticePop(final WulianNoticeBean.NoticesBean noticesBean, int position) {
        View view = findViewById(R.id.view_topstate);
//        View view = findViewById(R.id.layFrame);
        if (wlNoticePop == null) {
            wlNoticePop = new WulianNoticePop(this, noticesBean.getContent());
        }
        wlNoticePop.setOnPopClickListener(new WulianNoticePop.onPopClickListener() {
            @Override
            public void cancel() {
                preference.setWulianNoticeId(noticesBean.getIndex());
            }
        });

        if (!wlNoticePop.isShowing() && position == 0) {
            wlNoticePop.showUp2(view);
        }
        wlNoticePop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                wlNoticePop.backgroundAlpha(HomeActivity.this, 1f);
            }
        });
    }

    private void bindVirtualGateway() {
        deviceApiUnit = new DeviceApiUnit(this);
        final String id = VIRTUAL_GATEWAY_ID;
        final String password = "Wulian123";
        deviceApiUnit.doBindDevice(id, password, "GW99", new DeviceApiUnit.DeviceApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                deviceApiUnit.doGetAllDevice(new DeviceApiUnit.DeviceApiCommonListener<List<DeviceBean>>() {
                    @Override
                    public void onSuccess(List<DeviceBean> deviceBeanList) {
                        for (DeviceBean deviceBean : deviceBeanList) {
                            if (deviceBean.deviceId.equalsIgnoreCase(id)) {
                                ToastUtil.show(getResources().getString(R.string.Experience_Gateway_05));
                                switchGateway(deviceBean, password);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        ToastUtil.show(msg);
                    }
                });

            }

            @Override
            public void onFail(int code, String msg) {
                if (code == 20128) {
                    ToastUtil.show(R.string.Experience_Gateway_04);
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }


    //切换网关。只有在云账号登录下才有这个功能。
    private void switchGateway(DeviceBean bean, String password) {
        //清空当前网关信息
        MainApplication.getApplication().clearCurrentGateway();
        //保存网关密码
        preference.saveGatewayPassword(bean.deviceId, password);
        preference.saveCurrentGatewayID(bean.deviceId);
        deviceApiUnit.doSwitchGatewayId(bean.deviceId);
        saveCurrentGatewayInfo(bean);
        preference.saveCurrentGatewayState(bean.state);
        preference.saveGatewayRelationFlag(bean.relationFlag);
        EventBus.getDefault().post(new GatewayStateChangedEvent(null));
        MainApplication.getApplication().getDeviceCache().loadDatabaseCache(bean.deviceId, bean.state);//加载设备列表缓存
        EventBus.getDefault().post(new DeviceReportEvent(null));
        requestAllInfo(bean.deviceId);
    }

    private void requestAllInfo(String gatewayId) {
        getPushType(gatewayId);
        MQTTManager mqttManager = MainApplication.getApplication().getMqttManager();
        LocalInfo localInfo = MainApplication.getApplication().getLocalInfo();
        mqttManager.publishEncryptedMessage(
                MQTTCmdHelper.createGetAllDevices(gatewayId, localInfo.appID),
                mqttManager.MODE_CLOUD_ONLY);

        mqttManager.publishEncryptedMessage(
                MQTTCmdHelper.createGetAllRooms(gatewayId),
                MQTTManager.MODE_CLOUD_ONLY);

        mqttManager.publishEncryptedMessage(
                MQTTCmdHelper.createGetAllScenes(gatewayId),
                MQTTManager.MODE_CLOUD_ONLY);

        mqttManager.publishEncryptedMessage(
                MQTTCmdHelper.createGatewayInfo(gatewayId, 0, localInfo.appID, null),
                MQTTManager.MODE_CLOUD_ONLY);
    }

    //根据http接口先存储网关的基本信息，后面mqtt接口的信息刷新为详细
    private void saveCurrentGatewayInfo(DeviceBean bean) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("gwID", bean.deviceId);
        jsonObject.put("gwVer", bean.version);
        jsonObject.put("gwName", bean.getName());
        jsonObject.put("gwType", bean.getType());
        jsonObject.put("hostFlag", bean.getHostFlag());
        preference.saveCurrentGatewayInfo(jsonObject.toJSONString());
    }

    private void getPushType(String gwId) {
        deviceApiUnit.doGetIsPush(gwId, new DeviceApiUnit.DeviceApiCommonListener<DeviceIsPushBean>() {
            @Override
            public void onSuccess(DeviceIsPushBean bean) {
                if (StringUtil.equals(bean.isPush, "0")) {
                    preference.saveAlarmPush(false);
                } else if (StringUtil.equals(bean.isPush, "1")) {
                    preference.saveAlarmPush(true);
                }
            }

            @Override
            public void onFail(int code, String msg) {
            }
        });
    }

    private void showNotifyEnterGatewayPasswordDialog(final DeviceBean deviceBean) {
        if (enterGatewayPasswordDialog != null && enterGatewayPasswordDialog.isShowing()) {
            return;
        }

        String gwID = "";
        if (deviceBean != null && deviceBean.deviceId != null) {
            gwID = deviceBean.deviceId;
        }
        WLDialog.Builder builder = new WLDialog.Builder(this);
        String title = String.format(getString(R.string.GatewayChangePwd_Input), gwID);
        builder.setTitle(title)
                .setCancelOnTouchOutSide(false)
                .setEditTextHint(R.string.GatewayChangePwd_NewPwd_Hint)
                //根据领导要求，与登录页输入密码一致
                .inputPassword(true)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        currentPassword = msg;
                        if (Preference.ENTER_TYPE_GW.equals(preference.getUserEnterType())) {
                            preference.saveGatewayPassword(preference.getCurrentGatewayID(), currentPassword);
                            MainApplication.getApplication().getMqttManager().connectGatewayInCloud();
                        } else {
                            progressDialogManager.showDialog("VERIFY_PWD", HomeActivity.this, null, null, getResources().getInteger(R.integer.http_timeout));
                            deviceApiUnit.doVerifyGwPwdAndSaveGwId(deviceBean.deviceId, currentPassword, null, new DeviceApiUnit.DeviceApiCommonListener() {
                                @Override
                                public void onSuccess(Object bean) {
                                    progressDialogManager.dimissDialog("VERIFY_PWD", 0);
                                    MainApplication.getApplication().switchGateway(deviceBean, currentPassword);

                                    // 强刷
                                    EventBus.getDefault().post(HomeDeviceWidgetChangeEvent.SYNC());
                                    //弱密码检测
                                    if (TextUtils.equals(ENTER_TYPE_ACCOUNT, preference.getUserEnterType())) {
                                        if (RegularTool.isNeedResetPwd(currentPassword) || TextUtils.equals(currentPassword, deviceBean.deviceId.substring(deviceBean.deviceId.length() - 6).toUpperCase())) {
                                            if (TextUtils.equals("1", deviceBean.getState())) {
                                                ConfirmGatewayPasswordActivity.start(HomeActivity.this, currentPassword);
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onFail(int code, String msg) {
                                    progressDialogManager.dimissDialog("VERIFY_PWD", 0);
                                    ToastUtil.show(R.string.GatewayLoginActivity_Toast_Gateway_LoginFail13);
                                    showNotifyEnterGatewayPasswordDialog(deviceBean);
                                }
                            });
                        }
                    }

                    @Override
                    public void onClickNegative(View view) {
                        if (Preference.ENTER_TYPE_GW.equals(preference.getUserEnterType())) {
                            logoutGateway();
                        } else {
                            ((MainApplication) getApplication()).clearCurrentGateway();
                        }
                    }
                });
        enterGatewayPasswordDialog = builder.create();
        enterGatewayPasswordDialog.show();
    }

    private void showGatewayPasswordChangedDialog(final String gwID) {
        if (enterGatewayPasswordDialog != null && enterGatewayPasswordDialog.isShowing()) {
            return;
        }
        WLDialog.Builder builder = new WLDialog.Builder(this);
        String message = getString(R.string.GatewayChangePwd_Hint_GW);
        if (Preference.ENTER_TYPE_GW.equals(preference.getUserEnterType())) {
            message = getString(R.string.GatewayChangePwd_Hint_GW);
        }
        builder.setTitle(getString(R.string.GatewayChangePwd_ReInput))
                .setMessage(message)
                .setCancelOnTouchOutSide(false)
                .setCancelable(false)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View var1, String msg) {
                        enterGatewayPasswordDialog.dismiss();
                        if (Preference.ENTER_TYPE_GW.equals(preference.getUserEnterType())) {
                            logoutGateway();
                            Intent intent = new Intent(context, GatewayLoginActivity.class);
                            intent.putExtra("gwID", gwID);
                            startActivity(intent);
                        } else if (ENTER_TYPE_ACCOUNT.equals(preference.getUserEnterType())) {
                            ((MainApplication) getApplication()).clearCurrentGateway();
                            Intent intent = new Intent(context, GatewayListActivity.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onClickNegative(View var1) {
                    }
                });
        enterGatewayPasswordDialog = builder.create();
        enterGatewayPasswordDialog.show();
    }

    //退出网关登录操作
    private void logoutGateway() {
        ((MainApplication) getApplication()).logoutGateway();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onNotifyEnterGatewayPasswordEvent(GatewayPasswordChangedEvent event) {
        if ("0".equals(event.bean.data)) {
            if (Preference.ENTER_TYPE_GW.equals(preference.getUserEnterType())) {
                String verifyGwPwd = preference.getVerifyGatewayPassword();
                if (!TextUtils.isEmpty(verifyGwPwd) && TextUtils.equals(MD5Util.encrypt(verifyGwPwd), event.bean.gwPwd)) {

                } else {
                    showGatewayPasswordChangedDialog(preference.getCurrentGatewayID());
                }
            } else if (!preference.isAuthGateway()) {
                if (!MainApplication.getApplication().isGwSelfLogin()) {
                    showGatewayPasswordChangedDialog(preference.getCurrentGatewayID());
                }
                MainApplication.getApplication().setGwSelfLogin(false);
            }
            GatewayPasswordChangedEvent stickyEvent = EventBus.getDefault().getStickyEvent(GatewayPasswordChangedEvent.class);
            if (stickyEvent != null) {
                EventBus.getDefault().removeStickyEvent(stickyEvent);
            }
        }
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(NotifyInputGatewayPasswordEvent event) {
        showNotifyEnterGatewayPasswordDialog(event.deviceBean);
//        MainApplication.getApplication().clearCurrentGateway();
        NotifyInputGatewayPasswordEvent stickyEvent = EventBus.getDefault().getStickyEvent(NotifyInputGatewayPasswordEvent.class);
        if (stickyEvent != null) {
            EventBus.getDefault().removeStickyEvent(stickyEvent);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MQTTRegisterEvent event) {
        if (event.state == MQTTRegisterEvent.STATE_REGISTER_FAIL && "13".equals(event.data)) {
            String msg = event.msg;
            preference.saveGatewayPassword(preference.getCurrentGatewayID(), "");
            if (!TextUtils.isEmpty(msg)) {
                ToastUtil.single(msg);
                MainApplication.getApplication().getMqttManager().disconnectGateway();
            }
//            DeviceBean deviceBean = new DeviceBean();
//            deviceBean.deviceId = preference.getCurrentGatewayID();
//            showNotifyEnterGatewayPasswordDialog(deviceBean);
        } else if (event.state == MQTTRegisterEvent.STATE_REGISTER_SUCCESS) {
            if (enterGatewayPasswordDialog != null && enterGatewayPasswordDialog.isShowing()) {
                enterGatewayPasswordDialog.dismiss();
                if (TextUtils.equals(ENTER_TYPE_ACCOUNT, preference.getUserEnterType())) {
                    if (RegularTool.isNeedResetPwd(currentPassword) || TextUtils.equals(currentPassword, preference.getCurrentGatewayID().substring(preference.getCurrentGatewayID().length() - 6).toUpperCase())) {
                        if ("1".equals(preference.getCurrentGatewayState())) {
                            ConfirmGatewayPasswordActivity.start(HomeActivity.this, currentPassword);
                        }
                    }
                }
            }
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onConfirmPasswordEvent(ConfirmPasswordEvent event) {
        if (TextUtils.equals(event.id, preference.getCurrentGatewayID())) {
            if (TextUtils.equals(ENTER_TYPE_ACCOUNT, preference.getUserEnterType())) {
                if ("1".equals(preference.getCurrentGatewayState())) {
                    ConfirmGatewayPasswordActivity.start(HomeActivity.this, "");
                }
            }
        }
        ConfirmPasswordEvent stickyEvent = EventBus.getDefault().getStickyEvent(ConfirmPasswordEvent.class);
        if (stickyEvent != null) {
            EventBus.getDefault().removeStickyEvent(stickyEvent);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(NewFlagsChangedEvent event) {
        updateNewFlag();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SkinChangedEvent event) {
        updateSkin();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginSuccessEvent event) {
        bottomNavigationBar.selectTab(0);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginPopDismissEvent event) {
        if (loginAndRegisterPop != null && loginAndRegisterPop.isShowing()) {
            loginAndRegisterPop.dismiss();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GetGatewayListEvent event) {
        showGoToBindPop(currentPosition);
    }

    private WLDialog deviceDataUpdateDialog, deviceStatusChangedDialog;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ShareDeviceStatusChangedEvent event) {
        WLDialog.Builder builder = new WLDialog.Builder(this);
        String message = null;
        if (event.type == 0) {
            deviceStatusChangedDialog = null;
            if (deviceStatusChangedDialog == null) {
                message = getString(R.string.Cancelled_Share);
                deviceStatusChangedDialog = builder.setMessage(message)
                        .setCancelOnTouchOutSide(false)
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.Tip_I_Known))
                        .create();
            }
            if (!deviceStatusChangedDialog.isShowing()) {
                deviceStatusChangedDialog.show();
            }
        } else if (event.type == 1 || event.type == 2) {
            if (deviceDataUpdateDialog == null) {
                message = getString(R.string.Device_Date_Update);
                deviceDataUpdateDialog = builder.setMessage(message)
                        .setCancelOnTouchOutSide(false)
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.Tip_I_Known))
                        .create();
            }
            if (!deviceDataUpdateDialog.isShowing()) {
                deviceDataUpdateDialog.show();
            }
        } else if (event.type == 3) {
            deviceStatusChangedDialog = null;
            if (deviceStatusChangedDialog == null) {
                message = getString(R.string.Gateway_User_Hint3);
                deviceStatusChangedDialog = builder.setMessage(message)
                        .setCancelOnTouchOutSide(false)
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.Tip_I_Known))
                        .create();
            }
            if (!deviceStatusChangedDialog.isShowing()) {
                deviceStatusChangedDialog.show();
            }
        } else if (event.type == 4) {
            deviceStatusChangedDialog = null;
            if (deviceStatusChangedDialog == null) {
                message = getString(R.string.Experience_gw_time);
                deviceStatusChangedDialog = builder.setMessage(message)
                        .setCancelOnTouchOutSide(false)
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.Tip_I_Known))
                        .create();
            }
            if (!deviceStatusChangedDialog.isShowing()) {
                deviceStatusChangedDialog.show();
            }
        }

    }

    private WLDialog gwInSubMachineDialog;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GatewayInfoEvent event) {
        if (event.bean != null && !TextUtils.isEmpty(event.bean.masterGw)) {
            final String masterGw = event.bean.masterGw;
            String currentGwId = preference.getCurrentGatewayID();
            if (!TextUtils.equals(currentGwId, masterGw)) {
                if (gwInSubMachineDialog == null) {
                    WLDialog.Builder builder = new WLDialog.Builder(this);
                    gwInSubMachineDialog = builder.setTitle(R.string.Hint)
                            .setMessage(R.string.Fog_calculation_log_in_Sub_machine)
                            .setCancelOnTouchOutSide(false)
                            .setNegativeButton(R.string.Cancel)
                            .setPositiveButton(getString(R.string.AuthManager_UnBind))
                            .setListener(new WLDialog.MessageListener() {
                                @Override
                                public void onClickPositive(View var1, String msg) {
                                    WLDialog.Builder builder = new WLDialog.Builder(HomeActivity.this);
                                    builder.setTitle(R.string.Hint)
                                            .setMessage(R.string.Fog_calculation_Unbundled_prompt)
                                            .setCancelOnTouchOutSide(false)
                                            .setNegativeButton(R.string.Cancel)
                                            .setPositiveButton(getString(R.string.Sure))
                                            .setListener(new WLDialog.MessageListener() {
                                                @Override
                                                public void onClickPositive(View var1, String msg) {
                                                    String currentGatewayId = Preference.getPreferences().getCurrentGatewayID();
                                                    MainApplication.getApplication()
                                                            .getMqttManager()
                                                            .publishEncryptedMessage(
                                                                    MQTTCmdHelper.createMultiGateway(currentGatewayId, currentGatewayId, null, 6),
                                                                    MQTTManager.MODE_GATEWAY_FIRST);
                                                }

                                                @Override
                                                public void onClickNegative(View var1) {

                                                }
                                            })
                                            .create()
                                            .show();
                                }

                                @Override
                                public void onClickNegative(View var1) {

                                }
                            })
                            .create();
                }
                if (!gwInSubMachineDialog.isShowing()) {
                    gwInSubMachineDialog.show();
                }
            }

        } else if (event.bean != null && TextUtils.isEmpty(event.bean.masterGw)) {
            //从子机切换到主机后bean.masterGw为空需要关闭弹框提示
            if (gwInSubMachineDialog != null && gwInSubMachineDialog.isShowing()) {
                gwInSubMachineDialog.dismiss();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MultiGatewayEvent event) {
        if (event.bean != null) {
            if (event.bean.mode == 6 && event.bean.code == 0) {
                final String currentGatewayId = preference.getCurrentGatewayID();
                // 子机网关解绑成功， 重新请求数据
                MainApplication.getApplication()
                        .getMqttManager()
                        .sendGetDevicesInfoCmd(currentGatewayId, MQTTManager.MODE_GATEWAY_FIRST);
            }
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onDeviceForbiddenEvent(DeviceForbiddenEvent event) {
//        if (event.bean != null) {
//            if (TextUtils.equals(preference.getCurrentGatewayID(), event.bean.getGwID())) {
//                if (event.bean.getType() == 0 && event.bean.getStatus() == 0) {
//                    bottomNavigationBar.selectTab(0);
//                    DialogUtil.showDeviceForbiddenDialog(HomeActivity.this, getString(R.string.device_gateway_Disable));
//                }
//            }
//        }
//    }


    private long saveLastBackPressTime;
    private static final long BackPressTimeGap = 1800;

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        long currentTime = System.currentTimeMillis();
        if (currentTime - saveLastBackPressTime > BackPressTimeGap) {
            saveLastBackPressTime = currentTime;
            ToastUtil.show(R.string.exit_twotwice);
            return;
        }
        if (TextUtils.isEmpty(preference.getUserEnterType())) {
            MainApplication.getApplication().stopServcie();
            super.onBackPressed();
        } else {
            this.moveTaskToBack(true);
        }
    }
}
