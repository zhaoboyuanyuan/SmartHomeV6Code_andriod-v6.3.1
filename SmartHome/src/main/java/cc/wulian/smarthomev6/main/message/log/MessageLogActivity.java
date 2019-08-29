package cc.wulian.smarthomev6.main.message.log;

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
import cc.wulian.smarthomev6.main.message.adapter.MessageLogAdapter;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.MessageBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.customview.RecordDeletePop;
import cc.wulian.smarthomev6.support.customview.SmoothLinearLayoutManager;
import cc.wulian.smarthomev6.support.customview.SwipeRefreshLayout;
import cc.wulian.smarthomev6.support.customview.WLDatePicker;
import cc.wulian.smarthomev6.support.customview.popupwindow.DatePickerPopupWindow;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.EndlessRecyclerOnScrollListener;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.DateUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

public class MessageLogActivity extends BaseTitleActivity {

    public static final String FILTER = "deviceID";

    private String mDeviceID, mType, mDeviceName;
    private Device mDevice;

    private LinearLayout mLinearCtrl;
    private ImageView mImageArrow;
    private RecyclerView mRecyclerView;
    private SmoothLinearLayoutManager mLayoutManager;
    private EndlessRecyclerOnScrollListener mScrollListener;
    private SwipeRefreshLayout mSwipe;
    private TextView mTextNoResult;
    private View mLoadingView;

    private WLDialog.Builder builder;
    private WLDialog dialog;

    private DatePickerPopupWindow popupWindow;

    private int showYear, showMonth, showDay;                    // 月份需要 +1
    private int currentYear, currentMonth, currentDay;           // 月份需要 +1
    /**
     * 查看类型
     * 0 默认, 1 按日期查找
     */
    private int showType = 0;

    private MessageLogAdapter mAdapter;

    private DataApiUnit mDataApi;

    /**
     * 显示日志
     */
    public static void start(Context context, String deviceID, String type) {
        Intent intent = new Intent(context, MessageLogActivity.class);
        intent.putExtra(FILTER, deviceID);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    /**
     * 显示日志
     */
    public static void start(Context context, String deviceID, String type, String deviceName) {
        Intent intent = new Intent(context, MessageLogActivity.class);
        intent.putExtra(FILTER, deviceID);
        intent.putExtra("type", type);
        intent.putExtra("deviceName", deviceName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_log2, true);

        EventBus.getDefault().register(this);

        mSwipe = (SwipeRefreshLayout) findViewById(R.id.log_swipe);
        mRecyclerView = (RecyclerView) findViewById(R.id.log_recycler);
        mTextNoResult = (TextView) findViewById(R.id.log_text_no_result);
        mLinearCtrl = (LinearLayout) findViewById(R.id.log_linear_ctrl);
        mImageArrow = (ImageView) findViewById(R.id.log_image_arrow);
        mLoadingView = findViewById(R.id.loading_view);

        mAdapter = new MessageLogAdapter();
        mDataApi = new DataApiUnit(this);

        mLayoutManager = new SmoothLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mScrollListener = new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                getOld();
            }
        };

//        DefaultItemAnimator animator = new DefaultItemAnimator();
//        animator.setSupportsChangeAnimations(true);
//        animator.setAddDuration(300);
//        animator.setChangeDuration(300);
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

        popupWindow = new DatePickerPopupWindow(MessageLogActivity.this);
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
        getAlarm(true);
    }

    @Override
    protected void initTitle() {
        mDeviceID = getIntent().getStringExtra(FILTER);
        mType = getIntent().getStringExtra("type");
        mDeviceName = getIntent().getStringExtra("deviceName");
        mDevice = MainApplication.getApplication().getDeviceCache().get(mDeviceID);

        if (!TextUtils.isEmpty(mDeviceName)) {
            setToolBarTitleAndRightBtn(mDeviceName, getString(R.string.Message_Center_Emptyrecords));
        } else {
            if (mDevice == null) {
//            setToolBarTitle(R.string.Message_Center_Log);
                setToolBarTitleAndRightBtn(R.string.Message_Center_Log, R.string.Message_Center_Emptyrecords);
            } else {
//            setToolBarTitle(DeviceInfoDictionary.getNameByDevice(mDevice));
                setToolBarTitleAndRightBtn(DeviceInfoDictionary.getNameByDevice(mDevice), getString(R.string.Message_Center_Emptyrecords));
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.btn_right:
                showDeleteRecordPop();
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
                if (!TextUtils.isEmpty(event.device.messageCode) && event.device.messageCode.endsWith("2")) {
                    getNew();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceInfoChangedEvent event) {
        if (showType != 0) {
            return;
        }
        if (event.deviceInfoBean != null && mDevice != null) {
            if (TextUtils.equals(event.deviceInfoBean.devID, mDevice.devID)) {
                if (!TextUtils.isEmpty(event.deviceInfoBean.messageCode) && event.deviceInfoBean.messageCode.endsWith("2")) {
                    getNew();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
        clearCount();
    }

    private void clearCount() {
        String gwId = Preference.getPreferences().getCurrentGatewayID();
        if (DeviceInfoDictionary.isWiFiDevice(mType)) {
            gwId = "";
        }
        mDataApi.deGetAlarmCount(gwId, mDeviceID, "3", "2", new DataApiUnit.DataApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                MainApplication.getApplication().getAlarmCountCache().clearLogCloudCount(mDeviceID);
            }

            @Override
            public void onFail(int code, String msg) {
                WLog.i(TAG, "clearCount: fail " + msg);
            }
        });
    }

    private void showDeleteRecordPop() {
        final RecordDeletePop recordDeletePop = new RecordDeletePop(this);
        recordDeletePop.setOnPopClickListener(new RecordDeletePop.onPopClickListener() {
            @Override
            public void Delete(String tag) {
                String startTime = DateUtil.getStartTime(tag);
                clearLog(startTime, "");

            }
        });
        recordDeletePop.showAsDropDown(findViewById(R.id.btn_right), 0, 0);
        recordDeletePop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                recordDeletePop.backgroundAlpha((Activity) MessageLogActivity.this, 1f);
            }
        });
    }

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
                        new DataApiUnit(MessageLogActivity.this).deDeleteLog(mDeviceID,
                                DeviceInfoDictionary.isWiFiDevice(mType)
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

    private void getNew() {
        if (mAdapter.isEmpty()) {
            getAlarm(false);
            return;
        }

        String gwId = Preference.getPreferences().getCurrentGatewayID();
        if (DeviceInfoDictionary.isWiFiDevice(mType)) {
            gwId = "";
        }
        Calendar c = Calendar.getInstance();
        c.set(currentYear, currentMonth, currentDay, 0, 0, 0);
        c.add(Calendar.DAY_OF_MONTH, 1);
        mDataApi.doGetDeviceDataHistory(
                gwId,
                "2",
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

    private void getOld() {
        showLoadingMore();
        String gwId = Preference.getPreferences().getCurrentGatewayID();
        if (DeviceInfoDictionary.isWiFiDevice(mType)) {
            gwId = "";
        }
        Calendar c = Calendar.getInstance();
        c.set(showYear, showMonth, showDay, 0, 0, 0);
        // 如果是按日期查看
        // 开始时间为当天零点
        // 上拉的话, 也只能查看今天的
        String startTime = showType == 0 ? "1" : ("" + c.getTimeInMillis());
        mDataApi.doGetDeviceDataHistory(
                gwId,
                "2",
                mDeviceID,
                null,
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

//                                ObjectAnimator.ofFloat(mTextNoResult, "alpha", 1f, 0f).setDuration(700).start();
//                                mTextNoResult.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        mTextNoResult.setVisibility(View.INVISIBLE);
//                                    }
//                                }, 700);

                                mAdapter.sort(messageBean.recordList);
                                setEndTime(messageBean.recordList.get(messageBean.recordList.size() - 1).time);
                                mAdapter.addOld(messageBean.recordList);
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
        progressDialogManager.showDialog(TAG, MessageLogActivity.this, null, null, getResources().getInteger(R.integer.http_timeout));
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
        String gwId = Preference.getPreferences().getCurrentGatewayID();
        if (DeviceInfoDictionary.isWiFiDevice(mType)) {
            gwId = "";
        }
        Calendar c = Calendar.getInstance();
        c.set(currentYear, currentMonth, currentDay, 0, 0, 0);
        c.add(Calendar.DAY_OF_MONTH, 1);
        mDataApi.doGetDeviceDataHistory(
                gwId,
                "2",
                mDeviceID,
                null,
                "" + 1,
                "" + c.getTimeInMillis(),
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

                            mAdapter.sort(messageBean.recordList);
                            setEndTime(messageBean.recordList.get(messageBean.recordList.size() - 1).time);
                            mAdapter.setData(messageBean.recordList);
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
        String startTime = "" + c.getTimeInMillis();
        c.set(showYear, showMonth, showDay + 1, 0, 0, 0);
        String endTime = "" + c.getTimeInMillis();
        mDataApi.doGetDeviceDataHistory(
                gwId,
                "2",
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

                            mAdapter.sort(messageBean.recordList);
                            setEndTime(messageBean.recordList.get(messageBean.recordList.size() - 1).time);
                            mAdapter.setData(messageBean.recordList);
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
