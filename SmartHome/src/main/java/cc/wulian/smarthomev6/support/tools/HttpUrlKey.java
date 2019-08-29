package cc.wulian.smarthomev6.support.tools;

/**
 * 作者: mamengchao
 * 时间: 2017/4/21 0021
 * 描述:
 * 联系方式: 805901025@qq.com
 */

public interface HttpUrlKey {
    String URL_MALL = "https://wulian.jd.com/";
    String URL_MALL_EN = "http://www.wuliangroup.com/en/";

    //  String URL_BASE = "http://172.18.2.201:8080";
    String URL_BASE = "file:///android_asset/main";//配置到本地

    String URL_ADD_DEVICE = URL_BASE + "/addDevice/html/addDeviceNew.html";
    String URL_ADD_SCENE = URL_BASE + "/editScene/html/addScene.html?addScene=true";
    String URL_RECOMMEND_SCENE = URL_BASE + "/editScene/html/recommendScene.html?edit=true";
    String URL_EDIT_SCENE = URL_BASE + "/editScene/html/editScene.html?edit=true";
    String URL_EDIT_NEW_SCENE = URL_BASE + "/editScene-new/html/sceneTask.html?edit=true";
    String URL_HOUSEKEEPER = URL_BASE + "/houseKeeper1/html/stewardIndex.html";
    String URL_AUTOTASK = URL_BASE + "/houseKeeper1/html/";
    String URL_MEMBER_CENTER = "https://integral.wulian.cc/wulianMember.html";
    String URL_MEMBER_CENTER_DEBUG_TEST = "https://integraltest.wulian.cc/wulianMember.html";

    String URL_BASE_DEVICE = URL_BASE + "/device";
    String URL_DEVICE_03 = URL_BASE_DEVICE + "/doorSensor/doorSensor.html";
    String URL_DEVICE_50 = URL_BASE_DEVICE + "/socket/html/wallSocket.html";
    String URL_DEVICE_AW = URL_BASE_DEVICE + "/switch/html/switchHandle01.html";
    String URL_DEVICE_06 = URL_BASE_DEVICE + "/waterlogging/waterlogging.html";
    String URL_DEVICE_17 = URL_BASE_DEVICE + "/humiture/humiture.html";
    String URL_DEVICE_43 = URL_BASE_DEVICE + "/smoke/smoke.html";
    String URL_DEVICE_09 = URL_BASE_DEVICE + "/gas/gas.html";
    String URL_DEVICE_01 = URL_BASE_DEVICE + "/VolAlarm/VolAlarm.html";
    String URL_DEVICE_02 = URL_BASE_DEVICE + "/infrared/infrared.html";
    String URL_DEVICE_70 = URL_BASE_DEVICE + "/doorLockVOC/html/doorLockVOCCardHandle.html";
    String URL_DEVICE_Bc = URL_BASE_DEVICE + "/doorLock_Bc/LockMainView.html";
    String URL_DEVICE_Bd = URL_BASE_DEVICE + "/doorLock_Bd/bdLockHandle.html";
    String URL_DEVICE_OP = URL_BASE_DEVICE + "/goldCodeLock_op/goldCodeLockHandle.html";

    String URL_DEVICE_37 = URL_BASE_DEVICE + "/DeviceSceneKey_37/SceneKey.html";
    String URL_DEVICE_80 = URL_BASE_DEVICE + "/curtain_80/curtain.html";
    String URL_DEVICE_Aj = URL_BASE_DEVICE + "/SW16Aj/device_Aj.html";
    String URL_DEVICE_At = URL_BASE_DEVICE + "/switch_At/device_At.html";
    String URL_DEVICE_Am = URL_BASE_DEVICE + "/switch_Am/switch_Am.html";
    String URL_DEVICE_An = URL_BASE_DEVICE + "/switch_An/switch_An.html";
    String URL_DEVICE_Ao = URL_BASE_DEVICE + "/switch_Ao/switch_ao_main.html";
    String URL_DEVICE_Ar = URL_BASE_DEVICE + "/MetalCurtainControl/CurtainMainView.html";
    String URL_DEVICE_OW = URL_BASE_DEVICE + "/doorLock_OW/doorLockOWHandle.html";
    String URL_DEVICE_04 = URL_BASE_DEVICE + "/urgent_Button_04/urgent_Button_04.html";

    String URL_CUSTOMER_SERVICE = URL_BASE + "/chatWebSocket/mainChat.html";
    String URL_CUSTOMER_SERVICE_GW = URL_BASE + "/chatWebSocket/gatewayHint.html";
    String URL_CUSTOMER_SERVICE_GW_SHORT = "chatWebSocket/gatewayHint.html";

    String KEY_BASE_ABOUT_URL = URL_BASE + "/about";
    String KEY_ABOUT_URL = KEY_BASE_ABOUT_URL + "/about.html";
    String KEY_INTRODUCTION_URL = KEY_BASE_ABOUT_URL + "/introduction.html";

    String URL_DEVICE_Bg = URL_BASE_DEVICE + "/doorLock_Bg/bgLockHandle.html";
    String URL_DEVICE_Bf = URL_BASE_DEVICE + "/doorLock_Bf/bfLockHandle.html";

    String URL_DEVICE_61 = URL_BASE_DEVICE + "/switch_61/html/wallSocket.html";
    String URL_DEVICE_62 = URL_BASE_DEVICE + "/switch_62/html/wallSocket.html";
    String URL_DEVICE_63 = URL_BASE_DEVICE + "/switch_63/html/wallSocket.html";
    String URL_DEVICE_Ai = URL_BASE_DEVICE + "/switch_Ai/device_Ai.html";
    String URL_DEVICE_77 = URL_BASE_DEVICE + "/socket_77/device_77.html";
    String URL_DEVICE_A5 = URL_BASE_DEVICE + "/doorbell_Button_A5/doorbell_Button.html";
    String URL_DEVICE_31 = URL_BASE_DEVICE + "/Repeater_31/html/deviceMainView.html";
    String URL_DEVICE_A6 = URL_BASE_DEVICE + "/doorbell_Acoustooptic_A6/doorbell_Acoustooptic.html";
    String URL_DEVICE_52 = URL_BASE_DEVICE + "/switch_52/html/deviceMainView.html";
    String URL_DEVICE_54 = URL_BASE_DEVICE + "/switch_54/html/deviceMainView.html";
    String URL_DEVICE_55 = URL_BASE_DEVICE + "/switch_55/html/deviceMainView.html";
    String URL_DEVICE_81 = URL_BASE_DEVICE + "/curtain_81/curtain.html";
    String URL_DEVICE_65 = URL_BASE_DEVICE + "/curtain_65/CurtainMainView.html";
    String URL_DEVICE_42 = URL_BASE_DEVICE + "/CarbonDioxide_42/carbonDioxideMonitor.html";
    String URL_DEVICE_Bi = URL_BASE_DEVICE + "/switch_Bi/SceneKey.html";
    String URL_DEVICE_Ap = URL_BASE_DEVICE + "/thermostat_Ap/thermostat_Ap.html";
    String URL_DEVICE_Bm = URL_BASE_DEVICE + "/thermostat_Bm/thermostat_Bm.html";
    String URL_DEVICE_Bl = URL_BASE_DEVICE + "/controller_Bl/remoteControl.html";
    String URL_DEVICE_Bj = URL_BASE_DEVICE + "/switch_Bj/device_Bj.html";
    String URL_DEVICE_Bk = URL_BASE_DEVICE + "/switch_Bk/device_Bk.html";
    String URL_DEVICE_Bh = URL_BASE_DEVICE + "/curtain_Bh/CurtainMainView.html";
    String URL_DEVICE_33 = URL_BASE_DEVICE + "/switch_33/switch_33.html";
    String URL_DEVICE_82 = URL_BASE_DEVICE + "/thermostat_HAVC_82/thermostat_82.html";
    String URL_DEVICE_Oi = URL_BASE_DEVICE + "/thermostat_Oi/thermostat_Oi.html";
    String URL_DEVICE_32 = URL_BASE_DEVICE + "/switch_32/switch_32.html";
    String URL_DEVICE_16 = URL_BASE_DEVICE + "/Socket_16/device_16.html";
    String URL_DEVICE_25 = URL_BASE_DEVICE + "/Security_25/device_25.html";
    String URL_DEVICE_19 = URL_BASE_DEVICE + "/lx_19/device_19.html";
    String URL_DEVICE_A0 = URL_BASE_DEVICE + "/CO2_A0/carbonDioxide.html";
    String URL_DEVICE_D4 = URL_BASE_DEVICE + "/Noise_D4/noise.html";
    String URL_DEVICE_D5 = URL_BASE_DEVICE + "/PM2_5_D5/dust.html";
    String URL_DEVICE_D6 = URL_BASE_DEVICE + "/VOC_D6/voc.html";
    String URL_DEVICE_D7 = URL_BASE_DEVICE + "/LEDlight_D7/colorfulLight.html";
    String URL_DEVICE_D9 = URL_BASE_DEVICE + "/Radio_D9/dreamFlower_radioStation.html";
    String URL_DEVICE_Og = URL_BASE_DEVICE + "/PM2.5_Og/device_Og.html";
    String URL_DEVICE_DD = URL_BASE_DEVICE + "/music_DD/music_DD.html";
    String URL_FOG_GATEWAY = URL_BASE + "/ManagerGateWay/ManagerGWIndex.html";
    String URL_DEVICE_OF = URL_BASE_DEVICE + "/controll_OF/device_OF.html";
    String URL_DEVICE_90 = URL_BASE_DEVICE + "/LEDlight_90/colorfulLight.html";
    String URL_DEVICE_Az = URL_BASE_DEVICE + "/Device2013_Az/SceneKey.html";
    String URL_DEVICE_Bb = URL_BASE_DEVICE + "/Device2013_Bb/SceneKey.html";
    String URL_DEVICE_Ay = URL_BASE_DEVICE + "/Device2013_Ay/SceneKey.html";
    String URL_DEVICE_Aw = URL_BASE_DEVICE + "/Device2013_Aw/deviceAwMain.html";
    String URL_DEVICE_Ax = URL_BASE_DEVICE + "/Device2013_Ax/deviceAxMain.html";
    String URL_DEVICE_Oj = URL_BASE_DEVICE + "/controll_Oj/device_Oj.html";
    String URL_DEVICE_Ba = URL_BASE_DEVICE + "/Device2013_Ba/device_Ba.html";
    String URL_DEVICE_OZ = URL_BASE_DEVICE + "/thermostat_OZ/deviceInfo_OZ.html";
    String URL_DEVICE_28 = URL_BASE_DEVICE + "/waterValve_28/waterValve.html";
    String URL_DEVICE_a1 = URL_BASE_DEVICE + "/safety_A1/main.html";
    String URL_DEVICE_45 = URL_BASE_DEVICE + "/Weighing-scale-45/add-scale-index.html";
    String URL_DEVICE_C0 = URL_BASE_DEVICE + "/safety_C0/device_C0.html";
    String URL_DEVICE_Ad = URL_BASE_DEVICE + "/safety_Ad/device_Ad.html";
    String URL_DEVICE_48 = URL_BASE_DEVICE + "/Healthy_48/device_48.html";
    String URL_DEVICE_44 = URL_BASE_DEVICE + "/Dust_44/index.html";
    String URL_DEVICE_69 = URL_BASE_DEVICE + "/doorLock_69/LockMainView.html";
    String URL_DEVICE_34 = URL_BASE_DEVICE + "/switch_34/SceneKey.html";
    String URL_DEVICE_Au = URL_BASE_DEVICE + "/curtain_Au/curtain.html";
    String URL_DEVICE_A2 = URL_BASE_DEVICE + "/Translator_A2/roadTranslator.html";
    String URL_DEVICE_Ol = URL_BASE_DEVICE + "/Johnson_Ol/deviceInfo_Ol.html";
    String URL_DEVICE_Ok = URL_BASE_DEVICE + "/HA_Ok/device_Ok.html";
    String URL_DEVICE_Bo = URL_BASE_DEVICE + "/Translator_Bo/roadTranslator.html";
    String URL_DEVICE_A1 = URL_BASE_DEVICE + "/Translator_A1/translator_A1.html";
    String URL_DEVICE_B9 = URL_BASE_DEVICE + "/Translator_B9/translator-index.html";
    String URL_DEVICE_HS05 = URL_BASE_DEVICE + "/HA_HS05/index.html";
    String URL_DEVICE_XW01 = URL_BASE_DEVICE + "/music_XW01/music_xw01.html";
    String URL_DEVICE_On = URL_BASE_DEVICE + "/controll_On/device_On.html";
    String URL_DEVICE_Bq = URL_BASE_DEVICE + "/doorLock_Bq/bqLockHandle.html";
    String URL_DEVICE_Af = URL_BASE_DEVICE + "/controll_Af/thermostat_Af.html";
    String URL_DEVICE_Bp = URL_BASE_DEVICE + "/conditioning_Bp/device_Bp.html";
    String URL_DEVICE_Br = URL_BASE_DEVICE + "/Thermostat_Br/thermostat_Br.html";
    String URL_DEVICE_Bs = URL_BASE_DEVICE + "/controll_Bs/controll_Bs.html";
    String URL_DEVICE_Bu = URL_BASE_DEVICE + "/switch_Bu/html/device_Bu.html";
    String URL_DEVICE_Bv = URL_BASE_DEVICE + "/switch_Bv/html/device_Bv.html";
    String URL_DEVICE_Bw = URL_BASE_DEVICE + "/switch_Bw/html/device_Bw.html";
    String URL_DEVICE_Bt = URL_BASE_DEVICE + "/switch_Bt/html/socket_Bt.html";
    String URL_DEVICE_a0 = URL_BASE_DEVICE + "/conditioning_a0/deviceInfo_a0.html";
    String URL_DEVICE_Bx = URL_BASE_DEVICE + "/thermostat_Bx/thermostat_Bx.html";
    String URL_DEVICE_By = URL_BASE_DEVICE + "/conditioning_By/device_By.html";
    String URL_DEVICE_38 = URL_BASE_DEVICE + "/controller_38/remoteControl.html";
    String URL_DEVICE_Ca = URL_BASE_DEVICE + "/curtain_Ca/html/device_Ca.html";
    String URL_DEVICE_Cb = URL_BASE_DEVICE + "/curtain_Cb/html/device_Cb.html";
    String URL_DEVICE_64 = URL_BASE_DEVICE + "/switch_64/device_64.html";
    String URL_DEVICE_Cj = URL_BASE_DEVICE + "/thermostat_Cj/thermostat_Cj.html";
    String URL_DEVICE_Op = URL_BASE_DEVICE + "/controller_Op/html/deviceInfo_Op.html";
    String URL_DEVICE_Ck = URL_BASE_DEVICE + "/controller_Ck/device_Ck.html";
    String URL_DEVICE_Cl = URL_BASE_DEVICE + "/switch_Cl/html/device_Cl.html";
    String URL_DEVICE_Cm = URL_BASE_DEVICE + "/switch_Cm/html/device_Cm.html";
    String URL_DEVICE_Cn = URL_BASE_DEVICE + "/switch_Cn/html/device_Cn.html";
    String URL_DEVICE_Cp = URL_BASE_DEVICE + "/switch_Cp/remoteControl.html";
    String URL_DEVICE_Co = URL_BASE_DEVICE + "/curtain_Co/curtain.html";
}
