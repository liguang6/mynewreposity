var listdata = [];
var lastrow,lastcell,lastcellname;
$(function () {
    $("#dataGrid").dataGrid({
    	datatype: "local",
		data: listdata,
        columnModel: [			 		
//			{ label: '收货工厂', name: 'FACT_NO', index: 'FACT_NO', width: 80,align:'center'  }, 	
			{ label: '发货单号', name: 'DNNO', index: 'DNNO', width: 150,align:'center'  }, 
			{ label: 'STO送货单', name: 'DELIVERY_NO', index: 'DELIVERY_NO', width: 150,align:'center'  }, 
			{ label: '行项目', name: 'ITEM_NO', index: 'ITEM_NO', width: 150,align:'center'  },
			{ label: '物料号', name: 'MAT_NO', index: 'MAT_NO', width: 120,align:'center'  }, 			
			{ label: '物料描述', name: 'MAT_DESC', index: 'MAT_DESC', width: 80,align:'center'  }, 	
			{ label: '体积', name: 'VOLUME', index: 'VOLUME', editable:true ,width: 60,align:'center'}, 	
		    { label: '重量', name: 'WEIGHT', index: 'WEIGHT', editable:true ,width: 60,align:'center'  },
		    { label: '运费', name: 'FREIGHE', index: 'FREIGHE', width: 60,align:'center'  },
			{ label: '附加运费', name: 'FREIGHE_ADDITIONAL', index: 'FREIGHE_ADDITIONAL', editable:true ,width: 60,align:'center'  }, 			
			{ label: '总额外运费', name: 'FREIGHE_EXTRA', index: 'FREIGHE_EXTRA', editable:true ,width: 60,align:'center'  }, 	
			{ label: '箱数', name: 'BOXNO', index: 'BOXNO', width: 80,align:'center'  }, 			
			{ label: '发 货数量', name: 'ITEM_QTY', index: 'ITEM_QTY', width: 80,align:'center'  },
			{ label: '单位', name: 'UNIT_NO', index: 'UNIT_NO', width: 80,align:'center'  },
			{ label: '接收工厂', name: 'FACT_NO', index: 'FACT_NO', width: 80,align:'center'  },
			{ label: '客户订单', name: 'PO_NO', index: 'PO_NO', width: 80,align:'center'  },
			{ label: '需求号', name: 'REQUIREMENT_NO', index: 'REQUIREMENT_NO', width: 80,align:'center'  },
			{ label: 'SAP交货单', name: 'SAP_OUT_NO', index: 'SAP_OUT_NO', width: 80,align:'center'  },
			{ label: '联系人', name: 'RECEIVING_CARGO', index: 'RECEIVING_CARGO', width: 80,align:'center'  },
			{ label: '备注', name: 'REMARK', index: 'REMARK', width: 80,align:'center'   },
		],
		viewrecords: true,
        rowNum: 15,
        rownumWidth: 25, 
        cellEdit:true,
		cellurl:'#',
		cellsubmit:'clientArray',
        showCheckbox:true,
        gridComplete:function(){
        	$("#dataGrid").find("tbody tr").not(".jqgfirstrow").each(function (i){
        		var WEIGHT=$("#dataGrid").jqGrid("getCell",i+1,"WEIGHT")
            	var VOLUME=$("#dataGrid").jqGrid("getCell",i+1,"VOLUME")
            	if(WEIGHT)
            		$("#dataGrid").jqGrid('setCell', i+1, 'VOLUME', '','not-editable-cell');
        		if(VOLUME)
        			$("#dataGrid").jqGrid('setCell', i+1, 'WEIGHT', '','not-editable-cell');
        	})
        },
        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
		},
		beforeSelectRow: function (rowid, e) {  // 点击复选框才选中行
			   var $myGrid = $(this),  
			   i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),  
			   cm = $myGrid.jqGrid('getGridParam', 'colModel'); 
			   if(cm[i].name == 'cb'){
				   $('#dataGrid').jqGrid('setSelection',rowid);
			   }			   		
			   return (cm[i].name == 'cb'); 
		},
        afterSaveCell:function(rowid, cellname, value, iRow, iCol){
        	//let data = JSON.parse(vm.inputData)
        	let data = JSON.parse($('#data').val())
        	if(cellname == 'VOLUME'&&value){
        		$("#dataGrid").jqGrid('setCell', rowid, 'WEIGHT', '','not-editable-cell');
        		if(data&&data.volume_unit_price)
        			$("#dataGrid").jqGrid('setRowData',rowid,{"FREIGHE":(Number)(data.volume_unit_price)*value});
        	}
        	if(cellname == 'WEIGHT'&&value){
        		$("#dataGrid").jqGrid('setCell', rowid, 'VOLUME', '','not-editable-cell');
        		if(data.weight_unit_price)
        			$("#dataGrid").jqGrid('setRowData',rowid,{"FREIGHE":(Number)(data.weight_unit_price)*value});
        	}
        	var WEIGHT=$("#dataGrid").jqGrid("getCell",rowid,"WEIGHT")
        	var VOLUME=$("#dataGrid").jqGrid("getCell",rowid,"VOLUME")
        	if((!WEIGHT)&&(!VOLUME)){
        		var row = $("#dataGrid").find("tbody tr")
        		$(row[rowid]).find("td").eq(8).removeClass('not-editable-cell')
        		$(row[rowid]).find("td").eq(9).removeClass('not-editable-cell')        		
        	}
        }
    });

//------创建发货单---------
$("#newOperation").click(function(){
		js.layer.open({
			type: 2,
			title: "创建发货单",
			area: ["780px", "480px"],
			content: baseUrl + "wms/config/wms_c_shipping_edit.html",
			closeBtn:1,
			end:function(){
			//	$("#searchForm").submit();
			},
			btn: ["确定","取消"],
			yes:function(index,layero){
				var win = layero.find('iframe')[0].contentWindow;
				var deliver_date= $("#deliver_date", layero.find("iframe")[0].contentWindow.document);
				var receiving_cargo= $("#receiving_cargo", layero.find("iframe")[0].contentWindow.document);
				var receiving_address= $("#receiving_address", layero.find("iframe")[0].contentWindow.document);
				var receiving_telephone= $("#receiving_telephone", layero.find("iframe")[0].contentWindow.document);
				var freight_company= $("#freight_company", layero.find("iframe")[0].contentWindow.document);
				var freight_driver= $("#freight_driver", layero.find("iframe")[0].contentWindow.document);
				var consignment_number= $("#consignment_number", layero.find("iframe")[0].contentWindow.document);
				var freight_license_plate= $("#freight_license_plate", layero.find("iframe")[0].contentWindow.document);
				var shipment_date= $("#shipment_date", layero.find("iframe")[0].contentWindow.document);
				var expected_service= $("#expected_service", layero.find("iframe")[0].contentWindow.document);
				var volume_unit_price= $("#volume_unit_price", layero.find("iframe")[0].contentWindow.document);
				var weight_unit_price= $("#weight_unit_price", layero.find("iframe")[0].contentWindow.document);
//				var fact_no= $("#fact_no", layero.find("iframe")[0].contentWindow.document);
				var o = new Object();
				o.deliver_date = deliver_date[0].realValue
				o.receiving_cargo = receiving_cargo[0].value
				o.receiving_address = receiving_address[0].value
				o.receiving_telephone = receiving_telephone[0].value
				o.freight_company = freight_company[0].value
				o.freight_driver = freight_driver[0].value
				o.consignment_number = consignment_number[0].value
				o.freight_license_plate = freight_license_plate[0].value
				o.shipment_date = shipment_date[0].realValue
				o.expected_service = expected_service[0].value
				o.volume_unit_price = volume_unit_price[0].value
				o.weight_unit_price = weight_unit_price[0].value
//				o.fact_no = fact_no[0].value               								
				//$("#data").val(JSON.stringify(o))
				vm.inputData = JSON.stringify(o)
				vm.flag = true
				win.vm.saveFlag = true
				if(typeof listselectCallback == 'function'){
					listselectCallback(index,win.vm.saveFlag);
				}
			},
			no:function(index,layero){
				layer.close(index);
			}
	});
});
//-------修改发货单--------
$("#editOperation").click(function(){
	 var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
	 if(gr !== null && gr!==undefined){
		 var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
		 if(!grData.DNNO){
			 layer.msg("选择的数据还未创建发货单，不能修改",{time:1000});
			 return
		 }
		 var options = {
			type: 2,
			maxmin: true,
			shadeClose: true,
			title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>修改发货单',
			area: ["780px", "480px"],
			content:  baseURL+'wms/config/wms_c_shipping_edit.html',
			btn: ['<i class="fa fa-check"></i> 确定'],
			success:function(layero, index){
				var win = layero.find('iframe')[0].contentWindow;
				win.vm.saveFlag = false;				
				win.vm.getInfo(grData.DNNO,grData.ITEM_NO);
			},
			btn1:function(index,layero){
				var win = layero.find('iframe')[0].contentWindow;
				var btnSubmit= $("#btnSubmit", layero.find("iframe")[0].contentWindow.document);
				$(btnSubmit).click();
				let o = win.vm.maturgent;
				
				var m = new Object();
				m.deliver_date = o.DELIVER_DATE
				m.receiving_cargo = o.RECEIVING_CARGO
				m.receiving_address = o.RECEIVING_ADDRESS
				m.receiving_telephone = o.RECEIVING_TELEPHONE
				m.freight_company = o.FREIGHT_COMPANY
				m.freight_driver = o.FREIGHT_DRIVER 
				m.consignment_number = o.CONSIGNMENT_NUMBER
				m.freight_license_plate = o.FREIGHT_LICENSE_PLATE
				m.shipment_date = o.SHIPMENT_DATE
				m.expected_service = o.EXPECTED_SERVICE
				m.volume_unit_price = o.VOLUME_UNIT_PRICE
				m.weight_unit_price = o.WEIGHT_UNIT_PRICE
//				m.fact_no = fact_no[0].value               								
				//$("#data").val(JSON.stringify(o))
				var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
				for(var i=0;i<ids.length;i++){
					var VOLUME=$("#dataGrid").jqGrid("getCell",ids[i],"VOLUME")
					var WEIGHT=$("#dataGrid").jqGrid("getCell",ids[i],"WEIGHT")
					if(VOLUME)
						$("#dataGrid").jqGrid('setRowData',ids[i],{"FREIGHE":(Number)(VOLUME)*(m.volume_unit_price)});
					if(WEIGHT)
						$("#dataGrid").jqGrid('setRowData',ids[i],{"FREIGHE":(Number)(WEIGHT)*(m.weight_unit_price)});
					}
				vm.inputData = JSON.stringify(m)
				vm.flag = false
				if(typeof listselectCallback == 'function'){
					listselectCallback(index,win.vm.saveFlag);
				}
			}
		};
		options.btn.push('<i class="fa fa-close"></i> 关闭');
		js.layer.open(options);
	 }else{
		 layer.msg("请选择要编辑的行",{time:1000});
	 }
});

$("#comfirm").click(function(){
	$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
	var url = ""
	if(vm.flag ===true)
		url = "config/shipping/save"
	if(vm.flag ===false)
		url = "config/shipping/update"
	vm.flag = ''
	if(!url)
		return	
	
	if(!vm.inputData)		
		return
		
	var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
	if(ids.length==0){
		js.alert("请选择数据!");
		return;
	}
	var rows = [];
	for(var i=0;i<ids.length;i++){
		var rowData = $("#dataGrid").jqGrid('getRowData',ids[i]);							  		
			if (Number(rowData.FREIGHE) <= 0) {
				js.showErrorMessage("体积、重量或单价不能为空");
				return;
			}			
			rows.push(rowData);	
		}
	if(url=="config/shipping/update"){
		for(let r of rows){
			if(!r.DNNO){
				js.alert('选择的数据还未创建发货清单')
				return
			}
		}
	}
	if(url=="config/shipping/save"){
		for(let r of rows){
			if(r.DNNO){
				js.alert('选择的数据已创建发货清单')
				return
			}
		}
	}
	$.ajax({
		url:baseURL+url,
		dataType : "json",
		type : "post",		
		data : {
			"o":$("#data").val(),
			"itemList":JSON.stringify(rows)		
		},
		async: false,
		success:function(resp){
			if(resp.code == '0'){
				vm.inputData=''
				js.alert('操作成功')
			}
		}
	})
});

$("#btnquery").click(function () {

	if(!$("#DELIVERY_NO").val()){
		js.alert("送货单号不能为空！");
		return false;
	}
	queryList();
});
});

var vm = new Vue({
	el:'#rrapp',
	data:{
		inputData:'',
		flag:'' //true save 
	},
	methods: {
		refresh:function(){
			queryList();
        },
		reload: function (event) {
			queryList();
		},
		exp:function(){
			export2Excel();
		},
	}
});

function getSelRow(gridSelector){
	var gr = $(gridSelector).jqGrid('getGridParam', 'selrow');//获取选中的行号
	return gr != null?$(gridSelector).jqGrid("getRowData",gr):null;
}

function listselectCallback(index,rtn){
	rtn = rtn ||false;
	if(rtn){
		//操作成功，刷新表格
		//vm.reload();
		try {
			parent.layer.close(index);
        } catch(e){
        	layer.close(index);
        }
	}
}

function queryList(){
	vm.inputData=''
	$.ajax({
		url:baseURL+"config/shipping/list",
		dataType : "json",
		type : "post",
		data : {
			"DELIVERY_NO":$("#DELIVERY_NO").val(),
			"FACT_NO":$("#FACT_NO").val(),
		},
		success: function (response) { 
			if(response.code == "0"){
                let listdata = response.page.list
                let params = []
				$("#dataGrid").jqGrid('clearGridData');
				$("#dataGrid").jqGrid('setGridParam',{ 
					datatype:'local', data : response.page.list, 
			    }).trigger("reloadGrid");
			}else{
				listdata = [];
				$("#dataGrid").jqGrid('clearGridData');
				js.alert(response.msg);
			}
		}
	});
}
function export2Excel(){
	var column= [				
//		{"title": '发货工厂', "data":'FACT_NO' },
		{"title": '体积', "data":'VOLUME'},
		{"title": '重量', "data":'WEIGHT'},
		{"title": '运费', "data":'FREIGHE'},
		{"title": '附加运费', "data":'FREIGHE_ADDITIONAL'},
		{"title": '总额外运费', "data":'FREIGHE_EXTRA'},			
		{"title": '箱数', "data":'BOXNO'},
		{"title": 'STO送货单', "data":'DELIVERY_NO'},
		{"title": 'SAP交货单', "data":'SAP_OUT_NO'},
		{"title": 'SAP料号', "data":'MAT_NO'},
		{"title": '物料描述',"data":'MAT_DESC'},
		{"title": '发货数量',  "data":'ITEM_QTY'},
		{"title": '单位', "data":'UNIT_NO'},
		{"title": '收货工厂',"data":'FACT_NO'},
		{"title": '订单号', "data":'PO_NO'},
		{"title": '收货人/联系方式', "data":'RECEIVING_CARGO'},
		{"title": '备注', "data":'REMARK'},		
	]; 
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"config/shipping/list",
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) { 
		    results=response.page.list;
		}
	})
	if(!$("#DELIVERY_NO").val()){
		js.alert('请输入单号');
		return
	}
	if(results.length>0&&results[0].DNNO){		
	var table=document.getElementById("tb_excel");
//	var caption=$("<caption />");
//	caption.html('深圳坪山十五部  发货单编号:'+results[0].DNNO);
//	$(table).append(caption);
	var str = '<caption>深圳坪山十五部  发货单编号:'+results[0].DNNO+
		      '<c/aption><tr><td>发货日期</td><td>'+results[0].DELIVER_DATE+
			  '</td><td>收货人</td><td>'+results[0].RECEIVING_CARGO+
			  '</td><td>收货地址</td><td>'+results[0].RECEIVING_ADDRESS+
			  '</td><td>收货电话</td><td>'+results[0].RECEIVING_TELEPHONE+
			  '</td></tr>'+
			  '<tr><td>货运公司</td><td>'+results[0].FREIGHT_COMPANY+
			  '</td><td>货运司机</td><td>'+results[0].FREIGHT_DRIVER+
			  '</td><td>托运单号</td><td>'+results[0].CONSIGNMENT_NUMBER+
			  '</td><td>货车车牌</td><td>'+results[0].FREIGHT_LICENSE_PLATE+
			  '</td></tr>'+
			  '<tr><td>起运时间</td><td>'+results[0].SHIPMENT_DATE+
			  '</td><td>预计送达</td><td>'+results[0].EXPECTED_SERVICE+
			  '</td><td>体积单价</td><td>'+results[0].VOLUME_UNIT_PRICE+
			  '</td><td>重量单价</td><td>'+results[0].WEIGHT_UNIT_PRICE+
			  '</td></tr>'
	$(table).html(str)		 
	var rowGroups=[];
	getMergeTable(results,column,rowGroups,"发货清单");
	}else
		js.alert('发货单信息不存在');
}
