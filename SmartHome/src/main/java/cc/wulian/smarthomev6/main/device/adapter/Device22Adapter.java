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
import cc.wulian.smarthomev6.main.device.device_22.CustomRemoteActivity;
import cc.wulian.smarthomev6.main.device.device_22.TVRemoteActivity;
import cc.wulian.smarthomev6.support.core.mqtt.bean.Device22DetailBean;

import static cc.wulian.smarthomev6.main.device.device_22.Device22Activity.TYPE_AC;
import static cc.wulian.smarthomev6.main.device.device_22.Device22Activity.TYPE_CUSTOM;
import static cc.wulian.smarthomev6.main.device.device_22.Device22Activity.TYPE_TV;

/**
 * Created by 上海滩小马哥 on 2017/12/12.
 */

public class Device22Adapter extends RecyclerView.Adapter<Device22Adapter.ItemHolder> {

    private List<Device22DetailBean> data;
    private String deviceID;
    private OnItemClickListener clickListener;

    public interface OnItemClickListener{
        void onItemClick(Device22DetailBean bean);
    }
    public void setOnItemClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public Device22Adapter() {
    }

    public Device22Adapter(String deviceID) {
        this.deviceID = deviceID;
    }

    public void setData(List<Device22DetailBean> data) {
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
        String name = data.get(viewHolder.getAdapterPosition()).name;
        viewHolder.mTextName.setText(name);

        @DrawableRes int icon = R.drawable.ic_launcher;
        if (TextUtils.equals(data.get(viewHolder.getAdapterPosition()).type, TYPE_AC)) {
            icon = R.drawable.icon_uei_air;
        } else if (TextUtils.equals(data.get(viewHolder.getAdapterPosition()).type, TYPE_TV)) {
            icon = R.drawable.icon_uei_tv;
        } else if (TextUtils.equals(data.get(viewHolder.getAdapterPosition()).type, TYPE_CUSTOM)) {
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
