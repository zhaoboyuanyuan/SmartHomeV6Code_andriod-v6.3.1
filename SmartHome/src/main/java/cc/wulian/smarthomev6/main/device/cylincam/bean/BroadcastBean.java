package cc.wulian.smarthomev6.main.device.cylincam.bean;

import java.io.Serializable;

/**
 * created by huxc  on 2017/9/15.
 * func：  小物摄像机播报语言和音量Bean
 * email: hxc242313@qq.com
 */

public class BroadcastBean implements Serializable {
    private int language;//1.英语 2.汉语
    private int volume;//10.静音 -68.低 -36.中 -24.高 -2.超高

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
}

