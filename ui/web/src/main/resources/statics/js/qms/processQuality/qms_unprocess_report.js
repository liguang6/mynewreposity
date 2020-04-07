var vm = new Vue({
	el : '#page_div',
	data : {
		WERKS:'',
		TEST_TYPE:'',
		TEST_NODE:'',
		TEST_NODE_LIST:[],
		DATA_LIST :[],
		TARGET_LIST:[],
		X_LIST :[],
		WEIDU:'RECTIFY_UNIT',
		showChart:false,
	},
	watch:{
		TEST_TYPE : {
			handler : function(newVal, oldVal) {
				vm.TEST_NODE_LIST = vm.getTestNodes();
				//vm.query();
				vm.$nextTick(function(){
					$("#TEST_NODE").multipleSelect('destroy').multipleSelect({
				        selectAll: true,
				    });
				})
				
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
			if($("#WERKS").val()==null ||$("#WERKS").val().length==0){
				js.showErrorMessage("请选择工厂！");
				return false;
			}
			if($("#TEST_NODE").val()==null ||$("#TEST_NODE").val().length==0){
				js.showErrorMessage("请选择检验节点！");
				return false;
			}
			vm.showChart=true;
			vm.X_LIST=$("#TEST_NODE").val();
			
			$.ajax({
				url:baseUrl + "processQuality/test/getUnProcessReportData",
				dataType:'json',
				method:'post',
				async:false,
				data:{
					WERKS:$("#WERKS").val(),
					TEST_TYPE:vm.TEST_TYPE,
					TEST_NODE:$("#TEST_NODE").multipleSelect('getSelects').join(","),
					START_DATE:$("#start_date").val(),
					END_DATE:$("#end_date").val(),
					WEIDU:vm.WEIDU,
				},
				success:function(result){
					vm.showChart=true;
					vm.DATA_LIST=result.dataList;
					generateChart(vm.DATA_LIST);
					
				},
				error:function(){
					js.showErrorMessage("抱歉，系统异常!")
				}
			})
		}
	},
	created:function(){		
		//this.WERKS = $("#WERKS").find("option").first().val();
		this.TEST_TYPE='01'
	}
})	

$(function(){
	var cd=new Date();
	l_y = cd.getFullYear();
	l_m = cd.getMonth();
	l_d=1;
	$("#start_date").val(formatDate(new Date(l_y,l_m,l_d)))
	$("#end_date").val(getCurDate());
	
/*	$("#WERKS").multipleSelect({
        selectAll: true,
    });*/
	
	//vm.query();
})


function generateChart(dataList){
	
	var title = "整改单位汇总图";	
	if(vm.WEIDU=='FAULT_TYPE'){
		title = "问题分类汇总图";	
	}
	var xAxis = {
			categories : vm.X_LIST,
			crosshair: true,
			labels: {
                autoRotationLimit: 80
            }
		};

	var yAxis = {     
		  title:'',
		  stackLabels: {  // 堆叠数据标签
	            enabled: true,
	            style: {
	            	fontSize: '11px',
	                color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
	            }
	        }
	};
	
	var plotOptions ={
			column: {
	            stacking: 'normal',
	            dataLabels: {
	                enabled: false,
	                color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white',
	                style: {
	                    // 如果不需要数据标签阴影，可以将 textOutline 设置为 'none'
	                    textOutline: '1px 1px black'
	                }
	            }
	        }
	}
	
	var series=[];
	
	$.each(dataList,function(i,d){
		var s={
				type: 'column',
				name:d.ITEM,
				data:d.DATA,
				maxPointWidth:50,
		}
		series.push(s);
	})
	
	
	drowCharts("#chartsContainer", title, xAxis, series, yAxis,plotOptions);
}


function drowCharts(container, title, xAxis, series, yAxis,plotOptions) {
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
		tooltip: {
			// head + 每个 point + footer 拼接成完整的 table
			headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
			pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
			'<td style="padding:0"><b>{point.y} </b></td></tr>',
			footerFormat: '</table>',
			shared: true,
			useHTML: true
		},
		plotOptions:plotOptions||{},
		labels : {},
		series : series
	});
}


