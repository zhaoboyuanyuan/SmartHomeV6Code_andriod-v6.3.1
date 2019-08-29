package cc.wulian.smarthomev6.main.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;

import cc.wulian.smarthomev6.main.h5.NativeStorage;
import cc.wulian.smarthomev6.support.utils.WLog;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.support.customview.WLProgressBar;
import cc.wulian.smarthomev6.support.tools.HttpUrlKey;

/**
 * Created by mamengchao on 2017/3/1 0001.
 * Tips:关于物联
 */
public class AboutUsActivity extends BaseTitleActivity {
    protected WebView mWebView;

    private WLProgressBar mProgressBar;
    private NativeStorage mNativeStorage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h5_bridge, true);
        initWebSettings();
        loadUrl();
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Mine_About));
    }

    private void initWebSettings() {
        mWebView = (WebView) findViewById(R.id.bridge_webview);
        mNativeStorage = new NativeStorage();
        mProgressBar = (WLProgressBar) findViewById(R.id.bridge_progress);

        mWebView.setWebChromeClient(mWebChromeClient);
//        mWebView.clearCache(true);
//        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // H5 可以使用 loadStorage
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(mNativeStorage, "v6sysfunc");
    }

    protected WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            WLog.i(TAG, message);
            return super.onJsAlert(view, url, message, result);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            mProgressBar.setProgress(newProgress);
        }
    };

    protected void loadUrl() {
        mWebView.loadUrl(HttpUrlKey.KEY_ABOUT_URL);
    }
}