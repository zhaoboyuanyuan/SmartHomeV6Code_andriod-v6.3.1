package cc.wulian.smarthomev6.main.mine.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.support.tools.Preference;

public class SceneSettingActivity extends BaseTitleActivity {

    private RelativeLayout item_sudoku_3;
    private RelativeLayout item_sudoku_4;
    private RelativeLayout item_list;
    private ImageView iv_select_1;
    private ImageView iv_select_2;
    private ImageView iv_select_3;

    private String selectedItemTag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_setting,true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle("场景设置");
    }

    @Override
    protected void initView() {
        super.initView();
        item_sudoku_3 = (RelativeLayout) findViewById(R.id.item_sudoku_3);
        item_sudoku_4 = (RelativeLayout) findViewById(R.id.item_sudoku_4);
        item_list = (RelativeLayout) findViewById(R.id.item_list);
        iv_select_1 = (ImageView) findViewById(R.id.iv_selected_1);
        iv_select_2 = (ImageView) findViewById(R.id.iv_selected_2);
        iv_select_3 = (ImageView) findViewById(R.id.iv_selected_3);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        item_sudoku_3.setOnClickListener(this);
        item_sudoku_4.setOnClickListener(this);
        item_list.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        selectedItemTag = Preference.getPreferences().getSceneShowLayout();
        updateSelectedView(selectedItemTag);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.item_sudoku_3:
            case R.id.item_sudoku_4:
            case R.id.item_list:
                updateSelectedView(v.getTag().toString());
                Preference.getPreferences().setSceneShowLayout(v.getTag().toString());
                break;

        }
    }

    private void updateSelectedView(String tag) {
        iv_select_1.setVisibility(TextUtils.equals(tag, "sudoku_3") ? View.VISIBLE : View.GONE);
        iv_select_2.setVisibility(TextUtils.equals(tag, "sudoku_4") ? View.VISIBLE : View.GONE);
        iv_select_3.setVisibility(TextUtils.equals(tag, "list") ? View.VISIBLE : View.GONE);
    }
}
