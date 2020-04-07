var mydata = [{ id: "1",MAKTX: ""},{ id: "2",MAKTX: ""},{ id: "3",MAKTX: ""},{ id: "4",MAKTX: ""},{ id: "5",MAKTX: ""}];
var mydata2 = [{ id: "1",MAKTX: ""},{ id: "2",MAKTX: ""}];		//初始化批量订单数据
var mydata_mat = [{ id: "1",MAKTX: ""},{ id: "2",MAKTX: ""}];	//初始化批量物料数据
var mydata_sap = [{ id: "1",MAKTX: ""},{ id: "2",MAKTX: ""}];	//初始化批量SAP交货单数据
var mydata_pos = [{ id: "1",MAKTX: ""},{ id: "2",MAKTX: ""}];	//初始化批量采购订单数据
var trindex = 6;
var searchDataNum = 0;
var check = true;
var lastrow,lastcell;  
var vm = new Vue({
	el : '#rrapp',
	data:{warehourse:[],
		lgort:'',
		lgortlist:[]},
	created:function(){
		this.lgortList = getLgortList($("#werks").val())
	},
	methods : {
		refresh : function() {
			console.log('-->refresh')
			$('#dataGrid').trigger('reloadGrid');
			searchDataNum=0;
			switchLabel();
		},
		onTypeChange:function(event) {
			mydata = [{ id: "1",orderNo: ""},{ id: "2",orderNo: ""},{ id: "3",orderNo: ""},{ id: "4",orderNo: ""},{ id: "5",orderNo: ""}];
			trindex = 6;
			searchDataNum = 0;
			$("#dataGrid").jqGrid('GridUnload');
			$("#orders").val("");
			$("#matnrs").val("");
			$("#in_order").val("");
			$("#costcenter").val("");
			$("#wbs").val("");
			$("#vendor").val("");
			switchLabel()
			dataGridPaste();
		},
		getSapCostCenterInfo:function(event) {
			$.ajax({
				url:baseURL+"workshopReturn/getCostCenterInfo",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":$("#werks").val(),
					"COSTCENTER":$("#costcenter").val(),
				},
				async: true,
				success: function (response) { 
					if(response.result.MESSAGE == undefined){
						$("#re_werks").val(response.result.COMP_CODE)

						//是否在有效期内
						var now = formatDate(new Date()).replace(/\-/g, "");							
						if(response.result.VALID_FROM.replace(/\-/g, "") <= now && response.result.VALID_TO.replace(/\-/g, "") >= now){
							$("#costcenterInfo").html(response.result.DESCRIPT);
						}else{
							alert("成本中心"+$("#costcenter").val()+"不在有效期内!");
						}
					}else{
						alert(response.result.MESSAGE);
						$("#costcenterInfo").html("回车获取成本中心信息");
					}
				}
			});
		},
		getSapWbsInfo:function(event) {
			$.ajax({
				url:baseURL+"workshopReturn/getWbsInfo",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":$("#werks").val(),
					"WBS":$("#wbs").val(),
				},
				async: true,
				success: function (response) { 
					if(response.result.MESSAGE == undefined){
						$("#re_werks").val(response.result.COMP_CODE)
						$("#wbsInfo").html(response.result.DESCRIPTION);
						/*if($("#re_werks").val() != response.result.COMP_CODE){
							alert("该WBS元素为" + response.result.COMP_CODE);
						}else{
							$("#wbsInfo").html(response.result.DESCRIPTION);
						}*/
					}else{
						alert(response.result.MESSAGE);
						$("#wbsInfo").html("回车获取WBS元素信息");
					}
				}
			});
		},
		getSapVendorInfo:function(event) {
			$.ajax({
				url:baseURL+"workshopReturn/getSapVendorInfo",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":$("#werks").val(),
					"LIFNR":$("#vendor").val(),
				},
				async: true,
				success: function (response) { 
					if(response.result.length == 0){
						alert("获取委外单位信息失败！");
						$("#vendorInfo").html("回车获取委外单位信息");
					}else{
						$("#vendorInfo").html(response.result[0].NAME1);
					}
				}
			});
		},
		getSapInOrderInfo:function(event) {
			$.ajax({
				url:baseURL+"workshopReturn/getSapInOrderInfo",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":$("#werks").val(),
					"ORDER":$("#in_order").val(),
				},
				async: true,
				success: function (response) { 
					if(response.result.MESSAGE == undefined){
						if($("#werks").val() != response.result.WERKS){
							alert("该订单所属工厂为" + response.result.WERKS);
						}else{
							//判断内部订单状态， PHAS1=X且LOEKZ≠X为有效的可操作订单，否则系统报错：“订单*********不允许货物移动”。							
							if(!(response.result.PHAS1 == "X" && response.result.LOEKZ == "")){
								alert("订单"+$("#in_order").val()+"不允许货物移动");
							}else{
								$("#inOrderInfo").html(response.result.KTEXT);
							}
							//$("#inOrderInfo").html(response.result.KTEXT);
						}
					}else{
						alert(response.result.MESSAGE);
						$("#inOrderInfo").html("回车获取内部订单信息");
					}
				}
			});
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
			getBusinessNameList();
			this.lgortList = getLgortList(werks)
		},
		onRePlantChange: function(event) {
			console.log("-->onRePlantChange");
		}
	}
});

$(function(){
	$("#return_no").focus();
	$("#div_kuwei").hide();
	$("#div_nbdd").hide();
	$("#div_cbzx").hide();
	$("#div_wbs").hide();
	$("#div_vendor").hide();
	$("#div_sapOrders").hide();
	$("#div_pono").hide();
	$("#div_thgc").hide();
	$("#div_reWerks").hide();
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
	
	getBusinessNameList();
	
	fun_ShowTable();
	
	$("#btnSearchData").click(function () {	
		console.log("-->b_type=" + $("#re_type").find('option:selected').attr('b_type'));		
		
		if("35" == $("#re_type").val() || "36" == $("#re_type").val()){
			if("" == $("#orders").val()){
				alert("生产订单不能为空！");
				return false;
			}
		}else if("37" == $("#re_type").val()){
			if("回车获取内部订单信息" == $("#inOrderInfo").html()){
				alert("请输入正确的内部订单号并回车确认描述信息！");
				return false;
			}
		}else if("38" == $("#re_type").val()){
			if("回车获取成本中心信息" == $("#costcenterInfo").html()){
				alert("请输入正确的成本中心号并回车确认描述信息！");
				return false;
			}
		}else if("39" == $("#re_type").val()){
			if("回车获取WBS元素信息" == $("#wbsInfo").html()){
				alert("请输入正确的WBS元素号并回车确认描述信息！");
				return false;
			}
		}else if("69" == $("#re_type").val()){
			if("回车获取委外单位信息" == $("#vendorInfo").html()){
				alert("请输入正确的委外单位号并回车确认描述信息！");
				return false;
			}
		}else if("70" == $("#re_type").val()){
			if("" == $("#sapOrders").val()){
				alert("SAP交货单不能为空！");
				return false;
			}
		}else if("71" == $("#re_type").val()){
			if("" == $("#sapOrders").val()){
				alert("SAP交货单不能为空！");
				return false;
			}
		}else if("72" == $("#re_type").val() || "68" == $("#re_type").val()){
			if("" == $("#ponos").val()){
				alert("采购订单不能为空！");
				return false;
			}
		}
		
		$("#btnSearchData").val("查询中...");
		$("#btnSearchData").attr("disabled","disabled");		
		$.ajax({
			url:baseURL+"workshopReturn/getWorkshopReturnData",
			dataType : "json",
			type : "post",
			data : {
				"WERKS":$("#werks").val(),
				"F_WERKS":$("#t_werks").val(),
				"WH_NUMBER":$("#wh").val(),
				"BUSINESS_NAME":$("#re_type").val(),
				"ORDERS":$("#orders").val(),
				"SAP_ORDERS":$("#sapOrders").val(),
				"IN_ORDER":$("#in_order").val(),
				"PO_ORDER":$("#ponos").val(),
				"COSTCENTER":$("#costcenter").val(),
				"WBS":$("#wbs").val(),
				"LIFNR":$("#vendor").val(),
				"LGORT":$("#LGORT").val(),
				"MATNRS":$("#matnrs").val(),
			},
			async: true,
			success: function (response) { 
				$("#dataGrid").jqGrid("clearGridData", true);
				mydata.length=0;
				if(response.code == "0"){
					mydata = response.result;
					if(mydata.length > 0){
						if(0 == mydata[0].PC_COUNT){
							$("#btnConfirm").attr("disabled","disabled");
							alert($("#werks").val() + "工厂未配置收料房退货业务类型！")
						}
					}
					searchDataNum = response.result.length;
					trindex = response.result.length+1;
					$("#dataGrid").jqGrid('GridUnload');	
					//if("35" == $("#re_type").val() || "36" == $("#re_type").val())fun_ShowTable();
					if("35" == $("#re_type").val())fun_ShowTable();
					if("36" == $("#re_type").val())fun_ShowTable36();
					if("37" == $("#re_type").val())fun_ShowTable37();
					if("38" == $("#re_type").val())fun_ShowTable37();
					if("39" == $("#re_type").val())fun_ShowTable37();
					if("40" == $("#re_type").val())fun_ShowTable40();
					if("68" == $("#re_type").val())fun_ShowTable68();
					if("69" == $("#re_type").val())fun_ShowTable68();
					if("70" == $("#re_type").val())fun_ShowTable70();
					if("71" == $("#re_type").val())fun_ShowTable70();
					if("72" == $("#re_type").val())fun_ShowTable68();
					if("46" == $("#re_type").val())fun_ShowTable37();
				}else{
					js.showMessage(response.msg)
				}
				$("#btnSearchData").val("查询");
				$("#btnSearchData").removeAttr("disabled");
				dataGridPaste();
			}
		});
	});
	
	$("#btnMore").click(function () {
		fun_ShowMoreTable();
		dataGridMorePaste();
		var orders = "";
		layer.open({
    		type : 1,
    		offset : '50px',
    		title : "批量输入订单号：",
    		area : [ '500px', '350px' ],
    		shadeClose : false,
    		content : jQuery("#moreLayer"),
    		btn : [ '确定'],
    		btn1 : function(index) {
    			layer.close(index);
    			var trs=$("#dataGrid_1").children("tbody").children("tr");
    			$.each(trs,function(index,tr){
    				if(index>0&&($(tr).find("td").eq(1).text()!=" "&&$(tr).find("td").eq(1).text()!=""))orders += $(tr).find("td").eq(1).text() + ",";
    			});
    			$("#orders").val(orders.substring(0,orders.length-1));
    		}
    	});
	});
	$("#btnMore2").click(function () {
		fun_ShowMoreTable2();
		dataGridMorePaste2();
		var mats = "";
		layer.open({
    		type : 1,
    		offset : '50px',
    		title : "批量输入物料号：",
    		area : [ '500px', '350px' ],
    		shadeClose : false,
    		content : jQuery("#moreLayer2"),
    		btn : [ '确定'],
    		btn1 : function(index) {
    			layer.close(index);
    			var trs=$("#dataGrid_2").children("tbody").children("tr");
    			$.each(trs,function(index,tr){
    				console.log("==>" + $(tr).find("td").eq(1).text() + "<==");
    				if(index>0&&($(tr).find("td").eq(1).text()!=" "&&$(tr).find("td").eq(1).text()!=""))mats += $(tr).find("td").eq(1).text() + ",";
    			});
    			$("#matnrs").val(mats.substring(0,mats.length-1));
    		}
    	});
	});
	$("#btnSapOrderMore").click(function () {
		fun_ShowMoreTable3();
		dataGridMorePaste3();
		var saps = "";
		layer.open({
    		type : 1,
    		offset : '50px',
    		title : "批量输入SAP交货单：",
    		area : [ '500px', '350px' ],
    		shadeClose : false,
    		content : jQuery("#moreLayer3"),
    		btn : [ '确定'],
    		btn1 : function(index) {
    			layer.close(index);
    			var trs=$("#dataGrid_3").children("tbody").children("tr");
    			$.each(trs,function(index,tr){
    				console.log("==>" + $(tr).find("td").eq(1).text() + "<==");
    				if(index>0&&($(tr).find("td").eq(1).text()!=" "&&$(tr).find("td").eq(1).text()!=""))saps += $(tr).find("td").eq(1).text() + ",";
    			});
    			$("#sapOrders").val(saps.substring(0,saps.length-1));
    		}
    	});
	});
	$("#btnPonoMore").click(function () {
		fun_ShowMoreTable4();
		dataGridMorePaste4();
		var pos = "";
		layer.open({
    		type : 1,
    		offset : '50px',
    		title : "批量输入采购订单号：",
    		area : [ '500px', '350px' ],
    		shadeClose : false,
    		content : jQuery("#moreLayer4"),
    		btn : [ '确定'],
    		btn1 : function(index) {
    			layer.close(index);
    			var trs=$("#dataGrid_4").children("tbody").children("tr");
    			$.each(trs,function(index,tr){
    				console.log("==>" + $(tr).find("td").eq(1).text() + "<==");
    				if(index>0&&($(tr).find("td").eq(1).text()!=" "&&$(tr).find("td").eq(1).text()!=""))pos += $(tr).find("td").eq(1).text() + ",";
    			});
    			$("#ponos").val(pos.substring(0,pos.length-1));
    		}
    	});
	});
	
	$("#newOperation").click(function () {		
		mydata.push([{ id: trindex,MAKTX: ""}])
		var addtr = '<tr id = "'+trindex+'" role="row" tabindex="-1" class="ui-widget-content jqgrow ui-row-ltr '+((trindex%2 == 0)?"ui-priority-secondary":"")+'" aria-selected = "false">'+
		'<td role="gridcell" class="ui-state-default jqgrid-rownum" style="text-align:center;" title="2" aria-describedby="dataGrid_rn">' + trindex + 
		'</td><td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_cb"><input role="checkbox" type="checkbox" id="jqg_dataGrid_' + trindex +
		'" class="cbox noselect2" name="jqg_dataGrid_' + trindex +'"></td>'+
		'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_"></td>'+
		'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_"></td>'+
		'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_"></td>'+
		'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_"></td>'+
		'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_"></td>'+
		'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_"></td>'+
		'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_"></td>'+
		'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_"></td>'+
		'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_"></td>'+
		'<td role="gridcell" style="text-align:center;" title="" aria-describedby="dataGrid_werks">'+
		'</td><td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_"></td></tr>'
		$("#dataGrid tbody").append(addtr);
		trindex++ ;
	});
	$("#btn_delete").click(function () {
		var trs=$("#dataGrid").children("tbody").children("tr");
		var reIndex = 1;
		$.each(trs,function(index,tr){
			var td = $(tr).find("td").eq(0);
			var td_check = $(tr).find("td").eq(1).find("input");
			if(index > 0){
				var ischecked=$(td_check).is(":checked");
				if(ischecked){
					console.log(index);
					if(index <= searchDataNum)searchDataNum--;
					tr.remove();
				}else{
					td.text(reIndex);
					$(tr).attr('id',reIndex);
					$(td_check).attr('name','jqg_dataGrid_' + reIndex);
					$(td_check).attr('id','jqg_dataGrid_' + reIndex);
					reIndex++;
				}
			}
		});
		trindex = reIndex;
	});
	$("#newOperation_1").click(function () {
		var trMoreindex = $("#dataGrid_1").children("tbody").children("tr").length;
		mydata2.push({ id: trMoreindex,MAKTX: ""});
		var addtr = '<tr id = "'+trMoreindex+'"  role="row" tabindex="-1" class="ui-widget-content jqgrow jqgrow ui-row-ltr '+((trMoreindex%2 == 0)?"ui-priority-secondary":"")+'" aria-selected = "false">'+
		'<td role="gridcell" class="ui-state-default jqgrid-rownum" style="text-align:center;" title="2" aria-describedby="dataGrid_rn">' + trMoreindex + 
		'</td><td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_1_"></td>'+
		'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_1_"><i title="删除" aria-hidden="true" onclick="$(this).parent().parent().remove();delMoreTr();" class="delMoreTr fa fa-times"></i></td><td></td></tr>'
		$("#dataGrid_1 tbody").append(addtr);
	});
	$("#newOperation_2").click(function () {
		var trMoreindex = $("#dataGrid_2").children("tbody").children("tr").length;
		mydata_mat.push({ id: trMoreindex,MAKTX: ""});
		var addtr = '<tr id = "'+trMoreindex+'"  role="row" tabindex="-1" class="ui-widget-content jqgrow jqgrow ui-row-ltr '+((trMoreindex%2 == 0)?"ui-priority-secondary":"")+'" aria-selected = "false">'+
		'<td role="gridcell" class="ui-state-default jqgrid-rownum" style="text-align:center;" title="2" aria-describedby="dataGrid_rn">' + trMoreindex + 
		'</td><td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_1_"></td>'+
		'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_1_"><i title="删除" aria-hidden="true" onclick="$(this).parent().parent().remove();delMoreTr2();" class="delMoreTr fa fa-times"></i></td><td></td></tr>'
		$("#dataGrid_2 tbody").append(addtr);
	});
	$("#newOperation_3").click(function () {
		var trMoreindex = $("#dataGrid_3").children("tbody").children("tr").length;
		mydata_sap.push({ id: trMoreindex,MAKTX: ""});
		var addtr = '<tr id = "'+trMoreindex+'"  role="row" tabindex="-1" class="ui-widget-content jqgrow jqgrow ui-row-ltr '+((trMoreindex%2 == 0)?"ui-priority-secondary":"")+'" aria-selected = "false">'+
		'<td role="gridcell" class="ui-state-default jqgrid-rownum" style="text-align:center;" title="2" aria-describedby="dataGrid_rn">' + trMoreindex + 
		'</td><td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_1_"></td>'+
		'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_1_"><i title="删除" aria-hidden="true" onclick="$(this).parent().parent().remove();delMoreTr3();" class="delMoreTr fa fa-times"></i></td><td></td></tr>'
		$("#dataGrid_3 tbody").append(addtr);
	});
	$("#newOperation_4").click(function () {
		var trMoreindex = $("#dataGrid_4").children("tbody").children("tr").length;
		mydata_pos.push({ id: trMoreindex,MAKTX: ""});
		var addtr = '<tr id = "'+trMoreindex+'"  role="row" tabindex="-1" class="ui-widget-content jqgrow jqgrow ui-row-ltr '+((trMoreindex%2 == 0)?"ui-priority-secondary":"")+'" aria-selected = "false">'+
		'<td role="gridcell" class="ui-state-default jqgrid-rownum" style="text-align:center;" title="2" aria-describedby="dataGrid_rn">' + trMoreindex + 
		'</td><td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_1_"></td>'+
		'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_1_"><i title="删除" aria-hidden="true" onclick="$(this).parent().parent().remove();delMoreTr4();" class="delMoreTr fa fa-times"></i></td><td></td></tr>'
		$("#dataGrid_4 tbody").append(addtr);
	});
	
	$("#newReset_1").click(function () {
		mydata2 = [{ id: "1",MAKTX: ""},{ id: "2",MAKTX: ""}]
		$("#dataGrid_1").jqGrid('GridUnload');
		fun_ShowMoreTable();
		dataGridMorePaste();
	});
	$("#newReset_2").click(function () {
		mydata_mat = [{ id: "1",MAKTX: ""},{ id: "2",MAKTX: ""}]
		$("#dataGrid_2").jqGrid('GridUnload');
		fun_ShowMoreTable2();
		dataGridMorePaste2();
	});
	$("#newReset_3").click(function () {
		mydata_sap = [{ id: "1",MAKTX: ""},{ id: "2",MAKTX: ""}]
		$("#dataGrid_3").jqGrid('GridUnload');
		fun_ShowMoreTable3();
		dataGridMorePaste3();
	});
	$("#newReset_4").click(function () {
		mydata_pos = [{ id: "1",MAKTX: ""},{ id: "2",MAKTX: ""}]
		$("#dataGrid_4").jqGrid('GridUnload');
		fun_ShowMoreTable4();
		dataGridMorePaste4();
	});
	
	$("#btnReset").click(function () {
		mydata = [{ id: "1",orderNo: ""},{ id: "2",orderNo: ""},{ id: "3",orderNo: ""},{ id: "4",orderNo: ""},{ id: "5",orderNo: ""}];
		trindex = 6;
		searchDataNum = 0;
		$("#dataGrid").jqGrid('GridUnload');
		$("#orders").val("");
		$("#matnrs").val("");
		//fun_ShowTable();
		switchLabel();
		dataGridPaste();
	});
	
	dataGridPaste();
	
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
				console.log("-->QTY1=" + arrList[i].QTY1);
				if((" " == arrList[i].KT_QTY) || (" " == arrList[i].QTY1)|| ("" == arrList[i].QTY1)|| ("" == arrList[i].QTY1)){
					alert("勾选的行项目数据填写不正确！");
					return false;
				}
				
				//不能勾选重复的项目
				if("40" == $("#re_type").val()){
					for (var j = i+1; j < ids.length; j++) {
						var arrList2 = new Array();
						arrList2[0] = $("#dataGrid").jqGrid('getRowData',ids[j]);
						if(arrList[i].REQUIREMENT_NO + arrList[i].REQUIREMENT_ITEM_NO + arrList[i].MATNR
								== arrList2[0].REQUIREMENT_NO + arrList2[0].REQUIREMENT_ITEM_NO + arrList2[0].MATNR){						
							alert("不能勾选重复的项目！");
							return false;
						}
					}
				} else {
					console.log(arrList[i].AUFNR + arrList[i].RSNUM + arrList[i].RSPOS + arrList[i].MATNR);
					for (var j = i+1; j < ids.length; j++) {
						var arrList2 = new Array();
						arrList2[0] = $("#dataGrid").jqGrid('getRowData',ids[j]);
						if(arrList[i].AUFNR + arrList[i].RSNUM + arrList[i].RSPOS + arrList[i].MATNR
								== arrList2[0].AUFNR + arrList2[0].RSNUM + arrList2[0].RSPOS + arrList2[0].MATNR){						
							alert("不能勾选重复的项目！");
							return false;
						}
					}
				}
				
			}
			
			console.log(arrList);
			
			$("#btnConfirm").val("正在操作,请稍候...");	
			$("#btnConfirm").attr("disabled","disabled");
			var BUSINESS_TYPE = $("#re_type").find('option:selected').attr('b_type');
			var F_WERKS = $("#werks").val();
			if("46" === $("#re_type").val()){
				F_WERKS = $("#t_werks").val();
			}
			$.ajax({
				url:baseURL+"workshopReturn/createWorkshopReturn",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":$("#werks").val(),
					"F_WERKS":F_WERKS,
					"WH_NUMBER":$("#wh").val(),
					"ORDERS":$("#orders").val(),
					"SAP_ORDERS":$("#sapOrders").val(),
					"IN_ORDER":$("#in_order").val(),
					"PO_ORDER":$("#ponos").val(),
					"COST_CENTER":$("#costcenter").val(),
					"WBS":$("#wbs").val(),
					"LIFNR":$("#vendor").val(),
					"LGORT":$("#LGORT").val(),
					"MATNRS":$("#matnrs").val(),
					"RE_TYPE":$("#re_type").val(),
					"BUSINESS_TYPE":BUSINESS_TYPE,
					"BUSINESS_NAME":$("#re_type").val(),
					"ARRLIST":JSON.stringify(arrList)
				},
				async: true,
				success: function (response) {
					if(response.code=='0'){
						//alert("操作成功，凭证号 " + response.outNo);
		            	$("#btnConfirm").val("创建退料单");
		            	$("#outNo").html(response.outNo);
		            	layer.open({
		            		type : 1,
		            		offset : '50px',
		            		skin : 'layui-layer-molv',
		            		title : "退货单创建成功",
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
		            	$("#dataGrid").jqGrid("clearGridData", true);
		            	mydata.length=0;		//BUG#1211 创建退料单成功后，清空列表的数据
					}else{
						js.showMessage(response.msg);
		            	$("#btnConfirm").val("创建退料单");
					}
					//$("#btnSearchData").click();
					$("#btnConfirm").removeAttr("disabled");
				}
			});
			
		}
		check = true;
	});

	$("#btnPrint1").click(function () {
		window.open(baseURL+"docPrint/workshopReturnOutPrint?PageSize=0&outNo=" + document.getElementById("outNo").innerHTML);
	});
	$("#btnPrint2").click(function () {
		window.open(baseURL+"docPrint/workshopReturnOutPrint?PageSize=1&outNo=" + document.getElementById("outNo").innerHTML);
	});

	$(document).bind("click",function(e){
		if("searchForm" == $(e.target).attr("id")||"ui-jqgrid-bdiv" == $(e.target).attr("class")){
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
		}
	});
	
});

function getBusinessNameList(){
	$.ajax({
		url:baseURL+"workshopReturn/getBusinessNameListByWerks",
		dataType : "json",
		type : "post",
		data : {
			"WERKS":$("#werks").val()
		},
		async: true,
		success: function (response) { 
			$("#re_type").empty();
			var strs = "";
		    $.each(response.result, function(index, value) {
		    	strs += "<option b_type="+ value.BUSINESS_TYPE +" b_class="+ value.BUSINESS_CLASS +" value=" + value.BUSINESS_NAME + ">" + value.VALUE + "</option>";
		    });
		    $("#re_type").append(strs);
		    switchLabel()
		}
	});
}

function delMoreTr(){
	var trs=$("#dataGrid_1").children("tbody").children("tr");
	var reIndex = 1;
	$.each(trs,function(index,tr){
		var td = $(tr).find("td").eq(0);
		if(index > 0){
			td.text(reIndex);
			$(tr).attr('id',reIndex);
			reIndex++;
		}
	});
}
function delMoreTr2(){
	var trs=$("#dataGrid_2").children("tbody").children("tr");
	var reIndex = 1;
	$.each(trs,function(index,tr){
		var td = $(tr).find("td").eq(0);
		if(index > 0){
			td.text(reIndex);
			$(tr).attr('id',reIndex);
			reIndex++;
		}
	});
}
function delMoreTr3(){
	var trs=$("#dataGrid_3").children("tbody").children("tr");
	var reIndex = 1;
	$.each(trs,function(index,tr){
		var td = $(tr).find("td").eq(0);
		if(index > 0){
			td.text(reIndex);
			$(tr).attr('id',reIndex);
			reIndex++;
		}
	});
}
function delMoreTr4(){
	var trs=$("#dataGrid_4").children("tbody").children("tr");
	var reIndex = 1;
	$.each(trs,function(index,tr){
		var td = $(tr).find("td").eq(0);
		if(index > 0){
			td.text(reIndex);
			$(tr).attr('id',reIndex);
			reIndex++;
		}
	});
}

function dataGridPaste(){
	$('#dataGrid').bind('paste', function(e) {
		console.log("-->paste" + $("#re_type").val());
		e.preventDefault(); // 消除默认粘贴
		// 获取粘贴板数据
		var data = null;
		var clipboardData = window.clipboardData || e.originalEvent.clipboardData; // IE || chrome
		data = clipboardData.getData('Text');
		// console.log(data.replace(/\t/g, '\\t').replace(/\n/g,'\\n')); //data转码
		// 解析数据
		var arr = data.split('\n').filter(function(item) { // 兼容Excel行末\n，防止出现多余空行
			return (item !== "")
		}).map(function(item) {
			return item.split("\t");
		});
		// 输出至网页表格
		var tab = this; // 表格DOM
		var td = $(e.target).parents('td');
		var startRow = td.parents('tr')[0].rowIndex;
		var startCell = td[0].cellIndex;
		var rows = tab.rows.length; // 总行数
		
		//console.log(arr.length + "|" + arr[0].length)
		if(arr.length === 1 && arr[0].length === 1){	//单个复制
			var cell = tab.rows[startRow].cells[startCell];
			$(cell).find(':text').val(arr[0][0]); //找到cell下的input:text，设置value
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			$(cell).html(arr[0][0]);
		}

		for (var i = 0; i < arr.length; i++) {
			if(startRow + i >= rows){
				$("#newOperation").click();
			}
			var cells = tab.rows[startRow + i].cells.length; // 该行总列数
			for (var j = 0; j < arr[i].length && startCell + j < cells; j++) {
				var cellNum = startCell + j
				console.log('cellNum = ' + cellNum)
				//BUG 1484  支持从EXCEL，复制料号和数量，从物料主数据带出物料描述和单位，添加行项目和粘贴填入的数据不需要校验前期需求号领料数据。
				if("40" === $("#re_type").val()){
					if(startCell + j > 4)cellNum = cellNum + 4
				}
				
				var cell = tab.rows[startRow + i].cells[cellNum];
				$(cell).find(':text').val(arr[i][j]); //找到cell下的input:text，设置value
				$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
				$(cell).html(arr[i][j]);
			}
		}
		refreshGridDate(0)
	});
}

function dataGridMorePaste(){
	$('#dataGrid_1').bind('paste', function(e) {
		e.preventDefault(); // 消除默认粘贴
		// 获取粘贴板数据
		var data = null;
		var clipboardData = window.clipboardData || e.originalEvent.clipboardData; // IE || chrome
		data = clipboardData.getData('Text');
		// 解析数据
		var arr = data.split('\n').filter(function(item) { // 兼容Excel行末\n，防止出现多余空行
			return (item !== "")
		}).map(function(item) {
			return item.split("\t");
		});
		// 输出至网页表格
		var tab = this; // 表格DOM
		var td = $(e.target).parents('td');
		var startRow = td.parents('tr')[0].rowIndex;
		var startCell = td[0].cellIndex;
		var rows = tab.rows.length; // 总行数
		for (var i = 0; i < arr.length; i++) {
			if(startRow + i >= rows){
				$("#newOperation_1").click();
			}
			var cells = tab.rows[startRow + i].cells.length; // 该行总列数
			for (var j = 0; j < arr[i].length && startCell + j < cells; j++) {
				var cellNum = startCell + j
				var cell = tab.rows[startRow + i].cells[cellNum];
				$(cell).find(':text').val(arr[i][j]); 		//找到cell下的input:text，设置value
				$('#dataGrid_1').jqGrid("saveCell", startRow + i, 1);
				$(cell).html(arr[i][j]);
			}
		}
	});
}
function dataGridMorePaste2(){
	$('#dataGrid_2').bind('paste', function(e) {
		e.preventDefault(); // 消除默认粘贴
		// 获取粘贴板数据
		var data = null;
		var clipboardData = window.clipboardData || e.originalEvent.clipboardData; // IE || chrome
		data = clipboardData.getData('Text');
		// 解析数据
		var arr = data.split('\n').filter(function(item) { // 兼容Excel行末\n，防止出现多余空行
			return (item !== "")
		}).map(function(item) {
			return item.split("\t");
		});
		// 输出至网页表格
		var tab = this; // 表格DOM
		var td = $(e.target).parents('td');
		var startRow = td.parents('tr')[0].rowIndex;
		var startCell = td[0].cellIndex;
		var rows = tab.rows.length; // 总行数
		for (var i = 0; i < arr.length; i++) {
			if(startRow + i >= rows){
				$("#newOperation_2").click();
			}
			var cells = tab.rows[startRow + i].cells.length; // 该行总列数
			for (var j = 0; j < arr[i].length && startCell + j < cells; j++) {
				var cell = tab.rows[startRow + i].cells[startCell + j];
				$(cell).find(':text').val(arr[i][j]); //找到cell下的input:text，设置value
				$('#dataGrid_2').jqGrid("saveCell", startRow + i, 1);
				$(cell).html(arr[i][j]);
			}
		}
	});
}
function dataGridMorePaste3(){
	$('#dataGrid_3').bind('paste', function(e) {
		e.preventDefault(); // 消除默认粘贴
		// 获取粘贴板数据
		var data = null;
		var clipboardData = window.clipboardData || e.originalEvent.clipboardData; // IE || chrome
		data = clipboardData.getData('Text');
		// 解析数据
		var arr = data.split('\n').filter(function(item) { // 兼容Excel行末\n，防止出现多余空行
			return (item !== "")
		}).map(function(item) {
			return item.split("\t");
		});
		// 输出至网页表格
		var tab = this; // 表格DOM
		var td = $(e.target).parents('td');
		var startRow = td.parents('tr')[0].rowIndex;
		var startCell = td[0].cellIndex;
		var rows = tab.rows.length; // 总行数
		for (var i = 0; i < arr.length; i++) {
			if(startRow + i >= rows){
				$("#newOperation_3").click();
			}
			var cells = tab.rows[startRow + i].cells.length; // 该行总列数
			for (var j = 0; j < arr[i].length && startCell + j < cells; j++) {
				var cell = tab.rows[startRow + i].cells[startCell + j];
				$(cell).find(':text').val(arr[i][j]); //找到cell下的input:text，设置value
				$('#dataGrid_3').jqGrid("saveCell", startRow + i, 1);
				$(cell).html(arr[i][j]);
			}
		}
	});
}
function dataGridMorePaste4(){
	$('#dataGrid_4').bind('paste', function(e) {
		e.preventDefault(); // 消除默认粘贴
		// 获取粘贴板数据
		var data = null;
		var clipboardData = window.clipboardData || e.originalEvent.clipboardData; // IE || chrome
		data = clipboardData.getData('Text');
		// 解析数据
		var arr = data.split('\n').filter(function(item) { // 兼容Excel行末\n，防止出现多余空行
			return (item !== "")
		}).map(function(item) {
			return item.split("\t");
		});
		// 输出至网页表格
		var tab = this; // 表格DOM
		var td = $(e.target).parents('td');
		var startRow = td.parents('tr')[0].rowIndex;
		var startCell = td[0].cellIndex;
		var rows = tab.rows.length; // 总行数
		for (var i = 0; i < arr.length; i++) {
			if(startRow + i >= rows){
				$("#newOperation_4").click();
			}
			var cells = tab.rows[startRow + i].cells.length; // 该行总列数
			for (var j = 0; j < arr[i].length && startCell + j < cells; j++) {
				var cell = tab.rows[startRow + i].cells[startCell + j];
				$(cell).find(':text').val(arr[i][j]); //找到cell下的input:text，设置value
				$('#dataGrid_4').jqGrid("saveCell", startRow + i, 1);
				$(cell).html(arr[i][j]);
			}
		}
	});
}

function refreshGridDate(iRow){
	console.log("-->refreshGridDate iRow=" + iRow + ";searchDataNum=" + searchDataNum + ";trNum=" + mydata.length);
	console.log("-->re_type : " + $("#re_type").val())
	var trs=$("#dataGrid").children("tbody").children("tr");
	var check = true;
	var mat_index = 0;var matinfo_index = 0;
	if("35" == $("#re_type").val() || "36" == $("#re_type").val()){
		mat_index = 5;matinfo_index = 6;
	}else if("37" == $("#re_type").val() || "38" == $("#re_type").val()){
		mat_index = 2;matinfo_index = 3;
	}else if("40" == $("#re_type").val()){
		mat_index = 4;matinfo_index = 5;
	}
	var mat="";
	//手动添加或粘贴的行项目不能出现与查询结果有重复的料号
	if(iRow > searchDataNum){	//手动输入
		//console.log($("#dataGrid").children("tbody").children("tr").eq(iRow).find("td").eq(5).html());	
		mat = $.trim($("#dataGrid").children("tbody").children("tr").eq(iRow).find("td").eq(mat_index).html());
		console.log("mat=" + mat);
		$.each(trs,function(index,tr){
			if(index < iRow && index > 0){
				console.log(mat + "|" + $(tr).find("td").eq(mat_index).html());
				if(mat == $.trim($(tr).find("td").eq(mat_index).html())){
					alert("手动添加或粘贴的行项目不能出现与查询结果有重复的料号");
					$("#dataGrid").children("tbody").children("tr").eq(iRow).find("td").eq(matinfo_index).html("");
					$("#dataGrid").children("tbody").children("tr").eq(iRow).find("td").eq(matinfo_index+1).html("");
					$("#dataGrid").children("tbody").children("tr").eq(iRow).find("td").eq(matinfo_index+2).html("");
					$("#dataGrid").children("tbody").children("tr").eq(iRow).find("td").eq(matinfo_index+3).html("");
					check = false;
				}
			}
		});
	}
	if(iRow == 0){		//粘贴多行
		for(var i = searchDataNum+1;i <= mydata.length; i++){
			mat = $.trim($("#dataGrid").children("tbody").children("tr").eq(i).find("td").eq(mat_index).html());
			console.log("mats=" + mat);
			if(mat != '&nbsp;'){
				$.each(trs,function(index,tr){
					if(index < i && index > 0){
						console.log(mat + "|" + $(tr).find("td").eq(mat_index).html());
						if(mat == $.trim($(tr).find("td").eq(mat_index).html())){
							alert("手动添加或粘贴的行项目不能出现与查询结果有重复的料号");
							$("#dataGrid").children("tbody").children("tr").eq(i).find("td").eq(matinfo_index).html("");
							$("#dataGrid").children("tbody").children("tr").eq(i).find("td").eq(matinfo_index+1).html("");
							$("#dataGrid").children("tbody").children("tr").eq(i).find("td").eq(matinfo_index+2).html("");
							$("#dataGrid").children("tbody").children("tr").eq(i).find("td").eq(matinfo_index+3).html("");
							check = false;
						}
					}
				});
			}
		}
		
	}
	
	if(check){
		$.each(trs,function(index,tr){
			if(index > 0){
				if(iRow==0){
					getTableDate(tr);
				}else{
					if(index == iRow){
						getTableDate(tr);
					}
				}
			}
		});
	}
}

function getTableDate(tr){
	var parm1 = 0;var parm2 = 0;var parm3 = 0;	//预留编号OR需求号 ;行项目号;料号 所在表格的位置
	var tr_mat_start = 100;
	if("35" == $("#re_type").val() || "36" == $("#re_type").val()){
		parm1 = 3;parm2 = 4;parm3 = 5;tr_mat_start = 6;
	}else if("37" == $("#re_type").val() || "38" == $("#re_type").val()){
		parm3 = 2;tr_mat_start = 3;
	}else if("39" == $("#re_type").val()){
		parm3 = 2;tr_mat_start = 3;
	}else if("40" == $("#re_type").val()){
		parm3 = 4;tr_mat_start = 5;
	}
	if(($(tr).find("td").eq(parm3).html() != "&nbsp;" && $(tr).find("td").eq(parm3).html() != "")){
			console.log("-->getTableDate MATNR = " + $(tr).find("td").eq(parm3).html().replace('\n',""))
			$.ajax({
				url:baseURL+"workshopReturn/getSapMoComponentDate",
				dataType : "json",
				type : "post",
				data : {
					"RSNUM":$(tr).find("td").eq(parm1).html().replace('\n',""),		//预留编号OR需求号
					"RSPOS":$(tr).find("td").eq(parm2).html().replace('\n',""),		//行项目号
					"MATNR":$(tr).find("td").eq(parm3).html().replace('\n',""),		//料号
					"WERKS":$("#werks").val(),
					"WH_NUMBER":$("#wh").val(),
					"WBS":$("#wbs").val(),
					"RECEIVE_LGORT":$("#LGORT").val(),
					"MONO":$("#orders").val(),
					"IO_NO":$("#in_order").val(),
					"COST_CENTER":$("#costcenter").val(),
					"MO_NO":$(tr).find("td").eq(2).html().replace('\n',""),
					"BUSINESS_NAME":$("#re_type").val(),
				},
				async: true,
				success: function (response) { 
					if(response.code == '0'){
						if(response.result.length == 1){
							if(response.result[0].WERKS != $("#werks").val()){
								mydata[$(tr).find("td").eq(0).html()-1].MAKTX = "";
								mydata[$(tr).find("td").eq(0).html()-1].MEINS = "";
								mydata[$(tr).find("td").eq(0).html()-1].TL_QTY = "";
								mydata[$(tr).find("td").eq(0).html()-1].KT_QTY = "";
								$(tr).find("td").eq(tr_mat_start).html("");
								$(tr).find("td").eq(tr_mat_start+1).html("");
								$(tr).find("td").eq(tr_mat_start+2).html("");
								$(tr).find("td").eq(tr_mat_start+3).html("");
								js.showMessage("物料号" + $(tr).find("td").eq(parm3).html().replace('\n',"") + "不属于工厂" + $("#werks").val());
							}else{
								mydata[$(tr).find("td").eq(0).html()-1].MAKTX = response.result[0].MAKTX;
								mydata[$(tr).find("td").eq(0).html()-1].MEINS = response.result[0].MEINS;
								if("40" != $("#re_type").val()){
									mydata[$(tr).find("td").eq(0).html()-1].TL_QTY = response.result[0].TL_QTY;
									mydata[$(tr).find("td").eq(0).html()-1].KT_QTY = response.result[0].TL_QTY - response.result[0].RETURN_QTY;

									$(tr).find("td").eq(tr_mat_start+2).html(response.result[0].TL_QTY);
									$(tr).find("td").eq(tr_mat_start+3).html(response.result[0].KT_QTY);
								}
								//mydata[$(tr).find("td").eq(0).html()-1].QTY1 = response.result[0].TL_QTY - response.result[0].RETURN_QTY;
								$(tr).find("td").eq(tr_mat_start).html(response.result[0].MAKTX);
								$(tr).find("td").eq(tr_mat_start+1).html(response.result[0].MEINS);
								//$(tr).find("td").eq(tr_mat_start+4).html(response.result[0].KT_QTY);
							}
						}else{
							mydata[$(tr).find("td").eq(0).html()-1].MAKTX = "";
							mydata[$(tr).find("td").eq(0).html()-1].MEINS = "";
							mydata[$(tr).find("td").eq(0).html()-1].TL_QTY = "";
							mydata[$(tr).find("td").eq(0).html()-1].KT_QTY = "";
							$(tr).find("td").eq(tr_mat_start).html("");
							$(tr).find("td").eq(tr_mat_start+1).html("");
							$(tr).find("td").eq(tr_mat_start+2).html("");
							$(tr).find("td").eq(tr_mat_start+3).html("");
						}
					}
				}
			});
		}
}

function fun_ShowMoreTable(){
	$("#dataGrid_1").dataGrid({
		datatype: "local",
		data: mydata2,
        colModel: [			
            {label: '<span style="color:red">*</span><span style="color:blue"><b>生产订单</b></span>', name: 'AUFNR',index:"AUFNR", width: "200",align:"center",sortable:false,editable:true}, 
            {label: '删除', name: 'AUFNR',index:"AUFNR", width: "80",align:"center",sortable:false,editable:false,
            	formatter:function(value, name, record){
            		return '<i title="删除" aria-hidden="true" onclick="$(this).parent().parent().remove();delMoreTr();" class="fa fa-times"></i>';
            	}, 
            },
            {label: '', name: '',index:"AUFNR", width: "150",align:"center",sortable:false,editable:false} 
        ],
		shrinkToFit: false,
        width:600,
        cellEdit:true,
        cellurl:'#',
		cellsubmit:'clientArray',
        showCheckbox:false
    });
}
function fun_ShowMoreTable2(){
	$("#dataGrid_2").dataGrid({
		datatype: "local",
		data: mydata_mat,
        colModel: [			
            {label: '<span style="color:red">*</span><span style="color:blue"><b>物料号</b></span>', name: 'AUFNR',index:"AUFNR", width: "200",align:"center",sortable:false,editable:true}, 
            {label: '删除', name: 'AUFNR',index:"AUFNR", width: "80",align:"center",sortable:false,editable:false,
            	formatter:function(value, name, record){
            		return '<i title="删除" aria-hidden="true" onclick="$(this).parent().parent().remove();delMoreTr2();" class="fa fa-times"></i>';
            	}, 
            },
            {label: '', name: '',index:"AUFNR", width: "150",align:"center",sortable:false,editable:false} 
        ],
		shrinkToFit: false,
        width:600,
        cellEdit:true,
        cellurl:'#',
		cellsubmit:'clientArray',
        showCheckbox:false
    });
}
function fun_ShowMoreTable3(){
	$("#dataGrid_3").dataGrid({
		datatype: "local",
		data: mydata_sap,
        colModel: [			
            {label: '<span style="color:red">*</span><span style="color:blue"><b>SAP交货单</b></span>', name: 'AUFNR',index:"AUFNR", width: "200",align:"center",sortable:false,editable:true}, 
            {label: '删除', name: 'AUFNR',index:"AUFNR", width: "80",align:"center",sortable:false,editable:false,
            	formatter:function(value, name, record){
            		return '<i title="删除" aria-hidden="true" onclick="$(this).parent().parent().remove();delMoreTr3();" class="fa fa-times"></i>';
            	}, 
            },
            {label: '', name: '',index:"AUFNR", width: "150",align:"center",sortable:false,editable:false} 
        ],
		shrinkToFit: false,
        width:600,
        cellEdit:true,
        cellurl:'#',
		cellsubmit:'clientArray',
        showCheckbox:false
    });
}
function fun_ShowMoreTable4(){
	$("#dataGrid_4").dataGrid({
		datatype: "local",
		data: mydata_sap,
        colModel: [			
            {label: '<span style="color:red">*</span><span style="color:blue"><b>采购订单号</b></span>', name: 'AUFNR',index:"AUFNR", width: "200",align:"center",sortable:false,editable:true}, 
            {label: '删除', name: 'AUFNR',index:"AUFNR", width: "80",align:"center",sortable:false,editable:false,
            	formatter:function(value, name, record){
            		return '<i title="删除" aria-hidden="true" onclick="$(this).parent().parent().remove();delMoreTr3();" class="fa fa-times"></i>';
            	}, 
            },
            {label: '', name: '',index:"AUFNR", width: "150",align:"center",sortable:false,editable:false} 
        ],
		shrinkToFit: false,
        width:600,
        cellEdit:true,
        cellurl:'#',
		cellsubmit:'clientArray',
        showCheckbox:false
    });
}

function fun_ShowTable(){
	$("#dataGrid").dataGrid({
		datatype: "local",
		data: mydata,
        colModel: [			
            {label: '<span style="color:red">*</span><span style="color:blue"><b>生产订单</b></span>', name: 'AUFNR',index:"AUFNR", width: "150",align:"center",sortable:false,editable:true}, 
            {label: '<span style="color:red">*</span><span style="color:blue"><b>预留编号</b></span>', name: 'RSNUM',index:"RSNUM", width: "80",align:"center",sortable:false,editable:true}, 
            {label: '<span style="color:red">*</span><span style="color:blue"><b>项目号</b></span>', name: 'RSPOS',index:"RSPOS", width: "80",align:"center",sortable:false,editable:true}, 
            {label: '<span style="color:red">*</span><span style="color:blue"><b>料号</b></span>', name: 'MATNR',index:"MATNR", width: "100",align:"center",sortable:false,editable:true}, 
            {label: '物料描述', name: 'MAKTX',index:"MAKTX", width: "200",align:"center",sortable:false,frozen: true},
            {label: '单位', name: 'MEINS',index:"MEINS", width: "80",align:"center",sortable:false,frozen: true},
            {label: '领料数量', name: 'TL_QTY',index:"TL_QTY", width: "80",align:"center",sortable:false,frozen: true},
            {label: '可退数量', name: 'KT_QTY',index:"KT_QTY", width: "80",align:"center",sortable:false,frozen: true},
            {label: '<span style="color:red">*</span><span style="color:blue"><b>退料数量</b></span>', name: 'QTY1',index:"QTY1", width: "100",align:"center",sortable:false,editable:true}, 
            {label: '<span style="color:blue"><b>退料原因</b></span>', name: 'REASON',index:"REASON", width: "150",align:"center",sortable:false,editable:true}, 
            {label: '<span style="color:blue"><b>SQE</b></span>', name: 'SQE',index:"SQE", width: "100",align:"center",sortable:false,editable:true}, 
            {label: 'LGORT', name: 'LGORT',index:"LGORT", width: "50",align:"center",sortable:false,hidden:true},
            {label: 'SOBKZ', name: 'SOBKZ',index:"SOBKZ", width: "50",align:"center",sortable:false,hidden:true},
            {label: 'POSNR', name: 'POSNR',index:"POSNR", width: "50",align:"center",sortable:false,hidden:true},
            {label: 'LIFNR', name: 'LIFNR',index:"LIFNR", width: "50",align:"center",sortable:false,hidden:true},        
            ],
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
			//3.3.3.4 3生产订单退料（不冲预留）这种业务场景可以超出可退数量[用于支持月底集中退料，无法知悉发料生产订单的情况]。
			//用于支持月底集中退料，无法知悉发料生产订单的情况
			if(cellname=='QTY1' && $("#re_type").val() != '36'){
				if(parseFloat(value) > parseFloat(row.KT_QTY)){
					alert("退货数量不能大于可退数量！");
					$("#dataGrid").jqGrid('setCell', rowid, cellname, mydata[rowid-1].KT_QTY,'editable-cell');
					mydata[rowid-1].QTY1 = row.KT_QTY;
				}else{
					mydata[rowid-1].QTY1 = value;
				}
			}
			check = true;
			refreshGridDate(iRow);
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

function fun_ShowTable36(){
	$("#dataGrid").dataGrid({
		datatype: "local",
		data: mydata,
        colModel: [			
        	{label: '<span style="color:red">*</span><span style="color:blue"><b>生产订单</b></span>', name: 'AUFNR',index:"AUFNR", width: "150",align:"center",sortable:false,editable:true}, 
            {label: '<span style="color:red">*</span><span style="color:blue"><b>预留编号</b></span>', name: 'RSNUM',index:"RSNUM", width: "80",align:"center",sortable:false,editable:true}, 
            {label: '<span style="color:red">*</span><span style="color:blue"><b>项目号</b></span>', name: 'RSPOS',index:"RSPOS", width: "80",align:"center",sortable:false,editable:true}, 
            {label: '<span style="color:red">*</span><span style="color:blue"><b>料号</b></span>', name: 'MATNR',index:"MATNR", width: "150",align:"center",sortable:false,editable:true}, 
            {label: '物料描述', name: 'MAKTX',index:"MAKTX", width: "200",align:"center",sortable:false,frozen: true},
            {label: '单位', name: 'MEINS',index:"MEINS", width: "80",align:"center",sortable:false,frozen: true},
            {label: '领料数量', name: 'TL_QTY',index:"TL_QTY", width: "80",align:"center",sortable:false,frozen: true},
            {label: '可退数量', name: 'KT_QTY',index:"KT_QTY", width: "80",align:"center",sortable:false,frozen: true},
            {label: '<span style="color:red">*</span><span style="color:blue"><b>退料数量</b></span>', name: 'QTY1',index:"QTY1", width: "100",align:"center",sortable:false,editable:true}, 
            {label: '<span style="color:blue"><b>退料原因</b></span>', name: 'REASON',index:"REASON", width: "150",align:"center",sortable:false,editable:true}, 
            {label: '<span style="color:blue"><b>SQE</b></span>', name: 'SQE',index:"SQE", width: "100",align:"center",sortable:false,editable:true}, 
            {label: 'LGORT', name: 'LGORT',index:"LGORT", width: "50",align:"center",sortable:false,hidden:true},
            {label: 'SOBKZ', name: 'SOBKZ',index:"SOBKZ", width: "50",align:"center",sortable:false,hidden:true},
            {label: 'POSNR', name: 'POSNR',index:"POSNR", width: "50",align:"center",sortable:false,hidden:true},
            {label: 'COST_CENTER', name: 'COST_CENTER',index:"COST_CENTER", width: "50",align:"center",sortable:false,hidden:true},
            {label: 'IO_NO', name: 'IO_NO',index:"IO_NO", width: "50",align:"center",sortable:false,hidden:true},
            ],
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
			//1.1.5.4退料数量：必填，允许编辑，这种业务场景可以超出可退数量[用于支持月底集中退料，无法知悉发料生产订单的情况]。
			//用于支持月底集中退料，无法知悉发料生产订单的情况
			/*if(cellname=='QTY1'){
				if(parseFloat(value) > parseFloat(row.KT_QTY)){
					alert("退货数量不能大于可退数量！");
					$("#dataGrid").jqGrid('setCell', rowid, cellname, mydata[rowid-1].KT_QTY,'editable-cell');
					mydata[rowid-1].QTY1 = row.KT_QTY;
				}else{
					mydata[rowid-1].QTY1 = value;
				}
			}else{
				refreshGridDate(iRow);
			}*/
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

function fun_ShowTable37(){
	$("#dataGrid").dataGrid({
		datatype: "local",
		data: mydata,
        colModel: [			
            {label: '<span style="color:red">*</span><span style="color:blue"><b>料号</b></span>', name: 'MATNR',index:"MATNR", width: "150",align:"center",sortable:false,editable:true}, 
            {label: '物料描述', name: 'MAKTX',index:"MAKTX", width: "200",align:"center",sortable:false,frozen: true},
            {label: '单位', name: 'MEINS',index:"MEINS", width: "80",align:"center",sortable:false,frozen: true},
            {label: '领料数量', name: 'TL_QTY',index:"TL_QTY", width: "80",align:"center",sortable:false,frozen: true},
            {label: '可退数量', name: 'KT_QTY',index:"KT_QTY", width: "80",align:"center",sortable:false,frozen: true},
            {label: '<span style="color:red">*</span><span style="color:blue"><b>退料数量</b></span>', name: 'QTY1',index:"QTY1", width: "100",align:"center",sortable:false,editable:true}, 
            {label: '<span style="color:blue"><b>退料原因</b></span>', name: 'REASON',index:"REASON", width: "150",align:"center",sortable:false,editable:true}, 
            {label: '<span style="color:blue"><b>SQE</b></span>', name: 'SQE',index:"SQE", width: "100",align:"center",sortable:false,editable:true}, 
            {label: 'LGORT', name: 'LGORT',index:"LGORT", width: "50",align:"center",sortable:false,hidden:true},
            {label: 'SOBKZ', name: 'SOBKZ',index:"SOBKZ", width: "50",align:"center",sortable:false,hidden:true},
            {label: 'POSNR', name: 'POSNR',index:"POSNR", width: "50",align:"center",sortable:false,hidden:true},
            {label: 'COST_CENTER', name: 'COST_CENTER',index:"COST_CENTER", width: "50",align:"center",sortable:false,hidden:true},
            {label: 'IO_NO', name: 'IO_NO',index:"IO_NO", width: "50",align:"center",sortable:false,hidden:true},
            ],
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
			if(cellname=='QTY1'){
				if(parseFloat(value) > parseFloat(row.KT_QTY)){
					alert("退货数量不能大于可退数量！");
					$("#dataGrid").jqGrid('setCell', rowid, cellname, mydata[rowid-1].KT_QTY,'editable-cell');
					mydata[rowid-1].QTY1 = row.KT_QTY;
				}else{
					mydata[rowid-1].QTY1 = value;
				}
			}else{
				refreshGridDate(iRow);
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

function fun_ShowTable40(){
	$("#dataGrid").dataGrid({
		datatype: "local",
		data: mydata,
        colModel: [			
            {label: '<span style="color:blue"><b>需求号</b></span>', name: 'REQUIREMENT_NO',index:"REQUIREMENT_NO", width: "150",align:"center",sortable:false,editable:true}, 
            {label: '<span style="color:blue"><b>行项目号</b></span>', name: 'REQUIREMENT_ITEM_NO',index:"REQUIREMENT_ITEM_NO", width: "80",align:"center",sortable:false,editable:true}, 
            {label: '<span style="color:red">*</span><span style="color:blue"><b>料号</b></span>', name: 'MATNR',index:"MATNR", width: "100",align:"center",sortable:false,editable:true}, 
            {label: '物料描述', name: 'MAKTX',index:"MAKTX", width: "200",align:"center",sortable:false,frozen: true},
            {label: '单位', name: 'MEINS',index:"MEINS", width: "80",align:"center",sortable:false,frozen: true},
            {label: '领料数量', name: 'TL_QTY',index:"TL_QTY", width: "80",align:"center",sortable:false,frozen: true},
            {label: '可退数量', name: 'KT_QTY',index:"KT_QTY", width: "80",align:"center",sortable:false,frozen: true},
            {label: '<span style="color:red">*</span><span style="color:blue"><b>退料数量</b></span>', name: 'QTY1',index:"QTY1", width: "100",align:"center",sortable:false,editable:true}, 
            {label: '<span style="color:blue"><b>退料原因</b></span>', name: 'REASON',index:"REASON", width: "150",align:"center",sortable:false,editable:true}, 
            {label: '<span style="color:blue"><b>SQE</b></span>', name: 'SQE',index:"SQE", width: "100",align:"center",sortable:false,editable:true}, 
            {label: 'LGORT', name: 'LGORT',index:"LGORT", width: "50",align:"center",sortable:false,hidden:true},
            {label: 'SOBKZ', name: 'SOBKZ',index:"SOBKZ", width: "50",align:"center",sortable:false,hidden:true},
            {label: 'POSNR', name: 'POSNR',index:"POSNR", width: "50",align:"center",sortable:false,hidden:true},
            
            ],
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
			if(cellname=='QTY1'){
				if(parseFloat(value) > parseFloat(row.KT_QTY)){
					alert("退货数量不能大于可退数量！");
					$("#dataGrid").jqGrid('setCell', rowid, cellname, mydata[rowid-1].KT_QTY,'editable-cell');
					mydata[rowid-1].QTY1 = row.KT_QTY;
				}else{
					mydata[rowid-1].QTY1 = value;
				}
			}
			check = true;
			refreshGridDate(iRow);
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

function fun_ShowTable70(){
	$("#dataGrid").dataGrid({
		datatype: "local",
		data: mydata,
        colModel: [			
            {label: '<span style="color:red">*</span><span style="color:blue"><b>SAP交货单</b></span>', name: 'SAP_OUT_NO',index:"SAP_OUT_NO", width: "150",align:"center",sortable:false,editable:true}, 
            {label: '<span style="color:red">*</span><span style="color:blue"><b>项目号</b></span>', name: 'SAP_OUT_ITEM_NO',index:"SAP_OUT_ITEM_NO", width: "80",align:"center",sortable:false,editable:true}, 
            {label: '<span style="color:red">*</span><span style="color:blue"><b>料号</b></span>', name: 'MATNR',index:"MATNR", width: "100",align:"center",sortable:false,editable:true}, 
            {label: '物料描述', name: 'MAKTX',index:"MAKTX", width: "200",align:"center",sortable:false,frozen: true},
            {label: '单位', name: 'MEINS',index:"MEINS", width: "80",align:"center",sortable:false,frozen: true},
            {label: '领料数量', name: 'TL_QTY',index:"TL_QTY", width: "80",align:"center",sortable:false,frozen: true},
            {label: '可退数量', name: 'KT_QTY',index:"KT_QTY", width: "80",align:"center",sortable:false,frozen: true},
            {label: '<span style="color:red">*</span><span style="color:blue"><b>退料数量</b></span>', name: 'QTY1',index:"QTY1", width: "100",align:"center",sortable:false,editable:true}, 
            {label: '<span style="color:blue"><b>退料原因</b></span>', name: 'REASON',index:"REASON", width: "150",align:"center",sortable:false,editable:true}, 
            {label: '<span style="color:blue"><b>SQE</b></span>', name: 'SQE',index:"SQE", width: "100",align:"center",sortable:false,editable:true}, 
            {label: 'LGORT', name: 'LGORT',index:"LGORT", width: "50",align:"center",sortable:false,hidden:true},
            {label: 'SOBKZ', name: 'SOBKZ',index:"SOBKZ", width: "50",align:"center",sortable:false,hidden:true},
            {label: 'POSNR', name: 'POSNR',index:"POSNR", width: "50",align:"center",sortable:false,hidden:true},
            {label: 'LIFNR', name: 'LIFNR',index:"LIFNR", width: "50",align:"center",sortable:false,hidden:true},
            
            ],
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
			if(cellname=='QTY1'){
				if(parseFloat(value) > parseFloat(row.KT_QTY)){
					alert("退货数量不能大于可退数量！");
					$("#dataGrid").jqGrid('setCell', rowid, cellname, mydata[rowid-1].KT_QTY,'editable-cell');
					mydata[rowid-1].QTY1 = row.KT_QTY;
				}else{
					mydata[rowid-1].QTY1 = value;
				}
			}
			check = true;
			//refreshGridDate(iRow);
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

function fun_ShowTable68(){
	$("#dataGrid").dataGrid({
		datatype: "local",
		data: mydata,
        colModel: [			
            {label: '<span style="color:red">*</span><span style="color:blue"><b>采购订单</b></span>', name: 'RSNUM',index:"RSNUM", width: "80",align:"center",sortable:false,editable:true}, 
            {label: '<span style="color:red">*</span><span style="color:blue"><b>项目号</b></span>', name: 'RSPOS',index:"RSPOS", width: "80",align:"center",sortable:false,editable:true}, 
            {label: '<span style="color:red">*</span><span style="color:blue"><b>料号</b></span>', name: 'MATNR',index:"MATNR", width: "100",align:"center",sortable:false,editable:true}, 
            {label: '物料描述', name: 'MAKTX',index:"MAKTX", width: "200",align:"center",sortable:false,frozen: true},
            {label: '单位', name: 'MEINS',index:"MEINS", width: "80",align:"center",sortable:false,frozen: true},
            {label: '领料数量', name: 'TL_QTY',index:"TL_QTY", width: "80",align:"center",sortable:false,frozen: true},
            {label: '可退数量', name: 'KT_QTY',index:"KT_QTY", width: "80",align:"center",sortable:false,frozen: true},
            {label: '<span style="color:red">*</span><span style="color:blue"><b>退料数量</b></span>', name: 'QTY1',index:"QTY1", width: "100",align:"center",sortable:false,editable:true}, 
            {label: '<span style="color:blue"><b>退料原因</b></span>', name: 'REASON',index:"REASON", width: "150",align:"center",sortable:false,editable:true}, 
            {label: '<span style="color:blue"><b>SQE</b></span>', name: 'SQE',index:"SQE", width: "100",align:"center",sortable:false,editable:true}, 
            {label: 'LGORT', name: 'LGORT',index:"LGORT", width: "50",align:"center",sortable:false,hidden:true},
            {label: 'SOBKZ', name: 'SOBKZ',index:"SOBKZ", width: "50",align:"center",sortable:false,hidden:true},
            {label: 'POSNR', name: 'POSNR',index:"POSNR", width: "50",align:"center",sortable:false,hidden:true},
            
            ],
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
			if(cellname=='QTY1'){
				if(parseFloat(value) > parseFloat(row.KT_QTY)){
					alert("退货数量不能大于可退数量！");
					$("#dataGrid").jqGrid('setCell', rowid, cellname, mydata[rowid-1].KT_QTY,'editable-cell');
					mydata[rowid-1].QTY1 = row.KT_QTY;
				}else{
					mydata[rowid-1].QTY1 = value;
				}
			}
			check = true;
			refreshGridDate(iRow);
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

function switchLabel(){
	console.log("-->switchLabel re_type:" + $("#re_type").val());
	//if("35" == $("#re_type").val() || "36" == $("#re_type").val()){
	if ("35" == $("#re_type").val()){
	//生产订单退料
		$("#div_scdd").show();
		$("#div_kuwei").hide();
		$("#div_nbdd").hide();
		$("#div_cbzx").hide();
		$("#div_wbs").hide();
		$("#div_vendor").hide();
		$("#div_sapOrders").hide();
		$("#div_pono").hide();
		$("#div_thgc").hide();
		$("#div_reWerks").hide();
		fun_ShowTable();
	}else if ("36" == $("#re_type").val()){
		//生产订单退料
		$("#div_scdd").show();
		$("#div_kuwei").hide();
		$("#div_nbdd").hide();
		$("#div_cbzx").hide();
		$("#div_wbs").hide();
		$("#div_vendor").hide();
		$("#div_sapOrders").hide();
		$("#div_pono").hide();
		$("#div_thgc").hide();
		$("#div_reWerks").hide();
		fun_ShowTable36();
	}else if("37" == $("#re_type").val()){
		//37 研发/内部/CO订单退料(262)
		$("#div_nbdd").show();
		$("#div_scdd").hide();
		$("#div_kuwei").hide();
		$("#div_cbzx").hide();
		$("#div_wbs").hide();
		$("#div_vendor").hide();
		$("#div_sapOrders").hide();
		$("#div_pono").hide();
		$("#div_thgc").hide();
		$("#div_reWerks").hide();
		
		$("#inOrderInfo").html("回车获取内部订单信息");
		fun_ShowTable37();
	}else if("38" == $("#re_type").val()){
		//38 成本中心退料(202)
		$("#div_nbdd").hide();
		$("#div_scdd").hide();
		$("#div_kuwei").hide();
		$("#div_wbs").hide();
		$("#div_cbzx").show();
		$("#div_vendor").hide();
		$("#div_sapOrders").hide();
		$("#div_pono").hide();
		$("#div_thgc").hide();
		$("#div_reWerks").show();
		$("#re_werks").val('');
		$("#costcenterInfo").html("回车获取成本中心信息");
		fun_ShowTable37();
	}else if("39" == $("#re_type").val()){
		//39 WBS元素退料(222)
		$("#div_nbdd").hide();
		$("#div_scdd").hide();
		$("#div_kuwei").hide();
		$("#div_cbzx").hide();
		$("#div_wbs").show();
		$("#div_vendor").hide();
		$("#div_sapOrders").hide();
		$("#div_pono").hide();
		$("#div_thgc").hide();
		$("#div_reWerks").show();
		$("#re_werks").val('');
		$("#wbsInfo").html("回车获取WBS元素信息");
		fun_ShowTable37();
	}else if("40" == $("#re_type").val()){
		//40 线边仓退料(312)
		$("#div_nbdd").hide();
		$("#div_scdd").show();
		$("#div_kuwei").show();
		$("#div_cbzx").hide();
		$("#div_wbs").hide();
		$("#div_vendor").hide();
		$("#div_sapOrders").hide();
		$("#div_pono").hide();
		$("#div_thgc").hide();
		$("#div_reWerks").hide();
		fun_ShowTable40();
	}else if("69" == $("#re_type").val()){
		//69 委外加工退料(542)
		$("#div_nbdd").hide();
		$("#div_scdd").hide();
		$("#div_kuwei").hide();
		$("#div_cbzx").hide();
		$("#div_wbs").hide();
		$("#div_vendor").show();
		$("#div_sapOrders").hide();
		$("#div_pono").show();
		$("#div_thgc").hide();
		$("#div_reWerks").hide();
		
		$("#vendorInfo").html("回车获取委外单位信息");
		fun_ShowTable68();
	}else if("70" == $("#re_type").val() || "71" == $("#re_type").val()){
		//70 STO销售退货	71 销售订单退货(657)
		$("#div_nbdd").hide();
		$("#div_scdd").hide();
		$("#div_kuwei").hide();
		$("#div_cbzx").hide();
		$("#div_wbs").hide();
		$("#div_vendor").hide();
		$("#div_sapOrders").show();
		$("#div_pono").hide();
		$("#div_thgc").hide();
		$("#div_reWerks").hide();
		
		fun_ShowTable70();
	}else if("72" == $("#re_type").val()){
		//72 外部销售退货(252)
		$("#div_nbdd").hide();
		$("#div_scdd").hide();
		$("#div_kuwei").hide();
		$("#div_cbzx").hide();
		$("#div_wbs").hide();
		$("#div_vendor").hide();
		$("#div_sapOrders").hide();
		$("#div_pono").show();
		$("#div_thgc").hide();
		$("#div_reWerks").hide();
		
		fun_ShowTable68();
	}else if("68" == $("#re_type").val()){
		//68 UB转储单退货(352)
		$("#div_nbdd").hide();
		$("#div_scdd").hide();
		$("#div_kuwei").hide();
		$("#div_cbzx").hide();
		$("#div_wbs").hide();
		$("#div_vendor").hide();
		$("#div_sapOrders").hide();
		$("#div_pono").show();
		$("#div_thgc").hide();
		$("#div_reWerks").hide();
		
		fun_ShowTable68();
	}else if("46" == $("#re_type").val()){
		//46 工厂间调拨
		$("#div_nbdd").hide();
		$("#div_scdd").hide();
		$("#div_kuwei").hide();
		$("#div_cbzx").hide();
		$("#div_wbs").hide();
		$("#div_vendor").hide();
		$("#div_sapOrders").hide();
		$("#div_pono").hide();
		$("#div_thgc").show();
		$("#div_reWerks").hide();
		
		fun_ShowTable37();
	}
}

function getLgortList(WERKS) {
	//查询库位
	$.ajax({
		url : baseUrl + "in/wmsinbound/lgortlist",
		data : {
			"DEL" : '0',
			"WERKS" : WERKS,
			"SOBKZ" : "Z",
			"BAD_FLAG" : '0'
		},
		success : function(resp) {
			vm.lgortlist = resp.result;
		}
	});
}
