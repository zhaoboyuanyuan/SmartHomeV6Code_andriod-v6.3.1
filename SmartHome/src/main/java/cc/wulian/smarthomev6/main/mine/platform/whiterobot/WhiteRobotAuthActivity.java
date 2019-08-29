package cc.wulian.smarthomev6.main.mine.platform.whiterobot;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.h5.CommonH5Activity;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;

/**
 * Created by zbl on 2017/8/24.
 * 小白机器人，授权界面
 * 跳转返回例子：http://www.wuliangroup.com/cn/?code=Bk0rvQ&state=xyz
 */

public class WhiteRobotAuthActivity extends CommonH5Activity {

    @Override
    protected void initWebSettings() {
        super.initWebSettings();
        setToolBarTitle(getString(R.string.Authorize_Login));
        mWebView.setWebViewClient(webViewClient);
    }

    @Override
    protected String getUrl() {
        return ApiConstant.getBaseServer() + "/oauth/oauth/v2/authorize?client_id=" + ApiConstant.WHITEROBOT_CLIEND_ID + "&response_type=code&redirect_uri=http://www.wuliangroup.com/cn&state=xyz"
                + "&uId=" + ApiConstant.getUserID() + "&token=" + ApiConstant.getAppToken();
    }

    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!TextUtils.isEmpty(url) && url.startsWith("http://www.wuliangroup.com/cn/?")) {
                Uri uri = Uri.parse(url);
                String code = uri.getQueryParameter("code");
                if (!TextUtils.isEmpty(code)) {
                    if (!TextUtils.isEmpty(Preference.getPreferences().getCurrentGatewayID())) {
                        Intent intent = new Intent(WhiteRobotAuthActivity.this, WhiteRobotWifiActivity.class);
                        intent.putExtra("code", code);
                        startActivity(intent);
                        finish();
                    } else {
                        WLDialog.Builder builder = new WLDialog.Builder(WhiteRobotAuthActivity.this);
                        builder.setMessage(getString(R.string.AddDevice_UnboundedGateway_Content))
                                .setCancelOnTouchOutSide(false)
                                .setCancelable(false)
                                .setPositiveButton(getResources().getString(R.string.Tip_I_Known))
                                .create()
                                .show();
                    }
                }
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    };
}
