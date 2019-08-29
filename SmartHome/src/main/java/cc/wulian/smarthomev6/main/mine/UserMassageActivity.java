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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.support.core.apiunit.SsoApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.UserBean;
import cc.wulian.smarthomev6.support.customview.CircleImageView;
import cc.wulian.smarthomev6.support.customview.popupwindow.ChoosePortraitPopupWindow;
import cc.wulian.smarthomev6.support.tools.ImageLoaderTool;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.BitmapUtil;
import cc.wulian.smarthomev6.support.utils.FileUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by syf on 2017/2/16
 */
public class UserMassageActivity extends BaseTitleActivity {

    private static final String RENAME = "RENAME";
    private static final int PORTRAIT_IMAGE_TAKE = 1;
    private static final int PORTRAIT_IMAGE_ALBUM = 2;
    private static final int CROP_PICTURE = 3;
    private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    private static final int PERMISSION_CAMERA_REQUEST_CODE = 1;
    private LinearLayout portraitLayout;
    private LinearLayout nameLayout;
    private CircleImageView portraitImageView;
    private TextView nameTextView;
    private TextView idTextView;
    private ChoosePortraitPopupWindow choosePortraitPopupWindow;
    private WLDialog.Builder builder;
    private WLDialog dialog;
    private SsoApiUnit ssoApiUnit;
    private UserBean userBean;

    private static final String IMAGE_CAPTURE_LOCATION = "file:///" + FileUtil.getTempDirectoryPath() + "/avatarCache.jpg";
    private static final Uri CAPTURE_IMAGE_URI = Uri.parse(IMAGE_CAPTURE_LOCATION);
    private static final String IMAGE_FILE_LOCATION = "file:///" + FileUtil.getTempDirectoryPath() + "/tempCrop.jpg";
    private static final Uri CROP_IMAGE_URI = Uri.parse(IMAGE_FILE_LOCATION);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ssoApiUnit = new SsoApiUnit(this);
        setContentView(R.layout.activity_user_massage, true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.PersonalInfo_PersonalInfo));
    }

    @Override
    protected void initView() {
        portraitLayout = (LinearLayout) findViewById(R.id.setting_manager_item_name_ly);
        nameLayout = (LinearLayout) findViewById(R.id.setting_manager_item_name);
        portraitImageView = (CircleImageView) findViewById(R.id.account_icon);
        nameTextView = (TextView) findViewById(R.id.setting_manager_item_name_tv);
        idTextView = (TextView) findViewById(R.id.setting_manager_item_id_tv);
    }

    @Override
    protected void initData() {
        choosePortraitPopupWindow = new ChoosePortraitPopupWindow(UserMassageActivity.this);
        userBean = JSON.parseObject(preference.getCurrentAccountInfo(), UserBean.class);
        if (userBean != null) {
            if (TextUtils.isEmpty(userBean.nick)) {
                nameTextView.setText(userBean.phone);
            } else {
                nameTextView.setText(userBean.nick);
            }
            idTextView.setText(userBean.phone);
            ImageLoader.getInstance().displayImage(userBean.avatar, portraitImageView, ImageLoaderTool.getUserAvatarOptions());
        }
    }


    @Override
    protected void initListeners() {
        portraitLayout.setOnClickListener(this);
        nameLayout.setOnClickListener(this);
        choosePortraitPopupWindow.setPopupClickListener(new ChoosePortraitPopupWindow.OnPopupClickListener() {
            @Override
            public void onTakePhoto() {
                // 启动相机
                checkCameraPermission();
            }

            @Override
            public void onAlbum() {
                //调用系统相册
                startAlbum();
            }
        });
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this,
                PERMISSION_CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSION_CAMERA)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{PERMISSION_CAMERA},
                        PERMISSION_CAMERA_REQUEST_CODE);

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{PERMISSION_CAMERA},
                        PERMISSION_CAMERA_REQUEST_CODE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
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

    private void startAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PORTRAIT_IMAGE_ALBUM);
    }

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);

        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

//        intent.putExtra("outputX", 600);
//        intent.putExtra("outputY", 300);

        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, CROP_IMAGE_URI);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection

        startActivityForResult(intent, CROP_PICTURE);
    }

    private void showChangeNameDialog() {
        builder = new WLDialog.Builder(this);
        builder.setTitle(getString(R.string.PersonalInfo_ChangeName))
                .setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setEditTextHint(R.string.EditText_UserInfo_Nick)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        if (!StringUtil.isNullOrEmpty(msg)) {
                            changeName(msg);
                        }
                    }

                    @Override
                    public void onClickNegative(View view) {

                    }
                });
        dialog = builder.create();
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.setting_manager_item_name_ly:
                choosePortraitPopupWindow.showParent(v);
                break;
            case R.id.setting_manager_item_name:
                showChangeNameDialog();
                break;
            default:
                break;
        }
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
        WLog.i("User", "requestCode: " + requestCode + ", resultCode: " + resultCode);
        //获取图片路径
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PORTRAIT_IMAGE_TAKE) {
//                Bundle bundle = data.getExtras();
//                Bitmap bitmap = (Bitmap) bundle.get("data");
//
//                String path = getExternalCacheDir().getPath() + "/avatarCache.jpg";
//                File file = new File(path);
//                if (file.exists()) {
//                    file.delete();
//                }
//                try {
//                    FileOutputStream fos = new FileOutputStream(file);
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
//                    fos.flush();
//                    fos.close();
//                    changeAvatar(path);
//                } catch (IOException e2) {
//                    e2.printStackTrace();
//                }
                startPhotoZoom(CAPTURE_IMAGE_URI);
            } else if (requestCode == PORTRAIT_IMAGE_ALBUM && data != null) {
                Uri selectedImage = data.getData();
                startPhotoZoom(selectedImage);
//                String[] filePathColumns = {MediaStore.Images.Media.DATA};
//                Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
//                c.moveToFirst();
//                int columnIndex = c.getColumnIndex(filePathColumns[0]);
//                String imageUri = c.getString(columnIndex);
//                c.close();
//                changeAvatar(imageUri);
            } else if (requestCode == CROP_PICTURE) {
                changeAvatar(CROP_IMAGE_URI.getPath());
            }
        }
    }

    private void changeAvatar(String path) {
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
                while (size >= 200) {
                    Matrix matrix = new Matrix();
                    matrix.postScale(0.8f, 0.8f);
                    newBitmap = Bitmap.createBitmap(originBitmap, 0, 0, originBitmap.getWidth(), originBitmap.getHeight(), matrix, true);
                    size = newBitmap.getHeight() * newBitmap.getRowBytes() / 1024;
                    if (size >= 200) {
                        originBitmap = newBitmap;
                    }
                    WLog.i("User", "changeAvatar: " + size + "kb");
                }
                File file1 = new File(pathThumb);
                if (file1.exists()) {
                    file1.delete();
                }
                WLog.i("User", "changeAvatarStop: " + size + "kb");
                boolean isSuccess = BitmapUtil.saveBitmap(newBitmap, file1);
                originBitmap.recycle();
                newBitmap.recycle();
                path = pathThumb;
                WLog.i("User", "changeAvatar: " + path);
                WLog.i("User", "changeAvatar: " + size + "kb");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        progressDialogManager.showDialog("0", UserMassageActivity.this, null, null, getResources().getInteger(R.integer.http_timeout));
        ssoApiUnit.doChangeAvatar(path, new SsoApiUnit.SsoApiCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                progressDialogManager.dimissDialog("0", 0);
                ToastUtil.show(R.string.PersonalInfo_ChangeName_Successful);
                getUserInfo();
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog("0", 0);
                ToastUtil.show(msg);
            }
        });
    }

    private void changeName(String name) {
        progressDialogManager.showDialog(RENAME, this, null, null, getResources().getInteger(R.integer.http_timeout));
        ssoApiUnit.doChangeUserInfo(name, new SsoApiUnit.SsoApiCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                dialog.dismiss();
                ToastUtil.show(R.string.PersonalInfo_ChangeName_Successful);
                getUserInfo();
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(RENAME, 0);
                ToastUtil.show(msg);
            }
        });
    }

    private void getUserInfo() {
        ssoApiUnit.doGetUserInfo(new SsoApiUnit.SsoApiCommonListener<UserBean>() {
            @Override
            public void onSuccess(final UserBean bean) {
                progressDialogManager.dimissDialog(RENAME, 0);
                if (TextUtils.isEmpty(bean.nick)) {
                    nameTextView.setText(bean.phone);
                } else {
                    nameTextView.setText(bean.nick);
                }
                idTextView.setText(bean.phone);
                ImageLoader.getInstance().displayImage(bean.avatar, portraitImageView, ImageLoaderTool.getUserAvatarOptions());
                WLog.i("User", "onSuccess: " + userBean.avatar);
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(RENAME, 0);
                ToastUtil.show(msg);
            }
        });
    }
}
