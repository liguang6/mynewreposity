var vm = new Vue({
	el : "#vue-app",
	data : {
		warehourse : [],// 仓库列表
		data : {
			REQUIREMENT_TYPE : null,
			WH_NUMBER : "",
			REQUIREMENT_TYPE : "",
			//REQUIREMENT_STATUS : REQUIREMENT_STATUS,
			REQUIREMENT_NO : REQUIREMENT_NO,
			WERK : WERKS,
			WHNUM : WH_NUMBER,
			REQUIREMENT_HEAD_STATUS:REQUIREMENT_HEAD_STATUS,
		},// 查询条件
		more_query_params : true,// 查询条件显示
	},
	created : function() {
		// 1. 初始化仓库工厂
		var werks = $("#werks").val();
		this.data.WERKS = werks;
		$.ajax({
			url : baseUrl + "common/getWhDataByWerks",
			data : {
				"WERKS" : werks,
				"MENU_KEY":"A91"
			},
			success : function(resp) {
				vm.warehourse = resp.data;
			}
		});

		var list=[];
		$.ajax({
			url: baseUrl + "common/getBusinessList",
			dataType: "json",
			type: "post",
			async: false,
			data: {
				business_class: "07",
				MENU_KEY: "A91",
				WERKS: werks
			},
			success: function (response) {
				list = response.data;
			}
		});
		this.businessList = list;
		if(undefined == this.businessList || this.businessList.length<=0){
			js.showErrorMessage("工厂未配置需求业务类型！");
		}else{
			this.requireTypes=this.businessList[0].CODE;
		}

		// 2.初始化表格
		// 动态表格列
		var defaultHideColums = ['HX_FLAG','LGORT','RECEIVE_LGORT','BOX_COUNT','SORT_SEQ','LIFNR','STATION','PO_NO','PO_LINE_NO','MO_NO','MO_ITEM_NO','RSNUM','RSPOS','SO_NO','SO_ITEM_NO','SAP_OUT_NO','SAP_OUT_ITEM_NO','COST_CENTER','IO_NO','WBS','CUSTOMER','SAKTO','SOBKZ','CREATOR','CREATE_DATE'];
		$(function() {
			vm.initTable();
			DynamicColume(defaultHideColums,"列显示","#jqGrid");
		});


	},
	methods : {
		initTable:function() {
			$("#jqGrid").dataGrid({
				searchForm : $("#searchForm"),
				url : baseURL + 'out/requirement/item/',
				datatype : "json",
				colModel : [ {
					label : '需求号',
					name : 'REQUIREMENT_NO',
					width : 120,
					align : 'center'
				},
				{
					label : '需求行项目号',
					name : 'REQUIREMENT_ITEM_NO',
					width : 100,
					align : 'center'
				},
				{
					label : '物料号',
					name : 'MATNR',
					width : 100,
					align : 'center'
				},
				{
					label : '物料描述',
					name : 'MAKTX',
					width : 100,
					align : 'center'
				},
				{
					label : '单位',
					name : 'UNIT',
					width : 100,
					align : 'center'
				},{
					label : '需求数量',
					name : 'QTY',
					width : 100,
					align : 'center'
				},
				{
					label : '下架数量',
					name : 'QTY_XJ',
					width : 100,
					align : 'center'
				},
				{
					label : '实领/发数量（交接）',
					name : 'QTY_REAL',
					width : 150,
					align : 'center'
				},{
					label : '冲销/取消数量',
					name : 'QTY_CANCEL',
					width : 140,
					align : 'center'
				},{
					label : 'WMS业务类型名称',
					name : 'BUSINESS_NAME_VALUE',
					width : 120,
					align : 'center'
				},{
					label : 'WMS业务类型代码',
					name : 'BUSINESS_TYPE_VALUE',
					width : 120,
					align : 'center'
				},{
					label : '行项目状态',
					name : 'REQ_ITEM_STATUS',
					align : 'center',
					formatter : function(v){
						if(v == '00'){
							return '已创建'
						}
						if(v == '01') {
							return '备料中'
						}
						if(v == '02') {
							return '部分下架'
						}
						if(v == '03') {
							return '已下架'
						}
						if(v == '04') {
							return '部分交接'
						}
						if(v == '05') {
							return '已交接'
						}
						if(v == '06') {
							return '关闭'
						}
						if(v == 'X') {
							return '已删除'
						}
						return '';
					},
					unformat : function(v){
						return v;
					}
				},
				{
					label : '核销标识',
					name : 'HX_FLAG',
					width : 100,
					align : 'center'
				},
				{
					label : '库位',
					name : 'LGORT',
					width : 100,
					align : 'center'
				},
				{
					label : '接收库位',
					name : 'RECEIVE_LGORT',
					width : 100,
					align : 'center'
				},
				{
					label : '预计箱数',
					name : 'BOX_COUNT',
					width : 100,
					align : 'center'
				},
				{
					label : '排序字符串',
					name : 'SORT_SEQ',
					width : 100,
					align : 'center'
				},
				{
					label : '供应商',
					name : 'LIFNR',
					align : 'center'
				},{
					label : '工位',
					name : 'STATION',
					align : 'center'
				},{
					label : '采购订单号',
					name : "PO_NO",
					align : 'center'
				},{
					label : '采购订单行项目号',
					name : 'PO_LINE_NO',
					align : 'center'
				},{
					label : '生产订单号',
					name : 'MO_NO',
					align : 'center'
				},{
					label : '生产订单行项目号',
					name : 'MO_ITEM_NO',
					align : 'center'
				},
				{
					label : '预留号',
					name : 'RSNUM',
					align : 'center'
				},
				{
					label : '预留行项目号',
					name : 'RSPOS',
					align : 'center'
				},{
					label : '销售订单号',
					name : 'SO_NO',
					align : 'center'
				},
				{
					label : '销售订单行项目号',
					name : 'SO_ITEM_NO',
					align : 'center'
				},
				{
					label : 'SAP交货单号',
					name : 'SAP_OUT_NO',
					align : 'center'
				},
				{
					label : 'SAP交货单行项目号',
					name : 'SAP_OUT_ITEM_NO',
					align : 'center'
				},
				{
					label : '成本中心',
					name : 'COST_CENTER',
					align : 'center'
				},
				{
					label : '研发/内部订单号',
					name : 'IO_NO',
					align : 'center'
				},
				{
					label : 'WBS元素号',
					name : 'WBS',
					align : 'center'
				},
				{
					label : '客户',
					name : 'CUSTOMER',
					align : 'center'
				},
				{
					label : '总账科目编号',
					name : 'SAKTO',
					align : 'center'
				},
				{
					label : '特殊库存标识',
					name : 'SOBKZ',
					align : 'center'
				},
				{
					label : '记录创建人',
					name : 'CREATOR',
					align : 'center'
				},
				{
					label : '记录创建时间',
					name : 'CREATE_DATE',
					align : 'center'
				},
				{
					label : '记录修改人',
					name : 'EDITOR',
					align : 'center'
				},
				{
					label : '记录修改时间',
					name : 'EDIT_DATE',
					align : 'center'
				},
					{label: '', name: 'BUSINESS_NAME', index: 'BUSINESS_NAME', hidden: true},
					{label: '', name: 'WERKS', index: 'WERKS', hidden: true},
					{label: '', name: 'WH_NUMBER', index: 'WH_NUMBER', hidden: true},
					{label: '', name: 'BUKRS_NAME', index: 'BUKRS_NAME', hidden: true},
					{label: '', name: 'LIFNR', index: 'LIFNR', hidden: true},
					{label: '', name: 'LIKTX', index: 'LIKTX', hidden: true},
					{label: '', name: 'QTY', index: 'QTY', hidden: true},
					{label: '', name: 'QTY_REAL', index: 'QTY_REAL', hidden: true},
					{label: '', name: 'ROWNUM', index: 'ROWNUM', hidden: true},
					{label: '', name: 'BATCH', index: 'BATCH', hidden: true},
					{label: '', name: 'SAP_MATDOC_NO', index: 'SAP_MATDOC_NO', hidden: true},
					{label: '', name: 'COST_CENTER', index: 'COST_CENTER', hidden: true},
					{label: '', name: 'WBS', index: 'WBS', hidden: true},
					{label: '', name: 'PO_NO', index: 'PO_NO', hidden: true},
					{label: '', name: 'MO_NO', index: 'MO_NO', hidden: true},
					{label: '', name: 'SO_NO', index: 'SO_NO', hidden: true},
					{label: '', name: 'IO_NO', index: 'IO_NO', hidden: true},
					{label: '', name: 'MEMO', index: 'MEMO', hidden: true},
					{label: '', name: 'RECEIVE_LGORT', index: 'RECEIVE_LGORT', hidden: true},
					{label: '', name: 'RECEIVE_WERKS', index: 'RECEIVE_WERKS', hidden: true},
					{label: '', name: 'CUSTOMER', index: 'CUSTOMER', hidden: true},
					{label: '', name: 'PURPOSE', index: 'PURPOSE', hidden: true},
					{label: '', name: 'RECEIVER', index: 'RECEIVER', hidden: true},
					//一步联动发货单抬头表配置bug2019
					{label: '', name: 'COMPANY_CODE', index: 'COMPANY_CODE', hidden: true},
					{label: '', name: 'COMPANY_NAME', index: 'COMPANY_NAME', hidden: true},
					{label: '', name: 'COMPANY_ENGLISH', index: 'COMPANY_ENGLISH', hidden: true},
					{label: '', name: 'TEL', index: 'TEL', hidden: true},
					{label: '', name: 'FAX', index: 'FAX', hidden: true},
					{label: '', name: 'ZIP_CODE', index: 'ZIP_CODE', hidden: true},
					{label: '', name: 'ADRESS', index: 'ADRESS', hidden: true},
					{label: '', name: 'ADRESS_ENGLISH', index: 'ADRESS_ENGLISH', hidden: true},
					//一步联动调入工厂、调出工厂信息可配置表
					{label: '', name: 'CONSIGNOR_TEL', index: 'CONSIGNOR_TEL', hidden: true},
					{label: '', name: 'CONSIGNOR', index: 'CONSIGNOR', hidden: true},
					{label: '', name: 'CONSIGNOR_ADRESS', index: 'CONSIGNOR_ADRESS', hidden: true},
					{label: '', name: 'CONSIGNEE_TEL', index: 'CONSIGNEE_TEL', hidden: true},
					{label: '', name: 'CONSIGNEE', index: 'CONSIGNEE', hidden: true},
					{label: '', name: 'CONSIGNEE_ADRESS', index: 'CONSIGNEE_ADRESS', hidden: true},
				],
				viewrecords : true,
				/* height: "100%", */
				rowNum : 2147483647,
				pageSize:2147483647,
				rownumbers : false,
				rownumWidth : 25,
				autowidth : true,
				multiselect : true,
				// 横向滚动条
				shrinkToFit : false,
				autoScroll : true
			});
		},
		onPlantChange : function(event) {
			console.info("baseUrl   "+baseUrl);
			console.info("baseURL   "+baseURL);
			// 工厂变化的时候，更新仓库号下拉框
			var plantCode = event.target.value;
			if (plantCode === null || plantCode === ''
					|| plantCode === undefined) {
				return;
			}
			this.data.WERKS = plantCode;
			// 查询工厂仓库
			$.ajax({
				url : baseUrl + "common/getWhDataByWerks",
				data : {
					"WERKS" : plantCode,
					"MENU_KEY":"A91"
				},
				success : function(resp) {
					vm.warehourse = resp.data;
					vm.data.WH_NUMBER = resp.data[0].WH_NUMBER;
				}
			});
			var list=[];
			$.ajax({
				url: baseUrl + "common/getBusinessList",
				dataType: "json",
				type: "post",
				async: false,
				data: {
					business_class: "07",
					MENU_KEY: "A91",
					WERKS: plantCode
				},
				success: function (response) {
					list = response.data;
				}
			});
			this.businessList = list;
			if(undefined == this.businessList || this.businessList.length<=0){
				js.showErrorMessage("工厂未配置需求业务类型！");
			}else{
				this.requireTypes=this.businessList[0].CODE;
			}
		},
		query : function() {
			$("#jqGrid").jqGrid('GridUnload');
			vm.initTable()
		},
		//关闭/删除(只有已创建才能删除)
		deleteItem : function(){
			//从jqgrid获取选中的数据
			var rows = $("#jqGrid").jqGrid('getGridParam','selarrrow');
			//var rows = vm.getGridSelectedItems();
			if(rows.length === 0){
				js.showMessage("请选择需要操作的行");
				return;
			}
			var rowList = [];
			for(var rowId of rows){
				var rowData = $("#jqGrid").getRowData(rowId);
				/*if(rowData.REQ_ITEM_STATUS === '已交接'){
					js.showMessage("该单据已交接")
					return ;
				}
				if(rowData.REQ_ITEM_STATUS === '关闭'){
					js.showMessage("该单据已被关闭/删除!")
					return;
				}*/
				rowList.push({"REQUIREMENT_ITEM_NO":rowData.REQUIREMENT_ITEM_NO,
					"REQUIREMENT_NO":rowData.REQUIREMENT_NO,
				"REQUIREMENT_HEAD_STATUS":REQUIREMENT_HEAD_STATUS});
			}
			//调用后台接口，更新
			$.ajax({
				url : baseUrl + "out/requirement/close",
				method : 'post',
				data:{
					"params":JSON.stringify(rowList),
				},
				success : function(r){
					if(r.code != '0'){
						js.showErrorMessage(r.msg);
					}else {
						js.showMessage("关闭/删除成功")
						vm.query();
					}
				}
			})
		},
		getGridSelectedItems:function(all){
			//获取选中的数据
			var s  = $("#jqGrid").jqGrid('getGridParam','selarrrow');
			if(all){
				s = $("#jqGrid").getDataIDs();
			}
			var items = [];
			//获取行数据
			for(var i=0;i<s.length;i++){
				var rowData = $("#jqGrid").jqGrid('getRowData',s[i]);
				items[i] = rowData;
			}
			return items;
		},
		print1 : function() {
			//小letter打印
			//TODO
			//window.open(baseURL+"docPrint/wareHouseOutPrint?PageSize=0&outNo=" + document.getElementById("outNo").innerHTML);
			//获取选中表格数据
			//var ids = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
			var ids = $("#jqGrid").jqGrid("getGridParam", "selarrrow");
			if(ids.length == 0){
				alert("当前还没有勾选任何行项目数据！");
				return false;
			}
			var saveData = [];
			for (var i = 0; i < ids.length; i++) {
				var data=$("#jqGrid").jqGrid('getRowData',ids[i]);
				saveData[i] = $("#jqGrid").jqGrid('getRowData',ids[i]);
			}
			if(saveData.length>0)
				$("#smallLabelList").val(JSON.stringify(saveData));
			$("#smallPrintButton").click();
		},
		print2 : function() {
			//大letter打印
			//TODO
			var ids = $("#jqGrid").jqGrid("getGridParam", "selarrrow");
			if(ids.length == 0){
				alert("当前还没有勾选任何行项目数据！");
				return false;
			}
			var saveData = [];
			for (var i = 0; i < ids.length; i++) {
				var data=$("#jqGrid").jqGrid('getRowData',ids[i]);
				saveData[i] = $("#jqGrid").jqGrid('getRowData',ids[i]);
			}
			if(saveData.length>0)
				$("#bigLabelList").val(JSON.stringify(saveData));
			$("#bigPrintButton").click();
		},
		exp: function () {
			export2Excel();
		}
	}
})
function export2Excel(){
	var column= [
		{title:'需求号', data:'REQUIREMENT_NO'},
		{title:'需求行', data:'REQUIREMENT_ITEM_NO'},
		{title:'料号', data:'MATNR'},
		{title:'物料描述', data:'MAKTX'},
		{title:'单位', data:'UNIT'},
		{title:'需求数量', data:'QTY'},
		{title:'下架数量', data:'QTY_XJ'},
		{title:'交接数量', data:'QTY_REAL'},
		{title:'冲销数量', data:'QTY_CANCEL'},
		{title:'WMS业务类型', data:'BUSINESS_NAME'},
		{title:'状态', data:'REQ_ITEM_STATUS'},
		{title:'库位', data:'LGORT'},
		{title:'接收库位', data:'RECEIVE_LGORT'},
		{title:'预计箱数', data:'BOX_COUNT'},
		{title:'供应商', data:'LIFNR'},
		{title:'工位', data:'STATION'},
		{title:'采购订单', data:'PO_NO'},
		{title:'采购订单行', data:'PO_ITEM_NO'},
		{title:'生产订单', data:'MO_NO'},
		{title:'生产订单行', data:'MO_ITEM_NO'},
		{title:'预留号', data:'RSNUM'},
		{title:'预留行号', data:'RSPOS'},
		{title:'销售订单', data:'SO_NO'},
		{title:'销售订单行', data:'SO_ITEM_NO'},
		{title:'SAP交货单行号', data:'SAP_MATDOC_NO'},
		{title:'SAP交货单行号', data:'SAP_MATDOC_ITEM_NO'},
		{title:'成本中心', data:'COST_CENTER'},
		{title:'研发/内部订单号', data:'IO_NO'},
		{title:'WBS元素号', data:'WBS'},
		{title:'客户', data:'CUSTOMER'},
		{title:'总账科目编号', data:'SAKTO'},
		{title:'特殊库存标识', data:'SOBKZ'},

	]
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i");
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"out/requirement/items",
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) {
		    results=response.page.list;
		    console.log("results",results);
		}
	});
	var rowGroups=[];
	getMergeTable1(results,column,rowGroups,"需求明细");
}
function getMergeTable1(result,columns,rowsGroup,filename){
	/**
	 * 创建table
	 */
	var table=document.getElementById("tb_excel");

	/**
	 * 创建table head
	 */
	var table_head=$("<tr />");
	$.each(columns,function(i,column){
		var th=$("<th />");
		th.attr("class",column.class);
		th.attr("width",column.width);
		th.html(column.title);
		$(table_head).append(th);
	})

	$(table).append($(table_head));

	var warp = document.createDocumentFragment();// 创建文档碎片节点,最后渲染该碎片节点，减少浏览器渲染消耗的资源

	var data_process={};
	data_process.result=result;
	data_process.columns=columns;
	data_process.rowsGroup=rowsGroup;


    var curWwwPath=window.document.location.href;
    // 获取主机地址之后的目录，如： uimcardprj/share/meun.jsp
    var pathName=window.document.location.pathname;
    var pos=curWwwPath.indexOf(pathName);
    // 获取主机地址，如： http://localhost:8083
    var localhostPath=curWwwPath.substring(0,pos);
    // 获取带"/"的项目名，如：/uimcardprj
    var projectName=pathName.substring(0,pathName.substr(1).indexOf('/')+1);
    var baseRoot = localhostPath+projectName;
	//alert(localhostPath);

	var worker=new Worker(baseRoot+"/statics/js/mergeTableCell.js")
	worker.postMessage(data_process);

	worker.onmessage=function(event){

	var trs_data=event.data;
	$.each(trs_data,function(i,tr_obj){
		var tr=$("<tr />");
		$.each(tr_obj,function(j,td_obj){
			var td=$("<td />");
			td.attr("class",td_obj.class);
			td.attr("id",td_obj.id);
			td.attr("rowspan",td_obj.rowspan);
			td.attr("width",td_obj.width);
			if (j ===2) {
				td.html("&nbsp;"+td_obj.html);
			}else{
				td.html(td_obj.html);
			}


			if(!td_obj.hidden){
				$(td).appendTo(tr)
			}

		});
		$(warp).append(tr);
	})

	$(table).append($(warp));
	//alert($(table).html())

	/**
	 * 导出excel
	 */
	htmlToExcel("tb_excel", "", "",filename,"");


	//导出后清除表格
	$(table).empty();
	//document.body.removeChild(table);
	worker.terminate();
	}
}
