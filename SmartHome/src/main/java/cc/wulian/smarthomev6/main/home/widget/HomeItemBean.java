package cc.wulian.smarthomev6.main.home.widget;

import android.text.TextUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

import java.io.Serializable;

import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.utils.StringUtil;

/**
 * 作者: chao
 * 时间: 2017/5/8
 * 描述: 首页widget类
 * 联系方式: 805901025@qq.com
 */
@Entity
public class HomeItemBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotNull
    private String widgetID;
    private String type;
    private int asciiType;
    private String name;
    private boolean isAdd;
    private boolean show;

    @NotNull
    private int sort;
    @NotNull
    private String user;
    @NotNull
    private String gwID;

    private String extData;

    public HomeItemBean(String widgetID, String type, int asciiType, String name, boolean isAdd, boolean show, int sort, String user, String gwID) {
        this.widgetID = widgetID;
        this.type = type;
        this.name = name;
        this.asciiType = asciiType;
        this.isAdd = isAdd;
        this.show = show;
        this.sort = sort;
        this.user = user;
        this.gwID = gwID;
    }

    public void update(Device device) {
        if (!TextUtils.isEmpty(device.name)) {
            this.name = device.name;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HomeItemBean that = (HomeItemBean) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public HomeItemBean() {
    }
    @Generated(hash = 1635573268)
    public HomeItemBean(Long id, @NotNull String widgetID, String type, int asciiType, String name, boolean isAdd, boolean show, int sort,
            @NotNull String user, @NotNull String gwID, String extData) {
        this.id = id;
        this.widgetID = widgetID;
        this.type = type;
        this.asciiType = asciiType;
        this.name = name;
        this.isAdd = isAdd;
        this.show = show;
        this.sort = sort;
        this.user = user;
        this.gwID = gwID;
        this.extData = extData;
    }

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getWidgetID() {
        return this.widgetID;
    }
    public void setWidgetID(String widgetID) {
        this.widgetID = widgetID;
    }
    public String getType() {
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean getIsAdd() {
        return this.isAdd;
    }
    public void setIsAdd(boolean isAdd) {
        this.isAdd = isAdd;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public int getAsciiType() {
        return this.asciiType;
    }
    public void setAsciiType(int asciiType) {
        this.asciiType = asciiType;
    }
    public int getSort() {
        return this.sort;
    }
    public void setSort(int sort) {
        this.sort = sort;
    }
    public String getUser() {
        return this.user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public String getGwID() {
        return this.gwID;
    }
    public void setGwID(String gwID) {
        this.gwID = gwID;
    }

    public boolean getShow() {
        return this.show;
    }

    public String getExtData() {
        return extData;
    }

    public void setExtData(String extData) {
        this.extData = extData;
    }
}
