package cc.wulian.smarthomev6.main.mine.sharedevice;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.WLBaseAdapter;
import cc.wulian.smarthomev6.support.core.apiunit.AuthApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.UserBean;
import cc.wulian.smarthomev6.support.customview.swipemenu.SwipeMenu;
import cc.wulian.smarthomev6.support.customview.swipemenu.SwipeMenuAdapter;
import cc.wulian.smarthomev6.support.customview.swipemenu.SwipeMenuCreator;
import cc.wulian.smarthomev6.support.customview.swipemenu.SwipeMenuItem;
import cc.wulian.smarthomev6.support.customview.swipemenu.SwipeMenuLayout;
import cc.wulian.smarthomev6.support.customview.swipemenu.SwipeMenuListView;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.DisplayUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by zbl on 2017/8/24.
 */

public class ShareDeviceAccountListActivity extends BaseTitleActivity {
    private static final String DATA_DEVICE = "DATA_DEVICE";
    private static final String GET_DATA = "GET_DATA";
    private static final String CANCEL_SHARE = "CANCEL_SHARE";

    private Context context;
    private SwipeMenuListView mListView;
    private View layout_nodata;
    private AccountListAdapter adapter;
    private AuthApiUnit authApiUnit;
    private List<UserBean> userBeanList = new ArrayList<>();
    private WLDialog dialog;

    private DeviceBean deviceBean;

    public static void start(Context context, DeviceBean device) {
        Intent intent = new Intent(context, ShareDeviceAccountListActivity.class);
        intent.putExtra(DATA_DEVICE, device);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_sharedevice_main, true);
    }

    @Override
    protected void initTitle() {
        setToolBarTitle(getString(R.string.Share_List));
        setToolBarTitleAndRightImg(getString(R.string.Share_List), R.drawable.icon_add);
    }

    @Override
    protected void initView() {
        mListView = (SwipeMenuListView) findViewById(R.id.listView);
        layout_nodata = findViewById(R.id.layout_nodata);
        adapter = new AccountListAdapter(this, userBeanList);
        adapter.setMenuCreator(creatLeftDeleteItem());
        adapter.setOnMenuItemClickListener(new SwipeMenuAdapter.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                UserBean userBean = adapter.getData().get(position);
                showCancelDialog(userBean.uId);
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (deviceBean.isGateway()) {
                    UserBean userBean = adapter.getData().get(position);
                    ShareDeviceChildListActivity.start(ShareDeviceAccountListActivity.this, deviceBean, userBean);
                }
            }
        });
        mListView.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        deviceBean = getIntent().getParcelableExtra(DATA_DEVICE);
        if (deviceBean == null || TextUtils.isEmpty(deviceBean.deviceId)) {
            return;
        }
        authApiUnit = new AuthApiUnit(this);
        getData();
    }

    private void getData() {
        ProgressDialogManager.getDialogManager().showDialog(GET_DATA, this, null, null, getResources().getInteger(R.integer.http_timeout));
        authApiUnit.doGetAllAuthAccount(deviceBean.deviceId, new AuthApiUnit.AuthApiCommonListener<List<UserBean>>() {
            @Override
            public void onSuccess(List<UserBean> bean) {
                userBeanList.clear();
                userBeanList.addAll(bean);
                adapter.swapData(userBeanList);
                if (userBeanList.size() > 0) {
                    layout_nodata.setVisibility(View.GONE);
                } else {
                    layout_nodata.setVisibility(View.VISIBLE);
                }
                ProgressDialogManager.getDialogManager().dimissDialog(GET_DATA, 0);
            }

            @Override
            public void onFail(int code, String msg) {
                ProgressDialogManager.getDialogManager().dimissDialog(GET_DATA, 0);
                ToastUtil.show(msg);
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.img_right:
                ShareDeviceSearchAccountActivity.start(this, deviceBean, 1);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {//新增了分享用户
            getData();
        }
    }


    private void showCancelDialog(final String uid) {
        WLDialog.Builder builder = new WLDialog.Builder(this);
        builder.setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setMessage(getString(R.string.Sure_Cancel_Share))
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        cancelShare(uid);
                        dialog.dismiss();
                    }

                    @Override
                    public void onClickNegative(View view) {
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
        dialog.show();
    }

    /**
     * 取消分享
     */
    private void cancelShare(String granteeUid) {
        ProgressDialogManager.getDialogManager().showDialog(CANCEL_SHARE, this, null, null, getResources().getInteger(R.integer.http_timeout));
        authApiUnit.doUnAuthAccount(deviceBean.deviceId, granteeUid, new AuthApiUnit.AuthApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                ProgressDialogManager.getDialogManager().dimissDialog(CANCEL_SHARE, 0);
                getData();
            }

            @Override
            public void onFail(int code, String msg) {
                ProgressDialogManager.getDialogManager().dimissDialog(CANCEL_SHARE, 0);
                ToastUtil.show(msg);
            }
        });
    }

    class AccountListAdapter extends SwipeMenuAdapter<UserBean> {
        private LayoutInflater mInflater;

        public AccountListAdapter(Context context, List<UserBean> data) {
            super(context, data);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                View view = mInflater.inflate(R.layout.item_share_account_list, null);
                holder.tvAccountName = (TextView) view.findViewById(R.id.tv_account_name);
                holder.tvAccountId = (TextView) view.findViewById(R.id.tv_account_id);
                convertView = createMenuView(position, parent, view);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ((SwipeMenuLayout) convertView).setPosition(position);
            UserBean userBean = mData.get(position);
            if (TextUtils.isEmpty(userBean.nick)) {
                holder.tvAccountName.setText(userBean.phone);
            } else {
                holder.tvAccountName.setText(userBean.nick);
            }

            holder.tvAccountId.setText(userBean.phone);

            return convertView;
        }
    }

    public final class ViewHolder {
        public TextView tvAccountName;
        public TextView tvAccountId;
    }

    /**
     * 创建左划删除item样式
     */
    private SwipeMenuCreator creatLeftDeleteItem() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu, int position) {
                SwipeMenuItem settingItem = new SwipeMenuItem(context);
                settingItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                settingItem.setWidth(DisplayUtil.dip2Pix(context, 90));
                settingItem.setTitle(R.string.Cancel_Share);
                settingItem.setTitleSize(DisplayUtil.dip2Sp(context, 5));
                settingItem.setTitleColor(context.getResources().getColor(R.color.white));
                menu.addMenuItem(settingItem);
            }
        };
        return creator;
    }
}
