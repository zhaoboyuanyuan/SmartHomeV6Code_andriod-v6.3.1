package cc.wulian.smarthomev6.main.device.camera_lc;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.device.camera_lc.adapter.LcHistoryRecordAdapter;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.LcLocalRecordBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.customview.WLDatePicker;
import cc.wulian.smarthomev6.support.customview.popupwindow.DatePickerPopupWindow;
import cc.wulian.smarthomev6.support.customview.recycleSwipe.view.YRecyclerView;
import cc.wulian.smarthomev6.support.utils.DateUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

public class LcHistoryRecordActivity extends BaseTitleActivity {
    private static final String QUERY = "QUERY";
    private LinearLayout llDatePick;
    private ImageView ivArrow;
    private TextView tvPickDate;
    private TextView tvNoResult;

    private Device device;
    private YRecyclerView recyclerView;
    private DeviceApiUnit deviceApiUnit;
    private LcHistoryRecordAdapter recordAdapter;
    private DatePickerPopupWindow popupWindow;
    private List<LcLocalRecordBean.RecordsBean> recordList;
    private List<LcLocalRecordBean.RecordsBean> pageRecordList;
    private int page;
    private int channelId;
    private int showYear, showMonth, showDay;                    // 月份需要 +1
    private int currentYear, currentMonth, currentDay;
    private String deviceId;
    private String curDate;
    private String channelName;

    public static void start(Context context, String deviceID, int channelId) {
        Intent intent = new Intent(context, LcHistoryRecordActivity.class);
        intent.putExtra("deviceId", deviceID);
        intent.putExtra("channelId", channelId);
        context.startActivity(intent);
    }

    public static void start(Context context, String deviceID, int channelId, String channelName) {
        Intent intent = new Intent(context, LcHistoryRecordActivity.class);
        intent.putExtra("deviceId", deviceID);
        intent.putExtra("channelId", channelId);
        intent.putExtra("channelName", channelName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lc_history_record, true);
    }


    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(R.string.addDevice_CG27_Playback_local);
    }

    @Override
    protected void initView() {
        super.initView();
        llDatePick = (LinearLayout) findViewById(R.id.ll_date_pick);
        recyclerView = (YRecyclerView) findViewById(R.id.rl_record);
        ivArrow = (ImageView) findViewById(R.id.iv_arrow);
        tvPickDate = (TextView) findViewById(R.id.tv_date);
        tvNoResult = (TextView) findViewById(R.id.tv_no_result);
        initRecyclerViewStyle();
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        llDatePick.setOnClickListener(this);
        recordAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                tvNoResult.setVisibility(recordAdapter.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
        recyclerView.setRefreshAndLoadMoreListener(new YRecyclerView.OnRefreshAndLoadMoreListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh: ");
            }

            @Override
            public void onLoadMore() {
                page++;
                getHistoryRecord();
                recyclerView.setLoadMoreEnabled(false);
                Log.i(TAG, "onLoadMore: ");
            }
        });

    }

    @Override
    protected void initData() {
        super.initData();
        deviceId = getIntent().getStringExtra("deviceId");
        channelId = getIntent().getIntExtra("channelId", 0);
        channelName = getIntent().getStringExtra("channelName");
        device = mainApplication.getDeviceCache().get(deviceId);
        deviceApiUnit = new DeviceApiUnit(this);
        recordList = new ArrayList<>();
        pageRecordList = new ArrayList<>();
        if (device == null) {
            return;
        }
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        curDate = s.format(calendar.getTime());  //当前日期
        showYear = calendar.get(Calendar.YEAR);
        currentYear = calendar.get(Calendar.YEAR);
        showMonth = calendar.get(Calendar.MONTH);
        currentMonth = calendar.get(Calendar.MONTH);
        showDay = calendar.get(Calendar.DAY_OF_MONTH);
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        tvPickDate.setText(DateUtil.getFormatSimpleDate(new Date()));

        popupWindow = new DatePickerPopupWindow(LcHistoryRecordActivity.this);
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
                ObjectAnimator.ofFloat(ivArrow, "rotationX", 180f, 0f).setDuration(700).start();
            }
        });
        getHistoryRecord();

    }

    private void initRecyclerViewStyle() {
        recordAdapter = new LcHistoryRecordAdapter(this);
        recyclerView.setAdapter(recordAdapter);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLoadMoreEnabled(true);
        recyclerView.setRefreshEnabled(false);
        recordAdapter.setOnItemClickListener(new LcHistoryRecordAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, String recordId, String time1, String time2) {
                long startTime = 0;
                long endTime = 0;
                try {
                    startTime = Long.parseLong(DateUtil.dateToStamp(time1));
                    endTime = Long.parseLong(DateUtil.dateToStamp(time2));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                PlayCameraBackActivity.start(LcHistoryRecordActivity.this, deviceId, recordId, startTime, endTime,channelName);
            }
        });
    }


    private void pickDate(int year, int month, int day) {
        showYear = year;
        showMonth = month;
        showDay = day;
        Calendar calendar = Calendar.getInstance();
        calendar.set(showYear, showMonth, showDay);
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        curDate = s.format(calendar.getTime());  //当前日期
        tvPickDate.setText(curDate);
        page = 0;
        recordList.clear();
        getHistoryRecord();

    }

    private void getHistoryRecord() {
        progressDialogManager.showDialog(QUERY, this, "", null, 5000);
        String startTime;
        String endTime;
        int startRange;
        int endRange;
        String queryRange;
        startTime = curDate + " " + "00:00:00";
        endTime = curDate + " " + "23:59:59";
        startRange = 30 * page + 1;
        endRange = 30 * (page + 1);
        queryRange = startRange + "-" + endRange;
        Log.i(TAG, "curDate: " + curDate);
        Log.i(TAG, "startTime: " + startTime);
        Log.i(TAG, "endTime: " + endTime);
        Log.i(TAG, "queryRange: " + queryRange);

        deviceApiUnit.getLocalRecordFragment(deviceId, String.valueOf(channelId), startTime, endTime, "All", queryRange, new DeviceApiUnit.DeviceApiCommonListener<LcLocalRecordBean>() {
            @Override
            public void onSuccess(LcLocalRecordBean bean) {
                progressDialogManager.dimissDialog(QUERY, 0);
                if (bean != null && bean.getRecords() != null) {
                    pageRecordList = bean.getRecords();
                    recordList.addAll(pageRecordList);
                }
                recordAdapter.addData(recordList);
                recyclerView.setloadMoreComplete();
                recyclerView.setLoadMoreEnabled(true);
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(QUERY, 0);
                if (code == 3001001) {
                    ToastUtil.show(R.string.No_SD_Look_Back);
                }
                recyclerView.setloadMoreComplete();
                recyclerView.setLoadMoreEnabled(false);
            }
        });
    }

    /**********************************************************************
     *                              添加日历修改
     *                              2017年12月18日10:34:14
     **********************************************************************/
    private void showDatePicker() {
        popupWindow.showParent(llDatePick, showYear, showMonth, showDay);
        // 动画
        ObjectAnimator.ofFloat(ivArrow, "rotationX", 0f, 180f).setDuration(700).start();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ll_date_pick:
                showDatePicker();
                break;
        }
    }
}
