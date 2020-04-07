var mydata = [];
var check = true;
var lastrow,lastcell;  
var vm = new Vue({
	el : '#rrapp',
	data:{warehourse:[]},
	methods : {
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
			
		}
	}
});

$(function(){
	$("#out_no").focus();	
	
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
	
	fun_showtab();
	
	$("#btnSearchData").click(function () {

		if(""==$("#pono").val()){
			alert("采购订单不能为空！");
			return false;
		}
		$("#btnSearchData").val("查询中...");
		$("#btnSearchData").attr("disabled","disabled");	
		
		$.ajax({
			url:baseURL+"returngoods/getWareHouseOutPickupData",
			dataType : "json",
			type : "post",
			data : {
				"WERKS":$("#werks").val(),
				"WH_NUMBER":$("#wh").val(),
				"STATUS":$("#status").val(),
				"OUT_NO":$("#out_no").val()
			},
			async: true,
			success: function (response) { 
				$("#dataGrid").jqGrid("clearGridData", true);
				mydata.length=0;
				if(response.code == "0"){
					mydata = response.result;
					$("#dataGrid").jqGrid('GridUnload');	
					fun_showtab();
					if(mydata.length==""){
						alert("无此退货单信息！");
					}else{
						console.log("-->RETURN_STATUS = " + mydata[0].RETURN_STATUS)
						//9.3.2.5如单据“删除标示”=X，则系统返回消息：该退货单号：***************已删除。
						if("X" == mydata[0].DEL){
							$("#btnCancel").attr("disabled","disabled");	
							$("#btnCreate").attr("disabled","disabled");	
							alert("退货单号："+$("#out_no").val()+"已删除!");
						}
						
						//9.3.2.4如单据状态为“完成”，则系统返回消息：退货单号：***************已完成退货。
						if("03" == mydata[0].RETURN_STATUS){
							$("#btnCancel").attr("disabled","disabled");	
							$("#btnCreate").attr("disabled","disabled");	
							alert("退货单号："+$("#out_no").val()+"已完成退货!");
						}
						
						if("0" == mydata[0].BIN_CODE_XJ){
							$("#btnCancel").attr("disabled","disabled");	
							$("#btnCreate").attr("disabled","disabled");	
							alert("仓库号"+$("#wh").val()+"没有维护拣配位!");
						}
						
						if("0" == mydata[0].PICK_FLAG){
							$("#btnCancel").attr("disabled","disabled");	
							$("#btnCreate").attr("disabled","disabled");	
							alert("退货单拣配标识≠X，无需拣配!");
						}
						
						/*//9.3.2.2	针对“创建”未删除状态的退货单行项目，查询成功以列表形式展示查询结果，当退货单行项目为“创建”状态时，不能执行“取消下架”动作。
						if("00" == mydata[0].RETURN_STATUS){
							$("#btnCancel").attr("disabled","disabled");
							$("#btnCreate").removeAttr("disabled");
						}
						//9.3.2.3	针对退货单行项目状态为“下架”未删除状态的行项目， 查询成功以列表形式展示查询结果，允许执行“取消下架”操作，不允许再次操作下架。
						if("02" == mydata[0].RETURN_STATUS){
							$("#btnCancel").removeAttr("disabled");
							$("#btnCreate").attr("disabled","disabled");
						}*/
					}
					
				}else{
					alert(response.msg)
				}
				$("#btnSearchData").val("查询");
				$("#btnSearchData").removeAttr("disabled");
				$("#jqg_dataGrid_2").attr("disabled","disabled");	
			}
		});
	});
	
	$("#btnReset").click(function () {
		$("#dataGrid").jqGrid("clearGridData", true);
		$("#dataGrid").jqGrid("setFrozenColumns");
		$("#btnCreate").attr("disabled","disabled");	
		$("#btnCancel").attr("disabled","disabled");	
		//console.log(document.getElementById("sp1").innerHTML);
		//document.getElementById("sp1").innerHTML="111111";
	});
	$("#btnCreate").click(function () {
		//校验数据
		var trs=$("#dataGrid").children("tbody").children("tr");
		//var total = 0;var freeze = 0;
		var total2 = 0;var freeze2 = 0; var matnr="";
		$.each(trs,function(index,tr){
			var cbx=$(tr).find("td").find("input").attr("type");
			
			if(cbx!=undefined){	
				var c_checkbox=$(tr).find('input[type=checkbox]');
				var ischecked=$(c_checkbox).is(":checked");
				
				//增加针对料号相同行项目不同的情况的校验
				if(ischecked){
					if(matnr == ""){
						total2 = parseFloat(mydata[$(tr).find("td").eq(0).html()-1].QTY1);
						freeze2 = parseFloat(mydata[$(tr).find("td").eq(0).html()-1].FREEZE_QTY);
					}else{
						if($(tr).find("td").eq(3).html() == matnr){
							total2 += parseFloat(mydata[$(tr).find("td").eq(0).html()-1].QTY1);
						}else{
							total2 = parseFloat(mydata[$(tr).find("td").eq(0).html()-1].QTY1);
							freeze2 = parseFloat(mydata[$(tr).find("td").eq(0).html()-1].FREEZE_QTY);
						}
					}
					//拣配只能选择状态为00已创建的行
					if("00" != mydata[$(tr).find("td").eq(0).html()-1].ITEM_STATUS){
						alert("拣配下架只能选择状态为[创建]的行！");
						check = false;
						return false;
					}
					
					console.log("-->" + matnr + "|total2=" + total2 + "|freeze2=" + freeze2);
					if(total2 > freeze2){
						alert("相同物料退货数量之和不能超过该物料冻结数量！");
						check = false;
						return false;
					}
					matnr = $(tr).find("td").eq(2).html();
				}
			}
			
		});
		
		$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
		if(check){	//防止用户在输入数量的文本框没有消失就直接点创建
			var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");	
			var BUSINESS_TYPE = "";
			var arrList = new Array();
			if(ids.length == 0){
				alert("当前还没有勾选任何行项目数据！");
				return false;
			}
			for (var i = 0; i < ids.length; i++) {
				arrList[i] = $("#dataGrid").jqGrid('getRowData',ids[i]);
			}
			
			//console.log(ids);
			console.log(arrList);
			
			$("#btnCreate").val("正在操作..");	
			$("#btnCreate").attr("disabled","disabled");
			
			$.ajax({
				url:baseURL+"returngoods/wareHouseOutPickup",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":$("#werks").val(),
					"WH_NUMBER":$("#wh").val(),
					"ARRLIST":JSON.stringify(arrList)
				},
				async: true,
				success: function (response) {
					if(response.code=='0'){
						js.showMessage("操作成功");
		            	$("#btnCreate").val("拣配下架");
		            	//$("#outNo").html(response.outNo);
		            	/*layer.open({
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
		    					$("#btnCreate").removeAttr("disabled");
		            		}
		            	});*/
					}else{
						js.showMessage(response.msg);
		            	$("#btnCreate").val("拣配下架");
    					$("#btnCreate").removeAttr("disabled");
					}
					$("#btnSearchData").click();
				}
			});
		}
		check = true;
	});
	
	$("#btnPrint").click(function () {
		//打印输出出仓拣配单（单据模板参照出库模块拣配单模板）。
		window.open(baseURL+"docPrint/wareHouseOutPickupPrint?PageSize=0&outNo=" + $("#out_no").val());
	})
	
	$("#btnCancel").click(function () {
		var trs=$("#dataGrid").children("tbody").children("tr");
		$.each(trs,function(index,tr){
			var cbx=$(tr).find("td").find("input").attr("type");
			if(cbx!=undefined){	
				var c_checkbox=$(tr).find('input[type=checkbox]');
				var ischecked=$(c_checkbox).is(":checked");
				if(ischecked){
					//取消下架只能选择状态为02已下架的行
					if("02" != mydata[$(tr).find("td").eq(0).html()-1].ITEM_STATUS){
						alert("取消下架只能选择状态为[下架]的行！");
						check = false;
						return false;
					}
				}
			}
		});
		
		if(check){
			var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");	
			var BUSINESS_TYPE = "";
			var arrList = new Array();
			if(ids.length == 0){
				alert("当前还没有勾选任何行项目数据！");
				return false;
			}
			for (var i = 0; i < ids.length; i++) {
				arrList[i] = $("#dataGrid").jqGrid('getRowData',ids[i]);
			}
			console.log(arrList);

			$("#btnCancel").val("正在操作..");	
			$("#btnCancel").attr("disabled","disabled");
			$.ajax({
				url:baseURL+"returngoods/wareHouseOutPickupCancel",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":$("#werks").val(),
					"WH_NUMBER":$("#wh").val(),
					"ARRLIST":JSON.stringify(arrList)
				},
				async: true,
				success: function (response) {
					if(response.code=='0'){
						alert("取消下架成功");
						$("#btnSearchData").click();
					}
					$("#btnCancel").val("取消下架");	
					$("#btnCancel").removeAttr("disabled");
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
	
	function fun_showtab(){
		$("#dataGrid").dataGrid({
			datatype: "local",
			data: mydata,
	        colModel: [		
	        	{label: '工厂', name: 'WERKS',index:"WERKS", width: "100",align:"center",sortable:false,frozen: true,
	            	formatter:function(val){
	            		return '<p style="height:20px;padding-top:7px">'+val+'</p>';
	            	}
	            },	
	            {label: '料号', name: 'MATNR',index:"MATNR", width: "100",align:"center",sortable:false,frozen: true,},
	            {label: '物料描述', name: 'MAKTX',index:"MAKTX", width: "200",align:"center",sortable:false,frozen: true,
	            	formatter:function(val){
	            		return val;
	            	}
	            },
	            {label: '冻结数量', name: 'FREEZE_QTY',index:"FREEZE_QTY", width: "80",align:"center",sortable:true,frozen: true},  
	            {label: '退货数量', name: 'RETURN_QTY',index:"RETURN_QTY", width: "80",align:"center",sortable:true,frozen: true},  
	            {label: '<span style="color:red">*</span><span style="color:blue"><b>下架数量</b></span>', name: 'QTY1',index:"QTY1", width: "100",align:"center",sortable:false,editable:true,
	            	formatter:'number',editrules:{number:true},
	            	formatoptions:{decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}
	            },     
	            {label: '批次', name: 'BATCH',index:"BATCH", width: "100",align:"center",sortable:true,frozen: true},     
	            {label: '储位', name: 'BIN_CODE',index:"BIN_CODE", width: "60",align:"center",sortable:true},       
	            {label: '库位', name: 'LGORT',index:"LGORT", width: "60",align:"center",sortable:true},          
	            {label: '单位', name: 'UNIT',index:"UNIT", width: "80",align:"center",sortable:true},      
	            {label: '库存类型', name: 'SOBKZ',index:"SOBKZ", width: "80",align:"center",sortable:true},      
	            {label: '供应商代码', name: 'LIFNR',index:"LIFNR", width: "100",align:"center",sortable:true},      
	            {label: '供应商名称', name: 'LIKTX',index:"LIKTX", width: "100",align:"center",sortable:true},
	            {label: '状态', name: 'ITEM_STATUS',index:"ITEM_STATUS", width: "50",align:"center",sortable:false,
	            	formatter:function(val){	//00创建  02下架  03完成
	            		if("00"==val)return "创建";
	            		if("02"==val)return "下架";
	            		if("03"==val)return "完成";
	            	}
	            },
	            {label: 'FREEZE_QTY', name: 'FREEZE_QTY',index:"FREEZE_QTY", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'RETURN_STATUS', name: 'RETURN_STATUS',index:"RETURN_STATUS", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'RETURN_NO', name: 'RETURN_NO',index:"RETURN_NO", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'RETURN_ITEM_NO', name: 'RETURN_ITEM_NO',index:"RETURN_ITEM_NO", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'BUSINESS_NAME', name: 'BUSINESS_NAME',index:"BUSINESS_NAME", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'BUSINESS_TYPE', name: 'BUSINESS_TYPE',index:"BUSINESS_TYPE", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'I_ID', name: 'I_ID',index:"I_ID", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'S_ID', name: 'S_ID',index:"S_ID", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'D_ID', name: 'D_ID',index:"D_ID", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'PO_NO', name: 'PO_NO',index:"PO_NO", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'PO_ITEM_NO', name: 'PO_ITEM_NO',index:"PO_ITEM_NO", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'MEINS', name: 'MEINS',index:"MEINS", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'LMEIN', name: 'LMEIN',index:"LMEIN", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'UMREN', name: 'UMREN',index:"UMREN", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'UMREZ', name: 'UMREZ',index:"UMREZ", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'BIN_CODE_XJ', name: 'BIN_CODE_XJ',index:"BIN_CODE_XJ", width: "50",align:"center",sortable:false,hidden:true},
	            ],
	        viewrecords: true,
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
				//console.log("-->FREEZE_QTY : " + row.FREEZE_QTY);
				if(cellname=='QTY1'){
					if("" == value){
						alert("下架数量不能为空！");
						$("#dataGrid").jqGrid('setCell', rowid, cellname, mydata[rowid-1].QTY1,'editable-cell');
						mydata[rowid-1].QTY3 = row.QTY2;
						check = false;
						return false;
					}
					if(parseFloat(value) > parseFloat(row.RETURN_QTY)){
						alert("下架数量不能大于退货数量！");
						$("#dataGrid").jqGrid('setCell', rowid, cellname, mydata[rowid-1].QTY1,'editable-cell');
						mydata[rowid-1].QTY1 = row.RETURN_QTY;
						check = false;
					}else{
						mydata[rowid-1].QTY1 = value;
						check = true;
					}
				}
				$('#dataGrid').jqGrid('setGridParam',{data: mydata}).trigger( 'reloadGrid' );
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

	$("#btnCreate").attr("disabled","disabled");	
	$("#btnCancel").attr("disabled","disabled");
});
