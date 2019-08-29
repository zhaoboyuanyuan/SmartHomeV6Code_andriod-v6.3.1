package cc.wulian.smarthomev6.main.smart;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.main.home.adapter.AutoTaskAdapter;
import cc.wulian.smarthomev6.main.home.scene.HouseKeeperActivity;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.bean.AutoTaskBean;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.customview.RecycleViewDivider;
import cc.wulian.smarthomev6.support.customview.recycleSwipe.view.YRecyclerView;
import cc.wulian.smarthomev6.support.event.GatewayStateChangedEvent;
import cc.wulian.smarthomev6.support.event.GetAutoProgramListEvent;
import cc.wulian.smarthomev6.support.event.GetAutoProgramTaskEvent;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;

public class HouseKeeperFragment extends WLFragment {

    private final static String AUTO_TASK_DELETE = "delete";
    private final static String AUTO_TASK_REVERSE = "reverse";
    private final static String AUTO_TASK_ITEM = "item_auto_task";
    private YRecyclerView recyclerView;
    private LinearLayout layoutNoAutoTaskTip;
    private List<AutoTaskBean.RuleArrayBean> autoTaskList;
    private List<AutoTaskBean.RuleArrayBean> allTaskList;
    private AutoTaskAdapter autoTaskAdapter;
    private AutoTaskBean autoTaskBean;
    private int currentPage;
    private int selectPosition;
    private String selectProgramID;

    @Override
    public int layoutResID() {
        return R.layout.fragment_house_keeper;
    }

    @Override
    protected void initTitle(View v) {
        super.initTitle(v);
        header.setVisibility(View.GONE);
    }

    @Override
    public void initView(View view) {
        recyclerView = view.findViewById(R.id.rv_house_keeper);
        layoutNoAutoTaskTip = view.findViewById(R.id.ll_no_scene_tip);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initData() {
        super.initData();
        currentPage = 1;
        allTaskList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        autoTaskAdapter = new AutoTaskAdapter(getActivity(), null);
//        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.addItemDecoration(new RecycleViewDivider(getActivity(), LinearLayout.VERTICAL, 1, getResources().getColor(R.color.gray)));
        recyclerView.setAdapter(autoTaskAdapter);
        getAutoTaskList(1);
        recyclerView.setRefreshEnabled(true);
        recyclerView.setLoadMoreEnabled(true);
        recyclerView.setRefreshAndLoadMoreListener(new YRecyclerView.OnRefreshAndLoadMoreListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh: ");
                currentPage = 1;
                getAutoTaskList(currentPage);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setReFreshComplete();
                    }
                }, 1000);

            }

            @Override
            public void onLoadMore() {
                Log.i(TAG, "onLoadMore: " + currentPage);
                getAutoTaskList(currentPage);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setloadMoreComplete();
                    }
                }, 1000);

            }
        });
        autoTaskAdapter.setOnClickListener(new AutoTaskAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, String tag) {
                selectPosition = position;
                if (TextUtils.equals(AUTO_TASK_DELETE, tag)) {
                    deleteProgram(allTaskList.get(position).getProgramID());
                } else if (TextUtils.equals(AUTO_TASK_REVERSE, tag)) {
                    if (TextUtils.equals(allTaskList.get(position).getStatus(), "0")) {
                        changeProgramStatus(allTaskList.get(position).getProgramID(), "1");
                    } else {
                        changeProgramStatus(allTaskList.get(position).getProgramID(), "0");
                    }
                } else if (TextUtils.equals(AUTO_TASK_ITEM, tag)) {
                    if (TextUtils.equals("2", allTaskList.get(position).getTriggerArray().get(0).getType())) {
                        startActivity(new Intent(getActivity(), HouseKeeperActivity.class)
                                .putExtra("url", "circumstances.html?programID=" + allTaskList.get(position).getProgramID()));
                    } else if (TextUtils.equals("1", allTaskList.get(position).getTriggerArray().get(0).getType())) {
                        startActivity(new Intent(getActivity(), HouseKeeperActivity.class)
                                .putExtra("url", "timeTask1.html?programID=" + allTaskList.get(position).getProgramID()));
                    }
                }
            }
        });
    }


    private void getAutoTaskList(int page) {
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "508");
            object.put("gwID", preference.getCurrentGatewayID());
            object.put("appID", MainApplication.getApplication().getLocalInfo().appID);
            object.put("i", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MainApplication.getApplication().getMqttManager()
                .publishEncryptedMessage(
                        object.toString(),
                        MQTTManager.MODE_GATEWAY_FIRST);
    }


    private void deleteProgram(String programID) {
        ProgressDialogManager.getDialogManager().showDialog(AUTO_TASK_DELETE, getActivity(), "", null, 5000);
        selectProgramID = programID;
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "507");
            object.put("gwID", preference.getCurrentGatewayID());
            object.put("programID", programID);
            object.put("operType", "D");
            object.put("userID", ApiConstant.getUserID());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MainApplication.getApplication().getMqttManager()
                .publishEncryptedMessage(
                        object.toString(),
                        MQTTManager.MODE_GATEWAY_FIRST);
    }

    private void changeProgramStatus(String programID, String status) {
        selectProgramID = programID;
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "507");
            object.put("gwID", preference.getCurrentGatewayID());
            object.put("programID", programID);
            object.put("operType", "S");
            object.put("status", status);
            object.put("userID", ApiConstant.getUserID());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MainApplication.getApplication().getMqttManager()
                .publishEncryptedMessage(
                        object.toString(),
                        MQTTManager.MODE_GATEWAY_FIRST);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGatewayStateChangedEvent(GatewayStateChangedEvent event) {
        recyclerView.setloadMoreComplete();
        allTaskList.clear();
        currentPage = 1;
        getAutoTaskList(1);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GetAutoProgramListEvent event) {
        autoTaskBean = JSON.parseObject(event.jsonData, AutoTaskBean.class);
        autoTaskList = autoTaskBean.getRuleArray();
        if (currentPage == 1) {
            allTaskList.clear();
        }
        if (autoTaskList != null && autoTaskList.get(0) != null && autoTaskBean.getI() == currentPage) {
            currentPage = autoTaskBean.getI() + 1;
            allTaskList.addAll(autoTaskList);
        } else {
            if (allTaskList != null && allTaskList.size() > 0) {
//                recyclerView.setloadMoreComplete();
//                recyclerView.setReFreshComplete();
//                return;
            }
        }
        if (allTaskList != null && allTaskList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            layoutNoAutoTaskTip.setVisibility(View.GONE);
            autoTaskAdapter.update(allTaskList);
        } else {
            recyclerView.setVisibility(View.GONE);
            layoutNoAutoTaskTip.setVisibility(View.VISIBLE);
        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GetAutoProgramTaskEvent event) {
        try {
            JSONObject jsonObject = new JSONObject(event.jsonData);
            String operType = jsonObject.optString("operType");
            String programID = jsonObject.optString("programID");
            Log.i(TAG, "onEvent: " + operType + programID + selectProgramID);
            if (TextUtils.equals("D", operType) && TextUtils.equals(programID, selectProgramID)) {//删除
                ProgressDialogManager.getDialogManager().dimissDialog(AUTO_TASK_DELETE, 0);
                autoTaskAdapter.remove(selectPosition);
            } else if (TextUtils.equals("S", operType) && TextUtils.equals(programID, selectProgramID)) {//状态修改
                String status = jsonObject.optString("status");
                allTaskList.get(selectPosition).setStatus(status);
                autoTaskAdapter.update(allTaskList);
                Log.i(TAG, "onEvent: status = " + status);
            } else if (TextUtils.equals("C", operType)) {//新增
                allTaskList.clear();
                currentPage = 1;
                getAutoTaskList(1);
            } else if (TextUtils.equals("U", operType)) {//修改名称
                allTaskList.clear();
                currentPage = 1;
                getAutoTaskList(1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
