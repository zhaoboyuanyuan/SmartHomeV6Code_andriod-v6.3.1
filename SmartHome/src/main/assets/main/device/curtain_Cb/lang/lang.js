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

    window.device_Cb_name = languageUtil.getResource("device_Cb_name")//"智敬系列二路窗帘"
    window.device_Cb_Motor_reversal = languageUtil.getResource("device_Cb_Motor_reversal")//"电机反转"
    window.Device_Cb_Details_Prompt = languageUtil.getResource("Device_Cb_Details_Prompt")//"提示"
    window.device_Cb_Motor_reversal_hint = languageUtil.getResource("device_Cb_Motor_reversal_hint")//"在窗帘模式下，快击窗帘“开”键4次，电机会调转方向"
    window.Add_batches_i_know = languageUtil.getResource("Add_batches_i_know")//"我知道了"
    window.device_Cb_mode = languageUtil.getResource("device_Cb_mode")//"模式切换"
    window.device_Cb_curtain = languageUtil.getResource("device_Cb_curtain")//"窗帘模式"
    window.device_Cb_switch = languageUtil.getResource("device_Cb_switch")//"触发开关模式"
    window.device_Cb_ok = languageUtil.getResource("device_Cb_ok")//"确定"
    window.device_Cb_Switch_success = languageUtil.getResource("device_Cb_Switch_success")//"切换成功"
    window.device_Cb_Switch_failure = languageUtil.getResource("device_Cb_Switch_failure")//"切换失败"
    window.device_Cb_time_out = languageUtil.getResource("device_Cb_time_out");//"连接超时"
    window.device_Cb_switch_hint = languageUtil.getResource("device_Cb_switch_hint");//"设备当前为触发开关模式，您可将其添加至管家中做触发条件，从而执行相应任务；也可以点击右上角图标进入模式切换，在窗帘模式下控制窗帘。";
	window.Smart_no_set = languageUtil.getResource("Smart_no_set");//"被分享网关无此权限"
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