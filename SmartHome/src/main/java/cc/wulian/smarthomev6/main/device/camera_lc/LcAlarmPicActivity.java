package cc.wulian.smarthomev6.main.device.camera_lc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.camera_lc.business.util.ImageHelper;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesAlarmDetailBean;
import cc.wulian.smarthomev6.main.message.adapter.LcAlarmAdapter;
import cc.wulian.smarthomev6.support.core.apiunit.EquesApiUnit;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;

/**
 * 作者: chao
 * 时间: 2017/6/14
 * 描述: 乐橙摄像机报警详情页查看图片界面
 * 联系方式: 805901025@qq.com
 */

public class LcAlarmPicActivity extends BaseTitleActivity {
    private ImageView imageView;

    private String deviceId;
    private Device device;
    private String picUrl;


    public static void start(Context context, String deviceId, String picUrl) {
        Intent intent = new Intent(context, LcAlarmPicActivity.class);
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("picUrl", picUrl);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceId = getIntent().getStringExtra("deviceId");
        picUrl = getIntent().getStringExtra("picUrl");
        device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        setContentView(R.layout.activity_cateye_pic, true);
    }

    @Override
    protected void initTitle() {
        setToolBarTitle(DeviceInfoDictionary.getNameByDevice(device));
    }

    @Override
    protected void initView() {
        imageView = (ImageView) findViewById(R.id.iv_pic);
    }

    @Override
    protected void initData() {
        if (!TextUtils.isEmpty(picUrl)) {
            //下载
            ImageHelper.loadCacheImage(picUrl, deviceId, deviceId, new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    // TODO Auto-generated method stub
                    super.handleMessage(msg);
                    imageView.setImageDrawable((Drawable) msg.obj);
                }
            });
        }
    }
}
