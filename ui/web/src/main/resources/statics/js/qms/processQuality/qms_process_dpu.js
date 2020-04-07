var vm = new Vue({
	el : '#page_div',
	data : {
		WERKS:'',
		TEST_TYPE:'',
		TEST_NODE:'',
		TEST_NODE_LIST:[],
		WEIDU:'month',
		TEST_RECORD_LIST :[],
		showChart:false,
		formUrl:baseUrl + "processQuality/test/getProcessDpuData",
	},
	watch:{
		TEST_TYPE : {
			handler : function(newVal, oldVal) {
				$("#dataGrid").jqGrid('clearGridData');
				vm.TEST_NODE_LIST = vm.getTestNodes();
				//vm.query();
			}
		},
	},
	methods:{
		getTestNodes : function() {
			let list = [];
			$.ajax({
				url : baseUrl + '/qms/common/getTestNodes',
				type : 'post',
				async : false,
				dataType : 'json',
				data : {
					testType : vm.TEST_TYPE,
					TEST_CLASS:'整车'
				},
				success : function(resp) {
					if (resp.msg == 'success') {
						list = resp.list;
					}
				}
			})
			return list;
		},
		query:function(){
			$.ajax({
				url:baseUrl + "processQuality/test/getProcessDpuData",
				dataType:'json',
				method:'post',
				async:false,
				data:{
					WERKS:vm.WERKS,
					TEST_TYPE:vm.TEST_TYPE,
					TEST_NODE:vm.TEST_NODE,
					START_DATE:$("#start_date").val(),
					END_DATE:$("#end_date").val(),
					WEIDU:vm.WEIDU
				},
				success:function(result){
					vm.showChart=true;
					vm.TEST_RECORD_LIST=result.page;
					generateChart(result.chartList,result.itemList,vm.WEIDU);
					showTable();
				},
				error:function(){
					js.showErrorMessage("抱歉，系统异常!")
				}
			})
		}
	},
	created:function(){		
		this.WERKS = $("#WERKS").find("option").first().val();
		this.TEST_TYPE='01';
	}
})	

$(function(){
	var cd=new Date();
	l_y = cd.getFullYear();
	l_m = cd.getMonth();
	l_d=1;
	$("#start_date").val(formatDate(new Date(l_y,l_m,l_d)))
	$("#end_date").val(getCurDate());
	
	vm.query();
})


function generateChart(chartList,itemList,queryItem){
	var bug_avg = new Array();
	var categories=new Array();
	var targetVal=0;
	var linedata=new Array();
	if(itemList==undefined){
		itemList=new Array();
	}
	if(itemList.length>0){
		$.each(itemList,function(index,item){
			bug_avg[item]=0;
			if(queryItem=='week'){
				categories[index]="第"+item+"周";
			}
			if(queryItem=='month'){
				categories[index]=item+"月";
			}
			if(queryItem=='day'){
				categories[index]=item;
			}
		});
	}
	$.each(chartList,function(index,data){
		targetVal=data.target_val==undefined?0:data.target_val;
		var busNum=isNaN(parseInt(data.bus_num))?0:parseInt(data.bus_num);
		var bugNum=isNaN(parseInt(data.bug_num))?0:parseInt(data.bug_num);		
		if(queryItem=='order'){
			categories[index]=data.item;
			if(busNum==0){
				bug_avg[data.item]=0
			}else
			 bug_avg[data.item]=Number((bugNum/busNum).toFixed(2));
		}/*else if(queryItem=='day'){
			bug_avg[data.item]=Number((bugNum/busNum).toFixed(2));
		}*/else{
			if(busNum==0){
				bug_avg[data.item]=0
			}else
				bug_avg[data.item]=Number((bugNum/busNum).toFixed(2));
		}
	});
	var title_line = "单车缺陷数(DPU)趋势图";	
	var xAxis_line = {
			categories : categories,
			labels: {
                autoRotationLimit: 80
            }
		};
	var fn_formatter_line = function(obj) {
		var s = "";
		s = obj.x + ": " + obj.y ;
		return s;
	}
	var yAxis_line = {
			title : {
				text : ''
			},
			//max:Number(targetVal)+3,
			plotLines : [ { //一条延伸到整个绘图区的线，标志着轴中一个特定值。
				color : 'red',
				dashStyle : 'Dash', //Dash,Dot,Solid,默认Solid
				width : 1.5,
				value : targetVal, //y轴显示位置
				zIndex : 0,
				label : {
					text : '目标值：'+targetVal, //标签的内容
					align : 'left', //标签的水平位置，水平居左,默认是水平居中center
					x : 10
				//标签相对于被定位的位置水平偏移的像素，重新定位，水平居左10px
				}
			} ]
		};

	for (value in bug_avg){
		linedata.push(bug_avg[value]);
	}
	var series_line = [];
	var l_obj = {
			type : 'line',
			name : 'DPU',
			data : linedata,
			marker : {
				lineWidth : 2,
				lineColor : Highcharts.getOptions().colors[3],
				fillColor : 'white'
			},
			dataLabels : {
				enabled : true,
				formatter : function(obj) {
					return this.y ;
				},
				color : '#606060',
				style : {
					fontSize : '10px'
				}
			}
		};
	series_line.push(l_obj);
	
	drowCharts("#chartsContainer", title_line, xAxis_line,fn_formatter_line, series_line, yAxis_line);
}


function drowCharts(container, title, xAxis, fn_formatter, series, yAxis,tooltip,plotOptions) {
	$(container).highcharts({
		chart : {
			shadow : false,// 是否设置阴影
			zoomType : 'xy'// 拖动鼠标放大图表的方向
		},
		colors : [  '#90ed7d', '#f7a35c','#8085e9', '#f15c80', '#e4d354', '#8085e8','#8d4653', '#91e8e1','#7cb5ec' ],
		// 图表版权信息。默认情况下，highcharts图表的右下方会放置一个包含链接的版权信息。
		credits : {
			enabled : false
		},
		title : {
			text : title
		},
		xAxis : xAxis,
		yAxis : yAxis,
		tooltip : tooltip||{
			formatter : function() {
				var s;
				s = fn_formatter(this);
				return s;
			}
		},
		plotOptions:plotOptions||{},
		labels : {},
		series : series
	});
}

function showTable(){
	var data = $('#dataGrid').data("dataGrid");
	if (data) {
		//$("#dataGrid").jqGrid('GridUnload');
		$("#dataGrid").setGridParam({rowNum:$("#pageSize").val()||15}).trigger("reloadGrid"); 
	}
	var columns = [ {
		label : '车号/VIN',
		name : 'VIN',
		align : "center",
		index : 'VIN',
		width : '150',
		formatter : function(val, obj, row, act) {
			if(row.BUS_NO!='' && row.BUS_NO!=null){
				return row.BUS_NO;
			}else 
				return val;
		}
	}, {
		label : '工厂',
		name : 'WERKS',
		align : "center",
		index : 'WERKS',
		width : '90'
	}, {
		label : '检验节点 ',
		name : 'TEST_NODE',
		align : "center",
		index : 'TEST_NODE',
		width : '100'
	},  {
		label : '检验项目 ',
		name : 'TEST_ITEM',
		align : "center",
		index : 'TEST_ITEM',
		width : '180'
	},{
		label : '故障 ',
		name : 'TEST_RESULT',
		align : "center",
		index : 'TEST_RESULT',
		width : '210'
	},{
		label : '检验员 ',
		name : 'CREATOR',
		align : "center",
		index : 'CREATOR',
		width : '90'
	},{
		label : '检验日期 ',
		name : 'TEST_DATE',
		align : "center",
		index : 'TEST_DATE',
		width : '160'
	},{
		name : 'BUS_NO',
		index : 'BUS_NO',
		hidden: true
	},
	];

	dataGrid = $("#dataGrid").dataGrid(
					{
						searchForm : $("#searchForm"),
						colModel : columns,
						viewrecords: true,
						rownumbers:false,
					    shrinkToFit:true,
					    autowidth:true,
						rowNum: 15,  
						rowList : [15,30 ,50],	
						dataid:'id',
					});
}
