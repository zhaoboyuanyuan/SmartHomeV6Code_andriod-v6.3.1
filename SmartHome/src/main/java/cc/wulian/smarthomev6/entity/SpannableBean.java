package cc.wulian.smarthomev6.entity;

import android.view.View;

/**
 * 作者：luzx on 2017/6/12 10:11
 * 邮箱：zhengxiang.lu@wuliangroup.com
 */
public class SpannableBean {

    public int color;

    public int size;

    public View.OnClickListener onClickListener;


    public SpannableBean(int color, int size, View.OnClickListener onClickListener) {
        this.color = color;
        this.size = size;
        this.onClickListener = onClickListener;
    }
}
