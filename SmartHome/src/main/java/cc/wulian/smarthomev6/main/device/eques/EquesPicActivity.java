package cc.wulian.smarthomev6.main.device.eques;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesAlarmDetailBean;
import cc.wulian.smarthomev6.support.core.apiunit.EquesApiUnit;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;

/**
 * 作者: chao
 * 时间: 2017/6/14
 * 描述: 移康猫眼报警详情页查看图片界面
 * 联系方式: 805901025@qq.com
 */

public class EquesPicActivity extends BaseTitleActivity {
    private ImageView imageView;

    private String deviceid;
    private Device device;
    private EquesAlarmDetailBean bean;

    private DisplayImageOptions mOptions;

    public static void start(Context context, String deviceid, EquesAlarmDetailBean bean) {
        Intent intent = new Intent(context, EquesPicActivity.class);
        intent.putExtra("deviceid", deviceid);
        intent.putExtra("equesAlarmDetailBean", bean);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceid = getIntent().getStringExtra("deviceid");
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
        bean = (EquesAlarmDetailBean) getIntent().getSerializableExtra("equesAlarmDetailBean");

        mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.common_loading_icon)
//                .showImageOnFail(R.drawable.cateye_alarm_default)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        MainApplication.getApplication().getEquesApiUnit().loadAlarmFile(bean.fid.get(0), deviceid, bean.type, bean.time, new EquesApiUnit.EquesFileDownloadListener() {
            @Override
            public void onSuccess(File file) {
                ImageLoader.getInstance().displayImage("file://"+ file.getAbsolutePath().trim(), imageView, mOptions);
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }
}
