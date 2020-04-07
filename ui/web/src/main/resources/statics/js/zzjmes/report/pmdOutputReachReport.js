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
		use_workshop_list: [],
		use_workshop: '',
		status: '',
		prod_process: '',
    },
	created:function(){	
		var order_no=getUrlKey("order_no");
		var werks=getUrlKey("werks");
		var status=getUrlKey("status");
		var prod_process = getUrlKey("prod_process");
		if(undefined != status && 'undefined' != status && null != status && status!=''){
			//$("#status option[value='"+status+"']").prop("selected",true);
			this.status = status;
		}
		if(undefined != prod_process && 'undefined' != prod_process && null != prod_process && prod_process!=''){
			this.prod_process = prod_process;
		}
		if(undefined != order_no && 'undefined' != order_no && null != order_no && order_no!=''){
			this.order_no = order_no;
		}
		if(undefined != werks && 'undefined' != werks && null != werks && werks!=''){
			this.werks = werks;
		}else{
			this.werks = $("#werks").find("option").first().val();
		}
	},
	watch:{
		werks : {
		  handler:function(newVal,oldVal){
			  var workshop=getUrlKey("workshop");
			$.ajax({
				url:baseURL+"masterdata/getUserWorkshopByWerks",
				dataType : "json",
				type : "post",
				async : false,
				data : {
					"WERKS":newVal,
					"MENU_KEY":'ZZJMES_PMD_OUTPUT_REACH_REPORT',
				},
				async: true,
				success: function (response) { 
					vm.workshop_list=response.data;
					if(undefined != workshop && 'undefined' != workshop && null != workshop && workshop!=''){
						vm.workshop = workshop;
					}else{
						if(response.data && response.data.length &&  response.data.length>0){
							vm.workshop=response.data[0]['code'];
						}
					}

					if(undefined != vm.workshop && 'undefined' != vm.workshop && null != vm.workshop && vm.workshop!=''){
						var line=getUrlKey("line");
						$.ajax({
							url:baseURL+"masterdata/getUserLine",
							dataType : "json",
							type : "post",
							async : false,
							data : {
								"WERKS":vm.werks,
								"WORKSHOP": vm.workshop,
								"MENU_KEY":'ZZJMES_PMD_OUTPUT_REACH_REPORT',
							},
							async: true,
							success: function (response) { 
								vm.line_list=response.data;
								if(undefined != line && 'undefined' != line && null != line && line!=''){
									vm.line = line;
								}else{
									if(response.data && response.data.length &&  response.data.length>0){
										vm.line=response.data[0]['code'];
									}
								}
							}
						});							
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
			
		  }
		},
		workshop: {
			handler:function(newVal,oldVal){
				var line=getUrlKey("line");
				$.ajax({
					url:baseURL+"masterdata/getUserLine",
					dataType : "json",
					type : "post",
					async : false,
					data : {
						"WERKS":vm.werks,
						"WORKSHOP":newVal,
						"MENU_KEY":'ZZJMES_PMD_OUTPUT_REACH_REPORT',
					},
					async: true,
					success: function (response) { 
						vm.line_list=response.data;
						if(undefined != line && 'undefined' != line && null != line && line!=''){
							vm.line = line;
						}else{
							if(response.data && response.data.length &&  response.data.length>0){
								vm.line=response.data[0]['code'];
							}
						}
					}
				});	
			}
		},
		line: {
			handler: function(newVal,oldVal){
				var line=getUrlKey("line");
				if(undefined != line && 'undefined' != line && null != line && line!=''){
					vm.$nextTick(function(){
						vm.query();
					})
				}
			}
		}
	},
	methods: {
		getOrderNoFuzzy:function(){
			if($("#werks").val() === ''){
				alert('请先选择工厂！');
				return false;
			}
			getZZJOrderNoSelect("#search_order",null,fn_cb_getOrderInfo_search,"#werks");
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
			$("#searchForm").attr("action",baseUrl + "zzjmes/report/pmdOutputReachReport");
			$('#dataGrid').dataGrid({
				searchForm: $("#searchForm"),
				columnModel: [
		            {label: '序号', name: 'no',index:"no", width: "50",align:"center",sortable:false,editable:false,},
		            {label: '图号', name: 'material_no',index:"material_no", width: "120",align:"center",sortable:true,editable:false,},
		            {label: '零部件号', name: 'zzj_no',index:"zzj_no", width: "120",align:"center",sortable:true,editable:false,},
		            {label: '零部件名称', name: 'zzj_name',index:"zzj_name", width: "150",align:"center",sortable:false,editable:false},
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
		            {label: '生产工序', name: 'prod_process',index:"prod_process", width: "70",align:"center",sortable:false,editable:false},
		            {label: '需求数量', name: 'pmd_req_qty',index:"pmd_req_qty", width: "70",align:"center",sortable:false,editable:false},
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
		            {label: '装配位置', name: 'assembly_position',index:"assembly_position", width: "200",align:"center",sortable:true,},
		            {label: '使用工序', name: 'process',index:"process", width: "70",align:"center",sortable:true,editable:false,}, //frozen: true
		            {label: '工段', name: 'section',index:"section", width: "120",align:"center",sortable:false,editable:false},
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
			$("#exportForm").attr("action",baseUrl + "zzjmes/report/pmdOutputReachReportExport");
			$("#exportForm").unbind("submit"); // 移除jqgrid的submit事件,否则会执行dataGrid的查询
			$("#exportForm").submit();	
		},	
		showTable:function(mydata){
			$("#dataGrid").dataGrid({
				datatype: "local",
				data: mydata,
				colModel: [	   
		            {label: '序号', name: 'no',index:"no", width: "50",align:"center",sortable:false,editable:false,},
		            {label: '图号', name: 'material_no',index:"material_no", width: "120",align:"center",sortable:true,editable:false,},
		            {label: '零部件号', name: 'zzj_no',index:"zzj_no", width: "130",align:"center",sortable:true,editable:false,},
		            {label: '零部件名称', name: 'zzj_name',index:"zzj_name", width: "150",align:"center",sortable:true,editable:false},
		            {label: '工艺流程', name: 'process_flow',index:"process_flow", width: "200",align:"center",sortable:false,editable:false,},
		            {label: '生产工序', name: 'prod_process',index:"prod_process", width: "70",align:"center",sortable:true,editable:false},
		            {label: '需求数量', name: 'pmd_req_qty',index:"pmd_req_qty", width: "70",align:"center",sortable:false,editable:false},
		            {label: '计划数量', name: 'plan_quantity',index:"plan_quantity", width: "70",align:"center",sortable:false,},
		            {label: '生产数量', name: 'prod_quantity',index:"prod_quantity", width: "70",align:"center",sortable:false,},
		            {label: '欠产数量', name: 'un_prod_quantity',index:"un_prod_quantity", width: "70",align:"center",sortable:false,},
		            {label: '装配位置', name: 'assembly_position',index:"assembly_position", width: "200",align:"center",sortable:true,},
		            {label: '使用工序', name: 'process',index:"process", width: "70",align:"center",sortable:true,editable:false,}, //frozen: true
		            {label: '工段', name: 'section',index:"section", width: "120",align:"center",sortable:true,editable:false},
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

	}
});

$(function () {
	var werks=getUrlKey("werks");
	if(undefined == werks || 'undefined' == werks || null == werks || werks==''){
		vm.showTable();
	}
	
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

function getUrlKey(name){
	return decodeURIComponent((new RegExp('[?|&]'+name+'='+'([^&;]+?)(&|#|;|$)').exec(location.href)||[,""])[1].replace(/\+/g,'%20'))||null;
}