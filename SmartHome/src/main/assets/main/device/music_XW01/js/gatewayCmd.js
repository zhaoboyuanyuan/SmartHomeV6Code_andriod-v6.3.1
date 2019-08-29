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
                    bridge.registerHandler("newDataRefresh", onsucceed);
                } else {
                    console.log("fail");
                    return;
                }
            }
            return B.exec(callback, _BARCODE, "newDataRefresh");
        }
	};
	window.plus.gatewayCmd = gatewayCmd;
})();

//注册回调
(function() {
	var _BARCODE = 'gatewayCmdRush',
		B = window.plus.bridge;
	var gatewayCmdRush = {
		houserkeep_507_rush: function(param, onsucceed) {
			var callback = function(bridge) {
				if(typeof onsucceed == "function") {
					bridge.registerHandler("houserkeep_507_rush", param, onsucceed);
				} else {
					console.log("fail");
					return;
				}
			}
			return B.exec(callback, _BARCODE, "houserkeep_507_rush");
		},
		houserkeep_508_rush: function(param, onsucceed) {
			var callback = function(bridge) {
				if(typeof onsucceed == "function") {
					bridge.registerHandler("houserkeep_508_rush", param, onsucceed);
				} else {
					console.log("houserkeep_508_rush_fail");
					return;
				}
			}
			return B.exec(callback, _BARCODE, "houserkeep_508_rush");
		},
		scene_503_rush: function(param, onsucceed) {
			var callback = function(bridge) {
				if(typeof onsucceed == "function") {
					bridge.registerHandler("scene_503_rush", param, onsucceed);
				} else {
					console.log("fail");
					return;
				}
			}
			return B.exec(callback, _BARCODE, "scene_503_rush");
		},
		scene_504_rush: function(param, onsucceed) {
			var callback = function(bridge) {
				if(typeof onsucceed == "function") {
					bridge.registerHandler("scene_504_rush", param, onsucceed);
				} else {
					console.log("fail");
					return;
				}
			}
			return B.exec(callback, _BARCODE, "scene_504_rush");
		},
		newDataRefresh: function(onsucceed) {
			var callback = function(bridge) {
				if(typeof onsucceed == "function") {
					bridge.registerHandler("newDataRefresh", onsucceed);
				} else {
					console.log("fail");
					return;
				}
			}
			return B.exec(callback, _BARCODE, "newDataRefresh");
		}
	};
	window.plus.gatewayCmdRush = gatewayCmdRush;
})();