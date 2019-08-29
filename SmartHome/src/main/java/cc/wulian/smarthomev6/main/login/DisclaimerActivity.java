package cc.wulian.smarthomev6.main.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.util.Locale;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.support.customview.WLProgressBar;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * 作者: mamengchao
 * 时间: 2017/4/12 0012
 * 描述: 服务条款与免责声明
 * 联系方式: 805901025@qq.com
 */

public class DisclaimerActivity extends BaseTitleActivity {
    protected WebView mWebView;

    private WLProgressBar mProgressBar;


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
        setToolBarTitle(getString(R.string.Login_Agree_Message_Title));
    }

    private void initWebSettings() {
        mWebView = (WebView) findViewById(R.id.bridge_webview);
        mProgressBar = (WLProgressBar) findViewById(R.id.bridge_progress);

        mWebView.setWebChromeClient(mWebChromeClient);
//        mWebView.clearCache(true);
//        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // H5 可以使用 loadStorage
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
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
        String wulianLanguage = LanguageUtil.getWulianCloudLanguage();
        String fileNameExt = "en_us";
        if ("en".equals(wulianLanguage)) {
            fileNameExt = "en_us";
        } else if ("ja".equals(wulianLanguage)) {
            fileNameExt = "ja_jp";
        } else if ("zh-cn".equals(wulianLanguage)) {
            fileNameExt = "zh_cn";
        } else if ("he".equals(wulianLanguage)) {
            fileNameExt = "he";
        }
//        String language = Locale.getDefault().getLanguage();
//        String country = Locale.getDefault().getCountry();
//        String temp = (language + "_" + country).toLowerCase(Locale
//                .getDefault());
        String name = "disclaimer";
        mWebView.loadUrl("file:///android_asset/main/disclaimer/" + name + "_" + fileNameExt + ".html");
    }
}
