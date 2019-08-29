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
			var offArr = document.getElementsByClassName("mask_layer")
			if (offArr != '' && offArr.length != 0) {
				offArr[0].style.top = "8.8rem";
                offArr[0].style.height = "calc(100% - 8.8rem)";
			}
			var offArr1 = document.getElementsByClassName("mask_loading")
			if (offArr1 != '' && offArr1.length != 0) {
				offArr1[0].style.top = "8.8rem";
			}
			var offArr2 = document.getElementsByClassName("saveToast")[0]
			offArr2.style.top = "8.8rem";
			var offArr3 = document.getElementsByClassName("mask_name")
			if (offArr3 != '' && offArr3.length != 0) {
				offArr1[0].style.top = "8.8rem";
				offArr1[1].style.top = "8.8rem";
			}
			var body = document.getElementsByClassName("header")[0];
			body.style.paddingTop = "2.4rem"
			var top3 = document.getElementsByTagName("body")[0].children[1].style.paddingTop;
			if (top3 == "6.4rem") {
				document.getElementsByTagName("body")[0].children[1].style.paddingTop = "8.8rem";
			} else if (top3 == "9.8rem") {
				document.getElementsByTagName("body")[0].children[1].style.paddingTop = "12.2rem";
			}
		}
	} catch (e) {
		//TODO handle the exception
	}
	var languageCode = window.sysfun.getLang(); //window.sysfun.getLang();
	languageUtil.onJsLoaded = function () {
		initLanguage();
	}
	languageUtil.init(languageCode, "lang/");
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
	window.lang_name_a0 = languageUtil.getResource("device_a0_name"); //"空调翻译器";
	window.lang_host = languageUtil.getResource("Host"); //"主机";
	window.lang_slave = languageUtil.getResource("Slave"); //"从机";
	window.lang_Details_Open = languageUtil.getResource("On"); //"开机";
	window.lang_Details_Close = languageUtil.getResource("Off"); //"关机";
	window.lang_Details_Temperature = languageUtil.getResource("Temp"); //室温";
	window.lang_Details_Type = languageUtil.getResource("Type"); //"模式";
	window.lang_Details_Type1 = languageUtil.getResource("Type1"); //"送风";
	window.lang_Details_Type2 = languageUtil.getResource("Type3"); //"制热";
	window.lang_Details_Type3 = languageUtil.getResource("Type2"); //"制冷";
	window.lang_Details_Type4 = languageUtil.getResource("Type4"); //"自动";
	window.lang_Details_Type5 = languageUtil.getResource("Type5"); //"除湿";
	window.lang_Details_Speed = languageUtil.getResource("Speed"); //"风速";
	window.lang_Details_Speed1 = languageUtil.getResource("Speed1"); //"高";
	window.lang_Details_Speed2 = languageUtil.getResource("Speed2"); //"中";
	window.lang_Details_Speed3 = languageUtil.getResource("Speed3"); //"低";
	window.lang_Details_Speed4 = languageUtil.getResource("Speed4"); //超低;
	window.lang_Details_Speed5 = languageUtil.getResource("Speed5"); //超高;
	window.lang_Details_Offline = languageUtil.getResource("offLine"); //"设备已离线";
	window.lang_moreSettingLog = languageUtil.getResource("moreSettingLog"); //"日志";
	window.lang_moreSettingAlarm = languageUtil.getResource("moreSettingAlarm"); //"报警消息";
	window.lang_Details_CloseAll = languageUtil.getResource("CloseAll"); //"全部关闭";
	window.lang_Details_OpenAll = languageUtil.getResource("OpenAll"); //"全部开启";
	window.lang_Details_Closed = languageUtil.getResource("Closed"); //"已关闭";
	window.lang_Details_CloseAlltips = languageUtil.getResource("Device_OZ_Details_CloseAlltips"); //"你确定全部关闭吗";
	window.lang_Details_OpenAlltips = languageUtil.getResource("Device_OZ_Details_OpenAlltips"); //"你确定全部开启吗";
	window.lang_timing = languageUtil.getResource("Timing"); //"计时";
	window.lang_Confirm = languageUtil.getResource("Confirm"); //"确定";
	window.lang_Cancel = languageUtil.getResource("Cancel"); //"取消";
	window.count_down_off = languageUtil.getResource("Time_close"); //定时关机
	window.count_down_on = languageUtil.getResource("Time_open"); //定时开机
	window.count_time_off = languageUtil.getResource("Timing_close"); //%@时 %@分 后关机
	window.count_time_on = languageUtil.getResource("Timing_open"); //%@时 %@分 后开机
	window.Device_a0_air = languageUtil.getResource("Device_a0_aircondition"); //空调
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
		var all = document.all;
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