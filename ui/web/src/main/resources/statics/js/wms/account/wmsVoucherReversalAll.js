var mydata = [];
var check = true;
var BARCODE_FLAG = true;//启用条码标签不能修改数量
var lastrow,lastcell;  
var BUSINESS_CLASS = "";
var vm = new Vue({
	el : '#rrapp',
	data : {
		warehourse:[],
		WH_NUMBER:""
	},
	methods : {
		refresh : function() {
			$('#dataGrid').trigger('reloadGrid');
		},
		onPlantChange : function(event) {
			console.log("-->onPlantChange werks = " + $("#werks").val());
			//工厂变化的时候，更新仓库号下拉框
			var plantCode = event.target.value;
			if(plantCode === null || plantCode === '' || plantCode === undefined){
				return;
			}
			// 工厂变化的时候，更新库存下拉框BUSINESS_NAME
			var werks = event.target.value;
			if (werks === null || werks === '' || werks === undefined) {
				return;
			}
			//查询工厂仓库
			$.ajax({
				url:baseUrl + "common/getWhDataByWerks",
				data:{"WERKS":plantCode},
				success:function(resp){
					vm.warehourse = resp.data;
					if(resp.data.length>0){
						vm.WH_NUMBER=resp.data[0].WH_NUMBER//方便 v-mode取值
					}

				}
			})
		},
	}
});

$(function(){
	$("#return_no").focus();
	
	var now = new Date(); //当前日期
	//$("#date_pz").val(formatDate(now));
	$("#date_gz").val(formatDate(now));
	
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
	
	fun_ShowTable();
	
	$("#btnSearchData").click(function () {
		if("" == $("#vo_no").val()){
			alert("凭证编号不能为空！");
			return false;
		}
		$("#btnSearchData").val("查询中...");
		$("#btnSearchData").attr("disabled","disabled");		
		$.ajax({
			//account/wmsVoucherReversal/getVoucherReversalData
			//account/wmsOutReversal/getOutReversalData
			url:baseURL+"account/wmsVoucherReversal/getVoucherReversalData",
			dataType : "json",
			type : "post",
			data : {
				"WERKS":$("#werks").val(),
				"WH_NUMBER":$("#wh").val(),
				"VO_TYPE":$("#vo_type").val(),
				"VO_NO":$("#vo_no").val(),
				"JZ_DATE":$("#date_gz").val(),
			},
			async: true,
			success: function (response) { 
				$("#dataGrid").jqGrid("clearGridData", true);	
				mydata = [];
				if(response.code == "0"){
					$("#btnConfirm").removeAttr("disabled");
					mydata = response.result;
					$("#dataGrid").jqGrid('GridUnload');
					console.log("-->BARCODE_FLAG = " + mydata[0].BARCODE_FLAG)
					if("X"=== mydata[0].BARCODE_FLAG){
						BARCODE_FLAG = false
					}
					fun_ShowTable();
					$("#h_text").val($("#vo_no").val());
					$("#date_pz").val(response.result[0].PZ_DATE);
					BUSINESS_CLASS = response.result[0].BUSINESS_CLASS;
				}else{
					js.showMessage(response.msg)
				}
				$("#btnSearchData").val("查询");
				$("#btnSearchData").removeAttr("disabled");
			}
		});
	});
	
	$("#btnReset").click(function () {
		$("#dataGrid").jqGrid("clearGridData", true);
		$("#dataGrid").jqGrid("setFrozenColumns");
		$("#vo_no").val("");
		$("#h_text").val("");
		BUSINESS_CLASS="";
		var now = new Date(); //当前日期
		$("#date_pz").val("");
		$("#date_gz").val(formatDate(now));
		$("#vo_no").focus();
	});
	$("#btnConfirm").click(function () {
		var flag=true;
		$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
		var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");	
		var cm = $('#dataGrid').jqGrid('getGridParam', 'colModel'); 
		var arrList = new Array();
		if(ids.length == 0){
			alert("当前还没有勾选任何行项目数据！");
			return false;
		}
		for (var i = 0; i < ids.length; i++) {
			arrList[i] = $("#dataGrid").jqGrid('getRowData',ids[i]);
			//	
			//flag=checkRequiredCell(cm, arrList[i]);
		}
		console.log(ids);
		console.log('arrList===============>>> '+arrList);
		$("#btnConfirm").val("正在操作,请稍候...");	
		$("#btnConfirm").attr("disabled","disabled");
		
		if(flag)

			var url ;
			console.info(arrList[0].BUSINESS_NAME);
		//41 42 43 44 45 46 47 48 49 50 51 52 53 54 64 73 75 76 77
			if(arrList[0].BUSINESS_NAME =='41' || arrList[0].BUSINESS_NAME =='42'
				||arrList[0].BUSINESS_NAME =='43' || arrList[0].BUSINESS_NAME =='44' || arrList[0].BUSINESS_NAME =='45'
				||arrList[0].BUSINESS_NAME =='46' || arrList[0].BUSINESS_NAME =='47'
				|| arrList[0].BUSINESS_NAME =='48'|| arrList[0].BUSINESS_NAME =='49'
				|| arrList[0].BUSINESS_NAME =='50'|| arrList[0].BUSINESS_NAME =='51'
				|| arrList[0].BUSINESS_NAME =='52'|| arrList[0].BUSINESS_NAME =='53'
				|| arrList[0].BUSINESS_NAME =='54'|| arrList[0].BUSINESS_NAME =='64'
				|| arrList[0].BUSINESS_NAME =='73'|| arrList[0].BUSINESS_NAME =='75'|| arrList[0].BUSINESS_NAME =='76'
				|| arrList[0].BUSINESS_NAME =='77'){
				url = baseURL+"account/wmsOutReversal/confirmOutReversalData";
			}else {
				url = baseURL+"account/wmsVoucherReversal/confirmVoucherReversalData";
			}
		console.info("url: "+url);

			$.ajax({
				url:url,
				dataType : "json",
				type : "post",
				data : {
					"WERKS":$("#werks").val(),
					"WH_NUMBER":$("#wh").val(),
					"VO_NO":$("#vo_no").val(),
					"PZ_DATE":$("#date_pz").val(),
					"JZ_DATE":$("#date_gz").val(),
					"HEADER_TXT":$("#h_text").val(),
					"BUSINESS_CLASS":BUSINESS_CLASS,
					"ARRLIST":JSON.stringify(arrList)
				},
				async: true,
				success: function (response) {
					if(response.code=='0'){
						console.info('response.data'+response.result);
						$("#outNoLayer").html(response.result);
						layer.open({
							type : 1,
							offset : '50px',
							skin : 'layui-layer-molv',
							title : "冲销成功",
							area : [ '600px', '200px' ],
							shade : 0,
							shadeClose : false,
							content : jQuery("#resultLayer"),
							btn : [ '确定'],
							btn1 : function(index) {
								layer.close(index);
							}
						});
						$("#dataGrid").jqGrid("clearGridData", true);
						$("#btnConfirm").val("确认");
						$("#btnConfirm").removeAttr("disabled");
					}else{
						js.alert(response.msg);
						js.showErrorMessage(response.msg,1000*100);
						$("#btnConfirm").val("确认");
						$("#btnConfirm").removeAttr("disabled");
					}





					/*if(response.code=='0'){
						//alert("确认成功，凭证号 " + response.result);
		            	//$("#btnConfirm").val("确认");
					}else{
						js.showMessage(response.msg);
		            	$("#btnConfirm").val("确认");
						$("#btnConfirm").removeAttr("disabled");
					}
					$("#btnSearchData").click();*/
				}
			});
	});

	$(document).bind("click",function(e){
		if("searchForm" == $(e.target).attr("id")||"ui-jqgrid-bdiv" == $(e.target).attr("class")){
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
		}
	});
	
	function fun_ShowTable(){
		$("#dataGrid").dataGrid({
			datatype: "local",
			data: mydata,
	        colModel: [
	            {label: 'WMS凭证', name: 'WMS_NO',index:"WMS_NO", width: "150",align:"center",sortable:true,frozen: true,
	            	/*formatter:function(val){
	            		return '<p style="height:20px;padding-top:7px">'+val+'</p>';
	            	}*/
	            },
	            {label: '行项目', name: 'WMS_ITEM_NO',index:"WMS_ITEM_NO", width: "100",align:"center",sortable:false,frozen: true},
	            {label: '料号', name: 'MATNR',index:"MATNR", width: "100",align:"center",sortable:false,frozen: true},
	            {label: '物料描述', name: 'MAKTX',index:"MAKTX", width: "200",align:"center",sortable:false,frozen: true},
	            {label: '可冲销数量', name: 'QTY_WMS_MAX',index:"QTY_WMS_MAX", width: "80",align:"center",sortable:true},  
	            {label: '<span style="color:red">*</span><span style="color:blue"><b>冲销数量</b></span>', name: 'QTY_WMS',index:"QTY_WMS", width: "80",align:"center",sortable:true,editable:BARCODE_FLAG,
	            	formatter:'number',editrules:{number:true,required:true,},
	            	formatoptions:{decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}
	            },{label: '单位', name: 'UNIT',index:"UNIT", width: "50",align:"center",sortable:true},  
	            {label: '批次', name: 'BATCH',index:"BATCH", width: "100",align:"center",sortable:true},
				{label: 'WMS移动类型', name: 'WMS_MOVE_TYPE',index:"WMS_MOVE_TYPE", width: "100",align:"center",sortable:false},
				{label: '工厂', name: 'WERKS',index:"WERKS", width: "60",align:"center",sortable:true}, 
				{label: '仓库号', name: 'WH_NUMBER',index:"WH_NUMBER", width: "60",align:"center",sortable:true}, 
				{label: '库位', name: 'LGORT',index:"LGORT", width: "60",align:"center",sortable:true}, 
				{label: '库存类型', name: 'SOBKZ',index:"SOBKZ", width: "65",align:"center",sortable:true,editable:true},
				{label: '供应商', name: 'LIFNR',index:"LIFNR", width: "80",align:"center",sortable:false},
				{label: '供应商描述', name: 'LIKTX',index:"LIKTX", width: "130",align:"center",sortable:false},
				{label: '<span style="color:red">*</span><span style="color:blue"><b>行文本</b></span>', name: 'ITEM_TEXT',index:"ITEM_TEXT", 
					width: "200",align:"center",editable:true,sortable:true}, 
				{label: 'ID', name: 'ID',index:"ID", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'F_WERKS', name: 'F_WERKS',index:"F_WERKS", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'F_BATCH', name: 'F_BATCH',index:"F_BATCH", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'IO_NO', name: 'IO_NO',index:"IO_NO", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'WBS', name: 'WBS',index:"WBS", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'BUSINESS_TYPE', name: 'BUSINESS_TYPE',index:"BUSINESS_TYPE", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'BUSINESS_NAME', name: 'BUSINESS_NAME',index:"BUSINESS_NAME", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'SAP_OUT_NO', name: 'SAP_OUT_NO',index:"SAP_OUT_NO", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'SAP_OUT_ITEM_NO', name: 'SAP_OUT_ITEM_NO',index:"SAP_OUT_ITEM_NO", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'WBS', name: 'WBS',index:"WBS", width: "50",align:"center",sortable:false,hidden:true},				
				{label: 'MO_NO', name: 'MO_NO',index:"MO_NO", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'MO_ITEM_NO', name: 'MO_ITEM_NO',index:"MO_ITEM_NO", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'RSNUM', name: 'RSNUM',index:"RSNUM", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'RSPOS', name: 'RSPOS',index:"RSPOS", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'PO_NO', name: 'PO_NO',index:"PO_NO", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'PO_ITEM_NO', name: 'PO_ITEM_NO',index:"PO_ITEM_NO", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'RECEIPT_ITEM_NO', name: 'RECEIPT_ITEM_NO',index:"RECEIPT_ITEM_NO", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'RECEIPT_NO', name: 'RECEIPT_NO',index:"RECEIPT_NO", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'WMS_MOVE_TYPE', name: 'WMS_MOVE_TYPE',index:"WMS_MOVE_TYPE", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'SAP_MOVE_TYPE', name: 'SAP_MOVE_TYPE',index:"SAP_MOVE_TYPE", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'SAP_FLAG', name: 'SAP_FLAG',index:"SAP_FLAG", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'RETURN_NO', name: 'RETURN_NO',index:"RETURN_NO", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'RETURN_ITEM_NO', name: 'RETURN_ITEM_NO',index:"RETURN_ITEM_NO", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'WMS_SAP_MAT_DOC', name: 'WMS_SAP_MAT_DOC',index:"WMS_SAP_MAT_DOC", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'PZ_DATE', name: 'PZ_DATE',index:"PZ_DATE", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'SAP_MATDOC_NO', name: 'SAP_MATDOC_NO',index:"SAP_MATDOC_NO", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'SAP_MATDOC_ITEM_NO', name: 'SAP_MATDOC_ITEM_NO',index:"SAP_MATDOC_ITEM_NO", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'GM_CODE', name: 'GM_CODE',index:"GM_CODE", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'BIN_CODE', name: 'BIN_CODE',index:"BIN_CODE", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'INBOUND_NO', name: 'INBOUND_NO',index:"INBOUND_NO", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'INBOUND_ITEM_NO', name: 'INBOUND_ITEM_NO',index:"INBOUND_ITEM_NO", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'INSPECTION_NO', name: 'INSPECTION_NO',index:"INSPECTION_NO", hidden:true},
				{label: 'INSPECTION_ITEM_NO', name: 'INSPECTION_ITEM_NO',index:"INSPECTION_ITEM_NO", hidden:true},
				{label: 'COST_CENTER', name: 'COST_CENTER',index:"COST_CENTER", hidden:true},
				{label: 'IO_NO', name: 'IO_NO',index:"IO_NO", hidden:true},
				{label: 'WBS', name: 'WBS',index:"WBS", hidden:true},
				{label: 'SAKTO', name: 'SAKTO',index:"SAKTO", hidden:true},
				{label: 'ANLN1', name: 'ANLN1',index:"ANLN1", hidden:true},
				{label: 'MO_NO', name: 'MO_NO',index:"MO_NO", hidden:true},
				{label: 'MO_ITEM_NO', name: 'MO_ITEM_NO',index:"MO_ITEM_NO", hidden:true},
				{label: 'RSNUM', name: 'RSNUM',index:"RSNUM", hidden:true},
				{label: 'RSPOS', name: 'RSPOS',index:"RSPOS", hidden:true},
				{label: 'F_WERKS', name: 'F_WERKS',index:"F_WERKS", hidden:true},
				{label: 'F_LGORT', name: 'F_LGORT',index:"F_LGORT", hidden:true},
				{label: 'F_BATCH', name: 'F_BATCH',index:"F_BATCH", hidden:true},
				
				{label: 'FLAG', name: 'FLAG',index:"FLAG", hidden:true},
				{label: 'MESSAGE', name: 'MESSAGE',index:"MESSAGE", hidden:true},
				{label: 'LABEL_NO', name: 'LABEL_NO',index:"LABEL_NO", hidden:true},
				{label: 'REQUIREMENT_NO', name: 'REQUIREMENT_NO',index:"REQUIREMENT_NO", hidden:true},
				{label: 'REQUIREMENT_ITEM_NO', name: 'REQUIREMENT_ITEM_NO',index:"REQUIREMENT_ITEM_NO", hidden:true},
				{label: 'COST_CENTER', name: 'COST_CENTER',index:"COST_CENTER", hidden:true},
				
				{label: 'REF_SAP_MATDOC_NO', name: 'REF_SAP_MATDOC_NO',index:"REF_SAP_MATDOC_NO", hidden:true},
				{label: 'REF_SAP_MATDOC_ITEM_NO', name: 'REF_SAP_MATDOC_ITEM_NO',index:"REF_SAP_MATDOC_ITEM_NO", hidden:true},
				{label: 'REF_SAP_MATDOC_YEAR', name: 'REF_SAP_MATDOC_YEAR',index:"REF_SAP_MATDOC_YEAR", hidden:true},
				{label: 'UMREN', name: 'UMREN',index:"UMREN", hidden:true},
				{label: 'UMREZ', name: 'UMREZ',index:"UMREZ", hidden:true},
				{label: 'MOVE_PLANT', name: 'MOVE_PLANT',index:"MOVE_PLANT", hidden:true},
				{label: 'MOVE_STLOC', name: 'MOVE_STLOC',index:"MOVE_STLOC", hidden:true},
				{label: 'CUSTOMER', name: 'CUSTOMER',index:"CUSTOMER", hidden:true},
				{ label: '', name: 'ASNNO', index: 'ASNNO', width: 80,hidden:true},
				{ label: '', name: 'ASNITM', index: 'ASNITM', width: 80,hidden:true},
	        ],
	        loadComplete:function(data){
	        	var rows=$("#dataGrid").jqGrid('getRowData');
				$.each(rows,function(i,row){	
					if(row.FLAG !=null && row.FLAG !=undefined && row.FLAG !='undefined' && row.FLAG=='false') {
						 $("#dataGrid tbody").children("tr").eq(i+1).children("td").find(".cbox").attr("disabled",true);
					  }	  
					  
				});
				$("#btnSearchData").removeAttr("disabled");
				$("#btnConfirm").removeAttr("disabled");
				js.closeLoading();
	        },
			beforeEditCell:function(rowid, cellname, v, iRow, iCol){
				lastrow=iRow;
				lastcell=iCol;
			},
			afterSaveCell:function(rowid, cellname, value, iRow, iCol){	
				var row =  $("#dataGrid").jqGrid('getRowData', rowid);
				if(cellname=='QTY_WMS'){
					if(isNaN(value)){
						js.alert("冲销数量必须为数字！");
						$("#dataGrid").jqGrid('setCell', rowid, cellname, row.QTY_WMS_MAX,'editable-cell');
						return false;
					}
					if(Number(value)>Number(row.QTY_WMS_MAX)){
						js.alert("冲销数量不能大于"+row.QTY_WMS_MAX);
						$("#dataGrid").jqGrid('setCell', rowid, cellname, row.QTY_WMS_MAX,'editable-cell');
						return false;
					}
					if(Number(value)<=0){
						js.alert("冲销数量必须大于0");
						$("#dataGrid").jqGrid('setCell', rowid, cellname, row.QTY_WMS_MAX,'editable-cell');
						return false;
					}
					
				}
			},
			beforeSelectRow : function(rowid, e) { // 点击复选框才选中行
				var $myGrid = $(this), i = $.jgrid.getCellIndex($(e.target).closest('td')[0]), cm = $myGrid.jqGrid('getGridParam', 'colModel');
				if (cm[i].name == 'cb') {
					$('#dataGrid').jqGrid('setSelection', rowid);
				}
				return (cm[i].name == 'cb');
			},
			onSelectRow:function(rowid,status,e){
				console.log("-->rowid : " + rowid);
		    	if(status == true){
		    		var row = $("#dataGrid").jqGrid('getRowData',rowid);
					if(row.FLAG !=null && row.FLAG !=undefined && row.FLAG !='undefined' && row.FLAG=='false') {
						$("#dataGrid").jqGrid("setSelection", rowid,false);//设置该行不能被选中。
						js.showMessage(row.MESSAGE);
					} 
		    	}
			},
			onSelectAll:function(rowids,status){
				   if(status==true){
					   $('#dataGrid').jqGrid("saveCell", lastrow, lastcell);				   
					   $.each(rowids,function(i,rowid){					  
						  var row = $("#dataGrid").jqGrid("getRowData",rowid);
							if(row.FLAG !=null && row.FLAG !=undefined && row.FLAG !='undefined' && row.FLAG=='false') {
								$("#dataGrid").jqGrid("setSelection", rowid,false);//设置该行不能被选中。
								js.showMessage("第"+rowid+"行"+row.MESSAGE);
							} 	
					   })
				   }
			},
			shrinkToFit: false,
	        width:1900,
	        cellEdit:true,
	        cellurl:'#',
			cellsubmit:'clientArray',
	        showCheckbox:true
	    });
		
		$("#dataGrid").jqGrid("setFrozenColumns");
	}
	
	
});

$('body').on('DOMNodeInserted', 'input', function () {
	
	$("input[name='REASON']").typeahead({
		source : function(input, process) {
			$.get(baseURL+"common/getReasonData", {"REASON_DESC" : input}, function(response) {
				var data=response.reason;
				busNumberlist = data;
				var results = new Array();
				$.each(data, function(index, value) {
					results.push(value.REASON_DESC);
				})
				return process(results);
			}, 'json');
		},
		items : 15,
		matcher : function(item) {				
			return true;
		},
		updater : function(item) {				
			return item;
		}
	});
});