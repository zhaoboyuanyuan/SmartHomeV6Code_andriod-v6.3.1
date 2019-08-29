package cc.wulian.smarthomev6.main.device.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.SafeDogScaningInfo;
import cc.wulian.smarthomev6.support.core.apiunit.bean.SafeDogSchedulesBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.SafeDogSchedulesDetailBean;
import cc.wulian.smarthomev6.support.customview.RecycleImageView;

/**
 * Created by 上海滩小马哥 on 2018/01/26.
 * 安全狗扫描列表adapter
 */

public class SafedogScanningAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_TITTLE = 0;
    private static final int TYPE_MESSAGE = 1;
    private LayoutInflater layoutInflater;
    private List<SafeDogScaningInfo> mData;
    private Animation columnAnim;

    public SafedogScanningAdapter(Context context) {
        this.mData = new ArrayList<>();
        layoutInflater = LayoutInflater.from(context);
    }

    public void setData(SafeDogSchedulesBean bean) {
        if (null == bean) {
            return;
        }
        if (mData == null || mData.size() == 0) {
            List<SafeDogScaningInfo> list = new ArrayList<>();
            for (int i = 0; i < bean.getData().size(); i++) {
                ArrayList<SafeDogSchedulesDetailBean> itemList = bean.getData().get(i);
                if (null != itemList) {
                    boolean isFirst = true;
                    int firstPosition = 0;
                    int tittleState = 1;
                    for (SafeDogSchedulesDetailBean itemBean : itemList) {
                        if (isFirst) {
                            isFirst = false;
                            SafeDogScaningInfo tittleInfo = new SafeDogScaningInfo(itemBean.scheduleType);
                            list.add(tittleInfo);
                            firstPosition = list.size() - 1;
                        }
                        if (itemBean.status == 2 || itemBean.status == 3) {   //正在扫描、失败
                            tittleState = 2;
                        }
                        if (itemBean.status == 4 && tittleState != 2) {  //危险
                            tittleState = 4;
                        }
                        SafeDogScaningInfo scaningInfo = new SafeDogScaningInfo(itemBean);
                        if (itemList.indexOf(itemBean) == itemList.size() - 1) {
                            scaningInfo.setEnd(true);
                        }
                        list.add(scaningInfo);
                    }
                    list.get(firstPosition).setTittleStatus(tittleState);
                }
            }
            mData = list;
            notifyDataSetChanged();
        } else {
            update(bean);
        }
    }

    private void update(SafeDogSchedulesBean bean) {
        List<ArrayList<SafeDogSchedulesDetailBean>> newBean = bean.getData();
        String tittleType = null;
        int tittlePosition = 0;

        for (SafeDogScaningInfo item : mData) {
            if (item.isTittle) {
                tittlePosition = mData.indexOf(item);
                tittleType = item.getSchedulesType();
            } else {
                outer:
                for (ArrayList<SafeDogSchedulesDetailBean> schedules : newBean) {
                    if (schedules == null) {
                        continue;
                    }
                    for (SafeDogSchedulesDetailBean schedule : schedules) {
                        if (!TextUtils.equals(schedule.scheduleType, item.getSchedulesType())) {
                            continue outer;
                        }
                        if (TextUtils.equals(schedule.deviceId, item.getDeviceId())) {
                            if (schedule.status != item.getStatus()) {
                                item.setStatus(schedule.status);
                                notifyItemChanged(mData.indexOf(item));
                            }
                            break outer;
                        }
                    }
                }
            }
            //更新tittle
            if (item.isEnd()) {
                int lastTittleState = mData.get(tittlePosition).getTittleStatus();
                mData.get(tittlePosition).setTittleStatus(1);
                for (int i = tittlePosition + 1; i < mData.size(); i++) {
                    if (!TextUtils.equals(tittleType, mData.get(i).getSchedulesType())) {
                        if (lastTittleState != mData.get(tittlePosition).getTittleStatus()) {
                            notifyItemChanged(tittlePosition);
                        }
                        break;
                    }

                    if (mData.get(i).getStatus() == 2 || mData.get(i).getStatus() == 3) {   //正在扫描
                        mData.get(tittlePosition).setTittleStatus(2);
                    }
                    if (mData.get(i).getStatus() == 4 && mData.get(tittlePosition).getTittleStatus() != 2) {  //危险
                        mData.get(tittlePosition).setTittleStatus(4);
                    }

                    if (i == (mData.size() - 1)) {
                        if (lastTittleState != mData.get(tittlePosition).getTittleStatus()) {
                            notifyItemChanged(tittlePosition);
                        }
                        break;
                    }
                }
            }
        }
    }

    public void updateItem(SafeDogSchedulesDetailBean bean) {
        String tittleType = bean.scheduleType;
        int tittlePosition = 0;
        for (SafeDogScaningInfo info : mData) {
            if (info.isTittle) {
                tittlePosition = mData.indexOf(info);
                tittleType = info.getSchedulesType();
            }
            if (TextUtils.equals(info.getDeviceId(), bean.deviceId)) {
                info.updateItem(bean);
                notifyItemChanged(mData.indexOf(info));
                break;
            }
        }
        mData.get(tittlePosition).setTittleStatus(1);
        for (int i = tittlePosition + 1; i < mData.size(); i++) {
            if (!TextUtils.equals(tittleType, mData.get(i).getSchedulesType())) {
                notifyItemChanged(tittlePosition);
                break;
            }

            if (mData.get(i).getStatus() == 2 || mData.get(i).getStatus() == 3) {   //正在扫描
                mData.get(tittlePosition).setTittleStatus(2);
            }
            if (mData.get(i).getStatus() == 4 && mData.get(tittlePosition).getTittleStatus() != 2) {  //危险
                mData.get(tittlePosition).setTittleStatus(4);
            }
        }
        if (TextUtils.equals(tittleType, "12010")) {
            notifyItemChanged(tittlePosition);
        }
    }

    public void clear() {
        mData.clear();
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.get(position).isTittle) {
            return TYPE_TITTLE;
        } else {
            return TYPE_MESSAGE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_TITTLE) {
            View itemView = layoutInflater.inflate(R.layout.layout_safedog_column, parent, false);
            return new TittleHolder(itemView);
        }
        View itemView = layoutInflater.inflate(R.layout.layout_safedog_item, parent, false);
        return new MessageHolder(itemView);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof MessageHolder) {
            ((MessageHolder) holder).state.viewRecycle();
        }
        if (holder instanceof TittleHolder) {
            ((TittleHolder) holder).tittleState.viewRecycle();
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_TITTLE) {
            if (TextUtils.equals(mData.get(position).getSchedulesType(), "12030")) {
                ((TittleHolder) holder).tittleName.setText(R.string.Device_safe01);
            } else if (TextUtils.equals(mData.get(position).getSchedulesType(), "12060")) {
                ((TittleHolder) holder).tittleName.setText(R.string.Device_safe02);
            } else if (TextUtils.equals(mData.get(position).getSchedulesType(), "12040")) {
                ((TittleHolder) holder).tittleName.setText(R.string.Device_safe03);
            } else if (TextUtils.equals(mData.get(position).getSchedulesType(), "12020")) {
                ((TittleHolder) holder).tittleName.setText(R.string.Device_safe04);
            } else if (TextUtils.equals(mData.get(position).getSchedulesType(), "12010")) {
                ((TittleHolder) holder).tittleName.setText(R.string.Device_safe05);
            }
            if (mData.get(position).getTittleStatus() == 2) {
                ((TittleHolder) holder).tittleState.setImageResource(R.drawable.safedog_column_loading);
                if (null == columnAnim) {
                    columnAnim = new RotateAnimation(0f, 360f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                    columnAnim.setDuration(2000);
                    columnAnim.setRepeatMode(Animation.RESTART);
                    columnAnim.setRepeatCount(-1);
                    columnAnim.setInterpolator(new LinearInterpolator());
                    columnAnim.start();
                }
                ((TittleHolder) holder).tittleState.setAnimation(columnAnim);
            } else if (mData.get(position).getTittleStatus() == 1) {
                ((TittleHolder) holder).tittleState.setImageResource(R.drawable.safedog_column_ok);
            } else if (mData.get(position).getTittleStatus() == 4) {
                ((TittleHolder) holder).tittleState.setImageResource(R.drawable.safedog_column_warning);
            }
        } else if (getItemViewType(position) == TYPE_MESSAGE) {
            ((MessageHolder) holder).deviceName.setText(mData.get(position).getHostname());
            ((MessageHolder) holder).deviceMac.setText(mData.get(position).getDeviceId());
            if (mData.get(position).getStatus() == 1) {
                ((MessageHolder) holder).state.setImageResource(R.drawable.safedog_item_ok);
            } else if (mData.get(position).getStatus() == 4) {
                ((MessageHolder) holder).state.setImageResource(R.drawable.safedog_item_warning);
            } else {
                ((MessageHolder) holder).state.setImageResource(R.drawable.safedog_item_loading);
                if (null == columnAnim) {
                    columnAnim = new RotateAnimation(0f, 360f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                    columnAnim.setDuration(2000);
                    columnAnim.setRepeatMode(Animation.RESTART);
                    columnAnim.setRepeatCount(-1);
                    columnAnim.setInterpolator(new LinearInterpolator());
                    columnAnim.start();
                }
                ((MessageHolder) holder).state.setAnimation(columnAnim);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    private class MessageHolder extends RecyclerView.ViewHolder {
        private TextView deviceName;
        private TextView deviceMac;
        private RecycleImageView state;

        public MessageHolder(View itemView) {
            super(itemView);

            deviceName = (TextView) itemView.findViewById(R.id.tv_item_name);
            deviceMac = (TextView) itemView.findViewById(R.id.tv_item_mac);
            state = (RecycleImageView) itemView.findViewById(R.id.iv_item_state);
        }
    }

    private class TittleHolder extends RecyclerView.ViewHolder {
        private TextView tittleName;
        private RecycleImageView tittleState;

        public TittleHolder(View itemView) {
            super(itemView);

            tittleName = (TextView) itemView.findViewById(R.id.tv_tittle);
            tittleState = (RecycleImageView) itemView.findViewById(R.id.iv_loading);
        }
    }
}
