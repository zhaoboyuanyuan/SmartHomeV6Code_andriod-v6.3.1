package cc.wulian.smarthomev6.main.device.eques;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;

/**
 * 作者: chao
 * 时间: 2017/6/15
 * 描述: 移康猫眼放个记录图片查看页
 * 联系方式: 805901025@qq.com
 */

public class EquesVisitorPicActivity extends BaseTitleActivity {
    public static String KEY_PATH = "KEY_PATH";
    public static String KEY_DEVICEID = "KEY_DEVICEID";

    private ImageView imageView;

    private String deviceid;
    private String path;
    private Device device;
    private DisplayImageOptions mOptions;

    public static void start(Context context, String deviceid, String path) {
        Intent intent = new Intent(context, EquesVisitorPicActivity.class);
        intent.putExtra(KEY_PATH, path);
        intent.putExtra(KEY_DEVICEID, deviceid);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceid = getIntent().getStringExtra(KEY_DEVICEID);
        device = MainApplication.getApplication().getDeviceCache().get(deviceid);
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
        path = getIntent().getStringExtra(KEY_PATH);
        mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.common_loading_icon)
//                .showImageOnFail(R.drawable.cateye_alarm_default)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        ImageLoader.getInstance().displayImage("file://"+ path, imageView, mOptions);
    }
}
