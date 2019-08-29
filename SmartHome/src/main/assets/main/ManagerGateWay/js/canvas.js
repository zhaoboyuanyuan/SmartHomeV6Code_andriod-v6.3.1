 var canvas = document.getElementById('canvas');
 var ctx = canvas.getContext('2d');
 canvas.width = canvas.parentNode.offsetWidth;
 canvas.height = canvas.parentNode.offsetHeight;

 window.requestAnimFrame = (function() {
 	return window.requestAnimationFrame ||
 		window.webkitRequestAnimationFrame ||
 		window.mozRequestAnimationFrame ||
 		function(callback) {
 			window.setTimeout(callback, 1000);
 		};
 })();

 //初始角度为0
 var step = 0;
 var lines = ["rgba(255,255,255, 0.3)", "rgba(255,255,255, 0.6)", "rgba(255,255,255, 0.8)"];

 function loop() {
 	ctx.clearRect(0, 0, canvas.width, canvas.height);
 	step++;
 	var H = canvas.height / 10
 	//画3个不同颜色的矩形
 	for(var j = lines.length - 1; j >= 0; j--) {
 		ctx.fillStyle = lines[j];
 		//每个矩形的角度都不同，每个之间相差45度
 		var angle = (step + j * 45) * Math.PI / 180;
 		var deltaHeight = Math.sin(angle) * 100;
 		var deltaHeightRight = Math.cos(angle) * 100;
 		ctx.beginPath();
 		ctx.moveTo(0, canvas.height / 2 + deltaHeight);
 		ctx.bezierCurveTo(canvas.width / 2, canvas.height / 2 + deltaHeight - H, canvas.width / 2, canvas.height / 2 + j * deltaHeightRight - H, canvas.width, canvas.height / 2 + deltaHeightRight);
 		ctx.lineTo(canvas.width, canvas.height);
 		ctx.lineTo(0, canvas.height);
 		ctx.lineTo(0, canvas.height / 2 + deltaHeight);
 		ctx.closePath();
 		ctx.fill();
 	}
 	requestAnimFrame(loop);
 }
 loop();