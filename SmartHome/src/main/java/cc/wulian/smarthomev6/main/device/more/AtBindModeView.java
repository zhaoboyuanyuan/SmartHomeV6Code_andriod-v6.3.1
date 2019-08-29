package cc.wulian.smarthomev6.main.device.more;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.MoreConfig;
import cc.wulian.smarthomev6.main.device.IDeviceMore;
import cc.wulian.smarthomev6.support.tools.HttpUrlKey;

/**
 * 作者: chao
 * 时间: 2017/7/21
 * 描述: 内嵌二路开关，开启绑定/解绑模式
 * 联系方式: 805901025@qq.com
 */

public class AtBindModeView extends FrameLayout implements IDeviceMore {
    String devID = "";
    String gwID="";
    String url = "";

    public AtBindModeView(Context context) {
        super(context);
        initView(context);
    }

    @Override
    public void onBindView(MoreConfig.ItemBean bean) {
        gwID=bean.getValueByKey("gwID");
        devID=bean.getValueByKey("deviceID");
        url=bean.getValueByKey("url");
    }

    @Override
    public void onViewRecycled() {
    }

    private void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_device_more_custom_aj_bind_mode, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        rootView.setOnClickListener(onClickListener);
    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            AtBindModeSwitchActivity.start(getContext(), HttpUrlKey.URL_BASE + "/"+ url, devID);
        }
    };

}
