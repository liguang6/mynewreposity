var pageNo = 1;
var totalPage;
var dataGridNum = 0;
var CONFIRM_QUANTITY = 0;
var OVERSTEP_REQ_FLAG;
$(function() {
	var now = new Date(); // 当前日期
	var pzDate = new Date(now.getTime());
	//初始化推荐表格
	$("#docDate").val(pzDate.Format("yyyy-MM-dd"));
	$("#debitDate").val(pzDate.Format("yyyy-MM-dd"));
	$("#dataGrid").jqGrid({
		url : baseUrl + "out/pda/xiaJiaJianPei/tuiJian",
		datatype : "local",
		mtype : "POST",
		jsonReader : jsonreader,
		colNames : ['单号', '推荐储位', '物料号', '批次', '数量', '' ],
		colModel : [{
			name : 'REFERENCE_DELIVERY_NO',
			index : 'REFERENCE_DELIVERY_NO',
			width : 100,
			align : 'center'
		}, {
			name : 'FROM_BIN_CODE',
			index : 'FROM_BIN_CODE',
			width : 60,
			align : 'center'
		}, {
			name : 'MATNR',
			index : 'MATNR',
			width : 70,
			align : 'center'
		}, {
			name : 'BATCH',
			index : 'BATCH',
			width : 70,
			align : 'center'
		}, {
			name : 'QUANTITY',
			index : 'QUANTITY',
			width : 63,
			align : 'center'
		}, {
			name : 'TASK_NUM',
			index : 'TASK_NUM',
			width : 63,
			hidden : true
		} ],
		width : 225,
		loadonce : true,
		multiselect : false,
		shrinkToFit : true,
		rowNum : 10,
		shrinkToFit : false,
		pager : "#pager"
	});
	//初始化条码暂存表格
	$("#dataGrid_barcode").jqGrid({
		datatype : "local",
		jsonReader : jsonreader,
		colNames : ['条码', '数量'],
		colModel : [{
			name : 'LABEL_NO',
			index : 'LABEL_NO',
			width : 130,
			align : 'center'		
		}, {
			name : 'BOX_QTY',
			index : 'BOX_QTY',
			width : 65,
			align : 'center'
		}],
		width : 225,
		loadonce : true,
		multiselect : true,
		shrinkToFit : true,
		rowNum : 10,
		shrinkToFit : false,
		pager : "#pager_barcode"
	});
})
/*
JGrid中获取GRID的所有数据的函数有 
$("#igrid").getRowData(); 获取所有的数据， 
$(“#gridTable”).jqGrid(‘getRowData’,rowId); 获取一行的数据 
$("#gridRakuData").getLocalRow(rowIds[row]); 获取不带html格式的数据 
除了通过getLocalRow的方法来解决html标签的问题，还可以我们自己写方法来解决
--没有格式化的数据（没有配置formatter）
获取jqgrid数据(array)
var idata = $("#igrid").getRowData();
alert(idata);
 将jqgrid之array数据转为json，并赋给隐藏域
$("#igrid_hidden").val($.parseJSON(idata)); 
--带有input格式化的grid
//获取所有id
var rowIds = $("#gridRakuData").jqGrid('getDataIDs');

//获取数据都添加到 gridData中
var gridData=new Array()
for(var row =0;row<rowIds.length;row++){
    var rowData = $("#gridRakuData").getLocalRow(rowIds[row]);
    gridData.push(rowData);
}

//格式化数据
var gridResult = {"dataList":gridData}

$("#rakuGridJsonData").val(JSON.stringify(gridResult));
 */

//获取表格所有数据(数组)$("#dataGrid_barcode").getRowData()或$("#dataGrid_barcode").jqGrid('getRowData')
//获取表格所有ID$("#dataGrid_barcode").jqGrid('getDataIDs');

/**
 * 校验条码重复扫描
 * @param barcode
 * @returns
 */
function jiaoyanchongfu(barcode){
	var rowIds = $("#dataGrid_barcode").jqGrid('getDataIDs');
	for (var row = 0; row < rowIds.length; row++) {
		var rowData = $("#dataGrid_barcode").getLocalRow(rowIds[row]);
		if(barcode === rowData.LABEL_NO){
			$("#divInfo").html("不能重复扫描。");
			document.getElementById("divInfo").style.visibility = "visible";
			return true;
		}
	}
}

/**
 * 推荐
 * @returns
 */
function tuiJian() {
	qkData();
	document.getElementById("divInfo").style.visibility = "hidden";
	if ($("#REQUIREMENT_NO").val() == "") {
		$("#divInfo").html("请输入需求号");
		document.getElementById("divInfo").style.visibility = "visible";
		return false;
	}
	$("#dataGrid").setGridParam({
		url : baseUrl + "out/pda/xiaJiaJianPei/tuiJian",
		datatype : "json",
		mtype : "POST",
		postData : {
			"REQUIREMENT_NO" : $("#REQUIREMENT_NO").val(),
			"DINGWEI_LABLE" : $("#DINGWEI_LABLE").val()
		},
		jsonReader : jsonreader,
		rowNum : 10
	}).trigger("reloadGrid");
}

/**
 * 删除数据表选中的行
 * @returns
 */
function del_shujubiao_row(){
	var rowIdsOriginal = $("#dataGrid_barcode").getGridParam('selarrrow');
	var rowIds = [];
	$.extend(rowIds, rowIdsOriginal);//克隆	 
	for(i in rowIds){
		var shuliang = $("#dataGrid_barcode").getLocalRow(rowIds[i]).BOX_QTY;
		CONFIRM_QUANTITY=CONFIRM_QUANTITY-shuliang;
		dataGridNum=dataGridNum - 1;
		$("#CONFIRM_QUANTITY").val(CONFIRM_QUANTITY);
	    $("#dataGrid_barcode").delRowData(rowIds[i]);
	}
	$("#dataGrid_barcode").trigger("reloadGrid");
	var barcode = $("#dataGrid_barcode").jqGrid("getRowData")[dataGridNum-1].LABEL_NO
	$("#SYGTM").html(barcode);
}
/**
 * 清空数据
 * @returns
 */
function qkData(){
	pageNo = 1;
	totalPage = 0;
	dataGridNum = 0;
	CONFIRM_QUANTITY = 0
	$("#CONFIRM_QUANTITY").val("");
	$("#SYGTM").html("");
	$("#dataGrid_barcode").jqGrid('clearGridData');
}

/**
 * 拣配下一个
 * @returns
 */
function xyg() {
	document.getElementById("divInfo").style.visibility = "hidden";
	document.getElementById("qr").removeAttribute("disabled");
	CONFIRM_QUANTITY=0;
	$("#CONFIRM_QUANTITY").val("");
	pageNo = pageNo + 1;
	dataGridNum = 0;
	ksxj();
	if (totalPage === pageNo) {
		document.getElementById("xyg").style.display = "none";
	}
}

/**
 * 开始下架
 * @returns
 */
function ksxj() {
	document.getElementById("qr").removeAttribute("disabled");
	$.ajax({
		url : baseUrl + "out/pda/xiaJiaJianPei/tuiJian",
		dataType : "json",
		type : "post",
		data : {
			"REQUIREMENT_NO" : $("#REQUIREMENT_NO").val(),
			"DINGWEI_LABLE" : $("#DINGWEI_LABLE").val(),
			"pageSize" : 1,
			"pageNo" : pageNo
		},
		success : function(resp) {
			if (resp.code == 0) {
				OVERSTEP_REQ_FLAG=resp.OVERSTEP_REQ_FLAG;
				$("#TASK_NUM").html(resp.page.list[0].TASK_NUM);
				$("#MATNR").html(resp.page.list[0].MATNR);
				$("#MAKTX").html(resp.page.list[0].MAKTX);
				$("#STORAGE_AREA_CODE").html(resp.page.list[0].FROM_BIN_CODE);
				$("#BATCH").html(resp.page.list[0].BATCH);
				$("#QUANTITY").html(resp.page.list[0].QUANTITY);
				$("#CONFIRM_QUANTITY").val(resp.page.list[0].CONFIRM_QUANTITY);
				CONFIRM_QUANTITY=resp.page.list[0].CONFIRM_QUANTITY;
				totalPage = resp.page.totalPage;
				if (totalPage === pageNo) {
					document.getElementById("xyg").style.display = "none";
				}
			} else {
				$("#divInfo").html(resp.msg);
				document.getElementById("divInfo").style.visibility = "visible";
			}
		}
	});

}

/**
 * 选中文本
 * @param v
 * @returns
 */
function select(v) {
	v.select();
}
/**
 * 确认
 * @returns
 */
function qr(){
	document.getElementById("qr").setAttribute("disabled", true);
	saveXiaJiaXinXi();
}
/**
 * 回到首页
 * @returns
 */
function sy(){
	window.location.href=baseUrl;
}
/**
 * 保存下架拣配信息
 * @returns
 */
function saveXiaJiaXinXi() {
	var QBJP = ($("#QUANTITY")[0].innerText==CONFIRM_QUANTITY);
	$
			.ajax({
				url : baseUrl + "out/pda/xiaJiaJianPei/saveXiaJiaXinXi",
				dataType : "json",
				type : "post",
				data : {
					"TASK_NUM" : $("#TASK_NUM")[0].innerText,
					"CONFIRM_QUANTITY" : CONFIRM_QUANTITY,
					"QBJP" : QBJP,
					"barcodes" : JSON.stringify($("#dataGrid_barcode").getRowData())
				},
				success : function(resp) {
					if (resp.code == 0) {
						$("#divInfo").html(resp.msg);
						document.getElementById("divInfo").style.visibility = "visible";
					} else {
						$("#divInfo").html(resp.msg);
						document.getElementById("divInfo").style.visibility = "visible";
					}
				}
			});
}

/**
 * 获取条码信息并暂存到条码表格
 * @returns
 */
function scanLabel(){
	$.ajax({
		url : baseUrl + "out/pda/xiaJiaJianPei/scanLabel",
		dataType : "json",
		type : "post",
		data : {
			"matnr" :$("#MATNR")[0].innerText,
			"batch" : $("#BATCH")[0].innerText,
			"bin_code" : $("#STORAGE_AREA_CODE")[0].innerText,
			"label_no" : $("#BARCODE").val()
		},
		success : function(resp) {
			if (resp.code == 0 && resp.page.count === undefined){
				document.getElementById("divInfo").style.visibility = "hidden";
				if(jiaoyanchongfu(resp.page.LABEL_NO)){
					$("#BARCODE").val("");
					return;
				}
				if(OVERSTEP_REQ_FLAG === "0" && CONFIRM_QUANTITY + resp.page.BOX_QTY > $("#QUANTITY")[0].innerText){
					$("#divInfo").html("不能超发，请先拆箱。");
					document.getElementById("divInfo").style.visibility = "visible";
					return;
				}
				$("#SYGTM").html(resp.page.LABEL_NO);
				$("#CONFIRM_QUANTITY").val(CONFIRM_QUANTITY + resp.page.BOX_QTY);				
				$("#BARCODE").val("");
    		    $("#dataGrid_barcode").jqGrid('addRowData',dataGridNum,resp.page);
    		    $("#dataGrid_barcode").setGridParam().trigger("reloadGrid");
    		    dataGridNum=dataGridNum + 1;
    		    CONFIRM_QUANTITY = CONFIRM_QUANTITY + resp.page.BOX_QTY;
			} else if(resp.code == 0 && resp.page.count === 0){
				$("#divInfo").html("此条码不可用");
				document.getElementById("divInfo").style.visibility = "visible";
				$("#BARCODE").val("");
			}
			else {
				$("#divInfo").html(resp.msg);
				document.getElementById("divInfo").style.visibility = "visible";
			}
		}
	});
}

/**
 * 按钮事件
 * @param v
 * @returns
 */
function changeBtn(v) {
	var page_1 = document.getElementById("page_1").style.display;
	var page_2 = document.getElementById("page_2").style.display;
	var page_3 = document.getElementById("page_3").style.display;
	var page_4 = document.getElementById("page_4").style.display;
	document.getElementById("divInfo").style.visibility = "hidden";
	if (v === undefined) {
		var v_xianshi=["rback","confirm","page_1"];
		var v_yincang=["cxgh","ksxj","bdwlqj","gg","xyg","qr","sjb","sc","page_2","page_3","page_4"];
		set_display("拣配",v_xianshi,v_yincang);
		return;
	}
	//推荐
	if (v.id === "tuiJian") {
		var v_xianshi=["rback","cxgh","ksxj","page_2"];
		var v_yincang=["confirm","bdwlqj","gg","xyg","qr","sjb","sc","page_1","page_3","page_4"];
		set_display("拣配下架顺序",v_xianshi,v_yincang);
		return;
	}
	//开始下架
	if (v.id === "ksxj") {
		var v_xianshi=["rback","bdwlqj","gg","xyg","qr","sjb","page_3"];
		var v_yincang=["confirm","cxgh","ksxj","page_1","sc","page_2","page_4"];
		set_display("拣配下架",v_xianshi,v_yincang);
		return;
	}
	//显示数据表
	if (v.id === "sjb") {	
		var v_xianshi=["rback","sc","page_4"];
		var v_yincang=["confirm","cxgh","ksxj","bdwlqj","gg","xyg","qr","sjb","page_1","page_2","page_3"];
		set_display("拣配下架",v_xianshi,v_yincang);
		return;
	}
	//点返回显示page_1
	if (v.id === "rback" && page_2 === "") {
		var v_xianshi=["rback","confirm","page_1"];
		var v_yincang=["cxgh","ksxj","bdwlqj","gg","xyg","qr","sjb","sc","page_2","page_3","page_4"];
		set_display("拣配",v_xianshi,v_yincang);
		return;
	}
	//点返回显示page_2
	if (v.id === "rback" && page_3 === "") {
		tuiJian();
		var v_xianshi=["rback","cxgh","ksxj","page_2"];
		var v_yincang=["confirm","bdwlqj","gg","xyg","qr","sjb","sc","page_1","page_3","page_4"];
		set_display("拣配下架顺序",v_xianshi,v_yincang);
		return;
	}
	//点返回显示page_3
	if (v.id === "rback" && page_4 === "") {
		var v_xianshi=["rback","bdwlqj","gg","xyg","qr","sjb","page_3"];
		var v_yincang=["confirm","cxgh","ksxj","sc","page_1","page_2","page_4"];
		set_display("拣配下架",v_xianshi,v_yincang);
		return;
	}
}

/**
 * 设置标题、元素的display属性
 * @param title 标题显示的信息
 * @param xian_shi 要显示的元素ID
 * @param yin_cang 要隐藏的元素ID
 * @returns
 */
function set_display(title,xian_shi,yin_cang){
	$("#title").html(title);
	for (var value of yin_cang) {
		document.getElementById(value).style.display = "none";
	}
	for (var value of xian_shi) {
		document.getElementById(value).style.display = "";
	}	
	
	if (totalPage === pageNo) {
		document.getElementById("xyg").style.display = "none";
	}
}

/**
 * 扫描条码后执行
 */
document.onkeydown = function() {
	if (window.event.keyCode == 13 && document.activeElement.id == 'BARCODE') {
		scanLabel();
	}
	if (window.event.keyCode == 118) {
		history.back(-1);
	}
}
// ----------------------------------

// $.ajax({
// url: baseUrl + "out/xiaJiaJianPei/tuiJian",
// dataType : "json",
// type : "post",
// data : {
// "REQUIREMENT_NO":$("#REQUIREMENT_NO").val(),
// "DINGWEI_LABLE":$("#DINGWEI_LABLE").val()
// },
// success:function(resp){
// if(resp.code == 0){
// $("#barcode").val("");
// $("#boxlabel").html(resp.boxs);
// $("#barcodelabel").html(resp.result[0].LABEL_NO);
// $("#matnrlabel").html(resp.result[0].MATNR);
// $("#maktxlabel").html(resp.result[0].MAKTX);
// $("#qtylabel").html(resp.result[0].BOX_QTY);
// $("#batchlabel").html(resp.result[0].BATCH);
// document.getElementById("div2").style.display="";
// document.getElementById("divInfo").style.visibility="hidden";
// $(".btn").attr("disabled", false);
// } else {
// $("#divInfo").html(resp.msg);
// document.getElementById("divInfo").style.visibility="visible";
// $(".btn").attr("disabled", false);
// }
// }
// });
// function postDiv() {
// document.getElementById("div1").style.display = "none";
// document.getElementById("div2").style.display = "none";
// document.getElementById("div3").style.display = "none";
// document.getElementById("div4").style.display = "none";
// document.getElementById("div5").style.display = "";
// document.getElementById("div6").style.display = "none";
//
// document.getElementById("btn1").style.display = "none";
// document.getElementById("btn3").style.display = "none";
// document.getElementById("btn4").style.display = "";
// document.getElementById("btn5").style.display = "none";
// document.getElementById("btn6").style.display = "none";
// document.getElementById("btn7").style.display = "";
// }
//
// // ------删除操作---------
// $("#btn5")
// .click(
// function() {
// var gr = $("#dataGrid1").jqGrid('getGridParam',
// 'selarrrow');// 获取选中的行号
// var ids = "";
// var selectedRows = [];
// var docNo;
// var docItem;
// $.each(gr, function(i, rowid) {
// ids = ids + rowid + ",";
// var row = $("#dataGrid1").jqGrid("getRowData",
// rowid);
// docNo = row.REF_DOC_NO;
// docItem = row.REF_DOC_ITEM;
// selectedRows.push(row);
// })
//
// if (ids != "") {
// document.getElementById("divInfo").style.visibility = "hidden";
// $
// .ajax({
// type : "POST",
// url : baseUrl
// + "in/autoPutaway/deleteLabel",
// async : false,
// dataType : "json",
// data : {
// deleteList : JSON
// .stringify(selectedRows)
// },
// success : function(r) {
// if (r.code == 0) {
// $("#divInfo").html("删除成功");
// $("#dataGrid1").jqGrid(
// "clearGridData");
// $("#dataGrid1")
// .setGridParam(
// {
// url : baseUrl
// + "in/autoPutaway/labelItem",
// datatype : "json",
// mtype : "POST",
// postData : {
// "docNo" : docNo,
// "docItem" : docItem
// },
// jsonReader : jsonreader,
// rowNum : 3
// }).trigger(
// "reloadGrid");
// } else {
// $("#divInfo").html("删除成功");
//
// }
// document.getElementById("divInfo").style.visibility = "visible";
// }
// });
// } else {
// $("#divInfo").html("请选择条码");
// document.getElementById("divInfo").style.visibility = "visible";
// return false;
// }
// });
// });
//
// function docItem() {
// document.getElementById("div1").style.display = "none";
// document.getElementById("div2").style.display = "none";
// document.getElementById("div3").style.display = "";
// document.getElementById("div4").style.display = "none";
// document.getElementById("div5").style.display = "none";
// document.getElementById("div6").style.display = "none";
//
// document.getElementById("btn1").style.display = "none";
// document.getElementById("btn3").style.display = "none";
// document.getElementById("btn4").style.display = "";
// document.getElementById("btn5").style.display = "none";
// document.getElementById("btn6").style.display = "none";
// document.getElementById("btn7").style.display = "none";
//
// $("#dataGrid").jqGrid("clearGridData");
// $("#dataGrid").setGridParam({
// url : baseUrl + "in/autoPutaway/docItem",
// datatype : "json",
// mtype : "POST",
// jsonReader : jsonreader
// }).trigger("reloadGrid");
//
// }
//
// function labelItem(docNo, docItem) {
// document.getElementById("div1").style.display = "none";
// document.getElementById("div2").style.display = "none";
// document.getElementById("div3").style.display = "none";
// document.getElementById("div4").style.display = "";
// document.getElementById("div5").style.display = "none";
// document.getElementById("div6").style.display = "none";
//
// document.getElementById("btn1").style.display = "none";
// document.getElementById("btn3").style.display = "none";
// document.getElementById("btn4").style.display = "none";
// document.getElementById("btn5").style.display = "";
// document.getElementById("btn6").style.display = "";
// document.getElementById("btn7").style.display = "none";
//
// $("#dataGrid1").jqGrid("clearGridData");
// $("#dataGrid1").setGridParam({
// url : baseUrl + "in/autoPutaway/labelItem",
// datatype : "json",
// mtype : "POST",
// postData : {
// "docNo" : docNo,
// "docItem" : docItem
// },
// jsonReader : jsonreader,
// rowNum : 10
// }).trigger("reloadGrid");
//
// }
// document.onkeydown = function() {
//
// if (window.event.keyCode == 13 && document.activeElement.id == 'barcode') {
// scanPre();
// }
// if (window.event.keyCode == 118) {
// history.back(-1);
// }
// }
