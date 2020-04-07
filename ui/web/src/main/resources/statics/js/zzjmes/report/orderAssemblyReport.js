var mydata = [];
var vm = new Vue({
	el:'#rrapp',
	data:{
		werks:"",
		workshop:"",
		workshop_list :[],
		line:"",
		line_list :[],
		batchplanlist: [],
		zzj_plan_batch :'',
		order_no: '',
		status: '',
    },
	created:function(){	
		this.werks = $("#werks").find("option").first().val();
	},
	watch:{
		werks : {
		  handler:function(newVal,oldVal){
			$.ajax({
				url:baseURL+"masterdata/getUserWorkshopByWerks",
				dataType : "json",
				type : "post",
				async : false,
				data : {
					"WERKS":newVal,
					"MENU_KEY":'ZZJMES_ORDER_ASSEMBLY_REPORT',
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
							async : false,
							data : {
								"WERKS":vm.werks,
								"WORKSHOP": vm.workshop,
								"MENU_KEY":'ZZJMES_ORDER_ASSEMBLY_REPORT',
							},
							success: function (response) { 
								vm.line_list=response.data;
								if(response.data && response.data.length &&  response.data.length>0){
									vm.line = response.data[0]['code'];
								}else{
									vm.line = '';
								}
							}
						});						

					}else{
						vm.workshop = '';
					}
				}
			});	
			
			//根据工厂获取所有车间
			$.ajax({
				url:baseURL+"masterdata/getWerksWorkshopList",
				dataType : "json",
				type : "post",
				async : false,
				data : {
					"WERKS":newVal,
				},
				async: true,
				success: function (response) { 
					vm.use_workshop_list=response.data;
				}
			});	
			vm.getBatchSelects();
		  }
		},
		workshop: {
			handler:function(newVal,oldVal){
				$.ajax({
					url:baseURL+"masterdata/getUserLine",
					dataType : "json",
					type : "post",
					async : false,
					data : {
						"WERKS":vm.werks,
						"WORKSHOP":newVal,
						"MENU_KEY":'ZZJMES_ORDER_ASSEMBLY_REPORT',
					},
					async: true,
					success: function (response) { 
						vm.line_list=response.data;
						if(response.data && response.data.length &&  response.data.length>0){
							vm.line=response.data[0]['code'];
						}
					}
				});	
				vm.getBatchSelects();
			}
		},
	},
	methods: {
		getOrderNoFuzzy:function(){
			if($("#werks").val() === ''){
				alert('请先选择工厂！');
				return false;
			}
			getZZJOrderNoSelect("#search_order",null,fn_cb_getOrderInfo_search,"#werks");
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
			jQuery('#dataGrid').GridUnload();
			$("#exportForm").attr("id","searchForm");
			$("#searchForm").attr("action",baseUrl + "zzjmes/report/orderAssemblyReport");
			$('#dataGrid').dataGrid({
				searchForm: $("#searchForm"),
				columnModel: [
		            {label: '图号', name: 'material_no',index:"material_no", width: "120",align:"center",sortable:true,editable:false,},
		            {label: '零部件号', name: 'zzj_no',index:"zzj_no", width: "120",align:"center",sortable:true,editable:false,},
		            {label: '零部件名称', name: 'zzj_name',index:"zzj_name", width: "150",align:"center",sortable:false,editable:false},
		            {label: '批次', name: 'batch',index:"batch", width: "50",align:"center",sortable:true,editable:false,},
		            {label: '车付数', name: 'batch_qty',index:"batch_qty", width: "55",align:"center",sortable:false,editable:false},
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
					{label: '需求子件种类', name: 'component_req_quantity',index:"component_req_quantity", width: "100",align:"center",							
						formatter:function(val, obj, row, act){
							var actions = [];
							var link = '<a onClick=vm.pmdSearch("'+row.order_no+'","'+row.zzj_no+'","'+row.zzj_name+'") title="下料明细" style="cursor: pointer;">'+val+'</a>';
							actions.push(link);
						    return actions.join('');
					}},
			        {label: '完成子件种类', name: 'component_finished_quantity',index:"component_finished_quantity", width: "100",align:"center",						
		            	formatter:function(val, obj, row, act){
							var actions = [];
							var link = '<a onClick=vm.batchOutputReachReport("'+row.order_no+'","ok","'+row.zzj_plan_batch+'","'+row.zzj_no+'","'+row.zzj_name+'") title="批次加工进度" style="cursor: pointer;">'+val+'</a>';
							actions.push(link);
						    return actions.join('');
					}},
			        {label: '欠产子件种类', name: 'component_un_finished_quantity',index:"component_un_finished_quantity", width: "100",align:"center",						
		            	formatter:function(val, obj, row, act){
							var actions = [];
							var link = "";
							if(val==0){
								link = '<a onClick=vm.batchOutputReachReport("'+row.order_no+'","ng","'+row.zzj_plan_batch+'","'+row.zzj_no+'","'+row.zzj_name+'") title="批次加工进度" style="cursor: pointer;">'+val+'</a>';
							}else{
								link = '<a onClick=vm.batchOutputReachReport("'+row.order_no+'","ng","'+row.zzj_plan_batch+'","'+row.zzj_no+'","'+row.zzj_name+'") title="批次加工进度" style="cursor: pointer;"><span style="color:red;font-size:13px;font-weight:bold">'+val+'</span></a>';
							}
							actions.push(link);
						    return actions.join('');
					}},		            
		        	{label: '订单', name: 'order_desc',index:"order_desc", width: "200",align:"center",sortable:false,editable:false},
		        ],	
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
		exp:function(){
			$("#searchForm").attr("id","exportForm");
			$("#exportForm").attr("action",baseUrl + "zzjmes/report/orderAssemblyReportExport");
			$("#exportForm").unbind("submit"); // 移除jqgrid的submit事件,否则会执行dataGrid的查询
			$("#exportForm").submit();	
		},	
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
		            {label: '需求数量', name: 'pmd_batch_req_qty',index:"pmd_batch_req_qty", width: "70",align:"center",sortable:false,editable:false},
		            {label: '计划数量', name: 'plan_quantity',index:"plan_quantity", width: "70",align:"center",sortable:false,},
		            {label: '生产数量', name: 'prod_quantity',index:"prod_quantity", width: "70",align:"center",sortable:false,},
		            {label: '欠产数量', name: 'un_prod_quantity',index:"un_prod_quantity", width: "70",align:"center",sortable:false,},
		            {label: '需求子件种类', name: 'component_req_quantity',index:"component_req_quantity", width: "100",align:"center",},
		            {label: '完成子件种类', name: 'component_finished_quantity',index:"component_finished_quantity", width: "100",align:"center",},
		            {label: '欠产子件种类', name: 'component_un_finished_quantity',index:"component_un_finished_quantity", width: "100",align:"center",},
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
				rowList : [15,50,100,200],
				cellsubmit:'clientArray',
		    });	
		},
		pmdSearch: function (order_no,zzj_no,zzj_name) {
			var assembly_position = zzj_no + "_" + zzj_name;
			js.addTabPage(null, '<i class="fa fa-plus" style="font-size:12px">下料明细查询</i>',
			'zzjmes/product/pmdSearch.html?order_no='+order_no+'&werks='+vm.werks+'&workshop='+vm.workshop+'&line='+vm.line+'&assembly_position='+assembly_position, 
			true, true);
		},		
		batchOutputReachReport: function (order_no,status,zzj_plan_batch,zzj_no,zzj_name) {
			var last_process = 'LAST';
			var assembly_position = zzj_no + "_" + zzj_name;
			js.addTabPage(null, '<i class="fa fa-plus" style="font-size:12px">批次加工进度</i>',
			'zzjmes/report/batchOutPutReachReport.html?order_no='+order_no+'&werks='+vm.werks+'&workshop='+vm.workshop+
			'&line='+vm.line+'&status='+status+'&zzj_plan_batch='+zzj_plan_batch+'&last_process='+last_process+'&assembly_position='+assembly_position, 
			true, true);
		},
	}
});

$(function () {
	vm.showTable();
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
