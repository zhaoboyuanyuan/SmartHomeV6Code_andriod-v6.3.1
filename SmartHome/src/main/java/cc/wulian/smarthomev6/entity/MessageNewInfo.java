package cc.wulian.smarthomev6.entity;

import android.text.TextUtils;

import cc.wulian.smarthomev6.support.core.apiunit.bean.MessageBean;
import cc.wulian.smarthomev6.support.tools.MessageTool;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by Veev on 2017/7/11
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    MessageNewInfo
 */
public class MessageNewInfo {
    private MessageBean.RecordListBean mRecordListBean;
    private boolean mDayFirst;
    private boolean mDayLast;
    private boolean mIsDate;
    private String mDate;
    private String mMsg;
    private String mFullDate;

    public static MessageNewInfo getMessageBean(MessageBean.RecordListBean recordListBean) {
        MessageNewInfo info = new MessageNewInfo(recordListBean);
        info.mDayFirst = false;
        info.mDayLast = false;
        info.mIsDate = false;
        WLog.i("MessageNewInfo", "date: " + info.mDate + ", full: " + info.mFullDate);
        return info;
    }

    public static MessageNewInfo getDateBean(MessageBean.RecordListBean recordListBean) {
        MessageNewInfo info = new MessageNewInfo(recordListBean);
        info.mDayFirst = true;
        info.mDayLast = false;
        info.mIsDate = true;
        WLog.i("MessageNewInfo", "date: " + info.mDate + ", full: " + info.mFullDate);
        return info;
    }

    private MessageNewInfo(MessageBean.RecordListBean recordListBean){
        mRecordListBean = recordListBean;
        mDate = MessageTool.getMessageTime(recordListBean.time);
        mMsg = MessageTool.getAlarmMessage(recordListBean);
        mFullDate = MessageTool.getDate(recordListBean.time);
    }

    public boolean isSameDay(MessageNewInfo that) {
        return TextUtils.equals(mFullDate, that.getFullDate());
    }

    public MessageBean.RecordListBean getRecordListBean() {
        return mRecordListBean;
    }

    public void setRecordListBean(MessageBean.RecordListBean recordListBean) {
        mRecordListBean = recordListBean;
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
