
var vm = new Vue({
	el:'#rrapp',
	data:{
    mydata:[],
    workshoplist:[],
		check:true
	},
	created:function(){
		getWorkShopList();
	},
	methods: {
		fun_query: function () {
			if($("#search_order").val() === ''){
				alert("请输入订单号查询！")
				return false;
			}
			$("#btnSearchData").attr("disabled","disabled");
			$.ajax({
				url:baseURL+"bjmes/productManage/getProductsForNoManage",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":$("#search_werks").val(),
					"ORDER_NO":$("#search_order").val(),
					"WORKSHOP":$("#search_workshop").val(),
					"PRODUCT_TYPE_CODE":$("#search_prductTypeCode").val(),
					"PRODUCT_NAME":$("#search_prductTypeName").val(),
					"PRODUCT_NO":$("#search_productNo").val()
				},
				async: true,
				success: function (response) { 
					$("#dataGrid").jqGrid("clearGridData", true);
					vm.mydata.length=0;
					if(response.code == "0"){
						if(response.data.length === 0){
							vm.mydata = [];
							js.showMessage("没有查询到任何数据！");
						}else{
							vm.mydata = response.data;
						}
					}
					$("#dataGrid").jqGrid('GridUnload');
					fun_ShowTable();
					$("#btnSearchData").removeAttr("disabled");
				}
			})

		},
		fun_generate: function() {
			$("#btnSearchData").attr("disabled","disabled");
			$("#btnGenerate").attr("disabled","disabled");
			$("#btnGenerate").val('生成中..')
			$.ajax({
				url:baseURL+"bjmes/productManage/generateProductsNo",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":$("#search_werks").val(),
					"ORDER_NO":$("#search_order").val(),
					"WORKSHOP":$("#search_workshop").val(),
					"PRODUCT_TYPE_CODE":$("#search_prductTypeCode").val(),
					"PRODUCT_NAME":$("#search_prductTypeName").val()
				},
				async: true,
				success: function (response) { 
					$("#btnSearchData").removeAttr("disabled");
					$("#btnGenerate").removeAttr("disabled");
					$("#btnGenerate").val('生成')
					if(response.result == "-1"){
						alert("编号已生成！")
					}else{
						alert("编号生成成功，共生成" +response.result + "个产品编号！" )
					}
				}
			})
		},
		fun_delete: function() {
			
		},
		getOrderNoFuzzy:function(){
			getBjOrderNoSelect("#search_order",null,null)
		},
		refresh : function() {
			$('#dataGrid').trigger('reloadGrid');
		},
		onWerksChange:function(event) {
			getWorkShopList()
			$("#dataGrid").jqGrid("clearGridData", true);
			vm.mydata.length=0;
			fun_ShowTable();
		},
		onWorkshopChange:function(event) {
			$("#dataGrid").jqGrid("clearGridData", true);
			vm.mydata.length=0;
			fun_ShowTable();
		}
	},
	watch: {
		
	}
});
$(function () {
	fun_ShowTable();
	
	$(document).on("click",".removeEcnTr",function(e){
		$(e.target).closest("tr").remove();		
	})
	
	$("#btn_delete").click(function () {
	});
	$(document).bind("click",function(e){
		if("searchForm" == $(e.target).attr("id")||"ui-jqgrid-bdiv" == $(e.target).attr("class")||"form-inline" == $(e.target).attr("class")){
			$('#dataGrid').jqGrid("saveCell", vm.lastrow, vm.lastcell);
		}
	});
	
});
function fun_ShowTable(){
	$("#dataGrid").dataGrid({
		datatype: "local",
		data: vm.mydata,
		columnModel: [
      { label: '产品编号', name: 'product_no',align:'center', index: 'product_no', width: 150 ,sortable:false,frozen: false}, 		
      { label: '订单', name: 'order_no', align:'center', index: 'order_no', width: 100 ,frozen: false,sortable:false,editable:false},
      { label: '工厂', name: 'werks', align:'center', index: 'werks', width: 80 ,frozen: false,sortable:false,editable:false},
      { label: '车间', name: 'workshop_name', align:'center', index: 'workshop_name', width: 60 ,frozen: false,sortable:false,editable:false},
      { label: '产品类别', name: 'product_type_code', align:'center', index: 'product_type_code', width: 80 ,frozen: false,sortable:false,editable:false},
      { label: '类别名称', name: 'product_type_name', align:'center', index: 'product_type_name', width: 80 ,frozen: false,sortable:false,editable:false},
      { label: '产品名称', name: 'product_name', align:'center', index: 'product_name', width: 150 ,frozen: false,sortable:false,editable:false},
      { label: '生成者', name: 'editor', align:'center', index: 'editor', width: 100 ,frozen: false,sortable:false,editable:false},
      { label: '生成时间', name: 'edit_date', align:'center', index: 'edit_date', width: 150 ,frozen: false,sortable:false,editable:false},
      { label: 'id', name: 'id', align:'center', index: 'id', width: 10 ,hidden:true}, 
    ],
    formatCell:function(rowid, cellname, value, iRow, iCol){
    },
    onCellSelect:function(rowid,iCol,cellcontent,e){
		},
		beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			vm.check = false;
		},
		afterEditCell:function(rowid, cellname, v, iRow, iCol){			
			vm.check = true;
		},
		beforeSelectRow : function(rowid, e) { // 点击复选框才选中行
			var $myGrid = $(this), i = $.jgrid.getCellIndex($(e.target).closest('td')[0]), cm = $myGrid.jqGrid('getGridParam', 'colModel');
			if (cm[i].name == 'cb') {
				$('#dataGrid').jqGrid('setSelection', rowid);
			}
			return (cm[i].name == 'cb');
		},
		beforeSaveCell:function(rowid, cellname, value, iRow, iCol){
		},
		afterSaveCell:function(rowid, cellname, value, iRow, iCol){
		},
		shrinkToFit: false,
		width:4000,
		cellEdit:true,
		cellurl:'#',
		cellsubmit:'clientArray',
		showCheckbox:true
    });
}

function getWorkShopList(){
	$.ajax({
  	  url:baseUrl + "masterdata/getUserWorkshopByWerks",
  	  data:{
  		  "WERKS":$("#search_werks").val(),
  		  "MENU_KEY":"ZZJMES_PMD_MANAGE",
  	  },
  	  //async:false,
  	  success:function(resp){
  		  vm.workshoplist = resp.data;
  	  }
    })
}




