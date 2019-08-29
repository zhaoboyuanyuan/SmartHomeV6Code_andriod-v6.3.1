package cc.wulian.smarthomev6.support.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import java.io.File;
import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.home.HomeActivity;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * 作者：luzx on 2017/6/13 18:51
 * 邮箱：zhengxiang.lu@wuliangroup.com
 */
public class SystemDownloadManager {

    private Context mContext;

    private WLDialog dialog;

    private String installFilePath;

    public SystemDownloadManager(Context context) {
        super();
        mContext = context;
    }

    public void downloadApk(String url, String destUrl){
        android.app.DownloadManager downloadManager = (android.app.DownloadManager) mContext
                .getSystemService(Context.DOWNLOAD_SERVICE);
        android.app.DownloadManager.Query query = new android.app.DownloadManager.Query();
        long downloadId = Preference.getPreferences().getDownloadId("downloadId");
        query.setFilterById(downloadId);
        Cursor myDownload = downloadManager.query(query);
        int status = 0;
        if (myDownload.moveToFirst()) {
            int statusIdx = myDownload.getColumnIndex(android.app.DownloadManager.COLUMN_STATUS);
            status = myDownload.getInt(statusIdx);
        }
        myDownload.close();
        if(status == android.app.DownloadManager.STATUS_RUNNING || status == android.app.DownloadManager.STATUS_PENDING){
            ToastUtil.show(R.string.Updatereminder_Download);
            return;
        }else{
            File apkFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + destUrl);
            if(apkFile.exists()){
                installFilePath = apkFile.getAbsolutePath();
                installAPK(installFilePath);
                return;
            }
        }
        android.app.DownloadManager.Request request = new android.app.DownloadManager.Request(Uri.parse(url));
        //设置状态栏中显示Notification
        request.setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setTitle(mContext.getResources().getString(R.string.app_name));
        //设置可用的网络类型
        request.setAllowedNetworkTypes(android.app.DownloadManager.Request.NETWORK_MOBILE | android.app.DownloadManager.Request.NETWORK_WIFI);
        //不显示下载界面
        request.setVisibleInDownloadsUi(false);

        //创建文件的下载路径
        File folder = new File(Environment.DIRECTORY_DOWNLOADS, destUrl);
        if (!folder.getParentFile().exists()) {
            folder.mkdirs();
        }
        //指定下载的路径为和上面创建的路径相同
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS , destUrl);

        //设置文件类型
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
        request.setMimeType(mimeString);
        //将请求加入请求队列会 downLoadManager会自动调用对应的服务执行者个请求
        downloadId = downloadManager.enqueue(request);
        Preference.getPreferences().saveDownloadId("downloadId", downloadId);
    }

    //安装APK
    public void installAPK(String filePath) {
        if(!TextUtils.isEmpty(filePath)){
            installFilePath = filePath;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!mContext.getPackageManager().canRequestPackageInstalls()) {
                WLDialog.Builder builder = new WLDialog.Builder(mContext);
                builder.setCancelOnTouchOutSide(false);
                builder.setDismissAfterDone(false);
                builder.setMessage(mContext.getResources().getString(R.string.Open_permission));
                builder.setPositiveButton(mContext.getResources().getString(R.string.Sure));
                builder.setNegativeButton(mContext.getResources().getString(R.string.Cancel));
                builder.setListener(new WLDialog.MessageListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClickPositive(View view, String msg) {
                        Uri packageURI = Uri.parse("package:" + mContext.getPackageName());
                        //注意这个是8.0新API
                        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
                        ((Activity)mContext).startActivityForResult(intent, HomeActivity.INSTALL_PACKAGES_REQUESTCODE);
                        dialog.dismiss();
                    }

                    @Override
                    public void onClickNegative(View view) {
                        dialog.dismiss();
                    }
                });
                dialog = builder.create();
                dialog.show();
            }else{
                doInstall();
            }
        }else{
            doInstall();
        }
    }

    private void doInstall(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri imageUri = FileProvider.getUriForFile(mContext, "cc.wulian.smarthomev6.fileprovider", new File(installFilePath));
            intent.setDataAndType(imageUri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }else{
            intent.setDataAndType(Uri.fromFile(new File(installFilePath)), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        mContext.startActivity(intent);
    }
}
