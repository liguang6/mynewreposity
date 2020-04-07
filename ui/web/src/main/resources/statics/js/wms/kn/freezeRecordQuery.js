
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
		onPlantChange: function(event){
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
		}
	},
	
});
$(function () {
	$('#dataGrid').dataGrid({
		searchForm: $("#searchForm"),
		columnModel: [
			{header:'工厂', name:'werks', width:60, align:"center"},
			{header:'仓库号', name:'whNumber', width:60, align:"center"},
			{header:'料号', name:'matnr', width:120, align:"center"},
			{header:'物料描述', name:'maktx', width:200, align:"center"},
			{header:'库位', name:'lgort', width:70, align:"center"},
			{header:'储位', name:'binCode', width:70, align:"center"},
			{header:'批次', name:'batch', width:120, align:"center"},
			{header:'库存类型', name:'sobkz', width:80, align:"center"},
			{header:'操作类型', name:'freezeType', width:80, align:"center",formatter: function(item, index){
    		    return item=='00' ? '冻结' : '解冻';
		    	}
		    }, 	
			{header:'数量', name:'freezeQty', width:60, align:"center"},
			{header:'单位', name:'meins', width:60, align:"center"},
			{header:'供应商代码', name:'lifnr', width:90, align:"center"},
			{header:'供应商名称', name:'liktx', width:140, align:"center"},
			{header:'冻结原因', name:'reason', width:80, align:"center"},
			//{header:'储位名称',name:'binCode',hidden:true},
		],	
		rowNum: 15,  
	});
});