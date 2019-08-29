package cc.wulian.smarthomev6.main.h5;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.alibaba.fastjson.JSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cc.wulian.smarthomev6.BuildConfig;
import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.H5Storage;
import cc.wulian.smarthomev6.entity.H5StorageDao;
import cc.wulian.smarthomev6.entity.RoomInfo;
import cc.wulian.smarthomev6.entity.SceneInfo;
import cc.wulian.smarthomev6.main.application.BaseActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.AreaManager;
import cc.wulian.smarthomev6.main.device.device_22.Device22Activity;
import cc.wulian.smarthomev6.main.device.device_23.Device23Activity;
import cc.wulian.smarthomev6.main.device.device_if02.WifiIRActivity;
import cc.wulian.smarthomev6.main.home.scene.SceneManager;
import cc.wulian.smarthomev6.main.ztest.TestActivity;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.bean.DeviceForbiddenBean;
import cc.wulian.smarthomev6.support.customview.WLProgressBar;
import cc.wulian.smarthomev6.support.tools.AssetsManager;
import cc.wulian.smarthomev6.support.tools.HttpUrlKey;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.FileUtil;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;
import cc.wulian.smarthomev6.support.utils.NetworkUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

public abstract class H5BridgeActivity extends BaseActivity {

    protected final String TAG = this.getClass().getSimpleName();

    protected WWebView mWebView;
    protected View mBottomSpace;
    protected View mViewLossNetwork;

    protected WVJBWebViewClient mWebViewClient;

    private WLProgressBar mProgressBar;

    protected NativeStorage nativeStorage;

    private View loadingView;

    private AreaManager areaManager;

    /**
     * 在没有网络的时候
     * 是否显示 暂无网络连接
     */
    protected boolean showLossNetwork = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                Class decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
                Field field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor");
                field.setAccessible(true);
                field.setInt(getWindow().getDecorView(), Color.TRANSPARENT);  //改为透明
            } catch (Exception e) {
            }
        }
        //透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        }
        setContentView(R.layout.activity_h5_bridge);

        mWebView = (WWebView) findViewById(R.id.bridge_webview);
        mBottomSpace = findViewById(R.id.bridge_space_bottom);
        mProgressBar = (WLProgressBar) findViewById(R.id.bridge_progress);
        mViewLossNetwork = findViewById(R.id.bridge_loss_network);
        loadingView = findViewById(R.id.loading_view);
        mWebView.setVerticalScrollBarEnabled(false);
        nativeStorage = new NativeStorage();
        int statusBarHeight = -1;
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            // 根据资源ID获取响应的尺寸值
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        String height = statusBarHeight == -1 ? "2.4rem" : statusBarHeight + "px";
        nativeStorage.setItem("StraightBangs", "StraightBangs", height);
        mWebViewClient = new MyWebViewClient(mWebView);
        View errorBack = findViewById(R.id.error_back);
        View retryBtn = findViewById(R.id.retry_btn);
        errorBack.setOnClickListener(mOnClickListener);
        retryBtn.setOnClickListener(mOnClickListener);
        init();

        //浏览器不检测http和https资源混用
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        // 初始化webView的设置
        initWebSettings();
        registerHandler();
        if (!NetworkUtil.isNetworkConnected(this)) {
            if (showLossNetwork) {
                mViewLossNetwork.setVisibility(View.VISIBLE);
            } else {
                loadPage();
            }
        } else {
            loadPage();
        }

        // 是否允许调试
        WebView.setWebContentsDebuggingEnabled(BuildConfig.WEB_DEBUG);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.error_back:
                    finish();
                    break;
                case R.id.retry_btn:
                    if (!NetworkUtil.isNetworkConnected(H5BridgeActivity.this)) {
                        if (showLossNetwork) {
                            mViewLossNetwork.setVisibility(View.VISIBLE);
                        }
                    } else {
                        loadPage();
                        mViewLossNetwork.setVisibility(View.INVISIBLE);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.destroy();
    }

//    @Override
//    public void onBackPressed() {
//        if (mWebView.canGoBack()) {
//            mWebView.goBack();
//        } else {
//            super.onBackPressed();
//        }
//    }

    protected void init() {
    }

    protected void registerHandler() {
    }

    protected void setWebChromeClient(WebChromeClient client) {
        if (mWebView == null) {
            return;
        }

        if (client == null) {
            return;
        }

        mWebView.setWebChromeClient(client);
    }

    @SuppressLint("SetJavaScriptEnabled")
    protected void initWebSettings() {
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.clearCache(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
//        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // H5 可以使用 loadStorage
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.addJavascriptInterface(nativeStorage, "v6sysfunc");
        mWebView.setWebViewClient(mWebViewClient);
    }

    protected abstract String getUrl();

    private void loadPage() {
        String url = getUrl();
        loadingView.setVisibility(View.VISIBLE);
        if (!TextUtils.equals("release", BuildConfig.BUILD_TYPE)) {
            if (TestActivity.USE_REMOTE_DOMAIN) {
                if (!TextUtils.isEmpty(url) && url.startsWith(HttpUrlKey.URL_BASE)) {
                    url = url.replace(HttpUrlKey.URL_BASE, TestActivity.REMOTE_BASE_URL);
                    mWebView.loadUrl(url);
                    return;
                }
            }
        }
        if (!TextUtils.isEmpty(url)) {
            if (url.startsWith(HttpUrlKey.URL_BASE_DEVICE)) {//从包内复制到外存
                final String devicePath = url.substring(HttpUrlKey.URL_BASE_DEVICE.length() + 1);
                final String deviceExtenalPath = FileUtil.getDeviceHtmlResourcesPath();
                WLog.i("devicePath:" + devicePath);
                String[] parsePath = devicePath.split("/");
                String folderName = parsePath[0];
                String fromPath = HttpUrlKey.URL_BASE_DEVICE.substring("file:///android_asset/".length()) + "/" + folderName;
                final String toPath = deviceExtenalPath + "/" + folderName;
                if (!AssetsManager.isNeedUpdate(getApplicationContext(), fromPath, toPath)) {
                    //资源已经存在,且版本相同，直接加载页面
                    mWebView.loadUrl("file://" + deviceExtenalPath + "/" + devicePath);
                    return;
                }
//                ProgressDialogManager.getDialogManager().showDialog(TAG, this, null, null, Integer.MAX_VALUE);
                AssetsManager.copyAssetsToExternalStorage(getApplicationContext(), fromPath, toPath, new AssetsManager.CopyTaskCallback() {
                    @Override
                    public void onFinish() {
                        mWebView.loadUrl("file://" + deviceExtenalPath + "/" + devicePath);
//                        ProgressDialogManager.getDialogManager().dimissDialog(TAG, 0);
                    }
                });
            } else if (url.startsWith(HttpUrlKey.URL_BASE)) {
                final String modulePath = url.substring(HttpUrlKey.URL_BASE.length() + 1);
                final String moduleExtenalPath = FileUtil.getHtmlResourcesPath();
                WLog.i("modulePath:" + modulePath);
                String[] parsePath = modulePath.split("/");
                String folderName = parsePath[0];
                String fromPath = HttpUrlKey.URL_BASE.substring("file:///android_asset/".length()) + "/" + folderName;
                final String toPath = moduleExtenalPath + "/" + folderName;
                if (!AssetsManager.isNeedUpdate(getApplicationContext(), fromPath, toPath)) {
                    //资源已经存在,且版本相同，直接加载页面
                    mWebView.loadUrl("file://" + moduleExtenalPath + "/" + modulePath);
                    return;
                }
//                ProgressDialogManager.getDialogManager().showDialog(TAG, this, null, null, Integer.MAX_VALUE);
                AssetsManager.copyAssetsToExternalStorage(getApplicationContext(), fromPath, toPath, new AssetsManager.CopyTaskCallback() {
                    @Override
                    public void onFinish() {
                        mWebView.loadUrl("file://" + moduleExtenalPath + "/" + modulePath);
//                        ProgressDialogManager.getDialogManager().dimissDialog(TAG, 0);
                    }
                });
            } else {
                mWebView.loadUrl(url);
            }
        }
    }


    protected void onPageDone(WebView view, String url) {
        mWebViewClient.callHandler("onReady");
    }

    protected void onPageStart(WebView view, String url) {

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
            // return super.onJsAlert(view, url, message, result);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            mProgressBar.setProgress(newProgress);
        }
    };

    private class MyWebViewClient extends WVJBWebViewClient {
        MyWebViewClient(WebView webView) {
            /*super(webView, new WVJBWebViewClient.WVJBHandler() {

                @Override
                public void request(Object data, WVJBResponseCallback callback) {
                    //
                }
            });*/
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

            registerHandler("toast", new WVJBWebViewClient.WVJBHandler() {
                @Override
                public void request(Object data, WVJBResponseCallback callback) {
                    WLog.i(TAG, "提示 toast");
                    if (data != null) {
                        ToastUtil.single(data.toString());
                    }
                }
            });

            // 登录状态
            registerHandler("getLoginType", new WVJBWebViewClient.WVJBHandler() {
                @Override
                public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                    switch (preference.getUserEnterType()) {
                        case Preference.ENTER_TYPE_GW:
                            callback.callback("101");
                            break;
                        case Preference.ENTER_TYPE_ACCOUNT:
                            callback.callback("100");
                            break;
                    }
                }
            });

            registerHandler("getDeviceList", new WVJBWebViewClient.WVJBHandler() {
                @Override
                public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                    WLog.i(TAG, "getDeviceList: 请求设备列表 - " + data);

                    JSONArray array = new JSONArray();
                    ArrayList<Device> devicesList = new ArrayList<>();
                    devicesList.addAll(MainApplication.getApplication().getDeviceCache().getDevices());
                    Collections.sort(devicesList, new Comparator<Device>() {//按名称排序
                        @Override
                        public int compare(Device o1, Device o2) {
                            int result = o1.sortStr.compareTo(o2.sortStr);
                            return result;
                        }
                    });
                    for (Device device : devicesList) {
                        try {
                            device.roomName = MainApplication.getApplication().getRoomCache().getRoomName(device);
                            array.put(new JSONObject(com.alibaba.fastjson.JSONObject.toJSONString(device)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    callback.callback(array);
                }
            });

            registerHandler("getSceneList", new WVJBWebViewClient.WVJBHandler() {
                @Override
                public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                    WLog.i(TAG, "请求场景列表 getSceneList:" + data);

                    JSONArray array = new JSONArray();
                    SceneManager manager = new SceneManager(H5BridgeActivity.this);
                    List<SceneInfo> infos = manager.acquireScene();
                    for (SceneInfo i : infos) {
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("sceneID", i.getSceneID());
                            obj.put("sceneName", i.getName());
                            obj.put("sceneIcon", i.getIcon());
                            array.put(obj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    callback.callback(array);
                }
            });


            // 获取分区列表
            registerHandler("getGroupList", new WVJBWebViewClient.WVJBHandler() {
                @Override
                public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                    WLog.i(TAG, "请求: 获取分区列表");
                    JSONArray array = new JSONArray();
//                    int count = MainApplication.getApplication().getRoomCache().getDevices().size();
//                    // JSONObject array[] = new JSONObject[count];
//                    int i = 0;
                    if (areaManager == null) {
                        areaManager = new AreaManager(H5BridgeActivity.this);
                    }
                    List<RoomInfo> infos = areaManager.loadDiskRoom();
                    for (RoomInfo room : infos) {
                        try {
                            JSONObject object = new JSONObject();
                            object.put("name", room.getName());
                            object.put("roomID", room.getRoomID());
//                        array[i++] = object;
                            array.put(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    WLog.i(TAG, "返回: 获取分区列表 " + array);
                    callback.callback(array);
                }
            });

            registerHandler("infraredSceneOrHouseKepper", new WVJBHandler() {
                @Override
                public void request(Object data, WVJBResponseCallback callback) {
                    try {
                        JSONObject jsonObject = new JSONObject(data.toString());
                        String deviceId = (String) jsonObject.get("deviceId");
                        String type = (String) jsonObject.get("type");
                        if (TextUtils.equals(type, "22") || TextUtils.equals(type, "24")) {
                            Device22Activity.start(H5BridgeActivity.this, deviceId, true);
                        } else if (TextUtils.equals(type, "23")) {
                            Device23Activity.start(H5BridgeActivity.this, deviceId, true);
                        } else if (TextUtils.equals(type, "IF02")) {
                            WifiIRActivity.start(H5BridgeActivity.this, deviceId, true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            registerHandler("isShowGuidePage", new WVJBHandler() {
                @Override
                public void request(Object data, WVJBResponseCallback callback) {
                    preference.getH5ShowGuidePage(data.toString());
                    callback.callback(preference.getH5ShowGuidePage(data.toString()));
                    preference.setH5ShowGuidePage(data.toString(), "NO");
                }
            });

            //判断设备是否被禁用
            registerHandler("isDeviceDisalbe", new WVJBHandler() {
                @Override
                public void request(Object data, WVJBResponseCallback callback) {
                    String deviceId = data.toString();
                    DeviceForbiddenBean deviceForbiddenBean = null;
                    String forbiddenDevice = mainApplication.forbiddenDevice;
                    if (!TextUtils.isEmpty(forbiddenDevice)) {
                        deviceForbiddenBean = JSON.parseObject(forbiddenDevice, DeviceForbiddenBean.class);
                    }
                    if (deviceForbiddenBean != null && TextUtils.equals(preference.getCurrentGatewayID(), deviceForbiddenBean.getGwID())) {
                        String flag = "NO";
                        if (deviceForbiddenBean.getType() == 1 && deviceForbiddenBean.getStatus() == 0) {
                            for (String devId :
                                    deviceForbiddenBean.getDevIDs()) {
                                if (TextUtils.equals(deviceId, devId)) {
                                    flag = "YES";
                                    break;
                                }
                            }
                        }
                        callback.callback(flag);
                    } else {
                        callback.callback("NO");
                    }
                }
            });
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            WLog.i(TAG, "onPageFinished: " + url);
            onPageDone(view, url);
            loadingView.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            onPageStart(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    public class NativeStorage {
        private static final String key_setItem = "setItem:";
        private static final String key_getItem = "getItem:";
        private static final String key_removeItem = "removeItem:";
        private static final String key_clear = "clear:";
        private static final String key_getLang = "getLang:";

        @JavascriptInterface
        public String sysfun(String json) {
            WLog.i(TAG, "sysfun: " + json);
            try {
                JSONObject object = new JSONObject(json);
                String sysFunc = object.optString("sysFunc");
                String room = object.optString("room");
                String id = object.optString("id");
                String data = object.optString("data");
                switch (sysFunc) {
                    case key_setItem:
                        return setItem(room, id, data);
                    case key_getItem:
                        String dd = getItem(room, id);
                        WLog.i(TAG, "getItem: result " + dd);
                        return getItem(room, id);
                    case key_removeItem:
                        return removeItem(room, id);
                    case key_clear:
                        return clear(room);
                    case key_getLang:
                        return LanguageUtil.getWulianCloudLanguage();
                    default:
                        return "sysFunc not found";
                }
            } catch (JSONException e) {
                return e.getMessage();
            }
        }

        public synchronized String getItem(String room, String id) {
            /*WLog.i(TAG, "getItem: id " + id);
            WLog.i(TAG, "getItem: query " + MainApplication.
                    getApplication().
                    getDaoSession().
                    getH5StorageDao().
                    queryBuilder().where(H5StorageDao.Properties.Room.eq(room), H5StorageDao.Properties.Key.eq(id))
                    .list());
            WLog.i(TAG, "getItem: list " + MainApplication.getApplication().getDaoSession().getH5StorageDao().queryBuilder().list());*/
            H5Storage storage = MainApplication.getApplication().getDaoSession().getH5StorageDao()
                    .queryBuilder()
                    .where(H5StorageDao.Properties.Room.eq(room), H5StorageDao.Properties.Key.eq(id))
                    .unique();
            if (storage != null) {
                return storage.getValue();
            } else {
                return null;
            }
        }

        public synchronized String setItem(String room, String id, String data) {
            H5Storage storage = MainApplication.getApplication().getDaoSession().getH5StorageDao()
                    .queryBuilder()
                    .where(H5StorageDao.Properties.Room.eq(room), H5StorageDao.Properties.Key.eq(id))
                    .unique();

            if (storage == null) {
                storage = new H5Storage(room, id, data);
                MainApplication.getApplication().getDaoSession().getH5StorageDao()
                        .save(storage);
            } else {
                storage.setValue(data);
                MainApplication.getApplication().getDaoSession().getH5StorageDao()
                        .update(storage);
            }

            return "YES";
        }

        private synchronized String removeItem(String room, String id) {
            H5Storage storage = MainApplication.getApplication().getDaoSession().getH5StorageDao()
                    .queryBuilder()
                    .where(H5StorageDao.Properties.Room.eq(room), H5StorageDao.Properties.Key.eq(id))
                    .unique();
            if (storage != null) {
                MainApplication.getApplication().getDaoSession().getH5StorageDao().deleteByKey(storage.getId());
            }
            return "YES";
        }

        private synchronized String clear(String room) {
            H5StorageDao dao = MainApplication.getApplication().getDaoSession().getH5StorageDao();
            dao.deleteInTx(dao.queryBuilder().where(H5StorageDao.Properties.Room.eq(room)).list());
            return "YES";
        }
    }
}
