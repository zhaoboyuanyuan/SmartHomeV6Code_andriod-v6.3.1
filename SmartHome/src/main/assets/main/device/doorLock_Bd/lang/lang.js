function initlan() {
	try {
		var iphoneX = { sysFunc: "getItem:", room: "iphoneX", id: "iphoneX", data: "" };
		var iStr = '';
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
		if(item[i].type == "search" && item[i].placeholder != "" && item[i].localName == "input") {
			item[i].placeholder = languageUtil.getResource(item[i].placeholder);
		} else {
			var languagerText = languageUtil.getResource(item[i].getAttribute("key"));
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
	window.account_txt_01 = languageUtil.getResource("device_optxt_01") // "添加名额已满"
	window.account_txt_02 = languageUtil.getResource("device_optxt_02") //"单次密码每次仅1条有效，请使用后继续添加"
	window.account_txt_03 = languageUtil.getResource("device_optxt_03") //"下拉获取最新账号列表"
	window.account_txt_04 = languageUtil.getResource("device_optxt_04") //"暂无普通用户，请添加"
	window.account_txt_05 = languageUtil.getResource("device_optxt_05") //"暂无临时用户，请添加"
	window.account_txt_06 = languageUtil.getResource("device_optxt_06") //"暂无单次密码，请添加"
	window.account_txt_07 = languageUtil.getResource("device_op_txt_07") //"管理员用户"
	window.account_txt_08 = languageUtil.getResource("device_op_txt_08") //"普通用户"
	window.account_txt_09 = languageUtil.getResource("device_op_txt_09") //"临时用户"
	window.account_txt_10 = languageUtil.getResource("device_op_txt_10") //"单次密码"
	window.account_txt_11 = languageUtil.getResource("device_op_txt_11") //"有效"
	window.account_txt_12 = languageUtil.getResource("device_op_txt_12") //"过期"
	window.account_txt_13 = languageUtil.getResource("device_op_txt_13") //"确认删除用户及密码？"
	window.account_txt_14 = languageUtil.getResource("device_op_txt_14") //"取消"
	window.account_txt_15 = languageUtil.getResource("device_op_txt_15") //"确定"
	window.account_txt_16 = languageUtil.getResource("device_op_txt_16") //"操作失败<br />另一管理员用户正在编辑此页面"
	window.account_txt_17 = languageUtil.getResource("device_op_txt_17") //"设备离线"
	window.account_txt_18 = languageUtil.getResource("device_op_txt_18") //"密码错误"
	window.account_txt_19 = languageUtil.getResource("device_op_txt_19") //"密码已经存在"
	window.account_txt_20 = languageUtil.getResource("device_op_txt_20") //"用户添加已满"
	window.account_txt_21 = languageUtil.getResource("device_op_txt_21") //"完成"
	window.account_txt_22 = languageUtil.getResource("device_op_txt_22") //"编辑"
	window.account_txt_23 = languageUtil.getResource("device_op_txt_23") //"删除"
	window.account_txt_24 = languageUtil.getResource("device_op_txt_24") //"长期有效"
	window.account_txt_25 = languageUtil.getResource("device_op_txt_25") //"添加用户失败"
	window.account_txt_26 = languageUtil.getResource("device_op_txt_26") //"修改密码失败"
	window.account_txt_27 = languageUtil.getResource("device_op_txt_27") //"动态密码失败"
	window.account_txt_28 = languageUtil.getResource("device_op_txt_28") //"添加名单存在重复"
	window.account_txt_29 = languageUtil.getResource("device_op_txt_29") //"一键清除失败"
	window.account_txt_30 = languageUtil.getResource("device_op_txt_30") //"删除用户失败"
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
	window.normal_txt_01 = languageUtil.getResource("normal_txt_01") //"用户名及密码不能为空"
	window.normal_txt_02 = languageUtil.getResource("normal_txt_02") //"用户名不能为空"
	window.normal_txt_03 = languageUtil.getResource("normal_txt_03") //"请输入6位数字密码"
	window.normal_txt_04 = languageUtil.getResource("normal_txt_04") //"保存超时，请重试"
	window.isSave = languageUtil.getResource("lang_op_isSave") //"正在保存"
	window.alarm_txt_05 = languageUtil.getResource("lang_alarm_txt_05") //"保存成功"
	window.once_txt_03 = languageUtil.getResource("once_txt_01") //"请生成6位数字密码"
	window.lang_name_op = languageUtil.getResource("device_op_title") //"wulian玄武湖系列网络锁"
	window.lang_txt_01 = languageUtil.getResource("lang_txt_01") //"请输入密码"
	window.lang_txt_02 = languageUtil.getResource("lang_txt_02") //"门锁已上保险"
	window.lang_txt_03 = languageUtil.getResource("lang_txt_03") //"门锁已解除保险"
	window.lang_txt_04 = languageUtil.getResource("lang_txt_04") //"门锁反锁,无法打开"
	window.lang_txt_05 = languageUtil.getResource("lang_txt_05") //"门锁已解除反锁"
	window.lang_txt_06 = languageUtil.getResource("lang_txt_06") //"门锁已解除锁定"
	window.lang_txt_07 = languageUtil.getResource("lang_txt_07") //"开锁失败"
	window.lang_txt_08 = languageUtil.getResource("lang_txt_08") //"开锁密码验证错误"
	window.lang_txt_09 = languageUtil.getResource("lang_txt_09") //"时间同步失败"
	window.lang_txt_16 = languageUtil.getResource("lang_txt_16") //"操作失败"
	window.lang_txt_17 = languageUtil.getResource("lang_txt_17") //"门锁已开"
	window.lang_txt_18 = languageUtil.getResource("lang_txt_18") //"密码错误，请重试"
	window.lang_txt_19 = languageUtil.getResource("lang_txt_19") //"输入错误次数较多，门锁已被锁定，5分钟后再试"
	window.lang_txt_20 = languageUtil.getResource("lang_txt_20") //"自动上锁"
	window.lang_txt_22 = languageUtil.getResource("lang_txt_22") //"强制上锁"
	window.lang_txt_21 = languageUtil.getResource("lang_txt_21") //"已关闭网络开门，如需开启请管理员在锁体上设置"
	window.lang_txt_23 = languageUtil.getResource("lang_txt_23") //"开锁中..."
	window.lang_txt_24 = languageUtil.getResource("lang_txt_24") //"请求超时！请重试"
	window.account_txt_22 = languageUtil.getResource("device_op_txt_22") //"编辑"
	window.normal_txt_05 = languageUtil.getResource("device_op_save") //"保存"
	window.device_Bd_title = languageUtil.getResource("device_Bd_title") //"保存"

	window.device_bd_alarm_tit = languageUtil.getResource("device_bc_alarm_tit") //"报警设置";
	window.device_bd_alarm_04 = languageUtil.getResource("device_bc_alarm_04") //"手机开锁";
	window.device_bd_alarm_05 = languageUtil.getResource("device_bc_alarm_05") //"射频卡开锁";
	window.device_bd_alarm_06 = languageUtil.getResource("device_bc_alarm_06") //"密码开锁";
	window.device_bd_alarm_07 = languageUtil.getResource("device_bc_alarm_07") //"指纹开锁";
	window.device_bd_alarm_08 = languageUtil.getResource("device_bc_alarm_08") //"反锁";
	window.device_bd_alarm_09 = languageUtil.getResource("device_bd_alarm_09") //"反锁";

	window.moreSettingLog = languageUtil.getResource("moreSettingLog") //"日志";
	window.moreSettingAlarm = languageUtil.getResource("moreSettingAlarm") //"报警";
	window.moreSettingPush = languageUtil.getResource("moreSettingPush") //"推送设置";
	window.moreSettingDoorBoard = languageUtil.getResource("moreSettingDoorBoard") //"是否安装门扣板";
	window.moreSettingUserManger = languageUtil.getResource("moreSettingUserManger") //"用户管理";
	window.moreSettingLeaveHomeBtn = languageUtil.getResource("moreSettingLeaveHomeBtn") //"离家按钮m";
    window.device_bc_send_content_1 = languageUtil.getResource("DoorLook_More_Send_content_1");
    window.device_bc_send_content_2 = languageUtil.getResource("DoorLook_More_Send_content_2");
    window.device_bc_send_content_3 = languageUtil.getResource("DoorLook_More_Send_content_3");
    window.device_bc_send_content_4 = languageUtil.getResource("DoorLook_More_Send_content_4");

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