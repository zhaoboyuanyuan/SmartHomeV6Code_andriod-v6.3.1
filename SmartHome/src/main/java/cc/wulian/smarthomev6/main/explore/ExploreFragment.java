package cc.wulian.smarthomev6.main.explore;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cc.wulian.smarthomev6.BuildConfig;
import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.main.h5.CommonH5Activity;
import cc.wulian.smarthomev6.main.login.SigninActivity;
import cc.wulian.smarthomev6.main.ztest.TestActivity;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.bean.UserBean;
import cc.wulian.smarthomev6.support.event.AccountEvent;
import cc.wulian.smarthomev6.support.event.SkinChangedEvent;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;


/**
 * Created by Administrator on 2017/1/15
 */

public class ExploreFragment extends WLFragment implements View.OnClickListener {
    private RelativeLayout itemExploreStore;
    private RelativeLayout itemExploreScan;
    private RelativeLayout itemExploreMessage;
    private RelativeLayout item_smartlife;
    private RelativeLayout itemTest;
    private View item_setup_service;
    private View item_tasks;

    private boolean isChina;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        isChina = LanguageUtil.isChina();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public int layoutResID() {
        return R.layout.fragment_explore;
    }

    @Override
    public void initView(View v) {
        getFragmentTitleText().setText(R.string.Bottom_Navigation_Find);
        setLeftImgGone();

        itemExploreStore = (RelativeLayout) v.findViewById(R.id.item_explore_store);
        itemExploreScan = (RelativeLayout) v.findViewById(R.id.item_explore_scan);
        itemExploreMessage = (RelativeLayout) v.findViewById(R.id.item_explore_message);
        item_smartlife = (RelativeLayout) v.findViewById(R.id.item_smartlife);
        itemTest = (RelativeLayout) v.findViewById(R.id.item_test);
        item_setup_service = v.findViewById(R.id.item_setup_service);
        item_tasks = v.findViewById(R.id.item_tasks);

        // 开发模式下, 才会显示 测试栏
        itemTest.setVisibility("debug".equals(BuildConfig.BUILD_TYPE) ? View.VISIBLE : View.GONE);
        if ("debug".equals(BuildConfig.BUILD_TYPE)) {
            itemExploreScan.setVisibility(View.VISIBLE);
        } else {
            itemExploreScan.setVisibility(View.GONE);
        }
        if (!LanguageUtil.isChina()) {
            item_smartlife.setVisibility(View.GONE);
            item_setup_service.setVisibility(View.GONE);
        } else {
            UserBean userBean = JSON.parseObject(preference.getCurrentAccountInfo(), UserBean.class);
            if (userBean.regType == 5) {
                item_tasks.setVisibility(View.VISIBLE);
            }
        }
        initListenner();
    }

    public void initListenner() {
        itemExploreStore.setOnClickListener(this);
        itemExploreScan.setOnClickListener(this);
        itemExploreMessage.setOnClickListener(this);
        item_smartlife.setOnClickListener(this);
        itemTest.setOnClickListener(this);
        item_setup_service.setOnClickListener(this);
        item_tasks.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_explore_store:
                startActivity(new Intent(mActivity, WulianStoreActivity.class));
//                String url = HttpUrlKey.URL_MALL;
//                if(!LanguageUtil.isChina()){
//                    url = HttpUrlKey.URL_MALL_EN;
//                }
//                WebActivity.start(getContext(), url, getString(R.string.Find_WuLianMarket));
                break;
            case R.id.item_explore_scan:
                startActivity(new Intent(mActivity, ExploreQRCodeActivity.class));
                break;
            case R.id.item_explore_message:
                break;
            case R.id.item_smartlife:
                startActivity(new Intent(mActivity, WulianSmartLifeActivity.class));
//                WebActivity.start(getContext(), "http://dwz.cn/6wCqHs", getString(R.string.Find_Intelligentlife));
                break;
            case R.id.item_test:
                startActivity(new Intent(mActivity, TestActivity.class));
                break;
            case R.id.item_setup_service:
                if (preference.isLogin()) {
                    CommonH5Activity.start(mActivity, "https://luban.wulian.cc/lubanfront/track/index.html?token=" + ApiConstant.getAppToken(), "安装服务");
                } else {
                    startActivity(new Intent(mActivity, SigninActivity.class));
                }
                break;
            case R.id.item_tasks:
                if (preference.isLogin()) {
                    CommonH5Activity.start(mActivity, "https://luban.wulian.cc/lubanfront/manager/index.html?token=" + ApiConstant.getAppToken(), "任务大厅");
                } else {
                    startActivity(new Intent(mActivity, SigninActivity.class));
                }
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SkinChangedEvent event) {
        updateSkin();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(AccountEvent event) {
        if (event.action == AccountEvent.ACTION_LOGIN) {
            if (LanguageUtil.isChina() && event.userBean != null) {
                if (event.userBean.regType == 5) {
                    item_tasks.setVisibility(View.VISIBLE);
                } else {
                    item_tasks.setVisibility(View.GONE);
                }
            }
        } else if (event.action == AccountEvent.ACTION_LOGOUT) {
            item_tasks.setVisibility(View.GONE);
        }
    }
}
