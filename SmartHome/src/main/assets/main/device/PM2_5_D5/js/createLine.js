//console.log(window.v6sysfunc)
var optGrid = {}
if(window.v6sysfunc){
	optGrid = {}
}else{
    if($(document).width() == "1242"){
        optGrid = {
            x:150,
            x2:150,
            y:150
        }
    }else{
        optGrid = {
            x:80,
            x2:80,
            y:80
        }
    }
}
//绘制折线图
function editLine(timeArr, ppmArr) {
	// 路径配置
	require.config({
		paths: {
			echarts: 'js'
		}
	});
	// 使用
	require(
		[
			'echarts',
			'echarts/chart/line' // 使用柱状图就加载bar模块，按需加载
		],
		function(ec) {
			// 基于准备好的dom，初始化echarts图表
			var myChart = ec.init(document.getElementById('line'));
			var option = {
				//点击提示的线条和文字
				tooltip: {
					trigger: 'axis',
					axisPointer: {
						lineStyle: {
							color: 'pink',
							type: 'dashed',
							width: 1
						}
					}
				},
				calculable: false,
				//x轴
				xAxis: [{
					name: lang_hour,
					type: 'category',
					boundaryGap: false,
					axisLabel: {
						interval:5,
						textStyle: {
							color: '#aaa',
							fontSize:20/640*document.body.clientWidth
						}
					},
					//0刻度轴线
					axisLine: {
						lineStyle: {
							width: 0,
							color: '#aaa'
						}
					},
					//x轴刻度线
					axisTick:{
						show:false
					},
					data: timeArr
				}],
				//y轴：二氧化碳指数
				yAxis: [{
                    name:'ug/m3',
					type: 'value',
					axisLabel: {
						formatter: '{value}',
						textStyle: {
							color: '#aaa',
							fontSize:16/640*document.body.clientWidth
						}
					},
					axisLine: {
						lineStyle: {
							width: 0,
							color: '#aaa'
						}
					},
					min: 0,
					max: 150
                }],
				grid:optGrid,
				//折线数据
				series: [{
					name: 'ug/m3',
                    smooth: true,
                    symbol: 'none',
                    type: 'line',
					data: ppmArr,
					draggable:false,
					itemStyle: {
						normal: {
							color: '#98DA63',
							lineStyle: {
								color: '#98DA63'
							}
						}
					}
				}]
			};
			// 为echarts对象加载数据 
			myChart.setOption(option);
			window.onresize = myChart.resize;
		}
	);
}
