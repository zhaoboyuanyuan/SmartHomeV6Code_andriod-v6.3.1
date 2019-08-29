(function() {
	var _BARCODE = 'gatewayCmd',
		B = window.plus.bridge;
	var gatewayCmd = {
        getGatewayID: function(onsucceed) {
			var callback = function(bridge) {
				bridge.callHandler("getGatewayID", null, onsucceed);
			}
			return B.exec(callback, _BARCODE, "getGatewayID");
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
		toNativeController: function(params,onsucceed) {
			var callback = function(bridge) {
				bridge.callHandler("toNativeController", params, onsucceed)
			}
			return B.exec(callback, _BARCODE, "toNativeController");
		},
		getGroupList: function(onsucceed) {
			var callback = function(bridge) {
				bridge.callHandler("getGroupList", null, onsucceed)
			}
			return B.exec(callback, _BARCODE, "getGroupList");
		},
		setDeviceInfo: function(param,onsucceed) {
			var callback = function(bridge) {
				bridge.callHandler("setDeviceInfo", param, onsucceed)
			}
			return B.exec(callback, _BARCODE, "setDeviceInfo");
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
		gatewayAddDevice: function(param,onsucceed) {
            onsucceed = onsucceed || param;
			var callback = function(bridge) {
				if(typeof onsucceed == "function") {
					if(typeof param == "function"){
                        bridge.callHandler("gatewayAddDevice",onsucceed)
					}else{
                        bridge.callHandler("gatewayAddDevice",param,onsucceed)
					}
				} else {
					console.log("fail");
					return;
				}
			}
			return B.exec(callback, _BARCODE, "gatewayAddDevice");
		},
		gatewayAddDeviceByType: function(param,onsucceed) {
            onsucceed = onsucceed || param;
			var callback = function(bridge) {
				if(typeof onsucceed == "function") {
					if(typeof param == "function"){
                        bridge.callHandler("gatewayAddDeviceByType",onsucceed)
					}else{
                        bridge.callHandler("gatewayAddDeviceByType",param,onsucceed)
					}
				} else {
					console.log("fail");
					return;
				}
			}
			return B.exec(callback, _BARCODE, "gatewayAddDeviceByType");
		},
		getGatewayID: function(param,onsucceed) {
            onsucceed = onsucceed || param;
			var callback = function(bridge) {
				if(typeof onsucceed == "function") {
					if(typeof param == "function"){
                        bridge.callHandler("getGatewayID",onsucceed)
					}else{
                        bridge.callHandler("getGatewayID",param,onsucceed)
					}
				} else {
					console.log("fail");
					return;
				}
			}
			return B.exec(callback, _BARCODE, "gatewayAddDevice");
		},
		isBindGw: function(onsucceed) {
			var callback = function(bridge) {
				bridge.callHandler("isBindGw", onsucceed);
			}
			return B.exec(callback, _BARCODE, "isBindGw");
		},
		goToBind: function(onsucceed) {
            var callback = function(bridge) {
                bridge.callHandler("goToBind", onsucceed);
            }
            return B.exec(callback, _BARCODE, "goToBind");
        },
        getQRCode: function(params,onsucceed) {
            var callback = function(bridge) {
                bridge.callHandler("getQRCode", params, onsucceed)
            }
            return B.exec(callback, _BARCODE, "getQRCode");
        },
        bindingDevice: function(params,onsucceed) {
            var callback = function(bridge) {
                bridge.callHandler("bindingDevice", params, onsucceed)
            }
            return B.exec(callback, _BARCODE, "bindingDevice");
        },
        // 获取网关信息
        getCurrentGWInfo: function(onsucceed){
            var callback = function(bridge){
                bridge.callHandler("getCurrentGWInfo", onsucceed);
            }
            return B.exec(callback, _BARCODE, "getCurrentGWInfo");
        },
        checkGatewaySupportDevice:function(params,onsucceed) {
            var callback = function(bridge) {
                bridge.callHandler("checkGatewaySupportDevice", params, onsucceed)
            }
            return B.exec(callback, _BARCODE, "checkGatewaySupportDevice");
        },
        V5ToV6CheckGWPassword:function(params,onsucceed) {
            var callback = function(bridge) {
                bridge.callHandler("V5ToV6CheckGWPassword", params, onsucceed)
            };
            return B.exec(callback, _BARCODE, "V5ToV6CheckGWPassword");
        },
        V5ToV6GetGWversion:function(params,onsucceed) {
            var callback = function(bridge) {
                bridge.callHandler("V5ToV6GetGWversion", params, onsucceed)
            };
            return B.exec(callback, _BARCODE, "V5ToV6GetGWversion");
        },
        V5ToV6UpGradeGW:function(params,onsucceed) {
            var callback = function(bridge) {
                bridge.callHandler("V5ToV6UpGradeGW", params, onsucceed)
            };
            return B.exec(callback, _BARCODE, "V5ToV6UpGradeGW");
        }
	};
	window.plus.gatewayCmd = gatewayCmd;
})();

