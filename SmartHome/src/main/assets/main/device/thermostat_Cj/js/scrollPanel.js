/**
 * Created by Veev on 2017/11/17.
 */

var startX, startY, moveEndX, moveEndY, X, Y;
var hideOtherNumTimer;//数字消失的延时
var sendSetTemperatureTimer;//发送设置温度指令的延时
///////////////////////////
var SectionTopHeight = 10;//顶部预留空间
var CellNormalHeight = 7;//7rem
///////////////////////////
var showNum = 0;//显示的数字
var panel1Top = SectionTopHeight + 0;
var panel2Top = SectionTopHeight + CellNormalHeight;
var panel3Top = SectionTopHeight + CellNormalHeight * 2;
var panel4Top = SectionTopHeight + CellNormalHeight * 3;
var panel5Top = SectionTopHeight + CellNormalHeight * 4;

// 是否可用
var isEnable = true;
var mData = [1,2,3,5,8];
// 最大值 最小值 步进
var tempMax = mData.length - 1;
var tempMin = 0;
var mCallback;

function scrollPanelInit(data, index, callback) {
    isEnable = true;
    mCallback = callback;
    mData = data;
    tempMax = mData.length - 1;
    setShowNum(index);
}

function setScrollPanelEnable(b) {
    isEnable = b;
}

function setRange(max, min, indexing) {
    tempMax = 32;
    tempMin = 10;
}

// 设置显示的数值
function setShowNum(v) {
    showNum = v;
    topschange();
    setTimeout('onlyShowNum()', 500);
}
function getShowNum() {
    return showNum;
}

function print(message) {
    // console.log(message);
}

$(".panel1").on('touchstart touchmove touchend', function (event) {
    if (panel1Top == SectionTopHeight + CellNormalHeight * 2 || (event.type == 'touchend')) {
        handleEvent(event);
    }
});
$(".panel2").on('touchstart touchmove touchend', function (event) {
    if (panel2Top == SectionTopHeight + CellNormalHeight * 2 || (event.type == 'touchend')) {
        handleEvent(event);
    }
});
$(".panel3").on('touchstart touchmove touchend', function (event) {
    if (panel3Top == SectionTopHeight + CellNormalHeight * 2 || (event.type == 'touchend')) {
        handleEvent(event);
    }
});
$(".panel4").on('touchstart touchmove touchend', function (event) {
    if (panel4Top == SectionTopHeight + CellNormalHeight * 2 || (event.type == 'touchend')) {
        handleEvent(event);
    }
});
$(".panel5").on('touchstart touchmove touchend', function (event) {
    if (panel5Top == SectionTopHeight + CellNormalHeight * 2 || (event.type == 'touchend')) {
        handleEvent(event);
    }
});

function handleEvent(event) {
    if (!isEnable) {
        return
    }
    switch (event.type) {
        case 'touchstart': {
            window.clearInterval(hideOtherNumTimer);
            clearTimeout(sendSetTemperatureTimer);
            falg = true;
            event.preventDefault();
            startX = event.touches[0].pageX;
            startY = event.touches[0].pageY;
        }
            break;
        case 'touchmove': {
            event.preventDefault();
            moveEndX = event.changedTouches[0].pageX;
            moveEndY = event.changedTouches[0].pageY;
            X = moveEndX - startX;
            Y = moveEndY - startY;
            if (Y > 0) {
                print("向下");
                changeNum(falg, "down")
            } else if (Y < 0) {
                print("向上");
                changeNum(falg, "up");
            } else {
//                        alert("没滑动");
            }
        }
            break;
        case 'touchend': {
            falg = false;
            hideOtherNumTimer = window.setTimeout(function () {
                onlyShowNum();
            }, 1500);
            sendSetTemperatureTimer = window.setTimeout(function () {
                // 回调
                mCallback(showNum);
            }, 1000);
            if (!falg) {
                print('点击');
            } else {
                print('滑动');
            }
        }
            break;
    }
}

function changeNum(falgtype, fangXiang) {
    if (falgtype) {
        falg = false;
        if (fangXiang == "up" && showNum < tempMax) {
            showNum += 1;
            topschange(fangXiang);
        } else if (fangXiang == "down" && showNum > tempMin) {
            showNum -= 1;
            topschange(fangXiang);
        }
    }

}

function topschange(fangxiang) {
    if (fangxiang == "up") {
        panel1Top = (panel1Top > SectionTopHeight + 0) ? panel1Top - CellNormalHeight : SectionTopHeight + CellNormalHeight * 4;
        panel2Top = (panel2Top > SectionTopHeight + 0) ? panel2Top - CellNormalHeight : SectionTopHeight + CellNormalHeight * 4;
        panel3Top = (panel3Top > SectionTopHeight + 0) ? panel3Top - CellNormalHeight : SectionTopHeight + CellNormalHeight * 4;
        panel4Top = (panel4Top > SectionTopHeight + 0) ? panel4Top - CellNormalHeight : SectionTopHeight + CellNormalHeight * 4;
        panel5Top = (panel5Top > SectionTopHeight + 0) ? panel5Top - CellNormalHeight : SectionTopHeight + CellNormalHeight * 4;
        updatePanelUI();
    } else if (fangxiang == "down") {
        panel1Top = (panel1Top < SectionTopHeight + CellNormalHeight * 4) ? panel1Top + CellNormalHeight : SectionTopHeight + 0;
        panel2Top = (panel2Top < SectionTopHeight + CellNormalHeight * 4) ? panel2Top + CellNormalHeight : SectionTopHeight + 0;
        panel3Top = (panel3Top < SectionTopHeight + CellNormalHeight * 4) ? panel3Top + CellNormalHeight : SectionTopHeight + 0;
        panel4Top = (panel4Top < SectionTopHeight + CellNormalHeight * 4) ? panel4Top + CellNormalHeight : SectionTopHeight + 0;
        panel5Top = (panel5Top < SectionTopHeight + CellNormalHeight * 4) ? panel5Top + CellNormalHeight : SectionTopHeight + 0;
        updatePanelUI();

    } else {
        panel1Top = SectionTopHeight + 0;
        panel2Top = SectionTopHeight + CellNormalHeight;
        panel3Top = SectionTopHeight + CellNormalHeight * 2;
        panel4Top = SectionTopHeight + CellNormalHeight * 3;
        panel5Top = SectionTopHeight + CellNormalHeight * 4;
        updatePanelUI();
    }
}

function updatePanelUI() {
    var panelsarry = [panel1Top, panel2Top, panel3Top, panel4Top, panel5Top];
    var panelObjectsarry = [".panel1", ".panel2", ".panel3", ".panel4", ".panel5"];
    for (var i = 0; i < panelsarry.length; i++) {
        var panelTemp = panelsarry[i];
        var panelTempStr = panelTemp + "rem";
        switch (panelTemp) {
            case SectionTopHeight + 0:
                $(panelObjectsarry[i]).find('p').html(mData[showNum - 2]);
                $(panelObjectsarry[i]).find('p').css("font-size", "2rem");
                if (showNum - 2 < tempMin) {
                    $(panelObjectsarry[i]).animate({
                        top: panelTempStr, opacity: '0'
                    }, "fast");
                } else {
                    $(panelObjectsarry[i]).animate({
                        top: panelTempStr, opacity: '0'
                    }, "fast");
                }
                break;
            case SectionTopHeight + CellNormalHeight:
                $(panelObjectsarry[i]).find('p').html(mData[showNum - 1]);
                $(panelObjectsarry[i]).find('p').css("font-size", "2rem");
                if (showNum - 1 < tempMin) {
                    $(panelObjectsarry[i]).animate({
                        top: panelTempStr, opacity: '0'
                    }, "fast");
                } else {
                    $(panelObjectsarry[i]).animate({
                        top: panelTempStr, opacity: '1'
                    }, "fast");
                }
                break;
            case SectionTopHeight + CellNormalHeight * 2:
                $(panelObjectsarry[i]).find('p').html(mData[showNum]);
                $(panelObjectsarry[i]).animate({
                    top: panelTempStr, opacity: '1'
                }, "fast", "linear", function () {
                    var cellIndex = $.inArray(SectionTopHeight + CellNormalHeight * 2, panelsarry);
                    $(panelObjectsarry[cellIndex]).find('p').css("font-size", "9rem");
                });
                break;
            case SectionTopHeight + CellNormalHeight * 3:
                $(panelObjectsarry[i]).find('p').html(mData[showNum + 1]);
                $(panelObjectsarry[i]).find('p').css("font-size", "2rem");
                if (showNum + 1 > tempMax) {
                    $(panelObjectsarry[i]).animate({
                        top: panelTempStr, opacity: '0'
                    }, "fast");
                } else {
                    $(panelObjectsarry[i]).animate({
                        top: panelTempStr, opacity: '1'
                    }, "fast");
                }
                break;
            case SectionTopHeight + CellNormalHeight * 4:
                $(panelObjectsarry[i]).find('p').html(mData[showNum + 2]);
                $(panelObjectsarry[i]).find('p').css("font-size", "2rem");
                if (showNum + 2 > tempMax) {
                    $(panelObjectsarry[i]).animate({
                        top: panelTempStr, opacity: '0'
                    }, "fast");
                } else {
                    $(panelObjectsarry[i]).animate({
                        top: panelTempStr, opacity: '0'
                    }, "fast");
                }
                break;
        }

    }
}

function onlyShowNum() {
    var panelsarry = [panel1Top, panel2Top, panel3Top, panel4Top, panel5Top];
    var panelObjectsarry = [".panel1", ".panel2", ".panel3", ".panel4", ".panel5"];
    var cellIndex = $.inArray(SectionTopHeight + CellNormalHeight * 2, panelsarry);
    for (var i = 0; i < panelObjectsarry.length; i++) {
        if (i != cellIndex) {
            $(panelObjectsarry[i]).animate({
                opacity: '0'
            }, "fast");
        } else {
            $(panelObjectsarry[i]).animate({
                opacity: '1'
            }, "fast");
        }
    }
}