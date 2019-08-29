package cc.wulian.smarthomev6.main.device.device_ow;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.StringRes;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import cc.wulian.smarthomev6.main.device.IDeviceMore;
import cc.wulian.smarthomev6.main.h5.NativeStorage;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.dialog.CustomProgressDialog;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by Veev on 2017/6/8
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    OpAccountManageView
 */

public class OwAccountManageView extends RelativeLayout implements IDeviceMore {

    private Context context;

    private TextView textName;
    private View rootView;

    private MoreConfig.ItemBean mItemBean;

    private Device mDevice;
    private String deviceID;
    private String mToken;
    private String url;

    private WLDialog.Builder builder;
    private WLDialog dialog;
    private String returnAppID;

    private ProgressDialogManager progressDialogManager = ProgressDialogManager.getDialogManager();
    private boolean isChceking = false;

    public OwAccountManageView(Context context) {
        super(context);

        this.context = context;
        initView(context);
    }

    @Override
    public void onBindView(MoreConfig.ItemBean bean) {
        EventBus.getDefault().register(this);
        mItemBean = bean;

        String name = bean.name;
        textName.setText(name);

        for (MoreConfig.ParamBean p : bean.param) {
            if ("deviceID".equals(p.key)) {
                deviceID = p.value;
                mDevice = MainApplication.getApplication().getDeviceCache().get(p.value);
                continue;
            }

            if ("url".equals(p.key)) {
                url = p.value;
            }
        }

        updateMode();
    }

    private void updateMode() {
        if (mItemBean.offLineDisable && mDevice != null && !mDevice.isOnLine()) {
            rootView.setEnabled(false);
            textName.setAlpha(0.54f);
        } else {
            rootView.setEnabled(true);
            textName.setAlpha(1f);
        }
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void initView(Context context) {
        rootView = LayoutInflater.from(context).inflate(R.layout.item_device_more_custom_ow_account_manage, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        textName = (TextView) rootView.findViewById(R.id.op_name);

        rootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                checkToken();
            }
        });
    }

    private void checkToken() {
        if (mToken != null) {
            jumpAccountManage();
        } else {
            getToken();
        }
    }

    private void jumpAccountManage() {
        Intent intent = new Intent(context, LockOwActivity.class);
        intent.putExtra("deviceID", deviceID);
        intent.putExtra("url", url);
        context.startActivity(intent);
        EventBus.getDefault().unregister(this);
    }

    private void getToken() {
        showDialog();
    }

    private void showDialog() {
        builder = new WLDialog.Builder(context);

        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_op_password, null);
        final EditText editText = (EditText) view.findViewById(R.id.et_gateway_password);
        editText.setTypeface(Typeface.DEFAULT);
        editText.setTransformationMethod(new PasswordTransformationMethod());
        editText.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Drawable drawable = editText.getCompoundDrawables()[2];
                if (drawable == null) {
                    // don't have end drawable
                    return false;
                }

                // 点击了 输入框中 右边的 x
                if (event.getX() > editText.getWidth()
                        - editText.getPaddingRight()
                        - drawable.getIntrinsicWidth()){
                    editText.setText("");
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

        builder.setTitle(getResources().getString(R.string.Device_More_EnterPassword))
                .setContentView(view)
                .setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        String password = editText.getText().toString();
                        if (password.length() >= 6 && password.length() <= 9 && mDevice != null) {

                            JSONObject object = new JSONObject();
                            try {
                                object.put("cmd", "501");
                                object.put("gwID", mDevice.gwID);
                                object.put("devID", mDevice.devID);
                                object.put("appID", MainApplication.getApplication().getLocalInfo().appID);
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
                                isChceking = true;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            progressDialogManager.showDialog("Check", context, getResources().getString(R.string.Handling), new CustomProgressDialog.OnDialogDismissListener() {
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
    public void onDeviceReportEvent(DeviceReportEvent event) {
        if (event == null) {
            return;
        }

        if (TextUtils.equals(event.device.devID, deviceID)) {
            mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
            updateMode();
            if (isChceking) {
                progressDialogManager.dimissDialog("Check", 0);
                dealData(event.device.data);
            }
        }
    }

    private void dealData(String data) {
        try {
            JSONObject object = new JSONObject(data);
            returnAppID = object.optString("appID");
            JSONArray endpoints = object.optJSONArray("endpoints");
            // TODO: 2017/5/16 更新 endpointNumber
//                        endpointNumber = object.getString("endpointNumber");
            JSONArray clusters = ((JSONObject) endpoints.get(0)).optJSONArray("clusters");
            JSONArray attributes = ((JSONObject) clusters.get(0)).optJSONArray("attributes");
            String attributeValue = ((JSONObject) attributes.get(0)).optString("attributeValue");
            int attributeId = ((JSONObject) attributes.get(0)).optInt("attributeId");
            // 更新状态
            updateState(attributeId, attributeValue);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void updateState(int attributeId, String attributeValue) {
        if (attributeValue.isEmpty()) {
            return;
        }
        int value = 0;
        try {
            value = Integer.parseInt(attributeValue);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        switch (attributeId) {
            case 0x8001:
                if (value == 4) {
                    toast(R.string.Xuanwulakeseries_Widget_Errorsmore);
                }
            case 0x8006:
                if (value == 4 || value == 5 || value == 10) {
                    toast(R.string.Device_Vidicon_AdministratorPassworderror);
                }
                break;
            case 0x8007:
                if (! TextUtils.equals(returnAppID, MainApplication.getApplication().getLocalInfo().appID)) {
                    return;
                }
                if (attributeValue.startsWith("01")) {
                    mToken = attributeValue.substring(2);
                    saveToken(mToken);
                }
                break;
        }
    }

    private void toast(@StringRes int resId) {
        if (mToken == null) {
            ToastUtil.single(resId);
        }
    }

    private void saveToken(String token) {
        String room = "Token_OW";
        new NativeStorage().setItem(room, room, token);

        jumpAccountManage();
    }

}
