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

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cc.wulian.smarthomev6.BuildConfig;
import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.MessageNewInfo;
import cc.wulian.smarthomev6.main.device.cateye.BcPlayVideoActivity;
import cc.wulian.smarthomev6.main.device.cateye.CateyePicActivity;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.ICamCloudApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.MessageBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamGetSipInfoBean;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by Veev on 2017/7/7
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    MessageAlarmAdapter
 */

public class BcAlarmAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_FIRST = 0;
    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_NORMAL_PIC = 2;
    public static final int TYPE_NORMAL_ICON = 3;
    private static String sodoMain = "";
    private Context mContext;
    private List<MessageNewInfo> mData;
    private ICamCloudApiUnit iCamCloudApiUnit;
    private DisplayImageOptions mOptions;
    private String deviceID = "";
    private String cameraId = "";

    public BcAlarmAdapter(Context context, String deviceID) {
        mData = new ArrayList<>();
        mContext = context;
        this.deviceID = deviceID;
        iCamCloudApiUnit = new ICamCloudApiUnit(mContext);
        sodoMain = "";
        mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.common_loading_icon)
//                .showImageOnFail(R.drawable.cateye_alarm_default)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

    }

    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
        getSodoMain(cameraId);
    }

    public void addNew(List<MessageBean.RecordListBean> data) {
        if (data.isEmpty()) {
            return;
        }
        if (mData.isEmpty()) {
            setData(data);
            return;
        }

        List<MessageNewInfo> list = new ArrayList<>();
        for (MessageBean.RecordListBean bean : data) {
            list.add(MessageNewInfo.getMessageBean(bean));
            WLog.i("MessageLogActivity", "Log: " + MessageNewInfo.getMessageBean(bean).getMsg());
        }

        list.get(0).setDayFirst(true);
        list.add(0, MessageNewInfo.getDateBean(list.get(0).getRecordListBean()));
        // 如果最新一条消息与 第一条消息是同一天
        if (list.get(list.size() - 1).isSameDay(mData.get(0))) {
            mData.remove(0);
            notifyItemRemoved(0);
            mData.get(0).setDayFirst(false);
            notifyItemChanged(0);
        } else {
            list.get(list.size() - 1).setDayLast(true);
        }
        for (int i = 1, count = list.size(); i < count; i++) {
            if (list.get(i).isDate()) {
                continue;
            }
            if (!list.get(i).isSameDay(list.get(i - 1))) {
                list.get(i).setDayFirst(true);
                list.add(i, MessageNewInfo.getDateBean(list.get(i).getRecordListBean()));
                count++;
                i++;
            }

            if (i<count - 1 && !list.get(i).isSameDay(list.get(i + 1))) {
                list.get(i).setDayLast(true);
            }
        }

        mData.addAll(0, list);
        WLog.i("MessageLogActivity", "addNew: " + list.size());
//        notifyItemRangeInserted(0, list.size());
        notifyItemInserted(0);
    }

    public void addOld(List<MessageBean.RecordListBean> data) {
        if (data.isEmpty()) {
            return;
        }
        List<MessageNewInfo> list = new ArrayList<>();
//        sort(data);
        for (MessageBean.RecordListBean bean : data) {
            list.add(MessageNewInfo.getMessageBean(bean));
        }

        int lastIndex = mData.size() - 1;
        if (!mData.isEmpty()) {
            if (mData.get(lastIndex).isSameDay(list.get(0))) {
                mData.get(lastIndex).setDayLast(false);
                notifyItemChanged(lastIndex);
            }
        }

        if (!list.get(0).isSameDay(mData.get(lastIndex))) {
            list.get(0).setDayFirst(true);
            list.add(0, MessageNewInfo.getDateBean(list.get(0).getRecordListBean()));
        }
        list.get(list.size() - 1).setDayLast(true);
        for (int i = 1, count = list.size() - 1; i < count; i++) {
            if (list.get(i).isDate()) {
                continue;
            }
            if (!list.get(i).isSameDay(list.get(i - 1))) {
                list.get(i).setDayFirst(true);
                list.add(i, MessageNewInfo.getDateBean(list.get(i).getRecordListBean()));
                count++;
                i++;
            }

            if (i < count - 1 && !list.get(i).isSameDay(list.get(i + 1))) {
                list.get(i).setDayLast(true);
            }
        }

        mData.addAll(list);
        notifyItemRangeInserted(lastIndex + 1, list.size());
    }

    public void setData(List<MessageBean.RecordListBean> data) {
        if (data.isEmpty()) {
            return;
        }
        List<MessageNewInfo> list = new ArrayList<>();
//        sort(data);
        for (MessageBean.RecordListBean bean : data) {
            list.add(MessageNewInfo.getMessageBean(bean));
        }

        list.get(0).setDayFirst(true);
        list.add(0, MessageNewInfo.getDateBean(list.get(0).getRecordListBean()));
        list.get(list.size() - 1).setDayLast(true);
        for (int i = 1, count = list.size(); i < count; i++) {
            if (list.get(i).isDate()) {
                continue;
            }
            if (!list.get(i).isSameDay(list.get(i - 1))) {
                list.get(i).setDayFirst(true);
                list.add(i, MessageNewInfo.getDateBean(list.get(i).getRecordListBean()));
                count++;
                i++;
            }

            if (i < count - 1 && !list.get(i).isSameDay(list.get(i + 1))) {
                list.get(i).setDayLast(true);
            }
        }

        mData = list;
        notifyDataSetChanged();
    }

    public long getFirstTime() {
        if (isEmpty()) {
            return 0;
        }

        return mData.get(0).getRecordListBean().time;
    }

    public int lastPosition() {
        return mData.size() - 1;
    }

    public boolean isEmpty() {
        return mData.isEmpty();
    }

    public void clear() {
        if (mData != null) {
            mData.clear();
            notifyDataSetChanged();
        }
    }

    public void sort(List<MessageBean.RecordListBean> data) {
        if (data == null) {
            return;
        }
        Collections.sort(data, new Comparator<MessageBean.RecordListBean>() {
            @Override
            public int compare(MessageBean.RecordListBean o1, MessageBean.RecordListBean o2) {
                if (o1.time < o2.time) {
                    return 1;
                } else if (o1.time > o2.time) {
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

        if (viewType == TYPE_NORMAL_PIC) {
            View itemView = layoutInflater.inflate(R.layout.item_alarm_detail_0104011, parent, false);
            return new MessageAlarmHolderPic(itemView);
        }

        if (viewType == TYPE_NORMAL_ICON) {
            View itemView = layoutInflater.inflate(R.layout.item_message_alarm_icon, parent, false);
            return new MessageAlarmIconHolder(itemView);
        }

        View itemView = layoutInflater.inflate(R.layout.item_message_alarm, parent, false);
        return new MessageAlarmHolder(itemView);
    }

    private final String TAG = BcAlarmAdapter.class.getSimpleName();

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        WLog.i(TAG, "onBindViewHolder: " + position);
        if (getItemViewType(position) == TYPE_NORMAL) {
            ((MessageAlarmHolder) holder).mTop.setVisibility(mData.get(position).isDayFirst() ? View.INVISIBLE : View.VISIBLE);
            ((MessageAlarmHolder) holder).mBottom.setVisibility(mData.get(position).isDayLast() ? View.INVISIBLE : View.VISIBLE);
            ((MessageAlarmHolder) holder).mTextTime.setText(mData.get(position).getDate());
            ((MessageAlarmHolder) holder).mTextMsg.setText(mData.get(position).getMsg());
        }

        if (getItemViewType(position) == TYPE_NORMAL_ICON) {
            ((MessageAlarmIconHolder) holder).mTop.setVisibility(mData.get(position).isDayFirst() ? View.INVISIBLE : View.VISIBLE);
            ((MessageAlarmIconHolder) holder).mBottom.setVisibility(mData.get(position).isDayLast() ? View.INVISIBLE : View.VISIBLE);
            ((MessageAlarmIconHolder) holder).mTextTime.setText(mData.get(position).getDate());
            ((MessageAlarmIconHolder) holder).mTextMsg.setText(mData.get(position).getMsg());
            try {
                ((MessageAlarmIconHolder) holder).mImageIcon.setBackgroundResource(getIconByAlarmCode(mData.get(position).getRecordListBean().messageCode));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (getItemViewType(position) == TYPE_NORMAL_PIC) {
            ((MessageAlarmHolderPic) holder).mTop.setVisibility(mData.get(position).isDayFirst() ? View.INVISIBLE : View.VISIBLE);
            ((MessageAlarmHolderPic) holder).mBottom.setVisibility(mData.get(position).isDayLast() ? View.INVISIBLE : View.VISIBLE);
            ((MessageAlarmHolderPic) holder).mTextTime.setText(mData.get(position).getDate());
            ((MessageAlarmHolderPic) holder).mTextMsg.setText(mData.get(position).getMsg());
            ((MessageAlarmHolderPic) holder).mImageView.setImageResource(R.drawable.eques_snapshot_default);
            final String msg = mData.get(position).getMsg();
            try {
                MessageBean.RecordListBean r = mData.get(position).getRecordListBean();
                String cameraID = (cameraId == null ? (r.endpoints.get(0).clusters.get(0).attributes.get(0).attributeValue.substring(2)) : cameraId);
                final String deviceId = (deviceID == null ? (r.devID) : deviceID);
                getSodoMain(cameraID);
                String extData = r.extData;
                String bucket = ApiConstant.BUCKET;
                String region = ApiConstant.REGION;
                if (r.extData1 != null){
                    JSONObject extData1 = new JSONObject(r.extData1);
                    bucket = extData1.getString("bucket");
                    region = extData1.getString("region");
                }
                if (TextUtils.isEmpty(extData) || TextUtils.isEmpty(cameraID)) {
                    ((MessageAlarmHolderPic) holder).mImageView.setVisibility(View.GONE);
                    ((MessageAlarmHolderPic) holder).mImagePlayMp4.setVisibility(View.GONE);
                } else {
                    ((MessageAlarmHolderPic) holder).mImageView.setVisibility(View.VISIBLE);
                    String picUrl = null;
                    String videoUrl = null;

                    //解析url
                    if (extData.endsWith("._PIR.h264")) {
                        String suffix = extData.substring(extData.lastIndexOf("."));
                        if (!extData.contains("/")) {
                            picUrl = "v/" + cameraID + "/" + extData;
                            videoUrl = "v/" + cameraID + "/" + extData;
                        }

                        if (TextUtils.equals(suffix, ".h264")) {
                            picUrl = extData;
                            picUrl = picUrl.replace(".h264", ".jpg");
                        }
                    } else if (extData.endsWith("_PIR.mp4")) {

                        if (!extData.contains("/")) {
                            videoUrl = "v/" + cameraID + "/" + extData;
                        }
                    } else if ((extData.endsWith("_PIR") || extData.endsWith("_RNG")) && extData.length() >= 18) {
                        String ss = extData.substring(0, 8);
                        String se = extData.substring(8, 18);
                        picUrl = ss + "/" + cameraID + "/" + se + ".jpg";
                    } else if (extData.endsWith("_TMP") && extData.length() >= 18) {
                        String ss = extData.substring(0, 8);
                        String se = extData.substring(8, 18);
                        picUrl = ss + cameraID + se + ".jpg";
                    } else if (extData.endsWith("_RNG") && extData.length() >= 18) {
                        String ss = extData.substring(0, 8);
                        String se = extData.substring(8, 18);
                        picUrl = ss + "/" + cameraID + "/" + se + ".jpg";
                    }

                    final ImageView img = (ImageView) ((MessageAlarmHolderPic) holder).itemView.findViewById(R.id.item_alarm_detail_image_pic);
                    if (!StringUtil.isNullOrEmpty(videoUrl)) {
                        String suffix = extData.substring(extData.lastIndexOf("."));
                        if (TextUtils.equals(suffix, ".mp4")) {
                            picUrl = videoUrl.replace(".mp4", ".jpg");
                        }
                        if (((MessageAlarmHolderPic) holder).mImagePlayMp4 != null) {
                            ((MessageAlarmHolderPic) holder).mImagePlayMp4.setVisibility(View.VISIBLE);
                        }
                    } else if (!StringUtil.isNullOrEmpty(picUrl)) {
                        if (((MessageAlarmHolderPic) holder).mImagePlayMp4 != null) {
                            ((MessageAlarmHolderPic) holder).mImagePlayMp4.setVisibility(View.GONE);
                        }
                    }

                    final String f_picUrl = picUrl;
                    final String f_videoUrl = videoUrl;
                    final String f_deviceId = deviceId;
                    final String f_cameraId = cameraID;
                    final long rtime = r.time;
                    WLog.d(TAG, "f_deviceId=" + f_deviceId + "  f_cameraId=" + f_cameraId + "   f_picUrl=" + f_picUrl + "   f_videoUrl=" + f_videoUrl);
                    iCamCloudApiUnit.doGetPic(f_cameraId, rtime, f_picUrl, bucket, region, new ICamCloudApiUnit.IcamApiCommonListener<File>() {
                        @Override
                        public void onSuccess(File file) {
                            WLog.d(TAG, "imgUrl=" + file.getAbsolutePath().trim());
                            ImageLoader.getInstance().displayImage("file://" + file.getAbsolutePath().trim(), img, mOptions);
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            WLog.d(TAG, "加载图片失败 code=" + code + " msg=" + msg);
                            ((MessageAlarmHolderPic) holder).mImageView.setVisibility(View.GONE);
                            ((MessageAlarmHolderPic) holder).mImagePlayMp4.setVisibility(View.GONE);
                        }
                    });
                    final String finalBucket = bucket;
                    final String finalRegion = region;
                    ((MessageAlarmHolderPic) holder).mImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!TextUtils.isEmpty(f_videoUrl)) {
                                BcPlayVideoActivity.start(BcAlarmAdapter.this.mContext, deviceId, f_cameraId, msg, f_picUrl, f_videoUrl, rtime, sodoMain, finalBucket, finalRegion);
                            } else if (!TextUtils.isEmpty(f_picUrl)) {
                                CateyePicActivity.start(mContext, f_deviceId, f_cameraId, msg, rtime, f_picUrl, finalBucket, finalRegion);
                            }
                        }
                    });
                }
            } catch (Exception e) {
                ((MessageAlarmHolderPic) holder).mImageView.setVisibility(View.GONE);
                ((MessageAlarmHolderPic) holder).mImagePlayMp4.setVisibility(View.GONE);
            }
        }

        if (getItemViewType(position) == TYPE_FIRST) {
            ((MessageDateHolder) holder).mTextDate.setText(mData.get(position).getFullDate());
        }
    }

    private int getIconByAlarmCode(String alarmCode) {
        switch (alarmCode) {
            case "0103041":
                return R.drawable.icon_alarm_door_nopen;
            case "0103031":
                return R.drawable.icon_alarm_destruction;
            case "0103011":
                return R.drawable.icon_alarm_lock;
            case "0103091":
                return R.drawable.icon_alarm_key;
            case "0103051":
                return R.drawable.icon_alarm_slope;
        }
        return 0;
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

        if (TextUtils.equals(mData.get(position).getRecordListBean().messageCode, "0103071")
                || TextUtils.equals(mData.get(position).getRecordListBean().messageCode, "0103061")) {
            return TYPE_NORMAL_PIC;
        }

        switch (mData.get(position).getRecordListBean().messageCode) {
            case "0103041":
            case "0103031":
            case "0103011":
            case "0103091":
            case "0103051":
                return TYPE_NORMAL_ICON;
        }

        return TYPE_NORMAL;
    }

    private class MessageAlarmHolder extends RecyclerView.ViewHolder {

        private View mTop, mBottom;
        private TextView mTextTime, mTextMsg;

        public MessageAlarmHolder(View itemView) {
            super(itemView);

            mTop = itemView.findViewById(R.id.item_alarm_detail_line_top);
            mBottom = itemView.findViewById(R.id.item_alarm_detail_line_bottom);
            mTextMsg = (TextView) itemView.findViewById(R.id.item_alarm_detail_text_msg);
            mTextTime = (TextView) itemView.findViewById(R.id.item_alarm_detail_text_date);
        }
    }

    private class MessageAlarmIconHolder extends RecyclerView.ViewHolder {

        private View mTop, mBottom;
        private TextView mTextTime, mTextMsg;
        private ImageView mImageIcon;

        public MessageAlarmIconHolder(View itemView) {
            super(itemView);

            mTop = itemView.findViewById(R.id.item_alarm_detail_line_top);
            mBottom = itemView.findViewById(R.id.item_alarm_detail_line_bottom);
            mTextMsg = (TextView) itemView.findViewById(R.id.item_alarm_detail_text_msg);
            mTextTime = (TextView) itemView.findViewById(R.id.item_alarm_detail_text_date);
            mImageIcon = (ImageView) itemView.findViewById(R.id.item_alarm_detail_image_icon);
        }
    }

    private class MessageAlarmHolderPic extends RecyclerView.ViewHolder {

        private View mTop, mBottom;
        private TextView mTextTime, mTextMsg;
        public ImageView mImageView;
        public View itemView = null;
        private ImageView mImagePlayMp4;

        public MessageAlarmHolderPic(View itemView) {
            super(itemView);
            this.itemView = itemView;
            mTop = itemView.findViewById(R.id.item_alarm_detail_line_top);
            mBottom = itemView.findViewById(R.id.item_alarm_detail_line_bottom);
            mTextMsg = (TextView) itemView.findViewById(R.id.item_alarm_detail_text_msg);
            mTextTime = (TextView) itemView.findViewById(R.id.item_alarm_detail_text_date);
            mImageView = (ImageView) itemView.findViewById(R.id.item_alarm_detail_image_pic);
            mImagePlayMp4 = (ImageView) itemView.findViewById(R.id.item_alarm_detail_image_pic_mp4);
        }
    }

    private class MessageDateHolder extends RecyclerView.ViewHolder {

        private TextView mTextDate;

        public MessageDateHolder(View itemView) {
            super(itemView);

            mTextDate = (TextView) itemView.findViewById(R.id.item_message_text_date);
        }
    }

    private void getSodoMain(String cameraId) {
        if (StringUtil.isNullOrEmpty(sodoMain)) {
            iCamCloudApiUnit.doGetSipInfo(cameraId, false, new ICamCloudApiUnit.IcamApiCommonListener<ICamGetSipInfoBean>() {
                @Override
                public void onSuccess(ICamGetSipInfoBean bean) {
                    if (bean != null) {
                        WLog.d(TAG, "sodoMain_success=" + bean.deviceDomain);
                        sodoMain = bean.deviceDomain;
                    }
                }

                @Override
                public void onFail(int code, String msg) {
                    WLog.d(TAG, "sodoMain_fail=" + msg);
                }
            });
        }
    }


}
