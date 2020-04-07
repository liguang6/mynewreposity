var mydata =[];
var lastrow,lastcell,lastcellname;
var last_scan_ele;
var vm = new Vue({
	el:'#rrapp',
	data:{
		table_items: [],
		werks: '',
		workshoplist: [],
		workshop: '',
		linelist: [],
		line: '',
		processList: [],
		process: '',
		order_no: '', //订单编号
		search_order: '', //查询订单
		batchplanlist: [],
		zzj_plan_batch: '',
		zzj_no: '',
		business_date: getCurDate(),
		vendor: '',
		total_weight: '0',
	},
	created:function(){
		this.werks=$("#werks").find("option").first().val();
		
	},
	methods: {
		//清空表格数据
		clearTable: function() {
			mydata = [];
			$("#dataGrid").jqGrid('clearGridData');
			//$("#dataGrid").jqGrid('setGridParam',{data: mydata,datatype:'local'}).trigger('reloadGrid'); 
		},
		getOrderNoFuzzy: function(){
			if($("#werks").val() === ''){
				alert('请先选择工厂！');
				return false;
			}
			getZZJOrderNoSelect("#search_order",null,fn_cb_getOrderInfo,"#werks");
		},
		getBatchSelects: function(){
			if(vm.werks == '' ||vm.workshop=='' || vm.line=='' || vm.search_order==''){
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
					"order_no": vm.search_order
				},
				success:function(response){
					vm.batchplanlist = response.data;
				}	
			});
		},
		queryMatInfo: function(){
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
           	if(vm.process==''){
           		js.showErrorMessage('请选择委外工序！');
        		return false;
        	}
         	if(vm.order_no==''){
           		js.showErrorMessage('请选择订单！');
        		return false;
        	}
        	if(vm.zzj_plan_batch==''){
           		js.showErrorMessage('请选择批次！');
        		return false;
			}
			if(vm.process == ''){
				js.showErrorMessage('请选择委外工序！');
				return false;
			}
			if(vm.zzj_no == ''){
				return false;
			}
           	//初始化工厂、车间、线别名称值
        	$('#werks_name').val($('#werks').find("option:selected").data("name"));
        	$('#workshop_name').val($('#workshop').find("option:selected").text());
        	$('#line_name').val($('#line').find("option:selected").text());
			
			$.ajax({
				url:baseUrl + "zzjmes/outsourcing/getOutsourcingMatInfo",
				data:{
					"werks": vm.werks,
					"workshop": vm.workshop,
					"line": vm.line,
					"order_no": vm.order_no,
					"zzj_plan_batch": vm.zzj_plan_batch,
					"zzj_no": vm.zzj_no,
					"process_name": $('#process').find("option:selected").text()
				},
				type:"post",
				success:function(response){
					if(response.code==500){
						js.showErrorMessage(response.msg);
						return false;
					}
					if(response.data){
						if(response.data.length <= 0){
							js.showMessage("无委外发货数据！");
							return false;
						}
						addMat(response.data);
					}
				},
			});

			vm.zzj_no = '';
		},
		btnSave: function(){
			//校验必填字段
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
        	if(vm.zzj_plan_batch==''){
           		js.showErrorMessage('请选择批次！');
        		return false;
        	}
        	if(vm.vendor==''){
           		js.showErrorMessage('请填写委外单位！');
        		return false;
        	}
        	
           	//初始化工厂、车间、线别名称值
        	$('#werks_name').val($('#werks').find("option:selected").data("name"));
        	$('#workshop_name').val($('#workshop').find("option:selected").text());
        	$('#line_name').val($('#line').find("option:selected").text());
			
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			
			js.loading("处理中...");
			$("#btnSave").html("保存中...");
			$("#btnSave").attr("disabled","disabled");
			
			var rows=$("#dataGrid").jqGrid('getRowData');
			if(rows.length <= 0){
				js.closeLoading();
				$("#btnSave").attr("disabled", false);
				js.showErrorMessage("没有需要保存的数据！");
			   return false;
			}
			
			$.ajax({
				type : "post",
				dataType: "json",
				async : true,
				url :baseUrl+ "zzjmes/outsourcing/saveOutsourcing",
				data : {
					"werks" : vm.werks,
					"werks_name": $('#werks_name').val(),
					"workshop" : vm.workshop,
					"workshop_name": $('#workshop_name').val(),
					"line" : vm.line,
					"line_name": $('#line_name').val(),
					"business_date": $('#business_date').val(),
					"total_weight": vm.total_weight,
					"vendor": vm.vendor,
					
					"matInfoList": JSON.stringify(rows),
				},
				success:function(response){
					$("#btnSave").html("保存");	
					$("#btnSave").removeAttr("disabled");
					js.closeLoading();
					if(response.code==500){
						js.showErrorMessage(response.msg);
						return false;
					}
					if(response.code==0){
						vm.clearTable();
						//js.showMessage("操作成功！");
			        	$("#resultMsg").html("操作成功！");
			        	$("#headInfo").val(JSON.stringify(response.headInfo));
			        	$("#matList").val(JSON.stringify(response.matList))
			        	layer.open({
			        		type : 1,
			        		offset : '50px',
			        		skin : 'layui-layer-molv',
			        		title : "发货成功",
			        		area : [ '500px', '250px' ],
			        		shade : 0,
			        		shadeClose : false,
			        		content : jQuery("#resultLayer"),
			        		btn : [ '确定'],
			        		btn1 : function(index) {
			        			layer.close(index);
								//$("#btnCreat").removeAttr("disabled");
			        		}
			        	});
					}
					
				}	
			});
			
		},
	},
	watch: {
		werks: {
			handler:function(newVal,oldVal){
				 $.ajax({
		        	  url:baseUrl + "masterdata/getUserWorkshopByWerks",
		        	  data:{
		        		  "WERKS":newVal,
		        		  "MENU_KEY":"ZZJMES_SUBCONTRACTING",
		        	  },
		        	  success:function(resp){
		        		 vm.workshoplist = resp.data;
						 if(resp.data && resp.data.length &&  resp.data.length>0){
							 vm.workshop=resp.data[0].CODE;
							 
							 $.ajax({
								url:baseUrl + "masterdata/getUserLine",
								data:{
									"WERKS": vm.werks,
									"WORKSHOP": vm.workshop,
									"MENU_KEY": "ZZJMES_SUBCONTRACTING",
								},
								success:function(resp){
								   vm.linelist = resp.data;
								   if(resp.data && resp.data.length &&  resp.data.length>0){
									   vm.line=resp.data[0].CODE;
								   }else{
									   vm.line = '';
								   }
								   
								}
							})
							
							$.ajax({
								url:baseUrl + "masterdata/getWorkshopProcessList",
								data:{
									"WERKS": vm.werks,
									"WORKSHOP": vm.workshop,
									"PROCESS_TYPE": '01' //委外工序
								},
								success:function(resp){
								   vm.processList = resp.data;
								   if(resp.data && resp.data.length &&  resp.data.length>0){
									   	vm.process = resp.data[0].PROCESS_CODE;
								   }else{
										vm.process = '';
								   }
								   
								}
							})

		        		 }
		        		 
		        	  }
		          })
			}
		},
		workshop: {
			handler:function(newVal,oldVal){
				 $.ajax({
		        	  url:baseUrl + "masterdata/getUserLine",
		        	  data:{
		        		  "WERKS": vm.werks,
		        		  "WORKSHOP": newVal,
		        		  "MENU_KEY": "ZZJMES_SUBCONTRACTING",
		        	  },
		        	  success:function(resp){
		        		 vm.linelist = resp.data;
		        		 if(resp.data && resp.data.length &&  resp.data.length>0){
		        			 vm.line=resp.data[0].CODE;
		        		 }else{
							 vm.line = '';
						 }
		        		 
		        	  }
		          })
		          
		          $.ajax({
		        	  url:baseUrl + "masterdata/getWorkshopProcessList",
		        	  data:{
		        		  "WERKS": vm.werks,
		        		  "WORKSHOP": newVal,
		        		  "PROCESS_TYPE": '01' //委外工序
		        	  },
		        	  success:function(resp){
		        		 vm.processList = resp.data;
		        		 if(resp.data && resp.data.length &&  resp.data.length>0){
		        			 vm.process = resp.data[0].PROCESS_CODE;
		        		 }else{
							vm.process = '';
						 }
		        		 
		        	  }
		          })
			}
		},
		search_order: {
			handler:function(newVal,oldVal){
				vm.getBatchSelects();
			}
		},
		total_weight: {
			handler:function(newVal,oldVal){
				if(!const_int_validate.test(newVal) || !const_float_validate.test(newVal)){
		    		alert("请输入正整数或浮点数！");
		    		vm.total_weight = 0;
		    		return false;
		    	}
			}
		}
/*	    'orOrder.order_name': function(newVal){
	    	if(this.orOrder.order_name != '' && this.orOrder.bus_type_code != '' && this.orOrder.order_qty != ''){
				this.orOrder.order_desc = this.orOrder.order_name + $("#bus_type").find("option:selected").text() + " " + this.orOrder.order_qty + "台"
			}else{
				this.orOrder.order_desc = ''
			}
	    },*/
	}
});
$(function () {
	fun_ShowTable();
	
	/**
	 * 扫描零部件二维码,解析二维码
	 */
	$(document).on("keydown","#zzj_no",function(e){		
		if(e.keyCode == 13){
			var mat = JSON.parse(e.target.value);	
			vm.order_no = mat.order_no;
			vm.search_order = mat.order_no;
			vm.zzj_no = mat.zzj_no;	
			vm.getBatchSelects();
			vm.zzj_plan_batch = mat.zzj_plan_batch;
			vm.queryMatInfo();
		}
	})
	
});
function fun_ShowTable(){
	$("#dataGrid").dataGrid({
		datatype: "local",
		data: mydata,
	    cellEdit:true,
		cellurl:'#',
		cellsubmit:'clientArray',
		columnModel: [
			{label:'order_no',name:'order_no',hidden:true},
			{label:'zzj_pmd_items_id',name:'zzj_pmd_items_id',hidden:true},
			{label:'zzj_plan_batch',name:'zzj_plan_batch',hidden:true},
			{label:'outsourcing_process',name:'outsourcing_process',hidden:true},
			{label:'outsourcing_process_name',name:'outsourcing_process_name',hidden:true},
			
			{label:'max_quantity',name:'max_quantity',hidden:true}, //最大可委外数量
			{label:'id',name:'id', index: 'id',key: true ,hidden:true,formatter:function(val, obj, row, act){
				return obj.rowId
			}},
			{ label: ' ', name: 'EMPTY_COL', index: '', align:"center",width: 30, sortable:false,formatter:function(val, obj, row, act){
   				var actions = [];
   				actions.push('<a href="#" onClick="delMat('+row.id+')" title="删除" data-confirm="确认要删除该行数据吗？"><i class="fa fa-times" style=\"color:red\"></i></a>');
   				
   				return actions.join(",");
   			}},
			{ label: '订单', name: 'order_desc', align:'center', index: 'order_desc', width: 200 }, 	
			{ label: 'SAP工单', name: 'product_order', align:'center', index: 'product_order', width: 100 }, 	
    		{ label: 'SAP料号', name: 'sap_mat', align:'center', index: 'sap_mat', width: 100 }, 	
    		{ label: '零部件号', name: 'zzj_no', align:'center', index: 'zzj_no', width: 120 }, 		
    		{ label: '零部件名称', name: 'zzj_name', align:'center', index: 'zzj_name', width: 150 }, 
    		{ label: '上工序产量', name: 'prod_quantity', align:'center', index: 'prod_quantity', width: 90 }, 
    		{ label: '已发数量', name: 'already_outsourcing_quantity', align:'center', index: 'already_outsourcing_quantity', width: 80 }, 
    		{ label: '<span style="color:red">*</span><span style="color:blue"><b>委外数量</b></span>', name: 'outsourcing_quantity', align:"center",index: 'outsourcing_quantity', width: 70,editable:true,editrules:{
   				required:true,number:true},  formatter:'number',formatoptions:{
   				decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 0,defaultValue:''}
			},
    		{ label: '<span style="color:blue">发料人</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'sender\')" title="向下填充" style="color:green" aria-hidden="true"></i>', 
   				name: 'sender', align:"center",index: 'sender', width: 90,editable:true, sortable:false,  },
    		{ label: '单位', name: 'unit', align:'center',  index: 'unit', width: 70 }, 			
    		{ label: '单重', name: 'weight', align:'center', index: 'weight', width: 70,editable:true, }, 		
    		{ label: '重量', name: 'total_weight', align:'center', index: 'total_weight', width: 90,editable:true, }, 	
    		{ label: '<span style="color:blue"><b>备注</b></span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'memo\')" title="向下填充" style="color:green" aria-hidden="true"></i>', 
   				name: 'memo', align:"center",index: 'memo', width: 90,editable:true, sortable:false,  },
    		{ label: '装配位置', name: 'assembly_position', align:'center', index: 'assembly_position', width: 120 }, 	
    		{ label: '使用车间', name: 'use_workshop',align:"center", index: 'use_workshop', width: 80 },
    		{ label: '使用工序', name: 'process',align:'center',  index: 'process', width: 90 }, 		
    		{ label: '工艺流程', name: 'process_flow', align:'center',  index: 'process_flow', width: 220 }, 			
    	],
    	viewrecords: false,
        showRownum: true, 
		shrinkToFit: false,
        width:1900,
        rownumWidth: 25, 
        orderBy:"creat_date desc",
        autowidth:true,
        multiselect: false,			
 	   	beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
		},
        afterSaveCell:function(rowid, cellname, value, iRow, iCol){	
			if(value==""){
				$("#dataGrid").jqGrid('setCell', rowid, cellname, '&nbsp;','editable-cell');
			}
		
			var row =  $("#dataGrid").jqGrid('getRowData', rowid);
			if(cellname=='outsourcing_quantity'){
				var field="委外数量";
				var able_qty= Number(row.prod_quantity)-Number(row.already_outsourcing_quantity);
				if(Number(value)>Number(able_qty)){
					js.alert(field+"不能大于"+able_qty);
					$("#dataGrid").jqGrid('setCell', rowid, cellname, able_qty,'editable-cell');
				}
				if(value.trim()=="" ){
					js.alert(field+"不能为空！");
					$("#dataGrid").jqGrid('setCell', rowid, cellname, able_qty,'editable-cell');
				}
				if(value=="0" ){
					js.alert(field+"必须大于0！");
					$("#dataGrid").jqGrid('setCell', rowid, cellname, able_qty,'editable-cell');
				}
				
				if(Number(value)>0 && Number(row.weight)>0){
					$("#dataGrid").jqGrid('setCell', rowid, "total_weight", Number(value)*Number(row.weight),'editable-cell');
					var allRows = $("#dataGrid").jqGrid('getRowData');
					var head_total_weight = 0;
					$.each(allRows,function(i,row){	
						if(Number(row.total_weight)>0){
							head_total_weight += Number(row.total_weight);
						}
					});	
					vm.total_weight = head_total_weight;
				}
			}
			if(cellname=='weight'){
				if(Number(value)>0 && Number(row.outsourcing_quantity)>0){
					$("#dataGrid").jqGrid('setCell', rowid, "total_weight", Number(value)*Number(row.outsourcing_quantity),'editable-cell');
					var allRows = $("#dataGrid").jqGrid('getRowData');
					var head_total_weight = 0;
					$.each(allRows,function(i,row){	
						if(Number(row.total_weight)>0){
							head_total_weight += Number(row.total_weight);
						}
					});	
					vm.total_weight = head_total_weight;
				}
			}
			
		},
    });
	//$("#dataGrid").jqGrid("setFrozenColumns");
}

function fn_cb_getOrderInfo(order){
	vm.order_no = order.order_no;
	vm.search_order = order.order_no;
}

function addMat(matList){
	var selectedId = $("#dataGrid").jqGrid("getGridParam", "selrow"); 
	var ids = $("#dataGrid").jqGrid('getDataIDs');
	//console.info(ids)
	if(ids.length==0){
		ids=[0]
	}
	var addedMatStr = "";
	var rows=$("#dataGrid").jqGrid('getRowData');
	$.each(rows,function(i,row){	
		addedMatStr += ";" + row.zzj_pmd_items_id+"-"+row.zzj_plan_batch +";";
	});	

    var rowid = Math.max.apply(null,ids);
    console.info("addedMatStr:"+addedMatStr);
	$.each(matList,function(i,row){	
		console.info(addedMatStr.indexOf(";" + row.zzj_pmd_items_id+"-"+row.zzj_plan_batch +";"));
		if(addedMatStr.indexOf(";" + row.zzj_pmd_items_id+"-"+row.zzj_plan_batch +";") == -1){
			if(Number(row.prod_quantity)-Number(row.already_outsourcing_quantity) <=0){
				js.showMessage(row.zzj_no+"-"+row.zzj_plan_batch+"可委外发货数量为0！");
			}else{
				var data = {};
				data.id = ++rowid;  
				data.outsourcing_process = vm.process;
				data.outsourcing_process_name = $('#process').find("option:selected").text();
				
				data.zzj_plan_batch = row.zzj_plan_batch;
				data.zzj_pmd_items_id = row.zzj_pmd_items_id;
				data.order_no = row.order_no;
				data.order_desc = row.order_desc;
		    	data.product_order = row.product_order;
		    	data.sap_mat = row.sap_mat;
		    	data.zzj_no = row.zzj_no;
		    	data.zzj_name = row.zzj_name;
		    	data.prod_quantity = row.prod_quantity;
		    	data.already_outsourcing_quantity = row.already_outsourcing_quantity;
		    	data.outsourcing_quantity = Number(row.prod_quantity)-Number(row.already_outsourcing_quantity);
		    	data.max_quantity = Number(row.prod_quantity)-Number(row.already_outsourcing_quantity);
		    	data.sender = row.FULL_NAME||'';
		    	data.unit = row.unit;
		    	data.weight = row.weight;
		    	data.total_weight = Number(row.prod_quantity)*Number(row.weight);
		    	data.assembly_position = row.assembly_position;
		    	data.use_workshop = row.use_workshop;
		    	data.process = row.process;
		    	data.process_flow = row.process_flow;
		    	
		    	$("#dataGrid").jqGrid("addRowData", rowid, data, "last"); 
		    	
		    	vm.total_weight = Number(vm.total_weight) + Number(row.prod_quantity)*Number(row.weight);
			}

		}else{
			js.showMessage(row.zzj_no+"-"+row.zzj_plan_batch+"数据重复！");
		}
	    	
	});	
}

function delMat(id){
	console.info(id)
	$("#dataGrid").delRowData(id)
}


function fillDown(e){
	$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
	var ids=$("#dataGrid").getGridParam("selarrrow");
	var rowid=lastrow;
	var rows=$("#dataGrid").jqGrid('getRowData');
/*	if(ids!=null){
		rowid=Math.min.apply(null,ids);
	}*/
	var last_row=$("#dataGrid").jqGrid('getRowData',rowid);
	var cellvalue=last_row[lastcellname];
	
	if(lastcellname==e){
		$.each(rows,function(i,row){
			if(Number(row.id)>=Number(rowid)&&cellvalue){
				$("#dataGrid").jqGrid('setCell',Number(row.id), e, cellvalue,'editable-cell');
			}
		})
	}
}

//调用摄像头扫二维码
function doScan(ele){
	var url = "../../doScan";
	last_scan_ele = ele;
	var iframe_id=self.frameElement.getAttribute('id');
	$(window.parent.document).find("#activeIframe").val(iframe_id)
	var a = document.createElement("a");
	a.target="_parent";
	a.href = url;
	a.click();
}

function funFromjs(result){ 
	var e = $.Event('keydown');
    e.keyCode = 13; 	
	// var mat = JSON.parse(result);	
	// vm.order_no= mat.order_no;
	// vm.zzj_no = mat.zzj_no;	
	// vm.zzj_plan_batch = mat.zzj_plan_batch;
    $("#"+last_scan_ele).val(result);
    $("#"+last_scan_ele).trigger(e);

}