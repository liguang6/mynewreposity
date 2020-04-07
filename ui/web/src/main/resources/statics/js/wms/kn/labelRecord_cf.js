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



function printpage(){
	var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");
	if(ids.length == 0 || ids.length!=2){
		alert("请勾选全部行数据！");
		return false;
	}
	var saveData = [];
	for (var i = 0; i < ids.length; i++) {
		var data=$("#dataGrid").jqGrid('getRowData',ids[i]);
		saveData[i] = $("#dataGrid").jqGrid('getRowData',ids[i]);
	}
	console.info(saveData[0].BOX_QTY);
	console.info(saveData[1].BOX_QTY);
	if(saveData[0].BOX_QTY<0){
		alert("拆分数量不能大于原始条码数量");
		return false;
	}
	$.ajax({
		type: "POST",
		url: baseURL + "kn/labelRecord/saveByCf",
		//contentType: "application/json",
		dataType : "json",
		async:false,
		data: {
			"params":JSON.stringify(saveData),
		},
		success: function(data){
			if(data.code == 0){
				if(saveData.length>0)
					$("#labelList").val(JSON.stringify(saveData));
				$("#printButton").click();
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




function getInfo(params,numNo){
	$("#params").val(params);
	$("#numNo").val(numNo);
}


function init(){
	$("#dataGrid").dataGrid({
		searchForm:$("#searchForm"),
		datatype: "json",
		columnModel: [
			{header: '工厂', name: 'WERKS', width: 60, align: "center"},
			{header: '条码号', name: 'LABEL_NO', width: 70, align: "center"},

			{header: '订单号', name: 'PO_NO', width: 70, align: "center"},
			{header: '行项目号', name: 'PO_ITEM_NO', width: 90, align: "center"},

			{header: '料号', name: 'MATNR', width: 100, align: "center"},
			{header: '物料描述', name: 'MAKTX', width: 150, align: "center"},
			{header: '供应商代码', name: 'LIFNR', width: 100, align: "center"},
			{header: '数量', name: 'BOX_QTY', width: 60, align: "center"},
			/*{
				header: '拆分数量', name: 'CF_BOX_QTY', width: 90, align: "center", sortable: false,
				formatter: function (val) {
					return "<input  id = 'test ' type='text' style='width:98%'/>";
				}

			},*/

			{header: '单位', name: 'UNIT', width: 60, align: "center"},
			{header: '生产日期', name: 'PRODUCT_DATE', width: 100, align: "center"},

			{header: '生产批次', name: 'F_BATCH', width: 100, align: "center"},

			{header: 'BYD生产批次', name: 'BATCH', width: 110, align: "center"},
			{header: '备注', name: 'REMARK', width: 70, align: "center"},
			{label: 'ID', name: 'ID', index: 'ID', hidden: true},

			{label: '收货单号', name: 'RECEIPT_NO', index: 'RECEIPT_NO', hidden: true},
			{label: '收货单行项目号', name: 'RECEIPT_ITEM_NO', index: 'RECEIPT_ITEM_NO', hidden: true},
			{label: '进仓单编号', name: 'INBOUND_NO', index: 'INBOUND_NO', hidden: true},
			{label: '进仓单行项目号', name: 'INBOUND_ITEM_NO', index: 'INBOUND_ITEM_NO', hidden: true},
			{label: 'WMS凭证号', name: 'WMS_NO', index: 'WMS_NO', hidden: true},
			{label: 'WMS凭证行项目', name: 'WMS_ITEM_NO', index: 'WMS_ITEM_NO', hidden: true},
			{label: '源标签号', name: 'F_LABEL_NO', index: 'F_LABEL_NO', hidden: true},
			{label: '标签状态', name: 'LABEL_STATUS', index: 'LABEL_STATUS', hidden: true},
			{label: '箱序号', name: 'BOX_SN', index: 'BOX_SN', hidden: true},
			{label: '满箱数量（定额数量）', name: 'FULL_BOX_QTY', index: 'FULL_BOX_QTY', hidden: true},
			{label: '尾箱标识', name: 'END_FLAG', index: 'END_FLAG', hidden: true},
			{label: '删除标识', name: 'DEL', index: 'DEL', hidden: true},
			{label: '仓库号（进仓）', name: 'WH_NUMBER', index: 'WH_NUMBER'},
			{label: '库位（进仓）', name: 'LGORT', index: 'LGORT', hidden: true},
			{label: '储位（进仓）', name: 'BIN_CODE', index: 'BIN_CODE', hidden: true},
			{label: '源单位', name: 'F_UNIT', index: 'F_UNIT', hidden: true},
			{label: '特殊库存类型', name: 'SOBKZ', index: 'SOBKZ', hidden: true},
			{label: '供应商描述', name: 'LIKTX', index: 'LIKTX', hidden: true},
			{label: '质检结果', name: 'QC_RESULT_CODE', index: 'QC_RESULT_CODE', hidden: true},
			{label: '有效期', name: 'F_UEFFECT_DATENIT', index: 'F_UEFFECT_DATENIT', hidden: true},


		],
		showCheckbox:true,
		// viewrecords: true,
		rowNum: 15,
		rownumWidth: 25,
		height: '30px',
	});
}


