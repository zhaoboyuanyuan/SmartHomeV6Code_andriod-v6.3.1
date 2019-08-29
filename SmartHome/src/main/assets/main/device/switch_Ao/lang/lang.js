function initlan(callback) {
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
                offArr[1].style.top = "8.8rem";
                offArr[0].style.height = "calc(100% - 8.8rem)";
                offArr[1].style.height = "calc(100% - 8.8rem)";
            }
            var pupWindow = document.getElementsByClassName("pupWindow");
            if (pupWindow != '' && pupWindow.length != 0) {
                pupWindow[0].style.top = "9.2rem";
            }
            var body = document.getElementsByClassName("header_top")[0];
            body.style.paddingTop = "4.4rem"
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
	var languageCode = window.sysfun.getLang();
//	var languageCode="zh-cn";
	languageUtil.onJsLoaded = function() {
		initLanguage();
		callback();
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
	window.switch_detail_title = languageUtil.getResource("device_Ao_name"); //""
	window.switch_mode_01 = languageUtil.getResource("switch_mode_01"); //""
	window.switch_mode_02 = languageUtil.getResource("switch_mode_02"); //""
	window.switch_mode_03 = languageUtil.getResource("switch_mode_03"); //""
	window.switch_Ao_01 = languageUtil.getResource("switch_Ao_01"); //""
	window.switch_Ao_02 = languageUtil.getResource("switch_Ao_02"); //""
	window.switch_Ao_03 = languageUtil.getResource("switch_Ao_03"); //""
	window.switch_Ao_04 = languageUtil.getResource("switch_Ao_04"); //""
	window.switch_Ao_05 = languageUtil.getResource("switch_Ao_05"); //""
	window.switch_Ao_06 = languageUtil.getResource("switch_Ao_06"); //""
	window.switch_Ao_07 = languageUtil.getResource("switch_Ao_07"); //""
	window.switch_Ao_08 = languageUtil.getResource("switch_Ao_08"); //""
	window.switch_Ao_09 = languageUtil.getResource("switch_Ao_09"); //""
	window.switch_Ao_10 = languageUtil.getResource("switch_Ao_10"); //""
	window.switch_Ao_11 = languageUtil.getResource("switch_Ao_11"); //""
	window.switch_Ao_12 = languageUtil.getResource("switch_Ao_12"); //""
	window.switch_Ao_13 = languageUtil.getResource("switch_Ao_13"); //""
	window.switch_Ao_14 = languageUtil.getResource("switch_Ao_14"); //""
	window.switch_Ao_15 = languageUtil.getResource("switch_Ao_15"); //""
	window.switch_Ao_On = languageUtil.getResource("switch_Ao_On"); //""
	window.switch_Ao_Off = languageUtil.getResource("switch_Ao_Off"); //""
	window.switch_Ao_Stop = languageUtil.getResource("switch_Ao_Stop"); //""
	window.switch_Ao_Binding = languageUtil.getResource("switch_Ao_Binding"); //""
	window.switch_Ao_BindingSuc = languageUtil.getResource("switch_Ao_BindingSuc"); //""
	window.switch_Ao_BindingFail = languageUtil.getResource("switch_Ao_BindingFail"); //""
	window.switch_Ao_NoSwitchMode = languageUtil.getResource("switch_Ao_NoSwitchMode"); //""
	window.switch_Ao_ChangeMode01 = languageUtil.getResource("switch_Ao_ChangeMode01"); //""
	window.switch_Ao_ChangeMode02 = languageUtil.getResource("switch_Ao_ChangeMode02"); //""
	window.switch_Ao_ChangeMode03 = languageUtil.getResource("switch_Ao_ChangeMode03"); //""
	window.switch_Ao_ChangeMode04 = languageUtil.getResource("switch_Ao_ChangeMode04"); //""
	window.switch_Am_Name = languageUtil.getResource("device_Am_name"); //""
	window.switch_An_Name = languageUtil.getResource("device_An_name"); //""
	window.switch_Ao_Name = languageUtil.getResource("device_Ao_name"); //""
	window.switch_Ar_Name = languageUtil.getResource("device_Ar_Name");
	window.switch_Ao_ChooseScene= languageUtil.getResource("switch_Ao_ChooseScene");
	window.switch_Ao_timeout= languageUtil.getResource("switch_Ao_timeout");
	window.switch_Ao_saveSuc=languageUtil.getResource("switch_Ao_saveSuc");
	window.switch_Ao_UnBinding=languageUtil.getResource("switch_Ao_UnBinding");
	window.lang_SwitchAnTitle	= languageUtil.getResource("SwitchAnTitle");
	window.lang_Log	= languageUtil.getResource("Log");
	window.lang_un_room	= languageUtil.getResource("un_room");
	window.lang_offLine	= languageUtil.getResource("offLine");
	window.lang_onLine	= languageUtil.getResource("onLine");
	window.lang_switch	= languageUtil.getResource("switch");
	window.lang_BindDeviceTitle	= languageUtil.getResource("BindDeviceTitle");
	window.swtich_Ao_Complete=languageUtil.getResource("swtich_Ao_Complete");
	window.switch_Ao_Saving=languageUtil.getResource("switch_Ao_Saving");
	window.switch_Ao_ChooseBtn=languageUtil.getResource("switch_Ao_ChooseBtn");
	window.switch_Ao_UpdateSuc=languageUtil.getResource("switch_Ao_UpdateSuc");
	window.swich_Ao_UnbindSuc=languageUtil.getResource("swich_Ao_UnbindSuc");
	window.swich_Ao_Unbundling=languageUtil.getResource("swich_Ao_Unbundling");
	window.Share_No_Permission=languageUtil.getResource("Share_No_Permission");
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