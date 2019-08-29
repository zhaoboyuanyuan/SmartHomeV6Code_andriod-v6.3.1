package cc.wulian.smarthomev6.main.mine.gatewaycenter;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.mine.gatewaycenter.adapter.SearchAccountAdapter;
import cc.wulian.smarthomev6.support.core.apiunit.AuthApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.UserApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.UserBean;
import cc.wulian.smarthomev6.support.tools.RegularTool;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by mamengchao on 2017/2/28 0028.
 * Tips:授权用户页
 */

public class AuthAccountActivity extends BaseTitleActivity {

    private static final String SEARCH = "SEARCH";
    private static final String AUTH = "AUTH";
    private EditText searchAccountEditText;
    private Button searchAccountButton;
    private ListView accountListView;
    private LinearLayout authLayout;
    private Button authGatewayButton;
    private SearchAccountAdapter adapter;
    private List<UserBean> infos;
    private WLDialog.Builder builder;
    private WLDialog dialog;

    private UserApiUnit userApiUnit;
    private AuthApiUnit authApiUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_account, true);
        userApiUnit = new UserApiUnit(this);
        authApiUnit = new AuthApiUnit(this);
    }

    @Override
    protected void initTitle() {
        setToolBarTitle(getString(R.string.AuthUsers_Authorize));
    }

    @Override
    protected void initView() {
        searchAccountEditText = (EditText) findViewById(R.id.search_account_editText);
        searchAccountButton = (Button) findViewById(R.id.search_account_button);
        accountListView = (ListView) findViewById(R.id.account_listView);
        authLayout = (LinearLayout) findViewById(R.id.auth_layout);
        authGatewayButton = (Button) findViewById(R.id.auth_current_gateway_button);
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
        infos = new ArrayList<>();
        adapter = new SearchAccountAdapter(this, infos);
        accountListView.setAdapter(adapter);
    }

    @Override
    protected void initListeners() {
        searchAccountButton.setOnClickListener(this);
        authGatewayButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.search_account_button:
                searchAccount();
                break;
            case R.id.auth_current_gateway_button:
                if (infos != null){
                    authAccount(infos.get(0).uId);
                }
                break;
            default:
                break;
        }
    }

    private void searchAccount(){
        String id = searchAccountEditText.getText().toString();
        if (StringUtil.isNullOrEmpty(id)){
            showNoneDialog();
            return;
        }
        if (!(RegularTool.isLegalChinaPhoneNumber(id) || RegularTool.isLegalEmailAddress(id))) {
            ToastUtil.show(getString(R.string.AccountSecurity_tips));
            return;
        }
        progressDialogManager.showDialog(SEARCH, this, null, null, getResources().getInteger(R.integer.http_timeout));
        userApiUnit.doSearchUser(id, new UserApiUnit.UserApiCommonListener<UserBean>() {
            @Override
            public void onSuccess(UserBean userBean) {
                progressDialogManager.dimissDialog(SEARCH, 0);
                if (TextUtils.isEmpty(userBean.uId)){
                    ToastUtil.show(getString(R.string.AuthUsers_NoUser));
                }else {
                    infos.clear();
                    infos.add(userBean);
                    adapter.swapData(infos);
                    authLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(SEARCH, 0);
                ToastUtil.show(msg);
                infos.clear();
                adapter.swapData(infos);
                authLayout.setVisibility(View.GONE);
                showNoneDialog();
            }
        });
    }

    private void authAccount(String granteeUid){
        String currentGateway = preference.getCurrentGatewayID();
        progressDialogManager.showDialog(AUTH, this, null, null, getResources().getInteger(R.integer.http_timeout));
        authApiUnit.doAuthAccount(currentGateway, granteeUid, new AuthApiUnit.AuthApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                progressDialogManager.dimissDialog(AUTH, 0);
                ToastUtil.show(R.string.AuthAccountActivity_Auth_Success);
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(AUTH, 0);
                ToastUtil.show(msg);
            }
        });
    }

    private void showNoneDialog(){
        builder = new WLDialog.Builder(AuthAccountActivity.this);
        builder.setTitle(getString(R.string.Tip_Warm))
                .setCancelOnTouchOutSide(false)
                .setMessage(getString(R.string.AuthUsers_NoUser))
                .setPositiveButton(AuthAccountActivity.this.getResources().getString(R.string.Sure))
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
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }
}
