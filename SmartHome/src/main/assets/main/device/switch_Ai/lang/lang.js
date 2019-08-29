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
/*
 * 处理js中的国际化
 */
function globalLang() {
	window.lang_sms = languageUtil.getResource("Log")// "日志"
    window.lang_custom_01 = languageUtil.getResource("Settings_Totalpower_cleared")// "累计电量清零"
    window.lang_custom_02 = languageUtil.getResource("Settings_Overload_protection")// "过载保护设置"
    window.alarmSet_txt_02 = languageUtil.getResource("alarmSet_txt_02")//用户不存在
    window.alarmSet_txt_03 = languageUtil.getResource("alarmSet_txt_04")//token错误
    window.alarmSet_txt_05 = languageUtil.getResource("alarmSet_txt_05")//用户名已被使用
    window.alarmSet_txt_06 = languageUtil.getResource("alarmSet_txt_06")//手机号已被注册
    window.alarmSet_txt_07 = languageUtil.getResource("alarmSet_txt_03")//用户密码错误
    window.alarmSet_txt_04 = languageUtil.getResource("alarmSet_txt_07")//手机号未认证
    window.alarmSet_txt_08 = languageUtil.getResource("alarmSet_txt_08")//邮箱已被注册
    window.alarmSet_txt_09 = languageUtil.getResource("alarmSet_txt_09")//邮箱未认证
    window.alarmSet_txt_10 = languageUtil.getResource("alarmSet_txt_10")//手机验证码错误
    window.alarmSet_txt_11 = languageUtil.getResource("alarmSet_txt_11")//设备不存在
    window.alarmSet_txt_12 = languageUtil.getResource("alarmSet_txt_12")//该设备不是网关
    window.alarmSet_txt_13 = languageUtil.getResource("alarmSet_txt_13")//设备密码错误
    window.alarmSet_txt_14 = languageUtil.getResource("alarmSet_txt_14")//用户不是设备的拥有者
    window.alarmSet_txt_15 = languageUtil.getResource("alarmSet_txt_15")//设备已与用户绑定
    window.alarmSet_txt_16 = languageUtil.getResource("alarmSet_txt_16")//设备与用户未绑定
    window.alarmSet_txt_17 = languageUtil.getResource("alarmSet_txt_17")//设备密码错误
    window.alarmSet_txt_18 = languageUtil.getResource("alarmSet_txt_18")//查询失败，请稍后重试
    window.alarmSet_txt_19 = languageUtil.getResource("alarmSet_txt_19")//设备密码错误

    window.device_Ai_name = languageUtil.getResource("device_Ai_name")//"一位DIN开关计量版"
    window.device_delay_on = languageUtil.getResource("device_Delay_open")//"延时打开"
    window.device_delay_off = languageUtil.getResource("device_Delay_off")//"延时关闭"
    window.device_time_on = languageUtil.getResource("Time_open")//"打开时间"
    window.device_time_off = languageUtil.getResource("Time_close")//"关闭时间"
    window.device_sure = languageUtil.getResource("Selection_time_confirm")//"确定"
    window.device_cancel = languageUtil.getResource("Selection_time_cancel")//"取消"
    window.device_titleTxt = languageUtil.getResource("Settings_Overload_Popup")//"检测到用电器功率超出额定范围，已为您自动断电"
    window.device_no_close = languageUtil.getResource("Delay_off_notstarted")//"未启动延时关闭功能"
    window.device_no_open = languageUtil.getResource("Delay_open_notstarted")//"未启动延时打开功能"
    window.device_month = languageUtil.getResource("Power_data_month")//"月"
    window.device_Ai_Electric = languageUtil.getResource("device_Ai_Electric")//"电量"
}


