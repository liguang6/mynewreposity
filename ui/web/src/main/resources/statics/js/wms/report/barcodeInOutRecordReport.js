
var vm = new Vue({
	el:'#rrapp',
	data:{
		showDiv:false,
		lgort:[],
		wh_list:[],
		werks:'',
		whNumber:'',
	},
	watch:{
		werks : {
			handler:function(newVal,oldVal){
				$.ajax({
					url:baseURL+"common/getWhDataByWerks",
					dataType : "json",
					type : "post",
					data : {
						"WERKS":newVal
					},
					async: true,
					success: function (response) {
						vm.wh_list=response.data
						vm.whNumber=response.data[0]['WH_NUMBER']
						vm.lgort=getLgortList();
					}
				});

			}
		}
	},
	methods: {
		query: function () {
			$("#pageNo").val(1);
			$("#searchForm").submit();
		},

		exp:function(){
			export2Excel();
		},
		show:function(){
			vm.showDiv=!vm.showDiv;
			$('#dataGrid').trigger('reloadGrid');
		}
	},
	created:function(){
		this.werks=$("#werks").find("option").first().val();
	}
});
$(function () {
	var now = new Date(); //当前日期
	var startDate=new Date(now.getTime()-7*24*3600*1000);
	startDate.setHours(0);
	startDate.setMinutes(0);
	startDate.setSeconds(0);
	$("#createDateStart").val(startDate.Format("yyyy-MM-dd hh:mm:ss"));
	$("#createDateEnd").val(now.Format("yyyy-MM-dd hh:mm:ss"));

	var wmsNo=getUrlKey("wmsNo");
	var werks=getUrlKey("werks");
	console.info(werks);
	$("#wmsNo").val(wmsNo);
	if(werks !=null && werks!=''){
		vm.werks=werks;
		$("#werks option[value='"+werks+"']").prop("selected",true);
	}
	if(wmsNo==''){
		var now = new Date(); //当前日期
		var startDate=new Date(now.getTime()-30*24*3600*1000);
		startDate.setHours(0);
		startDate.setMinutes(0);
		startDate.setSeconds(0);
		$("#createDateStart").val(startDate.Format("yyyy-MM-dd hh:mm:ss"));
		$("#createDateEnd").val(now.Format("yyyy-MM-dd hh:mm:ss"));
	}
	$('#dataGrid').dataGrid({
		searchForm: $("#searchForm"),
		columnModel: [
			{header:'工厂', name:'WERKS', width:60, align:"center",frozen: true},
			{header:'仓库号', name:'WH_NUMBER', width:60, align:"center",frozen: true},
			{header:'库位', name:'LGORT', width:60, align:"center",frozen: true},
			{header:'移动类型', name:'WMS_MOVE_TYPE', width:70, align:"center",frozen: true},
			{header:'业务类型名称', name:'BUSINESS_NAME_DESC', width:100, align:"center",frozen: true},
			{header:'料号', name:'MATNR', width:100, align:"center",frozen: true},
			{header:'物料描述', name:'MAKTX', width:150, align:"center"},
			{header:'供应商代码', name:'LIFNR', width:80, align:"center"},
			{header:'供应商名称', name:'LIKTX', width:200, align:"center"},
			{header:'批次', name:'BATCH', width:100, align:"center"},
			{header:'数量', name:'QTY_WMS', width:50, align:"center"},
			{header:'单位', name:'UNIT', width:60, align:"center"},
			{header:'记账日期', name:'JZ_DATE', width:90, align:"center"},
			{header:'交接人员', name:'HANDOVER', width:70, align:"center"},
			{header:'WMS凭证', name:'WMS_NO', width:130, align:"center"},
			{header:'WMS凭证行项目号', name:'WMS_ITEM_NO', width:150, align:"center"},
			//{header:'行文本', name:'ITEM_TEXT', width:130, align:"center"},
			{header:'SAP凭证', name:'WMS_SAP_MAT_DOC', width:150, align:"center"},
			{header:'订单号', name:'SO_NO', width:140, align:"center"},
			{header:'需求跟踪号', name:'BEDNR', width:80, align:"center"},
			{header:'单据编号', name:'TRY_QTY', width:80, align:"center"},
			{header:'参考凭证', name:'REF_WMS_NO', width:80, align:"center"},
			{header:'交接人', name:'HANDOVER', width:80, align:"center"},
			{header:'成本中心', name:'COST_CENTER', width:80, align:"center"},
			{header:'WBS', name:'WBS', width:80, align:"center"},
			{header:'源工厂', name:'F_WERKS', width:80, align:"center"},
			{header:'源库位', name:'F_LGORT', width:80, align:"center"},
			{header:'操作人', name:'CREATOR', width:100, align:"center"},
			{header:'操作日期', name:'CREATE_DATE', width:140, align:"center"},
			{header:'抬头文本', name:'HEADER_TXT', width:80, align:"center"},
			{header:'预留编号', name:'RSNUM', width:80, align:"center"},
			{header:'进仓单号', name:'INBOUND_NO', width:80, align:"center"},
			{header:'行项目', name:'INBOUND_ITEM_NO', width:80, align:"center"},
			{header:'收货单号', name:'RECEIPT_NO', width:80, align:"center"},
			{header:'收货单行项目', name:'RECEIPT_ITEM_NO', width:80, align:"center"},
			{header:'SCM送货单号', name:'ASNNO', width:80, align:"center"},
			{header:'SCM送货单行项目', name:'ASNITM', width:80, align:"center"},
			{header:'生产订单号', name:'MO_NO', width:80, align:"center"},
			{header:'行项目', name:'MO_ITEM_NO', width:80, align:"center"},
			{header:'出库需求号', name:'REQUIREMENT_NO', width:80, align:"center"},
			{header:'行项目', name:'REQUIREMENT_ITEM_NO', width:80, align:"center"},
			{header:'采购订单号', name:'PO_NO', width:80, align:"center"},
			{header:'采购订单行项目', name:'PO_ITEM_NO', width:80, align:"center"},
			{header:'SAP交货单号', name:'SAP_OUT_NO', width:80, align:"center"},
			{header:'SAP交货单行项目号', name:'SAP_OUT_ITEM_NO', width:80, align:"center"},
			{header:'退货单号', name:'RETURN_NO', width:80, align:"center"},
			{header:'退货单行项目', name:'RETURN_ITEM_NO', width:80, align:"center"},
			{header:'配送单号', name:'DISTRIBUTION_NO', width:80, align:"center"},
			{header:'配送单行项目', name:'DISTRIBUTION_ITEM_NO', width:80, align:"center"},
			{header:'标签号', name:'LABEL_NO', width:100, align:"center",formatter: function(val, obj, row, act){
					var actions = [];
					actions.push('<a href="#" title="条码明细" onclick=showBarInfos("'+row.WMS_NO+'","'+row.WMS_ITEM_NO+'","'+row.WERKS+'","'+row.WH_NUMBER+'")><i class="ace-icon fa fa-barcode black bigger-160 btn_scan" style="cursor: pointer"></i></a>');
					return actions.join('  ');
				},unformat:function(cellvalue, options, rowObject){
					return "";
				}
			}

		],
		viewrecords: true,
		shrinkToFit:false,
		showCheckbox:true,
		rowNum: 15,
		rowList : [15,50,100,200],
		width:3500,
		height:'auto'
	});
	//$("#dataGrid").jqGrid("setFrozenColumns");
	var hideCol=['INBOUND_NO','INBOUND_ITEM_NO','RECEIPT_NO','RECEIPT_ITEM_NO','ASNNO','ASNITM','MO_NO','MO_ITEM_NO',
		'REQUIREMENT_NO','REQUIREMENT_ITEM_NO','PO_NO','PO_ITEM_NO','SAP_OUT_NO','SAP_OUT_ITEM_NO',
		'RETURN_NO','RETURN_ITEM_NO','MO_NO','MO_ITEM_NO','DISTRIBUTION_NO','DISTRIBUTION_ITEM_NO'];
	DynamicColume(hideCol,"列选择","#dataGrid","#example-getting-started");
	$(window).resize(function(){
		$("#dataGrid").setGridWidth($(window).width());
		$("#dataGrid").setGridHeight($(window).height());
	});
});
/*function export2Excel(){
	var column=[
		{"title":'工厂', "data":'WERKS', "class":"center"},
		{"title":'仓库号', "data":'LGORT', "class":"center"},
		{"title":'库位', "data":'LGORT', "class":"center"},
		{"title":'移动类型', "data":'WMS_MOVE_TYPE', "class":"center"},
		{"title":'料号', "data":'MATNR', "class":"center"},
		{"title":'物料描述', "data":'MAKTX', "class":"center"},
		{"title":'供应商代码', "data":'LIFNR', "class":"center"},
		{"title":'供应商名称', "data":'LIKTX', "class":"center"},
		{"title":'批次', "data":'BATCH', "class":"center"},
		{"title":'数量', "data":'QTY_WMS', "class":"center"},
		{"title":'单位', "data":'UNIT', "class":"center"},
		{"title":'记账日期', "data":'JZ_DATE', "class":"center"},
		{"title":'交接人员', "data":'HANDOVER', "class":"center"},
		{"title":'WMS凭证', "data":'WMS_NO',"class":"center"},
		{"title":'SAP凭证', "data":'WMS_SAP_MAT_DOC',"class":"center"},
		{"title":'订单号', "data":'SO_NO', "class":"center"},
		{"title":'需求跟踪号', "data":'BEDNR',"class":"center"},
		{"title":'单据编号', "data":'TRY_QTY', "class":"center"},
		{"title":'参考凭证', "data":'REF_WMS_NO', "class":"center"},
		{"title":'操作人', "data":'CREATOR', "class":"center"},
		{"title":'操作日期', "data":'CREATE_DATE', "class":"center"},
		{"title":'成本中心', "data":'COST_CENTER', "class":"center"},
		{"title":'WBS', "data":'WBS', "class":"center"},
		{"title":'源工厂', "data":'F_WERKS',  "class":"center"},
		{"title":'源库位', "data":'F_LGORT', "class":"center"},
		{"title":'抬头文本', "data":'HEADER_TXT', "class":"center"},
		{"title":'预留编号', "data":'RSNUM',"class":"center"},
		{"title":'进仓单号', "data":'INBOUND_NO', "class":"center"},
		{"title":'行项目', "data":'INBOUND_ITEM_NO', "class":"center"},
		{"title":'收货单号', "data":'RECEIPT_NO', "class":"center"},
		{"title":'收货单行项目', "data":'RECEIPT_ITEM_NO', "class":"center"},
		{"title":'SCM送货单号', "data":'ASNNO', "class":"center"},
		{"title":'SCM送货单行项目', "data":'ASNITM', "class":"center"},
		{"title":'生产订单号', "data":'MO_NO', "class":"center"},
		{"title":'行项目', "data":'MO_ITEM_NO', "class":"center"},
		{"title":'出库需求号', "data":'REQUIREMENT_NO', "class":"center"},
		{"title":'行项目', "data":'REQUIREMENT_ITEM_NO', "class":"center"},
		{"title":'采购订单号', "data":'PO_NO', "class":"center"},
		{"title":'采购订单行项目', "data":'PO_ITEM_NO',"class":"center"},
		{"title":'SAP交货单号', "data":'SAP_OUT_NO', "class":"center"},
		{"title":'SAP交货单行项目号', "data":'SAP_OUT_ITEM_NO', "class":"center"},
		{"title":'退货单号', "data":'RETURN_NO', "class":"center"},
		{"title":'退货单行项目', "data":'RETURN_ITEM_NO', "class":"center"},
		{"title":'配送单号', "data":'DISTRIBUTION_NO', "class":"center"},
		{"title":'配送单行项目', "data":'DISTRIBUTION_ITEM_NO', "class":"center"},
		{"title":'标签号', "data":'LABEL_NO', "class":"center"},
	]	;
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i");
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"query/docQuery/list",
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) {
			results=response.page.list;
		}
	})
	var rowGroups=[];
//	getMergeTable(results,column,rowGroups,"事务记录");
	toExcel("事务记录",results,column);
}*/
function getLgortList(){
	var lgortList=[];
	//查询库位
	$.ajax({
		url:baseUrl + "common/getAllLoList",
		async:false,
		data:{
			"WERKS":$("#werks").val(),
		},
		success:function(resp){
			lgortList = resp.data;
		}
	});

	return lgortList;
}
function getUrlKey(name){
	return decodeURIComponent((new RegExp('[?|&]'+name+'='+'([^&;]+?)(&|#|;|$)').exec(location.href)||[,""])[1].replace(/\+/g,'%20'))||null;
}

/**
 * 点击条码操作，显示WMS事务记录关联的条码信息
 * @param asnno
 */
function showBarInfos(wms_no,wms_item_no,werks,wh_number){
	var options = {
		type: 2,
		maxmin: true,
		shadeClose: true,
		title: '<i aria-hidden="true" class="glyphicon glyphicon-search">&nbsp;</i>条码明细',
		area: ["1060px", "500px"],
		content: "wms/query/barcodeInOutRecordQuery.html",
		btn: [],
		success:function(layero, index){
			var win = top[layero.find('iframe')[0]['name']];
			console.log(wms_no+" "+wms_item_no+" "+werks+" "+wh_number);
			win.vm.getBarDetail(wms_no,wms_item_no,werks,wh_number);
		},
	};
	options.btn.push('<i class="fa fa-close"></i> 关闭');
	options['btn'+options.btn.length] = function(index, layero){
		js.layer.close(index);
	};
	js.layer.open(options);
}