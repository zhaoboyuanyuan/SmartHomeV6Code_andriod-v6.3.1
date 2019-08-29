package cc.wulian.smarthomev6.main.mine;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import cc.wulian.smarthomev6.BuildConfig;
import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.LocalInfo;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.main.explore.WulianSmartLifeActivity;
import cc.wulian.smarthomev6.main.explore.WulianStoreActivity;
import cc.wulian.smarthomev6.main.h5.H5BridgeCommonActivity;
import cc.wulian.smarthomev6.main.home.scene.HouseKeeperActivity;
import cc.wulian.smarthomev6.main.login.SigninActivity;
import cc.wulian.smarthomev6.main.mine.gatewaycenter.GatewayCenterActivity;
import cc.wulian.smarthomev6.main.mine.gatewaycenter.GatewayListActivity;
import cc.wulian.smarthomev6.main.mine.setting.SettingActivity;
import cc.wulian.smarthomev6.main.mine.sharedevice.ShareDeviceMainActivity;
import cc.wulian.smarthomev6.main.ztest.TestActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceIsPushBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.UserBean;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.customview.CircleImageView;
import cc.wulian.smarthomev6.support.customview.EmptyGWBindPop;
import cc.wulian.smarthomev6.support.event.AccountEvent;
import cc.wulian.smarthomev6.support.event.CustomerServiceEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GatewayStateChangedEvent;
import cc.wulian.smarthomev6.support.event.MQTTRegisterEvent;
import cc.wulian.smarthomev6.support.event.NewFlagsChangedEvent;
import cc.wulian.smarthomev6.support.event.SkinChangedEvent;
import cc.wulian.smarthomev6.support.tools.FlagsTool;
import cc.wulian.smarthomev6.support.tools.GatewayVersionTool;
import cc.wulian.smarthomev6.support.tools.HttpUrlKey;
import cc.wulian.smarthomev6.support.tools.ImageLoaderTool;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.TimeLock;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by Administrator on 2017/2/10 0010.
 */

public class MineFragment extends WLFragment implements View.OnClickListener {
    private static final String GET_DATA = "GET_DATA";
    private static final String VIRTUAL_GATEWAY_ID = "000000000000";
    private View itemAccountLogin;
    private View itemGatewayCenter;
    private View itemShareDevice;
    private View itemCustomService;
    private View itemCustomFeedback;
    private View itemMemberCenter;
    private View itemSetting;
    private View itemAboutUs;
    private View itemTest;
    private View itemExploreStore;
    private View itemSmartLife;
    private CircleImageView iconHead;
    private TextView accountName;
    private UserBean userBean;

    private View mCustomerServiceNode;
    private TextView tvShareManage;

    private ImageView iv_member_center_newflag, iv_sharedevice_newflag;

    private TimeLock timeLock = new TimeLock();

    private DeviceApiUnit deviceApiUnit;

    @Override
    public int layoutResID() {
        return R.layout.fragment_mine;
    }

    @Override
    public void initView(View v) {
        getFragmentTitleText().setText(R.string.Bottom_Navigation_Mine);
        setLeftImgGone();
        tvShareManage = v.findViewById(R.id.tv_sharedevice);
        itemAccountLogin = v.findViewById(R.id.item_account_login);
        itemGatewayCenter = v.findViewById(R.id.item_gateway_center);
        itemShareDevice = v.findViewById(R.id.item_sharedevice);
        itemCustomService = v.findViewById(R.id.item_customer_service);
        itemCustomFeedback = v.findViewById(R.id.item_customer_feedback);
        itemSetting = v.findViewById(R.id.item_setting);
        itemAboutUs = v.findViewById(R.id.item_about);
        itemTest = v.findViewById(R.id.item_test);
        itemMemberCenter = v.findViewById(R.id.item_member_center);
        itemExploreStore = v.findViewById(R.id.item_explore_store);
        itemSmartLife = v.findViewById(R.id.item_smart_life);
        iconHead = (CircleImageView) v.findViewById(R.id.item_account_login_icon);
        mCustomerServiceNode = v.findViewById(R.id.item_customer_service_icon_node);

        iv_member_center_newflag = (ImageView) v.findViewById(R.id.iv_member_center_newflag);
        iv_sharedevice_newflag = (ImageView) v.findViewById(R.id.iv_sharedevice_newflag);

        accountName = (TextView) v.findViewById(R.id.item_account_login_name);
//        userBean = new UserBean();

        // 开发模式下, 才会显示 测试栏
        itemTest.setVisibility("debug".equals(BuildConfig.BUILD_TYPE) ? View.VISIBLE : View.GONE);
        initListenner();
        updateNewFlag();
    }

    private void updateNewFlag() {
        if (FlagsTool.getFlag(FlagsTool.NEW_WULIANMEMBER_MASK)) {
            iv_member_center_newflag.setVisibility(View.GONE);
        } else {
            iv_member_center_newflag.setVisibility(View.VISIBLE);
        }
        if (FlagsTool.getFlag(FlagsTool.NEW_SHAREMANAGE_MASK)) {
            iv_sharedevice_newflag.setVisibility(View.GONE);
        } else {
            iv_sharedevice_newflag.setVisibility(View.VISIBLE);
        }
    }

    public void initListenner() {
        itemAccountLogin.setOnClickListener(this);
        itemGatewayCenter.setOnClickListener(this);
        itemShareDevice.setOnClickListener(this);
        itemCustomService.setOnClickListener(this);
        itemCustomFeedback.setOnClickListener(this);
        itemSetting.setOnClickListener(this);
        itemAboutUs.setOnClickListener(this);
        itemTest.setOnClickListener(this);
        itemMemberCenter.setOnClickListener(this);
        itemSmartLife.setOnClickListener(this);
        itemExploreStore.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            refreshView();
        }
    }

    @Override
    public void onClick(View view) {
        if (timeLock.isLock()) {
            return;
        }
        timeLock.lock();
        switch (view.getId()) {
            case R.id.item_account_login:
                if (preference.isLogin()) {
                    startActivity(new Intent(mActivity, UserMassageActivity.class));
                } else {
                    startActivity(new Intent(mActivity, SigninActivity.class));
                }
                break;
            case R.id.item_gateway_center:
                if (preference.isLogin()) {
                    startActivity(new Intent(mActivity, GatewayCenterActivity.class));
                } else {
                    startActivity(new Intent(mActivity, SigninActivity.class));
                }
                break;
            case R.id.item_member_center:
                if (preference.isLogin()) {
                    FlagsTool.setFlag(FlagsTool.NEW_WULIANMEMBER_MASK, true);
                    EventBus.getDefault().post(new NewFlagsChangedEvent());
                    startActivity(new Intent(mActivity, MemberCenterActivity.class));
                } else {
                    startActivity(new Intent(mActivity, SigninActivity.class));
                }
                break;
            case R.id.item_sharedevice:
                if (preference.isLogin()) {
                    FlagsTool.setFlag(FlagsTool.NEW_SHAREMANAGE_MASK, true);
                    EventBus.getDefault().post(new NewFlagsChangedEvent());
                    startActivity(new Intent(mActivity, ShareDeviceMainActivity.class));
                } else {
                    startActivity(new Intent(mActivity, SigninActivity.class));
                }
                break;
            case R.id.item_customer_service:
                if (preference.isLogin()) {
//                    showCustomerServiceDialog();
//                    startActivity(new Intent(mActivity, CustomerServiceActivity.class));
                    if (TextUtils.equals(Preference.ENTER_TYPE_ACCOUNT, preference.getUserEnterType())) {
//                        startActivity(new Intent(mActivity, CustomerServiceTitleActivity.class));
                        startActivity(new Intent(mActivity, OnlineServiceActivity.class));
                    } else {
                        Intent intent = new Intent(getContext(), H5BridgeCommonActivity.class);
                        intent.putExtra("url", HttpUrlKey.URL_CUSTOMER_SERVICE_GW_SHORT);
                        startActivity(intent);
                    }
                    mCustomerServiceNode.setVisibility(View.INVISIBLE);
                } else {
                    startActivity(new Intent(mActivity, SigninActivity.class));
                }
                break;
            case R.id.item_customer_feedback:
                if (preference.isLogin()) {
                    startActivity(new Intent(mActivity, FeedbackActivity.class));
                } else {
                    startActivity(new Intent(mActivity, SigninActivity.class));
                }
                break;
            case R.id.item_setting:
                if (preference.isLogin()) {
                    startActivity(new Intent(mActivity, SettingActivity.class));
                } else {
                    startActivity(new Intent(mActivity, SigninActivity.class));
                }
                break;
            case R.id.item_about:
                startActivity(new Intent(mActivity, AboutActivity.class));
                break;
            case R.id.item_test:
                startActivity(new Intent(mActivity, TestActivity.class));
                break;
            case R.id.item_explore_store:
                startActivity(new Intent(mActivity, WulianStoreActivity.class));
                break;
            case R.id.item_smart_life:
                startActivity(new Intent(mActivity, WulianSmartLifeActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MQTTRegisterEvent event) {
        if (event.state == MQTTRegisterEvent.STATE_REGISTER_SUCCESS && Preference.ENTER_TYPE_GW.equals(preference.getUserEnterType())) {
            itemAccountLogin.setVisibility(View.GONE);
            itemMemberCenter.setVisibility(View.GONE);
            itemShareDevice.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CustomerServiceEvent event) {
        WLog.i(TAG, "onCustomerServiceEventEvent: " + event.toString());
        mCustomerServiceNode.setVisibility(View.VISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(AccountEvent event) {
        UserBean userBean = event.userBean;
        if (event.action == AccountEvent.ACTION_LOGIN) {
            if (StringUtil.isNullOrEmpty(userBean.nick)) {
                accountName.setText(userBean.phone);
            } else {
                accountName.setText(userBean.nick);
            }

            itemAccountLogin.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(userBean.avatar, iconHead, ImageLoaderTool.getUserAvatarOptions());
        } else if (event.action == AccountEvent.ACTION_LOGOUT) {
            itemAccountLogin.setVisibility(View.VISIBLE);
            iconHead.setImageResource(R.drawable.icon_head);
            accountName.setText(R.string.Mine_Login_Register);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(NewFlagsChangedEvent event) {
        updateNewFlag();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SkinChangedEvent event) {
        updateSkin();
    }

    private void refreshView() {
        if (!TextUtils.isEmpty(preference.getCurrentAccountInfo())) {
            userBean = JSON.parseObject(preference.getCurrentAccountInfo(), UserBean.class);
        }
        if (!preference.isLogin()) {
            itemAccountLogin.setVisibility(View.VISIBLE);
            itemCustomFeedback.setVisibility(View.VISIBLE);
            itemMemberCenter.setVisibility(View.VISIBLE);
            itemShareDevice.setVisibility(View.VISIBLE);
            iconHead.setImageResource(R.drawable.icon_head);
            accountName.setText(R.string.Mine_Login_Register);
        } else {
            if (preference.getUserEnterType().equals(Preference.ENTER_TYPE_ACCOUNT)) {
                if (userBean != null) {
                    if (StringUtil.isNullOrEmpty(userBean.nick)) {
                        accountName.setText(userBean.phone);
                    } else {
                        accountName.setText(userBean.nick);
                    }
                    ImageLoader.getInstance().displayImage(userBean.avatar, iconHead, ImageLoaderTool.getUserAvatarOptions());
                    if (VIRTUAL_GATEWAY_ID.equals(preference.getCurrentGatewayID())) {
                        itemShareDevice.setEnabled(false);
                        tvShareManage.setTextColor(getResources().getColor(R.color.grey));
                    } else {
                        itemShareDevice.setEnabled(true);
                        tvShareManage.setTextColor(getResources().getColor(R.color.black));
                    }
                }

                itemAccountLogin.setVisibility(View.VISIBLE);
            } else if (preference.getUserEnterType().equals(Preference.ENTER_TYPE_GW)) {
                itemAccountLogin.setVisibility(View.GONE);
                itemCustomFeedback.setVisibility(View.GONE);
                itemMemberCenter.setVisibility(View.GONE);
                itemShareDevice.setVisibility(View.GONE);
            }
        }
    }

}
