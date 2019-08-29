package cc.wulian.smarthomev6.main.device.more;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.device.Attribute;
import cc.wulian.smarthomev6.support.core.device.Cluster;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;

/**
 * 作者: zbl
 * 时间: 2018/5/31
 * 描述: Aw模式设置
 */

public class AwSetModeActivity extends BaseTitleActivity {
//    private static final String SET_SLEEP_MODE_VALUE = "set_sleep_mode_value";
    public static String VALUE = "value";
    public static String DEVICE_ID = "device_id";
    private String deviceId;
    private int length = 2;
    private int checkValue = 0;
    private Device device;

    public static void start(Context context, String deviceId) {
        Intent intent = new Intent(context, AwSetModeActivity.class);
        intent.putExtra(DEVICE_ID, deviceId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_overload_protection, true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initTitle() {
        setToolBarTitleAndRightBtn(getString(R.string.device_Aw_mode), getString(R.string.Done));
    }

    @Override
    protected void initView() {
        ListView listview = (ListView) findViewById(R.id.listview);
        listview.setAdapter(mBaseAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkValue = position;
                mBaseAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        deviceId = intent.getStringExtra(DEVICE_ID);
        device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        if (device != null) {
            updateShow(device);
        }
    }

    private void sendCmd(int value) {
//        ProgressDialogManager.getDialogManager().showDialog(SET_SLEEP_MODE_VALUE, AwSetModeActivity.this, null, null, 10000);
        if (device.isOnLine()) {
            JSONObject object = new JSONObject();
            try {
                object.put("cmd", "501");
                object.put("gwID", device.gwID);
                object.put("devID", device.devID);
                object.put("commandType", 1);
                object.put("clusterId", 0x0006);
                object.put("commandId", 0x06);
                String valueString = null;
                if (value == 0) {
                    valueString = "0";
                } else {
                    valueString = "1";
                }
                JSONArray parameter = new JSONArray();
                parameter.put(valueString);
                object.put("parameter", parameter);

                MainApplication.getApplication()
                        .getMqttManager()
                        .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null) {
            if (TextUtils.equals(event.device.devID, deviceId)) {
                device = MainApplication.getApplication().getDeviceCache().get(deviceId);
                if (device != null) {
                    updateShow(device);
                }
            }
        }
    }

    private void updateShow(Device device) {
        if (device.mode == 3) {
            finish();
            return;
        }
        EndpointParser.parse(device, new EndpointParser.ParserCallback() {
            @Override
            public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                if (cluster.clusterId == 0x0006) {
                    if (attribute.attributeId == 0x0002) {
                        if (TextUtils.equals("1", attribute.attributeValue)) {
                            checkValue = 1;
                        } else {
                            checkValue = 0;
                        }
                        mBaseAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClickView(View v) {
        if (v.getId() == R.id.btn_right) {
            sendCmd(checkValue);
            finish();
        }
    }

    private BaseAdapter mBaseAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(AwSetModeActivity.this)
                        .inflate(R.layout.item_set_overload_protection_view, null);
                viewHolder.value = (TextView) convertView
                        .findViewById(R.id.value);
                viewHolder.checkIcon = (ImageView) convertView
                        .findViewById(R.id.check_icon);
                viewHolder.line = convertView
                        .findViewById(R.id.line);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            switch (position) {
                case 0:
                    viewHolder.value.setText(R.string.device_Aw_mode_Switch);
                    break;
                case 1:
                    viewHolder.value.setText(R.string.device_Aw_mode_electric_bell);
                    break;
            }
            if (checkValue == position) {
                viewHolder.checkIcon.setVisibility(View.VISIBLE);
            } else {
                viewHolder.checkIcon.setVisibility(View.GONE);
            }
            return convertView;
        }
    };

    class ViewHolder {
        TextView value;
        ImageView checkIcon;
        View line;
    }
}
