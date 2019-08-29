function initlan() {
	try {
		var iphoneX = {sysFunc: "getItem:", room: "iphoneX", id: "iphoneX", data: ""};
		var iStr ='';
		if(!window.v6sysfunc) {
             iStr = prompt(JSON.stringify(iphoneX))
        }
		if(iStr == "iphoneX") {
			var offArr = document.getElementsByClassName("mask_layer")
			if(offArr != '' && offArr.length != 0){
				offArr[0].style.top = "8.8rem";
                offArr[0].style.height = "calc(100% - 8.8rem)";
			}
            var offArr1 = document.getElementsByClassName("mask_loading")
            if(offArr1 != '' && offArr1.length != 0){
                offArr1[0].style.top = "8.8rem";
            }
            var offArr2 = document.getElementsByClassName("saveToast")[0]
            offArr2.style.top = "8.8rem";
            var offArr3 = document.getElementsByClassName("mask_name")
            if(offArr3 != '' && offArr3.length != 0){
                offArr1[0].style.top = "8.8rem";
                offArr1[1].style.top = "8.8rem";
            }
			var body = document.getElementsByClassName("header")[0];
			body.style.paddingTop = "2.4rem"
			var top3 = document.getElementsByTagName("body")[0].children[1].style.paddingTop;
			if(top3 == "6.4rem") {
				document.getElementsByTagName("body")[0].children[1].style.paddingTop = "8.8rem";
			} else if(top3 == "9.8rem") {
				document.getElementsByTagName("body")[0].children[1].style.paddingTop = "12.2rem";
			}
		}
	} catch(e) {
		//TODO handle the exception
	}
	var languageCode = window.sysfun.getLang();//window.sysfun.getLang();
	languageUtil.onJsLoaded = function() {
		initLanguage();
	}
	languageUtil.init(languageCode, "../lang/");
}

/*
 * 处理标签中的国际化
 */
function initLanguage() {
	globalLang();
	var item = document.getElementsByClassName("autoSwitchLanguager");
	for(var i = 0; i < item.length; i++) {
		var languagerText
		if(!(item[i].getAttribute("key") == null) || item[i].getAttribute("key") == "") {
			languagerText = languageUtil.getResource(item[i].getAttribute("key"));
		} else {
			languagerText = languageUtil.getResource(item[i].id);
		}
		if(item[i].placeholder != "" && item[i].localName == "input") {
			if(languagerText) {
				item[i].placeholder = languagerText;
			}
		} else {
			if(languagerText) {
				if(item[i].type == "button") {
					item[i].value = languagerText;
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
    window.device_Op_name = languageUtil.getResource("device_Op_name");//"空调新风地暖集控器";
    window.device_Op_fan_Bias_High = languageUtil.getResource("device_Op_fan_Bias_High");//"中高风";
    window.device_Op_fan_Bias_Low = languageUtil.getResource("device_Op_fan_Bias_Low");//"中低风";
    window.device_Op_closed_hint = languageUtil.getResource("device_Op_closed_hint");//开启防冻保护可避免设备意外损坏，确定要关闭吗？";
    window.device_Op_Frost_protect_open = languageUtil.getResource("device_Op_Frost_protect_open");//"防冻保护已开启";
    window.device_Op_Frost_protect_closed = languageUtil.getResource("device_Op_Frost_protect_closed");//"防冻保护已关闭";
    window.Device_Op_Details_Prompt = languageUtil.getResource("Device_Op_Details_Prompt");//"提示";
    window.Add_batches_i_know = languageUtil.getResource("Add_batches_i_know");//"我知道了";
    window.Device_Op_Refresh = languageUtil.getResource("Device_Op_Refresh");//"刷新列表";
    window.device_Op_all_closed = languageUtil.getResource("device_Op_all_closed");//"全部关闭";
    window.Device_Op_Host = languageUtil.getResource("Device_Op_Host");//"主机";
    window.Device_Op_Slave = languageUtil.getResource("Device_Op_Slave");//"从机";
    window.Device_Op_Details_Temperature1 = languageUtil.getResource("Device_Op_Details_Temperature1");//"室温";
    window.device_Op_mode = languageUtil.getResource("device_Op_mode");//"模式";
    window.device_Op_mode_1 = languageUtil.getResource("device_Op_mode_1");//"模式：";
    window.device_Op_mode_cool = languageUtil.getResource("device_Op_mode_cool");//"制冷";
    window.device_Op_mode_heat = languageUtil.getResource("device_Op_mode_heat");// "制热";
    window.device_Op_mode_fan = languageUtil.getResource("device_Op_mode_fan");//"送风";
    window.houseKeeper1_Op_Type4 = languageUtil.getResource("houseKeeper1_Op_Type4");//"除湿";
    window.Device_Op_Details_Type5 = languageUtil.getResource("Device_Op_Details_Type5");//"预热";
    window.device_Op_mode_auto = languageUtil.getResource("device_Op_mode_auto");//"自动";
    window.Device_Op_Details_Type7 = languageUtil.getResource("Device_Op_Details_Type7");//"干燥";
    window.Device_Op_Details_Type8 = languageUtil.getResource("Device_Op_Details_Type8");//"清爽";
    window.Device_Op_Details_Type9 = languageUtil.getResource("Device_Op_Details_Type9");//"睡眠";
    window.Device_Op_Details_Type10 = languageUtil.getResource("Device_Op_Details_Type10");//"杀菌";
    window.Device_Op_Details_Type11 = languageUtil.getResource("Device_Op_Details_Type11");//"干爽除湿";
	window.Device_Op_Details_Type12 = languageUtil.getResource("Device_Op_Details_Type12");//"强力除湿";
	window.device_Op_Wind_speed = languageUtil.getResource("device_Op_Wind_speed");//"风速";
	window.device_Op_Wind_speed_1 = languageUtil.getResource("device_Op_Wind_speed_1");//"风速：";
	window.airsystem_Op_Wind_speed_high = languageUtil.getResource("airsystem_Op_Wind_speed_high");//"高风";
	window.airsystem_Op_Wind_speed_medium = languageUtil.getResource("airsystem_Op_Wind_speed_medium");//"中风";
	window.airsystem_Op_Wind_speed_low = languageUtil.getResource("airsystem_Op_Wind_speed_low");//"低风";
	window.Device_Op_Details_Time = languageUtil.getResource("Device_Op_Details_Time");//"定时";
	window.Device_Op_Cancel = languageUtil.getResource("Device_Op_Cancel");//"取消";
	window.Device_Op_OK = languageUtil.getResource("Device_Op_OK");//"确定";
	window.Device_Op_Details_Time_TIPS3 = languageUtil.getResource("Device_Op_Details_Time_TIPS3");//"倒计时开启";
	window.Device_Op_Details_Time7 = languageUtil.getResource("Device_Op_Details_Time7");//"时";
	window.Device_Op_Details_Time8 = languageUtil.getResource("Device_Op_Details_Time8");//"分";
	window.device_Op_open_after = languageUtil.getResource("device_Op_open_after");//"后开启";
	window.Device_Op_Details_Time_TIPS4 = languageUtil.getResource("Device_Op_Details_Time_TIPS4");//"倒计时关闭";
	window.device_Op_Countdown_closed = languageUtil.getResource("device_Op_Countdown_closed");//"后关闭";
	window.device_Op_Sure_to_cancel = languageUtil.getResource("device_Op_Sure_to_cancel");//"确定取消倒计时？";
	window.device_Op_set = languageUtil.getResource("device_Op_set");//"设置";
	window.device_Op_Rename = languageUtil.getResource("device_Op_Rename");//"重命名";
	window.device_Op_enter_name = languageUtil.getResource("device_Op_enter_name");//"请输入名称";
	window.device_Op_on = languageUtil.getResource("device_Op_on");//"已开启";
	window.device_Op_off = languageUtil.getResource("device_Op_off");//"已关闭";
	window.device_Op_indoor = languageUtil.getResource("device_Op_indoor");//"室内";
	window.device_Op_outdoor = languageUtil.getResource("device_Op_outdoor");//"室外";
	window.device_Op_mode_Ventilation = languageUtil.getResource("device_Op_mode_Ventilation");//"换气";
	window.device_Op_mode_Exhaust = languageUtil.getResource("device_Op_mode_Exhaust");//"排风";
	window.device_Op_mode_smart = languageUtil.getResource("device_Op_mode_smart");//"智能";
	window.device_Op_mode_strong = languageUtil.getResource("device_Op_mode_strong");//"强劲";
	window.device_Op_mode_Power_saving = languageUtil.getResource("device_Op_mode_Power_saving");//"省电";
	window.device_Op_Details_Temperature2 = languageUtil.getResource("device_Op_Details_Temperature2");//"地温";
	window.device_Op_Alarm_01 = languageUtil.getResource("device_Op_Alarm_01");//"室温传感器故障";
	window.device_Op_Alarm_02 = languageUtil.getResource("device_Op_Alarm_02");//"地温传感器故障";
	window.offLine_1 = languageUtil.getResource("offLine_1");//"离线";
	window.onLine = languageUtil.getResource("onLine");//"在线";
	window.group_text = languageUtil.getResource("group_text");//"未分区";
	window.offLine = languageUtil.getResource("offLine");//"设备已离线";
	window.device_Op_time_out = languageUtil.getResource("device_Op_time_out");//"连接超时";
	window.device_Op_ing = languageUtil.getResource("device_Op_ing");//"正在加载";
	window.device_Op_Details_Low_Protect = languageUtil.getResource("device_Op_Details_Low_Protect");//"防冻";
}

var languageUtil = function() {};

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
	init: function(langCode, langSrcFolder) {
		try {
			if(this.currentlangCode != langCode) {
				this.removeJs(this.currentJsObj);
				this.currentlangCode = langCode;
				var that = this;
				this.currentJsObj = this.loadJs(langSrcFolder + langCode + ".js");
			}
		} catch(e) {
			console.log(e);
		}
		return this.currentJsObj;
	},
	/**
	 * 转换语言
	 * @param {Object} node 需要转换的节点
	 */
	doSwitch: function(node) {
		var children = node.children;
		if(children.length > 0) {
			for(var i = 0; i < children.length; i++) {
				this.doSwitch(children[i]);
			}
		} else {
			var key = node.attributes[this.langKey];
			if(typeof key != "undefined") {
				switch(node.type) {
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
	loadJs: function(jsPath) {
		this.isLoaded = false;
		this.isLoading = true;
		var mHead = document.getElementsByTagName("head")[0];
		var jsObj = document.createElement("script");
		jsObj.src = jsPath;
		jsObj.type = "text/javascript";
		mHead.appendChild(jsObj);
		jsObj.onload = function() {
			languageUtil.onJsLoaded();
		};
		jsObj.onerror = function() {
			if(this.currentlangCode != "en") {
				languageUtil.init("en", "lang/");
			}
		}
		return jsObj;
	},
	/**
	 * 当动态装载的js准备完毕调用
	 */
	onJsLoaded: function() {
		this.isLoaded = true;
		this.isLoading = false;
		var all = document.all;
		for(var i = 0; i < all.length; i++) {
			var node = all[i];
			var key = node.attributes[this.langKey];
			if(typeof key != "undefined") {
				switch(node.type) {
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
	removeJs: function(jsObj) {
		if(jsObj != null) {
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
	getResource: function(key) {
		try {
			return $lang[key];
		} catch(e) {
			return "";
		}
	}

};

window.languageUtil = new languageUtil();