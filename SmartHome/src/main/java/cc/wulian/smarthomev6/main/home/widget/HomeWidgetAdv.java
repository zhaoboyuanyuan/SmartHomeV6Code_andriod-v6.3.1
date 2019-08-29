package cc.wulian.smarthomev6.main.home.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.AdvertisementEntity;
import cc.wulian.smarthomev6.entity.BannerItemBean;
import cc.wulian.smarthomev6.entity.BannerListBean;
import cc.wulian.smarthomev6.main.h5.CommonH5Activity;
import cc.wulian.smarthomev6.support.core.apiunit.AdApiUnit;
import cc.wulian.smarthomev6.support.customview.figure.ImageCycleView;
import cc.wulian.smarthomev6.support.tools.ImageLoaderTool;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;
import cc.wulian.smarthomev6.support.utils.NetworkUtil;

/**
 * 作者: chao
 * 时间: 2017/5/8
 * 描述:  首页广告
 * 联系方式: 805901025@qq.com
 */

public class HomeWidgetAdv extends RelativeLayout {
    private ImageCycleView mAdView;
    private List<AdvertisementEntity> advertisementEntites = new ArrayList<>();
    private AdApiUnit adApiUnit;

    public HomeWidgetAdv(Context context) {
        super(context);
        adApiUnit = new AdApiUnit(context);
        initView(context);
    }

    private void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.fragment_home_weather, null);
        addView(rootView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mAdView = (ImageCycleView) findViewById(R.id.vp_home);

        loadData();
    }

    public void loadData() {
        //广告图片加载
        adApiUnit.getBannerList(new AdApiUnit.CommonListener<BannerListBean>() {
            @Override
            public void onSuccess(BannerListBean bannerListBean) {
                advertisementEntites.clear();
                for (BannerItemBean bean : bannerListBean.slideshowVOs) {
                    AdvertisementEntity entity = new AdvertisementEntity();
                    entity.setUrl(bean.imageUrl);
                    entity.setContent(bean.url);
                    advertisementEntites.add(entity);
                }
                mAdView.setImageResources((ArrayList<AdvertisementEntity>) advertisementEntites, mAdCycleViewListener);
            }

            @Override
            public void onFail(int code, String msg) {
                loadDefaultData();
            }
        });
    }

    //默认图片加载
    private void loadDefaultData() {
        String[] imageUrls;
        if (LanguageUtil.isChina()) {
            imageUrls = new String[]{
                    "assets://ad_image/adv_img_6_1_2_01.png",
                    "assets://ad_image/adv_img1_6_0_3.png",
                    "assets://ad_image/adv_img2.png",
                    "assets://ad_image/adv_img3.png",
                    "assets://ad_image/adv_img4.png"
            };
        } else {
            imageUrls = new String[]{
                    "assets://ad_image/adv_img_6_1_2_01.png",
                    "assets://ad_image/adv_img1_en_6_0_3.png",
                    "assets://ad_image/adv_img2_en.png",
                    "assets://ad_image/adv_img3_en.png",
                    "assets://ad_image/adv_img4_en.png"
            };
        }

        advertisementEntites.clear();
        for (int i = 0; i < imageUrls.length; i++) {
            AdvertisementEntity entity = new AdvertisementEntity();
            entity.setUrl(imageUrls[i]);
            entity.setContent(null);
            advertisementEntites.add(entity);
        }
        mAdView.setImageResources((ArrayList<AdvertisementEntity>) advertisementEntites, mAdCycleViewListener);
    }

    //广告轮播监听
    private ImageCycleView.ImageCycleViewListener mAdCycleViewListener = new ImageCycleView.ImageCycleViewListener() {

        @Override
        public void onImageClick(AdvertisementEntity info, int position, View imageView) {
            String url = info.getContent();
            if (!TextUtils.isEmpty(url) && url.startsWith("http")) {
                CommonH5Activity.start(getContext(), url,"");
            }
        }

        @Override
        public void displayImage(String imageURL, ImageView imageView) {
            ImageLoader.getInstance().displayImage(imageURL, imageView, ImageLoaderTool.getAdOptions());// 使用ImageLoader对图片进行加装！
        }
    };
}
