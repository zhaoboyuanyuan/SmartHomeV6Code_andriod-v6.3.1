package cc.wulian.smarthomev6.main.home.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.message.alarm.BcAlarmActivity;
import cc.wulian.smarthomev6.main.message.log.MessageLogActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.AllMessageBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.MessageBean;
import cc.wulian.smarthomev6.support.core.device.Attribute;
import cc.wulian.smarthomev6.support.core.device.Cluster;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.customview.PinCodeView;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GetRoomListEvent;
import cc.wulian.smarthomev6.support.event.KeyboardEvent;
import cc.wulian.smarthomev6.support.event.RoomInfoEvent;
import cc.wulian.smarthomev6.support.tools.MessageTool;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.KeyboardUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by Veev on 2017/6/5
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    网络智能(Bc) 首页widget
 */

public class HomeWidget_Bc_NetLock extends RelativeLayout implements IWidgetLifeCycle {

    private static final String TAG = "HomeWidget_Bc_NetLock";
    private static final int ANIMATION_DURING = 700;

    private HomeItemBean mHomeItem;
    private JSONObject extJson;

    private Device mDevice;
    private boolean isAllowRemoteOpen = true;
    private boolean mIsLock = true;//是否上锁, 默认上锁
    private boolean mInsurance = false;//保险

    private TextView textName, textArea, textState, toast, textHandle, textInput;
    private EditText editHide;
    private ImageView ivBattery;
    private RelativeLayout title, relativeStatus;

    private ImageView imageArrow, imageLock, imageLoadingLog, imageLoadingAlarm, imageVideo;
    private LinearLayout linearHandle, linearRoot;
    private TextView textSecondLock, textSecondState, textMainLock, textMainState, textDoor, textDoorState, textStatus;

    private TextView textLog0, textLogTime0, textLog1, textLogTime1, textLog2, textLogTime2,
            textAlarm2, textAlarmTime2, textAlarm1, textAlarmTime1, textAlarm0, textAlarmTime0,
            textLogNone, textAlarmNone, textLogMore, textAlarmMore, textAlarmForbid, textLogForbid;

    private LinearLayout linearLog0, linearLog1, linearLog2, linearAlarm0, linearAlarm1, linearAlarm2,
            linearLogAll, linearAlarmAll, linearLoadingLog, linearLoadingAlarm;

    private PinCodeView mPin;

    private int maxHeight = -1;

    private boolean isNeedToast;

    private void setHeight(int h) {
        if (maxHeight < 0) {
            maxHeight = h;
        } else {
            if (h > maxHeight) {
                maxHeight = h;
            }
        }
        if (extJson != null) {
            try {
                extJson.put("maxHeight", maxHeight);
                mHomeItem.setExtData(extJson.toString());
                HomeWidgetManager.update(mHomeItem);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isShow = false;

    private int mode;

    public HomeWidget_Bc_NetLock(Context context) {
        super(context);

        initView(context);
    }

    @Override
    public void onBindViewHolder(HomeItemBean bean) {
        EventBus.getDefault().register(this);
        if (bean == null) {
            return;
        }
        mHomeItem = HomeWidgetManager.get(bean.getWidgetID());
        mDevice = MainApplication.getApplication().getDeviceCache().get(bean.getWidgetID());

        imageVideo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceInfoDictionary.showDetail(getContext(), mDevice);
            }
        });
//        sendCmd_800A();
        try {
            extJson = new JSONObject(mHomeItem.getExtData());
            isShow = extJson.optBoolean("isShow");
//            maxHeight = extJson.optInt("maxHeight");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            extJson = new JSONObject();
        } finally {
            showExpand();
        }

        mode = mDevice.mode;
        // 设置在线离线的状态
        isNeedToast = false;
        updateMode();

        dealDevice(mDevice);
        WLog.i(TAG, "mDevice: " + mDevice.endpoints);

        setName();
        setRoomName();
        setGatewayState();
//        if (!HomeWidgetManager.hasInCache(mDevice)) {
            sendCmd_800A();
//        }
//        HomeWidgetManager.add2Cache(mDevice);
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void initView(final Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_bc_net_lock, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));//DisplayUtil.dip2Pix(context, 300)));

        textName = (TextView) rootView.findViewById(R.id.widget_title_name);
        textState = (TextView) rootView.findViewById(R.id.widget_title_state);
        textArea = (TextView) rootView.findViewById(R.id.widget_title_room);
        ivBattery = (ImageView) rootView.findViewById(R.id.iv_battery);
        toast = (TextView) rootView.findViewById(R.id.jzm_lock_text_toast);
        editHide = (EditText) rootView.findViewById(R.id.jzm_lock_edit_input_hide);

        imageArrow = (ImageView) rootView.findViewById(R.id.jzm_lock_image_arrow);
        imageLock = (ImageView) rootView.findViewById(R.id.jzm_lock_image_lock);
        linearRoot = (LinearLayout) rootView.findViewById(R.id.jzm_lock_linear_root);
        linearHandle = (LinearLayout) rootView.findViewById(R.id.jzm_lock_linear_handle);
        textHandle = (TextView) rootView.findViewById(R.id.jzm_lock_text_handle);
        textInput = (TextView) rootView.findViewById(R.id.jzm_lock_text_input);
        mPin = (PinCodeView) rootView.findViewById(R.id.jzm_lock_pin);
        imageVideo = (ImageView) rootView.findViewById(R.id.jzm_lock_image_bc);

        textSecondLock = (TextView) rootView.findViewById(R.id.jzm_lock_text_lock_second);
        textSecondState = (TextView) rootView.findViewById(R.id.jzm_lock_text_lock_state_second);
        textMainLock = (TextView) rootView.findViewById(R.id.jzm_lock_text_lock_main);
        textMainState = (TextView) rootView.findViewById(R.id.jzm_lock_text_lock_state_main);
        textDoorState = (TextView) rootView.findViewById(R.id.jzm_lock_text_lock_state_door);
        textDoor = (TextView) rootView.findViewById(R.id.jzm_lock_text_lock_door);

        textLog0 = (TextView) rootView.findViewById(R.id.jzm_lock_text_log_0);
        textLog1 = (TextView) rootView.findViewById(R.id.jzm_lock_text_log_1);
        textLog2 = (TextView) rootView.findViewById(R.id.jzm_lock_text_log_2);
        textAlarm0 = (TextView) rootView.findViewById(R.id.jzm_lock_text_alarm_0);
        textAlarm1 = (TextView) rootView.findViewById(R.id.jzm_lock_text_alarm_1);
        textAlarm2 = (TextView) rootView.findViewById(R.id.jzm_lock_text_alarm_2);
        textLogTime0 = (TextView) rootView.findViewById(R.id.jzm_lock_text_log_0_time);
        textLogTime1 = (TextView) rootView.findViewById(R.id.jzm_lock_text_log_1_time);
        textLogTime2 = (TextView) rootView.findViewById(R.id.jzm_lock_text_log_2_time);
        textAlarmTime0 = (TextView) rootView.findViewById(R.id.jzm_lock_text_alarm_0_time);
        textAlarmTime1 = (TextView) rootView.findViewById(R.id.jzm_lock_text_alarm_1_time);
        textAlarmTime2 = (TextView) rootView.findViewById(R.id.jzm_lock_text_alarm_2_time);
        textAlarmNone = (TextView) rootView.findViewById(R.id.jzm_lock_text_no_alarm);
        textAlarmForbid = (TextView) rootView.findViewById(R.id.jzm_lock_text_forbid_alarm);
        textLogNone = (TextView) rootView.findViewById(R.id.jzm_lock_text_no_log);
        textLogForbid = (TextView) rootView.findViewById(R.id.jzm_lock_text_forbid_log);
        textLogMore = (TextView) rootView.findViewById(R.id.jzm_lock_text_log_more);
        textAlarmMore = (TextView) rootView.findViewById(R.id.jzm_lock_text_alarm_more);

        linearLog0 = (LinearLayout) rootView.findViewById(R.id.jzm_lock_linear_log_0);
        linearLog1 = (LinearLayout) rootView.findViewById(R.id.jzm_lock_linear_log_1);
        linearLog2 = (LinearLayout) rootView.findViewById(R.id.jzm_lock_linear_log_2);
        linearLogAll = (LinearLayout) rootView.findViewById(R.id.jzm_lock_linear_log_all);
        linearAlarm0 = (LinearLayout) rootView.findViewById(R.id.jzm_lock_linear_alarm_0);
        linearAlarm1 = (LinearLayout) rootView.findViewById(R.id.jzm_lock_linear_alarm_1);
        linearAlarm2 = (LinearLayout) rootView.findViewById(R.id.jzm_lock_linear_alarm_2);
        linearAlarmAll = (LinearLayout) rootView.findViewById(R.id.jzm_lock_linear_alarm_all);
        linearLoadingLog = (LinearLayout) rootView.findViewById(R.id.jzm_lock_linear_loading_log);
        linearLoadingAlarm = (LinearLayout) rootView.findViewById(R.id.jzm_lock_linear_loading_alarm);

        imageLoadingAlarm = (ImageView) rootView.findViewById(R.id.jzm_lock_image_loading_alarm);
        imageLoadingLog = (ImageView) rootView.findViewById(R.id.jzm_lock_image_loading_log);

        relativeStatus = (RelativeLayout) rootView.findViewById(R.id.jzm_lock_relative_status);
        textStatus = (TextView) rootView.findViewById(R.id.jzm_lock_text_status);

//        setHeight(getTargetHeight(linearRoot));
        linearHandle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });

        textLogMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageLogActivity.class);
                intent.putExtra(MessageLogActivity.FILTER, mDevice.devID);
                context.startActivity(intent);
            }
        });

        textAlarmMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(context, MessageAlarmActivity.class);
                intent.putExtra(MessageAlarmActivity.FILTER, mDevice.devID);
                context.startActivity(intent);*/

                BcAlarmActivity.start(context, mDevice.devID, BcAlarmActivity.TYPE_ALL);
            }
        });

        editHide.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable editable = editHide.getText();
                int length = editable.toString().trim().length();

                mPin.setInputCount(length);

                if (length == 6 && count == 1) {
                    // 开锁
                    sendCmd(editHide.getText().toString());
                }

                if (length > 6) {
                    int selEndIndex = Selection.getSelectionEnd(editable);
                    String str = editable.toString();
                    //截取新字符串
                    String newStr = str.substring(0, 6);
                    editHide.setText(newStr);
                    editable = editHide.getText();

                    //新字符串的长度
                    int newLen = editable.length();
                    //旧光标位置超过字符串长度
                    if (selEndIndex > newLen) {
                        selEndIndex = editable.length();
                    }
                    //设置新光标所在的位置
                    Selection.setSelection(editable, selEndIndex);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editHide.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                /*if (hasFocus) {
                    textInput.setVisibility(INVISIBLE);
                    mPin.setVisibility(VISIBLE);
                }*/
                setCouldInput(hasFocus);
            }
        });

        DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
        int titleH = mDisplayMetrics.widthPixels - TypedValue.complexToDimensionPixelSize(30, mDisplayMetrics);
        textName.setMaxWidth(titleH / 2);
        textArea.setMaxWidth(titleH / 4);
    }

    /**
     * 设置密码框 能否输入
     */
    private void setCouldInput(boolean isCloudInput) {
        if (isCloudInput) {
            textInput.setVisibility(INVISIBLE);
            mPin.setVisibility(VISIBLE);
        } else {
            editHide.setText("");
            editHide.clearFocus();
            textInput.setVisibility(VISIBLE);
            mPin.setVisibility(INVISIBLE);
        }
    }

    private void setGatewayState() {
        if (TextUtils.equals(Preference.ENTER_TYPE_ACCOUNT, Preference.getPreferences().getUserEnterType())) {
            textAlarmForbid.setVisibility(INVISIBLE);
            textLogForbid.setVisibility(INVISIBLE);

            linearLogAll.setVisibility(VISIBLE);
            linearAlarmAll.setVisibility(VISIBLE);
            textLogNone.setVisibility(VISIBLE);
            textAlarmNone.setVisibility(VISIBLE);
            textLogMore.setVisibility(VISIBLE);
            textAlarmMore.setVisibility(VISIBLE);
        } else {
            textAlarmForbid.setVisibility(VISIBLE);
            textLogForbid.setVisibility(VISIBLE);

            linearLogAll.setVisibility(INVISIBLE);
            linearAlarmAll.setVisibility(INVISIBLE);
            textLogNone.setVisibility(INVISIBLE);
            textAlarmNone.setVisibility(INVISIBLE);

            textLogMore.setVisibility(GONE);
            textAlarmMore.setVisibility(GONE);
        }
    }

    /**
     * 设置反锁状态
     *
     * @param isLock true 反锁
     */
    private void setSecondLock(boolean isLock) {
        if (isLock) {
            textSecondState.setText(R.string.Device_Lock_Widget_Doublelocked);
            textSecondLock.setTextColor(getResources().getColor(R.color.newSecondary));
            textSecondState.setTextColor(getResources().getColor(R.color.newSecondary));
        } else {
            textSecondState.setText(R.string.Device_Lock_Widget_Undoublelocked);
            textSecondLock.setTextColor(getResources().getColor(R.color.newPrimaryText));
            textSecondState.setTextColor(getResources().getColor(R.color.newPrimaryText));
        }
    }

    private void setMainLock(boolean isLock) {
        mIsLock = isLock;
        if (isLock) {
//            textMainState.setText(R.string.Device_Lock_Widget_MainLockstatusa);
            imageLock.setImageResource(R.drawable.icon_op_lock_off);
        } else {
//            textMainState.setText(R.string.Device_Lock_Widget_MainLockstatusb);
            imageLock.setImageResource(R.drawable.icon_op_lock_on);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    setMainLock(true);
                }
            }, 5000);
        }
    }

    private void setInsurance(boolean isLock) {
        mInsurance = isLock;
        if (isLock) {
            textMainState.setText(R.string.widget_lock_3_key_tags_lock);
            textMainLock.setTextColor(getResources().getColor(R.color.newPrimaryText));
            textMainState.setTextColor(getResources().getColor(R.color.newPrimaryText));
        } else {
            textMainState.setText(R.string.widget_lock_3_key_tags_unlock);
            textMainLock.setTextColor(getResources().getColor(R.color.newSecondary));
            textMainState.setTextColor(getResources().getColor(R.color.newSecondary));
        }
    }

    /**
     * 设置允许远程开锁
     */
    private void setRemoteOpen(boolean isAllow) {
//        isAllowRemoteOpen = isAllow;
//        // 允许远程开锁
//        if (isAllow) {
//            textInput.setText(R.string.Xuanwulakeseries_Widget_Inputpassword);
//            textInput.setBackgroundResource(R.drawable.shape_home_widget_lock);
//            textInput.setTextColor(getResources().getColor(R.color.newPrimary));
//            mPin.setBackgroundResource(R.drawable.shape_home_widget_lock);
//            editHide.setEnabled(true);
//        } else {
//            textInput.setText(R.string.Xuanwulakeseries_Entrance_Closenetwork);
//            textInput.setBackgroundResource(R.drawable.shape_home_widget_lock_nor);
//            textInput.setTextColor(getResources().getColor(R.color.newSecondaryText));
//            mPin.setBackgroundResource(R.drawable.shape_home_widget_lock_nor);
//            editHide.setEnabled(false);
//        }
    }

    private void setDoorState(String state) {
        if (state == null) {
            return;
        }

        switch (state) {
            case "00":
                textDoorState.setText(R.string.Device_Lock_Widget_Gatestaopen);
                textDoorState.setTextColor(getResources().getColor(R.color.newSecondary));
                textDoor.setTextColor(getResources().getColor(R.color.newSecondary));
                break;
            case "01":
                textDoorState.setText(R.string.Device_Lock_Widget_Gatestatusa);
                textDoorState.setTextColor(getResources().getColor(R.color.newPrimaryText));
                textDoor.setTextColor(getResources().getColor(R.color.newPrimaryText));
                break;
            case "02":
                textDoorState.setText(R.string.Device_Lock_Widget_Gatestatusb);
                textDoorState.setTextColor(getResources().getColor(R.color.newSecondary));
                textDoor.setTextColor(getResources().getColor(R.color.newSecondary));
                break;
        }

    }

    private void hasDoor(String state) {
        if (state == null) {
            return;
        }
        switch (state) {
            case "00":
                textDoorState.setVisibility(GONE);
                textDoor.setVisibility(GONE);
                break;
            case "01":
                textDoorState.setVisibility(VISIBLE);
                textDoor.setVisibility(VISIBLE);
                break;
        }
    }

    /**
     * 显示家在动画
     */
    private void showLoading() {
        if (!TextUtils.equals(Preference.ENTER_TYPE_ACCOUNT, Preference.getPreferences().getUserEnterType())) {
            linearLoadingLog.setVisibility(INVISIBLE);
            linearLoadingAlarm.setVisibility(INVISIBLE);

            textLogNone.setVisibility(INVISIBLE);
            textAlarmNone.setVisibility(INVISIBLE);
            return;
        }

        linearLogAll.setVisibility(INVISIBLE);
        linearAlarmAll.setVisibility(INVISIBLE);
        textLogNone.setVisibility(INVISIBLE);
        textAlarmNone.setVisibility(INVISIBLE);
        linearLoadingLog.setVisibility(VISIBLE);
        linearLoadingAlarm.setVisibility(VISIBLE);

        ObjectAnimator animatorL = ObjectAnimator.ofFloat(imageLoadingLog, "rotation", 0, 360).setDuration(2000);
        animatorL.setRepeatCount(ValueAnimator.INFINITE);
        animatorL.setInterpolator(new FastOutLinearInInterpolator());
        animatorL.start();
        ObjectAnimator animatorA = ObjectAnimator.ofFloat(imageLoadingAlarm, "rotation", 0, 360).setDuration(2000);
        animatorA.setRepeatCount(ValueAnimator.INFINITE);
        animatorA.setInterpolator(new FastOutLinearInInterpolator());
        animatorA.start();
    }

    private void hideLoading() {
        if (linearLoadingLog.getVisibility() != GONE) {
            linearLoadingLog.setVisibility(GONE);
            linearLoadingAlarm.setVisibility(GONE);
        }
    }

    /**
     * 显示 折叠区域
     */
    private void showExpand() {
        // TODO: 2017/6/27 暂时关闭
//        if (true) {
//            return;
//        }

        if (isShow) {
            showLoading();
            getMessage();
            sendCmd_800A();

            linearRoot.setVisibility(VISIBLE);
            linearRoot.requestLayout();
            textHandle.setText(R.string.Device_Lock_Widget_Fold);
            imageArrow.setRotationX(180f);
            if (maxHeight <= 0) {
                setHeight(linearRoot.getHeight());
            }
        } else {
            linearRoot.setVisibility(GONE);
            linearRoot.requestLayout();
            textHandle.setText(R.string.Device_Lock_Widget_Spread);
            imageArrow.setRotationX(0f);
        }
    }

    private void toggleWithoutAnim() {
        if (!isShow) {
            isShow = true;

            sendCmd_800A();
            showLoading();
            getMessage();

            linearRoot.setVisibility(VISIBLE);
            imageArrow.setRotationX(180f);
            setHeight(linearRoot.getHeight());
            textHandle.setText(R.string.Device_Lock_Widget_Fold);
        } else {
            isShow = false;

            linearRoot.setVisibility(GONE);
            imageArrow.setRotationX(0f);
            textHandle.setText(R.string.Device_Lock_Widget_Spread);
        }

        if (extJson != null) {
            try {
                extJson.put("isShow", isShow);
                mHomeItem.setExtData(extJson.toString());
                HomeWidgetManager.update(mHomeItem);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 开关折叠
     */
    private void toggle() {
        // TODO: 2017/6/27 暂时关闭
//        if (true) {
//            return;
//        }

        if (maxHeight <= 0) {
            toggleWithoutAnim();
            return;
        }

        if (!isShow) {
            isShow = true;

            sendCmd_800A();
            showLoading();
            getMessage();

            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageArrow, View.ROTATION_X, 0f, 180f);
            objectAnimator.setDuration(ANIMATION_DURING);
            objectAnimator.start();

            ValueAnimator va = ValueAnimator.ofInt(0, maxHeight);
            va.removeAllListeners();
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int h = (Integer) animation.getAnimatedValue();
                    linearRoot.getLayoutParams().height = h;
                    linearRoot.requestLayout();
                }
            });
            va.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    linearRoot.getLayoutParams().height = maxHeight;
                    linearRoot.setVisibility(VISIBLE);
                    textHandle.setText(R.string.Device_Lock_Widget_Fold);
                }
            });
            va.setInterpolator(new LinearOutSlowInInterpolator());
            va.setDuration(ANIMATION_DURING).start();
            linearRoot.setVisibility(VISIBLE);
        } else {
            isShow = false;

            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageArrow, View.ROTATION_X, 180f, 0f);
            objectAnimator.setDuration(ANIMATION_DURING);
            objectAnimator.start();

            ValueAnimator va = ValueAnimator.ofInt(maxHeight, 0);
            va.removeAllListeners();
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    int h = (Integer) animation.getAnimatedValue();
                    linearRoot.getLayoutParams().height = h;
                    linearRoot.requestLayout();
                }
            });
            va.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    linearRoot.setVisibility(GONE);
                    linearRoot.getLayoutParams().height = maxHeight;
                    textHandle.setText(R.string.Device_Lock_Widget_Spread);
                }
            });
            va.setInterpolator(new LinearOutSlowInInterpolator());
            va.setDuration(ANIMATION_DURING).start();
        }

        if (extJson != null) {
            try {
                extJson.put("isShow", isShow);
                mHomeItem.setExtData(extJson.toString());
                HomeWidgetManager.update(mHomeItem);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取新的日志报警
     */
    private void getMessage() {
        // TODO: 2017/6/27 暂时关闭
//        if (true) {
//            return;
//        }

        // 只有展开时, 才会获取数据
        if (!isShow) {
            hideLoading();
            return;
        }

        if (!TextUtils.equals(Preference.ENTER_TYPE_ACCOUNT, Preference.getPreferences().getUserEnterType())) {
            hideLoading();
            return;
        }

        new DataApiUnit(getContext()).doGetDeviceAlarmAndLog(mDevice.devID, new DataApiUnit.DataApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                hideLoading();
                setMessage((AllMessageBean) bean);
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.single(msg);
                hideLoading();
                setMessageFail();
            }
        });
    }

    private void setMessageFail() {
        textLogNone.setVisibility(VISIBLE);
        linearLogAll.setVisibility(INVISIBLE);

        textAlarmNone.setVisibility(VISIBLE);
        linearAlarmAll.setVisibility(INVISIBLE);
    }

    private void setMessage(AllMessageBean bean) {
        List<MessageBean.RecordListBean> actionRecords = bean.actionRecords;
        List<MessageBean.RecordListBean> alarmRecords = bean.alarmRecords;

        if (actionRecords.isEmpty()) {
            textLogNone.setVisibility(VISIBLE);
            linearLogAll.setVisibility(INVISIBLE);
        } else {
            textLogNone.setVisibility(INVISIBLE);
            linearLogAll.setVisibility(VISIBLE);
            linearLog0.setVisibility(GONE);
            linearLog1.setVisibility(GONE);
            linearLog2.setVisibility(GONE);

            int length = actionRecords.size();
            if (length >= 1) {
                linearLog0.setVisibility(VISIBLE);
                textLog0.setText(MessageTool.getWidgetMessage(actionRecords.get(0)));
                textLogTime0.setText(longTimeToString(actionRecords.get(0).time));
            }

            if (length >= 2) {
                linearLog1.setVisibility(VISIBLE);
                textLog1.setText(MessageTool.getWidgetMessage(actionRecords.get(1)));
                textLogTime1.setText(longTimeToString(actionRecords.get(1).time));
            }

            if (length >= 3) {
                linearLog2.setVisibility(VISIBLE);
                textLog2.setText(MessageTool.getWidgetMessage(actionRecords.get(2)));
                textLogTime2.setText(longTimeToString(actionRecords.get(2).time));
            }
        }

        if (alarmRecords.isEmpty()) {
            textAlarmNone.setVisibility(VISIBLE);
            linearAlarmAll.setVisibility(INVISIBLE);
        } else {
            textAlarmNone.setVisibility(INVISIBLE);
            linearAlarmAll.setVisibility(VISIBLE);
            linearAlarm0.setVisibility(GONE);
            linearAlarm1.setVisibility(GONE);
            linearAlarm2.setVisibility(GONE);

            int length = alarmRecords.size();
            if (length >= 1) {
                linearAlarm0.setVisibility(VISIBLE);
                textAlarm0.setText(MessageTool.getWidgetMessage(alarmRecords.get(0)));
                textAlarmTime0.setText(longTimeToString(alarmRecords.get(0).time));
            }

            if (length >= 2) {
                linearAlarm1.setVisibility(VISIBLE);
                textAlarm1.setText(MessageTool.getWidgetMessage(alarmRecords.get(1)));
                textAlarmTime1.setText(longTimeToString(alarmRecords.get(1).time));
            }

            if (length >= 3) {
                linearAlarm2.setVisibility(VISIBLE);
                textAlarm2.setText(MessageTool.getWidgetMessage(alarmRecords.get(2)));
                textAlarmTime2.setText(longTimeToString(alarmRecords.get(2).time));
            }
        }
    }

    private String longTimeToString(long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());

        return sdf.format(date);
    }

    /**
     * 强行刷新出 view的高度
     */
    private int getTargetHeight(View v) {
        try {
            Method m = v.getClass().getDeclaredMethod("onMeasure", int.class,
                    int.class);
            m.setAccessible(true);
            m.invoke(v,
                    v.getLayoutParams().width,
                    v.getLayoutParams().height
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v.getMeasuredHeight();
    }

    private void setName() {
        if (mDevice == null) {
            textName.setText(DeviceInfoDictionary.getNameByTypeAndName(mHomeItem.getName(), mHomeItem.getType()));
        } else {
            textName.setText(DeviceInfoDictionary.getNameByTypeAndName(mDevice.type, mDevice.name));
        }
    }

    /**
     * 设置分区名
     */
    private void setRoomName() {
        String areaName = ((MainApplication) getContext().getApplicationContext()).getRoomCache().getRoomName(mDevice);
        textArea.setText(areaName);
    }

    /**
     * 更新上下线
     */
    private void updateMode() {
        switch (mode) {
            case 0:
            case 4:
            case 1:
                // 上线
                textState.setText(R.string.Device_Online);
                textState.setTextColor(getResources().getColor(R.color.colorPrimary));

                // 如果可以远程开锁, 才设置为可用
                if (isAllowRemoteOpen) {
                    editHide.setEnabled(true);
                    textInput.setBackgroundResource(R.drawable.shape_home_widget_lock);
                    textInput.setTextColor(getResources().getColor(R.color.newPrimary));
                    mPin.setBackgroundResource(R.drawable.shape_home_widget_lock);
                }

                relativeStatus.setVisibility(VISIBLE);
                textStatus.setVisibility(INVISIBLE);
                setMainLock(mIsLock);

                imageVideo.setEnabled(true);
                imageVideo.setImageResource(R.drawable.ic_widget_bc_video_1);
                ivBattery.setVisibility(VISIBLE);
                break;
            case 2:
                // 离线
                textState.setText(R.string.Device_Offline);
                textState.setTextColor(getResources().getColor(R.color.newStateText));
                textInput.setBackgroundResource(R.drawable.shape_home_widget_lock_nor);
                textInput.setTextColor(getResources().getColor(R.color.newStateText));
                mPin.setBackgroundResource(R.drawable.shape_home_widget_lock_nor);
                editHide.setText("");
                editHide.setEnabled(false);

                relativeStatus.setVisibility(INVISIBLE);
                textStatus.setVisibility(VISIBLE);
                imageLock.setImageResource(R.drawable.icon_op_lock_lineoff);

                imageVideo.setEnabled(true);
                imageVideo.setImageResource(R.drawable.ic_widget_bc_video_2);
                ivBattery.setVisibility(GONE);
                break;
            case 3:
                break;
        }
    }

    /**
     * 解析设备数据
     */
    private void dealDevice(Device device) {
        mode = device.mode;
        updateMode();
        if (device.mode == 3) {
            // 设备删除
        } else if (device.mode == 2) {
            // 设备离线
        } else if (device.mode == 1) {
            // 更新上线
            dealData(device);
        } else if (device.mode == 0) {
            dealData(device);
        }
    }

    private void dealData(Device device) {
        EndpointParser.parse(device, new EndpointParser.ParserCallback() {
            @Override
            public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                // 更新状态
                updateState(attribute.attributeId, attribute.attributeValue);
            }
        });
    }

    /**
     * 处理数据
     * <p>
     * 废弃时间: 2017年11月27日11:15:34
     */
    @Deprecated
    private void dealData(String data) {
        try {
            JSONObject object = new JSONObject(data);
            JSONArray endpoints = object.getJSONArray("endpoints");
            // TODO: 2017/5/16 更新 endpointNumber
//                        endpointNumber = object.getString("endpointNumber");
            JSONArray clusters = ((JSONObject) endpoints.get(0)).getJSONArray("clusters");
            JSONArray attributes = ((JSONObject) clusters.get(0)).getJSONArray("attributes");
            String attributeValue = ((JSONObject) attributes.get(0)).getString("attributeValue");
            int attributeId = ((JSONObject) attributes.get(0)).getInt("attributeId");
            // 更新状态
            updateState(attributeId, attributeValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void toast(String sss) {
        toast.setText(sss);
        toast.setVisibility(VISIBLE);
        toast.removeCallbacks(toastRun);
        toast.postDelayed(toastRun, 1500);
    }

    private void toast(@StringRes int sss) {
        toast.setText(sss);
        toast.setVisibility(VISIBLE);
        toast.removeCallbacks(toastRun);
        toast.postDelayed(toastRun, 1500);
    }

    private Runnable toastRun = new Runnable() {
        @Override
        public void run() {
            toast.setVisibility(INVISIBLE);
        }
    };

    private void toastCheckPwd(boolean isChecking) {
        removeCallbacks(checkPwdRun);
        if (isChecking) {
            checkProcess = 1;
            showCheckPwd();
        } else {
            toast.setVisibility(INVISIBLE);
        }
    }

    private int checkProcess = 1;
    Runnable checkPwdRun = new Runnable() {
        @Override
        public void run() {
            StringBuffer sb = new StringBuffer();
            sb.append(getResources().getString(R.string.Device_Lock_Widget_Status));
            for (int i = 1; i <= checkProcess; i++) {
                sb.append(".");
            }
            for (int i = checkProcess; i <= 3; i++) {
                sb.append(" ");
            }
            toast.setVisibility(VISIBLE);
            toast.setText(sb);
            if (++checkProcess > 3) {
                checkProcess = 1;
            }
            showCheckPwd();
        }
    };

    private void showCheckPwd() {
        postDelayed(checkPwdRun, 700);
    }

    private void updateBatteryLevel(String battery) {
        switch (battery) {
            case "00":
                ivBattery.setImageResource(R.drawable.icon_battery_0);
                break;
            case "01":
                ivBattery.setImageResource(R.drawable.icon_battery_1);
                break;
            case "02":
                ivBattery.setImageResource(R.drawable.icon_battery_2);
                break;
            case "03":
                ivBattery.setImageResource(R.drawable.icon_battery_3);
                break;
        }
    }


    private void updateState(int attributeId, String attributeValue) {
        WLog.i(TAG, "updateState: attributeId-" + attributeId + ", attributeValue-" + attributeValue);

        // 验证完成
        toastCheckPwd(false);
        if (attributeValue.isEmpty()) {
            return;
        }
        int value = 0;
        try {
            value = Integer.parseInt(attributeValue);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        WLog.i(TAG, attributeId + " " + attributeValue);

        switch (attributeId) {
            case 0x0000:
                if (value == 2) {
                    setInsurance(true);
                }
                if (value == 3) {
                    setInsurance(false);
                }
                if (value == 4) {
                    setSecondLock(true);
                }
                if (value == 5) {
                    setSecondLock(false);
                }
                break;
            case 0x8001:
                if (value == 4) {
                    if (isNeedToast) {
                        toast(R.string.Xuanwulakeseries_Widget_Errorsmore);
                    }
                }
                break;
            case 0x8002:
                if (isNeedToast) {
                    toast(R.string.Home_Widget_Lock_Opened);
                    editHide.setText("");
                    setMainLock(false);
                }
                break;
            case 0x8003:
//                if (value == 3 || value == 4) {
//                    setMainLock(true);
//                }
//                if (value == 5) {
//                    setRemoteOpen(true);
//                }
//                if (value == 6) {
//                    setRemoteOpen(false);
//                }
                break;
            case 0x8004:
                break;
            case 0x8005:
                if (attributeValue.startsWith("1")) {
                    String aa = attributeValue.substring(1, 3);
//                    String bb = attributeValue.substring(3, 5);
                    String cc = attributeValue.substring(5, 7);
                    String dd = attributeValue.substring(7, 9);
                    String ee = attributeValue.substring(9, 11);
                    String ff = attributeValue.substring(11, 13);
                    String gg = attributeValue.substring(13);
                    if ("01".equals(aa)) {
                        // 解除保险
                        setInsurance(false);
                    } else {
                        setInsurance(true);
                    }
                    if ("01".equals(ee)) {
                        setRemoteOpen(false);
                    } else {
                        setRemoteOpen(true);
                    }

                    if ("01".equals(cc)) {
                        setSecondLock(true);
                    } else {
                        setSecondLock(false);
                    }

                    hasDoor(ff);
                    if (TextUtils.equals(ff, "01")) {
                        setDoorState(gg);
                    }
                    updateBatteryLevel(dd);
                }

                break;
            case 0x8006:
                if (isNeedToast) {
                    if (value == 1) {
                        toast(R.string.Home_Widget_Password_Error);
                        editHide.setText("");
                    }
                    if (value == 2) {
                        toast(R.string.Home_Widget_Password_Error);
                        editHide.setText("");
                    }
                    if (value == 11) {
                        toast(R.string.Xuanwulakeseries_Widget_Lock);
                        setSecondLock(true);
                    }
                }
                break;
            case 0x8007:
                break;
            case 0x8008:
                if (value == 10) {
                    setDoorState("00");
                }
                if (value == 14) {
                    setDoorState("01");
                }
                if (value == 15) {
                    setDoorState("02");
                }
                break;
        }
    }

    private long lastSendTime = -1;

    private void sendCmd(String pwd) {
        // 消抖
        // 300ms
        /*long sendTime = System.currentTimeMillis();
        if (sendTime - lastSendTime < 300) {
            return;
        }
        lastSendTime = sendTime;*/

        if (mode != 0 && mode != 1) {
            return;
        }
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", mDevice.gwID);
            object.put("devID", mDevice.devID);
            object.put("clusterId", 257);
            object.put("commandType", 1);
            object.put("commandId", 32772);
            object.put("endpointNumber", 1);
            JSONArray array = new JSONArray();
            array.put(pwd);
            object.put("parameter", array);

            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);

            postDelayed(checkTimeOut, 20000);

            // 发送命令后 隐藏键盘
            KeyboardUtil.hideKeyboard(getContext(), this);

            toastCheckPwd(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendCmd_800A() {
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", mDevice.gwID);
            object.put("devID", mDevice.devID);
            object.put("clusterId", 257);
            object.put("commandType", 1);
            object.put("commandId", 0x800A);
            object.put("endpointNumber", 1);

            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断超时
     */
    private Runnable checkTimeOut = new Runnable() {
        @Override
        public void run() {
            removeCallbacks(checkPwdRun);
            editHide.setText("");
            toast(R.string.Xuanwulakeseries_Widget_Requesttimeout);
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRoomInfoEvent(RoomInfoEvent event) {
        mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
        setRoomName();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetRoomListEvent(GetRoomListEvent event) {
        mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
        setRoomName();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && mDevice != null) {
            if (TextUtils.equals(event.device.devID, mDevice.devID)) {
                if (event.type == DeviceReportEvent.DEVICE_ALARM) {
                    getMessage();
                }
                mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                isNeedToast = true;
                dealDevice(JSON.parseObject(event.device.data, Device.class));
                removeCallbacks(checkTimeOut);
                setName();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInfoChanged(DeviceInfoChangedEvent event) {
        if (event.deviceInfoBean != null && mDevice != null) {
            if (TextUtils.equals(event.deviceInfoBean.devID, mDevice.devID)) {
                if (event.deviceInfoBean.mode == 2) {
                    mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                    setRoomName();
                    setName();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onKeyboardEvent(KeyboardEvent event) {
        if (event.type == KeyboardEvent.KEYBOARD_HIDE) {
            /*editHide.setText("");
            editHide.clearFocus();
            textInput.setVisibility(VISIBLE);
            mPin.setVisibility(INVISIBLE);*/

            setCouldInput(false);
        }
    }
}
