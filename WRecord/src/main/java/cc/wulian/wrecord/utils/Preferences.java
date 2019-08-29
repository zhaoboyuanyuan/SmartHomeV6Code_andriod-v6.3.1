package cc.wulian.wrecord.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.ArrayMap;

import java.util.Map;

import cc.wulian.wrecord.C;
import cc.wulian.wrecord.WRecord;

/**
 * Created by Veev on 2017/8/3
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    Preferences
 */

public class Preferences {

    private static final String TAG = "Preferences";

    private static Map<String, Preferences> sPrefMap = new ArrayMap<>();

    public static Preferences get() {
        return get(C.pref.name_record);
    }

    public static Preferences get(String name) {
        Preferences p = sPrefMap.get(name);
        if (p == null) {
            synchronized (Preferences.class) {
                if (p == null) {
                    p = new Preferences(name);
                    sPrefMap.put(name, p);
                }
            }
        }

        return p;
    }

    private Preferences(String name) {
        mContext = WRecord.getContext();
        mPreferences = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    private Context mContext;
    private SharedPreferences mPreferences;

    public void clear() {
        mPreferences.edit().clear().apply();
    }

    public Map<String, ?> getAll() {
        return mPreferences.getAll();
    }

    public void remove(String key) {
        mPreferences.edit().remove(key).apply();
    }

    public void put(String key, String value) {
        mPreferences.edit().putString(key, value).apply();
    }

    public void put(String key, long value) {
        mPreferences.edit().putLong(key, value).apply();
    }

    public void put(String key, int value) {
        mPreferences.edit().putInt(key, value).apply();
    }

    public void put(String key, boolean value) {
        mPreferences.edit().putBoolean(key, value).apply();
    }

    public long getLong(String key) {
        return getLong(key, -1L);
    }

    public long getLong(String key, long defValue) {
        return mPreferences.getLong(key, defValue);
    }

    public int getInt(String key) {
        return getInt(key, -1);
    }

    public int getInt(String key, int defValue) {
        return mPreferences.getInt(key, defValue);
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return mPreferences.getBoolean(key, defValue);
    }

    public String getString(String key) {
        return getString(key, "");
    }

    public String getString(String key, String defValue) {
        return mPreferences.getString(key, defValue);
    }

    public float getFloat(String key) {
        return getFloat(key, -1F);
    }

    public float getFloat(String key, float defValue) {
        return mPreferences.getFloat(key, defValue);
    }
}
