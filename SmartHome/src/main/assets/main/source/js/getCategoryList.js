/**
 * Created by Administrator on 2018/6/13.
 */
function getCategoryList(){
    var categoryList = [{
        "name":languageUtil.getResource("device_list_Categories"),//"全部类别"
        "type":"0"
    },{
        "name":languageUtil.getResource("device_list_Lock"),//"智能门锁",
        "type":"1"
    },{
        "name":languageUtil.getResource("device_list_Camera"),//"摄像机",
        "type":"2"
    },{
        "name":languageUtil.getResource("device_list_Switch"),//"开关照明",
        "type":"3"
    },{
        "name":languageUtil.getResource("device_list_Socket"),//"插座",
        "type":"4"
    },{
        "name":languageUtil.getResource("device_list_Security"),//"安防设备",
        "type":"5"
    },{
        "name":languageUtil.getResource("device_list_Environmental"),//"环境监测",
        "type":"6"
    },{
        "name":languageUtil.getResource("device_list_Curtain"),//"窗帘",
        "type":"7"
    },{
        "name":languageUtil.getResource("device_list_Controller"),//"智能遥控",
        "type":"8"
    },{
        "name":languageUtil.getResource("device_list_Repeater"),//"中继器",
        "type":"9"
    },{
        "name":languageUtil.getResource("device_list_Thermostat"),//"控制器",
        "type":"10"
    },{
        "name":languageUtil.getResource("device_list_Music"),//"背景音乐",
        "type":"11"
    },{
        "name":languageUtil.getResource("device_list_Health"),//"健康管理",
        "type":"12"
    },{
        "name":languageUtil.getResource("device_list_appliances"),//"家用电器",
        "type":"13"
    }];
    return categoryList;
}

function filterRoom(zoneText,category){
    $('.device-list li').each(function(index,item){
        if(category != "" && category != "0"){
            if(zoneText == "" && $(item).attr("data-category") == category){
                $(item).show();
            }else if($(item).attr("data-category") == category && $(item).find(".roomName").attr("data-room") == zoneText){
                $(item).show();
            }
        }else if(zoneText == ""){
            $(item).show();
        }else if($(item).find(".roomName").attr("data-room") == zoneText){
            $(item).show();
        }
    })
}
function filterCategory(zoneText,category){
    $('.device-list li').each(function(index,item){
        if(zoneText != ""){
            if(category == "0" && $(item).find(".roomName").attr("data-room") == zoneText){
                $(item).show();
            }else{
                if($(item).attr("data-category") == category && $(item).find(".roomName").attr("data-room") == zoneText){
                    $(item).show();
                }
            }
        }else if(category == "0"){
            $(item).show();
        }else if($(item).attr("data-category") == category){
            $(item).show();
        }
    })
}