package cc.wulian.smarthomev6.support.core.device;

import android.util.ArrayMap;

import org.greenrobot.eventbus.EventBus;

import cc.wulian.smarthomev6.support.core.apiunit.bean.MessageCountBean;
import cc.wulian.smarthomev6.support.event.AlarmUpdateEvent;

/**
 * Created by Veev on 2017/4/25
 * Tel: 18365264930
 * QQ: 2355738466
 * Function: 未读消息数量缓存
 */

public class AlarmCountCache {

    // 报警消息数， 日志消息数
    private int alarmTotalCount = 0, logTotalCount = 0;

    /**
     * 未读报警，每个设备对应的数量
     */
    private ArrayMap<String, Integer> alarmMap = new ArrayMap<>();
    /**
     * 未读日志，每个设备对应的数量
     */
    private ArrayMap<String, Integer> logMap = new ArrayMap<>();

    public AlarmCountCache() {
    }

    /**
     * 未读报警数 自增
     * @param devID     设备ID
     */
    public synchronized void alarmCountAtom(String devID) {
        Integer i = alarmMap.get(devID);
        // 没有记录
        // 即为0
        if (i == null) {
            i = 0;
        }

        // 记录
        // 总数 +1
        alarmMap.put(devID, i + 1);
        alarmTotalCount += 1;

        // 更新小红点
        EventBus.getDefault().post(new AlarmUpdateEvent());
    }

    /**
     * 清除某设备的  报警未读数
     * @param devID     设备ID
     */
    public synchronized void clearAlarmCloudCount(String devID) {
        Integer count = alarmMap.get(devID);
        if (count != null) {
            alarmMap.remove(devID);
            alarmTotalCount -= count;
            // 更新小红点
            EventBus.getDefault().post(new AlarmUpdateEvent());
        }
    }

    /**
     * 清除 某设备的 日志未读数
     * @param devID     设备ID
     */
    public synchronized void clearLogCloudCount(String devID) {
        Integer count = logMap.get(devID);
        if (count != null) {
            logMap.remove(devID);
            logTotalCount -= count;
            EventBus.getDefault().post(new AlarmUpdateEvent());
        }
    }

    /**
     * 设置全部的报警数量
     * @param bean      云报警列表
     */
    public synchronized void setAlarmCount(MessageCountBean bean) {
        alarmMap.clear();
        alarmTotalCount = bean.totalCount;
        for (MessageCountBean.ChildDevicesBean child : bean.childDevices) {
            alarmMap.put(child.childDeviceId, child.count);
        }

        EventBus.getDefault().post(new AlarmUpdateEvent());
    }

    /**
     * 设置全部的日志数量
     * @param bean      云日志列表
     */
    public synchronized void setLogCount(MessageCountBean bean) {
        logMap.clear();
        logTotalCount = bean.totalCount;
        for (MessageCountBean.ChildDevicesBean child : bean.childDevices) {
            logMap.put(child.childDeviceId, child.count);
        }

        EventBus.getDefault().post(new AlarmUpdateEvent());
    }

    /**
     * 清楚全部
     */
    public synchronized void clear() {
        alarmMap.clear();
        alarmTotalCount = 0;

        logMap.clear();
        logTotalCount = 0;

        EventBus.getDefault().post(new AlarmUpdateEvent());
    }

    /**
     * 获取报警数量
     */
    public int getAlarmTotalCount() {
        return alarmTotalCount;
    }

    /**
     * 获取日志数量
     */
    public int getLogTotalCount() {
        return logTotalCount;
    }

    /**
     * 获取某个设备的 报警未读
     * @param devID     设备ID
     * @return          未读数量
     */
    public int getAlarmChildCount(String devID) {
        Integer count = alarmMap.get(devID);
        return count == null ? 0 : count;
    }

    /**
     * 获取某个设备的 日志未读
     * @param devID     设备ID
     * @return          未读数量
     */
    public int getLogChildCount(String devID) {
        Integer count = logMap.get(devID);
        return count == null ? 0 : count;
    }

    /**
     * 日志  全部已读
     */
    public synchronized void readAllLog() {
        for (String key : logMap.keySet()) {
            logMap.put(key, 0);
        }

        logTotalCount = 0;
        EventBus.getDefault().post(new AlarmUpdateEvent());
    }

    /**
     * 报警  全部已读
     */
    public synchronized void readAllAlarm() {
        for (String key : alarmMap.keySet()) {
            alarmMap.put(key, 0);
        }

        alarmTotalCount = 0;
        EventBus.getDefault().post(new AlarmUpdateEvent());
    }
}
