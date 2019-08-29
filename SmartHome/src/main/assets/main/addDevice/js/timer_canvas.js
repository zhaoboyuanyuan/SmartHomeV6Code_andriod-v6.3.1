function picture(allTime) {
	var canvas = document.getElementById('canvas');
	canvas.width = canvas.parentNode.offsetWidth;
	canvas.height = canvas.parentNode.offsetHeight;
	canvasHeight = canvas.height;
	canvasWidth = canvas.width;
	var ctx = canvas.getContext('2d');
	ctx.clearRect(0, 0, canvasWidth, canvasHeight);
	if(canvas.getContext) {
		ctx.beginPath();
		//设置弧线的颜色为蓝色
		ctx.strokeStyle = "#8dd652";
		if(!window.v6sysfunc) {
			ctx.lineWidth = 3;
        }else {
        		ctx.lineWidth = 1;
        }

		//沿着坐标点(100,100)为圆心、半径为50px的圆的顺时针方向绘制弧线
		ctx.arc(canvasWidth / 2, canvasHeight / 2, canvasHeight/2-10, -0.5 * Math.PI, 1.5 * Math.PI, false);
		//按照指定的路径绘制弧线
		ctx.stroke();
		ctx.closePath();
		//开始一个新的绘制路径
		ctx.beginPath();
		//设置弧线的颜色为蓝色
		ctx.strokeStyle = "white";
		if(!window.v6sysfunc) {
			ctx.lineWidth = 4;
        }else {
        		ctx.lineWidth = 2;
        }
		//沿着坐标点(100,100)为圆心、半径为50px的圆的顺时针方向绘制弧线
		ctx.arc(canvasWidth / 2, canvasHeight / 2, canvasHeight/2-10, -0.5 * Math.PI, Math.PI * 2 * (1 - time_canvas / allTime) - Math.PI * 0.5, false);
		//按照指定的路径绘制弧线
		ctx.stroke();
		//设置字体样式
		var xxSize = 150/640*canvasWidth;
		ctx.font = 150/640*canvasWidth + "px Arial";
		//设置字体填充颜色
		ctx.fillStyle = "#8dd652";
		//从坐标点(50,50)开始绘制文字
		if(time_canvas < 10) {
			ctx.fillText(time_canvas + "s", canvasWidth / 2 - xxSize/2, canvasHeight / 2 + xxSize/4);
//			ctx.fillText(time_canvas + "s", canvasWidth / 2 - 15, canvasHeight / 2 + 5);
		} else if(time_canvas < 100) {
//			ctx.fillText(time_canvas + "s", canvasWidth / 2 - 20, canvasHeight / 2 + 5);
            ctx.fillText(time_canvas + "s", canvasWidth / 2 - xxSize*2/3, canvasHeight / 2 + xxSize/4);
		} else{
            ctx.fillText(time_canvas + "s", canvasWidth / 2 - xxSize, canvasHeight / 2 + xxSize/4);
		}
	}
}