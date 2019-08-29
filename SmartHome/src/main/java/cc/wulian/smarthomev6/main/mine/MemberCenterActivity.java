package cc.wulian.smarthomev6.main.mine;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.webkit.WebView;

import cc.wulian.smarthomev6.BuildConfig;
import cc.wulian.smarthomev6.main.h5.H5BridgeActivity;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.tools.HttpUrlKey;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by 上海滩小马哥 on 2017/8/31
 */

public class MemberCenterActivity extends H5BridgeActivity {

    @Override
    protected void init() {
        super.init();

        showLossNetwork = true;
    }

    @Override
    protected void onPageStart(WebView view, String url) {
        if (url.endsWith("backtoAPP.html")) {
            finish();
        }
    }

    @Override
    protected String getUrl() {
        String baseUrl = preference.getBaseServer();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl + "api";
        } else {
            baseUrl = baseUrl + "/api";
        }
        baseUrl = new String(Base64.encode(baseUrl.getBytes(), Base64.DEFAULT));
        String url = HttpUrlKey.URL_MEMBER_CENTER;
        if (!TextUtils.equals(BuildConfig.BUILD_TYPE, "release")) {
            url = HttpUrlKey.URL_MEMBER_CENTER_DEBUG_TEST;
        }
        return url
                + "?WL-PARTNER-ID=" + ApiConstant.APPID
                + "&WL-TID=" + ApiConstant.getTerminalId()
                + "&lang=" + LanguageUtil.getWulianCloudLanguage()
                + "&WL-TOKEN=" + ApiConstant.getAppToken()
                + "&baseUrl=" + baseUrl;
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack() && isUrlCanBack(mWebView.getUrl())) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 这些页面不可以返回上一级，直接退出
     */
    private static final String[] CANNOT_GOBACK_PAGES = {

    };

    private boolean isUrlCanBack(String url) {
        WLog.i("backUrl:" + url);
        try {
            Uri uri = Uri.parse(url);
            String lastPathSegment = uri.getLastPathSegment();
            WLog.i("uri.getLastPathSegment:" + lastPathSegment);
            if (lastPathSegment != null) {
                for (String page : CANNOT_GOBACK_PAGES) {
                    if (lastPathSegment.equals(page)) {
                        return false;
                    }
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
