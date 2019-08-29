package cc.wulian.smarthomev6.main.mine.platform;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.h5.CommonH5Activity;
import cc.wulian.smarthomev6.main.mine.platform.whiterobot.WhiteRobotAuthActivity;
import cc.wulian.smarthomev6.main.mine.platform.whiterobot.WhiteRobotUnauthActivity;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.ControlPlatformApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.GrantRelationBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.PlatformBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.PlatformListBean;
import cc.wulian.smarthomev6.support.tools.HttpUrlKey;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by zbl on 2017/12/19.
 * 第三方控制平台界面
 */

public class ControlPlatformActivity extends BaseTitleActivity {
    private static final String GET_DATA = "GET_DATA";
    private static final int LOCALTYPE_WHITE_ROBOT = 0xff;

    private ListView lv_platforms;
    private DataAdapter adapter;
    private LayoutInflater inflater;

    private ControlPlatformApiUnit controlPlatformApiUnit;

    private ArrayList<PlatformBean> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controlPlatformApiUnit = new ControlPlatformApiUnit(this);
        inflater = LayoutInflater.from(this);
        setContentView(R.layout.activity_control_platform, true);
    }

    @Override
    protected void initTitle() {
        setToolBarTitle(getString(R.string.ThirdPartyPlatform));
    }

    @Override
    protected void initView() {
        lv_platforms = (ListView) findViewById(R.id.lv_platforms);
        adapter = new DataAdapter();
        lv_platforms.setAdapter(adapter);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        lv_platforms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlatformBean bean = dataList.get(position);
                if (bean.localType == LOCALTYPE_WHITE_ROBOT) {
                    startWhiteRobot();
                } else {
                    CommonH5Activity.start(ControlPlatformActivity.this, bean.h5url, bean.name);
                }
            }
        });
    }

    @Override
    protected void initData() {
        dataList.clear();
        if (LanguageUtil.isChina()) {
            PlatformBean bean = new PlatformBean();
            bean.name = getString(R.string.Small_White);
            bean.localType = LOCALTYPE_WHITE_ROBOT;
            bean.type = "6";
            dataList.add(bean);
            adapter.notifyDataSetChanged();
        }
        ProgressDialogManager.getDialogManager().showDialog(GET_DATA, this, null, null, getResources().getInteger(R.integer.http_timeout));
        controlPlatformApiUnit.doGetPlatformList(new ControlPlatformApiUnit.CommonListener<PlatformListBean>() {
            @Override
            public void onSuccess(PlatformListBean bean) {
                if (bean != null && bean.icps != null) {
                    dataList.addAll(bean.icps);
                    adapter.notifyDataSetChanged();
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

    private void startWhiteRobot() {
        ProgressDialogManager.getDialogManager().showDialog(GET_DATA, this, null, null, getResources().getInteger(R.integer.http_timeout));
        controlPlatformApiUnit.doGetAuthStatus(ApiConstant.getUserID(), ApiConstant.WHITEROBOT_CLIEND_ID, new ControlPlatformApiUnit.CommonListener<GrantRelationBean>() {
            @Override
            public void onSuccess(GrantRelationBean bean) {
                ProgressDialogManager.getDialogManager().dimissDialog(GET_DATA, 0);
                if (bean.isGrant()) {
                    Intent intent = new Intent(ControlPlatformActivity.this, WhiteRobotUnauthActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ControlPlatformActivity.this, WhiteRobotAuthActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFail(int code, String msg) {
                ProgressDialogManager.getDialogManager().dimissDialog(GET_DATA, 0);
                ToastUtil.show(msg);
            }
        });

    }

    private class DataAdapter extends BaseAdapter {

        private class ViewHolder {
            public TextView tv_name;
            public ImageView iv_icon;
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_common_action_list, null);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            PlatformBean bean = dataList.get(position);
            holder.tv_name.setText(bean.name);
            setIconByType(dataList.get(position).type, holder);
            return convertView;
        }

        private void setIconByType(String type, ViewHolder holder) {
            if(TextUtils.isEmpty(type)){
                return;
            }
            switch (type) {
                case "0":
                    holder.iv_icon.setImageResource(R.drawable.icon_echo);
                    break;
                case "1":
                case "5":
                    holder.iv_icon.setImageResource(R.drawable.icon_ruoqi);
                    break;
                case "2":
                    holder.iv_icon.setImageResource(R.drawable.icon_dingdong);
                    break;
                case "3":
                    holder.iv_icon.setImageResource(R.drawable.icon_tianmao);
                    break;
                case "4":
                    holder.iv_icon.setImageResource(R.drawable.icon_xiaowei);
                    break;
                case "6":
                    holder.iv_icon.setImageResource(R.drawable.icon_xiaobai);
                    break;
                case "7":
                    holder.iv_icon.setImageResource(R.drawable.icon_xiaodu);
                    break;
                case "8":
                    holder.iv_icon.setImageResource(R.drawable.icon_google_home);
                    break;
            }
        }
    }
}
