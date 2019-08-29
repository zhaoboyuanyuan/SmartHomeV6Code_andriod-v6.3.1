(function(window,document){
	var Animation = function(canvas){
        this.init(canvas);
	}
    Animation.prototype = {
        init:function(canvas){
            this.canvas = canvas;
            this.r4;
            this.r10;
            this.r15;
            this.r20;
            this.r25;
            this.r30;
            this.r40;
            this.r45;
            this.r50;
            this.r60;
            this.r65;
            this.r70;
            this.r75;
            this.r140;
            this.openTimer;
            this.closeTimer;

            this.ctx;
            this.midX;
            this.midY;
		},
        initCanvas:function(canvas) {
            this.ctx = canvas.getContext('2d');
			var canvas_rect = canvas.getBoundingClientRect();
			//实际画布大小 px
			var width = canvas_rect.width;
			var height = canvas_rect.height;
			//iphone6画布大小 px
			var TCanvasW = 375 * 0.9 * 2;
			//各个屏幕的比例计算
			var resize = width / TCanvasW;
			//设置画布大小
			canvas.setAttribute("width", width);
			canvas.setAttribute("height", height);
			//计算中点
            this.midX = parseInt(width) / 2;
            this.midY = height / 2 - 10;

            this.r4 = 4 * resize;
            this.r10 = 10 * resize;
            this.r15 = 15 * resize;
            this.r20 = 20 * resize;
            this.r25 = 25 * resize;
            this.r30 = 30 * resize;
            this.r40 = 40 * resize;
            this.r45 = 45 * resize;
            this.r50 = 50 * resize;
            this.r60 = 60 * resize;
            this.r65 = 65 * resize;
            this.r70 = 70 * resize;
            this.r75 = 75 * resize;
            this.r140 = 140 * resize;
			ctx.clearRect(0,0,width,height);
    	},
        openAnimation: function () {
            this.initCanvas(this.canvas);
			//画两根竖线
            this.ctx.beginPath();

            this.ctx.lineCap = 'round';
            this.ctx.moveTo(this.midX + r20, this.midY - r50);
            this.ctx.lineTo(this.midX + r20, this.midY + r50);
            this.ctx.moveTo(this.midX - r20, midY - r50);
            this.ctx.lineTo(this.midX - r20, midY + r50);
            this.ctx.lineWidth = r10;
            this.ctx.strokeStyle = "#8dd652";
            this.ctx.stroke();

            this.ctx.closePath();

			//画一根横线
            this.ctx.beginPath();
            this.ctx.moveTo(this.midX - r140, midY + r70);
            this.ctx.lineTo(this.midX + r140, midY + r70);
            this.ctx.lineWidth = r4;
            this.ctx.strokeStyle = "#8dd652";
            this.ctx.stroke();

            this.ctx.closePath();

			//动画箭头
			this.drawOpen(0, ctx, this.midX, midY);

    	},
        //打开
        drawOpen:function (count, ctx, midX, midY) {
			count++;
			this.getRightOpenAnimation(ctx, this.midX, midY, count);
            this.getLeftOpenAnimation(ctx, this.midX, midY, count);
			if(count > 24) {
				count = 0;
			}
			if(this.openTimer) {
				clearTimeout(this.openTimer);
			}
			if(this.closeTimer) {
				clearTimeout(this.closeTimer);
			}
            this.openTimer = setTimeout(function() {
                this.drawOpen(count, ctx, this.midX, midY);
			}, 75);
		},
        //右半边打开动画
        getRightOpenAnimation:function (ctx, midX, midY, count) {
            this.ctx.clearRect(this.midX + r30, midY - r25, this.midX - r30, r50);
            this.ctx.beginPath();
			for(var i = 0; i < 6; i++) {
                this.ctx.moveTo(this.midX + r60 + i * r40, midY - r25);
                this.ctx.lineTo(this.midX + r75 + i * r40, midY);
                this.ctx.lineTo(this.midX + r60 + i * r40, midY + r25);
                this.ctx.lineWidth = r4;
			}
			var lineargradienRight = this.ctx.createLinearGradient(this.midX + r50, midY, this.midX + r70 + 5.6 * r40, midY);

			lineargradienRight.addColorStop(0, 'white');
			lineargradienRight.addColorStop(0.04 * count, '#8dd652');
			lineargradienRight.addColorStop(1, 'white');

			ctx.strokeStyle = lineargradienRight;
			ctx.stroke();
			ctx.closePath();
		},
        getLeftOpenAnimation:function (ctx, midX, midY, count) {
        //左半边打开动画
			ctx.clearRect(0, midY - r25, this.midX - r30, r50);
			ctx.beginPath();
			for(var i = 0; i < 6; i++) {
				ctx.moveTo(this.midX - r60 - i * r40, midY - r25);
				ctx.lineTo(this.midX - r75 - i * r40, midY);
				ctx.lineTo(this.midX - r60 - i * r40, midY + r25);
				ctx.lineWidth = r4;
			}
			var lineargradienLeft = ctx.createLinearGradient(this.midX - r50, midY, this.midX - r70 - 5.6 * r40, midY);

			lineargradienLeft.addColorStop(0, 'white');
			lineargradienLeft.addColorStop(0.04 * count, '#8dd652');
			lineargradienLeft.addColorStop(1, 'white');

			ctx.strokeStyle = lineargradienLeft;
			ctx.stroke();
			ctx.closePath();
		},
        closeAnimation: function() {
			this.initCanvas(this.canvas);
			//画两根竖线
			ctx.beginPath();

			ctx.lineCap = 'round';
			ctx.moveTo(this.midX + r20, midY - r50);
			ctx.lineTo(this.midX + r20, midY + r50);
			ctx.moveTo(this.midX - r20, midY - r50);
			ctx.lineTo(this.midX - r20, midY + r50);
			ctx.lineWidth = r10;
			ctx.strokeStyle = "#8dd652";
			ctx.stroke();

			ctx.closePath();

			//画一根横线
			ctx.beginPath();
			ctx.moveTo(this.midX - r140, midY + r70);
			ctx.lineTo(this.midX + r140, midY + r70);
			ctx.lineWidth = r4;
			ctx.strokeStyle = "#8dd652";
			ctx.stroke();

			ctx.closePath();

			//动画箭头
			this.drawClose(0, ctx, this.midX, midY);
		},
        //关闭
        drawClose: function (count, ctx, midX, midY) {
			count++;
            this.getRightCloseAnimation(ctx, this.midX, midY, count);
            this.getLeftCloseAnimation(ctx, this.midX, midY, count);
			if(count > 24) {
				count = 0;
			}
			if(openTimer) {
				clearTimeout(openTimer);
			}
			if(closeTimer) {
				clearTimeout(closeTimer);
			}
			closeTimer = setTimeout(function() {
                this.drawClose(count, ctx, this.midX, midY);
			}, 75);
		},
		//右半边关闭动画
    	getRightCloseAnimation: function (ctx, midX, midY, count) {
			ctx.clearRect(this.midX + r30, midY - r25, this.midX - r30, r50);
			ctx.beginPath();
			for(var i = 0; i < 6; i++) {
				ctx.moveTo(this.midX + r75 + i * r40, midY - r25);
				ctx.lineTo(this.midX + r60 + i * r40, midY);
				ctx.lineTo(this.midX + r75 + i * r40, midY + r25);
				ctx.lineWidth = r4;
			}
			var lineargradienRight = ctx.createLinearGradient(this.midX + r50, midY, this.midX + r70 + 5.6 * r40, midY);

			lineargradienRight.addColorStop(0, 'white');
			lineargradienRight.addColorStop(1 - 0.04 * count, '#8dd652');
			lineargradienRight.addColorStop(1, 'white');

			ctx.strokeStyle = lineargradienRight;
			ctx.stroke();
			ctx.closePath();
		},
    	//左半边关闭动画
		getLeftCloseAnimation: function (ctx, midX, midY, count) {
			ctx.clearRect(0, midY - r25, this.midX - r30, r50);
			ctx.beginPath();
			for(var i = 0; i < 6; i++) {
				ctx.moveTo(this.midX - r75 - i * r40, midY - r25);
				ctx.lineTo(this.midX - r60 - i * r40, midY);
				ctx.lineTo(this.midX - r75 - i * r40, midY + r25);
				ctx.lineWidth = r4;
			}
			var lineargradienLeft = ctx.createLinearGradient(this.midX - r50, midY, this.midX - r70 - 5.6 * r40, midY);

			lineargradienLeft.addColorStop(0, 'white');
			lineargradienLeft.addColorStop(1 - 0.04 * count, '#8dd652');
			lineargradienLeft.addColorStop(1, 'white');

			ctx.strokeStyle = lineargradienLeft;
			ctx.stroke();
			ctx.closePath();
		},

    	//停止动画
    	stopAnimation: function() {
			if(openTimer) {
				clearTimeout(openTimer);
			}
			if(closeTimer) {
				clearTimeout(closeTimer);
			}
            this.initCanvas(this.canvas);
			var img = new Image();
			var ctx_var = ctx;
			var midX_var = this.midX;
			var r140_var = r140;
			var midY_var = midY;
			var r65_var = r65;
			img.onload = function(){
				ctx_var.drawImage(img,midX_var-r140_var,midY_var-r65_var,r140_var*2,r65_var*2);
			};
			img.src = "fonts/curtain_icon.png";
    	}
	}
    window.Animation = Animation;
}(window,document))






