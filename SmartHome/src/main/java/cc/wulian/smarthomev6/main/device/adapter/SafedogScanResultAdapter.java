package cc.wulian.smarthomev6.main.device.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.SafeDogScaningInfo;
import cc.wulian.smarthomev6.support.core.apiunit.bean.SafeDogSchedulesBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.SafeDogSchedulesDetailBean;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;

/**
 * Created by 上海滩小马哥 on 2018/01/29.
 * 安全狗扫描结果页adapter
 */

public class SafedogScanResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_TITTLE = 0;
    private static final int TYPE_MESSAGE = 1;
    private LayoutInflater layoutInflater;
    private List<SafeDogScaningInfo> mData;

    public SafedogScanResultAdapter(Context context) {
        this.mData = new ArrayList<>();
        layoutInflater = LayoutInflater.from(context);
    }

    public void setData(SafeDogSchedulesBean bean) {
        if (null == bean) {
            return;
        }
        List<SafeDogScaningInfo> list = new ArrayList<>();
        for (int i = 0; i < bean.getData().size(); i++) {
            int tittlePosition = 0;
            ArrayList<SafeDogSchedulesDetailBean> itemList = bean.getData().get(i);
            if (null != itemList) {
                boolean isFirst = true;
                boolean hasWarnning = false;
                for (SafeDogSchedulesDetailBean itemBean : itemList) {
                    if (isFirst) {
                        isFirst = false;
                        tittlePosition = list.size();
                        SafeDogScaningInfo tittleInfo = new SafeDogScaningInfo(itemBean.scheduleType);
                        list.add(tittleInfo);
                    }
                    if (itemBean .status == 4){
                        hasWarnning = true;
                        SafeDogScaningInfo scaningInfo = new SafeDogScaningInfo(itemBean);
                        list.add(scaningInfo);
                    }
                }
                if (!hasWarnning){
                    list.remove(tittlePosition);
                }
            }
        }
        mData = list;
        notifyDataSetChanged();
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
        View itemView = layoutInflater.inflate(R.layout.layout_safedog_result_item, parent, false);
        return new MessageHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_TITTLE) {
            ((TittleHolder) holder).tittleState.setVisibility(View.GONE);
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
        }else if (getItemViewType(position) == TYPE_MESSAGE){
            ((MessageHolder) holder).deviceName.setText(mData.get(position).getHostname());
            ((MessageHolder) holder).deviceIp.setText(mData.get(position).getIp());
            if (TextUtils.equals(mData.get(position).getSchedulesType(), "12030")) {
                ((MessageHolder) holder).problem.setText(R.string.Device_safe002);
                ((MessageHolder) holder).tips.setText(R.string.Device_safe003);
            } else if (TextUtils.equals(mData.get(position).getSchedulesType(), "12060")) {
                ((MessageHolder) holder).problem.setText(R.string.Device_safe004);
                ((MessageHolder) holder).tips.setText(R.string.Device_safe005);
            } else if (TextUtils.equals(mData.get(position).getSchedulesType(), "12040")) {
                ((MessageHolder) holder).problem.setText(R.string.Device_safe006);
                ((MessageHolder) holder).tips.setText(R.string.Device_safe005);
            } else if (TextUtils.equals(mData.get(position).getSchedulesType(), "12020")) {
                ((MessageHolder) holder).problem.setText(R.string.Device_safe007);
                ((MessageHolder) holder).tips.setText(R.string.Device_safe005);
            } else if (TextUtils.equals(mData.get(position).getSchedulesType(), "12010")) {
                ((MessageHolder) holder).problem.setText(R.string.Device_safe008);
                ((MessageHolder) holder).tips.setText(R.string.Device_safe05);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    private class MessageHolder extends RecyclerView.ViewHolder {
        private TextView deviceName;
        private TextView deviceIp;
        private TextView problem;
        private TextView tips;

        public MessageHolder(View itemView) {
            super(itemView);

            deviceName = (TextView) itemView.findViewById(R.id.tv_item_name);
            deviceIp = (TextView) itemView.findViewById(R.id.tv_item_ip);
            problem = (TextView) itemView.findViewById(R.id.tv_item_problem);
            tips = (TextView) itemView.findViewById(R.id.tv_item_tips);
        }
    }

    private class TittleHolder extends RecyclerView.ViewHolder {
        private TextView tittleName;
        private ImageView tittleState;

        public TittleHolder(View itemView) {
            super(itemView);

            tittleName = (TextView) itemView.findViewById(R.id.tv_tittle);
            tittleState = (ImageView) itemView.findViewById(R.id.iv_loading);
        }
    }
}
