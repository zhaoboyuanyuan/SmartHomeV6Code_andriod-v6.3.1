package cc.wulian.smarthomev6.main.device.safeDog.config;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.WLFragment;

/**
 * created by huxc  on 2018/1/24.
 * func： 安全狗连接成功
 * email: hxc242313@qq.com
 */

public class SDConnectSuccessFragment extends WLFragment implements View.OnClickListener {


    public static SDConnectSuccessFragment newInstance(String deviceId) {
        Bundle bundle = new Bundle();
        bundle.putString("deviceId", deviceId);
        SDConnectSuccessFragment successFragment = new SDConnectSuccessFragment();
        successFragment.setArguments(bundle);
        return successFragment;
    }

    private TextView tvName;
    private TextView tvArea;

    @Override
    public int layoutResID() {
        return R.layout.fragment_safe_dog_connect_success;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void initView(View view) {
        tvName = (TextView) view.findViewById(R.id.tvName);
        tvArea = (TextView) view.findViewById(R.id.tvArea);

    }

    @Override
    protected void initTitle(View v) {
        super.initTitle(v);
        setLeftImg(R.drawable.icon_back);
        mTvTitle.setText(getString(R.string.Config_Add_Success));
    }

    @Override
    public void initListener() {
        super.initListener();
        tvArea.setOnClickListener(this);
        tvName.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvName:
                break;
            case R.id.tvArea:
                break;
            default:
                break;
        }

    }
}
