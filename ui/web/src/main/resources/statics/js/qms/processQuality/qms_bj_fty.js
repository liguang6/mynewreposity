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
		showChart:false,
		WEIDU:'werks',
	},
	watch:{
		TEST_TYPE : {
			handler : function(newVal, oldVal) {
				vm.TEST_NODE_LIST = vm.getTestNodes();
				//vm.query();
				vm.$nextTick(function(){
					$("#TEST_NODE").multipleSelect('destroy').multipleSelect({
				        selectAll: true,
				    }).multipleSelect('uncheckAll');
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
					TEST_CLASS:'零部件'
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
			
			$.ajax({
				url:baseUrl + "processQuality/test/getBJFTYData",
				dataType:'json',
				method:'post',
				async:false,
				data:{
					WERKS:$("#WERKS").multipleSelect('getSelects').join(","),
					TEST_TYPE:vm.TEST_TYPE,
					TEST_NODE:$("#TEST_NODE").multipleSelect('getSelects').join(","),
					START_DATE:$("#start_date").val(),
					END_DATE:$("#end_date").val(),
					WEIDU : vm.WEIDU,
				},
				success:function(result){
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
	
	$("#WERKS").multipleSelect({
        selectAll: true,
    });
	
	$("#WEIDU").change(function(e){
		if($(e.target).val() == 'order' ){
			$("#WERKS").multipleSelect('destroy').multipleSelect({
		        selectAll: false,
		        single: true,
		    }).multipleSelect('uncheckAll');
		}
	if($(e.target).val() == 'werks' ){
			$("#WERKS").multipleSelect('destroy').multipleSelect({
		        selectAll: true,
		        //multiple:true,
		    }).multipleSelect('uncheckAll');
		}
	})
	//vm.query();
})


function generateChart(dataList){
	
	var title = "一次交检合格率";	
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
		title:'一次交检合格率',
		labels: {
			    formatter:function(){
			    	return this.value+"%";
			  }
		},
		opposite: true
	}];
	
	var series=[];
	
	$.each(dataList,function(i,d){
		var type='column';
		var tooltip = {
			valueSuffix: ''
		}
		var y_axis=0;
		
		if(d.ITEM=='一次交检合格率'){
			type='spline';
			 tooltip = {
				valueSuffix: '%'
			}
			 y_axis=1;
		}
		var s={
				type:type,
				name:d.ITEM,
				data:d.DATA,
				tooltip : tooltip,
				yAxis: y_axis,
				maxPointWidth:50,
		}
		series.push(s);
	})
	
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


