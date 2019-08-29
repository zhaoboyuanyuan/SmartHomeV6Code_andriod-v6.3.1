package cc.wulian.smarthomev6.main.mine.gatewaycenter;

import android.content.Intent;
import android.net.Uri;

import cc.wulian.smarthomev6.main.h5.H5BridgeActivity;
import cc.wulian.smarthomev6.main.h5.WVJBWebViewClient;
import cc.wulian.smarthomev6.support.tools.HttpUrlKey;

public class GatewayWanPortSettingActivity extends H5BridgeActivity {


    @Override
    protected void init() {
    }

    @Override
    protected String getUrl() {
        return HttpUrlKey.URL_BASE + "/" + "ManagerGateWay/network_1.html";
    }

    @Override
    protected void registerHandler() {
        mWebViewClient.registerHandler("goToWlan", new WVJBWebViewClient.WVJBHandler() {

            @Override
            public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
                Intent intent = new Intent(Intent.ACTION_VIEW);    //为Intent设置Action属性
                intent.setData(Uri.parse("http://192.168.188.1")); //为Intent设置DATA属性
                startActivity(intent);
            }
        });

    }

}
