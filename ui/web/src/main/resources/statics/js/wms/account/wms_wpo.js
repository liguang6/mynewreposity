var mydata = [];
var lastrow,lastcell,lastcellname;  
var sobkz_list=[];
var globalPo = {};
var vm = new Vue({
	el : '#rrapp',
	data : {
		businessList:[],
		formUrl:baseUrl+"account/wmsAccountWPO/listWPOMat",
		WERKS:"",
		whList:[],
		WH_NUMBER:'',
		LIFNR:'',//供应商代码
		BEDNR:'', //需求跟踪号
		lgortList:[],
	},
	watch:{
		WERKS:{
			handler:function(newVal,oldVal){
					console.info(newVal);
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
		        		  vm.lgortList = getLgortList(newVal,null,null,'Z')
			        	  
					})
				}
		},
	},
	methods : {
		refresh : function() {
			$('#dataGrid').trigger('reloadGrid');
		},
		query: function () {
			if(vm.LIFNR.trim().length==0){
				js.showErrorMessage("请输入供应商代码！");
				return false;
			}
			//$("#LGORT_NAME").val($("#LGORT").find('option:selected').attr("logrt_name"));
			vm.$nextTick(function(){//数据渲染后调用
				ajaxQuery();
			})
	
		},
		post: function(){
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
			//必填字段判断，未填不能选中
			$.each(rows,function(i,rowid){
				var row = $("#dataGrid").jqGrid("getRowData",rowid);
				if(row.PO_NO==""){
					js.showErrorMessage("第"+rowid+"行必填字段（采购订单号）未填写完整！");	
					flag=false;
					return false;
				}
				if(row.PO_ITEM_NO==""){
					js.showErrorMessage("第"+rowid+"行必填字段（采购订单行项目号）未填写完整！");	
					flag=false;
					return false;
				}
				selectedRows.push(row);
			});
			//var rowData = $("#dataGrid").jqGrid('getRowData'); 表格所有行项目数量
			if(flag){
				js.loading("处理中...");
				$("#btnConfirm").val("过账中...");	
				$("#btnConfirm").attr("disabled","disabled");
				$("#btnSearchData").attr("disabled","disabled");	
				
				/**
				 * 无PO收货账务处理
				 */
				$.ajax({
					url:baseUrl+"account/wmsAccountWPO/postGI",
					type:"post",
					async:true,
					dataType:"json",
					data:{
						matList:JSON.stringify(selectedRows),
						WERKS:vm.WERKS,
						WH_NUMBER:vm.WH_NUMBER,
						PZ_DATE:$("#PZ_DATE").val(),
						JZ_DATE:$("#JZ_DATE").val(),
					},
					success:function(response){
						if(response.code==500){
							$("#btnConfirm").val("过账");	
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
			            		title : "无PO收货账务处理成功！",
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
						$("#btnConfirm").val("过账");	
						$("#btnConfirm").removeAttr("disabled");
						$("#btnSearchData").removeAttr("disabled");
						js.closeLoading();
						
						fun_tab();
					}
				});
				js.closeLoading();
				$("#btnConfirm").html("过账");	
				$("#btnConfirm").removeAttr("disabled");
				$("#btnSearchData").removeAttr("disabled");
			}
		}
	},
	created:function(){
		this.WERKS=$("#WERKS").find("option").first().val();
	}
});

$(function(){
	$("#LIFNR").focus();
	var now = new Date(); //当前日期
	var curDate = formatDate(now);
	var startDate=new Date(now.getTime()-6*24*3600*1000);
	$("#CREATE_DATE_S").val(formatDate(startDate));//收货日期-开始
	$("#CREATE_DATE_E").val(curDate);
	$("#PZ_DATE").val(curDate);
	$("#JZ_DATE").val(curDate);
	sobkz_list=getDictList('SOBKZ');
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
			"WERKS":vm.WERKS,
			"WH_NUMBER":vm.WH_NUMBER,
			"LIFNR":vm.LIFNR,
			"BEDNR":vm.BEDNR,
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
			{label: '工厂', name: 'WERKS',index:"WERKS", width: "55",align:"center",sortable:false,frozen: true,},
			{label: '仓库号', name: 'WH_NUMBER',index:"WH_NUMBER", width: "60",align:"center",sortable:false,frozen: true,},
            {label: '收货单号', name: 'RECEIPT_NO',index:"RECEIPT_NO", width: "110",align:"center",sortable:false,frozen: true,},
            {label: '行项目', name: 'RECEIPT_ITEM_NO',index:"RECEIPT_ITEM_NO", width: "60",align:"center",sortable:false,frozen: true},
            {label: '料号', name: 'MATNR',index:"MATNR", width: "90",align:"center",sortable:true,frozen: true},
            {label: '物料描述', name: 'MAKTX',index:"MAKTX", width: "250",align:"center",sortable:false},
            {label: '库存类型', name: 'SOBKZ',index:"SOBKZ", width: "70",align:"center",sortable:false,frozen: true},
   			/*{label: '<span style="color:blue">库存类型 </span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'SOBKZ\')" title="向下填充" style="color:green" aria-hidden="true"></i> ', 
   					name: 'SOBKZ',align:"center", index: 'SOBKZ', width: "90" ,sortable:false,editable:true,edittype:'select'},*/
   			{label: '需创单数量', name: 'PATCH_QTY',index:"PATCH_QTY", width: "80",align:"center",sortable:true},
			{label: '<span style="color:red">*</span><span style="color:blue"><b>采购订单 </b></span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'PO_NO\')" title="向下填充" style="color:green" aria-hidden="true"></i> ', 
            	name: 'PO_NO',index:"PO_NO", width: "100",sortable:false,align:"center",editable:true},		
            {label: '<span style="color:red">*</span><span style="color:blue"><b>行项目</b></span>', 
            		name: 'PO_ITEM_NO',index:"PO_ITEM_NO", width: "70",align:"center",sortable:false,editable:true,edittype:'select'},
            {label: '库位', name: 'LGORT',index:"LGORT", width: "70",align:"center",sortable:false}, 
/*            {label: '<span style="color:blue">库位 </span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'LGORT\')" title="向下填充" style="color:green" aria-hidden="true"></i> ', 
            name: 'LGORT',align:"center", index: 'LGORT', width: 70 ,sortable:false,editable:true,edittype:'select'},*/
            {label: '需求跟踪号', name: 'BEDNR',index:"BEDNR", width: "90",align:"center",sortable:false}, 
/*            {label: '<span style="color:blue">需求跟踪号 </span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'BEDNR\')" title="向下填充" style="color:green" aria-hidden="true"></i> ',  
            	name: 'BEDNR',index:"BEDNR", width: "100",align:"center",sortable:false,editable:true}, */
            {label: '实收数量', name: 'RECEIPT_QTY',index:"RECEIPT_QTY", width: "80",align:"center",sortable:true},
            {label: '收料房退货数量', name: 'RETURN_QTY',index:"RETURN_QTY", width: "100",align:"center",sortable:true},
            {label: '库房退货数量', name: 'WH_RETURN_QTY',index:"WH_RETURN_QTY", width: "100",align:"center",sortable:true},
            {label: '单位', name: 'UNIT',index:"UNIT", width: "50",align:"center",sortable:false}, 
            {label: '进仓数量', name: 'IN_QTY',index:"IN_QTY", width: "90",align:"center",sortable:false,},
            {label: '收货日期', name: 'RECEIPT_DATE',index:"RECEIPT_DATE", width: "100",align:"center",sortable:false,},
            {label: '供应商代码', name: 'LIFNR',index:"LIFNR", width: "100",align:"center",sortable:true}, 
            {label: '供应商描述', name: 'LIKTX',index:"LIKTX", width: "200",align:"center",sortable:false}, 
            {label:'ITEM_TEXT',name:'ITEM_TEXT',hidden:true},
            {label:'BATCH',name:'BATCH',hidden:true},
            {label:'RECEIVER',name:'RECEIVER',hidden:true},/* 收料员 */
            {label:'AFNAM',name:'AFNAM',hidden:true},/* 申请人 */
            {label:'MAX_MENGE',name:'MAX_MENGE',hidden:true},
            {label:'poItemOptions',name:'poItemOptions',hidden:true},
        ],
		formatCell:function(rowid, cellname, value, iRow, iCol){
			var row =  $("#dataGrid").jqGrid('getRowData', rowid);
/*			if(cellname=='LGORT'){
	            $('#dataGrid').jqGrid('setColProp', 'LGORT', { editoptions: {value:getLotOption(vm.WERKS)}
	            });
			}
			if(cellname=='SOBKZ'){					
	            $('#dataGrid').jqGrid('setColProp', 'SOBKZ', { editoptions: {value:getSobzkOption()}
	            });
			}*/
			if(cellname=='PO_ITEM_NO'){
				if(row.PO_NO == undefined || row.PO_NO == ''){
					alert('请先输入采购订单号！');
		            $('#dataGrid').jqGrid('setColProp', 'PO_ITEM_NO', { editoptions: {value:""}
		            });
				}else if(row.poItemOptions == undefined || row.poItemOptions ==''){
					alert('请输入正确的采购订单号！');
		            $('#dataGrid').jqGrid('setColProp', 'PO_ITEM_NO', { editoptions: {value:""}
		            });
				}else {
		            $('#dataGrid').jqGrid('setColProp', 'PO_ITEM_NO', { editoptions: {value:row.poItemOptions}
		            });
				}

			}
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
			var row =  $("#dataGrid").jqGrid('getRowData', rowid);
			if(cellname=='PO_NO'){
				var poInfo = getPoItemInfo(row.PO_NO);
				if(poInfo==undefined || poInfo==null || poInfo==""){
					alert('采购订单号有误！');
					$("#dataGrid").jqGrid('setCell',rowid,"PO_NO","&nbsp",'editable-cell');
				}else{
					var poFlg = true;
					var poItemOptions = "";
					for(i=0;i<poInfo.length;i++){
						if(poInfo[i].WERKS != row.WERKS){
							alert('输入的采购订单'+row.PO_NO+'工厂不是'+row.WERKS+'！');
							$("#dataGrid").jqGrid('setCell',rowid,"PO_NO","&nbsp",'editable-cell');
							poFlg = false;
						}else{
			 				if(i != poInfo.length - 1) {
			 					poItemOptions += poInfo[i].EBELP + ":" +poInfo[i].EBELP + ";";
			 				} else {
			 					poItemOptions += poInfo[i].EBELP + ":" + poInfo[i].EBELP;
			 				}
						}

					}
					if(poFlg){
						globalPo[row.PO_NO] = poInfo;
						$("#dataGrid").jqGrid('setCell',rowid,"poItemOptions",poItemOptions);
					}
				}

			}
			if(cellname=='PO_ITEM_NO'){
				//校验选择的采购订单及行项目是否满足需求
				var poItemOptions = globalPo[row.PO_NO];//获取填写的采购订单所有行项目信息
				var poItem = null;
				for(ii=0;ii<poItemOptions.length;ii++){
					var poItem_c = poItemOptions[ii];
					if(poItem_c.EBELP == row.PO_ITEM_NO){
						poItem = poItem_c;
						break;
					}
				}
				
				if(poItem != null){
					//选中的行项目号，匹配料号、特殊库存类型、采购单位是否一致
					if(row.MATNR == poItem.MATNR && row.SOBKZ == poItem.SOBKZ && row.UNIT == poItem.MEINS){
						//统计当前页面已选择此采购订单行项目匹配的收料数量
						var poItemQty_t = 0;
						var allRows=$("#dataGrid").jqGrid('getRowData');
						$.each(allRows,function(i,cur_row){
							if(cur_row.PO_ITEM_NO == row.PO_ITEM_NO){
								poItemQty_t += Number(cur_row.PATCH_QTY);
							}
						});
						poItemQty_t = poItemQty_t-Number(row.PATCH_QTY);
						console.info('当前页面已选择此采购订单行项目匹配的收料数量:'+poItemQty_t);
						//匹配，校验采购订单剩余收货数量是否大于等于无PO收货数量
						if(Number(poItem.MAX_MENGE)-Number(poItem.RECEIPTED_QTY)-poItemQty_t < Number(row.PATCH_QTY) ){
							alert('选中的采购订单行项目可收货数量不足！');
							$("#dataGrid").jqGrid('setCell', rowid, 'PO_ITEM_NO', '&nbsp','editable-cell');
							return false;
						}
						$("#dataGrid").jqGrid('setCell', rowid, 'MAX_MENGE', poItem.MAX_MENGE,'editable-cell');
					}else{
						if(row.MATNR != poItem.MATNR){
							alert('选中的采购订单行项目料号不匹配！');
							$("#dataGrid").jqGrid('setCell', rowid, 'PO_ITEM_NO', '&nbsp','editable-cell');
							return false;
						}
						if(row.SOBKZ != poItem.SOBKZ){
							alert('选中的采购订单行项目库存类型不匹配！');
							$("#dataGrid").jqGrid('setCell', rowid, 'PO_ITEM_NO', '&nbsp','editable-cell');
							return false;
						}
						if(row.UNIT != poItem.MEINS){
							alert('选中的采购订单行项目单位不匹配！');
							$("#dataGrid").jqGrid('setCell', rowid, 'PO_ITEM_NO', '&nbsp','editable-cell');
							return false;
						}
					}
				}
				
			}
			$('#dataGrid').jqGrid('setGridParam',{data: mydata}).trigger( 'reloadGrid' );
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
			SOBKZ:SOBKZ
		},
		success:function(resp){
			if(resp.msg=='success'){	
				list=resp.data;
			}
		}			
	})
	return list;
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

function getLotOption(WERKS,PO_NO,PO_ITEM_NO){
	var options="";
	$.ajax({
		url:baseUrl+'common/getLoList',
		type:'post',
		async:false,
		dataType:'json',
		data:{
			WERKS:WERKS,
			PO_NO:PO_NO||'',
			PO_ITEM_NO:PO_ITEM_NO||''
		},
		success:function(resp){
			//alert(resp.msg)
			if(resp.msg=='success'){		
				for(i=0;i<resp.data.length;i++){
	 				if(i != resp.data.length - 1) {
	 					options += resp.data[i].LGORT + ":" +resp.data[i].LGORT + ";";
	 				} else {
	 					options += resp.data[i].LGORT + ":" + resp.data[i].LGORT;
	 				}
				}
				//alert(options)
				console.info(options)
				
			}
		}		
	})
	return options;
}

function getPoItemInfo(PO_NO){
	var poInfo = [];
	$.ajax({
		url:baseUrl+'account/wmsAccountWPO/getPoItemInfo',
		type:'post',
		async:false,
		dataType:'json',
		data:{
			PO_NO:PO_NO||'',
		},
		success:function(resp){
			//alert(resp.msg)
			if(resp.msg=='success'){
				poInfo = resp.result;
			}
		}		
	})
	return poInfo;
}

/**
 * 获取字典定义的特殊库存类型
 * @returns
 */
function getSobzkOption(){
	var options="";
	for(i=0;i<sobkz_list.length;i++){
			if(i != sobkz_list.length - 1) {
				options += sobkz_list[i].CODE + ":" +sobkz_list[i].CODE + ";";
			} else {
				options += sobkz_list[i].CODE + ":" + sobkz_list[i].CODE;
			}
	}
	return options;
}