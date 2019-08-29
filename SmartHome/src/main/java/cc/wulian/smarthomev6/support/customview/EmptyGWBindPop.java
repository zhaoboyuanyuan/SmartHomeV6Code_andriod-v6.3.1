package cc.wulian.smarthomev6.support.customview;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceBean;

/**
 * created by huxc  on 2019/1/7.
 * func：未绑定网关是点击场景弹出的底部pop
 * email: hxc242313@qq.com
 */

public class EmptyGWBindPop extends PopupWindow {
    private static final String TAG = "EmptyGWBindPop";
    private static final String VIRTUAL_GW_ID = "000000000000";
    private final View view;
    private Context context;
    private DeviceApiUnit deviceApiUnit;
    private List<DeviceBean> gateWayList;
    private TextView btnBind;
    private TextView btnVirtual;
    private TextView btnCancel;
    private onPopClickListener listener;

    public interface onPopClickListener {
        void virtualGateway();

        void bindGateway();

        void cancel();
    }

    public EmptyGWBindPop(Context context) {
        super(context);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.popwindow_empty_gateway, null);
        initView();
        initData();
        initPopWindow();
    }


    public void setOnPopClickListener(onPopClickListener listener) {
        this.listener = listener;
    }

    private void initView() {
        btnVirtual = view.findViewById(R.id.btn_virtual);
        btnBind = view.findViewById(R.id.btn_bind);
        btnCancel = view.findViewById(R.id.btn_cancel);

        btnVirtual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (listener != null) {
                    listener.virtualGateway();
                }
            }
        });
        btnBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (listener != null) {
                    listener.bindGateway();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (listener != null) {
                    listener.cancel();
                }
            }
        });

    }


    private void initPopWindow() {
        this.setContentView(view);
        // 设置弹出窗体的宽
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        // 设置弹出窗体的高
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        // 设置弹出窗体可点击()
        this.setFocusable(true);
        this.setOutsideTouchable(false);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.mypopwindow_anim_style);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x00FFFFFF);
        //设置弹出窗体的背景
        this.setBackgroundDrawable(dw);
        backgroundAlpha((Activity) context, 0.5f);//0.0-1.0
    }

    private void initData() {
        gateWayList = new ArrayList<>();
        deviceApiUnit = new DeviceApiUnit(context);
        deviceApiUnit.getExperienceGatewayStatus(VIRTUAL_GW_ID, new DeviceApiUnit.DeviceApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                String jsonData = bean.toString();
                String data = null;
                try {
                    org.json.JSONObject object = new org.json.JSONObject(jsonData);
                    data = object.optString("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (TextUtils.equals("1", data)) {
                    deviceApiUnit.doGetAllDevice(new DeviceApiUnit.DeviceApiCommonListener<List<DeviceBean>>() {
                        @Override
                        public void onSuccess(List<DeviceBean> deviceBeanList) {
                            gateWayList.clear();
                            List<DeviceBean> allGateway = new ArrayList<>();
                            for (DeviceBean deviceBean : deviceBeanList) {
                                if (!deviceBean.isShared()) {
                                    gateWayList.add(deviceBean);
                                }
                                allGateway.add(deviceBean);
                            }
                            btnVirtual.setVisibility((allGateway.isEmpty() || allGateway.size() == 0) ? View.VISIBLE : View.GONE);
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            Log.i(TAG, "onFail: " + msg);
                        }
                    });
                }
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    /**
     * 设置添加屏幕的背景透明度(值越大,透明度越高)
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }
}
