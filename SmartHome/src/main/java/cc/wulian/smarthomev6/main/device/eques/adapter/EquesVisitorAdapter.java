package cc.wulian.smarthomev6.main.device.eques.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLBaseAdapter;
import cc.wulian.smarthomev6.main.device.eques.EquesVisitorPicActivity;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesVisitorDetailBean;
import cc.wulian.smarthomev6.support.core.apiunit.EquesApiUnit;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.utils.FileUtil;

/**
 * 作者: chao
 * 时间: 2017/6/15
 * 描述:
 * 联系方式: 805901025@qq.com
 */

public class EquesVisitorAdapter extends WLBaseAdapter<EquesVisitorDetailBean> {
    private DisplayImageOptions mOptions;
    private Device device;
    private String deviceId;

    public EquesVisitorAdapter(Context context, String deviceId, List<EquesVisitorDetailBean> data) {
        super(context, data);
        this.deviceId = deviceId;
        device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.common_loading_icon)
//                .showImageOnFail(R.drawable.cateye_alarm_default)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    public void addMore(List<EquesVisitorDetailBean> data) {
        mData.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    protected View newView(Context context, LayoutInflater inflater, ViewGroup parent, int pos) {
        return inflater.inflate(R.layout.item_cateye_visitor, null, false);
    }

    @Override
    protected void bindView(final Context context, View view, int pos, final EquesVisitorDetailBean item) {
        final ImageView img = (ImageView) view.findViewById(R.id.item_alarm_detail_image);
        ImageView state = (ImageView) view.findViewById(R.id.item_alarm_detail_text_img);
        TextView time = (TextView) view.findViewById(R.id.item_alarm_detail_text_date);
        TextView msg = (TextView) view.findViewById(R.id.item_alarm_detail_text_msg);

        time.setText(stampToDate(item.ringtime));
//        msg.setText(String.format(context.getString(R.string.CateyeVisitor_call), DeviceInfoDictionary.getNameByDevice(device)));
        msg.setText(String.format(context.getString(R.string.CateyeVisitor_call), ""));

        MainApplication.getApplication().getEquesApiUnit().loadRingPic(item.fid, item.bid,
                item.ringtime+ "", new EquesApiUnit.EquesFileDownloadListener() {
                    @Override
                    public void onSuccess(File file) {
                        ImageLoader.getInstance().displayImage("file://"+ file.getAbsolutePath().trim(), img, mOptions);
                    }

                    @Override
                    public void onFail(int code, String msg) {

                    }
                });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EquesVisitorPicActivity.start(context, deviceId, FileUtil.getEquesRingPath() + "/"+ deviceId + "/" + item.ringtime + ".jpg");
            }
        });
    }

    public static String stampToDate(long s)  {
        Date date = new Date();
        DateFormat dateformat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        date.setTime(s);
        return dateformat.format(date);
    }
}
