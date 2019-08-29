package cc.wulian.smarthomev6.main.device.gateway_mini;

import android.app.Dialog;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.WLBaseAdapter;
import cc.wulian.smarthomev6.support.core.apiunit.bean.WifiInfoBean;
import cc.wulian.smarthomev6.support.core.socket.GatewaySearchUnit;
import cc.wulian.smarthomev6.support.utils.DisplayUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by hxc on 2017/8/29.
 * mini网关Wifi列表dialog
 */

public class MiniGatewayWifiListDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private ListView mListView;
    private WifiListAdapter adapter;
    private TextView btn_cancel;
    private OnWifiSelectedListener listener;
    private List<WifiInfoBean> wifiList;

    private int itemTextPadding = 15;

    public interface OnWifiSelectedListener {
        void onWifiSelected(WifiInfoBean bean);
    }

    public MiniGatewayWifiListDialog(@NonNull Context context, @NonNull OnWifiSelectedListener listener, List<WifiInfoBean> wifiList) {
        super(context, R.style.loading_dialog);
        this.context = context;
        this.listener = listener;
        this.wifiList = wifiList;
        itemTextPadding = DisplayUtil.dip2Pix(context, 10);
        getWindow().setContentView(R.layout.dialog_mini_gateway_wifi_list);
        setCanceledOnTouchOutside(true);

        btn_cancel = (TextView) findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.listview);
        adapter = new WifiListAdapter(context, wifiList);
        mListView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if (v == btn_cancel) {
            dismiss();
        }
    }


    class WifiListAdapter extends WLBaseAdapter<WifiInfoBean> {
        private List<WifiInfoBean> mData;

        public WifiListAdapter(Context context, List<WifiInfoBean> data) {
            super(context, data);
            this.mData = data;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            WifiListAdapter.ViewHolder holder = null;
            if (convertView == null) {
                holder = new WifiListAdapter.ViewHolder();
                convertView = mInflater.inflate(R.layout.item_mini_gateway_wifi_list, null);
                holder.ivSignal = (ImageView) convertView.findViewById(R.id.iv_signal);
                holder.tvWifiName = (TextView) convertView.findViewById(R.id.tv_wifi_name);
                convertView.setTag(holder);
            } else {
                holder = (WifiListAdapter.ViewHolder) convertView.getTag();
            }
            holder.tvWifiName.setText(mData.get(position).essid);
            holder.tvWifiName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WifiInfoBean bean = mData.get(position);
                    listener.onWifiSelected(bean);
                    dismiss();
                }
            });
            return convertView;
        }

        public class ViewHolder {
            public ImageView ivSignal;
            public TextView tvWifiName;
        }

    }

}
