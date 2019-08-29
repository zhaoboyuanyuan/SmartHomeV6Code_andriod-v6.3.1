package cc.wulian.smarthomev6.support.core.device;

import android.content.Context;
import android.text.TextUtils;

import java.util.HashMap;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.DeviceActivationActivity;
import cc.wulian.smarthomev6.main.device.DeviceDetailActivity;
import cc.wulian.smarthomev6.main.device.camera_lc.LcOutdoorCameraActivity;
import cc.wulian.smarthomev6.main.device.camera_lc.LcPTZCameraActivity;
import cc.wulian.smarthomev6.main.device.camera_lc.NVRDeviceListActivity;
import cc.wulian.smarthomev6.main.device.cateye.CateyeDetailActivity;
import cc.wulian.smarthomev6.main.device.cylincam.CylincamPlayActivity;
import cc.wulian.smarthomev6.main.device.device_12.RegulateSwitchActivity;
import cc.wulian.smarthomev6.main.device.device_13.RegulateSwitch13Activity;
import cc.wulian.smarthomev6.main.device.device_22.Device22Activity;
import cc.wulian.smarthomev6.main.device.device_23.Device23Activity;
import cc.wulian.smarthomev6.main.device.device_91.Device91Activity;
import cc.wulian.smarthomev6.main.device.device_Av.RegulateSwitchAvActivity;
import cc.wulian.smarthomev6.main.device.device_Be.RegulateSwitchBeActivity;
import cc.wulian.smarthomev6.main.device.device_Bn.DeviceBnCameraActivity;
import cc.wulian.smarthomev6.main.device.device_CG27.VideoScreenActivity;
import cc.wulian.smarthomev6.main.device.device_Cc.RegulateSwitchCcActivity;
import cc.wulian.smarthomev6.main.device.device_bc.DeviceBcCameraActivity;
import cc.wulian.smarthomev6.main.device.device_if02.WifiIRActivity;
import cc.wulian.smarthomev6.main.device.eques.EquesPlayActivity;
import cc.wulian.smarthomev6.main.device.gateway_mini.device_d8.DeviceD8Activity;
import cc.wulian.smarthomev6.main.device.lookever.LookeverDetailActivity;
import cc.wulian.smarthomev6.main.device.outdoor.OutdoorDetailActivity;
import cc.wulian.smarthomev6.main.device.penguin.PenguinDetailActivity;
import cc.wulian.smarthomev6.main.device.safeDog.SafeDogMainActivity;
import cc.wulian.smarthomev6.support.tools.HttpUrlKey;

/**
 * Created by zbl on 2017/4/7.
 * 设备信息字典<p/>
 * 设备type到资源的映射
 */

public class DeviceInfoDictionary {

    public static final String DEVICE_NAME_PLACE_HOLDER = "#$default$#";

    private static final HashMap<String, DeviceInfoDataBean> deviceDataMap = new HashMap<>();

    /**
     * type         设备的type
     * typeID       这个自己定, 只要和之前的不重复就行
     * <p>
     * hasWidget    没有widget 或者 没有添加widget之前  请用 【false】 表示
     * hasWidget    没有widget 或者 没有添加widget之前  请用 【false】 表示
     * hasWidget    没有widget 或者 没有添加widget之前  请用 【false】 表示
     */
    private static final Object[][] deviceInfoData = new Object[][]{
            // type		typeId		iconRes     nameRes     category        url     hasWidget
            // 声光报警器
            {"01", 1, R.drawable.icon_volalarm, R.string.Device_Default_Name_01, 5, HttpUrlKey.URL_DEVICE_01, true},
            // 红外入侵监测器
            {"02", 2, R.drawable.icon_infrared, R.string.Device_Default_Name_02, 5, HttpUrlKey.URL_DEVICE_02, false},
            // 门磁感应器
            {"03", 3, R.drawable.icon_door_sensor, R.string.Device_Default_Name_03, 5, HttpUrlKey.URL_DEVICE_03, false},
            // 水浸传感器
            {"06", 6, R.drawable.icon_water, R.string.Device_Default_Name_06, 5, HttpUrlKey.URL_DEVICE_06, false},
            // 可燃气体泄漏探测器
            {"09", 9, R.drawable.icon_gas, R.string.Device_Default_Name_09, 5, HttpUrlKey.URL_DEVICE_09, false},
            // 温湿度检测器
            {"17", 17, R.drawable.icon_temp_hum, R.string.Device_Default_Name_17, 6, HttpUrlKey.URL_DEVICE_17, false},
            // 烟雾探测器
            {"43", 43, R.drawable.icon_smoke, R.string.Device_Default_Name_43, 5, HttpUrlKey.URL_DEVICE_43, false},
            // 墙面插座
            {"50", 50, R.drawable.icon_device_wall_socket, R.string.Device_Default_Name_50, 4, HttpUrlKey.URL_DEVICE_50, true},
            // 密码门锁
            {"70", 70, R.drawable.icon_lock_70, R.string.Device_Default_Name_70, 1, HttpUrlKey.URL_DEVICE_70, true},
            // 二键单零火开关
//            {"Aw", 126, R.drawable.icon_double_button_switch, R.string.Device_Default_Name_Aw, 3, HttpUrlKey.URL_DEVICE_AW, false},
            // 智能猫眼
            {"CMICA1", 127, R.drawable.icon_cateye, R.string.Device_Default_Name_CMICA1, 2, null, true},
            // 玄武湖系列网络锁
            {"OP", 128, R.drawable.icon_gold_lock, R.string.Xuanwulakeseries_Devicelist_Networklock, 1, HttpUrlKey.URL_DEVICE_OP, true},
            // 智能猫眼mini
            {"CMICY1", 129, R.drawable.icon_eques, R.string.Cateyemini_Adddevice_Cateyemini, 2, null, true},
            // 网络智能锁
            {"Bc", 130, R.drawable.icon_bc_visualdoorlock, R.string.Device_List_Networklock, 1, HttpUrlKey.URL_DEVICE_Bc, true},
            // 随便看摄像机
            {"CMICA2", 131, R.drawable.icon_cmica2, R.string.Lookever, 2, null, true},
            // 网络锁
            {"Bd", 132, R.drawable.icon_bd_visualdoorlock, R.string.Device_List_Netlock, 1, HttpUrlKey.URL_DEVICE_Bd, true},
            // 六路场景开关02型
            {"37", 133, R.drawable.icon_device_37, R.string.Device_Default_Name_37, 3, HttpUrlKey.URL_DEVICE_37, false},
            // 窗帘电机
            {"80", 134, R.drawable.icon_device_80, R.string.Device_Default_Name_80, 7, HttpUrlKey.URL_DEVICE_80, true},
            // 内嵌式零火一路开关
            {"Aj", 135, R.drawable.icon_device_aj, R.string.Device_Default_Name_Aj, 3, HttpUrlKey.URL_DEVICE_Aj, true},
            // 内嵌式零火二路开关
            {"At", 136, R.drawable.icon_device_at, R.string.Device_Default_Name_At, 3, HttpUrlKey.URL_DEVICE_At, true},
            // Metalsingleway_name
            {"Am", 137, R.drawable.icon_device_am, R.string.Metalsingleway_name, 3, HttpUrlKey.URL_DEVICE_Am, true},
            // 金属两路开关
            {"An", 138, R.drawable.icon_device_an, R.string.MetalPaths_name, 3, HttpUrlKey.URL_DEVICE_An, true},
            // 金属三路开关
            {"Ao", 139, R.drawable.icon_device_ao, R.string.Metalthreeway_name, 3, HttpUrlKey.URL_DEVICE_Ao, true},
            // 金属窗帘控制器
            {"Ar", 140, R.drawable.icon_device_ar, R.string.Metalcurtain_name, 7, HttpUrlKey.URL_DEVICE_Ar, true},
            // 企鹅摄像机
            {"CMICA3", 141, R.drawable.icon_cmica3, R.string.Penguin, 2, null, true},
            // 紧急按钮
            {"04", 142, R.drawable.icon_device_04, R.string.Device_Default_Name_04, 5, HttpUrlKey.URL_DEVICE_04, false},
            // 紫金山锁
            {"OW", 143, R.drawable.icon_device_ow, R.string.Device_Default_Name_OW, 1, HttpUrlKey.URL_DEVICE_OW, true},
            // mini网关声光设备
            {"D8", 144, R.drawable.icon_device_d8, R.string.Minigateway_Devicelist_Name, 3, null, false},
            // 红外转发器
            {"23", 145, R.drawable.icon_device_23, R.string.Device_Default_Name_23, 8, null, true},
            // 网络锁02型
            {"Bf", 146, R.drawable.icon_bf_visualdoorlock, R.string.Device_Default_Name_Bf, 1, HttpUrlKey.URL_DEVICE_Bf, true},
            // 洞庭湖门锁
            {"Bg", 147, R.drawable.icon_bg_visualdoorlock, R.string.Device_Default_Name_Bg, 1, HttpUrlKey.URL_DEVICE_Bg, true},
            //小物摄像机
            {"CMICA4", 148, R.drawable.icon_cmica4, R.string.Cylincam, 2, null, true},
            // Wulian单路开关
            {"61", 149, R.drawable.wulian_switch1, R.string.Device_61_Name, 3, HttpUrlKey.URL_DEVICE_61, true},
            {"62", 150, R.drawable.wulian_switch2, R.string.Device_62_Name, 3, HttpUrlKey.URL_DEVICE_62, true},
            {"63", 151, R.drawable.wulian_switch3, R.string.Device_63_Name, 3, HttpUrlKey.URL_DEVICE_63, true},
            //一、二、三路绑定开关
            {"52", 152, R.drawable.device_icon_52, R.string.Device_Default_Name_52, 3, HttpUrlKey.URL_DEVICE_52, false},
            {"54", 153, R.drawable.device_icon_54, R.string.Device_Default_Name_54, 3, HttpUrlKey.URL_DEVICE_54, false},
            {"55", 154, R.drawable.device_icon_55, R.string.Device_Default_Name_55, 3, HttpUrlKey.URL_DEVICE_55, false},
            //CO2 监测器
            {"42", 155, R.drawable.icon_device_42, R.string.Device_Default_Name_42, 6, HttpUrlKey.URL_DEVICE_42, false},
            //户外摄像机
            {"CMICA5", 156, R.drawable.icon_cmica5, R.string.Device_Default_Name_CMICA5, 2, null, true},
            //物联门铃按钮02型
            {"A5", 157, R.drawable.icon_device_a5, R.string.Device_Default_Name_A5, 5, HttpUrlKey.URL_DEVICE_A5, true},
            //门铃声光器
            {"A6", 158, R.drawable.icon_device_a6, R.string.Device_Default_Name_A6, 5, HttpUrlKey.URL_DEVICE_A6, false},
            //物联中继器
            {"31", 159, R.drawable.icon_repeater, R.string.Device_Default_Name_31, 9, HttpUrlKey.URL_DEVICE_31, false},
            // wulian双轨驱动自动窗帘
            {"81", 160, R.drawable.device_81_icon, R.string.Device_Default_Name_81, 7, HttpUrlKey.URL_DEVICE_81, true},
            // wulian一位DIN开关计量版02型
            {"Ai", 161, R.drawable.device_ai_icon, R.string.Device_Default_Name_Ai, 3, HttpUrlKey.URL_DEVICE_Ai, true},
            // wulian单火线触摸调光开关
            {"12", 162, R.drawable.regulate_switch_icon, R.string.Device_Default_Name_12, 3, null, true},
            // wulian移动插座计量版02型
            {"77", 163, R.drawable.device_77_icon, R.string.Device_Default_Name_77, 4, HttpUrlKey.URL_DEVICE_77, true},
            // wulian窗帘控制器02型
            {"65", 164, R.drawable.device_65_icon, R.string.Device_Default_Name_65, 7, HttpUrlKey.URL_DEVICE_65, true},
            // 一路场景开关
            {"Bi", 165, R.drawable.device_bi_icon, R.string.Device_Default_Name_Bi, 3, HttpUrlKey.URL_DEVICE_Bi, false},
            // wulian 地暖
            {"Ap", 166, R.drawable.device_ap_icon, R.string.Device_Default_Name_Ap, 10, HttpUrlKey.URL_DEVICE_Ap, true},
            // 5038 地暖
            {"Bm", 167, R.drawable.device_bm_icon, R.string.Device_Default_Name_Bm, 10, HttpUrlKey.URL_DEVICE_Bm, true},
            // 遥控器
            {"Bl", 168, R.drawable.device_bl_icon, R.string.Device_Default_Name_Bl, 8, HttpUrlKey.URL_DEVICE_Bl, false},
            // 回家/离家场景开关
            {"Bj", 169, R.drawable.device_bj_icon, R.string.Device_Default_Name_Bj, 3, HttpUrlKey.URL_DEVICE_Bj, false},
            // 睡觉/起床场景开关
            {"Bk", 170, R.drawable.device_bk_icon, R.string.Device_Default_Name_Bk, 3, HttpUrlKey.URL_DEVICE_Bk, false},
            // 三位场景开关
            {"33", 172, R.drawable.icon_switch_33, R.string.Device_Default_Name_33, 3, HttpUrlKey.URL_DEVICE_33, false},
            // 一位免零线智能调光开关
            {"Av", 173, R.drawable.icon_switch_av, R.string.Device_Default_Name_Av, 3, null, true},
            // 二位智能窗帘开关
            {"Bh", 174, R.drawable.icon_switch_bh, R.string.Device_Default_Name_Bh, 7, HttpUrlKey.URL_DEVICE_Bh, true},
            // wulian 美标
            {"82", 175, R.drawable.icon_device_82, R.string.Device_Default_Name_82, 10, HttpUrlKey.URL_DEVICE_82, true},
            // 罗格朗 空调遥控器 5038
            {"Oi", 176, R.drawable.icon_device_oi, R.string.Device_Default_Name_Oi, 10, HttpUrlKey.URL_DEVICE_Oi, true},
            // 罗格朗 空调遥控器 5038
            {"32", 177, R.drawable.icon_device_32, R.string.Device_Default_Name_32, 3, HttpUrlKey.URL_DEVICE_32, false},
            //梦想之花 二氧化碳
            {"A0", 178, R.drawable.icon_device_a0, R.string.Device_Default_Name_A0, 6, HttpUrlKey.URL_DEVICE_A0, true},
            //梦想之花 噪声
            {"D4", 179, R.drawable.icon_device_d4, R.string.Device_Default_Name_D4, 6, HttpUrlKey.URL_DEVICE_D4, true},
            //梦想之花 粉尘
            {"D5", 180, R.drawable.icon_device_d5, R.string.Device_Default_Name_D5, 6, HttpUrlKey.URL_DEVICE_D5, true},
            //梦想之花 VOC
            {"D6", 181, R.drawable.icon_device_d6, R.string.Device_Default_Name_D6, 6, HttpUrlKey.URL_DEVICE_D6, true},
            //4G企鹅摄像机
            {"CMICA6", 182, R.drawable.icon_cmica3, R.string.Device_Default_Name_CMICA6, 2, null, true},
            //梦想之花电台
            {"D9", 183, R.drawable.icon_device_d9, R.string.Device_Default_Name_D9, 11, HttpUrlKey.URL_DEVICE_D9, true},
            //全角度红外转发
            {"22", 184, R.drawable.icon_device_22, R.string.Device_Default_Name_22, 8, null, true},
            // 移动插座
            {"16", 185, R.drawable.icon_device_16, R.string.Device_Default_Name_16, 4, HttpUrlKey.URL_DEVICE_16, true},
            // 机械手
            {"25", 186, R.drawable.icon_device_25, R.string.Device_Default_Name_25, 5, HttpUrlKey.URL_DEVICE_25, true},
            // 光强传感器
            {"19", 187, R.drawable.device_icon_19, R.string.Device_Default_Name_19, 6, HttpUrlKey.URL_DEVICE_19, false},
            //梦想之花炫彩灯
            {"D7", 188, R.drawable.icon_device_d7, R.string.Device_Default_Name_D7, 3, HttpUrlKey.URL_DEVICE_D7, false},
            // 光强传感器
            {"Og", 189, R.drawable.device_icon_og, R.string.Device_Default_Name_Og, 6, HttpUrlKey.URL_DEVICE_Og, false},
            // 全宅背景音乐
            {"DD", 190, R.drawable.device_icon_dd, R.string.Device_Default_Name_DD, 11, HttpUrlKey.URL_DEVICE_DD, true},
            // 智能晾衣机
            {"OF", 191, R.drawable.icon_device_of, R.string.Device_Default_Name_OF, 10, HttpUrlKey.URL_DEVICE_OF, true},
            // LED炫彩灯
            {"90", 192, R.drawable.device_icon_90, R.string.Device_Default_Name_90, 3, HttpUrlKey.URL_DEVICE_90, true},
            // 安全狗
            {"sd01", 193, R.drawable.device_icon_sd01, R.string.Device_Default_Name_sd01, 5, null, false},
            //六键场景开关（电池型）
            {"Az", 194, R.drawable.device_icon_az, R.string.Device_Default_Name_Az, 3, HttpUrlKey.URL_DEVICE_Az, false},
//            //六键场景开关（电源型）
            {"Bb", 195, R.drawable.device_icon_bb, R.string.Device_Default_Name_Bb, 3, HttpUrlKey.URL_DEVICE_Bb, false},
            //1/2模组（场景）
            {"Ay", 196, R.drawable.device_icon_ay, R.string.Device_Default_Name_Ay, 3, HttpUrlKey.URL_DEVICE_Ay, false},
            //1/2开关模组(单零火)
            {"Aw", 197, R.drawable.device_icon_aw, R.string.Device_Default_Name_Aw, 3, HttpUrlKey.URL_DEVICE_Aw, true},
            //六路开关模组
            {"Ax", 198, R.drawable.device_icon_ax, R.string.Device_Default_Name_Ax, 3, HttpUrlKey.URL_DEVICE_Ax, true},
            // 新风控制器
            {"Oj", 199, R.drawable.device_icon_oj, R.string.Device_Default_Name_Oj, 10, HttpUrlKey.URL_DEVICE_Oj, true},
            //两位插座
            {"Ba", 200, R.drawable.device_icon_ba, R.string.Device_Default_Name_Ba, 4, HttpUrlKey.URL_DEVICE_Ba, true},
            //1/2模组-调光
            {"Be", 201, R.drawable.device_icon_be, R.string.Device_Default_Name_Be, 3, null, true},
            //中央空调
            {"OZ", 202, R.drawable.device_icon_oz, R.string.Device_Default_Name_OZ, 10, HttpUrlKey.URL_DEVICE_OZ, false},
            //智能水阀
            {"28", 203, R.drawable.device_icon_28, R.string.Device_Default_Name_28, 10, HttpUrlKey.URL_DEVICE_28, true},
            // 万能红外转发器
            {"24", 204, R.drawable.icon_device_24, R.string.Device_Default_Name_24, 8, null, true},
            // 幕帘探测器
            {"a1", 205, R.drawable.icon_device_a1, R.string.Device_Default_Name_a1, 5, HttpUrlKey.URL_DEVICE_a1, false},
            // 粉尘监测器
            {"44", 206, R.drawable.icon_device_44, R.string.Device_Default_Name_44, 6, HttpUrlKey.URL_DEVICE_44, false},
            // 玻璃门锁
            {"69", 207, R.drawable.icon_device_69, R.string.Device_Default_Name_69, 1, HttpUrlKey.URL_DEVICE_69, false},
            //多功能红外人体探测器(强电版)
            {"C0", 208, R.drawable.device_icon_ad, R.string.Device_Default_Name_C0, 5, HttpUrlKey.URL_DEVICE_C0, true},
            //多功能红外人体探测器(电池版)
            {"Ad", 209, R.drawable.device_icon_ad, R.string.Device_Default_Name_Ad, 5, HttpUrlKey.URL_DEVICE_Ad, true},
            //电子血压计
            {"48", 210, R.drawable.device_icon_48, R.string.Device_Default_Name_48, 12, HttpUrlKey.URL_DEVICE_48, false},
            //智能体重秤
            {"45", 211, R.drawable.device_icon_45, R.string.Device_Default_Name_45, 12, HttpUrlKey.URL_DEVICE_45, false},
            //4键场景开关
            {"34", 212, R.drawable.device_icon_34, R.string.Device_Default_Name_34, 3, HttpUrlKey.URL_DEVICE_34, false},
            //内嵌式窗帘控制器
            {"Au", 213, R.drawable.device_icon_au, R.string.Device_Default_Name_Au, 7, HttpUrlKey.URL_DEVICE_Au, true},
            //江森温控器_Ol(大写字母O，小写字母L)
            {"Ol", 214, R.drawable.device_icon_ol, R.string.Device_Default_Name_Ol, 10, HttpUrlKey.URL_DEVICE_Ol, false},
            //海信ZigBee空调_Ok
            {"Ok", 215, R.drawable.device_icon_ok, R.string.Device_Default_Name_Ok, 13, HttpUrlKey.URL_DEVICE_Ok, true},
            //二路输出型翻译器
            {"A2", 220, R.drawable.device_icon_trans_a2, R.string.Device_Default_Name_A2, 10, HttpUrlKey.URL_DEVICE_A2, false},
            //二路输出型翻译器02型
            {"Bo", 221, R.drawable.device_icon_trans_bo, R.string.Device_Default_Name_Bo, 10, HttpUrlKey.URL_DEVICE_Bo, false},
            //四路输入型翻译器
            {"A1", 222, R.drawable.device_icon_trans_a1, R.string.Device_Default_Name_A1, 10, HttpUrlKey.URL_DEVICE_A1, false},
            //一路输入一路输出型翻译器
            {"B9", 223, R.drawable.device_icon_trans_b9, R.string.Device_Default_Name_B9, 10, HttpUrlKey.URL_DEVICE_B9, false},
            //海信冰箱
            {"HS05", 224, R.drawable.device_icon_hs05, R.string.Device_Default_Name_HS05, 13, HttpUrlKey.URL_DEVICE_HS05, true},
            //向往背景音乐
            {"XW01", 225, R.drawable.device_icon_xw01, R.string.Device_Default_Name_XW01, 11, HttpUrlKey.URL_DEVICE_XW01, true},
            //wifi红外转发
            {"IF02", 226, R.drawable.icon_device_if01, R.string.Device_Default_Name_IF02, 8, null, true},
            //海信日立全热交换新风系统
            {"On", 227, R.drawable.device_icon_controller_on, R.string.Device_Default_Name_On, 10, HttpUrlKey.URL_DEVICE_On, true},
            //皇冠可视锁
            {"Bn", 228, R.drawable.icon_device_bn, R.string.Device_Default_Name_Bn, 1, null, true},
            //皇冠非可视锁
            {"Bq", 229, R.drawable.icon_device_bq, R.string.Device_Default_Name_Bq, 1, HttpUrlKey.URL_DEVICE_Bq, true},
            //物联温控器
            {"Af", 230, R.drawable.icon_device_af, R.string.Device_Default_Name_Af, 10, HttpUrlKey.URL_DEVICE_Af, true},
            //罗格朗VRV空调
            {"Bp", 231, R.drawable.device_icon_bp, R.string.Device_Default_Name_Bp, 10, HttpUrlKey.URL_DEVICE_Bp, true},
            //联排温度控制器
            {"Br", 232, R.drawable.icon_device_br, R.string.Device_Default_Name_Br, 10, HttpUrlKey.URL_DEVICE_Br, true},
            //新联排插座
            {"Bt", 233, R.drawable.icon_device_bt, R.string.Device_Default_Name_Bt, 4, HttpUrlKey.URL_DEVICE_Bt, true},
            //丹弗斯地暖温控器
            {"Bs", 234, R.drawable.icon_device_bs, R.string.Device_Default_Name_Bs, 10, HttpUrlKey.URL_DEVICE_Bs, true},
            //新联排一路开关
            {"Bu", 235, R.drawable.icon_device_bu, R.string.Device_Default_Name_Bu, 3, HttpUrlKey.URL_DEVICE_Bu, true},
            //新联排两路开关
            {"Bv", 236, R.drawable.icon_device_bv, R.string.Device_Default_Name_Bv, 3, HttpUrlKey.URL_DEVICE_Bv, true},
            //新联排三路开关
            {"Bw", 237, R.drawable.icon_device_bw, R.string.Device_Default_Name_Bw, 3, HttpUrlKey.URL_DEVICE_Bw, true},
            //大金中央空调
            {"a0", 238, R.drawable.device_icon_a0, R.string.Device_Default_Name_a0, 10, HttpUrlKey.URL_DEVICE_a0, false},
            //联排地暖温控器
            {"Bx", 239, R.drawable.device_icon_bx, R.string.Device_Default_Name_Bx, 10, HttpUrlKey.URL_DEVICE_Bx, true},
            //智敬VRV空调
            {"By", 240, R.drawable.device_icon_by, R.string.Device_Default_Name_By, 10, HttpUrlKey.URL_DEVICE_By, true},
            //小型四键遥控器
            {"38", 241, R.drawable.device_icon_38, R.string.Device_Default_Name_38, 8, HttpUrlKey.URL_DEVICE_38, false},
            //调色温调光led灯
            {"91", 242, R.drawable.device_icon_91, R.string.Device_Default_Name_91, 3, null, true},
            //串口型翻译器
            {"OX", 243, R.drawable.device_icon_ox, R.string.Device_Default_Name_OX, 10, null, false},
            //致敬系列调光开关
            {"Cc", 244, R.drawable.device_icon_cc, R.string.Device_Default_Name_Cc, 3, null, true},
            //致敬系列一路触发开关
            {"Ca", 245, R.drawable.device_icon_ca, R.string.Device_Default_Name_Ca, 7, HttpUrlKey.URL_DEVICE_Ca, true},
            //致敬系列二路触发开关
            {"Cb", 246, R.drawable.device_icon_cb, R.string.Device_Default_Name_Cb, 7, HttpUrlKey.URL_DEVICE_Cb, true},
            //两路调光开关
            {"13", 247, R.drawable.device_icon_13, R.string.Device_Default_Name_13, 3, null, false},
            //4路开关
            {"64", 248, R.drawable.device_icon_64, R.string.Device_Default_Name_64, 3, HttpUrlKey.URL_DEVICE_64, false},
            // 防水防尘枪击摄像机
            {"CG22", 249, R.drawable.device_icon_cg22, R.string.Device_Default_Name_CG22, 2, null, true},
            // 防水防尘枪击摄像机(海外版)
            {"CG23", 250, R.drawable.device_icon_cg22, R.string.Device_Default_Name_CG23, 2, null, true},
            // 云台摄像机
            {"CG24", 251, R.drawable.device_icon_cg24, R.string.Device_Default_Name_CG24, 2, null, true},
            // 云台摄像机（海外版）
            {"CG25", 252, R.drawable.device_icon_cg24, R.string.Device_Default_Name_CG25, 2, null, true},
            // 中弘空调控制器
            {"Op", 253, R.drawable.device_icon_op, R.string.Device_Default_Name_Op, 10, HttpUrlKey.URL_DEVICE_Op, true},
            //pl02地暖温控器
            {"Cj", 254, R.drawable.device_icon_cj, R.string.Device_Default_Name_Cj, 10, HttpUrlKey.URL_DEVICE_Cj, true},
            // DVR硬盘摄像机
            {"CG26", 255, R.drawable.device_icon_cg26, R.string.Device_Default_Name_CG26, 2, null, true},
            // 罗格朗1、2、3位开关
            {"Cl", 256, R.drawable.device_icon_cl, R.string.Device_Default_Name_Cl, 3, HttpUrlKey.URL_DEVICE_Cl, true},
            {"Cm", 257, R.drawable.device_icon_cm, R.string.Device_Default_Name_Cm, 3, HttpUrlKey.URL_DEVICE_Cm, true},
            {"Cn", 258, R.drawable.device_icon_cn, R.string.Device_Default_Name_Cn, 3, HttpUrlKey.URL_DEVICE_Cn, true},
            //PL02新风控制器
            {"Ck", 259, R.drawable.device_icon_ck, R.string.Device_Default_Name_Ck, 10, HttpUrlKey.URL_DEVICE_Ck, true},
            //罗格朗可视门禁
            {"CG27", 260, R.drawable.device_icon_cg27, R.string.Device_Default_Name_CG27, 2, null, true},
            //智敬四键开关
            {"Cp", 261, R.drawable.device_icon_cp, R.string.Device_Default_Name_Cp, 3, HttpUrlKey.URL_DEVICE_Cp, false},
            //杜亚窗帘
            {"Co", 262, R.drawable.device_icon_co, R.string.Device_Default_Name_Co, 7, HttpUrlKey.URL_DEVICE_Co, true}
    };

    static {
        for (int i = 0; i < deviceInfoData.length; i++) {
            deviceDataMap.put((String) deviceInfoData[i][0], new DeviceInfoDataBean(deviceInfoData[i]));
        }
    }

    private static class DeviceInfoDataBean {
        public String type;
        public int typeId;
        public int iconRes;
        public int nameRes;
        public int category;
        public String url;
        public boolean hasWidget;

        public DeviceInfoDataBean(Object[] data) {
            this.type = (String) data[0];
            this.typeId = (int) data[1];
            this.iconRes = (int) data[2];
            this.nameRes = (int) data[3];
            this.category = (int) data[4];
            this.url = (String) data[5];
            this.hasWidget = (boolean) data[6];
        }
    }

    /**
     * 设备type到图标资源映射
     *
     * @param type
     * @return
     */
    public static int getIconByType(String type) {
        if (type == null) {
            return R.drawable.icon_device_unknown;
        }

        DeviceInfoDataBean bean = deviceDataMap.get(type);
        if (bean == null) {
            return R.drawable.icon_device_unknown;
        }
        return bean.iconRes;
    }

    public static boolean isWiFiDevice(String type) {
        if (TextUtils.isEmpty(type)) {
            return false;
        }
        switch (type) {
            case "CMICA1":      // 智能猫眼
            case "CMICA2":      // 随便看摄像机
            case "CMICY1":      // 智能猫眼mini
            case "CMICA3":      // 企鹅摄像机
            case "CMICA6":      // 4G企鹅1080P摄像机
            case "CMICA4":      // 小物摄像机
            case "CMICA5":      // 户外高清摄像机
            case "HS01":        // 海信 家用空调
            case "HS02":        // 海信 中央空调
            case "HS03":        // 海信 油烟机
            case "HS04":        // 海信 燃气灶
            case "HS05":        // 海信 冰箱
            case "HS06":        // 海信 洗衣机
            case "sd01":        // 安全狗
            case "XW01":        // 向往背景音乐
            case "IF02":        // wifi红外转发
            case "CG22":        // 乐橙户外摄像机
            case "CG23":        // 乐橙户外摄像机
            case "CG24":        // 乐橙云台摄像机
            case "CG25":        // 乐橙云台摄像机
            case "CG26":        // DVR硬盘录像机
            case "CG27":        // 罗格朗门禁
                return true;
            default:
                return false;
        }
    }

    public static int getIconByDevice(Device device) {
        if (device == null) {
            return getIconByType(null);
        }

        return getIconByType(device.type);
    }

    /**
     * 设备type到名称资源映射
     *
     * @param type
     * @return
     */
    public static int getDefaultNameByType(String type) {
        if (type == null) {
            return R.string.Device_Unknow;
        }

        if (type.startsWith("GW01")) {
            return R.string.Device_Default_Name_GW01;
        } else if (type.startsWith("GW02")) {
            return R.string.Lookever_Gateway;
        } else if (type.startsWith("GW03")
                || type.startsWith("GW13")) {
            return R.string.Penguin_Gateway;
        } else if (type.startsWith("GW04")) {
            return R.string.Minigateway_Device_Title;
        } else if (type.startsWith("GW05")) {
            return R.string.Cylincam_Gateway;
        } else if (type.startsWith("GW06")) {
            return R.string.Device_Default_Name_GW06;
        } else if (type.startsWith("GW08")) {
            return R.string.Device_Default_Name_GW08;
        } else if (type.startsWith("GW09")) {
            return R.string.addDevice_GW09_name;
        } else if (type.startsWith("GW10")) {
            return R.string.addDevice_GW10_name;
        } else if (type.startsWith("GW12")) {
            return R.string.Device_Default_Name_GW12;
        } else if (type.startsWith("GW11")) {
            return R.string.Device_Default_Name_GW11;
        } else if (type.startsWith("GW15")) {
            return R.string.Device_Default_Name_GW15;
        } else if (type.startsWith("GW99")) {
            return R.string.Experience_Gateway_07;
        } else if (type.startsWith("GW14")) {
            return R.string.Device_Default_Name_GW14;
        }

        if (type.startsWith("GW")) {
            return R.string.Login_Gateway_Login;
        }

        DeviceInfoDataBean bean = deviceDataMap.get(type);
        if (bean == null) {
            return R.string.Device_Unknow;
        }
        return bean.nameRes;
    }

    /**
     * 根据现有设备名字段的情况和type，获取设备名<P/>
     * 如果含有占位符，则用默认名称字符串替换占位符
     *
     * @param type
     * @param name
     * @return
     */
    public static String getNameByTypeAndName(String type, String name) {
        Context context = MainApplication.getApplication();
        String deviceName = null;
        if (TextUtils.isEmpty(name)) {
            deviceName = context.getString(getDefaultNameByType(type));
        } else if (name.startsWith(DEVICE_NAME_PLACE_HOLDER)) {
            deviceName = name.replace(DEVICE_NAME_PLACE_HOLDER, context.getString(getDefaultNameByType(type)));
        } else {
            deviceName = name;
        }
        return deviceName;
    }

    public static String getNameByDevice(Device device) {
        if (device == null) {
            return MainApplication.getApplication().getResources().getString(R.string.Device_Unknow);
        }
        return getNameByTypeAndName(device.type, device.name);
    }

    /**
     * 设备type到设备大类
     *
     * @param type
     * @return 大类ID <p/>
     * 1 智能门锁，2 摄像机，3 开关，4 插座，5 安防设备，6 环境监测，7 窗帘，8 智能遥控，9 中继器，10 控制器, 11 背景音乐 12健康管理 13家用电器
     */
    public static int getCategoryByType(String type) {
        if (type == null) {
            return 0;
        }

        DeviceInfoDataBean bean = deviceDataMap.get(type);
        if (bean == null) {
            return 0;
        }
        return bean.category;
    }

    /**
     * 设备type到设备界面url
     *
     * @param type
     * @return url
     */
    public static String getUrlByType(String type) {
        if (type == null) {
            return "";
        }

        DeviceInfoDataBean bean = deviceDataMap.get(type);
        if (bean == null) {
            return "";
        }
        return bean.url;
    }

    /**
     * 通过type 获取是否有widget
     */
    public static boolean hasWidget(String type) {
        if (type == null) {
            return false;
        }

        DeviceInfoDataBean bean = deviceDataMap.get(type);
        if (bean == null) {
            return false;
        }
        return bean.hasWidget;
    }

    /**
     * 通过type 获取是否有widget
     */
    public static int getDeviceTypeID(String type) {
        if (type == null) {
            return -128;
        }

        DeviceInfoDataBean bean = deviceDataMap.get(type);
        if (bean == null) {
            switch (type) {
                case "Banner":
                    return -1;
                case "Scene":
                    return -2;
                case "Video":
                    return -3;
                case "security_sensor":
                    return -7;
                case "environment_det":
                    return -8;
                default:
                    return -128;
            }
        }
        if (bean.hasWidget) {
            return bean.typeId;
        }
        return -128;
    }

    /**
     * 是否支持该设备
     */
    public static boolean supportMe(Device device) {
        if (device == null) {
            return false;
        }

        return supportMe(device.type);
    }

    /**
     * 是否支持该设备
     */
    public static boolean supportMe(String type) {
        return deviceDataMap.containsKey(type);
    }


    /**
     * 不是当前网关上报的数据是否需要解析和传递
     * 例如：XW01切换歌曲500上报的数据不应该被过滤
     * 新增：大华摄像机设备上下线
     *
     * @param type
     * @return
     */
    public static boolean supportParseCmdData(String type) {
        boolean result;
        switch (type) {
            case "XW01":
            case "CG22":
            case "CG23":
            case "CG24":
            case "CG25":
            case "CG26":
            case "CG27":
                result = true;
                break;
            default:
                result = false;

        }
        return result;
    }

    /**
     * 跳转设备详情页
     */
    public static void showDetail(final Context context, Device device) {
        if (device == null) {
            return;
        }
        String type = device.type;
        if (TextUtils.isEmpty(type)) {
            return;
        }
        switch (type) {
            case "CMICA1":
                CateyeDetailActivity.start(context, device);
                break;
            case "CMICA2":
                LookeverDetailActivity.start(context, device, false);
                break;
            case "CMICY1":
                EquesPlayActivity.start(context, device);
                break;
            case "CMICA3":
            case "CMICA6":
                PenguinDetailActivity.start(context, device, false);
                break;
            case "CMICA4":
                CylincamPlayActivity.start(context, device.devID);
                break;
            case "CMICA5":
                OutdoorDetailActivity.start(context, device, false);
                break;
            case "Bc":
                DeviceBcCameraActivity.startActivity(context, device.devID);
                break;
            case "D8":
                DeviceD8Activity.start(context, device.devID);
                break;
            case "23":
                Device23Activity.start(context, device.devID, false);
                break;
            case "22":
            case "24":
                Device22Activity.start(context, device.devID, false);
                break;
            case "12":
                RegulateSwitchActivity.start(context, device.devID);
                break;
            case "Av":
                RegulateSwitchAvActivity.start(context, device.devID);
                break;
            case "DD":
                // 需要激活的设备, 如果离线了, 直接进设备详情
                if (device.isOnLine()) {
                    // 判断是否激活
                    if (MainApplication.getApplication().getActivationCache().isActive(device)) {
                        DeviceDetailActivity.start(context, device.devID);
                    } else {
                        DeviceActivationActivity.start(context, device.devID);
                    }
                } else {
                    DeviceDetailActivity.start(context, device.devID);
                }
                break;
            case "sd01":
                SafeDogMainActivity.start(context, device.devID);
                break;
            case "Be":
                RegulateSwitchBeActivity.start(context, device.devID);
                break;
            case "IF02":
                WifiIRActivity.start(context, device.devID, false);
                break;
            case "Bn":
                DeviceBnCameraActivity.startActivity(context, device.devID);
                break;
            case "91":
                Device91Activity.start(context, device.devID);
                break;
            case "Cc":
                RegulateSwitchCcActivity.start(context, device.devID);
                break;
            case "13":
                RegulateSwitch13Activity.start(context, device.devID);
                break;
            case "CG22":
            case "CG23":
                LcOutdoorCameraActivity.start(context, device.devID);
                break;
            case "CG24":
            case "CG25":
                LcPTZCameraActivity.start(context, device.devID);
                break;
            case "CG26":
                NVRDeviceListActivity.start(context, device.devID);
            case "CG27":
                VideoScreenActivity.start(context, device.devID.substring(0, 8), device.devID.substring(8));
                break;
            default:
                // 其他设备
                DeviceDetailActivity.start(context, device.devID);
                break;

        }
    }


    //开关照明和插座是否支持批量操作（默认支持,只列出了不支持的）
    public static boolean isSupportBatch(String type) {
        switch (type) {
            case "34":
            case "37":
            case "52":
            case "54":
            case "55":
            case "01":
            case "04":
            case "A6":
            case "25":
                return false;
            default:
                return true;
        }
    }

    //是否是乐橙摄像机
    public static boolean isLcCamera(String type) {
        if (TextUtils.isEmpty(type)) {
            return false;
        }
        switch (type) {
            case "CG22":
            case "CG23":
            case "CG24":
            case "CG25":
            case "CG26":
                return true;
            default:
                return false;
        }
    }
}
