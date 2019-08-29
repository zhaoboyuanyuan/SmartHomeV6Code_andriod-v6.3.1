package cc.wulian.smarthomev6.support.core.socket.receiver;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import cc.wulian.smarthomev6.support.core.socket.SocketClient;
import cc.wulian.smarthomev6.support.utils.DesUtil;

/**
 * Created by zbl on 2017/3/14.
 * 只执行一次监听的接收器
 */

public abstract class SingleCmdReceiver implements ISocketReceiver {

    private DesUtil desUtil;
    private String cmd;

    public SingleCmdReceiver(@NonNull String cmd, @NonNull DesUtil desUtil) {
        this.cmd = cmd;
        this.desUtil = desUtil;
    }

    @Override
    public void onReceive(SocketClient client, String text) {
        try {
            JSONObject jsonObject = new JSONObject(desUtil.Decode(text));
            String cmd = jsonObject.optString("cmd");
            if (cmd != null && cmd.equals(this.cmd)) {
                onReceive(jsonObject);
                client.unregistReceiver(this);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public abstract void onReceive(JSONObject jsonObject);
}
