var mydata = [];
var lastrow,lastcell,lastcellname;  
var vm = new Vue({
	el : '#rrapp',
	data : {
		businessList:[],
		formUrl:baseUrl+"account/wmsAccountKPO/listKPOMat",
		GR_WERKS:"",
		WERKS:"",
		whList:[],
		WH_NUMBER:'',
		PO_NO:'',
		MATNR:'',
		LIFNR:'',//供应商代码
		BEDNR:'', //需求跟踪号
		lgortList:[],
	},
	watch:{
		GR_WERKS:{
			handler:function(newVal,oldVal){
					 vm.whList = [];
					 vm.lgortList = [];
			         this.$nextTick(function(){
			        	 
						 $.ajax({
							    url:baseURL+"common/getWhDataByWerks",
								dataType : "json",
								type : "post",
								data : {
									"WERKS":newVal
								},
								async: true,
					        	  success:function(resp){
					        		 if(resp && resp.data.length>0){
						        		 vm.whList = resp.data;
						        		 vm.WH_NUMBER = resp.data[0].WH_NUMBER;
					        		 }
					        	  }
				          })
			        	  
					})
				}
		},
	},
	methods : {
		refresh : function() {
			$('#dataGrid').trigger('reloadGrid');
		},
		query: function () {
			if(vm.PO_NO.trim().length==0 && vm.LIFNR.trim().length==0){
				js.showErrorMessage("请输入采购订单或供应商代码！");
				return false;
			}
			//$("#LGORT_NAME").val($("#LGORT").find('option:selected').attr("logrt_name"));
			vm.$nextTick(function(){//数据渲染后调用
				ajaxQuery();
			})
	
		},
		post_101: function(){
			var selectedRows = [];
			var flag=true;
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			var rows = getSelectedRows();
			if(rows<=0){
				//js.showMessage("请选择需要保存的行项目数据！");	
				alert("请选择一条或多条记录！");
				flag = false;
				return false;
			}
			//需收货数量(101)必须大于0，否则不能选中
			$.each(rows,function(i,rowid){
				var row = $("#dataGrid").jqGrid("getRowData",rowid);
				if(row.QTY_SAP=="" || Number(row.QTY_SAP) <=0){
					js.showErrorMessage("第"+rowid+"需收货数量(101)必须大于0！");	
					flag=false;
					return false;
				}
				selectedRows.push(row);
			});
			//var rowData = $("#dataGrid").jqGrid('getRowData'); 表格所有行项目数量
			if(flag && selectedRows.length>0){
				js.loading();
				$("#btnConfirm").val("过账中...");	
				$("#btnConfirm").attr("disabled","disabled");
				$("#btnSearchData").attr("disabled","disabled");	
				
				/**
				 * 跨工厂收货账务处理
				 */
				$.ajax({
					url:baseUrl+"account/wmsAccountKPO/postGR",
					type:"post",
					async:true,
					dataType:"json",
					data:{
						matList:JSON.stringify(selectedRows),
						WERKS:vm.WERKS,
						GR_WERKS: vm.GR_WERKS,
						WH_NUMBER:vm.WH_NUMBER,
						PZ_DATE:$("#PZ_DATE").val(),
						JZ_DATE:$("#JZ_DATE").val(),
					},
					success:function(response){
						if(response.code==500){
							$("#btnConfirm").val("101收货");	
							$("#btnConfirm").removeAttr("disabled");
							$("#btnSearchData").removeAttr("disabled");
							js.closeLoading();
							js.showErrorMessage(response.msg);
							return false;
						}
						
						if(response.code=='0'){
							js.showMessage(response.msg);
			            	$("#rtnMst").html(response.msg);
			            	layer.open({
			            		type : 1,
			            		offset : '50px',
			            		skin : 'layui-layer-molv',
			            		title : "跨工厂收货账务处理成功！",
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
						}
						
						$('#dataGrid').jqGrid("clearGridData");
						$("#btnConfirm").val("101收货");	
						$("#btnConfirm").removeAttr("disabled");
						$("#btnSearchData").removeAttr("disabled");
						js.closeLoading();
						
						fun_tab();
					}
				});
				js.closeLoading();
				$("#btnConfirm").html("101收货");	
				$("#btnConfirm").removeAttr("disabled");
				$("#btnSearchData").removeAttr("disabled");
			}
		},
		transfer: function(){
			var selectedRows = [];
			var flag=true;
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			var rows = getSelectedRows();
			if(rows<=0){
				//js.showMessage("请选择需要保存的行项目数据！");	
				alert("请选择一条或多条记录！");
				flag = false;
				return false;
			}
			//需调拨数量(STO)必须大于0，否则不能选中
			$.each(rows,function(i,rowid){
				var row = $("#dataGrid").jqGrid("getRowData",rowid);
				if(row.QTY_TRANSFER=="" || Number(row.QTY_TRANSFER) <=0){
					js.showErrorMessage("第"+rowid+"需调拨数量(STO)必须大于0！");	
					flag=false;
					return false;
				}
				selectedRows.push(row);
			});
			if(flag && selectedRows.length>0){
				js.showMessage('功能完善中...');
			}
		}
	},
	created:function(){
		this.WERKS=$("#WERKS").find("option").first().val();
		this.GR_WERKS=$("#GR_WERKS").find("option").first().val();
	}
});

$(function(){
	$("#PO_NO").focus();
	var now = new Date(); //当前日期
	var curDate = formatDate(now);
	var startDate=new Date(now.getTime()-6*24*3600*1000);
	$("#CREATE_DATE_S").val(formatDate(startDate));//收货日期-开始
	$("#CREATE_DATE_E").val(curDate);
	$("#PZ_DATE").val(curDate);
	$("#JZ_DATE").val(curDate);
	fun_tab();
});


function ajaxQuery(){
	
	$("#btnSearchData").val("查询中...");
	$("#btnSearchData").attr("disabled","disabled");	
	
	$.ajax({
		url:vm.formUrl,
		dataType : "json",
		type : "post",
		data : {
			"GR_WERKS":vm.GR_WERKS,
			"WERKS":vm.WERKS,
			"WH_NUMBER":vm.WH_NUMBER,
			"LIFNR":vm.LIFNR,
			"BEDNR":vm.BEDNR,
			"PO_NO":vm.PO_NO,
			"MATNR":vm.MATNR,
			"CREATE_DATE_S":$("#CREATE_DATE_S").val(),
			"CREATE_DATE_E":$("#CREATE_DATE_E").val(),
		},
		async: true,
		success: function (response) { 
			$("#dataGrid").jqGrid("clearGridData", true);
			mydata.length=0;
			if(response.code == "0"){
				mydata = response.result;
				$("#dataGrid").jqGrid('GridUnload');	
				if(mydata.length=="")
					alert("未查询到符合条件的数据！");
				fun_tab();
			}else{
				alert(response.msg)
			}
			$("#btnSearchData").val("查询");
			$("#btnSearchData").removeAttr("disabled");
		}
	});

}


function fun_tab(){
	$("#dataGrid").dataGrid({
		datatype: "local",
		data: mydata,
        colModel: [
			{label:'序号',name:'ID', index: 'ID',key: true,width:"40",align:"center" ,hidden:false,formatter:function(val, obj, row, act){
				return obj.rowId
			}},
			/*{label: '价格有效', name: 'PRICE_FLAG',index:"PRICE_FLAG", width: "70",align:"center",sortable:false,frozen: true,},*/
			{label: '收货工厂', name: 'GR_WERKS',index:"GR_WERKS", width: "65",align:"center",sortable:false,frozen: true,},
			{label: '收货仓库号', name: 'WH_NUMBER',index:"WH_NUMBER", width: "75",align:"center",sortable:false,frozen: true,},
            {label: 'WMS凭证号', name: 'REF_WMS_NO',index:"REF_WMS_NO", width: "130",align:"center",sortable:false,frozen: true,},
            {label: '行项目', name: 'REF_WMS_ITEM_NO',index:"REF_WMS_ITEM_NO", width: "60",align:"center",sortable:false,frozen: true},
            {label: 'PO工厂', name: 'WERKS',index:"WERKS", width: "65",align:"center",sortable:false,frozen: true,},
            {label: '采购订单', name: 'PO_NO',index:"PO_NO", width: "90",align:"center",sortable:true,frozen: true},
            {label: '行项目', name: 'PO_ITEM_NO',index:"PO_ITEM_NO", width: "60",align:"center",sortable:true,frozen: true},
            {label: '料号', name: 'MATNR',index:"MATNR", width: "90",align:"center",sortable:true,frozen: true},
            {label: '物料描述', name: 'MAKTX',index:"MAKTX", width: "250",align:"center",sortable:false},
            {label: '批次', name: 'BATCH',index:"BATCH", width: "150",align:"center",sortable:false},
            {label: '库存类型', name: 'SOBKZ',index:"SOBKZ", width: "70",align:"center",sortable:false,frozen: true},
   			{label: '进仓数量', name: 'QTY_WMS',index:"QTY_WMS", width: "80",align:"center",sortable:true},
   			{label: '需收货数量(101)', name: 'QTY_SAP_101',index:"QTY_SAP_101", width: "120",align:"center",sortable:true},
   			{label: '需调拨数量(STO)', name: 'QTY_TRANSFER',index:"QTY_TRANSFER", width: "120",align:"center",sortable:true},
            {label: '库位', name: 'LGORT',index:"LGORT", width: "70",align:"center",sortable:false}, 
/*            {label: '<span style="color:blue">库位 </span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'LGORT\')" title="向下填充" style="color:green" aria-hidden="true"></i> ', 
            name: 'LGORT',align:"center", index: 'LGORT', width: 70 ,sortable:false,editable:true,edittype:'select'},*/
            {label: '单位', name: 'UNIT',index:"UNIT", width: "50",align:"center",sortable:false}, 
            {label: '需求跟踪号', name: 'BEDNR',index:"BEDNR", width: "90",align:"center",sortable:false}, 
            {label: '收货日期', name: 'CREATE_DATE',index:"CREATE_DATE", width: "100",align:"center",sortable:false,},
            {label: '供应商代码', name: 'LIFNR',index:"LIFNR", width: "100",align:"center",sortable:true}, 
            {label: '供应商描述', name: 'LIKTX',index:"LIKTX", width: "200",align:"center",sortable:false}, 
   			{ label: '<span style="color:blue">行文本</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'ITEM_TEXT\')" title="向下填充" style="color:green" aria-hidden="true"></i>', 
   				name: 'ITEM_TEXT', align:"center",index: 'ITEM_TEXT', width: 280,editable:true,sortable:false,},            
            {label:'RECEIVER',name:'RECEIVER',hidden:true},/* 收料员 */
            {label:'RECEIPT_NO',name:'RECEIPT_NO',hidden:true},
            {label:'BATCH_SAP',name:'BATCH_SAP',hidden:true},
            {label:'RECEIPT_ITEM_NO',name:'RECEIPT_ITEM_NO',hidden:true},
            {label:'INBOUND_NO',name:'INBOUND_NO',hidden:true},
            {label:'INBOUND_ITEM_NO',name:'INBOUND_ITEM_NO',hidden:true},
        ],
		formatCell:function(rowid, cellname, value, iRow, iCol){
			//var row =  $("#dataGrid").jqGrid('getRowData', rowid);
/*			if(cellname=='LGORT'){
	            $('#dataGrid').jqGrid('setColProp', 'LGORT', { editoptions: {value:getLotOption(vm.WERKS)}
	            });
			}
			if(cellname=='SOBKZ'){					
	            $('#dataGrid').jqGrid('setColProp', 'SOBKZ', { editoptions: {value:getSobzkOption()}
	            });
			}*/
		},
	    beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
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

		},
		shrinkToFit: false,
        width:1900,
        showRownum:false,
        cellEdit:true,
        cellurl:'#',
		cellsubmit:'clientArray',
	    viewrecords: false,
        showCheckbox:true,
        autoScroll: true, 
        rowNum:-1,
    });
	
	$("#dataGrid").jqGrid("setFrozenColumns");
}

function fillDown(e){
	$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
	var ids=$("#dataGrid").getGridParam("selarrrow");
	var rowid=lastrow;
	var rows=$("#dataGrid").jqGrid('getRowData');
	var last_row=$("#dataGrid").jqGrid('getRowData',rowid);
	var cellvalue=last_row[lastcellname];
	if(lastcellname==e){
		$.each(rows,function(i,row){
			if(Number(row.ID)>=Number(rowid)&&cellvalue){
				if(e == 'PO_NO'){
					//var poInfo = getPoItemInfo(cellvalue);
					var poInfo = last_row.last_row;
					var poItemOptions = last_row.poItemOptions;
/*					for(i=0;i<poInfo.length;i++){
		 				if(i != poInfo.length - 1) {
		 					poItemOptions += poInfo[i].EBELP + ":" +poInfo[i].EBELP + ";";
		 				} else {
		 					poItemOptions += poInfo[i].EBELP + ":" + poInfo[i].EBELP;
		 				}
					}*/
					$("#dataGrid").jqGrid('setCell',Number(row.ID),"poInfo",poInfo);
					$("#dataGrid").jqGrid('setCell',Number(row.ID),"poItemOptions",poItemOptions);
				}
				$("#dataGrid").jqGrid('setCell',Number(row.ID), e, cellvalue,'editable-cell');
			}
		})
	}
}
