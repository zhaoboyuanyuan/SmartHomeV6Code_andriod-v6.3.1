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

public class DeviceBwBean {
    public DeviceBwBean() {
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
    public List<DeviceBwExtDataBean> extDataBeanList;

    public void initExtDataBean(String devType) {
        if (TextUtils.equals(devType, "Bw")) {
            extDataBeanList.add(DeviceBwExtDataBean.createNewBtn(1));
            extDataBeanList.add(DeviceBwExtDataBean.createNewBtn(2));
            extDataBeanList.add(DeviceBwExtDataBean.createNewBtn(3));
        }
    }

    public DeviceBwExtDataBean getExtBeanByEpNum(int epNum) {
        DeviceBwExtDataBean extDataBean = null;
        for (DeviceBwExtDataBean bean : extDataBeanList) {
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
