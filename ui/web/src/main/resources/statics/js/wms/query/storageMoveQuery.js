
var vm = new Vue({
	el:'#rrapp',
	data:{
		whNumber:"",
		warehourse:[]
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
		refresh : function() {
			$('#dataGrid').trigger('reloadGrid');
		},
		reload: function (event) {
			$("#searchForm").submit();
		},
		
		onPlantChange:function(){
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
		
		exp:function(){
			export2Excel();
		},
	}
});
$(function () {
	
	$('#dataGrid').dataGrid({
		searchForm: $("#searchForm"),
		columnModel: [
			{header:'工厂', name:'WERKS', width:60, align:"center"},
			{header:'仓库号', name:'WH_NUMBER', width:60, align:"center"},
			{header:'库位', name:'LGORT', width:70, align:"center"},
			{header:'料号', name:'MATNR', width:100, align:"center"},
			{header:'物料描述', name:'MAKTX', width:150, align:"center"},
			{header:'批次', name:'BATCH', width:100, align:"center"},
			{header:'单位', name:'MEINS', width:60, align:"center"},
			{header:'供应商代码', name:'LIFNR', width:90, align:"center"},
			{header:'供应商名称', name:'LIKTX', width:140, align:"center"},
			{header:'特殊库存类型', name:'SOBKZ', width:90, align:"center"},
			{header:'数量', name:'MOVE_QTY', width:60, align:"center"},
			{header:'源储位', name:'F_BIN_CODE', width:70, align:"center"},
			{header:'目标储位', name:'BIN_CODE', width:70, align:"center"},
			{header:'创建人', name:'EDITOR', width:70, align:"center"},
			{header:'创建时间', name:'EDIT_DATE', width:100, align:"center"},
		],	
		rowNum: 15,  
	});
});
function exportExcel(){
	var column=[
      	{"title":'工厂', "data":'WERKS', "class":"center"},
		{"title":'仓库号', "data":'WH_NUMBER', "class":"center"},
		{"title":'库位', "data":'LGORT', "class":"center"},
		{"title":'料号', "data":'MATNR', "class":"center"},
		{"title":'物料描述', "data":'MAKTX', "class":"center"},
		{"title":'批次', "data":'BATCH',"class":"center"},
		{"title":'单位', "data":'MEINS', "class":"center"},
		{"title":'供应商代码', "data":'LIFNR', "class":"center"},
		{"title":'供应商名称', "data":'LIKTX', "class":"center"},
		{"title":'特殊库存类型', "data":'SOBKZ', "class":"center"},
		{"title":'数量', "data":'MOVE_QTY',"class":"center"},
		{"title":'源储位', "data":'F_BIN_CODE',"class":"center"},
		{"title":'目标储位', "data":'BIN_CODE', "class":"center"},
		{"title":'创建人', "data":'EDITOR', "class":"center"},
		{"title":'创建时间', "data":'EDIT_DATE', "class":"center"},
      ]	;
	var results=[];
	$.ajax({
		url:baseURL+"query/knQuery/getStorageMoveList",
		dataType : "json",
		type : "post",
		data : {
			WERKS:$("#werks").val(),
			WH_NUMBER:$("#whNumber").val(),
			LOGRT:$('#lgort').val(),
			BATCH:$("#batch").val(),
			LIFNR:$('#lifnr').val(),
			MATNR:$("#matnr").val(),

		},
		async: false,
		success: function (response) {
			if(response.code==0){
				results=response.list;
			}
		}
	})
	var rowGroups=[];
	getMergeTable(results,column,rowGroups,"移储记录");	
}