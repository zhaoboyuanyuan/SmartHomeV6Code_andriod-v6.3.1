package cc.wulian.smarthomev6.support.tools.dialog;

import android.view.View;

/**
 * Created by Veev on 2017/6/22
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    MessageListener 的 adapter
 */

public abstract class MessageListenerAdapter implements WLDialog.MessageListener {
    @Override
    public void onClickPositive(View var1, String msg){
    }

    @Override
    public void onClickNegative(View var1) {
    }
}
