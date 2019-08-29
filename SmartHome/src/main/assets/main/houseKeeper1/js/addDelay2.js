function load(){
	if(browser.versions.android) {
		//延时安卓与ios样式调整
		addDelay("#demo", 2,"top","10.8rem",languageUtil.getResource("circumstances_txt27"),languageUtil.getResource("delay_sec"),60)
	} else if(browser.versions.ios) {
		var top = "10.8rem";
		if(window.iphoneXTimeTop10_8){
			top = window.iphoneXTimeTop10_8
		}
		if($(document).width() == "1242" || $(document).width() == "1125"){
			addDelay("#demo", 4,"top",top,languageUtil.getResource("circumstances_txt27"),languageUtil.getResource("delay_sec"),96)
		}else if($(document).width() == "640"){
            addDelay("#demo", 4,"top",top,languageUtil.getResource("circumstances_txt27"),languageUtil.getResource("delay_sec"),51)
		}else{
			//添加延时页面ios及安卓样式不同进行调整
			addDelay("#demo", 4,"top",top,languageUtil.getResource("circumstances_txt27"),languageUtil.getResource("delay_sec"),60)
		}
	}
	$("#demo").on("touchstart", function() {
        $('#demo').blur()
		var state = $("#switchContorl .on").attr("data-state")
		if(state == "off") {
			$(".mobiscroll").show()
			$(".slideDown").show()
			$("#switchContorl .on").attr("data-state", "on")
			$("#switchContorl").css("background", "#8dd652")
			$("#switchContorl i").animate({
				left: '60%'
			}, 200)
			$(".slideDown").animate({
				top: "24rem"
			}, 0)
		} else {
			$(".mobiscroll").hide()
			$(".slideDown").hide()
			$("#switchContorl .on").attr("data-state", "off")
			$("#switchContorl").css("background", "#aaa")
			$("#switchContorl i").animate({
				left: '10%'
			}, 200)
		}
	})

	function addDelay(ele, row, direction, num, min, second,high) {
		wheels = [];
		wheels[0] = {
			'分钟': {}
		};
		wheels[1] = {
			'秒': {}
		};
		for(var i = 0; i < 100; i++) {
			if(i < 60) wheels[1]['秒'][i] = (i < 10) ? ('0' + i) : i;
			wheels[0]['分钟'][i] = (i < 10) ? ('0' + i) : i;
		}
		$(ele).scroller({
			theme: 'mobiscroll',
			display: 'inline',
			rows: row,
			height: high,
			timeWheels: 'HHii',
			timeFormat: 'HH:ii',
			wheels: wheels,
			onShow: function() {
				var str = "<div class='choose'><span>：</span><span></span></div>"
				$(".dwwr").append(str);
				$(".mobiscroll").css(direction,num);
				$(".dwv").attr("id","bing");
                $("#switchContorl .on").attr("data-state", "off");
                $("#switchContorl").css("background", "#aaa");
                $(".slideDown").hide();
                $("#switchContorl i").animate({
                    left: '10%'
                }, 200);
				$(".slideDown").animate({
					top: "24rem"
				}, 0)
			}
		});
        $(".mobiscroll").hide()
        $(".slideDown").hide()
	}
}

addLoadEvent(load);