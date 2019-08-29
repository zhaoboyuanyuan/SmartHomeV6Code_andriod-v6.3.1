(function() {
    var _BARCODE = 'gatewayCmd',
        B = window.plus.bridge;
    var gatewayCmd = {
        myDialog: function(param, onsucceed) {
            var callback = function(bridge) {
                if(typeof onsucceed == "function") {
                    bridge.callHandler("myDialog", param, onsucceed);
                } else {
                    console.log("fail");
                    return;
                }
            }
            return B.exec(callback, _BARCODE, "myDialog");
        },
        houserkeep_507: function(param, onsucceed) {
            var callback = function(bridge) {
                if(typeof onsucceed == "function") {
                    bridge.callHandler("houserkeep_507", param, onsucceed);
                } else {
                    console.log("fail");
                    return;
                }
            }
            return B.exec(callback, _BARCODE, "houserkeep_507");
        },
        houserkeep_508: function(param, onsucceed) {
            var callback = function(bridge) {
                if(typeof onsucceed == "function") {
                    bridge.callHandler("houserkeep_508", param, onsucceed);
                } else {
                    console.log("fail");
                    return;
                }
            }
            return B.exec(callback, _BARCODE, "houserkeep_508");
        },
        DataRefresh_508: function(onsucceed) {
            var callback = function(bridge) {
                if(typeof onsucceed == "function") {
                    bridge.registerHandler("DataRefresh_508",onsucceed)
                } else {
                    console.log("fail");
                    return;
                }
            }
            return B.exec(callback, _BARCODE, "houserkeep_508");
        },
        scene_503: function(param, onsucceed) {
            var callback = function(bridge) {
                if(typeof onsucceed == "function") {
                    bridge.callHandler("scene_503", param, onsucceed);
                } else {
                    console.log("fail");
                    return;
                }
            }
            return B.exec(callback, _BARCODE, "scene_503");
        },
        scene_504: function(param, onsucceed) {
            var callback = function(bridge) {
                if(typeof onsucceed == "function") {
                    bridge.callHandler("scene_504", param, onsucceed);
                } else {
                    console.log("fail");
                    return;
                }
            }
            return B.exec(callback, _BARCODE, "scene_504");
        },
        getDeviceInfo: function(param, onsucceed) {
            var callback = function(bridge) {
                if(typeof onsucceed == "function") {
                    bridge.callHandler("getDeviceInfo", param, onsucceed);
                } else {
                    console.log("fail");
                    return;
                }
            }
            return B.exec(callback, _BARCODE, "getDeviceInfo");
        },
        sceneInfo: function(onsucceed) {
            var callback = function(bridge) {
                bridge.callHandler("sceneInfo", onsucceed);
            }
            return B.exec(callback, _BARCODE, "sceneInfo");
        },
        getDeviceList: function(onsucceed) {
            var callback = function(bridge) {
                bridge.callHandler("getDeviceList", onsucceed);
            }
            return B.exec(callback, _BARCODE, "getDeviceList");
        },
        getCurrentGatewayID: function(onsucceed) {
            var callback = function(bridge) {
                bridge.callHandler("getCurrentGatewayID", null, onsucceed);
            }
            return B.exec(callback, _BARCODE, "getCurrentGatewayID");
        },
        getCurrentAppID: function(onsucceed) {
            var callback = function(bridge) {
                bridge.callHandler("getCurrentAppID", onsucceed);
            }
            return B.exec(callback, _BARCODE, "getCurrentAppID");
        },
        controlDevice: function(param, onsucceed) {
            var callback = function(bridge) {
                if(typeof onsucceed == "function") {
                    bridge.callHandler("controlDevice", param, onsucceed);
                } else {
                    console.log("fail");
                    return;
                }
            }
            return B.exec(callback, _BARCODE, "controlDevice");
        },
        newDataRefresh: function(onsucceed) {
            var callback = function(bridge) {
                if(typeof onsucceed == "function") {
                    bridge.registerHandler("newDataRefresh",onsucceed)
                } else {
                    console.log("fail");
                    return;
                }
            }
            return B.exec(callback, _BARCODE, "newDataRefresh");
        },
        getAlarmList: function(param, onsucceed) {
            var callback = function(bridge) {
                if(typeof onsucceed == "function") {
                    bridge.callHandler("getAlarmList", param, onsucceed);
                } else {
                    console.log("fail");
                    return;
                }
            }
            return B.exec(callback, _BARCODE, "getAlarmList");
        }
    };
    window.plus.gatewayCmd = gatewayCmd;
})();