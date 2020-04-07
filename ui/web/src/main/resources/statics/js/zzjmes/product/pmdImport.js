var mydata =[];
var vm = new Vue({
	el:'#rrapp',
	data:{
		table_items: [],
		werks: '',
		workshoplist: [],
		workshop: '',
		linelist: [],
		line: '',
		order_no: '',
	},
	created:function(){
		this.werks=$("#werks").find("option").first().val();
	},
	methods: {
		//清空表格数据
		clearTable : function() {
			mydata = [];
			$("#dataGrid").jqGrid('clearGridData');
			//$("#dataGrid").jqGrid('setGridParam',{data: mydata,datatype:'local'}).trigger('reloadGrid'); 
		},
		getOrderNoFuzzy:function(){
			if(vm.werks === ''){
				alert('请先选择工厂！');
				return false;
			}
			getZZJOrderNoSelect("#order_no",null,fn_cb_getOrderInfo,"#werks");
		},
		importPmd: function(){
			mydata = [];
			$("#dataGrid").jqGrid('clearGridData');
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
           	//初始化工厂、车间、线别名称值
        	$('#werks_name').val($('#werks').find("option:selected").data("name"));
        	$('#workshop_name').val($('#workshop').find("option:selected").text());
        	$('#line_name').val($('#line').find("option:selected").text());
			
			//jQuery('#dataGrid').GridUnload();  
			var form = new FormData(document.getElementById("uploadForm"));
			console.log("form",form);
			js.loading("正在处理导入数据,请您耐心等待...");
			$("#btnImport").val("正在导入...");	
			$("#btnImport").attr("disabled","disabled");
			$.ajax({
				url:baseUrl + "zzjmes/pmdImport/upload",
				data:form,
				type:"post",
				contentType:false,
				processData:false,
				success:function(response){
					js.closeLoading();
					$("#btnImport").val("导入");	
					$("#btnImport").removeAttr("disabled");
					mydata= response.data;
					if(response.code == 0){
						//$('#dataGrid').trigger('reloadGrid');
						$("#dataGrid").jqGrid('setGridParam',{data: mydata,datatype:'local'}).trigger('reloadGrid'); 
						js.showMessage("导入成功：" + response.saveCount+"条数据，导入失败："+response.errorCount+"条数据！");
					}else{
						//$("#dataGrid").jqGrid('setGridParam',{data: mydata,datatype:'local'}).trigger('reloadGrid');
						js.showErrorMessage("上传失败," + response.msg);
					} 
					fun_ShowTable();
				},
				complete:function(XMLHttpRequest, textStatus){
					$("#btnImport").val("导入");	
					$("#btnImport").removeAttr("disabled");
					js.closeLoading();
				}
			});
		},
		exportExcel : function() {
			var excelData = $("#dataGrid").jqGrid('getRowData');
			if(excelData.length>0){
				$("#entityList").val(JSON.stringify(excelData));
				$("#exportForm").submit();
			}else{
				js.showMessage("没有错误数据可导出！");
			}
			
		},
	},
	watch: {
		werks: {
			handler:function(newVal,oldVal){
				 $.ajax({
		        	  url:baseUrl + "masterdata/getUserWorkshopByWerks",
		        	  data:{
		        		  "WERKS":newVal,
		        		  "MENU_KEY":"ZZJMES_PMD_IMPORT",
		        	  },
		        	  success:function(resp){
		        		 vm.workshoplist = resp.data;
		        		 if(resp.data.length>0){
							 vm.workshop=resp.data[0].CODE;
							 $.ajax({
								url:baseUrl + "masterdata/getUserLine",
								data:{
									"WERKS": vm.werks,
									"WORKSHOP": vm.workshop,
									"MENU_KEY": "ZZJMES_PMD_IMPORT",
								},
								success:function(resp){
								   vm.linelist = resp.data;
								   if(resp.data && resp.data.length && resp.data.length>0){
									   vm.line=resp.data[0].CODE;
								   }else{
										vm.line = '';
								   }
								   
								}
							})
		        		 }else{
							vm.workshop = '';
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
		        		  "MENU_KEY": "ZZJMES_PMD_IMPORT",
		        	  },
		        	  success:function(resp){
						 vm.linelist = resp.data;
						 if(resp.data && resp.data.length && resp.data.length>0){
		        			 vm.line=resp.data[0].CODE;
		        		 }else{
							vm.line = '';
						 }
		        		 
		        	  }
		          })
			}
		},
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
});
function fun_ShowTable(){
	$("#dataGrid").dataGrid({
		datatype: "local",
		data: mydata,
		columnModel: [
    		{ label: '<span style="color:red">*</span><span style="color:blue"><b>序号</b></span>', name: 'no',align:'center',  index: 'no', width: 50,formatter: customFmatter}, 		
    		{ label: 'SAP码', name: 'sap_mat', align:'center', index: 'sap_mat', width: 100 }, 			
    		{ label: '<span style="color:red">*</span><span style="color:blue"><b>物料描述</b></span>', name: 'mat_description', align:'center', index: 'mat_description', width: 150 }, 
    		{ label: '<span style="color:red">*</span><span style="color:blue"><b>物料类型</b></span>', name: 'mat_type', align:'center',  index: 'mat_type', width: 80 }, 	
    		{ label: '<span style="color:red">*</span><span style="color:blue"><b>材料/规格</b></span>', name: 'specification', align:'center', index: 'specification', width: 90 }, 			
    		{ label: '单位', name: 'unit', align:'center',  index: 'unit', width: 70 }, 			
    		{ label: '<span style="color:red">*</span><span style="color:blue"><b>单车损耗%</b></span>', name: 'loss', align:'center', index: 'loss', width: 90 }, 	
    		{ label: '<span style="color:red">*</span><span style="color:blue"><b>单车用量</b></span>', name: 'quantity', align:'center', index: 'quantity', width: 90 }, 	
    		{ label: '单重', name: 'weight', align:'center', index: 'weight', width: 70 }, 		
    		{ label: '总重含损耗', name: 'total_weight', align:'center', index: 'total_weight', width: 90 }, 	
    		{ label: '<span style="color:red">*</span><span style="color:blue"><b>使用车间</b></span>', name: 'use_workshop', align:'center', index: 'use_workshop', width: 90 }, 	
    		{ label: '<span style="color:red">*</span><span style="color:blue"><b>工序</b></span>', name: 'process', align:'center', index: 'process', width: 90 }, 	
    		{ label: '<span style="color:red">*</span><span style="color:blue"><b>装配位置</b></span>', name: 'assembly_position', align:'center', index: 'assembly_position', width: 90 }, 	
    		{ label: '<span style="color:red">*</span><span style="color:blue"><b>工艺标识</b></span>', name: 'zzj_no', align:'center', index: 'zzj_no', width: 90 }, 	
    		{ label: '下料尺寸', name: 'filling_size', align:'center', index: 'filling_size', width: 90 }, 	
    		{ label: '精度要求', name: 'accuracy_demand', index: 'accuracy_demand', width: 80 },
    		{ label: '表面处理', name: 'surface_treatment',align:'center',  index: 'surface_treatment', width: 80 }, 		
    		{ label: '备注', name: 'memo', align:'center', index: 'memo', width: 50 }, 			
    		{ label: '工艺备注', name: 'processflow_memo', align:'center', index: 'processflow_memo', width: 90 }, 
    		{ label: '变更说明', name: 'change_description', align:'center',  index: 'change_description', width: 80 }, 	
    		{ label: '变更主体', name: 'change_subject', align:'center', index: 'change_subject', width: 80 }, 			
    		{ label: '<span style="color:red">*</span><span style="color:blue"><b>材料类型</b></span>', name: 'cailiao_type', align:'center',  index: 'cailiao_type', width: 90 }, 			
    		{ label: '<span style="color:red">*</span><span style="color:blue"><b>图号</b></span>', name: 'material_no', align:'center',  index: 'material_no', width: 90 }, 			
    		{ label: '<span style="color:red">*</span><span style="color:blue"><b>名称</b></span>', name: 'zzj_name', align:'center',  index: 'zzj_name', width: 90 }, 			
    		{ label: '<span style="color:red">*</span><span style="color:blue"><b>分包类型</b></span>', name: 'subcontracting_type', align:'center',  index: 'subcontracting_type', width: 90 }, 			
    		{ label: '加工顺序', name: 'process_sequence', align:'center', index: 'process_sequence', width: 70 }, 	
    		{ label: '<span style="color:red">*</span><span style="color:blue"><b>工艺流程</b></span>', name: 'process_flow', align:'center',  index: 'process_flow', width: 150 }, 			
    		{ label: '加工工时', name: 'process_time', align:'center', index: 'process_time', width: 120 }, 		
    		{ label: '加工设备', name: 'process_machine', align:'center', index: 'process_machine', width: 90 }, 
    		{ label: '<span style="color:red">*</span><span style="color:blue"><b>工段</b></span>', name: 'section', align:'center',  index: 'section', width: 90 }, 			
    		{ label: '孔特征', name: 'aperture', index: 'aperture', width: 80 },
    		{ label: '埋板', name: 'maiban', index: 'maiban', width: 80 },
    		{ label: '板厚', name: 'banhou', index: 'banhou', width: 80 },
    		{ label: '特殊大尺寸', name: 'filling_size_max', index: 'filling_size_max', width: 120 },
    		{ label: '错误消息', name: 'errorMsg', align:'center', index: 'errorMsg', width: 200 }, 
    	],
    	viewrecords: false,
        showRownum: true, 
		shrinkToFit: false,
        width:1900,
        rownumWidth: 25, 
        orderBy:"creat_date desc",
        autowidth:true,
        multiselect: false,
    });
	//$("#dataGrid").jqGrid("setFrozenColumns");
}

function customFmatter(cellvalue, options, rowObject){  
    return cellvalue+"";  
}

function fn_cb_getOrderInfo(order){
	vm.order_no = order.order_no;
}