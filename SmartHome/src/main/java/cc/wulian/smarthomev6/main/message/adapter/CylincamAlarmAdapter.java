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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.MessageNewInfo;
import cc.wulian.smarthomev6.main.device.cateye.CateyePicActivity;
import cc.wulian.smarthomev6.support.core.apiunit.ICamApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.ICamCloudApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.MessageBean;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by 上海滩小马哥 on 2017/10/10.
 */

public class CylincamAlarmAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_FIRST = 0;
    public static final int TYPE_NORMAL = 1;
    private static String sodoMain = "";
    private Context mContext;
    private List<MessageNewInfo> mData;
    private ICamApiUnit icamApiUnit;
    private ICamCloudApiUnit iCamCloudApiUnit;
    private DisplayImageOptions mOptions;
    private String deviceID = "";

    public CylincamAlarmAdapter(Context context) {
        mData = new ArrayList<>();
        mContext = context;
        icamApiUnit = new ICamApiUnit(this.mContext, Preference.getPreferences().getUserID());
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

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
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

            if (i < count - 1 && !list.get(i).isSameDay(list.get(i + 1))) {
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

        View itemView = layoutInflater.inflate(R.layout.item_alarm_detail_0104011, parent, false);
        return new MessageAlarmHolderPic(itemView);
    }

    private final String TAG = CylincamAlarmAdapter.class.getSimpleName();

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
//        holder.itemView.setTag(getItemViewType(position));
//        holder.itemView.setContentDescription(mData.get(position).getFullDate());

        WLog.i(TAG, "onBindViewHolder: " + position);

        if (getItemViewType(position) == TYPE_NORMAL) {
            ((MessageAlarmHolderPic) holder).mTop.setVisibility(mData.get(position).isDayFirst() ? View.INVISIBLE : View.VISIBLE);
            ((MessageAlarmHolderPic) holder).mBottom.setVisibility(mData.get(position).isDayLast() ? View.INVISIBLE : View.VISIBLE);
            ((MessageAlarmHolderPic) holder).mTextTime.setText(mData.get(position).getDate());
            ((MessageAlarmHolderPic) holder).mTextMsg.setText(mData.get(position).getMsg());
            ((MessageAlarmHolderPic) holder).mImagePlayMp4.setVisibility(View.GONE);

            try {
                MessageBean.RecordListBean r = mData.get(position).getRecordListBean();
                String extData = r.extData;
                final String deviceId = r.devID;
                final long time = r.time;
                if (TextUtils.isEmpty(extData)) {
                    ((MessageAlarmHolderPic) holder).mImageView.setVisibility(View.INVISIBLE);
                } else {
                    ((MessageAlarmHolderPic) holder).mImageView.setVisibility(View.VISIBLE);
                    final ImageView img = (ImageView) ((MessageAlarmHolderPic) holder).itemView.findViewById(R.id.item_alarm_detail_image_pic);
                    ImageLoader.getInstance().displayImage(extData, img, mOptions);

                    final String finalPicUrl1 = extData;
                    final String finalMsg = mData.get(position).getMsg();
                    ((MessageAlarmHolderPic) holder).mImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!TextUtils.isEmpty(finalPicUrl1)) {
                                CateyePicActivity.start(mContext, deviceId, null, finalMsg, time, finalPicUrl1, null, null);
                            }
                        }
                    });
                }
            } catch (Exception e) {
                ((MessageAlarmHolderPic) holder).mImageView.setVisibility(View.INVISIBLE);
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
