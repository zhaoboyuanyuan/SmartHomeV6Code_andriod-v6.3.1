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
import cc.wulian.smarthomev6.main.device.device_if02.WifiIRManage;
import cc.wulian.smarthomev6.main.device.device_if02.bean.ControllerListBean;

/**
 * Created by hxc on 2018/6/19
 * Function:    遥控器列表
 */
public class DeviceIF02Adapter extends RecyclerView.Adapter<DeviceIF02Adapter.ItemHolder> {

    private List<ControllerListBean.ControllerBean> data;
    private String deviceID;

    private OnItemClickListener clickListener;

    public interface OnItemClickListener{
        void onItemClick(ControllerListBean.ControllerBean bean);
    }
    public void setOnItemClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public DeviceIF02Adapter() {
    }

    public DeviceIF02Adapter(String deviceID) {
        this.deviceID = deviceID;
    }

    public void setData(List<ControllerListBean.ControllerBean> data) {
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
        String name = data.get(viewHolder.getAdapterPosition()).blockName;

        viewHolder.mTextName.setText(name);
//        viewHolder.mTextDesc.setText(brand);

        @DrawableRes int icon = R.drawable.ic_launcher;
        if (TextUtils.equals(data.get(viewHolder.getAdapterPosition()).blockType, WifiIRManage.TYPE_AIR)) {
            icon = R.drawable.icon_uei_air;
        } else if (TextUtils.equals(data.get(viewHolder.getAdapterPosition()).blockType, WifiIRManage.TYPE_TV)) {
            icon = R.drawable.icon_uei_tv;
        }else if (TextUtils.equals(data.get(viewHolder.getAdapterPosition()).blockType, WifiIRManage.TYPE_FAN)) {
            icon = R.drawable.icon_uei_fan;
        }else if (TextUtils.equals(data.get(viewHolder.getAdapterPosition()).blockType, WifiIRManage.TYPE_STB)) {
            icon = R.drawable.icon_uei_stb;
        }else if (TextUtils.equals(data.get(viewHolder.getAdapterPosition()).blockType, WifiIRManage.TYPE_IT_BOX)) {
            icon = R.drawable.icon_uei_it_box;
        }else if (TextUtils.equals(data.get(viewHolder.getAdapterPosition()).blockType, WifiIRManage.TYPE_PROJECTOR)) {
            icon = R.drawable.icon_uei_projector;
        }else if (TextUtils.equals(data.get(viewHolder.getAdapterPosition()).blockType, WifiIRManage.TYPE_CUSTOM)) {
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
