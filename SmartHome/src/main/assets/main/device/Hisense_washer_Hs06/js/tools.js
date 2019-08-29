(function() {
	var _BARCODE = 'tools',
		B = window.plus.bridge;
	var tools = {
		//判断场景是否重命名
		isSceneExist: function(param, onsucceed) {
			var callback = function(bridge) {
				if(typeof onsucceed == "function") {
					bridge.callHandler("isSceneExist", param, onsucceed);
				} else {
					console.log("fail");
					return;
				}
			}
			return B.exec(callback, _BARCODE, "isSceneExist");
		},
		back: function(onsucceed) {
			var callback = function(bridge) {
				bridge.callHandler("back", onsucceed);
			}
			return B.exec(callback, _BARCODE, "back");
		},
		more: function(onsucceed) {
			var callback = function(bridge) {
				bridge.callHandler("more", onsucceed);
			}
			return B.exec(callback, _BARCODE, "more");
		},
		getLoginType: function(onsucceed) {
			var callback = function(bridge) {
				bridge.callHandler("getLoginType", onsucceed);
			}
			return B.exec(callback, _BARCODE, "mogetLoginTypere");
		}
	};
	window.plus.tools = tools;
})();

function getUrlParam(key) {
	if(key == null || typeof(key) == "undefined" || key.length == 0) return "";
	var reg = new RegExp(key + "=([{}\\[\\]\"'a-zA-Z0-9%:,\\.\\-_]+)&{0,1}");
	var value = window.location.search;
	if(reg.test(value)) {
		value = RegExp.$1;
	} else {
		value = "";
	}
	return value;
}

function getEncodeURIParam(key) {
    if(key == null || typeof(key) == "undefined" || key.length == 0) return "";
    var reg = new RegExp(key + "=([{}\\[\\]\"'a-zA-Z0-9%:,\\.\\-_]+)&{0,1}");
    var value = window.location.search;
    if(reg.test(value)) {
        value = decodeURI(RegExp.$1);
    } else {
        value = "";
    }
    return value;
}
(function() {
	if(!window.sysfun) {
		var p = function() {};
		var da = "";
		p.prototype = {
			init: function(interfacce) {
				var model = {
					sysFunc: "", //函数名称
					room: interfacce, //存储区域
					id: "", // key
					data: "" //值
				}
				da = model;
			},
			getItem: function(key) {
				da.id = key;
				da.sysFunc = "getItem:";
				var result;
				if(window.v6sysfunc) {
					result = window.v6sysfunc.sysfun(JSON.stringify(da));
				} else {
					result = prompt(JSON.stringify(da), "");
				}
				return result || null;
			},
			setItem: function(key, data) {
				da.id = key;
				da.data = data;
				da.sysFunc = "setItem:";
				var result;
				if(window.v6sysfunc) {
					result = window.v6sysfunc.sysfun(JSON.stringify(da));
				} else {
					result = prompt(JSON.stringify(da), "");
				}
				return result;
			},
			removeItem: function(key) {
				da.id = key;
				da.sysFunc = "removeItem:";
				var result;
				if(window.v6sysfunc) {
					result = window.v6sysfunc.sysfun(JSON.stringify(da));
				} else {
					result = prompt(JSON.stringify(da), "");
				}
				return result;
			},
			clear: function() {
				da.sysFunc = "clear:";
				var result;
				if(window.v6sysfunc) {
					result = window.v6sysfunc.sysfun(JSON.stringify(da));
				} else {
					result = prompt(JSON.stringify(da), "");
				}
				return result;
			},
            getLang: function() {
                var fun = {};
                fun.sysFunc = "getLang:";
                var result;
                if(window.v6sysfunc) {
                    result = window.v6sysfunc.sysfun(JSON.stringify(fun));
                } else {
                    result = prompt(JSON.stringify(fun), "");
                }
                return result;
            },
		};
		window.sysfun = new p();
	}
})();