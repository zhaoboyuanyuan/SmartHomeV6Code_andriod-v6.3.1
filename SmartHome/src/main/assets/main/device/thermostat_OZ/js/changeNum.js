var sendTemperatureType;//"heat"or"cool"发动的温度类型（制热、制冷）
var sendSetTemperatureTimer;//发送设置温度指令的延时
///////////////////////////
var shownum=0;//显示的数字

var isCanChangeNum = false;//是否可以滑动温度表

var MaxTemperature=32;
var MinTemperature=10;

var numArry = [];
function setShowNum(v) {
    shownum = v;
    if(numArry.length > 0){
    }

}


function updatePickerArry() {
    numArry.length = 0;
    for(var i = MinTemperature;i<=MaxTemperature;i++){
        numArry.push(i);
    }
    picker.setData(numArry);
    picker.pickers[0].setSelectedIndex((shownum-MinTemperature));
    picker.show();
}
var picker = new mui.PopPicker();
for(var i = MinTemperature;i<=MaxTemperature;i++){
    numArry.push(i);
}
picker.setData(numArry);
picker.pickers[0].setSelectedIndex(0);
picker.show(function (selectItems) {

})