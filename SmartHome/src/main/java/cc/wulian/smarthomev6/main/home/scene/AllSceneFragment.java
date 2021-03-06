package cc.wulian.smarthomev6.main.home.scene;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.SceneInfo;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.main.device.nfc.NFCWriteActivity;
import cc.wulian.smarthomev6.main.home.adapter.SceneAllAdapter;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.customview.popupwindow.EditScenePopupWindow;
import cc.wulian.smarthomev6.support.event.GatewayStateChangedEvent;
import cc.wulian.smarthomev6.support.event.GetSceneListEvent;
import cc.wulian.smarthomev6.support.event.LoginEvent;
import cc.wulian.smarthomev6.support.event.SceneInfoEvent;
import cc.wulian.smarthomev6.support.event.SortSceneEvent;
import cc.wulian.smarthomev6.support.tools.DividerGridItemDecoration;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.CustomProgressDialog;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.KeyboardUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.Trans2PinYin;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * created by huxc  on 2018/12/5.
 * func：
 * email: hxc242313@qq.com
 */

public class AllSceneFragment extends WLFragment implements View.OnClickListener {

    private final String OPEN_SCENE = "OPEN_SCENE";
    private final String DELETE_SCENE = "DELETE_SCENE";

    private View layout_search_header;
    private LinearLayout linearSearch;
    private EditText editSearch;
    private TextView textCancel, textNoResult, textNoData;
    private RecyclerView mRecyclerView;
    private SceneAllAdapter mRecyclerAdapter;

    private List<SceneInfo> sceneInfos;
    private List<SceneInfo> searchList;
    private boolean isSearching = false;

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
        return R.layout.fragment_all_scene;
    }

    @Override
    protected void initTitle(View v) {
        super.initTitle(v);
        header.setVisibility(View.GONE);
    }

    @Override
    protected void updateSkin() {
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(layout_search_header, SkinResouceKey.BITMAP_NAV_BOTTOM_BACKGROUND);
    }

    @Override
    public void initView(View view) {
        layout_search_header = view.findViewById(R.id.layout_search_header);
        linearSearch = (LinearLayout) view.findViewById(R.id.all_scene_linear_search);
        editSearch = (EditText) view.findViewById(R.id.all_scene_edit_search);
        textCancel = (TextView) view.findViewById(R.id.all_scene_text_cancel);
        textNoResult = (TextView) view.findViewById(R.id.all_scene_text_noResult);
        textNoData = (TextView) view.findViewById(R.id.all_scene_text_noData);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.all_scene_recycler);

        linearSearch.setOnClickListener(this);
        textCancel.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        sceneManager = new SceneManager(getActivity());
        sceneInfos = sceneManager.acquireScene();
        initRecycleViewStyle();
        popupWindow = new EditScenePopupWindow(getActivity());
        MainApplication.getApplication()
                .getMqttManager()
                .publishEncryptedMessage(
                        MQTTCmdHelper.createGetAllScenes(preference.getCurrentGatewayID()),
                        MQTTManager.MODE_GATEWAY_FIRST);

        initLis();
    }

    private void initLis() {
        editSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Drawable drawable = editSearch.getCompoundDrawables()[2];
                if (drawable == null) {
                    // don't have end drawable
                    return false;
                }

                // 点击了 输入框中 右边的 x
                if (event.getX() > editSearch.getWidth()
                        - editSearch.getPaddingRight()
                        - drawable.getIntrinsicWidth()) {
                    editSearch.setText("");
                }
                return false;
            }
        });

        editSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    // TODO: 2017/3/31 Test Toast Here
                }
                return false;
            }
        });

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
                                sceneManager.deleteScene(
                                        isSearching
                                                ? searchList.get(longClickItem)
                                                : sceneInfos.get(longClickItem));
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
//                GatewayVersionTool.doGatewayVersionCompare(getActivity(), new Runnable() {
//                    @Override
//                    public void run() {
                SceneInfo info = isSearching ? searchList.get(longClickItem) : sceneInfos.get(longClickItem);
                EditSceneActivity.start(getActivity(),
                        info.getStatus(),
                        info.getName(),
                        info.getSceneID(),
                        info.getIcon(),
                        info.getProgramID(),
                        info.getGroupID());
//                    }
//                });
            }

            @Override
            public void onEditNewScene() {
                SceneInfo info = isSearching ? searchList.get(longClickItem) : sceneInfos.get(longClickItem);
                EditNewSceneActivity.start(getActivity(),
                        info.getStatus(),
                        info.getName(),
                        info.getSceneID(),
                        info.getIcon(),
                        info.getProgramID());
//                    }
            }

            @Override
            public void onWriteNFC() {
                SceneInfo info = isSearching ? searchList.get(longClickItem) : sceneInfos.get(longClickItem);
                NFCWriteActivity.start(getActivity(), info);
            }
        });

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                WLog.i("WL--->", "before: " + s + ", count: " + count);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                WLog.i("WL--->", "on: " + s + ", count: " + count);
                Drawable drawableRight = getResources().getDrawable(R.drawable.icon_delete);
                Drawable drawableLeft = getResources().getDrawable(R.drawable.icon_search);
                if (count == 0) {
                    editSearch.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
                } else {
                    editSearch.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, drawableRight, null);
                }
                doSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                WLog.i("WL--->", "after: " + s);
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
                    textNoData.setVisibility(View.VISIBLE);
                } else {
                    textNoData.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void initRecycleViewStyle() {
        String style = Preference.getPreferences().getSceneShowLayout();
        mRecyclerAdapter = new SceneAllAdapter(getActivity(), sceneInfos,style);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        switch (style) {
            case "sudoku_3":
                mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                mRecyclerView.addItemDecoration(new DividerGridItemDecoration(getActivity()));
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSceneReport(GetSceneListEvent event) {
        sceneInfos = sceneManager.acquireScene();
        mRecyclerAdapter.update(sceneInfos);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSortSceneEvent(SortSceneEvent event) {
        sceneInfos = sceneManager.acquireScene();
        mRecyclerAdapter.update(sceneInfos);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginEvent(LoginEvent event) {
        sceneInfos = sceneManager.acquireScene();
        mRecyclerAdapter.update(sceneInfos);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGatewayStateChangedEvent(GatewayStateChangedEvent event) {
        sceneInfos = sceneManager.acquireScene();
        mRecyclerAdapter.update(sceneInfos);
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
        if (isSearching) {
            doSearch(editSearch.getText().toString().trim());
        } else {
            mRecyclerAdapter.update(sceneInfos);
        }
    }


    /**
     * 执行搜索
     *
     * @param key 搜索的关键字
     */
    private void doSearch(String key) {
        if (key.trim().isEmpty()) {
            searchList = sceneInfos;
            textNoResult.setVisibility(View.INVISIBLE);
            mRecyclerAdapter.update(searchList);
            return;
        }
        searchList = sceneManager.searchScenes(key);

        String lowerKey = key.toLowerCase().trim();
        for (SceneInfo info : sceneInfos) {
            if (Trans2PinYin.isFirstCharacter(lowerKey, info.getName())) {
                if (!searchList.contains(info)) {
                    searchList.add(info);
                }
            }
            if (Trans2PinYin.isStartPinYin(lowerKey, info.getName())) {
                if (!searchList.contains(info)) {
                    searchList.add(info);
                }
            }
            if (Trans2PinYin.isContainsPinYin(lowerKey, info.getName())) {
                if (!searchList.contains(info)) {
                    searchList.add(info);
                }
            }
        }

        if (searchList.size() == 0) {
            textNoResult.setVisibility(View.VISIBLE);
            if (textNoData.getVisibility() == View.VISIBLE) {
                textNoData.setVisibility(View.INVISIBLE);
            }
            mRecyclerView.setVisibility(View.INVISIBLE);
            return;
        } else {
            textNoResult.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
        mRecyclerAdapter.update(searchList);
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

        List<SceneInfo> list = isSearching ? searchList : sceneInfos;

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
            case R.id.all_scene_linear_search:
                searchViewClick();
                break;
            case R.id.all_scene_text_cancel:
                cancelTextClick(v);
                break;
        }
    }

    private void showCenterToast(String msg) {
        ToastUtil.singleCenter(msg);
    }

    private void showCenterToast(@StringRes int msg) {
        ToastUtil.singleCenter(msg);
    }

    /**
     * 取消TextView点击事件
     */
    private void cancelTextClick(View v) {
        isSearching = false;

        mRecyclerAdapter.update(sceneInfos);

        linearSearch.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        editSearch.setVisibility(View.INVISIBLE);
        textCancel.setVisibility(View.INVISIBLE);
        textNoResult.setVisibility(View.INVISIBLE);
        editSearch.clearFocus();
        // 强制隐藏键盘
        KeyboardUtil.hideKeyboard(getActivity(), v);
    }

    /**
     * 点击 searchView
     */
    private void searchViewClick() {
        isSearching = true;
        searchList = sceneInfos;
//        doSearch(editSearch.getText().toString());

        linearSearch.setVisibility(View.INVISIBLE);
        editSearch.setVisibility(View.VISIBLE);
        textCancel.setVisibility(View.VISIBLE);
        editSearch.setFocusable(true);
        editSearch.setFocusableInTouchMode(true);
        editSearch.requestFocus();
        // 强制打开键盘
        KeyboardUtil.showKeyboard(getActivity());
        editSearch.setText("");
    }

}
