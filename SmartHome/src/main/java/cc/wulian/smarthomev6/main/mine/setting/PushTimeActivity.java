package cc.wulian.smarthomev6.main.mine.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.account.ForgotVerificationActivity;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.customview.wheel.NumericWheelAdapter;
import cc.wulian.smarthomev6.support.customview.wheel.OnWheelChangedListener;
import cc.wulian.smarthomev6.support.customview.wheel.WheelView;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by mamengchao on 2017/8/22 0001.
 * Tips:推送时间选择
 */
public class PushTimeActivity extends BaseTitleActivity {

    private static String SETTINT = "setting";
    private TextView pushTimeStartTextView;
    private LinearLayout itemPushTimeStart;
    private TextView pushTimeEndTextView;
    private LinearLayout itemPushTimeEnd;
    private WheelView timeHour;
    private WheelView timeMin;

    private OnWheelChangedListener onWheelChangedListener;
    private DeviceApiUnit deviceApiUnit;
    private String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceApiUnit = new DeviceApiUnit(this);
        setContentView(R.layout.activity_push_time, true);
    }

    @Override
    protected void initTitle() {
        setToolBarTitleAndRightBtn(getString(R.string.mine_Setting_pushtime), getString(R.string.Sure));
    }

    @Override
    protected void initView() {
        itemPushTimeEnd = (LinearLayout) findViewById(R.id.item_push_time_end);
        pushTimeEndTextView = (TextView) findViewById(R.id.tv_push_time_end);
        itemPushTimeStart = (LinearLayout) findViewById(R.id.item_push_time_start);
        pushTimeStartTextView = (TextView) findViewById(R.id.tv_push_time_start);
        timeHour = (WheelView) findViewById(R.id.time_hour);
        timeMin = (WheelView) findViewById(R.id.time_min);
    }

    @Override
    protected void initData() {
        if (getIntent() != null) {
            time = getIntent().getStringExtra("TIME");
        }

        initTimePicker();
        if (TextUtils.isEmpty(time)){
            pushTimeStartTextView.setText("00:00");
            pushTimeEndTextView.setText("23:59");
        }else {
            String startTime = time.split(",")[0];
            String endTime = time.split(",")[1];
            pushTimeStartTextView.setText(startTime);
            pushTimeEndTextView.setText(endTime);
        }
        resetListener(pushTimeStartTextView);
    }

    @Override
    protected void initListeners() {
        itemPushTimeStart.setOnClickListener(this);
        itemPushTimeEnd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        switch (v.getId()) {
            case R.id.img_left:
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
                onBackPressed();
                break;
            case R.id.btn_right:
                savePushTime();
                break;
            case R.id.item_push_time_start:
                itemPushTimeStart.setBackgroundColor(getResources().getColor(R.color.newLightGreen));
                itemPushTimeEnd.setBackgroundColor(getResources().getColor(R.color.white));

                resetListener(pushTimeStartTextView);
                break;
            case R.id.item_push_time_end:
                itemPushTimeStart.setBackgroundColor(getResources().getColor(R.color.white));
                itemPushTimeEnd.setBackgroundColor(getResources().getColor(R.color.newLightGreen));

                resetListener(pushTimeEndTextView);
                break;
            default:
                break;
        }
    }

    private void initTimePicker(){
        timeHour.setAdapter(new NumericWheelAdapter(0, 23, "%02d"));
        timeMin.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
        Calendar calender = Calendar.getInstance(Locale.getDefault());
        timeHour.setCurrentItem(calender.get(Calendar.HOUR_OF_DAY));
        timeMin.setCurrentItem(calender.get(Calendar.MINUTE));

        timeHour.setCyclic(true);// 可循环滚动
        timeMin.setCyclic(true);
    }

    private void resetListener(final TextView textView){
        if (onWheelChangedListener != null){
            timeHour.removeChangingListener(onWheelChangedListener);
            timeMin.removeChangingListener(onWheelChangedListener);
        }

        String value = textView.getText().toString();
        if (!TextUtils.isEmpty(value)){
            int hour = Integer.valueOf(value.split(":")[0]);
            int min = Integer.valueOf(value.split(":")[1]);
            timeHour.setCurrentItem(hour);
            timeMin.setCurrentItem(min);
        }

        onWheelChangedListener = new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                textView.setText(String.format("%02d:%02d", timeHour.getCurrentItem(), timeMin.getCurrentItem()));
            }
        };

        timeHour.addChangingListener(onWheelChangedListener);
        timeMin.addChangingListener(onWheelChangedListener);
    }

    private void savePushTime(){
        StringBuilder time = new StringBuilder();
        time.append(pushTimeStartTextView.getText().toString());
        time.append(",");
        time.append(pushTimeEndTextView.getText().toString());
        final String pushTime = time.toString();
        progressDialogManager.showDialog(SETTINT, this, null, null, getResources().getInteger(R.integer.http_timeout));
        deviceApiUnit.doSaveUserPushSetts(null, null, "1", "1", "0", time.toString(), new DeviceApiUnit.DeviceApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                progressDialogManager.dimissDialog(SETTINT, 0);
                ToastUtil.show(R.string.Setting_Success);
                Intent intent = new Intent();
                intent.putExtra("TIME", pushTime);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(SETTINT, 0);
                ToastUtil.show(R.string.Setting_Fail);
            }
        });
    }
}
