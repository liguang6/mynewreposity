var mydata = [];
var check = true;
var userName = "";
var lastrow,lastcell;  
var barCodeFlag = false;
var vm = new Vue({
	el : '#rrapp',
	data:{warehourse:[]},
	created: function(){
    	this.getUser();
    },
	methods : {
		getUser: function () {
            $.getJSON(baseURL+"sys/user/info?_" + $.now(), function (r) {
            	userName = r.user.fullName;
            });
        },
		refresh : function() {
			$('#dataGrid').trigger('reloadGrid');
		},
		onPlantChange : function(event) {
			console.log("-->onPlantChange werks = " + $("#werks").val());			
			// 工厂变化的时候，更新库存下拉框BUSINESS_NAME
			var werks = event.target.value;
			if (werks === null || werks === '' || werks === undefined) {
				return;
			}
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

$(function(){
	$("#return_no").focus();
	
	var now = new Date(); //当前日期
	$("#date_pz").val(formatDate(now));
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
		if("" == $("#return_no").val()){
			alert("退货单号不能为空！");
			return false;
		}
		$("#btnSearchData").val("查询中...");
		$("#btnSearchData").attr("disabled","disabled");		
		$.ajax({
			url:baseURL+"returngoods/getReceiveRoomOutReturnData",
			dataType : "json",
			type : "post",
			data : {
				"WERKS":$("#werks").val(),
				"WH_NUMBER":$("#wh").val(),
				"RETURN_NO":$("#return_no").val(),
				"JZ_DATE":$("#date_gz").val(),
			},
			async: true,
			success: function (response) { 
				$("#dataGrid").jqGrid("clearGridData", true);
				mydata.length=0;
				if(response.code == "0"){
					mydata = response.result;
					$("#dataGrid").jqGrid('GridUnload');	
					barCodeFlag = ("X" == mydata[0].BARCODE_FLAG)?false:true;
					fun_ShowTable();
					//ModBy:YK 190422 
					//5.3.3.1	针对工厂在【工厂质检配置表-WMS_C_QC_PLANT】质检标识-TEST_FLAG 为02 无需质检即未上质检模块的，
					//且工厂+仓库号【仓库配置表-WMS_C_WH】中BARCODE_FLAG为X 即启用条码 时，
					//则系统返回消息：‘**仓库号启用了条码管理且未上质检功能，请选择PDA扫描退货’
					console.log("-->TEST_FLAG = " + mydata[0].TEST_FLAG)
					console.log("-->BARCODE_FLAG = " + mydata[0].BARCODE_FLAG)
					if("02" === mydata[0].TEST_FLAG && "X" === mydata[0].BARCODE_FLAG){
						$("#btnConfirm").attr("disabled","disabled");
						alert($("#werks").val() + "仓库号启用了条码管理且未上质检功能，请选择PDA扫描退货")
					}else{
						$("#btnConfirm").removeAttr("disabled");
					}
				}else{
					js.showMessage(response.msg)
				}
				$("#btnSearchData").val("查询");
				$("#btnSearchData").removeAttr("disabled");
			}
		});
	});
	
	$("#btnReset").click(function () {
		$("#btnConfirm").removeAttr("disabled");
		$("#dataGrid").jqGrid("clearGridData", true);
		$("#dataGrid").jqGrid("setFrozenColumns");
		$("#return_no").val("");
		var now = new Date(); //当前日期
		$("#date_pz").val(formatDate(now));
		$("#date_gz").val(formatDate(now));
		$("#return_no").focus();
	});
	$("#btnConfirm").click(function () {
		$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
		if(check){	//防止用户在输入数量的文本框没有消失就直接点创建
			var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");	
			var arrList = new Array();
			if(ids.length == 0){
				alert("当前还没有勾选任何行项目数据！");
				return false;
			}
			for (var i = 0; i < ids.length; i++) {
				arrList[i] = $("#dataGrid").jqGrid('getRowData',ids[i]);
			}
			
			console.log(ids);
			console.log(arrList);
			
			$("#btnConfirm").val("正在操作,请稍候...");	
			$("#btnConfirm").attr("disabled","disabled");
			
			$.ajax({
				url:baseURL+"returngoods/confirmReceiveRoomOutReturn",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":$("#werks").val(),
					"RETURN_NO":$("#return_no").val(),
					"PZ_DATE":$("#date_pz").val(),
					"JZ_DATE":$("#date_gz").val(),
					"USERNAME":userName,
					"ARRLIST":JSON.stringify(arrList)
				},
				async: true,
				success: function (response) {
					if(response.code=='0'){
						//alert("确认成功<br/>" + response.result);
		            	$("#btnConfirm").val("确认退货");
		            	$("#msg").html(response.result);
		            	layer.open({
		            		type : 1,
		            		offset : '50px',
		            		skin : 'layui-layer-molv',
		            		title : "操作成功",
		            		area : [ '500px', '200px' ],
		            		shade : 0,
		            		shadeClose : false,
		            		content : jQuery("#resultLayer"),
		            		btn : [ '确定'],
		            		btn1 : function(index) {
		            			layer.close(index);
		    					$("#btnConfirm").removeAttr("disabled");
		            		}
		            	});
					}else{
						js.showMessage(response.msg);
		            	$("#btnConfirm").val("确认退货");
					}
					$("#btnSearchData").click();
					$("#btnConfirm").removeAttr("disabled");
				}
			});
			
		}
		check = true;
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
	            {label: '退货单号', name: 'RETURN_NO',index:"RETURN_NO", width: "100",align:"center",sortable:false,frozen: true
	            },
	            {label: '料号', name: 'MATNR',index:"MATNR", width: "100",align:"center",sortable:true,frozen: true},
	            {label: '物料描述', name: 'MAKTX',index:"MAKTX", width: "200",align:"center",sortable:false,frozen: true},
	            {label: '退货数量', name: 'TOTAL_RETURN_QTY',index:"TOTAL_RETURN_QTY", width: "70",align:"center",sortable:true,hidden:true},  
	            {label: '<span style="color:red">*</span><span style="color:blue"><b>退货数量</b></span>', name: 'QTY2',index:"QTY2", width: "80",align:"center",sortable:true,editable:barCodeFlag,
	            	formatter:'number',editrules:{number:true},
	            	formatoptions:{decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}
	            },
	            {label: '<span style="color:red">*</span><span style="color:blue"><b>行文本</b></span>', name: 'ITEM_TEXT',index:"ITEM_TEXT", width: "150",align:"center",sortable:false,editable:true}, 
	            {label: '退货批次', name: 'BATCH',index:"BATCH", width: "100",align:"center",sortable:true},
				{label: '单位', name: 'UNIT',index:"UNIT", width: "50",align:"center",sortable:false},
				{label: '库存类型', name: 'SOBKZ',index:"SOBKZ", width: "100",align:"center",sortable:true}, 
				{label: '供应商代码', name: 'LIFNR',index:"LIFNR", width: "100",align:"center",sortable:true},
				{label: '供应商名称', name: 'LIKTX',index:"LIKTX", width: "100",align:"center",sortable:true},
				{label: '发货工厂', name: 'F_WERKS',index:"F_WERKS", width: "100",align:"center",sortable:true}, 
				{label: '收货单号', name: 'RECEIPT_NO',index:"RECEIPT_NO", width: "100",align:"center",sortable:true}, 
				{label: 'HID', name: 'HID',index:"HID", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'IID', name: 'IID',index:"IID", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'DID', name: 'DID',index:"DID", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'SAP_MOVE_TYPE', name: 'SAP_MOVE_TYPE',index:"SAP_MOVE_TYPE", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'WMS_MOVE_TYPE', name: 'WMS_MOVE_TYPE',index:"WMS_MOVE_TYPE", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'RECEIPT_ITEM_NO', name: 'RECEIPT_ITEM_NO',index:"RECEIPT_ITEM_NO", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'BUSINESS_TYPE', name: 'BUSINESS_TYPE',index:"BUSINESS_TYPE", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'BUSINESS_CODE', name: 'BUSINESS_CODE',index:"BUSINESS_CODE", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'BUSINESS_NAME', name: 'BUSINESS_NAME',index:"BUSINESS_NAME", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'BUSINESS_CLASS', name: 'BUSINESS_CLASS',index:"BUSINESS_CLASS", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'LABEL_NO', name: 'LABEL_NO',index:"LABEL_NO", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'BARCODE_FLAG', name: 'BARCODE_FLAG',index:"BARCODE_FLAG", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'F_BATCH', name: 'F_BATCH',index:"F_BATCH", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'WERKS', name: 'WERKS',index:"WERKS", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'WH_NUMBER', name: 'WH_NUMBER',index:"WH_NUMBER", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'LGORT', name: 'LGORT',index:"LGORT", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'PO_NO', name: 'PO_NO',index:"PO_NO", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'PO_ITEM_NO', name: 'PO_ITEM_NO',index:"PO_ITEM_NO", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'IO_NO', name: 'IO_NO',index:"IO_NO", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'WBS', name: 'WBS',index:"WBS", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'MO_NO', name: 'MO_NO',index:"MO_NO", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'MO_ITEM_NO', name: 'MO_ITEM_NO',index:"MO_ITEM_NO", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'RSNUM', name: 'RSNUM',index:"RSNUM", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'RSPOS', name: 'RSPOS',index:"RSPOS", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'SAP_OUT_NO', name: 'SAP_OUT_NO',index:"SAP_OUT_NO", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'SAP_OUT_ITEM_NO', name: 'SAP_OUT_ITEM_NO',index:"SAP_OUT_ITEM_NO", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'RETURN_ITEM_NO', name: 'RETURN_ITEM_NO',index:"RETURN_ITEM_NO", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'REF_SAP_MATDOC_NO', name: 'REF_SAP_MATDOC_NO',index:"REF_SAP_MATDOC_NO", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'REF_SAP_MATDOC_ITEM_NO', name: 'REF_SAP_MATDOC_ITEM_NO',index:"REF_SAP_MATDOC_ITEM_NO", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'REF_SAP_MATDOC_YEAR', name: 'REF_SAP_MATDOC_YEAR',index:"REF_SAP_MATDOC_YEAR", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'XCHPF', name: 'XCHPF',index:"XCHPF", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'ASNNO', name: 'ASNNO',index:"ASNNO", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'ASNITM', name: 'ASNITM',index:"ASNITM", width: "50",align:"center",sortable:false,hidden:true},
	        ],
			beforeEditCell:function(rowid, cellname, v, iRow, iCol){
				lastrow=iRow;
				lastcell=iCol;
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
			},
			afterSaveCell:function(rowid, cellname, value, iRow, iCol){
				var row =  $("#dataGrid").jqGrid('getRowData', rowid);
				if(cellname=='QTY2'){
					if("" == value){
						alert("退货数量不能为空！");
						$("#dataGrid").jqGrid('setCell', rowid, cellname, mydata[rowid-1].RETURN_QTY,'editable-cell');
						mydata[rowid-1].QTY2 = row.RETURN_QTY;
						check = false;
						return false;
					}
					if(parseFloat(value) > parseFloat(row.TOTAL_RETURN_QTY)){
						alert("退货数量不能大于可退数量！");
						$("#dataGrid").jqGrid('setCell', rowid, cellname, mydata[rowid-1].TOTAL_RETURN_QTY,'editable-cell');
						mydata[rowid-1].QTY2 = row.TOTAL_RETURN_QTY;
						check = false;
					}else{
						mydata[rowid-1].QTY2 = value;
						check = true;
					}
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