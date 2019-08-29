package cc.wulian.smarthomev6.support.event;

/**
 * Created by Veev on 2017/6/19
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    键盘事件
 */

public class KeyboardEvent {
    public static final int KEYBOARD_SHOW = 1;      // 键盘弹起
    public static final int KEYBOARD_HIDE = 2;      // 键盘消失

    public int type;
    public int keyboardHeight;

    public KeyboardEvent(int type) {
        this.type = type;
    }

    public KeyboardEvent(int type, int keyboardHeight) {
        this.type = type;
        this.keyboardHeight = keyboardHeight;
    }
}
