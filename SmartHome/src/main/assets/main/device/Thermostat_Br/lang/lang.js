function initlan() {
	try {
		var iphoneX = {sysFunc: "getItem:", room: "iphoneX", id: "iphoneX", data: ""};
		var iStr ='';
		if(!window.v6sysfunc) {
             iStr = prompt(JSON.stringify(iphoneX))
        }
		if(iStr == "iphoneX") {
            var body = document.getElementsByClassName("header")[0];
            body.style.paddingTop = "2.4rem"
            var top3 = document.getElementsByTagName("body")[0].children[1].style.paddingTop;
            if(top3 == "6.4rem") {
                document.getElementsByTagName("body")[0].children[1].style.paddingTop = "8.8rem";
            } else if(top3 == "9.8rem") {
                document.getElementsByTagName("body")[0].children[1].style.paddingTop = "12.2rem";
            }

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
		}
	} catch(e) {
		//TODO handle the exception
	}
	var languageCode = window.sysfun.getLang();//window.sysfun.getLang();
	languageUtil.onJsLoaded = function() {
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
    window.lang_name_OZ = languageUtil.getResource("device_Ol_name");//"空调翻译器";
    window.lang_Details_Open = languageUtil.getResource("On");//"已开机，点击关机";
    window.lang_Details_Close = languageUtil.getResource("Off");//"已关机，点击开机";
    window.lang_Details_Temperature = languageUtil.getResource("Temperature");//室温";
    window.lang_Details_Type = languageUtil.getResource("Pattern_02");//"模式";
    window.lang_Details_Type1 = languageUtil.getResource("Mode_Switch_02");//"制冷";
    window.lang_Details_Type2 = languageUtil.getResource("Mode_Switch_01");//"制热";
    window.lang_Details_Type3 = languageUtil.getResource("Mode_Switch_03");//"通风";
    window.lang_Details_Type4 = languageUtil.getResource("device_Ol_mode_warm");//"地暖";
    window.lang_Details_Type5 = languageUtil.getResource("device_Ol_mode_warm_heat");//"地暖+制热";
    window.lang_Details_Speed = languageUtil.getResource("Windspeed_02");//"风速";
    window.lang_Details_Speed1 = languageUtil.getResource("Mode_Switch_06");//"高";
    window.lang_Details_Speed2 = languageUtil.getResource("Mode_Switch_05");//"中";
    window.lang_Details_Speed3 = languageUtil.getResource("Mode_Switch_04");//"低";
    window.lang_Details_Speed4 = languageUtil.getResource("Mode_Switch_07");//"自动";
    window.lang_Details_Offline = languageUtil.getResource("Mode_Switch_08");//"设备已离线";
    window.lang_Details_CloseAll = languageUtil.getResource("device_Ol_all_closed");//"全部关闭";
    window.lang_Details_OpenAll = languageUtil.getResource("device_Ol_all_open");//"全部开启";
    window.lang_Details_Closed = languageUtil.getResource("device_Ol_Close");//"已关闭";

    window.lang_device_Ol_Countdown_closed = languageUtil.getResource("Count_down_04");//"后关闭";
    window.lang_device_Ol_Countdown_open = languageUtil.getResource("Count_down_03");//"后开启";

    window.lang_device_Ol_Countdown_Off = languageUtil.getResource("Count_down_01");//"倒计时关闭";
    window.lang_device_Ol_Countdown_Open = languageUtil.getResource("Count_down_02");//"倒计时开启";

    window.lang_device_Ol_hour = languageUtil.getResource("Hour");//"小时";
    window.lang_device_Ol_Minute = languageUtil.getResource("Minute");//"分钟";

    window.lang_Details_CloseAlltips = languageUtil.getResource("Device_Ol_Details_CloseAlltips");//"你确定全部关闭吗";
    window.lang_Details_OpenAlltips = languageUtil.getResource("Device_Ol_Details_OpenAlltips");//"你确定全部开启吗";

    window.lang_device_Ol_Cancel = languageUtil.getResource("Cancel");//"取消";
    window.lang_device_Ol_ok = languageUtil.getResource("Complete");//"确定";
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