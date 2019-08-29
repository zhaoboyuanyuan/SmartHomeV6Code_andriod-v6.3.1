var browser={
    versions:function(){
        var u = navigator.userAgent, app = navigator.appVersion;
        return {
            trident: u.indexOf('Trident') > -1, //IE内核
            presto: u.indexOf('Presto') > -1, //opera内核
            webKit: u.indexOf('AppleWebKit') > -1, //苹果、谷歌内核
            gecko: u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1,//火狐内核
            mobile: !!u.match(/AppleWebKit.*Mobile.*/), //是否为移动终端
            ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), //ios终端
            android: u.indexOf('Android') > -1 || u.indexOf('Linux') > -1, //android终端或者uc浏览器
            iPhone: u.indexOf('iPhone') > -1 , //是否为iPhone或者QQHD浏览器
            iPad: u.indexOf('iPad') > -1, //是否iPad
            webApp: u.indexOf('Safari') == -1, //是否web应该程序，没有头部与底部
            weixin: u.indexOf('MicroMessenger') > -1, //是否微信 （2015-01-22新增）
            qq: u.match(/\sQQ/i) == " qq" //是否QQ
        };
    }(),
    language:(navigator.browserLanguage || navigator.language).toLowerCase()
}
;(function() {
	if(!window.plus) {
		var p = function() {};
		var da = this;
		p.prototype = {
			plusReady: function(Callee, bridge) {
				if(!this.OnReadyCallees) {
					this.OnReadyCallees = [];
				}
				this.OnReadyCallees[this.OnReadyCallees.length] = Callee;
				var callback = function(bridge) {
					if(browser.versions.android){
						bridge.init(function(message, responseCallback) {})
					}
					bridge.registerHandler("onReady", function(data) {
						console.log("初始化成功")
						plus.onReady();
					})
				}
				window.plus.bridge.exec(callback);
			},
			onReady: function() {
				if(this.OnReadyCallees) {
					this.OnReadyCallees.forEach(function(item, index, array) {
						if(typeof item == "function") {
							item();
						}
					});
				}
			},
		};
		window.plus = new p();
	}
})();
(function() {
	if(!plus.bridge) {
		var b = function() {};
		b.prototype = {
			exec: function(callback, interfaceName, mathodName) {
				try {
					this.setupWebViewJavascriptBridge(callback);
				} catch(e) {
					console.log(e);
				}
			},
			setupWebViewJavascriptBridge: function(callback) {
				if(window.WebViewJavascriptBridge) {
					return callback(WebViewJavascriptBridge);
				} else {
					document.addEventListener(
						'WebViewJavascriptBridgeReady',
						function() {
							callback(WebViewJavascriptBridge)
						},
						false
					);
				}
				if(window.WVJBCallbacks) { return window.WVJBCallbacks.push(callback); }
				window.WVJBCallbacks = [callback];
				var WVJBIframe = document.createElement('iframe');
				WVJBIframe.style.display = 'none';
				WVJBIframe.src = 'https://__bridge_loaded__';
				document.documentElement.appendChild(WVJBIframe);
				setTimeout(function() { document.documentElement.removeChild(WVJBIframe) }, 0)
			}
		};
		window.plus.bridge = new b();
	}
})();