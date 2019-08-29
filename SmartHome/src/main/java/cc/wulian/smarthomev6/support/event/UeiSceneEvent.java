package cc.wulian.smarthomev6.support.event;

/**
 * Created by 上海滩小马哥 on 2018/02/26.
 */

public class UeiSceneEvent {
    public String deviceState;     //遥控器名称
    public String deviceEpData;    //红外码

    public UeiSceneEvent(String deviceState, String deviceEpData) {
        this.deviceState = deviceState;
        this.deviceEpData = deviceEpData;
    }
}
