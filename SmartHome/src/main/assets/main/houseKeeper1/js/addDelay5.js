$(document).ready(function() {
	if(browser.versions.android) {
		//延时安卓与ios样式调整
		addDelay1("#mobiscroll", 2,60)
	} else if(browser.versions.ios) {
		if($(document).width() == "1242" || $(document).width() == "1125"){
			addDelay1("#mobiscroll",4,96)
		}else if($(document).width() == "640"){
            addDelay1("#mobiscroll",4,51)
		}else{
			//添加延时页面ios及安卓样式不同进行调整
			addDelay1("#mobiscroll",4,60)
		}
	}
	function addDelay1(ele, row,high) {
		wheels = [];
		wheels[0] = {
			'温度': {}
		};
		for(var i = 20; i > 0; i--) {
			wheels[0]['温度'][20-i] = "-" + i;
		}
		for(var i = 0;i<=60;i++){
			wheels[0]['温度'][i + 20] = i
		}
		$(ele).scroller({
			theme: 'mobiscroll',
			display: 'bottom',
			rows: row,
			height: high,
            defaultIndex: 46,
			wheels: wheels,
			onShow: function(event, inst) {
				var str = "<div class='choose'><span>℃</span></div>"
				$(".dwwr").append(str)
				$(".mobiscroll").css({"top":"50%","-webkit-transform":"translateY(-50%)"})
			}
		});
		$(ele).mobiscroll('show');
	}
})