package cc.wulian.smarthomev6.main.h5;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import cc.wulian.smarthomev6.BuildConfig;
import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.support.customview.WLProgressBar;

/**
 * Created by Veev on 2017/9/22
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    浏览器
 */
public class WebActivity extends BaseTitleActivity {

    protected WebView mWebView;
    private WLProgressBar mProgressBar;
    protected View mViewLossNetwork;

    protected NativeStorage nativeStorage;

    private String mUrl, mName;

    public static void start(Context context, String url, String name) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("name", name);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web, true);

        mUrl = getIntent().getStringExtra("url");
        mName = getIntent().getStringExtra("name");

        mWebView = (WebView) findViewById(R.id.web_web_view);
        mProgressBar = (WLProgressBar) findViewById(R.id.web_progress);
        mViewLossNetwork = findViewById(R.id.web_loss_network);

        nativeStorage = new NativeStorage();

        // 初始化webView的设置
        initWebSettings();
        // 是否允许调试
        WebView.setWebContentsDebuggingEnabled(BuildConfig.WEB_DEBUG);

        setToolBarTitle(mName);

        loadUrl();
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private void loadUrl() {
        mWebView.loadUrl(mUrl);
    }

    private void initWebSettings() {
        mWebView.clearCache(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // H5 可以使用 loadStorage
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(nativeStorage, "v6sysfunc");

        // 大小自适应
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

//                setToolBarTitle(view.getTitle());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mWebView.destroy();
    }
}
