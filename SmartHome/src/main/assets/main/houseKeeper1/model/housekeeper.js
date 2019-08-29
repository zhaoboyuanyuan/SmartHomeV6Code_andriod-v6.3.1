/*programType
 * 自动程序类型
0代表简单的场景任务执行程序，特征是Trigger为只有一个场景条目，Condition为空
1代表定时场景程序，特征是Trigger只有一条时间类型(Type 1)，Condition为空，Action只有一条场景类型（Type＝0）
2代表一般管家规则，不满足场景简单任务和定时场景的都是一般管家规则
 */
(function() {
	var M = function() {}
	M.prototype = {
		//新增
		send_add_507: {
			"cmd": "507",
			"gwID": "gwID",
			"appID": "appID",
			"operType": "C",
			"programID": "",
			"programName": "programName",
			"programType": "programType",
			"status": "1",
			"triggerArray": "triggerArray",
			"actionArray":"actionArray"
		},
	//单条数据查询
		//当operType＝R时，查询条件可以为programID、programDesc、programName、programType，为空则表示不作为条件
		send_detail_507: {
			"cmd": "507",
			"gwID":"gwID",
			"appID": "appID",
			"operType": "R",
			"programID": "programID",
			"programName": "programName",
			"programType": "programType",
			"programDesc": "programDesc",
			"triggerArray": ""
		},
		//修改
		send_change_507: {
			"cmd": "507",
			"gwID": "gwID",
			"appID": "appID",
			"operType": "U",
			"programID": "programID",
			"programType": "programType",
			"programName": "programName",
			"status": "status",
			"triggerArray": "triggerArray"
		},
		//删除
		send_delete_507: {
			"cmd": "507",
			"operType": "D",
			"programID": "programID",
			"triggerArray": ""
		},
		//修改生效状态
		//当operType＝S时，只需要填写programID，status，其它字段均为空，即使有值也不需要处理。
		send_switch_507: {
			"cmd": "507",
			"gwID": "gwID",
			"appID": "appID",
			"operType": "S",
			"programID": "programID",
			"status": "status",
			"triggerArray": ""
		},
		rev_507: {
			"cmd": "507",
			"gwID": "gwID",
			"appID": "appID",
		},

		//任务列表查询
		send_search_508: {
			"cmd": "508",
			"gwID": "gwID",
			"appID": "", //非必要参数
		},

		rev_search_508: {
			"cmd": "508",
			"gwID": "gwID",
			"appID": "appID", //非必要参数
			"ruleArry": [{
				"rule": {
					"programID": "programID",
					"programName": "programName",
					"programDesc": "programDesc", //自动程序描述
					"status": "status",
					"programType": "programType",
				}
			}],
			"p": {
				"i": "i",
				"t": "t"
			}
		}
	}
	window.houserkeeperModel = new M();
})()