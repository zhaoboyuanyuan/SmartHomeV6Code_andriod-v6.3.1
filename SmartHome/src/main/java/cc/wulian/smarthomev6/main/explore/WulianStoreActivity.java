package cc.wulian.smarthomev6.main.explore;

import android.webkit.WebSettings;

import cc.wulian.smarthomev6.main.h5.H5BridgeActivity;
import cc.wulian.smarthomev6.support.tools.HttpUrlKey;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;

public class WulianStoreActivity extends H5BridgeActivity {

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void init() {
        super.init();

        showLossNetwork = true;
    }

    @Override
    protected void initWebSettings() {
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // H5 可以使用 loadStorage
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(nativeStorage, "v6sysfunc");
        mWebView.setWebViewClient(mWebViewClient);

        // 大小自适应
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
    }

    @Override
    protected void registerHandler() {

    }

    @Override
    protected String getUrl() {
        String url = HttpUrlKey.URL_MALL;
        if(!LanguageUtil.isChina()){
            url = HttpUrlKey.URL_MALL_EN;
        }
        return url;
    }
}
