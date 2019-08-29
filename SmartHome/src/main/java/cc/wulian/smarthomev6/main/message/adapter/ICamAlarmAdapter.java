package cc.wulian.smarthomev6.main.message.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.CateyeVideoEntity;
import cc.wulian.smarthomev6.entity.ICamAlarmNewInfo;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.cateye.BcPlayVideoActivity;
import cc.wulian.smarthomev6.main.device.cateye.CateyePicActivity;
import cc.wulian.smarthomev6.main.device.cateye.CateyePlayVideoActivity;
import cc.wulian.smarthomev6.support.core.apiunit.ICamCloudApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamAlarmUrlBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamGetSipInfoBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.tools.AlarmTool;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * 作者: chao
 * 时间: 2017/7/19
 * 描述: 猫眼/随便看报警页adapter
 * 联系方式: 805901025@qq.com
 */
@Deprecated
public class ICamAlarmAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_FIRST = 0;
    public static final int TYPE_NORMAL = 1;
    private Context mContext;
    private ICamCloudApiUnit iCamCloudApiUnit;
    private DisplayImageOptions mOptions;
    private String deviceId;
    private String sdomain;
    private List<ICamAlarmNewInfo> mData;

    public ICamAlarmAdapter(Context mContext, String deviceId, String sdomain) {
        this.mContext = mContext;
        this.deviceId = deviceId;
        this.sdomain = sdomain;
        iCamCloudApiUnit = new ICamCloudApiUnit(mContext);
        mData = new ArrayList<>();
        getSodoMain(deviceId);
        mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.common_loading_icon)
//                .showImageOnFail(R.drawable.cateye_alarm_default)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    public void addOld(List<ICamAlarmUrlBean> data) {
        if (data.isEmpty()) {
            return;
        }
        List<ICamAlarmNewInfo> list = new ArrayList<>();
//        sort(data);
        for (ICamAlarmUrlBean bean : data) {
            list.add(ICamAlarmNewInfo.getMessageBean(bean));
        }

        /*if (mData.isEmpty()) {
            list.get(list.size() - 1).setDayLast(true);
        } else {
            if (!mData.get(1).isSameDay(list.get(list.size() - 1))) {
                list.get(list.size() - 1).setDayLast(true);
            } else {
                mData.remove(0);
                notifyItemRemoved(0);
                mData.get(0).setDayFirst(false);
                notifyItemChanged(0);
            }
        }*/

        int lastIndex = mData.size() - 1;
        if (!mData.isEmpty()) {
            if (mData.get(lastIndex).isSameDay(list.get(0))) {
                mData.get(lastIndex).setDayLast(false);
            }
        }

        if (!list.get(0).isSameDay(mData.get(lastIndex))) {
            list.get(0).setDayFirst(true);
            list.add(0, ICamAlarmNewInfo.getDateBean(list.get(0).getRecordListBean()));
        }
        list.get(list.size() - 1).setDayLast(true);
        for (int i = 1, count = list.size(); i < count; i++) {
            if (list.get(i).isDate()) {
                continue;
            }
            if (!list.get(i).isSameDay(list.get(i - 1))) {
                list.get(i).setDayFirst(true);
                list.add(i, ICamAlarmNewInfo.getDateBean(list.get(i).getRecordListBean()));
                count++;
                i++;
            }

            if (i<count - 1 && !list.get(i).isSameDay(list.get(i + 1))) {
                list.get(i).setDayLast(true);
            }
        }

        mData.addAll(list);
        notifyItemRangeInserted(lastIndex, list.size());
    }

    public void setData(List<ICamAlarmUrlBean> data) {
        if (data.isEmpty()) {
            return;
        }
        List<ICamAlarmNewInfo> list = new ArrayList<>();
//        sort(data);
        for (ICamAlarmUrlBean bean : data) {
            list.add(ICamAlarmNewInfo.getMessageBean(bean));
        }

        list.get(0).setDayFirst(true);
        list.add(0, ICamAlarmNewInfo.getDateBean(list.get(0).getRecordListBean()));
        list.get(list.size() - 1).setDayLast(true);
        for (int i = 1, count = list.size() - 1; i < count; i++) {
            if (list.get(i).isDate()) {
                continue;
            }
            if (!list.get(i).isSameDay(list.get(i - 1))) {
                list.get(i).setDayFirst(true);
                list.add(i, ICamAlarmNewInfo.getDateBean(list.get(i).getRecordListBean()));
                count++;
                i++;
            }

            if (i<count - 1 && !list.get(i).isSameDay(list.get(i + 1))) {
                list.get(i).setDayLast(true);
            }
        }

        mData = list;
        notifyDataSetChanged();
    }

    public int lastPosition() {
        return mData.size() - 1;
    }

    public boolean isEmpty() {
        return mData.isEmpty();
    }

    public void sort(List<ICamAlarmUrlBean> data) {
        if (data == null) {
            return;
        }
        Collections.sort(data, new Comparator<ICamAlarmUrlBean>() {
            @Override
            public int compare(ICamAlarmUrlBean o1, ICamAlarmUrlBean o2) {
                if (o1.createdat < o2.createdat) {
                    return 1;
                } else if (o1.createdat > o2.createdat) {
                    return -1;
                }
                return 0;
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        if (viewType == TYPE_FIRST) {
            View itemView = layoutInflater.inflate(R.layout.item_message_date, parent, false);
            return new MessageDateHolder(itemView);
        }

        View itemView = layoutInflater.inflate(R.layout.item_cateye_alarm_detail, parent, false);
        return new MessageAlarmHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Device device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        WLog.i("MessageAlarmAdapter", "onBindViewHolder: " + position);
        if (getItemViewType(position) == TYPE_NORMAL) {
            ((MessageAlarmHolder) holder).mTop.setVisibility(mData.get(position).isDayFirst() ? View.INVISIBLE : View.VISIBLE);
            ((MessageAlarmHolder) holder).mBottom.setVisibility(mData.get(position).isDayLast() ? View.INVISIBLE : View.VISIBLE);
            ((MessageAlarmHolder) holder).mTextTime.setText(mData.get(position).getDate());
            String msg = "";
            if (TextUtils.equals(device.type, "CMICA1")){
                if (TextUtils.isEmpty(mData.get(position).url) || mData.get(position).url.endsWith("PIR.jpg")){
                    ((MessageAlarmHolder) holder).mTextMsg.setText(String.format(mContext.getString(R.string.CateyeVisitor_alarm), ""));
                    msg = String.format(mContext.getString(R.string.CateyeVisitor_alarm), "");
                }else if (mData.get(position).url.endsWith("Ring.jpg")){
                    ((MessageAlarmHolder) holder).mTextMsg.setText(String.format(mContext.getString(R.string.CateyeVisitor_call),  ""));
                    msg = String.format(mContext.getString(R.string.CateyeVisitor_call),  "");
                }
            }else if (TextUtils.equals(device.type, "CMICA2")||TextUtils.equals(device.type, "CMICA3")||TextUtils.equals(device.type, "CMICA5")||TextUtils.equals(device.type, "CMICA6")){
                ((MessageAlarmHolder) holder).mTextMsg.setText(String.format(mContext.getString(R.string.Detection_Picture_Change),  ""));
                msg = String.format(mContext.getString(R.string.Detection_Picture_Change),  "");
            }
            final String uri = mData.get(position).url;
            final String pictureUrl = mData.get(position).url_pic;
            final String videoUrl = mData.get(position).url_video;
            final ICamAlarmUrlBean iCamAlarmUrlBean = mData.get(position).getRecordListBean();
            final String finalMsg = msg;

            if (!TextUtils.isEmpty(uri)) {
                ((MessageAlarmHolder) holder).ivAlarmPlay.setVisibility(View.GONE);
                ((MessageAlarmHolder) holder).ivAlarmPlay.setOnClickListener(null);
                //此刻为抓拍，调下载图片的接口
//                ((MessageAlarmHolder) holder).ivAlarmPic.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        CateyePicActivity.start(mContext, deviceId, null, finalMsg, iCamAlarmUrlBean.createdat, uri);
//                    }
//                });
//                iCamCloudApiUnit.doGetPic(deviceId, iCamAlarmUrlBean.createdat, mData.get(position).url, new ICamCloudApiUnit.IcamApiCommonListener<File>() {
//                    @Override
//                    public void onSuccess(File file) {
//                        ImageLoader.getInstance().displayImage("file://"+ file.getAbsolutePath().trim(), ((MessageAlarmHolder) holder).ivAlarmPic, mOptions);
//                    }
//
//                    @Override
//                    public void onFail(int code, String msg) {
//
//                    }
//                });
            } else {
                ((MessageAlarmHolder) holder).ivAlarmPlay.setVisibility(View.VISIBLE);
                ((MessageAlarmHolder) holder).ivAlarmPic.setOnClickListener(null);
                iCamCloudApiUnit.doGetVideo(deviceId, pictureUrl, videoUrl, sdomain, iCamAlarmUrlBean.createdat, new ICamCloudApiUnit.IcamApiCommonListener<CateyeVideoEntity>() {
                    @Override
                    public void onSuccess(final CateyeVideoEntity bean) {
                        ImageLoader.getInstance().displayImage("file://"+ bean.picUrl, ((MessageAlarmHolder) holder).ivAlarmPic, mOptions);
                        ((MessageAlarmHolder) holder).ivAlarmPlay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (videoUrl.endsWith(".mp4")){
//                                    BcPlayVideoActivity.start(mContext, deviceId, null,videoUrl, iCamAlarmUrlBean.createdat, sdomain);
                                }else {
                                    CateyePlayVideoActivity.start(mContext, bean.videoUrl, getFileName(videoUrl));
                                }
                            }
                        });
                    }

                    @Override
                    public void onFail(int code, String msg) {

                    }
                });
            }
        }

        if (getItemViewType(position) == TYPE_FIRST) {
            ((MessageDateHolder) holder).mTextDate.setText(mData.get(position).getFullDate());
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.get(position).isDate()) {
            return TYPE_FIRST;
        }
        return TYPE_NORMAL;
    }

    private String getFileName(String url) {
        String data[] = new String[2];
        data = url.split("/");
        String name = data[2];
        WLog.i("FileName", name);
        return name;
    }

    private class MessageAlarmHolder extends RecyclerView.ViewHolder {

        private View mTop, mBottom;
        private TextView mTextTime, mTextMsg;
        private ImageView ivAlarmPic;
        private ImageView ivAlarmPlay;
        public MessageAlarmHolder(View itemView) {
            super(itemView);

            mTop = itemView.findViewById(R.id.item_alarm_detail_line_top);
            mBottom = itemView.findViewById(R.id.item_alarm_detail_line_bottom);
            mTextMsg = (TextView) itemView.findViewById(R.id.item_alarm_detail_text_msg);
            mTextTime = (TextView) itemView.findViewById(R.id.item_alarm_detail_text_date);
            ivAlarmPic = (ImageView) itemView.findViewById(R.id.item_alarm_detail_img);
            ivAlarmPlay = (ImageView) itemView.findViewById(R.id.item_alarm_detail_play);
        }
    }

    private class MessageDateHolder extends RecyclerView.ViewHolder {

        private TextView mTextDate;

        public MessageDateHolder(View itemView) {
            super(itemView);

            mTextDate = (TextView) itemView.findViewById(R.id.item_message_text_date);
        }
    }
    private void getSodoMain(String deviceId){
        if(StringUtil.isNullOrEmpty(sdomain)){
            iCamCloudApiUnit.doGetSipInfo(deviceId, false, new ICamCloudApiUnit.IcamApiCommonListener<ICamGetSipInfoBean>() {
                @Override
                public void onSuccess(ICamGetSipInfoBean bean) {
                    sdomain=bean.deviceDomain;
                }

                @Override
                public void onFail(int code, String msg) {

                }
            });
        }
    }
}
