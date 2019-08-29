package cc.wulian.smarthomev6.entity;

import android.text.TextUtils;

import cc.wulian.smarthomev6.support.core.apiunit.bean.HouseKeeperLogBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.SceneLogBean;
import cc.wulian.smarthomev6.support.tools.MessageTool;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by Veev on 2017/7/11
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    MessageNewInfo
 */
public class HouseKeeperMessageNewInfo {
    private HouseKeeperLogBean.HouseKeeperData houseKeeperData;
    private boolean mDayFirst;
    private boolean mDayLast;
    private boolean mIsDate;
    private String mDate;
    private String mMsg;
    private String mFullDate;

    public static HouseKeeperMessageNewInfo getMessageBean(HouseKeeperLogBean.HouseKeeperData houseKeeperData) {
        HouseKeeperMessageNewInfo info = new HouseKeeperMessageNewInfo(houseKeeperData);
        info.mDayFirst = false;
        info.mDayLast = false;
        info.mIsDate = false;
        WLog.i("MessageNewInfo", "date: " + info.mDate + ", full: " + info.mFullDate);
        return info;
    }

    public static HouseKeeperMessageNewInfo getDateBean(HouseKeeperLogBean.HouseKeeperData houseKeeperData) {
        HouseKeeperMessageNewInfo info = new HouseKeeperMessageNewInfo(houseKeeperData);
        info.mDayFirst = true;
        info.mDayLast = false;
        info.mIsDate = true;
        WLog.i("MessageNewInfo", "date: " + info.mDate + ", full: " + info.mFullDate);
        return info;
    }

    private HouseKeeperMessageNewInfo(HouseKeeperLogBean.HouseKeeperData houseKeeperData) {
        this.houseKeeperData = houseKeeperData;
        mDate = MessageTool.getMessageTime(Long.parseLong(houseKeeperData.time));
        mMsg = houseKeeperData.name;
        mFullDate = MessageTool.getDate(Long.parseLong(houseKeeperData.time));
    }

    public boolean isSameDay(HouseKeeperMessageNewInfo that) {
        return TextUtils.equals(mFullDate, that.getFullDate());
    }

    public HouseKeeperLogBean.HouseKeeperData getRecordListBean() {
        return houseKeeperData;
    }

    public void setRecordListBean(HouseKeeperLogBean.HouseKeeperData houseKeeperData) {
        houseKeeperData = houseKeeperData;
    }

    public boolean isDayFirst() {
        return mDayFirst;
    }

    public void setDayFirst(boolean dayFirst) {
        mDayFirst = dayFirst;
    }

    public boolean isDayLast() {
        return mDayLast;
    }

    public void setDayLast(boolean dayLast) {
        mDayLast = dayLast;
    }

    public boolean isDate() {
        return mIsDate;
    }

    public void setDate(boolean date) {
        mIsDate = date;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getMsg() {
        return mMsg;
    }

    public void setMsg(String msg) {
        mMsg = msg;
    }

    public String getFullDate() {
        return mFullDate;
    }

    public void setFullDate(String fullDate) {
        mFullDate = fullDate;
    }
}
