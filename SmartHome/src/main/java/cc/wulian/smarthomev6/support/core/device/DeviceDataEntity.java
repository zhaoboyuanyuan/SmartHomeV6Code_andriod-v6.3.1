package cc.wulian.smarthomev6.support.core.device;

import com.alibaba.fastjson.JSON;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by zbl on 2018/1/10.
 * 设备信息缓存数据库
 */

@Entity
public class DeviceDataEntity {

    @Id
    private Long id;
    @Property
    private String gwID;
    @Property
    private String devID;
    @Property
    private String type;
    @Property
    private String data;

    @Generated(hash = 933540491)
    public DeviceDataEntity(Long id, String gwID, String devID, String type,
                            String data) {
        this.id = id;
        this.gwID = gwID;
        this.devID = devID;
        this.type = type;
        this.data = data;
    }

    @Generated(hash = 1513375530)
    public DeviceDataEntity() {
    }

    public DeviceDataEntity(Device device) {
        this.gwID = device.gwID;
        this.devID = device.devID;
        this.type = device.type;
        setDeviceData(device);
    }

    public void setDeviceData(Device device) {
        this.data = JSON.toJSONString(device);
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

    public String getDevID() {
        return this.devID;
    }

    public void setDevID(String devID) {
        this.devID = devID;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
