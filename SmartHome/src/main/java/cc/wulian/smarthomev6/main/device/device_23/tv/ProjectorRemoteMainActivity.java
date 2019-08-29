package cc.wulian.smarthomev6.main.device.device_23.tv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.uei.control.ACEService;
import com.umeng.socialize.utils.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.UeiConfig;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.device_23.ControllerMoreActivity;
import cc.wulian.smarthomev6.main.device.device_23.Device23Activity;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.GatewayConfigCache;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.GatewayConfigBean;
import cc.wulian.smarthomev6.support.customview.TvCenterControlView;
import cc.wulian.smarthomev6.support.customview.popupwindow.DeleteUeiCodePopupWindow;
import cc.wulian.smarthomev6.support.customview.popupwindow.EditUeiCustomKeyPopupWindow;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GatewayConfigEvent;
import cc.wulian.smarthomev6.support.event.UeiSceneEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

import static cc.wulian.smarthomev6.main.device.device_23.tv.RemoteControlBrandActivity.SINGLE_CODE_PROJECTOR;
import static cc.wulian.smarthomev6.support.customview.TvCenterControlView.AREA_BOTTOM;
import static cc.wulian.smarthomev6.support.customview.TvCenterControlView.AREA_CENTER;
import static cc.wulian.smarthomev6.support.customview.TvCenterControlView.AREA_LEFT;
import static cc.wulian.smarthomev6.support.customview.TvCenterControlView.AREA_RIGHT;
import static cc.wulian.smarthomev6.support.customview.TvCenterControlView.AREA_TOP;

/**
 * Created by 上海滩小马哥 on 2017/11/02.
 */

public class ProjectorRemoteMainActivity extends BaseTitleActivity {

    public static final String MODE_LEARN = "MODE_LEARN";
    public static final String MODE_CONTROL = "MODE_CONTROL";
    private static final String SAVE = "SAVE";
    private static final String DELETE = "DELETE";
    private FrameLayout contentLayout;
    private TvCenterControlView tvCenterControlView;
    private CheckBox btnMore;
    private View layoutMainView, layoutMore;
    private RecyclerView moreContent;
    private GridLayoutManager gridLayoutManager;
    private CustomViewAdapter customViewAdapter;
    private WLDialog.Builder builder;
    private WLDialog matchingDialog;
    private WLDialog matchingFailDialog;
    private WLDialog createCustomKeyDialog;
    private DeleteUeiCodePopupWindow deleteCodePopup;

    private String mode, type, brandName, mName, mPickCode, mUeiUserID, createTime;
    private String deviceID;
    private boolean isSceneOrHouseKeeper;
    private Device device;
    // 码库文件列表
    private LinkedHashMap<String, String> learnMap = new LinkedHashMap<>();
    private List<TvViewHelper.TvItem> list;
    private String currentTag, currentValue;
    private TvViewHelper tvViewHelper;
    private GatewayConfigCache gatewayConfigCache;
    private GatewayConfigBean mConfigBean;
    private String moreKeys = "[{'0':'模式'},{'8':'静音'},{'0':'信号源'},{'36':'退出'},{'24':'播放'},{'25':'暂停'},{'43':'上页'},{'44':'下页'},{'46':'信息'},{'90':'画面冻结'},{'0':'自动'},{'136':'帮助'}]";
    private JSONArray moreKeyArray;

    public static void startForScene(Activity context, String deviceID, String type, String name, String brand, String pick, String time) {
        Intent intent = new Intent(context, ProjectorRemoteMainActivity.class);
        intent.putExtra("deviceID", deviceID);
        intent.putExtra("mode", MODE_CONTROL);
        intent.putExtra("type", type);
        intent.putExtra("name", name);
        intent.putExtra("brand", brand);
        intent.putExtra("pick", pick);
        intent.putExtra("time", time);
        intent.putExtra("isSceneOrHouseKeeper", true);
        context.startActivityForResult(intent, 1);
    }

    /**
     * 遥控
     *
     * @param name  遥控器名
     * @param brand 品牌
     * @param pick  码库名
     */
    public static void start(Context context, String deviceID, String type, String name, String brand, String pick, String time) {
        Intent intent = new Intent(context, ProjectorRemoteMainActivity.class);
        intent.putExtra("deviceID", deviceID);
        intent.putExtra("mode", MODE_CONTROL);
        intent.putExtra("type", type);
        intent.putExtra("name", name);
        intent.putExtra("brand", brand);
        intent.putExtra("pick", pick);
        intent.putExtra("time", time);
        intent.putExtra("isSceneOrHouseKeeper", false);
        context.startActivity(intent);
    }

    /**
     * 学习
     *
     * @param name 遥控器名
     */
    public static void learn(Context context, String deviceID, String type, String name, String time) {
        Intent intent = new Intent(context, ProjectorRemoteMainActivity.class);
        intent.putExtra("deviceID", deviceID);
        intent.putExtra("mode", MODE_LEARN);
        intent.putExtra("type", type);
        intent.putExtra("name", name);
        intent.putExtra("time", time);
        intent.putExtra("isSceneOrHouseKeeper", false);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        deviceID = getIntent().getStringExtra("deviceID");
        device = MainApplication.getApplication().getDeviceCache().get(deviceID);
        setContentView(R.layout.activity_projector_main, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initTitle() {
        super.initTitle();

        Intent intent = getIntent();
        // 获取 intent
        deviceID = intent.getStringExtra("deviceID");
        brandName = intent.getStringExtra("brandName");
        mName = intent.getStringExtra("name");
        mPickCode = intent.getStringExtra("pick");
        mode = intent.getStringExtra("mode");
        type = intent.getStringExtra("type");
        createTime = intent.getStringExtra("time");
        isSceneOrHouseKeeper = getIntent().getBooleanExtra("isSceneOrHouseKeeper", false);

        gatewayConfigCache = MainApplication.getApplication().getGatewayConfigCache();
        mUeiUserID = ACEService.ACEncryptUserId(deviceID + System.currentTimeMillis());

        setTitle();
    }

    @Override
    protected void initView() {
        contentLayout = (FrameLayout) findViewById(R.id.fl_content);
        layoutMainView = findViewById(R.id.layout_main_view);
        layoutMore = findViewById(R.id.layout_more);
        btnMore = (CheckBox) findViewById(R.id.btn_more);
        tvCenterControlView = (TvCenterControlView) findViewById(R.id.center_control);
        moreContent = (RecyclerView) findViewById(R.id.more_content);
        gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        moreContent.setLayoutManager(gridLayoutManager);
        moreContent.setItemAnimator(new DefaultItemAnimator());
        try {
            moreKeyArray = new JSONArray(moreKeys);
            customViewAdapter = new CustomViewAdapter(this, moreKeyArray);
            moreContent.post(new Runnable() {
                @Override
                public void run() {
                    int c = moreContent.getChildCount();
                    if(moreKeyArray.length() == moreContent.getChildCount()){
                        loadAllViews();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        moreContent.setAdapter(customViewAdapter);
        deleteCodePopup = new DeleteUeiCodePopupWindow(this);
        tvViewHelper = new TvViewHelper();
    }

    @Override
    protected void initData() {
        loadAllKeys();
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        registerKeyEvent(contentLayout);
        btnMore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    layoutMainView.setVisibility(View.INVISIBLE);
                    layoutMore.setVisibility(View.VISIBLE);
                    btnMore.setBackgroundResource(R.drawable.selector_tv_remote_cancle);
                } else {
                    layoutMainView.setVisibility(View.VISIBLE);
                    layoutMore.setVisibility(View.INVISIBLE);
                    btnMore.setBackgroundResource(R.drawable.selector_tv_remote_custom);
                }
            }
        });
        tvCenterControlView.setOnClickListener(new TvCenterControlView.OnClickListener() {
            @Override
            public void onClick(TvCenterControlView.AreaItem item) {
                if (TextUtils.equals(mode, MODE_CONTROL)) {
                    if (item.isEnable()) {
                        sendControlCmd(item.getTag());
                    } else {
                        ToastUtil.single(R.string.Infraredtransponder_No_Infraredcode);
                    }

                } else if (TextUtils.equals(mode, MODE_LEARN)) {
                    sendLearnCmd(item.getTag());
                }
            }
        });
        tvCenterControlView.setOnLongClickListener(new TvCenterControlView.OnLongClickListener() {
            @Override
            public void onLongClick(String tag) {
                if (TextUtils.equals(mode, MODE_LEARN)) {
                    deleteCodePopup.showParent(tvCenterControlView, tag);
                }
            }
        });
        deleteCodePopup.setPopupClickListener(new DeleteUeiCodePopupWindow.OnPopupClickListener() {
            @Override
            public void onDelete(String tag) {
                sendDeleteCmd(tag);
            }
        });
    }

    /**
     * 设置标题
     */
    private void setTitle() {
        if (TextUtils.equals(mode, MODE_CONTROL)) {
            if (isSceneOrHouseKeeper){
                if (TextUtils.isEmpty(mName)) {
                    setToolBarTitle(mainApplication.getResources().getString(R.string.Infrared_ransponder_Projector));
                } else {
                    setToolBarTitle(mName);
                }
            }else {
                if (TextUtils.isEmpty(mName)) {
                    setToolBarTitleAndRightImg(mainApplication.getResources().getString(R.string.Infrared_ransponder_Projector), R.drawable.icon_more);
                } else {
                    setToolBarTitleAndRightImg(mName, R.drawable.icon_more);
                }
            }
        } else if (TextUtils.equals(mode, MODE_LEARN)) {
            if (TextUtils.isEmpty(mName)) {
                setToolBarTitleAndRightBtn(mainApplication.getResources().getString(R.string.Infrared_ransponder_Projector), getResources().getString(R.string.Save));
            } else {
                setToolBarTitleAndRightBtn(mName, getResources().getString(R.string.Save));
            }
        }
    }

    //加载所有支持按键
    public void loadAllKeys() {
        mConfigBean = gatewayConfigCache.get(deviceID, "list");

        if (mConfigBean == null || TextUtils.isEmpty(mConfigBean.v)) {
            return;
        }

        List<UeiConfig> configs = JSON.parseArray(mConfigBean.v, UeiConfig.class);
        for (UeiConfig ueiConfig : configs) {
            if (TextUtils.equals(ueiConfig.time, createTime)) {
                if (isCustomRemote()) {
                    learnMap = ueiConfig.learnKeyCodeDic;
                    tvViewHelper.convertKeyToItem(ueiConfig.learnKeyCodeDic);
                } else {
                    tvViewHelper.convertKeyToItem(ueiConfig.supportKeyArr);
                }
                break;
            }
        }
    }

    //加载遥控器布局
    public void loadAllViews() {
        //加载固定键盘
        if (TextUtils.equals(mode, MODE_CONTROL)) {
            tvCenterControlView.setMode(TvCenterControlView.MODE_CONTROL);
            updateControlView(contentLayout);
        } else if (TextUtils.equals(mode, MODE_LEARN)) {
            tvCenterControlView.setMode(TvCenterControlView.MODE_LEARN);
            updateLearnView(contentLayout);
        }
        //加载中心圆盘
        upDateCenterKeys();
    }

    //加载按键视图---控制模式
    private void updateControlView(View view) {
        if (view != null) {
            if (view.getTag() != null) {
                ViewGroup viewParent = null;
                TextView textView = null;
                if (tvViewHelper.getKeyByTag(view.getTag().toString()) != null) {
                    switch (view.getTag().toString()) {
                        case "1":
                        case "121":
                        case "33":
                        case "22":
                        case "37":
                            view.setBackgroundResource(getResource("selector_tv_remote_c_" + view.getTag()));
                            break;
                        case "6":
                        case "4":
                            ImageView imageViewUp = (ImageView) view;
                            imageViewUp.setImageResource(getResource("selector_tv_remote_c_up"));
                            break;
                        case "7":
                        case "5":
                            ImageView imageViewDown = (ImageView) view;
                            imageViewDown.setImageResource(getResource("selector_tv_remote_c_down"));
                            break;
                        case "9":
                        case "10":
                        case "11":
                        case "12":
                        case "13":
                        case "14":
                        case "15":
                        case "16":
                        case "17":
                        case "18":
                            view.setBackgroundResource(getResource("selector_tv_remote_c_common"));
                            break;
                        case "207":
                            viewParent = ((ViewGroup)view.getParent());
                            textView = (TextView) viewParent.getChildAt(viewParent.getChildCount() - 1);
                            textView.setTextColor(getResources().getColor(R.color.v6_text_uei_key));
                            ImageView imageViewEnlarge = (ImageView) view;
                            imageViewEnlarge.setImageResource(getResource("selector_tv_remote_c_enlarge"));
                            break;
                        case "208":
                            viewParent = ((ViewGroup)view.getParent());
                            textView = (TextView) viewParent.getChildAt(viewParent.getChildCount() - 1);
                            textView.setTextColor(getResources().getColor(R.color.v6_text_uei_key));
                            ImageView imageViewLssen = (ImageView) view;
                            imageViewLssen.setImageResource(getResource("selector_tv_remote_c_lessen"));
                            break;
                        case "0":
                        case "8":
                        case "36":
                        case "24":
                        case "25":
                        case "43":
                        case "44":
                        case "46":
                        case "90":
                        case "136":
                            viewParent = ((ViewGroup)view.getParent());
                            textView = (TextView) viewParent.getChildAt(viewParent.getChildCount() - 1);
                            textView.setTextColor(getResources().getColor(R.color.v6_text_uei_key));
                            ImageView imageView = (ImageView) view;
                            imageView.setImageResource(getResource("selector_tv_remote_custom_bg"));
                            break;

                    }
                } else {
                    switch (view.getTag().toString()) {
                        case "1":
                        case "121":
                        case "33":
                        case "22":
                        case "37":
                            view.setBackgroundResource(getResource("tv_remote_unenable_" + view.getTag()));
                            break;
                        case "6":
                        case "4":
                            ImageView imageViewUp = (ImageView) view;
                            imageViewUp.setImageResource(getResource("tv_remote_unenable_up"));
                            break;
                        case "7":
                        case "5":
                            ImageView imageViewDown = (ImageView) view;
                            imageViewDown.setImageResource(getResource("tv_remote_unenable_down"));
                            break;
                        case "9":
                        case "10":
                        case "11":
                        case "12":
                        case "13":
                        case "14":
                        case "15":
                        case "16":
                        case "17":
                        case "18":
                            view.setBackgroundResource(getResource("tv_remote_unenable_common"));
                            break;
                        case "207":
                            viewParent = ((ViewGroup)view.getParent());
                            textView = (TextView) viewParent.getChildAt(viewParent.getChildCount() - 1);
                            textView.setTextColor(getResources().getColor(R.color.v6_text_gray));
                            ImageView imageViewEnlarge = (ImageView) view;
                            imageViewEnlarge.setImageResource(getResource("tv_remote_unenable_enlarge"));
                            break;
                        case "208":
                            viewParent = ((ViewGroup)view.getParent());
                            textView = (TextView) viewParent.getChildAt(viewParent.getChildCount() - 1);
                            textView.setTextColor(getResources().getColor(R.color.v6_text_gray));
                            ImageView imageViewLssen = (ImageView) view;
                            imageViewLssen.setImageResource(getResource("tv_remote_unenable_lessen"));
                            break;
                        case "0":
                        case "8":
                        case "36":
                        case "24":
                        case "25":
                        case "43":
                        case "44":
                        case "46":
                        case "90":
                        case "136":
                            viewParent = ((ViewGroup)view.getParent());
                            textView = (TextView) viewParent.getChildAt(viewParent.getChildCount() - 1);
                            textView.setTextColor(getResources().getColor(R.color.v6_text_gray));
                            ImageView imageView = (ImageView) view;
                            imageView.setImageResource(getResource("tv_remote_custom_bg_unenable"));
                            break;

                    }
                }
            }
        }
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            if (vg.getChildCount() > 0) {
                for (int i = 0; i < vg.getChildCount(); i++) {
                    updateControlView(vg.getChildAt(i));
                }
            }
        }
    }

    //加载按键视图---学习模式
    private void updateLearnView(View view) {
        if (view != null) {
            if (view.getTag() != null) {
                ViewGroup viewParent = null;
                TextView textView = null;
                if (tvViewHelper.getKeyByTag(view.getTag().toString()) != null) {
                    switch (view.getTag().toString()) {
                        case "1":
                        case "121":
                        case "33":
                        case "22":
                        case "37":
                            view.setBackgroundResource(getResource("selector_tv_remote_l_" + view.getTag()));
                            break;
                        case "6":
                        case "4":
                            ImageView imageViewUp = (ImageView) view;
                            imageViewUp.setImageResource(getResource("selector_tv_remote_l_up"));
                            break;
                        case "7":
                        case "5":
                            ImageView imageViewDown = (ImageView) view;
                            imageViewDown.setImageResource(getResource("selector_tv_remote_l_down"));
                            break;
                        case "9":
                        case "10":
                        case "11":
                        case "12":
                        case "13":
                        case "14":
                        case "15":
                        case "16":
                        case "17":
                        case "18":
                            view.setBackgroundResource(getResource("selector_tv_remote_l_common"));
                            break;
                        case "207":
                            viewParent = ((ViewGroup)view.getParent());
                            textView = (TextView) viewParent.getChildAt(viewParent.getChildCount() - 1);
                            textView.setTextColor(getResources().getColor(R.color.v6_text_uei_key));
                            ImageView imageViewEnlarge = (ImageView) view;
                            imageViewEnlarge.setImageResource(getResource("selector_tv_remote_l_enlarge"));
                            break;
                        case "208":
                            viewParent = ((ViewGroup)view.getParent());
                            textView = (TextView) viewParent.getChildAt(viewParent.getChildCount() - 1);
                            textView.setTextColor(getResources().getColor(R.color.v6_text_uei_key));
                            ImageView imageViewLssen = (ImageView) view;
                            imageViewLssen.setImageResource(getResource("selector_tv_remote_l_lessen"));
                            break;
                        case "0":
                        case "8":
                        case "36":
                        case "24":
                        case "25":
                        case "43":
                        case "44":
                        case "46":
                        case "90":
                        case "136":
                            viewParent = ((ViewGroup)view.getParent());
                            textView = (TextView) viewParent.getChildAt(viewParent.getChildCount() - 1);
                            textView.setTextColor(getResources().getColor(R.color.v6_text_uei_key));
                            ImageView imageView = (ImageView) view;
                            imageView.setImageResource(getResource("selector_tv_remote_custom_bg"));
                            break;
                    }
                } else {
                    switch (view.getTag().toString()) {
                        case "1":
                        case "121":
                        case "33":
                        case "22":
                        case "37":
                            view.setBackgroundResource(getResource("tv_remote_learn_" + view.getTag()));
                            break;
                        case "6":
                        case "4":
                            ImageView imageViewUp = (ImageView) view;
                            imageViewUp.setImageResource(getResource("tv_remote_learn_up"));
                            break;
                        case "7":
                        case "5":
                            ImageView imageViewDown = (ImageView) view;
                            imageViewDown.setImageResource(getResource("tv_remote_learn_down"));
                            break;
                        case "9":
                        case "10":
                        case "11":
                        case "12":
                        case "13":
                        case "14":
                        case "15":
                        case "16":
                        case "17":
                        case "18":
                            view.setBackgroundResource(getResource("tv_remote_learn_common"));
                            break;
                        case "207":
                            viewParent = ((ViewGroup)view.getParent());
                            textView = (TextView) viewParent.getChildAt(viewParent.getChildCount() - 1);
                            textView.setTextColor(getResources().getColor(R.color.v6_text_gray));
                            ImageView imageViewEnlarge = (ImageView) view;
                            imageViewEnlarge.setImageResource(getResource("tv_remote_learn_enlarge"));
                            break;
                        case "208":
                            viewParent = ((ViewGroup)view.getParent());
                            textView = (TextView) viewParent.getChildAt(viewParent.getChildCount() - 1);
                            textView.setTextColor(getResources().getColor(R.color.v6_text_gray));
                            ImageView imageViewLssen = (ImageView) view;
                            imageViewLssen.setImageResource(getResource("tv_remote_learn_lessen"));
                            break;
                        case "0":
                        case "8":
                        case "36":
                        case "24":
                        case "25":
                        case "43":
                        case "44":
                        case "46":
                        case "90":
                        case "136":
                            viewParent = ((ViewGroup)view.getParent());
                            textView = (TextView) viewParent.getChildAt(viewParent.getChildCount() - 1);
                            textView.setTextColor(getResources().getColor(R.color.v6_text_gray));
                            ImageView imageView = (ImageView) view;
                            imageView.setImageResource(getResource("tv_remote_custom_bg_learn"));
                            break;
                    }
                }
            }
        }
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            if (vg.getChildCount() > 0) {
                for (int i = 0; i < vg.getChildCount(); i++) {
                    updateLearnView(vg.getChildAt(i));
                }
            }
        }
    }

    //加载圆盘自定义view（上下左右确定键）
    private void upDateCenterKeys() {
        tvCenterControlView.updateViews(tvViewHelper.getKeyByTag(AREA_LEFT) == null ? false : true,
                tvViewHelper.getKeyByTag(AREA_TOP) == null ? false : true,
                tvViewHelper.getKeyByTag(AREA_RIGHT) == null ? false : true,
                tvViewHelper.getKeyByTag(AREA_BOTTOM) == null ? false : true,
                tvViewHelper.getKeyByTag(AREA_CENTER) == null ? false : true);
    }


    //更新单个按键视图
    private void upDateOneKey(View view, String tag, boolean enable) {
        if (view != null) {
            if (view.getTag() != null && TextUtils.equals((String) view.getTag(), tag)) {
                ViewGroup viewParent = null;
                TextView textView = null;
                if (enable) {
                    switch (view.getTag().toString()) {
                        case "1":
                        case "121":
                        case "33":
                        case "22":
                        case "37":
                            view.setBackgroundResource(getResource("selector_tv_remote_c_" + view.getTag()));
                            break;
                        case "6":
                        case "4":
                            ImageView imageViewUp = (ImageView) view;
                            imageViewUp.setImageResource(getResource("selector_tv_remote_c_up"));
                            break;
                        case "7":
                        case "5":
                            ImageView imageViewDown = (ImageView) view;
                            imageViewDown.setImageResource(getResource("selector_tv_remote_c_down"));
                            break;
                        case "9":
                        case "10":
                        case "11":
                        case "12":
                        case "13":
                        case "14":
                        case "15":
                        case "16":
                        case "17":
                        case "18":
                            view.setBackgroundResource(getResource("selector_tv_remote_c_common"));
                            break;
                        case "207":
                            viewParent = ((ViewGroup)view.getParent());
                            textView = (TextView) viewParent.getChildAt(viewParent.getChildCount() - 1);
                            textView.setTextColor(getResources().getColor(R.color.v6_text_uei_key));
                            ImageView imageViewEnlarge = (ImageView) view;
                            imageViewEnlarge.setImageResource(getResource("selector_tv_remote_c_enlarge"));
                            break;
                        case "208":
                            viewParent = ((ViewGroup)view.getParent());
                            textView = (TextView) viewParent.getChildAt(viewParent.getChildCount() - 1);
                            textView.setTextColor(getResources().getColor(R.color.v6_text_uei_key));
                            ImageView imageViewLssen = (ImageView) view;
                            imageViewLssen.setImageResource(getResource("selector_tv_remote_c_lessen"));
                            break;
                        case "0":
                        case "8":
                        case "36":
                        case "24":
                        case "25":
                        case "43":
                        case "44":
                        case "46":
                        case "90":
                        case "136":
                            viewParent = ((ViewGroup)view.getParent());
                            textView = (TextView) viewParent.getChildAt(viewParent.getChildCount() - 1);
                            textView.setTextColor(getResources().getColor(R.color.v6_text_uei_key));
                            ImageView imageView = (ImageView) view;
                            imageView.setImageResource(getResource("selector_tv_remote_custom_bg"));
                            break;

                    }
                } else {
                    switch (view.getTag().toString()) {
                        case "1":
                        case "121":
                        case "33":
                        case "22":
                        case "37":
                            view.setBackgroundResource(getResource("tv_remote_learn_" + view.getTag()));
                            break;
                        case "6":
                        case "4":
                            ImageView imageViewUp = (ImageView) view;
                            imageViewUp.setImageResource(getResource("tv_remote_learn_up"));
                            break;
                        case "7":
                        case "5":
                            ImageView imageViewDown = (ImageView) view;
                            imageViewDown.setImageResource(getResource("tv_remote_learn_down"));
                            break;
                        case "9":
                        case "10":
                        case "11":
                        case "12":
                        case "13":
                        case "14":
                        case "15":
                        case "16":
                        case "17":
                        case "18":
                            view.setBackgroundResource(getResource("tv_remote_learn_common"));
                            break;
                        case "207":
                            viewParent = ((ViewGroup)view.getParent());
                            textView = (TextView) viewParent.getChildAt(viewParent.getChildCount() - 1);
                            textView.setTextColor(getResources().getColor(R.color.v6_text_gray));
                            ImageView imageViewEnlarge = (ImageView) view;
                            imageViewEnlarge.setImageResource(getResource("tv_remote_learn_enlarge"));
                            break;
                        case "208":
                            viewParent = ((ViewGroup)view.getParent());
                            textView = (TextView) viewParent.getChildAt(viewParent.getChildCount() - 1);
                            textView.setTextColor(getResources().getColor(R.color.v6_text_gray));
                            ImageView imageViewLssen = (ImageView) view;
                            imageViewLssen.setImageResource(getResource("tv_remote_learn_lessen"));
                            break;
                        case "0":
                        case "8":
                        case "36":
                        case "24":
                        case "25":
                        case "43":
                        case "44":
                        case "46":
                        case "90":
                        case "136":
                            viewParent = ((ViewGroup)view.getParent());
                            textView = (TextView) viewParent.getChildAt(viewParent.getChildCount() - 1);
                            textView.setTextColor(getResources().getColor(R.color.v6_text_gray));
                            ImageView imageView = (ImageView) view;
                            imageView.setImageResource(getResource("tv_remote_custom_bg_learn"));
                            break;
                    }
                }
                return;
            }
            if (view instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) view;
                if (vg.getChildCount() > 0) {
                    for (int i = 0; i < vg.getChildCount(); i++) {
                        upDateOneKey(vg.getChildAt(i), tag, enable);
                    }
                }
            }
        }
    }

    public int getResource(String imageName) {
        Context ctx = getBaseContext();
        int resId = getResources().getIdentifier(imageName, "drawable", ctx.getPackageName());
        return resId;
    }

    private boolean isCustomRemote() {
        if (TextUtils.isEmpty(mPickCode)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_left:
                onBackPressed();
                break;
            case R.id.btn_right:
                //保存
                saveCustomRemoteInfo();
                break;
            case R.id.img_right:
                //更多
                ControllerMoreActivity.start(ProjectorRemoteMainActivity.this, deviceID, createTime, mName, type, SINGLE_CODE_PROJECTOR);
                break;
            default:
                break;
        }
    }

    //递归添加事件
    private void registerKeyEvent(View view) {
        if (view != null) {
            if (view.getTag() != null) {
                if (TextUtils.equals(mode, MODE_CONTROL)) {
                    view.setOnClickListener(controlClickListener);
                } else if (TextUtils.equals(mode, MODE_LEARN)) {
                    view.setOnClickListener(learnClickListener);
                    view.setOnLongClickListener(deleteClickListener);
                }
            }
            if (view instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) view;
                if (vg.getChildCount() > 0) {
                    for (int i = 0; i < vg.getChildCount(); i++) {
                        registerKeyEvent(vg.getChildAt(i));
                    }
                }
            }
        }
    }

    private View.OnClickListener controlClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() != null) {
                if (tvViewHelper.getKeyByTag(v.getTag().toString()) != null) {
                    sendControlCmd(v.getTag().toString());
                } else {
                    ToastUtil.single(R.string.Infraredtransponder_No_Infraredcode);
                }
            }
        }
    };

    private View.OnClickListener learnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() != null) {
                sendLearnCmd(v.getTag().toString());
            }
        }
    };

    private View.OnLongClickListener deleteClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (v.getTag() != null) {
                if (learnMap.containsKey((String) v.getTag())){
                    deleteCodePopup.showParent(v, v.getTag().toString());
                }
            }
            return true;
        }
    };

    //发送控制命令
    public void sendControlCmd(String tag) {
        TvViewHelper.TvItem item = tvViewHelper.getKeyByTag(tag);
        if (item != null) {
            StringBuilder data = new StringBuilder("0A000801");
            if (!isCustomRemote()) {
                data.append("00");
                data.append(StringUtil.toHexString(Integer.parseInt(mPickCode.substring(1, mPickCode.length())), 4));
                data.append(StringUtil.toHexString(Integer.parseInt(tag), 2));
                data.append("800000");
            } else {
                data.append("00000000");
                data.append("A0");
                data.append(item.getValue());
            }

            if (isSceneOrHouseKeeper){
                EventBus.getDefault().post(new UeiSceneEvent(mName, data.toString().toUpperCase()));
                setResult(RESULT_OK);
                finish();
            }else {
                JSONObject object = new JSONObject();
                try {
                    object.put("cmd", "501");
                    object.put("devID", deviceID);
                    object.put("clusterId", 0x0F01);
                    object.put("commandType", 1);
                    object.put("gwID", device.gwID);
                    object.put("commandId", 0x8010);
                    object.put("endpointNumber", 1);
                    JSONArray array = new JSONArray();
                    array.put(data.toString().toUpperCase());
                    object.put("parameter", array);
                    MainApplication.getApplication()
                            .getMqttManager()
                            .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    //发送学习命令
    public void sendLearnCmd(String tag) {
        showMatchingDialog();
        StringBuilder data = new StringBuilder("0A000307");
        currentTag = tag;
        currentValue = StringUtil.toHexString(gatewayConfigCache.getMaxNumByDeviceId(deviceID) + 1, 4);
        data.append(currentValue);

        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("devID", deviceID);
            object.put("clusterId", 0x0F01);
            object.put("commandType", 1);
            object.put("gwID", device.gwID);
            object.put("commandId", 0x8010);
            object.put("endpointNumber", 1);

            JSONArray array = new JSONArray();
            array.put(data.toString().toUpperCase());
            object.put("parameter", array);
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //删除
    public void sendDeleteCmd(String tag) {
        StringBuilder data = new StringBuilder("0A000308");
        currentTag = tag;
        String value = learnMap.get(currentTag);
        data.append(value);

        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("devID", deviceID);
            object.put("clusterId", 0x0F01);
            object.put("commandType", 1);
            object.put("gwID", device.gwID);
            object.put("commandId", 0x8010);
            object.put("endpointNumber", 1);

            JSONArray array = new JSONArray();
            array.put(data.toString().toUpperCase());
            object.put("parameter", array);
            progressDialogManager.showDialog(DELETE, ProjectorRemoteMainActivity.this, getString(R.string.Device_List_Delete), null, getResources().getInteger(R.integer.http_timeout));
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //保存按键信息
    private void saveCustomRemoteInfo() {
        boolean isExist = false;
        int m = 2;
        List<UeiConfig> configs;
        GatewayConfigBean bean = MainApplication.getApplication().getGatewayConfigCache().get(deviceID, "list");
        if (bean == null) {
            m = 1;
            configs = new ArrayList<>();
        } else {
            if (TextUtils.isEmpty(bean.v)) {
                m = 1;
                configs = new ArrayList<>();
            } else {
                configs = JSON.parseArray(bean.v, UeiConfig.class);
            }
        }

        String[] supportKeyArr = new String[learnMap.size()];
        int i = 0;
        for (String key : learnMap.keySet()) {
            supportKeyArr[i++] = key;
        }

        String time = System.currentTimeMillis() + "";

        for (UeiConfig config : configs) {
            if (TextUtils.equals(config.time, createTime)) {
                isExist = true;
                config.time = time;
                config.supportKeyArr = supportKeyArr;
                config.learnKeyCodeDic = learnMap;
                break;
            }
        }
        if (!isExist) {
            UeiConfig ueiConfig = UeiConfig.newUeiDevice(time, null, mName, null, type, SINGLE_CODE_PROJECTOR, supportKeyArr, learnMap);
            configs.add(ueiConfig);
        }
        createTime = time + "";

        String v = Base64.encodeToString(JSON.toJSONString(configs).getBytes(), Base64.NO_WRAP);
        progressDialogManager.showDialog(SAVE, ProjectorRemoteMainActivity.this, getString(R.string.Disposing), null, getResources().getInteger(R.integer.http_timeout));
        MainApplication.getApplication().getMqttManager().publishEncryptedMessage(
                MQTTCmdHelper.createGatewayConfig(
                        Preference.getPreferences().getCurrentGatewayID(),
                        m,
                        MainApplication.getApplication().getLocalInfo().appID,
                        deviceID,
                        "list",
                        v,
                        System.currentTimeMillis() + ""
                ), MQTTManager.MODE_GATEWAY_FIRST
        );
    }

    private void showMatchingDialog() {
        if (matchingDialog == null){
            builder = new WLDialog.Builder(this);
            builder.setContentView(R.layout.dialog_tv_remote_match_content)
                    .setCancelOnTouchOutSide(false)
                    .setDismissAfterDone(false)
                    .setPositiveButton(this.getResources().getString(R.string.Infraredrelay_Custom_Matching))
                    .setListener(new WLDialog.MessageListener() {
                        @Override
                        public void onClickPositive(View view, String msg) {
                        }

                        @Override
                        public void onClickNegative(View view) {
                        }
                    });
            matchingDialog = builder.create();
        }
        if (!matchingDialog.isShowing()) {
            matchingDialog.show();
        }
    }

    private void showMatchingFailDialog() {
        if (matchingFailDialog == null){
            builder.setTitle(R.string.Infraredrelay_Custom_Matchfailed)
                    .setCancelOnTouchOutSide(false)
                    .setDismissAfterDone(false)
                    .setMessage(R.string.Infraredrelay_Custom_Matchfailed_Prompt)
                    .setPositiveButton(this.getResources().getString(R.string.Sure))
                    .setListener(new WLDialog.MessageListener() {
                        @Override
                        public void onClickPositive(View view, String msg) {
                            matchingFailDialog.dismiss();
                        }

                        @Override
                        public void onClickNegative(View view) {
                        }
                    });
            matchingFailDialog = builder.create();
        }
        if (!matchingFailDialog.isShowing()) {
            matchingFailDialog.show();
        }
    }

    private class CustomViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;
        private JSONArray data;

        public CustomViewAdapter(Context context, JSONArray data) {
            this.context = context;
            this.data = data;
        }

        public void update() {
            notifyDataSetChanged();
        }

        public void removeData(int position){
            notifyItemRemoved(position);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            ItemView viewholder = new ItemView(layoutInflater.inflate(R.layout.item_tv_remote_custom, parent, false));
            return viewholder;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            if (TextUtils.equals(mode, MODE_LEARN)) {
                ((ItemView) holder).imageView.setImageResource(R.drawable.tv_remote_custom_bg_learn);
                ((ItemView) holder).textView.setTextColor(getResources().getColor(R.color.v6_text_gray));
            }else{
                ((ItemView) holder).imageView.setImageResource(R.drawable.selector_tv_remote_custom_bg);
                ((ItemView) holder).textView.setTextColor(getResources().getColor(R.color.v6_text_uei_key));
            }
            try {
                String name = data.getJSONObject(position).names().get(0).toString();
                ((ItemView) holder).imageView.setTag(name);
                ((ItemView) holder).textView.setText(data.getJSONObject(position).getString(name));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ((ItemView) holder).imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.equals(mode, MODE_CONTROL)) {
                        if (tvViewHelper.getKeyByTag(v.getTag().toString()) != null) {
                            sendControlCmd(v.getTag().toString());
                        } else {
                            ToastUtil.single(R.string.Infraredtransponder_No_Infraredcode);
                        }
                    } else if (TextUtils.equals(mode, MODE_LEARN)) {
                        sendLearnCmd(v.getTag().toString());
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.length();
        }

        class ItemView extends RecyclerView.ViewHolder {
            private ImageView imageView;
            private TextView textView;

            public ItemView(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.item_bg);
                textView = (TextView) itemView.findViewById(R.id.item_text);
            }
        }
    }

    private void dealData(String data) {
        try {
            JSONObject object = new JSONObject(data);
            JSONArray endpoints = object.optJSONArray("endpoints");
            JSONArray clusters = ((JSONObject) endpoints.get(0)).optJSONArray("clusters");
            JSONArray attributes = ((JSONObject) clusters.get(0)).optJSONArray("attributes");
            String attributeValue = ((JSONObject) attributes.get(0)).optString("attributeValue");
            String command = attributeValue.substring(2, 4);
            String result = attributeValue.substring(attributeValue.length() - 2, attributeValue.length());
            //07学习
            if (TextUtils.equals(command, "07")) {
                matchingDialog.dismiss();
                //00成功
                if (TextUtils.equals(result, "00")) {
                    ToastUtil.single(R.string.Infraredrelay_Custom_Matchsuccessfully);
                    learnMap.put(currentTag, currentValue);
                    gatewayConfigCache.setMaxNumByDeviceId(deviceID, gatewayConfigCache.getMaxNumByDeviceId(deviceID) + 1);
                    //刷新界面
                    upDateOneKey(contentLayout, currentTag, true);
                    tvCenterControlView.updateView(currentTag, true);
                }
                //超时
                else if (TextUtils.equals(result, "06")) {
                    showMatchingFailDialog();
                } else {
                    showMatchingFailDialog();
                }
            }
            //08删除
            else if (TextUtils.equals(command, "08")) {
//                if (TextUtils.equals(result, "00") || TextUtils.equals(result, "0A")) {
                progressDialogManager.dimissDialog(DELETE, 0);
                learnMap.remove(currentTag);
                upDateOneKey(contentLayout, currentTag, false);
                tvCenterControlView.updateView(currentTag, false);
//                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && device != null) {
            if (TextUtils.equals(event.device.devID, device.devID)) {
                dealData(event.device.data);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGatewayConfigEvent(GatewayConfigEvent event) {
        if (!TextUtils.equals(event.bean.d, deviceID)) {
            return;
        }
        GatewayConfigBean bean = MainApplication.getApplication().getGatewayConfigCache().get(deviceID, "list");
        if (bean != null) {
            if (!TextUtils.isEmpty(bean.v)) {
                boolean hasMe = false;
                List<UeiConfig> configs = JSON.parseArray(bean.v, UeiConfig.class);
                for (UeiConfig config : configs) {
                    if (TextUtils.equals(createTime, config.time)) {
                        mName = config.getName();

                        setTitle();

                        hasMe = true;
                        break;
                    }
                }

                if (TextUtils.equals(mode, MODE_LEARN)) {
                    // 添加成功
                    if (hasMe) {
                        progressDialogManager.dimissDialog(SAVE, 0);
                        tvViewHelper.addKeys(learnMap);
                        Device23Activity.start(this, deviceID, false);
                        finish();
                    }
                } else if (TextUtils.equals(mode, MODE_CONTROL)) {
                    // 删除成功
                    if (!hasMe) {
                        finish();
                    }
                }
            }
        }else {
            finish();
        }
    }
}
