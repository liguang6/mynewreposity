
var vm = new Vue({
	el:'#rrapp',
	data:{
		asnno:''
	},
	methods: {
		getBarDetail : function(asnno) {
			console.log("bar+asnno:"+asnno);
			vm.asnno=asnno;
			// $("#ASNNO").val(asnno);
			this.$nextTick(function(){//渲染后再执行此函数
				initGridTable();
			});

		},
		/**
		 * 条码打印
		 */
		barPrint: function () {
			//获取选中表格数据
			var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");
			if(ids.length == 0){
				alert("当前还没有勾选任何行项目数据！");
				return false;
			}
			var saveData = [];
			// var labelType = null;
			// var WERKS = null;
			for (var i = 0; i < ids.length; i++) {
				var data=$("#dataGrid").jqGrid('getRowData',ids[i]);
				// if(WERKS == null){
				// 	WERKS = data.WERKS;
				// }
				saveData[i] = $("#dataGrid").jqGrid('getRowData',ids[i]);
			}
			if(saveData.length>0){
				$("#print").attr('action',baseUrl+"docPrint/labelLabelPreview");
				// $("#WERKS").val(WERKS);
				$("#labelList").val(JSON.stringify(saveData));
				$("#printButton").click();
			}

		},
		/**
		 * A4纸打印
		 */
		printA4: function () {
			//获取选中表格数据
			var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");
			if(ids.length == 0){
				alert("当前还没有勾选任何行项目数据！");
				return false;
			}
			var saveData = [];
			// var WERKS = null;
			// var labelType = null;
			for (var i = 0; i < ids.length; i++) {
				var data=$("#dataGrid").jqGrid('getRowData',ids[i]);
				// if(WERKS == null){
				// 	WERKS = data.WERKS;
				// }

				saveData[i] = $("#dataGrid").jqGrid('getRowData',ids[i]);
			}
			if(saveData.length>0){

				$("#printA4").attr('action',baseUrl+"docPrint/labelPreview_A4");
				// $("#WERKSA4").val(WERKS);
				$("#labelListA4").val(JSON.stringify(saveData));
				$("#printButtonA4").click();
			}
		},
	}
});

function initGridTable(){
	$("#dataGrid").dataGrid({
		searchForm:$("#searchForm"),
		datatype: "json",
		colModel:[
			{label:'标签号', name:'LABEL_NO', width:120, align:"center"},
			{label:'工厂', name:'WERKS', width:50, align:"center",sortable:false},
			{label:'批次', name:'BATCH', width:110, align:"center"},
			// {header:'仓库号', name:'WH_NUMBER', width:60, align:"center",sortable:false},
			{label:'供应商代码', name:'LIFNR', width:80, align:"center"},
			{label:'供应商描述', name:'LIKTX', width:200, align:"center"},
			{label:'料号', name:'MATNR', width:90, align:"center"},
			{label:'物料描述', name:'MAKTX', width:160, align:"center",sortable:false},
			{label:'数量', name:'BOX_QTY', width:60, align:"center",sortable:false},
			{label:'单位', name:'UNIT', width:50, align:"center",sortable:false},
			// {label:'库位', name:'LGORT', width:60, align:"center",sortable:false},
			// {label:'储位代码', name:'BIN_CODE', width:70, align:"center",sortable:false},
			{label:'需求跟踪号', name:'BEDNR', width:80, align:"center"},
			{label:'生产日期', name:'PRODUCT_DATE', width:90, align:"center",sortable:false},
			{label:'有效日期', name:'EFFECT_DATE', width:90, align:"center",sortable:false},
			{label:'PO_NO',name:'PO_NO',hidden:true},
			{label:'PO_ITEM_NO',name:'PO_ITEM_NO',hidden:true},
			{label:'REMARK',name:'REMARK',hidden:true}

		],
		rowNum: 15,
		rownumWidth: 25,
		autowidth:true,
		shrinkToFit:false,
		autoScroll: true,
		multiselect:true,
		beforeSelectRow: function (rowid, e) {  //点击复选框才选中行
			var $myGrid = $(this),
				i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),
				cm = $myGrid.jqGrid('getGridParam', 'colModel');
			/*if(cm[i].name == 'cb'){
                console.info(">>>");
                $('#dataGrid').jqGrid('setSelection',rowid);
            }*/

			return (cm[i].name == 'cb');
		}
	});
}
