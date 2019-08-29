function initlan() {
	try {
		var iphoneX = {sysFunc: "getItem:", room: "iphoneX", id: "iphoneX", data: ""};
		var iStr ='';
		if(!window.v6sysfunc) {
             iStr = prompt(JSON.stringify(iphoneX))
        }
		if(iStr == "iphoneX") {
			var offArr = document.getElementsByClassName("mask_layer")
            var load = document.getElementsByClassName("loadToast")
			if(offArr != '' && offArr.length != 0){
				offArr[0].style.top = "8.8rem";
                offArr[0].style.height = "calc(100% - 8.8rem)";
                load[0].style.top = "8.8rem";
                load[0].style.height = "calc(100% - 8.8rem)";
			}
			var body = document.getElementsByClassName("header")[0];
			body.style.paddingTop = "2.4rem"
			var top3 = document.getElementsByTagName("body")[0].children[1].style.paddingTop;
			if(top3 == "6.4rem") {
				document.getElementsByTagName("body")[0].children[1].style.paddingTop = "8.8rem";
			} else if(top3 == "9.8rem") {
				document.getElementsByTagName("body")[0].children[1].style.paddingTop = "12.2rem";
			}
            var headHeight = document.getElementsByClassName("header")[0].offsetHeight;
            document.getElementsByClassName("detail")[0].style.height = "calc(100% - " + headHeight + "px)";
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
    initMidCss();
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
    window.device_Ok_name = languageUtil.getResource("Device_Ok_Details_Name")//分体式智能空调
    window.Device_Ok_Details_Open = languageUtil.getResource("Device_Ok_Details_Open");//"开机";
    window.Device_Ok_Details_Close = languageUtil.getResource("Device_Ok_Details_Close");//"关机";
    window.count_down_off = languageUtil.getResource("Device_Ok_Details_Time1");
    window.count_down_on = languageUtil.getResource("Device_Ok_Details_Time2");
    window.left_right = languageUtil.getResource("Device_Ok_Details_Direction1");//左右风
    window.up_down = languageUtil.getResource("Device_Ok_Details_Direction2");//上下风
    window.direction_off = languageUtil.getResource("Device_Ok_Details_TIPS0");//关闭
    window.mode_cold = languageUtil.getResource("Device_Ok_Details_Type1");//"制冷";
    window.mode_heat = languageUtil.getResource("Device_Ok_Details_Type2");//"制热";
    window.mode_blowing = languageUtil.getResource("Device_Ok_Details_Type3");//"送风";
    window.mode_dehumidification = languageUtil.getResource("Device_Ok_Details_Type4");//"除湿";
    window.mode_auto = languageUtil.getResource("Device_Ok_Details_Type6");//"自动";
    window.speed_mute = languageUtil.getResource("Device_Ok_Details_Type11");//"静音";
    window.speed_high = languageUtil.getResource("Device_Ok_Details_Speed1");//"高";
    window.speed_medium = languageUtil.getResource("Device_Ok_Details_Speed2");//"中";
    window.speed_low = languageUtil.getResource("Device_Ok_Details_Speed3");//"低";
    window.speed_auto = languageUtil.getResource("Device_Ok_Details_Speed4");//"自动";
    window.fast_mode = languageUtil.getResource("Device_Ok_Details_Type9_tips");//"快速";
    window.Device_Ok_Details_Cancel = languageUtil.getResource("Device_Ok_Details_Cancel");//"取消";
    window.Device_Ok_Details_Confirm = languageUtil.getResource("Device_Ok_Details_Confirm");//"确定";
    window.hours_unit = languageUtil.getResource("Device_Ok_Details_Time_TIPS1");//"小时";
    window.minute_unit = languageUtil.getResource("Device_Ok_Details_Time_TIPS2");//"分钟";
    window.count_down_on_title = languageUtil.getResource("Device_Ok_Details_Time_TIPS3");//"倒计时开启";
    window.count_down_off_title = languageUtil.getResource("Device_Ok_Details_Time_TIPS4");//"倒计时关闭";
}


