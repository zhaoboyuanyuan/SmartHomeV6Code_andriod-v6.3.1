function addTime(start, end, week) {
	var startHour = start.substr(0, 2)
	var startMin = start.substr(3, 2)
	var startTime = Number(startHour) * 60 + Number(startMin)
	var endTime = Number(end.substr(0, 2)) * 60 + Number(end.substr(3, 2))
	if(endTime < startTime){
        endTime = endTime + 1440;
	}
	var min = (endTime - startTime) + ""
	if(min.length == 1) {
		min = "000" + min
	} else if(min.length == 2) {
		min = "00" + min
	} else if(min.length == 3) {
		min = "0" + min
	}
	var dic = {
		"Sun": 1,
		"Mon": 7,
		"Tue": 6,
		"Wed": 5,
		"Thu": 4,
		"Fri": 3,
		"Sat": 2
	}
	var arr = [0, 0, 0, 0, 0, 0, 0, 0]
	for(var i = 0; i < week.length; i++) {
		var str = week[i];
		var num = dic[str];
		arr[num] = 1;
	}
	var weekTime = ''
	for(var i = 0; i < arr.length; i++) {
		weekTime += arr[i]
	}
	var high = weekTime.substr(0, 4)
	var low = weekTime.substr(4, 4)
	var Str16 = {
		"0000": "0",
		"0001": "1",
		"0010": "2",
		"0011": "3",
		"0100": "4",
		"0101": "5",
		"0110": "6",
		"0111": "7",
		"1000": "8",
		"1001": "9",
		"1010": "A",
		"1011": "B",
		"1100": "C",
		"1101": "D",
		"1110": "E",
		"1111": "F"
	}
	return startHour + startMin + min + Str16[high] + Str16[low]
}
//生效时段 0009078003  0300012007
function effectTime(time) {
	var timeJson = {};
	var hour = time.substr(0, 2)
	var min = time.substr(2, 2)
	var timeStr = parseInt(time.substr(4, 4)) + parseInt(time.substr(0, 2)) * 60 + parseInt(time.substr(2, 2))
	var endMin = parseInt(timeStr % 60) < 10 ? "0" + parseInt(timeStr % 60) : parseInt(timeStr % 60)
	var endHour = parseInt(timeStr / 60);
	if(endHour > parseInt(hour) && endHour > 24){
        endHour = endHour - 24;
	}
	// 判断当endhour为24的情况 显示0:00
	// else if(endHour > parseInt(hour) && endHour == 24){
     //    endHour = 0;
	// }
    endHour = endHour < 10 ? "0" + endHour : endHour;
	var endTime = endHour + ":" + endMin
	var week = time.substr(8, 2)
	var Str16 = {
		"0": "0000",
		"1": "0001",
		"2": "0010",
		"3": "0011",
		"4": "0100",
		"5": "0101",
		"6": "0110",
		"7": "0111",
		"8": "1000",
		"9": "1001",
		"A": "1010",
		"B": "1011",
		"C": "1100",
		"D": "1101",
		"E": "1110",
		"F": "1111"
	}
	var weekNum = Str16[week.substr(0, 1)] + Str16[week.substr(1, 1)]
	var weekStr = "";
	for(var i = 8; i > 0; i--) {
		var we = weekNum.substr(i, 1)
		switch(i) {
			case 0:
				break;
			case 1:
				we = (we == 1) ? "Sun" : "";
				break;
			case 2:
				we = (we == 1) ? "Sat" : "";
				break;
			case 3:
				we = (we == 1) ? "Fri" : "";
				break;
			case 4:
				we = (we == 1) ? "Thu" : "";
				break;
			case 5:
				we = (we == 1) ? "Wed" : "";
				break;
			case 6:
				we = (we == 1) ? "Tue" : "";
				break;
			case 7:
				we = (we == 1) ? "Mon" : "";
				break
		}
		if(i == 8 && we != "") {
			weekStr = we;
		} else if(i < 8 && we != "") {
			if(weekStr == "") {
				weekStr = we;
			} else {
				weekStr = weekStr + "," + we
			}
		}
	}
    timeJson.startTime = hour + ":" + min;
	timeJson.endTime = endTime;
	timeJson.weekStr = weekStr;
	return timeJson;

}