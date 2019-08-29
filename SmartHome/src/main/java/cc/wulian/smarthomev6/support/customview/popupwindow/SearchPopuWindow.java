package cc.wulian.smarthomev6.support.customview.popupwindow;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import cc.wulian.smarthomev6.BuildConfig;
import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.ztest.TestActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.adapter.DeviceListAdapter;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.InputMethodUtils;
import cc.wulian.smarthomev6.support.utils.KeyboardUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.TaskExecutor;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.Trans2PinYin;


public class SearchPopuWindow {

    private View layout_search_header;
    private ListView searchListview;
    private TextView noneTextView;
    private EditText editTextView;
    private DeviceListAdapter searchAdapter;
    private Activity activity;
    private PopupWindow popupWindow;
    private List<Device> deviceListData;
    private int statusBarHeight;

    public List<Device> getDeviceListData() {
        return deviceListData;
    }

    public void setDeviceListData(List<Device> deviceListData) {
        this.deviceListData = deviceListData;
    }

    public SearchPopuWindow(final Activity mActivity) {
        this.activity = mActivity;
        View contentView = View.inflate(mActivity, R.layout.device_list_search_edittext, null);
        layout_search_header = contentView.findViewById(R.id.layout_search_header);
        editTextView = (EditText) contentView.findViewById(R.id.et_title_search);
        noneTextView = (TextView) contentView.findViewById(R.id.search_device_none);
        searchListview = (ListView) contentView.findViewById(R.id.device_list_listview);
        searchAdapter = new DeviceListAdapter(mActivity, deviceListData);
        searchListview.setAdapter(searchAdapter);
        editTextView.addTextChangedListener(new EditTextWatcher());
        editTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Drawable drawable = editTextView.getCompoundDrawables()[2];
                if (drawable == null) {
                    // don't have end drawable
                    return false;
                }

                // 点击了 输入框中 右边的 x
                if (motionEvent.getX() > editTextView.getWidth()
                        - editTextView.getPaddingRight()
                        - drawable.getIntrinsicWidth()) {
                    editTextView.setText("");
                    return true;
                }
                return false;
            }
        });
        searchListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Device device = searchAdapter.getData().get(position);
                DeviceInfoDictionary.showDetail(mActivity, device);
            }
        });

        popupWindow = new PopupWindow();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        //获取状态栏高度，适配显示置顶所有手机
        Rect rect = new Rect();
        mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        statusBarHeight = rect.top;
        popupWindow.setWidth(displayMetrics.widthPixels);
        popupWindow.setHeight(displayMetrics.heightPixels - statusBarHeight);

        // 指定popupWindow的宽和高
//		popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
//		popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        // 使popupWindow全屏显示
        //popupWindow.setClippingEnabled(false);
        popupWindow.setContentView(contentView);

//		popupWindow.setAnimationStyle(R.style.AnimBottom);
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        popupWindow.setBackgroundDrawable(dw);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        contentView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodUtils.hide(activity, editTextView);
                dismiss();
            }
        });
        updateSkin();
    }

    public void updateSkin() {
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(layout_search_header, SkinResouceKey.BITMAP_TITLE_BACKGROUND);
    }

    public void setOnDismissListener(OnDismissListener listener) {
        popupWindow.setOnDismissListener(listener);
    }

    public void show(View view) {
        popupWindow.showAtLocation(view, Gravity.TOP, 0, statusBarHeight);
        editTextView.setFocusable(true);
        editTextView.setFocusableInTouchMode(true);
        editTextView.requestFocus();
        // 强制打开键盘
        KeyboardUtil.showKeyboard(activity);
    }

    public void dismiss() {
        popupWindow.dismiss();
    }

    public boolean isShown() {
        return popupWindow.isShowing();
    }

    class EditTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            getSearchDevice(charSequence.toString(), 10);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    /**
     * 调试
     */
    private void debug(String cmd) {
        if (TextUtils.isEmpty(cmd)) {
            return;
        }
//        if (cmd.startsWith("..domain2custom") && cmd.endsWith("#")) {
//            String url = cmd.substring(15, cmd.length() -1);
//            ApiConstant.setBaseUrl(url);
//            return;
//        }

        if ("..debug".equals(cmd)) {
            if (!TextUtils.equals("release", BuildConfig.BUILD_TYPE)) {
                TestActivity.start(activity);
            }
        } else if ("..showbaseurl".equals(cmd)) {
            ToastUtil.single("BASE_URL: " + ApiConstant.BASE_URL);
        } else if ("..showbuildtype".equals(cmd)) {
            ToastUtil.single("BUILD_TYPE: " + BuildConfig.BUILD_TYPE);
        } /*else if ("..resetdomain".endsWith(cmd)) {
            ApiConstant.resetBaseUrl();
            ToastUtil.single("重置环境：" + ApiConstant.BASE_URL);
        } else if ("..domain2test".equals(cmd)) {
            ApiConstant.setBaseUrl("https://testv6.wulian.cc");
            ToastUtil.single("修改为测试环境：https://testv6.wulian.cc");
        } else if ("..domain2debug".equals(cmd)) {
            ApiConstant.setBaseUrl("https://testv2.wulian.cc:50090");
            ToastUtil.single("修改为开发环境：https://testv2.wulian.cc:50090");
        } else if ("..domain2release".equals(cmd)) {
            ApiConstant.setBaseUrl("https://iot.wuliancloud.com:443");
            ToastUtil.single("修改为正式环境：https://iot.wuliancloud.com:443");
        }*/
    }

    private void getSearchDevice(final String searchKey, final int pageSize) {
        try {
            debug(searchKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final List<Device> result = new ArrayList<>();
        if (StringUtil.isNullOrEmpty(searchKey) || deviceListData == null) {
            searchListview.setVisibility(View.GONE);
            noneTextView.setVisibility(View.GONE);
        } else {
            TaskExecutor.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    Set<Device> allSet = new LinkedHashSet<>();
                    String key = searchKey.toLowerCase().trim();
                    boolean isOver = false;
                    for (int i = 0; i < deviceListData.size(); i++) {
                        Device deviceInfo = deviceListData.get(i);
                        String deviceName = DeviceInfoDictionary.getNameByTypeAndName(deviceInfo.type, deviceInfo.name).toLowerCase().trim();
                        if (StringUtil.isNullOrEmpty(deviceName))
                            continue;
                        if (Trans2PinYin.isFirstCharacter(key, deviceName)) {
                            allSet.add(deviceInfo);
                        }
                        if (allSet.size() >= pageSize) {
                            isOver = true;
                            break;
                        }
                    }
                    if (!isOver) {
                        for (int i = 0; i < deviceListData.size(); i++) {
                            Device deviceInfo = deviceListData.get(i);
                            String deviceName = DeviceInfoDictionary.getNameByTypeAndName(deviceInfo.type, deviceInfo.name).toLowerCase().trim();
                            if (StringUtil.isNullOrEmpty(deviceName))
                                continue;
                            if (Trans2PinYin.isStartPinYin(key, deviceName)) {
                                allSet.add(deviceInfo);
                            }
                            if (allSet.size() >= pageSize) {
                                isOver = true;
                                break;
                            }
                        }
                    }
                    if (!isOver) {
                        for (int i = 0; i < deviceListData.size(); i++) {
                            Device deviceInfo = deviceListData.get(i);
                            String deviceName = DeviceInfoDictionary.getNameByTypeAndName(deviceInfo.type, deviceInfo.name).toLowerCase().trim();
                            if (StringUtil.isNullOrEmpty(deviceName))
                                continue;
                            if (Trans2PinYin.isContainsPinYin(key, deviceName)) {
                                allSet.add(deviceInfo);
                            }
                            if (allSet.size() >= pageSize) {
                                isOver = true;
                                break;
                            }

                        }
                    }
                    result.addAll(allSet);
                    activity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            searchAdapter.swapData(result);
                            if (result != null && result.size() != 0) {
                                searchListview.setVisibility(View.VISIBLE);
                                noneTextView.setVisibility(View.INVISIBLE);
                            } else {
                                searchListview.setVisibility(View.INVISIBLE);
                                noneTextView.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            });

        }
    }

    public void refereshDeviceListData(List<Device> deviceListData) {
        this.deviceListData = deviceListData;
        searchAdapter.notifyDataSetChanged();
    }
}
