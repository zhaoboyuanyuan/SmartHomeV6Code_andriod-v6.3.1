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
			mm:'',
   		};
		var opts = $.extend({},defaults,option); //配置选项
		var initX; //触摸位置
		var initY;
		var maxLeft;
		var self;
		var lastLeft = null;
		var isClick = 0;
		var changeL = 0;
		$(this).on('touchstart', function(event) {
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
		$(".delete").on("click", function(e) {
			leftDeleteAction($(this).parents("dl").attr("gwID"));
//          $(this).parents("dl").remove()
//			if($(".lists dl").length == 0 || $(".lists dl") == null || $(".lists dl") == undefined){
//              $(".dropload-down").hide();
//              $(".noMission").show();
//              $(".getMission").hide();
//			}else{
//              opts.mm.resetload()
//              nextPage()
//			}
		})
		//链式返回
		return this;
	};
})(jQuery);

