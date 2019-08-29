package cc.wulian.smarthomev6.main.device.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.MoreConfig;
import cc.wulian.smarthomev6.main.device.DeviceMoreRouter;
import cc.wulian.smarthomev6.main.device.IDeviceMore;
import cc.wulian.smarthomev6.main.device.device_22.Device22MoreClearStorage;
import cc.wulian.smarthomev6.main.device.device_23.Device23MoreClearStorage;
import cc.wulian.smarthomev6.main.device.device_70.Lock70AccountManageView;
import cc.wulian.smarthomev6.main.device.device_Ap.ApButtonShockView;
import cc.wulian.smarthomev6.main.device.device_Ap.ApButtonSoundView;
import cc.wulian.smarthomev6.main.device.device_Ap.ApReturnSettsView;
import cc.wulian.smarthomev6.main.device.device_Ap.ApSaveEnergyView;
import cc.wulian.smarthomev6.main.device.device_Ap.ApSystemChooseView;
import cc.wulian.smarthomev6.main.device.device_Ap.ApSystemResetView;
import cc.wulian.smarthomev6.main.device.device_Ap.ApTempScaleView;
import cc.wulian.smarthomev6.main.device.device_Az.DeviceMoreHelpView;
import cc.wulian.smarthomev6.main.device.device_Bg.BgLockAndRingManagerView;
import cc.wulian.smarthomev6.main.device.device_Bm.BmCalibrationIndoorView;
import cc.wulian.smarthomev6.main.device.device_Bm.BmCalibrationSurfaceView;
import cc.wulian.smarthomev6.main.device.device_Bm.BmReturnSettsView;
import cc.wulian.smarthomev6.main.device.device_Bm.BmSaveEnergyView;
import cc.wulian.smarthomev6.main.device.device_Bm.BmSystemChooseView;
import cc.wulian.smarthomev6.main.device.device_Bm.BmSystemResetView;
import cc.wulian.smarthomev6.main.device.device_Bq.BqLockAndRingManagerView;
import cc.wulian.smarthomev6.main.device.device_Bx.BxBacklashSetView;
import cc.wulian.smarthomev6.main.device.device_Bx.BxCalibrationIndoorView;
import cc.wulian.smarthomev6.main.device.device_Bx.BxCalibrationSurfaceView;
import cc.wulian.smarthomev6.main.device.device_Bx.BxOverTempProtectView;
import cc.wulian.smarthomev6.main.device.device_Bx.BxRestoreFactorySetView;
import cc.wulian.smarthomev6.main.device.device_Bx.BxSensorSettingView;
import cc.wulian.smarthomev6.main.device.device_Bx.BxSystemChooseView;
import cc.wulian.smarthomev6.main.device.device_Ck.CkCalibrationIndoorView;
import cc.wulian.smarthomev6.main.device.device_Ck.CkRestoreFactorySetView;
import cc.wulian.smarthomev6.main.device.device_Co.DeviceCoMoreView;
import cc.wulian.smarthomev6.main.device.device_bc.settingmore.LeaveHomeButtonView;
import cc.wulian.smarthomev6.main.device.device_bc.settingmore.UserManagerButtonView;
import cc.wulian.smarthomev6.main.device.device_bc.settingmore.WifiSettingButtonView;
import cc.wulian.smarthomev6.main.device.device_cj.CjCalibrationIndoorView;
import cc.wulian.smarthomev6.main.device.device_cj.CjCalibrationSurfaceView;
import cc.wulian.smarthomev6.main.device.device_dd.DDChoiceSourceView;
import cc.wulian.smarthomev6.main.device.device_ow.OwAccountManageView;
import cc.wulian.smarthomev6.main.device.lock_bd.BdDoorPanelView;
import cc.wulian.smarthomev6.main.device.lock_op.OpAccountManageView;
import cc.wulian.smarthomev6.main.device.more.AfSettingMoreView;
import cc.wulian.smarthomev6.main.device.more.AiOverloadProtectionView;
import cc.wulian.smarthomev6.main.device.more.AiRecoverStatusView;
import cc.wulian.smarthomev6.main.device.more.AjBindModeView;
import cc.wulian.smarthomev6.main.device.more.AjClearQuantityView;
import cc.wulian.smarthomev6.main.device.more.AjRecoverStateView;
import cc.wulian.smarthomev6.main.device.more.AtBindModeView;
import cc.wulian.smarthomev6.main.device.more.AtRecoverStateView;
import cc.wulian.smarthomev6.main.device.more.AwLEDLightView;
import cc.wulian.smarthomev6.main.device.more.AwModeView;
import cc.wulian.smarthomev6.main.device.more.B9InputOutputSettingView;
import cc.wulian.smarthomev6.main.device.more.BaClearQuantityView;
import cc.wulian.smarthomev6.main.device.more.BaOverloadProtectionView;
import cc.wulian.smarthomev6.main.device.more.BcBnSettinsView;
import cc.wulian.smarthomev6.main.device.more.BrSettingMoreView;
import cc.wulian.smarthomev6.main.device.more.BsSettingMoreView;
import cc.wulian.smarthomev6.main.device.more.BtRecoverStatusView;
import cc.wulian.smarthomev6.main.device.more.CaChangeModeView;
import cc.wulian.smarthomev6.main.device.more.CaMotorReverseView;
import cc.wulian.smarthomev6.main.device.more.DoorbellSnapshotView;
import cc.wulian.smarthomev6.main.device.more.OkSleepModeView;
import cc.wulian.smarthomev6.main.device.more.TouchIDUnlockView;
import cc.wulian.smarthomev6.main.device.more.TranslatorBacklightStateView;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.bean.GatewayInfoBean;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by Veev on 2017/6/6
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    MoreConfigAdapter
 */

public class MoreConfigAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int GROUP = 1;
    private static final int CUSTOM_LEAVE_HOME_BTN = 0x20;
    private static final int CUSTOM_WIFISETTING_BTN = 0x21;
    private static final int CUSTOM_OP_ACCOUNT_MANAGE = 0x22;
    private static final int CUSTOM_Bc_USERMANAGER = 0x23;
    private static final int CUSTOM_BD_DOOR_PANNEL = 0x24;

    private static final int CUSTOM_AJ_CLEAR_QUANTITY = 0x25;
    private static final int CUSTOM_AJ_RECOVER_STATE = 0x26;
    private static final int CUSTOM_AJ_BIND_MODE = 0x27;

    private static final int CUSTOM_AT_BIND_MODE = 0x28;
    private static final int CUSTOM_AT_RECOVER_STATE = 0x29;
    private static final int CUSTOM_OW_ACCOUNT_MANAGE = 0x2A;
    private static final int CUSTOM_BG_LOCK_RING = 0x2B;
    private static final int CUSTOM_CLEAR_CONTROLLER_STORAGE = 0x2C;
    private static final int CUSTOM_SET_OVERLOAD_PROTECTION = 0x2D;

    private static final int CUSTOM_BM_SYSTEM_CHOOSE = 0x2E;
    private static final int CUSTOM_BM_RETURN_SETTS = 0x2F;

    private static final int CUSTOM_BM_SAVE_ENERGY = 0x30;
    private static final int CUSTOM_BM_SYSTEM_RESET = 0x31;
    private static final int CUSTOM_BM_CALIBRATION_SURFACE = 0x32;
    private static final int CUSTOM_BM_CALIBRATION_INDOOR = 0x33;
    private static final int CUSTOM_AP_SYSTEM_CHOOSE = 0x35;
    private static final int CUSTOM_AP_RETURN_SETTS = 0x36;
    private static final int CUSTOM_AP_SAVE_ENERGY = 0x37;
    private static final int CUSTOM_AP_SYSTEM_RESET = 0x38;
    private static final int CUSTOM_AP_BUTTON_SOUND = 0x39;
    private static final int CUSTOM_AP_BUTTON_SHOCK = 0x3A;
    private static final int CUSTOM_AP_TEMP_SCALE = 0x3B;
    private static final int CUSTOM_22_CLEAR_CONTROLLER_STORAGE = 0x3C;
    private static final int CUSTOM_DD_CHOICE_SOURCE = 0x3D;

    private static final int CUSTOM_2013_HELP = 0x3F;
    private static final int CUSTOM_AW_LED = 0x40;
    private static final int CUSTOM_BA_CLEAR_QUANTITY = 0x41;
    private static final int CUSTOM_BA_SET_OVERLOAD_PROTECTION = 0x42;
    private static final int CUSTOM_70_ACCOUNT_MANAGE = 0x43;
    private static final int CUSTOM_OK_SET_SLEEP_MODE = 0x44;
    private static final int CUSTOM_B9_INPUT_OUTPUT_SETTING = 0x45;

    private static final int CUSTOM_Bo_BACKLIGHT_STATUS = 0x46;
    private static final int CUSTOM_AW_MODE = 0x47;
    private static final int CUSTOM_BQ_LOCK_RING = 0x48;
    private static final int CUSTOM_AF_MORE = 0x49;
    private static final int CUSTOM_BR_MORE = 0x50;
    private static final int CUSTOM_BS_MORE = 0x51;
    private static final int CUSTOM_BOOR_SNAPSHOT = 0x52;
    private static final int CUSTOM_TOUCHID_UNLOCK = 0x53;
    private static final int CUSTOM_AI_RECOVER_STATUS = 0x54;
    private static final int CUSTOM_BX_BACKLASH_SET = 0x55;
    private static final int CUSTOM_BX_RESTORE_FACTORY_SET = 0x56;
    private static final int CUSTOM_BX_SYSTEM_CHOOSE = 0x57;
    private static final int CUSTOM_BX_OVER_TEMP_PROTECT = 0x58;
    private static final int CUSTOM_BX_CALIBRATION_INDOOR = 0x59;
    private static final int CUSTOM_BX_CALIBRATION_SURFACE = 0x5A;
    private static final int CUSTOM_BX_SENSOR_SETTING = 0x5B;
    private static final int CUSTOM_BT_RECOVER_STATUS = 0x5C;
    private static final int CUSTOM_CA_MOTOR_REVERSE = 0x5D;
    private static final int CUSTOM_CA_CHANGE_MODE = 0x5E;
    private static final int CUSTOM_LOCK_SETTINGS = 0x5F;
    private static final int CUSTOM_CJ_CALIBRATION_INDOOR = 0x60;
    private static final int CUSTOM_CJ_CALIBRATION_SURFACE = 0x61;
    private static final int CUSTOM_CK_CALIBRATION_INDOOR = 0x62;
    private static final int CUSTOM_CK_RESTORE_FACTORY_SET = 0x63;
    private static final int CUSTOM_CO_SETTINGS = 0x64;
    private Context mContext;
    private List<MoreConfig.ItemBean> list;
    private Device device;
    private List<CustomHolder> mCustomHolders = new ArrayList<>();

    public void recycleAll() {
        for (CustomHolder holder : mCustomHolders) {
            holder.custom.onViewRecycled();
        }
    }

    public MoreConfigAdapter(Context context, List<MoreConfig.ItemBean> list) {
        this.mContext = context;
        this.list = list;
    }

    public MoreConfigAdapter(Context mContext, List<MoreConfig.ItemBean> list, Device device) {
        this.mContext = mContext;
        this.list = list;
        this.device = device;
    }

    public void updateDeviceMode(Device device) {
        this.device = device;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        if (viewType == GROUP) {
            View itemView = layoutInflater.inflate(R.layout.item_device_more_group, parent, false);
            return new GroupHolder(itemView);
        }

        CustomHolder holder = null;
        if (viewType == CUSTOM_LEAVE_HOME_BTN) {
            holder = new CustomHolder(new LeaveHomeButtonView(mContext));
        } else if (viewType == CUSTOM_WIFISETTING_BTN) {
            holder = new CustomHolder(new WifiSettingButtonView(mContext));
        } else if (viewType == CUSTOM_OP_ACCOUNT_MANAGE) {
            holder = new CustomHolder(new OpAccountManageView(mContext));
        } else if (viewType == CUSTOM_Bc_USERMANAGER) {
            holder = new CustomHolder(new UserManagerButtonView(mContext));
        } else if (viewType == CUSTOM_BD_DOOR_PANNEL) {
            holder = new CustomHolder(new BdDoorPanelView(mContext));
        } else if (viewType == CUSTOM_AJ_CLEAR_QUANTITY) {
            holder = new CustomHolder(new AjClearQuantityView(mContext, device.devID, device.gwID));
        } else if (viewType == CUSTOM_AJ_RECOVER_STATE) {
            holder = new CustomHolder(new AjRecoverStateView(mContext));
        } else if (viewType == CUSTOM_AJ_BIND_MODE) {
            holder = new CustomHolder(new AjBindModeView(mContext));
        } else if (viewType == CUSTOM_AT_BIND_MODE) {
            holder = new CustomHolder(new AtBindModeView(mContext));
        } else if (viewType == CUSTOM_AT_RECOVER_STATE) {
            holder = new CustomHolder(new AtRecoverStateView(mContext));
        } else if (viewType == CUSTOM_OW_ACCOUNT_MANAGE) {
            holder = new CustomHolder(new OwAccountManageView(mContext));
        } else if (viewType == CUSTOM_BG_LOCK_RING) {
            holder = new CustomHolder(new BgLockAndRingManagerView(mContext));
        } else if (viewType == CUSTOM_CLEAR_CONTROLLER_STORAGE) {
            holder = new CustomHolder(new Device23MoreClearStorage(mContext));
        } else if (viewType == CUSTOM_SET_OVERLOAD_PROTECTION) {
            holder = new CustomHolder(new AiOverloadProtectionView(mContext, device.devID, device.gwID));
        } else if (viewType == CUSTOM_BM_SYSTEM_CHOOSE) {
            holder = new CustomHolder(new BmSystemChooseView(mContext));
        } else if (viewType == CUSTOM_BM_RETURN_SETTS) {
            holder = new CustomHolder(new BmReturnSettsView(mContext));
        } else if (viewType == CUSTOM_BM_SAVE_ENERGY) {
            holder = new CustomHolder(new BmSaveEnergyView(mContext));
        } else if (viewType == CUSTOM_BM_CALIBRATION_SURFACE) {
            holder = new CustomHolder(new BmCalibrationSurfaceView(mContext));
        } else if (viewType == CUSTOM_BM_CALIBRATION_INDOOR) {
            holder = new CustomHolder(new BmCalibrationIndoorView(mContext));
        } else if (viewType == CUSTOM_BM_SYSTEM_RESET) {
            holder = new CustomHolder(new BmSystemResetView(mContext));
        } else if (viewType == CUSTOM_AP_SYSTEM_CHOOSE) {
            holder = new CustomHolder(new ApSystemChooseView(mContext));
        } else if (viewType == CUSTOM_AP_RETURN_SETTS) {
            holder = new CustomHolder(new ApReturnSettsView(mContext));
        } else if (viewType == CUSTOM_AP_SAVE_ENERGY) {
            holder = new CustomHolder(new ApSaveEnergyView(mContext));
        } else if (viewType == CUSTOM_AP_SYSTEM_RESET) {
            holder = new CustomHolder(new ApSystemResetView(mContext));
        } else if (viewType == CUSTOM_AP_BUTTON_SOUND) {
            holder = new CustomHolder(new ApButtonSoundView(mContext));
        } else if (viewType == CUSTOM_AP_BUTTON_SHOCK) {
            holder = new CustomHolder(new ApButtonShockView(mContext));
        } else if (viewType == CUSTOM_AP_TEMP_SCALE) {
            holder = new CustomHolder(new ApTempScaleView(mContext));
        } else if (viewType == CUSTOM_22_CLEAR_CONTROLLER_STORAGE) {
            holder = new CustomHolder(new Device22MoreClearStorage(mContext));
        } else if (viewType == CUSTOM_DD_CHOICE_SOURCE) {
            holder = new CustomHolder(new DDChoiceSourceView(mContext));
        } else if (viewType == CUSTOM_2013_HELP) {
            holder = new CustomHolder(new DeviceMoreHelpView(mContext));
        } else if (viewType == CUSTOM_AW_LED) {
            holder = new CustomHolder(new AwLEDLightView(mContext, device.devID, device.gwID));
        } else if (viewType == CUSTOM_AW_MODE) {
            holder = new CustomHolder(new AwModeView(mContext, device.devID, device.gwID));
        } else if (viewType == CUSTOM_BA_CLEAR_QUANTITY) {
            holder = new CustomHolder(new BaClearQuantityView(mContext, device.devID, device.gwID));
        } else if (viewType == CUSTOM_BA_SET_OVERLOAD_PROTECTION) {
            holder = new CustomHolder(new BaOverloadProtectionView(mContext, device.devID, device.gwID));
        } else if (viewType == CUSTOM_70_ACCOUNT_MANAGE) {
            holder = new CustomHolder(new Lock70AccountManageView(mContext));
        } else if (viewType == CUSTOM_OK_SET_SLEEP_MODE) {
            holder = new CustomHolder(new OkSleepModeView(mContext, device.devID, device.gwID));
        } else if (viewType == CUSTOM_B9_INPUT_OUTPUT_SETTING) {
            holder = new CustomHolder(new B9InputOutputSettingView(mContext));
        } else if (viewType == CUSTOM_Bo_BACKLIGHT_STATUS) {
            holder = new CustomHolder(new TranslatorBacklightStateView(mContext));
        } else if (viewType == CUSTOM_BQ_LOCK_RING) {
            holder = new CustomHolder(new BqLockAndRingManagerView(mContext));
        } else if (viewType == CUSTOM_AF_MORE) {
            holder = new CustomHolder(new AfSettingMoreView(mContext, device.devID, device.gwID));
        } else if (viewType == CUSTOM_BR_MORE) {
            holder = new CustomHolder(new BrSettingMoreView(mContext, device.devID, device.gwID));
        } else if (viewType == CUSTOM_BS_MORE) {
            holder = new CustomHolder(new BsSettingMoreView(mContext, device.devID, device.gwID));
        } else if (viewType == CUSTOM_BOOR_SNAPSHOT) {
            holder = new CustomHolder(new DoorbellSnapshotView(mContext, device.devID, device.gwID));
        } else if (viewType == CUSTOM_TOUCHID_UNLOCK) {
            holder = new CustomHolder(new TouchIDUnlockView(mContext, device.devID, device.gwID));
        } else if (viewType == CUSTOM_AI_RECOVER_STATUS) {
            holder = new CustomHolder(new AiRecoverStatusView(mContext, device.devID, device.gwID));
        } else if (viewType == CUSTOM_BX_BACKLASH_SET) {
            holder = new CustomHolder(new BxBacklashSetView(mContext, device.devID, device.gwID));
        } else if (viewType == CUSTOM_BX_RESTORE_FACTORY_SET) {
            holder = new CustomHolder(new BxRestoreFactorySetView(mContext, device.devID, device.gwID));
        } else if (viewType == CUSTOM_BX_SYSTEM_CHOOSE) {
            holder = new CustomHolder(new BxSystemChooseView(mContext));
        } else if (viewType == CUSTOM_BX_OVER_TEMP_PROTECT) {
            holder = new CustomHolder(new BxOverTempProtectView(mContext, device.devID, device.gwID));
        } else if (viewType == CUSTOM_BX_CALIBRATION_INDOOR) {
            holder = new CustomHolder(new BxCalibrationIndoorView(mContext));
        } else if (viewType == CUSTOM_BX_CALIBRATION_SURFACE) {
            holder = new CustomHolder(new BxCalibrationSurfaceView(mContext));
        } else if (viewType == CUSTOM_BX_SENSOR_SETTING) {
            holder = new CustomHolder(new BxSensorSettingView(mContext));
        } else if (viewType == CUSTOM_BT_RECOVER_STATUS) {
            holder = new CustomHolder(new BtRecoverStatusView(mContext, device.devID, device.gwID));
        } else if (viewType == CUSTOM_CA_MOTOR_REVERSE) {
            holder = new CustomHolder(new CaMotorReverseView(mContext, device.devID, device.gwID));
        } else if (viewType == CUSTOM_CA_CHANGE_MODE) {
            holder = new CustomHolder(new CaChangeModeView(mContext));
        } else if (viewType == CUSTOM_LOCK_SETTINGS) {
            holder = new CustomHolder(new BcBnSettinsView(mContext, device.devID, device.gwID));
        } else if (viewType == CUSTOM_CJ_CALIBRATION_INDOOR) {
            holder = new CustomHolder(new CjCalibrationIndoorView(mContext));
        } else if (viewType == CUSTOM_CJ_CALIBRATION_SURFACE) {
            holder = new CustomHolder(new CjCalibrationSurfaceView(mContext));
        } else if (viewType == CUSTOM_CK_CALIBRATION_INDOOR) {
            holder = new CustomHolder(new CkCalibrationIndoorView(mContext));
        } else if (viewType == CUSTOM_CK_RESTORE_FACTORY_SET) {
            holder = new CustomHolder(new CkRestoreFactorySetView(mContext, device.devID, device.gwID));
        } else if (viewType == CUSTOM_CO_SETTINGS) {
            holder = new CustomHolder(new DeviceCoMoreView(mContext));
        }
        if (holder != null) {
            mCustomHolders.add(holder);
            return holder;
        }

        View itemView = layoutInflater.inflate(R.layout.item_device_more_item, parent, false);
        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final MoreConfig.ItemBean bean = list.get(position);
        if (viewHolder instanceof ItemHolder) {
            ((ItemHolder) viewHolder).itemName.setText(bean.name);
            if (TextUtils.isEmpty(bean.desc)) {
                ((ItemHolder) viewHolder).tv_desc.setVisibility(View.GONE);
            } else {
                ((ItemHolder) viewHolder).tv_desc.setVisibility(View.VISIBLE);
                ((ItemHolder) viewHolder).tv_desc.setText(bean.desc);
            }

            boolean offLineDisable = false;
            // 离线不可点  并且  设备不在线
            // 离线不可用  的优先级高于  网关登录不可用
            if (bean.offLineDisable && device != null && !device.isOnLine()) {
                offLineDisable = true;
                viewHolder.itemView.setEnabled(false);
                ((ItemHolder) viewHolder).itemView.setAlpha(0.54f);
            } else {
                viewHolder.itemView.setEnabled(true);
                ((ItemHolder) viewHolder).itemView.setAlpha(1f);
            }

            // 处理网关登录  不可用的情况
            // enableWithEnterType 这个属性必须设置
            // enableWithEnterType 这个属性 与 登录状态不同
            if (!TextUtils.isEmpty(bean.enableWithEnterType) &&
                    !TextUtils.equals(bean.enableWithEnterType, Preference.getPreferences().getUserEnterType())) {
                viewHolder.itemView.setEnabled(false);
                ((ItemHolder) viewHolder).itemName.setAlpha(0.54f);
            } else {
                if (!offLineDisable) {
                    viewHolder.itemView.setEnabled(true);
                    ((ItemHolder) viewHolder).itemName.setAlpha(1f);
                }
            }

            if (bean.action.startsWith("jump")) {
                final String key = bean.action.substring(bean.action.indexOf(":") + 1);
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Class clazz = DeviceMoreRouter.getClass(key);
                        if (TextUtils.equals(key, "Lock_Message")) {
                            if (!TextUtils.isEmpty(Preference.getPreferences().getCurrentGatewayInfo())) {
                                GatewayInfoBean gatewayInfoBean = JSON.parseObject(Preference.getPreferences().getCurrentGatewayInfo(), GatewayInfoBean.class);
                                if (!TextUtils.isEmpty(gatewayInfoBean.gwType) && TextUtils.equals("GW14", gatewayInfoBean.gwType)) {
                                    ToastUtil.show(R.string.Toast_Litegateway_Sms);
                                    return;
                                }
                            }
                        }
                        if (clazz != null) {
                            Intent intent = new Intent(mContext, clazz);

                            for (MoreConfig.ParamBean b : bean.param) {
                                if ("string".equals(b.type)) {
                                    intent.putExtra(b.key, b.value);
                                }
                            }
                            mContext.startActivity(intent);
                        }
                    }
                });
            }
        }

        if (viewHolder instanceof GroupHolder) {
            if (TextUtils.isEmpty(bean.name)) {
                ((GroupHolder) viewHolder).groupName.setVisibility(View.GONE);
                ((GroupHolder) viewHolder).groupName.setText(bean.name);
            } else {
                ((GroupHolder) viewHolder).groupName.setVisibility(View.VISIBLE);
                ((GroupHolder) viewHolder).groupName.setText(bean.name);
            }
        }

        if (viewHolder instanceof CustomHolder) {
            ((CustomHolder) viewHolder).custom.onBindView(bean);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.equals(list.get(position).type, "group")) {
            return GROUP;
        }

        if (TextUtils.equals(list.get(position).type, "custom")) {
            String action = list.get(position).action;
            if (TextUtils.equals(action, "custom:LeaveHomeBtn")) {
                return CUSTOM_LEAVE_HOME_BTN;
            } else if (TextUtils.equals(action, "custom:DevBc_wifiSetting")) {
                return CUSTOM_WIFISETTING_BTN;
            } else if (TextUtils.equals(action, "custom:OP_Account_Manage")) {
                return CUSTOM_OP_ACCOUNT_MANAGE;
            } else if (TextUtils.equals(action, "custom:DevBc_userManager")) {
                return CUSTOM_Bc_USERMANAGER;
            } else if (TextUtils.equals(action, "custom:Bd_Door_Panel")) {
                return CUSTOM_BD_DOOR_PANNEL;
            } else if (TextUtils.equals(action, "custom:Aj_Clear_Quantity")) {
                return CUSTOM_AJ_CLEAR_QUANTITY;
            } else if (TextUtils.equals(action, "custom:Aj_Recover_State")) {
                return CUSTOM_AJ_RECOVER_STATE;
            } else if (TextUtils.equals(action, "custom:Aj_Bind_Mode")) {
                return CUSTOM_AJ_BIND_MODE;
            } else if (TextUtils.equals(action, "custom:At_Bind_Mode")) {
                return CUSTOM_AT_BIND_MODE;
            } else if (TextUtils.equals(action, "custom:At_Recover_State")) {
                return CUSTOM_AT_RECOVER_STATE;
            } else if (TextUtils.equals(action, "custom:OW_Account_Manage")) {
                return CUSTOM_OW_ACCOUNT_MANAGE;
            } else if (TextUtils.equals(action, "custom:bg_lock_ring")) {
                return CUSTOM_BG_LOCK_RING;
            } else if (TextUtils.equals(action, "custom:Bf_Door_Panel")) {
                return CUSTOM_BD_DOOR_PANNEL;
            } else if (TextUtils.equals(action, "custom:Bg_Door_Panel")) {
                return CUSTOM_BD_DOOR_PANNEL;
            } else if (TextUtils.equals(action, "custom:Clear_Controller_Storage")) {
                return CUSTOM_CLEAR_CONTROLLER_STORAGE;
            } else if (TextUtils.equals(action, "custom:Set_Overload_Protection")) {
                return CUSTOM_SET_OVERLOAD_PROTECTION;
            } else if (TextUtils.equals(action, "custom:Bm_System_Choose")) {
                return CUSTOM_BM_SYSTEM_CHOOSE;
            } else if (TextUtils.equals(action, "custom:Bm_Return_Setts")) {
                return CUSTOM_BM_RETURN_SETTS;
            } else if (TextUtils.equals(action, "custom:Bm_Save_Energy")) {
                return CUSTOM_BM_SAVE_ENERGY;
            } else if (TextUtils.equals(action, "custom:Bm_Calibration_Surface")) {
                return CUSTOM_BM_CALIBRATION_SURFACE;
            } else if (TextUtils.equals(action, "custom:Bm_Calibration_Indoor")) {
                return CUSTOM_BM_CALIBRATION_INDOOR;
            } else if (TextUtils.equals(action, "custom:Bm_System_Reset")) {
                return CUSTOM_BM_SYSTEM_RESET;
            } else if (TextUtils.equals(action, "custom:Ap_System_Choose")) {
                return CUSTOM_AP_SYSTEM_CHOOSE;
            } else if (TextUtils.equals(action, "custom:Ap_Return_Setts")) {
                return CUSTOM_AP_RETURN_SETTS;
            } else if (TextUtils.equals(action, "custom:Ap_Save_Energy")) {
                return CUSTOM_AP_SAVE_ENERGY;
            } else if (TextUtils.equals(action, "custom:Ap_System_Reset")) {
                return CUSTOM_AP_SYSTEM_RESET;
            } else if (TextUtils.equals(action, "custom:Ap_Button_Sound")) {
                return CUSTOM_AP_BUTTON_SOUND;
            } else if (TextUtils.equals(action, "custom:Ap_Button_Shock")) {
                return CUSTOM_AP_BUTTON_SHOCK;
            } else if (TextUtils.equals(action, "custom:Ap_Temp_Scale")) {
                return CUSTOM_AP_TEMP_SCALE;
            } else if (TextUtils.equals(action, "custom:22_Clear_Controller_Storage")) {
                return CUSTOM_22_CLEAR_CONTROLLER_STORAGE;
            } else if (TextUtils.equals(action, "custom:DD_Choice_Source")) {
                return CUSTOM_DD_CHOICE_SOURCE;
            } else if (TextUtils.equals(action, "custom:2013_help")) {
                return CUSTOM_2013_HELP;
            } else if (TextUtils.equals(action, "custom:aw_led")) {
                return CUSTOM_AW_LED;
            } else if (TextUtils.equals(action, "custom:aw_mode")) {
                return CUSTOM_AW_MODE;
            } else if (TextUtils.equals(action, "custom:Ba_Clear_Quantity")) {
                return CUSTOM_BA_CLEAR_QUANTITY;
            } else if (TextUtils.equals(action, "custom:Ba_Set_Overload_Protection")) {
                return CUSTOM_BA_SET_OVERLOAD_PROTECTION;
            } else if (TextUtils.equals(action, "custom:70_Account_Manage")) {
                return CUSTOM_70_ACCOUNT_MANAGE;
            } else if (TextUtils.equals(action, "custom:Ok_Set_Sleep_Mode")) {
                return CUSTOM_OK_SET_SLEEP_MODE;
            } else if (TextUtils.equals(action, "custom:B9_input_output_setting")) {
                return CUSTOM_B9_INPUT_OUTPUT_SETTING;
            } else if (TextUtils.equals(action, "custom:Bo_Backlight_Status")) {
                return CUSTOM_Bo_BACKLIGHT_STATUS;
            } else if (TextUtils.equals(action, "custom:bq_lock_ring")) {
                return CUSTOM_BQ_LOCK_RING;
            } else if (TextUtils.equals(action, "custom:af_more_setting")) {
                return CUSTOM_AF_MORE;
            } else if (TextUtils.equals(action, "custom:br_more_setting")) {
                return CUSTOM_BR_MORE;
            } else if (TextUtils.equals(action, "custom:bs_more_setting")) {
                return CUSTOM_BS_MORE;
            } else if (TextUtils.equals(action, "custom:door_ring_snapshot")) {
                return CUSTOM_BOOR_SNAPSHOT;
            } else if (TextUtils.equals(action, "custom:touchid_unlock")) {
                return CUSTOM_TOUCHID_UNLOCK;
            } else if (TextUtils.equals(action, "custom:ai_recover_status")) {
                return CUSTOM_AI_RECOVER_STATUS;
            } else if (TextUtils.equals(action, "custom:Bx_Return_Setts")) {
                return CUSTOM_BX_BACKLASH_SET;
            } else if (TextUtils.equals(action, "custom:Bx_System_Reset")) {
                return CUSTOM_BX_RESTORE_FACTORY_SET;
            } else if (TextUtils.equals(action, "custom:Bx_System_Choose")) {
                return CUSTOM_BX_SYSTEM_CHOOSE;
            } else if (TextUtils.equals(action, "custom:Bx_Return_protect")) {
                return CUSTOM_BX_OVER_TEMP_PROTECT;
            } else if (TextUtils.equals(action, "custom:Bx_Calibration_Indoor")) {
                return CUSTOM_BX_CALIBRATION_INDOOR;
            } else if (TextUtils.equals(action, "custom:Bx_Calibration_Surface")) {
                return CUSTOM_BX_CALIBRATION_SURFACE;
            } else if (TextUtils.equals(action, "custom:Bx_Sensor_Setting")) {
                return CUSTOM_BX_SENSOR_SETTING;
            } else if (TextUtils.equals(action, "custom:Bt_recover_status")) {
                return CUSTOM_BT_RECOVER_STATUS;
            } else if (TextUtils.equals(action, "custom:motorInversion")) {
                return CUSTOM_CA_MOTOR_REVERSE;
            } else if (TextUtils.equals(action, "custom:changeMode")) {
                return CUSTOM_CA_CHANGE_MODE;
            } else if (TextUtils.equals(action, "custom:DevBc_settings")) {
                return CUSTOM_LOCK_SETTINGS;
            } else if (TextUtils.equals(action, "custom:Cj_Calibration_Indoor")) {
                return CUSTOM_CJ_CALIBRATION_INDOOR;
            } else if (TextUtils.equals(action, "custom:Cj_Calibration_Surface")) {
                return CUSTOM_CJ_CALIBRATION_SURFACE;
            } else if (TextUtils.equals(action, "custom:Ck_Calibration_Indoor")) {
                return CUSTOM_CK_CALIBRATION_INDOOR;
            } else if (TextUtils.equals(action, "custom:Ck_System_Reset")) {
                return CUSTOM_CK_RESTORE_FACTORY_SET;
            } else if (TextUtils.equals(action, "custom:Co_Settings")) {
                return CUSTOM_CO_SETTINGS;
            }


        }

        return ITEM;
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);

        if (holder instanceof CustomHolder) {
            mCustomHolders.remove(holder);
            ((CustomHolder) holder).custom.onViewRecycled();
        }
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        private TextView itemName, tv_desc;

        public ItemHolder(View itemView) {
            super(itemView);

            itemName = (TextView) itemView.findViewById(R.id.item_name);
            tv_desc = (TextView) itemView.findViewById(R.id.tv_desc);
        }
    }

    class GroupHolder extends RecyclerView.ViewHolder {

        private TextView groupName;

        public GroupHolder(View itemView) {
            super(itemView);

            groupName = (TextView) itemView.findViewById(R.id.group_name);
        }
    }

    class CustomHolder extends RecyclerView.ViewHolder {

        private IDeviceMore custom;

        public CustomHolder(View itemView) {
            super(itemView);

            custom = (IDeviceMore) itemView;
        }
    }
}
