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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.message.adapter.BcAlarmAdapter;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.MessageBean;
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

public class CG27AlarmActivity extends BaseTitleActivity {

    public static final String FILTER = "deviceID";
    public static final String TYPE_ALL = "type_all";
    public static final String TYPE_ALARM = "type_alarm";

    private String mDeviceID;
    private String cameraID;
    private String msgType;
    private Device mDevice;


    private RecyclerView mRecyclerView;
    private SmoothLinearLayoutManager mLayoutManager;
    private EndlessRecyclerOnScrollListener mScrollListener;
    private SwipeRefreshLayout mSwipe;
    private TextView mTextNoResult;
    private View mLoadingView;

    private BcAlarmAdapter mAdapter;

    private DataApiUnit mDataApi;
    private String sodomain = "";

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
    private boolean getPickData = false;

    /**
     * 显示报警
     */
    public static void start(Context context, String deviceID, String type) {
        Intent intent = new Intent(context, CG27AlarmActivity.class);
        intent.putExtra(FILTER, deviceID);
        intent.putExtra("msgType", type);
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
        msgType = getIntent().getStringExtra("msgType");
        mDeviceID = getIntent().getStringExtra(FILTER);
        mDevice = MainApplication.getApplication().getDeviceCache().get(mDeviceID);
        mAdapter = new BcAlarmAdapter(this, mDeviceID);
        mDataApi = new DataApiUnit(this);
        if (mDevice == null) {
            return;
        }
        getCameraId(mDeviceID);

        mLayoutManager = new SmoothLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mScrollListener = new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                if (getPickData) {
                    getByDate(true);
                } else {
                    getOld();
                }

            }
        };
//        mRecyclerView.addOnScrollListener(mScrollListener);
//        DefaultItemAnimator animator = new DefaultItemAnimator();
//        animator.setSupportsChangeAnimations(true);
//        animator.setAddDuration(1000);
//        animator.setChangeDuration(1000);
//        mRecyclerView.setItemAnimator(animator);
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

        popupWindow = new DatePickerPopupWindow(CG27AlarmActivity.this);
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
        /*mSwipe.post(new Runnable() {
            @Override
            public void run() {
                mSwipe.setRefreshing(true);
                getAlarm();
            }
        });*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
        clearCount();
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
        mDataApi.deGetAlarmCount(Preference.getPreferences().getCurrentGatewayID(), mDeviceID, "3", "1", new DataApiUnit.DataApiCommonListener() {
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
        mDataApi.doGetDeviceDataHistory(
                Preference.getPreferences().getCurrentGatewayID(),
                "1",
                mDeviceID,
                null,
                "" + (mAdapter.getFirstTime() + 1),
                "" + c.getTimeInMillis(),
                new DataApiUnit.DataApiCommonListener() {
                    @Override
                    public void onSuccess(Object bean) {
                        mSwipe.setRefreshing(false);

                        MessageBean messageBean = (MessageBean) bean;
                        mAdapter.sort(messageBean.recordList);
                        mAdapter.addNew(messageBean.recordList);

                        if (mLayoutManager.findLastCompletelyVisibleItemPosition() < 10) {
                            mRecyclerView.smoothScrollToPosition(0);
                        }
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
        String startTime = showType == 0 ? "1" : ("" + c.getTimeInMillis());
        mDataApi.doGetDeviceDataHistory(
                Preference.getPreferences().getCurrentGatewayID(),
                "1",
                mDeviceID,
                /*"70".equals(mDevice.type) ? "0103012" : */null,
                startTime,
                "" + getTimeEnd(),
                new DataApiUnit.DataApiCommonListener() {
                    @Override
                    public void onSuccess(Object bean) {
                        mSwipe.setRefreshing(false);

                        MessageBean messageBean = (MessageBean) bean;
                        int length = messageBean.recordList.size();
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
//                                ObjectAnimator.ofFloat(mRecyclerView, "alpha", 0f, 1f).setDuration(700).start();

                                ObjectAnimator.ofFloat(mTextNoResult, "alpha", 1f, 0f).setDuration(700).start();
                                mTextNoResult.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mTextNoResult.setVisibility(View.INVISIBLE);
                                    }
                                }, 700);

                                List<MessageBean.RecordListBean> list = new ArrayList<>();
                                if (TextUtils.equals(msgType, TYPE_ALARM)) {
                                    for (MessageBean.RecordListBean r : messageBean.recordList) {
                                        boolean isDoorCall = TextUtils.equals("0104021", r.messageCode)
                                                || TextUtils.equals("0103061", r.messageCode);
                                        if (!isDoorCall) {
                                            list.add(r);
                                        }
                                    }
                                } else if (TextUtils.equals(msgType, TYPE_ALL)) {
                                    list = messageBean.recordList;
                                }
                                mAdapter.sort(list);
                                setEndTime(messageBean.recordList.get(messageBean.recordList.size() - 1).time);
                                mAdapter.addOld(list);
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
        progressDialogManager.showDialog(TAG, CG27AlarmActivity.this, null, null, getResources().getInteger(R.integer.http_timeout));
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
        mDataApi.doGetDeviceDataHistory(
                Preference.getPreferences().getCurrentGatewayID(),
                "1",
                mDeviceID,
                null,
                "" + 1,
                "" + new Date().getTime(),
                new DataApiUnit.DataApiCommonListener() {
                    @Override
                    public void onSuccess(Object bean) {
                        mSwipe.setRefreshing(false);

                        MessageBean messageBean = (MessageBean) bean;
                        int length = messageBean.recordList.size();
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

                            List<MessageBean.RecordListBean> list = new ArrayList<>();
                            if (TextUtils.equals(msgType, TYPE_ALARM)) {
                                for (MessageBean.RecordListBean r : messageBean.recordList) {
                                    boolean isDoorCall = TextUtils.equals("0104021", r.messageCode)
                                            || TextUtils.equals("0103061", r.messageCode);
                                    if (!isDoorCall) {
                                        list.add(r);
                                    }
                                }
                            } else if (TextUtils.equals(msgType, TYPE_ALL)) {
                                list = messageBean.recordList;
                            }
                            mAdapter.sort(list);
                            setEndTime(messageBean.recordList.get(messageBean.recordList.size() - 1).time);
                            mAdapter.setData(list);
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

    @Override
    protected void initTitle() {
        if (mDevice == null) {
            setToolBarTitleAndRightBtn(getString(R.string.Message_Center_AlarmMessage), getString(R.string.Message_Center_Emptyrecords));
        } else {
            if (TextUtils.equals(msgType, TYPE_ALL)) {
                setToolBarTitleAndRightBtn(DeviceInfoDictionary.getNameByDevice(mDevice), getString(R.string.Message_Center_Emptyrecords));
            } else {
                setToolBarTitleAndRightBtn(DeviceInfoDictionary.getNameByDevice(mDevice), "");
            }

        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.btn_right:
                if (TextUtils.equals(msgType, TYPE_ALL)) {
                    showDeleteRecordPop();
                }
                break;
            case R.id.log_linear_ctrl:
                showDatePicker();
                break;
        }
    }

    private void showDeleteRecordPop() {
        final RecordDeletePop recordDeletePop = new RecordDeletePop(this);
        recordDeletePop.setOnPopClickListener(new RecordDeletePop.onPopClickListener() {
            @Override
            public void Delete(String tag) {
                clearLog(DateUtil.getStartTime(tag), "");
            }
        });
        recordDeletePop.showAsDropDown(findViewById(R.id.btn_right), 0, 0);
        recordDeletePop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                recordDeletePop.backgroundAlpha((Activity) CG27AlarmActivity.this, 1f);
            }
        });
    }

    private WLDialog.Builder builder;
    private WLDialog dialog;

    private void clearLog(final String startTime, final String endTime) {
        builder = new WLDialog.Builder(this);
        builder.setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setMessage(getString(R.string.Message_Center_EmptyConfirm))
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        new DataApiUnit(CG27AlarmActivity.this).deDeleteAlarm(mDeviceID,
                                DeviceInfoDictionary.isWiFiDevice("Bc")
                                        ? mDeviceID
                                        : Preference.getPreferences().getCurrentGatewayID(), startTime, endTime,
                                new DataApiUnit.DataApiCommonListener() {
                                    @Override
                                    public void onSuccess(Object bean) {
                                        if (TextUtils.isEmpty(startTime)) {
                                            finish();
                                        } else {
                                            mAdapter.clear();
                                            getAlarm(true);
                                        }
                                    }

                                    @Override
                                    public void onFail(int code, String msg) {
                                        ToastUtil.single(msg);
                                    }
                                });
                        dialog.dismiss();
                    }

                    @Override
                    public void onClickNegative(View view) {
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    private void getCameraId(String deviceId) {
        showLoading();
        new DataApiUnit(CG27AlarmActivity.this).doGetBcCameraId(deviceId, mDevice.type, new DataApiUnit.DataApiCommonListener<String>() {
            @Override
            public void onSuccess(String bean) {
                if (TextUtils.isEmpty(bean)) {
                    cameraID = null;
                } else {
                    cameraID = bean;
                }
                mAdapter.setCameraId(cameraID);
                getAlarm(true);
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.single(msg);
            }
        });
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

        getByDate(false);
    }

    private void chooseErrorDay() {
        // 日期选择错误，显示一个动画来提醒用户
        ObjectAnimator.ofFloat(mLinearCtrl, "rotation", 0, 20, 0, -15, 0, 10, 0, -5, 0).setDuration(700).start();
    }

    private void getByDate(boolean isLastTime) {
        showLoadingMore();
        String gwId = Preference.getPreferences().getCurrentGatewayID();
        Calendar c = Calendar.getInstance();
        c.set(showYear, showMonth, showDay, 0, 0, 0);
        String startTime = "" + c.getTimeInMillis();
        c.set(showYear, showMonth, showDay + 1, 0, 0, 0);
        String endTime = null;
        if (isLastTime) {
            endTime = getTimeEnd() + "";
        } else {
            endTime = "" + c.getTimeInMillis();
        }

        if (!getPickData) {
            mAdapter.clear();
        }
        getPickData = true;
        mDataApi.doGetDeviceDataHistory(
                gwId,
                "1",
                mDeviceID,
                null,
                startTime,
                endTime,
                new DataApiUnit.DataApiCommonListener() {
                    @Override
                    public void onSuccess(Object bean) {
                        mSwipe.setRefreshing(false);

                        MessageBean messageBean = (MessageBean) bean;
                        int length = messageBean.recordList.size();
                        List<MessageBean.RecordListBean> list = new ArrayList<>();
                        if (TextUtils.equals(msgType, TYPE_ALARM)) {
                            for (MessageBean.RecordListBean r : messageBean.recordList) {
                                boolean isDoorCall = TextUtils.equals("0104021", r.messageCode)
                                        || TextUtils.equals("0103061", r.messageCode);
                                if (!isDoorCall) {
                                    list.add(r);
                                }
                            }
                        } else if (TextUtils.equals(msgType, TYPE_ALL)) {
                            list = messageBean.recordList;
                        }
                        if (list.size() == 0) {
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

                            mAdapter.sort(list);
                            setEndTime(messageBean.recordList.get(messageBean.recordList.size() - 1).time);
                            mAdapter.setData(list);
//                            mRecyclerView.smoothScrollToPosition(mAdapter.lastPosition());
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
