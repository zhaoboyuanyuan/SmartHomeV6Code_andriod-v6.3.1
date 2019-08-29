package cc.wulian.smarthomev6.support.tools;

import android.app.Activity;
import android.text.TextUtils;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.GatewayLastVersionBean;
import cc.wulian.smarthomev6.support.core.device.GatewayDataEntity;
import cc.wulian.smarthomev6.support.core.device.GatewayManager;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;

/**
 * 封装网关版本判断的逻辑操作
 */
public class GatewayVersionTool {
    private static WLDialog noticeUpdateGatewayDialog;

    /**
     * 判断当前网关是否满足软件版本要求
     *
     * @param activity
     * @param doJump   执行跳转操作
     */
    public static void doGatewayVersionCompare(final Activity activity, final Runnable doJump) {
        Preference preference = Preference.getPreferences();
        final String GET_DATA = "doGatewayVersionCompare";
        if (Preference.ENTER_TYPE_ACCOUNT.equals(preference.getUserEnterType())) {
            DeviceApiUnit deviceApiUnit = new DeviceApiUnit(activity);
            GatewayManager gatewayManager = new GatewayManager();
            String currentGatewayID = preference.getCurrentGatewayID();
            if (!TextUtils.isEmpty(currentGatewayID)) {
                final GatewayDataEntity gatewayDataEntity = gatewayManager.getGatewayInfo(currentGatewayID);
                if (gatewayDataEntity != null) {
                    ProgressDialogManager.getDialogManager().showDialog(GET_DATA, activity, null, null, activity.getResources().getInteger(R.integer.http_timeout));
                    deviceApiUnit.doGetGatewayVersion(gatewayDataEntity.getType(), new DeviceApiUnit.DeviceApiCommonListener<GatewayLastVersionBean>() {
                        @Override
                        public void onSuccess(GatewayLastVersionBean bean) {
                            ProgressDialogManager.getDialogManager().dimissDialog(GET_DATA, 0);
                            try {
                                String currentGatewayVersion = gatewayDataEntity.getSoftVer();
                                if (currentGatewayVersion.substring(currentGatewayVersion.length() - 3).compareTo(bean.version) >= 0) {
                                    doJump.run();
                                } else {
                                    //提醒升级网关
                                    if (noticeUpdateGatewayDialog == null || noticeUpdateGatewayDialog.getActivityContext() != activity) {
                                        WLDialog.Builder builder = new WLDialog.Builder(activity);
                                        noticeUpdateGatewayDialog = builder.setMessage(activity.getString(R.string.Gateway_version_is_too_low))
                                                .setCancelOnTouchOutSide(false)
                                                .setCancelable(false)
                                                .setPositiveButton(activity.getString(R.string.Tip_I_Known))
                                                .create();
                                    }
                                    if (!noticeUpdateGatewayDialog.isShowing()) {
                                        noticeUpdateGatewayDialog.show();
                                    }
                                }
                            } catch (Exception e) {
                                doJump.run();
                            }
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            ProgressDialogManager.getDialogManager().dimissDialog(GET_DATA, 0);
                            doJump.run();
                        }
                    });
                    //满足判断条件，才去查询，否则直接跳转，保证正常使用
                    return;
                }
            }
        }
        doJump.run();
    }
}
