package cc.wulian.smarthomev6.main.device.device_bc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;

/**
 * Created by yuxiaoxuan on 2017/6/8.
 * wifi名称的列表
 */

public class wifiNameAdapter extends BaseAdapter {
    private Context mcontext;
    private List<String> wifiNames;
    private LayoutInflater mInflater;

    public wifiNameAdapter(Context context) {
        this.mcontext = context;
        this.wifiNames = new ArrayList<>();
        this.mInflater = LayoutInflater.from(context);
    }

    public void setWifiNames(List<String> wifiNames) {
        this.wifiNames = wifiNames;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.wifiNames.size();
    }

    @Override
    public Object getItem(int i) {
        if (this.wifiNames.size() == 0 || i < 0 || i >= this.wifiNames.size()) {
            return null;
        } else {
            return this.wifiNames.get(i);
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        holderView holderView = null;
        if (convertView == null) {
            holderView = new holderView();
            convertView = mInflater.inflate(R.layout.item_wifilist, null);
            holderView.wifiName = (TextView) convertView.findViewById(R.id.wifiName);
            convertView.setTag(holderView);
        } else {
            holderView = (wifiNameAdapter.holderView) convertView.getTag();
        }
        String wifiName = this.wifiNames.get(i);
        holderView.wifiName.setText(wifiName);
        return convertView;
    }

    private class holderView {
        public TextView wifiName;
    }

}
