package cc.wulian.smarthomev6.main.h5;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.webkit.WebView;

/**
 * Created by Veev on 2017/8/7
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    WWebView
 */

public class WWebView extends WebView {
    public WWebView(Context context) {
        super(context);
    }

    public WWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
//        outAttrs.imeOptions = EditorInfo.IME_ACTION_SEND;
        return super.onCreateInputConnection(outAttrs);
    }
}
