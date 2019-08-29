package cc.wulian.smarthomev6.main.message.log;

import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.AlarmInfoTest;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.message.adapter.LogListAdapter;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.MessageCountBean;
import cc.wulian.smarthomev6.support.core.device.AlarmCountCache;
import cc.wulian.smarthomev6.support.customview.SwipeRefreshLayout;
import cc.wulian.smarthomev6.support.event.AlarmUpdateEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

public class MessageLogListActivity extends BaseTitleActivity {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipe;
    private LogListAdapter mAdapter;
    private TextView mTextNoResult;

    private DataApiUnit mDataApi;
    private List<AlarmInfoTest> mAlarmList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_log_list, true);
        EventBus.getDefault().register(this);

        mDataApi = new DataApiUnit(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.log_recycler);
        mSwipe = (SwipeRefreshLayout) findViewById(R.id.log_swipe);
        mTextNoResult = (TextView) findViewById(R.id.alarm_text_no_result);

        mAdapter = new LogListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, 0, 0, 3);
            }
        });
        mSwipe.setColorSchemeColors(getResources().getColor(R.color.newPrimary));
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLogList();
            }
        });

        mSwipe.post(new Runnable() {
            @Override
            public void run() {
                mSwipe.setRefreshing(true);
                getLogList();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        getLogList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initTitle() {
        super.initTitle();

//        setToolBarTitle(getString(R.string.Message_Center_Log));
        setToolBarTitleAndRightBtn(getString(R.string.Message_Center_Log), getString(R.string.Message_Center_Allread));
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.btn_right:
                readAll();
                break;
        }
    }

    private void readAll() {
        StringBuilder sb = new StringBuilder();
        sb.append(Preference.getPreferences().getCurrentGatewayID()).append(",");
        for (AlarmInfoTest info : mAlarmList) {
            sb.append(info.deviceId).append(",");
        }
        if (sb.length() == 0) {
            return;
        }
        String deviceIdList = sb.deleteCharAt(sb.length() - 1).toString();
        MainApplication.getApplication().getAlarmCountCache().readAllLog();
        mDataApi.deGetAlarmCount(deviceIdList, null, "4", "2", new DataApiUnit.DataApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                WLog.i(TAG, "onSuccess: " + bean);
            }

            @Override
            public void onFail(int code, String msg) {
                WLog.i(TAG, "onFail: " + msg);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAlarmUpdateEvent(AlarmUpdateEvent event) {
        AlarmCountCache alarmCountCache = MainApplication.getApplication().getAlarmCountCache();
        for (AlarmInfoTest info : mAlarmList) {
            info.alarmCount = alarmCountCache.getLogChildCount(info.deviceId);
        }
        mAdapter.setList(mAlarmList);
    }

    private void getLogList() {

        mDataApi.deGetAlarmCount(Preference.getPreferences().getCurrentGatewayID(), null, "1", "2", new DataApiUnit.DataApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                WLog.i("Message", "onSuccess: " + bean);
                // 调整未读消息，和当前置顶消息
                MessageCountBean messageCountBean = (MessageCountBean) bean;
                if (bean != null) {
                    if (messageCountBean.childDevices.isEmpty()) {
                        showHasResult(false);
                        return;
                    } else {
                        showHasResult(true);
                    }
                    mAlarmList.clear();
                    if (!TextUtils.isEmpty(preference.getCurrentGatewayID())) {
                        AlarmInfoTest sceneAlarm = new AlarmInfoTest("scene", getString(R.string.Home_Module_Scene), R.drawable.icon_log_scene);
                        AlarmInfoTest houseKeeperAlarm = new AlarmInfoTest("housekeeper", getString(R.string.Message_Log_houseKeeper), R.drawable.icon_log_housekeeper);
                        mAlarmList.add(sceneAlarm);
                        mAlarmList.add(houseKeeperAlarm);
                    }
                    MainApplication.getApplication().getAlarmCountCache().setLogCount(messageCountBean);
                    for (MessageCountBean.ChildDevicesBean child : messageCountBean.childDevices) {
                        AlarmInfoTest alarm = new AlarmInfoTest(child);
                        mAlarmList.add(alarm);
                    }

                    mAdapter.setList(mAlarmList);
                } else {
                    showHasResult(false);
                }
            }

            @Override
            public void onFail(int code, String msg) {
                WLog.i("Message", "onFail: " + msg);
                ToastUtil.single(msg);
                showHasResult(false);
            }
        });
    }

    private void showHasResult(boolean hasResult) {
        mSwipe.setRefreshing(false);

        if (!hasResult) {
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
//            ObjectAnimator.ofFloat(mRecyclerView, "alpha", 0f, 1f).setDuration(700).start();

            ObjectAnimator.ofFloat(mTextNoResult, "alpha", 1f, 0f).setDuration(700).start();
            /*mTextNoResult.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mTextNoResult.setVisibility(View.INVISIBLE);
                }
            }, 700);*/
        }
    }
}
