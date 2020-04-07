
var vm = new Vue({
	el:'#rrapp',
	data:{
		showList:false,
	},
	created:function(){
		$.ajax({
			url:baseURL+"common/getWhDataByWerks",
			dataType : "json",
			type : "post",
			data : {
				"WERKS":$("#werks").val()
			},
			async: true,
			success: function (response) { 
				$("#wh").empty();
				var strs = "";
			    $.each(response.data, function(index, value) {
			    	strs += "<option value=" + value.WH_NUMBER + ">" + value.WH_NUMBER + "</option>";
			    });
			    $("#wh").append(strs);
			}
		});
	},
	methods: {
		query: function () {
			$("#searchForm").submit();
		},
		exp:function(){
			export2Excel();
		},
		onPlantChange:function(event){
			  //工厂变化的时候，更新库存下拉框
	          var werks = event.target.value;
	          $.ajax({
	  			url:baseURL+"common/getWhDataByWerks",
	  			dataType : "json",
	  			type : "post",
	  			data : {
	  				"WERKS":werks
	  			},
	  			async: true,
	  			success: function (response) { 
	  				$("#wh").empty();
	  				var strs = "";
	  			    $.each(response.data, function(index, value) {
	  			    	strs += "<option value=" + value.WH_NUMBER + ">" + value.WH_NUMBER + "</option>";
	  			    });
	  			    $("#wh").append(strs);
	  			}
	  		});
	       },
	}
});
$(function () {
	$('#dataGrid').dataGrid({
		searchForm: $("#searchForm"),
		columnModel: [
			{header:'工厂', name:'WERKS', width:60, align:"center"},
			{header:'仓库号', name:'WH_NUMBER', width:60, align:"center"},
			{header:'料号', name:'MATNR', width:120, align:"center"},
			{header:'物料描述', name:'MAKTX', width:200, align:"center"},
			{header:'库位', name:'LGORT', width:70, align:"center"},
			{header:'储位', name:'BIN_CODE', width:70, align:"center"},
			{header:'批次', name:'BATCH', width:120, align:"center"},
			{header:'库存类型', name:'SOBKZ', width:80, align:"center"},
			{header:'操作类型', name:'FREEZE_TYPE', width:80, align:"center"}, 	
			{header:'数量', name:'FREEZE_QTY', width:60, align:"center"},
			{header:'单位', name:'MEINS', width:60, align:"center"},
			{header:'供应商代码', name:'LIFNR', width:90, align:"center"},
			{header:'供应商名称', name:'LIKTX', width:140, align:"center"},
			{header:'冻结原因', name:'REASON', width:80, align:"center"},
			//{header:'储位名称',name:'binCode',hidden:true},
		],	
		rowNum: 15,  
	});
});
function export2Excel(){
	var column=[      
        {"title":'工厂', "data":'WERKS',"class":"center"},
		{"title":'仓库号', "data":'WH_NUMBER', "class":"center"},
		{"title":'料号', "data":'MATNR', "class":"center"},
		{"title":'物料描述', "data":'MAKTX', "class":"center"},
		{"title":'库位', "data":'LGORT', "class":"center"},
		{"title":'储位', "data":'BIN_CODE', "class":"center"},
		{"title":'批次', "data":'BATCH', "class":"center"},
		{"title":'库存类型', "data":'SOBKZ', "class":"center"},
		{"title":'操作类型', "data":'FREEZE_TYPE', "class":"center"}, 	
		{"title":'数量', "data":'FREEZE_QTY', "class":"center"},
		{"title":'单位', "data":'MEINS',"class":"center"},
		{"title":'供应商代码', "data":'LIFNR', "class":"center"},
		{"title":'供应商名称', "data":'LIKTX', "class":"center"},
		{"title":'冻结原因', "data":'REASON', "class":"center"},
      ]	;
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"query/knQuery/getFreezeRecordList",
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) { 
		    results=response.page.list;
		}
	});
	var rowGroups=[];
	getMergeTable(results,column,rowGroups,"冻结记录");	
}
