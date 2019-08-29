package cc.wulian.smarthomev6.support.event;

/**
 * Created by Veev on 2017/9/25
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    UEI Event
 */

public class UEIEvent {
    /**
     * 事件类型
     * 1            添加遥控器
     */
    public int mode = 0;
    /**
     * 类型
     * Z            空调
     */
    public String type = "";

    public static UEIEvent addAir() {
        UEIEvent event = new UEIEvent();
        event.mode = 1;
        event.type = "Z";
        return event;
    }
}
