package cc.wulian.smarthomev6.entity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cc.wulian.smarthomev6.R;

/**
 * Created by Administrator on 2016/10/18.
 */

public class InformationSensorDeviceItem {
    protected Context mContext;
    protected LayoutInflater inflater;
    private View view;
    private ImageView deviceImagView;
    private TextView deviceDataTextView;
    private TextView deviceTypeTextView;
    private TextView deviceDescribeTextView;

    public InformationSensorDeviceItem(Context context){
        this.mContext = context;
        inflater = LayoutInflater.from(this.mContext);
        initSystemState();
    }

    private void initSystemState(){
        view = inflater.inflate(R.layout.home_environment_item, null);
        deviceImagView= (ImageView) view.findViewById(R.id.device_imagview);
        deviceDataTextView= (TextView) view.findViewById(R.id.device_data_tv);
        deviceTypeTextView= (TextView) view.findViewById(R.id.device_type_tv);
        deviceDescribeTextView= (TextView) view.findViewById(R.id.device_describe_tv);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(deviceItemClickListener!=null){
                    deviceItemClickListener.doSomeThing();
                }
            }
        });
    }

    public TextView getDeviceDataTextView() {
        return deviceDataTextView;
    }

    public TextView getDeviceDescribeTextView() {
        return deviceDescribeTextView;
    }

    public ImageView getDeviceImagView() {
        return deviceImagView;
    }

    public TextView getDeviceTypeTextView() {
        return deviceTypeTextView;
    }

    public View getView() {
        return view;
    }

    private DeviceItemClickListener deviceItemClickListener;

    public void setDeviceItemClickListener(DeviceItemClickListener deviceItemClickListener){
        this.deviceItemClickListener=deviceItemClickListener;
    }

    public interface DeviceItemClickListener{
        public void doSomeThing();
    }

}
