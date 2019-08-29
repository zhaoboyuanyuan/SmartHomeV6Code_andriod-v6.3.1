package cc.wulian.smarthomev6.main.device.cateye;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.ICamCloudApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.ShareNoticeBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.tools.dialog.ShareDialog;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import uk.co.senab.photoview.PhotoView;

/**
 * 作者: chao
 * 时间: 2017/6/13
 * 描述: 猫眼报警详情页查看图片界面
 * 联系方式: 805901025@qq.com
 */

public class CateyePicActivity extends BaseTitleActivity {
    private PhotoView imageView;

    private String deviceId;
    private String cameraId;
    private String msg;
    private String pictureURL;
    private long time;
    private Device device;
    private ICamCloudApiUnit iCamCloudApiUnit;
    private boolean isChina;
    private String bucket;
    private String region;

    public static void start(Context context, String deviceId, String cameraId, String msg, long time, String pictureURL, String bucket, String region) {
        Intent intent = new Intent(context, CateyePicActivity.class);
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("cameraId", cameraId);
        intent.putExtra("msg", msg);
        intent.putExtra("pictureURL", pictureURL);
        intent.putExtra("time", time);
        intent.putExtra("bucket", bucket);
        intent.putExtra("region", region);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceId = getIntent().getStringExtra("deviceId");
        cameraId = getIntent().getStringExtra("cameraId");
        bucket = getIntent().getStringExtra("bucket");
        region = getIntent().getStringExtra("region");
        msg = getIntent().getStringExtra("msg");
        time = getIntent().getLongExtra("time", 0);
        device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        iCamCloudApiUnit = new ICamCloudApiUnit(this);
        setContentView(R.layout.activity_cateye_pic, true);
    }

    @Override
    protected void initTitle() {
        isChina = LanguageUtil.isChina();
        if (isChina) {
            setToolBarTitleAndRightImg(DeviceInfoDictionary.getNameByDevice(device), R.drawable.icon_btn_share);
        } else {
            setToolBarTitle(DeviceInfoDictionary.getNameByDevice(device));
        }
    }

    @Override
    protected void initView() {
        imageView = (PhotoView) findViewById(R.id.iv_pic);
    }

    @Override
    protected void initData() {
        pictureURL = getIntent().getStringExtra("pictureURL");
        if (device != null && TextUtils.equals(device.type, "CMICA4")) {
            ImageLoader.getInstance().displayImage(pictureURL, imageView);
        } else {
            iCamCloudApiUnit.doGetPic(deviceId, time, pictureURL, bucket, region, new ICamCloudApiUnit.IcamApiCommonListener<File>() {
                @Override
                public void onSuccess(File file) {
                    ImageLoader.getInstance().displayImage("file://" + file.getAbsolutePath().trim(), imageView);
                }

                @Override
                public void onFail(int code, String msg) {

                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.img_right) {
            if (device != null && TextUtils.equals(device.type, "CMICA4")) {
                shareUrl(pictureURL, null);
            } else {
                String id = deviceId;
                if (!TextUtils.isEmpty(cameraId)) {
                    id = cameraId;
                }
                iCamCloudApiUnit.doPostShare(id, pictureURL, null, new ICamCloudApiUnit.IcamApiCommonListener<ShareNoticeBean>() {
                    @Override
                    public void onSuccess(ShareNoticeBean bean) {
                        shareUrl(bean.pic, null);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        ToastUtil.show(msg);
                    }
                });
            }
        }
    }

    private void shareUrl(String picUrl, String videoUrl) {
        ShareDialog shareDialog = new ShareDialog(this);
        String type = "";
        if (device != null) {
            shareDialog.setShareTitle(String.format(getString(R.string.Share_Source), getString(DeviceInfoDictionary.getDefaultNameByType(device.type))));
            shareDialog.setShareMsg(msg);
            type = device.type;
        } else {
            shareDialog.setShareTitle(String.format(getString(R.string.Share_Source), getString(R.string.Message_Center_AlarmMessage)));
        }
        shareDialog.setShareUrl(picUrl, videoUrl, type);
        shareDialog.show();
    }
}
