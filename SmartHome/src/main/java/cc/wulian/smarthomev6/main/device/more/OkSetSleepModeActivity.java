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
 * 作者: luzx
 * 时间: 2018/5/8
 * 描述: 海信空调睡眠模式
 */

public class OkSetSleepModeActivity extends BaseTitleActivity {
    private static final String SET_SLEEP_MODE_VALUE = "set_sleep_mode_value";
    public static String VALUE = "value";
    public static String DEVICE_ID = "device_id";
    private String deviceId;
    private int length = 5;
    private int checkValue = 0;
    private Device device;

    public static void start(Context context, String deviceId) {
        Intent intent = new Intent(context, OkSetSleepModeActivity.class);
        intent.putExtra(DEVICE_ID, deviceId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
//        String value = intent.getStringExtra(VALUE);
//        if(!TextUtils.isEmpty(value)){
//            try {
//                checkValue = Integer.valueOf(value);
//            }catch (NumberFormatException e){
//                e.printStackTrace();
//            }
//        }
        deviceId = intent.getStringExtra(DEVICE_ID);
        device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        EndpointParser.parse(device, new EndpointParser.ParserCallback() {
            @Override
            public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                if (cluster.clusterId == 0x0201) {
                    if(attribute.attributeId == 0x8102){
                        try {
                            int value = Integer.valueOf(attribute.attributeValue);
                            if (value == 0) {
                                checkValue = 4;
                            } else {
                                checkValue = value - 1;
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        setContentView(R.layout.activity_set_overload_protection, true);
        EventBus.getDefault().register(this);

    }

    @Override
    protected void initTitle() {
        setToolBarTitle(getString(R.string.device_Ok_SleepMod));
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
                JSONArray ja = new JSONArray();
                String sleepSend = "";
                if(checkValue == 4){
                    sleepSend = "01020";
                    ja.put(sleepSend);//睡眠模式关
                }else{
                    sleepSend = "0102" + (checkValue + 1);
                    ja.put(sleepSend + ",01012,010B0");//低风风速 快速冷热关
                }
                sendCmd(0x0202, ja);
            }
        });
    }

    private void sendCmd(int commandId, JSONArray parameter) {
        ProgressDialogManager.getDialogManager().showDialog(SET_SLEEP_MODE_VALUE, OkSetSleepModeActivity.this, null, null, 10000);
        if (device.isOnLine()) {
            JSONObject object = new JSONObject();
            try {
                object.put("cmd", "501");
                object.put("gwID", device.gwID);
                object.put("devID", device.devID);
                object.put("commandType", 1);
                object.put("clusterId", 0x0201);
                object.put("commandId", commandId);
                if (parameter != null) {
                    object.put("parameter", parameter);
                }
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
                if (event.device.mode == 3) {
                    finish();
                }else if(event.device.mode == 0){
                    EndpointParser.parse(event.device, new EndpointParser.ParserCallback() {
                        @Override
                        public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                            if (cluster.clusterId == 0x0201) {
                                if (attribute.attributeId == 0x8102) {
//                                    switchState = attribute.attributeValue;
                                    ProgressDialogManager.getDialogManager().dimissDialog(SET_SLEEP_MODE_VALUE, 0);
                                    finish();
                                }
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
                convertView = LayoutInflater.from(OkSetSleepModeActivity.this)
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
            switch (position){
                case 0:
                    viewHolder.value.setText(R.string.Device_Ok_Details_Sleep1);
                    break;
                case 1:
                    viewHolder.value.setText(R.string.Device_Ok_Details_Sleep2);
                    break;
                case 2:
                    viewHolder.value.setText(R.string.Device_Ok_Details_Sleep3);
                    break;
                case 3:
                    viewHolder.value.setText(R.string.Device_Ok_Details_Sleep4);
                    break;
                case 4:
                    viewHolder.value.setText(R.string.Device_Ok_Details_Sleep5);
                    break;
            }
            if(checkValue == position){
                viewHolder.checkIcon.setVisibility(View.VISIBLE);
            }else{
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
