package cc.wulian.smarthomev6.main.home.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

import cc.wulian.smarthomev6.support.utils.DisplayUtil;

/**
 * Created by zbl on 2018/2/24.
 * 语音控制，帮助详细清单
 */

public class VoiceControlHelpDetailView extends ScrollView {

    private Context context;
    private LinearLayout listLayout;

    private int textSize, itemHeight, leftMargin;
    private int textColor = 0xffffffff;

    public VoiceControlHelpDetailView(Context context) {
        super(context);
        init(context);
    }

    public VoiceControlHelpDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VoiceControlHelpDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        initParams();
        listLayout = new LinearLayout(context);
        listLayout.setOrientation(LinearLayout.VERTICAL);
        listLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        ScrollView.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(listLayout, params);
    }

    private void initParams() {
        itemHeight = DisplayUtil.dip2Pix(context, 40);
        textSize = 20;//DisplayUtil.dip2Pix(context, 20);
        leftMargin = DisplayUtil.dip2Pix(context, 36);
    }

    public void setContent(List<String> helpList, List<String> supportList) {
        listLayout.removeAllViews();
        if (helpList != null && helpList.size() > 0) {
            TextView tv_helpTitle = new TextView(context);
            tv_helpTitle.setText("您可以这样说：");
            tv_helpTitle.setTextColor(textColor);
            tv_helpTitle.setTextSize(textSize);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, itemHeight);
            lp.leftMargin = leftMargin;
            lp.bottomMargin = itemHeight / 2;
            listLayout.addView(tv_helpTitle, lp);
            for (String text : helpList) {
                TextView tv_helpItem = new TextView(context);
                tv_helpItem.setText(text);
                tv_helpItem.setTextColor(textColor);
                tv_helpItem.setTextSize(textSize);
                tv_helpItem.setGravity(Gravity.CENTER_VERTICAL);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, itemHeight);
                listLayout.addView(tv_helpItem, layoutParams);
            }
        }
        if (supportList != null && supportList.size() > 0) {
            TextView tv_supportTitle = new TextView(context);
            tv_supportTitle.setText("支持设备列表：");
            tv_supportTitle.setTextColor(textColor);
            tv_supportTitle.setTextSize(textSize);
            tv_supportTitle.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, itemHeight);
            lp.leftMargin = leftMargin;
            lp.topMargin = itemHeight;
            listLayout.addView(tv_supportTitle, lp);
            for (String text : supportList) {
                TextView tv_supportItem = new TextView(context);
                tv_supportItem.setText("-" + text);
                tv_supportItem.setTextColor(textColor);
                tv_supportItem.setTextSize(textSize);
                tv_supportItem.setGravity(Gravity.CENTER_VERTICAL);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, itemHeight);
                layoutParams.leftMargin = leftMargin;
                listLayout.addView(tv_supportItem, layoutParams);
            }
        }
    }
}
