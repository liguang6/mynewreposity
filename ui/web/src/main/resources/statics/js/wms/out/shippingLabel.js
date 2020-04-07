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
	
	$.ajax({
		type: "POST",
		url: baseURL + "out/picking/saveShippingLabel",
		dataType : "json",
		async:false,
		data: {
			"params":JSON.stringify(saveData),
		},
		success: function(data){
			if(data.code == 0){
				if(saveData.length>0) {
					if(template_type === "A4"){
						$("#labelListA4").val(JSON.stringify(data.list));
						$("#printButtonA4").click();
					}else{
						$("#labelList").val(JSON.stringify(data.list));
						$("#printButton").click();
					}
				}
				js.showMessage('操作成功');
			}else{
				alert(data.msg);
			}
		}
	});

	if(ids.length != 0){
		js.layer.closeAll('iframe');
	}

};




function getInfo(params,whNumber,matnr){
	$("#REQUIREMENT_NO").val(params)
	$("#WH_NUMBER").val(whNumber)
	$("#MATNR").val(matnr)
}


function init(){
	$("#dataGrid").dataGrid({
		searchForm:$("#searchForm"),
		datatype: "json",
		columnModel: [
			{header: '工厂', name: 'WERKS', width: 55, align: "center"},
			{header: '仓库号', name: 'WH_NUMBER', width: 65, align: "center"},
			{header: '需求号', name: 'REQUIREMENT_NO', width: 110, align: "center"},
			{header: '行号', name: 'REQUIREMENT_ITEM_NO', width: 55, align: "center"},
			{header: '料号', name: 'MATNR', width: 100, align: "center"},
			{header: '物料描述', name: 'ZMAKTX', width: 150, align: "center"},
			{header: '供应商代码', name: 'LIFNR', width: 80, align: "center"},
			{header: '下架数量', name: 'CONFIRM_QUANTITY', width: 65, align: "center"},
			{header: '单位', name: 'UNIT', width: 60, align: "center"},
			{header: '仓库任务', name: 'TASK_NUM', width: 110, align: "center"},
			{header: '工位', name: 'POINT_OF_USE', width: 75, align: "center"},
			{header: '产线', name: 'LINE_NO', width: 75, align: "center"},
			{header: '分拣储位', name: 'POU', width: 80, align: "center"},
			{header: '批次', name: 'BATCH', width: 90, align: "center"},
			{header: '标准用量', name: 'DOSAGE', width: 90, align: "center"},
			{header: '配送单号', name: 'DISPATCHING_NO', width: 110, align: "center"},
			{header: '行项目', name: 'DISPATCHING_ITEM_NO', width: 55, align: "center"},
			{header: '上线类型', name: 'DELIVERY_TYPE', width: 90, align: "center"},
			{header: '上线洞口', name: 'LINE_FEEDING_ROUTE', width: 90, align: "center"},
			{header: '产品编号', name: 'END_MATERIAL_CODE', width: 90, align: "center"},
			{header: '备注', name: 'REMARK', width: 60, align: "center"},
			{label: 'QTY_XJ', name: 'QTY_XJ', index: 'QTY_XJ', hidden: true},
			{label: 'LIKTX', name: 'LIKTX', index: 'LIKTX', hidden: true},
			{label: 'REFERENCE_NO', name: 'REFERENCE_NO', index: 'REFERENCE_NO', hidden: true},
			{label: 'ID', name: 'ID', index: 'ID', hidden: true},
		],
		showCheckbox:true,
		// viewrecords: true,
		shrinkToFit:false,
		rowNum: 2147483647,
		rownumWidth: 25,
		height: '30px',
	});
}


