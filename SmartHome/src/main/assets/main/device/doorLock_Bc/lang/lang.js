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
			var Mtop3 = document.getElementsByTagName("body")[0].children[1].style.marginTop;
			if(Mtop3 == "6.4rem") {
				document.getElementsByTagName("body")[0].children[1].style.marginTop = "8.8rem";
			} else if(Mtop3 == "9.8rem") {
				document.getElementsByTagName("body")[0].children[1].style.marginTop = "12.2rem";
			}
            for(var iphoneXIndex = 0; iphoneXIndex < document.getElementsByClassName("iphoneX").length; iphoneXIndex++)
            {
                var iphoneXTop = document.getElementsByClassName("iphoneX")[iphoneXIndex].style.top;
                var iphoneXMarginTop = document.getElementsByClassName("iphoneX")[iphoneXIndex].style.marginTop;
                if (iphoneXTop == "6.4rem"){
                    document.getElementsByClassName("iphoneX")[iphoneXIndex].style.top = "8.8rem";
                }
                if (iphoneXMarginTop == "6.4rem") {
                    document.getElementsByClassName("iphoneX")[iphoneXIndex].style.marginTop = "8.8rem";
                }

            }
			document.getElementsByClassName("iphoneX88")[0].style.top = "8.8rem"
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
	window.alarmSet_txt_02 = languageUtil.getResource("alarmSet_txt_02") //"用户不存在"
	window.alarmSet_txt_03 = languageUtil.getResource("alarmSet_txt_03") //"用户密码错误"
	window.alarmSet_txt_04 = languageUtil.getResource("alarmSet_txt_04") //"token错误"
	window.alarmSet_txt_05 = languageUtil.getResource("alarmSet_txt_05") //"用户名已被使用"
	window.alarmSet_txt_06 = languageUtil.getResource("alarmSet_txt_06") //"手机号已被注册"
	window.alarmSet_txt_07 = languageUtil.getResource("alarmSet_txt_07") //"手机号未认证"
	window.alarmSet_txt_08 = languageUtil.getResource("alarmSet_txt_08") //"邮箱已被注册"
	window.alarmSet_txt_09 = languageUtil.getResource("alarmSet_txt_09") //"邮箱未认证"
	window.alarmSet_txt_10 = languageUtil.getResource("alarmSet_txt_10") //"手机验证码错误"
	window.alarmSet_txt_11 = languageUtil.getResource("alarmSet_txt_11") //"设备不存在"
	window.alarmSet_txt_12 = languageUtil.getResource("alarmSet_txt_12") //"该设备不是网关"
	window.alarmSet_txt_13 = languageUtil.getResource("alarmSet_txt_13") //"设备密码错误"
	window.alarmSet_txt_14 = languageUtil.getResource("alarmSet_txt_14") //"用户不是设备的拥有者"
	window.alarmSet_txt_15 = languageUtil.getResource("alarmSet_txt_15") //"设备已与用户绑定"
	window.alarmSet_txt_16 = languageUtil.getResource("alarmSet_txt_16") //"设备与用户未绑定"
	window.alarmSet_txt_17 = languageUtil.getResource("alarmSet_txt_17") //"设备密码错误"
	window.alarmSet_txt_18 = languageUtil.getResource("alarmSet_txt_18") //"查询失败，请稍后重试"
	window.alarmSet_txt_19 = languageUtil.getResource("alarmSet_txt_19") //"设备间关系已存在"

	window.alarmSet_js_01 = languageUtil.getResource("alarmSet_txt_19") //网络智能锁
	window.alarmSet_js_02 = languageUtil.getResource("alarmSet_js_02") //门未关好  
	window.alarmSet_js_03 = languageUtil.getResource("inputPassWord") //请输入密码
	window.alarmSet_js_04 = languageUtil.getResource("alarmSet_js_04") //上保险
	window.alarmSet_js_05 = languageUtil.getResource("alarmSet_js_05") //解除保险
	window.alarmSet_js_06 = languageUtil.getResource("alarmSet_js_06") //解除反锁
	window.alarmSet_js_07 = languageUtil.getResource("alarmSet_js_07") //门已反锁
	window.alarmSet_js_08 = languageUtil.getResource("alarmSet_js_08") //门锁已开
	window.alarmSet_js_09 = languageUtil.getResource("alarmSet_js_09") //密码错误，请重试
	window.alarmSet_js_10 = languageUtil.getResource("alarmSet_js_10") //强制上锁
	window.alarmSet_js_11 = languageUtil.getResource("alarmSet_js_11") //自动上锁
	window.alarmSet_js_12 = languageUtil.getResource("alarmSet_js_12") //登记密码

	window.alarmSet_js_13 = languageUtil.getResource("alarmSet_js_13") //无线端密码开锁失败 ，请重试
	window.alarmSet_js_14 = languageUtil.getResource("alarmSet_js_14") //动态密码失败，请重试
	window.alarmSet_js_15 = languageUtil.getResource("alarmSet_js_15") //操作失败，请重试
	window.alarmSet_js_16 = languageUtil.getResource("alarmSet_js_16") //请求超时!
	window.alarmSet_js_17 = languageUtil.getResource("alarmSet_js_17") //开启失败，请重试！

	window.alarmSet_js_18 = languageUtil.getResource("alarmSet_js_18") //操作失败
	window.alarmSet_js_19 = languageUtil.getResource("offLine") //设备已离线
	window.alarmSet_js_20 = languageUtil.getResource("alarmSet_js_20") //密码错误
	window.alarmSet_js_21 = languageUtil.getResource("alarmSet_js_21") //密码已存在，请换一个
	window.alarmSet_js_22 = languageUtil.getResource("alarmSet_js_22") //用户添加数量已达上限
	window.alarmSet_js_23 = languageUtil.getResource("alarmSet_js_23") //添加新名单失败
	window.alarmSet_js_24 = languageUtil.getResource("alarmSet_js_24") //密码必须为6位数字
	window.alarmSet_js_25 = languageUtil.getResource("alarmSet_js_25") //用户名不能为空
	window.alarmSet_js_26 = languageUtil.getResource("alarmSet_js_26") //删除用户失败

	window.alarmSet_js_27 = languageUtil.getResource("alarmSet_js_27") //下拉获取最新账号列表
	window.alarmSet_js_28 = languageUtil.getResource("alarmSet_js_28") //管理员用户
	window.alarmSet_js_29 = languageUtil.getResource("alarmSet_js_29") //普通用户
	window.alarmSet_js_30 = languageUtil.getResource("alarmSet_js_30") //临时用户
	window.alarmSet_js_31 = languageUtil.getResource("addDevice_know") //删除
	window.alarmSet_js_32 = languageUtil.getResource("alarmSet_js_32") //开锁中...

	window.chooseScene_js_01 = languageUtil.getResource("chooseScene_js_01"); //选择场景
	window.chooseScene_js_02 = languageUtil.getResource("chooseScene_js_02"); //绑定
	window.chooseScene_js_03 = languageUtil.getResource("chooseScene_js_03"); //解绑
	window.chooseScene_js_04 = languageUtil.getResource("chooseScene_js_04"); //正在绑定
	window.chooseScene_js_05 = languageUtil.getResource("chooseScene_js_05"); //正在解除绑定
	window.chooseSecne_js_06 = languageUtil.getResource("chooseSecne_js_06"); //解绑成功
	window.chooseSecne_js_07 = languageUtil.getResource("chooseSecne_js_07"); //绑定成功
	window.alarmSetting_js_04 = languageUtil.getResource("alarmSetting_js_04"); //高
	window.alarmSetting_js_08 = languageUtil.getResource("alarmSetting_js_08"); //低
	window.alarmSetting_js_07 = languageUtil.getResource("alarmSetting_js_07"); //无
	window.alarmSetting_js_10 = languageUtil.getResource("alarmSetting_js_10"); //抓拍
	window.alarmSetting_js_11 = languageUtil.getResource("alarmSetting_js_11"); //录像
	window.alarmSetting_js_12 = languageUtil.getResource("alarmSetting_js_12"); //设置成功
	window.alarmSetting_js_13 = languageUtil.getResource("alarmSetting_js_13"); //设置失败
	window.alarmSetting_js_14 = languageUtil.getResource("alarmSetting_js_14"); //正在保存
	window.alarmSetting_js_15 = languageUtil.getResource("alarmSetting_js_15"); //正在查询

	window.alarmSetting_js_16 = languageUtil.getResource("inputName"); //请输入用户名

	window.moreSetting01 = languageUtil.getResource("moreSetting01"); //访客记录
	window.moreSetting02 = languageUtil.getResource("moreSetting02"); //抓拍图库
	window.moreSetting03 = languageUtil.getResource("moreSetting03"); //WiFi配置
	window.moreSetting04 = languageUtil.getResource("moreSetting04"); //离家按钮
	window.moreSetting05 = languageUtil.getResource("moreSetting05"); //用户管理
	window.moreSetting06 = languageUtil.getResource("moreSetting06"); //报警消息
	window.moreSetting07 = languageUtil.getResource("moreSetting07"); //日志
	window.moreSetting08 = languageUtil.getResource("moreSetting08"); //报警设置
	window.moreSetting09 = languageUtil.getResource("moreSetting09"); //设备信息
	window.cancel = languageUtil.getResource("cancel");
	window.sure = languageUtil.getResource("sure");
	window.account_txt_11 = languageUtil.getResource("device_op_txt_11") //"有效"
	window.account_txt_12 = languageUtil.getResource("device_op_txt_12") //"过期"
	window.normal_txt_04 = languageUtil.getResource("normal_txt_04") //"保存超时，请重试"
	window.isSave = languageUtil.getResource("lang_op_isSave") //"正在保存"
	window.alarm_txt_05 = languageUtil.getResource("lang_op_isSave") //"保存成功"
	window.device_bc_num_02 = languageUtil.getResource("device_bc_num_02"); //普通用户 只可在锁体上设置，最多可设20人
	window.device_bc_send_content_1 = languageUtil.getResource("DoorLook_More_Send_content_1");
	window.device_bc_send_content_2 = languageUtil.getResource("DoorLook_More_Send_content_2");
	window.device_bc_send_content_3 = languageUtil.getResource("DoorLook_More_Send_content_3");
	window.device_bc_send_content_4 = languageUtil.getResource("DoorLook_More_Send_content_4");
	window.lock_Scenes_bind_time_out = languageUtil.getResource("lock_Scenes_bind_time_out");
	window.lock_Scenes_bind_no_bind = languageUtil.getResource("lock_Scenes_bind_no_bind");
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