var mydata = [];
var vm = new Vue({
	el:'#rrapp',
	data:{
		werks:"",
		workshop:"",
		workshop_list :[],
		line:"",
		line_list :[],
		zzj_plan_batch :'',
		error_data:[],
		process:'',
		processList:[],
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
				data : {
					"WERKS":newVal,
					"MENU_KEY":'ZZJMES_MACHINE_PLAN_IMPORT',
				},
				async: true,
				success: function (response) { 
					vm.workshop_list=response.data;
					if(response.data && response.data.length && response.data.length>0){
						vm.workshop=response.data[0]['code'];

						$.ajax({
							url:baseURL+"masterdata/getUserLine",
							dataType : "json",
							type : "post",
							data : {
								"WERKS":vm.werks,
								"WORKSHOP":vm.workshop,
								"MENU_KEY":'ZZJMES_MACHINE_PLAN_IMPORT',
							},
							async: true,
							success: function (response) { 
								vm.line_list=response.data;
								if(response.data && response.data.length && response.data.length>0){
									vm.line=response.data[0]['code'];
								}else{
									vm.line = '';
								}
							}
						});	
						vm.getBatchSelects();
						getProcessList();
					}else{
						vm.workshop = '';
					}
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
						"MENU_KEY":'ZZJMES_MACHINE_PLAN_IMPORT',
					},
					async: true,
					success: function (response) { 
						vm.line_list=response.data;
						if(response.data && response.data.length && response.data.length>0){
							vm.line=response.data[0]['code'];
							vm.getBatchSelects();
							getProcessList();
						}else{
							vm.line = '';
						}
					}
				});	
			}
		}
	},
	methods: {
		getOrderNoFuzzy:function(){
			getZZJOrderNoSelect("#search_order",null,vm.getBatchSelects);
		},
		getBatchSelects:function(selectval){
			if($("#search_order").val()!=''){
				$.ajax({
					type : "post",
					dataType : "json",
					async : false,
					url :baseUrl+ "/zzjmes/common/getPlanBatchList",
					data : {
						"werks" : this.werks,
						"workshop" : this.workshop,
						"line" : this.line,
						"order_no":$("#search_order").val()
					},
					success:function(response){
						getBatchSelects(response.data,selectval,"#zzj_plan_batch","全部");	
					}	
				});
			}
		},
		imp:function(){
			if($("#search_order").val()==''){
				js.showErrorMessage("请输入订单号！");
				return;
			}
			if($("#zzj_plan_batch").val()==''){
				js.showErrorMessage("请输入批次！");
				return;
			}
			jQuery('#dataGrid').GridUnload();  
			var form = new FormData(document.getElementById("expTemplateForm"));
			form.append("werks_name",$("#werks :selected").attr("data-name"));
			form.append("workshop_name",$("#workshop :selected").text());
			form.append("line_name",$("#line :selected").text());
			console.log("form",form);
			js.loading("正在处理导入数据,请您耐心等待...");
			$("#btnImport").val("正在导入...");	
			$("#btnImport").attr("disabled","disabled");
			$.ajax({
				url:baseUrl + "zzjmes/machinePlan/upload",
				data:form,
				type:"post",
				contentType:false,
				processData:false,
				success:function(resp){
					js.closeLoading();
					if(resp.code == 0){
						vm.showTable(resp.data);
						vm.error_data=resp.data;
						js.showMessage("保存成功："+resp.saveCount+"条; 校验失败："+resp.errorCount+"条！");
					}else{
						vm.error_data=[];
						js.showErrorMessage(resp.msg);
					} 
				},
				complete:function(XMLHttpRequest, textStatus){
					$("#btnImport").val("导入");	
					$("#btnImport").removeAttr("disabled");
				}
			});
		},
		exp:function(){
			$("#entityList").val(JSON.stringify(vm.error_data));
			$("#exportForm").submit();
		},
		load:function(){
			if(vm.workshop==''){
				js.showErrorMessage("请选择车间");
				return false;
			}
			if(vm.line==''){
				js.showErrorMessage("请选择线别");
				return false;
			}
			if($("#search_order").val()==''){
				js.showErrorMessage("请输入订单");
				return false;
			}
			$("#expTemplateForm").submit();
		},
		showTable:function(mydata){
			$("#dataGrid").dataGrid({
				datatype: "local",
				data: mydata,
		        colModel: [		
		        	//{label: '序号', name: 'row_no',index:"row_no", width: "70",align:"center",sortable:false,frozen: true},
		        	{label: 'SAP码', name: 'sap_mat',index:"sap_mat", width: "60",align:"center",sortable:false,frozen: true},
		            {label: '<SPAN STYLE="COLOR:RED">*</SPAN>使用工序', name: 'process',index:"process", width: "70",align:"center",sortable:false,frozen: true,},
		            {label: '<SPAN STYLE="COLOR:RED">*</SPAN>装配位置', name: 'assembly_position',index:"assembly_position", width: "80",align:"center",sortable:false,frozen: true},
		            {label: '<SPAN STYLE="COLOR:RED">*</SPAN>零部件号', name: 'zzj_no',index:"zzj_no", width: "120",align:"center",sortable:true,frozen: true},
		            {label: '零部件名称', name: 'zzj_name',index:"zzj_name", width: "120",align:"center",sortable:false,frozen: true},
		            {label: '<SPAN STYLE="COLOR:RED">*</SPAN>工艺流程', name: 'process_flow',index:"process_flow", width: "130",align:"center",sortable:true},
		            {label: '<SPAN STYLE="COLOR:RED">*</SPAN>计划工序', name: 'plan_process',index:"plan_process", width: "70",align:"center",sortable:false},
		            {label: '<SPAN STYLE="COLOR:RED">*</SPAN>计划数量', name: 'plan_quantity',index:"plan_quantity", width: "70",align:"center",sortable:false},
		            {label: '<SPAN STYLE="COLOR:RED">*</SPAN>机台', name: 'machine',index:"machine", width: "110",align:"center",sortable:false},
		            {label: '<SPAN STYLE="COLOR:RED">*</SPAN>计划日期', name: 'plan_date',index:"plan_date", width: "80",align:"center"},
		            {label: '生产工单', name: 'product_order',index:"product_order", width: "75",align:"center",sortable:false},
		            {label: '校验消息', name: 'msg',index:"msg", width: "220",align:"center",sortable:false,cellattr: addCellAttr},
		        ],
		        loadComplete:function(){
					js.closeLoading();
					$(".btn").attr("disabled", false);
				},
				shrinkToFit: false,
				rownumbers: false,
		        cellurl:'#',
				cellsubmit:'clientArray',
		    });	
		}	
	}
});
$(function () {
	vm.showTable();
});

function reset(){
	$("#btnImport").removeAttr("disabled");
	$("#search_order").val("");
	$("#dataGrid").jqGrid("clearGridData", true);
}
function addCellAttr(rowId, val, rowObject, cm, rdata) {
    if(rowObject.msg != null && rowObject.msg!='' ){
        return "style='color:red'";
    }
}
function getProcessList(){
	$.ajax({
		url : baseUrl + "masterdata/getWorkshopProcessList",
		data : {
			"WERKS" : vm.werks,
			"WORKSHOP" : vm.workshop,
		},
		success : function(resp) {
			vm.processList = resp.data;
		}
	})
}