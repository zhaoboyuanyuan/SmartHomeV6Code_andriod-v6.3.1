package cc.wulian.smarthomev6.main.mine.gatewaycenter.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceBean;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;

public class GatewayListsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<DeviceBean> datas;

    private OnItemClickListener onClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position, String tag);
    }


    public GatewayListsAdapter(Context context, List<DeviceBean> datas) {
        this.context = context;
        this.datas = datas;
        notifyDataSetChanged();
    }

    public void update(List<DeviceBean> datas) {
        if (datas != null) {
            this.datas = datas;
            notifyDataSetChanged();
        }

    }


    public void remove(int index) {
        datas.remove(index);
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return datas.isEmpty();
    }

    public void setOnClickListener(GatewayListsAdapter.OnItemClickListener listener) {
        this.onClickListener = listener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_gateway_list_swipe_menu, parent, false);
        return new GatewayListsAdapter.ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof GatewayListsAdapter.ItemHolder) {
            DeviceBean deviceBean = datas.get(position);
            ((ItemHolder) holder).ivDeviceIcon.setVisibility(View.GONE);
            if(TextUtils.isEmpty(deviceBean.getName())){
                ((ItemHolder) holder).tvDeviceName.setText(DeviceInfoDictionary.getDefaultNameByType(deviceBean.type));
            }else{
                ((ItemHolder) holder).tvDeviceName.setText(deviceBean.getName());
            }
            if (deviceBean.getDeviceId().startsWith("CG")) {
                ((ItemHolder) holder).tvDeviceType.setText(context.getString(R.string.BindGateway_GatewayID) + ":" + deviceBean.getDeviceId().substring(0, 11));
            } else {
                ((ItemHolder) holder).tvDeviceType.setText(context.getString(R.string.BindGateway_GatewayID) + ":" + deviceBean.getDeviceId());
            }
            ((ItemHolder) holder).tvDeviceArea.setVisibility(View.GONE);
            if ("1".equals(deviceBean.getState())) {
                ((ItemHolder) holder).tvDeviceName.setTextColor(Color.BLACK);
            } else {
                ((ItemHolder) holder).tvDeviceName.setTextColor(Color.GRAY);
            }
            if (deviceBean.getIsSelect()) {
                ((ItemHolder) holder).ivSelected.setVisibility(View.VISIBLE);
                ((ItemHolder) holder).ivSelected.setImageResource(R.drawable.theme_select);
            } else {
                ((ItemHolder) holder).ivSelected.setVisibility(View.GONE);
            }

            //分享设备的分享者信息
            if (deviceBean.isShared()) {
                String shareSource = deviceBean.granterUserPhone;
                if (TextUtils.isEmpty(shareSource)) {
                    shareSource = deviceBean.granterUserEmail;
                }
                if (TextUtils.isEmpty(shareSource)) {
                    ((ItemHolder) holder).tvDeviceDesc.setVisibility(View.GONE);
                } else {
                    ((ItemHolder) holder).tvDeviceDesc.setVisibility(View.VISIBLE);
                    ((ItemHolder) holder).tvDeviceDesc.setText(String.format(context.getString(R.string.Share_Source), shareSource));
                }
            } else {
                ((ItemHolder) holder).tvDeviceDesc.setVisibility(View.GONE);
            }


            if (onClickListener != null) {
                ((GatewayListsAdapter.ItemHolder) holder).tvDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickListener.onItemClick(position, v.getTag().toString());
                    }
                });
                ((GatewayListsAdapter.ItemHolder) holder).llItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickListener.onItemClick(position, v.getTag().toString());
                    }
                });
            }
        }


    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }


    class ItemHolder extends RecyclerView.ViewHolder {

        public ImageView ivDeviceIcon;
        public TextView tvDeviceName;
        public TextView tvDeviceType;
        public TextView tvDeviceArea;
        public ImageView ivSelected;
        public TextView tvDeviceDesc;
        private TextView tvDelete;
        private LinearLayout llItem;

        public ItemHolder(View itemView) {
            super(itemView);
            tvDelete = (TextView) itemView.findViewById(R.id.tv_delete);
            ivDeviceIcon = (ImageView) itemView.findViewById(R.id.iv_device_icon);
            tvDeviceName = (TextView) itemView.findViewById(R.id.tv_device_name);
            tvDeviceType = (TextView) itemView.findViewById(R.id.tv_device_type);
            tvDeviceArea = (TextView) itemView.findViewById(R.id.tv_device_area);
            ivSelected = (ImageView) itemView.findViewById(R.id.iv_device_info);
            tvDeviceDesc = (TextView) itemView.findViewById(R.id.tv_device_desc);
            llItem = itemView.findViewById(R.id.ll_item_gateway_list);
        }
    }
}
