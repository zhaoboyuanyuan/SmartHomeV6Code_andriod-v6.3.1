package cc.wulian.smarthomev6.main.h5;

import android.webkit.JavascriptInterface;

import org.json.JSONException;
import org.json.JSONObject;

import cc.wulian.smarthomev6.entity.H5Storage;
import cc.wulian.smarthomev6.entity.H5StorageDao;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by Veev on 2017/7/26
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    NativeStorage 前端的localStorage封装
 */

public class NativeStorage {
    private static final String TAG = "NativeStorage";

    private static final String key_setItem = "setItem:";
    private static final String key_getItem = "getItem:";
    private static final String key_removeItem = "removeItem:";
    private static final String key_clear = "clear:";
    private static final String key_getLang = "getLang:";

    @JavascriptInterface
    public String sysfun(String json) {
        WLog.i(TAG, "sysfun: " + json);
        try {
            JSONObject object = new JSONObject(json);
            String sysFunc = object.optString("sysFunc");
            String room = object.optString("room");
            String id = object.optString("id");
            String data = object.optString("data");
            switch (sysFunc) {
                case key_setItem:
                    return setItem(room, id, data);
                case key_getItem:
                    String dd = getItem(room, id);
                    WLog.i(TAG, "getItem: result " + dd);
                    return getItem(room, id);
                case key_removeItem:
                    return removeItem(room, id);
                case key_clear:
                    return clear(room);
                case key_getLang:
                    return LanguageUtil.getWulianCloudLanguage();
                default:
                    return "sysFunc not found";
            }
        } catch (JSONException e) {
            return e.getMessage();
        }
    }
    public synchronized String getItem(String room, String id) {
            /*WLog.i(TAG, "getItem: id " + id);
            WLog.i(TAG, "getItem: query " + MainApplication.
                    getApplication().
                    getDaoSession().
                    getH5StorageDao().
                    queryBuilder().where(H5StorageDao.Properties.Room.eq(room), H5StorageDao.Properties.Key.eq(id))
                    .list());
            WLog.i(TAG, "getItem: list " + MainApplication.getApplication().getDaoSession().getH5StorageDao().queryBuilder().list());*/
        H5Storage storage = MainApplication.getApplication().getDaoSession().getH5StorageDao()
                .queryBuilder()
                .where(H5StorageDao.Properties.Room.eq(room), H5StorageDao.Properties.Key.eq(id))
                .unique();
        if (storage != null) {
            return storage.getValue();
        } else {
            return null;
        }
    }
    public synchronized String setItem(String room, String id, String data) {
        H5Storage storage = MainApplication.getApplication().getDaoSession().getH5StorageDao()
                .queryBuilder()
                .where(H5StorageDao.Properties.Room.eq(room), H5StorageDao.Properties.Key.eq(id))
                .unique();

        if (storage == null) {
            storage = new H5Storage(room, id, data);
            MainApplication.getApplication().getDaoSession().getH5StorageDao()
                    .save(storage);
        } else {
            storage.setValue(data);
            MainApplication.getApplication().getDaoSession().getH5StorageDao()
                    .update(storage);
        }

        return "YES";
    }

    private synchronized String removeItem(String room, String id) {
        H5Storage storage = MainApplication.getApplication().getDaoSession().getH5StorageDao()
                .queryBuilder()
                .where(H5StorageDao.Properties.Room.eq(room), H5StorageDao.Properties.Key.eq(id))
                .unique();
        if (storage != null) {
            MainApplication.getApplication().getDaoSession().getH5StorageDao().deleteByKey(storage.getId());
        }
        return "YES";
    }

    private synchronized String clear(String room) {
        H5StorageDao dao = MainApplication.getApplication().getDaoSession().getH5StorageDao();
        dao.deleteInTx(dao.queryBuilder().where(H5StorageDao.Properties.Room.eq(room)).list());
        return "YES";
    }
}
