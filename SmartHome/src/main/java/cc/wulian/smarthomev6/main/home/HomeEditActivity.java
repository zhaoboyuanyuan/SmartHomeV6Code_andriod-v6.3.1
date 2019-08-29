package cc.wulian.smarthomev6.main.home;

import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.home.widget.HomeItemBean;
import cc.wulian.smarthomev6.main.home.widget.HomeWidgetManager;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.event.AccountEvent;
import cc.wulian.smarthomev6.support.tools.dialog.MessageListenerAdapter;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.dragsort.OnItemCallbackListener;
import cc.wulian.smarthomev6.support.tools.dragsort.OnStartDragListener;
import cc.wulian.smarthomev6.support.tools.dragsort.SortTouchCallback;

import static cc.wulian.smarthomev6.main.home.widget.HomeWidgetManager.Banner;
import static cc.wulian.smarthomev6.main.home.widget.HomeWidgetManager.Environment_det;
import static cc.wulian.smarthomev6.main.home.widget.HomeWidgetManager.Scene;
import static cc.wulian.smarthomev6.main.home.widget.HomeWidgetManager.Security_sensor;
import static cc.wulian.smarthomev6.main.home.widget.HomeWidgetManager.Video;

/**
 * 首页widget编辑顺序、同步
 *
 * 一共两个列表
 * 上面列表带有拖拽功能 RecyclerView自带
 */
public class HomeEditActivity extends BaseTitleActivity {

    private final static int MAX_COUNT = 20;

    private RecyclerView mRecyclerSort;
    private RecyclerView mRecyclerBottom;

    private SortAdapter sortAdapter;
    private ItemTouchHelper helper;
    private BottomAdapter bottomAdapter;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setContentView(R.layout.activity_home_edit, true);

        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAccountEvent(AccountEvent event) {
        // 排序  排着排着  账号被顶了 就不排了
        if (event.action == AccountEvent.ACTION_LOGOUT) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Home_Edit_Title));
        setToolBarLeftAndRightText(getString(R.string.Cancel), getString(R.string.Done));
        mTitle.setVisibility(View.VISIBLE);
    }

    protected void initView() {
        mRecyclerSort = (RecyclerView) findViewById(R.id.home_edit_recycler_sort);
        mRecyclerBottom = (RecyclerView) findViewById(R.id.home_edit_add_recyclerview);

        mRecyclerSort.setNestedScrollingEnabled(false);
        mRecyclerBottom.setNestedScrollingEnabled(false);
    }

    protected void initData() {

        List<HomeItemBean> sortList = HomeWidgetManager.getCurrentWidgetWithAdd(true);
        List<HomeItemBean> bottomList = HomeWidgetManager.getCurrentWidgetWithAdd(false);
        sortAdapter = new SortAdapter(sortList, new OnStartDragListener() {
            @Override
            public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
                helper.startDrag(viewHolder);
            }
        });
        mRecyclerSort.setAdapter(sortAdapter);

        SortTouchCallback callback = new SortTouchCallback(sortAdapter);
        helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mRecyclerSort);

        bottomAdapter = new BottomAdapter(bottomList);
        mRecyclerBottom.setAdapter(bottomAdapter);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_left:
                // 返回
                finish();
                break;
            case R.id.btn_right:
                // 同步
                sort();
                this.finish();
            default:
                break;
        }
    }

    private void sort() {
        List<HomeItemBean> list = sortAdapter.getList();
        if (list == null){
            MainApplication.getApplication().WidgetSecurityPosition = 0;
            MainApplication.getApplication().WidgetEnvironmentPosition = 0;
        }else {
            boolean hasEnvironment = false;
            boolean hasSecurity = false;
            for (HomeItemBean item:list) {
                if (TextUtils.equals("environment_det", item.getType())){
                    hasEnvironment = true;
                }
                if (TextUtils.equals("security_sensor", item.getType())){
                    hasSecurity = true;
                }
            }
            if (!hasEnvironment){
                MainApplication.getApplication().WidgetEnvironmentPosition = 0;
            }
            if (!hasSecurity){
                MainApplication.getApplication().WidgetSecurityPosition = 0;
            }
        }
        HomeWidgetManager.sort(sortAdapter.getList(), bottomAdapter.getList());
    }

    /**
     * 要展示部分
     */
    private class SortAdapter extends RecyclerView.Adapter<SortAdapter.SortHolder>
            implements OnItemCallbackListener {

        private List<HomeItemBean> list;
        private OnStartDragListener mDragStartListener;

        public List<HomeItemBean> getList() {
            return list;
        }

        public SortAdapter(List<HomeItemBean> list, OnStartDragListener mDragStartListener) {
            this.list = list;
            this.mDragStartListener = mDragStartListener;
        }

        @Override
        public SortHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View itemView = layoutInflater.inflate(R.layout.list_item_click_remove, parent, false);
            SortHolder holder = new SortHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(final SortHolder holder, final int position) {
            String name;
            switch (list.get(position).getType()) {
                case Banner:
                    name = getString(R.string.Home_Module_Adv);
                    break;
                case Scene:
                    name = getString(R.string.Home_Module_Scene);
                    break;
                case Video:
                    name = getString(R.string.Home_Module_Video);
                    break;
                case Security_sensor:
                    name = getString(R.string.Home_Widget_AlarmUniversalSensor);
                    break;
                case Environment_det:
                    name = getString(R.string.Environmental_Monitoring);
                    break;
                default:
                    name = DeviceInfoDictionary.getNameByTypeAndName(list.get(position).getType(), list.get(position).getName());
            }

            holder.name.setText(name);
            holder.sort.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                        mDragStartListener.onStartDrag(holder);
                    }
                    return false;
                }
            });
            holder.remove.setTag(list.get(position));
            holder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 移动后, position也会移动
                    // 比如从第五移到了第三
                    // 移动后的位置为 0 1 4 2 3
                    // 当点击第四个位置时, 得到的position 是 2, 并不是3
                    // 所以这里使用 tag来解决这个问题
                    remove((HomeItemBean) v.getTag());
                }
            });
            if(!TextUtils.isEmpty(list.get(position).getWidgetID())) {
                Device device = MainApplication.getApplication().getDeviceCache().get(list.get(position).getWidgetID());
                if (device != null && device.isZigbee()
//                        && device.roomID != null//去除非空判断，空返回未分区
                        ) {
                    holder.room.setText("[" + MainApplication.getApplication().getRoomCache().getRoomName(device.roomID) + "]");
                } else {
                    holder.room.setText("");
                }
            }
        }

        /**
         * 从当前列表移除
         * 不显示这个widget
         */
        private void remove(HomeItemBean bean) {
            bottomAdapter.add(bean);
            list.remove(bean);
            notifyDataSetChanged();
        }

        /**
         * 添加至 当前列表
         * 显示这个widget
         */
        public void add(HomeItemBean bean) {
            list.add(bean);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        /**
         * 是否达到最大数量
         */
        public boolean isMax() {
            return list.size() >= MAX_COUNT;
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
            private TextView room;
            private View sort;
            private View remove;

            SortHolder(View itemView) {
                super(itemView);

                name = (TextView) itemView.findViewById(R.id.text);
                room = (TextView) itemView.findViewById(R.id.room);
                remove = itemView.findViewById(R.id.click_remove);
                sort = itemView.findViewById(R.id.drag_handle);
            }
        }
    }

    /**
     * 不展示部分
     */
    private class BottomAdapter extends RecyclerView.Adapter<BottomAdapter.BottomHolder> {

        private List<HomeItemBean> list;

        public List<HomeItemBean> getList() {
            return list;
        }

        public BottomAdapter(List<HomeItemBean> list) {
            this.list = list;
        }

        @Override
        public BottomHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View itemView = layoutInflater.inflate(R.layout.home_edit_add_item, parent, false);
            BottomHolder holder = new BottomHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(BottomHolder holder, final int position) {
            String name;
            switch (list.get(position).getType()) {
                case Banner:
                    name = getString(R.string.Home_Module_Adv);
                    break;
                case Scene:
                    name = getString(R.string.Home_Module_Scene);
                    break;
                case Video:
                    name = getString(R.string.Home_Module_Video);
                    break;
                case Security_sensor:
                    name = getString(R.string.Home_Widget_AlarmUniversalSensor);
                    break;
                case Environment_det:
                    name = getString(R.string.Environmental_Monitoring);
                    break;
                default:
                    name = DeviceInfoDictionary.getNameByTypeAndName(list.get(position).getType(), list.get(position).getName());
            }

            holder.name.setText(name);
            holder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remove(position);
                }
            });
            if(!TextUtils.isEmpty(list.get(position).getWidgetID())) {
                Device device = MainApplication.getApplication().getDeviceCache().get(list.get(position).getWidgetID());
                if (device != null && device.isZigbee()
//                        && device.roomID != null//去除非空判断，空返回未分区
                        ) {
                    holder.room.setText("[" + MainApplication.getApplication().getRoomCache().getRoomName(device.roomID) + "]");
                } else {
                    holder.room.setText("");
                }
            }
        }

        /**
         * 从当前列表移除
         * 意味着：显示这个widget
         */
        private void remove(int position) {
            if (sortAdapter.isMax()) {
                showMax();
            } else {
                HomeItemBean bean = list.get(position);
                sortAdapter.add(bean);
                list.remove(position);
                notifyDataSetChanged();

            }
        }

        /**
         * 添加至当前列表
         * 意味着：不显示这个widget
         */
        public void add(HomeItemBean bean) {
            list.add(bean);
            Collections.sort(list, new Comparator<HomeItemBean>() {
                @Override
                public int compare(HomeItemBean o1, HomeItemBean o2) {
                    if (o1.getId() > o2.getId()) {
                        return -1;
                    } else if (o1.getId() < o2.getId()) {
                        return 1;
                    }
                    return 0;
                }
            });
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class BottomHolder extends RecyclerView.ViewHolder {

            private TextView name;
            private TextView room;
            private View add;

            BottomHolder(View itemView) {
                super(itemView);

                name = (TextView) itemView.findViewById(R.id.item_name);
                room = (TextView) itemView.findViewById(R.id.room);
                add = itemView.findViewById(R.id.item_add);
            }
        }
    }

    private void showMax() {
        WLDialog.Builder builder = new WLDialog.Builder(HomeEditActivity.this);
        builder.setMessage(getString(R.string.Home_Widget_Edit_Alarm))
                .setPositiveButton(getResources().getString(R.string.Sure))
                .setListener(new MessageListenerAdapter() {

                });
        WLDialog dialog = builder.create();
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.toptobottom_out);
    }
}
