function getLang() {
	
    var fun = {};
    fun.sysFunc = "getLang:";
    var result;
    if(window.v6sysfunc) {
        result = window.v6sysfunc.sysfun(JSON.stringify(fun));
    } else {
        result = prompt(JSON.stringify(fun), "");
    }
    return result;
}
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
            var iphoneXArr = document.getElementsByClassName("iphoneX");
            if(iphoneXArr != '' && iphoneXArr.length != 0){
                for(var m=0;m<iphoneXArr.length;m++){
                    iphoneXArr[m].style.top = "8.8rem";
					iphoneXArr[m].style.height = "calc(100% - 8.8rem)";
                }
            }
			var iphoneXStrArr = document.getElementsByClassName("iphoneXStr");
			if(iphoneXStrArr != '' && iphoneXStrArr.length != 0){
				for(var m=0;m<iphoneXStrArr.length;m++){
					iphoneXStrArr[m].style.paddingTop = "8.8rem";
					iphoneXStrArr[m].style.height = "calc(100% - 8.8rem)";
				}
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
    window.device_Bw_name = languageUtil.getResource("device_Bw_name")
    window.device_Bw_on = languageUtil.getResource("device_Bw_on")
    window.device_Bw_off = languageUtil.getResource("device_Bw_off")
    window.device_Bw_control = languageUtil.getResource("device_Bw_control")
    window.device_Bw_Rename = languageUtil.getResource("device_Bw_Rename")
    window.device_Bw_enter_name = languageUtil.getResource("device_Bw_enter_name")
    window.device_Bw_Cancel = languageUtil.getResource("device_Bw_Cancel")
    window.device_Bw_Confirm = languageUtil.getResource("device_Bw_Confirm")
    window.lang_unBind = languageUtil.getResource("device_Bw_Untied")
    window.lang_time_out = languageUtil.getResource("device_Bw_time_out")
    window.lang_Untied_success = languageUtil.getResource("device_Bw_Untied_success")
    window.lang_Binding_success = languageUtil.getResource("device_Bw_Binding_success");
    window.lang_device_switch = languageUtil.getResource("device_switch");
    window.lang_offLine = languageUtil.getResource("offLine");
    window.lang_onLine = languageUtil.getResource("onLine");
    window.lang_group_text = languageUtil.getResource("group_text");
    window.lang_has_been_bound = languageUtil.getResource("switch_An_NoSwitchMode");
    window.lang_Log = languageUtil.getResource("Log");
    window.lang_group = languageUtil.getResource("group_text");

    window.lang_device_switch = languageUtil.getResource("device_switch");

    window.lang_device_rename = languageUtil.getResource("device_Bw_Rename");
    window.lang_device_input_name = languageUtil.getResource("device_Bw_enter_name");
    window.lang_device_cancal = languageUtil.getResource("device_Bw_Cancel");
    window.lang_device_sure = languageUtil.getResource("device_Bw_Confirm");

    window.bindNoneScene = languageUtil.getResource("bindNoneScene") // 未绑定场景
    window.bindNoneDevice = languageUtil.getResource("bindNoneDevice") // 未绑定设备
    window.modeSwitch = languageUtil.getResource("modeSwitch") // 开关模式
    window.modeScene = languageUtil.getResource("modeScene") // 场景模式
    window.modeBind = languageUtil.getResource("modeBind") // 绑定模式
    window.lock_Scenes_bind_dont = languageUtil.getResource("lock_Scenes_bind_dont") // 不绑定
    window.not_switch = languageUtil.getResource("not_switch") // 非开关模式
	window.switch_Cn_NoSwitchMode = languageUtil.getResource("switch_Cn_NoSwitchMode") // 非开关模式，请至设备端控制
    window.device_Bw_Binding = languageUtil.getResource("device_Bw_Binding") // 正在绑定，请稍后...
    window.device_Bw_Binding_failure = languageUtil.getResource("device_Bw_Binding_failure") // 绑定失败，请重试
    window.switch_Cn_Saving = languageUtil.getResource("switch_Cn_Saving") //正在保存
    window.saveSuccessful = languageUtil.getResource("saveSuccessful") //保存成功
    window.saveFailed = languageUtil.getResource("saveFailed") //保存失败
    window.switch_Cn_ChangeMode02 = languageUtil.getResource("switch_Cn_ChangeMode02") //切换后原有已绑定设备将解除绑定
    window.switch_Cn_ChangeMode03 = languageUtil.getResource("switch_Cn_ChangeMode03") //切换后原有已绑定场景将解除绑定
    window.switch_Cn_03 = languageUtil.getResource("switch_Cn_03") //該綁定設備為窗帘控制器，解綁其中一個按鍵則三鍵全部解綁，確認全部解除綁定嗎？
	window.Share_No_Permission = languageUtil.getResource("Share_No_Permission")

    //引用deviceList.js，场景中的设备名称以后不用在这里添加全局变量了
    var new_element = document.createElement("script");
    new_element.setAttribute("type", "text/javascript");
    new_element.setAttribute("src", "../js/deviceList.js");
    document.body.appendChild(new_element);
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