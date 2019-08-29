package cc.wulian.smarthomev6.main.device;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.WLog;

public class AreaDetailActivity extends BaseTitleActivity {

    private String TAG = this.getClass().getSimpleName();

    private static final String BIND = "BIND";
    private static final String UB_BIND = "UB_BIND";

    private static final String AreaDetail_NAME = "name";
    private static final String AreaDetail_ID = "id";

    private String areaName, areaId;

    private ArrayList<Device> listNone, listArea;

    private TextView textAreaName;
    private WLDialog.Builder builder;
    private WLDialog dialog;

    private RecyclerView recyclerArea, recyclerNone;
    private AreaListAdapter areaListAdapter;
    private AreaNoneListAdapter noneListAdapter;

    public static void start(Context context, String areaName, String areaId) {
        Intent in = new Intent(context, AreaDetailActivity.class);
        in.putExtra(AreaDetail_NAME, areaName);
        in.putExtra(AreaDetail_ID, areaId);
        context.startActivity(in);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_detail, true);

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

        Intent intent = getIntent();
        areaName = intent.getStringExtra(AreaDetail_NAME);
        areaId = intent.getStringExtra(AreaDetail_ID);
        setToolBarTitle(areaName + getString(R.string.Area_Manager));
    }

    @Override
    protected void initView() {
        textAreaName = (TextView) findViewById(R.id.area_detail_text_AreaName);
        recyclerArea = (RecyclerView) findViewById(R.id.area_detail_recycler_Area);
        recyclerNone = (RecyclerView) findViewById(R.id.area_detail_recycler_NoneArea);

        textAreaName.setText(areaName);

        recyclerArea.setNestedScrollingEnabled(false);
        recyclerNone.setNestedScrollingEnabled(false);
    }

    @Override
    protected void initData() {
        getAreaData();
    }

    private void getAreaData() {
        listNone = new ArrayList<>();
        listArea = new ArrayList<>();

        listNone.addAll(MainApplication.getApplication().getDeviceCache().getZigBeeDevices());

        for (Device device : (ArrayList<Device>) listNone.clone()) {
            WLog.i(TAG, "initData: " + device.roomID);
            if (device.roomID != null) {
                if ("".equals(device.roomID)) {
                    continue;
                }

                listNone.remove(device);
                if (TextUtils.equals(device.roomID, areaId)) {
                    listArea.add(device);
                }
            }
        }
    }

    @Override
    protected void initListeners() {
        areaListAdapter = new AreaListAdapter(new ArrayList<>(listArea));
        noneListAdapter = new AreaNoneListAdapter(new ArrayList<>(listNone));

        recyclerNone.setAdapter(noneListAdapter);
        recyclerArea.setAdapter(areaListAdapter);
    }

    /**
     * 获取新增的子设备
     * <p>
     * 如果 adapter 里面的数据，
     * 如果在 listArea 里面不存在相应的数据，
     * 则说明是新增的
     */
    private List<Device> getInsertDevice() {
        List<Device> list = new ArrayList<>();
        // 如果原始数据为空，则说明新的数据全部都是新增的
        if (listArea.isEmpty()) {
            return areaListAdapter.getList();
        }
        for (Device device : areaListAdapter.getList()) {
            int len = listArea.size();
            int i = 0;
            INNER:
            for (; i < len; i++) {
                if (TextUtils.equals(device.devID, listArea.get(i).devID)) {
                    break INNER;
                }
            }

            if (i < len) {
                list.add(device);
            }
        }
        return list;
    }

    /**
     * 获取删除的子设备
     * <p>
     * 遍历 listArea 里面的数据
     * 如果 adapter 里面不存在相应的数据
     * 则说明已经删除
     */
    private List<Device> getRemoveDevice() {
        List<Device> list = new ArrayList<>();
        List<Device> adapterList = areaListAdapter.getList();
        // 如果新增数据为空，则说明原始数据全部删除了
        if (adapterList.isEmpty()) {
            return listArea;
        }
        for (Device device : listArea) {
            int i = 0, len = adapterList.size();
            INNER:
            for (; i < len; i++) {
                if (TextUtils.equals(device.devID, adapterList.get(i).devID)) {
                    break INNER;
                }
            }

            if (i < len) {
                list.add(device);
            }
        }
        return list;
    }

    private class AreaListAdapter extends RecyclerView.Adapter<Holder> {

        List<Device> list;

        public AreaListAdapter(List<Device> list) {
            this.list = list;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            Holder holder = new Holder(
                    layoutInflater.inflate(R.layout.item_area_detail, parent, false));

            return holder;
        }

        @Override
        public void onBindViewHolder(Holder holder, final int position) {
            Device device = list.get(position);
            holder.textName.setText(DeviceInfoDictionary.getNameByTypeAndName(device.type, device.name));
            holder.imageDevice.setImageResource(DeviceInfoDictionary.getIconByType(device.type));
            holder.imageIcon.setImageResource(R.drawable.sort_delete);

            holder.imageIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    showDeleteDeviceDialog(position);
                    delete(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void add(Device device) {
            list.add(device);
            notifyItemInserted(list.size());
        }

        private void delete(final int position) {
            Device device = list.get(position);
            list.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, list.size());
            noneListAdapter.add(device);

            unBindArea(device);
        }

        public List<Device> getList() {
            return list;
        }

        private void showDeleteDeviceDialog(final int position) {
            builder = new WLDialog.Builder(AreaDetailActivity.this);
            builder.setTitle(getString(R.string.Hint))
                    .setCancelOnTouchOutSide(false)
                    .setDismissAfterDone(true)
                    .setMessage(getString(R.string.Device_DeleteFrom_Group))
                    .setPositiveButton(getResources().getString(R.string.Sure))
                    .setNegativeButton(getResources().getString(R.string.Cancel))
                    .setListener(new WLDialog.MessageListener() {
                        @Override
                        public void onClickPositive(View view, String msg) {
                            delete(position);
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

    }

    private class AreaNoneListAdapter extends RecyclerView.Adapter<Holder> {

        List<Device> list;

        public AreaNoneListAdapter(List<Device> list) {
            this.list = list;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            Holder holder = new Holder(
                    layoutInflater.inflate(R.layout.item_area_detail, parent, false));

            return holder;
        }

        @Override
        public void onBindViewHolder(Holder holder, final int position) {
            Device device = list.get(position);
            holder.textName.setText(DeviceInfoDictionary.getNameByTypeAndName(device.type, device.name));
            holder.imageDevice.setImageResource(DeviceInfoDictionary.getIconByType(device.type));
            holder.imageIcon.setImageResource(R.drawable.sort_add);

            holder.imageIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    showAddDeviceDialog(position);
                    delete(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void delete(final int position) {
            Device device = list.get(position);
            list.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, list.size());

            areaListAdapter.add(device);

            bindArea(device);
        }

        public void add(Device device) {
            list.add(device);
            notifyItemInserted(list.size());
        }

        public List<Device> getList() {
            return list;
        }

        private void showAddDeviceDialog(final int position) {
            builder = new WLDialog.Builder(AreaDetailActivity.this);
            builder.setTitle(getString(R.string.Hint))
                    .setCancelOnTouchOutSide(false)
                    .setDismissAfterDone(true)
                    .setMessage(getString(R.string.Device_AddTo_Group))
                    .setPositiveButton(getResources().getString(R.string.Sure))
                    .setNegativeButton(getResources().getString(R.string.Cancel))
                    .setListener(new WLDialog.MessageListener() {
                        @Override
                        public void onClickPositive(View view, String msg) {
                            delete(position);
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
    }

    private void bindArea(Device device) {
        WLog.i(TAG, "bindArea: 00000000000");
        progressDialogManager.showDialog(BIND, AreaDetailActivity.this, null, null, getResources().getInteger(R.integer.http_timeout));
        MainApplication.getApplication().getMqttManager().
                publishEncryptedMessage(
                        MQTTCmdHelper.createSetDeviceInfo(
                                Preference.getPreferences().getCurrentGatewayID(),
                                device.devID,
                                2,
                                null,
                                areaId),
                        MQTTManager.MODE_GATEWAY_FIRST);
    }

    private void unBindArea(Device device) {
        WLog.i(TAG, "bindArea: 00000000000");
        progressDialogManager.showDialog(BIND, AreaDetailActivity.this, null, null, getResources().getInteger(R.integer.http_timeout));
        MainApplication.getApplication().getMqttManager().
                publishEncryptedMessage(
                        MQTTCmdHelper.createSetDeviceInfo(
                                Preference.getPreferences().getCurrentGatewayID(),
                                device.devID,
                                2,
                                null,
                                ""),
                        MQTTManager.MODE_GATEWAY_FIRST);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DeviceInfoChangedEvent event) {
        WLog.i(TAG, "onEvent: 11111111111");
        progressDialogManager.dimissDialog(BIND, 0);
        getAreaData();

    }

    class Holder extends RecyclerView.ViewHolder {
        private TextView textName;
        private ImageView imageIcon;
        private ImageView imageDevice;

        public Holder(View itemView) {
            super(itemView);
            textName = (TextView) itemView.findViewById(R.id.item_area_detail_name);
            imageIcon = (ImageView) itemView.findViewById(R.id.item_area_detail_add);
            imageDevice = (ImageView) itemView.findViewById(R.id.item_area_detail_image_device);
        }
    }
}
