package cc.wulian.smarthomev6.support.tools.singlechoice;

import android.widget.Checkable;

/**
 * Created by Veev on 2017/8/22
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    单选接口
 */
public interface SingleChoice extends Checkable {
    void addCheckedListener(CheckedListener listener);
    void removeCheckedListener(CheckedListener listener);
    void clearCheckedListener(CheckedListener listener);
}
