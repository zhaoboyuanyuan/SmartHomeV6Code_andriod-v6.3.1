package cc.wulian.smarthomev6.support.customview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.InformationSensorDeviceItem;

/**
 * Created by Administrator on 2016/10/18.
 */

public class DeviceStatusItem {
    protected Context mContext;
    protected LayoutInflater inflater;
    private View view;

    private LinearLayout firstLl;
    private LinearLayout secondLl;
    private LinearLayout thirdLl;
    private LinearLayout fourthLl;

    public LinearLayout getFirstLl() {
        return firstLl;
    }

    public LinearLayout getFourthLl() {
        return fourthLl;
    }

    public LinearLayout getSecondLl() {
        return secondLl;
    }

    public LinearLayout getThirdLl() {
        return thirdLl;
    }

    public View getView() {
        return view;
    }

    private List<InformationSensorDeviceItem> data;


    public DeviceStatusItem(Context context, List<InformationSensorDeviceItem> data){
        this.mContext = context;
        inflater = LayoutInflater.from(this.mContext);
        this.data=data;
        initSystemState();
    }

    private void initSystemState(){
        view = inflater.inflate(R.layout.information_device_status_item, null);
        firstLl= (LinearLayout) view.findViewById(R.id.one);
        secondLl= (LinearLayout) view.findViewById(R.id.two);
        thirdLl= (LinearLayout) view.findViewById(R.id.three);
        fourthLl= (LinearLayout) view.findViewById(R.id.four);

        if(data!=null){
            for(int i=0;i<data.size();i++){
                switch (i){
                    case 0:
                        firstLl.removeAllViews();
                        ViewGroup viewGroup=(ViewGroup)data.get(0).getView().getParent();
                        if(viewGroup!=null){
                            viewGroup.removeAllViews();
                        }
                        firstLl.addView(data.get(0).getView(), WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                        break;
                    case 1:
                        secondLl.removeAllViews();
                        ViewGroup viewGroup1=(ViewGroup)data.get(1).getView().getParent();
                        if(viewGroup1!=null){
                            viewGroup1.removeAllViews();
                        }
                        secondLl.addView(data.get(1).getView(), WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                        break;
                    case 2:
                        thirdLl.removeAllViews();
                        ViewGroup viewGroup2=(ViewGroup)data.get(2).getView().getParent();
                        if(viewGroup2!=null){
                            viewGroup2.removeAllViews();
                        }
                        thirdLl.addView(data.get(2).getView(), WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                        break;
                    case 3:
                        fourthLl.removeAllViews();
                        ViewGroup viewGroup3=(ViewGroup)data.get(3).getView().getParent();
                        if(viewGroup3!=null){
                            viewGroup3.removeAllViews();
                        }
                        fourthLl.addView(data.get(3).getView(), WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                        break;
                }
            }
        }

    }

}
