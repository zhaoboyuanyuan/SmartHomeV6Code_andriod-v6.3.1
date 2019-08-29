
///////////////////////////
var shownum=0;//显示的数字

var isCanChangeNum = false;//是否可以滑动温度表
var isShowChangeNum = true;//温度是否显示正常值

var MaxTemperature=32;
var MinTemperature=10;

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
        if (!isShowChangeNum) {
            numArry.push('--');
            picker.setData(numArry);
            picker.pickers[0].setSelectedIndex(0);
            picker.show();
        } else {
            numArry.push(i);
            picker.setData(numArry);
            picker.pickers[0].setSelectedIndex((shownum-MinTemperature)*2);
            picker.show();
        }

    }
}
var picker = new mui.PopPicker();
for(var i = MinTemperature;i<=MaxTemperature;i+=0.5){
    numArry.push(i);
}
picker.setData(numArry);
picker.pickers[0].setSelectedIndex(0);
picker.show(function (selectItems) {

})