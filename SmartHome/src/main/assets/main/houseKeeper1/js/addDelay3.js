function load(){
	if(browser.versions.android) {
		//延时安卓与ios样式调整
		addDelay("#start", 2,"bottom","0",60)
		addDelay("#end", 2,"bottom","0",60)
	} else if(browser.versions.ios) {
		if($(document).width() == "1242" || $(document).width() == "1125"){
			addDelay("#start", 4,"bottom","0",96)
			addDelay("#end", 4,"bottom","0",96)
		}else if($(document).width() == "640"){
            addDelay("#start", 4,"bottom","0",51)
            addDelay("#end", 4,"bottom","0",51)
        }else{
			//添加延时页面ios及安卓样式不同进行调整
			addDelay("#start", 4,"bottom","0",60)
			addDelay("#end", 4,"bottom","0",60)
		}
	}
	$(".choose_date li").on("touchstart", function() {
		$(this).css("background", "#f2faed").siblings().css("background", "#fff")
	})
    $('#demo').on("focus",function(){
        $(this).blur()
    })
	$(".switch_01").on("click", function() {
		var state = $(".switch_01 .on").attr("data-state")
		if(state == "off") {
			$(".switch_01 .on").attr("data-state", "on")
			$(".switch_01").css("background", "#8dd652")
			$(".switch_01 i").animate({
				left: '60%'
			}, 200)
		} else {
			$(".switch_01 .on").attr("data-state", "off")
			$(".switch_01").css("background", "#aaa")
			$(".switch_01 i").animate({
				left: '10%'
			}, 200)
		}
	})
	function addDelay(ele, row, direction, num, high) {
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
			timeWheels: 'HHii',
			timeFormat: 'HH:ii',
			wheels: wheels,
			onShow: function() {
                var str = "<div class='choose' style='border:0;background:none;'><span>：</span><span></span></div>"
                $(".dwwr").append(str);
				$(".mobiscroll").css(direction,num);
				$(".dwv").attr("id","bing")
			},
			onChange: function(){
				if($(".choose_date li").eq(1).css("background-color") == "rgb(242, 250, 237)") {
					var min = $(".dwv").html().split(" ")[1];
					min = min.length == 1 ? "0" + min : min;
					var hour = $(".dwv").html().split(" ")[0]
					hour = hour.length == 1 ? "0" + hour : hour;
					$(".choose_date li").eq(1).find("b").text(languageUtil.getResource("effectiveDate_txt02"))
					$(".choose_date li").eq(1).find("span").text(hour+":"+min)
					var startHour = $(".choose_date li").eq(0).find("span").html().split(":")[0]
					var startMin = parseInt($(".choose_date li").eq(0).find("span").html().split(":")[1]) + parseInt(startHour)*60;
					var endMin = parseInt(hour)*60 + parseInt(min);
					if(endMin <= startMin){
						$("#timeTitle").show();
					}else{
                        $("#timeTitle").hide();
					}
				}else if($(".choose_date li").eq(0).css("background-color") == "rgb(242, 250, 237)") {
					var min = $(".dwv").html().split(" ")[1];
					min = min.length == 1 ? "0" + min : min;
					var hour = $(".dwv").html().split(" ")[0]
					hour = hour.length == 1 ? "0" + hour : hour;
					$(".choose_date li").eq(0).find("b").text(languageUtil.getResource("effectiveDate_txt01"))
					$(".choose_date li").eq(0).find("span").text(hour+":"+min)
				}
			}
		});
	}
}

addLoadEvent(load);