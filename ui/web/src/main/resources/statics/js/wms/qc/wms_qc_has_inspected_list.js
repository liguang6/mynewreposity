$(function() {
	$("#dataGrid")
			.dataGrid(
					{
						url : baseUrl
								+ 'qc/wmsqcinspectionitem/listHasInspected',
						datatype : "json",
						colNames : [ 'id', '批次', '料号', '物料描述',
								'库位', '仓库号', '可改判数量', '质检改判', '单位', '供应商代码',
								'供应商名称', '工厂', '收货时间' ,'紧急物料','收货单号', '行项目'],
						searchForm : $("#searchForm"),
						colModel : [
								{
									name : 'ID',
									index : 'ID',
									width : 100,
									key : true,
									hidden : true
								},
								{
									name : 'BATCH',
									index : 'BATCH',
									width : 100,
									align : "center"
								},
								{
									name : 'MATNR',
									index : 'MATNR',
									width : 100,
									align : "center"
								},
								{
									name : 'MAKTX',
									index : 'MAKTX',
									width : 250,
									align : "center"
								},
								{
									name : 'LGORT',
									index : 'LGORT',
									width : 70,
									align : "center"
								},
								{
									name : 'WH_NUMBER',
									index : 'WH_NUMBER',
									width : 70,
									align : "center",
									editable : true
								},
								{
									name : 'UPDATEABLE',
									index : 'UPDATEABLE',
									width : 100,
									align : "center"
								},
								{
									name : 'UPDATE',
									index : 'UPDATE',
									width : 70,
									formatter : function(val, obj, row, act) {
										if(row.BARCODE_FLAG == 'X'){
											return '<span title="仓库已启用条码管理，请使用PDA功能维护改判结果"> <a  class="btn btn-default disabled" role="button">检验</a> </span>'
										}
										var url = baseUrl
												+ "qc/redirect/inspectionrejudge?id="
												+ row.ID;
										return '<a class="btn btn-primary btnList" title="已质检改判" href="'
												+ url + '">检验</a>'
									},
									align : "center"
								}, 
								{
									name : 'UNIT',
									index : 'UNIT',
									width : 50,
									align : "center"
								}, {
									name : 'LIFNR',
									index : 'LIFNR',
									width : 90,
									align : "center"
								}, {
									name : 'LIKTX',
									index : 'LIKTX',
									width : 250,
									align : "center"
								}, {
									name : 'WERKS',
									index : 'WERKS',
									width : 50,
									align : "center"
								}, {
									name : 'RECEIPT_DATE',
									index : 'RECEIPT_DATE',
									width : 120,
									align : "center"
								},{
									name:"URGENT_FLAG",
									index:"URGENT_FLAG",
									hidden:true
								},
								{
									name : 'RECEIPT_NO',
									index : 'RECEIPT_NO',
									width : 120,
									align : "center"
								},
								{
									name : 'RECEIPT_ITEM_NO',
									index : 'RECEIPT_ITEM_NO',
									width : 70,
									align : "center"
								}
						],
						cellEdit : false,
						autowidth : true,
						shrinkToFit : false,
						autoScroll : true,
						multiselect : true,
						gridComplete:function(){
					        	//紧急物料（urgentFlag不为空的）设置自定义样式
					            var ids = $("#dataGrid").getDataIDs();
					            for(var i=0;i<ids.length;i++){
					                var rowData = $("#dataGrid").getRowData(ids[i]);
					                if(rowData.URGENT_FLAG != null && rowData.URGENT_FLAG !='' && rowData.URGENT_FLAG != undefined){
					                	//如果是紧急物料设置自定义的颜色
					                    $('#'+ids[i]).find("td").addClass("urgent-select");
					                }
					            }
					        }
					});
})

var vm = new Vue({
	el : "#vue",
	data : {
		warehourse : []
	},
	created : function() {
		// 初始化查询仓库
		var plantCode = $("#werks").val();
		// 初始化仓库
		$.ajax({
      	  url:baseUrl + "common/getWhDataByWerks",
    	  data:{"WERKS":plantCode},
			success : function(resp) {
				vm.warehourse = resp.data;
			}
		});
	},
	methods : {
		onPlantChange : function(event) {
			// 工厂变化的时候，更新库存下拉框
			var plantCode = event.target.value;
			if (plantCode === null || plantCode === ''
					|| plantCode === undefined) {
				return;
			}
			//查询工厂仓库
			$.ajax({
		      	url:baseUrl + "common/getWhDataByWerks",
		    	data:{"WERKS":plantCode},
				success : function(resp) {
					vm.warehourse = resp.data;
				}
			});
		},
		inspect : function() {

		},
		query : function() {
			if ($("#inspectionItemStatus").val() === '00' || $("#inspectionItemStatus").val() === '01') {
				//跳转到 (未质检 ,已质检)
				var queryparams = $("#searchForm").serialize()
				window.location.href = baseUrl + "qc/redirect/redirecttoinspect?"+queryparams
			}
		},

		exp: function () {
			exportExcel();
		}
	}
});

function exportExcel(){
	var column=[
		{"title":'批次', "data":'BATCH',"class":"center"},
		{"title":'料号', "data":'MATNR', "class":"center"},
		{"title":'物料描述', "data":'MAKTX', "class":"center"},
		{"title":'库位', "data":'LGORT',"class":"center"},
		{"title":'仓库号', "data":'WH_NUMBER', "class":"center"},
		{"title":'可改判数量', "data":'UPDATEABLE', "class":"center"},
		{"title":'单位', "data":'UNIT', "class":"center"},
		{"title":'供应商代码', "data":'LIFNR', "class":"center"},
		{"title":'供应商名称', "data":'LIKTX',  "class":"center"},
		{"title":'工厂', "data":'WERKS',  "class":"center"},
		{"title":'收货时间', "data":'RECEIPT_DATE',  "class":"center"},
		{"title":'收货单号', "data":'RECEIPT_NO',  "class":"center"},
		{"title":'行项目号', "data":'RECEIPT_ITEM_NO', "class":"center"},
	]	;
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i");
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+'qc/wmsqcinspectionitem/listHasInspected',
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) {
			console.log(response.page.list)
			results=response.page.list;
		}
	});
	var rowGroups=[];
	getMergeTable(results,column,rowGroups,"来料质检清单");
}