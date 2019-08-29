/**
 * Created by Administrator on 2018/6/12.
 */
/**
 * Created by Administrator on 2018/6/12.
 */
//设备做触发条件时需在数组中添加该设备的type
// window.deviceTypeList = ['03','06','04','02','09','43','17','42','Bc','OP','Bd',
//     'OW','Bg','Bf','70','Bn','Bq','69','A0','D4','D5','44','D6','19','Og','a1',
//     '12','A5','Av','Ad','C0','A1','B9'];
//设备作为触发条件及执行任务时的设备列表各项配置
window.deviceList = {
    "03": {
        "name": languageUtil.getResource("addScene_doorContack"), //"门磁感应器"
        "icon": "../../source/deviceIcon/device_icon_03.png",
        //timeTask定时任务设备跳转页面
        "stateUrl": "setDoorState.html",
        //triggerTask触发条件设备跳转页面
        "triggerUrl": "setSceneState.html",
        //sceneTask情景任务执行任务跳转页面
        "sceneUrl": "setDeviceState.html"
    },
    "50": {
        "name": languageUtil.getResource("addScene_socket"), //"墙面插座"
        "icon": "../../source/deviceIcon/device_icon_50.png",
        "stateUrl": "setDoorState.html",
        "sceneUrl": "setSocketState.html"
    },
    "Bt": {
        "name": languageUtil.getResource("device_name_Bt"), //"一位墙面插座"
        "icon": "../../source/deviceIcon/device_icon_Bt.png",
        "stateUrl": "setDoorState.html",
        "sceneUrl": "setSocketState.html"
    },
    "06": {
        "name": languageUtil.getResource("addDevice_06_name"), //"水浸监测器"
        "icon": "../../source/deviceIcon/device_icon_06.png",
        "stateUrl": "setDoorState.html",
        "triggerUrl": "setSecurityDevice.html",
        "sceneUrl": "setDeviceState.html"
    },
    "04": {
        "name": languageUtil.getResource("houseKeeper1_04_name"), //"紧急按钮"
        "icon": "../../source/deviceIcon/device_icon_04.png",
        "triggerUrl": "setSecurityDevice.html"
    },
    "02": {
        "name": languageUtil.getResource("addDevice_02_name"), //"红外入侵监测器"
        "icon": "../../source/deviceIcon/device_icon_02.png",
        "stateUrl": "setDoorState.html",
        "triggerUrl": "setSecurityDevice.html",
        "sceneUrl": "setDeviceState.html"
    },
    "09": {
        "name": languageUtil.getResource("addDevice_09_name"), //"可燃气体泄漏探测器"
        "icon": "../../source/deviceIcon/device_icon_09.png",
        "stateUrl": "setDoorState.html",
        "triggerUrl": "setSecurityDevice.html",
        "sceneUrl": "setDeviceState.html"
    },
    "43": {
        "name": languageUtil.getResource("addDevice_43_name"), //"烟雾监测器"
        "icon": "../../source/deviceIcon/device_icon_43.png",
        "stateUrl": "setDoorState.html",
        "triggerUrl": "setSecurityDevice.html",
        "sceneUrl": "setDeviceState.html"
    },
    "17": {
        "name": languageUtil.getResource("addDevice_17_name"), //"温湿度"
        "icon": "../../source/deviceIcon/device_icon_17.png",
        "triggerUrl": "setHumitureDevice.html"
    },
    "42": {
        "name": languageUtil.getResource("device_name_42"), //"二氧化碳监测器"
        "icon": "../../source/deviceIcon/device_icon_42.png",
        "triggerUrl": "setcarbonDioxideDevice.html"
    },
    "Bc": {
        "name": languageUtil.getResource("device_Bc_name"), //"Bc锁"
        "icon": "../../source/deviceIcon/device_icon_Bc.png",
        "triggerUrl": "setSecurityDevice.html"
    },
    "OP": {
        "name": languageUtil.getResource("device_OP_name"), //"OP锁玄武湖网络锁"
        "icon": "../../source/deviceIcon/device_icon_OP.png",
        "triggerUrl": "setSecurityDevice.html"
    },
    "Bd": {
        "name": languageUtil.getResource("device_Bd_name"), //"Bd锁网络锁"
        "icon": "../../source/deviceIcon/device_icon_Bd.png",
        "triggerUrl": "setSecurityDevice.html"
    },
    "OW": {
        "name": languageUtil.getResource("device_Ow_name"), //"紫金山网络锁"
        "icon": "../../source/deviceIcon/device_icon_OW.png",
        "triggerUrl": "setSecurityDevice.html"
    },
    "Bg": {
        "name": languageUtil.getResource("device_Bg_name"), //"洞庭湖系列网络锁"
        "icon": "../../source/deviceIcon/device_icon_Bg.png",
        "triggerUrl": "setSecurityDevice.html"
    },
    "Bf": {
        "name": languageUtil.getResource("device_Bf_name"), //"网络锁02型"
        "icon": "../../source/deviceIcon/device_icon_Bf.png",
        "triggerUrl": "setSecurityDevice.html"
    },
    "70": {
        "name": languageUtil.getResource("device_70_name"), //"智能门锁"
        "icon": "../../source/deviceIcon/device_icon_70.png",
        "triggerUrl": "setSecurityDevice.html"
    },
    "Bn": {
        "name": languageUtil.getResource("device_name_Bn"), //"Wu-Lock Crown Plus"
        "icon": "../../source/deviceIcon/device_icon_Bn.png",
        "triggerUrl": "setSecurityDevice.html"
    },
    "Bq": {
        "name": languageUtil.getResource("device_Bq_name"), //"Wu-Lock Crown"
        "icon": "../../source/deviceIcon/device_icon_Bq.png",
        "triggerUrl": "setSecurityDevice.html"
    },
    "69": {
        "name": languageUtil.getResource("device_name_69"), //"镜面触屏玻璃门锁"
        "icon": "../../source/deviceIcon/device_icon_69.png",
        "triggerUrl": "setSecurityDevice.html"
    },
    "A0": {
        "name": languageUtil.getResource("device_name_A0"), //"二氧化碳监测器"
        "icon": "../../source/deviceIcon/device_icon_A0.png",
        "triggerUrl": "setcarbonDioxideDevice.html"
    },
    "D4": {
        "name": languageUtil.getResource("device_name_D4"), //"噪声监测器"
        "icon": "../../source/deviceIcon/device_icon_D4.png",
        "triggerUrl": "setcarbonDioxideDevice.html"
    },
    "D5": {
        "name": languageUtil.getResource("device_name_D5"), //"粉尘监测器"
        "icon": "../../source/deviceIcon/device_icon_D5.png",
        "triggerUrl": "setcarbonDioxideDevice.html"
    },
    "44": {
        "name": languageUtil.getResource("device_name_44"), //"粉尘监测器"
        "icon": "../../source/deviceIcon/device_icon_44.png",
        "triggerUrl": "setcarbonDioxideDevice.html"
    },
    "D6": {
        "name": languageUtil.getResource("device_name_D6"), //"VOC监测器"
        "icon": "../../source/deviceIcon/device_icon_D6.png",
        "triggerUrl": "setcarbonDioxideDevice.html"
    },
    "19": {
        "name": languageUtil.getResource("device_19_name"), //"光强监测器"
        "icon": "../../source/deviceIcon/device_icon_19.png",
        "triggerUrl": "setLightIntensity.html"
    },
    "Og": {
        "name": languageUtil.getResource("device_name_Og"), //"PM2.5检测仪"
        "icon": "../../source/deviceIcon/device_icon_Og.png",
        "triggerUrl": "device_Og.html"
    },
    "a1": {
        "name": languageUtil.getResource("device_name_a1"), //"幕帘探测器"
        "icon": "../../source/deviceIcon/device_icon_a1.png",
        "triggerUrl": "setSecurityDevice.html",
        //timeTask定时任务设备跳转页面
        "stateUrl": "setDoorState.html",
        "sceneUrl": "setDeviceState.html"
    },
    "01": {
        "name": languageUtil.getResource("addDevice_01_name"), //"声光报警器"
        "icon": "../../source/deviceIcon/device_icon_01.png",
        "stateUrl": "setDoorState.html",
        "sceneUrl": "setDeviceState.html"
    },
    "Ar": {
        "name": languageUtil.getResource("addDevice_Ar_name"), //"金属窗帘"
        "icon": "../../source/deviceIcon/device_icon_Ar.png",
        "stateUrl": "setDoorState.html",
        "sceneUrl": "setDeviceState.html"
    },
    "Au": {
        "name": languageUtil.getResource("device_name_Au"), //"内嵌式窗帘控制器"
        "icon": "../../source/deviceIcon/device_icon_Au.png",
        "stateUrl": "setDoorState.html",
        "sceneUrl": "setDeviceState.html"
    },
    "65": {
        "name": languageUtil.getResource("device_name_65"), //"窗帘控制器02型"
        "icon": "../../source/deviceIcon/device_icon_65.png",
        "stateUrl": "setDoorState.html",
        "sceneUrl": "setDeviceState.html"
    },
    "Aj": {
        "name": languageUtil.getResource("device_Aj_name"), //内嵌式零火一路开关
        "icon": "../../source/deviceIcon/device_icon_Aj.png",
        "stateUrl": "action_timeTask_setSwitch.html",
        "sceneUrl": "action_sceneTask_setSwitch.html",
        "triggerUrl": "setSecurityDevice.html"
    },
    "80": {
        "name": languageUtil.getResource("device_80_name"), //"窗帘电机"
        "icon": "../../source/deviceIcon/device_icon_80.png",
        "stateUrl": "curtain_80_timeTask.html",
        "sceneUrl": "curtain_80_sceneTask.html"
    },
    "Co": {
        "name": languageUtil.getResource("device_name_Co"), //"杜亚窗帘电机"
        "icon": "../../source/deviceIcon/device_icon_Co.png",
        "stateUrl": "curtain_80_timeTask.html",
        "sceneUrl": "curtain_80_sceneTask.html"
    },
    "At": {
        "name": languageUtil.getResource("device_At_name"), //内嵌式零火二路开关
        "icon": "../../source/deviceIcon/device_icon_At.png",
        "stateUrl": "action_timeTask_setSwitch.html",
        "sceneUrl": "action_sceneTask_setSwitch.html",
        "triggerUrl": "action_triggerTask_switchName.html"
    },
    "Am": {
        "name": languageUtil.getResource("device_Am_name"), //"金属单路开关"
        "icon": "../../source/deviceIcon/device_icon_Am.png",
        "stateUrl": "action_timeTask_setSwitch_lock.html",
        "sceneUrl": "action_sceneTask_setSwitch_lock.html"
    },
    "An": {
        "name": languageUtil.getResource("device_An_name"), //"金属二路开关"
        "icon": "../../source/deviceIcon/device_icon_An.png",
        "stateUrl": "action_timeTask_setSwitch_lock.html",
        "sceneUrl": "action_sceneTask_setSwitch_lock.html"
    },
    "Ao": {
        "name": languageUtil.getResource("device_Ao_name"), //"金属三路开关"
        "icon": "../../source/deviceIcon/device_icon_Ao.png",
        "stateUrl": "action_timeTask_setSwitch_lock.html",
        "sceneUrl": "action_sceneTask_setSwitch_lock.html"
    },
    "D8": {
        "name": languageUtil.getResource("addDevice_D8_minigateway"), //"mini网关声光设备"
        "icon": "../../source/deviceIcon/device_icon_D8.png",
        "stateUrl": "D8VoiceOrLight.html?Task=timeTask",
        "sceneUrl": "D8VoiceOrLight.html?Task=sceneTask"
    },
    "61": {
        "name": languageUtil.getResource("Onewayswitch_61_adddevice"), //Wulian单路开关
        "icon": "../../source/deviceIcon/device_icon_61.png",
        "stateUrl": "action_timeTask_setSwitch.html",
        "sceneUrl": "action_sceneTask_setSwitch.html"
    },
    "62": {
        "name": languageUtil.getResource("Twowayswitch_62_adddevice"), //Wulian二路开关
        "icon": "../../source/deviceIcon/device_icon_62.png",
        "stateUrl": "action_timeTask_setSwitch.html",
        "sceneUrl": "action_sceneTask_setSwitch.html"
    },
    "63": {
        "name": languageUtil.getResource("Threewayswitch_63_adddevice"), //Wulian三路开关
        "icon": "../../source/deviceIcon/device_icon_63.png",
        "stateUrl": "action_timeTask_setSwitch.html",
        "sceneUrl": "action_sceneTask_setSwitch.html"
    },
    "Bu": {
        "name": languageUtil.getResource("device_name_Bu"), //Wulian单路开关
        "icon": "../../source/deviceIcon/device_icon_Bu.png",
        "stateUrl": "action_timeTask_setSwitch_lock.html",
        "sceneUrl": "action_sceneTask_setSwitch_lock.html",
        "triggerUrl": "action_triggerTask_setSwitch.html"
    },
    "Bv": {
        "name": languageUtil.getResource("device_name_Bv"), //联排二路开关
        "icon": "../../source/deviceIcon/device_icon_Bv.png",
        "stateUrl": "action_timeTask_setSwitch_lock.html",
        "sceneUrl": "action_sceneTask_setSwitch_lock.html",
        "triggerUrl": "action_triggerTask_setSwitch.html"
    },
    "Bw": {
        "name": languageUtil.getResource("device_name_Bw"), //联排三路开关
        "icon": "../../source/deviceIcon/device_icon_Bw.png",
        "stateUrl": "action_timeTask_setSwitch_lock.html",
        "sceneUrl": "action_sceneTask_setSwitch_lock.html",
        "triggerUrl": "action_triggerTask_setSwitch.html"
    },
    "Ai": {
        "name": languageUtil.getResource("device_name_Ai"), //"移动插座计量版"
        "icon": "../../source/deviceIcon/device_icon_Ai.png",
        "stateUrl": "setDoorState.html",
        "sceneUrl": "setDeviceState.html"
    },
    "77": {
        "name": languageUtil.getResource("device_name_77"), //"移动插座计量版"
        "icon": "../../source/deviceIcon/device_icon_77.png",
        "stateUrl": "setDoorState.html",
        "sceneUrl": "setDeviceState.html"
    },
    "12": {
        "name": languageUtil.getResource("device_name_12"), //"调光开关"
        "icon": "../../source/deviceIcon/device_icon_12.png",
        "stateUrl": "device_12_timeTask.html",
        "triggerUrl": "setSecurityDevice.html",
        "sceneUrl": "device_12_sceneTask.html"
    },
    "81": {
        "name": languageUtil.getResource("device_name_81"), //"调光开关"
        "icon": "../../source/deviceIcon/device_icon_81.png",
        "stateUrl": "switch_At_timeTask.html",
        "sceneUrl": "switch_At_sceneTask.html"
    },
    "A5": {
        "name": languageUtil.getResource("addDevice_A5_name"), //"门铃按钮02型"
        "icon": "../../source/deviceIcon/device_icon_A5.png",
        "stateUrl": "setDoorState.html",
        "triggerUrl": "setSecurityDevice.html",
        "sceneUrl": "setDeviceState.html"
    },
    "A6": {
        "name": languageUtil.getResource("addDevice_A6_name"), //"门铃声光器"
        "icon": "../../source/deviceIcon/device_icon_A6.png",
        "stateUrl": "setDoorState.html",
        "sceneUrl": "setDeviceState.html"
    },
    "Bh": {
        "name": languageUtil.getResource("device_name_Bh"), //"两路窗帘"
        "icon": "../../source/deviceIcon/device_icon_Bh.png",
        "stateUrl": "switch_At_timeTask.html",
        "sceneUrl": "switch_At_sceneTask.html"
    },
    "Av": {
        "name": languageUtil.getResource("device_name_Av"), //"调光开关"
        "icon": "../../source/deviceIcon/device_icon_Av.png",
        "stateUrl": "device_Av_timeTask.html",
        "triggerUrl": "setSecurityDevice.html",
        "sceneUrl": "device_Av_sceneTask.html"
    },
    "Ap": {
        "name": languageUtil.getResource("device_Ap_name"), //"地暖控制器"
        "icon": "../../source/deviceIcon/device_icon_Ap.png",
        "stateUrl": "device_Ap_timeTask.html",
        "sceneUrl": "device_Ap_sceneTask.html"
    },
    "Bm": {
        "name": languageUtil.getResource("device_Bm_name"), //"wulian地暖控制器"
        "icon": "../../source/deviceIcon/device_icon_Bm.png",
        "stateUrl": "device_Bm_timeTask.html",
        "sceneUrl": "device_Bm_sceneTask.html"
    },
    "Oi": {
        "name": languageUtil.getResource("device_Oi_name"), //"地暖控制器"
        "icon": "../../source/deviceIcon/device_icon_Oi.png",
        "stateUrl": "device_Oi_timeTask.html",
        "sceneUrl": "device_Oi_sceneTask.html"
    },
    "82": {
        "name": languageUtil.getResource("device_82_name"), //"地暖控制器"
        "icon": "../../source/deviceIcon/device_icon_82.png",
        "stateUrl": "device_82_timeTask.html",
        "sceneUrl": "device_82_sceneTask.html"
    },
    "D7": {
        "name": languageUtil.getResource("device_name_D7"), //LED炫彩灯
        "icon": "../../source/deviceIcon/device_icon_D7.png",
        "stateUrl": "device_D7_timeTask.html",
        "sceneUrl": "device_D7_sceneTask.html"
    },
    "16": {
        "name": languageUtil.getResource("device_name_16"), //"移动插座非计量版"
        "icon": "../../source/deviceIcon/device_icon_16.png",
        "stateUrl": "setDoorState.html",
        "sceneUrl": "setDeviceState.html"
    },
    "25": {
        "name": languageUtil.getResource("device_name_25"), //"电动机械手"
        "icon": "../../source/deviceIcon/device_icon_25.png",
        "stateUrl": "setDoorState.html",
        "sceneUrl": "setDeviceState.html"
    },
    "DD": {
        "name": languageUtil.getResource("device_name_DD"), //"背景音乐"
        "icon": "../../source/deviceIcon/device_icon_DD.png",
        "stateUrl": "device_DD_timeTask.html",
        "sceneUrl": "device_DD_sceneTask.html"
    },
    "D9": {
        "name": languageUtil.getResource("device_name_D9"),
        "icon": "../../source/deviceIcon/device_icon_D9.png",
        "stateUrl": "device_D9_timeTask.html",
        "sceneUrl": "device_D9_sceneTask.html"
    },
    "OF": {
        "name": languageUtil.getResource("device_name_OF"), //"智能晾衣机"
        "icon": "../../source/deviceIcon/device_icon_OF.png",
        "stateUrl": "device_OF.html",
        "sceneUrl": "device_OF.html"
    },
    "90": {
        "name": languageUtil.getResource("device_name_90"), //"LED炫彩灯"
        "icon": "../../source/deviceIcon/device_icon_90.png",
        "stateUrl": "setColorfulLight_90.html",
        "sceneUrl": "setColorfulLight_90.html"
    },
    "Aw": {
        "name": languageUtil.getResource("device_name_Aw"), //1/2开关模组
        "icon": "../../source/deviceIcon/device_icon_Aw.png",
        "stateUrl": "setDoorState.html",
        "sceneUrl": "setDeviceState.html"
    },
    "Ax": {
        "name": languageUtil.getResource("device_name_Ax"), //六路开关模组
        "icon": "../../source/deviceIcon/device_icon_Ax.png",
        "stateUrl": "switch_At_timeTask.html",
        "sceneUrl": "chooseChildSwitch.html?viewType=scene"
        // "sceneUrl": "chooseChildSwitch.html?devID="+data[i].devID+"&viewType=scene"
    },
    "Ba": {
        "name": languageUtil.getResource("device_name_Ba"), //"两位插座"
        "icon": "../../source/deviceIcon/device_icon_Ba.png",
        "stateUrl": "device_Ba.html",
        "sceneUrl": "device_Ba.html"
    },
    "22": {
        "name": languageUtil.getResource("device_name_22"), //全角度红外转发器
        "icon": "../../source/deviceIcon/device_icon_22.png",
        "stateUrl": "javascript:;",
        "sceneUrl": "javascript:;"
    },
    "23": {
        "name": languageUtil.getResource("device_name_23"), //半角度红外转发器
        "icon": "../../source/deviceIcon/device_icon_23.png",
        "stateUrl": "javascript:;",
        "sceneUrl": "javascript:;"
    },
    "Oj": {
        "name": languageUtil.getResource("device_name_Oj"), // 新风控制器
        "icon": "../../source/deviceIcon/device_icon_Oj.png",
        "stateUrl": "device_Oj.html",
        "sceneUrl": "device_Oj.html"
    },
    "Be": {
        "name": languageUtil.getResource("device_name_Be"), //"两位插座"
        "icon": "../../source/deviceIcon/device_icon_Be.png",
        "stateUrl": "device_Be.html",
        "sceneUrl": "device_Be.html"
    },
    "OZ": {
        "name": languageUtil.getResource("device_OZ_name"), //中央空调
        "icon": "../../source/deviceIcon/device_icon_OZ.png",
        "stateUrl": "device_OZ_01.html",
        "sceneUrl": "device_OZ_01.html"
    },
    "24": {
        "name": languageUtil.getResource("device_name_24"), //万能红外转发器
        "icon": "../../source/deviceIcon/device_icon_24.png",
        "stateUrl": "javascript:;",
        "sceneUrl": "javascript:;"
    },
    "28": {
        "name": languageUtil.getResource("device_name_28"), //智能水阀
        "icon": "../../source/deviceIcon/device_icon_28.png",
        "stateUrl": "device_water_valve_28.html",
        "sceneUrl": "device_water_valve_28.html"
    },
    "Ad": {
        "name": languageUtil.getResource("device_name_Ad"), //多功能红外人体探测器（电池版）
        "icon": "../../source/deviceIcon/device_icon_Ad.png",
        "stateUrl": "setDoorState.html",
        "triggerUrl": "setMultifunctional.html",
        "sceneUrl": "setDeviceState.html"
    },
    "C0": {
        "name": languageUtil.getResource("device_name_C0"), //多功能红外人体探测器（强电版）
        "icon": "../../source/deviceIcon/device_icon_C0.png",
        "stateUrl": "setDoorState.html",
        "triggerUrl": "setMultifunctional.html",
        "sceneUrl": "setDeviceState.html"
    },
    "A1": {
        "name": languageUtil.getResource("device_name_A1"), //四路翻译器
        "icon": "../../source/deviceIcon/device_icon_a1_01.png",
        "stateUrl": "setSwitch.html",
        "triggerUrl": "switch_At_sceneTask.html",
        "sceneUrl": "switch_At_sceneTask.html"
    },
    "A2": {
        "name": languageUtil.getResource("device_name_A2"), //二路输出型翻译器
        "icon": "../../source/deviceIcon/device_icon_A2.png",
        "stateUrl": "device_A2.html",
        "sceneUrl": "device_A2.html"
    },
    "Bo": {
        "name": languageUtil.getResource("device_name_Bo"), //二路输出型翻译器
        "icon": "../../source/deviceIcon/device_icon_Bo.png",
        "stateUrl": "device_Bo.html",
        "sceneUrl": "device_Bo.html"
    },
    "B9": {
        "name": languageUtil.getResource("device_name_B9"), //输入输出型翻译器
        "icon": "../../source/deviceIcon/device_icon_B9.png",
        "stateUrl": "switch_At_timeTask.html",
        "triggerUrl": "setSecurityDevice.html",
        "sceneUrl": "switch_At_sceneTask.html"
    },
    "Ok": {
        "name": languageUtil.getResource("device_Ok_name"), //分体式智能空调
        "icon": "../../source/deviceIcon/device_icon_Ok.png",
        "stateUrl": "set_device_Ok.html",
        "sceneUrl": "set_device_Ok.html"
    },
    "Ol": {
        "name": languageUtil.getResource("device_name_Ol"), //风盘地暖温控器
        "icon": "../../source/deviceIcon/device_icon_Ol.png",
        "stateUrl": "device_Ol_01.html",
        "sceneUrl": "device_Ol_01.html"
    },
    "HS05": {
        "name": languageUtil.getResource("device_HS05_name"), //"海信冰箱"
        "icon": "../../source/deviceIcon/device_icon_HS05.png",
        "stateUrl": "set_HS05.html",
        "sceneUrl": "set_HS05.html"
    },
    "CMICA2": {
        "name": languageUtil.getResource("device_name_CMICA2"), //随便看
        "icon": "../../source/deviceIcon/device_icon_CMICA2.png",
        "stateUrl": "setCameraAction.html",
        "sceneUrl": "setCameraAction.html"
    },
    "CMICA3": {
        "name": languageUtil.getResource("device_name_CMICA3"), //企鹅
        "icon": "../../source/deviceIcon/device_icon_CMICA3.png",
        "stateUrl": "setCameraAction.html",
        "sceneUrl": "setCameraAction.html"
    },
    "CMICA4": {
        "name": languageUtil.getResource("device_name_CMICA4"), //小物摄像机
        "icon": "../../source/deviceIcon/device_icon_CMICA4.png",
        "stateUrl": "setCameraAction.html",
        "sceneUrl": "setCameraAction.html"
    },
    "CMICA5": {
        "name": languageUtil.getResource("device_name_CMICA5"), //户外
        "icon": "../../source/deviceIcon/device_icon_CMICA5.png",
        "stateUrl": "setCameraAction.html",
        "sceneUrl": "setCameraAction.html"
    },
    "CMICA6": {
        "name": languageUtil.getResource("device_name_CMICA6"), //企鹅4G
        "icon": "../../source/deviceIcon/device_icon_CMICA3.png",
        "stateUrl": "setCameraAction.html",
        "sceneUrl": "setCameraAction.html"
    },
    "XW01": {
        "name": languageUtil.getResource("device_name_XW01"), //向往背景音乐
        "icon": "../../source/deviceIcon/device_icon_XW01.png",
        "stateUrl": "music_XW01.html",
        "sceneUrl": "music_XW01.html"
    },
    "On": {
        "name": languageUtil.getResource("device_name_On"), //
        "icon": "../../source/deviceIcon/device_icon_On.png",
        "stateUrl": "device_On_01.html",
        "sceneUrl": "device_On_01.html"
    },
    "Af": {
        "name": languageUtil.getResource("device_name_Af"), //
        "icon": "../../source/deviceIcon/device_icon_Af.png",
        "stateUrl": "device_Af.html",
        "sceneUrl": "device_Af.html"
    },
    "Br": {
        "name": languageUtil.getResource("device_name_Br"), //
        "icon": "../../source/deviceIcon/device_icon_Br.png",
        "stateUrl": "device_Af.html",
        "sceneUrl": "device_Af.html"
    },
    "Bp": {
        "name": languageUtil.getResource("addDevice_Bp_name"), //
        "icon": "../../source/deviceIcon/device_icon_Bp.png",
        "stateUrl": "set_device_Bp.html",
        "sceneUrl": "set_device_Bp.html"
    },
    "IF02": {
        "name": languageUtil.getResource("device_IF01_name"), //wifi红外转发器
        "icon": "../../source/deviceIcon/device_icon_IF02.png",
        "stateUrl": "javascript:;",
        "sceneUrl": "javascript:;"
    },
    "a0": {
        "name": languageUtil.getResource("device_a0_name"), //中央空调
        "icon": "../../source/deviceIcon/device_icon_a0_01.png",
        "stateUrl": "device_a0_01.html",
        "sceneUrl": "device_a0_01.html"
    },
    "Bs": {
        "name": languageUtil.getResource("device_name_Bs"), //
        "icon": "../../source/deviceIcon/device_icon_Bs.png",
        "stateUrl": "device_Bs.html",
        "sceneUrl": "device_Bs.html"
    },
    "Bx": {
        "name": languageUtil.getResource("device_Bx_name"), //"联排地暖控制器"
        "icon": "../../source/deviceIcon/device_icon_Bx.png",
        "stateUrl": "device_Bx.html",
        "sceneUrl": "device_Bx.html"
    },
    "By": {
        "name": languageUtil.getResource("device_By_name"), //"智敬空调控制器"
        "icon": "../../source/deviceIcon/device_icon_By.png",
        "stateUrl": "device_By.html",
        "sceneUrl": "device_By.html"
    },
    "91": {
        "name": languageUtil.getResource("device_91_name"), //"门磁感应器"
        "icon": "../../source/deviceIcon/device_icon_91.png",
        //timeTask定时任务设备跳转页面
        "stateUrl": "device_91_1.html",
        //sceneTask情景任务执行任务跳转页面
        "sceneUrl": "device_91_1.html"
    },
    "Cc": {
        "name": languageUtil.getResource("device_Cc_name"), //"门磁感应器"
        "icon": "../../source/deviceIcon/device_icon_Cc.png",
        //timeTask定时任务设备跳转页面
        "stateUrl": "device_Cc.html",
        //sceneTask情景任务执行任务跳转页面
        "sceneUrl": "device_Cc.html"
    },
    "Ca": {
        "name": languageUtil.getResource("device_name_Ca"), //"智敬一路窗帘"
        "icon": "../../source/deviceIcon/device_icon_Ca.png",
        //timeTask定时任务设备跳转页面
        "stateUrl": "device_Ca.html",
        //sceneTask情景任务执行任务跳转页面
        "sceneUrl": "device_Ca.html",
        "triggerUrl": "action_Ca_triggerTask.html",
    },
    "Cb": {
        "name": languageUtil.getResource("device_name_Cb"), //"智敬二路窗帘"
        "icon": "../../source/deviceIcon/device_icon_Cb.png",
        //timeTask定时任务设备跳转页面
        "stateUrl": "device_Cb.html",
        //sceneTask情景任务执行任务跳转页面
        "sceneUrl": "device_Cb.html",
        "triggerUrl": "triggerTask_Cb.html",
    },
    //6.2.8
    "64": {
        "name": languageUtil.getResource("device_name_64"), //Wulian三路开关
        "icon": "../../source/deviceIcon/device_icon_64.png",
        "stateUrl": "action_timeTask_setSwitch.html",
        "sceneUrl": "action_sceneTask_setSwitch.html"
    },
    "13": {
        "name": languageUtil.getResource("device_name_13"), //"调光开关"
        "icon": "../../source/deviceIcon/device_icon_13.png",
        "stateUrl": "device_13.html?taskType=timeTask",
        "sceneUrl": "device_13.html?taskType=sceneTask",
    },
    "Cj": {
        "name": languageUtil.getResource("device_Cj_name"), //"联排地暖控制器"
        "icon": "../../source/deviceIcon/device_icon_Cj.png",
        "stateUrl": "device_Bx.html",
        "sceneUrl": "device_Bx.html"
    },
    "Op": {
        "name": languageUtil.getResource("device_name_Op"), //"联排地暖控制器"
        "icon": "../../source/deviceIcon/device_icon_Op_1.png",
        "stateUrl": "device_triple_Op.html",
        "sceneUrl": "device_triple_Op.html"
    },
    "Cl": {
        "name": languageUtil.getResource("device_name_Cl"), //一路智能开关
        "icon": "../../source/deviceIcon/device_icon_Cl.png",
        "stateUrl": "action_timeTask_setSwitch_lock.html",
        "sceneUrl": "action_sceneTask_setSwitch_lock.html",
        "triggerUrl": "action_triggerTask_setSwitch.html"
    },
    "Cm": {
        "name": languageUtil.getResource("device_name_Cm"), //二路智能开关
        "icon": "../../source/deviceIcon/device_icon_Cm.png",
        "stateUrl": "action_timeTask_setSwitch_lock.html",
        "sceneUrl": "action_sceneTask_setSwitch_lock.html",
        "triggerUrl": "action_triggerTask_setSwitch.html"
    },
    "Cn": {
        "name": languageUtil.getResource("device_name_Cn"), //三路智能开关
        "icon": "../../source/deviceIcon/device_icon_Cn.png",
        "stateUrl": "action_timeTask_setSwitch_lock.html",
        "sceneUrl": "action_sceneTask_setSwitch_lock.html",
        "triggerUrl": "action_triggerTask_setSwitch.html"
    },
    "Ck": {
        "name": languageUtil.getResource("device_name_Ck"), // 新风控制器
        "icon": "../../source/deviceIcon/device_icon_Ck.png",
        "stateUrl": "device_Ck.html",
        "sceneUrl": "device_Ck.html"
    }
};
