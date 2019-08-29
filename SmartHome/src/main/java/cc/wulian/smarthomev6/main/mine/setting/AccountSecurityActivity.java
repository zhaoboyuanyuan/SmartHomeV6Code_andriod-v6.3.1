package cc.wulian.smarthomev6.main.mine.setting;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.alibaba.fastjson.JSON;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.shareboard.SnsPlatform;

import java.util.List;
import java.util.Map;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.mine.gatewaycenter.ChangePasswordActivity;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.SsoApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.AccountBindInfoBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.ThirdPartyBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.UserBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.sso.RegisterPhoneBean;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by mamengchao on 2017/3/1 0001.
 * Tips:
 */
public class AccountSecurityActivity extends BaseTitleActivity implements CompoundButton.OnCheckedChangeListener{

    private static final String BIND = "BIND";
    private static final String UNBIND = "UNBIND";
    private static final String GET_BIND_INFO = "GET_BIND_INFO";
    private final String QQ_PACKAGE_NAME = "com.tencent.mobileqq";
    private final String WEIXIN_PACKAGE_NAME = "com.tencent.mm";
    private final String WEIBO_PACKAGE_NAME = "com.sina.weibo";
    private static final int REQUEST_CODE_RESET_PWD = 1;
    private RelativeLayout itemSecurityPhone;
    private RelativeLayout itemSecurityMail;
    private RelativeLayout itemSecurityAccountInfo;
    private RelativeLayout itemSecurityBindWeChat;
    private RelativeLayout itemSecurityBindQQ;
    private RelativeLayout itemSecurityBindWeiBo;
    private TextView itemSecurityPhoneTv;
    private TextView itemSecurityMailTv;
    private TextView itemSecurityAccountInfoTv;
    private ToggleButton item_bind_wechat;
    private ToggleButton item_bind_qq;
    private ToggleButton item_bind_weibo;

    private WLDialog.Builder builder;
    private WLDialog dialog;

    private UserBean userBean;
    private SsoApiUnit ssoApiUnit;
    private int currentBindType = 0;
    private boolean isBind = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ssoApiUnit = new SsoApiUnit(this);
        setContentView(R.layout.activity_account_security, true);

        initView();
        initData();
        doGetBindInfo();
        initListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isBind){
            progressDialogManager.dimissDialog(BIND, 0);
            setCheck(currentBindType, false);
            isBind = false;
        }
        initData();
    }

    @Override
    protected void initTitle() {
        setToolBarTitle(getString(R.string.AccountSecurity_Safety));
    }

    protected void initView() {
        itemSecurityPhone = (RelativeLayout) findViewById(R.id.item_account_security_phone);
        itemSecurityMail = (RelativeLayout) findViewById(R.id.item_account_security_mail);
        itemSecurityAccountInfo = (RelativeLayout) findViewById(R.id.item_account_security_account_info);
        itemSecurityBindWeChat = (RelativeLayout) findViewById(R.id.item_account_security_bind_wechat);
        itemSecurityBindQQ = (RelativeLayout) findViewById(R.id.item_account_security_bind_qq);
        itemSecurityBindWeiBo = (RelativeLayout) findViewById(R.id.item_account_security_bind_weibo);
        itemSecurityPhoneTv = (TextView) findViewById(R.id.item_account_security_phone_tv);
        itemSecurityMailTv = (TextView) findViewById(R.id.item_account_security_mail_tv);
        itemSecurityAccountInfoTv = (TextView) findViewById(R.id.item_account_account_info_tv);
        item_bind_wechat = (ToggleButton) findViewById(R.id.item_bind_wechat);
        item_bind_qq = (ToggleButton) findViewById(R.id.item_bind_qq);
        item_bind_weibo = (ToggleButton) findViewById(R.id.item_bind_weibo);

        if (LanguageUtil.isChina()) {
            itemSecurityBindWeChat.setVisibility(View.VISIBLE);
            itemSecurityBindQQ.setVisibility(View.VISIBLE);
            itemSecurityBindWeiBo.setVisibility(View.VISIBLE);
        } else {
            itemSecurityBindWeChat.setVisibility(View.GONE);
            itemSecurityBindQQ.setVisibility(View.GONE);
            itemSecurityBindWeiBo.setVisibility(View.GONE);
        }
    }

    protected void initData() {
        userBean = JSON.parseObject(preference.getCurrentAccountInfo(), UserBean.class);
        if (StringUtil.isNullOrEmpty(userBean.email)){
            itemSecurityMailTv.setText(getResources().getString(R.string.AccountSecurity_Unbind));
        }else {
            itemSecurityMailTv.setText(userBean.email);
        }
        itemSecurityPhoneTv.setText(userBean.phone);

        if (userBean.passSet == 1){
            itemSecurityAccountInfoTv.setVisibility(View.VISIBLE);
        }else {
            itemSecurityAccountInfoTv.setVisibility(View.GONE);
        }
    }

    protected void initListeners() {
        itemSecurityPhone.setOnClickListener(this);
        itemSecurityMail.setOnClickListener(this);
        itemSecurityAccountInfo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.item_account_security_phone:
                if (TextUtils.isEmpty(userBean.phone)){
                    startActivity(new Intent(this, ChangePhoneNumberActivity.class));
                }else {
                    startActivity(new Intent(this, ConfirmPhoneNumberActivity.class));
                }
                break;
            case R.id.item_account_security_mail:
                if (TextUtils.isEmpty(userBean.email)){
                    startActivity(new Intent(this, BindMailBoxActivity.class));
                }else {
                    startActivity(new Intent(this, ConfirmOldMailActivity.class));
                }
                break;
            case R.id.item_account_security_account_info:
                Intent intent = new Intent();
//                intent.setClass(this, ChangePasswordActivity.class);
//                intent.putExtra("type", ChangePasswordActivity.CHANGE_USER_PWD);
//                startActivityForResult(intent, REQUEST_CODE_RESET_PWD);
                intent.setClass(this, ChangePasswordConfirmActivity.class);
                startActivityForResult(intent, REQUEST_CODE_RESET_PWD);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RESET_PWD) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
            }
        }
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    private void doBind(String openId, String unionId, final int thirdType, String uId, int partnerId){
        ssoApiUnit.doUserCenterThirdBind(openId, unionId, thirdType, uId, new SsoApiUnit.SsoApiCommonListener<RegisterPhoneBean>() {
            @Override
            public void onSuccess(RegisterPhoneBean bean) {
                progressDialogManager.dimissDialog(BIND, 0);
                setCheck(currentBindType, true);
                ToastUtil.show( R.string.Bind_Success);
            }

            @Override
            public void onFail(int code, String msg) {
                setCheck(thirdType, false);
                progressDialogManager.dimissDialog(BIND, 0);
                ToastUtil.show(msg);
            }
        });
    }

    private void doUnbind(final int type){
        progressDialogManager.showDialog(UNBIND, this, null, null, getResources().getInteger(R.integer.http_timeout));
        ssoApiUnit.doUserCenterThirdUnbind(type, ApiConstant.getUserID(), new SsoApiUnit.SsoApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                progressDialogManager.dimissDialog(UNBIND, 0);
                ToastUtil.show(R.string.GatewaySetts_Disbind_Success);
            }

            @Override
            public void onFail(int code, String msg) {
                setCheck(type, true);
                progressDialogManager.dimissDialog(UNBIND, 0);
            }
        });
    }

    private void doGetBindInfo(){
        progressDialogManager.showDialog(GET_BIND_INFO, this, null, null, getResources().getInteger(R.integer.http_timeout));
        ssoApiUnit.doUserCenterThirdGet(userBean.uId, new SsoApiUnit.SsoApiCommonListener<AccountBindInfoBean>() {
            @Override
            public void onSuccess(AccountBindInfoBean bean) {
                if (bean.userThirds != null){
                    for (AccountBindInfoBean.BindInfo info: bean.userThirds){
                        setCheck(info.thirdType, true);
                    }
                }
                item_bind_wechat.setOnCheckedChangeListener(AccountSecurityActivity.this);
                item_bind_qq.setOnCheckedChangeListener(AccountSecurityActivity.this);
                item_bind_weibo.setOnCheckedChangeListener(AccountSecurityActivity.this);
                progressDialogManager.dimissDialog(GET_BIND_INFO, 0);
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(GET_BIND_INFO, 0);
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int type = 1;
        String packageName = null;
        String errorMsg = null;
        SHARE_MEDIA media = SHARE_MEDIA.WEIXIN;
        switch (buttonView.getId()){
            case R.id.item_bind_wechat:
                type = 1;
                packageName = WEIXIN_PACKAGE_NAME;
                media = SHARE_MEDIA.WEIXIN;
                errorMsg = this.getString(R.string.Login_Wechat_Login);
                break;
            case R.id.item_bind_qq:
                type = 2;
                packageName = QQ_PACKAGE_NAME;
                media = SHARE_MEDIA.QQ;
                errorMsg = this.getString(R.string.Login_QQ_Login);
                break;
            case R.id.item_bind_weibo:
                type = 3;
                packageName = WEIBO_PACKAGE_NAME;
                media = SHARE_MEDIA.SINA;
                errorMsg = this.getString(R.string.Login_WeiBo_Login);
                break;
        }
        if (isChecked){
            if (isThirdPaatyInstalled(packageName)) {
                currentBindType = type;
                isBind = true;
                loginFromThirdParty(media);
            } else {
                ToastUtil.show(R.string.Third_Party_Not_Installed);
                setCheck(type, false);
            }
        }else {
            showUnbindDialog(type);
        }
    }

    //通过包名判断是否安装第三方软件
    private boolean isThirdPaatyInstalled(String packageName) {
        final PackageManager packageManager = this.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void loginFromThirdParty(SHARE_MEDIA thirdPartyType) {
        if (!thirdPartyType.toString().equals(SHARE_MEDIA.GENERIC.toString())) {
            SnsPlatform platform = thirdPartyType.toSnsPlatform();
            progressDialogManager.showDialog(BIND, AccountSecurityActivity.this, null, null, 300000);
            //获取授权
            //每次先调用撤销授权，否则微信无法每次都弹出授权页面
            UMShareAPI.get(this).deleteOauth(this, platform.mPlatform, null);
            UMShareAPI.get(this).doOauthVerify(this, platform.mPlatform, authListener);
        }
    }

    UMAuthListener authListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            //获取授权成功后，获取个人信息
            UMShareAPI.get(AccountSecurityActivity.this).getPlatformInfo(AccountSecurityActivity.this, platform.toSnsPlatform().mPlatform, authInfoListener);
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            if (platform == SHARE_MEDIA.WEIXIN) {
                setCheck(1, false);
            } else if (platform == SHARE_MEDIA.QQ) {
                setCheck(2, false);
            } else if (platform == SHARE_MEDIA.SINA) {
                setCheck(3, false);
            }
            progressDialogManager.dimissDialog(BIND, 0);
            ToastUtil.show(t.getMessage());
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            if (platform == SHARE_MEDIA.WEIXIN) {
                setCheck(1, false);
            } else if (platform == SHARE_MEDIA.QQ) {
                setCheck(2, false);
            } else if (platform == SHARE_MEDIA.SINA) {
                setCheck(3, false);
            }
            progressDialogManager.dimissDialog(BIND, 0);
        }
    };

    UMAuthListener authInfoListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            final ThirdPartyBean thirdPartyData = getThirdPartyData(platform, data);
//            preference.saveCurrentAccountID("");
//            preference.saveThirdPartyType(thirdPartyData.getPartnerId());
//            preference.saveThirdPartyUid(thirdPartyData.getOpenId());
            doBind(thirdPartyData.getOpenId(), thirdPartyData.getUnionid(), thirdPartyData.getPartnerId(), ApiConstant.getUserID(), thirdPartyData.getPartnerId());
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            if (platform == SHARE_MEDIA.WEIXIN) {
                setCheck(1, false);
            } else if (platform == SHARE_MEDIA.QQ) {
                setCheck(2, false);
            } else if (platform == SHARE_MEDIA.SINA) {
                setCheck(3, false);
            }
            progressDialogManager.dimissDialog(BIND, 0);
            ToastUtil.show(t.getMessage());
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            if (platform == SHARE_MEDIA.WEIXIN) {
                setCheck(1, false);
            } else if (platform == SHARE_MEDIA.QQ) {
                setCheck(2, false);
            } else if (platform == SHARE_MEDIA.SINA) {
                setCheck(3, false);
            }
            progressDialogManager.dimissDialog(BIND, 0);
        }
    };

    private ThirdPartyBean getThirdPartyData(SHARE_MEDIA platform, Map<String, String> data) {
        int thirdPartyType = 1;
        if (platform == SHARE_MEDIA.WEIXIN) {
            thirdPartyType = 1;
        } else if (platform == SHARE_MEDIA.QQ) {
            thirdPartyType = 2;
        } else if (platform == SHARE_MEDIA.SINA) {
            thirdPartyType = 3;
        }

        String openId = data.get("openid");
        if (TextUtils.isEmpty(openId)) {
            openId = data.get("uid");
        }

        String iconUrl = "";
        if (!StringUtil.isNullOrEmpty(data.get("iconurl"))) {
            iconUrl = data.get("iconurl");
        }

        String nick = data.get("screen_name");

        int gender = 0;
        String genderStr = data.get("gender");
        if (genderStr.equals("男")) {
            gender = 0;
        } else if (genderStr.equals("女")) {
            gender = 1;
        }

        String unionid = data.get("unionid");

        ThirdPartyBean thirdPartyData = new ThirdPartyBean(thirdPartyType, openId, unionid, iconUrl, nick, gender, true);
        return thirdPartyData;
    }

    private void showUnbindDialog(final int type) {
        String msg = getString(R.string.AccountSecurity_Rebunding_Wechat);
        if (type == 1){
            msg = getString(R.string.AccountSecurity_Rebunding_Wechat);
        }else if (type == 2){
            msg = getString(R.string.AccountSecurity_Rebunding_QQ);
        }else if (type == 3){
            msg = getString(R.string.AccountSecurity_Rebunding_Weibo);
        }
        builder = new WLDialog.Builder(this);
        builder.setTitle(getString(R.string.AccountSecurity_Rebunding_Third_PartyLogin)).
                setCancelOnTouchOutSide(false)
                .setMessage(msg)
                .setPositiveButton(getString(R.string.AuthManager_UnBind))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        doUnbind(type);
                    }

                    @Override
                    public void onClickNegative(View view) {
                        setCheck(type, true);
                    }
                });
        dialog = builder.create();
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    private void setCheck(int type, boolean ischeck){
        switch (type){
            case 1:
                item_bind_wechat.setOnCheckedChangeListener(null);
                item_bind_wechat.setChecked(ischeck);
                item_bind_wechat.setOnCheckedChangeListener(AccountSecurityActivity.this);
                break;
            case 2:
                item_bind_qq.setOnCheckedChangeListener(null);
                item_bind_qq.setChecked(ischeck);
                item_bind_qq.setOnCheckedChangeListener(AccountSecurityActivity.this);
                break;
            case 3:
                item_bind_weibo.setOnCheckedChangeListener(null);
                item_bind_weibo.setChecked(ischeck);
                item_bind_weibo.setOnCheckedChangeListener(AccountSecurityActivity.this);
                break;
        }
    }
}