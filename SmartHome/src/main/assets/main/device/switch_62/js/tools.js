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

		},
		back: function(onsucceed) {
			var callback = function(bridge) {
				bridge.callHandler("back", onsucceed);
			}
			return B.exec(callback, _BARCODE, "back");
		},
        toast: function(param, onsucceed) {
            var callback = function(bridge) {
                bridge.callHandler("toast", param, onsucceed);
            }
            return B.exec(callback, _BARCODE, "toast");
        },
        goBack: function(onsucceed) {
            var callback = function(bridge) {
                bridge.callHandler("goBack", onsucceed);
            }
            return B.exec(callback, _BARCODE, "goBack");
        },
		more: function(param,onsucceed) {
            var callback = function(bridge) {
                if(typeof onsucceed == "function") {
                    bridge.callHandler("more", param, onsucceed);
                } else {
                    console.log("fail");
                    return;
                }
            }
            return B.exec(callback, _BARCODE, "more");
		},
        newPage: function(param,onsucceed) {
            var callback = function(bridge) {
                if(typeof onsucceed == "function") {
                    bridge.callHandler("newPage", param, onsucceed);
                } else {
                    console.log("fail");
                    return;
                }
            }
            return B.exec(callback, _BARCODE, "newPage");
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
    var reg = new RegExp("(^|&)" + key + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return decodeURI(r[2]);
    return null;
}
function noFace(param){
    var regRule = /\uD83C[\uDF00-\uDFFF]|\uD83D[\uDC00-\uDE4F]/g;
    param = param.replace(/(^\s*)|(\s*$)/g, "");
    if(param.match(regRule)) {
        param = param.replace(/\uD83C[\uDF00-\uDFFF]|\uD83D[\uDC00-\uDE4F]/g, "");
        return param;
    }else{
        return param;
    }
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