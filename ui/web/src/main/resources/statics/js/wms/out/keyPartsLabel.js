var vm = new Vue({
    el:'#rrapp',
    data:{
		label:{},
    	saveFlag:false
    },
    methods:{
		print: function () {
			//获取选中表格数据
			//var ids = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
			var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");
			if(ids.length == 0){
				alert("当前还没有勾选任何行项目数据！");
				return false;
			}
			var saveData = [];
			for (var i = 0; i < ids.length; i++) {
				var data=$("#dataGrid").jqGrid('getRowData',ids[i]);
				saveData[i] = $("#dataGrid").jqGrid('getRowData',ids[i]);
			}
			if(saveData.length>0)
				$("#labelList").val(JSON.stringify(saveData));
			$("#printButton").click();
		},
		printA4: function () {
			//获取选中表格数据
			//var ids = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
			var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");
			if(ids.length == 0){
				alert("当前还没有勾选任何行项目数据！");
				return false;
			}
			var saveData = [];
			for (var i = 0; i < ids.length; i++) {
				var data=$("#dataGrid").jqGrid('getRowData',ids[i]);
				saveData[i] = $("#dataGrid").jqGrid('getRowData',ids[i]);
			}
			if(saveData.length>0)
				$("#labelListA4").val(JSON.stringify(saveData));
			$("#printButtonA4").click();
		},


    }
});



function printpage(template_type){
	
	var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");
	if(ids.length == 0){
		layer.msg("请勾选行项目！",{time:1500});
		return false;
	}
	
	var saveData = [];
	for (var i = 0; i < ids.length; i++) {
		var data=$("#dataGrid").jqGrid('getRowData',ids[i]);
		saveData.push(data);
	}
	
	if(template_type === "A4"){
		$("#labelListA4").val(JSON.stringify(saveData));
		$("#printButtonA4").click();
	}else{
		$("#labelList").val(JSON.stringify(saveData));
		$("#printButton").click();
	}

	if(ids.length != 0){
		js.layer.closeAll('iframe');
	}

};




function getInfo(params,whNumber){
	$("#REQUIREMENT_NO").val(params)
	$("#WH_NUMBER").val(whNumber)
}


function init(){
	$("#dataGrid").dataGrid({
		searchForm:$("#searchForm"),
		datatype: "json",
		columnModel: [
			{header: '工厂', name: 'WERKS', width: 55, align: "center"},
			{header: '仓库号', name: 'WH_NUMBER', width: 65, align: "center"},
			{header: '料号', name: 'MATNR', width: 100, align: "center"},
			{header: '物料描述', name: 'MAKTX', width: 200, align: "center"},
			{header: '数量', name: 'QTY', width: 65, align: "center"},
			{header: '下架数量', name: 'XJ', width: 65, align: "center"},
			{header: '单位', name: 'UNIT', width: 55, align: "center"},
			{header: '批次', name: 'BATCH', width: 95, align: "center"},
			{header: '供应商代码', name: 'LIFNR', width: 80, align: "center"},
			{header: '供应商名称', name: 'LIKTX', width: 163, align: "center"},
			{label: 'ID', name: 'ID', index: 'ID', hidden: true},
		],
		showCheckbox:true,
		// viewrecords: true,
		shrinkToFit:false,
		rowNum: 15,
		rownumWidth: 25,
		height: '30px',
	});
	
}