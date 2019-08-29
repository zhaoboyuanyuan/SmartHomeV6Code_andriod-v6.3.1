///////////////////////////发动命令---start////////////////////////////
function sendCmd(gwID,dID,endpointNumber,commandId,parameter){
    var jsonData = {}
    jsonData.cmd = "501";
    jsonData.gwID = gwID;
    jsonData.devID = dID;
    jsonData.endpointNumber = endpointNumber;
    jsonData.clusterId = 0x0102;
    jsonData.commandType = 1;
    jsonData.commandId = commandId;
    if(parameter){
        jsonData.parameter = parameter;
    }
    plus.gatewayCmd.controlDevice(jsonData,function(){})
}
///////////////////////////发动命令---end////////////////////////////