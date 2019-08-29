package cc.wulian.smarthomev6.main.mine.gatewaycenter;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceIsPushBean;
import cc.wulian.smarthomev6.support.utils.StringUtil;

/**
 * Created by mamengchao on 2017/2/28 0028.
 * Tips:消息推送设置
 */

public class RemindSetActivity extends BaseTitleActivity {

    private static final String SET_TYPE = "SET_TYPE";
    private static final String GET_TYPE = "GET_TYPE";
    private RelativeLayout itemRemindTime;
    private ToggleButton itemRemindpush;
    private DeviceApiUnit deviceApiUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceApiUnit = new DeviceApiUnit(this);
        setContentView(R.layout.activity_remind_set, true);
    }

    @Override
    protected void initTitle() {
        setToolBarTitle(getString(R.string.GatewayCenter_NotifySetts));
    }

    @Override
    protected void initView() {
        itemRemindTime = (RelativeLayout) findViewById(R.id.item_remind_time);
        itemRemindpush = (ToggleButton) findViewById(R.id.item_remind_alarm_push);

        getPushType();
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        itemRemindTime.setOnClickListener(this);
        itemRemindpush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    setPushType(true);
                }else {
                    setPushType(false);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.item_remind_time:
                startActivity(new Intent(RemindSetActivity.this,PushTimeActivity.class));
                break;
            default:
                break;
        }
    }

    private void setPushType(final boolean isPush){
        if (StringUtil.equals(preference.getUserEnterType(), "account")){
            progressDialogManager.showDialog(SET_TYPE, this, null, null, getResources().getInteger(R.integer.http_timeout));
            deviceApiUnit.doIsPush(isPush, preference.getCurrentGatewayID(), new DeviceApiUnit.DeviceApiCommonListener() {
                @Override
                public void onSuccess(Object bean) {
                    preference.saveAlarmPush(isPush);
                    itemRemindpush.setChecked(isPush);
                    progressDialogManager.dimissDialog(SET_TYPE, 0);
                }

                @Override
                public void onFail(int code, String msg) {
                    progressDialogManager.dimissDialog(SET_TYPE, 0);
                }
            });
        }else {
            preference.saveAlarmPush(isPush);
            itemRemindpush.setChecked(isPush);
        }
    }

    private void getPushType() {
        if (TextUtils.isEmpty(preference.getCurrentAccountID())){
            return;
        }
        progressDialogManager.showDialog(GET_TYPE, this, null, null, getResources().getInteger(R.integer.http_timeout));
        deviceApiUnit.doGetIsPush(preference.getCurrentGatewayID(), new DeviceApiUnit.DeviceApiCommonListener<DeviceIsPushBean>() {
            @Override
            public void onSuccess(DeviceIsPushBean bean) {
                if (StringUtil.equals(bean.isPush, "0")) {
                    preference.saveAlarmPush(false);
                    itemRemindpush.setChecked(false);
                } else if (StringUtil.equals(bean.isPush, "1")) {
                    preference.saveAlarmPush(true);
                    itemRemindpush.setChecked(true);
                }
                progressDialogManager.dimissDialog(GET_TYPE, 0);
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(GET_TYPE, 0);
            }
        });
    }
}
