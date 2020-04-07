var mydata = [{ id: "1",MAKTX: ""},{ id: "2",MAKTX: ""},{ id: "3",MAKTX: ""},{ id: "4",MAKTX: ""},{ id: "5",MAKTX: ""}];
var col_list = [];
var re_type = '0';
var batchEdit = false;	//批次是否允许修改
var HANDOVER_TYPE = "";
var col_list_base = [			
	{label: '工厂', name: 'WERKS',index:"WERKS", width: "80",align:"center",sortable:false,frozen: true},
    {label: '仓库号', name: 'WH_NUMBER',index:"WH_NUMBER", width: "80",align:"center",sortable:false,frozen: true},
    {label: '退货单号', name: 'RETURN_NO',index:"RETURN_NO", width: "120",align:"center",sortable:false,frozen: true},
    {label: '行项目号', name: 'RETURN_ITEM_NO',index:"RETURN_ITEM_NO", width: "80",align:"center",sortable:false,frozen: false},
    {label: '料号', name: 'MATNR',index:"MATNR", width: "100",align:"center",sortable:false,frozen: false},
    {label: '物料描述', name: 'MAKTX',index:"MAKTX", width: "200",align:"center",sortable:false,frozen: false},
    {label: '单位', name: 'UNIT',index:"UNIT", width: "80",align:"center",sortable:false,frozen: false},
    {label: '创单数量', name: 'RETURN_QTY',index:"RETURN_QTY", width: "80",align:"center",sortable:false,frozen: false},
    {label: '<span style="color:red">*</span><span style="color:blue"><b>退货数量</b></span>', name: 'QTY1',index:"QTY1", width: "80",align:"center",sortable:false,editable:true}, 
    {label: '<span style="color:red">*</span><span style="color:blue"><b>装箱数量</b></span>', name: 'BOXQTY',index:"BOXQTY", width: "80",align:"center",sortable:false,editable:true}, 
    {label: '<span style="color:red">*</span><span style="color:blue"><b>接收库位</b></span><a onclick="flgort();" id="flgort">&darr;填充</a></span>', name: 'LGORT',index:"LGORT", width: "100",align:"center",sortable:false,edittype:'select',editable:true}, 
    {label: '<span style="color:red">*</span><span style="color:blue"><b>储位</b></span>', name: 'BIN_CODE',index:"BIN_CODE", width: "80",align:"center",sortable:false,editable:true}, 
    {label: '<span style="color:red">*</span><span style="color:blue"><b>供应商代码</b></span>', name: 'LIFNR',index:"LIFNR", width: "100",align:"center",sortable:false,editable:true}, 
    {label: '供应商名称', name: 'LIKTX',index:"LIKTX", width: "150",align:"center",sortable:false,frozen: false},
    {label: '库存类型', name: 'SOBKZ',index:"SOBKZ", width: "100",align:"center",sortable:false,frozen: false,edittype:'select',editable:true},
    {label: '<span style="color:red">*</span><span style="color:blue"><b>行文本</b></span>', name: 'ITEM_TEXT',index:"ITEM_TEXT", width: "100",align:"center",sortable:false,editable:true}, 
    {label: '<span style="color:red">*</span><span style="color:blue"><b>日期</b></span>', name: 'RECEIPT_DATE',index:"RECEIPT_DATE", width: "100",align:"center",sortable:false,editable:true,
    	formatter:function(val){
    		var now = new Date() //当前日期 用于生产新批次，用于接收日期或生产日期
    		return formatDate(now)
    	}, 
    },
    {label: 'HID', name: 'HID',index:"HID", width: "50",align:"center",sortable:false,hidden:true},
    {label: 'IID', name: 'IID',index:"IID", width: "50",align:"center",sortable:false,hidden:true},
    {label: 'DID', name: 'DID',index:"DID", width: "50",align:"center",sortable:false,hidden:true},
    {label: 'BUSINESS_CLASS', name: 'BUSINESS_CLASS',index:"BUSINESS_CLASS", width: "50",align:"center",sortable:false,hidden:true},
    {label: 'BUSINESS_NAME', name: 'BUSINESS_NAME',index:"BUSINESS_NAME", width: "50",align:"center",sortable:false,hidden:true},
    {label: 'BUSINESS_TYPE', name: 'BUSINESS_TYPE',index:"BUSINESS_TYPE", width: "50",align:"center",sortable:false,hidden:true},
    {label: 'TEST_FLAG', name: 'TEST_FLAG',index:"TEST_FLAG", width: "50",align:"center",sortable:false,hidden:true},
    {label: 'PO_NO', name: 'PO_NO',index:"PO_NO", width: "50",align:"center",sortable:false,hidden:true},
    {label: 'PO_ITEM_NO', name: 'PO_ITEM_NO',index:"PO_ITEM_NO", width: "50",align:"center",sortable:false,hidden:true},
    {label: 'MO_NO', name: 'MO_NO',index:"MO_NO", width: "50",align:"center",sortable:false,hidden:true},
    {label: 'MO_ITEM_NO', name: 'MO_ITEM_NO',index:"MO_ITEM_NO", width: "50",align:"center",sortable:false,hidden:true},
    {label: 'RSNUM', name: 'RSNUM',index:"RSNUM", width: "50",align:"center",sortable:false,hidden:true},
    {label: 'RSPOS', name: 'RSPOS',index:"RSPOS", width: "50",align:"center",sortable:false,hidden:true},
    {label: 'F_LGORT', name: 'F_LGORT',index:"F_LGORT", width: "50",align:"center",sortable:false,hidden:true},
    {label: 'RECEIPT_NO', name: 'RECEIPT_NO',index:"RECEIPT_NO", width: "50",align:"center",sortable:false,hidden:true},
    {label: 'WMS_MOVE_TYPE', name: 'WMS_MOVE_TYPE',index:"WMS_MOVE_TYPE", width: "50",align:"center",sortable:false,hidden:true},
    {label: 'SAP_MOVE_TYPE', name: 'SAP_MOVE_TYPE',index:"SAP_MOVE_TYPE", width: "50",align:"center",sortable:false,hidden:true},
    {label: 'RECEIPT_ITEM_NO', name: 'RECEIPT_ITEM_NO',index:"RECEIPT_ITEM_NO", width: "50",align:"center",sortable:false,hidden:true},
    ];
var check = true;
var lastrow,lastcell;  
var vm = new Vue({
	el : '#rrapp',
	data:{warehourse:[]},
	methods : {
		refresh : function() {
			col_list = col_list_base.slice(0);
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
	col_list = col_list_base.slice(0);
	var now = new Date(); //当前日期
	$("#date_jz").val(formatDate(now));
	$("#date_pz").val(formatDate(now));
	
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
	$("#originalBatch").change(function () {
		$("#dataGrid").jqGrid("clearGridData", true)
	})
	
	$("#btnSearchData").click(function () {		
		
		$("#btnSearchData").val("查询中...");
		$("#btnSearchData").attr("disabled","disabled");		
		$.ajax({
			url:baseURL+"workshopReturn/getWorkshopReturnConfirmData",
			dataType : "json",
			type : "post",
			data : {
				"WERKS":$("#werks").val(),
				"WH_NUMBER":$("#wh").val(),
				"RETURN_NO":$("#return_no").val(),
				"ORIGINALBATCH":$("#originalBatch").val()
			},
			async: true,
			success: function (response) { 
				$("#dataGrid").jqGrid("clearGridData", true);
				mydata.length=0;
				if(response.code == "0"){
					if(response.result.length>0){
						mydata = response.result;
						$("#dataGrid").jqGrid('GridUnload');
						re_type = response.result[0].BUSINESS_NAME;
						HANDOVER_TYPE = response.result[0].HANDOVER_TYPE;
						var return_status = response.result[0].RETURN_STATUS;
						if('1'===$("#originalBatch").val()){
							batchEdit = false
						}else{
							batchEdit = false	//ModBy:YK190801 改回批次由系统生成
						}
						console.log("-->RETURN_TYPE : " + re_type + ";RETURN_STATUS = " + return_status + ";batchEdit = " + batchEdit);
						col_list = col_list_base.slice(0);
						if("00" == return_status){
							if("35" == re_type){		//生产订单
								col_list.push({label: '<span style="color:blue"><b>批次</b></span>', name: 'BATCH',index:"BATCH", width: "80",align:"center",sortable:false,editable:batchEdit}) 				    
								col_list.push({label: '生产订单', name: 'MO_NO',index:"MO_NO", width: "100",align:"center",sortable:false,frozen: false})
								col_list.push({label: '行项目号', name: 'MO_ITEM_NO',index:"MO_ITEM_NO", width: "100",align:"center",sortable:false,frozen: false})
								col_list.push({label: '预留号', name: 'RSNUM',index:"RSNUM", width: "100",align:"center",sortable:false,frozen: false})
								col_list.push({label: '预留行项目号', name: 'RSPOS',index:"RSPOS", width: "120",align:"center",sortable:false,frozen: false})
							}else if("36" == re_type){
								col_list.push({label: '<span style="color:blue"><b>批次</b></span>', name: 'BATCH',index:"BATCH", width: "80",align:"center",sortable:false,editable:batchEdit}) 				    
							}else if("37" == re_type){	//内部订单
								col_list.push({label: '<span style="color:blue"><b>批次</b></span>', name: 'BATCH',index:"BATCH", width: "80",align:"center",sortable:false,editable:batchEdit}) 				    
								col_list.push({label: '内部订单', name: 'IO_NO',index:"IO_NO", width: "100",align:"center",sortable:false,frozen: false})
								col_list.push({label: '接收工厂', name: 'F_WERKS',index:"F_WERKS", width: "100",align:"center",sortable:false,frozen: false})
							}else if("38" == re_type){	//成本中心
								col_list.push({label: '<span style="color:blue"><b>批次</b></span>', name: 'BATCH',index:"BATCH", width: "80",align:"center",sortable:false,editable:batchEdit}) 				    
								col_list.push({label: '成本中心', name: 'COST_CENTER',index:"COST_CENTER", width: "100",align:"center",sortable:false,frozen: false})
							}else if("39" == re_type){	//WBS
								col_list.push({label: '<span style="color:red">*</span><span style="color:blue"><b>批次</b></span>', name: 'BATCH',index:"BATCH", width: "80",align:"center",sortable:false,editable:batchEdit}) 				    
								col_list.push({label: 'WBS元素', name: 'WBS',index:"WBS", width: "100",align:"center",sortable:false,frozen: false})
							}else if("70" == re_type){	//STO
								col_list.push({label: '<span style="color:blue"><b>批次</b></span>', name: 'BATCH',index:"BATCH", width: "80",align:"center",sortable:false,editable:batchEdit}) 				    
								col_list.push({label: 'SAP交货单', name: 'SAP_OUT_NO',index:"SAP_OUT_NO", width: "100",align:"center",sortable:false,frozen: false})
								col_list.push({label: 'SAP行项目', name: 'SAP_OUT_ITEM_NO',index:"SAP_OUT_ITEM_NO", width: "100",align:"center",sortable:false,frozen: false})
							}else if("68" == re_type){	//UB
								col_list.push({label: '<span style="color:blue"><b>批次</b></span>', name: 'BATCH',index:"BATCH", width: "80",align:"center",sortable:false,editable:batchEdit}) 				    
								col_list.push({label: '采购订单', name: 'PO_NO',index:"PO_NO", width: "100",align:"center",sortable:false,frozen: false,
									formatter:function(value, name, record){
										return record.RSNUM
									}
								})
								col_list.push({label: '采购订单行项目', name: 'PO_ITEM_NO',index:"PO_ITEM_NO", width: "120",align:"center",sortable:false,frozen: false,
									formatter:function(value, name, record){
										return record.RSPOS
									}
								})
							}else if("69" == re_type){	//委外
								col_list.push({label: '<span style="color:blue"><b>批次</b></span>', name: 'BATCH',index:"BATCH", width: "80",align:"center",sortable:false,editable:batchEdit}) 				    
								col_list.push({label: '采购订单', name: 'PO_NO',index:"PO_NO", width: "100",align:"center",sortable:false,frozen: false})
								col_list.push({label: '采购订单行项目', name: 'PO_ITEM_NO',index:"PO_ITEM_NO", width: "120",align:"center",sortable:false,frozen: false})
							}else if("46" == re_type){	//工厂间调拨
								col_list.remove(10);
								col_list.remove(11);
								col_list.push({label: '<span style="color:blue"><b>批次</b></span>', name: 'BATCH',index:"BATCH", width: "80",align:"center",sortable:false,editable:batchEdit}) 				    
								col_list.push({label: '退货工厂', name: 'F_WERKS',index:"F_WERKS", width: "100",align:"center",sortable:false,frozen: false})
							}else if("40" == re_type){	//线边仓退回
								col_list.push({label: '<span style="color:red">*</span><span style="color:blue"><b>批次</b></span>', name: 'BATCH',index:"BATCH", width: "100",align:"center",sortable:false,editable:true}) 
							}
							
							fun_ShowTable();
						}else if("03" == return_status){
							mydata = [{ id: "1",orderNo: ""},{ id: "2",orderNo: ""},{ id: "3",orderNo: ""},{ id: "4",orderNo: ""},{ id: "5",orderNo: ""}];
							col_list = col_list_base.slice(0);
							$("#dataGrid").jqGrid('GridUnload');
							fun_ShowTable();
							js.showMessage("该退货单已经完成！");
						}
					}else{
						js.showMessage("没有查询到任何数据，请检查查询条件。");
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
		mydata = [{ id: "1",orderNo: ""},{ id: "2",orderNo: ""},{ id: "3",orderNo: ""},{ id: "4",orderNo: ""},{ id: "5",orderNo: ""}];
		col_list = col_list_base.slice(0);
		$("#dataGrid").jqGrid('GridUnload');
		fun_ShowTable();
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
			//验证勾选的行项目数据填写完整
			for (var i = 0; i < ids.length; i++) {
				arrList[i] = $("#dataGrid").jqGrid('getRowData',ids[i]);
				if(''===$("#dataGrid").jqGrid('getRowData',ids[i]).QTY1.trim()){
					alert("退货数量不能为空！");
					return false;
				}
				if(''===$("#dataGrid").jqGrid('getRowData',ids[i]).BOXQTY.trim()){
					alert("装箱数量不能为空！");
					return false;
				}
				if(''===$("#dataGrid").jqGrid('getRowData',ids[i]).LGORT.trim()){
					alert("接收库位不能为空！");
					return false;
				}
				if(''===$("#dataGrid").jqGrid('getRowData',ids[i]).BIN_CODE.trim()){
					alert("储位不能为空！");
					return false;
				}
				if(''===$("#dataGrid").jqGrid('getRowData',ids[i]).LIFNR.trim() && 'Z'!=$("#dataGrid").jqGrid('getRowData',ids[i]).SOBKZ.trim()){
					alert("供应商不能为空！");
					return false;
				}
			}
			
			//退料确认时，需触发交接功能，交接功能参考《公共功能文档》开发。
			if("00" != HANDOVER_TYPE){
				commonHandover(returnConfirm,returnConfirm,HANDOVER_TYPE);
			}else{
				returnConfirm("","");
			}
			
			console.log('-->confirm')
		}
		check = true;
	});

	$(document).bind("click",function(e){
		if("searchForm" == $(e.target).attr("id")||"ui-jqgrid-bdiv" == $(e.target).attr("class")){
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
		}
	});
	
});

function returnConfirm(user,username){
	console.log('-->returnConfirm user:' + user + ",username:" + username)
	var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");	
	var arrList = new Array();

	for (var i = 0; i < ids.length; i++) {
		arrList[i] = $("#dataGrid").jqGrid('getRowData',ids[i]);
	}
	
	$("#btnConfirm").val("正在操作,请稍候...");	
	$("#btnConfirm").attr("disabled","disabled");
	var BUSINESS_TYPE = $("#re_type").find('option:selected').attr('b_type');
	
	$.ajax({
		url:baseURL+"workshopReturn/confirmWorkshopReturn",
		dataType : "json",
		type : "post",
		data : {
			"WERKS":$("#werks").val(),
			"WH_NUMBER":$("#wh").val(),
			"RETURN_NO":$("#return_no").val(),
			"ORIGINALBATCH":$("#originalBatch").val(),
			"ARRLIST":JSON.stringify(arrList),
			"JZ_DATE":$("#date_jz").val(),
			"PZ_DATE":$("#date_pz").val(),
			"HANDOVER":user+username	//交接人: 工号+名字
		},
		async: true,
		success: function (response) {
			if(response.code=='0'){
				//alert("操作成功，凭证号 " + response.MESSAGE);
            	$("#btnConfirm").val("退料确认");
            	$("#msg").html(response.MESSAGE);
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
    			$("#btnSearchData").click();
			}else{
				js.showMessage(response.msg);
            	$("#btnConfirm").val("退料确认");
			}
			$("#btnConfirm").removeAttr("disabled");
		}
	});
}

function fun_ShowTable(){
	$("#dataGrid").dataGrid({
		datatype: "local",
		data: mydata,
        colModel: col_list,
		formatCell:function(rowid, cellname, value, iRow, iCol){
			if(cellname=='LGORT'){
				var rec = $('#dataGrid').jqGrid('getRowData', rowid);
	            $('#dataGrid').jqGrid('setColProp', 'LGORT', { editoptions: {value:getLotOption(rec.WERKS,rec.PO_NO,rec.PO_ITEM_NO,'')}
	            });
			}
			if(cellname=='SOBKZ'){
				$('#dataGrid').jqGrid('setColProp', 'SOBKZ', { editoptions: {value:'K:K;Z:Z'}
	            });
			}
		},
		beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			check = false;
		},
		beforeSelectRow : function(rowid, e) { // 点击复选框才选中行
			var $myGrid = $(this), i = $.jgrid.getCellIndex($(e.target).closest('td')[0]), cm = $myGrid.jqGrid('getGridParam', 'colModel');
			if (cm[i].name == 'cb') {
				$('#dataGrid').jqGrid('setSelection', rowid);
			}
			return (cm[i].name == 'cb');
		},
		onSelectRow:function(rowid,status,e){
			//console.log("-->rowid : " + rowid);
		},
		afterSaveCell:function(rowid, cellname, value, iRow, iCol){
			var row =  $("#dataGrid").jqGrid('getRowData', rowid);
			var trs=$("#dataGrid").children("tbody").children("tr")
			if(cellname=='QTY1'){
				if(parseFloat(value) > parseFloat(row.RETURN_QTY)){
					alert("退货数量不能大于退货单中的退料数量！");
					$("#dataGrid").jqGrid('setCell', rowid, cellname, mydata[rowid-1].RETURN_QTY,'editable-cell');
					mydata[rowid-1].QTY1 = row.RETURN_QTY;
				}else{
					mydata[rowid-1].QTY1 = value;
					$.each(trs,function(index,tr){
						console.log(index + "|" + value)
						if(index === parseInt(rowid)){
							$(tr).find("td").eq(10).html(value)
						}
					})
				}
			}
			if(cellname=='BOXQTY'){
				console.log("-->BOXQTY : " + row.BOXQTY)
				if(value == '0'){
					alert("装箱数量不能为0！");
					mydata[rowid-1].BOXQTY = '1';
					$.each(trs,function(index,tr){
						if(index === parseInt(rowid)){
							$(tr).find("td").eq(11).html('1')
						}
					})
				}
			}
			if(cellname=='LIFNR'){
				console.log("-->LIFNR : " + value)
				mydata[rowid-1].LIKTX = value.trim();
				getLiktx()
			}
			if(cellname=='LGORT'){
				console.log("-->LGORT : " + value + "|rowid : " + rowid)
				
				$.each(trs,function(index,tr){
					if(index === parseInt(rowid)){
						if(value.substring(value.indexOf('|')+1) === '0'){
							//$("#dataGrid").jqGrid('setCell', rowid, 'SOBKZ', '', 'editable-cell');
							$(tr).find("td").eq(16).removeClass('not-editable-cell');
							mydata[rowid-1].SOBKZ = '';
							$(tr).find("td").eq(16).html('');
						}else{
							$(tr).find("td").eq(16).html(value.substring(value.indexOf('|')+1))
							mydata[rowid-1].SOBKZ = value.substring(value.indexOf('|')+1);
							$("#dataGrid").jqGrid('setCell', rowid, 'SOBKZ', '', 'not-editable-cell');
						}
						mydata[rowid-1].LGORT = value.substring(0,value.indexOf('|'));
						console.log("-->LGORT : " + mydata[rowid-1].LGORT)
					}
				})
			}
			check = true;
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

function getLiktx(){
	console.log("-->getLiktx")
	var trs=$("#dataGrid").children("tbody").children("tr");
	$.each(trs,function(index,tr){
		if(index > 0){
			$.ajax({
				url:baseUrl+'common/getVendorList',
				dataType : "json",
				type : "post",
				data : {
					"lifnr":$(tr).find("td").eq(14).html().trim(),
					"werks":$(tr).find("td").eq(2).html()
				},
				async: true,
				success: function (response) {
					$(tr).find("td").eq(15).html(response.list[0].NAME1)
					$(tr).find("td").eq(14).html(response.list[0].LIFNR)
				}
			})
		}
		
	})
}

function getLotOption(WERKS,PO_NO,PO_ITEM_NO,SOBKZ){
	var options="0:请选择;";
	var lgortList=getLgortList(WERKS,PO_NO,PO_ITEM_NO,SOBKZ);
	for(i=0;i<lgortList.length;i++){
			if(i != lgortList.length - 1) {
				options += lgortList[i].LGORT+'|'+lgortList[i].SOBKZ + ":" +lgortList[i].LGORT + ";";
			} else {
				options +=lgortList[i].LGORT+'|'+lgortList[i].SOBKZ + ":" + lgortList[i].LGORT;
			}
	}
	return options;
}

function flgort(){
	console.log("flgort");
	var lgort = "";var sobkz="";
	var tdIndex = 0;
	var trs=$("#dataGrid").children("tbody").children("tr");
	$.each(trs,function(index,tr){
		var cbx=$(tr).find("td").find("input").attr("type");
		if(cbx!=undefined){	
			var c_checkbox=$(tr).find('input[type=checkbox]');
			var ischecked=$(c_checkbox).is(":checked");
			if(ischecked){
				if(lgort == ""){
					lgort = $(tr).find("td").eq(12).html();
					sobkz = $(tr).find("td").eq(16).html();
					var che =$(tr).find("td").eq(12).find("input").attr("type");
					if(che!=undefined){	
						tdIndex = index;
					}else{
						return false;
					}
				}
			}
		}
	});
	if(lgort == ""){
		lgort = mydata[0].LGORT;
		sobkz = mydata[0].SOBKZ;
	}
	var trs=$("#dataGrid").children("tbody").children("tr");
	$.each(trs,function(index,tr){
		if(index >tdIndex){
			if("undefined" != typeof($(tr).attr("id"))){
				$(tr).find("td").eq(12).html(lgort);
				$(tr).find("td").eq(16).html(sobkz);
			}
			console.log("---->index = " + index + "|mydata.length = " + mydata.length)
			mydata[index-1].LGORT = lgort;
			mydata[index-1].SOBKZ = sobkz;
		}
	});
}

function getLgortList(WERKS,PO_NO,PO_ITEM_NO,SOBKZ){
	var list=[];
	$.ajax({
		url:baseUrl+'common/getLoList',
		type:'post',
		async:false,
		dataType:'json',
		data:{
			WERKS:WERKS,
			PO_NO:PO_NO,
			PO_ITEM_NO:PO_ITEM_NO,
			SOBKZ:SOBKZ,
			BAD_FLAG:'X'
		},
		success:function(resp){
			if(resp.msg=='success'){	
				list=resp.data;
			}
		}			
	})
	return list;
}

Array.prototype.remove=function(dx)
{
　if(isNaN(dx)||dx>this.length){return false;}
　for(var i=0,n=0;i<this.length;i++)
　{
　　　if(this[i]!=this[dx])
　　　{
　　　　　this[n++]=this[i]
　　　}
　}
　this.length-=1
}