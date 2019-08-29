package cc.wulian.smarthomev6.main.device.device_Bq;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.MoreConfig;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.IDeviceMore;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.utils.StringUtil;

public class BqLockAndRingManagerView extends RelativeLayout implements IDeviceMore {
    private Context context;
    private MoreConfig.ItemBean mItemBean;
    private View rootView;
    private Device mDevice;
    private String deviceID;
    private String url;
    private RelativeLayout viewOneKeyLock;
    private ImageView imgOneKeyLock;

    private ImageView imgDoorbell;
    private LinearLayout layoutDoorbellTime;
    private TextView txtDoorbellTime;
    private ImageView imgDoorbellTime;

    private TextView txtOneKeyLock;
    private TextView txtDoorbell;

    private String oneKeyLockFlag=""; /*一键锁死标记*/
    private boolean doorbellFlag=false;/*门铃免打扰标记 true免打扰开启；false免打扰关闭*/
    private String curDoorbellTime="";
    public BqLockAndRingManagerView(Context context){
        super(context);
        this.context=context;
        initView(context);
    }
    @Override
    public void onBindView(MoreConfig.ItemBean bean) {
        EventBus.getDefault().register(this);
        mItemBean = bean;
        for (MoreConfig.ParamBean p : bean.param) {
            if ("deviceId".equals(p.key)) {
                deviceID = p.value;
                mDevice = MainApplication.getApplication().getDeviceCache().get(p.value);
                updateState();
                continue;
            }
            if ("url".equals(p.key)) {
                url = p.value;
            }
        }
        sendCmdOnkeyLock("2");//获取一键锁死状态
        sendCmdDoorbellArg("2");//获取免扰时间
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }
    private void initView(Context context){
        rootView= LayoutInflater.from(context).inflate(R.layout.sample_bq_lock_and_ring_manager_view,null);
        addView(rootView,new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        viewOneKeyLock= (RelativeLayout) rootView.findViewById(R.id.viewOneKeyLock);
        imgOneKeyLock= (ImageView) rootView.findViewById(R.id.imgOneKeyLock);
        layoutDoorbellTime= (LinearLayout) rootView.findViewById(R.id.layoutDoorbellTime);
        imgDoorbell= (ImageView) rootView.findViewById(R.id.imgDoorbell);
        txtDoorbellTime= (TextView) rootView.findViewById(R.id.txtDoorbellTime);
        imgDoorbellTime= (ImageView) rootView.findViewById(R.id.imgDoorbellTime);
        txtOneKeyLock= (TextView) rootView.findViewById(R.id.txtOneKeyLock);
        txtDoorbell= (TextView) rootView.findViewById(R.id.txtDoorbell);

        imgOneKeyLock.setOnClickListener(onclick_imgOneKeyLock);
        imgDoorbell.setOnClickListener(onclick_imgDoorbell);
        txtDoorbellTime.setOnClickListener(onclick_setDoorbellTime);
        imgDoorbellTime.setOnClickListener(onclick_setDoorbellTime);


    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReportEvent(DeviceReportEvent event){
        if(event==null){
            return;
        }
        boolean isRight=event.device!=null
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
                                    setUiDoorlock(strSuffix);
                                }else if(strPrefix.equals("4")) {/*一键锁死功能*/
                                    setUiOneKeyLock(strSuffix);
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

    /**
     * 根据一键锁死功能的参数设置界面
     * @param strSuffix attributeValue的后缀
     */
    private void setUiOneKeyLock(String strSuffix){
        oneKeyLockFlag=strSuffix;
        if (strSuffix.equals("01")) {/*锁死启动*/
            viewOneKeyLock.setVisibility(View.VISIBLE);
            imgOneKeyLock.setImageResource(R.drawable.icon_on);
        } else if (strSuffix.equals("00")) {/*锁死取消*/
            viewOneKeyLock.setVisibility(View.VISIBLE);
            imgOneKeyLock.setImageResource(R.drawable.icon_off);
        } else if (strSuffix.equals("02")) {/*不支持此功能*/
            viewOneKeyLock.setVisibility(View.GONE);
        }
    }

    /**
     * 根据门铃参数设置界面
     * @param strSuffix
     */
    private void setUiDoorlock(String strSuffix){
        doorbellFlag=strSuffix.length()==8&&!TextUtils.equals(strSuffix,"00000000");
        if(doorbellFlag){
            curDoorbellTime=strSuffix;
            imgDoorbell.setImageResource(R.drawable.icon_on);
            layoutDoorbellTime.setVisibility(View.VISIBLE);
            String startHH=strSuffix.substring(0,2);
            String startMM=strSuffix.substring(2,4);
            String endhh=strSuffix.substring(4,6);
            String endmm=strSuffix.substring(6,8);
            //从00:00至23:59
            String strDisplay=this.context.getString(R.string.mine_Setting_pushtime_from)+" "+startHH+":"+startMM+" "+this.context.getString(R.string.mine_Setting_pushtime_to)+" "+endhh+":"+endmm;
            txtDoorbellTime.setText(strDisplay);
        }else{
            imgDoorbell.setImageResource(R.drawable.icon_off);
            layoutDoorbellTime.setVisibility(View.GONE);
        }
    }
    View.OnClickListener onclick_imgOneKeyLock=new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if (!StringUtil.isNullOrEmpty(oneKeyLockFlag)&&!TextUtils.equals(oneKeyLockFlag,"02")) {
                if(TextUtils.equals(oneKeyLockFlag,"00")){
                    sendCmdOnkeyLock("1");
                }else if(TextUtils.equals(oneKeyLockFlag,"01")){
                    sendCmdOnkeyLock("0");
                }
            }
        }
    };
    View.OnClickListener onclick_imgDoorbell=new View.OnClickListener(){

        @Override
        public void onClick(View v) {

            if (doorbellFlag) {/*若免打扰当前是开启，则关闭它*/
                sendCmdDoorbellArg("100000000");
            } else {/*若免打扰当前是关闭，则开启它*/
                sendCmdDoorbellArg("100002359");
            }
        }
    };
    View.OnClickListener onclick_setDoorbellTime=new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            BqDoorNotDisturbTimeActivity.Start(BqLockAndRingManagerView.this.context,deviceID,curDoorbellTime);
        }
    };
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

    /**
     * 一键锁死功能
     * @param arg 参数
     *            0：设置一键锁死取消；
     *            1：设置一键锁死启用;
     *            2：获取一键锁死状态；
     */
    private void sendCmdOnkeyLock(String arg){
        sendCmd(0x801D,arg);
    }

    /**
     * 门铃参数
     * @param arg 参数
     *           ["1HHMMhhmm"]:设置免扰时间(24小时制，HHMM-hhmm,全0为一直启用)
     *           ["2"]:获取免扰时间
     */
    private void sendCmdDoorbellArg(String arg){
        sendCmd(0x801E,arg);
    }


    private void updateState() {
        if (mDevice.isOnLine()) {
            txtOneKeyLock.setTextColor(getResources().getColor(R.color.newPrimaryText));
            txtDoorbell.setTextColor(getResources().getColor(R.color.newPrimaryText));
            setEnabled(true);
        } else {
            txtOneKeyLock.setTextColor(getResources().getColor(R.color.newStateText));
            txtDoorbell.setTextColor(getResources().getColor(R.color.newStateText));
            setEnabled(false);
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DeviceReportEvent event) {
        if (event != null) {
            mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
            updateState();
        }
    }
}
