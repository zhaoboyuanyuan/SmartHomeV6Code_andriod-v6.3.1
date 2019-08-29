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
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLBaseAdapter;
import cc.wulian.smarthomev6.main.device.adapter.DoorLockAdapter;
import cc.wulian.smarthomev6.main.device.lookever.setting.BindLockCallback;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;

/**
 * created by huxc  on 2017/8/22.
 * func：  mini网关声音adapter
 * email: hxc242313@qq.com
 */

public class VoiceAdapter extends WLBaseAdapter<String> {
    private LayoutInflater mInflater;
    private List<String> mData;
    private Context context;

    public VoiceAdapter(Context context, List<String> data) {
        super(context, data);
        mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context =context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VoiceAdapter.ViewHolder holder = null;
        if (convertView == null) {
            holder = new VoiceAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.item_mini_gateway_voice, null);
            holder.ivChecked = (ImageView) convertView.findViewById(R.id.iv_checked);
            holder.tvVoiceType = (TextView) convertView.findViewById(R.id.tv_voice_type);
            holder.llVoice = (LinearLayout) convertView.findViewById(R.id.ll_voice);
            convertView.setTag(holder);
        } else {
            holder = (VoiceAdapter.ViewHolder) convertView.getTag();
        }
        holder.llVoice.setTag(position);
        holder.tvVoiceType.setText(mData.get(position));
        if (position == selectItem) {
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
        public LinearLayout llVoice;
    }

    public  void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }
    private int  selectItem=-1;
}
