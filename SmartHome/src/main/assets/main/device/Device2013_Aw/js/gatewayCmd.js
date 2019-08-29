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
        getSceneList: function(onsucceed){
            var callback = function(bridge){
                bridge.callHandler("getSceneList", onsucceed);
            }
            return B.exec(callback, _BARCODE, "getSceneList");
        },
        getDeviceList: function(onsucceed){
            var callback = function(bridge){
                bridge.callHandler("getDeviceList", onsucceed);
            }
            return B.exec(callback, _BARCODE, "getDeviceList");
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
        jumpScene: function(onsucceed) {
            var callback = function(bridge) {
                bridge.callHandler("jumpScene", null, onsucceed)
            }
            return B.exec(callback, _BARCODE, "jumpScene");
        }
    };
    window.plus.gatewayCmd = gatewayCmd;
})();

