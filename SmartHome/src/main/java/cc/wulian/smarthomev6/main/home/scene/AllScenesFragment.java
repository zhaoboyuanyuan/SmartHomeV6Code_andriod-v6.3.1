package cc.wulian.smarthomev6.main.home.scene;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.SceneInfo;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.main.device.nfc.NFCWriteActivity;
import cc.wulian.smarthomev6.main.home.adapter.SceneAllAdapter;
import cc.wulian.smarthomev6.main.home.adapter.SceneAreaAdapter;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.SceneGroupListBean;
import cc.wulian.smarthomev6.support.customview.popupwindow.EditScenePopupWindow;
import cc.wulian.smarthomev6.support.event.GatewayStateChangedEvent;
import cc.wulian.smarthomev6.support.event.GetAutoProgramTaskEvent;
import cc.wulian.smarthomev6.support.event.GetSceneGroupListEvent;
import cc.wulian.smarthomev6.support.event.GetSceneListEvent;
import cc.wulian.smarthomev6.support.event.LoginEvent;
import cc.wulian.smarthomev6.support.event.SceneGroupSetEvent;
import cc.wulian.smarthomev6.support.event.SceneInfoEvent;
import cc.wulian.smarthomev6.support.event.SceneVolumeGroupSetEvent;
import cc.wulian.smarthomev6.support.event.SortSceneEvent;
import cc.wulian.smarthomev6.support.tools.DividerGridItemDecoration;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.CustomProgressDialog;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * created by huxc  on 2018/12/5.
 * func：全部场景界面
 * email: hxc242313@qq.com
 */

public class AllScenesFragment extends WLFragment implements View.OnClickListener {

    private final String OPEN_SCENE = "OPEN_SCENE";
    private final String DELETE_SCENE = "DELETE_SCENE";

    private LinearLayout llNoData;
    private FrameLayout flGroupRecycle;
    private RecyclerView mRecyclerView;
    private RecyclerView groupRecycleView;
    private SceneAllAdapter mRecyclerAdapter;
    private SceneAreaAdapter sceneAreaAdapter;

    private List<SceneInfo> sceneInfos;
    private List<SceneInfo> selectedList;
    private List<SceneGroupListBean.DataBean> groupList;
    private int selectGroupPosition;

    private SceneManager sceneManager;

    private EditScenePopupWindow popupWindow;
    private WLDialog.Builder builder;
    private WLDialog dialog;
    // 长按呼出菜单时，所点击的item
    private int longClickItem = -1;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public int layoutResID() {
        return R.layout.fragment_all_scenes;
    }

    @Override
    protected void initTitle(View v) {
        super.initTitle(v);
        header.setVisibility(View.GONE);
    }


    @Override
    public void initView(View view) {
        Log.i(TAG, "initView: ");
        llNoData = (LinearLayout) view.findViewById(R.id.ll_no_scene_tip);
        flGroupRecycle = (FrameLayout) view.findViewById(R.id.fl_recycleview);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.all_scene_recycler);
        groupRecycleView = (RecyclerView) view.findViewById(R.id.rl_scene_area);
    }

    @Override
    protected void initData() {
        super.initData();
        Log.i(TAG, "initData: ");
        sceneManager = new SceneManager(getActivity());
        selectGroupPosition = 0;
        sceneInfos = sceneManager.acquireScene();
        selectedList = new ArrayList<>(sceneInfos);
        initRecycleViewStyle();
        initSceneGroupView();
        getSceneGroupsInfo();
        popupWindow = new EditScenePopupWindow(getActivity());
        MainApplication.getApplication()
                .getMqttManager()
                .publishEncryptedMessage(
                        MQTTCmdHelper.createGetAllScenes(preference.getCurrentGatewayID()),
                        MQTTManager.MODE_GATEWAY_FIRST);

        initLis();
    }

    private void initLis() {
        popupWindow.setPopupClickListener(new EditScenePopupWindow.OnPopupClickListener() {
            @Override
            public void onDelete() {
                builder = new WLDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.Home_Scene_DeleteScene))
                        .setCancelOnTouchOutSide(false)
                        .setMessage(getString(R.string.Home_Scene_DeleteScene_Tip))
                        .setPositiveButton(getResources().getString(R.string.Sure))
                        .setNegativeButton(getResources().getString(R.string.Cancel))
                        .setListener(new WLDialog.MessageListener() {
                            @Override
                            public void onClickPositive(View view, String msg) {
                                // 删除之
                                sceneManager.deleteScene(selectedList.get(longClickItem));
                                ProgressDialogManager.getDialogManager().showDialog(DELETE_SCENE, getActivity(), null, new CustomProgressDialog.OnDialogDismissListener() {
                                    @Override
                                    public void onDismiss(CustomProgressDialog var1, int var2) {
                                        if (var2 != 0) {
                                            showCenterToast(R.string.Home_Scene_DeleteScene_Failed);
                                        }
                                    }
                                }, getResources().getInteger(R.integer.http_timeout));
                                dialog.dismiss();
                            }

                            @Override
                            public void onClickNegative(View var1) {
                                dialog.dismiss();
                            }

                        });
                dialog = builder.create();
                if (!dialog.isShowing()) {
                    dialog.show();
                }
            }

            @Override
            public void onEdit() {
                SceneInfo info = selectedList.get(longClickItem);
                EditSceneActivity.start(getActivity(),
                        info.getStatus(),
                        info.getName(),
                        info.getSceneID(),
                        info.getIcon(),
                        info.getProgramID(),
                        info.getGroupID()
                );
            }

            @Override
            public void onEditNewScene() {
                SceneInfo info = selectedList.get(longClickItem);
                EditNewSceneActivity.start(getActivity(),
                        info.getStatus(),
                        info.getName(),
                        info.getSceneID(),
                        info.getIcon(),
                        info.getProgramID());
            }

            @Override
            public void onWriteNFC() {
                SceneInfo info = selectedList.get(longClickItem);
                NFCWriteActivity.start(getActivity(), info);
            }
        });

        mRecyclerAdapter.setOnClickListener(new SceneAllAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                openSceneByPosition(position);
            }
        });
        mRecyclerAdapter.setOnItemLongClickListener(new SceneAllAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position) {
//                if (preference.getCurrentGatewayState().equals("0")) {
//                    return;
//                }

                longClickItem = position;
                popupWindow.showParent(mRecyclerView);
            }
        });
        // 注册 监听场景数据变化
        mRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                if (mRecyclerAdapter.isEmpty()) {
                    llNoData.setVisibility(View.VISIBLE);
                } else {
                    llNoData.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.i(TAG, "onHiddenChanged: ");
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            Log.i(TAG, "onCreateAnimation: ");
        }
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    private void getSceneGroupsInfo() {
        MainApplication.getApplication().getSceneCache().clearGroups();
        if (preference.getCurrentGatewayID() != null) {
            sceneManager.getSceneGroupList(preference.getCurrentGatewayID());
        }
    }

    private void initRecycleViewStyle() {
        String style = Preference.getPreferences().getSceneShowLayout();
        mRecyclerAdapter = new SceneAllAdapter(getActivity(), sceneInfos, style);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        switch (style) {
            case "sudoku_3":
                mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                break;
            case "sudoku_4":
                mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
                mRecyclerView.addItemDecoration(new DividerGridItemDecoration(getActivity()));
                break;
            case "list":
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                        outRect.set(0, 0, 0, 3);
                    }
                });
                break;
        }
    }

    private void initSceneGroupView() {
        sceneAreaAdapter = new SceneAreaAdapter(getActivity());
        groupRecycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        groupRecycleView.setItemAnimator(new DefaultItemAnimator());
        groupRecycleView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, 0, 0, 3);
            }
        });
        groupRecycleView.setAdapter(sceneAreaAdapter);
        sceneAreaAdapter.setOnItemClickListener(new SceneAreaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                selectGroupPosition = position;
                showSelectedGroupScene((groupList != null && groupList.size() != 0) ? groupList.get(selectGroupPosition).getGroupID() : "");
            }
        });
    }

    private void showSelectedGroupScene(String groupID) {
        selectedList.clear();
        if (selectGroupPosition == 0 || TextUtils.isEmpty(groupID)) {
            selectedList.addAll(sceneInfos);
        } else {
            for (SceneInfo sceneInfo : sceneInfos
                    ) {
                if (TextUtils.equals(sceneInfo.getGroupID(), groupID)) {
                    selectedList.add(sceneInfo);
                }
            }
        }
        mRecyclerAdapter.update(selectedList);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSceneReport(GetSceneListEvent event) {
        sceneInfos = sceneManager.acquireScene();
        showSelectedGroupScene((groupList != null && groupList.size() != 0) ? groupList.get(selectGroupPosition).getGroupID() : "");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSortSceneEvent(SortSceneEvent event) {
        sceneInfos = sceneManager.acquireScene();
        showSelectedGroupScene((groupList != null && groupList.size() != 0) ? groupList.get(selectGroupPosition).getGroupID() : "");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginEvent(LoginEvent event) {
        sceneInfos = sceneManager.acquireScene();
        mRecyclerAdapter.update(sceneInfos);
        getSceneGroupsInfo();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGatewayStateChangedEvent(GatewayStateChangedEvent event) {
        flGroupRecycle.setVisibility(View.GONE);
        sceneInfos = sceneManager.acquireScene();
        mRecyclerAdapter.update(sceneInfos);
        getSceneGroupsInfo();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSceneUpdated(SceneInfoEvent event) {
        switch (event.sceneBean.mode) {
            case 0:// 切换状态
                // 菊花加载
                ProgressDialogManager.getDialogManager().dimissDialog(OPEN_SCENE, 0);
                break;
            case 1:// 新增
                break;
            case 2:// 修改
                break;
            case 3:// 删除
                // 菊花加载
                ProgressDialogManager.getDialogManager().dimissDialog(DELETE_SCENE, 0);
                // 下面这行放开就有动画
//                mRecyclerAdapter.remove(longClickItem);
                break;
        }
        sceneInfos = sceneManager.acquireScene();
        showSelectedGroupScene((groupList != null && groupList.size() != 0) ? groupList.get(selectGroupPosition).getGroupID() : "");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSceneGroupList(GetSceneGroupListEvent event) {
        if (event.bean.getData() == null) {
            flGroupRecycle.setVisibility(View.GONE);
        } else {
            groupList = MainApplication.getApplication().getSceneCache().getGroups();
            flGroupRecycle.setVisibility(View.VISIBLE);
            sceneAreaAdapter.update(groupList);
            showSelectedGroupScene((groupList != null && groupList.size() != 0) ? groupList.get(selectGroupPosition).getGroupID() : "");
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSceneGroupSet(SceneGroupSetEvent event) {
        selectGroupPosition = 0;
        groupList = MainApplication.getApplication().getSceneCache().getGroups();
        if (groupList.size() == 0) {
            flGroupRecycle.setVisibility(View.GONE);
            selectedList.clear();
            selectedList.addAll(sceneInfos);
            mRecyclerAdapter.update(selectedList);
        } else {
            sceneAreaAdapter.setSelectPosition(0);
            showSelectedGroupScene((groupList != null && groupList.size() != 0) ? groupList.get(selectGroupPosition).getGroupID() : "");
            flGroupRecycle.setVisibility(View.VISIBLE);
            sceneAreaAdapter.update(groupList);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVolumeGroupSet(SceneVolumeGroupSetEvent event) {
        MainApplication.getApplication().getSceneCache().getScenes();
        MainApplication.getApplication()
                .getMqttManager()
                .publishEncryptedMessage(
                        MQTTCmdHelper.createGetAllScenes(preference.getCurrentGatewayID()),
                        MQTTManager.MODE_GATEWAY_FIRST);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GetAutoProgramTaskEvent event) {
        try {
            JSONObject jsonObject = new JSONObject(event.jsonData);
            String operType = jsonObject.getString("operType");
            if ("C".equals(operType)) {
                JSONObject object = jsonObject.getJSONArray("triggerArray").getJSONObject(0);
                String type = object.getString("type");
                if ("0".equals(type)) {
                    String sceneId = object.getString("object");
                    String programID = jsonObject.getString("programID");
                    SceneManager manager = new SceneManager(getActivity());
                    manager.setSceneProgramId(sceneId, programID);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开 场景 通过 位置
     */
    private void openSceneByPosition(int position) {
        if (preference.getCurrentGatewayState().equals("0")) {
            ToastUtil.show(R.string.Gateway_Offline);
            return;
        }
        // 菊花加载
        ProgressDialogManager.getDialogManager().showDialog(OPEN_SCENE, getActivity(), null, new CustomProgressDialog.OnDialogDismissListener() {
            @Override
            public void onDismiss(CustomProgressDialog var1, int var2) {
                if (var2 != 0) {
                    showCenterToast(R.string.Home_Scene_OpenScene_Failed);
                }
            }
        }, getResources().getInteger(R.integer.http_timeout));

        List<SceneInfo> list = new ArrayList<>();
        list.addAll(selectedList);

        // 切换场景
        sceneManager.toggleScene(list.get(position));
        // TODO: 2017/3/20 这里可以优化刷新的数量 减少绘制
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.all_scene_image_sort: {
                Intent intent = new Intent(getActivity(), SceneSortActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.bottomtotop_in, 0);
            }
            break;
            case R.id.all_scene_image_add:
//                GatewayVersionTool.doGatewayVersionCompare(this, new Runnable() {
//                    @Override
//                    public void run() {
                startActivity(new Intent(getActivity(), AddSceneActivity.class));
//                    }
//                });
                break;
        }
    }


    private void showCenterToast(@StringRes int msg) {
        ToastUtil.singleCenter(msg);
    }

}
