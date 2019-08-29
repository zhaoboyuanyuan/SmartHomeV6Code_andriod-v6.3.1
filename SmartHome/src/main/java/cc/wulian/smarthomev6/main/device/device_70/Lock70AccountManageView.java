package cc.wulian.smarthomev6.main.device.device_70;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.MoreConfig;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.IDeviceMore;
import cc.wulian.smarthomev6.support.core.device.Device;

/**
 * created by huxc  on 2018/5/3.
 * func：70门锁用户设置view
 * email: hxc242313@qq.com
 */

public class Lock70AccountManageView extends RelativeLayout implements IDeviceMore {

    private Context context;

    private TextView textName;
    private View rootView;

    private MoreConfig.ItemBean mItemBean;

    private Device mDevice;
    private String deviceID;
    private String url;

    public Lock70AccountManageView(Context context) {
        super(context);

        this.context = context;
        initView(context);
    }

    @Override
    public void onBindView(MoreConfig.ItemBean bean) {
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
        rootView = LayoutInflater.from(context).inflate(R.layout.item_device_more_custom_70_account_manage, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        textName = (TextView) rootView.findViewById(R.id.op_name);

        rootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpAccountManage();
            }
        });
    }


    private void jumpAccountManage() {
        Intent intent = new Intent(context, Lock70Activity.class);
        intent.putExtra("deviceID", deviceID);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

}
