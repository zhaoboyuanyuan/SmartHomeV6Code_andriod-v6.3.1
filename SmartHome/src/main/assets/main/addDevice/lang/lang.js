function initlan() {
	try {
		var iphoneX = {sysFunc: "getItem:", room: "iphoneX", id: "iphoneX", data: ""};
		var iStr ='';
		if(!window.v6sysfunc) {
             iStr = prompt(JSON.stringify(iphoneX))
        }
		if(iStr == "iphoneX") {
            var iphoneXArr = document.getElementsByClassName("iphoneX")
            if(iphoneXArr != '' && iphoneXArr.length != 0){
				for(var m=0;m<iphoneXArr.length;m++){
                    iphoneXArr[m].style.top = "8.8rem";
				}
            }
            var body = document.getElementsByClassName("header")[0];
            body.style.paddingTop = "2.4rem";
            var top3 = document.getElementsByTagName("body")[0].childNodes[3].style.paddingTop;
            if(top3 == "6.4rem") {
                document.getElementsByTagName("body")[0].childNodes[3].style.paddingTop = "8.8rem";
            } else if(top3 == "9.8rem") {
                document.getElementsByTagName("body")[0].childNodes[3].style.paddingTop = "12.2rem";
            }
            if(document.getElementsByClassName("iphoneX9_4")){
                if(document.getElementsByClassName("iphoneX9_4").length != 0) {
                    document.getElementsByClassName("iphoneX9_4")[0].style.top = "9.4rem"
                }
			}
			if(document.getElementsByClassName("searchHeader")){
                if(document.getElementsByClassName("searchHeader").length != 0) {
                    document.getElementsByClassName("searchHeader")[0].style.paddingTop = "4.8rem";
                    if(document.getElementsByClassName("get_list")[0].style.marginTop == "6.8rem") {
                        document.getElementsByClassName("get_list")[0].style.marginTop = "9.2rem";
                    }
                }
			}
		}
	} catch(e) {
		//TODO handle the exception
	}
	var languageCode = window.sysfun.getLang();
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
	window.lang_txtContent = languageUtil.getResource("addDevice_group_text")
	window.addDevice_add = languageUtil.getResource("addDevice_add")
	window.addDevice_op_txt_08 = languageUtil.getResource("addDevice_op_txt_08")
	window.lang_name_Bd = languageUtil.getResource("addDevice_Bd_title")
	window.lang_lock = languageUtil.getResource("addDevice_lock")
	window.lang_name_70_01 = languageUtil.getResource("addDoorLock_title")
	window.lang_name_70_02 = languageUtil.getResource("addDoorLockF_title")
	window.lang_name_bc_01 = languageUtil.getResource("addDevice_Bc")
	window.addScene_txt_01 = languageUtil.getResource("addScene_txt_01")
	window.addGateway3_toast_00 = languageUtil.getResource("addGateway3_toast_00") //"账号或密码不能为空";
	window.addGateway3_toast_01 = languageUtil.getResource("addGateway3_toast_01") //"用户不存在";
	window.addGateway3_toast_02 = languageUtil.getResource("addGateway3_toast_02") //"用户密码错误";
	window.addGateway3_toast_03 = languageUtil.getResource("addGateway3_toast_03") //"token错误";
	window.addGateway3_toast_04 = languageUtil.getResource("addGateway3_toast_04") //"用户名已被使用";
	window.addGateway3_toast_05 = languageUtil.getResource("addGateway3_toast_05") //"手机号已被注册";
	window.addGateway3_toast_06 = languageUtil.getResource("addGateway3_toast_06") //"手机号未认证";
	window.addGateway3_toast_07 = languageUtil.getResource("addGateway3_toast_07") //"邮箱已被注册";
	window.addGateway3_toast_08 = languageUtil.getResource("addGateway3_toast_08") //"邮箱未认证";
	window.addGateway3_toast_09 = languageUtil.getResource("addGateway3_toast_09") //"手机验证码错误";
	window.addGateway3_toast_10 = languageUtil.getResource("addGateway3_toast_10") //"设备不存在";
	window.addGateway3_toast_11 = languageUtil.getResource("addGateway3_toast_11") //"该设备不是网关";
	window.addGateway3_toast_12 = languageUtil.getResource("addGateway3_toast_12") //"设备密码错误";
	window.addGateway3_toast_13 = languageUtil.getResource("addGateway3_toast_13") //"用户不是设备的拥有者";
	window.addGateway3_toast_14 = languageUtil.getResource("addGateway3_toast_14") //"设备已与用户绑定";
	window.addGateway3_toast_15 = languageUtil.getResource("addGateway3_toast_15") //"设备与用户未绑定";
	window.addGateway3_toast_16 = languageUtil.getResource("addGateway3_toast_16") //"设备密码错误";
	window.addSwitch_Step2 = languageUtil.getResource("addSwitch_Step2");
	window.addGateway_toast = languageUtil.getResource("addGateway_toast");
	window.addGateway_empty = languageUtil.getResource("addGateway_empty");
	window.addScene_scene_toast = languageUtil.getResource("addScene_scene_toast");
	window.addScene_penguin = languageUtil.getResource("addScene_penguin");
	window.lang_deviceSelf09_name = languageUtil.getResource("addScene_Freestanding_gasdetector_69");
	window.lang_deviceSelf43_name = languageUtil.getResource("addScene_Freestanding_Firedetectionalarm_43");
	window.addDevice_name_repetition = languageUtil.getResource("addDevice_name_repetition");
	window.addDevice_name_empty = languageUtil.getResource("addDevice_name_empty");
	window.lang_device22_2_name = languageUtil.getResource("addDevice_22_name_2");
	window.lang_device22_3_name = languageUtil.getResource("addDevice_22_name_3");
	window.addDevice_G50_name = languageUtil.getResource("addDevice_G50_name");
	window.addDevice_G50_title = languageUtil.getResource("addDevice_G50_title");
    window.addDevice_toast_03 = languageUtil.getResource("addDevice_toast_03");

    window.addDevice_noGw = languageUtil.getResource("addDevice_noGw");
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