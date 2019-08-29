/**
 * Created by Veev on 2017/7/24.
 */
var gwID,devID,appID;
/*修改Ep的名称
 endpointNumber：1，2，3分别表示1键、2键、3键
 endpointName：名称
 * */
function updateEpName(endpointNumber,endpointName){
    var jsonData = {
        "cmd":"502",
        "appID":appID,
        "gwID":gwID,
        "mode":2,
        "devID":devID,
        "endpointNumber":endpointNumber,
        "endpointName":endpointName
    };
    send(jsonData);
}

function toogleSwitch(endpointNumber, commandId) {
    var jsonData = {
        "cmd":"501",
        "gwID":gwID,
        "devID":devID,
        "clusterId": 6,
        "endpointNumber":endpointNumber,
        "commandId": commandId,
        "commandType": 1
    };
    send(jsonData);
}

/*解绑，ep=0表示清除所有的绑定关系*/
function UnBindingDev(ep){
    sendCmdForAn(ep,0x8012,[ep+""]);
}

function unBindScene(ep) {
    var jsonData = {
        "cmd":"513",
        "appID":appID,
        "gwID":gwID,
        "mode":3,
        "devID":devID,
        "data":[
            {"endpointNumber":"" + ep}
        ]
    };
    send(jsonData);
}

function sendCmdForAn(endpointNumber,commandId,param){
    var jsonData;
    if(param!=null){
        jsonData = {
            "cmd": "501",
            "gwID": gwID,
            "devID": devID,
            "endpointNumber": endpointNumber,
            "commandType": 1,
            "commandId": commandId,
            "parameter": param
        };
    }else{
        jsonData = {
            "cmd": "501",
            "gwID": gwID,
            "devID": devID,
            "endpointNumber": endpointNumber,
            "commandType": 1,
            "commandId": commandId
        };
    }
    send(jsonData);
}

/*
 * 设备控制
 */
function send(jsonData) {
    plus.gatewayCmd.controlDevice(jsonData, function(result) {})
}