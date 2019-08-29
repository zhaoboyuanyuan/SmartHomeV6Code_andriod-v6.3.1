package cc.wulian.smarthomev6.main.device.device_Bq;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.customview.wheel.NumericWheelAdapter;
import cc.wulian.smarthomev6.support.customview.wheel.OnWheelChangedListener;
import cc.wulian.smarthomev6.support.customview.wheel.WheelView;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

public class BqDoorNotDisturbTimeActivity extends BaseTitleActivity {

    private static String SETTINT = "setting";
    private TextView pushTimeStartTextView;
    private LinearLayout itemPushTimeStart;
    private TextView pushTimeEndTextView;
    private LinearLayout itemPushTimeEnd;
    private WheelView timeHour;
    private WheelView timeMin;
    private Device mDevice;
    private String deviceID;
    private OnWheelChangedListener onWheelChangedListener;
    private String time;
    private boolean isSencSaveCmd=false;

    /**
     * 启动门铃免打扰设置
     * @param context
     * @param deviceID 设备ID
     * @param time 时间
     */
    public static void Start(Context context, String deviceID, String time){
        Intent intent=new Intent();
        intent.setClass(context,BqDoorNotDisturbTimeActivity.class);
        intent.putExtra("deviceID",deviceID);
        intent.putExtra("TIME",time);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_bq_door_not_disturb_time, true);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void initTitle() {
        setToolBarTitleAndRightBtn(getString(R.string.Do_not_disturb_time), getString(R.string.Sure));
    }

    @Override
    protected void initView() {
        itemPushTimeEnd = (LinearLayout) findViewById(R.id.item_push_time_end);
        pushTimeEndTextView = (TextView) findViewById(R.id.tv_push_time_end);
        itemPushTimeStart = (LinearLayout) findViewById(R.id.item_push_time_start);
        pushTimeStartTextView = (TextView) findViewById(R.id.tv_push_time_start);
        timeHour = (WheelView) findViewById(R.id.time_hour);
        timeMin = (WheelView) findViewById(R.id.time_min);
    }

    @Override
    protected void initData() {
        if (getIntent() != null) {
            time = getIntent().getStringExtra("TIME");
            deviceID=getIntent().getStringExtra("deviceID");
            mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
        }

        initTimePicker();
        if (TextUtils.isEmpty(time)){
            pushTimeStartTextView.setText("00:00");
            pushTimeEndTextView.setText("23:59");
        }else {
            String startHH=time.substring(0,2);
            String startMM=time.substring(2,4);
            String endhh=time.substring(4,6);
            String endmm=time.substring(6,8);
            pushTimeStartTextView.setText(startHH+":"+startMM);
            pushTimeEndTextView.setText(endhh+":"+endmm);
        }
        resetListener(pushTimeStartTextView);

    }

    @Override
    protected void initListeners() {
        itemPushTimeStart.setOnClickListener(this);
        itemPushTimeEnd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        switch (v.getId()) {
            case R.id.img_left:
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
                onBackPressed();
                break;
            case R.id.btn_right:
                savePushTime();
                break;
            case R.id.item_push_time_start:
                itemPushTimeStart.setBackgroundColor(getResources().getColor(R.color.newLightGreen));
                itemPushTimeEnd.setBackgroundColor(getResources().getColor(R.color.white));

                resetListener(pushTimeStartTextView);
                break;
            case R.id.item_push_time_end:
                itemPushTimeStart.setBackgroundColor(getResources().getColor(R.color.white));
                itemPushTimeEnd.setBackgroundColor(getResources().getColor(R.color.newLightGreen));

                resetListener(pushTimeEndTextView);
                break;
            default:
                break;
        }
    }

    private void initTimePicker(){
        timeHour.setAdapter(new NumericWheelAdapter(0, 23, "%02d"));
        timeMin.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
        Calendar calender = Calendar.getInstance(Locale.getDefault());
        timeHour.setCurrentItem(calender.get(Calendar.HOUR_OF_DAY));
        timeMin.setCurrentItem(calender.get(Calendar.MINUTE));

        timeHour.setCyclic(true);// 可循环滚动
        timeMin.setCyclic(true);
    }

    private void resetListener(final TextView textView){
        if (onWheelChangedListener != null){
            timeHour.removeChangingListener(onWheelChangedListener);
            timeMin.removeChangingListener(onWheelChangedListener);
        }

        String value = textView.getText().toString();
        if (!TextUtils.isEmpty(value)){
            int hour = Integer.valueOf(value.split(":")[0]);
            int min = Integer.valueOf(value.split(":")[1]);
            timeHour.setCurrentItem(hour);
            timeMin.setCurrentItem(min);
        }

        onWheelChangedListener = new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                textView.setText(String.format("%02d:%02d", timeHour.getCurrentItem(), timeMin.getCurrentItem()));
            }
        };

        timeHour.addChangingListener(onWheelChangedListener);
        timeMin.addChangingListener(onWheelChangedListener);
    }

    private void savePushTime(){
        StringBuilder time = new StringBuilder();
        time.append(pushTimeStartTextView.getText().toString());
//        time.append(",");
        time.append(pushTimeEndTextView.getText().toString());
        final String pushTime = "1"+time.toString().replace(":","");
        isSencSaveCmd=true;
        progressDialogManager.showDialog(SETTINT, this, null, null, getResources().getInteger(R.integer.http_timeout));
        sendCmd(0x801E,pushTime);
    }

    /**
     * 发送命令
     * @param commandId 命令类型
     * @param args 参数
     */
    private void sendCmd(int commandId,String args){
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", mDevice.gwID);
            object.put("devID", mDevice.devID);
//            object.put("appID", MainApplication.getApplication().getLocalInfo().appID);
            object.put("clusterId", 0x0101);
            object.put("commandType", 1);
            object.put("commandId", commandId);
            object.put("endpointNumber", 1);
            JSONArray array = new JSONArray();
            array.put(args);
            object.put("parameter", array);
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReportEvent(DeviceReportEvent event){
        if(event==null){
            return;
        }
        boolean isRight=isSencSaveCmd
                &&event.device!=null
                &&!StringUtil.isNullOrEmpty(event.device.devID)
                && TextUtils.equals(event.device.devID,deviceID)
                && !StringUtil.isNullOrEmpty(event.device.data);
        if(isRight){
            dealData(event.device.data);
        }
    }
    private void dealData(String data) {
        try {
            JSONObject object = new JSONObject(data);
            JSONArray endpoints = object.optJSONArray("endpoints");
            if(endpoints.length()>0){
                JSONArray clusters = ((JSONObject) endpoints.get(0)).optJSONArray("clusters");
                if(clusters.length()>0){
                    JSONArray attributes = ((JSONObject) clusters.get(0)).optJSONArray("attributes");
                    if(attributes.length()>0){
                        String attributeValue = ((JSONObject) attributes.get(0)).optString("attributeValue");
                        int attributeId = ((JSONObject) attributes.get(0)).optInt("attributeId");
                        if(attributeId==0x8005){
                            if(!StringUtil.isNullOrEmpty(attributeValue)&&attributeValue.length()>1){
                                String strPrefix=attributeValue.substring(0,1);
                                String strSuffix=attributeValue.substring(1);
                                if(strPrefix.equals("3")){/*门铃参数*/
                                    ToastUtil.show(R.string.Setting_Success);
                                    finish();
                                }
                            }
                        }
                    }

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}