package cc.wulian.smarthomev6.main.device;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Paint;
import android.os.Bundle;

import cc.wulian.smarthomev6.entity.RoomInfo;
import cc.wulian.smarthomev6.support.customview.SwipeRefreshLayout;

import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.adapter.AreaListAdapter;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.RoomBean;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.GetRoomListEvent;
import cc.wulian.smarthomev6.support.event.RoomInfoEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.dragsort.OnItemCallbackListener;
import cc.wulian.smarthomev6.support.tools.dragsort.OnStartDragListener;
import cc.wulian.smarthomev6.support.tools.dragsort.SortTouchCallback;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by syf on 2017/2/15
 */
public class AreaActivity extends BaseTitleActivity {

    private final String TAG = this.getClass().getSimpleName();

    private SwipeRefreshLayout areaSwipe;
    private ListView areaListView;
    private RecyclerView mRecyclerSort;
    private View sortLayout;
    private TextView tvNoAreaTip;
    private AreaListAdapter areaListAdapter;
    private SortAdapter sortAdapter;
    private ItemTouchHelper helper;
    private WLDialog.Builder builder;
    private WLDialog dialog;
    private AreaManager areaManager;
    private List<RoomInfo> areaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management_devision, true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitleAndTwoRightImg(getString(R.string.Area_Manager_Group), R.drawable.icon_add, R.drawable.icon_order);
    }

    @Override
    protected void initView() {
        areaListView = (ListView) findViewById(R.id.area_listView);
        tvNoAreaTip = (TextView) findViewById(R.id.tv_no_area_tip);
        areaSwipe = (SwipeRefreshLayout) findViewById(R.id.area_swipe);
        mRecyclerSort = (RecyclerView) findViewById(R.id.area_sort_recyclerView);
        sortLayout = findViewById(R.id.sort_layout);
        areaListAdapter = new AreaListAdapter(this, null);
        areaListView.setAdapter(areaListAdapter);
        mRecyclerSort.setNestedScrollingEnabled(false);
        areaSwipe.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        areaListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                if (areaListAdapter.isEmpty()) {
                    tvNoAreaTip.setVisibility(View.VISIBLE);
                } else {
                    tvNoAreaTip.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    protected void initData() {
        areaManager = new AreaManager(AreaActivity.this);
        getAreaInfo();
    }

    @Override
    protected void initListeners() {
        areaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AreaDetailActivity.start(AreaActivity.this, areaList.get(i).getName(), areaList.get(i).getRoomID());
            }
        });

        areaSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                areaSwipe.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        areaSwipe.setRefreshing(false);
                    }
                }, 700);

                getRoomList();
            }
        });
    }

    /**
     * 获取分区列表
     */
    private void getRoomList() {
        MainApplication.getApplication().getMqttManager().
                publishEncryptedMessage(
                        MQTTCmdHelper.createGetAllRooms(
                                Preference.getPreferences().getCurrentGatewayID()),
                        MQTTManager.MODE_GATEWAY_FIRST);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GetRoomListEvent event) {
        getAreaInfo();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RoomInfoEvent event) {
        getAreaInfo();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DeviceInfoChangedEvent event) {
        areaListAdapter.notifyDataSetChanged();
    }

    private void getAreaInfo() {
        areaList = areaManager.loadDiskRoom();
        areaListAdapter.swapData(areaList);
        areaListAdapter.notifyDataSetChanged();
        if (sortAdapter != null) {
            sortAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClickView(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        switch (v.getId()) {
            case R.id.img_left:
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
                onBackPressed();
                break;
            case R.id.btn_left:
                if (sortLayout.getVisibility() == View.VISIBLE) {
                    sortLayout.setVisibility(View.GONE);
                    areaSwipe.setVisibility(View.VISIBLE);
                    setToolBarTitleAndTwoRightImg(getString(R.string.Area_Manager_Group), R.drawable.icon_add, R.drawable.icon_order);
                }
                break;
            case R.id.img_right:
                addArea();
                break;
            case R.id.img_right1:
                sortLayout.setVisibility(View.VISIBLE);
                areaSwipe.setVisibility(View.GONE);
                setToolBarLeftTitleAndRightText(R.string.Cancel, R.string.Home_Scene_SortScene, R.string.Done);
                if (sortAdapter == null) {
                    sortAdapter = new SortAdapter(new OnStartDragListener() {
                        @Override
                        public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
                            helper.startDrag(viewHolder);
                        }
                    });
                    mRecyclerSort.setAdapter(sortAdapter);

                    SortTouchCallback callback = new SortTouchCallback(sortAdapter);
                    helper = new ItemTouchHelper(callback);
                    helper.attachToRecyclerView(mRecyclerSort);
                }
                sortAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_right:
                if (areaList.size() > 0) {
                    sortLayout.setVisibility(View.GONE);
                    areaSwipe.setVisibility(View.VISIBLE);
                    setToolBarTitleAndTwoRightImg(getString(R.string.Area_Manager_Group), R.drawable.icon_add, R.drawable.icon_order);
                    areaListAdapter.swapData(areaList);
                    areaListAdapter.notifyDataSetChanged();
                    areaManager.saveOrder(areaList);
                }
                break;
            default:
                break;
        }
    }

    private void addArea() {
        builder = new WLDialog.Builder(this);
        builder.setTitle(getString(R.string.Area_NewArea))
                .setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setEditTextHint(R.string.Area_NewArea_Hint)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        MainApplication.getApplication().getMqttManager().
                                publishEncryptedMessage(
                                        MQTTCmdHelper.createSetRoomInfo(
                                                Preference.getPreferences().getCurrentGatewayID(),
                                                1,
                                                msg,
                                                null,
                                                null),
                                        MQTTManager.MODE_GATEWAY_FIRST);
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

    /**
     * 分区排序
     */
    private class SortAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
            implements OnItemCallbackListener {

        private OnStartDragListener mDragStartListener;

        public SortAdapter(OnStartDragListener mDragStartListener) {
            this.mDragStartListener = mDragStartListener;
        }

        public void update(List<RoomInfo> list) {
            areaList = list;
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
            View itemView = layoutInflater.inflate(R.layout.list_item_room_sort, parent, false);
            SortHolder holder = new SortHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            if (holder instanceof SortHolder) {
                ((SortHolder) holder).sort.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                            mDragStartListener.onStartDrag(holder);
                        }
                        return false;
                    }
                });
                ((SortHolder) holder).room.setText(areaList.get(position).getName());
            } else if (holder instanceof FootHolder) {
                ((FootHolder) holder).reset.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
                ((FootHolder) holder).reset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (areaList.size() > 0) {
                            update(areaManager.acquireDefaultSortRoom());
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return areaList == null ? 0 : areaList.size() + 1;
        }

        @Override
        public void onMove(int fromPosition, int toPosition) {
            //滑动事件
            Collections.swap(areaList, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == getItemCount() - 1) {
                return 1;
            }

            return 0;
        }

        @Override
        public void onSwipe(int position) {

        }

        class SortHolder extends RecyclerView.ViewHolder {
            private TextView room;
            private View sort;

            SortHolder(View itemView) {
                super(itemView);
                room = (TextView) itemView.findViewById(R.id.room);
                sort = itemView.findViewById(R.id.drag_handle);
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
