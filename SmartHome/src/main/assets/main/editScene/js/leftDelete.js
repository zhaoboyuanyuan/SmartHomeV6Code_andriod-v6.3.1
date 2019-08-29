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
		info.init("V6Scene");
		var isClick = 0;
		$(this).on('touchstart', function(event) {
            // event.stopPropagation();
			var obj = this;
			self = $(this)
			maxLeft = event.changedTouches[0].pageX;
			initX = event.targetTouches[0].pageX;
			initY = event.targetTouches[0].pageY;
		})
		$(this).on('touchmove', function(event) {
            // event.stopPropagation();
			initX = event.targetTouches[0].pageX - initX;
			initY = event.targetTouches[0].pageY - initY;
			if(Math.abs(initX) > Math.abs(initY)){
				event.preventDefault();
			}
		})
		$(this).on('touchend', function(event) {
            // event.stopPropagation();
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
		$(".delete").on("click", function(event) {
            // event.stopPropagation();
			isClick = "true"
			info.setItem("isClick",isClick)
			//删除场景任务
			deleteDeviceTask($(this))
		})
		//链式返回
		return this;
	};
})(jQuery);

