(function() {
	var _BARCODE = 'wuliancloud',
		B = window.plus.bridge;
	var wuliancloud = {
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
		}
	};
	window.plus.wuliancloud = wuliancloud;
})();