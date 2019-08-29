//model   数据操作
MVC.M = {
    getDeviceInfo: function () {
        plus.gatewayCmd.getDeviceInfo(function (result) {
            DEVICEINFO = result;
            DEVID = result.devID;
            GWID = result.gwID;
            APPID = result.appID;
            endPointNum = getUrlParam("endPointNum");
            bindSceneID = getUrlParam("bindSceneID");
            if (bindSceneID == '-1') {
                article.setAttribute('binded', "on");
            }
            //获取场景列表
            plus.gatewayCmd.getSceneList(function (result) {
                if (result != undefined) {
                    if (result.length == 0) {
                        window.location = "SceneEmpty.html";
                    } else {
                        sceneList = result;
                        nav.innerHTML = '';
                        for (var i = 0; i < sceneList.length; i++) {
                            var scene = sceneList[i];
                            var sceneID = scene.sceneID;
                            var sceneName = scene.sceneName;
                            var sceneIcon = scene.sceneIcon;
                            sceneIcon = 'url(../../source/sceneIcon/scene_normal_' + sceneIcon + '.png)';
                            addLi(sceneID, sceneName, sceneIcon);
                        }
                    }

                }
            });
        });
    },

    /*
     * 500信息返回
     */
    rush_500: function (dID, gID) {
        plus.gatewayCmd.newDataRefresh(function (result) {
            if (result.cmd == "500" && dID == result.devID && gID == result.gwID) {

            } else if (result.cmd == "502" && dID == result.devID && gID == result.gwID) {

            } else if (result.cmd == "514" && dID == result.devID && gID == result.gwID) {

            }
        });
    },
    /*
     * 设置场景绑定
     */
    setBind: function (data) {
        var jsonData = {
            "cmd": "513",
            "gwID": GWID,
            "devID": DEVID,
            "appID": APPID,
            "mode": 1,
            "data": data
        };
        plus.gatewayCmd.controlDevice(jsonData, function (result) {
            console.log("result" + result);
        })
    },

    /*
     * 解绑
     */
    unBind: function (data) {
        var jsonData = {
            "cmd": "513",
            "gwID": GWID,
            "devID": DEVID,
            "mode": 3,
            "data": data
        };
        plus.gatewayCmd.controlDevice(jsonData, function (result) {
            console.log("result" + result);
        })
    },
};