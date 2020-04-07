var vm = new Vue({
	el : '#page_div',
	data : {
		WERKS:'',
		TEST_TYPE:'',
		RESP_UNIT:'',
		RESP_UNIT_LIST:[],
		DATA_LIST :[],
		X_LIST :[],
		showChart:false,
		WEIDU:'',
		TABLE_HEADS : [],
	},
	watch:{
		WERKS : {
			handler :function(newVal,oldVal){
				vm.getRespUnitList()
			}
		},
		WEIDU : {
			handler : function(newVal,oldVal){
				if(newVal == 'workgroup' ){
					vm.TABLE_HEADS=['责任单位','班组','问题数','百分比','累计百分比']
				}
				if(newVal == 'faultType' ){
					vm.TABLE_HEADS=['车间','异常分类','问题数','百分比','累计百分比']
				}
				//vm.DATA_LIST=[];
				//vm.query();
			}
		}
	},
	methods:{
		getRespUnitList : function(){
			$.ajax({  
			         type : "post",  
			          url : baseUrl+"masterdata/getWerksWorkshopList",  
			          data : {
			        	  WERKS:vm.WERKS
			          },  
			          async : false,  
			          success : function(result){  
			        	  vm.RESP_UNIT_LIST=result.data; 
			          }  
			     }); 
		},
		query:function(){
			if($("#WERKS").val()==null ||$("#WERKS").val().length==0){
				js.showErrorMessage("请选择工厂！");
				return false;
			}
			if($("#RESP_UNIT").val()==null ||$("#RESP_UNIT").val().length==0){
				js.showErrorMessage("请选择责任单位！");
				return false;
			}
			vm.showChart=true;
			
			$.ajax({
				url:baseUrl + "processQuality/test/getFaultScatterData",
				dataType:'json',
				method:'post',
				async:false,
				data:{
					WERKS:vm.WERKS,
					TEST_TYPE:vm.TEST_TYPE,
					RESP_UNIT:vm.RESP_UNIT,
					START_DATE:$("#start_date").val(),
					END_DATE:$("#end_date").val(),
					WEIDU : vm.WEIDU,
				},
				success:function(result){
					if(!result.msg=="success"){
						js.showErrorMessage("抱歉，系统异常!")
						return false;
					}
					vm.showChart=true;
					vm.DATA_LIST=result.dataList;
					vm.X_LIST=result.X_LIST;
					generateChart(vm.DATA_LIST);
					
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
		this.WEIDU = 'workgroup';
	}
})	

$(function(){
	var cd=new Date();
	l_y = cd.getFullYear();
	l_m = cd.getMonth();
	l_d=1;
	$("#start_date").val(formatDate(new Date(l_y,l_m,l_d)))
	$("#end_date").val(getCurDate());

})


function generateChart(dataList){
	
	var title = vm.RESP_UNIT+"班组异常分布"
	if(vm.WEIDU == "faultType"){
		title=vm.RESP_UNIT+"问题点分类分布情况"
	}
	var xAxis = {
			categories : vm.X_LIST,
			crosshair: true,
			labels: {
                autoRotationLimit: 80
            }
		};

	var yAxis =[{     
		  title:'数量',
		  labels: {
			    formatter:function(){
			    	return this.value;
			  }
			}
	},{
		title:'百分比',
		max:100,
		labels: {
			    formatter:function(){
			    	return this.value+"%";
			  }
		},
		opposite: true
	}];
	
	var series=[];
	var data_column=[];
	var data_line = [];
	
	$.each(dataList,function(i,d){
		data_column.push(d.FAULT_NUM);
		data_line.push(d.TOTAL_RATE);
	})
	
	var s={
				type : 'column',
				name : '问题数',
				data : data_column,
				yAxis : 0,
				maxPointWidth : 50,
		}
	
	var s_line ={
		type : 'spline',
		name : '累计百分比',
		data : data_line,
		tooltip :  {
			valueSuffix: '%'
		},
		yAxis : 1,
	}
	
	series.push(s);
	series.push(s_line);
	
	drowCharts("#chartsContainer", title, xAxis, series, yAxis);
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
			shared: true
		},
		plotOptions:plotOptions||{},
		labels : {},
		series : series
	});
}


