package cc.wulian.smarthomev6.main.message.alarm;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.Date;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.message.adapter.LcAlarmAdapter;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.LcAlarmBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.LcDeviceInfoBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.customview.RecordDeletePop;
import cc.wulian.smarthomev6.support.customview.SmoothLinearLayoutManager;
import cc.wulian.smarthomev6.support.customview.SwipeRefreshLayout;
import cc.wulian.smarthomev6.support.customview.WLDatePicker;
import cc.wulian.smarthomev6.support.customview.popupwindow.DatePickerPopupWindow;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.EndlessRecyclerOnScrollListener;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.DateUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

public class LcCameraAlarmActivity extends BaseTitleActivity {

    public static final String FILTER = "deviceID";

    private String mDeviceID;
    private Device mDevice;
    private String mType;
    private String channelId;
    private String nextAlarmId = "-1";
    private String pickDayNextAlarmId = "-1";
    private String startTime;
    private String endTime;

    private RecyclerView mRecyclerView;
    private SmoothLinearLayoutManager mLayoutManager;
    private EndlessRecyclerOnScrollListener mScrollListener;
    private SwipeRefreshLayout mSwipe;
    private TextView mTextNoResult;
    private View mLoadingView;

    private WLDialog.Builder builder;
    private WLDialog dialog;

    private LcAlarmAdapter mAdapter;

    private DataApiUnit mDataApi;

    private DatePickerPopupWindow popupWindow;

    private int showYear, showMonth, showDay;                    // 月份需要 +1
    private int currentYear, currentMonth, currentDay;           // 月份需要 +1
    /**
     * 查看类型
     * 0 默认, 1 按日期查找
     */
    private int showType = 0;
    private LinearLayout mLinearCtrl;
    private ImageView mImageArrow;

    public static void start(Context context, String deviceID) {
        Intent intent = new Intent(context, LcCameraAlarmActivity.class);
        intent.putExtra(FILTER, deviceID);
        context.startActivity(intent);
    }

    public static void start(Context context, String deviceID, String channelId) {
        Intent intent = new Intent(context, LcCameraAlarmActivity.class);
        intent.putExtra(FILTER, deviceID);
        intent.putExtra("channelId", channelId);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_alarm, true);

        EventBus.getDefault().register(this);

        mSwipe = (SwipeRefreshLayout) findViewById(R.id.alarm_swipe);
        mRecyclerView = (RecyclerView) findViewById(R.id.alarm_recycler);
        mTextNoResult = (TextView) findViewById(R.id.alarm_text_no_result);
        mLinearCtrl = (LinearLayout) findViewById(R.id.log_linear_ctrl);
        mImageArrow = (ImageView) findViewById(R.id.log_image_arrow);
        mLoadingView = findViewById(R.id.loading_view);

        mAdapter = new LcAlarmAdapter(LcCameraAlarmActivity.this, mDeviceID);

        mLayoutManager = new SmoothLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mScrollListener = new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                getOld();
            }
        };
        mRecyclerView.setAdapter(mAdapter);

        mSwipe.setEnabled(false);
        mSwipe.setColorSchemeColors(getResources().getColor(R.color.newPrimary));
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOld();
            }
        });

        Calendar calendar = Calendar.getInstance();
        showYear = calendar.get(Calendar.YEAR);
        currentYear = calendar.get(Calendar.YEAR);
        showMonth = calendar.get(Calendar.MONTH);
        currentMonth = calendar.get(Calendar.MONTH);
        showDay = calendar.get(Calendar.DAY_OF_MONTH);
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        popupWindow = new DatePickerPopupWindow(LcCameraAlarmActivity.this);
        popupWindow.setDatePickListener(new WLDatePicker.DatePickListener() {
            @Override
            public void datePicked(int year, int month, int day) {
                if (DateUtil.isFutureDay(year, month, day)) {
                    return;
                }
                pickDate(year, month, day);
                popupWindow.dismiss();
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // pup消失时 图标的动画
                ObjectAnimator.ofFloat(mImageArrow, "rotationX", 180f, 0f).setDuration(700).start();
            }
        });
        mLinearCtrl.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mDeviceID = getIntent().getStringExtra(FILTER);
        channelId = getIntent().getStringExtra("channelId");
        mDevice = MainApplication.getApplication().getDeviceCache().get(mDeviceID);
        mType = mDevice.type;
        mDataApi = new DataApiUnit(this);
        if (mDevice == null) {
            setToolBarTitle(getString(R.string.Message_Center_AlarmMessage));
        } else {
            setToolBarTitle(DeviceInfoDictionary.getNameByDevice(mDevice));
        }
        if (TextUtils.isEmpty(channelId)) {
            getLcDeviceInfo();
        } else {
            getAlarm(true);
        }
    }

    //获取摄像机详细信息
    private void getLcDeviceInfo() {
        new DeviceApiUnit(this).getLcDeviceInfo(mDeviceID, new DeviceApiUnit.DeviceApiCommonListener<LcDeviceInfoBean>() {
            @Override
            public void onSuccess(LcDeviceInfoBean bean) {
                channelId = String.valueOf(bean.getExtData().getChannels().get(0).getChannelId());
                getAlarm(true);
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(msg);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
        clearCount();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.btn_right:
                break;
            case R.id.log_linear_ctrl:
                showDatePicker();
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (showType != 0) {
            return;
        }
        if (event.device != null && mDevice != null) {
            if (TextUtils.equals(event.device.devID, mDevice.devID)) {
                if (!TextUtils.isEmpty(event.device.messageCode) && event.device.messageCode.endsWith("1")) {
                    getNew();
                }
            }
        }
    }

    private void clearCount() {
        mDataApi.deGetAlarmCount(mDeviceID, mDeviceID, "3", "1", new DataApiUnit.DataApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                MainApplication.getApplication().getAlarmCountCache().clearAlarmCloudCount(mDeviceID);
            }

            @Override
            public void onFail(int code, String msg) {
                WLog.i(TAG, "clearCount: fail " + msg);
            }
        });
    }


    /**
     * 是否有新的数据
     */
    private boolean hasNew = false;
    /**
     * 是否正在请求新的数据
     */
    private boolean isGettingNew = false;

    private void getNew() {
        synchronized (this) {
            // 如果正在请求新数据
            // 就返回
            // 同时标记有新的请求
            if (isGettingNew) {
                WLog.i(TAG, "getNew: 有新的请求");
                hasNew = true;
                return;
            }
            // 标记正在请求
            isGettingNew = true;
        }

        if (mAdapter.isEmpty()) {
            getAlarm(false);
            return;
        }

        Calendar c = Calendar.getInstance();
        c.set(currentYear, currentMonth, currentDay, 0, 0, 0);
        c.add(Calendar.DAY_OF_MONTH, 1);
        if (showType == 0) {
            startTime = "946656000000";
            endTime = new Date().getTime() + "";
        } else {
            nextAlarmId = pickDayNextAlarmId;
        }
        mDataApi.doGetLcAlarmList(
                Preference.getPreferences().getCurrentGatewayID(),
                "1",
                mDeviceID,
                null,
                startTime,
                endTime, nextAlarmId, channelId, 10,
                new DataApiUnit.DataApiCommonListener() {
                    @Override
                    public void onSuccess(Object bean) {
                        mSwipe.setRefreshing(false);

                        LcAlarmBean messageBean = (LcAlarmBean) bean;
                        if (showType == 0) {
                            nextAlarmId = String.valueOf(messageBean.getNextAlarmId());
                        } else {
                            pickDayNextAlarmId = String.valueOf(messageBean.getNextAlarmId());
                        }
                        mAdapter.sort(messageBean.getRecordList());
                        mAdapter.addNew(messageBean.getRecordList());

//                        if (mLayoutManager.findLastCompletelyVisibleItemPosition() < 10) {
                        mRecyclerView.smoothScrollToPosition(0);
//                        }
                        hideLoading();

                        synchronized (this) {
                            // 请求完成
                            isGettingNew = false;
                            // 如果有新的请求
                            // 再次调用getNew()
                            // 同事去掉新的请求的标记
                            if (hasNew) {
                                WLog.i(TAG, "onSuccess: 请求新的");
                                hasNew = false;
                                getNew();
                            }
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        ToastUtil.single(msg);
                        mSwipe.setRefreshing(false);
                        hideLoading();

                        synchronized (this) {
                            // 请求完成
                            isGettingNew = false;
                        }
                    }
                }
        );
    }

    private void getOld() {
        showLoadingMore();
        Calendar c = Calendar.getInstance();
        c.set(showYear, showMonth, showDay, 0, 0, 0);
        if (showType == 0) {
            startTime = "946656000000";
            endTime = new Date().getTime() + "";
        } else {
            nextAlarmId = pickDayNextAlarmId;
        }
        mDataApi.doGetLcAlarmList(
                Preference.getPreferences().getCurrentGatewayID(),
                "1",
                mDeviceID,
                null,
                startTime,
                endTime, nextAlarmId, channelId, 10,
                new DataApiUnit.DataApiCommonListener() {
                    @Override
                    public void onSuccess(Object bean) {
                        mSwipe.setRefreshing(false);

                        LcAlarmBean messageBean = (LcAlarmBean) bean;
                        if (showType == 0) {
                            nextAlarmId = String.valueOf(messageBean.getNextAlarmId());
                        } else {
                            pickDayNextAlarmId = String.valueOf(messageBean.getNextAlarmId());
                        }
                        int length = messageBean.getRecordList().size();
                        if (mAdapter.isEmpty()) {
                            if (length == 0) {
                                mTextNoResult.setVisibility(View.VISIBLE);
                                ObjectAnimator.ofFloat(mTextNoResult, "alpha", 0f, 1f).setDuration(700).start();

                                ObjectAnimator.ofFloat(mRecyclerView, "alpha", 1f, 0f).setDuration(700).start();
                                mRecyclerView.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRecyclerView.setVisibility(View.INVISIBLE);
                                    }
                                }, 700);
                            }
                        } else {
                            if (length > 0) {
                                mRecyclerView.setVisibility(View.VISIBLE);

                                ObjectAnimator.ofFloat(mTextNoResult, "alpha", 1f, 0f).setDuration(700).start();

                                mAdapter.sort(messageBean.getRecordList());
                                setEndTime(messageBean.getRecordList().get(messageBean.getRecordList().size() - 1).getTime());
                                mAdapter.addOld(messageBean.getRecordList());
                                // 滚动到旧数据的最后一条
//                                mRecyclerView.smoothScrollToPosition(mAdapter.lastPosition());
                            }
                        }
                        hideLoading();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        ToastUtil.single(msg);
                        mSwipe.setRefreshing(false);
                        hideLoading();
                    }
                }
        );
    }

    private long loadingTime = 0;

    private void showLoading() {
        loadingTime = System.currentTimeMillis();
        progressDialogManager.showDialog(TAG, LcCameraAlarmActivity.this, null, null, getResources().getInteger(R.integer.http_timeout));
    }

    private void showLoadingMore() {
        findViewById(R.id.loading_container).setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);
        mLoadingView.startAnimation(animation);
        mRecyclerView.scrollToPosition(mAdapter.lastPosition());
    }

    private void hideLoading() {
        long delta = (System.currentTimeMillis() - loadingTime) / 1000;
        if (delta > 700) {
            progressDialogManager.dimissDialog(TAG, 0);
        } else {
            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialogManager.dimissDialog(TAG, 0);
                }
            }, 700 - delta);
        }

        findViewById(R.id.loading_container).setVisibility(View.GONE);
        mLoadingView.clearAnimation();
    }

    private void getAlarm(boolean showLoading) {
        if (showLoading) {
            showLoading();
        }
        if (showType == 0) {
            startTime = "946656000000";
            endTime = new Date().getTime() + "";
        } else {
            nextAlarmId = pickDayNextAlarmId;
        }
        mDataApi.doGetLcAlarmList(
                Preference.getPreferences().getCurrentGatewayID(),
                "1",
                mDeviceID,
                null,
                startTime,
                endTime, nextAlarmId, channelId, 10,
                new DataApiUnit.DataApiCommonListener() {
                    @Override
                    public void onSuccess(Object bean) {
                        mSwipe.setRefreshing(false);
                        LcAlarmBean messageBean = (LcAlarmBean) bean;
                        if (showType == 0) {
                            nextAlarmId = String.valueOf(messageBean.getNextAlarmId());
                        } else {
                            pickDayNextAlarmId = String.valueOf(messageBean.getNextAlarmId());
                        }
                        int length = messageBean.getRecordList().size();
                        if (length == 0) {
                            mTextNoResult.setVisibility(View.VISIBLE);
                            ObjectAnimator.ofFloat(mTextNoResult, "alpha", 0f, 1f).setDuration(700).start();

                            ObjectAnimator.ofFloat(mRecyclerView, "alpha", 1f, 0f).setDuration(700).start();
                            mRecyclerView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mRecyclerView.setVisibility(View.INVISIBLE);
                                }
                            }, 700);
                        } else {
                            mRecyclerView.setVisibility(View.VISIBLE);
                            ObjectAnimator.ofFloat(mRecyclerView, "alpha", 0f, 1f).setDuration(700).start();

                            ObjectAnimator.ofFloat(mTextNoResult, "alpha", 1f, 0f).setDuration(700).start();
                            mTextNoResult.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mTextNoResult.setVisibility(View.INVISIBLE);
                                }
                            }, 700);

                            mAdapter.sort(messageBean.getRecordList());
                            setEndTime(messageBean.getRecordList().get(messageBean.getRecordList().size() - 1).getTime());
                            mAdapter.setData(messageBean.getRecordList());
//                            mRecyclerView.smoothScrollToPosition(mAdapter.lastPosition());
                        }

                        hideLoading();
                        if (length >= 10) {
                            mRecyclerView.addOnScrollListener(mScrollListener);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        ToastUtil.single(msg);
                        mSwipe.setRefreshing(false);
                        hideLoading();
                    }
                }
        );
    }

    private long timeEnd = -1;

    private void setEndTime(long end) {
        timeEnd = end;
    }

    private long getTimeEnd() {
        if (timeEnd == -1) {
            return new Date().getTime();
        } else {
            return timeEnd;
        }
    }

    /**********************************************************************
     *                              添加日历修改
     *                              2017年12月18日10:34:14
     **********************************************************************/
    private void showDatePicker() {
        popupWindow.showParent(mLinearCtrl, showYear, showMonth, showDay);
        // 动画
        ObjectAnimator.ofFloat(mImageArrow, "rotationX", 0f, 180f).setDuration(700).start();
    }

    private void pickDate(int year, int month, int day) {
        showYear = year;
        showMonth = month;
        showDay = day;
        showType = 1;
        pickDayNextAlarmId = "-1";

        getByDate(year, month, day);
    }

    private void chooseErrorDay() {
        // 日期选择错误，显示一个动画来提醒用户
        ObjectAnimator.ofFloat(mLinearCtrl, "rotation", 0, 20, 0, -15, 0, 10, 0, -5, 0).setDuration(700).start();
    }

    private void getByDate(int y, int m, int d) {
        showLoading();
        String gwId = Preference.getPreferences().getCurrentGatewayID();
        if (DeviceInfoDictionary.isWiFiDevice(mType)) {
            gwId = "";
        }
        Calendar c = Calendar.getInstance();
        c.set(showYear, showMonth, showDay, 0, 0, 0);
        startTime = "" + c.getTimeInMillis();
        c.set(showYear, showMonth, showDay + 1, 0, 0, 0);
        endTime = "" + c.getTimeInMillis();
        mDataApi.doGetLcAlarmList(
                gwId,
                "1",
                mDeviceID,
                null,
                startTime,
                endTime, pickDayNextAlarmId, channelId, 10,
                new DataApiUnit.DataApiCommonListener() {
                    @Override
                    public void onSuccess(Object bean) {
                        mSwipe.setRefreshing(false);

                        LcAlarmBean messageBean = (LcAlarmBean) bean;
                        if (showType == 0) {
                            nextAlarmId = String.valueOf(messageBean.getNextAlarmId());
                        } else {
                            pickDayNextAlarmId = String.valueOf(messageBean.getNextAlarmId());
                        }
                        int length = messageBean.getRecordList().size();
                        if (length == 0) {
                            mTextNoResult.setVisibility(View.VISIBLE);
                            ObjectAnimator.ofFloat(mTextNoResult, "alpha", 0f, 1f).setDuration(700).start();

                            ObjectAnimator.ofFloat(mRecyclerView, "alpha", 1f, 0f).setDuration(700).start();
                            mRecyclerView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mRecyclerView.setVisibility(View.INVISIBLE);
                                }
                            }, 700);
                        } else {
                            mRecyclerView.setVisibility(View.VISIBLE);
                            ObjectAnimator.ofFloat(mRecyclerView, "alpha", 0f, 1f).setDuration(700).start();

                            ObjectAnimator.ofFloat(mTextNoResult, "alpha", 1f, 0f).setDuration(700).start();
                            mTextNoResult.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mTextNoResult.setVisibility(View.INVISIBLE);
                                }
                            }, 700);

                            mAdapter.sort(messageBean.getRecordList());
                            setEndTime(messageBean.getRecordList().get(messageBean.getRecordList().size() - 1).getTime());
                            mAdapter.setData(messageBean.getRecordList());
                            mRecyclerView.smoothScrollToPosition(0);
                        }
                        hideLoading();
                        if (length >= 10) {
                            mRecyclerView.addOnScrollListener(mScrollListener);
                        } else {
                            mRecyclerView.removeOnScrollListener(mScrollListener);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        ToastUtil.single(msg);
                        mSwipe.setRefreshing(false);
                        hideLoading();
                    }
                }
        );
    }
}
