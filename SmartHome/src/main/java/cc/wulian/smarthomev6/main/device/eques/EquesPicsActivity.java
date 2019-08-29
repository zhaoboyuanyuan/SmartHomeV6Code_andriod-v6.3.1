package cc.wulian.smarthomev6.main.device.eques;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.cateye.album.adapter.PagerViewAdapter;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesAlarmDetailBean;
import cc.wulian.smarthomev6.support.core.apiunit.EquesApiUnit;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.customview.CustomViewPager;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.utils.AlbumUtils;
import cc.wulian.smarthomev6.support.utils.FileUtil;
import cc.wulian.smarthomev6.support.utils.ImageLoader;

/**
 * 作者: chao
 * 时间: 2017/6/14
 * 描述:
 * 联系方式: 805901025@qq.com
 */

public class EquesPicsActivity extends BaseTitleActivity implements View.OnClickListener{
    private CustomViewPager viewPager;
    private PagerViewAdapter albumAdapter;
    private TextView album_count;
    private int currentPostion;// 当前显示的图片位置
    private ImageLoader mLoader;
    private List<String> allJpgFilePathList; // 当前文件夹下所有图片路径
    private AlbumUtils mAlbumUtil;
    private String deviceid;
    private Device device;
    private EquesAlarmDetailBean bean;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:// 加载结束
                    updatePositionAndTitle(0);
                    break;
                case 1:// 删除照片
                    int position = (int) msg.obj;// 刚刚删除的位置
                    int newSize = allJpgFilePathList.size();
                    if (newSize > 0) {
                        if (position == newSize)// 刚删除的是尾部照片
                            updatePositionAndTitle(position - 1);// 最后下标相应少一个
                        else
                            updatePositionAndTitle(position);
                    } else {
                        EquesPicsActivity.this.finish();
                    }
                    ProgressDialogManager.getDialogManager().dimissDialog("DELETE_PHOTO", 0);
                    break;
                default:
                    break;
            }
            initAdapter();
        }
    };


    public static void start(Context context, String deviceid, EquesAlarmDetailBean bean) {
        Intent intent = new Intent(context, EquesPicsActivity.class);
        intent.putExtra("deviceid", deviceid);
        intent.putExtra("equesAlarmDetailBean", bean);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_eques_pics, true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        deviceid = getIntent().getStringExtra("deviceid");
        device = MainApplication.getApplication().getDeviceCache().get(deviceid);
        setToolBarTitle(DeviceInfoDictionary.getNameByDevice(device));
    }

    private void initAdapter() {
        if (allJpgFilePathList != null && allJpgFilePathList.size() > 0) {
            if (albumAdapter == null) {
                albumAdapter = new PagerViewAdapter(this, device.type, mLoader, viewPager);
                albumAdapter.setSourceData(allJpgFilePathList);
                viewPager.setAdapter(albumAdapter);
                viewPager.setCurrentItem(0, true);
            } else {
                albumAdapter = (PagerViewAdapter) viewPager.getAdapter();
                albumAdapter.setSourceData(allJpgFilePathList);
                albumAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void initData() {
        super.initData();
        viewPager.setPageMargin(this.getResources().getDimensionPixelSize(R.dimen.default_edittext_height));
        mAlbumUtil = new AlbumUtils(this);
        bean = (EquesAlarmDetailBean) getIntent().getSerializableExtra("equesAlarmDetailBean");
        mLoader = new ImageLoader(this);
        downLoadZip();
    }

    @Override
    protected void initView() {
        super.initView();
        viewPager = (CustomViewPager) this.findViewById(R.id.album_pic);
        album_count = (TextView) this.findViewById(R.id.album_count);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.titlebar_back:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * @MethodName: getAllDir
     * @Function: 初始化该文件夹下所有图片路径
     * @author: yuanjs
     * @date: 2015年4月1日
     * @email: yuanjsh@wuliangroup.cn
     */
    private void getAllDir(final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (allJpgFilePathList != null) {
                    allJpgFilePathList.clear();
                }
                allJpgFilePathList = mAlbumUtil.loadJpgs(path);
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLoader.cancelTask();
        mLoader.deletAllBitmapFromMemCache();
    }

    public void updatePositionAndTitle(int positon) {
        int size = allJpgFilePathList.size();
        long time = new File(allJpgFilePathList.get(positon)).lastModified();
        String timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH).format(new Date(time));
        album_count.setText(positon + 1 + "/" + size);
        currentPostion = positon;
    }

    private void downLoadZip(){
        final String path = FileUtil.getEquesAlarmPath()+ "/"+ deviceid + "/" +  bean.time;
        File file = new File(path);
        if (file.exists()){
            getAllDir(FileUtil.getEquesAlarmPath()+ "/"+ deviceid + "/" +  bean.time);
            return;
        }

        MainApplication.getApplication().getEquesApiUnit().loadAlarmFile(bean.fid.get(0), deviceid, bean.type, bean.time, new EquesApiUnit.EquesFileDownloadListener() {
            @Override
            public void onSuccess(File file) {
                try {
                    FileUtil.unZipFile(file, path);
                    getAllDir(path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }
}
