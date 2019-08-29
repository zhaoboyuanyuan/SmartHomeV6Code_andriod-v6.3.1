package cc.wulian.smarthomev6.main.device.cateye.setting;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wulian.sdk.android.ipc.rtcv2.IPCMsgController;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.CateyeZoneBean;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.adapter.CateyeZoneAdapter;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamDeviceBean;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.CameraUtil;
import cc.wulian.smarthomev6.support.utils.KeyboardUtil;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;
import cc.wulian.smarthomev6.support.utils.Trans2PinYin;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * created by huxc  on 2017/11/9.
 * func： 猫眼时区设置界面
 * email: hxc242313@qq.com
 */

public class CateyeZoneSettingActivity extends BaseTitleActivity {
    private View layout_brand_header;
    private RecyclerView mRecyclerView;
    private LinearLayout mLinearSearch;
    private EditText mEditSearch;
    private TextView mTextCancel, mTextNoResult;

    private CateyeZoneAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private List<CateyeZoneBean> mTotalList;
    private List<CateyeZoneBean> mSearchList;
    private String cityNum;
    private CateyeZoneBean selectedZoneName = new CateyeZoneBean();
    private ICamDeviceBean iCamDeviceBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_zone_setting, true);
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(layout_brand_header, SkinResouceKey.BITMAP_NAV_BOTTOM_BACKGROUND);
    }

    @Override
    protected void initView() {
        super.initView();
        layout_brand_header = findViewById(R.id.layout_brand_header);
        mRecyclerView = (RecyclerView) findViewById(R.id.camera_zone_recycler);
        mLinearSearch = (LinearLayout) findViewById(R.id.camera_zone_linear_search);
        mTextCancel = (TextView) findViewById(R.id.camera_zone_text_cancel);
        mTextNoResult = (TextView) findViewById(R.id.camera_zone_text_noResult);
        mEditSearch = (EditText) findViewById(R.id.camera_zone_edit_search);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, 0, 0, 3);
            }
        });

    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Time_Zone));
    }

    @Override
    protected void initData() {
        super.initData();
        mSearchList = new ArrayList<>();
        mTotalList = CameraUtil.getCateyeZoneDataFromJson(this);
        iCamDeviceBean = (ICamDeviceBean) getIntent().getSerializableExtra("ICamDeviceBean");
        cityNum = getIntent().getStringExtra("cityNum");
        mAdapter = new CateyeZoneAdapter();
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, 0, 0, 3);
            }
        });
        mAdapter.setData(mTotalList);
        if (!TextUtils.isEmpty(cityNum)) {
            selectedZoneName.en = mTotalList.get(Integer.parseInt(cityNum)).en;
            setDefaultSelectedItem(selectedZoneName);
        }
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        mAdapter.setOnItemClickListener(new CateyeZoneAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, CateyeZoneBean bean) {
                mAdapter.setSelectItem(position);
                mAdapter.notifyDataSetChanged();
                selectedZoneName = bean;
                for (int i = 0; i < mTotalList.size(); i++) {
                    if (mTotalList.get(i).cn.contains(bean.cn)) {
                        WLog.i(TAG, "deviceId = " + iCamDeviceBean.did + "\n" + "devDomain = " + iCamDeviceBean.sdomain + "\n" + "timeZone = " + Integer.parseInt(mTotalList.get(position).timeZone) + "\n" + "position = " + position);
                        IPCMsgController.MsgWulianBellQuerySetTimeZone(iCamDeviceBean.did, iCamDeviceBean.sdomain, Integer.parseInt(mTotalList.get(i).timeZone), i);
                        setResult(RESULT_OK, new Intent().putExtra("cityNum", i));
                        finish();
                    }
                }
            }
        });
        mLinearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchViewClick();
            }
        });
        mTextCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelTextClick(v);
            }
        });

        mEditSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Drawable drawable = mEditSearch.getCompoundDrawables()[2];
                if (drawable == null) {
                    return false;
                }

                // 点击了 输入框中 右边的 x
                if (event.getX() > mEditSearch.getWidth()
                        - mEditSearch.getPaddingRight()
                        - drawable.getIntrinsicWidth()) {
                    mEditSearch.setText("");
                }
                return false;
            }
        });

        mEditSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                }
                return false;
            }
        });

        mEditSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                WLog.i(TAG, "before: " + s + ", count: " + count);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                WLog.i(TAG, "on: " + s + ", count: " + count);
                if (LanguageUtil.isChina()) {
                    doChSearch(s.toString());
                } else {
                    doEnSearch(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                WLog.i("WL--->", "after: " + s);
            }
        });
    }

    /**
     * 默认设置地区显示
     * 取消键 显示设置的地区
     */
    private void setDefaultSelectedItem(CateyeZoneBean bean) {
        mAdapter.setSelectItem(-1);
        for (int i = 0; i < mTotalList.size(); i++) {
            if (mTotalList.get(i).en.contains(bean.en)) {
                mAdapter.setSelectItem(i);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 搜索后显示设置的地区
     */
    private void showSearchSelectedItem() {
        mAdapter.setSelectItem(-1);
        for (int i = 0; i < mSearchList.size(); i++) {
            if (!TextUtils.isEmpty(selectedZoneName.en)) {
                if (mSearchList.get(i).en.contains(selectedZoneName.en)) {
                    mAdapter.setSelectItem(i);
                }
            }
        }
    }

    /**
     * 取消TextView点击事件
     */
    private void cancelTextClick(View v) {

        mRecyclerView.setVisibility(View.VISIBLE);
        mAdapter.setData(mTotalList);
        setDefaultSelectedItem(selectedZoneName);
        mLinearSearch.setVisibility(View.VISIBLE);
        mEditSearch.setVisibility(View.INVISIBLE);
        mTextCancel.setVisibility(View.INVISIBLE);
        mTextNoResult.setVisibility(View.INVISIBLE);
        mEditSearch.clearFocus();
        // 强制隐藏键盘
        KeyboardUtil.hideKeyboard(CateyeZoneSettingActivity.this, v);
    }

    /**
     * 点击 searchView
     */
    private void searchViewClick() {

        mLinearSearch.setVisibility(View.INVISIBLE);
        mEditSearch.setVisibility(View.VISIBLE);
        mTextCancel.setVisibility(View.VISIBLE);
        mEditSearch.setFocusable(true);
        mEditSearch.setFocusableInTouchMode(true);
        mEditSearch.requestFocus();
        // 强制打开键盘
        KeyboardUtil.showKeyboard(CateyeZoneSettingActivity.this);
        mEditSearch.setText("");
    }

    private void doChSearch(String key) {
        if (key.trim().isEmpty()) {
            mSearchList = new ArrayList<>();
            mTextNoResult.setVisibility(View.INVISIBLE);
            mAdapter.setData(mSearchList);
            return;
        }

        String lowerKey = key.toLowerCase().trim();
        mSearchList.clear();

        for (CateyeZoneBean bean : mTotalList) {
            boolean not_add_me = true;
            // 分组
            if (TextUtils.equals(key, bean.cn)) {
                not_add_me = false;
            }
            // 匹配 brandName
            if (not_add_me && (bean.cn.startsWith(lowerKey) || bean.cn.endsWith(lowerKey) || bean.cn.toLowerCase().contains(lowerKey))) {
                not_add_me = false;
            }
            // 匹配 localName
            if (not_add_me && !TextUtils.isEmpty(bean.cn)) {
                // 匹配汉字
                if (bean.cn.startsWith(key) || bean.cn.endsWith(key) || bean.cn.contains(key)) {
                    not_add_me = false;
                }
                // 拼音首字母
                if (not_add_me && Trans2PinYin.isFirstCharacter(lowerKey, bean.cn)) {
                    not_add_me = false;
                }
                // 拼音开头
                if (not_add_me && Trans2PinYin.isStartPinYin(lowerKey, bean.cn)) {
                    not_add_me = false;
                }
                // 拼音包含
                if (not_add_me && Trans2PinYin.isContainsPinYin(lowerKey, bean.cn)) {
                    not_add_me = false;
                }
            }

            if (!not_add_me) {
                mSearchList.add(bean);
            }
        }

        if (mSearchList.size() == 0) {
            mTextNoResult.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
            return;
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mTextNoResult.setVisibility(View.INVISIBLE);
        }
        mAdapter.setData(mSearchList);
        showSearchSelectedItem();

    }

    private void doEnSearch(String key) {
        if (key.trim().isEmpty()) {
            mSearchList = new ArrayList<>();
            mTextNoResult.setVisibility(View.INVISIBLE);
            mAdapter.setData(mSearchList);
            return;
        }

        String lowerKey = key.toLowerCase().trim();
        mSearchList.clear();

        for (CateyeZoneBean bean : mTotalList) {
            boolean not_add_me = true;
            // 分组
            if (TextUtils.equals(key, bean.en)) {
                not_add_me = false;
            }
            // 匹配 brandName
            if (not_add_me && (bean.en.startsWith(lowerKey) || bean.en.endsWith(lowerKey) || bean.en.toLowerCase().contains(lowerKey))) {
                not_add_me = false;
            }
            // 匹配 localName
            if (not_add_me && !TextUtils.isEmpty(bean.en)) {
                // 匹配汉字
                if (bean.cn.startsWith(key) || bean.en.endsWith(key) || bean.en.contains(key)) {
                    not_add_me = false;
                }
                // 拼音首字母
                if (not_add_me && Trans2PinYin.isFirstCharacter(lowerKey, bean.en)) {
                    not_add_me = false;
                }
                // 拼音开头
                if (not_add_me && Trans2PinYin.isStartPinYin(lowerKey, bean.en)) {
                    not_add_me = false;
                }
                // 拼音包含
                if (not_add_me && Trans2PinYin.isContainsPinYin(lowerKey, bean.en)) {
                    not_add_me = false;
                }
            }

            if (!not_add_me) {
                mSearchList.add(bean);
            }
        }

        if (mSearchList.size() == 0) {
            mTextNoResult.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
            return;
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mTextNoResult.setVisibility(View.INVISIBLE);
        }
        mAdapter.setData(mSearchList);
        showSearchSelectedItem();

    }

}
