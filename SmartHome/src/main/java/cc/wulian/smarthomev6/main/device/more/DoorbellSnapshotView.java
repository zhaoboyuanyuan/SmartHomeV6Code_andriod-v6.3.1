package cc.wulian.smarthomev6.main.device.more;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.MoreConfig;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.IDeviceMore;
import cc.wulian.smarthomev6.support.core.device.Attribute;
import cc.wulian.smarthomev6.support.core.device.Cluster;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.utils.StringUtil;

/**
 * Created by fenaming on 2018/10/12.
 */

public class DoorbellSnapshotView  extends RelativeLayout implements IDeviceMore {
    private Context context;
    private static final String RING_SNAPSHOT_SETTING = "ring_setting";
    private MoreConfig.ItemBean mItemBean;
    private View rootView;
    private Device mDevice;
    private String deviceID;
    private String url;
    String gwID = "";

    private ImageView imgDoorbell;

    private TextView txtDoorbell;

    private boolean doorbellFlag=false;//门铃快照标记 true开启；false关闭
    private String PIR = "";

    public DoorbellSnapshotView(Context context, String deviceID, String gwID) {
        super(context);
        this.context = context;
        this.gwID = gwID;
        this.deviceID = deviceID;
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
        if (mDevice.isOnLine()) {
            ProgressDialogManager.getDialogManager().showDialog(RING_SNAPSHOT_SETTING, context, null, null, getResources().getInteger(R.integer.http_timeout));
        }
        sendCmd(0x8015,"");//获取门铃快照状态
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void initView(Context context){
        rootView= LayoutInflater.from(context).inflate(R.layout.item_device_more_custom_ring_snapshot,null);
        addView(rootView,new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        imgDoorbell= (ImageView) rootView.findViewById(R.id.imgDoorbell);
        txtDoorbell= (TextView) rootView.findViewById(R.id.txtDoorbell);

        imgDoorbell.setOnClickListener(onclick_imgDoorbell);

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
            dealData(event.device);
        }
    }

    private void dealData(Device device) {
        try {
            EndpointParser.parse(device, new EndpointParser.ParserCallback() {
                @Override
                public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                    if (attribute.attributeId == 0x8007){
                        if (attribute.attributeValue.startsWith("04")){
                            String attributeValue = attribute.attributeValue;
                            PIR = attributeValue.substring(6, 8);
                            String dd = attributeValue.substring(8, 10);

                            if (TextUtils.equals(dd, "00")){
                                doorbellFlag = false;
                            }else if (TextUtils.equals(dd, "01")){
                                doorbellFlag = true;
                            }
                            setUiDoorlock();
                        }
                    }if (attribute.attributeId == 0x8008){
                        sendCmd(0x8015,"");//获取门铃快照状态
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据门铃参数设置界面
     */
    private void setUiDoorlock(){
        ProgressDialogManager.getDialogManager().dimissDialog(RING_SNAPSHOT_SETTING, 0);
        if(doorbellFlag){
            imgDoorbell.setImageResource(R.drawable.icon_on);
        }else{
            imgDoorbell.setImageResource(R.drawable.icon_off);
        }
    }

    View.OnClickListener onclick_imgDoorbell=new View.OnClickListener(){

        @Override
        public void onClick(View v) {

            if (mDevice.isOnLine()) {
                ProgressDialogManager.getDialogManager().showDialog(RING_SNAPSHOT_SETTING, context, null, null, getResources().getInteger(R.integer.http_timeout));
            }
            if (doorbellFlag) {/*若当前是开启，则关闭它*/
                if (mDevice.type.equals("Bc")){
                    sendCmd(0x8013,PIR+"00");
                }else if (mDevice.type.equals("Bn")){
                    Integer pir = Integer.parseInt(PIR, 10);
                    sendCmd(0x8013,pir+"0");
                }
            } else {/*若当前是关闭，则开启它*/
                if (mDevice.type.equals("Bc")){
                    sendCmd(0x8013,PIR+"01");
                }else if (mDevice.type.equals("Bn")){
                    Integer pir = Integer.parseInt(PIR, 10);
                    sendCmd(0x8013,pir+"1");
                }
            }
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


    private void updateState() {
        if (mDevice.isOnLine()) {
            txtDoorbell.setTextColor(getResources().getColor(R.color.newPrimaryText));
            setEnabled(true);
        } else {
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