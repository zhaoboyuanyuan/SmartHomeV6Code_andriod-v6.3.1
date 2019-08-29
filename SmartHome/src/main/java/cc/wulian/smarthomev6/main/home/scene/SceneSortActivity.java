package cc.wulian.smarthomev6.main.home.scene;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.SceneInfo;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.home.adapter.SceneSelectAdapter;
import cc.wulian.smarthomev6.main.smart.SceneManageGroupActivity;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.GatewayInfoBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.SceneGroupListBean;
import cc.wulian.smarthomev6.support.customview.SceneGroupPop;
import cc.wulian.smarthomev6.support.event.GetSceneListEvent;
import cc.wulian.smarthomev6.support.event.SceneGroupSetEvent;
import cc.wulian.smarthomev6.support.event.SceneVolumeGroupSetEvent;
import cc.wulian.smarthomev6.support.event.SortSceneEvent;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.dragsort.OnItemCallbackListener;
import cc.wulian.smarthomev6.support.tools.dragsort.OnStartDragListener;
import cc.wulian.smarthomev6.support.tools.dragsort.SortTouchCallback;
import cc.wulian.smarthomev6.support.utils.BitmapUtil;
import cc.wulian.smarthomev6.support.utils.DrawableUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * 场景排序页面
 */
public class SceneSortActivity extends BaseTitleActivity implements View.OnClickListener {
    private final static String VOLUME_SET = "volume_set";
    private FrameLayout flSceneGroup;
    private LinearLayout llSortTip;
    private RecyclerView recyclerView;
    private RecyclerView groupRecyclerView;
    private TextView tvJoinGroup;
    private TextView tvManageGroup;

    private SortAdapter sortAdapter;
    private SceneSelectAdapter sceneSelectAdapter;
    private ItemTouchHelper helper;
    private SceneManager sceneManager;
    private SceneGroupPop sceneGroupPop;
    private WLDialog wlDialog;

    private List<SceneInfo> sceneInfos;
    private List<SceneGroupListBean.DataBean> groupList;
    private List<SceneInfo> selectList;
    private HashMap<Integer, Boolean> groupMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_sort, true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setSortTitleView();
    }

    protected void initView() {
        flSceneGroup = (FrameLayout) findViewById(R.id.fl_scene_group);
        llSortTip = (LinearLayout) findViewById(R.id.ll_scene_sort_tip);
        recyclerView = (RecyclerView) findViewById(R.id.scene_sort_drag_list);
        groupRecyclerView = (RecyclerView) findViewById(R.id.rl_scene_group);
        tvJoinGroup = (TextView) findViewById(R.id.tv_join_group);
        tvManageGroup = (TextView) findViewById(R.id.tv_manage_group);
        tvJoinGroup.setOnClickListener(this);
        tvManageGroup.setOnClickListener(this);
    }

    protected void initData() {
        sceneManager = new SceneManager(SceneSortActivity.this);
        sceneInfos = sceneManager.acquireScene();
        selectList = new ArrayList<>();
        sortAdapter = new SortAdapter(sceneInfos, new OnStartDragListener() {
            @Override
            public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
                helper.startDrag(viewHolder);
            }
        });
        sortAdapter.setResetClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 恢复默认排序
                loadDefaultOrder();
            }
        });
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(sortAdapter);
        SortTouchCallback callback = new SortTouchCallback(sortAdapter);
        helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);
        sceneSelectAdapter = new SceneSelectAdapter(this, sceneInfos);
        sceneSelectAdapter.setOnClickListener(new SceneSelectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(HashMap<Integer, Boolean> map) {
                groupMap = map;
            }
        });
        groupRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        groupRecyclerView.setAdapter(sceneSelectAdapter);
//        getSceneGroupsInfo();
        showSortView();
    }

    private void loadDefaultOrder() {
        sceneInfos = sceneManager.acquireDefaultSortScene();
        sortAdapter.update(sceneInfos);
    }

//    private void getSceneGroupsInfo() {
//        groupList = mainApplication.getSceneCache().getGroups();
//        if (groupList.size() > 0) {
////            sortSceneGroup();
//            tvManageGroup.setEnabled(true);
//        } else {
//            tvManageGroup.setEnabled(false);
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 保存场景顺序
     */
    private void saveOrder() {
        sceneManager.saveOrder(sceneInfos);
    }

    private void setSortTitleView() {
        setToolBarTitle(getString(R.string.Mine_Setts));
        img_right.setImageResource(R.drawable.icon_group);
        img_right.setVisibility(View.VISIBLE);
        mTitle.setVisibility(View.VISIBLE);
    }

    private void showGroupView() {
        img_left.setVisibility(View.GONE);
        img_right.setVisibility(View.GONE);
        btn_left.setText(R.string.Cancel);
        btn_left.setVisibility(View.VISIBLE);
        btn_right.setVisibility(View.VISIBLE);
        btn_right.setText(R.string.CateEye_Album_Tittle_Select_All);
        btn_right.setTag("all");
        recyclerView.setVisibility(View.GONE);
        flSceneGroup.setVisibility(View.VISIBLE);
        llSortTip.setVisibility(View.GONE);
    }

    private void showSortView() {
        img_left.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(preference.getCurrentGatewayInfo())) {
            GatewayInfoBean gatewayInfoBean = JSON.parseObject(preference.getCurrentGatewayInfo(), GatewayInfoBean.class);
            img_right.setVisibility(TextUtils.equals("GW14", gatewayInfoBean.gwType) ? View.GONE : View.VISIBLE);
        }
        btn_left.setVisibility(View.GONE);
        btn_right.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        llSortTip.setVisibility(View.VISIBLE);
        flSceneGroup.setVisibility(View.GONE);
        sceneInfos = sceneManager.acquireScene();
        sortAdapter.update(sceneInfos);
    }

    private void showSceneGroupPop(List<SceneGroupListBean.DataBean> groupList) {
        sceneGroupPop = new SceneGroupPop(this, groupList);
        sceneGroupPop.setOnClickListener(new SceneGroupPop.onPopClickListener() {
            @Override
            public void CreateGroup() {
                Log.i(TAG, "CreateGroup: ");
                sceneGroupPop.dismiss();
                showAddGroupDialog();
            }

            @Override
            public void joinGroup(String groupID) {
                Log.i(TAG, "joinGroup: " + groupID);
                sceneGroupPop.dismiss();
                joinSceneGroup(groupID);
            }

        });
        sceneGroupPop.showAtLocation(findViewById(R.id.activity_scene_sort),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        sceneGroupPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                sceneGroupPop.backgroundAlpha(SceneSortActivity.this, 1f);
            }
        });
    }

    private void showAddGroupDialog() {
        WLDialog.Builder builder = new WLDialog.Builder(this);
        builder.setEditTextHint(R.string.Smart_group_name)
                .setPositiveButton(R.string.Sure)
                .setNegativeButton(R.string.Cancel)
                .setCancelOnTouchOutSide(false)
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View var1, String msg) {
                        addSceneGroup(msg);
                        wlDialog.dismiss();
                    }

                    @Override
                    public void onClickNegative(View var1) {
                        wlDialog.dismiss();

                    }
                });
        wlDialog = builder.create();
        if (!wlDialog.isShowing()) {
            wlDialog.show();
        }
    }

    private void addSceneGroup(String name) {
        sceneManager.setSceneGroup(preference.getCurrentGatewayID(), 0, null, name);
    }

    private void joinSceneGroup(String groupID) {
        JSONArray jsonArray = new JSONArray();
        for (int key : groupMap.keySet()) {
            if (groupMap.get(key)) {
                jsonArray.put(sceneInfos.get(key).getSceneID());
            }
        }
        Log.i(TAG, "joinSceneGroup: " + jsonArray.toString());
        progressDialogManager.showDialog(VOLUME_SET, SceneSortActivity.this, "", null, 10000);
        sceneManager.VolumeSetSceneGroup(preference.getCurrentGatewayID(), groupID, jsonArray);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                showSortView();
                break;
            case R.id.img_left:
                // 完成排序
                saveOrder();
                EventBus.getDefault().post(new SortSceneEvent());
                SceneSortActivity.this.finish();
                break;
            case R.id.img_right:
                showGroupView();
                break;
            case R.id.btn_right:
                if (btn_right.getTag().equals("all")) {
                    btn_right.setText(R.string.All_Not);
                    btn_right.setTag("none");
                    groupMap = sceneSelectAdapter.selectAll();
                } else if (btn_right.getTag().equals("none")) {
                    btn_right.setText(R.string.CateEye_Album_Tittle_Select_All);
                    btn_right.setTag("all");
                    groupMap = sceneSelectAdapter.selectNone();
                }
                break;
            case R.id.tv_join_group:
                //全选
//                if (btn_right.getTag().equals("none")) {
//                    groupMap = new HashMap<>();
//                    if (sceneInfos != null) {
//                        for (int i = 0; i < sceneInfos.size(); i++) {
//                            groupMap.put(i, true);
//                        }
//                    }
//                }
                if (groupMap != null && groupMap.containsValue(true)) {
                    showSceneGroupPop(mainApplication.getSceneCache().getGroups());
                }
                break;
            case R.id.tv_manage_group:
                if (mainApplication.getSceneCache().getGroups() != null && mainApplication.getSceneCache().getGroups().size() > 0) {
                    startActivity(new Intent(this, SceneManageGroupActivity.class));
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSceneGroupSet(SceneGroupSetEvent event) {
        switch (event.bean.getMode()) {
            case 0:
                joinSceneGroup(event.bean.getGroupID());
                break;
            case 1:
            case 2:
                loadDefaultOrder();
                sceneSelectAdapter.update(this, sceneInfos);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVolumeGroupSet(SceneVolumeGroupSetEvent event) {
        ToastUtil.show(R.string.Smart_in_group_ok);
        mainApplication.getSceneCache().getScenes();
        progressDialogManager.dimissDialog(VOLUME_SET, 0);
        MainApplication.getApplication()
                .getMqttManager()
                .publishEncryptedMessage(
                        MQTTCmdHelper.createGetAllScenes(preference.getCurrentGatewayID()),
                        MQTTManager.MODE_GATEWAY_FIRST);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSceneReport(GetSceneListEvent event) {
        showSortView();
        sceneSelectAdapter.selectClear();
        sceneSelectAdapter.update(this, sceneInfos);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.toptobottom_out);
    }

    private class SortAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
            implements OnItemCallbackListener {

        private List<SceneInfo> list;
        private OnStartDragListener mDragStartListener;

        public SortAdapter(List<SceneInfo> list, OnStartDragListener mDragStartListener) {
            this.list = list;
            this.mDragStartListener = mDragStartListener;
        }

        public void update(List<SceneInfo> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 1) {
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                View itemView = layoutInflater.inflate(R.layout.item_scene_sort_foot, parent, false);
                FootHolder holder = new FootHolder(itemView);
                return holder;
            }
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View itemView = layoutInflater.inflate(R.layout.item_scene_sort, parent, false);
            SortHolder holder = new SortHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof SortHolder) {
                ((SortHolder) holder).name.setText(list.get(position).getName());
                String groupName = null;
                groupName = mainApplication.getSceneCache().getGroupName(list.get(position).getGroupID());
                if (TextUtils.isEmpty(groupName)) {
                    ((SortHolder) holder).tvGroup.setText(groupName);
                } else {
                    ((SortHolder) holder).tvGroup.setText("[" + groupName + "]");
                }

                Bitmap bitmap = DrawableUtil.drawableToBitmap(SceneManager.getSceneIconNormal(SceneSortActivity.this, list.get(position).getIcon()));
                ((SortHolder) holder).scene_icon.setImageBitmap(BitmapUtil.changeBitmapColor(bitmap, 0xff262626));
                ((SortHolder) holder).sort.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                            mDragStartListener.onStartDrag(holder);
                        }
                        return false;
                    }
                });
            }

            if (holder instanceof FootHolder) {
                ((FootHolder) holder).reset.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
                if (resetClick != null) {
                    ((FootHolder) holder).reset.setOnClickListener(resetClick);
                }
            }
        }

        private View.OnClickListener resetClick;

        public void setResetClick(View.OnClickListener resetClick) {
            this.resetClick = resetClick;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == getItemCount() - 1) {
                return 1;
            }

            return 0;
        }

        @Override
        public int getItemCount() {
            return list.size() + 1;
        }

        @Override
        public void onMove(int fromPosition, int toPosition) {
            //滑动事件
            Collections.swap(list, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onSwipe(int position) {

        }

        class SortHolder extends RecyclerView.ViewHolder {

            private TextView name;
            private TextView tvGroup;
            private ImageView sort;
            private ImageView scene_icon;

            public SortHolder(View itemView) {
                super(itemView);

                name = (TextView) itemView.findViewById(R.id.item_scene_sort_title);
                tvGroup = (TextView) itemView.findViewById(R.id.tv_scene_group);
                sort = (ImageView) itemView.findViewById(R.id.drag_handle);
                scene_icon = (ImageView) itemView.findViewById(R.id.scene_icon);
            }
        }

        class FootHolder extends RecyclerView.ViewHolder {

            private TextView reset;

            public FootHolder(View itemView) {
                super(itemView);

                reset = (TextView) itemView.findViewById(R.id.scene_sort_text_reset);
            }
        }
    }
}
