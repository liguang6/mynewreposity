
var vm = new Vue({
	el:'#rrapp',
	data:{
		showDiv:false,
		warehourse:[],
		lgort:[]
	},
	created:function(){
		var werks = $("#werks").val();
		//初始化仓库
        $.ajax({
          url:baseUrl + "common/getWhDataByWerks",
      	  data:{"WERKS": werks},
      	  success:function(resp){
      		 vm.warehourse = resp.data;
      	  }
        });
      //查询库位
      $.ajax({
        url:baseUrl + "common/getLoList",
        data:{
        	"DEL":'0',
        	"WERKS":$("#werks").val(),
        },
        success:function(resp){
        	vm.lgort = resp.data;
        }
      });
	},
	methods: {
		query: function () {
			$("#searchForm").submit();
		},
		onPlantChange:function(event){
		  //工厂变化的时候，更新库存下拉框
          var plantCode = event.target.value;
          if(plantCode === null || plantCode === '' || plantCode === undefined){
        	  return;
          }
          //查询工厂仓库
          $.ajax({
              url:baseUrl + "common/getWhDataByWerks",
          	  data:{"WERKS": plantCode},
        	  success:function(resp){
        		 vm.warehourse = resp.data;
        	  }
          });
	        //查询库位
	      $.ajax({
	        url:baseUrl + "common/getLoList",
	        data:{
	        	"DEL":'0',
	        	"WERKS":$("#werks").val(),
	        },
	        success:function(resp){
	        	vm.lgort = resp.data;
	        }
	       });
		},
		exp:function(){
			export2Excel();
		},
		show:function(){
			vm.showDiv=!vm.showDiv;
			$('#dataGrid').trigger('reloadGrid');
		}
	}
});
$(function () {
	var wmsNo=getUrlKey("wmsNo");
	$("#wmsNo").val(wmsNo);
	var now = new Date(); //当前日期
	var startDate=new Date(now.getTime()-30*24*3600*1000);
	$("#createDateStart").val(formatDate(startDate));
	$("#createDateEnd").val(formatDate(now));
	$('#dataGrid').dataGrid({
		searchForm: $("#searchForm"),
		columnModel: [
			{"header":'WMS凭证', "name":'WMS_NO',"class":"center"},
			{"header":'项目号', "name":'WMS_ITEM_NO',"class":"center"},
			{"header":'订单号', "name":'PO_NO', "class":"center"},
			{"header":'移动类型', "name":'WMS_MOVE_TYPE', "class":"center"},
			{"header":'料号', "name":'MATNR', "class":"center"},
			{"header":'物料描述', "name":'MAKTX', "class":"center"},

			{"header":'条码号', "name":'LABELN0', "class":"center"},
			{"header":'数量', "name":'BOX_QTY', "class":"center"},
			{"header":'单位', "name":'UNIT', "class":"center"},
			{"header":'库位', "name":'LGORT', "class":"center"},
			{"header":'供应商代码', "name":'LIFNR', "class":"center"},
			{"header":'品质状态', "name":'QC_RESULT_CODE', "class":"center"},
			{"header":'BYD批次', "name":'BATCH', "class":"center"},
/*			{header:'工厂', name:'WERKS', width:60, align:"center",frozen: true},
			{header:'库位', name:'LGORT', width:60, align:"center",frozen: true},
			{header:'移动类型', name:'WMS_MOVE_TYPE', width:70, align:"center",frozen: true},
			{header:'料号', name:'MATNR', width:100, align:"center",frozen: true},
			{header:'物料描述', name:'MAKTX', width:150, align:"center"},
			{header:'供应商代码', name:'LIFNR', width:80, align:"center"},
			{header:'批次', name:'BATCH', width:100, align:"center"},
			{header:'数量', name:'QTY_WMS', width:50, align:"center"},
			{header:'单位', name:'UNIT', width:60, align:"center"},
			{header:'记账日期', name:'JZ_DATE', width:90, align:"center"},
			{header:'交接人员', name:'HANDOVER', width:70, align:"center"},
			{header:'WMS凭证', name:'WMS_NO', width:130, align:"center"},
			{header:'订单号', name:'SO_NO', width:140, align:"center"},
			{header:'需求跟踪号', name:'BEDNR', width:80, align:"center"},
			{header:'单据编号', name:'TRY_QTY', width:80, align:"center"},
			{header:'参考凭证', name:'REF_WMS_NO', width:80, align:"center"},
			//{header:'供应商名称', name:'BATCH', width:80, align:"center"},
			{header:'成本中心', name:'COST_CENTER', width:80, align:"center"},
			{header:'WBS', name:'WBS', width:80, align:"center"},
			{header:'接收工厂', name:'WERKS', width:80, align:"center"},
			{header:'接收库位', name:'LGORT', width:80, align:"center"},
			//{header:'操作日期', name:'PO_ITEM_NO', width:80, align:"center"},
			//{header:'委外供应商', name:'ASNNO', width:80, align:"center"},
			{header:'抬头文本', name:'HEADER_TXT', width:80, align:"center"},
			{header:'预留编号', name:'RSNUM', width:80, align:"center"},
			{header:'收货单号', name:'RECEIPT_NO', width:80, align:"center"},
			{header:'收货单行项目', name:'RECEIPT_ITEM_NO', width:80, align:"center"},
			{header:'SCM送货单号', name:'ASNNO', width:80, align:"center"},
			{header:'SCM送货单行项目', name:'ASNITM', width:80, align:"center"},
			{header:'采购订单号', name:'PO_NO', width:80, align:"center"},
			{header:'采购订单行项目', name:'PO_ITEM_NO', width:80, align:"center"},
			{header:'SAP交货单号', name:'SAP_OUT_NO', width:80, align:"center"},
			{header:'SAP交货单行项目号', name:'SAP_OUT_ITEM_NO', width:80, align:"center"},
			{header:'退货单号', name:'RETURN_NO', width:80, align:"center"},
			{header:'退货单行项目', name:'RETURN_ITEM_NO', width:80, align:"center"},*/
		],	
		viewrecords: true,
        shrinkToFit:false,
		//rowNum: 15,
		width:3100,
		height:'auto',


		pager: 'dataGridPage', //分页工具栏，pager:分页DIV的id
		rowNum:10, //每页显示记录数
		viewrecords: true, //是否显示行数
		rowList:[10,20,30], //可调整每页显示的记录数
		multiselect: false, //是否支持多选
		//caption: "信息显示"

	});
	//$("#dataGrid").jqGrid("setFrozenColumns");
	var hideCol=['RECEIPT_NO','RECEIPT_ITEM_NO','ASNNO','ASNITM','PO_NO','PO_ITEM_NO','SAP_OUT_NO',
		'SAP_OUT_ITEM_NO','RETURN_NO','RETURN_ITEM_NO'];
	DynamicColume(hideCol,"列选择","#dataGrid","#example-getting-started");
	$(window).resize(function(){  
	    $("#dataGrid").setGridWidth($(window).width());
	    $("#dataGrid").setGridHeight($(window).height());
	});
});
function export2Excel(){
	var column=[      
      	/*{"title":'工厂', "data":'F_WERKS', "class":"center"},
		{"title":'库位', "data":'F_LGORT', "class":"center"},*/
		{"title":'WMS凭证', "data":'WMS_SAP_MAT_DOC',"class":"center"},
		{"title":'项目号', "data":'WMS_ITEM_NO',"class":"center"},
		{"title":'订单号', "data":'PO_NO', "class":"center"},
		{"title":'移动类型', "data":'WMS_MOVE_TYPE', "class":"center"},
		{"title":'料号', "data":'MATNR', "class":"center"},
		{"title":'物料描述', "data":'MAKTX', "class":"center"},

		{"title":'条码号', "data":'', "class":"center"},
		{"title":'数量', "data":'QTY_WMS', "class":"center"},
		{"title":'单位', "data":'UNIT', "class":"center"},
		{"title":'库位', "data":'', "class":"center"},
		{"title":'供应商代码', "data":'LIFNR', "class":"center"},
		{"title":'品质状态', "data":'', "class":"center"},
		{"title":'BYD批次', "data":'BATCH', "class":"center"},
/*		{"title":'记账日期', "data":'JZ_DATE', "class":"center"},
		{"title":'交接人员', "data":'HANDOVER', "class":"center"},
		{"title":'WMS凭证', "data":'WMS_SAP_MAT_DOC',"class":"center"},
		{"title":'订单号', "data":'SO_NO', "class":"center"},
		{"title":'需求跟踪号', "data":'BEDNR',"class":"center"},
		{"title":'单据编号', "data":'TRY_QTY', "class":"center"},
		{"title":'参考凭证', "data":'REF_WMS_NO', "class":"center"},
		//{header:'供应商名称', name:'BATCH', width:80, align:"center"},
		{"title":'成本中心', "data":'COST_CENTER', "class":"center"},
		{"title":'WBS', "data":'WBS', "class":"center"},
		{"title":'接收工厂', "data":'WERKS',  "class":"center"},
		{"title":'接收库位', "data":'LGORT', "class":"center"},
		//{header:'操作日期', name:'PO_ITEM_NO', width:80, align:"center"},
		//{header:'委外供应商', name:'ASNNO', width:80, align:"center"},
		{"title":'抬头文本', "data":'HEADER_TXT', "class":"center"},
		{"title":'预留编号', "data":'RSNUM',"class":"center"},*/
      ]	;
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"query/docPackingQuery/list",
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) { 
		    results=response.page.list;
		}
	})
	var rowGroups=[];
	getMergeTable(results,column,rowGroups,"发料包装记录");
}
function getUrlKey(name){
	return decodeURIComponent((new RegExp('[?|&]'+name+'='+'([^&;]+?)(&|#|;|$)').exec(location.href)||[,""])[1].replace(/\+/g,'%20'))||null;
}