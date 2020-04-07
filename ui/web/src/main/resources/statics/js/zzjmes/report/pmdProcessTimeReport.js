var mydata = [];
var vm = new Vue({
	el:'#rrapp',
	data:{
		werks:"",
		workshop:"",
		workshop_list :[],
		line:"",
		line_list :[],
		order_no: '',
		batchplanlist: [],
		zzj_plan_batch: '',
		prod_process: '',
		tab_id: '',
		formUrl: '#',
		columnModel: [],
		report_type: ''
    },
	created:function(){	
		this.werks = $("#werks").find("option").first().val();
		this.tab_id="#div_1";
		this.report_type = 'order';
	},
	watch:{
		tab_id : {
			handler : function(newVal,oldVal){
				var initFlag = false;
				$("#gbox_dataGrid"+oldVal.replace("#","_")).css("display","none");
				$("#gbox_dataGrid"+newVal.replace("#","_")).css("display","");
				if(newVal == "#div_1"){// 加工时间
					if(vm.formUrl ==='#'){
						initFlag = true;
					}
					vm.formUrl = baseUrl + "zzjmes/report/pmdProcessTimeReport";
					vm.report_type = "order";
					vm.columnModel = [	   
						{label: '图号', name: 'material_no',index:"material_no", width: "120",align:"center",sortable:true,editable:false,},
						{label: '零部件号', name: 'zzj_no',index:"zzj_no", width: "120",align:"center",sortable:true,editable:false,},
						{label: '零部件名称', name: 'zzj_name',index:"zzj_name", width: "150",align:"center",sortable:false,editable:false},
						{label: '批次', name: 'batch',index:"batch", width: "50",align:"center",sortable:true,editable:false,},
						{label: '车付数', name: 'batch_qty',index:"batch_qty", width: "55",align:"center",sortable:false,editable:false},
						{label: '工艺流程', name: 'process_flow',index:"process_flow", width: "200",align:"center",sortable:false,editable:false,formatter:function(val, obj, row, act){
							if(undefined != row.current_process_name &&  null != row.current_process_name && row.current_process_name !=""){
								var process_arr = [];
								val.split("-").forEach(function (element, index) {
												if(element == row.current_process_name){
													process_arr.push("<span style='color:red;font-size:13px;font-weight:bold'>"+element+"</span>");
												}else{
													process_arr.push(element);
												}
												
											  });
								return process_arr.join("-");
							}else{
								return val;
							}
	
						}},
						{label: '加工工时', name: 'process_time',index:"process_time", width: "120",align:"center",sortable:false,editable:false,},
						{label: '生产工序', name: 'prod_process',index:"prod_process", width: "70",align:"center",sortable:false,editable:false},
						{label: "<span style='color:blue;font-size:13px;font-weight:bold'>加工时长</span>", name: 'product_time',index:"product_time", width: "70",align:"center",},
						{label: '需求数量', name: 'pmd_batch_req_qty',index:"pmd_batch_req_qty", width: "70",align:"center",sortable:false,editable:false},
						{label: '计划数量', name: 'plan_quantity',index:"plan_quantity", width: "70",align:"center",sortable:false,},
						{label: '生产数量', name: 'prod_quantity',index:"prod_quantity", width: "70",align:"center",sortable:false,},
						{label: '欠产数量', name: 'un_prod_quantity',index:"un_prod_quantity", width: "70",align:"center",sortable:false,formatter:function(val, obj, row, act){
							if(val==0){
								return 0
							}else{
								var html="<span style='color:red;font-size:13px;font-weight:bold'>"+val+"</span>";
								return html;
							}
	
						}},
						{label: '当前工序', name: 'current_process_name',index:"current_process_name", width: "70",align:"center",},          
						{label: '装配位置', name: 'assembly_position',index:"assembly_position", width: "200",align:"center",sortable:true,},
						{label: '使用工序', name: 'process',index:"process", width: "70",align:"center",sortable:true,editable:false,}, //frozen: true
						{label: '订单', name: 'order_desc',index:"order_desc", width: "200",align:"center",sortable:false,editable:false},
			        ];
				}
				if(newVal == "#div_2"){// 流转时间
					vm.formUrl = baseUrl + "zzjmes/report/pmdProcessTransferTimeReport";
					vm.report_type = 'use_workshop';
					vm.columnModel = [	   
						{label: '零部件号', name: 'zzj_no',index:"zzj_no", width: "120",align:"center",sortable:true,editable:false,},
						{label: '零部件名称', name: 'zzj_name',index:"zzj_name", width: "150",align:"center",sortable:false,editable:false},
						{label: '批次', name: 'batch',index:"batch", width: "50",align:"center",sortable:true,editable:false,},
						{label: '工艺流程', name: 'process_flow',index:"process_flow", width: "200",align:"center",sortable:false,editable:false,},
						{label: '加工工时', name: 'process_time',index:"process_time", width: "120",align:"center",sortable:false,editable:false,},
						{label: '前工序', name: 'pre_prod_process',index:"pre_prod_process", width: "70",align:"center",sortable:false,editable:false},
						{label: "<span style='color:blue;font-size:13px;font-weight:bold'>流入时长</span>", name: 'process_in_time',index:"process_in_time", width: "70",align:"center",},
						{label: '生产工序', name: 'prod_process',index:"prod_process", width: "70",align:"center",sortable:false,editable:false},
						{label: "<span style='color:blue;font-size:13px;font-weight:bold'>加工时长</span>", name: 'product_time',index:"product_time", width: "70",align:"center",},
						{label: "<span style='color:blue;font-size:13px;font-weight:bold'>流出时长</span>", name: 'process_out_time',index:"process_out_time", width: "70",align:"center",},
						{label: '后工序', name: 'next_prod_process',index:"next_prod_process", width: "70",align:"center",sortable:false,editable:false},
						{label: "<span style='color:blue;font-size:13px;font-weight:bold'>流转时长</span>", name: 'process_circulation_time',index:"process_circulation_time", width: "70",align:"center",},
						{label: '装配位置', name: 'assembly_position',index:"assembly_position", width: "200",align:"center",sortable:true,},
						{label: '使用工序', name: 'process',index:"process", width: "70",align:"center",sortable:true,editable:false,}, //frozen: true
						{label: '订单', name: 'order_desc',index:"order_desc", width: "200",align:"center",sortable:false,editable:false},
					];
				}
				vm.$nextTick(function(){
					if(initFlag){
						vm.showTable();
					}else{
						vm.query();
					}
					
				})
				
			}
		},
		werks : {
		  handler:function(newVal,oldVal){
			$.ajax({
				url:baseURL+"masterdata/getUserWorkshopByWerks",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":newVal,
					"MENU_KEY":'ZZJMES_ORDER_BATCH_REACH_REPORT',
				},
				async: true,
				success: function (response) { 
					vm.workshop_list=response.data;
					if(response.data && response.data.length &&  response.data.length>0){
						vm.workshop=response.data[0]['code'];
						$.ajax({
							url:baseURL+"masterdata/getUserLine",
							dataType : "json",
							type : "post",
							data : {
								"WERKS":vm.werks,
								"WORKSHOP": vm.workshop,
								"MENU_KEY":'ZZJMES_ORDER_BATCH_REACH_REPORT',
							},
							async: true,
							success: function (response) { 
								vm.line_list=response.data;
								if(response.data && response.data.length &&  response.data.length>0){
									vm.line=response.data[0]['code'];
								}else{
									vm.line = '';
								}
							}
						});	
					}else{
						vm.workshop = '';
					}
					vm.getBatchSelects();
				}
			});	
			
		  }
		},
		workshop: {
			handler:function(newVal,oldVal){
				$.ajax({
					url:baseURL+"masterdata/getUserLine",
					dataType : "json",
					type : "post",
					data : {
						"WERKS":vm.werks,
						"WORKSHOP":newVal,
						"MENU_KEY":'ZZJMES_ORDER_BATCH_REACH_REPORT',
					},
					async: true,
					success: function (response) { 
						vm.line_list=response.data;
						if(response.data && response.data.length &&  response.data.length>0){
							vm.line=response.data[0]['code'];
						}else{
							vm.line = '';
						}
						vm.getBatchSelects();
					}
				});	
			}
		}
	},
	methods: {
		getOrderNoFuzzy:function(){
			if($("#werks").val() === ''){
				alert('请先选择工厂！');
				return false;
			}
			getZZJOrderNoSelect("#order_no",null,fn_cb_getOrderInfo_search,"#werks");
		},
		moreZzjNo: function() {
			fun_ShowMoreTable();
			dataGridMorePaste();
			var orders = "";
			layer.open({
	    		type : 1,
	    		offset : '50px',
	    		title : "批量输入零部件号：",
	    		area : [ '500px', '350px' ],
	    		shadeClose : false,
	    		content : jQuery("#moreZzjNoLayer"),
	    		btn : [ '确定'],
	    		btn1 : function(index) {
	    			layer.close(index);
	    			var trs=$("#dataGrid_1").children("tbody").children("tr");
	    			$.each(trs,function(index,tr){
	    				if(index>0&&($(tr).find("td").eq(1).text()!=" "&&$(tr).find("td").eq(1).text()!=""))orders += $(tr).find("td").eq(1).text() + ",";
	    			});
	    			$("#zzj_no").val(orders.substring(0,orders.length-1));
	    		}
			});
		},
		getBatchSelects:function(selectval){
			if(vm.werks =='' || vm.workshop == '' || vm.line == '' || vm.order_no == ''){
				vm.batchplanlist = [];
				return false;
			}
			$.ajax({
				type : "post",
				dataType : "json",
				async : false,
				url :baseUrl+ "/zzjmes/common/getPlanBatchList",
				data : {
					"werks" : vm.werks,
					"workshop" : vm.workshop,
					"line" : vm.line,
					"order_no": vm.order_no
				},
				success:function(response){
					vm.batchplanlist = response.data;
				}	
			});
		},
		query:function(){
           	if(vm.werks==''){
         		js.showErrorMessage('请选择工厂！');
        		return false;
        	}
           	if(vm.workshop==''){
           		js.showErrorMessage('请选择车间！');
        		return false;
        	}
           	if(vm.line==''){
           		js.showErrorMessage('请选择线别！');
        		return false;
        	}
         	if(vm.order_no==''){
           		js.showErrorMessage('请选择订单！');
        		return false;
        	}
			jQuery("#dataGrid").GridUnload();
			$("#searchForm").attr("action",vm.formUrl);
			$("#dataGrid").dataGrid({
				searchForm: $("#searchForm"),
				columnModel: vm.columnModel,	
				viewrecords: true,
		        shrinkToFit:false,
		        showCheckbox:false,
		        multiselect: false,
				rowNum: 15,  
				rowList : [15,50,100,200],
			});
		},
		enter:function(e){
			vm.query();
		},
		//业务初始化表格
		showTable:function(mydata){
			$("#dataGrid").dataGrid({
				datatype: "local",
				data: mydata,
				colModel: [	   
		            {label: '图号', name: 'material_no',index:"material_no", width: "120",align:"center",sortable:true,editable:false,},
		            {label: '零部件号', name: 'zzj_no',index:"zzj_no", width: "130",align:"center",sortable:true,editable:false,},
		            {label: '零部件名称', name: 'zzj_name',index:"zzj_name", width: "150",align:"center",sortable:true,editable:false},
		            {label: '批次', name: 'batch',index:"batch", width: "50",align:"center",sortable:true,editable:false,},
		            {label: '车付数', name: 'batch_qty',index:"batch_qty", width: "55",align:"center",sortable:false,editable:false},
		            {label: '工艺流程', name: 'process_flow',index:"process_flow", width: "200",align:"center",sortable:false,editable:false,},
					{label: '加工工时', name: 'process_time',index:"process_time", width: "120",align:"center",sortable:false,editable:false,},
					{label: '生产工序', name: 'prod_process',index:"prod_process", width: "70",align:"center",sortable:true,editable:false},
					{label: "<span style='color:blue;font-size:13px;font-weight:bold'>加工时长</span>", name: 'product_time',index:"product_time", width: "70",align:"center",},
					{label: '需求数量', name: 'pmd_batch_req_qty',index:"pmd_batch_req_qty", width: "70",align:"center",sortable:false,editable:false},
		            {label: '计划数量', name: 'plan_quantity',index:"plan_quantity", width: "70",align:"center",sortable:false,},
		            {label: '生产数量', name: 'prod_quantity',index:"prod_quantity", width: "70",align:"center",sortable:false,},
		            {label: '欠产数量', name: 'un_prod_quantity',index:"un_prod_quantity", width: "70",align:"center",sortable:false,},
		            {label: '当前工序', name: 'current_process_name',index:"current_process_name", width: "70",align:"center",},
		            {label: '装配位置', name: 'assembly_position',index:"assembly_position", width: "200",align:"center",sortable:true,},
		            {label: '使用工序', name: 'process',index:"process", width: "70",align:"center",sortable:true,editable:false,}, //frozen: true
		        	{label: '订单', name: 'order_desc',index:"order_desc", width: "200",align:"center",sortable:false,editable:false},
		        ],
		        loadComplete:function(){
					js.closeLoading();
					$(".btn").attr("disabled", false);
				},
				viewrecords: true,
		        shrinkToFit:false,
		        showCheckbox:false,
		        multiselect: false,
				rowNum: 15,  
				rowList : [15,30,100,200],
				cellsubmit:'clientArray',
		    });	
		},
		pmdSearch: function (order_no) {
			js.addTabPage(null, '<i class="fa fa-plus" style="font-size:12px">下料明细查询</i>',
			'zzjmes/product/pmdSearch.html?order_no='+order_no+'&werks='+vm.werks+'&workshop='+vm.workshop+'&line='+vm.line, 
			true, true);
		},
		machinePlanQuery: function (order_no,zzj_plan_batch) {
			js.addTabPage(null, '<i class="fa fa-plus" style="font-size:12px">机台计划查询</i>',
			'zzjmes/product/machinePlanQuery.html?order_no='+order_no+'&werks='+vm.werks+'&workshop='+vm.workshop+'&line='+vm.line+'&zzj_plan_batch='+zzj_plan_batch, 
			true, true);
		},
		batchOutputReachReport: function (order_no,status,zzj_plan_batch) {
			var last_process = '';
			if(vm.prod_process == ''){
				last_process = 'LAST';
			}
			js.addTabPage(null, '<i class="fa fa-plus" style="font-size:12px">批次加工进度</i>',
			'zzjmes/report/batchOutPutReachReport.html?order_no='+order_no+'&werks='+vm.werks+'&workshop='+vm.workshop+
			'&line='+vm.line+'&status='+status+'&zzj_plan_batch='+zzj_plan_batch+'&prod_process='+vm.prod_process+'&last_process='+last_process, 
			true, true);
		},
	}
});

$(function () {
	//设置默认日期，当前日期往前一个月
/*	var end_date = new Date().toLocaleDateString().replace(/\//g,"-");
	var start_date = GetDateStr(-30);//获取退后一个月的日期
	$("#start_date").val(start_date);
	$("#end_date").val(end_date);*/
	/**
	 * 切换Tab
	 */
	$(document).on("click","#myTab>li",function(e){
		var tag_name=$(e.target).prop("tagName");
		console.info("点击的元素:"+tag_name)
		if(tag_name=="I"){
			return false;
		}
		if(tag_name=="A"){
			vm.tab_id=$(e.target).attr("href")
		}else{
			vm.tab_id=$(e.target).children("a").attr("href")
		}
		
	})

	$("#newOperation_1").click(function () {
		var trMoreindex = $("#dataGrid_1").children("tbody").children("tr").length;
		var addtr = '<tr id = "'+trMoreindex+'"  role="row" tabindex="-1" class="ui-widget-content jqgrow jqgrow ui-row-ltr '+((trMoreindex%2 == 0)?"ui-priority-secondary":"")+'" aria-selected = "false">'+
		'<td role="gridcell" class="ui-state-default jqgrid-rownum" style="text-align:center;" title="2" aria-describedby="dataGrid_rn">' + trMoreindex + 
		'</td><td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_1_"></td>'+
		'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_1_"><i title="删除" aria-hidden="true" onclick="$(this).parent().parent().remove();delMoreTr();" class="delMoreTr fa fa-times"></i></td><td></td></tr>'
		$("#dataGrid_1 tbody").append(addtr);
	});
	$("#newReset_1").click(function () {
		$("#dataGrid_1").jqGrid('GridUnload');
		fun_ShowMoreTable();
		dataGridMorePaste();
	});

});

function fn_cb_getOrderInfo_search(order){
	vm.order_no = order.order_no;
	vm.getBatchSelects();
}

function GetDateStr(AddDayCount) { 
	   var dd = new Date();
	   dd.setDate(dd.getDate()+AddDayCount);//获取AddDayCount天后的日期
	   var y = dd.getFullYear(); 
	   var m = (dd.getMonth()+1)<10?"0"+(dd.getMonth()+1):(dd.getMonth()+1);//获取当前月份的日期，不足10补0
	   var d = dd.getDate()<10?"0"+dd.getDate():dd.getDate();//获取当前几号，不足10补0
	   return y+"-"+m+"-"+d; 
}

function funFromjs(result) {
	result = result.replace("{","").replace("}","")
	var inventoryNo = result.split(",")[0].split(":")[1];

	var e = $.Event('keydown');
	e.keyCode = 13;
	$(last_scan_ele).val(inventoryNo).trigger(e);
	vm.TEST_NODE=""
}

function doScan(ele) {
	var url = "../../doScan";
	last_scan_ele = $("#" + ele);
	if(IsPC() ){
		$(last_scan_ele).focus();
		return false;
	}
	var iframe_id=self.frameElement.getAttribute('id');
	$(window.parent.document).find("#activeIframe").val(iframe_id)
	var a = document.createElement("a");
	a.target="_parent";
	a.href = url;
	a.click();
}


function fun_ShowMoreTable(){
	$("#dataGrid_1").dataGrid({
		datatype: "local",
		data: [{id:0},{id:1}],
        colModel: [			
            {label: '<span style="color:red">*</span><span style="color:blue"><b>零部件号</b></span>', name: 'zzj_no',index:"zzj_no", width: "200",align:"center",sortable:false,editable:true}, 
            {label: '删除', name: 'AUFNR',index:"AUFNR", width: "80",align:"center",sortable:false,editable:false,
            	formatter:function(value, name, record){
            		return '<i title="删除" aria-hidden="true" onclick="$(this).parent().parent().remove();delMoreTr();" class="fa fa-times"></i>';
            	}, 
            },
            {label: '', name: '',index:"AUFNR", width: "150",align:"center",sortable:false,editable:false} 
        ],
		shrinkToFit: false,
        width:600,
        cellEdit:true,
        cellurl:'#',
		cellsubmit:'clientArray',
        showCheckbox:false
    });
}

function dataGridMorePaste(){
	$('#dataGrid_1').bind('paste', function(e) {
		e.preventDefault(); // 消除默认粘贴
		// 获取粘贴板数据
		var data = null;
		var clipboardData = window.clipboardData || e.originalEvent.clipboardData; // IE || chrome
		data = clipboardData.getData('Text');
		// 解析数据
		var arr = data.split('\n').filter(function(item) { // 兼容Excel行末\n，防止出现多余空行
			return (item !== "")
		}).map(function(item) {
			return item.split("\t");
		});
		// 输出至网页表格
		var tab = this; // 表格DOM
		var td = $(e.target).parents('td');
		var startRow = td.parents('tr')[0].rowIndex;
		var startCell = td[0].cellIndex;
		var rows = tab.rows.length; // 总行数
		for (var i = 0; i < arr.length; i++) {
			if(startRow + i >= rows){
				$("#newOperation_1").click();
			}
			var cells = tab.rows[startRow + i].cells.length; // 该行总列数
			for (var j = 0; j < arr[i].length && startCell + j < cells; j++) {
				var cell = tab.rows[startRow + i].cells[startCell + j];
				$(cell).find(':text').val(arr[i][j]); //找到cell下的input:text，设置value
				$('#dataGrid_1').jqGrid("saveCell", startRow + i, 1);
				$(cell).html(arr[i][j]);
			}
		}
	});
}