package cc.wulian.smarthomev6.support.core.apiunit.bean;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hxc on 2017/7/19.
 * Func:    金属三键开关bean
 * Email:  hxc242313@qq.com
 */

public class DeviceAoBean {
    public DeviceAoBean() {
        extDataBeanList = new ArrayList<>();
    }

    public String cmd;
    public String devID;
    public String gwID;
    public String gwName;
    public String messageCode;
    public String mode;
    public String name;
    public String roomName;
    public String roomID;
    public String type;
    public String time;
    public List<DeviceAoExtDataBean> extDataBeanList;

    public void initExtDataBean(String devType) {
        if (TextUtils.equals(devType, "Ao")) {
            extDataBeanList.add(DeviceAoExtDataBean.createNewBtn(1));
            extDataBeanList.add(DeviceAoExtDataBean.createNewBtn(2));
            extDataBeanList.add(DeviceAoExtDataBean.createNewBtn(3));
        }
    }

    public DeviceAoExtDataBean getExtBeanByEpNum(int epNum) {
        DeviceAoExtDataBean extDataBean = null;
        for (DeviceAoExtDataBean bean : extDataBeanList) {
            if (bean.endpointNumber == epNum) {
                extDataBean = bean;
                break;
            }
        }
        return extDataBean;
    }

    public JSONObject getExtData(JSONArray extDatas, int epNum) {
        JSONObject extJson = null;
        if (extDatas != null && extDatas.length() > 0) {
            for (int i = 0; i < extDatas.length(); i++) {
                try {
                    if (extDatas.getJSONObject(i).getInt("endpointNumber") == epNum) {
                        extJson = extDatas.getJSONObject(i);
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return extJson;
    }
}
