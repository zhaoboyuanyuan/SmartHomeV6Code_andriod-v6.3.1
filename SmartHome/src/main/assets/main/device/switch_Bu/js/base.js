/**
 * Created by Administrator on 2018/9/12.
 */

function overtimeWarn(self) {
    overtime = setTimeout(function () {
        $("#showOverTime").show();
        $("#otherDeviceUnBind").hide();
        $("#deviceUnBinding").hide();
        $(self).hide();
        setTimeout(function () {
            $("#showOverTime").hide();
        }, 3000)
    }, 15000);
}

function clearOvertimeWarn() {
    clearTimeout(overtime);
}

