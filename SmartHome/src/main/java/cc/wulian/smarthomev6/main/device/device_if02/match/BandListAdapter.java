package cc.wulian.smarthomev6.main.device.device_if02.match;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.device.device_if02.WifiIRManage;
import cc.wulian.smarthomev6.main.device.device_if02.airconditioner.AirOneByOneMatchActivity;
import cc.wulian.smarthomev6.support.core.apiunit.bean.BrandBean.BrandSortBean;

/**
 * Created by hxc on 2018/6/19
 * Function:    wifi红外转发通用遥控器品牌列表
 */
public class BandListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<BrandSortBean> data;
    private String deviceID;
    private String ueiType;

    public BandListAdapter(String deviceID, String ueiType) {
        this.deviceID = deviceID;
        this.ueiType = ueiType;
    }

    public void setData(List<BrandSortBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == 1) {
            View itemView = layoutInflater.inflate(R.layout.item_brand_list_group, parent, false);
            return new GroupHolder(itemView);
        }
        View itemView = layoutInflater.inflate(R.layout.item_brand_list_item, parent, false);
        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ItemHolder) {
            BrandSortBean bean = data.get(viewHolder.getAdapterPosition());
            ((ItemHolder) viewHolder).mLocalName.setText(TextUtils.isEmpty(bean.localName) ? bean.brandName : bean.localName);
            ((ItemHolder) viewHolder).mTextBrand.setText(data.get(viewHolder.getAdapterPosition()).brandName);
            ((ItemHolder) viewHolder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (TextUtils.equals(ueiType, WifiIRManage.TYPE_AIR)) {
//                        AirOneByOneMatchActivity.start(v.getContext(), deviceID, ueiType, data.get(viewHolder.getAdapterPosition()).brandId, data.get(viewHolder.getAdapterPosition()).brandName);
//                    } else {
                        MatchModelActivity.start(v.getContext(), deviceID, ueiType, data.get(viewHolder.getAdapterPosition()).brandId, data.get(viewHolder.getAdapterPosition()).brandName);
//                    }
                }
            });
        }

        if (viewHolder instanceof GroupHolder) {
            if (TextUtils.equals("#", data.get(viewHolder.getAdapterPosition()).groupName)) {
                //常见品牌
                ((GroupHolder) viewHolder).mTextGroup.setText(R.string.Infraredrelay_Addremote_Commonbrands);
            } else {
                //其他品牌
                ((GroupHolder) viewHolder).mTextGroup.setText(data.get(viewHolder.getAdapterPosition()).groupName);
            }
        }
    }

    public BrandSortBean get(int position) {
        return data == null ? null : data.get(position);
    }

    public int getGroupPosition(String group) {
        return data.indexOf(BrandSortBean.groupBean(group));
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).type;
    }

    private class ItemHolder extends RecyclerView.ViewHolder {

        private TextView mLocalName, mTextBrand;

        public ItemHolder(View itemView) {
            super(itemView);

            mLocalName = (TextView) itemView.findViewById(R.id.brand_item_local_name);
            mTextBrand = (TextView) itemView.findViewById(R.id.brand_item_brand_name);
        }
    }

    private class GroupHolder extends RecyclerView.ViewHolder {

        private TextView mTextGroup;

        public GroupHolder(View itemView) {
            super(itemView);

            mTextGroup = (TextView) itemView.findViewById(R.id.brand_group_name);
        }
    }
}
