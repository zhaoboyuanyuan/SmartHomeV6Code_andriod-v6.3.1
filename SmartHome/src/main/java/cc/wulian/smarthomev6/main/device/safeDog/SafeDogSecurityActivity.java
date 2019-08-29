package cc.wulian.smarthomev6.main.device.safeDog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.device.adapter.SafedogScanResultAdapter;
import cc.wulian.smarthomev6.main.device.adapter.SafedogScanningAdapter;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.SafeDogScheduleBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.SafeDogSchedulesBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.SafeDogSchedulesDetailBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.event.SafeDogInitEvent;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

public class SafeDogSecurityActivity extends BaseTitleActivity {

    private static final int GET_DATA = 0;
    //初始化
    public ConstraintLayout initLayout;
    public ImageView initLoadingImageView;
    //扫描
    public ConstraintLayout scanningLayout;
    public RecyclerView scanningRecyclerView;
    public Button btn_right;
    //安全
    public ConstraintLayout safeLayout;
    public TextView statisticsTextView;
    public Button backButton;
    //不安全
    public RelativeLayout unsafeLayout;
    public RecyclerView unsafeRecyclerView;
    public Button reScanButton;
    public TextView dataTextView;

    private Animation rotationAni;
    private DataApiUnit dataApiUnit;
    private SafedogScanningAdapter scaningAdapter;
    private SafedogScanResultAdapter scaningResultAdapter;
    public Device device;
    public String devId;
    private int deviceNum;
    private int schedulesNum;
    private int problemNum;

    private Handler dataHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            getScanData();
        }
    };

    public static void start(Activity activity, String devId) {
        Intent intent = new Intent(activity, SafeDogSecurityActivity.class);
        intent.putExtra("devId", devId);
        activity.startActivityForResult(intent, 2);
    }

    /**
     * @param type 1:初始化 2:扫描 3:安全 4:不安全
     */
    private void updateState(int type) {
        if (type == 1) {
            initLayout.setVisibility(View.VISIBLE);
            scanningLayout.setVisibility(View.GONE);
            safeLayout.setVisibility(View.GONE);
            unsafeLayout.setVisibility(View.GONE);
            btn_right.setVisibility(View.VISIBLE);

            if (null == rotationAni){
                rotationAni = new RotateAnimation(0f, 360f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                rotationAni.setDuration(2000);
                rotationAni.setRepeatMode(Animation.RESTART);
                rotationAni.setRepeatCount(-1);
                rotationAni.setInterpolator(new LinearInterpolator());
            }
            if (initLoadingImageView.getAnimation() == null){
                initLoadingImageView.clearAnimation();
                initLoadingImageView.setAnimation(rotationAni);
                rotationAni.start();
            }
        } else if (type == 2) {
            initLayout.setVisibility(View.GONE);
            scanningLayout.setVisibility(View.VISIBLE);
            safeLayout.setVisibility(View.GONE);
            unsafeLayout.setVisibility(View.GONE);
            btn_right.setVisibility(View.VISIBLE);
        } else if (type == 3) {
            initLayout.setVisibility(View.GONE);
            scanningLayout.setVisibility(View.GONE);
            safeLayout.setVisibility(View.VISIBLE);
            unsafeLayout.setVisibility(View.GONE);
            btn_right.setVisibility(View.GONE);
        } else if (type == 4) {
            initLayout.setVisibility(View.GONE);
            scanningLayout.setVisibility(View.GONE);
            safeLayout.setVisibility(View.GONE);
            unsafeLayout.setVisibility(View.VISIBLE);
            btn_right.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataApiUnit = new DataApiUnit(this);
        setContentView(R.layout.activity_safe_dog_security, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataHandler.removeMessages(GET_DATA);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        devId = getIntent().getStringExtra("devId");
        device = mainApplication.getDeviceCache().get(devId);
        setToolBarTitle(getResources().getString(R.string.Device_safe));
//        setToolBarTitleAndRightBtn(getResources().getString(R.string.Device_safe), getResources().getString(R.string.Cancel));
    }

    @Override
    protected void initView() {
        btn_right = getBtn_right();
        initLayout = (ConstraintLayout) findViewById(R.id.layout_init);
        safeLayout = (ConstraintLayout) findViewById(R.id.layout_safe);
        scanningLayout = (ConstraintLayout) findViewById(R.id.layout_scanning);
        unsafeLayout = (RelativeLayout) findViewById(R.id.layout_unsafe);
        scanningRecyclerView = (RecyclerView) findViewById(R.id.rv_scanning);
        unsafeRecyclerView = (RecyclerView) findViewById(R.id.rv_unsafe);
        initLoadingImageView = (ImageView) findViewById(R.id.iv_init);
        statisticsTextView = (TextView) findViewById(R.id.tv_statistics);
        dataTextView = (TextView) findViewById(R.id.tv_data);
        backButton = (Button) findViewById(R.id.btn_back);
        reScanButton = (Button) findViewById(R.id.btn_reScan);

        scaningResultAdapter = new SafedogScanResultAdapter(this);
        unsafeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        unsafeRecyclerView.setHasFixedSize(true);
        unsafeRecyclerView.setNestedScrollingEnabled(false);
        unsafeRecyclerView.setItemAnimator(new DefaultItemAnimator());
        unsafeRecyclerView.setAdapter(scaningResultAdapter);

        scaningAdapter = new SafedogScanningAdapter(this);
        scanningRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        scanningRecyclerView.setHasFixedSize(true);
        scanningRecyclerView.setItemAnimator(new DefaultItemAnimator());
        scanningRecyclerView.setAdapter(scaningAdapter);

        updateState(1);
    }

    @Override
    protected void initData() {
        getScanData();
    }

    @Override
    protected void initListeners() {
        backButton.setOnClickListener(this);
        reScanButton.setOnClickListener(this);
    }

    @Override
    public void onClickView(View v) {
        super.onClickView(v);
        switch (v.getId()) {
            case R.id.btn_reScan:
                initScan();
                break;
            case R.id.btn_back:
                initScan();
                break;
        }
    }

    //发起主动扫描, 等待初始化
    private void initScan() {
        updateState(1);
        scaningAdapter.clear();
        dataHandler.sendEmptyMessageDelayed(GET_DATA, 2000);
        dataApiUnit.doSafedogScan(devId, new DataApiUnit.DataApiCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {

            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.single(msg);
            }
        });
    }

    //轮循扫描数据、判断状态
    private void getScanData() {
        new DataApiUnit(this).doGetSafedogSchedules(devId, new DataApiUnit.DataApiCommonListener<SafeDogScheduleBean>() {

            @Override
            public void onSuccess(SafeDogScheduleBean bean) {
                if (bean.scanStatus == 0) {                                           //未扫描
                    initScan();
                } else if (bean.scanStatus == 1) {                                    //扫描中：1
                    dataHandler.removeMessages(GET_DATA);
                    dataHandler.sendEmptyMessageDelayed(GET_DATA, 5000);
                    scaningAdapter.setData(bean.schedules);
                    updateState(2);
                } else if (bean.scanStatus == 2) {                                     //初始化：2
                    dataHandler.removeMessages(GET_DATA);
                    dataHandler.sendEmptyMessageDelayed(GET_DATA, 5000);
                    updateState(1);
                } else if (bean.scanStatus == 3) {                                     //扫描完成
                    doCount(bean.schedules);
                    if (problemNum != 0) {
                        dataTextView.setText(String.format(getResources().getString(R.string.Device_safe001), String.valueOf(deviceNum), String.valueOf(schedulesNum), String.valueOf(problemNum)));
                        scaningResultAdapter.setData(bean.schedules);
                        updateState(4);
                    } else {
                        statisticsTextView.setText(String.format(getResources().getString(R.string.Device_safe009), String.valueOf(deviceNum), String.valueOf(schedulesNum)));
                        updateState(3);
                    }
                }
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.single(msg);
            }
        });
    }

    //分析扫描结果
    private void doCount(SafeDogSchedulesBean bean) {
        deviceNum = 0;
        problemNum = 0;
        schedulesNum = 0;
        if (null == bean) {
            return;
        }
        List<String> deviceId = new ArrayList<>();
        for (int i = 0; i < bean.getData().size(); i++) {
            ArrayList<SafeDogSchedulesDetailBean> itemList = bean.getData().get(i);
            if (null != itemList) {
                for (SafeDogSchedulesDetailBean itemBean : itemList) {
                    if (!deviceId.contains(itemBean.deviceId)) {
                        deviceId.add(itemBean.deviceId);
                    }
                    schedulesNum++;
                    if (itemBean.status == 4) {   //警告
                        problemNum++;
                    }
                }
            }
        }
        deviceNum = deviceId.size();
    }
}
