package cc.wulian.smarthomev6.support.core.socket.receiver;


import cc.wulian.smarthomev6.support.core.socket.SocketClient;

/**
 * Created by zbl on 2017/3/14.
 */

public interface ISocketReceiver {
    void onReceive(SocketClient client, String text);
}
