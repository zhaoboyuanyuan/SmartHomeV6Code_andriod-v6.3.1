package cc.wulian.smarthomev6.main.device.gateway_mini.config;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * created by huxc  on 2017/8/21.
 * func： min网关配网引导页
 * email: hxc242313@qq.com
 */

public class MiniGatewayGuideFragment extends WLFragment implements View.OnClickListener {
    private static final String TAG = "MiniGatewayGuideFragment";
    private static final String DEVICE_ID = "deviceId";
    private String deviceId;
    private String scanEntry;
    private Context context;
    private Button btnNextStep;
    private MiniGatewayConnectWifiFragment miniGatewayConnectWifiFragment;

    public static MiniGatewayGuideFragment newInstance(String deviceId,String scanEntry) {
        Bundle bundle = new Bundle();
        bundle.putString(DEVICE_ID, deviceId);
        bundle.putString("scanEntry", scanEntry);
        MiniGatewayGuideFragment miniGatewayGuideFragment = new MiniGatewayGuideFragment();
        miniGatewayGuideFragment.setArguments(bundle);
        return miniGatewayGuideFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        Bundle bundle = getArguments();
        if (bundle != null) {
            deviceId = bundle.getString(DEVICE_ID);
            scanEntry = bundle.getString("scanEntry");
            WLog.i(TAG, "onCreate: deviceId = " + deviceId);
        }
    }

    @Override
    public int layoutResID() {
        return R.layout.fragment_minigateway_guide;
    }

    @Override
    protected void initTitle(View v) {
        super.initTitle(v);
        setLeftImg(R.drawable.icon_back);
        mTvTitle.setText(getString(R.string.Minigateway_Device_Title));
    }

    @Override
    public void initView(View view) {
        btnNextStep = (Button) view.findViewById(R.id.btn_next_step);

    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(btnNextStep, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btnNextStep, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    @Override
    public void initListener() {
        super.initListener();
        btnNextStep.setOnClickListener(this);
        mImgBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next_step:
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                miniGatewayConnectWifiFragment = MiniGatewayConnectWifiFragment.newInstance(deviceId,scanEntry);
                ft.replace(android.R.id.content, miniGatewayConnectWifiFragment, MiniGatewayConnectWifiFragment.class.getName());
                ft.addToBackStack(null);
                ft.commit();
                break;
            case R.id.base_img_back_fragment:
                getActivity().finish();
            default:
                break;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getFocus();
    }

    //主界面获取焦点
    private void getFocus() {
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    getActivity().finish();
                    return true;
                }
                return false;
            }
        });
    }

}
