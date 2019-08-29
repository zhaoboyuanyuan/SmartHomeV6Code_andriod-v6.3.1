package cc.wulian.smarthomev6.entity;

import android.text.TextUtils;

import cc.wulian.smarthomev6.support.core.apiunit.bean.MessageBean;
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
public class SceneMessageNewInfo {
    private SceneLogBean.SceneData sceneData;
    private boolean mDayFirst;
    private boolean mDayLast;
    private boolean mIsDate;
    private String mDate;
    private String mMsg;
    private String mFullDate;

    public static SceneMessageNewInfo getMessageBean(SceneLogBean.SceneData sceneData) {
        SceneMessageNewInfo info = new SceneMessageNewInfo(sceneData);
        info.mDayFirst = false;
        info.mDayLast = false;
        info.mIsDate = false;
        WLog.i("MessageNewInfo", "date: " + info.mDate + ", full: " + info.mFullDate);
        return info;
    }

    public static SceneMessageNewInfo getDateBean(SceneLogBean.SceneData sceneData) {
        SceneMessageNewInfo info = new SceneMessageNewInfo(sceneData);
        info.mDayFirst = true;
        info.mDayLast = false;
        info.mIsDate = true;
        WLog.i("MessageNewInfo", "date: " + info.mDate + ", full: " + info.mFullDate);
        return info;
    }

    private SceneMessageNewInfo(SceneLogBean.SceneData sceneData) {
        this.sceneData = sceneData;
        mDate = MessageTool.getMessageTime(Long.parseLong(sceneData.time));
        mMsg = sceneData.name;
        mFullDate = MessageTool.getDate(Long.parseLong(sceneData.time));
    }

    public boolean isSameDay(SceneMessageNewInfo that) {
        return TextUtils.equals(mFullDate, that.getFullDate());
    }

    public SceneLogBean.SceneData getRecordListBean() {
        return sceneData;
    }

    public void setRecordListBean(SceneLogBean.SceneData sceneData) {
        sceneData = sceneData;
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
