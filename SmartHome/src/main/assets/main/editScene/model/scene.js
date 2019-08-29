(function() {
	var M = function() {}
	M.prototype = {
		send_add_503: {
			"cmd": "503",
			"gwID": "gwID",
			"mode": 1,
			"sceneID": "", //自增
			"name": "name", //必填
			"icon": "1", //必填
			"status": "1",

		},
		send_change_503: {
			"cmd": "503",
			"gwID": "gwID",
			"mode": 2,
			"sceneID": "", //必填
			"name": "name", //选择
			"icon": "1", //选择
			"status": "1",//选择

		},
		send_delete_503: {
			"cmd": "503",
			"gwID": "gwID",
			"mode": "3",
			"sceneID": "", //必填
		},
		send_delete_504: {
			"cmd": "504",
			"gwID": "gwID",
			"appID": "appID",
		}
	}
	window.sceneModel = new M();
})()

