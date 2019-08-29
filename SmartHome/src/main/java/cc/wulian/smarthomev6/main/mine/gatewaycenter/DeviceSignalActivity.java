package cc.wulian.smarthomev6.main.mine.gatewaycenter;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.device.Attribute;
import cc.wulian.smarthomev6.support.core.device.Cluster;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;

/**
 * Created by syf on 2017/2/16
 */
public class DeviceSignalActivity extends BaseTitleActivity {

    private RecyclerView mRecyclerView;
    private DeviceAdapter mDeviceAdapter;
    private List<Device> mDevices;
    private boolean isSignData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_signal, true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void initTitle() {
        setToolBarTitleAndRightBtn(getString(R.string.Channel_Title_02), getString(R.string.Refresh));
    }

    @Override
    protected void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.device_signal_recycler);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void initData() {
        super.initData();
        mDevices = MainApplication.getApplication().getDeviceCache().getZigBeeDevices();
        if (mDevices != null) {
            getDeviceSignal();
            if (mDeviceAdapter == null) {
                mDeviceAdapter = new DeviceAdapter();
            }
            mRecyclerView.setAdapter(mDeviceAdapter);
        }
    }

    @Override
    public void onClickView(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_right:
                refreshTime();
                getDeviceSignal();
                break;
            default:
                break;
        }
    }


    private void refreshTime() {
        CountDownTimer countDownTimer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                btn_right.setEnabled(false);
                String value = String.valueOf((int) (millisUntilFinished / 1000));
                setToolBarTitleAndRightBtn(getString(R.string.Channel_Title_02), value + "s");
            }

            @Override
            public void onFinish() {
                btn_right.setEnabled(true);
                setToolBarTitleAndRightBtn(getString(R.string.Channel_Title_02), getString(R.string.Refresh));
            }
        };
        countDownTimer.start();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null) {
            if (event.device.mode == 3) {
                // 设备删除
            } else if (event.device.mode == 2) {
                // 设备离线
            } else if (event.device.mode == 1) {
                // 更新上线
            } else if (event.device.mode == 0) {
                isSignData = false;
                EndpointParser.parse(event.device, new EndpointParser.ParserCallback() {
                    @Override
                    public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                        if (attribute.attributeId == 0x8F03) {
                            isSignData = true;
                        } else if (attribute.attributeId == 0x8F04) {
                            isSignData = true;
                        }
                    }
                });
                if (isSignData) {
                    mDeviceAdapter.update(event.device.devID);
                }
            }

        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onGatewayStateChanged(GatewayStateChangedEvent event) {
//        if (event.bean != null && TextUtils.equals(event.bean.gwID, preference.getCurrentGatewayID())) {
//            if ("15".equals(event.bean.cmd)) {
//
//            } else if ("01".equals(event.bean.cmd)) {
//
//            }
//        }
//    }

    private class DeviceAdapter extends RecyclerView.Adapter {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View itemView = layoutInflater.inflate(R.layout.item_device_signal, parent, false);
            return new ItemHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ItemHolder) {
                Device device = mDevices.get(position);
                if (!device.isOnLine()) {
                    ((ItemHolder) holder).deviceName.setTextColor(0xffB2B7BD);
                } else {
                    ((ItemHolder) holder).deviceName.setTextColor(0xff090909);
                }
                ((ItemHolder) holder).deviceIcon.setImageResource(DeviceInfoDictionary.getIconByType(device.type));
                ((ItemHolder) holder).deviceName.setText(DeviceInfoDictionary.getNameByTypeAndName(device.type, device.name));
                String areaName = MainApplication.getApplication().getRoomCache().getRoomName(device.roomID);
                if (TextUtils.isEmpty(areaName)) {
                    areaName = device.roomName;
                }
                ((ItemHolder) holder).deviceRoom.setText(areaName);
                ((ItemHolder) holder).upSignal.setText("");
                ((ItemHolder) holder).upSignal.setTextColor(0xffB2B7BD);
                ((ItemHolder) holder).downSignal.setText("");
                ((ItemHolder) holder).downSignal.setTextColor(0xffB2B7BD);
                ((ItemHolder) holder).upSignalView.setVisibility(View.GONE);
                ((ItemHolder) holder).downSignalView.setVisibility(View.GONE);
                EndpointParser.parse(device, new EndpointParser.ParserCallback() {
                    @Override
                    public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                        if (attribute.attributeId == 0x8F03) {
                            String down = attribute.attributeValue;
                            if (!TextUtils.isEmpty(down)) {
                                int downValue = Integer.parseInt(down, 16);
                                if (downValue > 75) {
                                    ((ItemHolder) holder).downSignal.setTextColor(0xffF64848);
                                }
                                ((ItemHolder) holder).downSignal.setText("-" + downValue);
                                ((ItemHolder) holder).downSignalView.setVisibility(View.VISIBLE);
                            } else {
                                ((ItemHolder) holder).downSignal.setText("");
                            }
                        } else if (attribute.attributeId == 0x8F04) {
                            String up = attribute.attributeValue;
                            if (!TextUtils.isEmpty(up)) {
                                int upValue = Integer.parseInt(up, 16);
                                if (upValue > 75) {
                                    ((ItemHolder) holder).upSignal.setTextColor(0xffF64848);
                                }
                                ((ItemHolder) holder).upSignal.setText("-" + upValue);
                                ((ItemHolder) holder).upSignalView.setVisibility(View.VISIBLE);
                            } else {
                                ((ItemHolder) holder).upSignal.setText("");
                            }
                        }
                    }

                });
            }
        }

        @Override
        public int getItemCount() {
            return mDevices.size();
        }

        public void update(String deviceId) {
            for (int i = 0, l = mDevices.size(); i < l; i++) {
                Device device = mDevices.get(i);
                if (device.devID.equals(deviceId)) {
                    synchronized (this) {
                        notifyItemChanged(i);
                    }
                }
            }
        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            super.onViewRecycled(holder);
        }

        class ItemHolder extends RecyclerView.ViewHolder {

            private ImageView deviceIcon;

            private TextView deviceName;

            private TextView deviceRoom;

            private TextView upSignal;

            private View upSignalView;

            private TextView downSignal;

            private View downSignalView;

            public ItemHolder(View itemView) {
                super(itemView);
                deviceIcon = (ImageView) itemView.findViewById(R.id.device_icon);
                deviceName = (TextView) itemView.findViewById(R.id.device_name);
                deviceRoom = (TextView) itemView.findViewById(R.id.device_room);
                upSignal = (TextView) itemView.findViewById(R.id.device_signal_up_value);
                downSignal = (TextView) itemView.findViewById(R.id.device_signal_down_value);
                upSignalView = itemView.findViewById(R.id.device_signal_up_layout);
                downSignalView = itemView.findViewById(R.id.device_signal_down_layout);
            }
        }
    }

    private void getDeviceSignal() {
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "532");
            object.put("gwID", preference.getCurrentGatewayID());
            object.put("appID", ((MainApplication) getApplication()).getLocalInfo().appID);
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
