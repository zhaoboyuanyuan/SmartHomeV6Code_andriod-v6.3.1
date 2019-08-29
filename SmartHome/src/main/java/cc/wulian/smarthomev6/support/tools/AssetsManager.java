package cc.wulian.smarthomev6.support.tools;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.welcome.SplashActivity;
import cc.wulian.smarthomev6.support.utils.FileUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by zbl on 2017/10/11.
 * 把资源从assets复制到外存
 */

public class AssetsManager {

    private static final String TAG = "AssetsManager";
    private static final int SHOW = 0x8000;
    private static final int MAX = 0x8001;
    private static final int INCREMENT = 0x8002;
    private static final int FINISH = 0x8003;
    private static int mCount = 0;

    //需要复制的文件数：
    private static int mSourceCount = 426;
    private static int mSKinSourceCount = 17;
    private static int mDoorLockBc = 77;
    private static int mTotalUpdate;

    public static Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW:
                    Bundle b = msg.getData();
                    String title = b.getString("title");
                    String msg1 = b.getString("msg");
                    SplashActivity.showProgressDialog(title, msg1);
                    break;
                case MAX:
                    SplashActivity.mProgressDialog.setMax(msg.arg1);
                    break;
                case INCREMENT:
                    SplashActivity.mProgressDialog.incrementProgressBy(1);
                    break;
                case FINISH:
                    SplashActivity.mProgressDialog.dismiss();
                    break;
            }
        };
    };

    public interface CopyTaskCallback {
        void onFinish();
    }

    /**
     * fromPath    assets下的路径，例如 "main", "main/about" 这样 <p/>
     * toPath  外存的绝对路径
     * isDeleteTargetFolder 是否需要先删除目标目录
     */
    private static class CopyTaskBean {
        public String fromPath;
        public String toPath;
        public boolean isDeleteTargetFolder = false;
    }

    /**
     * 复制默认html资源
     */
    public static void updateHtmlCommonAssets(final Context context, final boolean isShow, final CopyTaskCallback callback) {
        final String[] folders = new String[]{
                "source",
                "skinSource",
                // 因为 Bd Bf Bg 三把锁的用户管理功能直接引用的Bc的页面,
                // 为避免找不到页面, 故而一开始将Bc的页面导入
                "device/doorLock_Bc"
        };

        //根据info.json获取三个路径下的文件个数
        getCopyAssetsbyInfo(context, "main/source/info.json");

        ArrayList<CopyTaskBean> taskList = new ArrayList<>();
        for (String folder : folders) {
            String v6htmlPath = FileUtil.getHtmlResourcesPath();
            if (isNeedUpdate(context, "main/" + folder, v6htmlPath + "/" + folder)) {
                if ("skinSource".equals(folder) && new File(v6htmlPath + "/" + folder).exists()) {
                    //skinSource目录特殊处理，只要目标目录存在就不覆盖
                    continue;
                }
                CopyTaskBean bean = new CopyTaskBean();
                bean.fromPath = "main/" + folder;
                bean.toPath = v6htmlPath + "/" + folder;
                taskList.add(bean);

                if (TextUtils.equals(folder, "source")){
                    mTotalUpdate += mSourceCount;
                }else if (TextUtils.equals(folder, "skinSource")){
                    mTotalUpdate += mSKinSourceCount;
                }else if (TextUtils.equals(folder, "device/doorLock_Bc")){
                    mTotalUpdate += mDoorLockBc;
                }
            }
        }
        if (taskList.size() > 0) {
            copyAssetsToExternalStorage(context, isShow, taskList, callback);
        } else if (callback != null) {
            callback.onFinish();
        }
    }

    /**
     * 复制默认html资源
     */
    public static void updateHtmlCommonAssets(final Context context, final CopyTaskCallback callback) {
        updateHtmlCommonAssets(context, true, callback);
    }

    /**
     * 复制默认皮肤资源
     */
    public static void updateDefaultSkinAssets(final Context context, final boolean isshowed, final CopyTaskCallback callback) {

        ArrayList<CopyTaskBean> taskList = new ArrayList<>();
        String skinPath = FileUtil.getSkinPath();
        if (!new File(skinPath + "/" + Preference.DEFAULT_SKIN_ID + ".zip").exists()) {
            CopyTaskBean bean = new CopyTaskBean();
            bean.fromPath = "skin";
            bean.toPath = skinPath;
            taskList.add(bean);
        }
        if (taskList.size() > 0) {
            copyAssetsToExternalStorage(context, isshowed, taskList, callback);
        } else {
            callback.onFinish();
        }
    }

    /**
     * 复制默认皮肤资源
     */
    public static void updateDefaultSkinAssets(final Context context, final CopyTaskCallback callback) {
        updateDefaultSkinAssets(context, true, callback);
    }

    /**
     * 把资源从包内的assets复制到外存上
     */
    public static void copyAssetsToExternalStorage(final Context context, String fromPath, String dstPath, final CopyTaskCallback callback) {
        copyAssetsToExternalStorage(context, false, fromPath, dstPath, false, callback);
    }

    /**
     * 把资源从包内的assets复制到外存上
     */
    public static void copyAssetsToExternalStorage(final Context context, boolean isShowed, String fromPath, String dstPath, boolean isDeleteTargetFolder, final CopyTaskCallback callback) {
        final List<CopyTaskBean> taskList = new ArrayList<>();
        CopyTaskBean taskBean = new CopyTaskBean();
        taskBean.fromPath = fromPath;
        taskBean.toPath = dstPath;
        taskBean.isDeleteTargetFolder = isDeleteTargetFolder;
        taskList.add(taskBean);
        copyAssetsToExternalStorage(context, isShowed, taskList, callback);
    }

    /**
     * 把资源从包内的assets复制到外存上
     */
    public static void copyAssetsToExternalStorage(final Context context, boolean isShowed, final List<CopyTaskBean> taskBeenList, final CopyTaskCallback callback) {
        MyTask task1 = new MyTask(context, isShowed, taskBeenList, callback);
        task1.execute("");
    }

    private static class MyTask extends AsyncTask<String, String, Integer> {

        private List<CopyTaskBean> mTaskList;
        private CopyTaskCallback mTaskCallback;
        private AssetManager mAssetManager;
        private boolean misShowed;

        MyTask(Context context, boolean isShowed, List<CopyTaskBean> taskList, CopyTaskCallback taskCallback) {
            mTaskList = taskList;
            mTaskCallback = taskCallback;
            mAssetManager = context.getAssets();
            misShowed = isShowed;
            if (isShowed){
                Message msg1 = new Message();
                msg1.what = SHOW;
                Bundle b = new Bundle();
                b.putString("title", context.getString(R.string.Hint));
                b.putString("msg", context.getString(R.string.Device_Lock_Widget_Statusa));
                msg1.setData(b);
                mHandler.sendMessage(msg1);

                Message msg = new Message();
                msg.what = MAX;
                msg.arg1 = mTotalUpdate;
//            msg.arg1 = getCopyAssets(mAssetManager, mTaskList);
                mHandler.sendMessage(msg);
            }
        }

        @Override
        protected Integer doInBackground(String... strings) {

            for (CopyTaskBean taskBeen : mTaskList) {
                WLog.i(TAG, "doInBackground: " + taskBeen.toPath);
                if (taskBeen.isDeleteTargetFolder) {
                    FileUtil.delAllFile(taskBeen.toPath);
                }
                copyAssetsToExternalStorageRecursive(mAssetManager, misShowed, taskBeen.fromPath, taskBeen.toPath);
            }
            if (misShowed){
                mHandler.sendEmptyMessage(FINISH);
            }
            return 0;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            for (CopyTaskBean taskBeen : mTaskList) {
                WLog.i(TAG, "onPostExecute: " + taskBeen.toPath);
                FileUtil.createNewFileWithoutNewLine(
                        taskBeen.toPath + "/COPY_DONE_FLAG",
                        "" + AssetsManager.getInfoVersion(MainApplication.getApplication().getApplicationContext(), taskBeen.toPath));
            }

            if (mTaskCallback != null) {
                mTaskCallback.onFinish();
            }
        }
    }

    /**
     * 获取复制assets文件数
     *
     * @param assetManager
     */
    private static int getCopyAssets(AssetManager assetManager, List<CopyTaskBean> taskList) {
        try {
            mCount = 0;
            for (CopyTaskBean taskBeen : taskList) {
                getCopyAssets(assetManager, taskBeen.fromPath);
            }
            return mCount;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    private static void getCopyAssets(AssetManager assetManager, String assetsPath) {
        try {
            String[] files = assetManager.list(assetsPath);
            if (files.length == 0) {
                mCount++;
            } else {
                for (String file : files) {
                    getCopyAssets(assetManager,  assetsPath + "/" + file);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void getCopyAssetsbyInfo(Context context, String assetsJsonPath){
        String assetsInfoString = readFileToString(context, assetsJsonPath);
        if (assetsInfoString != null) {
            com.alibaba.fastjson.JSONObject assetsJsonObject = JSON.parseObject(assetsInfoString);
            try{
                Integer source = assetsJsonObject.getInteger("source");
                if (source != null) {
                    mSourceCount = source;
                    WLog.i("mSourceCount:"+mSourceCount);
                }

                Integer skinSource = assetsJsonObject.getInteger("skinSource");
                if (skinSource != null) {
                    mSKinSourceCount = skinSource;
                    WLog.i("mSKinSourceCount:"+mSKinSourceCount);
                }

                Integer doorLock = assetsJsonObject.getInteger("doorLock");
                if (doorLock != null) {
                    mDoorLockBc = doorLock;
                    WLog.i("mDoorLockBc:"+mDoorLockBc);
                }
            }catch (Exception e){
                mSourceCount = 352;
                mSKinSourceCount = 17;
                mDoorLockBc = 76;
            }
        }
    }

    /**
     * 复制assets文件到外存
     *
     * @param assetManager
     * @param assetsPath   assets下的路径，例如 "main", "main/about" 这样 <p/>
     * @param dstPath      外存的绝对路径
     */
    private static void copyAssetsToExternalStorageRecursive(AssetManager assetManager, boolean isShowed, String assetsPath, String dstPath) {
        try {
            String[] files = assetManager.list(assetsPath);
            if (files.length == 0) {
                WLog.i(TAG, "复制文件：" + assetsPath);
                if (isShowed){
                    mHandler.sendEmptyMessage(INCREMENT);
                }
                InputStream inputStream = assetManager.open(assetsPath);
                File dstFile = new File(dstPath);
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(dstFile));
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();

            } else {
                File dstFolder = new File(dstPath);
                if (!dstFolder.exists()) {
                    dstFolder.mkdirs();
                }
                WLog.i(TAG, "复制文件夹：" + assetsPath);
                for (String file : files) {
                    copyAssetsToExternalStorageRecursive(assetManager, isShowed, assetsPath + "/" + file, dstPath + "/" + file);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @SEE {{@link #readFileToString(Context, String, String)}}
     */
    public static String readFileToString(Context context, String filePath) {
        return readFileToString(context, filePath, "utf-8");
    }

    /**
     * 从assets中读文件并转换为String
     * 因是一次性读取，建议只用作读取小文件，否则有性能问题
     *
     * @param assetsPath
     * @param charset
     * @return
     */
    public static String readFileToString(Context context, String assetsPath, String charset) {
        final AssetManager assetManager = context.getAssets();
        try {
            AssetFileDescriptor assetFileDescriptor = assetManager.openFd(assetsPath);
            InputStream inputStream = assetManager.open(assetsPath);
            int size = (int) assetFileDescriptor.getLength();
            byte[] data = new byte[size];
            inputStream.read(data, 0, size);
            String string = new String(data, charset);
            return string;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断目标文件夹是否需要更新
     * 先判断目录有无，然后判断info.json有无，然后判断info.json中的版本号是否一致
     *
     * @param context
     * @param assetsPath 例如main/editScene
     * @param dstPath    例如/sdcard/wulian/h5/v6html/editScene
     * @return
     */
    public static boolean isNeedUpdate(Context context, String assetsPath, String dstPath) {
        // 文件拷贝完成标志
        String flagFileName = "COPY_DONE_FLAG";
        String fileName = dstPath + "/" + flagFileName;
        int flagVersion = -1;
        if (new File(fileName).exists()) {
            String flag = FileUtil.readFileToString(fileName);
            try {
                flagVersion = Integer.parseInt(flag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // 没有完成标志, 需要更新文件
            return true;
        }

        String infoFileName = "info.json";
        String infoJsonPath = dstPath + "/" + infoFileName;
        String assetsJsonPath = assetsPath + "/" + infoFileName;
        if (new File(infoJsonPath).exists()) {
            //检查资源版本
            String dstInfoString = FileUtil.readFileToString(infoJsonPath);
            String assetsInfoString = readFileToString(context, assetsJsonPath);
            if (dstInfoString != null && assetsInfoString != null) {
                com.alibaba.fastjson.JSONObject dstJsonObject = JSON.parseObject(dstInfoString);
                com.alibaba.fastjson.JSONObject assetsJsonObject = JSON.parseObject(assetsInfoString);
                Integer dstVersion = dstJsonObject.getInteger("version");
                Integer assetsVersion = assetsJsonObject.getInteger("version");
                if (dstVersion != null && assetsVersion != null) {
                    if (dstVersion >= assetsVersion && dstVersion == flagVersion) {
//                    if (dstVersion >= assetsVersion) {
                        //资源已经存在,且版本高于或者等于assets中资源的版本
                        // 并且, dst版本 与 完成标志的版本一致
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 获取本地 info.json 的版本
     */
    public static int getInfoVersion(Context context, String dstPath) {
        String infoFileName = "info.json";
        String infoJsonPath = dstPath + "/" + infoFileName;
        if (new File(infoJsonPath).exists()) {
            //检查资源版本
            String dstInfoString = FileUtil.readFileToString(infoJsonPath);
            if (dstInfoString != null) {
                com.alibaba.fastjson.JSONObject dstJsonObject = JSON.parseObject(dstInfoString);
                return dstJsonObject.getInteger("version");
            }
        }
        return -1;
    }

}
