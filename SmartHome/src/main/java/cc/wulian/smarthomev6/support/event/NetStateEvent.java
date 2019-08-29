package cc.wulian.smarthomev6.support.event;

/**
 * Created by zbl on 2017/3/21.
 */

public class NetStateEvent {

    public int state;
    public String text;

    public NetStateEvent(String text){
        this.text = text;
    }
}
