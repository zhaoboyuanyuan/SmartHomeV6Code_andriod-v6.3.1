package cc.wulian.smarthomev6.main.home.scene;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseFullscreenActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

public class AllSceneActivity extends BaseFullscreenActivity implements View.OnClickListener {

    private static final String ALL_SCENE = "all";
    private View layout_title;
    private View lineAllScene, lineRecommendScene;
    private TextView tvAllScene;
    private TextView tvRecommendScene;
    private ImageView ivSceneSort;
    private Context context;
    private ArrayList<Fragment> fragments;
    private AllSceneFragment allSceneFragment;
    private RecommendSceneFragment recommendSceneFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_scene);
        context = this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void initView() {

        layout_title = findViewById(R.id.layout_title);
        tvAllScene = (TextView) findViewById(R.id.all_scene_text_title);
        tvRecommendScene = (TextView) findViewById(R.id.tv_recommend_scene);
        ivSceneSort = (ImageView) findViewById(R.id.all_scene_image_sort);
        lineAllScene = findViewById(R.id.line_all_scene);
        lineRecommendScene = findViewById(R.id.line_recommend);
        findViewById(R.id.all_scene_image_back).setOnClickListener(this);
        findViewById(R.id.all_scene_image_sort).setOnClickListener(this);
        findViewById(R.id.all_scene_image_add).setOnClickListener(this);
        allSceneFragment = new AllSceneFragment();
        recommendSceneFragment = new RecommendSceneFragment();
        fragmentManager = getSupportFragmentManager();
        fragments = getFragments();
        showSelectedFragment(ALL_SCENE);
    }


    private ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(allSceneFragment);
        fragments.add(recommendSceneFragment);
        return fragments;
    }

    @Override
    protected View getFullscreenPaddingLayout() {
        return layout_title;
    }

    @Override
    protected void updateSkin() {
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(layout_title, SkinResouceKey.BITMAP_TITLE_BACKGROUND);
    }

    @Override
    protected void initData() {
    }


    @Override
    protected void initListeners() {
        tvAllScene.setOnClickListener(this);
        tvRecommendScene.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.all_scene_text_title:
                showSelectedFragment(v.getTag().toString());
                break;
            case R.id.tv_recommend_scene:
                showSelectedFragment(v.getTag().toString());
                break;
            case R.id.all_scene_image_back:
                AllSceneActivity.this.finish();
                break;
            case R.id.all_scene_image_sort: {
                Intent intent = new Intent(AllSceneActivity.this, SceneSortActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.bottomtotop_in, 0);
            }
            break;
            case R.id.all_scene_image_add:
                if ("1".equals(preference.getCurrentGatewayState())) {
                    startActivity(new Intent(AllSceneActivity.this, AddSceneActivity.class));
                } else {
                    ToastUtil.show(getString(R.string.Gateway_Offline));
                }
                break;
        }
    }

    private void showSelectedFragment(String tag) {
        ivSceneSort.setVisibility(ALL_SCENE.equals(tag) ? View.VISIBLE : View.INVISIBLE);
        tvAllScene.setTextColor(ALL_SCENE.equals(tag) ? getResources().getColor(R.color.white) : getResources().getColor(R.color.scene_text_unPressed));
        tvRecommendScene.setTextColor(!ALL_SCENE.equals(tag) ? getResources().getColor(R.color.white) : getResources().getColor(R.color.scene_text_unPressed));
        lineAllScene.setVisibility(ALL_SCENE.equals(tag) ? View.VISIBLE : View.INVISIBLE);
        lineRecommendScene.setVisibility(!ALL_SCENE.equals(tag) ? View.VISIBLE : View.INVISIBLE);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = ALL_SCENE.equals(tag) ? allSceneFragment : recommendSceneFragment;
        transaction.replace(R.id.layFrame, fragment, tag);
        transaction.commit();
    }


}
