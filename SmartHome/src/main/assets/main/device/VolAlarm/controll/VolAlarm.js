//control  粘合model和view
MVC.C = {
	back: function() {
		plus.tools.back(function() {});
	},
	more: function() {
		plus.tools.more(moreConfig, function() {});
	},
	initInfo: function() {
		MVC.V.bindClick();
		MVC.M.getDeviceInfo();
	},
	sendCmd: function(data) {
		MVC.M.sendCmd(data);
	}
};