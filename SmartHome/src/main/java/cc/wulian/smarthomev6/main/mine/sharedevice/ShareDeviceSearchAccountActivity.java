package cc.wulian.smarthomev6.main.mine.sharedevice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.mine.gatewaycenter.adapter.SearchAccountAdapter;
import cc.wulian.smarthomev6.support.core.apiunit.AuthApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.UserApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.UserBean;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.tools.RegularTool;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * 添加分享用户界面
 */

public class ShareDeviceSearchAccountActivity extends BaseTitleActivity {

    private static final String DATA_DEVICE = "DATA_DEVICE";
    private static final String SEARCH = "SEARCH";
    private static final String SHARE_DEVICE_TO_ACCOUNT = "SHARE_DEVICE_TO_ACCOUNT";

    private static final int REQUESTCODE_CHILDDEVICE = 1;

    private EditText searchAccountEditText;
    private Button searchAccountButton;
    private ListView accountListView;
    private LinearLayout authLayout;
    private View layout_childdevices;
    private Button authGatewayButton;
    private TextView tv_share_gateway_tip;
    private ImageView btn_clear;
    private SearchAccountAdapter adapter;
    private List<UserBean> infos;
    private WLDialog dialog, shareDialog;

    private UserApiUnit userApiUnit;
    private AuthApiUnit authApiUnit;

    private DeviceBean deviceBean;

    private ArrayList<String> grantChildDevices;
    private ArrayList<String> unGrantChildDevices;

    public static void start(Activity activity, DeviceBean device, int requestCode) {
        Intent intent = new Intent(activity, ShareDeviceSearchAccountActivity.class);
        intent.putExtra(DATA_DEVICE, device);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharedevice_search_account, true);
        userApiUnit = new UserApiUnit(this);
        authApiUnit = new AuthApiUnit(this);
    }

    @Override
    protected void initTitle() {
        setToolBarTitle(getString(R.string.Add_Share_User));
    }

    @Override
    protected void initView() {
        searchAccountEditText = (EditText) findViewById(R.id.search_account_editText);
        searchAccountButton = (Button) findViewById(R.id.search_account_button);
        accountListView = (ListView) findViewById(R.id.account_listView);
        authLayout = (LinearLayout) findViewById(R.id.auth_layout);
        layout_childdevices = findViewById(R.id.layout_childdevices);
        authGatewayButton = (Button) findViewById(R.id.auth_current_gateway_button);
        tv_share_gateway_tip = (TextView) findViewById(R.id.tv_share_gateway_tip);
        btn_clear = (ImageView) findViewById(R.id.btn_clear);
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(authGatewayButton, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(authGatewayButton, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    @Override
    protected void initData() {
        deviceBean = getIntent().getParcelableExtra(DATA_DEVICE);
        if (deviceBean != null) {
            if (deviceBean.isGateway()) {
                tv_share_gateway_tip.setVisibility(View.VISIBLE);
            } else {
                tv_share_gateway_tip.setVisibility(View.GONE);
            }
        }

        infos = new ArrayList<>();
        adapter = new SearchAccountAdapter(this, infos);
        accountListView.setAdapter(adapter);
    }

    @Override
    protected void initListeners() {
        searchAccountButton.setOnClickListener(this);
        authGatewayButton.setOnClickListener(this);
        layout_childdevices.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
        searchAccountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchAccountEditText.getText().length() > 0) {
                    btn_clear.setVisibility(View.VISIBLE);
                } else {
                    btn_clear.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.search_account_button:
                searchAccount();
                break;
            case R.id.auth_current_gateway_button:
                if (infos != null) {
                    showShareDialog();
                }
                break;
            case R.id.btn_clear:
                searchAccountEditText.setText("");
                break;
            case R.id.layout_childdevices:
                ShareDeviceChildListActivity.startForResult(this, deviceBean, infos.get(0), grantChildDevices, REQUESTCODE_CHILDDEVICE);
                break;
            default:
                break;
        }
    }

    private void searchAccount() {
        String id = searchAccountEditText.getText().toString();
        if (TextUtils.isEmpty(id)) {
            showNoneDialog();
            return;
        }
        if (!(RegularTool.isLegalChinaPhoneNumber(id) || RegularTool.isLegalEmailAddress(id))) {
            ToastUtil.show(R.string.AccountSecurity_tips);
            return;
        }
        progressDialogManager.showDialog(SEARCH, this, null, null, getResources().getInteger(R.integer.http_timeout));
        userApiUnit.doSearchUser(id, new UserApiUnit.UserApiCommonListener<UserBean>() {
            @Override
            public void onSuccess(UserBean userBean) {
                progressDialogManager.dimissDialog(SEARCH, 0);
                if (TextUtils.isEmpty(userBean.uId)) {
                    ToastUtil.show(R.string.AuthUsers_NoUser);
                } else {
                    infos.clear();
                    infos.add(userBean);
                    adapter.swapData(infos);
                    authLayout.setVisibility(View.VISIBLE);
                    if (deviceBean != null) {
                        if (deviceBean.isGateway()) {
                            layout_childdevices.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(SEARCH, 0);
                ToastUtil.show(msg);
                infos.clear();
                adapter.swapData(infos);
                authLayout.setVisibility(View.GONE);
                layout_childdevices.setVisibility(View.GONE);
                showNoneDialog();
            }
        });
    }

    private void showShareDialog() {
        if (shareDialog == null) {
            WLDialog.Builder builder = new WLDialog.Builder(ShareDeviceSearchAccountActivity.this);
            UserBean userBean = infos.get(0);
            String user = userBean.userName;
            if (TextUtils.isEmpty(user)) {
                if (!TextUtils.isEmpty(userBean.phone)) {
                    user = userBean.phone;
                } else {
                    user = userBean.email;
                }
            }
            String message = String.format(getString(R.string.Sure_Share_Tip), DeviceInfoDictionary.getNameByTypeAndName(deviceBean.getType(), deviceBean.getName()), user);
            builder.setCancelOnTouchOutSide(false)
                    .setMessage(message)
                    .setPositiveButton(ShareDeviceSearchAccountActivity.this.getResources().getString(R.string.Sure))
                    .setNegativeButton(ShareDeviceSearchAccountActivity.this.getResources().getString(R.string.Cancel))
                    .setListener(new WLDialog.MessageListener() {
                        @Override
                        public void onClickPositive(View view, String msg) {
                            authAccount(infos.get(0).uId);
                            shareDialog.dismiss();
                            shareDialog = null;
                        }

                        @Override
                        public void onClickNegative(View var1) {
                            shareDialog = null;
                        }

                    });
            shareDialog = builder.create();
        }
        if (!shareDialog.isShowing()) {
            shareDialog.show();
        }
    }

    private void authAccount(String granteeUid) {
        progressDialogManager.showDialog(SHARE_DEVICE_TO_ACCOUNT, this, null, null, getResources().getInteger(R.integer.http_timeout));
        AuthApiUnit.AuthApiCommonListener listener = new AuthApiUnit.AuthApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                progressDialogManager.dimissDialog(SHARE_DEVICE_TO_ACCOUNT, 0);
                ToastUtil.show(R.string.Share_Success);
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(SHARE_DEVICE_TO_ACCOUNT, 0);
                ToastUtil.show(msg);
            }
        };
        if (grantChildDevices == null || unGrantChildDevices == null) {
            authApiUnit.doAuthAccount(deviceBean.deviceId, granteeUid, "2", null, null, null, listener);
        } else {
            authApiUnit.doAuthAccount(deviceBean.deviceId, granteeUid, "1", grantChildDevices, unGrantChildDevices, null, listener);
        }
    }

    private void showNoneDialog() {
        if (dialog == null) {
            WLDialog.Builder builder = new WLDialog.Builder(ShareDeviceSearchAccountActivity.this);
            builder.setTitle(getString(R.string.Tip_Warm))
                    .setCancelOnTouchOutSide(false)
                    .setMessage(getString(R.string.AuthUsers_NoUser))
                    .setPositiveButton(ShareDeviceSearchAccountActivity.this.getResources().getString(R.string.Sure))
                    .setListener(new WLDialog.MessageListener() {
                        @Override
                        public void onClickPositive(View view, String msg) {
                            dialog.dismiss();
                        }

                        @Override
                        public void onClickNegative(View var1) {

                        }

                    });
            dialog = builder.create();
        }
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUESTCODE_CHILDDEVICE && resultCode == RESULT_OK) {
            if (data != null) {
                grantChildDevices = data.getStringArrayListExtra("grantChildDevices");
                unGrantChildDevices = data.getStringArrayListExtra("unGrantChildDevices");
            }
        }
    }
}
