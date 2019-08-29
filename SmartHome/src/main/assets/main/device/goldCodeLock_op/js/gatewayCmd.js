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
        getSceneList: function(onsucceed){
            var callback = function(bridge){
                bridge.callHandler("getSceneList", onsucceed);
            }
            return B.exec(callback, _BARCODE, "getSceneList");
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
        //保存报警设置
        saveUserPushInfo: function(params,onsucceed){
            var callback = function(bridge) {
                bridge.callHandler("saveUserPushInfo", params, onsucceed)
            }
            return B.exec(callback, _BARCODE, "saveUserPushInfo");
		},
		//查询所有报警设置
        queryUserPushInfo: function(params,onsucceed) {
            var callback = function(bridge) {
                bridge.callHandler("queryUserPushInfo", params, onsucceed);
            }
            return B.exec(callback, _BARCODE, "queryUserPushInfo");
        },
        openShare: function(params,onsucceed) {
            var callback = function(bridge) {
                bridge.callHandler("openShare", params, onsucceed);
            }
            return B.exec(callback, _BARCODE, "openShare");
        },
        isLockClick: function(onsucceed) {
            var callback = function(bridge) {
                bridge.callHandler("isLockClick", onsucceed);
            }
            return B.exec(callback, _BARCODE, "isLockClick");
        }
	};
	window.plus.gatewayCmd = gatewayCmd;
})();

