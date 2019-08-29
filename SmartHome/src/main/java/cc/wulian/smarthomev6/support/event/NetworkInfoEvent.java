package cc.wulian.smarthomev6.support.event;

import android.net.NetworkInfo;

/**
 * created by huxc  on 2018/1/25.
 * func：
 * email: hxc242313@qq.com
 */

public class NetworkInfoEvent {
    public  NetworkInfo networkInfo;
    public NetworkInfoEvent(NetworkInfo networkInfo){
        this.networkInfo = networkInfo;
    }
}
