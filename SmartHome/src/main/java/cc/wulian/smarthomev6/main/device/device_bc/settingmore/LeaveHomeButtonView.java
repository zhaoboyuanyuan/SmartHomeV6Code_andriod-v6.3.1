package cc.wulian.smarthomev6.main.device.device_bc.settingmore;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.MoreConfig;
import cc.wulian.smarthomev6.entity.SceneInfo;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.IDeviceMore;
import cc.wulian.smarthomev6.main.home.scene.SceneManager;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.SceneBindingBean;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GetSceneBindingEvent;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by Veev on 2017/6/6
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    LeaveHomeButtonView
 */

public class LeaveHomeButtonView extends RelativeLayout implements IDeviceMore {
    private static String TAG=LeaveHomeButtonView.class.getSimpleName();
    private TextView item_device_more_rename_name, left_rename;
    public LeaveHomeButtonView(Context context) {
        super(context);
        initView(context);
    }
    private String deviceID;
    private String gwID;
    private void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_device_more_custom_leave_home_btn, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        item_device_more_rename_name= (TextView) rootView.findViewById(R.id.item_device_more_rename_name);
        left_rename= (TextView) rootView.findViewById(R.id.left_rename);
        setOnClickListener(onClickListener);
    }

    private Device mDevice;

    @Override
    public void onBindView(MoreConfig.ItemBean bean) {
        EventBus.getDefault().register(this);
        gwID=bean.getValueByKey("gwID");
        deviceID=bean.getValueByKey("devId");
        getSceneBinding();

        mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);

        updateState();
    }

    private void updateState() {
        if (mDevice == null) {
            return;
        }
        if (mDevice.isOnLine()) {
            left_rename.setTextColor(getResources().getColor(R.color.newPrimaryText));
            setEnabled(true);
        } else {
            left_rename.setTextColor(getResources().getColor(R.color.newStateText));
            setEnabled(false);
        }
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }
    private void getSceneBinding(){
        MainApplication.getApplication().getMqttManager().
                publishEncryptedMessage(
                        MQTTCmdHelper.createGetSceneBinding(gwID,deviceID),
                        MQTTManager.MODE_GATEWAY_FIRST);
    }


    private View.OnClickListener onClickListener=new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            SceneManager manager = new SceneManager(LeaveHomeButtonView.this.getContext());
            List<SceneInfo> infos = manager.acquireScene();
            if(infos==null||infos.size()==0){
                String msg=LeaveHomeButtonView.this.getContext().getString(R.string.Device_AwayButton_NoScene);//没有场景可绑定
                ToastUtil.single(msg);
            }else {
                Intent intent=new Intent(LeaveHomeButtonView.this.getContext(), LeaveHomeActivity.class);
                intent.putExtra("devID", deviceID);
                intent.putExtra("gwID",gwID);
                LeaveHomeButtonView.this.getContext().startActivity(intent);
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetSceneBindingEvent(GetSceneBindingEvent event) {
        String devID=event.getDevID();
        if(devID.equals(deviceID)){
            String sceneName=this.getContext().getString(R.string.Device_More_Bind_Scene_Default);
            List<SceneBindingBean> sceneBindingBeanList=event.getSceneBindingList();
            if(sceneBindingBeanList!=null&&sceneBindingBeanList.size()>0){
                String sceneID=sceneBindingBeanList.get(0).sceneID;
                SceneManager manager = new SceneManager(LeaveHomeButtonView.this.getContext());
                List<SceneInfo> infos = manager.acquireScene();
                for(SceneInfo item:infos){
                    if(item.getSceneID().equals(sceneID)){
                        sceneName=item.getName();
                        break;
                    }
                }
            }
            item_device_more_rename_name.setText(sceneName);
        }
//        WLog.d(TAG,event.jsonData);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DeviceReportEvent event) {
        if (event != null) {
            mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
            updateState();
        }
    }


}
