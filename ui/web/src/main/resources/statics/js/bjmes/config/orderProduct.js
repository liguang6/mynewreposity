var product_type_option = "";
var product_option = "";
var process_flow_option = "";
var process_option="";
var mydata =[];
var lastrow,lastcell,lastcellname,last_scan_ele;
var vm = new Vue({
	el:'#rrapp',
	data:{
		werks: '',
		werks_name: '',
		workshopList: [],
		workgroupList:[],
		workshop: '',
		workshop_name: '',
		order_no: '',
		product_code: '',
		product_type_code:'',
		
		show:false,
	},
	created:function(){
		this.werks=$("#werks").find("option").first().val();
		
	},
	methods: {
		//清空表格数据
		clearTable: function() {
			mydata = [];
			$("#dataGrid").jqGrid('clearGridData');
			vm.product_code='';
		},
		
		query: function(){
			$("#dataGrid").jqGrid('clearGridData');
         	vm.queryInfo();
		},
		queryInfo: function(){
			$.ajax({
				url:baseUrl + "config/bjMesOrderProducts/getList",
				data:{
					"order_no":vm.order_no,
					"werks":vm.werks,
					"workshop":vm.workshop,
					"product_type_code":vm.product_type_code,
					"product_code":vm.product_code
				},
				type:"post",
				success:function(response){
					js.closeLoading();
					if(response.code==500){
						js.showErrorMessage(response.msg);
						return false;
					}
					if(response.data){
						if(response.data.length <= 0){
							js.showMessage("未找到符合条件的供货数据！");
							return false;
						}
						addMat(response.data);
					}
				},
			});

		},
		scInfo: function(){//生成按钮，产品库中查询
			$.ajax({
				url:baseUrl + "config/bjMesProducts/getList",
				data:{
					"product_type_code":vm.product_type_code,
					"product_code":vm.product_code
				},
				type:"post",
				success:function(response){
					js.closeLoading();
					if(response.code==500){
						js.showErrorMessage(response.msg);
						return false;
					}
					if(response.data){
						if(response.data.length <= 0){
							js.showMessage("未找到符合条件的供货数据！");
							return false;
						}
						addsc(response.data);
					}
				},
			});

		},
		deleteById:function(){
			
		var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
		if (gr != null  && gr!==undefined) {
			layer.confirm('确定删除选中的记录?', {icon: 3, title:'提示'}, function(index){
				var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
				$.ajax({
				  type: "POST",
				  url: baseURL + "config/bjMesOrderProducts/delById/"+grData.id,
				  contentType: "application/json",
				  success: function(r){
					  if(r.code == 0){
						  js.showMessage('删除成功!');
						  
						  vm.queryInfo();
					  }else{
						  alert(r.msg);
					  }
				  }
			  });
			  layer.close(index);
		  });
		} 
		else layer.msg("请选择要删除的行",{time:1000});
		},
		listComp:function(){

			var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
			if(gr !== null && gr!==undefined){
				var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
				var options = {
				   type: 2,
				   maxmin: true,
				   shadeClose: true,
				   title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>组件查看',
				   area: ["650px", "400px"],
				   content: 'bjmes/config/product_comp_list.html',
				   btn: ['<i class="fa fa-check"></i> 确定'],
				   success:function(layero, index){
					   var win = top[layero.find('iframe')[0]['name']];
					   win.vm.saveFlag = false;
					   win.vm.getInfo(grData.product_id);
				   },
				   btn1:function(index,layero){
					   
					   var win = top[layero.find('iframe')[0]['name']];
					   win.vm.saveOrUpdate();
					   /*var btnSubmit= $("#btnSubmit", layero.find("iframe")[0].contentWindow.document);
					   $(btnSubmit).click();*/
					   if(typeof listselectCallback == 'function'){
						   console.log("win.vm.saveFlag",win.vm.saveFlag);
						   listselectCallback(index,win.vm.saveFlag);
					   }
				   }
			   };
			   options.btn.push('<i class="fa fa-close"></i> 关闭');
			   js.layer.open(options);
			}else{
				layer.msg("请选择要查看的行",{time:1000});
			}
		 
		},
		save: function(){
           	//初始化工厂、车间、线别名称值
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			var rows=$("#dataGrid").jqGrid('getRowData');
			if(rows.length <= 0){
				js.closeLoading();
				$("#btnSave").attr("disabled", false);
				js.showErrorMessage("没有需要保存的数据！");
			   return false;
			}
			for(var i in rows){
				
			}
			
			js.loading("正在保存数据，请稍等...");
			$("#btnSave").html("保存中...");
			$("#btnSave").attr("disabled","disabled");
			$.ajax({
				type : "post",
				dataType: "json",
				async : true,
				url :baseUrl+ "config/bjMesOrderProducts/save",
				data : {
					"ARRLIST": JSON.stringify(rows),
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
						js.alert("操作成功！");
			        	       	
					}	
				}	
			});				
		}
	},
	watch: {
		werks: {
			handler:function(newVal,oldVal){
				$.ajax({
	        	  url:baseUrl + "masterdata/getUserWorkshopByWerks",
	        	  data:{
	        		  "WERKS":newVal,
	        		  "MENU_KEY":"ZZJMES_MACHINE_PLAN_MANAGE",
	        	  },
	        	  success:function(resp){
	        		 vm.workshopList = resp.data;
	        		 if(resp.data.length>0){
	        			 vm.workshop=resp.data[0].CODE;
	        			 if(vm.handover_type=='1'){
	        				 getWorkgroupList();
	        			 }
	        		 } 
	        	  }
			  });
			  //查询工位
			$.ajax({
				url:baseURL+"masterdata/process/getProcessList",
				dataType : "json",
				type : "post",
				data : {werks:newVal},
				async: false,
				success: function (response) {
					var processlist = response.data;
					$.each(processlist, function(index, value) {
						process_option+=value.PROCESS_CODE+":"+value.PROCESS_NAME+";";
						})
						process_option = process_option.substring(0,process_option.length-1);
				}
			});

			}
		},
		workshop: {
			handler:function(newVal,oldVal){
				if(vm.handover_type=='1'){
   				   getWorkgroupList();
   			    }
		    }
		}
		
	}
});
$(function () {
    $("#product_type_code option").each(function (){
		if($(this).val() != '')product_type_option += $(this).val() + ":" + $(this).text() + ";"
	})
	product_type_option = product_type_option.substring(0,product_type_option.length-1);

	//查询产品库
	$.ajax({
		url:baseURL+"config/bjMesProducts/getList",
		dataType : "json",
		type : "post",
		data : {"product_code":''},
		async: false,
		success: function (response) { 
			var productlist = response.data;
			$.each(productlist, function(index, value) {
				product_option+=value.product_code+":"+value.product_name+";";
			})
			product_option = product_option.substring(0,product_option.length-1);
		}
	});
	//查询流程
	$.ajax({
		url:baseUrl + "config/bjMesProcessFlow/getList",
		data:{},
		success:function(resp){
		   var processflowlist = resp.data;
		   $.each(processflowlist, function(index, value) {
			process_flow_option+=value.process_flow_code+":"+value.process_flow_name+";";
			})
			process_flow_option = process_flow_option.substring(0,process_flow_option.length-1);
		}
	})

	//查询工位
	$.ajax({
		url:baseURL+"masterdata/process/getProcessList",
		dataType : "json",
		type : "post",
		data : {werks:vm.werks},
		async: false,
		success: function (response) {
			var processlist = response.data;
			$.each(processlist, function(index, value) {
				process_option+=value.PROCESS_CODE+":"+value.PROCESS_NAME+";";
				})
				process_option = process_option.substring(0,process_option.length-1);
		}
	});

	fun_ShowTable();

});
function fun_ShowTable(){
	$("#dataGrid").dataGrid({
		datatype: "local",
		data: mydata,
	    cellEdit:true,
		cellurl:'#',
		cellsubmit:'clientArray',
		columnModel: [
			{ label: ' ', name: 'EMPTY_COL', index: '', align:"center",width: 30, sortable:false,formatter:function(val, obj, row, act){
   				var actions = [];
   				actions.push('<a href="#" onClick="delMat('+row.id+')" title="删除" data-confirm="确认要删除该行数据吗？"><i class="fa fa-times" style=\"color:red\"></i></a>');
   				return actions.join(",");
   			}},
   			
			{ label: '订单编号',name:'order_no', align:'center', index: 'order_no', width: 100 }, 
			{ label: '工厂', name: 'werks', align:'center', index: 'werks', width: 80 }, 		
			{ label: '车间', name: 'workshop_name', align:'center', index: 'workshop_name', width: 120 }, 
   			{ label: '产品类别', name: 'product_type_code', align:'center', index: 'product_type_code', width: 120,edittype:'select',editable:true }, 
			{ label: '产品名称', name: 'product_code', align:'center', index: 'product_code', width: 120,editable:true,edittype:'select'}, 
   			//{ label: '产品名称', name: 'product_name', align:'center', index: 'product_name', width: 140 }, 
			{ label: '加工流程编码', name: 'process_flow_code', align:'center', index: 'process_flow_code', width: 100,editable:true,edittype:'select' }, 	
			//{ label: '加工流程名称', name: 'process_flow_name', align:'center', index: 'process_flow_name', width: 100 }, 
    		{ label: '工位代码', name: 'process_code', align:'center', index: 'process_code', width: 100,editable:true,edittype:'select' }, 	
			//{ label: '工位名称', name: 'process_name', align:'center', index: 'process_name', width: 100 }, 
			{ label: 'SAP编码', name: 'sap_no', align:'center', index: 'sap_no', width: 100,editable:true }, 
			{ label: '单车用量', name: 'single_qty', align:'center', index: 'single_qty', width: 100,editable:true },
			{ label: '标准工时', name: 'standard_hours', align:'center', index: 'standard_hours', width: 100,editable:true },
			{ label: '编辑人', name: 'editor', index: 'editor', width: 120,align:'center'  }, 			
			{ label: '编辑时间', name: 'edit_date', index: 'edit_date', width: 140,align:'center'  },
			{ label: 'workshop', name: 'workshop', index: 'workshop', width: 80,align:'center',hidden:true  },
			{ label: 'id', name: 'id', index: 'id', width: 80,align:'center',hidden:true },
			{ label: 'product_id', name: 'product_id', index: 'product_id', width: 80,align:'center',hidden:true },
			{ label: 'product_code_a', name: 'product_code_a', index: 'product_code_a', width: 80,align:'center',hidden:true },
			{ label: 'product_type_code_a', name: 'product_type_code_a', index: 'product_type_code_a', width: 80,align:'center',hidden:true }
    		
    	],
    	viewrecords: false,
        showRownum: true, 
		shrinkToFit: false,
        width:1900,
        rownumWidth: 25, 
        autowidth:true,
        multiselect: false,	
        gridComplete:function(){
        	
        },
 	   	beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
		},
        afterSaveCell:function(rowid, cellname, value, iRow, iCol){	
			
			if(cellname=='product_code'){
				//console.info("product_code::",value);
				var rowId= $("#dataGrid").jqGrid('getGridParam','selrow');
				$("#dataGrid").jqGrid('setCell',rowId,"product_code_a",value);
			}

			if(cellname=='product_type_code'){
				console.info("product_type_code::",value);
				var rowId= $("#dataGrid").jqGrid('getGridParam','selrow');
				$("#dataGrid").jqGrid('setCell',rowId,"product_type_code_a",value);
			}

		},
		formatCell:function(rowid, cellname, value, iRow, iCol){
			if(cellname=='product_type_code'){
				
    			$('#dataGrid').jqGrid('setColProp', 'product_type_code', { 
    				editoptions: {value:product_type_option}
	            });
			}

			if(cellname=='product_code'){
				
    			$('#dataGrid').jqGrid('setColProp', 'product_code', { 
    				editoptions: {value:product_option,dataEvents:[
						{type:"change",fn:function(e){
							//查询产品库
						$.ajax({
							url:baseURL+"config/bjMesProducts/getList",
							dataType : "json",
							type : "post",
							data : {"product_code":this.value},
							async: false,
							success: function (response) { 
								var productlist = response.data;
								$.each(productlist, function(index, value) {
									//var rowId= $("#dataGrid").jqGrid('getGridParam','selrow');
									$("#dataGrid").jqGrid('setCell',rowid,"process_flow_code",value.process_flow_code); 
									$("#dataGrid").jqGrid('setCell',rowid,"process_code",value.process_code);  //
									$("#dataGrid").jqGrid('setCell',rowid,"product_id",value.id);
									$("#dataGrid").jqGrid('setCell',rowid,"product_code_a",value.product_code);
								})
								
							}
						});
							
						}},
						
					]}
	            });
			}

			if(cellname=='process_flow_code'){
				$('#dataGrid').jqGrid('setColProp', 'process_flow_code', {
					editoptions: {value:process_flow_option}
				}); 
			}

			if(cellname=='process_code'){
				$('#dataGrid').jqGrid('setColProp', 'process_code', {
					editoptions: {value:process_option}
				}); 
			}

			
			
		}
    });
}

function addMat(matList){
	$("#dataGrid").jqGrid('clearGridData');
	var selectedId = $("#dataGrid").jqGrid("getGridParam", "selrow"); 
	var ids = $("#dataGrid").jqGrid('getDataIDs');
	if(ids.length==0){
		ids=[0]
	}
	var addedMatStr = "";
	var rows=$("#dataGrid").jqGrid('getRowData');
	$.each(rows,function(i,row){	
		addedMatStr += ";" + row.order_no +";"
	});	
    var rowid = Math.max.apply(null,ids);
	$.each(matList,function(i,row){	
		//if(addedMatStr.indexOf(";" + row.order_no+";") == -1){
			var data = {};
			data.id = ++rowid;
			data.order_no = row.order_no;		
			
			data.werks = row.werks;
			data.workshop_name = row.workshop_name;
			data.workshop = row.workshop;
			data.product_type_code = row.product_type_code;
			data.product_code = row.product_code;
			data.product_name = row.product_name;
			
			data.process_flow_code = row.process_flow_code;
			data.process_flow_name = row.process_flow_name;
			data.process_code = row.process_code;
			data.process_name = row.process_name;
			data.sap_no = row.sap_no;
			data.single_qty = row.single_qty;
			data.standard_hours = row.standard_hours;
			data.editor = row.editor;
			data.edit_date = row.edit_date;

			data.product_id=row.product_id;
			data.product_code_a=row.product_code_a;
	    	$("#dataGrid").jqGrid("addRowData", rowid, data, "last");
		/*}else{
			js.showMessage(row.product_code+"数据重复！");
		}   */ 	
	});	
}
//生成
function addsc(matList){
	$("#dataGrid").jqGrid('clearGridData');
	var selectedId = $("#dataGrid").jqGrid("getGridParam", "selrow"); 
	var ids = $("#dataGrid").jqGrid('getDataIDs');
	if(ids.length==0){
		ids=[0]
	}
	vm.workshop_name=$("#workshop").find("option:selected").text();
	var selectedId = $("#dataGrid").jqGrid("getGridParam", "selrow"); 
	var ids = $("#dataGrid").jqGrid('getDataIDs');
	if(ids.length==0){
		ids=[0]
	}

    var rowid = Math.max.apply(null,ids);
	$.each(matList,function(i,row){
			var data = {};
			data.id = ++rowid;
			data.order_no = vm.order_no;
			data.werks = vm.werks;
			data.workshop = vm.workshop;
			data.workshop_name = vm.workshop_name;

			data.product_type_code = row.product_type_code;
			data.product_code = row.product_code;
			data.product_name = row.product_name;
			
			data.process_flow_code = row.process_flow_code;
			data.process_flow_name = row.process_flow_name;
			data.process_code = row.process_code;
			data.process_name = row.process_name;
			data.sap_no = row.sap_no;
			data.single_qty = row.single_qty;
			data.standard_hours = row.standard_hours;
			data.editor = row.editor;
			data.edit_date = row.edit_date;

			data.product_id=row.id;
			data.product_code_a=row.product_code;
	    	$("#dataGrid").jqGrid("addRowData", rowid, data, "last");
		/*}else{
			js.showMessage(row.product_code+"数据重复！");
		}   */ 	
	});	
}

function addNewRow(){
	
	vm.workshop_name=$("#workshop").find("option:selected").text();
	var selectedId = $("#dataGrid").jqGrid("getGridParam", "selrow"); 
	var ids = $("#dataGrid").jqGrid('getDataIDs');
	if(ids.length==0){
		ids=[0]
	}

    var rowid = Math.max.apply(null,ids);
	
			var data = {};
			data.id = ++rowid;
			data.order_no = vm.order_no;
			data.werks = vm.werks;
			data.workshop = vm.workshop;
			data.workshop_name = vm.workshop_name;
			
			data.product_code = '';
			data.product_name = '';
			
			data.process_flow_code = '';
			data.process_flow_name = '';
			data.process_code = '';
			data.process_name = '';
			data.sap_no = '';
			data.single_qty = '';
	    	data.standard_hours = '';
	    	$("#dataGrid").jqGrid("addRowData", rowid, data, "last");
		
	
}

function delMat(id){	
	var data=$("#dataGrid").jqGrid('getRowData',id);
	$("#dataGrid").delRowData(id);
}
function getworkgroupList(){
	$.ajax({
  	  url:baseUrl + "masterdata/getWorkshopWorkgroupList",
  	  data:{
  		  "WERKS": vm.werks,
  		  "WORKSHOP": vm.workshop,
  		  "MENU_KEY":"ZZJMES_MACHINE_PLAN_MANAGE",
		  "ALL_OPTION":'X'
	  },
  	  success:function(resp){
  		 if(resp.code==0){
  			vm.workgroupList = resp.data;
  			//vm.deliver_workgroup
  		 }
  	  }
    })
}




