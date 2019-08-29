var sendTemperatureType;//"heat"or"cool"发动的温度类型（制热、制冷）
var sendSetTemperatureTimer;//发送设置温度指令的延时
///////////////////////////
var mCallback;
var isEnable;

var shownum=26;//显示的数字

var isCanChangeNum = false;//是否可以滑动温度表

var MaxTemperature=35;
var MinTemperature=5;
var TemperatureStep = 0.5;//步进0.5

var deviceSetTemperature = "";//设置温度
var isNeedHandleTemp = false;

var numArry = [];


function scrollNumInit(num, callback) {
    mCallback = callback;
    setShowNum(num);
    console.log("setShowNum" + num);
}

function setScrollNumEnable(b) {
    isCanChangeNum = b;
}

function setShowNum(v) {
    shownum = v;
    if(numArry.length > 0 && MinTemperature && (shownum >= MinTemperature)){
        picker.pickers[0].setSelectedIndex(((shownum-MinTemperature)/TemperatureStep));
    }

}


function updatePickerArry() {
    numArry.length = 0;
    for(var i = MinTemperature;i<=MaxTemperature;i+=TemperatureStep){
        numArry.push(i);
    }
    picker.setData(numArry);
    picker.pickers[0].setSelectedIndex(((shownum-MinTemperature)/TemperatureStep));
    picker.show();
}
var picker = new mui.PopPicker();
for(var i = MinTemperature;i<=MaxTemperature;i+=TemperatureStep){
    numArry.push(i);
}
picker.setData(numArry);
picker.pickers[0].setSelectedIndex(0);
picker.show(function (selectItems) {

})