var r4;
var r10;
var r15;
var r20;
var r25;
var r30;
var r40;
var r45;
var r50;
var r60;
var r65;
var r70;
var r75;
var r140;
var openTimer;
var closeTimer;

var ctx;
var midX;
var midY;

function initCanvas() {

	var canvas = document.getElementById("animationview");
	ctx = canvas.getContext('2d');
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
	midX = parseInt(width) / 2;
	midY = height / 2 - 10;

	r4 = 4 * resize;
	r10 = 10 * resize;
	r15 = 15 * resize;
	r20 = 20 * resize;
	r25 = 25 * resize;
	r30 = 30 * resize;
	r40 = 40 * resize;
	r45 = 45 * resize;
	r50 = 50 * resize;
	r60 = 60 * resize;
	r65 = 65 * resize;
	r70 = 70 * resize;
	r75 = 75 * resize;
	r140 = 140 * resize;
	ctx.clearRect(0,0,width,height);
}

function openAnimation() {
	initCanvas();
	//画两根竖线
	ctx.beginPath();

	ctx.lineCap = 'round';
	ctx.moveTo(midX + r20, midY - r50);
	ctx.lineTo(midX + r20, midY + r50);
	ctx.moveTo(midX - r20, midY - r50);
	ctx.lineTo(midX - r20, midY + r50);
	ctx.lineWidth = r10;
	ctx.strokeStyle = "#8dd652";
	ctx.stroke();

	ctx.closePath();

	//画一根横线
	ctx.beginPath();
	ctx.moveTo(midX - r140, midY + r70);
	ctx.lineTo(midX + r140, midY + r70);
	ctx.lineWidth = r4;
	ctx.strokeStyle = "#8dd652";
	ctx.stroke();

	ctx.closePath();

	//动画箭头
	drawOpen(0, ctx, midX, midY);

}
//打开
function drawOpen(count, ctx, midX, midY) {
	count++;
	getRightOpenAnimation(ctx, midX, midY, count);
	getLeftOpenAnimation(ctx, midX, midY, count);
	if(count > 24) {
		count = 0;
	}
	if(openTimer) {
		clearTimeout(openTimer);
	}
	if(closeTimer) {
		clearTimeout(closeTimer);
	}
	openTimer = setTimeout(function() {
		drawOpen(count, ctx, midX, midY);
	}, 75);
}
//右半边打开动画
function getRightOpenAnimation(ctx, midX, midY, count) {
	ctx.clearRect(midX + r30, midY - r25, midX - r30, r50);
	ctx.beginPath();
	for(var i = 0; i < 6; i++) {
		ctx.moveTo(midX + r60 + i * r40, midY - r25);
		ctx.lineTo(midX + r75 + i * r40, midY);
		ctx.lineTo(midX + r60 + i * r40, midY + r25);
		ctx.lineWidth = r4;
	}
	var lineargradienRight = ctx.createLinearGradient(midX + r50, midY, midX + r70 + 5.6 * r40, midY);

	lineargradienRight.addColorStop(0, 'white');
	lineargradienRight.addColorStop(0.04 * count, '#8dd652');
	lineargradienRight.addColorStop(1, 'white');

	ctx.strokeStyle = lineargradienRight;
	ctx.stroke();
	ctx.closePath();
}
//左半边打开动画
function getLeftOpenAnimation(ctx, midX, midY, count) {
	ctx.clearRect(0, midY - r25, midX - r30, r50);
	ctx.beginPath();
	for(var i = 0; i < 6; i++) {
		ctx.moveTo(midX - r60 - i * r40, midY - r25);
		ctx.lineTo(midX - r75 - i * r40, midY);
		ctx.lineTo(midX - r60 - i * r40, midY + r25);
		ctx.lineWidth = r4;
	}
	var lineargradienLeft = ctx.createLinearGradient(midX - r50, midY, midX - r70 - 5.6 * r40, midY);

	lineargradienLeft.addColorStop(0, 'white');
	lineargradienLeft.addColorStop(0.04 * count, '#8dd652');
	lineargradienLeft.addColorStop(1, 'white');

	ctx.strokeStyle = lineargradienLeft;
	ctx.stroke();
	ctx.closePath();
}

function closeAnimation() {
	initCanvas();
	//画两根竖线
	ctx.beginPath();

	ctx.lineCap = 'round';
	ctx.moveTo(midX + r20, midY - r50);
	ctx.lineTo(midX + r20, midY + r50);
	ctx.moveTo(midX - r20, midY - r50);
	ctx.lineTo(midX - r20, midY + r50);
	ctx.lineWidth = r10;
	ctx.strokeStyle = "#8dd652";
	ctx.stroke();

	ctx.closePath();

	//画一根横线
	ctx.beginPath();
	ctx.moveTo(midX - r140, midY + r70);
	ctx.lineTo(midX + r140, midY + r70);
	ctx.lineWidth = r4;
	ctx.strokeStyle = "#8dd652";
	ctx.stroke();

	ctx.closePath();

	//动画箭头
	drawClose(0, ctx, midX, midY);
}
//关闭
function drawClose(count, ctx, midX, midY) {
	count++;
	getRightCloseAnimation(ctx, midX, midY, count);
	getLeftCloseAnimation(ctx, midX, midY, count);
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
		drawClose(count, ctx, midX, midY);
	}, 75);
}
//右半边关闭动画
function getRightCloseAnimation(ctx, midX, midY, count) {
	ctx.clearRect(midX + r30, midY - r25, midX - r30, r50);
	ctx.beginPath();
	for(var i = 0; i < 6; i++) {
		ctx.moveTo(midX + r75 + i * r40, midY - r25);
		ctx.lineTo(midX + r60 + i * r40, midY);
		ctx.lineTo(midX + r75 + i * r40, midY + r25);
		ctx.lineWidth = r4;
	}
	var lineargradienRight = ctx.createLinearGradient(midX + r50, midY, midX + r70 + 5.6 * r40, midY);

	lineargradienRight.addColorStop(0, 'white');
	lineargradienRight.addColorStop(1 - 0.04 * count, '#8dd652');
	lineargradienRight.addColorStop(1, 'white');

	ctx.strokeStyle = lineargradienRight;
	ctx.stroke();
	ctx.closePath();
}
//左半边关闭动画
function getLeftCloseAnimation(ctx, midX, midY, count) {
	ctx.clearRect(0, midY - r25, midX - r30, r50);
	ctx.beginPath();
	for(var i = 0; i < 6; i++) {
		ctx.moveTo(midX - r75 - i * r40, midY - r25);
		ctx.lineTo(midX - r60 - i * r40, midY);
		ctx.lineTo(midX - r75 - i * r40, midY + r25);
		ctx.lineWidth = r4;
	}
	var lineargradienLeft = ctx.createLinearGradient(midX - r50, midY, midX - r70 - 5.6 * r40, midY);

	lineargradienLeft.addColorStop(0, 'white');
	lineargradienLeft.addColorStop(1 - 0.04 * count, '#8dd652');
	lineargradienLeft.addColorStop(1, 'white');

	ctx.strokeStyle = lineargradienLeft;
	ctx.stroke();
	ctx.closePath();
}

//停止动画
function stopAnimation() {
	if(openTimer) {
		clearTimeout(openTimer);
	}
	if(closeTimer) {
		clearTimeout(closeTimer);
	}
	initCanvas();
	var img = new Image();
    img.onload = function(){
      ctx.drawImage(img,midX-r140,midY-r65,r140*2,r65*2);
    }
    img.src = "fonts/curtain_icon.png";
}

