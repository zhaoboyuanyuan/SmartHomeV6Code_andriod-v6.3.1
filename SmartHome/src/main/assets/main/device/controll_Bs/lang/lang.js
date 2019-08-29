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

            var content = document.getElementById("content");
            content.style.height = "calc(100% - 12.8rem)";
            content.style.paddingTop = "8.4rem";

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
    window.Device_Bs_on = languageUtil.getResource("Device_Bs_on");//;
    window.Device_Bs_off = languageUtil.getResource("Device_Bs_off");//;
    window.Device_Bs_Mode_02 = languageUtil.getResource("Device_Bs_Mode_02");//;
    window.Device_Bs_temperature_01= languageUtil.getResource("Device_Bs_temperature_01");//;
    window.Device_Bs_Mode_03 = languageUtil.getResource("Device_Bs_Mode_03");//;
    window.Device_Bs_Mode_04 = languageUtil.getResource("Device_Bs_Mode_04");//;
    window.Device_Bs_temperature_02 = languageUtil.getResource("Device_Bs_temperature_02");//;
    window.Device_Bs_Alarm_05 = languageUtil.getResource("Device_Bs_Alarm_05");//;
    window.Device_Bs_Alarm_04 = languageUtil.getResource("Device_Bs_Alarm_04");//;
    window.Device_Bs_Alarm_03 = languageUtil.getResource("Device_Bs_Alarm_03");//;
    window.Device_Bs_Alarm_02 = languageUtil.getResource("Device_Bs_Alarm_02");//;
    window.Device_Bs_Alarm_01 = languageUtil.getResource("Device_Bs_Alarm_01");//;
	window.Device_Bs_temperature_005 = languageUtil.getResource("Device_Bs_temperature_005");//;

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