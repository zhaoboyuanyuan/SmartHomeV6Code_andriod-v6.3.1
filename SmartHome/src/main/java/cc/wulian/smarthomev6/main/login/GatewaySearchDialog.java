package cc.wulian.smarthomev6.main.login;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.support.core.socket.GatewayBean;
import cc.wulian.smarthomev6.support.core.socket.GatewaySearchUnit;
import cc.wulian.smarthomev6.support.utils.DisplayUtil;

/**
 * Created by zbl on 2017/3/20.
 * 网关搜索dialog
 */

public class GatewaySearchDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private ListView mListView;
    private GatewayListAdapter adapter;
    private ProgressBar pb_loading;
    private TextView btn_cancel;
    private OnGatewaySelectedListener listener;
    private GatewaySearchUnit gatewaySearchUnit;
    private Handler handler;

    private int itemTextPadding = 15;

    public interface OnGatewaySelectedListener {
        void onGatewaySelected(GatewayBean bean);
    }

    public GatewaySearchDialog(@NonNull Context context, @NonNull OnGatewaySelectedListener listener) {
        super(context, R.style.loading_dialog);
        this.context = context;
        this.listener = listener;
        itemTextPadding = DisplayUtil.dip2Pix(context, 10);
        getWindow().setContentView(R.layout.dialog_gateway_search);
        setCanceledOnTouchOutside(true);

        btn_cancel = (TextView) findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
        pb_loading = (ProgressBar) findViewById(R.id.pb_loading);
        mListView = (ListView) findViewById(R.id.listview);
        adapter = new GatewayListAdapter();
        mListView.setAdapter(adapter);

        handler = new Handler();
        gatewaySearchUnit = new GatewaySearchUnit();
        startSearch();
    }

    @Override
    public void onClick(View v) {
        if (v == btn_cancel) {
            dismiss();
        }
    }

    private void startSearch() {
        mListView.setVisibility(View.GONE);
        pb_loading.setVisibility(View.VISIBLE);
        gatewaySearchUnit.startSearch(new GatewaySearchUnit.SearchGatewayCallback() {
            @Override
            public void result(final List<GatewayBean> list) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mListView.setVisibility(View.VISIBLE);
                        pb_loading.setVisibility(View.GONE);
                        adapter.getData().addAll(list);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    class GatewayListAdapter extends BaseAdapter {

        private List<GatewayBean> list = new ArrayList<>();

        public List<GatewayBean> getData() {
            return list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                TextView textView = new TextView(context);
                textView.setTextSize(16);
                textView.setPadding(itemTextPadding, itemTextPadding, itemTextPadding, itemTextPadding);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int p = (int) v.getTag();
                        GatewayBean bean = list.get(p);
                        listener.onGatewaySelected(bean);
                        dismiss();
                    }
                });
                convertView = textView;
            }
            TextView textView = (TextView) convertView;
            textView.setTag(position);
            GatewayBean bean = list.get(position);
            textView.setText(bean.host + ":" + bean.gwID);
            return convertView;
        }
    }

}
