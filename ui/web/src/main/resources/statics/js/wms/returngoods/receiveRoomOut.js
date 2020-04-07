var mydata = [];
var mydata2 = [];
var check = true;
var allreason = '';
var barCodeFlag = false;
var userName = "";
var lastrow,lastcell;  
var vm = new Vue({
	el : '#rrapp',
	data:{warehourse:[],businessList:[]},
	created: function(){
		this.businessList = getBusinessList("04","RG_RRO",$("#werks").val());
    	this.getUser();
    	this.businessList = getBusinessList("04","RG_RRO",$("#werks").val());
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
			var werks = $("#werks").val();
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
			vm.businessList = getBusinessList("04","RG_RRO",$("#werks").val());
		},
		onBusinessChange : function(event) {
			$("#sapno").val("");
			$("#sap_no").hide();
			$("#f_factory").hide();
			var business = event.target.value;
			if("27" == business){
				$("#sap_no").show();
			}else if("26" == business){
				$("#f_factory").show();
			}
		}
	}
});

$(function(){
	$("#inOrderNo").focus();
	$("#sap_no").hide();
	$("#f_factory").hide();
	var now = new Date(); //当前日期
	var startDate=new Date(now.getTime()-6*24*3600*1000);
	$("#dateStart").val(formatDate(startDate));
	$("#dateEnd").val(formatDate(now));
	
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
	
	fun_tab25();
	
	$("#btnSearchData").click(function () {
		console.log("-->btnSearchData")
		if("27"==$("#business_name").val()){
			if(""==$("#sapno").val()){
				alert("SAP交货单不能为空！");
				return false;
			}
			$("#btnSearchData").val("查询中...");
			$("#btnSearchData").attr("disabled","disabled");	
			
			$.ajax({
				url:baseURL+"returngoods/getReceiveRoomOutData",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":$("#werks").val(),
					"WH_NUMBER":$("#wh").val(),
					"BUSINESS_NAME":$("#business_name").find("option:selected").text(),
					"BUSINESS_CODE":$("#business_name").val(),
					"LIFNR":$("#lifnr").val(),
					"MATNR":$("#matnr").val(),
					"DATESTART":$("#dateStart").val(),
					"DATEEND":$("#dateEnd").val(),
					"deliveryNO":$("#sapno").val()
				},
				async: true,
				success: function (response) { 
					$("#dataGrid").jqGrid("clearGridData", true);
					mydata2.length=0;
					if(response.code == "0"){
						mydata2 = response.result;
						if(mydata2.length > 0){
							$("#dataGrid").jqGrid('GridUnload');
							console.log("-->TEST_FLAG = " + mydata2[0].TEST_FLAG)
							console.log("-->BARCODE_FLAG = " + mydata2[0].BARCODE_FLAG)
							if("02" === mydata2[0].TEST_FLAG && "X" === mydata2[0].BARCODE_FLAG){
								$("#btnCreat").attr("disabled","disabled");
								alert($("#werks").val() + "仓库号启用了条码管理且未上质检功能，请选择PDA扫描退货")
							}else{
								$("#btnCreat").removeAttr("disabled");
							}
							//AddBy:YK 190419 
							//退货数量修改时需判断工厂+仓库号【仓库配置表-WMS_C_WH】中是否启用条码管理-BARCODE_FLAG ，启用条码 =X 时不允许修改数量
							barCodeFlag = ("X" == mydata2[0].BARCODE_FLAG)?false:true;	
							fun_tab27();
							if(mydata2.length=="")js.showMessage("未查询到符合条件的数据！");
							//BUG #488 增加工厂业务类型是否配置的判断【WMS_C_PLANT_BUSINESS】！未找到配置时，报错“***工厂 未配置***业务类型！”
							if(0 == mydata2[0].PC_COUNT){
								$("#btnCreat").attr("disabled","disabled");
								alert($("#werks").val() + "工厂未配置收料房退货业务类型！")
							}
						}else{
							js.showMessage("未查询到符合条件的数据！");
						}
					}else{
						js.showMessage(response.msg)
					}
					$("#btnSearchData").val("查询");
					$("#btnSearchData").removeAttr("disabled");
				}
			});
			
		}else {	//if("25"==$("#business_name").val())
			$("#btnSearchData").val("查询中...");
			$("#btnSearchData").attr("disabled","disabled");		
			
			$.ajax({
				url:baseURL+"returngoods/getReceiveRoomOutData",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":$("#werks").val(),
					"F_WERKS":("26"==$("#business_name").val())?$("#f_werks").val():"",
					"WH_NUMBER":$("#wh").val(),
					"BUSINESS_NAME":$("#business_name").find("option:selected").text(),
					"BUSINESS_CODE":$("#business_name").val(),
					"LIFNR":$("#lifnr").val(),
					"MATNR":$("#matnr").val(),
					"DATESTART":$("#dateStart").val(),
					"DATEEND":$("#dateEnd").val(),
					"deliveryNO":$("#sapno").val()
				},
				async: true,
				success: function (response) { 
					$("#dataGrid").jqGrid("clearGridData", true);
					mydata.length=0;
					if(response.code == "0"){
						mydata = response.result;
						console.log("-->mydata.length = " + mydata.length)
						if(mydata.length==0){
							alert("未查询到符合条件的数据！");	
						}else{
							if(null == mydata[0].BUSINESS_CODE){
								alert("系统没有配置正确的退货业务类型！请联系系统管理员。")
								$("#btnSearchData").val("查询");
								$("#btnSearchData").removeAttr("disabled");
								return false;
							}
							//AddBy:YK 190419 
							//退货数量修改时需判断工厂+仓库号【仓库配置表-WMS_C_WH】中是否启用条码管理-BARCODE_FLAG ，启用条码 =X 时不允许修改数量
							barCodeFlag = ("X" == mydata[0].BARCODE_FLAG)?false:true;
							
							$("#dataGrid").jqGrid('GridUnload');	
							fun_tab25();
							console.log("-->25 TEST_FLAG = " + mydata[0].TEST_FLAG)
							console.log("-->25 BARCODE_FLAG = " + mydata[0].BARCODE_FLAG)
							if("02" === mydata[0].TEST_FLAG && "X" === mydata[0].BARCODE_FLAG){
								$("#btnCreat").attr("disabled","disabled");
								alert($("#werks").val() + "仓库号启用了条码管理且未上质检功能，请选择PDA扫描退货")
							}else{
								$("#btnCreat").removeAttr("disabled");
							}
						}
						//$('#dataGrid').jqGrid('setGridParam',{data: mydata}).trigger( 'reloadGrid' );
						//$("#dataGrid").jqGrid("setFrozenColumns");
					}else{
						alert(response.msg)
					}
					$("#btnSearchData").val("查询");
					$("#btnSearchData").removeAttr("disabled");
				}
			});
		}
		
	});
	
	$("#btnReset").click(function () {
		$("#dataGrid").jqGrid("clearGridData", true);
		$("#dataGrid").jqGrid("setFrozenColumns");
		$("#lifnr").val("");
		$("#matnr").val("");
		$("#sapno").val("");
		//console.log(document.getElementById("outNo").innerHTML);
		//document.getElementById("sp1").innerHTML="111111";
		//window.open("../../../../wms/docPrint/receiveRoomOutPrint?outNo=1RT0154;1RT0149");
	});
	$("#btnPrint1").click(function () {
		window.open(baseURL+"docPrint/receiveRoomOutPrint?PageSize=0&outNo=" + document.getElementById("outNo").innerHTML);
	});
	$("#btnPrint2").click(function () {
		window.open(baseURL+"docPrint/receiveRoomOutPrint?PageSize=1&outNo=" + document.getElementById("outNo").innerHTML);
	});
	$("#btnCreat").click(function () {
		
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
			
			console.log(JSON.stringify(arrList));
			
			$("#btnCreat").val("正在操作,请稍候...");	
			$("#btnCreat").attr("disabled","disabled");
			
			$.ajax({
				url:baseURL+"returngoods/createReceiveRoomOutReturn",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":$("#werks").val(),
					"WH_NUMBER":$("#wh").val(),
					"BUSINESS_NAME":$("#business_name").val(),
					"BUSINESS_CODE":$("#business_name").val(),
					"USERNAME":userName,
					"ARRLIST":JSON.stringify(arrList)
				},
				async: true,
				success: function (response) {
					if(response.code=='0'){
						js.showMessage("退货单创建成功");
		            	$("#btnCreat").val("创建退货单");
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
		    					$("#btnCreat").removeAttr("disabled");
		            		}
		            	});
					}else{
						js.showMessage(response.msg);
		            	$("#btnCreat").val("创建退货单");
    					$("#btnCreat").removeAttr("disabled");
					}
					$("#btnSearchData").click();
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
	
	function fun_tab25(){
		$("#dataGrid").dataGrid({
			datatype: "local",
			data: mydata,
	        colModel: [			
	            {label: '工厂', name: 'WERKS',index:"WERKS", width: "100",align:"center",sortable:false,frozen: true,
	            	formatter:function(val){
	            		return '<p style="height:20px;padding-top:7px">'+val+'</p>';
	            	}
	            },
	            {label: '仓库号', name: 'WH_NUMBER',index:"WH_NUMBER", width: "100",align:"center",sortable:false,frozen: true},
	            {label: '料号', name: 'MATNR',index:"MATNR", width: "100",align:"center",sortable:true,frozen: true},
	            {label: '物料描述', name: 'MAKTX',index:"MAKTX", width: "200",align:"center",sortable:false},
	            {label: '批次', name: 'BATCH',index:"BATCH", width: "100",align:"center",sortable:true},
	            {label: '供应商代码', name: 'LIFNR',index:"LIFNR", width: "100",align:"center",sortable:true},
	            {label: '供应商名称', name: 'LIKTX',index:"LIKTX", width: "150",align:"center",sortable:true},
	            {label: 'F_BATCH', name: 'F_BATCH',index:"F_BATCH", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'LIKTX', name: 'LIKTX',index:"LIKTX", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'LGORT', name: 'LGORT',index:"LGORT", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'BUSINESS_CODE', name: 'BUSINESS_CODE',index:"BUSINESS_CODE", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'BUSINESS_NAME', name: 'BUSINESS_NAME',index:"BUSINESS_NAME", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'BUSINESS_TYPE', name: 'BUSINESS_TYPE',index:"BUSINESS_TYPE", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'RECEIPT_ITEM_NO', name: 'RECEIPT_ITEM_NO',index:"RECEIPT_ITEM_NO", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'LABEL_NOS', name: 'LABEL_NOS',index:"LABEL_NOS", width: "50",align:"center",sortable:false,hidden:true},
				{label: '收料房数量', name: 'RH_QTY',index:"RH_QTY", width: "100",align:"center",sortable:true},      
	            {label: '已创建退货单数量', name: 'QTY1',index:"QTY1", width: "120",align:"center",sortable:true},
	            {label: '可退数量', name: 'QTY2',index:"QTY2", width: "100",align:"center",sortable:true,},
	            {label: '<span style="color:red">*</span><span style="color:blue"><b>退货数量</b></span>', name: 'QTY3',index:"QTY3", width: "100",align:"center",sortable:false,editable:barCodeFlag,
	            	formatter:'number',editrules:{number:true},
	            	formatoptions:{decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}
	            },
				{label: '<span style="color:blue"><b>退货原因</b> <a onclick="freason();" id="freason">&darr;填充</a></span>', name: 'REASON',index:"REASON", width: "200",sortable:false,align:"center",
	            	editable:true},			  
	            {label: '单位', name: 'UNIT',index:"UNIT", width: "50",align:"center",sortable:false}, 
	            {label: '库存类型', name: 'SOBKZ',index:"SOBKZ", width: "100",align:"center",sortable:true}, 
	            {label: '采购订单', name: 'PO_NO',index:"PO_NO", width: "150",align:"center",sortable:true}, 
	            {label: '订单行项目', name: 'PO_ITEM_NO',index:"PO_ITEM_NO", width: "100",align:"center",sortable:false}, 
	            {label: '送货单号', name: 'ASNNO',index:"ASNNO", width: "150",align:"center",sortable:true}, 
	            {label: '送货单行项目', name: 'ASNITM',index:"ASNITM", width: "120",align:"center",sortable:false}, 
	            {label: '收货单号', name: 'RECEIPT_NO',index:"RECEIPT_NO", width: "100",align:"center",sortable:true}, 
	            {label: '收货日期', name: 'RECEIPT_DATE',index:"RECEIPT_DATE", width: "100",align:"center",sortable:true}, 
	            {label: 'XCHPF', name: 'XCHPF',index:"XCHPF", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'REF_SAP_MATDOC_NO', name: 'REF_SAP_MATDOC_NO',index:"REF_SAP_MATDOC_NO", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'REF_SAP_MATDOC_ITEM_NO', name: 'REF_SAP_MATDOC_ITEM_NO',index:"REF_SAP_MATDOC_ITEM_NO", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'REF_SAP_MATDOC_YEAR', name: 'REF_SAP_MATDOC_YEAR',index:"REF_SAP_MATDOC_YEAR", width: "50",align:"center",sortable:false,hidden:true},
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
				if(cellname=='QTY3'){
					if("" == value){
						alert("退货数量不能为空！");
						$("#dataGrid").jqGrid('setCell', rowid, cellname, mydata[rowid-1].QTY2,'editable-cell');
						mydata[rowid-1].QTY3 = row.QTY2;
						check = false;
						return false;
					}
					if(parseFloat(value) > parseFloat(row.QTY2)){
						alert("退货数量不能大于可退数量！");
						$("#dataGrid").jqGrid('setCell', rowid, cellname, mydata[rowid-1].QTY2,'editable-cell');
						mydata[rowid-1].QTY3 = row.QTY2;
						check = false;
					}else{
						mydata[rowid-1].QTY3 = value;
						check = true;
					}
				}
				if(cellname=='REASON'){
					if(value != undefined){
						mydata[iRow-1].REASON = value;
					}else{
						mydata[iRow-1].REASON = allreason;
					}
				}
				$('#dataGrid').jqGrid('setGridParam',{data: mydata2}).trigger( 'reloadGrid' );
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
	
	function fun_tab27(){
		$("#dataGrid").dataGrid({
			datatype: "local",
			data: mydata2,
			width:1900,
	        colModel: [
	        	{label: '工厂', name: 'WERKS',index:"WERKS", width: "100",align:"center",sortable:false,frozen: true,
	            	formatter:function(val){
	            		return '<p style="height:20px;padding-top:7px">'+val+'</p>';
	            	}
	            },
	        	{label: '仓库号', name: 'WH_NUMBER',index:"WH_NUMBER", width: "100",align:"center",sortable:false,frozen: true},
	        	{label: '料号', name: 'MATNR',index:"MATNR", width: "100",align:"center",sortable:true,frozen: true},
	            {label: '物料描述', name: 'MAKTX',index:"MAKTX", width: "200",align:"center",sortable:false,frozen: true},
	            {label: '批次', name: 'BATCH',index:"BATCH", width: "100",align:"center",sortable:true},
	            {label: 'F_BATCH', name: 'F_BATCH',index:"F_BATCH", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'LIKTX', name: 'LIKTX',index:"LIKTX", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'LGORT', name: 'LGORT',index:"LGORT", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'BUSINESS_CODE', name: 'BUSINESS_CODE',index:"BUSINESS_CODE", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'BUSINESS_NAME', name: 'BUSINESS_NAME',index:"BUSINESS_NAME", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'BUSINESS_TYPE', name: 'BUSINESS_TYPE',index:"BUSINESS_TYPE", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'LABEL_NOS', name: 'LABEL_NOS',index:"LABEL_NOS", width: "50",align:"center",sortable:false,hidden:true},
				{label: 'XCHPF', name: 'XCHPF',index:"XCHPF", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'RECEIPT_ITEM_NO', name: 'RECEIPT_ITEM_NO',index:"RECEIPT_ITEM_NO", width: "50",align:"center",sortable:false,hidden:true},
	            {label: '单位', name: 'UNIT',index:"UNIT", width: "50",align:"center",sortable:false}, 
	            {label: '收料房数量', name: 'RH_QTY',index:"RH_QTY", width: "100",align:"center",sortable:true},    
	            {label: '已创建退货单数量', name: 'QTY1',index:"QTY1", width: "120",align:"center",sortable:true},
	            {label: '可退数量', name: 'QTY2',index:"QTY2", width: "100",align:"center",sortable:true,
	            	summaryType : function(value, name, record) {
	            		if(typeof value === 'string'){
							value = {totalAmount: 0};
						}
	            		value.totalAmount += parseFloat(record['QTY2']);
	            		return value; 
	        		},
	        		formatter: function (cellval, opts, rwdat, act) {
	        			if (opts.rowId === '') {
	        				return cellval.totalAmount;
	        			}else{
	        				return cellval;
	        			}
	        		},
	        	},
	            {label: '<span style="color:red">*</span><span style="color:blue"><b>退货数量</b></span>', name: 'QTY3',index:"QTY3", width: "100",align:"center",sortable:false,editable:true,
	            	formatter:'number',editrules:{number:true},
	            	formatoptions:{decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}
	            },
				{label: '<span style="color:blue"><b>退货原因</b> <a onclick="freason();" id="freason">&darr;填充</a></span>', name: 'REASON',index:"REASON", width: "200",sortable:false,align:"center",
	            	editable:true},			  
	            {label: '库存类型', name: 'SOBKZ',index:"SOBKZ", width: "100",align:"center",sortable:true}, 
	            {label: '供应商代码', name: 'LIFNR',index:"LIFNR", width: "100",align:"center",sortable:true},
	            {label: '供应商名称', name: 'LIKTX',index:"LIKTX", width: "100",align:"center",sortable:true},
	            {label: '采购订单', name: 'PO_NO',index:"PO_NO", width: "150",align:"center",sortable:true}, 
	            {label: '订单行项目', name: 'PO_ITEM_NO',index:"PO_ITEM_NO", width: "100",align:"center",sortable:false}, 
	            {label: 'SAP交货单', name: 'SAP_OUT_NO',index:"ASNNO", width: "150",align:"center",sortable:true}, 
	            {label: '交货单行项目', name: 'SAP_OUT_ITEM_NO',index:"ASNITM", width: "120",align:"center",sortable:false}, 
	            {label: '收货单号', name: 'RECEIPT_NO',index:"RECEIPT_NO", width: "100",align:"center",sortable:true}, 
	            {label: '收货日期', name: 'RECEIPT_DATE',index:"RECEIPT_DATE", width: "100",align:"center",sortable:true}, 
	            {label: 'stoStr', name: 'stoStr',index:"stoStr", width: "50",align:"center",sortable:false},
	            {label: 'g_id', name: 'g_id',index:"g_id", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'REF_SAP_MATDOC_NO', name: 'REF_SAP_MATDOC_NO',index:"REF_SAP_MATDOC_NO", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'REF_SAP_MATDOC_ITEM_NO', name: 'REF_SAP_MATDOC_ITEM_NO',index:"REF_SAP_MATDOC_ITEM_NO", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'REF_SAP_MATDOC_YEAR', name: 'REF_SAP_MATDOC_YEAR',index:"REF_SAP_MATDOC_YEAR", width: "50",align:"center",sortable:false,hidden:true},
	        ],
	        viewrecords: true,
			grouping: true,
			groupingView: {
				groupField: ["stoStr"],
				groupColumnShow: [false],
				groupText: ["<b>{0}</b>"],
				groupOrder: ["ASC"],
				groupSummary: [true],
				groupCollapse: false
			},
			shrinkToFit: false,
			showCheckbox:true,
			cellEdit:true,
			cellurl:'#',
			cellsubmit:'clientArray',
			beforeEditCell:function(rowid, cellname, v, iRow, iCol){
				lastrow=iRow;
				lastcell=iCol;
			},
			beforeSelectRow : function(rowid, e) { // 点击复选框才选中行
				var $myGrid = $(this), i = $.jgrid.getCellIndex($(e.target).closest('td')[0]), cm = $myGrid.jqGrid('getGridParam', 'colModel');
				if (cm[i].name == 'cb') {
					if('undefined' != typeof($(e.target).attr('id'))){					
						$('#dataGrid').jqGrid('setSelection', rowid);
					}else{
						return false;
					}
				}
				return false;//(cm[i].name == 'cb');
			},
			onSelectRow:function(rowid,status,e){
				console.log("-->rowid : " + rowid);
				var tr_num = 0;
				
				var ischecked=$("#jqg_dataGrid_"+rowid).is(":checked");
				if(ischecked){
					if($("#sp"+mydata2[rowid-1].g_id).val()!=""){tr_num = parseFloat($("#sp"+mydata2[rowid-1].g_id).val());}
					console.log("sp " + mydata2[rowid-1].g_id + " : " + $("#sp"+mydata2[rowid-1].g_id).val());
					tr_num = parseFloat(tr_num) + parseFloat(mydata2[rowid-1].QTY3);
					console.log("-->stonum = " + mydata2[rowid-1].stonum + "|tr_num = " + tr_num 
							+ "|gid=" + mydata2[rowid-1].g_id);
					
					$(".sp"+mydata2[rowid-1].g_id).html(tr_num);
					$("#sp"+mydata2[rowid-1].g_id).val(tr_num);
					if(tr_num <= mydata2[rowid-1].stonum){
						$("#btnCreat").removeAttr("disabled");
						$(".sp"+mydata2[rowid-1].g_id).css("color","blue");
					}else{
						$(".sp"+mydata2[rowid-1].g_id).css("color","red");
						$("#btnCreat").attr("disabled","disabled");	
						js.showMessage("当前勾选数量大于可退货数量，请重新选择！");
					}
				}else{
					if($("#sp"+mydata2[rowid-1].g_id).val()!=""){tr_num = parseFloat($("#sp"+mydata2[rowid-1].g_id).val());}
					tr_num = parseFloat(tr_num) - parseFloat(mydata2[rowid-1].QTY3);
					$(".sp"+mydata2[rowid-1].g_id).html(tr_num);
					$("#sp"+mydata2[rowid-1].g_id).val(tr_num);
					if(tr_num <= mydata2[rowid-1].stonum){
						$("#btnCreat").removeAttr("disabled");
						$(".sp"+mydata2[rowid-1].g_id).css("color","blue");
					}else{
						$("#btnCreat").attr("disabled","disabled");	
						$(".sp"+mydata2[rowid-1].g_id).css("color","red");
					}
				}
			},
			onSelectAll:function(aRowids,status){
				console.log("--->onSelectAll~~",status);
				
				var trs=$("#dataGrid").children("tbody").children("tr");
				var g_id = "0";
				var tr_num =0;
				var check = false;
				$.each(trs,function(index,tr){
					var num = $(tr).find("td").eq(20).html();
					if(!isNaN($(tr).attr("id"))){
						console.log("id:" + $(tr).attr("id") + "|" + mydata2[$(tr).attr("id")-1].g_id + "|" + num);
						var g_id2 = mydata2[$(tr).attr("id")-1].g_id;//$(tr).find("td").eq(10).html();
						console.log("g_id = " + g_id + " | g_id2 = " + g_id2);
						if(g_id == "0" || g_id != g_id2){
							tr_num = parseFloat(num);
						}else{
							tr_num += parseFloat(num);
						}
						console.log("-->tr_num = " + tr_num);
						tr_num = status?tr_num:0;
						$(".sp"+g_id2).html(tr_num);
						$("#sp"+g_id2).val(tr_num);
						if(tr_num <= mydata2[$(tr).attr("id")-1].stonum){
							$(".sp"+g_id2).css("color","blue");
							if(!status)$("#btnCreat").removeAttr("disabled");
						}else{
							$(".sp"+g_id2).css("color","red");
							$("#btnCreat").attr("disabled","disabled");	
							check = true;
						}
						g_id = g_id2;
					}
					
				});
				if(check){
					js.showMessage("当前勾选数量大于可退货数量，请重新选择！");
				}
			},
			afterSaveCell:function(rowid, cellname, value, iRow, iCol){		
				var row =  $("#dataGrid").jqGrid('getRowData', rowid);
				console.log("rowid：" + rowid);
				if(cellname=='QTY3'){
					if("" == value){
						alert("退货数量不能为空！");
						$("#dataGrid").jqGrid('setCell', rowid, cellname, mydata2[rowid-1].QTY2,'editable-cell');
						mydata2[rowid-1].QTY3 = mydata2[rowid-1].QTY2;
						check = false;
						return false;
					}
					if(parseFloat(value) > parseFloat(row.QTY2)){
						alert("退货数量不能大于可退数量！");
						$("#dataGrid").jqGrid('setCell', rowid, cellname, mydata2[rowid-1].QTY2,'editable-cell');
						mydata2[rowid-1].QTY3 = mydata2[rowid-1].QTY2;
						check = false;
					}else{
						mydata2[rowid-1].QTY3 = value;
						check = true;
					}
				}
				if(cellname=='REASON'){
					if(value != undefined){
						mydata[iRow-1].REASON = value;
					}else{
						mydata[iRow-1].REASON = allreason;
					}
				}
				$('#dataGrid').jqGrid('setGridParam',{data: mydata2}).trigger( 'reloadGrid' );
			},
	    });
		$("#dataGrid").jqGrid("setFrozenColumns");
	}
	
	
});
function freason(){
	console.log("freason");
	var reason = "";
	var tdIndex = 0;
	var trs=$("#dataGrid").children("tbody").children("tr");
	$.each(trs,function(index,tr){
		var cbx=$(tr).find("td").find("input").attr("type");
		if(cbx!=undefined){	
			var c_checkbox=$(tr).find('input[type=checkbox]');
			var ischecked=$(c_checkbox).is(":checked");
			if(ischecked){
				if(reason == ""){
					reason = $(tr).find("td").eq(21).html();
					var che =$(tr).find("td").eq(21).find("input").attr("type");
					if(che!=undefined){	
						tdIndex = index;
					}else{
						return false;
					}
				}
			}
		}
	});
	if(reason == ""){
		if("25"==$("#business_name").val()){
			reason = mydata[0].REASON;
		}else if("26"==$("#business_name").val()){
			reason = mydata[0].REASON;
		}
		else if("27"==$("#business_name").val()){
			reason = mydata2[0].REASON;
		}
	}
	allreason = reason
	var trs=$("#dataGrid").children("tbody").children("tr");
	$.each(trs,function(index,tr){
		if(index >tdIndex){
			if("undefined" != typeof($(tr).attr("id"))){
				$(tr).find("td").eq(21).html(reason);
			}
			console.log("---->index = " + index + "|mydata.length = " + mydata.length)
			mydata[index-1].REASON = reason;
		}
	});
	for (var i = tdIndex; i < mydata2.length; i++) {
		console.log("---->i = " + i)
		mydata2[i].REASON = reason;
	}
}

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