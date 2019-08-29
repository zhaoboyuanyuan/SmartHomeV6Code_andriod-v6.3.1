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
			var body = document.getElementsByClassName("header")[0];
			body.style.paddingTop = "2.4rem"
		}
	} catch(e) {
		//TODO handle the exception
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
	window.alarmSet_txt_01 = languageUtil.getResource("alarmSet_txt_01") //"修改成功"
	window.alarmSet_txt_03 = languageUtil.getResource("alarmSet_txt_03") //"用户密码错误"
	window.alarmSet_txt_04 = languageUtil.getResource("alarmSet_txt_04") //"token错误"
	window.alarmSet_txt_17 = languageUtil.getResource("alarmSet_txt_17") //"设备密码错误"
	window.alarmSet_txt_18 = languageUtil.getResource("alarmSet_txt_18") //"查询失败，请稍后重试"

	window.alarmSet_js_01 = languageUtil.getResource("Device_69_name") //玻璃门锁
	window.alarmSet_js_03 = languageUtil.getResource("device_69_txt_06") //请输入密码
	window.alarmSet_js_04 = languageUtil.getResource("alarmSet_js_04") //上保险
	window.alarmSet_js_05 = languageUtil.getResource("alarmSet_js_05") //解除保险
	window.alarmSet_js_06 = languageUtil.getResource("alarmSet_js_06") //解除反锁
	window.alarmSet_js_07 = languageUtil.getResource("alarmSet_js_07") //门已反锁
	window.alarmSet_js_08 = languageUtil.getResource("alarmSet_js_08") //门锁已开
	window.alarmSet_js_09 = languageUtil.getResource("alarmSet_js_09") //密码错误，请重试
	window.alarmSet_js_10 = languageUtil.getResource("alarmSet_js_10") //强制上锁
	window.alarmSet_js_11 = languageUtil.getResource("alarmSet_js_11") //自动上锁

    window.device_69_txt_08 = languageUtil.getResource("device_69_txt_08") //已上锁

	window.alarmSet_js_19 = languageUtil.getResource("offLine") //设备已离线
	window.alarmSet_js_20 = languageUtil.getResource("alarmSet_js_20") //密码错误

	window.alarmSet_js_32 = languageUtil.getResource("alarmSet_js_32") //开锁中...

    window.alarmSet_js_16 = languageUtil.getResource("alarmSet_js_16") //请求超时!

	window.moreSetting06 = languageUtil.getResource("Alarm"); //报警消息
	window.moreSetting07 = languageUtil.getResource("Log"); //日志
	window.moreSetting08 = languageUtil.getResource("moreSetting08"); //报警设置
	window.moreSetting09 = languageUtil.getResource("moreSetting09"); //设备信息
	window.cancel = languageUtil.getResource("cancel");
	window.sure = languageUtil.getResource("sure");
    window.lang_change_password = languageUtil.getResource("device_69_change_password");
    window.device_69_old_password_error = languageUtil.getResource("device_69_old_password_error");//原密码错误，请重新输入
    window.device_69_txt_03 = languageUtil.getResource("device_69_txt_03");//6位数字且无特殊字符
    window.device_69_txt_01 = languageUtil.getResource("device_69_txt_01");//连接超时
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