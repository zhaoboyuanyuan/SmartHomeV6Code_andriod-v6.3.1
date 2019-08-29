package cc.wulian.smarthomev6.main.smart;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.home.adapter.SceneGroupManageAdapter;
import cc.wulian.smarthomev6.main.home.scene.SceneManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.SceneGroupListBean;
import cc.wulian.smarthomev6.support.event.SceneGroupSetEvent;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;

public class SceneManageGroupActivity extends BaseTitleActivity {
    private final static String SET = "set";
    private RecyclerView recyclerView;
    private TextView tvNoGroupTips;

    private WLDialog renameDialog;
    private WLDialog deleteDialog;
    private WLDialog.Builder builder;
    private List<SceneGroupListBean.DataBean> groupList;
    private SceneManager sceneManager;
    private SceneGroupManageAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_group_manage, true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(R.string.Smart_group_manage);
    }

    @Override
    protected void initView() {
        super.initView();
        recyclerView = (RecyclerView) findViewById(R.id.rl_manage_group);
        tvNoGroupTips = (TextView) findViewById(R.id.tv_no_group_tip);
    }

    @Override
    protected void initData() {
        super.initData();
        sceneManager = new SceneManager(this);
        adapter = new SceneGroupManageAdapter(this, null);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        getSceneGroupsInfo();

        adapter.setOnClickListener(new SceneGroupManageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, String tag) {
                if (TextUtils.equals("edit", tag)) {
                    showRenameDialog(position);
                } else if (TextUtils.equals("delete", tag)) {
                    showDeleteGroupDialog(position);
                }
            }
        });
    }

    private void getSceneGroupsInfo() {
        groupList = mainApplication.getSceneCache().getGroups();
        sortAndUpdateView();
    }

    private void showRenameDialog(final int position) {
        builder = new WLDialog.Builder(this);
        builder.setTitle(getString(R.string.Device_Rename))
                .setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setEditTextText(groupList.get(position).getName())
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        if (TextUtils.isEmpty(msg.trim())) {
                            return;
                        }
                        setGroupInfo(1, groupList.get(position).getGroupID(), msg);
                        renameDialog.dismiss();
                    }

                    @Override
                    public void onClickNegative(View view) {
                        renameDialog.dismiss();
                    }
                });
        renameDialog = builder.create();
        if (!renameDialog.isShowing()) {
            renameDialog.show();
        }
    }

    private void showDeleteGroupDialog(final int position) {
        builder = new WLDialog.Builder(this);
        builder.setMessage(R.string.Smart_group_delete)
                .setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        setGroupInfo(2, groupList.get(position).getGroupID(), null);
                        deleteDialog.dismiss();
                    }

                    @Override
                    public void onClickNegative(View view) {
                        deleteDialog.dismiss();
                    }
                });
        deleteDialog = builder.create();
        if (!deleteDialog.isShowing()) {
            deleteDialog.show();
        }
    }

    private void setGroupInfo(int mode, String groupID, String name) {
        progressDialogManager.showDialog(SET, SceneManageGroupActivity.this, null, null, 5000);
        sceneManager.setSceneGroup(preference.getCurrentGatewayID(), mode, groupID, name);
    }

    private void sortAndUpdateView() {
        Collections.sort(groupList, new Comparator<SceneGroupListBean.DataBean>() {
            @Override
            public int compare(SceneGroupListBean.DataBean o1, SceneGroupListBean.DataBean o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        tvNoGroupTips.setVisibility(groupList.size() > 0 ? View.GONE : View.VISIBLE);
        recyclerView.setVisibility(groupList.size() > 0 ? View.VISIBLE : View.GONE);
        adapter.update(groupList);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSceneGroupSet(SceneGroupSetEvent event) {
        progressDialogManager.dimissDialog(SET, 0);
        switch (event.bean.getMode()) {
            case 0:
                Log.i(TAG, "onSceneGroupSet: " + event.bean.getGroupID());
                break;
            case 1://修改
                for (SceneGroupListBean.DataBean bean :
                        groupList) {
                    if (TextUtils.equals(bean.getGroupID(), event.bean.getGroupID())) {
                        bean.setName(event.bean.getName());
                        sortAndUpdateView();
                    }
                }
                break;
            case 2://删除
                for (SceneGroupListBean.DataBean bean :
                        groupList) {
                    if (TextUtils.equals(bean.getGroupID(), event.bean.getGroupID())) {
                        groupList.remove(bean);
                        sortAndUpdateView();
                    }
                }
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
