package cc.wulian.smarthomev6.main.message.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.MessageLcInfo;
import cc.wulian.smarthomev6.main.device.camera_lc.LcAlarmPicActivity;
import cc.wulian.smarthomev6.main.device.camera_lc.business.util.ImageHelper;
import cc.wulian.smarthomev6.support.core.apiunit.ICamCloudApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.LcAlarmBean;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by huxc
 * Function:    乐橙报警消息adapter
 */

public class LcAlarmAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_FIRST = 0;
    public static final int TYPE_NORMAL_PIC = 1;
    private Context mContext;
    private List<MessageLcInfo> mData;
    private ICamCloudApiUnit iCamCloudApiUnit;
    private DisplayImageOptions mOptions;
    private String deviceID = "";

    public LcAlarmAdapter(Context context, String deviceId) {
        mData = new ArrayList<>();
        this.mContext = context;
        this.deviceID = deviceId;
        iCamCloudApiUnit = new ICamCloudApiUnit(mContext);
        mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.common_loading_icon)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

    }

    public void addNew(List<LcAlarmBean.RecordListBean> data) {
        if (data.isEmpty()) {
            return;
        }
        if (mData.isEmpty()) {
            setData(data);
            return;
        }

        List<MessageLcInfo> list = new ArrayList<>();
        for (LcAlarmBean.RecordListBean bean : data) {
            list.add(MessageLcInfo.getMessageBean(bean));
            WLog.i("MessageLogActivity", "Log: " + MessageLcInfo.getMessageBean(bean).getMsg());
        }

        list.get(0).setDayFirst(true);
        list.add(0, MessageLcInfo.getDateBean(list.get(0).getRecordListBean()));
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
                list.add(i, MessageLcInfo.getDateBean(list.get(i).getRecordListBean()));
                count++;
                i++;
            }

            if (i < count - 1 && !list.get(i).isSameDay(list.get(i + 1))) {
                list.get(i).setDayLast(true);
            }
        }

        mData.addAll(0, list);
        WLog.i("MessageLogActivity", "addNew: " + list.size());
//        notifyItemRangeInserted(0, list.size());
        notifyItemInserted(0);
    }

    public void addOld(List<LcAlarmBean.RecordListBean> data) {
        if (data.isEmpty()) {
            return;
        }
        List<MessageLcInfo> list = new ArrayList<>();
//        sort(data);
        for (LcAlarmBean.RecordListBean bean : data) {
            list.add(MessageLcInfo.getMessageBean(bean));
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
            list.add(0, MessageLcInfo.getDateBean(list.get(0).getRecordListBean()));
        }
        list.get(list.size() - 1).setDayLast(true);
        for (int i = 1, count = list.size() - 1; i < count; i++) {
            if (list.get(i).isDate()) {
                continue;
            }
            if (!list.get(i).isSameDay(list.get(i - 1))) {
                list.get(i).setDayFirst(true);
                list.add(i, MessageLcInfo.getDateBean(list.get(i).getRecordListBean()));
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

    public void setData(List<LcAlarmBean.RecordListBean> data) {
        if (data.isEmpty()) {
            return;
        }
        List<MessageLcInfo> list = new ArrayList<>();
//        sort(data);
        for (LcAlarmBean.RecordListBean bean : data) {
            list.add(MessageLcInfo.getMessageBean(bean));
        }

        list.get(0).setDayFirst(true);
        list.add(0, MessageLcInfo.getDateBean(list.get(0).getRecordListBean()));
        list.get(list.size() - 1).setDayLast(true);
        for (int i = 1, count = list.size(); i < count; i++) {
            if (list.get(i).isDate()) {
                continue;
            }
            if (!list.get(i).isSameDay(list.get(i - 1))) {
                list.get(i).setDayFirst(true);
                list.add(i, MessageLcInfo.getDateBean(list.get(i).getRecordListBean()));
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

        return mData.get(0).getRecordListBean().getTime();
    }

    public void clear() {
        if (mData != null) {
            mData.clear();
            notifyDataSetChanged();
        }
    }

    public int lastPosition() {
        return mData.size() - 1;
    }

    public boolean isEmpty() {
        return mData.isEmpty();
    }

    public void sort(List<LcAlarmBean.RecordListBean> data) {
        if (data == null) {
            return;
        }
        Collections.sort(data, new Comparator<LcAlarmBean.RecordListBean>() {
            @Override
            public int compare(LcAlarmBean.RecordListBean o1, LcAlarmBean.RecordListBean o2) {
                if (o1.getTime() < o2.getTime()) {
                    return 1;
                } else if (o1.getTime() > o2.getTime()) {
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

        View itemView = layoutInflater.inflate(R.layout.item_message_alarm, parent, false);
        return new MessageAlarmHolder(itemView);
    }

    private final String TAG = LcAlarmAdapter.class.getSimpleName();

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        WLog.i(TAG, "onBindViewHolder: " + position);

        if (getItemViewType(position) == TYPE_NORMAL_PIC) {
            ((MessageAlarmHolderPic) holder).mTop.setVisibility(mData.get(position).isDayFirst() ? View.INVISIBLE : View.VISIBLE);
            ((MessageAlarmHolderPic) holder).mBottom.setVisibility(mData.get(position).isDayLast() ? View.INVISIBLE : View.VISIBLE);
            ((MessageAlarmHolderPic) holder).mTextTime.setText(mData.get(position).getDate());
            ((MessageAlarmHolderPic) holder).mTextMsg.setText(R.string.MessageCode_Widget_Key_0103071);

            LcAlarmBean.RecordListBean r = mData.get(position).getRecordListBean();
            final String thumbUrl = r.getThumbUrl();
            final String picUrl = r.getPicurlArray().get(0);
            if (!TextUtils.isEmpty(thumbUrl)) {
                //下载
                ImageHelper.loadCacheImage(thumbUrl, deviceID, deviceID, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        // TODO Auto-generated method stub
                        super.handleMessage(msg);
                        ((MessageAlarmHolderPic) holder).mImageView.setBackgroundDrawable((Drawable) msg.obj);
                    }
                });
            }
            ((MessageAlarmHolderPic) holder).mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(picUrl)) {
                        LcAlarmPicActivity.start(mContext, deviceID, picUrl);
                    }
                }
            });
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

        return TYPE_NORMAL_PIC;
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

}
