
///////////////////////////
var shownum=0;//显示的数字

var isCanChangeNum = false;//是否可以滑动温度表

var MaxTemperature=32;
var MinTemperature=16;

var numArry = [];
function setShowNum(v) {
    shownum = v;
    if(numArry.length > 0){
        updatePickerArry();
    }

}


function updatePickerArry() {
    numArry.length = 0;
    for(var i = MinTemperature;i<=MaxTemperature;i+=0.5){
        numArry.push(i);
    }
    picker.setData(numArry);
    picker.pickers[0].setSelectedIndex((shownum-MinTemperature)*2);
    picker.show();
}
var picker = new mui.PopPicker();
for(var i = MinTemperature;i<=MaxTemperature;i+=0.5){
    numArry.push(i);
}
picker.setData(numArry);
picker.pickers[0].setSelectedIndex(0);
picker.show(function (selectItems) {

})