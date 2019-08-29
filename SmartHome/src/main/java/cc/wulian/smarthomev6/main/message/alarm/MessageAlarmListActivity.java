package cc.wulian.smarthomev6.main.message.alarm;

import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.os.Bundle;
import cc.wulian.smarthomev6.support.customview.SmoothLinearLayoutManager;
import cc.wulian.smarthomev6.support.customview.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import cc.wulian.smarthomev6.main.message.adapter.AlarmListAdapter;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.MessageCountBean;
import cc.wulian.smarthomev6.support.core.device.AlarmCountCache;
import cc.wulian.smarthomev6.support.event.AlarmUpdateEvent;
import cc.wulian.smarthomev6.support.tools.EndlessRecyclerOnScrollListener;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

public class MessageAlarmListActivity extends BaseTitleActivity {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipe;
    private AlarmListAdapter mAdapter;
    private TextView mTextNoResult;
    private SmoothLinearLayoutManager mLayoutManager;
    private EndlessRecyclerOnScrollListener mScrollListener;
    private DataApiUnit mDataApi;
    private List<AlarmInfoTest> mAlarmList = new ArrayList<>();
    private int pageNum = 1;
    private int pageSize = 10;
    private View mLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_alarm_list, true);
        EventBus.getDefault().register(this);

        mDataApi = new DataApiUnit(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.alarm_recycler);
        mSwipe = (SwipeRefreshLayout) findViewById(R.id.alarm_swipe);
        mTextNoResult = (TextView) findViewById(R.id.alarm_text_no_result);
        mLoadingView = findViewById(R.id.loading_view);
        mAdapter = new AlarmListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mLayoutManager = new SmoothLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
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
                pageNum = 1;
                getAlarmList();
            }
        });

        mSwipe.post(new Runnable() {
            @Override
            public void run() {
                mSwipe.setRefreshing(true);
                pageNum = 1;
                getAlarmList();
            }
        });
        mScrollListener = new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                loadingMoreData();
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
//        getAlarmList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initTitle() {
        super.initTitle();

//        setToolBarTitle(getString(R.string.Message_Center_AlarmMessage));
        setToolBarTitleAndRightBtn(getString(R.string.Message_Center_AlarmMessage), getString(R.string.Message_Center_Allread));
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
        MainApplication.getApplication().getAlarmCountCache().readAllAlarm();
        mDataApi.deGetAlarmCount(deviceIdList, null, "4", "1", new DataApiUnit.DataApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                WLog.i("Message", "onSuccess: " + bean);
            }

            @Override
            public void onFail(int code, String msg) {
                WLog.i("Message", "onFail: " + msg);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAlarmUpdateEvent(AlarmUpdateEvent event) {
        AlarmCountCache alarmCountCache = MainApplication.getApplication().getAlarmCountCache();
        for (AlarmInfoTest info : mAlarmList) {
            info.alarmCount = alarmCountCache.getAlarmChildCount(info.deviceId);
        }
        mAdapter.setList(mAlarmList);
    }

    private boolean isAdd = false;

    private void getAlarmList() {
        mScrollListener.resetLoadMoreConfig();
        mDataApi.deGetAlarmCount(Preference.getPreferences().getCurrentGatewayID(), null, "1", "1", pageNum, pageSize, new DataApiUnit.DataApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                pageNum += 1;
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
//                    MainApplication.getApplication().getAlarmCountCache().setAlarmCount(messageCountBean);
                    for (MessageCountBean.ChildDevicesBean child : messageCountBean.childDevices) {
                        AlarmInfoTest alarm = new AlarmInfoTest(child);
                        mAlarmList.add(alarm);
                    }
                    mAdapter.setList(mAlarmList);
                } else {
                    showHasResult(false);
                }

                if (mAlarmList.size() >= 10 && !isAdd) {
                    isAdd = true;
                    mRecyclerView.addOnScrollListener(mScrollListener);
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

    private void getMoreAlarmList() {

        mDataApi.deGetAlarmCount(Preference.getPreferences().getCurrentGatewayID(), null, "1", "1", pageNum, pageSize, new DataApiUnit.DataApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                WLog.i("Message", "onSuccess: " + bean);
                // 调整未读消息，和当前置顶消息
                pageNum += 1;
                MessageCountBean messageCountBean = (MessageCountBean) bean;
                if (bean != null) {
//                    MainApplication.getApplication().getAlarmCountCache().setAlarmCount(messageCountBean);
                    if(messageCountBean.childDevices.size() > 0){
                        for (MessageCountBean.ChildDevicesBean child : messageCountBean.childDevices) {
                            AlarmInfoTest alarm = new AlarmInfoTest(child);
                            mAlarmList.add(alarm);
                        }
                        mAdapter.setList(mAlarmList);
                        mAdapter.stopLoadMore(true);
                    }else{
                        mAdapter.stopLoadMore(false);
                    }
                }
            }

            @Override
            public void onFail(int code, String msg) {
                WLog.i("Message", "onFail: " + msg);
                ToastUtil.single(msg);
                mAdapter.stopLoadMore(true);
            }
        });
    }

    private void loadingMoreData(){
//        showLoadingMore();
        mAdapter.startLoadAnimation();
        getMoreAlarmList();
    }

    private long loadingTime = 0;
//    private void showLoadingMore() {
//        findViewById(R.id.loading_container).setVisibility(View.VISIBLE);
//        Animation animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        animation.setRepeatCount(Animation.INFINITE);
//        animation.setDuration(700);
//        mLoadingView.startAnimation(animation);
//        mRecyclerView.scrollToPosition(mAdapter.lastPosition());
//    }

//    private void hideLoading() {
//        long delta = (System.currentTimeMillis() - loadingTime) / 1000;
//        if (delta > 700) {
//            progressDialogManager.dimissDialog(TAG, 0);
//        } else {
//            mRecyclerView.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    progressDialogManager.dimissDialog(TAG, 0);
//                }
//            }, 700 - delta);
//        }
//
//        findViewById(R.id.loading_container).setVisibility(View.GONE);
//        mLoadingView.clearAnimation();
//    }

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
//            mTextNoResult.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mTextNoResult.setVisibility(View.INVISIBLE);
//                }
//            }, 700);
        }
    }
}
