package cc.wulian.smarthomev6.entity;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cc.wulian.smarthomev6.main.device.eques.bean.EquesAlarmDetailBean;
import cc.wulian.smarthomev6.support.tools.MessageTool;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * 作者: chao
 * 时间: 2017/7/18
 * 描述:
 * 联系方式: 805901025@qq.com
 */

public class EquesAlarmNewInfo {

    private EquesAlarmDetailBean mRecordListBean;
    private boolean mDayFirst;
    private boolean mDayLast;
    private boolean mIsDate;
    private String mFullDate;

    public String uid;
    public String aid;
    public String time;
    public List<String> fid;  //报警图片 id 列表
    public String bid;
    public int type;
    public List<String> pvid;   //预览图片(缩略图)id 列表
    public String name;
    public long create;
    public int recordType;

    public static EquesAlarmNewInfo getMessageBean(EquesAlarmDetailBean recordListBean) {
        EquesAlarmNewInfo info = new EquesAlarmNewInfo(recordListBean);
        info.mDayFirst = false;
        info.mDayLast = false;
        info.mIsDate = false;
        WLog.i("EquesAlarmNewInfo", "date: " + info.time + ", full: " + info.mFullDate);
        return info;
    }

    public static EquesAlarmNewInfo getDateBean(EquesAlarmDetailBean recordListBean) {
        EquesAlarmNewInfo info = new EquesAlarmNewInfo(recordListBean);
        info.mDayFirst = true;
        info.mDayLast = false;
        info.mIsDate = true;
        WLog.i("EquesAlarmNewInfo", "date: " + info.time + ", full: " + info.mFullDate);
        return info;
    }

    private EquesAlarmNewInfo(EquesAlarmDetailBean recordListBean){
        mRecordListBean = recordListBean;
        time = stampToDate(recordListBean.time);
        uid = recordListBean.uid;
        fid = recordListBean.fid;
        bid = recordListBean.bid;
        type = recordListBean.type;
        pvid = recordListBean.pvid;
        name = recordListBean.name;
        create = recordListBean.create;
        recordType = recordListBean.recordType;
        mFullDate = MessageTool.getDate(recordListBean.time);
    }

    public boolean isSameDay(EquesAlarmNewInfo that) {
        return TextUtils.equals(mFullDate, that.getFullDate());
    }

    public EquesAlarmDetailBean getRecordListBean() {
        return mRecordListBean;
    }

    public void setRecordListBean(EquesAlarmDetailBean recordListBean) {
        mRecordListBean = recordListBean;
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
