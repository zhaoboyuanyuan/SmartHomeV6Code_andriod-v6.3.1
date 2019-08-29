package cc.wulian.smarthomev6.main.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cc.wulian.smarthomev6.main.home.widget.HomeItemBean;
import cc.wulian.smarthomev6.main.home.widget.HomeWidgetAdv;
import cc.wulian.smarthomev6.main.home.widget.HomeWidgetScene;
import cc.wulian.smarthomev6.main.home.widget.HomeWidgetVideo;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_01_AcoustoOptic;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_12_RegulateSwitch;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_16_Socket;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_22_UEI;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_23_UEI;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_25_Extraman;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_28_WaterValve;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_50_Socket;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_61_OneWaysSwitch;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_62_TwoWaysSwitch;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_63_ThreeWaysSwitch;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_65_MetalCurtain;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_70_TrickLock;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_77_MeteringSocket;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_80_CurtainElectricMotor;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_81_CurtainElectricMotor;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_82_Controller;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_90_LED;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_91_LED;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_A5_Doorbell02;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Af_Controller;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Ai_MeteringSwitch;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Aj_OneWaySwitch;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Am_OneWaysSwitch;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_An_TwoWaysSwitch;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Ao_MetalThreeWaysSwitch;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Ap_Thermostat;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Ar_MetalCurtain;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_At_TwoWaysSwitch;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Au_MetalCurtain;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Av_RegulateSwitch;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Aw_Switch;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Ax_Switch;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Ba_TwoWaysSocket;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Bc_NetLock;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Bd_NetLock;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Be_RegulateSwitch;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Bf_NetLock;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Bg_NetLock;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Bh_MetalCurtain;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Bm_Thermostat;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Bn_NetLock;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Bp_Controller;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Bq_NetLock;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Br_Controller;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Bs_Controller;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Bt_Socket;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Bu_Switch;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Bv_Switch;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Bw_Switch;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Bx_Thermostat;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_By_Controller;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_C0_Ad_Sensor;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_CMICA1_Cateye;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_CMICA2_Lookever;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_CMICA3_Penguin;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_CMICA4_Cylincam;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_CMICA5_Outdoor;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_CMICY1_Eques;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Ca_TriggerSwitch;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Cb_TriggerSwitch;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Cc_RegulateSwitch;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Cj_Thermostat;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Ck_Controller;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Cl_Switch;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Cm_Switch;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Cn_Switch;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Co_MetalCurtain;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_D9_Dream_Flower;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_DD_BGM;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Environment_Detection;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_HS05_Bridge;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_IF02_Wifi_IR;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Lc_CG22;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Lc_CG24;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Lc_CG26;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Entrance_Guard_CG27;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_OF_Ctrl;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_OP_GoldLock;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_OW_ZiJin;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Oj_Ctrl;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Ok_Controller;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_On_Controller;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Op_controller;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Qi_Controller;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Security_Sensor;
import cc.wulian.smarthomev6.main.home.widget.HomeWidget_Wish_BGM;
import cc.wulian.smarthomev6.main.home.widget.IWidgetLifeCycle;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * 作者: chao
 * 时间: 2017/5/8
 * 描述:  首页widget adapter
 * 联系方式: 805901025@qq.com
 */

public class HomeWidgetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "HomeWidgetAdapter";

    public static final int TYPE_ADV = -1;
    public static final int TYPE_SCENE = -2;
    public static final int TYPE_VIDEO = -3;
    public static final int TYPE_HEADER = -4;
    public static final int TYPE_FOOTER = -5;
    public static final int TYPE_ACOUSTO = DeviceInfoDictionary.getDeviceTypeID("01");
    public static final int TYPE_TRICK_LOCK = DeviceInfoDictionary.getDeviceTypeID("70");
    public static final int TYPE_CATEYE = DeviceInfoDictionary.getDeviceTypeID("CMICA1");
    public static final int TYPE_CMICA2 = DeviceInfoDictionary.getDeviceTypeID("CMICA2");
    public static final int TYPE_CMICA3 = DeviceInfoDictionary.getDeviceTypeID("CMICA3");
    public static final int TYPE_CMICY1 = DeviceInfoDictionary.getDeviceTypeID("CMICY1");
    public static final int TYPE_CMICA4 = DeviceInfoDictionary.getDeviceTypeID("CMICA4");
    public static final int TYPE_CMICA5 = DeviceInfoDictionary.getDeviceTypeID("CMICA5");
    public static final int TYPE_CMICA6 = DeviceInfoDictionary.getDeviceTypeID("CMICA6");
    public static final int TYPE_OP = DeviceInfoDictionary.getDeviceTypeID("OP");
    public static final int TYPE_Bc = DeviceInfoDictionary.getDeviceTypeID("Bc");
    public static final int TYPE_Bd = DeviceInfoDictionary.getDeviceTypeID("Bd");

    public static final int TYPE_80 = DeviceInfoDictionary.getDeviceTypeID("80");
    public static final int TYPE_Aj = DeviceInfoDictionary.getDeviceTypeID("Aj");
    public static final int TYPE_At = DeviceInfoDictionary.getDeviceTypeID("At");
    public static final int TYPE_Am = DeviceInfoDictionary.getDeviceTypeID("Am");
    public static final int TYPE_An = DeviceInfoDictionary.getDeviceTypeID("An");
    public static final int TYPE_Ao = DeviceInfoDictionary.getDeviceTypeID("Ao");
    public static final int TYPE_Ar = DeviceInfoDictionary.getDeviceTypeID("Ar");
    public static final int TYPE_OW = DeviceInfoDictionary.getDeviceTypeID("OW");
    public static final int TYPE_Bf = DeviceInfoDictionary.getDeviceTypeID("Bf");
    public static final int TYPE_Bg = DeviceInfoDictionary.getDeviceTypeID("Bg");
    public static final int TYPE_61 = DeviceInfoDictionary.getDeviceTypeID("61");
    public static final int TYPE_62 = DeviceInfoDictionary.getDeviceTypeID("62");
    public static final int TYPE_63 = DeviceInfoDictionary.getDeviceTypeID("63");
    public static final int TYPE_23 = DeviceInfoDictionary.getDeviceTypeID("23");
    public static final int TYPE_A5 = DeviceInfoDictionary.getDeviceTypeID("A5");
    public static final int TYPE_Ai = DeviceInfoDictionary.getDeviceTypeID("Ai");
    public static final int TYPE_81 = DeviceInfoDictionary.getDeviceTypeID("81");
    public static final int TYPE_65 = DeviceInfoDictionary.getDeviceTypeID("65");
    public static final int TYPE_77 = DeviceInfoDictionary.getDeviceTypeID("77");
    public static final int TYPE_12 = DeviceInfoDictionary.getDeviceTypeID("12");
    public static final int TYPE_Av = DeviceInfoDictionary.getDeviceTypeID("Av");
    public static final int TYPE_Bh = DeviceInfoDictionary.getDeviceTypeID("Bh");
    public static final int TYPE_Bm = DeviceInfoDictionary.getDeviceTypeID("Bm");
    public static final int TYPE_Ap = DeviceInfoDictionary.getDeviceTypeID("Ap");
    public static final int TYPE_Oi = DeviceInfoDictionary.getDeviceTypeID("Oi");
    public static final int TYPE_82 = DeviceInfoDictionary.getDeviceTypeID("82");
    public static final int TYPE_16 = DeviceInfoDictionary.getDeviceTypeID("16");
    public static final int TYPE_25 = DeviceInfoDictionary.getDeviceTypeID("25");
    public static final int TYPE_D7 = DeviceInfoDictionary.getDeviceTypeID("D7");
    public static final int TYPE_D9 = DeviceInfoDictionary.getDeviceTypeID("D9");
    public static final int TYPE_22 = DeviceInfoDictionary.getDeviceTypeID("22");
    public static final int TYPE_DD = DeviceInfoDictionary.getDeviceTypeID("DD");
    public static final int TYPE_OF = DeviceInfoDictionary.getDeviceTypeID("OF");
    public static final int TYPE_90 = DeviceInfoDictionary.getDeviceTypeID("90");
    public static final int TYPE_50 = DeviceInfoDictionary.getDeviceTypeID("50");
    public static final int TYPE_Oj = DeviceInfoDictionary.getDeviceTypeID("Oj");
    public static final int TYPE_Aw = DeviceInfoDictionary.getDeviceTypeID("Aw");
    public static final int TYPE_Ax = DeviceInfoDictionary.getDeviceTypeID("Ax");
    public static final int TYPE_Ba = DeviceInfoDictionary.getDeviceTypeID("Ba");
    public static final int TYPE_Be = DeviceInfoDictionary.getDeviceTypeID("Be");
    public static final int TYPE_28 = DeviceInfoDictionary.getDeviceTypeID("28");
    public static final int TYPE_24 = DeviceInfoDictionary.getDeviceTypeID("24");
    public static final int TYPE_Au = DeviceInfoDictionary.getDeviceTypeID("Au");
    public static final int TYPE_Ok = DeviceInfoDictionary.getDeviceTypeID("Ok");
    public static final int TYPE_HS05 = DeviceInfoDictionary.getDeviceTypeID("HS05");
    public static final int TYPE_XW01 = DeviceInfoDictionary.getDeviceTypeID("XW01");
    public static final int TYPE_IF02 = DeviceInfoDictionary.getDeviceTypeID("IF02");
    public static final int TYPE_On = DeviceInfoDictionary.getDeviceTypeID("On");
    public static final int TYPE_Bq = DeviceInfoDictionary.getDeviceTypeID("Bq");
    public static final int TYPE_Bn = DeviceInfoDictionary.getDeviceTypeID("Bn");
    public static final int TYPE_Af = DeviceInfoDictionary.getDeviceTypeID("Af");
    public static final int TYPE_Bp = DeviceInfoDictionary.getDeviceTypeID("Bp");
    public static final int TYPE_Br = DeviceInfoDictionary.getDeviceTypeID("Br");
    public static final int TYPE_Bt = DeviceInfoDictionary.getDeviceTypeID("Bt");
    public static final int TYPE_Bs = DeviceInfoDictionary.getDeviceTypeID("Bs");
    public static final int TYPE_Bu = DeviceInfoDictionary.getDeviceTypeID("Bu");
    public static final int TYPE_Bv = DeviceInfoDictionary.getDeviceTypeID("Bv");
    public static final int TYPE_Bw = DeviceInfoDictionary.getDeviceTypeID("Bw");
    public static final int TYPE_Bx = DeviceInfoDictionary.getDeviceTypeID("Bx");
    public static final int TYPE_By = DeviceInfoDictionary.getDeviceTypeID("By");
    public static final int TYPE_91 = DeviceInfoDictionary.getDeviceTypeID("91");
    public static final int TYPE_Cc = DeviceInfoDictionary.getDeviceTypeID("Cc");
    public static final int TYPE_Ca = DeviceInfoDictionary.getDeviceTypeID("Ca");
    public static final int TYPE_Cb = DeviceInfoDictionary.getDeviceTypeID("Cb");
    public static final int TYPE_CG22 = DeviceInfoDictionary.getDeviceTypeID("CG22");
    public static final int TYPE_CG23 = DeviceInfoDictionary.getDeviceTypeID("CG23");
    public static final int TYPE_CG24 = DeviceInfoDictionary.getDeviceTypeID("CG24");
    public static final int TYPE_CG25 = DeviceInfoDictionary.getDeviceTypeID("CG25");
    public static final int TYPE_Op = DeviceInfoDictionary.getDeviceTypeID("Op");
    public static final int TYPE_Cj = DeviceInfoDictionary.getDeviceTypeID("Cj");
    public static final int TYPE_CG26 = DeviceInfoDictionary.getDeviceTypeID("CG26");
    public static final int TYPE_Cl = DeviceInfoDictionary.getDeviceTypeID("Cl");
    public static final int TYPE_Cm = DeviceInfoDictionary.getDeviceTypeID("Cm");
    public static final int TYPE_Cn = DeviceInfoDictionary.getDeviceTypeID("Cn");
    public static final int TYPE_Ck = DeviceInfoDictionary.getDeviceTypeID("Ck");
    public static final int TYPE_C0 = DeviceInfoDictionary.getDeviceTypeID("C0");
    public static final int TYPE_Ad = DeviceInfoDictionary.getDeviceTypeID("Ad");
    public static final int TYPE_CG27 = DeviceInfoDictionary.getDeviceTypeID("CG27");
    public static final int TYPE_Co = DeviceInfoDictionary.getDeviceTypeID("Co");
    public static final int TYPE_SECURITY_SENSOR = -7;
    public static final int TYPE_ENVIRONMENT_DETECTION = -8;

    private Context context;
    private List<HomeItemBean> mList;
    private View mHeaderView;
    private View mFooterView;

    public HomeWidgetAdapter(Context context, List<HomeItemBean> mList) {
        this.context = context;
        this.mList = mList;
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public void setHeaderView(View headerView) {
        synchronized (this) {
            mHeaderView = headerView;
            notifyItemInserted(0);
        }
    }

    public View getFooterView() {
        return mFooterView;
    }

    public void setFooterView(View footerView) {
        synchronized (this) {
            mFooterView = footerView;
            notifyItemInserted(getItemCount());
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (TYPE_ADV == viewType) {
            return new DefaultWidgetHolder(new HomeWidgetAdv(context));
        } else if (TYPE_SCENE == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidgetScene(context));
        } else if (TYPE_VIDEO == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidgetVideo(context));
        } else if (TYPE_FOOTER == viewType) {
            return new DefaultWidgetHolder(mFooterView);
        } else if (TYPE_ACOUSTO == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_01_AcoustoOptic(context));
        } else if (TYPE_TRICK_LOCK == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_70_TrickLock(context));
        } else if (TYPE_CATEYE == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_CMICA1_Cateye(context));
        } else if (TYPE_CMICA2 == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_CMICA2_Lookever(context));
        } else if (TYPE_CMICA3 == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_CMICA3_Penguin(context));
        } else if (TYPE_CMICY1 == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_CMICY1_Eques(context));
        } else if (TYPE_CMICA4 == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_CMICA4_Cylincam(context));
        } else if (TYPE_CMICA5 == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_CMICA5_Outdoor(context));
        } else if (TYPE_CMICA6 == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_CMICA3_Penguin(context));
        } else if (TYPE_OP == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_OP_GoldLock(context));
        } else if (TYPE_Bc == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Bc_NetLock(context));
        } else if (TYPE_Bd == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Bd_NetLock(context));
        } else if (TYPE_80 == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_80_CurtainElectricMotor(context));
        } else if (TYPE_Aj == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Aj_OneWaySwitch(context));
        } else if (TYPE_At == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_At_TwoWaysSwitch(context));
        } else if (TYPE_Am == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Am_OneWaysSwitch(context));
        } else if (TYPE_An == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_An_TwoWaysSwitch(context));
        } else if (TYPE_Ao == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Ao_MetalThreeWaysSwitch(context));
        } else if (TYPE_Ar == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Ar_MetalCurtain(context));
        } else if (TYPE_OW == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_OW_ZiJin(context));
        } else if (TYPE_SECURITY_SENSOR == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Security_Sensor(context));
        } else if (TYPE_ENVIRONMENT_DETECTION == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Environment_Detection(context));
        } else if (TYPE_Bf == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Bf_NetLock(context));
        } else if (TYPE_Bg == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Bg_NetLock(context));
        } else if (TYPE_61 == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_61_OneWaysSwitch(context));
        } else if (TYPE_62 == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_62_TwoWaysSwitch(context));
        } else if (TYPE_63 == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_63_ThreeWaysSwitch(context));
        } else if (TYPE_23 == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_23_UEI(context));
        } else if (TYPE_A5 == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_A5_Doorbell02(context));
        } else if (TYPE_Ai == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Ai_MeteringSwitch(context));
        } else if (TYPE_81 == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_81_CurtainElectricMotor(context));
        } else if (TYPE_65 == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_65_MetalCurtain(context));
        } else if (TYPE_77 == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_77_MeteringSocket(context));
        } else if (TYPE_12 == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_12_RegulateSwitch(context));
        } else if (TYPE_Av == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Av_RegulateSwitch(context));
        } else if (TYPE_Bh == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Bh_MetalCurtain(context));
        } else if (TYPE_Bm == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Bm_Thermostat(context));
        } else if (TYPE_Ap == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Ap_Thermostat(context));
        } else if (TYPE_Oi == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Qi_Controller(context));
        } else if (TYPE_82 == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_82_Controller(context));
        } else if (TYPE_16 == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_16_Socket(context));
        } else if (TYPE_25 == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_25_Extraman(context));
        } else if (TYPE_D9 == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_D9_Dream_Flower(context));
        } else if (TYPE_22 == viewType || TYPE_24 == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_22_UEI(context));
        } else if (TYPE_DD == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_DD_BGM(context));
        } else if (TYPE_OF == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_OF_Ctrl(context));
        } else if (TYPE_90 == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_90_LED(context));
        } else if (TYPE_50 == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_50_Socket(context));
        } else if (TYPE_Oj == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Oj_Ctrl(context));
        } else if (TYPE_Aw == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Aw_Switch(context));
        } else if (TYPE_Ax == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Ax_Switch(context));
        } else if (TYPE_Ba == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Ba_TwoWaysSocket(context));
        } else if (TYPE_Be == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Be_RegulateSwitch(context));
        } else if (TYPE_28 == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_28_WaterValve(context));
        } else if (TYPE_Au == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Au_MetalCurtain(context));
        } else if (TYPE_Ok == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Ok_Controller(context));
        } else if (TYPE_HS05 == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_HS05_Bridge(context));
        } else if (TYPE_XW01 == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Wish_BGM(context));
        } else if (TYPE_IF02 == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_IF02_Wifi_IR(context));
        } else if (TYPE_On == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_On_Controller(context));
        } else if (TYPE_Bq == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Bq_NetLock(context));
        } else if (TYPE_Bn == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Bn_NetLock(context));
        } else if (TYPE_Af == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Af_Controller(context));
        } else if (TYPE_Bp == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Bp_Controller(context));
        } else if (TYPE_Br == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Br_Controller(context));
        } else if (TYPE_Bt == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Bt_Socket(context));
        } else if (TYPE_Bs == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Bs_Controller(context));
        } else if (TYPE_Bu == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Bu_Switch(context));
        } else if (TYPE_Bv == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Bv_Switch(context));
        } else if (TYPE_Bw == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Bw_Switch(context));
        } else if (TYPE_Bx == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Bx_Thermostat(context));
        } else if (TYPE_By == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_By_Controller(context));
        } else if (TYPE_91 == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_91_LED(context));
        } else if (TYPE_Cc == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Cc_RegulateSwitch(context));
        } else if (TYPE_Ca == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Ca_TriggerSwitch(context));
        } else if (TYPE_Cb == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Cb_TriggerSwitch(context));
        } else if (TYPE_CG22 == viewType || TYPE_CG23 == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Lc_CG22(context));
        } else if (TYPE_CG24 == viewType || TYPE_CG25 == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Lc_CG24(context));
        } else if (TYPE_Op == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Op_controller(context));
        } else if (TYPE_Cj == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Cj_Thermostat(context));
        } else if (TYPE_CG26 == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Lc_CG26(context));
        } else if (TYPE_Cl == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Cl_Switch(context));
        } else if (TYPE_Cm == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Cm_Switch(context));
        } else if (TYPE_Cn == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Cn_Switch(context));
        } else if (TYPE_Ck == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Ck_Controller(context));
        } else if (TYPE_C0 == viewType || TYPE_Ad == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_C0_Ad_Sensor(context));
        } else if (TYPE_CG27 == viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Entrance_Guard_CG27(context));
        }else if (TYPE_Co== viewType) {
            return new LifeCycleWidgetHolder(new HomeWidget_Co_MetalCurtain(context));
        }
        WLog.i("TAG", "onCreateViewHolder: " + viewType);
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (mHeaderView != null) {
            if (position == 0) {
                return;
            }
        }

        if (mFooterView != null) {
            if (position == getItemCount() - 1) {
                return;
            }
        }

        if (holder instanceof LifeCycleWidgetHolder) {
            ((LifeCycleWidgetHolder) holder).cycle.onBindViewHolder(mList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (mHeaderView == null && mFooterView == null) {
            return mList.size();
        } else if (mHeaderView == null && mFooterView != null) {
            return mList.size() + 1;
        } else if (mHeaderView != null && mFooterView == null) {
            return mList.size() + 1;
        } else {
            return mList.size() + 2;
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);

        if (holder instanceof LifeCycleWidgetHolder) {
            ((LifeCycleWidgetHolder) holder).cycle.onViewRecycled();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_FOOTER;
        } else {
            HomeItemBean homeItemBean = mList.get(position);
            return DeviceInfoDictionary.getDeviceTypeID(homeItemBean.getType());
        }
    }

    public void update(List<HomeItemBean> mList) {
        synchronized (this) {
            if (mList != null && mList.size() > 0) {
                WLog.i(TAG, "update");
                this.mList = mList;
                notifyDataSetChanged();
            }
        }
    }

    public void remove(HomeItemBean bean) {
        WLog.i(TAG, "remove");
        for (int i = 0, l = mList.size(); i < l; i++) {
            HomeItemBean b = mList.get(i);
            if (b.equals(bean)) {
                synchronized (this) {
                    mList.remove(i);
                    notifyItemRemoved(i);
                }
            }
        }
    }

    public void add(HomeItemBean bean) {
        WLog.i(TAG, "add");
        if (!(bean.isShow() && bean.getIsAdd())) {
            return;
        }
        for (int i = 0, l = mList.size(); i < l; i++) {
            HomeItemBean b = mList.get(i);
            if (bean.equals(b)) {
                return;
            }
        }
        for (int i = 0, l = mList.size(); i < l; i++) {
            HomeItemBean b = mList.get(i);
            if (bean.getSort() < b.getSort()) {
                synchronized (this) {
                    mList.add(i, bean);
                    notifyItemInserted(i);
                    break;
                }
            }

            if (i == l - 1) {
                synchronized (this) {
                    mList.add(bean);
                    notifyItemInserted(l);
                    break;
                }
            }

            HomeItemBean next = mList.get(i + 1);
            if (bean.getSort() < next.getSort()) {
                synchronized (this) {
                    mList.add(i + 1, bean);
                    notifyItemInserted(i + 1);
                    break;
                }
            }
        }
//        notifyItemInserted(mList.size());
    }

    private class DefaultWidgetHolder extends RecyclerView.ViewHolder {
        public DefaultWidgetHolder(View itemView) {
            super(itemView);
        }
    }

    private class LifeCycleWidgetHolder extends RecyclerView.ViewHolder {

        private IWidgetLifeCycle cycle;

        public LifeCycleWidgetHolder(View itemView) {
            super(itemView);

            cycle = (IWidgetLifeCycle) itemView;
        }
    }
}
