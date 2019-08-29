package cc.wulian.smarthomev6.main.mine;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import cc.wulian.smarthomev6.BuildConfig;
import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.h5.NativeStorage;
import cc.wulian.smarthomev6.main.h5.WVJBWebViewClient;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.SsoApiUnit;
import cc.wulian.smarthomev6.support.event.KeyboardEvent;
import cc.wulian.smarthomev6.support.tools.HttpUrlKey;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.BitmapUtil;
import cc.wulian.smarthomev6.support.utils.FileUtil;
import cc.wulian.smarthomev6.support.utils.NetworkUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

public class CustomerServiceTitleActivity extends BaseTitleActivity {

    private static final int PORTRAIT_IMAGE_TAKE = 1;
    private static final int PORTRAIT_IMAGE_ALBUM = 2;
    private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    private static final int PERMISSION_CAMERA_REQUEST_CODE = 1;

    private SsoApiUnit mSsoApi;
    private WVJBWebViewClient.WVJBResponseCallback selectCallback, takeCallback;

    private WebView mWebView;
    protected View mViewLossNetwork;

    protected WVJBWebViewClient mWebViewClient;
    private NativeStorage mNativeStorage;

    private static final String IMAGE_CAPTURE_LOCATION = "file:///" + FileUtil.getTempDirectoryPath() + "/servicePhotoCache.jpg";
    private static final Uri CAPTURE_IMAGE_URI = Uri.parse(IMAGE_CAPTURE_LOCATION);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_service_title, true);

        mWebView = (WebView) findViewById(R.id.webview);
        mViewLossNetwork = findViewById(R.id.bridge_loss_network);

        mNativeStorage = new NativeStorage();
        mWebViewClient = new MyWebViewClient(mWebView);

        if (!NetworkUtil.isNetworkConnected(this)) {
            mViewLossNetwork.setVisibility(View.VISIBLE);
        }

        initWebSettings();
        registerHandler();
        loadUrl();

        // 是否允许调试
        WebView.setWebContentsDebuggingEnabled(BuildConfig.WEB_DEBUG);

        mSsoApi = new SsoApiUnit(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
        // 这里要断开 webSocket
        mWebView.destroy();
    }

    private void loadUrl() {
        mWebView.loadUrl(HttpUrlKey.URL_CUSTOMER_SERVICE
                + "?token=" + ApiConstant.getAppToken()
                + "&uId=" + ApiConstant.getUserID());
//        mWebView.loadUrl("https://chat10.live800.com/live800/chatClient/chatbox.jsp?companyID=1059921&configID=235646&jid=3031214767&s=1");
    }

    private void registerHandler() {
        mWebViewClient.registerHandler("selectPhotoForCus", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                selectCallback = callback;
                //调用系统相册
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PORTRAIT_IMAGE_ALBUM);
            }
        });

        mWebViewClient.registerHandler("takePhotoForCus", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                takeCallback = callback;
                // 启动相机
                checkCameraPermission();
            }
        });

        mWebViewClient.registerHandler("uploadChatPic", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                String url = (String) data;
                uploadPic(url, callback);
            }
        });

        mWebViewClient.registerHandler("WLHttpGet", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
                String url = (String) data;
                new DataApiUnit(CustomerServiceTitleActivity.this).doGetServiceHistory(url, new DataApiUnit.DataApiCommonListener() {
                    @Override
                    public void onSuccess(Object bean) {
                        WLog.i(TAG, "onSuccess: 获取到了历史 - " + bean);
                        try {
                            callback.callback(new JSONObject(bean.toString()));
                        } catch (JSONException e) {
                            callback.callback(bean.toString());
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {

                    }
                });
            }
        });
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this,
                PERMISSION_CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSION_CAMERA)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{PERMISSION_CAMERA},
                        PERMISSION_CAMERA_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{PERMISSION_CAMERA},
                        PERMISSION_CAMERA_REQUEST_CODE);
            }
        } else {
            startCamera();
        }
    }

    private void startCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, CAPTURE_IMAGE_URI);
        startActivityForResult(intent, PORTRAIT_IMAGE_TAKE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                // Permission Denied
                ToastUtil.show(R.string.Toast_Permission_Denied);
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PORTRAIT_IMAGE_TAKE) {
                String path = CAPTURE_IMAGE_URI.getPath();
                int size;
                File file = new File(path);
                if (file.exists()) {
                    FileInputStream fis;
                    try {
                        fis = new FileInputStream(file);
                        size = fis.available() / 1024;
                        WLog.i("User", "changeAvatarStart: " + size + "kb");
                        String fileName = path.substring(0, path.lastIndexOf('.')) + "_thumb";
                        String pathThumb = FileUtil.getTempDirectoryPath() + "/thumb.jpg";

                        Bitmap originBitmap = BitmapFactory.decodeFile(path);
                        Bitmap newBitmap = null;
                        float scale = 600f/size;
//                        while (size >= 800) {
                        if(scale < 1) {
                            Matrix matrix = new Matrix();
                            matrix.postScale(scale, scale);
                            newBitmap = Bitmap.createBitmap(originBitmap, 0, 0, originBitmap.getWidth(), originBitmap.getHeight(), matrix, true);
                            size = newBitmap.getHeight() * newBitmap.getRowBytes() / 1024;
//                            if (size >= 800) {
//                                originBitmap = newBitmap;
//                            }
                            WLog.i("User", "changeAvatar: " + size + "kb");
                        }
//                        }
                        File file1 = new File(pathThumb);
                        if (file1.exists()) {
                            file1.delete();
                        }
                        WLog.i("User", "changeAvatarStop: " + size + "kb");
                        boolean isSuccess = BitmapUtil.saveBitmap(newBitmap, 90, file1);
                        originBitmap.recycle();
                        newBitmap.recycle();
                        path = pathThumb;
                        WLog.i("User", "changeAvatar: " + path);
                        WLog.i("User", "changeAvatar: " + size + "kb");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (takeCallback != null) {
                    takeCallback.callback(path);
                }
            } else if (requestCode == PORTRAIT_IMAGE_ALBUM && data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumns = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePathColumns[0]);
                String imageUri = c.getString(columnIndex);
                // showImage(imageUri);
                c.close();
                if (selectCallback != null) {
                    selectCallback.callback(imageUri);
                }
//                uploadPic(imageUri);
            }
        }
    }

    private void uploadPic(String path, final WVJBWebViewClient.WVJBResponseCallback callback) {
        WLog.i(TAG, "uploadPic: " + path);
        int size;
        File file = new File(path);
        if (file.exists()) {
            FileInputStream fis;
            try {
                fis = new FileInputStream(file);
                size = fis.available() / 1024;
                WLog.i(TAG, "changeAvatarStart: " + size + "kb");
                String pathThumb = FileUtil.getTempDirectoryPath() + "/thumb.jpg";

                Bitmap originBitmap = BitmapFactory.decodeFile(path);
                Bitmap newBitmap = originBitmap;
                while (size >= 800) {
                    Matrix matrix = new Matrix();
                    matrix.postScale(0.8f, 0.8f);
                    newBitmap = Bitmap.createBitmap(originBitmap, 0, 0, originBitmap.getWidth(), originBitmap.getHeight(), matrix, true);
                    size = newBitmap.getHeight() * newBitmap.getRowBytes() / 1024;
                    if (size >= 800) {
                        originBitmap = newBitmap;
                    }
                    WLog.i(TAG, "changeAvatar: " + size + "kb");
                }
                File file1 = new File(pathThumb);
                if (file1.exists()) {
                    file1.delete();
                }
                WLog.i(TAG, "changeAvatarStop: " + size + "kb");
                BitmapUtil.saveBitmap(newBitmap, file1);
                originBitmap.recycle();
                newBitmap.recycle();
                path = pathThumb;
                WLog.i(TAG, "changeAvatar: " + path);
                WLog.i(TAG, "changeAvatar: " + size + "kb");

            } catch (Exception e) {
                e.printStackTrace();
            }

            mSsoApi.doUploadServicePic(path, new SsoApiUnit.SsoApiCommonListener<Object>() {
                @Override
                public void onSuccess(Object bean) {
                    WLog.i(TAG, "onSuccess: " + bean);
                    try {
                        JSONObject json = new JSONObject(bean.toString());
                        callback.callback(json.optString("retData"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFail(int code, String msg) {
//                    ToastUtil.single(msg);
                    callback.callback("false");
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(KeyboardEvent event) {
        //
    }

    @SuppressLint("SetJavaScriptEnabled")
    protected void initWebSettings() {
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.clearCache(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // H5 可以使用 loadStorage
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(mNativeStorage, "v6sysfunc");
        mWebView.setWebViewClient(mWebViewClient);
    }

    @Override
    protected void initTitle() {
        super.initTitle();

        setToolBarTitle(R.string.OnlineService);
    }

    protected WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            WLDialog.Builder builder = new WLDialog.Builder(view.getContext());
            builder.setTitle(getString(R.string.Hint))
                    .setMessage(message)
                    .setPositiveButton(getString(R.string.Sure))
                    .setListener(new WLDialog.MessageListener() {
                        @Override
                        public void onClickPositive(View var1, String msg) {
                            result.confirm();
                        }

                        @Override
                        public void onClickNegative(View var1) {

                        }
                    })
                    .create()
                    .show();
            result.confirm();// 因为没有绑定事件，需要强行confirm,否则页面会变黑显示不了内容。
            return true;
        }
    };

    private class MyWebViewClient extends WVJBWebViewClient {
        MyWebViewClient(WebView webView) {
            super(webView);

            registerHandler("back", new WVJBWebViewClient.WVJBHandler() {
                @Override
                public void request(Object data, WVJBResponseCallback callback) {
                    WLog.i(TAG, "返回 back");
                    finish();
                }
            });

            registerHandler("goBack", new WVJBWebViewClient.WVJBHandler() {
                @Override
                public void request(Object data, WVJBResponseCallback callback) {
                    WLog.i(TAG, "返回 goBack");
                    if (mWebView.canGoBack()) {
                        mWebView.goBack();
                    } else {
                        finish();
                    }
                }
            });
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            WLog.i(TAG, "onPageFinished: " + url);
            mWebViewClient.callHandler("onReady");
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }
}
