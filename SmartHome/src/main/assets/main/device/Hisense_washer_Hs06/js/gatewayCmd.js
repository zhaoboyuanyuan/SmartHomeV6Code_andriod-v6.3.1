(function() {
	var _BARCODE = 'gatewayCmd',
		B = window.plus.bridge;
	var gatewayCmd = {
		getCurrentAppID: function(onsucceed) {
			var callback = function(bridge) {
				bridge.callHandler("getCurrentAppID", onsucceed);
			}
			return B.exec(callback, _BARCODE, "getCurrentAppID");
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
        }
	};
	window.plus.gatewayCmd = gatewayCmd;
})();

