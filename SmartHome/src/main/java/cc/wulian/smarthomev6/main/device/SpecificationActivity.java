package cc.wulian.smarthomev6.main.device;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.h5.NativeStorage;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.customview.WLProgressBar;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by huxc on 2018/1/21.
 * 电子说明书
 */
public class SpecificationActivity extends BaseTitleActivity {
    protected WebView mWebView;

    private WLProgressBar mProgressBar;
    private NativeStorage mNativeStorage;
    private String url;
    private String deviceId;

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
        setToolBarTitle(getString(R.string.device_More));
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
        deviceId = getIntent().getStringExtra("deviceId");
        Device device = mainApplication.getDeviceCache().get(deviceId);
        if (device == null) {
            return;
        }
        if (TextUtils.equals(device.type, "Br")) {
            url = "https://download.wuliangroup.cn/SmartHomeV6/instructions/instruction_Br.html";
        } else if (TextUtils.equals(device.type, "Bx")) {
            url = "https://download.wuliangroup.cn/SmartHomeV6/instructions/instruction_Bx.html";
        }
        mWebView.loadUrl(url);
    }
}