package cc.wulian.smarthomev6.main.login;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
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
 * 网关搜索popwindow
 */

public class GatewaySearchPop extends PopupWindow {

    private Context context;
    private ListView mListView;
    private GatewayListAdapter adapter;
    private OnGatewaySelectedListener listener;
    private GatewaySearchUnit gatewaySearchUnit;
    private Handler handler;
    private View rootView;

    private int itemTextPadding = 15;

    private List<GatewayBean> gatewayList;

    public interface OnGatewaySelectedListener {
        void onGatewaySearchFinish(List<GatewayBean> list);

        void onGatewaySelected(GatewayBean bean);
    }

    public GatewaySearchPop(@NonNull Context context, @NonNull OnGatewaySelectedListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
        setBackgroundDrawable(new BitmapDrawable());
        setOutsideTouchable(true);
        itemTextPadding = DisplayUtil.dip2Pix(context, 10);
        rootView = LayoutInflater.from(context).inflate(R.layout.popupwindow_gateway_search, null);
        setContentView(rootView);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        mListView = (ListView) rootView.findViewById(R.id.listview);
        adapter = new GatewayListAdapter();
        mListView.setAdapter(adapter);

        handler = new Handler();
        gatewaySearchUnit = new GatewaySearchUnit();
        startSearch();
    }

    public void startSearch() {
        if (adapter.getData().size() > 0) {
            listener.onGatewaySearchFinish(adapter.getData());
        }
        gatewaySearchUnit.startSearch(new GatewaySearchUnit.SearchGatewayCallback() {
            @Override
            public void result(final List<GatewayBean> list) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        gatewayList = list;
                        adapter.getData().clear();
                        adapter.getData().addAll(list);
                        adapter.notifyDataSetChanged();
                        listener.onGatewaySearchFinish(list);
                    }
                });
            }
        });
    }

    /**
     * 根据输入的网关id过滤网关列表
     *
     * @param gwId
     */
    public void filterGateway(String gwId) {
        List<GatewayBean> filterList = new ArrayList<>();
        if (gatewayList != null && gatewayList.size() > 0) {
            for (GatewayBean bean :
                    gatewayList) {
                if (bean.gwID.startsWith(gwId)) {
                    filterList.add(bean);
                }
                adapter.getData().clear();
                adapter.getData().addAll(filterList);
                adapter.notifyDataSetChanged();
                listener.onGatewaySearchFinish(filterList);
            }
        }

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
//            textView.setText(bean.host + ":" + bean.gwID);
            textView.setText(bean.gwID);
            return convertView;
        }
    }

}
