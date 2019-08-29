function getUrlParam(key) {
	if(key == null || typeof(key) == "undefined" || key.length == 0) return "";
	var reg = new RegExp(key + "=([{}\\[\\]\"'a-zA-Z0-9%:,\\.\\-_]+)&{0,1}");
	var value = window.location.search;
	if(reg.test(value)) {
		value = RegExp.$1;
	} else {
		value = "";
	}
	return value;
}