package cc.wulian.smarthomev6.main.mine;

import android.Manifest;
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
import android.view.ViewGroup;
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

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.h5.H5BridgeActivity;
import cc.wulian.smarthomev6.main.h5.WVJBWebViewClient;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.SsoApiUnit;
import cc.wulian.smarthomev6.support.event.KeyboardEvent;
import cc.wulian.smarthomev6.support.tools.HttpUrlKey;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.BitmapUtil;
import cc.wulian.smarthomev6.support.utils.FileUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * 在线客服
 * @see cc.wulian.smarthomev6.main.mine.CustomerServiceTitleActivity
 */
@Deprecated
public class CustomerServiceActivity extends H5BridgeActivity {

    private static final int PORTRAIT_IMAGE_TAKE = 1;
    private static final int PORTRAIT_IMAGE_ALBUM = 2;
    private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    private static final int PERMISSION_CAMERA_REQUEST_CODE = 1;

    private SsoApiUnit mSsoApi;
    private WVJBWebViewClient.WVJBResponseCallback selectCallback, takeCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSsoApi = new SsoApiUnit(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    @Override
    protected String getUrl() {
        String url;
        if (TextUtils.equals(Preference.ENTER_TYPE_ACCOUNT, preference.getUserEnterType())) {
            url = HttpUrlKey.URL_CUSTOMER_SERVICE
                    + "?token=" + ApiConstant.getAppToken()
                    + "&uId=" + ApiConstant.getUserID();

        } else {
            url = HttpUrlKey.URL_CUSTOMER_SERVICE_GW;
        }
        return url;
    }

    @Override
    protected void registerHandler() {
        super.registerHandler();

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
                new DataApiUnit(CustomerServiceActivity.this).doGetServiceHistory(url, new DataApiUnit.DataApiCommonListener() {
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

        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == PORTRAIT_IMAGE_TAKE) {
                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");

                String path = getExternalCacheDir().getPath() + "/CustomerServiceCache.jpg";
                File file = new File(path);
                if (file.exists()) {
                    file.delete();
                }
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                    fos.flush();
                    fos.close();
                    if (takeCallback != null) {
                        takeCallback.callback(path);
                    }
//                    uploadPic(path);
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            } else if (requestCode == PORTRAIT_IMAGE_ALBUM) {
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
                    callback.callback(bean.toString());
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
        ViewGroup.LayoutParams p = mBottomSpace.getLayoutParams();
        p.height = event.keyboardHeight;
        mBottomSpace.setLayoutParams(p);
        if (event.type == KeyboardEvent.KEYBOARD_SHOW) {

        } else {

        }
    }
}
