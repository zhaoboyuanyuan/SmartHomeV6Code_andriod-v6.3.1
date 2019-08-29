package cc.wulian.smarthomev6.main.device.device_if02.match;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.support.core.apiunit.bean.CodeLibraryBean;

/**
 * created by huxc  on 2017/11/2.
 * func： 品牌型号adapter
 * email: hxc242313@qq.com
 */

public class BandModelListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<CodeLibraryBean.ModelCodeBean> data;
    private String deviceID;
    private String brandName;
    private String ueiType;
    private String localName;

    public BandModelListAdapter(String deviceID, String brandName, String ueiType, String localName) {
        this.deviceID = deviceID;
        this.brandName = brandName;
        this.ueiType = ueiType;
        this.localName = localName;
    }

    public void setData(List<CodeLibraryBean.ModelCodeBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_brand_list_item, parent, false);
        return new BandModelListAdapter.ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof BandModelListAdapter.ItemHolder) {
            if (data != null) {
                ((ItemHolder) viewHolder).model.setText(data.get(position).model);
            }
            if (data != null) {
                ((ItemHolder) viewHolder).code.setText(data.get(position).code);
            }

            ((BandModelListAdapter.ItemHolder) viewHolder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DownLoadCodeActivity.start(v.getContext(),deviceID,brandName,ueiType, data.get(viewHolder.getAdapterPosition()).code);
                }
            });
        }
    }

    public CodeLibraryBean.ModelCodeBean get(int position) {
        return data == null ? null : data.get(position);
    }


    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }


    private class ItemHolder extends RecyclerView.ViewHolder {

        private TextView model, code;

        public ItemHolder(View itemView) {
            super(itemView);

            model = (TextView) itemView.findViewById(R.id.brand_item_local_name);
            code = (TextView) itemView.findViewById(R.id.brand_item_brand_name);
        }
    }

}