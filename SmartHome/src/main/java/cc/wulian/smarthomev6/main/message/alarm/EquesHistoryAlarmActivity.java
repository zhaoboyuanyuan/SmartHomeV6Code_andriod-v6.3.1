package cc.wulian.smarthomev6.main.message.alarm;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import cc.wulian.smarthomev6.support.customview.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
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

import com.lzy.okgo.OkGo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesAlarmBean;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesAlarmDetailBean;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesVisitorBean;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesVisitorDetailBean;
import cc.wulian.smarthomev6.main.message.adapter.EquesAlarmAdapter;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.EncryptCallBack;
import cc.wulian.smarthomev6.support.core.apiunit.EquesApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.MessageBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.MessageCountBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.ResponseBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.customview.SmoothLinearLayoutManager;
import cc.wulian.smarthomev6.support.customview.WLDatePicker;
import cc.wulian.smarthomev6.support.customview.popupwindow.DatePickerPopupWindow;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.EndlessRecyclerOnScrollListener;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.DateUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by 上海滩小马哥 on 2017/9/7.
 */

public class EquesHistoryAlarmActivity extends BaseTitleActivity {
    public static final String FILTER = "deviceID";

    private String mDeviceID;
    private Device mDevice;

    private RecyclerView mRecyclerView;
    private SmoothLinearLayoutManager mLayoutManager;
    private EndlessRecyclerOnScrollListener mScrollListener;
    private SwipeRefreshLayout mSwipe;
    private TextView mTextNoResult;
    private View mLoadingView;

    private EquesAlarmAdapter mAdapter;
    private List<EquesAlarmDetailBean> details = new ArrayList<>();
    private boolean isAlarmArrival = false;
    private boolean isRingArrival = false;

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

    /**
     * 显示报警
     */
    public static void start(Context context, String deviceID) {
        Intent intent = new Intent(context, EquesHistoryAlarmActivity.class);
        intent.putExtra(FILTER, deviceID);
        intent.putExtra("type", "CMICY1");
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_eques_alarml, true);

        mSwipe = (SwipeRefreshLayout) findViewById(R.id.alarm_swipe);
        mRecyclerView = (RecyclerView) findViewById(R.id.alarm_recycler);
        mTextNoResult = (TextView) findViewById(R.id.alarm_text_no_result);
        mLinearCtrl = (LinearLayout) findViewById(R.id.log_linear_ctrl);
        mImageArrow = (ImageView) findViewById(R.id.log_image_arrow);
        mLoadingView = findViewById(R.id.loading_view);

        mAdapter = new EquesAlarmAdapter(this, mDeviceID);

        mLayoutManager = new SmoothLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mScrollListener = new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                getOld();
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

        popupWindow = new DatePickerPopupWindow(EquesHistoryAlarmActivity.this);
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
        getAlarm();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        try {
            clearCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearCount() {
        new DataApiUnit(this).deGetAlarmCount(mDeviceID, mDeviceID, "3", "1", new DataApiUnit.DataApiCommonListener() {
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

    private void getOld() {
        showLoadingMore();
        Calendar c = Calendar.getInstance();
        c.set(showYear, showMonth, showDay, 0, 0, 0);
        String startTime = showType == 0 ? "1" : ("" + c.getTimeInMillis());
        details.clear();
        MainApplication.getApplication().getEquesApiUnit().getIcvss().equesGetAlarmMessageList(mDeviceID, Long.parseLong(startTime),
                getAlarmTimeEnd(), 10);
        MainApplication.getApplication().getEquesApiUnit().getIcvss().equesGetRingRecordList(mDeviceID, Long.parseLong(startTime), getRingTimeEnd(), 10);
    }

    private long loadingTime = 0;
    private void showLoading() {
        loadingTime = System.currentTimeMillis();
        progressDialogManager.showDialog(TAG, EquesHistoryAlarmActivity.this, null, null, getResources().getInteger(R.integer.http_timeout));
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

    private void getAlarm() {
        showLoading();
        details.clear();
        MainApplication.getApplication().getEquesApiUnit().getIcvss().equesGetAlarmMessageList(mDeviceID, 0,
                new Date().getTime(), 10);
        MainApplication.getApplication().getEquesApiUnit().getIcvss().equesGetRingRecordList(mDeviceID, 0, new Date().getTime(), 10);
    }

    private long alarmTimeEnd = -1;
    private long ringTimeEnd = -1;

    private void setEndTime(long alarmTimeEnd, long ringTimeEnd) {
        if (alarmTimeEnd != -1){
            this.alarmTimeEnd = alarmTimeEnd - 1;
        }
        if (ringTimeEnd != -1){
            this.ringTimeEnd = ringTimeEnd - 1;
        }
    }

    private long getAlarmTimeEnd() {
        if (alarmTimeEnd == -1) {
            return new Date().getTime();
        } else {
            return alarmTimeEnd;
        }
    }

    private long getRingTimeEnd() {
        if (ringTimeEnd == -1) {
            return new Date().getTime();
        } else {
            return ringTimeEnd;
        }
    }

    private boolean isFirstArrival = true;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEquesAlarmResult(EquesAlarmBean event) {
        if (event.alarms != null && !event.alarms.isEmpty()){
            mAdapter.sort(event.alarms);
            setEndTime(event.alarms.get(event.alarms.size()-1).create, -1);
            details.addAll(event.alarms);
        }
        if (isFirstArrival && event.max >= 10) {
            mRecyclerView.addOnScrollListener(mScrollListener);
        }
        isAlarmArrival = true;
        if (isAlarmArrival && isRingArrival){
            isAlarmArrival = false;
            isRingArrival = false;
            hideLoading();
            if (isFirstArrival){
                isFirstArrival = false;
                if (details.isEmpty() || details.size() == 0){
                    mTextNoResult.setVisibility(View.VISIBLE);
                    ObjectAnimator.ofFloat(mTextNoResult, "alpha", 0f, 1f).setDuration(700).start();

                    ObjectAnimator.ofFloat(mRecyclerView, "alpha", 1f, 0f).setDuration(700).start();
                    mRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.setVisibility(View.INVISIBLE);
                        }
                    }, 700);
                }else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    ObjectAnimator.ofFloat(mRecyclerView, "alpha", 0f, 1f).setDuration(700).start();

                    ObjectAnimator.ofFloat(mTextNoResult, "alpha", 1f, 0f).setDuration(700).start();
                    mTextNoResult.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mTextNoResult.setVisibility(View.INVISIBLE);
                        }
                    }, 700);
                }
                mAdapter.sort(details);
                mAdapter.setData(event.alarms);
                hideLoading();
            }else {
                if (event.max == 0){

                }else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    ObjectAnimator.ofFloat(mRecyclerView, "alpha", 0f, 1f).setDuration(700).start();

                    ObjectAnimator.ofFloat(mTextNoResult, "alpha", 1f, 0f).setDuration(700).start();
                    mTextNoResult.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mTextNoResult.setVisibility(View.INVISIBLE);
                        }
                    }, 700);

                    // 滚动到旧数据的最后一条
//                                mRecyclerView.smoothScrollToPosition(mAdapter.lastPosition());
                }
                mAdapter.sort(details);
                mAdapter.addOld(event.alarms);
                hideLoading();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEquesRingResult(EquesVisitorBean event) {
        if (event.rings != null && !event.rings.isEmpty()){
            sort(event.rings);
            setEndTime(-1, event.rings.get(event.rings.size()-1).ringtime);
            for (EquesVisitorDetailBean bean:event.rings) {
                EquesAlarmDetailBean temp = new EquesAlarmDetailBean();
                temp.recordType = 1;
                temp.time = bean.ringtime;
                temp.bid = bean.bid;
                temp.fid = new ArrayList<>();
                temp.fid.add(bean.fid);
                details.add(temp);
            }
        }
        if (event.rings.size() >= 10) {
            mRecyclerView.addOnScrollListener(mScrollListener);
        }
        isRingArrival = true;
        if (isAlarmArrival && isRingArrival) {
            isAlarmArrival = false;
            isRingArrival = false;
            hideLoading();
            if (isFirstArrival){
                isFirstArrival = false;
                if (details.isEmpty() || details.size() == 0){
                    mTextNoResult.setVisibility(View.VISIBLE);
                    ObjectAnimator.ofFloat(mTextNoResult, "alpha", 0f, 1f).setDuration(700).start();

                    ObjectAnimator.ofFloat(mRecyclerView, "alpha", 1f, 0f).setDuration(700).start();
                    mRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.setVisibility(View.INVISIBLE);
                        }
                    }, 700);
                }else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    ObjectAnimator.ofFloat(mRecyclerView, "alpha", 0f, 1f).setDuration(700).start();

                    ObjectAnimator.ofFloat(mTextNoResult, "alpha", 1f, 0f).setDuration(700).start();
                    mTextNoResult.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mTextNoResult.setVisibility(View.INVISIBLE);
                        }
                    }, 700);
                }
                mAdapter.sort(details);
                mAdapter.setData(details);
                hideLoading();
            }else {
                if (event.max == 1){

                }else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    ObjectAnimator.ofFloat(mRecyclerView, "alpha", 0f, 1f).setDuration(700).start();

                    ObjectAnimator.ofFloat(mTextNoResult, "alpha", 1f, 0f).setDuration(700).start();
                    mTextNoResult.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mTextNoResult.setVisibility(View.INVISIBLE);
                        }
                    }, 700);
                    // 滚动到旧数据的最后一条
//                                mRecyclerView.smoothScrollToPosition(mAdapter.lastPosition());
                }
                mAdapter.sort(details);
                mAdapter.addOld(details);
                hideLoading();
            }
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

    private void getNew() {
        showLoading();
        Calendar c = Calendar.getInstance();
        c.set(currentYear, currentMonth, currentDay, 0, 0, 0);
        c.add(Calendar.DAY_OF_MONTH, 1);
        MainApplication.getApplication().getEquesApiUnit().getIcvss().equesGetAlarmMessageList(mDeviceID, (mAdapter.getFirstTime() + 1),
                c.getTimeInMillis(), 10);
    }

    public void sort(List<EquesVisitorDetailBean> data) {
        if (data == null) {
            return;
        }
        Collections.sort(data, new Comparator<EquesVisitorDetailBean>() {
            @Override
            public int compare(EquesVisitorDetailBean o1, EquesVisitorDetailBean o2) {
                if (o1.ringtime < o2.ringtime) {
                    return 1;
                } else if (o1.ringtime > o2.ringtime) {
                    return -1;
                }
                return 0;
            }
        });
    }

    @Override
    protected void initTitle() {
        mDeviceID = getIntent().getStringExtra(FILTER);
        mDevice = MainApplication.getApplication().getDeviceCache().get(mDeviceID);

        if (mDevice == null) {
            setToolBarTitleAndRightBtn(getString(R.string.Message_Center_AlarmMessage), getString(R.string.Message_Center_Emptyrecords));
        } else {
            setToolBarTitleAndRightBtn(DeviceInfoDictionary.getNameByDevice(mDevice), getString(R.string.Message_Center_Emptyrecords));
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.btn_right:
                clearAllLog();
                break;
            case R.id.log_linear_ctrl:
                showDatePicker();
                break;
        }
    }

    private WLDialog.Builder builder;
    private WLDialog dialog;

    private void clearAllLog() {
        builder = new WLDialog.Builder(this);
        builder.setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setMessage(getString(R.string.Message_Center_EmptyConfirm))
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        EquesApiUnit equesApiUnit = new EquesApiUnit(EquesHistoryAlarmActivity.this);
                        equesApiUnit.getIcvss().equesDelAlarmMessage(mDevice.devID, null, 2);
                        equesApiUnit.getIcvss().equesDelRingRecord(mDevice.devID, null, 2);

                        mAdapter = new EquesAlarmAdapter(EquesHistoryAlarmActivity.this, mDeviceID);
                        mRecyclerView.setAdapter(mAdapter);

                        String url2 = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices/deleteAlarmInfo";
                        OkGo.get(url2)
                                .tag(this)
                                .params("token", ApiConstant.getAppToken())
                                .params("childDeviceId", mDeviceID)
                                .params("deviceId", mDeviceID)
                                .params("msgType", "1")
                                .execute(new EncryptCallBack<ResponseBean<MessageCountBean>>() {
                                    @Override
                                    public void onSuccess(ResponseBean<MessageCountBean> responseBean, Call call, Response response) {
                                        WLog.e("deDeleteAlarm:onSuccess", response.toString());
                                    }

                                    @Override
                                    public void onError(Call call, Response response, Exception e) {
                                        String msg = EquesHistoryAlarmActivity.this.getString(R.string.Service_Error);
                                        super.onError(call, response, e);
                                        WLog.e("deDeleteAlarm", "onError: " + response, e);
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
        ObjectAnimator.ofFloat(mLinearCtrl, "rotation", 0, 20,0,-15,0,10,0,-5,0).setDuration(700).start();
    }

    private void getByDate(int y, int m, int d) {
        showLoading();
        Calendar c = Calendar.getInstance();
        c.set(showYear, showMonth, showDay, 0, 0, 0);
        long startTime =c.getTimeInMillis();
        c.set(showYear, showMonth, showDay + 1, 0, 0, 0);
        long endTime = c.getTimeInMillis();
        details.clear();
        isFirstArrival = true;
        MainApplication.getApplication().getEquesApiUnit().getIcvss().equesGetAlarmMessageList(mDeviceID, startTime,
                endTime, 10);
        MainApplication.getApplication().getEquesApiUnit().getIcvss().equesGetRingRecordList(mDeviceID, startTime, endTime, 10);
    }
}
