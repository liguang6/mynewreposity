var last_scan_ele = "";
	
var vm = new Vue({
	el : '#rrapp',
	data : {
		werks:'',
		workshop:'',
		line:'',
		line_list:[],
		workshop_list:[],
		menu_key:'ZZJMES_PMD_COMB_QUERY',
	},
	watch:{
		werks : {
		  handler:function(newVal,oldVal){
			  vm.getUserWorkshop(newVal);
		  }
		},
		workshop: {
			handler:function(newVal,oldVal){
				vm.getUserLine(newVal);
			}
		},
	},
	methods:{
		getOrderNoFuzzy:function(){
			getZZJOrderNoSelect("#search_order",null,vm.getBatchSelects);
		},
		getBatchSelects:function(selectval){
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
		},
		getUserWorkshop : function(werks){
			$.ajax({
				url:baseURL+"masterdata/getUserWorkshopByWerks",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":werks,
					"MENU_KEY":vm.menu_key,
				},
				async: true,
				success: function (response) { 
					vm.workshop_list=response.data;
					vm.workshop=vm.workshop||response.data[0]['code'];
					if(vm.workshop !=''){
						vm.getUserLine(vm.workshop);
					}
				}
			});	
		},
		getUserLine :function(workshop){
			$.ajax({
				url:baseURL+"masterdata/getUserLine",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":vm.werks,
					"WORKSHOP":workshop,
					"MENU_KEY":vm.menu_key,
				},
				async: true,
				success: function (response) { 
					vm.line_list=response.data;
					vm.line=response.data[0]['code'];
				}
			});	
		},	
		query : function(){
			if(vm.werks==""){
				js.showErrorMessage("请选择工厂！",3000);
				return false;
			}
			if(vm.workshop==""){
				js.showErrorMessage("请选择车间！",3000);
				return false;
			}
			if(vm.line==""){
				js.showErrorMessage("请选择线别！",3000);
				return false;
			}
			if($("#zzj_no").val().trim()==""){
				js.showErrorMessage("请输入零部件号！",3000);
				return false;
			}
			if($("#search_order").val().trim()==""){
				js.showErrorMessage("请输入订单！",3000);
				return false;
			}
			var PMD_LEVEL = $("#PMD_LEVEL").multipleSelect('getSelects').join(",")||"";
			$("#PMD_LEVEL_SUBMIT").val(PMD_LEVEL);
			ajaxQuery();
		},
	},
	created: function(){
		this.werks = $("#werks").find("option").first().val();		
	}
})	

$(function(){
	$("#PMD_LEVEL").multipleSelect('destroy').multipleSelect({
        selectAll: true,
    }).multipleSelect('uncheckAll');
	$("#PMD_LEVEL").multipleSelect('setSelects',['L1','L0'])
})

function funFromjs(result) {
	var mat = JSON.parse(result);	
	vm.batch = mat.zzj_plan_batch;
	vm.order_no= mat.order_no;
	var e = $.Event('keydown');
	e.keyCode = 13;
	$(last_scan_ele).val(mat.zzj_no).trigger(e);
}

function doScan(ele) {
	var url = "../../doScan";
	last_scan_ele = $("#" + ele);
	var iframe_id=self.frameElement.getAttribute('id');
	$(window.parent.document).find("#activeIframe").val(iframe_id)
	var a = document.createElement("a");
	a.target="_parent";
	a.href = url;
	a.click();
}

var ajaxQuery = function(){
	var data = $("#dataGrid").data("dataGrid");
	if (data) {
		$("#dataGrid").jqGrid('GridUnload');
		js.closeLoading();
		$(".btn").attr("disabled", false);
		/*$(vm.table_id).jqGrid("clearGridData");
		$(vm.table_id).jqGrid('setGridParam',{datatype:'local'}).trigger('reloadGrid'); */
	}
	
	$('#dataGrid').dataGrid({
		searchForm: $("#searchForm"),
		columnModel: [
			{label: '明细序号', name: 'no',index:"no", width: "50",align:"center",sortable:true,},
            {label: '图号', name: 'material_no',index:"material_no", width: "120",align:"center",sortable:false,}, 
            {label: '零部件号', name: 'zzj_no',index:"zzj_no", width: "120",align:"center",},
            {label: '零部件名称', name: 'zzj_name',index:"zzj_name", width: "130",align:"center",sortable:false,},
            {label: '批次', name: 'batch',index:"batch", width: "60",align:"center",sortable:true,},
            {label: '批次车付', name: 'batch_qty',index:"batch_qty", width: "80",align:"center",sortable:false,},
            {label: '当前工序', name: 'current_process_name',index:"current_process_name", width: "100",align:"center",sortable:false,},
            {label: '需求数量', name: 'demand_quantity',index:"demand_quantity", width: "70",align:"center",sortable:true,},
            {label: '计划数量', name: 'plan_quantity',index:"plan_quantity", width: "70",align:"center",sortable:false,},
            {label: '已产数量', name: 'prod_quantity',index:"prod_quantity", width: "70",align:"center",sortable:false,},
            {label: '欠产数量', name: '',index:"", width: "70",align:"center",sortable:false,formatter:function(val, obj, row, act){
            	
            	return isNaN(Number(row.demand_quantity)-Number(row.prod_quantity))?"":Number(row.demand_quantity)-Number(row.prod_quantity)
            },},
            {label: '材料类型', name: 'mat_type',index:"mat_type", width: "90",align:"center",sortable:false,},
            {label: '使用车间', name: 'use_workshop',index:"use_workshop", width: "70",align:"center",sortable:false,},
            {label: '工艺流程', name: 'process_flow',index:"process_flow", width: "110",align:"center",sortable:false,},           
		],	
		viewrecords: true,
		showRownum : false,
        shrinkToFit:false,
        rowNum: 15,  
		rowList : [15,50 ,100],	

	});
}
