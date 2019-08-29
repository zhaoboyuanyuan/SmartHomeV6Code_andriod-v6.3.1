package cc.wulian.smarthomev6.main.message.setts;

import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.UserPushInfo;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.message.adapter.MessageSettingsAdapter;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

public class MessageSettingsActivity extends BaseTitleActivity {

    //    private ToggleButton mToggleButton;
    private RecyclerView mRecyclerView;
//    private View mViewToggle;

    private MessageSettingsAdapter mAdapter;
    private DeviceApiUnit mDeviceApi;
    private TextView mTextNoResult;
    private List<UserPushInfo.UserPushInfoBean> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_settings, true);

//        mToggleButton = findView(R.id.message_settings_toggle);
        mRecyclerView = findView(R.id.message_settings_recycler);
        mTextNoResult = (TextView) findViewById(R.id.alarm_text_no_result);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        mViewToggle = findView(R.id.view_back_push);

//        mToggleButton.setChecked(preference.getAlarmPush());

        mDeviceApi = new DeviceApiUnit(this);
        mAdapter = new MessageSettingsAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, 0, 0, 3);
            }
        });

//        mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                setPushType(isChecked);
//            }
//        });

//        mViewToggle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setPushType(! mToggleButton.isChecked());
//            }
//        });

//        getSetts();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(preference.getCurrentGatewayID())) {
            getHouseKeeperAlarmSetts();
        }
        getSetts();
    }

    @Override
    protected void initTitle() {
        super.initTitle();

        setToolBarTitle(getString(R.string.AccountSecurity_Messagemanagement));
    }

    private void getHouseKeeperAlarmSetts() {
        mDeviceApi.doQueryDevicePushSetts(preference.getCurrentGatewayID(), "3", "1", new DeviceApiUnit.DeviceApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                list.clear();
                UserPushInfo userPushInfo = (UserPushInfo) bean;
                if (!TextUtils.isEmpty(Preference.getPreferences().getCurrentGatewayID())) {
                    if (bean == null || ((UserPushInfo) bean).userPushInfo.size() == 0) {
                        list.add(new UserPushInfo.UserPushInfoBean("houseKeeper", 0, getString(R.string.Mine_set)));
                    } else {
                        list.add(new UserPushInfo.UserPushInfoBean("houseKeeper", userPushInfo.userPushInfo.get(0).pushFlag, getString(R.string.Mine_set)));
                    }

                }
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.single(msg);
            }
        });
    }

    private void getSetts() {
        progressDialogManager.showDialog("query", this, "", null, 5000);
        mDeviceApi.doQueryDevicePushSetts(preference.getCurrentGatewayID(), "2", "1", new DeviceApiUnit.DeviceApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                UserPushInfo userPushInfo = (UserPushInfo) bean;
                if (!((UserPushInfo) bean).userPushInfo.isEmpty()) {
                    showHasResult(true);
                    list.addAll(userPushInfo.userPushInfo);
                }
                progressDialogManager.dimissDialog("query", 0);
                mAdapter.setData(list);

            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog("query", 0);
                ToastUtil.single(msg);
//                showHasResult(false);
            }
        });
    }


    private void showHasResult(boolean hasResult) {
        if (!hasResult) {
            mTextNoResult.setVisibility(View.VISIBLE);
            ObjectAnimator.ofFloat(mTextNoResult, "alpha", 0f, 1f).setDuration(700).start();

            ObjectAnimator.ofFloat(mRecyclerView, "alpha", 1f, 0f).setDuration(700).start();
            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRecyclerView.setVisibility(View.INVISIBLE);
                }
            }, 700);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
//            ObjectAnimator.ofFloat(mRecyclerView, "alpha", 0f, 1f).setDuration(700).start();

            ObjectAnimator.ofFloat(mTextNoResult, "alpha", 1f, 0f).setDuration(700).start();
            mTextNoResult.setVisibility(View.INVISIBLE);
            /*mTextNoResult.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mTextNoResult.setVisibility(View.INVISIBLE);
                }
            }, 700);*/
        }
    }

//    private void setPush(boolean isPush) {
//        preference.saveAlarmPush(isPush);
//        mToggleButton.setChecked(isPush);
//        showRecycler(isPush);
//    }

//    private void showRecycler(boolean s) {
//        if (s) {
//            mRecyclerView.setVisibility(View.VISIBLE);
//            ObjectAnimator.ofFloat(mRecyclerView, "alpha", 0f, 1f).setDuration(700).start();
//        } else {
//            ObjectAnimator.ofFloat(mRecyclerView, "alpha", 1f, 0f).setDuration(700).start();
//            mRecyclerView.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mRecyclerView.setVisibility(View.INVISIBLE);
//                }
//            }, 700);
//        }
//    }

//    private void setPushType(final boolean isPush){
//        if (StringUtil.equals(preference.getUserEnterType(), "account")){
//            progressDialogManager.showDialog(TAG, this, null, null, getResources().getInteger(R.integer.http_timeout));
//            mDeviceApi.doIsPush(isPush, preference.getCurrentGatewayID(), new DeviceApiUnit.DeviceApiCommonListener() {
//                @Override
//                public void onSuccess(Object bean) {
//                    setPush(isPush);
//                    progressDialogManager.dimissDialog(TAG, 0);
//                }
//
//                @Override
//                public void onFail(int code, String msg) {
//                    progressDialogManager.dimissDialog(TAG, 0);
//                }
//            });
//        }else {
//            preference.saveAlarmPush(isPush);
//            mToggleButton.setChecked(isPush);
//        }
//
//    }
}
