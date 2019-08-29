function GetGWModel(name,gwID,masterID,type){
		var obj = new Object();
		obj.name = name;
		obj.gwID = gwID;
		obj.masterID = masterID;
		obj.type = type;
		return obj;
}

function GetDeviceModel(name,type,status,group){
	var obj = new Object();
	obj.name = name;
	obj.type = type;
	obj.status = status;
	obj.group = group;
	return obj;
}
