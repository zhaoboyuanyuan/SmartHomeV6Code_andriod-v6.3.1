package cc.wulian.smarthomev6.main.device.device_23.tv;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.BrandBean;
import cc.wulian.smarthomev6.support.customview.SortBar;
import cc.wulian.smarthomev6.support.event.UEIEvent;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.KeyboardUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.Trans2PinYin;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * 电视机遥控器、机顶盒品牌列表
 */
public class RemoteControlBrandActivity extends BaseTitleActivity {

    public static final String TYPE_TV = "T";
    public static final String TYPE_STB = "C";
    public static final String TYPE_PROJECTOR = "T";
    public static final String TYPE_AUDIO = "R,M";
    public static final String SINGLE_CODE_PROJECTOR = "T_T";//同电视、机顶盒一个type,区分投影仪的code
    private View layout_brand_header;
    private RecyclerView mRecyclerView;
    private SortBar mSortBar;
    private LinearLayout mLinearSearch;
    private EditText mEditSearch;
    private TextView mTextCancel, mTextNoResult;

    private BandListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private WLDialog updateNameDialog;

    private String deviceID;
    private String ueiType, singleCode;
    private List<BrandBean.BrandSortBean> mSortList;
    private List<BrandBean.BrandSortBean> mSearchList;
    private boolean isSearching = false;
    private String mLastGroup = "#", mGroup = "#";
    // 滑动状态
    private int mScrollState = 0;

    public static void start(Context context, String deviceID,String ueiType) {
        Intent intent = new Intent(context, RemoteControlBrandActivity.class);
        intent.putExtra("deviceID", deviceID);
        intent.putExtra("ueiType", ueiType);
        context.startActivity(intent);
    }

    public static void start(Context context, String deviceID,String ueiType, String singleCode) {
        Intent intent = new Intent(context, RemoteControlBrandActivity.class);
        intent.putExtra("deviceID", deviceID);
        intent.putExtra("ueiType", ueiType);
        intent.putExtra("singleCode", singleCode);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_remote_contrl_brand, true);
        EventBus.getDefault().register(this);

    }
    @Override
    protected void initTitle() {
        super.initTitle();
        String title = getResources().getString(R.string.Infraredrelay_Addremote_Brandchoice);
        String customText = getResources().getString(R.string.Infraredrelay_List_Custom);
        customText = customText.length() > 10 ? customText.substring(0, 8) + "..." : customText;
        setToolBarTitleAndRightBtn(title, customText);
    }

    @Override
    protected void initView() {
        layout_brand_header =findViewById(R.id.layout_brand_header);
        mRecyclerView = (RecyclerView) findViewById(R.id.device_23_recycler);
        mSortBar = (SortBar) findViewById(R.id.device_23_aort);
        mLinearSearch = (LinearLayout) findViewById(R.id.device_23_linear_search);
        mTextCancel = (TextView) findViewById(R.id.device_23_text_cancel);
        mTextNoResult = (TextView) findViewById(R.id.device_23_text_noResult);
        mEditSearch = (EditText) findViewById(R.id.device_23_edit_search);


        deviceID = getIntent().getStringExtra("deviceID");
        ueiType = getIntent().getStringExtra("ueiType");
        singleCode = getIntent().getStringExtra("singleCode");

        mAdapter = new BandListAdapter(deviceID,ueiType,singleCode);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mSortList = new ArrayList<>();
        mSearchList = new ArrayList<>();

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, 0, 0, 3);
            }
        });
        mSortBar.setIndex(mGroup);
        mSortBar.setOnSortChangedListener(new SortBar.OnSortChangedListener() {
            @Override
            public void onSortChanged(int newIndex, int oldIndex, String newText, String oldText) {
                int i = mAdapter.getGroupPosition(newText);
                if (i != -1 && (mScrollState == 0)) {
                    mRecyclerView.scrollToPosition(i);
                }
            }
        });

        new DataApiUnit(this).doGetUEIBrand(ueiType,null,new DataApiUnit.DataApiCommonListener<BrandBean>() {
            @Override
            public void onSuccess(BrandBean bean) {
                WLog.i(TAG, "onSuccess: " + bean);
                for (BrandBean.BrandsBean b : bean.brands) {
//                    WLog.i(TAG, "group: " + b.groupName);
                    if (b.group != null && b.group.size() > 0) {
                        mSortList.add(BrandBean.BrandSortBean.groupBean(b.groupName));
                    }
                    for (BrandBean.GroupBean g : b.group) {
//                        WLog.i(TAG, "\t localName: " + g.localName + "\t brandName" + g.brandName);
                        mSortList.add(BrandBean.BrandSortBean.itemBean(g, b.groupName));
                    }
                }

                mAdapter.setData(mSortList);
            }

            @Override
            public void onFail(int code, String msg) {
                WLog.i(TAG, "onFail: " + msg);
            }
        });


    }

    @Override
    protected void initListeners() {
        super.initListeners();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                WLog.i(TAG, "onScrollStateChanged: "+ newState);
                mScrollState = newState;
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int position = mLayoutManager.findFirstVisibleItemPosition();
                if ((isSearching ? mSortList : mSearchList).isEmpty() || position == -1) {
                    return;
                }
                mGroup = (isSearching ? mSortList : mSearchList).get(position).groupName;
                if (!TextUtils.equals(mGroup, mLastGroup)) {
                    mLastGroup = mGroup;
                    WLog.i(TAG, "onScrolled: " + mGroup);
                    mSortBar.setIndex(mGroup);
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
                    // don't have end drawable
                    return false;
                }

                // 点击了 输入框中 右边的 x
                if (event.getX() > mEditSearch.getWidth()
                        - mEditSearch.getPaddingRight()
                        - drawable.getIntrinsicWidth()){
                    mEditSearch.setText("");
                }
                return false;
            }
        });

        mEditSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    // TODO: 2017/3/31 Test Toast Here
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
                doSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                WLog.i("WL--->", "after: " + s);
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.btn_right:
                updateNameDialog();
                break;

        }
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(layout_brand_header, SkinResouceKey.BITMAP_NAV_BOTTOM_BACKGROUND);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UEIEvent event) {
        if (event.mode == 1 && TextUtils.equals(event.type, ueiType)) {
            finish();
        }
    }

    private void doSearch(String key) {
        if (key.trim().isEmpty()) {
            mSearchList = new ArrayList<>(mSortList);
            mTextNoResult.setVisibility(View.INVISIBLE);
            mAdapter.setData(mSearchList);
            return;
        }

        String lowerKey = key.toLowerCase().trim();
        mSearchList.clear();

        for (BrandBean.BrandSortBean bean : mSortList) {
            boolean not_add_me = true;
            // 分组
            if (TextUtils.equals(key, bean.groupName)) {
                not_add_me = false;
            }
            // 匹配 brandName
            if (not_add_me && (bean.brandName.toLowerCase().startsWith(lowerKey) || bean.brandName.toLowerCase().endsWith(lowerKey) || bean.brandName.toLowerCase().contains(lowerKey))) {
                not_add_me = false;
            }
            // 匹配 localName
            if (not_add_me && !TextUtils.isEmpty(bean.localName)) {
                // 匹配汉字
                if (bean.localName.startsWith(key) || bean.localName.endsWith(key) || bean.localName.contains(key)) {
                    not_add_me = false;
                }
                // 拼音首字母
                if (not_add_me && Trans2PinYin.isFirstCharacter(lowerKey, bean.localName)) {
                    not_add_me = false;
                }
                // 拼音开头
                if (not_add_me && Trans2PinYin.isStartPinYin(lowerKey, bean.localName)) {
                    not_add_me = false;
                }
                // 拼音包含
                if (not_add_me && Trans2PinYin.isContainsPinYin(lowerKey, bean.localName)) {
                    not_add_me = false;
                }
            }

            if (! not_add_me) {
                if (!mSearchList.isEmpty()) {
                    // 上一个的分组  和这一个的分组不一样
                    // 如果自己也不是分组
                    // 添加这个分组
                    if (!TextUtils.equals(mSearchList.get(mSearchList.size() - 1).groupName, bean.groupName) && bean.type != 1) {
                        mSearchList.add(BrandBean.BrandSortBean.groupBean(bean.groupName));
                    }
                } else {
                    if (bean.type != 1) {
                        mSearchList.add(BrandBean.BrandSortBean.groupBean(bean.groupName));
                    }
                }
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
    }

    private void updateNameDialog(){
        WLDialog.Builder builder = new WLDialog.Builder(this);
        builder.setEditTextHint(R.string.Infraredrelay_Custom_Entername)
                .setTitle(getString(R.string.Infraredrelay_Custom_Popuptitle))
                .setPositiveButton(R.string.Sure)
                .setNegativeButton(R.string.Cancel)
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View var1, String msg) {
                        if("R,M".equals(ueiType)){
                            AudioRemoteMainActivity.learn(RemoteControlBrandActivity.this, deviceID, ueiType, msg, null);
                        }else if("T".equals(ueiType)){
                            if(SINGLE_CODE_PROJECTOR.equals(singleCode)){
                                ProjectorRemoteMainActivity.learn(RemoteControlBrandActivity.this, deviceID, ueiType, msg, null);
                            }else{
                                TvRemoteMainActivity.learn(RemoteControlBrandActivity.this, deviceID, ueiType, msg, null);
                            }
                        }else if("C".equals(ueiType)){
                            TvRemoteMainActivity.learn(RemoteControlBrandActivity.this, deviceID, ueiType, msg, null);
                        }
                    }

                    @Override
                    public void onClickNegative(View var1) {
                        updateNameDialog.dismiss();

                    }
                });
        updateNameDialog = builder.create();
        updateNameDialog.show();
    }

    /**
     * 取消TextView点击事件
     */
    private void cancelTextClick(View v) {
        isSearching = false;

        mRecyclerView.setVisibility(View.VISIBLE);
        mAdapter.setData(mSortList);

        mLinearSearch.setVisibility(View.VISIBLE);
        mEditSearch.setVisibility(View.INVISIBLE);
        mTextCancel.setVisibility(View.INVISIBLE);
        mTextNoResult.setVisibility(View.INVISIBLE);
        mEditSearch.clearFocus();
        // 强制隐藏键盘
        KeyboardUtil.hideKeyboard(RemoteControlBrandActivity.this, v);
    }

    /**
     * 点击 searchView
     */
    private void searchViewClick() {
        isSearching = true;

        mLinearSearch.setVisibility(View.INVISIBLE);
        mEditSearch.setVisibility(View.VISIBLE);
        mTextCancel.setVisibility(View.VISIBLE);
        mEditSearch.setFocusable(true);
        mEditSearch.setFocusableInTouchMode(true);
        mEditSearch.requestFocus();
        // 强制打开键盘
        KeyboardUtil.showKeyboard(RemoteControlBrandActivity.this);
        mEditSearch.setText("");
    }

}

