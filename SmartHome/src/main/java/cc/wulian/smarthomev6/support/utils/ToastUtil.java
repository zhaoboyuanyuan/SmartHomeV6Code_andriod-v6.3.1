package cc.wulian.smarthomev6.support.utils;

import android.support.annotation.StringRes;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;

/**
 * Created by zbl on 2017/6/15.
 * 统一弹Toast
 */

public class ToastUtil {
    private static Toast toast;

    public static void show(String text) {
        if (toast == null) {
            toast = Toast.makeText(MainApplication.getApplication(), null, Toast.LENGTH_SHORT);
            toast.setText(text);
        } else {
            toast.setText(text);
        }
        toast.show();
    }

    public static void show(@StringRes int resId) {
        if (toast == null) {
            toast = Toast.makeText(MainApplication.getApplication(), null, Toast.LENGTH_SHORT);
            toast.setText(resId);
        } else {
            toast.setText(resId);
        }
        toast.show();
    }

    /**
     * 弹出多个toast时, 不会一个一个的弹, 后面一个要显示的内容直接显示在当前的toast上
     */
    public static void single(String msg) {
        if (toast == null) {
            toast = Toast.makeText(MainApplication.getApplication(), null, Toast.LENGTH_SHORT);
            toast.setText(msg);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    public static void singleLong(String msg) {
        if (toast == null) {
            toast = Toast.makeText(MainApplication.getApplication(), null, Toast.LENGTH_LONG);
            toast.setText(msg);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    /**
     * 多行居中显示
     */
    public static void singleCenter(@StringRes int msg) {
        if (toast == null) {
            toast = Toast.makeText(MainApplication.getApplication(), null, Toast.LENGTH_SHORT);
            toast.setText(msg);
        } else {
            toast.setText(msg);
        }
        ((TextView) toast.getView().findViewById(android.R.id.message)).setGravity(Gravity.CENTER);
        toast.show();
    }

    /**
     * 多行居中显示
     */
    public static void singleCenter(String msg) {
        if (toast == null) {
            toast = Toast.makeText(MainApplication.getApplication(), null, Toast.LENGTH_SHORT);
            toast.setText(msg);
        } else {
            toast.setText(msg);
        }
        ((TextView) toast.getView().findViewById(android.R.id.message)).setGravity(Gravity.CENTER);
        toast.show();
    }

    /**
     * 弹出多个toast时, 不会一个一个的弹, 后面一个要显示的内容直接显示在当前的toast上
     */
    public static void single(@StringRes int msg) {
        if (toast == null) {
            toast = Toast.makeText(MainApplication.getApplication(), null, Toast.LENGTH_SHORT);
            toast.setText(msg);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }
}
