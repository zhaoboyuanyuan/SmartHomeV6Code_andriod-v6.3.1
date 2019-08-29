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
 * 时间: 2017/10/12
 * 描述: 过载保护设置
 */

public class AiSetOverloadProtectionActivity extends BaseTitleActivity {
    private static final String SET_OVERLOAD_PROTECTION_VALUE = "set_overload_protection_value";
    public static String VALUE = "value";
    public static String DEVICE_ID = "device_id";
    private String deviceId;
    private int maxValue = 11;

    private String unit = "kw";

    private int checkValue = 11;
    private Device device;

    public static void start(Context context, String deviceId, String value) {
        Intent intent = new Intent(context, AiSetOverloadProtectionActivity.class);
        intent.putExtra(VALUE, value);
        intent.putExtra(DEVICE_ID, deviceId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String value = intent.getStringExtra(VALUE);
        if(!TextUtils.isEmpty(value)){
            try {
                checkValue = Integer.valueOf(value);
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
        }
        deviceId = intent.getStringExtra(DEVICE_ID);
        device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        setContentView(R.layout.activity_set_overload_protection, true);
        EventBus.getDefault().register(this);

    }

    @Override
    protected void initTitle() {
        setToolBarTitle(getString(R.string.DINswitch_Totalpower_Cleared));
    }

    @Override
    protected void initView() {
        ListView listview = (ListView) findViewById(R.id.listview);
        listview.setAdapter(mBaseAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkValue = maxValue - position;
                mBaseAdapter.notifyDataSetChanged();
                sendCmd(0x8010);
            }
        });
    }

    private void sendCmd(int commandId) {
        ProgressDialogManager.getDialogManager().showDialog(SET_OVERLOAD_PROTECTION_VALUE, AiSetOverloadProtectionActivity.this, null, null, 10000);
        if (device.isOnLine()) {
            JSONObject object = new JSONObject();
            try {
                object.put("cmd", "501");
                object.put("gwID", device.gwID);
                object.put("devID", device.devID);
                object.put("commandType", 1);
                object.put("commandId", commandId);
                JSONArray jSONArray = new JSONArray();
                jSONArray.put((checkValue * 1000) + "");
                object.put("parameter", jSONArray);
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
                            if (cluster.clusterId == 0x0b04) {
                                if (attribute.attributeId == 0x8003) {
//                                    switchState = attribute.attributeValue;
                                    ProgressDialogManager.getDialogManager().dimissDialog(SET_OVERLOAD_PROTECTION_VALUE, 0);
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
            return maxValue;
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
                convertView = LayoutInflater.from(AiSetOverloadProtectionActivity.this)
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
            viewHolder.value.setText((maxValue - position) + unit);
            if(checkValue == maxValue - position){
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
