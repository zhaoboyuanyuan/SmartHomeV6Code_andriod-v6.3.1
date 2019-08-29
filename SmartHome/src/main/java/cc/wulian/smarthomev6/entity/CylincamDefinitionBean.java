package cc.wulian.smarthomev6.entity;

import android.content.Context;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;

/**
 * Created by 上海滩小马哥 on 2017/9/19.
 */

public class CylincamDefinitionBean {
    public int value;
    public String name;
    private Context context;

    public CylincamDefinitionBean(Context context, int value) {
        this.value = value;
        this.context = context;
        this.name = getNameResByValue(context, value);
    }

    public static String getNameResByValue(Context context, int value) {
        if (value == 1) {
            return context.getString(R.string.Super_Definition);
        } else if (value == 2) {
            return context.getString(R.string.High_Definition);
        } else if (value == 3) {
            return context.getString(R.string.Standard_Definition);
        } else return "";
    }
}
