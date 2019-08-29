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

/*
 * 处理js中的国际化
 */
function globalLang() {
    window.Device_Bm_Details_Name	= languageUtil.getResource("Device_Ap_Details_Name");
    window.Device_Bm_Details_High_Protect	= languageUtil.getResource("Device_Ap_Details_High_Protect");
    window.Device_Bm_Details_Low_Protect	= languageUtil.getResource("Device_Ap_Details_Low_Protect");
    window.Device_Bm_Details_System	= languageUtil.getResource("Device_Ap_Details_System");
    window.Device_Bm_Details_System3	= languageUtil.getResource("Device_Ap_Details_System3");
    window.Device_Bm_Details_System4	= languageUtil.getResource("Device_Ap_Details_System4");
    window.Device_Bm_Details_Return	= languageUtil.getResource("Device_Ap_Details_Return");
    window.Device_Bm_Details_Save	= languageUtil.getResource("Device_Ap_Details_Save");
    window.Device_Bm_Details_Restore	= languageUtil.getResource("Device_Ap_Details_Restore");
    window.Device_Af_Details_Scale	= languageUtil.getResource("Device_Ap_Details_Scale");
    window.Device_Af_Details_Sound	= languageUtil.getResource("Device_Ap_Details_Sound");
    window.Device_Af_Details_Shock	= languageUtil.getResource("Device_Ap_Details_Shock");
    window.lang_Log	= languageUtil.getResource("moreSettingLog");
    window.lang_Alarm	= languageUtil.getResource("moreSettingAlarm");
    window.Device_Af_Details_Time1	= languageUtil.getResource("Device_Ap_Details_Time1");
    window.Device_Af_Details_Time2	= languageUtil.getResource("Device_Ap_Details_Time2");
    window.langEdit	= languageUtil.getResource("Device_Ap_Details_Time3");
    window.langStart	= languageUtil.getResource("Device_Ap_Details_Time4");
    window.langClose	= languageUtil.getResource("Device_Ap_Details_Time5");
    window.langNotZero	= languageUtil.getResource("Device_Ap_Details_Time6");
    window.langEidtError	= languageUtil.getResource("Device_Ap_Details_Time_TIPS1");
    window.langHour	= languageUtil.getResource("Device_Ap_Details_Time7");
    window.langMin	= languageUtil.getResource("Device_Ap_Details_Time8");
    window.langCancel	= languageUtil.getResource("Device_Ap_Details_Cancel");
    window.langConfirm	= languageUtil.getResource("Device_Ap_Details_Confirm");
    window.Device_Ap_Details_Scale	= languageUtil.getResource("Device_Ap_Details_Scale");
    window.innerErrorMsg	= languageUtil.getResource("Device_Ap_Details_Abnormal1");
    window.outerErrorMsg1	= languageUtil.getResource("Device_Ap_Details_Abnormal2");
    window.outerErrorMsg2	= languageUtil.getResource("Device_Ap_Details_Abnormal2_tips1");
    window.outerErrorMsg3	= languageUtil.getResource("Device_Ap_Details_Abnormal2_tips2");
    window.outerErrorMsg4	= languageUtil.getResource("Device_Ap_Details_Abnormal2_tips3");
    window.outerErrorMsg5	= languageUtil.getResource("Device_Ap_Details_Abnormal3");

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