package cc.wulian.smarthomev6.main.mine.setting;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.SkinBean;
import cc.wulian.smarthomev6.entity.SkinListBean;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.support.core.apiunit.SkinApiUnit;
import cc.wulian.smarthomev6.support.customview.GalleryViewPager;
import cc.wulian.smarthomev6.support.event.SkinChangedEvent;
import cc.wulian.smarthomev6.support.tools.ImageLoaderTool;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by syf on 2017/2/15.
 */
public class SkinActivity extends BaseTitleActivity {

    private static final String GET_DATA = "get_data";
    private static final String UNZIP_DATA = "unzip_data";

    private GalleryViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private TextView btn_skin;
    private ImageView iv_selected;
    private LayoutInflater inflater;
    private SkinApiUnit skinApiUnit;
    private ImageLoader imageLoader;

    private HashMap<Integer, View> viewMap = new HashMap<>();

    private List<SkinBean> skinList = new ArrayList<>();

    private long currentTime = System.currentTimeMillis() / 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preference.getPreferences().setIsClickSkin(false);
        setContentView(R.layout.activity_skin, true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.theme_tips1));
    }

    @Override
    protected void initView() {
        inflater = LayoutInflater.from(this);
        imageLoader = ImageLoader.getInstance();
        viewPager = (GalleryViewPager) findViewById(R.id.gallery_view_pager);
        pagerAdapter = new PagerAdapter() {
            @Override
            public float getPageWidth(int position) {
                return 0.65f;//建议值为0.6~1.0之间
            }

            @Override
            public int getCount() {
                return skinList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = inflater.inflate(R.layout.item_skin_viewpager, container, false);
                ImageView iv_preview = (ImageView) view.findViewById(R.id.iv_preview);
                TextView tv_name = (TextView) view.findViewById(R.id.tv_name);

                SkinBean skinBean = skinList.get(position);
                imageLoader.displayImage(skinBean.imageUrl, iv_preview, ImageLoaderTool.getAdOptions());
                tv_name.setText(skinBean.title);
                viewMap.put(position, view);
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(viewMap.get(position));
            }
        };
        viewPager.setNarrowFactor(0.8f);
        viewPager.addOnPageChangeListener(new GalleryViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                SkinBean skinBean = skinList.get(position);
                setButtonStatus(skinBean.status);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        btn_skin = (TextView) findViewById(R.id.btn_skin);
        iv_selected = (ImageView) findViewById(R.id.iv_selected);
    }

    @Override
    protected void updateSkin() {
//        super.updateSkin();
        setTitleTextBackground(R.drawable.icon_back_black, getResources().getColor(R.color.black));
        getmToolBarHelper().getToolBar().setBackgroundColor(getResources().getColor(R.color.v6_bg));
    }

    @Override
    protected void initData() {
        if (skinApiUnit == null) {
            skinApiUnit = new SkinApiUnit(this);
        }
        ProgressDialogManager.getDialogManager().showDialog(GET_DATA, this, null, null, getResources().getInteger(R.integer.http_timeout));
        skinApiUnit.getSkinList(new SkinApiUnit.CommonListener<SkinListBean>() {
            @Override
            public void onSuccess(SkinListBean bean) {
                skinList.clear();
                if (bean.appThemeVOs != null && bean.appThemeVOs.size() > 0) {
                    int jumpIndex = 0;
                    int i = 0;
                    for (SkinBean skinBean : bean.appThemeVOs) {
                        setSkinBeanStatus(skinBean);
                        if (skinBean.status == 2) {//获取当前皮肤位置，直接跳到该页
                            jumpIndex = i;
                        }
                        if (skinBean.endTime / 1000 == 0) {//一直可用的皮肤
                            skinList.add(skinBean);
                            i += 1;
                        } else if (skinBean.startTime / 1000 < currentTime && skinBean.endTime / 1000 > currentTime) {//节假日皮肤
                            skinList.add(skinBean);
                            i += 1;
                        }

                    }
                    viewPager.setAdapter(pagerAdapter);
                    pagerAdapter.notifyDataSetChanged();
                    viewPager.setCurrentItem(jumpIndex);
                    setButtonStatus(skinList.get(jumpIndex).status);
                }
                ProgressDialogManager.getDialogManager().dimissDialog(GET_DATA, 0);
            }

            @Override
            public void onFail(int code, String msg) {
                ProgressDialogManager.getDialogManager().dimissDialog(GET_DATA, 0);
                ToastUtil.show(msg);
            }
        });
    }

    @Override
    protected void initListeners() {
        btn_skin.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == btn_skin) {
            if (skinList.size() > 0) {
                SkinBean skinBean = skinList.get(viewPager.getCurrentItem());
                if (skinBean.status == 1) {
                    unzipSkinPackage(skinBean);
                } else if (skinBean.status == 0) {
                    downloadSkinPackage(skinBean);
                }
            }
        }
    }

    private void setSkinBeanStatus(SkinBean skinBean) {
        String currentSkinId = Preference.getPreferences().getCurrentSkin();
        if (TextUtils.equals(currentSkinId, skinBean.themeId)) {
            skinBean.status = 2;
        } else if (SkinManager.isSkinPackageExists(skinBean.themeId)) {
            skinBean.status = 1;
        } else {
            skinBean.status = 0;
        }
    }

    /**
     * 设置底部按钮状态
     * 0 未下载，1 已下载未使用，2 当前使用,3 正在下载中
     */
    private void setButtonStatus(int status) {
        if (status == 2) {
            btn_skin.setVisibility(View.GONE);
            iv_selected.setVisibility(View.VISIBLE);
        } else if (status == 1) {
            btn_skin.setVisibility(View.VISIBLE);
            btn_skin.setText(R.string.theme_tips6);
            iv_selected.setVisibility(View.GONE);
        } else if (status == 3) {
            btn_skin.setVisibility(View.VISIBLE);
            btn_skin.setText(R.string.Updatereminder_Download);
            iv_selected.setVisibility(View.GONE);
        } else {
            btn_skin.setVisibility(View.VISIBLE);
            btn_skin.setText(R.string.theme_tips7);
            iv_selected.setVisibility(View.GONE);
        }
    }

    private void downloadSkinPackage(final SkinBean skinBean) {
        skinBean.status = 3;
        setButtonStatus(skinBean.status);
        skinApiUnit.downloadSkin(skinBean.themeId, skinBean.themeUrl, new SkinApiUnit.CommonListener<File>() {
            @Override
            public void onSuccess(File bean) {
                skinBean.status = 1;
                int position = skinList.indexOf(skinBean);
                if (position == viewPager.getCurrentItem()) {
                    setButtonStatus(skinBean.status);
                }
                ToastUtil.show(R.string.theme_tips8);
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(R.string.Infraredrelay_Downloadfailed_Title);
            }
        });
    }

    private void unzipSkinPackage(final SkinBean skinBean) {
        ProgressDialogManager.getDialogManager().showDialog(UNZIP_DATA, this, null, null, Integer.MAX_VALUE);
        SkinManager.unzipSkinPackage(skinBean.themeId, new SkinManager.ZipCallback() {
            @Override
            public void onFinish() {
                ProgressDialogManager.getDialogManager().dimissDialog(UNZIP_DATA, 0);
                for (SkinBean bean : skinList) {
                    if (bean.status == 2) {
                        if (SkinManager.isSkinPackageExists(bean.themeId)) {
                            bean.status = 1;
                        } else {
                            bean.status = 0;
                        }
                    }
                }
                skinBean.status = 2;
                int position = skinList.indexOf(skinBean);
                if (position == viewPager.getCurrentItem()) {
                    setButtonStatus(skinBean.status);
                }
                Preference.getPreferences().saveCurrentSkin(skinBean.themeId);
                if (hasHolidaySkin(skinList) && skinBean.endTime / 1000 == 0) {
                    preference.saveCommonSkin(skinBean.themeId);
                    WLog.i("SkinLog", "有节假日皮肤时换常规皮肤"+skinBean.themeId);
                    preference.setAutoChangeSkin(false);
                }else if(skinBean.endTime / 1000 == 0){
                    preference.saveCommonSkin(skinBean.themeId);
                    WLog.i("SkinLog", "没有节假日皮肤时换常规皮肤"+skinBean.themeId);
                }
                ToastUtil.show(R.string.theme_tips9);
                EventBus.getDefault().post(new SkinChangedEvent());
            }

            @Override
            public void onFail() {
                ProgressDialogManager.getDialogManager().dimissDialog(UNZIP_DATA, 0);
                ToastUtil.show(R.string.Setting_Fail);
            }
        });
    }


    /**
     * 判断是否有节假日皮肤
     *
     * @param list
     * @return
     */
    private boolean hasHolidaySkin(List<SkinBean> list) {
        boolean hasHolidaySkin = false;
        for (SkinBean skinBean : list) {
            if (skinBean.startTime / 1000 < currentTime && skinBean.endTime / 1000 > currentTime) {
                hasHolidaySkin = true;
            }
        }
        return hasHolidaySkin;
    }
}
