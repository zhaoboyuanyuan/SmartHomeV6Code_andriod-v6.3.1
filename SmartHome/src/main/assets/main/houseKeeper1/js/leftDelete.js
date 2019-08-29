/**
 * jquery插件：向左滑动删除动效
 * 使用方法：$('.itemWipe').leftDelete();
 * 参数：左滑的距离百分比
 */
;
(function($) {
	$.fn.leftDelete = function(option) {
		var defaults = {      
			marginLeft: '0%', //删除元素
   		};
		var opts = $.extend({},defaults,option); //配置选项
		var initX; //触摸位置
		var initY;
		var maxLeft;
		var self;
		var lastLeft = null;
		var info = window.sysfun;
		info.init("V6Housekeeper");
		var isClick = 0;
		$(this).on('touchstart', function(event) {
			var obj = this;
			self = $(this)
			maxLeft = event.changedTouches[0].pageX;
			initX = event.targetTouches[0].pageX;
			initY = event.targetTouches[0].pageY;
		})
		$(this).on('touchmove', function(event) {
			initX = event.targetTouches[0].pageX - initX;
			initY = event.targetTouches[0].pageY - initY;
			if(Math.abs(initX) > Math.abs(initY)){
				event.preventDefault();
			}
		})
		$(this).on('touchend', function(event) {
			var changeL = event.changedTouches[0].pageX - maxLeft;
			if(changeL < -60){
				if(lastLeft == null){
					$(self).animate({marginLeft:opts.marginLeft},200);
					$(self).find("i").css("display","none")
					lastLeft = self;
				}else{
					$(lastLeft).animate({marginLeft:"0"},200);
					$(lastLeft).find("i").css("display","block")
					$(self).find("i").css("display","none")
					$(self).animate({marginLeft:opts.marginLeft},200);
					lastLeft = self;
				}
			}else if(changeL > 20){
				$(self).animate({marginLeft:"0"},500);
				$(self).find("i").css("display","block")
				lastLeft = null;
			}
		})
        $(".deleteCondition").on("touchend", function(event) {
            event.preventDefault();
            isClick = "true"
            info.setItem("isClick",isClick)
            var index = $(this).parent().index();
            var type = $(this).parent().attr("data-type");
            if(info.getItem("conditionArray")!= null && index != -1){
                var data1 = JSON.parse(info.getItem("conditionArray"));
                console.log(data1);
                data1.splice(index, 1);
                if(data1.length == 1){
                    $("#conditionTask").removeClass("cir_fix")
                    info.setItem("relative","");
                    $(".conditionRelation").remove();
				}else if(data1.length == 0){
                    info.setItem("relative","");
                    $(".conditionRelation").remove();
                    $("#conditionTask").addClass("cir_fix")
				}else{
                    if(info.getItem("relative") === "or"){
                        $(".conditionList li").find("span").append("<i style='color:#8dd652;' class='conditionRelation'>"+lang_or+"</i>");
                    }else if(info.getItem("relative") === "and"){
                        $(".conditionList li").find("span").append("<i style='color:#8dd652;' class='conditionRelation'>"+lang_and+"</i>");
                    }
					// $(".conditionList li").eq(-2).find("span").find("i").remove();
                    $(".conditionList li").eq(-1).find("span").find("i").remove();
                    $("#conditionTask").removeClass("cir_fix")
					if(index == data1.length){
                        $(".conditionList li").eq(-2).find("span").find("i").remove();
					}
                    $(".conditionRelation").on("click",function(){
                        info.setItem("relativeClick","toggle");
                        $(".relationDialog").show();
                    });
				}
                var dataStr1 = JSON.stringify(data1);
                info.setItem("conditionArray", dataStr1);
                if(type == 1){
                    var num1 = JSON.parse(info.getItem("timeCount")) - 1;
                    info.setItem("timeCount",num1);
				}else if(type == 0){
                    var num2 = JSON.parse(info.getItem("sceneCount")) - 1;
                    info.setItem("sceneCount",num2);
				}
            }
            $(this).parent().remove()
        })
		$(".deleteLdArr").on("touchend", function(event) {
			event.preventDefault();
			isClick = "true"
			info.setItem("isClick",isClick)
			var index = $(this).parent().index()
			if(info.getItem("ldArr")!= null && index != -1){
				var data1 = JSON.parse(info.getItem("ldArr"))
				console.log(data1)
				data1.splice(index, 1)
				var dataStr1 = JSON.stringify(data1)
				info.setItem("ldArr", dataStr1)
			}
			$(this).parent().remove()
		})
		$(".deleteAction").on("touchend", function(event) {
			event.preventDefault();
			isClick = "true"
			info.setItem("isClick",isClick)
			var index = $(this).parent().index()
			//actionArray
			if(info.getItem("actionArray")!=null && index != -1){
				var data = info.getItem("actionArray")
				var jsonData = JSON.parse(data)
				jsonData.splice(index, 1)
				var dataStr = JSON.stringify(jsonData)
				info.setItem("actionArray", dataStr);
			}
			$(this).parent().remove()
			
		})
		//链式返回
		return this;
	};
})(jQuery);

