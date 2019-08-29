package cc.wulian.smarthomev6.main.device.eques;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.eques.icvss.api.ICVSSUserInstance;
import com.lzy.okgo.OkGo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.eques.adapter.EquesVisitorAdapter;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesVisitorBean;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesVisitorDetailBean;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.EncryptCallBack;
import cc.wulian.smarthomev6.support.core.apiunit.EquesApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.MessageCountBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.ResponseBean;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者: chao
 * 时间: 2017/6/15
 * 描述: 移康猫眼访客记录
 * 联系方式: 805901025@qq.com
 */

public class EquesVisitorActivity extends BaseTitleActivity {
    private static final String KEY_DEVICE_ID = "deviceId";
    private ListView recordListView;
    private View footView;
    private AppCompatTextView noRecordTextView;
    private EquesVisitorAdapter equesVisitorAdapter;

    private ICVSSUserInstance icvss;
    private String deviceId = null;
    private long time = System.currentTimeMillis();
    private boolean hasMore = true;
    private boolean isScrolled = false;
    private boolean hasFootView = false;
    private List<EquesVisitorDetailBean> equesVisitorDetailBeanList;

    public static void start(Context context, String deviceId) {
        Intent intent = new Intent(context, EquesVisitorActivity.class);
        intent.putExtra(KEY_DEVICE_ID, deviceId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_cateye_visitor, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitleAndRightBtn(getString(R.string.CateEye_Visitor_Record), getString(R.string.Message_Center_Emptyrecords));
    }

    @Override
    protected void initView() {
        recordListView = (ListView) findViewById(R.id.alarm_detail_list);
        noRecordTextView = (AppCompatTextView) findViewById(R.id.alarm_detail_text_no_result);
        footView = View.inflate(this, R.layout.load_more, null);
    }

    @Override
    protected void initData() {
        Bundle bd = getIntent().getExtras();
        if (bd == null) {
            ToastUtil.show("Bundle is Empty!");
            return;
        }
        deviceId = bd.getString(KEY_DEVICE_ID);
        icvss = MainApplication.getApplication().getEquesApiUnit().getIcvss();
        equesVisitorDetailBeanList = new ArrayList<>();
        equesVisitorAdapter = new EquesVisitorAdapter(this, deviceId, equesVisitorDetailBeanList);
        recordListView.setAdapter(equesVisitorAdapter);
        getAlarmRecord();
    }

    @Override
    protected void initListeners() {
        recordListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolled = true;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (isScrolled) {
                    if (firstVisibleItem + visibleItemCount == totalItemCount) {
                        // 说明加载到最后一条了
                        showLoadMoreView(hasMore);
                        if (hasMore) {
                            getAlarmRecord();
                        }
                    }
                }
            }
        });
    }


    private void getAlarmRecord() {
        icvss.equesGetRingRecordList(deviceId, 0, time, 10);
    }

    public void showLoadMoreView(boolean isShown) {
        if (isShown) {
            if (!hasFootView) {
                recordListView.addFooterView(footView);
                hasFootView = true;
            }
        } else {
            if (hasFootView) {
                recordListView.removeFooterView(footView);
                hasFootView = false;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EquesVisitorBean event) {
        List<EquesVisitorDetailBean> record = event.rings;
        if (record.size() >= 10) {
            hasMore = true;
        } else {
            hasMore = false;
        }
        if (!record.isEmpty()) {
            time = record.get(record.size() - 1).ringtime;
            equesVisitorAdapter.addMore(record);
        }
        if (equesVisitorAdapter.getCount() == 0) {
            recordListView.setVisibility(View.INVISIBLE);
            noRecordTextView.setVisibility(View.VISIBLE);
        } else {
            recordListView.setVisibility(View.VISIBLE);
            noRecordTextView.setVisibility(View.INVISIBLE);
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
                        EquesApiUnit equesApiUnit = new EquesApiUnit(EquesVisitorActivity.this);
                        equesApiUnit.getIcvss().equesDelRingRecord(deviceId, null, 2);

                        equesVisitorDetailBeanList.clear();
                        equesVisitorAdapter.notifyDataSetChanged();

                        String url2 = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices/deleteAlarmInfo";
                        OkGo.get(url2)
                                .tag(this)
                                .params("token", ApiConstant.getAppToken())
                                .params("childDeviceId", deviceId)
                                .params("deviceId", deviceId)
                                .params("msgType", "1")
                                .execute(new EncryptCallBack<ResponseBean<MessageCountBean>>() {
                                    @Override
                                    public void onSuccess(ResponseBean<MessageCountBean> responseBean, Call call, Response response) {
                                        WLog.e("deDeleteAlarm:onSuccess", response.toString());
                                    }

                                    @Override
                                    public void onError(Call call, Response response, Exception e) {
                                        String msg = EquesVisitorActivity.this.getString(R.string.Service_Error);
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

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_right:
                clearAllLog();
                break;
        }
    }
}
