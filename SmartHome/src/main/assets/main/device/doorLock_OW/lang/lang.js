function initlan() {
	var iphoneX = {sysFunc: "getItem:", room: "iphoneX", id: "iphoneX", data: ""};
		var iStr ='';
		if(!window.v6sysfunc) {
             iStr = prompt(JSON.stringify(iphoneX))
        }
		if(iStr == "iphoneX") {
			var offArr = document.getElementsByClassName("mask_layer")
			var lockImg = document.getElementsByClassName("lockbackimageview")
			if(offArr != '' && offArr.length != 0){
				offArr[0].style.top = "8.8rem";
                offArr[0].style.height = "calc(100% - 8.8rem)";
                lockImg[0].style.height = "calc(100% - 8.8rem)";
			}
			var body = document.getElementsByClassName("header")[0];
			body.style.paddingTop = "2.4rem"
			var top3 = document.getElementsByTagName("body")[0].children[1].style.paddingTop;
			if(top3 == "6.4rem") {
				document.getElementsByTagName("body")[0].children[1].style.paddingTop = "8.8rem";
			} else if(top3 == "9.8rem") {
				document.getElementsByTagName("body")[0].children[1].style.paddingTop = "12.2rem";
			}
			var Mtop3 = document.getElementsByTagName("body")[0].children[1].style.marginTop;
			if(Mtop3 == "6.4rem") {
				document.getElementsByTagName("body")[0].children[1].style.marginTop = "8.8rem";
			} else if(Mtop3 == "9.8rem") {
				document.getElementsByTagName("body")[0].children[1].style.marginTop = "12.2rem";
			}
			if (document.getElementsByClassName("iphoneX88").length > 0){
                document.getElementsByClassName("iphoneX88")[0].style.top = "8.8rem"
			}
		}

	var languageCode = window.sysfun.getLang();
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
	window.lang_DoorLockTittle	= languageUtil.getResource("DoorLockTittle");
	window.lang_DoorLookInputPWD	= languageUtil.getResource("DoorLookInputPWD");
	window.lang_DoorLookOpenTouchIDTip	= languageUtil.getResource("DoorLookOpenTouchIDTip");
	
	window.lang_DoorLookTouchIDTip	= languageUtil.getResource("DoorLookTouchIDTip");
	window.lang_DoorLookNeverNotify	= languageUtil.getResource("DoorLookNeverNotify");
	window.lang_DoorLookToSetting	= languageUtil.getResource("DoorLookToSetting");
	
	window.lang_DoorLookTemporarilynotset	= languageUtil.getResource("DoorLookTemporarilynotset");
	window.lang_DoorLookFingerprinterror	= languageUtil.getResource("DoorLookFingerprinterror");
	window.lang_DoorLookPWDERRORTip	= languageUtil.getResource("DoorLookPWDERRORTip");

	window.lang_DoorLookPWDERRORTip1= languageUtil.getResource("DoorLookPWDERRORTip1");
	window.lang_DoorLookOK	= languageUtil.getResource("DoorLookOK");
	window.lang_DoorLookFingerprintsettingsuss	= languageUtil.getResource("DoorLookFingerprintsettingsuss");
	
	window.lang_DoorLookDeviceOffLine= languageUtil.getResource("DoorLookDeviceOffLine");
	window.lang_DoorLookPasswordmanager= languageUtil.getResource("DoorLookPasswordmanager");
	window.lang_DoorLookOrdinaryPassword	= languageUtil.getResource("DoorLookOrdinaryPassword");
	
	window.lang_DoorLookOrdinaryPasswordtip	= languageUtil.getResource("DoorLookOrdinaryPasswordtip");
	window.lang_DoorLookOne_timePassword	= languageUtil.getResource("DoorLookOne_timePassword");
	window.lang_DoorLookOne_timePasswordtip	= languageUtil.getResource("DoorLookOne_timePasswordtip");
	
	window.lang_DoorLookTemporaryPassword= languageUtil.getResource("DoorLookTemporaryPassword");
	window.lang_DoorLookTemporaryPasswordtip	= languageUtil.getResource("DoorLookTemporaryPasswordtip");
	window.lang_DoorLookMoreEffectivetime	= languageUtil.getResource("DoorLookMoreEffectivetime");
	
	window.lang_DoorLookMoreDeadtime	= languageUtil.getResource("DoorLookMoreDeadtime");
	window.lang_DoorLookMoreSetup	= languageUtil.getResource("DoorLookMoreSetup");
	window.lang_DoorLookMoreDeletePassword	= languageUtil.getResource("DoorLookMoreDeletePassword");
	
	window.lang_DoorLookMoreDeleteOne_timePassword	= languageUtil.getResource("DoorLookMoreDeleteOne_timePassword");
	window.lang_DoorLookMoreDeleteOne_timePasswordAffirm	= languageUtil.getResource("DoorLookMoreDeleteOne_timePasswordAffirm");
	window.lang_DoorLookMorecancel	= languageUtil.getResource("DoorLookMorecancel");
	
	window.lang_DoorLookMoreDelete	= languageUtil.getResource("DoorLookMoreDelete");
	window.lang_DoorLookOne_timePasswordSuss	= languageUtil.getResource("DoorLookOne_timePasswordSuss");
	window.lang_DoorLookOne_timePassword1	= languageUtil.getResource("DoorLookOne_timePassword1");
	
	window.lang_DoorLookMoreSend	= languageUtil.getResource("DoorLookMoreSend");
	window.lang_DoorLookMorefinish	= languageUtil.getResource("DoorLookMorefinish");
	window.lang_DoorLookTemporaryPasswordSuss	= languageUtil.getResource("DoorLookTemporaryPasswordSuss");
	
	window.lang_DoorLookTemporaryPassword1= languageUtil.getResource("DoorLookTemporaryPassword1");
	window.lang_DoorLookOrdinaryPasswordSuss	= languageUtil.getResource("DoorLookOrdinaryPasswordSuss");
	window.lang_DoorLookOrdinaryPassword1= languageUtil.getResource("DoorLookOrdinaryPassword1");
	
	window.lang_DoorLookMoreValidatePassword1	= languageUtil.getResource("DoorLookMoreValidatePassword1");
	window.lang_DoorLookMoreValidatePassword2	= languageUtil.getResource("DoorLookMoreValidatePassword2");
	window.lang_DoorLookMoreValidatePassword3= languageUtil.getResource("DoorLookMoreValidatePassword3");
	
	window.lang_DoorLookMoreValidatePassword4	= languageUtil.getResource("DoorLookMoreValidatePassword4");
	window.lang_DoorLookOpen = languageUtil.getResource("DoorLookOpen");
	window.lang_DoorLookPWDERROR = languageUtil.getResource("DoorLookPWDERROR");
	window.lang_DoorLookDeleteSuccessed = languageUtil.getResource("DoorLookDeleteSuccessed");

	window.lang_Log = languageUtil.getResource("Log");
	window.lang_Alarm = languageUtil.getResource("Alarm");
	window.lang_DoorLookMorecancel = languageUtil.getResource("DoorLookMorecancel")//"取消"
    window.lang_DoorLookOK = languageUtil.getResource("DoorLookOK")//"确定"
    window.lang_alarmSet_js_32 = languageUtil.getResource("alarmSet_js_32")//"开锁中"
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
		window.all = document.all;
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