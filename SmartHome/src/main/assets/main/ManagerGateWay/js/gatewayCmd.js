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
		getDeviceList: function(onsucceed){
			var callback = function(bridge){
				bridge.callHandler("getDeviceList", onsucceed);
			}
			return B.exec(callback, _BARCODE, "getDeviceList"); 
		},
		// 获取网关默认名称
		managerGWName: function(params,onsucceed){
			var callback = function(bridge){
				bridge.callHandler("managerGWName",params, onsucceed);
			}
			return B.exec(callback, _BARCODE, "managerGWName"); 
		},
		// 获取网关信息
		getCurrentGWInfo: function(onsucceed){
			var callback = function(bridge){
				bridge.callHandler("getCurrentGWInfo", onsucceed);
			}
			return B.exec(callback, _BARCODE, "getCurrentGWInfo"); 
		},
		// 获取子机下的设备
		getSubGWDevices: function(data,onsucceed){
			var callback = function(bridge){
				bridge.callHandler("getSubGWDevices",data, onsucceed);
			}
			return B.exec(callback, _BARCODE, "getSubGWDevices"); 
		},
        // 跳转到wlan外置浏览器
        goToWlan: function(onsucceed){
            var callback = function(bridge){
                bridge.callHandler("goToWlan", onsucceed);
            }
            return B.exec(callback, _BARCODE, "goToWlan");
        },
	};
	window.plus.gatewayCmd = gatewayCmd;
})();

