package cc.wulian.smarthomev6.support.tools.dialog;


import android.content.Context;
import android.content.DialogInterface;

import cc.wulian.smarthomev6.R;

/**
 * Created by syf on 2017/2/23.
 */
public class SingleErrorDialog {
    public static SingleErrorDialog instant = null;
    private static boolean isShowning = false;
    private WLDialog dialog = null;

    private SingleErrorDialog() {
    }

    public static SingleErrorDialog getInstant() {
        if(instant == null) {
            Class var0 = SingleErrorDialog.class;
            synchronized(SingleErrorDialog.class) {
                if(instant == null) {
                    instant = new SingleErrorDialog();
                }
            }
        }

        return instant;
    }

    public void showErrorDialog(Context mContext) {
        WLDialog.Builder builder = new WLDialog.Builder(mContext);
        builder.setNegativeButton((String)null);
        builder.setPositiveButton(R.string.Http_Time_Out_Off);
        builder.setContentView(R.layout.dialog_error_content);
        if(this.dialog == null) {
            this.dialog = builder.create();
        }

        this.dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog1) {
                SingleErrorDialog.isShowning = false;
                SingleErrorDialog.this.dialog = null;
            }
        });
        if(!isShowning) {
            isShowning = true;
            this.dialog.show();
        }

    }
}
