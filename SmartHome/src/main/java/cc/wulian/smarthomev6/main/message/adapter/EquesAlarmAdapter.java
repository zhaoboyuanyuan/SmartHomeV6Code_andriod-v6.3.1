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
import cc.wulian.smarthomev6.entity.EquesAlarmNewInfo;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.eques.EquesPicActivity;
import cc.wulian.smarthomev6.main.device.eques.EquesPicsActivity;
import cc.wulian.smarthomev6.main.device.eques.EquesVideoActivity;
import cc.wulian.smarthomev6.main.device.eques.EquesVisitorPicActivity;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesAlarmDetailBean;
import cc.wulian.smarthomev6.support.core.apiunit.EquesApiUnit;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.utils.FileUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * 作者: chao
 * 时间: 2017/7/18
 * 描述: 移康猫眼报警列表adapter
 * 联系方式: 805901025@qq.com
 */

public class EquesAlarmAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_FIRST = 0;
    public static final int TYPE_NORMAL = 1;
    private Context mContext;
    private List<EquesAlarmNewInfo> mData;
    private DisplayImageOptions mOptions;
    private String deviceId;

    public EquesAlarmAdapter(Context mContext, String deviceId) {
        this.mContext = mContext;
        this.deviceId = deviceId;
        mData = new ArrayList<>();

        mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.common_loading_icon)
//                .showImageOnFail(R.drawable.cateye_alarm_default)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    public long getFirstTime() {
        if (isEmpty()) {
            return 0;
        }

        return mData.get(0).getRecordListBean().time;
    }

    public void addOld(List<EquesAlarmDetailBean> data) {
        if (data.isEmpty()) {
            return;
        }
        List<EquesAlarmNewInfo> list = new ArrayList<>();
//        sort(data);
        for (EquesAlarmDetailBean bean : data) {
            list.add(EquesAlarmNewInfo.getMessageBean(bean));
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
                notifyItemChanged(lastIndex);
            }
        }

        if (!list.get(0).isSameDay(mData.get(lastIndex))) {
            list.get(0).setDayFirst(true);
            list.add(0, EquesAlarmNewInfo.getDateBean(list.get(0).getRecordListBean()));
        }
        list.get(list.size() - 1).setDayLast(true);
        for (int i = 1, count = list.size() - 1; i < count; i++) {
            if (list.get(i).isDate()) {
                continue;
            }
            if (!list.get(i).isSameDay(list.get(i - 1))) {
                list.get(i).setDayFirst(true);
                list.add(i, EquesAlarmNewInfo.getDateBean(list.get(i).getRecordListBean()));
                count++;
                i++;
            }

            if (i<count - 1 && !list.get(i).isSameDay(list.get(i + 1))) {
                list.get(i).setDayLast(true);
            }
        }

        mData.addAll(list);
//        sortAll(mData);
        notifyItemRangeInserted(lastIndex + 1, list.size());
    }

    public void setData(List<EquesAlarmDetailBean> data) {
        if (data.isEmpty()) {
            return;
        }
        List<EquesAlarmNewInfo> list = new ArrayList<>();
//        sort(data);
        for (EquesAlarmDetailBean bean : data) {
            list.add(EquesAlarmNewInfo.getMessageBean(bean));
        }

        list.get(0).setDayFirst(true);
        list.add(0, EquesAlarmNewInfo.getDateBean(list.get(0).getRecordListBean()));
        list.get(list.size() - 1).setDayLast(true);
        for (int i = 1, count = list.size(); i < count; i++) {
            if (list.get(i).isDate()) {
                continue;
            }
            if (!list.get(i).isSameDay(list.get(i - 1))) {
                list.get(i).setDayFirst(true);
                list.add(i, EquesAlarmNewInfo.getDateBean(list.get(i).getRecordListBean()));
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

    public void sortAll(List<EquesAlarmNewInfo> data) {
        if (data == null) {
            return;
        }
        Collections.sort(data, new Comparator<EquesAlarmNewInfo>() {
            @Override
            public int compare(EquesAlarmNewInfo o1, EquesAlarmNewInfo o2) {
                if (Long.valueOf(o1.time) < Long.valueOf(o2.time)) {
                    return 1;
                } else if (Long.valueOf(o1.time) > Long.valueOf(o2.time)) {
                    return -1;
                }
                return 0;
            }
        });
    }

    public void sort(List<EquesAlarmDetailBean> data) {
        if (data == null) {
            return;
        }
        Collections.sort(data, new Comparator<EquesAlarmDetailBean>() {
            @Override
            public int compare(EquesAlarmDetailBean o1, EquesAlarmDetailBean o2) {
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

        View itemView = layoutInflater.inflate(R.layout.item_cateye_alarm_detail, parent, false);
        return new MessageAlarmHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        Device device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        WLog.i("MessageAlarmAdapter", "onBindViewHolder: " + position);
        if (getItemViewType(position) == TYPE_NORMAL) {
            if ((mData.get(position)).type == 5){    //视频
                ((MessageAlarmHolder) holder).ivAlarmPlay.setVisibility(View.VISIBLE);
            }else {
                ((MessageAlarmHolder) holder).ivAlarmPlay.setVisibility(View.GONE);
            }
            ((MessageAlarmHolder) holder).mTop.setVisibility(mData.get(position).isDayFirst() ? View.INVISIBLE : View.VISIBLE);
            ((MessageAlarmHolder) holder).mBottom.setVisibility(mData.get(position).isDayLast() ? View.INVISIBLE : View.VISIBLE);
            ((MessageAlarmHolder) holder).mTextTime.setText(mData.get(position).getDate());
            if (mData.get(position).recordType == 1){
//                ((MessageAlarmHolder) holder).mTextMsg.setText(String.format(mContext.getString(R.string.CateyeVisitor_call), DeviceInfoDictionary.getNameByDevice(device)));
                ((MessageAlarmHolder) holder).mTextMsg.setText(String.format(mContext.getString(R.string.CateyeVisitor_call), ""));
                MainApplication.getApplication().getEquesApiUnit().loadRingPic(( mData.get(position)).fid.get(0), ( mData.get(position)).bid,
                        ( mData.get(position)).time+ "", new EquesApiUnit.EquesFileDownloadListener() {
                            @Override
                            public void onSuccess(File file) {
                                ImageLoader.getInstance().displayImage("file://"+ file.getAbsolutePath().trim(), ((MessageAlarmHolder) holder).ivAlarmPic, mOptions);
                            }

                            @Override
                            public void onFail(int code, String msg) {

                            }
                        });

                ((MessageAlarmHolder) holder).ivAlarmPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EquesVisitorPicActivity.start(mContext, deviceId, FileUtil.getEquesRingPath() + "/"+ deviceId + "/" + mData.get(position).time + ".jpg");
                    }
                });
            }else {
//                ((MessageAlarmHolder) holder).mTextMsg.setText(String.format(mContext.getString(R.string.CateyeVisitor_alarm), DeviceInfoDictionary.getNameByDevice(device)));
                ((MessageAlarmHolder) holder).mTextMsg.setText(String.format(mContext.getString(R.string.CateyeVisitor_alarm), ""));
                if (( mData.get(position)).pvid!= null&& !( mData.get(position)).pvid.isEmpty()){
                    MainApplication.getApplication().getEquesApiUnit().loadPvidPic(( mData.get(position)).pvid.get(0), deviceId,
                            (mData.get(position)).time+ "", new EquesApiUnit.EquesFileDownloadListener() {
                                @Override
                                public void onSuccess(File file) {
                                    ImageLoader.getInstance().displayImage("file://"+ file.getAbsolutePath().trim(), ((MessageAlarmHolder) holder).ivAlarmPic, mOptions);
                                }

                                @Override
                                public void onFail(int code, String msg) {

                                }
                            });
                }

                ((MessageAlarmHolder) holder).ivAlarmPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ((mData.get(position)).type == 3){     //单拍
                            EquesPicActivity.start(mContext, deviceId, mData.get(position).getRecordListBean());
                        }else if ((mData.get(position)).type == 4){    //连拍
                            EquesPicsActivity.start(mContext, deviceId, mData.get(position).getRecordListBean());
                        }else if (( mData.get(position)).type == 5){    //视频
                            EquesVideoActivity.start(mContext, deviceId, mData.get(position).getRecordListBean());
                        }
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
}