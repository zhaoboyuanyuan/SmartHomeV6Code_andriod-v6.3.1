$(document).ready(function() {
	if(browser.versions.android) {
		//延时安卓与ios样式调整
		addDelay(2, 60)
	} else if(browser.versions.ios) {
		if($(document).width() == "1242" || $(document).width() == "1125") {
			addDelay(4, 96)
		} else if($(document).width() == "640") {
			//添加延时页面ios及安卓样式不同进行调整
			addDelay(4, 51)
		} else {
			//添加延时页面ios及安卓样式不同进行调整
			addDelay(4, 60)
		}
	}

	function addDelay(row, high) {
		wheels = [];
		wheels[0] = {
			'分钟': {}
		};
		wheels[1] = {
			'秒': {}
		};
		for(var i = 0; i < 90; i++) {
			if(i < 60) wheels[1]['秒'][i] = (i < 10) ? ('0' + i) : i;
			wheels[0]['分钟'][i] = (i < 10) ? ('0' + i) : i;
		}
		$(".switch").on("touchstart", function() {

			var state = $(".on").attr("data-state")
			if(state == "off") {
				$(".mobiscroll").css("display", "block")
				$(".on").attr("data-state", "on")
				$(".switch").css("background", "#8dd652")
				$(".switch i").animate({
					left: '60%'
				}, 200)
			} else {
				$(".mobiscroll").css("display", "none")
				$(".on").attr("data-state", "off")
				$(".switch").css("background", "#aaa")
				$(".switch i").animate({
					left: '10%'
				}, 200)
			}
		})
		$('#demo').scroller({
			theme: 'mobiscroll',
			display: 'bottom',
			rows: row,
			height: high,
			timeWheels: 'iiss',
			timeFormat: 'ii:ss',
			wheels: wheels,
			onShow: function(event, inst) {
				var str = "<div class='choose'><span>分钟</span><span>秒</span></div>"
				$(".dwwr").append(str)
				var top = "10.8rem";
				if(window.iphoneXTimeTop10_8) {
					top = window.iphoneXTimeTop10_8
				}
				$(".dw-bottom").css("top", top)
			}
		});
	}
})