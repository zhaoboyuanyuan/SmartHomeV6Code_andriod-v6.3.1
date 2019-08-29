$(document).ready(function() {
	if(browser.versions.android) {
		//延时安卓与ios样式调整
		addDelay1("#mobiscroll", 2, 60)
	} else if(browser.versions.ios) {
		if($(document).width() == "1242" || $(document).width() == "1125") {
			addDelay1("#mobiscroll", 4, 96)
		} else if($(document).width() == "640") {
			//添加延时页面ios及安卓样式不同进行调整
			addDelay1("#mobiscroll", 4, 51)
		} else {
			//添加延时页面ios及安卓样式不同进行调整
			addDelay1("#mobiscroll", 4, 60)
		}
	}

	function addDelay1(ele, row, high) {
		wheels = [];
		wheels[0] = {
			'小时': {}
		};
		wheels[1] = {
			'分钟': {}
		};
		for(var i = 0; i < 60; i++) {
			if(i < 24) wheels[0]['小时'][i] = (i < 10) ? ('0' + i) : i;
			wheels[1]['分钟'][i] = (i < 10) ? ('0' + i) : i;
		}
		$(ele).scroller({
			theme: 'mobiscroll',
			display: 'bottom',
			rows: row,
			height: high,
			wheels: wheels,
			onShow: function(event, inst) {
                var str = "<div class='choose'><span>：</span><span></span></div>";
                $(".dwwr").append(str);
				if(window.iphoneXTimeTop) {
					$(".mobiscroll").css("top", window.iphoneXTimeTop)
				} else {
					$(".mobiscroll").css("top", "6.4rem")
				}

			}
		});
		// $(ele).mobiscroll('show')
	}
})