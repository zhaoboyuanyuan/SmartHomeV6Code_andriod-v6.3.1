//control  粘合model和view
MVC.C = {
	back: function() {
		plus.tools.back(function() {});
	},
	more: function() {
		plus.tools.more(DEVICEINFO, function() {});
	},
	initInfo: function() {
		MVC.V.bindClick();
		MVC.M.getDeviceInfo();
	},
};