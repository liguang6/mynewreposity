var mydata = [];
var lastrow,lastcell;
var vm = new Vue({
	el:'#rrapp',
	data:{
		whNumber:"",
		warehourse:[],
		relatedareaname:[],
	},
	created:function(){
		loadWhNumber();
	},
	methods: {
		reload: function (event) {
			$("#searchForm").submit();
		},
		onPlantChange:function(){
			loadWhNumber();
		},
		exp:function(){
			export2Excel();
		},
	}
});
$(function () {
	var now = new Date(); //当前日期
	var startDate=new Date(now.getTime()-6*24*3600*1000);
	$("#startDate").val(formatDate(startDate));
	$("#endDate").val(formatDate(now));
	showTable();
//	$("#btnQuery").click(function () {
//		$("#btnQuery").val("查询中...");
//		$("#btnQuery").attr("disabled","disabled");	
//		
//		$.ajax({
//			url:baseURL+"kn/inventory/getInventoryConfirm",
//			dataType : "json",
//			type : "post",
//			data : {
//				"WERKS":$("#werks").val(),
//				"WH_NUMBER":$("#whNumber").val(),
//				"INVENTORY_NO":$("#inventoryNo").val(),
//				"WH_MANAGER":$("#whManager").val(),
//				"START_DATE":$("#startDate").val(),
//				"END_DATE":$("#endDate").val()
//			},
//			async: true,
//			success: function (response) { 
//				$("#dataGrid").jqGrid("clearGridData", true);
//				mydata.length=0;
//				$("#btnQuery").removeAttr("disabled");
//				$("#btnQuery").val("查询");
//				if(response.code == "0"){
//					mydata = response.list;
//					$("#dataGrid").jqGrid('GridUnload');	
//					showTable();
//					if(mydata.length=="")alert("未查询到符合条件的数据！");
//				}else{
//					alert(response.msg);
//				}				
//			}
//		});
//	});	
});
function showTable(){
	$("#dataGrid").dataGrid({
//		datatype: "local",
//		data: mydata,
		searchForm: $("#searchForm"),
        colModel: [		
        	{label: '盘点任务号', name: 'INVENTORY_NO',index:"INVENTORY_NO", width: "120",align:"center",frozen: true},
        	{label: '行项目', name: 'INVENTORY_ITEM_NO',index:"INVENTORY_ITEM_NO", width: "55",align:"center",sortable:false,frozen: true},
            {label: '工厂', name: 'WERKS',index:"WERKS", width: "60",align:"center",sortable:false,frozen: true,},
            {label: '仓库号', name: 'WH_NUMBER',index:"WH_NUMBER", width: "60",align:"center",sortable:false,frozen: true},
            {label: '料号', name: 'MATNR',index:"MATNR", width: "100",align:"center",sortable:true,frozen: true},
            {label: '物料描述', name: 'MAKTX',index:"MAKTX", width: "150",align:"center",sortable:false,frozen: true},
            {label: '仓管员', name: 'WH_MANAGER',index:"WH_MANAGER", width: "70",align:"center",sortable:true},
            {label: '供应商代码', name: 'LIFNR',index:"LIFNR", width: "90",align:"center",sortable:false},
            {label: '供应商名称', name: 'LIKTX',index:"LIKTX", width: "130",align:"center",sortable:false},
            {label: '库位', name: 'LGORT',index:"LGORT", width: "50",align:"center",sortable:false},
            {label: '单位', name: 'MEINS',index:"MEINS", width: "50",align:"center",sortable:false},
            {label: '账面数量', name: 'STOCK_QTY',index:"STOCK_QTY", width: "70",align:"center",sortable:false},
        	{label: '复盘数量', name: 'INVENTORY_QTY_REPEAT',index:"INVENTORY_QTY_REPEAT", width: "70",align:"center",sortable:false},
            {label: '差异数量', name: 'DIF_QTY',index:"DIF_QTY", width: "80",align:"center",sortable:false},
            {label: '差异原因', name: 'DIFFERENCE_REASON',index:"DIFFERENCE_REASON", width: "100",align:"center",sortable:false},
            {label: '确认人', name: 'CONFIRMOR',index:"CONFIRMOR", width: "80",align:"center",sortable:false},         
        ],
		shrinkToFit: false,
        rownumbers:false,
        rowNum: 15, 
    });
	$("#dataGrid").jqGrid("setFrozenColumns");
}
function loadWhNumber(){
	//初始化查询仓库
	var plantCode = $("#werks").val();
    //初始化仓库
    $.ajax({
      url:baseURL+"common/getWhDataByWerks",
  	  data:{"WERKS": plantCode},
  	  success:function(resp){
  		 vm.warehourse = resp.data;
  		 vm.whNumber=vm.warehourse[0].WH_NUMBER;
  	  }
    });
}
function export2Excel(){
	var column=[      
		{"title": '盘点任务号', "data": 'INVENTORY_NO',"class":"center"},
    	{"title": '行项目', "data": 'INVENTORY_ITEM_NO',"class":"center"},
        {"title": '工厂', "data": 'WERKS',"class":"center"},
        {"title": '仓库号', "data": 'WH_NUMBER',"class":"center"},
        {"title": '料号', "data": 'MATNR',"class":"center"},
        {"title": '物料描述', "data": 'MAKTX',"class":"center"},
        {"title": '仓管员', "data": 'WH_MANAGER',"class":"center"},
        {"title": '供应商代码', "data": 'LIFNR',"class":"center"},
        {"title": '供应商名称', "data": 'LIKTX',"class":"center"},
        {"title": '库位', "data": 'LGORT',"class":"center"},
        {"title": '单位', "data": 'MEINS',"class":"center"},
        {"title": '账面数量', "data": 'STOCK_QTY',"class":"center"},
    	{"title": '复盘数量', "data": 'INVENTORY_QTY_REPEAT',"class":"center"},
        {"title": '差异数量', "data": 'DIF_QTY',"class":"center"},
        {"title": '差异原因', "data": 'DIFFERENCE_REASON',"class":"center"},
        {"title": '确认人', "data": 'CONFIRMOR',"class":"center"},         
      ]	;
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"query/knQuery/getInventoryList",
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) { 
		    results=response.page.list;
		}
	});
	var rowGroups=[];
	getMergeTable(results,column,rowGroups,"盘点记录");	
}
