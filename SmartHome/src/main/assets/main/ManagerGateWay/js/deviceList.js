
//	<ul>
//			<li>
//			<!--<a href="setDoorState.html">-->
//				<img src="../source/deviceIcon/device_icon_06.png"/>
//				<p>水浸传感器</p><br>
//				<em>在线</em>
//				<span>[大展桌]</span>
//			<!--</a>-->
//		</li>
//	</ul>
function createDeviceCell(deviceModel)
{
	var deviceName = deviceModel.name;
	var deviceIcon = "../source/deviceIcon/device_icon_" + deviceModel.type + ".png";
	var status = deviceModel.status != 2 ? online : offline;
	var group = "[" + deviceModel.group + "]";
	console.log(deviceIcon);
	var ul = $(".get_list ul");
	var li = $("<li></li>");
	li.appendTo(ul);
	
	var img = $("<img/>");
	img.attr("src", deviceIcon);
	img.appendTo(li);
	
	var p = $("<p></p>").text(deviceName);
	img.after(p);
	
	var br = $("<br>");
	p.after(br);
	
	var em = $("<em></em>").text(status);
	br.after(em);
	
	var span = $("<span></span>").text(group);
	em.after(span);
}

//网关下的子机
//	<ul>
//				<li>
//					<a href="deviceList.html">
//					<img src="../source/deviceIcon/device_icon_06.png"/>
//					<p>水浸传感器</p><br>
//					<p>50294D</p>
//					<p status="bind"></p>
//					</a>
//			</li>
//		</ul>
function createGWCell(gwModel)
{
	var deviceName = gwModel.name;
	var deviceIcon = "../source/deviceIcon/device_icon_" + gwModel.type + ".png";
	var status = false;
	if (gwModel.masterID != undefined && gwModel.masterID != "" && gwModel.masterID != null){
		status = true;
	} 
	var gwID = gwModel.gwID;
	var gwType = gwModel.type;
	console.log(deviceIcon);
	var ul = $(".get_list ul");
	var li = $("<li></li>");
	li.appendTo(ul);
	
	var a = $("<a></a>");
	a.appendTo(li);
	
	var img = $("<img/>");
	img.attr("src", deviceIcon);
	img.appendTo(a);
	
	var p = $("<p></p>").text(deviceName);
	img.after(p);
	if (deviceName == '') {
		plus.gatewayCmd.managerGWName(gwType, function(result) {
			 p.text(result)
		});
	}
	var br = $("<br>");
	p.after(br);
	
	var p2 = $("<p></p>").text(gwID);
	br.after(p2);
	
	if (status) {
		var p3 = $("<p></p>").text(bindedStatus);
		p3.attr("status", "bind");
		p2.after(p3);
	} else {
		var p3 = $("<p></p>");
		p3.attr("status", "nobind");
		p2.after(p3);
		a.attr("href", 'bindGW.html?gwid=' + gwID);
	}
}

