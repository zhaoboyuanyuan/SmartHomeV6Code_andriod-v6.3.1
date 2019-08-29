(function() {
	var _BARCODE = 'gatewayCmd',
		B = window.plus.bridge;
	var gatewayCmd = {
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
		getDeviceInfo: function(onsucceed) {
			var callback = function(bridge) {
				bridge.callHandler("getDeviceInfo", onsucceed);
			}
			return B.exec(callback, _BARCODE, "getDeviceInfo");
		},
		controlDevice: function(params,onsucceed) {
			var callback = function(bridge) {
				bridge.callHandler("controlDevice", params, onsucceed)
			}
			return B.exec(callback, _BARCODE, "controlDevice");
		},
        WLHttpGetBase: function(params,onsucceed) {
            var callback = function(bridge) {
                bridge.callHandler("WLHttpGetBase", params, onsucceed)
            }
            return B.exec(callback, _BARCODE, "WLHttpGetBase");
        },
        WLHttpPostBase: function(params,onsucceed) {
            var callback = function(bridge) {
                bridge.callHandler("WLHttpPostBase", params, onsucceed)
            }
            return B.exec(callback, _BARCODE, "WLHttpPostBase");
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

