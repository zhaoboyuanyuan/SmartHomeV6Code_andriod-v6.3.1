package cc.wulian.smarthomev6.main.smart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.model.GuidePage;
import com.app.hubert.guide.model.RelativeGuide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.main.home.scene.AddSceneActivity;
import cc.wulian.smarthomev6.main.home.scene.AllScenesFragment;
import cc.wulian.smarthomev6.main.home.scene.SceneSortActivity;
import cc.wulian.smarthomev6.support.customview.CreateAutoTaskPop;
import cc.wulian.smarthomev6.support.event.SkinChangedEvent;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

public class SmartFragment extends WLFragment implements View.OnClickListener {
    private static final String ALL_SCENE = "all";
    private View layout_title;
    private View lineAllScene, lineHousekeeper;
    private TextView tvAllScene;
    private TextView tvHouseKeeper;
    private ImageView ivAdd;
    private ImageView ivSceneSetting;
    private AllScenesFragment allSceneFragment;
    private HouseKeeperFragment houseKeeperFragment;
    private FragmentManager fragmentManager;
    private String currentSelectedTag = ALL_SCENE;
    private CreateAutoTaskPop createAutoTaskPop;

    @Override
    public int layoutResID() {
        return R.layout.fragment_smart;
    }

    @Override
    protected void initTitle(View v) {
        super.initTitle(v);
        header.setVisibility(View.GONE);
    }

    @Override
    public void initView(View view) {
        layout_title = view.findViewById(R.id.layout_title);
        lineAllScene = view.findViewById(R.id.line_all_scene);
        lineHousekeeper = view.findViewById(R.id.line_houseKeeper);
        tvAllScene = view.findViewById(R.id.tv_all_scene);
        tvHouseKeeper = view.findViewById(R.id.tv_houseKeeper);
        ivAdd = view.findViewById(R.id.all_scene_image_add);
        ivSceneSetting = view.findViewById(R.id.iv_scene_setting);
        ivAdd.setOnClickListener(this);
        ivSceneSetting.setOnClickListener(this);


        int layoutRes;
        if (LanguageUtil.isChina()) {
            layoutRes = R.layout.view_guide_scene_setting;
        } else {
            layoutRes = R.layout.view_guide_scene_setting_en;
        }
        NewbieGuide.with(this).setLabel("sceneSetting")
                .addGuidePage(GuidePage.newInstance().addHighLight(ivSceneSetting, new RelativeGuide(layoutRes,
                        Gravity.BOTTOM, 0)))
                .alwaysShow(false)
                .show();
    }

    @Override
    protected void initData() {
        super.initData();
        fragmentManager = getChildFragmentManager();
        showSelectedFragment(ALL_SCENE);
    }

    @Override
    public void initListener() {
        super.initListener();
        tvAllScene.setOnClickListener(this);
        tvHouseKeeper.setOnClickListener(this);
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(layout_title, SkinResouceKey.BITMAP_TITLE_BACKGROUND);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.i(TAG, "onHiddenChanged: ");
        if (!isHidden()) {
            showSelectedFragment(currentSelectedTag);
        }
    }

    private void showSelectedFragment(String tag) {
        currentSelectedTag = tag;
        ivAdd.setTag(ALL_SCENE.equals(tag) ? "scene" : "housekeeper");
        ivSceneSetting.setVisibility(ALL_SCENE.equals(tag) ? View.VISIBLE : View.INVISIBLE);
        lineAllScene.setVisibility(ALL_SCENE.equals(tag) ? View.VISIBLE : View.INVISIBLE);
        lineHousekeeper.setVisibility(!ALL_SCENE.equals(tag) ? View.VISIBLE : View.INVISIBLE);
        tvAllScene.setTextColor(ALL_SCENE.equals(tag) ? getResources().getColor(R.color.white) : getResources().getColor(R.color.scene_text_unPressed));
        tvHouseKeeper.setTextColor(!ALL_SCENE.equals(tag) ? getResources().getColor(R.color.white) : getResources().getColor(R.color.scene_text_unPressed));

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideAllFragment(transaction);
        if (ALL_SCENE.equals(tag)) {
            if (allSceneFragment == null) {
                allSceneFragment = new AllScenesFragment();
                transaction.add(R.id.layFrame, allSceneFragment);
            } else {
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                transaction.show(allSceneFragment);
            }
        } else {
            if (houseKeeperFragment == null) {
                houseKeeperFragment = new HouseKeeperFragment();
                transaction.add(R.id.layFrame, houseKeeperFragment);
            } else {
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                transaction.show(houseKeeperFragment);
            }

        }
        transaction.commit();
    }

    private void hideAllFragment(FragmentTransaction transaction) {
        if (allSceneFragment != null) {
            transaction.hide(allSceneFragment);
        }
        if (houseKeeperFragment != null) {
            transaction.hide(houseKeeperFragment);
        }
    }

    private void showCreateAutoTaskPop() {
        createAutoTaskPop = new CreateAutoTaskPop(getActivity());
        createAutoTaskPop.showAsDropDown(getActivity().findViewById(R.id.all_scene_image_add));
        createAutoTaskPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                createAutoTaskPop.backgroundAlpha((Activity) getActivity(), 1f);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_all_scene:
                showSelectedFragment(v.getTag().toString());
                break;
            case R.id.tv_houseKeeper:
                if (preference.getCurrentGatewayState().equals("0")) {
                    ToastUtil.show(R.string.Gateway_Offline);
                    return;
                }
                showSelectedFragment(v.getTag().toString());
                break;
            case R.id.iv_scene_setting:
                Intent intent = new Intent(getActivity(), SceneSortActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.bottomtotop_in, 0);
                break;
            case R.id.all_scene_image_add:
                if (preference.getCurrentGatewayState().equals("0")) {
                    ToastUtil.show(R.string.Gateway_Offline);
                    return;
                }
                if (TextUtils.equals("scene", ivAdd.getTag().toString())) {
                    startActivity(new Intent(getActivity(), AddSceneActivity.class));
                } else if (TextUtils.equals("housekeeper", ivAdd.getTag().toString())) {
                    showCreateAutoTaskPop();
                }
                break;
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SkinChangedEvent event) {
        updateSkin();
    }
}
