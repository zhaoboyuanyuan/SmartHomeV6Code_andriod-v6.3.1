package cc.wulian.smarthomev6.support.core.device;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamDeviceBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamInfoBean;
import cc.wulian.smarthomev6.support.utils.Trans2PinYin;

/**
 * Created by zbl on 2017/3/15.
 * 设备抽象
 */

public class Device {
    public String cmd;
    public String devID;
    public String extData;
    public String gwID;
    public String subGwid; //雾计算子机id
    public String gwName;
    public String messageCode;
    /**
     * 设备状态
     * 0        状态改变
     * 1        上线
     * 2        下线
     * 3        删除
     * 4        加网
     */
    public int mode;

    public String name;
    public String roomID;
    public String roomName;
    public String time;
    public String type;
    public String deviceDesc;
    public boolean isShared = false;
    public String shareSource;
    public String sortStr = "";
    public int inner;
    /**
     * 大类
     */
    public int cat = 0;

    @JSONField(serialize = false)
    public String data;

    public List<Endpoint> endpoints;

    @JSONField(serialize = false)
    public List<Endpoint> endpointCache;

    /**
     * 设备是否在线
     */
    public boolean isOnLine() {
        return mode == 0 || mode == 1 || mode == 4;
    }

    /**
     * 是否为ZigBee设备
     *
     * @see DeviceInfoDictionary 有相似功能的方法
     * 后续若需要修改, 请同步修改:
     * @see DeviceInfoDictionary#isWiFiDevice(String)
     */
    public boolean isZigbee() {
        switch (type) {
            case "CMICA1":      // 智能猫眼
            case "CMICA2":      // 随便看摄像机
            case "CMICY1":      // 智能猫眼mini
            case "CMICA3":      // 企鹅摄像机
            case "CMICA6":      // 4G企鹅1080P摄像机
            case "CMICA4":      // 小物摄像机
            case "CMICA5":      // 户外高清摄像机
            case "HS01":        // 海信 家用空调
            case "HS02":        // 海信 中央空调
            case "HS03":        // 海信 油烟机
            case "HS04":        // 海信 燃气灶
            case "HS05":        // 海信 冰箱
            case "HS06":        // 海信 洗衣机
            case "sd01":        // 安全狗
            case "XW01":        // 向往背景音乐
            case "IF02":        // wifi红外转发器
            case "CG22":        // 乐橙户外
            case "CG23":        // 乐橙户外海外版
            case "CG24":        // 乐橙云台
            case "CG25":        // 乐橙云台海外版
            case "CG26":        // 乐橙硬盘录像机
            case "CG27":        // 罗格朗门禁
                return false;
            default:
                return true;
        }
    }

    public Device() {
    }

    //猫眼
    public Device(DeviceBean bean) {
        devID = bean.deviceId;
        type = bean.type;
        if (TextUtils.isEmpty(bean.name)) {
            name = DeviceInfoDictionary.DEVICE_NAME_PLACE_HOLDER + devID.substring(devID.length() - 4);
        } else {
            name = bean.name;
        }
        mode = "1".equals(bean.state) ? 1 : 2;
        isShared = bean.isShared();

        //分享设备的分享者信息
        if (isShared) {
            shareSource = bean.granterUserPhone;
            if (TextUtils.isEmpty(shareSource)) {
                shareSource = bean.granterUserEmail;
            }
        }

        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(this));
        jsonObject.put("sdomain", bean.sdomain);
        jsonObject.put("location", bean.location);
        jsonObject.put("version", bean.version);
        data = jsonObject.toString();
    }

    //猫眼/随便看（爱看接口）
    public Device(ICamInfoBean bean) {
        devID = bean.deviceId;
        type = bean.type;
        if (TextUtils.isEmpty(bean.name)) {
            name = DeviceInfoDictionary.DEVICE_NAME_PLACE_HOLDER + devID.substring(devID.length() - 4);
        } else {
            name = bean.name;
        }
//        mode = bean.online.equals("1")? 1 : 2;
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(this));
        jsonObject.put("sdomain", bean.sdomain);
        jsonObject.put("location", bean.location);
        jsonObject.put("version", bean.version);
        data = jsonObject.toString();
    }

    //移康猫眼
    public Device(EquesBean bean) {
        devID = bean.bid;
        gwID = bean.bid;
        if (TextUtils.isEmpty(bean.nick)) {
            name = DeviceInfoDictionary.DEVICE_NAME_PLACE_HOLDER + devID.substring(devID.length() - 4);
        } else {
            name = bean.nick;
        }
        type = "CMICY1";
        mode = "1".equals(bean.status) ? 1 : 2;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("uid", bean.uid);
        jsonObject.put("nid", bean.nid);
        jsonObject.put("localupg", bean.localupg);
        jsonObject.put("remoteupg", bean.remoteupg);
        jsonObject.put("wifimac", bean.name);
        data = jsonObject.toString();
    }

    public void setName(String name) {
        this.name = name;
        this.sortStr = Trans2PinYin.trans2PinYin(getName().trim()).toLowerCase();
    }

    public String getName() {
        return DeviceInfoDictionary.getNameByTypeAndName(type, name);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Device{");
        sb.append("cmd='").append(cmd).append('\'');
        sb.append(", devID='").append(devID).append('\'');
        sb.append(", extData='").append(extData).append('\'');
        sb.append(", gwID='").append(gwID).append('\'');
        sb.append(", subGwid='").append(subGwid).append('\'');
        sb.append(", gwName='").append(gwName).append('\'');
        sb.append(", messageCode='").append(messageCode).append('\'');
        sb.append(", mode=").append(mode);
        sb.append(", name='").append(name).append('\'');
        sb.append(", roomID='").append(roomID).append('\'');
        sb.append(", roomName='").append(roomName).append('\'');
        sb.append(", time='").append(time).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append(", data='").append(data).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
