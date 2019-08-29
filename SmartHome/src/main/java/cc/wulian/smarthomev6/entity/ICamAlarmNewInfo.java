package cc.wulian.smarthomev6.entity;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamAlarmUrlBean;
import cc.wulian.smarthomev6.support.tools.MessageTool;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * 作者: chao
 * 时间: 2017/7/19
 * 描述: 爱看报警信息
 * 联系方式: 805901025@qq.com
 */

public class ICamAlarmNewInfo {
    private ICamAlarmUrlBean mICamAlarmUrlBean;
    private boolean mDayFirst;
    private boolean mDayLast;
    private boolean mIsDate;
    private String mFullDate;

    public String url;
    public String isread;
    public String time;
    public String url_pic;
    public String url_video;

    public static ICamAlarmNewInfo getMessageBean(ICamAlarmUrlBean mICamAlarmUrlBean) {
        ICamAlarmNewInfo info = new ICamAlarmNewInfo(mICamAlarmUrlBean);
        info.mDayFirst = false;
        info.mDayLast = false;
        info.mIsDate = false;
        WLog.i("ICamAlarmNewInfo", "date: " + info.time + ", full: " + info.mFullDate);
        return info;
    }

    public static ICamAlarmNewInfo getDateBean(ICamAlarmUrlBean mICamAlarmUrlBean) {
        ICamAlarmNewInfo info = new ICamAlarmNewInfo(mICamAlarmUrlBean);
        info.mDayFirst = true;
        info.mDayLast = false;
        info.mIsDate = true;
        WLog.i("ICamAlarmNewInfo", "date: " + info.time + ", full: " + info.mFullDate);
        return info;
    }

    private ICamAlarmNewInfo(ICamAlarmUrlBean mICamAlarmUrlBean) {
        this.mICamAlarmUrlBean = mICamAlarmUrlBean;
        mFullDate = MessageTool.getDate(mICamAlarmUrlBean.createdat);
        url = mICamAlarmUrlBean.url;
        isread = mICamAlarmUrlBean.isread;


//        if (mICamAlarmUrlBean.url_pic.startsWith("v/cmic08")) {
//            if (TextUtils.isEmpty(mICamAlarmUrlBean.url)) {
//                time = stampToDate(mICamAlarmUrlBean.url_pic);
//            } else {
//                time = stampToDate(mICamAlarmUrlBean.url);
//            }
//        } else {
            time = stampToDate(mICamAlarmUrlBean.createdat);
//        }

        url_pic = mICamAlarmUrlBean.url_pic;
        url_video = mICamAlarmUrlBean.url_video;
    }

    public boolean isSameDay(ICamAlarmNewInfo that) {
        return TextUtils.equals(mFullDate, that.getFullDate());
    }

    public ICamAlarmUrlBean getRecordListBean() {
        return mICamAlarmUrlBean;
    }

    public void setRecordListBean(ICamAlarmUrlBean mICamAlarmUrlBean) {
        this.mICamAlarmUrlBean = mICamAlarmUrlBean;
    }

    public static String stampToDate(String url) {
        String[] str = url.split("/");
        StringBuilder str1 = new StringBuilder(str[0]);
        StringBuilder str2 = new StringBuilder(str[2].split("_")[0]);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");

        long dateTime = 0;
        try {
            dateTime = simpleDateFormat.parse(str1+str2.toString()).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date date = new Date();
        DateFormat dateformat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        date.setTime(dateTime);
        return dateformat.format(date);
    }


    public static String stampToDate(long s)  {
        Date date = new Date();
        DateFormat dateformat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        date.setTime(s);
        return dateformat.format(date);
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
        return time;
    }

    public String getFullDate() {
        return mFullDate;
    }

    public void setFullDate(String fullDate) {
        mFullDate = fullDate;
    }
}