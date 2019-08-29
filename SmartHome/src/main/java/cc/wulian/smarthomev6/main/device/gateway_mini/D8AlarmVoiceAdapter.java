package cc.wulian.smarthomev6.main.device.gateway_mini;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.WLBaseAdapter;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * created by huxc  on 2017/8/22.
 * func： 报时声音adapter
 * email: hxc242313@qq.com
 */

public class D8AlarmVoiceAdapter extends WLBaseAdapter<String> {

    private LayoutInflater mInflater;
    private List<String> mData;
    private Context context;
    private static final String TAG = "D8AlarmVoiceAdapter";

    public D8AlarmVoiceAdapter(Context context, List<String> data) {
        super(context, data);
        this.mData = data;
        this.context =context;
        this.mInflater = LayoutInflater.from(context);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        D8AlarmVoiceAdapter.ViewHolder holder = null;
        if (convertView == null) {
            holder = new D8AlarmVoiceAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.item_mini_gateway_voice, null);
            holder.ivChecked = (ImageView) convertView.findViewById(R.id.iv_checked);
            holder.tvVoiceType = (TextView) convertView.findViewById(R.id.tv_voice_type);
            convertView.setTag(holder);
        } else {
            holder = (D8AlarmVoiceAdapter.ViewHolder) convertView.getTag();
        }
        holder.tvVoiceType.setText(mData.get(position));
        if (position == selectItem) {
            WLog.i(TAG, "getView: selectItem = "+selectItem);
            holder.ivChecked.setVisibility(View.VISIBLE);
        }
        else {
            holder.ivChecked.setVisibility(View.GONE);
        }
        return convertView;
    }

    public  class ViewHolder {
        public ImageView ivChecked;
        public TextView tvVoiceType;
    }

    public  void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }
    private int  selectItem=-1;

}
