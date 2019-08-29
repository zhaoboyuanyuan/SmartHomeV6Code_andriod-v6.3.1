(function () {
    var _BARCODE = 'tools',
        B = window.plus.bridge;
    var tools = {
        //判断场景是否重命名
        isSceneExist: function (param, onsucceed) {
            var callback = function (bridge) {
                if (typeof onsucceed == "function") {
                    bridge.callHandler("isSceneExist", param, onsucceed);
                } else {
                    console.log("fail");
                    return;
                }
            }
            return B.exec(callback, _BARCODE, "isSceneExist");
        },
        back: function (onsucceed) {
            var callback = function (bridge) {
                bridge.callHandler("back", onsucceed);
            }
            return B.exec(callback, _BARCODE, "back");
        },
        more: function (param, onsucceed) {
            var callback = function (bridge) {
                bridge.callHandler("more", param, onsucceed);
            }
            return B.exec(callback, _BARCODE, "more");
        },
        startCamera: function (param, onsucceed) {
            var callback = function (bridge) {
                bridge.callHandler("startCamera", param, onsucceed);
            }
            return B.exec(callback, _BARCODE, "startCamera");
        },
        startHisense: function (param, onsucceed) {
            var callback = function (bridge) {
                bridge.callHandler("startHisense", param, onsucceed);
            }
            return B.exec(callback, _BARCODE, "startHisense");
        },
        startWishBgm: function (param, onsucceed) {
            var callback = function (bridge) {
                bridge.callHandler("startWishBgm", param, onsucceed);
            }
            return B.exec(callback, _BARCODE, "startWishBgm");
        },
        startSmart: function (onsucceed) {
            var callback = function (bridge) {
                bridge.callHandler("startSmart", onsucceed);
            }
            return B.exec(callback, _BARCODE, "startSmart");
        },
        addRing: function (onsucceed) {
            var callback = function (bridge) {
                bridge.callHandler("addRing", null, onsucceed);
            }
            return B.exec(callback, _BARCODE, "addRing");
        },
        qrScan: function (onsucceed) {
            var callback = function (bridge) {
                bridge.callHandler("qrScan", null, onsucceed);
            }
            return B.exec(callback, _BARCODE, "qrScan");
        },
        getLoginType: function (onsucceed) {
            var callback = function (bridge) {
                bridge.callHandler("getLoginType", null, onsucceed);
            }
            return B.exec(callback, _BARCODE, "getLoginType");
        },
        getProject: function (onsucceed) {
            var callback = function (bridge) {
                bridge.callHandler("getProject", null, onsucceed);
            }
            return B.exec(callback, _BARCODE, "getProject");
        },
        startSafeDog: function (param, onsucceed) {
            var callback = function (bridge) {
                bridge.callHandler("startSafeDog", param, onsucceed);
            }
            return B.exec(callback, _BARCODE, "startSafeDog");
        },
        startWifiIR: function (param, onsucceed) {
            var callback = function (bridge) {
                bridge.callHandler("startWifiIR", param, onsucceed);
            }
            return B.exec(callback, _BARCODE, "startWifiIR");
        },
        startLiteGW:function(params,onsucceed) {
            var callback = function(bridge) {
                bridge.callHandler("startLiteGW", params, onsucceed)
            };
            return B.exec(callback, _BARCODE, "startLiteGW");
        },
        getGatewayIsShared: function (param, onsucceed) {
            onsucceed = onsucceed || param;
            var callback = function (bridge) {
                bridge.callHandler("getGatewayIsShared", param, onsucceed);
            }
            return B.exec(callback, _BARCODE, "getGatewayIsShared");
        },
        goToBindGateway: function (param, onsucceed) {
            onsucceed = onsucceed || param;
            var callback = function (bridge) {
                bridge.callHandler("goToBindGateway", param, onsucceed);
            }
            return B.exec(callback, _BARCODE, "goToBindGateway");
        },
    };
    window.plus.tools = tools;
})();

function getUrlParam(key) {
    if (key == null || typeof (key) == "undefined" || key.length == 0) return "";
    var reg = new RegExp(key + "=([{}\\[\\]\"'a-zA-Z0-9%:,\\.\\-_]+)&{0,1}");
    var value = window.location.search;
    if (reg.test(value)) {
        value = RegExp.$1;
    } else {
        value = "";
    }
    return value;
}
(function () {
    if (!window.sysfun) {
        var p = function () {};
        var da = "";
        p.prototype = {
            init: function (interfacce) {
                var model = {
                    sysFunc: "", //函数名称
                    room: interfacce, //存储区域
                    id: "", // key
                    data: "" //值
                }
                da = model;
            },
            getItem: function (key) {
                da.id = key;
                da.sysFunc = "getItem:";
                var result;
                if (window.v6sysfunc) {
                    result = window.v6sysfunc.sysfun(JSON.stringify(da));
                } else {
                    result = prompt(JSON.stringify(da), "");
                }
                return result || null;
            },
            setItem: function (key, data) {
                da.id = key;
                da.data = data;
                da.sysFunc = "setItem:";
                var result;
                if (window.v6sysfunc) {
                    result = window.v6sysfunc.sysfun(JSON.stringify(da));
                } else {
                    result = prompt(JSON.stringify(da), "");
                }
                return result;
            },
            removeItem: function (key) {
                da.id = key;
                da.sysFunc = "removeItem:";
                var result;
                if (window.v6sysfunc) {
                    result = window.v6sysfunc.sysfun(JSON.stringify(da));
                } else {
                    result = prompt(JSON.stringify(da), "");
                }
                return result;
            },
            clear: function () {
                da.sysFunc = "clear:";
                var result;
                if (window.v6sysfunc) {
                    result = window.v6sysfunc.sysfun(JSON.stringify(da));
                } else {
                    result = prompt(JSON.stringify(da), "");
                }
                return result;
            },
            getLang: function () {
                var fun = {};
                fun.sysFunc = "getLang:";
                var result;
                if (window.v6sysfunc) {
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