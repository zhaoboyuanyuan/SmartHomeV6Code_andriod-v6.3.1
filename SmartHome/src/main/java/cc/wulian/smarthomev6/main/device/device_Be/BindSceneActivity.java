package cc.wulian.smarthomev6.main.device.device_Be;

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

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.SceneInfo;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.DeviceDetailActivity;
import cc.wulian.smarthomev6.main.home.scene.SceneManager;
import cc.wulian.smarthomev6.support.core.device.Attribute;
import cc.wulian.smarthomev6.support.core.device.Cluster;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.SetSceneBindingEvent;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;

/**
 * 作者: luzx
 * 时间: 2017/10/12
 * 描述: 绑定场景
 */

public class BindSceneActivity extends BaseTitleActivity {
    private static final String BIND_SCENE = "bind_scene";
    public static String SCENE_ID = "scene_id";
    public static String DEVICE_ID = "device_id";
    private String deviceId;
    private String sceneID;
    private Device device;
    private List<SceneInfo> scenes;

    public static void start(Context context, String deviceId, String value) {
        Intent intent = new Intent(context, BindSceneActivity.class);
        intent.putExtra(SCENE_ID, value);
        intent.putExtra(DEVICE_ID, deviceId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        sceneID = intent.getStringExtra(SCENE_ID);
        deviceId = intent.getStringExtra(DEVICE_ID);
        device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        setContentView(R.layout.activity_bind_scene, true);
        EventBus.getDefault().register(this);

    }

    @Override
    protected void initTitle() {
        setToolBarTitleAndRightBtn(R.string.Device_More_Bind_Scene, R.string.Done);
    }

    @Override
    protected void initView() {
        SceneManager manager = new SceneManager(this);
        scenes = manager.acquireScene();
        ListView listview = (ListView) findViewById(R.id.listview);
        View noSceneLayout = findViewById(R.id.no_scene_layout);

        listview.setAdapter(mBaseAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    sceneID = "-1";
                }else{
                    ViewHolder viewHolder = (ViewHolder) view.getTag();
                    Object tag = viewHolder.sceneName.getTag();
                    sceneID = tag == null ? "" : tag.toString();
                }
                mBaseAdapter.notifyDataSetChanged();
            }
        });
        View btnRight = findViewById(R.id.btn_right);
        if(scenes == null || scenes.size() == 0){
            listview.setVisibility(View.GONE);
            btnRight.setVisibility(View.GONE);
            noSceneLayout.setVisibility(View.VISIBLE);
        }else{
            listview.setVisibility(View.VISIBLE);
            btnRight.setVisibility(View.VISIBLE);
            noSceneLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()){
            case R.id.btn_right:
                if("-1".equals(sceneID)){
                    try {
                        JSONArray data = new JSONArray();
                        JSONObject jo = new JSONObject();
                        jo.put("endpointNumber", "2");
                        data.put(jo);
                        sendCmd(3, data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else if(!TextUtils.isEmpty(sceneID)){
                    try {
                        JSONArray data = new JSONArray();
                        JSONObject jo = new JSONObject();
                        jo.put("endpointNumber", "2");
                        jo.put("sceneID", sceneID);
                        data.put(jo);
                        sendCmd(1, data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void sendCmd(int mode, JSONArray data) {
        ProgressDialogManager.getDialogManager().showDialog(BIND_SCENE, BindSceneActivity.this,
                getResources().getString(R.string.Scene_List_Bounding), null, 20000);
        if (device.isOnLine()) {
            JSONObject object = new JSONObject();
            try {
                object.put("cmd", "513");
                object.put("gwID", device.gwID);
                object.put("devID", device.devID);
                object.put("appID", MainApplication.getApplication().getLocalInfo().appID);
                object.put("mode", mode);
                object.put("data", data);
                MainApplication.getApplication()
                        .getMqttManager()
                        .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSceneBindingReport(SetSceneBindingEvent event) {
        if (event.jsonData != null) {
            try {
                JSONObject jsonData = new JSONObject(event.jsonData);
                String devID = jsonData.optString("devID");
                if(devID.equals(device.devID)){
                    JSONArray data = jsonData.optJSONArray("data");
                    JSONObject jo = data.optJSONObject(0);
                    String sceneID = jo.optString("sceneID");
                    if(sceneID.equals(sceneID)){
                        ProgressDialogManager.getDialogManager().dimissDialog(BIND_SCENE, 0);
                        finish();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
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
            return scenes.size() + 1;
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
                convertView = LayoutInflater.from(BindSceneActivity.this)
                        .inflate(R.layout.item_bind_scene_view, null);
                viewHolder.sceneIcon = (ImageView) convertView
                        .findViewById(R.id.scene_icon);
                viewHolder.sceneName = (TextView) convertView
                        .findViewById(R.id.scene_name);
                viewHolder.checkIcon = (ImageView) convertView
                        .findViewById(R.id.check_icon);
                viewHolder.line = convertView
                        .findViewById(R.id.line);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if(position == 0){
                viewHolder.sceneIcon.setVisibility(View.GONE);
                viewHolder.sceneName.setText(R.string.Scene_List_NotBound);
                viewHolder.sceneName.setTag("");
                if("-1".equals(sceneID)){
                    viewHolder.checkIcon.setVisibility(View.VISIBLE);
                }else{
                    viewHolder.checkIcon.setVisibility(View.GONE);
                }
            }else{
                int iconResId = getResources().getIdentifier("scene_normal_" + scenes.get(position - 1).getIcon(), "drawable", getPackageName());
                viewHolder.sceneIcon.setVisibility(View.VISIBLE);
                viewHolder.sceneIcon.setImageResource(iconResId);
                viewHolder.sceneName.setText(scenes.get(position - 1).getName());
                viewHolder.sceneName.setTag(scenes.get(position - 1).getSceneID());
                if(!TextUtils.isEmpty(sceneID)){
                    if(sceneID.equals(scenes.get(position - 1).getSceneID())){
                        viewHolder.checkIcon.setVisibility(View.VISIBLE);
                    }else{
                        viewHolder.checkIcon.setVisibility(View.GONE);
                    }
                }else{
                    if(position == 1){
                        viewHolder.checkIcon.setVisibility(View.VISIBLE);
                        sceneID = scenes.get(position - 1).getSceneID();
                    }else{
                        viewHolder.checkIcon.setVisibility(View.GONE);
                    }
                }
            }
            return convertView;
        }
    };

    class ViewHolder {
        ImageView sceneIcon;
        TextView sceneName;
        ImageView checkIcon;
        View line;
    }
}
