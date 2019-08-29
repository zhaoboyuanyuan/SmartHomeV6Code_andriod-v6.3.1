var vioceMaxNumKey = "vioceMaxNum";
var dialogKey = "html_dialog_key";
var XMLY = function(appkey, appsecret, serverAuthenticateStaticKey) {
	this.initialize(appkey, appsecret, serverAuthenticateStaticKey);
};

XMLY.prototype = {
	BASE_URL: 'http://api.ximalaya.com',
	initialize: function(appkey, appsecret, serverAuthenticateStaticKey) {
		//		this.appkey = '4328926b7e964ec07a417c22d5072127';
		//		this.appsecret = 'ca81a01a1ea79a5a492cbc2eb6e45e23';
		//		this.serverAuthenticateStaticKey = 'CCaxGE0U';
		this.appkey = appkey;
		this.appsecret = appsecret;
		this.serverAuthenticateStaticKey = serverAuthenticateStaticKey;
		this.password = appsecret + serverAuthenticateStaticKey;
	},
	getSignature: function(str) {
		var b64 = CryptoJS.enc.Base64.stringify(CryptoJS.enc.Utf8.parse(str));
		var sig = CryptoJS.HmacSHA1(b64, this.password);
		var sig1 = CryptoJS.MD5(sig);
		return sig1;
	},
	getRandomString: function(length) {
		var randStr = '';
		for (var i = 0; i < length; i++) {
			var randi = Math.floor(Math.random() * 62);
			randStr += "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".charAt(randi);
		}
		return randStr;
	},
	getUrl: function(uri, params) {
		params.put('app_key', this.appkey);
		params.put('client_os_type', '4');
		params.put('nonce', this.getRandomString(10));
		var uriParams = "";
		var p = "";
		params.eachByKeyOrder(function(key, value) {
			if (p != "") {
				p += "&";
				uriParams += "&";
			}
			p += (key + "=" + value);
			uriParams += (key + "=" + encodeURI(value));
		});

		var sig = this.getSignature(p);
		url = this.BASE_URL + uri + "?" + uriParams + "&sig=" + sig;
		return url;
	},
	callAPI: function(uri, params, onSuccess, onFailed) {
		var url = this.getUrl(uri, params);
		var xhr = new plus.net.XMLHttpRequest();
		xhr.onreadystatechange = function() {
			if (xhr.status == 200) {
				if (typeof(onSuccess) == "function") {
					onSuccess(xhr.responseText);
				}
			} else {
				//http请求异常
				if (typeof(onFailed) == "function") {
					onFailed(xhr.status);
				}
			}
		}
		xhr.open("GET", url);
		xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
		//		xhr.setRequestHeader('User-Agent', 'Mozilla/5.0 (Linux; Android 4.4.2; M812C Build/KVT49L) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36');
		xhr.send();
	}

}

function controlDevice(epData, onSuccess, onFailed) {
	var body = {};
	body.gwID = plus.ehomev5.getData($CONSTANTS.GATEWAYID);
	body.devID = plus.ehomev5.getData($CONSTANTS.DEVICEID);
	body.ep = plus.ehomev5.getData($CONSTANTS.EP);
	body.epType = plus.ehomev5.getData($CONSTANTS.EPTYPE);
	body.epData = epData;
	body.cmd = "12";
	var callbackId = body.gwID + "13";
	plus.ehomev5.sendControlDevice(JSON.stringify(body), callbackId, onSuccess, onFailed);
}

function sendCloudMsg(cmdIndex, epData, onSuccess, onFailed) {
	var msgBody = {};
	msgBody.gwID = plus.ehomev5.getData($CONSTANTS.GATEWAYID);
	if (epData == null || typeof(epData) == "undefined" || epData.length == 0) epData = "";
	msgBody.data = epData;
	msgBody.cmd = "405";
	msgBody.cmdindex = cmdIndex;
	var callBackKey = msgBody.gwID + msgBody.cmd + msgBody.cmdindex;
	plus.ehomev5.sendControlDevice(JSON.stringify(msgBody), callBackKey, function(value) {
		onSuccess(value);
	}, function(error) {
		onFailed(error);
	});
}

//搜索网盘上的MP3文件
function searchDiskMusic(path, onSuccess, onFailed) {
	var param = {};
	param.path = path;
	param.keyword = "*.mp3";
	sendCloudMsg("8", param, onSuccess, onFailed);
}

function initPlayState(data) {
	try {
		if (data != null && !(typeof(data) == "undefined") && data.length != 0) {
			var playInfo = JSON.parse(data);
			plus.ehomev5.setData($CONSTANTS.DEVICEPARAM, data);
			if(playInfo.iconUrl&&playInfo.iconUrl!="null"){
				document.getElementById("play_cover").src = playInfo.iconUrl;
			}
			if(playInfo.name){
				document.getElementById("play_name").textContent = playInfo.name;
			}
			var playIcon = document.getElementById("play_icon");
			if (playInfo.playStatus == "0") {
				playIcon.style.backgroundImage = "url(img/radio_play.png)";
				playIcon.onclick = function() {
					controlDevice("Z1", function(result) {
						initPlayState(result);
					}, function(error) {
						alert(plus.ehomev5.getLang("html_user_operation_failed"));
					});
				}
			} else {
				playIcon.style.backgroundImage = "url(img/radio_stop.png)";
				playIcon.onclick = function() {
					controlDevice("Z0", function(result) {
						initPlayState(result);
					}, function(error) {
						alert(plus.ehomev5.getLang("html_user_operation_failed"));
					});
				}
			}

			if (playInfo.overNumber) {
				plus.ehomev5.setData(vioceMaxNumKey, playInfo.overNumber);
			}
		}
	} catch (e) {
		console.log(e);
	}
	document.getElementById("play_cover").addEventListener("click", function() {
		var wobj = plus.webView.currentWebview();
		plus.ehomev5.closeWebview(wobj.id);
	});

	document.getElementById("play_name").addEventListener("click", function() {
		var wobj = plus.webView.currentWebview();
		plus.ehomev5.closeWebview(wobj.id);
	});
}

function playNext() {
	controlDevice("Y1", function(result) {
		initPlayState(result);
	}, function(error) {
		alert(plus.ehomev5.getLang("html_user_operation_failed"));
	});
}

function playPer() {
	controlDevice("Y0", function(result) {
		initPlayState(result);
	}, function(error) {
		alert(plus.ehomev5.getLang("html_user_operation_failed"));
	});
}

function formatParam(param) {
	param = param.replace("[\r\n]", "");
	if (param.length > 20) param = param.substring(0, 20);
	var regExp = /\s/
	if (regExp.exec(param)) {
		param = "\"" + param + "\"";
	}
	return param;
}

function regectCallBack(key, onSuccess, onFail) {
	if (onSuccess == null || typeof(onSuccess) != "function") {
		onSuccess = function(data) {
			initPlayState(data);
		};
	}
	plus.callbackUtil.putCallback(key, onSuccess, onFail);
	var body = {};
	body.gwID = plus.ehomev5.getData($CONSTANTS.GATEWAYID);
	body.devID = plus.ehomev5.getData($CONSTANTS.DEVICEID);
	body.ep = plus.ehomev5.getData($CONSTANTS.EP);
	body.epType = plus.ehomev5.getData($CONSTANTS.EPTYPE);
	body.epData = "";
	body.cmd = "12";
	var callbackId = body.gwID + "13";
	plus.ehomev5.sendControlDevice(JSON.stringify(body), callbackId, onSuccess, onFail);
}