package cc.wulian.smarthomev6.support.core.device;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.GatewayInfoBean;

/**
 * Created by zbl on 2018/4/16.
 * 网关信息缓存数据库
 */

@Entity
public class GatewayDataEntity {

    @Id
    private Long id;
    @Property
    private String gwID;
    @Property
    private String name;
    @Property
    private String softVer;
    @Property
    private String type;
    @Property
    private String state;
    @Property
    private String data;

    public GatewayDataEntity() {
    }

    public GatewayDataEntity(DeviceBean device) {
        setData(device);
    }

    public GatewayDataEntity(GatewayInfoBean bean) {
        setData(bean);
    }

    @Generated(hash = 732287606)
    public GatewayDataEntity(Long id, String gwID, String name, String softVer,
                             String type, String state, String data) {
        this.id = id;
        this.gwID = gwID;
        this.name = name;
        this.softVer = softVer;
        this.type = type;
        this.state = state;
        this.data = data;
    }

    public void setData(DeviceBean device) {
        this.gwID = device.deviceId;
        this.name = device.name;
        this.softVer = device.softVersion;
        this.type = device.type;
        this.state = device.state;
        this.data = JSON.toJSONString(device);
    }

    public void setData(GatewayInfoBean bean) {
        this.gwID = bean.gwID;
        this.name = bean.gwName;
        this.softVer = bean.gwVer;
        this.type = bean.gwType;
    }

    public DeviceBean getDeviceBean() {
        if (TextUtils.isEmpty(this.data)) {
            return null;
        } else {
            return JSON.parseObject(this.data, DeviceBean.class);
        }
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGwID() {
        return this.gwID;
    }

    public void setGwID(String gwID) {
        this.gwID = gwID;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSoftVer() {
        return this.softVer;
    }

    public void setSoftVer(String softVer) {
        this.softVer = softVer;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }
}
