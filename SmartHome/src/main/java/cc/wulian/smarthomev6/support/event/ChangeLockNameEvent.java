package cc.wulian.smarthomev6.support.event;

/**
 * Created by Veev on 2017/5/22
 * Tel: 18365264930
 * QQ: 2355738466
 * Function: 检测到开锁时, 需要提示用户更改开锁人的昵称 发送这个事件
 */

public class ChangeLockNameEvent {
    public String devID;

    public ChangeLockNameEvent(String devID) {
        this.devID = devID;
    }
}
