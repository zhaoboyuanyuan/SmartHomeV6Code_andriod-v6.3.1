package cc.wulian.smarthomev6.main.message;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.message.alarm.MessageAlarmListActivity;
import cc.wulian.smarthomev6.main.message.log.MessageLogListActivity;
import cc.wulian.smarthomev6.main.message.setts.MessageSettingsActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.MessageCountBean;
import cc.wulian.smarthomev6.support.customview.BadgeView2;
import cc.wulian.smarthomev6.support.event.AlarmUpdateEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.WLog;

public class MessageCenterNewActivity extends BaseTitleActivity {

    private TextView mTextAlarm;
    private View mViewLogNode;
    private BadgeView2 mBadgeAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center_new, true);

        EventBus.getDefault().register(this);

        mTextAlarm = (TextView) findViewById(R.id.message_center_text_alarm);
        mViewLogNode = findViewById(R.id.view_log_node);
        mBadgeAlarm = new BadgeView2(this);
        mBadgeAlarm.setTargetView(mTextAlarm);
        mBadgeAlarm.setBadgeGravity(Gravity.TOP | Gravity.END);

        findViewById(R.id.view_back_alarm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessageCenterNewActivity.this, MessageAlarmListActivity.class));
            }
        });
        findViewById(R.id.view_back_log).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessageCenterNewActivity.this, MessageLogListActivity.class));
            }
        });

        setAlarmCount();
        setLogCount();
        getAlarmCount();
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Message_Center_MessageCenter));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }


    private void getAlarmCount() {
        DataApiUnit apiUnit = new DataApiUnit(this);
        apiUnit.deGetAlarmCount(Preference.getPreferences().getCurrentGatewayID(),
                null,
                "1",
                "1",
                new DataApiUnit.DataApiCommonListener() {
                    @Override
                    public void onSuccess(Object bean) {
                        WLog.i("Message", "onSuccess: " + bean);
                        MessageCountBean messageCountBean = (MessageCountBean) bean;

                        MainApplication.getApplication().getAlarmCountCache().setAlarmCount(messageCountBean);

                        setAlarmCount();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        WLog.i("Message", "onFail: ");
                    }
                });
        apiUnit.deGetAlarmCount(Preference.getPreferences().getCurrentGatewayID(),
                null,
                "1",
                "2",
                new DataApiUnit.DataApiCommonListener() {
                    @Override
                    public void onSuccess(Object bean) {
                        WLog.i("Message", "onSuccess: " + bean);
                        MessageCountBean messageCountBean = (MessageCountBean) bean;
                        MainApplication.getApplication().getAlarmCountCache().setLogCount(messageCountBean);

                        setLogCount();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        WLog.i("Message", "onFail: ");
                    }
                });
    }

    private void setAlarmCount() {
        // 调整未读消息，和当前置顶消息
        mBadgeAlarm.setBadgeCount(MainApplication.getApplication().getAlarmCountCache().getAlarmTotalCount());
    }

    private void setLogCount() {
        // 调整未读消息，和当前置顶消息
        mViewLogNode.setVisibility(MainApplication.getApplication().getAlarmCountCache().getLogTotalCount() != 0 ? View.VISIBLE : View.INVISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(AlarmUpdateEvent event) {
        setAlarmCount();
        setLogCount();
    }
}
