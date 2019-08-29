package cc.wulian.smarthomev6.main.device.safeDog;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.SafeDogIncidentsBean;
import cc.wulian.smarthomev6.support.customview.SmoothLinearLayoutManager;
import cc.wulian.smarthomev6.support.customview.SwipeRefreshLayout;
import cc.wulian.smarthomev6.support.event.AlarmPushEvent;
import cc.wulian.smarthomev6.support.tools.EndlessRecyclerOnScrollListener;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

public class SafeDogAttackSourceRecord extends BaseTitleActivity {

    public static final String FILTER = "deviceID";

    private RecyclerView mRecyclerView;
    private SmoothLinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipe;
    private TextView mTextNoResult;
    private EndlessRecyclerOnScrollListener mScrollListener;
    private AttackSourceAdapter mAdapter;

    private DataApiUnit mDataApi;

    private DateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");

    private DateFormat hms = new SimpleDateFormat("HH:mm:ss");

    private DateFormat ymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void start(Activity activity, String devId) {
        Intent intent = new Intent(activity, SafeDogAttackSourceRecord.class);
        intent.putExtra("devId", devId);
        activity.startActivityForResult(intent, 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attack_source_record, true);
        final String devId = getIntent().getStringExtra("devId");
        mSwipe = (SwipeRefreshLayout) findViewById(R.id.record_swipe);
        mRecyclerView = (RecyclerView) findViewById(R.id.record_recycler);
        mTextNoResult = (TextView) findViewById(R.id.text_no_result);
        mAdapter = new AttackSourceAdapter();
        mDataApi = new DataApiUnit(this);

        mLayoutManager = new SmoothLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mScrollListener = new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
//                getSafedogIncidents(devId);
            }
        };
        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setSupportsChangeAnimations(true);
        animator.setAddDuration(1000);
        animator.setChangeDuration(1000);
        mRecyclerView.setItemAnimator(animator);
        mRecyclerView.setAdapter(mAdapter);

        mSwipe.setEnabled(false);
        getSafedogIncidents(devId);
    }

    @Override
    protected void initTitle() {
        setToolBarTitle(getResources().getString(R.string.Device_attack));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private long loadingTime = 0;

    private void showLoading() {
        loadingTime = System.currentTimeMillis();
        progressDialogManager.showDialog(TAG, SafeDogAttackSourceRecord.this, null, null, getResources().getInteger(R.integer.http_timeout));
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

    class AttackSourceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<SafeDogIncidentsBean> mData;

        public AttackSourceAdapter() {
            mData = new ArrayList<>();
        }

        public void setData(List<SafeDogIncidentsBean> data) {
            if (data == null) {
                return;
            }
            mData = data;
            notifyDataSetChanged();
        }

        public int lastPosition() {
            return mData.size() - 1;
        }

        public boolean isEmpty() {
            return mData.isEmpty();
        }

        public void sort(List<SafeDogIncidentsBean> data) {
            if (data == null) {
                return;
            }
            Collections.sort(data, new Comparator<SafeDogIncidentsBean>() {
                @Override
                public int compare(SafeDogIncidentsBean o1, SafeDogIncidentsBean o2) {
                    if (o1.startTime < o2.startTime) {
                        return 1;
                    } else if (o1.startTime > o2.startTime) {
                        return -1;
                    }
                    return 0;
                }
            });
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View itemView = layoutInflater.inflate(R.layout.item_attack_source_record, parent, false);
            return new RecordHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String titleDateStr = ymd.format(new Date(mData.get(position).startTime));
            WLog.i("luzx", ymdhms.format(new Date(mData.get(position).startTime)));
            ((RecordHolder) holder).textDate.setText(titleDateStr);
            ((RecordHolder) holder).mTextAttackDate.setText(hms.format(new Date(mData.get(position).startTime)));
            ((RecordHolder) holder).mTextAttackSource.setText(getTypeStr(mData.get(position).incidentType));
            ((RecordHolder) holder).mTextAttackDistance.setText(getDistanceStr(mData.get(position).distance));
            if(position > 0){
                String lastTitleDateStr = ymd.format(new Date(mData.get(position - 1).startTime));
                if(titleDateStr.equals(lastTitleDateStr)){
                    ((RecordHolder) holder).textDate.setVisibility(View.GONE);
                }else{
                    ((RecordHolder) holder).textDate.setVisibility(View.VISIBLE);
                }
                if(position == mData.size() - 1){
                    ((RecordHolder) holder).mTop.setVisibility(View.VISIBLE);
                    ((RecordHolder) holder).mBottom.setVisibility(View.INVISIBLE);
                    ((RecordHolder) holder).linkPoint.setVisibility(View.INVISIBLE);
                }
            }else{
                ((RecordHolder) holder).textDate.setVisibility(View.VISIBLE);
                ((RecordHolder) holder).mTop.setVisibility(View.INVISIBLE);
                ((RecordHolder) holder).mBottom.setVisibility(View.VISIBLE);
                ((RecordHolder) holder).linkPoint.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }

        private class RecordHolder extends RecyclerView.ViewHolder {

            private View mTop, mBottom, linkPoint;
            private TextView textDate, mTextAttackDate, mTextAttackSource, mTextAttackDistance;
            public RecordHolder(View itemView) {
                super(itemView);
                textDate = (TextView) itemView.findViewById(R.id.text_date);
                mTop = itemView.findViewById(R.id.line_top);
                mBottom = itemView.findViewById(R.id.line_bottom);
                linkPoint = itemView.findViewById(R.id.link_point);
                mTextAttackDate = (TextView) itemView.findViewById(R.id.attack_date);
                mTextAttackSource = (TextView) itemView.findViewById(R.id.attack_source);
                mTextAttackDistance = (TextView) itemView.findViewById(R.id.attack_distance);
            }
        }
    }

    private String getDistanceStr(int m){
        String d = String .format(getResources().getString(R.string.Device_attackdistance), String .format("%.1f", m / 1000f));
        return d;
    }

    private String getTypeStr(String type){
        /**
         0: 不存在攻击源
         1: wifi device
         2: zb device
         3: ble device
         */
        String result = "";
        switch (type){
            case "12010":
                result = getResources().getString(R.string.Device_safe020);
                break;
        }
        return result;
    }

    private void getSafedogIncidents(String devId) {
        showLoading();
        mDataApi.doGetSafedogIncidents(
                devId,
                new DataApiUnit.DataApiCommonListener<List<SafeDogIncidentsBean>>() {
                    @Override
                    public void onSuccess(List<SafeDogIncidentsBean> bean) {
                        mSwipe.setRefreshing(false);

                        int length = bean.size();
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

                            mAdapter.sort(bean);
                            mAdapter.setData(bean);
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
