package cc.wulian.smarthomev6.main.home.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.LocalInfo;
import cc.wulian.smarthomev6.entity.SceneInfo;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.home.adapter.SceneAllAdapter;
import cc.wulian.smarthomev6.main.home.scene.SceneManager;
import cc.wulian.smarthomev6.main.login.SigninActivity;
import cc.wulian.smarthomev6.main.mine.gatewaycenter.GatewayListActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceIsPushBean;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.customview.EmptyGWBindPop;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GatewayStateChangedEvent;
import cc.wulian.smarthomev6.support.event.GetSceneListEvent;
import cc.wulian.smarthomev6.support.event.LoginEvent;
import cc.wulian.smarthomev6.support.event.SceneInfoEvent;
import cc.wulian.smarthomev6.support.event.SortSceneEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.BitmapUtil;
import cc.wulian.smarthomev6.support.utils.DrawableUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * 作者: chao
 * 时间: 2017/5/8
 * 描述: 首页场景
 * 联系方式: 805901025@qq.com
 */

public class HomeWidgetScene extends RelativeLayout implements IWidgetLifeCycle {
    private static final String TAG = HomeWidgetScene.class.getSimpleName();
    private static final String VIRTUAL_GW_ID = "000000000000";
    private RecyclerView mSceneRecyclerView;
    private HomeSceneAdapter mHomeSceneAdapter;
    private GridLayoutManager mSceneGridLayoutManager;
    private SceneManager sceneManager;
    private Preference preference = Preference.getPreferences();
    private List<SceneInfo> sceneInfos;
    private long lastTime = 0;
    private int clickPosition = -1;//记录点击位置，防止同时点击
    private Handler mHandler;
    private ValueAnimator mLoadingAnimator;
    private EmptyGWBindPop popupWindow;
    private Context mContext;
    private View rootView;
    private TextView tvNoData;
    private DeviceApiUnit deviceApiUnit;

    public HomeWidgetScene(Context context) {
        super(context);
        this.mContext = context;
        sceneManager = new SceneManager(context);
        sceneInfos = new ArrayList<>();
        initView(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

//        EventBus.getDefault().register(this);
//        WLog.i(TAG, "HomeIWidgetScene: 注册事件");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        // view 销毁时, 注销 EventBus
//        EventBus.getDefault().unregister(this);
//        WLog.i(TAG, "onDetachedFromWindow: 注销事件");
    }

    private void initView(final Context context) {
        rootView = LayoutInflater.from(context).inflate(R.layout.fragment_home_scene, null);
        tvNoData = rootView.findViewById(R.id.tv_no_data);
        addView(rootView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mHomeSceneAdapter = new HomeSceneAdapter(context, null);
        mSceneRecyclerView = (RecyclerView) findViewById(R.id.home_scene_recyclerview);
        mSceneGridLayoutManager = new GridLayoutManager(context, 4);
        mSceneRecyclerView.setLayoutManager(mSceneGridLayoutManager);
        mSceneRecyclerView.setAdapter(mHomeSceneAdapter);
        mSceneRecyclerView.setHasFixedSize(true);
        mSceneRecyclerView.setNestedScrollingEnabled(false);
        mHomeSceneAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                if (mHomeSceneAdapter.isEmpty()) {
                    tvNoData.setVisibility(View.VISIBLE);
                } else {
                    tvNoData.setVisibility(View.GONE);
                }
            }
        });
        mHomeSceneAdapter.setOnClickListener(new SceneAllAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                if (preference.isLogin()) {
                    if (StringUtil.isNullOrEmpty(preference.getCurrentGatewayID())) {
                        showPopWindow();
                    } else {
                        openSceneByPosition(position);
                        final View itemView = mSceneGridLayoutManager.findViewByPosition(position);
                        WLog.i("luzx", "click");
                        lastTime = System.currentTimeMillis();//记录item点击时间
                        final View loadingView = itemView.findViewById(R.id.loading_layout);
                        final ImageView loadingIcon = (ImageView) itemView.findViewById(R.id.loading_icon);
                        loadingView.setVisibility(View.VISIBLE);
                        if (mLoadingAnimator == null) {
                            mLoadingAnimator = ValueAnimator.ofFloat(0, 360 * 16);
                            mLoadingAnimator.setInterpolator(new LinearInterpolator());
                            mLoadingAnimator.setDuration(10000);
                        }
                        mLoadingAnimator.removeAllUpdateListeners();
                        mLoadingAnimator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                if (System.currentTimeMillis() - lastTime > 1000) {
                                    loadingView.setVisibility(View.GONE);
                                    clickPosition = -1;
                                }
                            }
                        });
                        mLoadingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                loadingIcon.setRotation((float) animation.getAnimatedValue());
                            }
                        });
                        mLoadingAnimator.start();
                    }
                } else {
                    getContext().startActivity(new Intent(getContext(), SigninActivity.class));
                }
            }
        });

        reloadSceneData();
    }


    private void showPopWindow() {
        popupWindow = new EmptyGWBindPop(getContext());
        popupWindow.setOnPopClickListener(new EmptyGWBindPop.onPopClickListener() {
            @Override
            public void virtualGateway() {
                bindVirtualGateway();
            }

            @Override
            public void bindGateway() {
                mContext.startActivity(new Intent(getContext(), GatewayListActivity.class));
            }

            @Override
            public void cancel() {

            }
        });
        popupWindow.showAtLocation(rootView,
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popupWindow.backgroundAlpha((Activity) mContext, 1f);
            }
        });
    }

    private void bindVirtualGateway() {
        deviceApiUnit = new DeviceApiUnit(mContext);
        final String id = VIRTUAL_GW_ID;
        final String password = "Wulian123";
        deviceApiUnit.doBindDevice(id, password, "GW99", new DeviceApiUnit.DeviceApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                deviceApiUnit.doGetAllDevice(new DeviceApiUnit.DeviceApiCommonListener<List<DeviceBean>>() {
                    @Override
                    public void onSuccess(List<DeviceBean> deviceBeanList) {
                        for (DeviceBean deviceBean : deviceBeanList) {
                            if (deviceBean.deviceId.equalsIgnoreCase(VIRTUAL_GW_ID)) {
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

    public void reloadSceneData() {
        WLog.i(TAG, "reloadSceneData: " + preference.getCurrentGatewayID());
        if (mHomeSceneAdapter != null) {
            sceneInfos = sceneManager.acquireScene();
            mHomeSceneAdapter.update(sceneInfos);
        }
    }

    private void openSceneByPosition(int position) {
        if (preference.getCurrentGatewayState().equals("0")) {
            ToastUtil.show(R.string.Gateway_Offline);
            return;
        }

        // 切换场景
        sceneManager.toggleScene(sceneInfos.get(position));
        // TODO: 2017/3/20 这里可以优化刷新的数量 减少绘制
    }

    @Override
    public void onBindViewHolder(HomeItemBean bean) {
        EventBus.getDefault().register(this);
        WLog.i(TAG, "onBindViewHolder: 注册事件");
        reloadSceneData();
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
        WLog.i(TAG, "onViewRecycled: 注销事件");
    }

    class HomeSceneAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;
        private List<SceneInfo> mList;
        private SceneAllAdapter.OnItemClickListener onClickListener;

        public HomeSceneAdapter(Context context, ArrayList<SceneInfo> list) {
            this.context = context;
            mList = list;
        }

        public void setOnClickListener(SceneAllAdapter.OnItemClickListener listener) {
            this.onClickListener = listener;
        }

        public void update(List<SceneInfo> list) {
            if (list != null) {
                mList = list;
                notifyDataSetChanged();
            }
        }

        private int getColorByPosition(int position) {
            int color = 0xffff6362;
            switch (position) {
                case 0:
                    color = 0xffff6362;
                    break;
                case 1:
                    color = 0xffff840c;
                    break;
                case 2:
                    color = 0xffffbb19;
                    break;
                case 3:
                    color = 0xffb940ff;
                    break;
                case 4:
                    color = 0xff19f132;
                    break;
                case 5:
                    color = 0xff0bddda;
                    break;
                case 6:
                    color = 0xff12aaff;
                    break;
                case 7:
                    color = 0xff4940ff;
                    break;
            }
            return color;
        }

        public boolean isEmpty() {
            return mList.isEmpty();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            ItemView viewholder = new ItemView(layoutInflater.inflate(R.layout.home_scene_item, parent, false));

            return viewholder;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            SceneInfo info = new SceneInfo();
            int colorInt = getColorByPosition(position);
            if (mList != null && mList.size() != 0 && mList.size() > position) {
                info = mList.get(position);
                if (TextUtils.equals(info.getStatus(), "2")) {
                    ((ItemView) holder).name.setText(info.getName() + context.getString(R.string.Home_Scene_IsOpen));
                    ((ItemView) holder).name.setTextColor(context.getResources().getColor(R.color.v6_text_green));
                } else {
                    ((ItemView) holder).name.setText(info.getName());
                    ((ItemView) holder).name.setTextColor(context.getResources().getColor(R.color.v6_text_gray_dark));
                }
//                ((ItemView) holder).imageView.setImageDrawable(sceneManager.getSceneIconQuick(context, info.getIcon()));
                Bitmap bitmap = DrawableUtil.drawableToBitmap(SceneManager.getSceneIconQuick(context, info.getIcon()));

                ((ItemView) holder).imageView.setImageBitmap(BitmapUtil.changeBitmapColor(bitmap, colorInt));
                ((ItemView) holder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (clickPosition != -1) {
                            return;
                        }
                        clickPosition = position;
                        onClickListener.onItemClick(position);
                    }
                });
            }
            ((ItemView) holder).loadingView.setVisibility(View.GONE);

        }

        @Override
        public int getItemCount() {
            if (mList == null) {
                return 0;
            }

            if (mList.size() == 0) {
                return 0;
            } else if (mList.size() < 8) {
                return mList.size();
            } else {
                return 8;
            }
        }

        class ItemView extends RecyclerView.ViewHolder {
            private ImageView imageView;
            private View loadingView;
            private TextView name;

            public ItemView(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.scene_icon);
                loadingView = itemView.findViewById(R.id.loading_layout);
                name = (TextView) itemView.findViewById(R.id.scene_name);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSceneUpdated(SceneInfoEvent event) {
        long intval = System.currentTimeMillis() - lastTime;
        if (intval > 1000) {
            clickPosition = -1;
            reloadSceneData();
        } else {
            if (mHandler == null) {
                mHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 1) {
                            Log.i("luzx", "setVisibility:" + (System.currentTimeMillis() - lastTime));
                            clickPosition = -1;
                            reloadSceneData();
                        }
                    }
                };
            }
            mHandler.removeMessages(1);
            mHandler.sendEmptyMessageDelayed(1, 1000 - intval);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSceneReport(GetSceneListEvent event) {
        reloadSceneData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSortSceneEvent(SortSceneEvent event) {
        reloadSceneData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginEvent(LoginEvent event) {
        reloadSceneData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGatewayStateChangedEvent(GatewayStateChangedEvent event) {
        reloadSceneData();
    }
}
