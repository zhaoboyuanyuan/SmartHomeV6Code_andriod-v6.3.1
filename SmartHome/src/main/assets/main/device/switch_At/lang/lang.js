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
    window.lang_alarm = languageUtil.getResource("lang_alarm")// "报警消息"
    window.lang_sms = languageUtil.getResource("lang_sms")// "日志"
    window.lang_custom_02 = languageUtil.getResource("lang_custom_02")// "恢复断电前状态"
    window.lang_custom_02_desc_01 = languageUtil.getResource("lang_custom_02_desc_01")// "开启后，如遇停电再次供电时，设备将恢复断电前状态；关闭后，如遇停电再次供电时，设备将是关闭状态"
    window.lang_custom_03 = languageUtil.getResource("lang_custom_03")// "开启绑定/解绑模式"
    window.lang_custom_03_desc_01 = languageUtil.getResource("lang_custom_03_desc_01")// "开启后，设备可与场景开关绑定或解绑"
    window.device_At_name = languageUtil.getResource("device_At_name")//"内嵌式零火二路开关"
	window.device_hour = languageUtil.getResource("device_hour")//"小时"
    window.device_min = languageUtil.getResource("device_min")//"分钟"
    window.device_rename = languageUtil.getResource("device_rename")//"重命名"
    window.device_input_name = languageUtil.getResource("device_input_name")//"请输入名称"
    window.device_cancal = languageUtil.getResource("device_cancal")//"取消"
    window.device_sure = languageUtil.getResource("device_sure")//"确定"
    window.device_switch = languageUtil.getResource("device_switch")//"开关"
    window.device_countDown_open = languageUtil.getResource("device_countDown_open")//"倒计时开启开关"
    window.device_opened = languageUtil.getResource("device_opened")//"后开启"
    window.device_countDown_close = languageUtil.getResource("device_countDown_close")//"倒计时关闭开关"
    window.device_closed = languageUtil.getResource("device_closed")//"后关闭"
    window.device_Aj_open = languageUtil.getResource("device_Aj_open")//"已开启"
    window.device_Aj_close = languageUtil.getResource("device_Aj_close")//"已关闭"
    window.device_explain_01 = languageUtil.getResource("device_explain_01")//"绑定/解绑模式已开启，请执行以下操作："
    window.device_explain_02 = languageUtil.getResource("device_explain_02")//"1. 请于20S内长按绑定开关SET键，同时按一下需绑定/解绑按键，松开SET键.<br />2. 绑定开关指示灯闪烁3次表示绑定/解绑成功，闪烁6次绑定/解绑失败"
    window.device_explain_konw = languageUtil.getResource("device_explain_konw")//"我知道了"
}
