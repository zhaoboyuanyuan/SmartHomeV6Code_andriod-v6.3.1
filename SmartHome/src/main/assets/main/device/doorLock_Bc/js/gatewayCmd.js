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
		verifyTouchID: function(onsucceed) {
			var callback = function(bridge) {
				bridge.callHandler("verifyTouchID", null, onsucceed);
			}
			return B.exec(callback, _BARCODE, "verifyTouchID");
		},
		supportTouchID: function(onsucceed) {
			var callback = function(bridge) {
				bridge.callHandler("supportTouchID", null, onsucceed);
			}
			return B.exec(callback, _BARCODE, "supportTouchID");
		},
		getSceneList: function(onsucceed){
			var callback = function(bridge){
				bridge.callHandler("getSceneList", onsucceed);
			}
			return B.exec(callback, _BARCODE, "getSceneList"); 
		},
		BcCameraList: function(onsucceed){
			var callback = function(bridge){
				bridge.callHandler("BcCameraList",onsucceed);
			}
			return B.exec(callback, _BARCODE, "BcCameraList"); 
		},
		toWiFiSetting: function(onsucceed){
			var callback = function(bridge){
				bridge.callHandler("toWiFiSetting",onsucceed);
			}
			return B.exec(callback, _BARCODE, "toWiFiSetting"); 
		},
		BcCameraStart:function(params,onsucceed){
			var callback=function(bridge){
				bridge.callHandler("BcCameraStart",params,onsucceed);
			}
			return B.exec(callback, _BARCODE, "BcCameraStart"); 
		},
		bindingScene:function(params,onsucceed){
			var callback=function(bridge){
				bridge.callHandler("bindingScene",params,onsucceed);
			}
			return B.exec(callback, _BARCODE, "bindingScene"); 
		},
		cmd521: function(params,onsucceed){
			var callback = function(bridge){
				bridge.callHandler("cmd521",params,onsucceed);
			}
			return B.exec(callback, _BARCODE, "cmd521"); 
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
        }
	};
	window.plus.gatewayCmd = gatewayCmd;
})();

