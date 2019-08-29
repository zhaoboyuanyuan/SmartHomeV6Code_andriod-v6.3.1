package cc.wulian.smarthomev6.main.device.gateway_mini;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.WLBaseAdapter;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * created by huxc  on 2017/8/22.
 * func： 整点报时adapter
 * email: hxc242313@qq.com
 */

public class D8AlarmTimeAdapter extends WLBaseAdapter<String> {

    private LayoutInflater mInflater;
    private List<TimeEntity> mData;
    private Context context;
    private static final String TAG = "D8AlarmTimeAdapter";

    public D8AlarmTimeAdapter(Context context, List<String> data) {
        super(context, data);
        this.mData=new ArrayList<>();
        for(String temp:data){
            this.mData.add(new TimeEntity(temp));
        }

        this.context =context;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public String getItem(int position) {
        return super.getItem(position);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        D8AlarmTimeAdapter.ViewHolder holder = null;
        if (convertView == null) {
            holder = new D8AlarmTimeAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.item_mini_gateway_voice, null);
            holder.ivChecked = (ImageView) convertView.findViewById(R.id.iv_checked);
            holder.tvVoiceTime = (TextView) convertView.findViewById(R.id.tv_voice_type);
            convertView.setTag(holder);
        } else {
            holder = (D8AlarmTimeAdapter.ViewHolder) convertView.getTag();
        }
        TimeEntity timeEnitity=mData.get(position);
        holder.tvVoiceTime.setText(timeEnitity.strTime);
        if (timeEnitity.isChecked) {
            holder.ivChecked.setVisibility(View.VISIBLE);
        }else{
            holder.ivChecked.setVisibility(View.GONE);
        }
        return convertView;
    }

    public  class ViewHolder {
        public ImageView ivChecked;
        public TextView tvVoiceTime;
    }

    public class TimeEntity{
        public TimeEntity(String strTime){
            this.strTime=strTime;
        }
        public boolean isChecked=false;
        public String strTime;
    }

    public  void setSelectItem(int selectItem) {
        TimeEntity timeEntity=this.mData.get(selectItem);
        timeEntity.isChecked=!timeEntity.isChecked;
        this.selectItem = selectItem;
    }
    private int  selectItem=-1;

}
