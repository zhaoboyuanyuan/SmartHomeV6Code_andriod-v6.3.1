package cc.wulian.smarthomev6.main.home;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.main.home.adapter.HomeWidgetAdapter;
import cc.wulian.smarthomev6.main.home.widget.HomeWidgetManager;
import cc.wulian.smarthomev6.main.login.SigninActivity;
import cc.wulian.smarthomev6.main.message.MessageCenterNewActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.MessageCountBean;
import cc.wulian.smarthomev6.support.customview.BadgeView2;
import cc.wulian.smarthomev6.support.customview.TopStateView;
import cc.wulian.smarthomev6.support.event.AlarmUpdateEvent;
import cc.wulian.smarthomev6.support.event.GatewayStateChangedEvent;
import cc.wulian.smarthomev6.support.event.HomeDeviceWidgetChangeEvent;
import cc.wulian.smarthomev6.support.event.LoginEvent;
import cc.wulian.smarthomev6.support.event.ShareDeviceStatusChangedEvent;
import cc.wulian.smarthomev6.support.event.SkinChangedEvent;
import cc.wulian.smarthomev6.support.event.VideoEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by Administrator on 2017/1/15
 */
public class HomeFragment extends WLFragment {

    private RecyclerView mHomeRecyclerView;
    private LinearLayoutManager mHomeLinearLayoutManager;
    public HomeWidgetAdapter mHomeWidgetAdapter;

    //顶部状态文字
    private TopStateView view_topstate;
    // 右上角铃铛的角标
    private BadgeView2 homeBadge;

    private ImageView mImageGif;
    private AnimationDrawable mAnimGif;
    private boolean mHasGif = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int layoutResID() {
        return R.layout.home_layout_content;
    }

    @Override
    protected void initTitle(View v) {
        super.initTitle(v);

        setRightText("").setVisibility(View.GONE);
        setLeftImgGone();
        setTitleImg(R.drawable.home_logo);
        setRightImageAndEvent(R.drawable.home_alarm_message, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (preference.isLogin()) {
                    if (preference.getUserEnterType().equals(Preference.ENTER_TYPE_ACCOUNT)) {
                        startActivity(new Intent(getActivity(), MessageCenterNewActivity.class));
                    } else {
                        ToastUtil.show(R.string.Home_Login_In_Tip);
                    }
                } else {
                    startActivity(new Intent(getActivity(), SigninActivity.class));
                }
            }
        });
    }

    @Override
    public void initView(View v) {
        homeBadge = new BadgeView2(mActivity);
        homeBadge.setTargetView(v.findViewById(R.id.base_img_right));
        homeBadge.setBadgeGravity(Gravity.TOP | Gravity.START);

        view_topstate = (TopStateView) v.findViewById(R.id.view_topstate);
        mHomeRecyclerView = (RecyclerView) v.findViewById(R.id.home_content);
        mHomeLinearLayoutManager = new LinearLayoutManager(getActivity());
        mHomeWidgetAdapter = new HomeWidgetAdapter(getActivity(), HomeWidgetManager.acquireWidget());
        mHomeRecyclerView.setLayoutManager(mHomeLinearLayoutManager);
        mHomeRecyclerView.setAdapter(mHomeWidgetAdapter);
        mHomeRecyclerView.setHasFixedSize(true);
        mHomeRecyclerView.setNestedScrollingEnabled(false);
        mHomeRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, 0, 0, 30);
            }
        });

        mHomeWidgetAdapter.setFooterView(inflater.inflate(R.layout.home_recycle_footview, mHomeRecyclerView, false));
        mHomeWidgetAdapter.getFooterView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, HomeEditActivity.class);
                startActivity(intent);
                HomeFragment.this.getActivity().overridePendingTransition(R.anim.bottomtotop_in, 0);
            }
        });

        updateGatewayStateView();

        if (TextUtils.equals(preference.getUserEnterType(), Preference.ENTER_TYPE_ACCOUNT)) {
            syncWidgetSort();
            getAlarmCount();
        }

        EventBus.getDefault().register(this);

        WLog.i(TAG, "Skin: " + Preference.getPreferences().getCurrentSkin());
    }

    public void hideGifView() {
        if (mHasGif) {
            if (mAnimGif != null) {
                if (mAnimGif.isRunning()) {
                    mAnimGif.stop();
                }

                ((FrameLayout) rootView.getParent()).removeView(mImageGif);
                mHasGif = false;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().post(new VideoEvent());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            EventBus.getDefault().post(new VideoEvent());
        }
    }

    //根据当前网关状态改变顶部view
    private void updateGatewayStateView() {
        view_topstate.setVisibility(View.GONE);
    }

    /**
     * 获取未读报警消息数
     * <p>
     * 只有在账号登录的情况下,才获取消息中心http数据
     */
    private void getAlarmCount() {
        if (preference.isLogin() && preference.getUserEnterType().equals(Preference.ENTER_TYPE_ACCOUNT)) {
            DataApiUnit dataUnit = new DataApiUnit(getActivity());
            dataUnit.deGetAlarmCount(preference.getCurrentGatewayID(), null, "1", "1", new DataApiUnit.DataApiCommonListener() {
                @Override
                public void onSuccess(Object bean) {
                    MessageCountBean messageCountBean = (MessageCountBean) bean;
                    MainApplication.getApplication().getAlarmCountCache().setAlarmCount(messageCountBean);
                    WLog.i(TAG, "获取报警消息数 " + messageCountBean.totalCount);
                    // 显示报警消息数量
                    showAlarmCount();
                }

                @Override
                public void onFail(int code, String msg) {
                    // 获取未读消息数 - 失败
                    WLog.i(TAG, "onFail: --------------- 获取报警消息数 " + msg);
                }
            });
        } else {
            showAlarmCount();
        }
    }

    /**
     * 显示报警消息数量
     * <p>
     * 网关登录情况下, 不显示数量
     */
    private void showAlarmCount() {
        if (homeBadge == null) {
            return;
        }

        int count = MainApplication.getApplication().getAlarmCountCache().getAlarmTotalCount();
        if (TextUtils.equals(Preference.ENTER_TYPE_ACCOUNT, preference.getUserEnterType())) {
            homeBadge.setBadgeCount(count);
        } else {
            homeBadge.setBadgeCount(0);
        }
    }

    /**
     * widget同步
     */
    private void syncWidgetSort() {
        HomeWidgetManager.updateCloudSort();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHomeDeviceWidgetChangeEvent(HomeDeviceWidgetChangeEvent event) {
        if (event.getType() == HomeDeviceWidgetChangeEvent.ADD) {
            mHomeWidgetAdapter.add(event.getHomeItemBean());
        } else if (event.getType() == HomeDeviceWidgetChangeEvent.DELETE) {
            mHomeWidgetAdapter.remove(event.getHomeItemBean());
        } else if (event.getType() == HomeDeviceWidgetChangeEvent.ALL) {
            mHomeWidgetAdapter.update(HomeWidgetManager.acquireWidget());
        } else if (event.getType() == HomeDeviceWidgetChangeEvent.SYNC) {
            mHomeWidgetAdapter.update(HomeWidgetManager.acquireWidget());
            if (TextUtils.equals(preference.getUserEnterType(), Preference.ENTER_TYPE_ACCOUNT)) {
                syncWidgetSort();
                getAlarmCount();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGatewayStateChanged(GatewayStateChangedEvent event) {
        updateGatewayStateView();
        mHomeWidgetAdapter.update(HomeWidgetManager.acquireWidget());
        if (TextUtils.equals(preference.getUserEnterType(), Preference.ENTER_TYPE_ACCOUNT)) {
            syncWidgetSort();
            getAlarmCount();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginEvent(LoginEvent event) {
        WLog.i(TAG, "onLoginEvent: 登录事件");
        mHomeWidgetAdapter.update(HomeWidgetManager.acquireWidget());
        if (TextUtils.equals(preference.getUserEnterType(), Preference.ENTER_TYPE_ACCOUNT)) {
            syncWidgetSort();
            getAlarmCount();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAlarmUpdateEvent(AlarmUpdateEvent event) {
        // 显示报警消息数量
        showAlarmCount();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SkinChangedEvent event) {
        updateSkin();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ShareDeviceStatusChangedEvent event) {
        mHomeWidgetAdapter.update(HomeWidgetManager.acquireWidget());
    }
}
