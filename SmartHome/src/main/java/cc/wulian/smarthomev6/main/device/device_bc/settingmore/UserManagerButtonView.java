package cc.wulian.smarthomev6.main.device.device_bc.settingmore;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import cc.wulian.smarthomev6.main.device.DeviceMoreActivity;
import cc.wulian.smarthomev6.main.device.IDeviceMore;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.dialog.CustomProgressDialog;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by yuxx on 2017/6/12.
 * 用户管理的View
 */

public class UserManagerButtonView  extends RelativeLayout implements IDeviceMore {
    String devID = "";
    String gwID="";
    private Device mDevice;
    private Context mContext;
    private WLDialog.Builder builder;
    private TextView left_rename;
    private WLDialog dialog;
    private String appId="";
    private String token="";
    private ProgressDialogManager progressDialogManager = ProgressDialogManager.getDialogManager();
    private boolean isChceking=false;
    public UserManagerButtonView(Context context) {
        super(context);
        this.mContext=context;
        token="";
        initView();
    }

    private void initView(){
        View rootView = LayoutInflater.from(this.mContext).inflate(R.layout.item_device_more_custom_usermanager, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        left_rename = (TextView) rootView.findViewById(R.id.left_rename);
        setOnClickListener(onClickListener);
    }
    @Override
    public void onBindView(MoreConfig.ItemBean bean) {
        gwID=bean.getValueByKey("gwID");
        devID=bean.getValueByKey("devId");
        if(!StringUtil.isNullOrEmpty(devID)){
            mDevice = MainApplication.getApplication().getDeviceCache().get(devID);
            updateState();
        }
        MainApplication mainApplication=(MainApplication) getContext().getApplicationContext();
        if(mainApplication!=null&&mainApplication.getLocalInfo()!=null){
            appId=mainApplication.getLocalInfo().appID;
        }
        EventBus.getDefault().register(this);
    }

    private void updateState() {
        if (mDevice != null && mDevice.isOnLine()) {
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

    View.OnClickListener onClickListener=new OnClickListener() {
        @Override
        public void onClick(View view) {
            checkToken();
        }
    };
    private void checkToken() {
        if(TextUtils.isEmpty(token)){
            showDialog();
        }else {
            gotoUserManagerActivity();
        }
    }

    private boolean isOff;
    private void showDialog() {
        builder = new WLDialog.Builder(mContext);
        isOff = true;


        final View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_bd_password, null);
        final EditText editText = (EditText) view.findViewById(R.id.et_gateway_password);
        final Drawable off = getContext().getResources().getDrawable(R.drawable.icon_eyes_off);
        final Drawable on = getContext().getResources().getDrawable(R.drawable.icon_eyes_on);
        off.setBounds(0, 0, off.getMinimumWidth(), off.getMinimumHeight()); //设置边界
        on.setBounds(0, 0, on.getMinimumWidth(), on.getMinimumHeight()); //设置边界
        editText.setTypeface(Typeface.DEFAULT);
        editText.setTransformationMethod(new PasswordTransformationMethod());

        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Drawable drawable = editText.getCompoundDrawables()[2];
                if (drawable == null) {
                    // don't have end drawable
                    return false;
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // 点击了 输入框中 右边的 眼睛
                    if (event.getX() > editText.getWidth()
                            - editText.getPaddingRight()
                            - drawable.getIntrinsicWidth()){
                        editText.setCompoundDrawablesRelative(null, null, isOff ? on : off, null);
                        isOff = ! isOff;
                        editText.setTransformationMethod(isOff ? PasswordTransformationMethod.getInstance() : HideReturnsTransformationMethod.getInstance());
                    }
                }
                return false;
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable editable = editText.getText();
                int length = editable.toString().trim().length();

                if (length > 6) {
                    int selEndIndex = Selection.getSelectionEnd(editable);
                    String str = editable.toString();
                    //截取新字符串
                    String newStr = str.substring(0, 6);
                    editText.setText(newStr);
                    editable = editText.getText();

                    //新字符串的长度
                    int newLen = editable.length();
                    //旧光标位置超过字符串长度
                    if (selEndIndex > newLen) {
                        selEndIndex = editable.length();
                    }
                    //设置新光标所在的位置
                    Selection.setSelection(editable, selEndIndex);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        String title=mContext.getString(R.string.Device_Vidicon_AdministratorPassword);
        builder.setTitle(title)
                .setContentView(view)
                .setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        String password = editText.getText().toString();
                        if (password.length() == 6 && mDevice != null) {
                            JSONObject object = new JSONObject();
                            try {
                                object.put("appID",appId);
                                object.put("cmd", "501");
                                object.put("gwID", mDevice.gwID);
                                object.put("devID", mDevice.devID);
                                object.put("clusterId", 257);
                                object.put("commandType", 1);
                                object.put("commandId", 0x800B);
                                object.put("endpointNumber", 1);
                                JSONArray array = new JSONArray();
                                array.put(password);
                                object.put("parameter", array);
                                MainApplication.getApplication()
                                        .getMqttManager()
                                        .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
                                isChceking=true;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            progressDialogManager.showDialog("Check", getContext(), getResources().getString(R.string.Handling), new CustomProgressDialog.OnDialogDismissListener() {
                                @Override
                                public void onDismiss(CustomProgressDialog var1, int var2) {
                                    if (var2 != 0) {
                                        isChceking = false;
                                        ToastUtil.single(R.string.Xuanwulakeseries_Widget_Requesttimeout);
                                    }
                                }
                            }, 15000);
                            dialog.dismiss();
                        }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        boolean isRight=event!=null
                        &&event.device!=null
                &&!TextUtils.isEmpty(event.device.devID)
                &&event.device.mode==0;
        if (isRight){
            mDevice = MainApplication.getApplication().getDeviceCache().get(devID);
            updateState();
            JSONObject object = null;
            try {
                object = new JSONObject(event.device.data);
                JSONArray endpoints = object.getJSONArray("endpoints");
                JSONArray clusters = ((JSONObject) endpoints.get(0)).getJSONArray("clusters");
                JSONArray attributes = ((JSONObject) clusters.get(0)).getJSONArray("attributes");
                int clusterId = ((JSONObject) clusters.get(0)).getInt("clusterId");
                if(clusterId==257&&attributes!=null&&attributes.length()>0){
                    JSONObject attributesJson=attributes.getJSONObject(0);
                    int attributeId=attributesJson.getInt("attributeId");
                    String attributeValue=attributesJson.getString("attributeValue");
                    String appID=object.optString("appID");
                    if(attributeId==0x8006){/*管理员密码验证失败*/
                        closeProgressDialog();
                        showPwError(attributeValue);
                    }
                    else if(attributeId==0x8007&&!StringUtil.isNullOrEmpty(attributeValue)){/*管理员密码验证成功*/
                        if(TextUtils.equals(appID,MainApplication.getApplication().getLocalInfo().appID)){
                            String preFlag=attributeValue.substring(0,2);
                            if(TextUtils.equals(preFlag,"01")){
                                closeProgressDialog();
                                token=attributeValue.substring(2);
                                if(mContext!=null && mContext instanceof DeviceMoreActivity){
                                    if(!((DeviceMoreActivity)mContext).isForeground()){
                                        return;//更多界面不在前台则不跳转
                                    }
                                }
                                gotoUserManagerActivity();
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    private void closeProgressDialog(){
        if (isChceking) {
            progressDialogManager.dimissDialog("Check", 0);
        }
    }
    private void gotoUserManagerActivity(){
        Intent intent=new Intent();
        intent.setClass(getContext(),DevBc_userManagerActivity.class);
        intent.putExtra("token",token);
        intent.putExtra("gwID",gwID);
        intent.putExtra("devID",devID);
        getContext().startActivity(intent);
    }
    private void showPwError(String errorCode){
        String errormsg="";
        if(TextUtils.equals(errorCode,"1")){
            errormsg=getContext().getString(R.string.Device_BcRemind_01);//"无线端密码开锁失败!";
        }else if(TextUtils.equals(errorCode,"2")){
            errormsg=getContext().getString(R.string.Device_BcRemind_02);//"开锁密码验证错误";
        }else if(TextUtils.equals(errorCode,"5")){
            errormsg=getContext().getString(R.string.Device_Vidicon_AdministratorPassworderror);
        }else if(TextUtils.equals(errorCode,"6")){
            errormsg=getContext().getString(R.string.Device_BcRemind_03);//"动态密码失败";
        }else{
            errormsg=getContext().getString(R.string.Device_BcRemind_04);//"操作失败";
        }
        ToastUtil.single(errormsg);
    }
}
