function initlan() {
	try {
		var iphoneX = {
			sysFunc: "getItem:",
			room: "iphoneX",
			id: "iphoneX",
			data: ""
		};
		var iStr = '';
		if (!window.v6sysfunc) {
			iStr = prompt(JSON.stringify(iphoneX))
		}
		if (iStr == "iphoneX") {
			var body = document.getElementsByClassName("header")[0];
			body.style.paddingTop = "2.4rem";
			var list = document.getElementsByClassName("get_list")
			if (list.length != 0) {
				var ulList = document.getElementsByClassName("get_list")[0].style.paddingTop;
				if (ulList == "6.4rem") {
					document.getElementsByClassName("get_list")[0].style.paddingTop = "8.8rem";
				}
			}
			var iphoneXList = document.getElementsByClassName("iphoneXList");
			if(iphoneXList.length != 0){
				for(var i=0;i<iphoneXList.length;i++){
                    iphoneXList[i].style.paddingTop = "8.8rem";
                    iphoneXList[i].style.minHeight = "calc(100% - 8.8rem)";
				}
			}
			var zone = document.getElementsByClassName("zoneList")
			if (zone.length != 0) {
				zone[0].style.top = "12rem";
			}
			var mainview = document.getElementsByClassName("mainview")
			if (mainview.length != 0) {
				mainview[0].style.marginTop = "8.8rem";
				mainview[0].style.minHeight = "calc(100% - 8.8rem)";
			}
			var top3 = document.getElementsByTagName("body")[0].childNodes[3].style.paddingTop;
			if (top3 == "6.4rem") {
				document.getElementsByTagName("body")[0].childNodes[3].style.paddingTop = "8.8rem";
			} else if (top3 == "9.8rem") {
				document.getElementsByTagName("body")[0].childNodes[3].style.paddingTop = "12.2rem";
			}
            var demo = document.getElementById("demo");
            if(demo){
                var demoTop = demo.style.top;
                if(demoTop == "7rem"){
                    demo.style.top = "9.4rem";
                }
            }
            var actionList = document.getElementById("action_list");
            if(actionList){
                actionList.style.height = "calc(100% - 38.5rem)";
			}
		}
	} catch (e) {
		//TODO handle the exception
	}
	var languageCode = window.sysfun.getLang();
	var el = document.getElementById("noviceGuide");
    if(el){
        if(languageCode === "zh-cn"){
            el.children[0].setAttribute("src","../../source/commonImg/icon_guide_zh-cn.png");
        }else{
            el.children[0].setAttribute("src","../../source/commonImg/icon_guide_en.png");
        }
	}
	languageUtil.onJsLoaded = function () {
		initLanguage();
	};
	languageUtil.init(languageCode, "../lang/");
}

/*
 * 处理标签中的国际化
 */
function initLanguage() {
	globalLang();
	var item = document.getElementsByClassName("autoSwitchLanguager");
	for (var i = 0; i < item.length; i++) {
		var languagerText
		if (!(item[i].getAttribute("key") == null) || item[i].getAttribute("key") == "") {
			languagerText = languageUtil.getResource(item[i].getAttribute("key"));
		} else {
			languagerText = languageUtil.getResource(item[i].id);
		}
		if (item[i].placeholder != "" && item[i].localName == "input") {
			if (languagerText) {
				item[i].placeholder = languagerText;
			}
		} else {
			if (languagerText) {
				if (item[i].type == "button") {
					item[i].value = languagerText;
				} else if (item[i].getAttribute("data-id")) {
					item[i].setAttribute("data-name", languagerText);
				} else {
					item[i].innerHTML = languagerText;
				}
			}
		}
	}
}
/*
 * 处理js中的国际化
 */

function globalLang() {
	window.lang_open = languageUtil.getResource("open") //开启
	window.lang_close = languageUtil.getResource("close"); //"关闭"
	window.lang_play = languageUtil.getResource("device_status_play"); //"播放声音"
	window.lang_normalAlarm = languageUtil.getResource("normalAlarm") //通用报警
	window.lang_fireAlarm = languageUtil.getResource("fireAlarm") //火灾报警
	window.lang_inputName = languageUtil.getResource("inputName"); //请输入场景名称
	window.lang_disarming = languageUtil.getResource("disarming"); //"撤防"
	window.lang_fortify = languageUtil.getResource("fortify"); //"设防"
	window.lang_resetAlarm = languageUtil.getResource("stopAlarm"); //"停止报警"
	window.lang_group = languageUtil.getResource("addDevice_group_text"); //"未分区"
	window.lang_min = languageUtil.getResource("min"); //"分"
	window.lang_sec = languageUtil.getResource("sec"); //"秒"
	window.lang_secondDo = languageUtil.getResource("afterSecondsDo"); //"秒后开始执行"
	window.lang_delete = languageUtil.getResource("delete"); //"删除"
	window.lang_device_delete = languageUtil.getResource("device_delete"); //"设备已删除"
	window.lang_offLine = languageUtil.getResource("chooseSensor_offLine"); //"离线"
	window.lang_onLine = languageUtil.getResource("chooseSensor_onLine"); //"在线"
	window.lang_openSingle = languageUtil.getResource("openSingle") //"开"
	window.lang_closeSingle = languageUtil.getResource("closeSingle") //"关"
	window.lang_stopSingle = languageUtil.getResource("stopSingle") //"停"
	window.device_switch = languageUtil.getResource("device_switch") //"开关"

	window.switch_AoEs_03 = languageUtil.getResource("switch_AoEs_03") //"非开关模式"
	window.lang_D8_voice1 = languageUtil.getResource("minigateway_Sound_DingDong") //"叮咚"
	window.lang_D8_voice2 = languageUtil.getResource("minigateway_Sound_Jingle") //"叮铃"
	window.lang_D8_voice3 = languageUtil.getResource("minigateway_Sound_Crisp") //"清脆"
	window.lang_D8_voice4 = languageUtil.getResource("minigateway_Sound_Longsound") //"长音"
	window.lang_D8_voice5 = languageUtil.getResource("minigateway_Sound_Wave") //"波动"
	window.lang_D8_voice6 = languageUtil.getResource("minigateway_Sound_Cuckoo") //"布谷"
	window.lang_D8_voice7 = languageUtil.getResource("minigateway_Sound_Didi") //"嘀嘀"
	window.lang_D8_voice8 = languageUtil.getResource("minigateway_Sound_Fierce") //"激烈"
	window.lang_D8_voice9 = languageUtil.getResource("minigateway_Sound_Quick") //"急促"
	window.lang_D8_voice10 = languageUtil.getResource("minigateway_Sound_Sharp") //"尖利"
	window.lang_D8_voice11 = languageUtil.getResource("minigateway_Sound_Policecar") //"警车"

	window.lang_D8_mode1 = "OFF"
	window.lang_D8_mode2 = languageUtil.getResource("minigateway_Lantern_Always") //"常亮"
	window.lang_D8_mode3 = languageUtil.getResource("minigateway_Lantern_Flowingwater") //"流水"
	window.lang_D8_mode4 = languageUtil.getResource("minigateway_Lantern_Flash") //"快闪"
	window.lang_D8_mode5 = languageUtil.getResource("minigateway_Lantern_Slowflash") //"慢闪"

	window.lang_D8_color1 = languageUtil.getResource("minigateway_Colour_red") //"烈焰红"
	window.lang_D8_color2 = languageUtil.getResource("minigateway_Colour_purple") //"浪漫紫"
	window.lang_D8_color3 = languageUtil.getResource("minigateway_Colour_blue") //"湖水蓝"
	window.lang_D8_color4 = languageUtil.getResource("minigateway_Colour_orange") //"活力橙"
	window.lang_D8_color5 = languageUtil.getResource("minigateway_Colour_green") //"翡翠绿"
	window.lang_D8_color6 = languageUtil.getResource("minigateway_Colour_white") //"日光白"

	window.lang_afterMinsDo = languageUtil.getResource("afterMinsDo"); //分钟后执行
	window.lang_afterSecDo = languageUtil.getResource("afterSecDo"); //秒后执行
	window.lang_immediatelyDo = languageUtil.getResource("immediatelyDo"); //立即执行
	window.device_curtain = languageUtil.getResource("device_curtain") //"窗帘"
	//v6.0.3新增
	window.lang_D7_color = languageUtil.getResource("colour"); //颜色RGB
	window.lang_D7_brightness = languageUtil.getResource("brightness"); //亮度
	window.lang_D7_modeTitle = languageUtil.getResource("mode"); //模式
	window.lang_D7_setColor = languageUtil.getResource("set_colour"); //设置颜色和亮度
	window.lang_D7_setMode = languageUtil.getResource("set_mode"); //设置模式
	window.lang_D7_mode_dinner = languageUtil.getResource("mode_1"); //烛光晚餐
	window.lang_D7_mode_moon = languageUtil.getResource("mode_2"); //月色迷人
	window.lang_D7_mode_blue = languageUtil.getResource("mode_3"); //蓝色小夜曲
	window.lang_D7_mode_green = languageUtil.getResource("mode_4"); //绿色心情
	window.lang_D7_rgb = languageUtil.getResource("colour_rgb_title"); //RGB
	window.lang_D7_light = languageUtil.getResource("brightness_title"); //Light
	window.lang_D7_setColor_tips = languageUtil.getResource("set_rgb_ok_error"); //请设置六位有效颜色RGB值
	window.lang_DD_play = languageUtil.getResource("Music_houseKeeper_play"); //"背景音乐 播放"
	window.lang_DD_stop = languageUtil.getResource("Music_houseKeeper_stop"); //"背景音乐 停止"
	window.lang_DD_1 = languageUtil.getResource("Music_volume_1"); //"背景音乐 1"
	window.lang_DD_2 = languageUtil.getResource("Music_volume_2"); //"背景音乐 2"
	window.lang_DD_3 = languageUtil.getResource("Music_volume_3"); //"背景音乐 3"
	window.lang_DD_4 = languageUtil.getResource("Music_volume_4"); //"背景音乐 4"
	window.LEDlight_90_sunlight = languageUtil.getResource("LEDlight_90_sunlight"); //"暖白"
	window.LEDlight_90_Colorful = languageUtil.getResource("LEDlight_90_Colorful"); //"炫彩"
	window.LEDlight_90_ok = languageUtil.getResource("LEDlight_90_ok") //"确定";
	window.LEDlight_90_Cancel = languageUtil.getResource("LEDlight_90_Cancel") //"取消";
	window.LEDlight_90_Interval = languageUtil.getResource("LEDlight_90_Interval") //"间隔";
	window.device_Ba_SocketName = languageUtil.getResource("device_Ba_SocketName"); //"插座"
	window.device_Ba_Electricity = languageUtil.getResource("device_Ba_Electricity"); //"通电"
	window.device_Ba_PowerFailure = languageUtil.getResource("device_Ba_PowerFailure"); //"断电"

	//6.1.2
	window.lang_switch_cut = languageUtil.getResource("Switch_Setstate_handover") //"切换";

	//6.1.3
	window.switch_AoEs_01 = languageUtil.getResource("switch_AoEs_01"); // 打开
	window.switch_AoEs_02 = languageUtil.getResource("switch_AoEs_02"); // 关闭

	//6.1.4
	window.lang_OZ_Open = languageUtil.getResource("editScene_OZ_Open"); //"开关"
	window.lang_OZ_Type = languageUtil.getResource("editScene_OZ_Type"); // "模式";
	window.lang_OZ_Type1 = languageUtil.getResource("editScene_OZ_Type1"); // "制冷";
	window.lang_OZ_Type2 = languageUtil.getResource("editScene_OZ_Type2"); // "制热";
	window.lang_OZ_Type3 = languageUtil.getResource("editScene_OZ_Type3"); // "送风";
	window.lang_OZ_Type4 = languageUtil.getResource("editScene_OZ_Type4"); // "除湿";
	window.lang_OZ_Type5 = languageUtil.getResource("editScene_OZ_Type5"); // "预热";
	window.lang_OZ_Type6 = languageUtil.getResource("editScene_OZ_Type6"); // "自动";
	window.lang_OZ_Type7 = languageUtil.getResource("editScene_OZ_Type7"); // "干燥";
	window.lang_OZ_Type8 = languageUtil.getResource("editScene_OZ_Type8"); // "清爽";
	window.lang_OZ_Type9 = languageUtil.getResource("editScene_OZ_Type9"); // "睡眠";
	window.lang_OZ_Type10 = languageUtil.getResource("editScene_OZ_Type10"); // "杀菌";
	window.lang_OZ_Type11 = languageUtil.getResource("editScene_OZ_Type11"); // "干爽除湿";
	window.lang_OZ_Type12 = languageUtil.getResource("editScene_OZ_Type12"); // "强力除湿";
	window.lang_OZ_Speed = languageUtil.getResource("editScene_OZ_Speed"); //"风速";
	window.lang_OZ_Speed1 = languageUtil.getResource("editScene_OZ_Speed1"); // "高";
	window.lang_OZ_Speed2 = languageUtil.getResource("editScene_OZ_Speed2"); // "中";
	window.lang_OZ_Speed3 = languageUtil.getResource("editScene_OZ_Speed3"); // "低";
	window.lang_OZ_Speed4 = languageUtil.getResource("editScene_OZ_Speed4"); // "自动";
	window.lang_OZ_Direction = languageUtil.getResource("editScene_OZ_Direction"); // "风向";
	window.lang_OZ_Temperature = languageUtil.getResource("editScene_OZ_Temperature"); // "温度";
	window.lang_OZ_Confirm = languageUtil.getResource("editScene_OZ_Confirm"); // "确定";
	window.lang_OZ_Cancel = languageUtil.getResource("editScene_OZ_Cancel"); // "取消";
	window.lang_OZ_TIPS2 = languageUtil.getResource("editScene_OZ_TIPS2"); // "全选";

	//6.1.5
	window.device_28_on = languageUtil.getResource("device_28_on"); //开启
	window.device_28_off = languageUtil.getResource("device_28_off"); //关闭
	window.device_28_close_after = languageUtil.getResource("device_28_close_after"); //后关闭

	//6.1.6
	window.device_A1_way = languageUtil.getResource("device_A1_way"); //路
	window.device_A2_no_button = languageUtil.getResource("device_A2_no_button"); //没有按键
	window.device_Bo_no_button = languageUtil.getResource("device_Bo_no_button"); //没有按键
	window.editScene_Ok_Mute = languageUtil.getResource("editScene_Ok_Mute"); //静音
	window.editScene_Ok_Direction1 = languageUtil.getResource("editScene_Ok_Direction1"); //"左右风";
	window.editScene_Ok_Direction2 = languageUtil.getResource("editScene_Ok_Direction2"); //"上下风";
	window.editScene_Ok_Direction_close = languageUtil.getResource("airsystem_Oj_close"); //"关闭";

	window.device_Ol_mode_cool = languageUtil.getResource("device_Ol_mode_cool"); //"制冷";
	window.device_Ol_mode_heat = languageUtil.getResource("device_Ol_mode_heat"); //"制热";
	window.device_Ol_mode_fan = languageUtil.getResource("device_Ol_mode_fan"); //"送风";
	window.device_Ol_mode_warm = languageUtil.getResource("device_Ol_mode_warm"); //"地暖";
	window.device_Ol_mode_warm_heat = languageUtil.getResource("device_Ol_mode_warm_heat"); //"地暖+制热";
	window.lang_switch_on = languageUtil.getResource("switch_on"); //"开";
	window.lang_switch_off = languageUtil.getResource("switch_off"); //"关";

	// 6.1.7
	window.aw_bell_short = languageUtil.getResource("device_Aw_short"); //"段铃";
	window.aw_bell_long = languageUtil.getResource("device_Aw_long"); //"响铃";
	window.aw_bell_stop = languageUtil.getResource("device_Aw_stop"); //"停止";

	window.lang_name_XW01 = languageUtil.getResource("device_name_XW01"); //向往背景音乐
	window.lang_XW01_play = languageUtil.getResource("Music_houseKeeper_play"); //播放
	window.lang_XW01_stop = languageUtil.getResource("Music_houseKeeper_stop"); //停止播放
	window.lang_XW01_volume = languageUtil.getResource("minigateway_Sound_volume"); //音量

	window.lang_HS05_model = languageUtil.getResource("Refrigerator_Pattern"); //"模式"
	window.lang_HS05_model_0 = languageUtil.getResource("Refrigerator_mode_Holiday"); //"假日模式"
	window.lang_HS05_model_1 = languageUtil.getResource("Refrigerator_mode_Intelligence"); //"智能模式"
	window.lang_HS05_model_2 = languageUtil.getResource("Refrigerator_mode_Quickfrozen"); //"速冻模式"

	window.device_OW_title = languageUtil.getResource("addDevice_OW_name");
	window.device_Bd_title = languageUtil.getResource("device_Bd_title");
	window.device_Bc_title = languageUtil.getResource("alarmSet_txt_19");
	window.device_Bf_title = languageUtil.getResource("device_Bf_title");
	window.device_Bg_title = languageUtil.getResource("addDevice_Bg_name");
	window.device_OP_title = languageUtil.getResource("addDevice_op");
	window.device_list_unlock = languageUtil.getResource("device_list_unlock");

	// 6.1.8
	window.lang_camera_safety = languageUtil.getResource("lang_camera_safety"); //安全防护
	window.lang_camera_position = languageUtil.getResource("lang_camera_position"); //预置位
	window.lang_camera_opencamera = languageUtil.getResource("camera_houseKeeper_task"); //开启视频
	window.lang_camera_safety_open = languageUtil.getResource("lang_camera_safety_open"); //开启安全防护
	window.lang_camera_safety_close = languageUtil.getResource("lang_camera_safety_close"); //关闭安全防护

	//6.1.9
	window.lang_airsystem_Oj_Wind_speed_low = languageUtil.getResource("airsystem_Oj_Wind_speed_low"); //低风
	window.lang_airsystem_Oj_Wind_speed_mid = languageUtil.getResource("airsystem_Oj_Wind_speed_medium"); //中风
	window.lang_airsystem_Oj_Wind_speed_high = languageUtil.getResource("airsystem_Oj_Wind_speed_high"); //高风
	window.lang_allAreas = languageUtil.getResource("device_list_allAreas"); //全部分区
	window.lang_categories = languageUtil.getResource("device_list_Categories"); //全部分区

	// 6.2.0
	window.lang_name_Af = languageUtil.getResource("device_Af_name"); //Af 温度控制器
	window.device_Af_Cool = languageUtil.getResource("device_Af_Cool"); //制冷节能
	window.device_Af_Hot = languageUtil.getResource("device_Af_Hot"); //制热节能
	window.lang_name_Bp = languageUtil.getResource("device_Bp_name"); //罗格朗空调
	window.direction_swing = languageUtil.getResource("device_Bp_Swing"); //罗格朗空调摆动

	// 6.2.1
	window.Add_batches_switch = languageUtil.getResource("Add_batches_switch"); //开关类设备
	window.Add_batches_curtain = languageUtil.getResource("Add_batches_curtain"); //窗帘类设备
	window.Add_batches_socket = languageUtil.getResource("Add_batches_socket"); //插座类设备
	window.Add_batches_save = languageUtil.getResource("Add_batches_save"); //请设置完所有设备状态后，再保存
	window.unselect_all = languageUtil.getResource("unselect_all"); //全不选

	// 6.2.2
	window.lang_name_Bt = languageUtil.getResource("device_name_Bt"); //一位墙面插座
	window.lang_a0_Type1 = languageUtil.getResource("editScene_OZ_Type3"); // "送风";
	window.lang_a0_Type2 = languageUtil.getResource("editScene_OZ_Type2"); // "制热";
	window.lang_a0_Type3 = languageUtil.getResource("editScene_OZ_Type1"); // "制冷";
	window.lang_a0_Type4 = languageUtil.getResource("editScene_OZ_Type6"); // "自动";
	window.lang_a0_Type5 = languageUtil.getResource("editScene_OZ_Type4"); // "除湿";

	window.lang_editScene_type1 = languageUtil.getResource("Device_Bs_01"); //离开模式
	window.lang_editScene_type2 = languageUtil.getResource("Device_Bs_02"); //单室内温度控制
	window.lang_editScene_type3 = languageUtil.getResource("Device_Bs_03"); //双温双控
	window.lang_editScene_type4 = languageUtil.getResource("Device_Bs_04"); //单地面温度控制
	window.Device_Bs_05 = languageUtil.getResource("Device_Bs_05");
	window.Device_Bs_06 = languageUtil.getResource("Device_Bs_06");
	window.Device_a0_host = languageUtil.getResource("Device_a0_host");
	window.Device_a0_air = languageUtil.getResource("Device_a0_aircondition");
    window.Scene_icon_01 = languageUtil.getResource("Scene_icon_01");//"离家";
    window.Scene_icon_02 = languageUtil.getResource("Scene_icon_02");//"回家";
    window.Scene_icon_03 = languageUtil.getResource("Scene_icon_03");//"睡眠";
    window.Scene_icon_04 = languageUtil.getResource("Scene_icon_04");//"起夜";
    window.Scene_icon_05 = languageUtil.getResource("Scene_icon_05");//"起床";
    window.Scene_icon_06 = languageUtil.getResource("Scene_icon_06");//"影院";
    window.Scene_icon_07 = languageUtil.getResource("Scene_icon_07");//"会客";
    window.Scene_icon_08 = languageUtil.getResource("Scene_icon_08");//"就餐";
    window.Scene_icon_09 = languageUtil.getResource("Scene_icon_09");//"运动";
    window.Scene_icon_10 = languageUtil.getResource("Scene_icon_10");//"全开";
    window.Scene_icon_11 = languageUtil.getResource("Scene_icon_11");//"全关";
    window.Scene_icon_12 = languageUtil.getResource("Scene_icon_12");//"离房";
    window.Scene_icon_13 = languageUtil.getResource("Scene_icon_13");//"回房";
    window.Scene_icon_14 = languageUtil.getResource("Scene_icon_14");//"设防";
    window.Scene_icon_15 = languageUtil.getResource("Scene_icon_15");//"撤防";
    window.Scene_icon_16 = languageUtil.getResource("Scene_icon_16");//"加湿";
    window.Scene_icon_17 = languageUtil.getResource("Scene_icon_17");//"除湿";
    window.Scene_icon_18 = languageUtil.getResource("Scene_icon_18");//"温馨";
    window.Scene_icon_19 = languageUtil.getResource("Scene_icon_19");//"空调";
    window.Scene_icon_20 = languageUtil.getResource("Scene_icon_20");//"电视";
    window.Scene_icon_21 = languageUtil.getResource("Scene_icon_21");//"灯光";
    window.Scene_icon_22 = languageUtil.getResource("Scene_icon_22");//"窗帘";
    window.Scene_icon_23 = languageUtil.getResource("Scene_icon_23");//"风机";
    window.Scene_icon_24 = languageUtil.getResource("Scene_icon_24");//"自定义";
    window.unselect_all = languageUtil.getResource("unselect_all");//"全不选";
	//6.2.5
	window.direction_large = languageUtil.getResource("Wind_Direction_01");//"大角度";
    window.direction_mid = languageUtil.getResource("Wind_Direction_02");//"中角度";
    window.direction_small = languageUtil.getResource("Wind_Direction_03");//"小角度";
    window.direction_swing = languageUtil.getResource("Wind_Direction_04");//"扫风";

	//6.2.6
    window.device_91_01 = languageUtil.getResource("device_91_01");//"中性白"
    window.device_91_02 = languageUtil.getResource("device_91_02");//"冷色："
    window.device_91_03 = languageUtil.getResource("device_91_03");//"暖色："
    window.device_91_04 = languageUtil.getResource("device_91_04");//"亮度："

	//6.2.7
    window.device_Ca_name = languageUtil.getResource("device_name_Ca");//"智敬一路窗帘"
	window.device_Cb_name = languageUtil.getResource("device_name_Cb");//"智敬二路窗帘"
	window.device_Ca_mode_fail_01 = languageUtil.getResource("device_Ca_mode_fail_01");//"请先将设备设为窗帘模式";

	//引用deviceList.js，场景中的设备名称以后不用在这里添加全局变量了
	new_element = document.createElement("script");
	new_element.setAttribute("type", "text/javascript");
	new_element.setAttribute("src", "../js/deviceList.js");
	document.body.appendChild(new_element);
}

var languageUtil = function () {};

languageUtil.prototype = {

	/**
	 * 自定义html控件属性，例如 langKey="Key_hello"; 指明语言资源的键 为  Key_hello
	 */
	langKey: "langKey",
	/**
	 * 语言文件(例如 zh.js)是否正在加载
	 */
	isLoading: false,
	/**
	 * 语言文件(例如 zh.js)是否加载完成
	 */
	isLoaded: false,
	/**
	 * 当前语言  "zh"   "en" ....
	 */
	currentlangCode: "",
	/**
	 * 当前的语言文件对象
	 */
	currentJsObj: null,

	/**
	 *  初始化 加载 langCode对应的js
	 * @param {Object} langCode 例如 "zh" "en"
	 * @param {Object} langSrcFolder 语言文件所在位置的相对路径
	 */
	init: function (langCode, langSrcFolder) {
		try {
			if (this.currentlangCode != langCode) {
				this.removeJs(this.currentJsObj);
				this.currentlangCode = langCode;
				var that = this;
				this.currentJsObj = this.loadJs(langSrcFolder + langCode + ".js");
			}
		} catch (e) {
			console.log(e);
		}
		return this.currentJsObj;
	},
	/**
	 * 转换语言
	 * @param {Object} node 需要转换的节点
	 */
	doSwitch: function (node) {
		var children = node.children;
		if (children.length > 0) {
			for (var i = 0; i < children.length; i++) {
				this.doSwitch(children[i]);
			}
		} else {
			var key = node.attributes[this.langKey];
			if (typeof key != "undefined") {
				switch (node.type) {
					case "button":
						node.value = $lang[key.value];
						break;

					default:
						node.textContent = $lang[key.value];
						break;
				}

			}
		}
	},
	/**
	 * 动态装载js
	 */
	loadJs: function (jsPath) {
		this.isLoaded = false;
		this.isLoading = true;
		var mHead = document.getElementsByTagName("head")[0];
		var jsObj = document.createElement("script");
		jsObj.src = jsPath;
		jsObj.type = "text/javascript";
		mHead.appendChild(jsObj);
		jsObj.onload = function () {
			languageUtil.onJsLoaded();
		};
		jsObj.onerror = function () {
			if (this.currentlangCode != "en") {
				languageUtil.init("en", "lang/");
			}
		}
		return jsObj;
	},
	/**
	 * 当动态装载的js准备完毕调用
	 */
	onJsLoaded: function () {
		this.isLoaded = true;
		this.isLoading = false;
		window.all = document.all;
		for (var i = 0; i < all.length; i++) {
			var node = all[i];
			var key = node.attributes[this.langKey];
			if (typeof key != "undefined") {
				switch (node.type) {
					case "button":
						node.value = $lang[key.value];
						break;

					default:
						node.textContent = $lang[key.value];
						break;
				}

			}
		}

		/*
		 var mBody = document.getElementsByTagName("body")[0];
		 if (mBody == null) return;
		 this.doSwitch(mBody);
		 */
	},
	/**
	 * 移除
	 */
	removeJs: function (jsObj) {
		if (jsObj != null) {
			var mHead = document.getElementsByTagName("head")[0];
			mHead.removeChild(jsObj);
		}
		this.isLoaded = false;
		this.isLoading = false;
	},
	/**
	 * 根据 key 获取语言资源文件
	 * @param {Object} key
	 */
	getResource: function (key) {
		try {
			return $lang[key];
		} catch (e) {
			return "";
		}
	}

};

window.languageUtil = new languageUtil();