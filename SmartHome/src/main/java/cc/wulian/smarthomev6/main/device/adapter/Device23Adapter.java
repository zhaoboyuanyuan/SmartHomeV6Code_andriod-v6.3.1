package cc.wulian.smarthomev6.main.device.adapter;

import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.UeiConfig;
import cc.wulian.smarthomev6.main.device.device_23.AirConditioning.AirConditioningMainActivity;
import cc.wulian.smarthomev6.main.device.device_23.tv.TvRemoteMainActivity;
import cc.wulian.smarthomev6.support.core.mqtt.bean.Device22DetailBean;

import static cc.wulian.smarthomev6.main.device.device_23.tv.RemoteControlBrandActivity.SINGLE_CODE_PROJECTOR;

/**
 * Created by Veev on 2017/8/28
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    遥控器列表
 */
public class Device23Adapter extends RecyclerView.Adapter<Device23Adapter.ItemHolder> {

    private List<UeiConfig> data;
    private String deviceID;

    private OnItemClickListener clickListener;

    public interface OnItemClickListener{
        void onItemClick(UeiConfig bean);
    }
    public void setOnItemClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public Device23Adapter() {
    }

    public Device23Adapter(String deviceID) {
        this.deviceID = deviceID;
    }

    public void setData(List<UeiConfig> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void clear() {
        if (data != null) {
            data.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_remote_controller_list, parent, false);
        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ItemHolder viewHolder, int position) {
        String name = data.get(viewHolder.getAdapterPosition()).getName();

        viewHolder.mTextName.setText(name);
//        viewHolder.mTextDesc.setText(brand);

        @DrawableRes int icon = R.drawable.ic_launcher;
        if (TextUtils.equals(data.get(viewHolder.getAdapterPosition()).type, "Z")) {
            icon = R.drawable.icon_uei_air;
        } else if (TextUtils.equals(data.get(viewHolder.getAdapterPosition()).type, "T")) {
            if(SINGLE_CODE_PROJECTOR.equals(data.get(viewHolder.getAdapterPosition()).singleCode)){
                icon = R.drawable.icon_uei_projector;
            }else{
                icon = R.drawable.icon_uei_tv;
            }
        } else if (TextUtils.equals(data.get(viewHolder.getAdapterPosition()).type, "C")) {
            icon = R.drawable.icon_uei_stb;

        } else if (TextUtils.equals(data.get(viewHolder.getAdapterPosition()).type, "R,M")) {
            icon = R.drawable.icon_uei_audio;

        } else if (TextUtils.equals(data.get(viewHolder.getAdapterPosition()).type, "CUSTOM")) {
            icon = R.drawable.icon_uei_custom;
        }
        viewHolder.mImageIcon.setImageResource(icon);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != clickListener){
                    clickListener.onItemClick(data.get(viewHolder.getAdapterPosition()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        private TextView mTextName, mTextDesc;
        private ImageView mImageIcon;

        public ItemHolder(View itemView) {
            super(itemView);

            mTextName = (TextView) itemView.findViewById(R.id.controller_text_name);
            mTextDesc = (TextView) itemView.findViewById(R.id.controller_text_desc);
            mImageIcon = (ImageView) itemView.findViewById(R.id.controller_image_icon);
        }
    }
}
