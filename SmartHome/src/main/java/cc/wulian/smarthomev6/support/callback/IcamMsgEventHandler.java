package cc.wulian.smarthomev6.support.callback;

import com.wulian.sdk.android.ipc.rtcv2.message.IPCcameraXmlMsgEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Administrator on 2017/6/12.
 */

public interface IcamMsgEventHandler {
    void onMessageEvent(IPCcameraXmlMsgEvent event);
}
