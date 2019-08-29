package cc.wulian.smarthomev6.main.device.cateye;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cc.wulian.smarthomev6.BuildConfig;
import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.VisitRecordEntity;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.adapter.CateyeVisitorAdapter;
import cc.wulian.smarthomev6.main.message.alarm.BcAlarmActivity;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.MessageBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.customview.WLDatePicker;
import cc.wulian.smarthomev6.support.customview.popupwindow.DatePickerPopupWindow;
import cc.wulian.smarthomev6.support.tools.AlarmTool;
import cc.wulian.smarthomev6.support.tools.EndlessRecyclerOnScrollListener;
import cc.wulian.smarthomev6.support.utils.DateUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * 作者: chao
 * 时间: 2017/5/10
 * 描述: 访客记录
 * 联系方式: 805901025@qq.com
 */

public class CateyeVisitorActivity extends BaseTitleActivity {

    public static final String LOAD = "LOAD";
    private static final String KEY_DEVICE_ID = "deviceId";
    private static final String KEY_CAMERA_ID = "cameraId";
    private RecyclerView recordListView;
    private AppCompatTextView noRecordTextView;
    private CateyeVisitorAdapter cateyeVisitorAdapter;
    private EndlessRecyclerOnScrollListener mScrollListener;
    private LinearLayoutManager mLayoutManager;
    private LinearLayout mLinearCtrl;
    private ImageView mImageArrow;

    private String deviceId = null;
    private String cameraId = null;
    private String gwID = "";
    private DataApiUnit dataApiUnit;
    private long time = System.currentTimeMillis();
    private Device device;
    private DatePickerPopupWindow popupWindow;

    private int showYear, showMonth, showDay;                    // 月份需要 +1
    private int currentYear, currentMonth, currentDay;           // 月份需要 +1

    public static void start(Context context, String deviceId, String cameraId, String gwID) {
        Intent intent = new Intent(context, CateyeVisitorActivity.class);
        intent.putExtra(KEY_DEVICE_ID, deviceId);
        intent.putExtra(KEY_CAMERA_ID, cameraId);
        intent.putExtra("gwID", gwID);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doorlock_bc_visitor, true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.CateEye_Visitor_Record));
    }

    @Override
    protected void initView() {
        recordListView = (RecyclerView) findViewById(R.id.alarm_detail_list);
        noRecordTextView = (AppCompatTextView) findViewById(R.id.alarm_detail_text_no_result);
        mLinearCtrl = (LinearLayout) findViewById(R.id.log_linear_ctrl);
        mImageArrow = (ImageView) findViewById(R.id.log_image_arrow);
    }

    @Override
    protected void initData() {
        Bundle bd = getIntent().getExtras();
        if (bd == null) {
            ToastUtil.show("Bundle is Empty!");
            return;
        }
        deviceId = bd.getString(KEY_DEVICE_ID);
        cameraId = bd.getString(KEY_CAMERA_ID);
        device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        if (bd.containsKey("gwID")) {
            gwID = bd.getString("gwID");
        }

        mLayoutManager = new LinearLayoutManager(this);
        cateyeVisitorAdapter = new CateyeVisitorAdapter(this, deviceId,cameraId);
        recordListView.setAdapter(cateyeVisitorAdapter);
        recordListView.setLayoutManager(mLayoutManager);
        recordListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, 0, 0, 3);
            }
        });
        dataApiUnit = new DataApiUnit(this);
        getAlarmRecord("0", time + "");
        initDatePicker();
    }

    @Override
    protected void initListeners() {
        mScrollListener = new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                getAlarmRecord("0", time + "");
                WLog.i(TAG, "onLoadMore: "+new Date(time));
            }
        };
        mLinearCtrl.setOnClickListener(this);
    }

    /**
     * 初始化日期选择器
     */
    private void initDatePicker() {
        Calendar calendar = Calendar.getInstance();
        showYear = calendar.get(Calendar.YEAR);
        currentYear = calendar.get(Calendar.YEAR);
        showMonth = calendar.get(Calendar.MONTH);
        currentMonth = calendar.get(Calendar.MONTH);
        showDay = calendar.get(Calendar.DAY_OF_MONTH);
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        progressDialogManager.showDialog(LOAD, CateyeVisitorActivity.this, getString(R.string.Disposing), null, getResources().getInteger(R.integer.http_timeout));
        popupWindow = new DatePickerPopupWindow(CateyeVisitorActivity.this);
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
    }

    public List<VisitRecordEntity> changeMessage_Bc(MessageBean bean) {
        List<VisitRecordEntity> list = new ArrayList<>();
        for (MessageBean.RecordListBean r : bean.recordList) {
            boolean isDoorCall = TextUtils.equals("0104021", r.messageCode)
                    || TextUtils.equals("0103061", r.messageCode);
            if (!isDoorCall) {
                continue;
            }
            VisitRecordEntity visitRecordEntity = new VisitRecordEntity();
//            visitRecordEntity.date = r.createDate;
            Date Date = new Date(r.time);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            visitRecordEntity.date = sdf.format(Date);
            if (r.extData != null) {
                if (TextUtils.isEmpty(cameraId)) {
                    new DataApiUnit(this).doGetBcCameraId(deviceId, device.type, new DataApiUnit.DataApiCommonListener<String>() {
                        @Override
                        public void onSuccess(String bean) {
                            if (!TextUtils.isEmpty(bean)) {
                                cameraId = bean;
                            } else {
                                cameraId = null;
                            }
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            cameraId = null;
                        }
                    });
                }
                if (TextUtils.isEmpty(cameraId)) {
                    visitRecordEntity.url = null;
                } else {
                    if (r.extData.length() >= 8) {
                        String strPre = r.extData.substring(0, 8);//前缀日期
                        String strLast = r.extData.substring(8);//后缀时分秒
                        visitRecordEntity.url = strPre + "/" + cameraId + "/" + strLast + ".jpg";
                    }
                }
                WLog.d(TAG, "visitRecordEntity.url=" + visitRecordEntity.url);
            } else {
                visitRecordEntity.url = null;
            }
            if (r.extData1 != null) {
                try {
                    JSONObject extData1 = new JSONObject(r.extData1);
                    visitRecordEntity.extData1 = new VisitRecordEntity.ExtraData1();
                    visitRecordEntity.extData1.bucket = extData1.getString("bucket");
                    visitRecordEntity.extData1.region = extData1.getString("region");
                } catch (org.json.JSONException e) {
                    e.printStackTrace();
                }
            } else {
                visitRecordEntity.bucket = ApiConstant.BUCKET;
                visitRecordEntity.region = ApiConstant.REGION;
            }
            visitRecordEntity.msg = AlarmTool.getFormatString(getString(R.string.CateyeVisitor_call), "");
            visitRecordEntity.time = r.time;
            list.add(visitRecordEntity);
        }
        return list;
    }

    private void getAlarmRecord(String startTime, String endTime) {
        String alarmCode = "0103061";
        dataApiUnit.doGetDeviceDataHistory(
                gwID,
                "1",
                deviceId,
                alarmCode,
                startTime,
                endTime,
                new DataApiUnit.DataApiCommonListener() {
                    @Override
                    public void onSuccess(Object bean) {
                        progressDialogManager.dimissDialog(LOAD, 0);
                        MessageBean messageBean = (MessageBean) bean;
                        List<VisitRecordEntity> record = changeMessage_Bc(messageBean);
                        if (record.size() >= 10) {
                            recordListView.addOnScrollListener(mScrollListener);
                        } else {
                            recordListView.removeOnScrollListener(mScrollListener);
                        }
                        if (!record.isEmpty()) {
                            time = record.get(record.size() - 1).time;
                            cateyeVisitorAdapter.addMore(record);
                        }
                        if (cateyeVisitorAdapter.getItemCount() == 0) {
                            recordListView.setVisibility(View.INVISIBLE);
                            noRecordTextView.setVisibility(View.VISIBLE);
                        } else {
                            recordListView.setVisibility(View.VISIBLE);
                            noRecordTextView.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        recordListView.setVisibility(View.INVISIBLE);
                        noRecordTextView.setVisibility(View.VISIBLE);
                    }
                });

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.log_linear_ctrl:
                showDatePicker();
                break;
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
        Calendar c = Calendar.getInstance();
        c.set(showYear, showMonth, showDay, 0, 0, 0);
        String startTime = "" + c.getTimeInMillis();
        c.set(showYear, showMonth, showDay + 1, 0, 0, 0);
        String endTime = "" + c.getTimeInMillis();
        cateyeVisitorAdapter.clear();
        getAlarmRecord(startTime, endTime);
    }
}
