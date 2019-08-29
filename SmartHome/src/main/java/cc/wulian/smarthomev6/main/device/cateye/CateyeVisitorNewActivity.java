package cc.wulian.smarthomev6.main.device.cateye;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import cc.wulian.smarthomev6.main.message.alarm.ICamAlarmActivity;
import cc.wulian.smarthomev6.support.customview.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.message.adapter.ICamAlarmNewAdapter;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.ICamCloudApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.MessageBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamAlarmUrlBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.customview.SmoothLinearLayoutManager;
import cc.wulian.smarthomev6.support.customview.WLDatePicker;
import cc.wulian.smarthomev6.support.customview.popupwindow.DatePickerPopupWindow;
import cc.wulian.smarthomev6.support.tools.EndlessRecyclerOnScrollListener;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.DateUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by 上海滩小马哥 on 2017/10/20.
 */

public class CateyeVisitorNewActivity extends BaseTitleActivity {
    public static final String FILTER = "deviceID";

    private String mDeviceID;
    private String sdomain;
    private Device mDevice;

    private RecyclerView mRecyclerView;
    private SmoothLinearLayoutManager mLayoutManager;
    private EndlessRecyclerOnScrollListener mScrollListener;
    private SwipeRefreshLayout mSwipe;
    private TextView mTextNoResult;

    private ICamAlarmNewAdapter mAdapter;

    private ICamCloudApiUnit iCamCloudApiUnit;

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
    public static void start(Context context, String deviceID, String sdomain) {
        Intent intent = new Intent(context, CateyeVisitorNewActivity.class);
        intent.putExtra(FILTER, deviceID);
        intent.putExtra("sdomain", sdomain);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_alarm, true);

        mSwipe = (SwipeRefreshLayout) findViewById(R.id.alarm_swipe);
        mRecyclerView = (RecyclerView) findViewById(R.id.alarm_recycler);
        mTextNoResult = (TextView) findViewById(R.id.alarm_text_no_result);
        mLinearCtrl = (LinearLayout) findViewById(R.id.log_linear_ctrl);
        mImageArrow = (ImageView) findViewById(R.id.log_image_arrow);

        mAdapter = new ICamAlarmNewAdapter(this, mDeviceID, getIntent().getStringExtra("sdomain"));
        iCamCloudApiUnit = new ICamCloudApiUnit(this);

        mLayoutManager = new SmoothLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mScrollListener = new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                getOld();
            }
        };
//        mRecyclerView.addOnScrollListener(mScrollListener);
        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setSupportsChangeAnimations(true);
        animator.setAddDuration(1000);
        animator.setChangeDuration(1000);
        mRecyclerView.setItemAnimator(animator);
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

        popupWindow = new DatePickerPopupWindow(CateyeVisitorNewActivity.this);
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
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.log_linear_ctrl:
                showDatePicker();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        clearCount();
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
        showLoading();
        Calendar c = Calendar.getInstance();
        c.set(showYear, showMonth, showDay, 0, 0, 0);
        String startTime = showType == 0 ? "1" : ("" + c.getTimeInMillis());
        new DataApiUnit(this).doGetDeviceDataHistory(
                mDeviceID,
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
                                for (MessageBean.RecordListBean r : messageBean.recordList) {
                                    boolean isDoorCall = TextUtils.equals("0104021", r.messageCode)
                                            || TextUtils.equals("0103061", r.messageCode);
                                    if (isDoorCall) {
                                        list.add(r);
                                    }
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
        progressDialogManager.showDialog(TAG, CateyeVisitorNewActivity.this, null, null, getResources().getInteger(R.integer.http_timeout));
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
    }

    private void getAlarm(boolean showLoading) {
        if (showLoading) {
            showLoading();
        }
        new DataApiUnit(this).doGetDeviceDataHistory(
                mDeviceID,
                "1",
                mDeviceID,
                "0104021",
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
                            for (MessageBean.RecordListBean r : messageBean.recordList) {
                                boolean isDoorCall = TextUtils.equals("0104021", r.messageCode)
                                        || TextUtils.equals("0103061", r.messageCode);
                                if (isDoorCall) {
                                    list.add(r);
                                }
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
                });
    }

    private List<ICamAlarmUrlBean> selectRing(List<ICamAlarmUrlBean> bean){
        if (TextUtils.equals(mDeviceID, "CMICA1")){
            List<ICamAlarmUrlBean> newBeen = new ArrayList<>();
            for (ICamAlarmUrlBean icamBean:bean) {
                if (TextUtils.isEmpty(icamBean.url) || icamBean.url.endsWith("Ring.jpg")){
                    newBeen.add(icamBean);
                }
            }
            return newBeen;
        }
        return bean;
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
        mDeviceID = getIntent().getStringExtra(FILTER);
        mDevice = MainApplication.getApplication().getDeviceCache().get(mDeviceID);

        if (mDevice == null) {
            setToolBarTitle(getString(R.string.CateEye_Visitor_Record));
        } else {
            setToolBarTitle(DeviceInfoDictionary.getNameByDevice(mDevice));
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
        String startTime = "" + c.getTimeInMillis();
        c.set(showYear, showMonth, showDay + 1, 0, 0, 0);
        String endTime = "" + c.getTimeInMillis();
        new DataApiUnit(this).doGetDeviceDataHistory(
                mDeviceID,
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
                            for (MessageBean.RecordListBean r : messageBean.recordList) {
                                boolean isDoorCall = TextUtils.equals("0104021", r.messageCode)
                                        || TextUtils.equals("0103061", r.messageCode);
                                if (isDoorCall) {
                                    list.add(r);
                                }
                            }
                            mAdapter.sort(list);
                            setEndTime(messageBean.recordList.get(messageBean.recordList.size()-1).time);
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
