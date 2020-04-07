var detail=[];
var vm = new Vue({
	el:'#rrapp',
	data:{
		werks:'',
		whNumber:'',
		returnNo:'',
		bclass:'',
		bname:''
	},
	created:function(){
		this.werks = getUrlKey("werks")
		this.whNumber = getUrlKey("whNumber")
		this.bclass = getUrlKey("bclass")
		this.bname = getUrlKey("bname")
		var whNumber=getUrlKey("whNumber");
		$.ajax({
			url:baseURL+"common/getWhDataByWerks",
			dataType : "json",
			type : "post",
			data : {
				"WERKS":this.werks
			},
			async: true,
			success: function (response) { 
				$("#wh").empty();
				var strs = "";
			    $.each(response.data, function(index, value) {
			    	if(value.WH_NUMBER==whNumber){
			    		strs += "<option value=" + value.WH_NUMBER + " selected=true>" + value.WH_NUMBER + "</option>";
			    	}else{
				    	strs += "<option value=" + value.WH_NUMBER + ">" + value.WH_NUMBER + "</option>";
			    	}
			    });
			    $("#wh").append(strs);
			}
		});
	},
	methods: {
		detail: function () {
			console.log('-->detail returnNo : ' + this.returnNo + "|" + this.whNumber)
			$.ajax({
				url:baseURL+"query/returngoodsQuery/detail",
				dataType : "json",
				type : "post",
				data : {
					WERKS:$("#werks").val(),
					WH_NUMBER:this.whNumber,
					RETURN_NO:$("#returnNo").val(),
					
				},
				async: true,
				success: function (response) { 
					$("#dataGrid").jqGrid("clearGridData", true);
					$("#dataGrid").jqGrid('GridUnload');
				    detail=response.list;
				    showTable(detail);
				}
			});
			var hideCol=['F_BATCH','ITEM_TEXT','RECEIPT_NO','RECEIPT_ITEM_NO','PO_NO','PO_ITEM_NO','SAP_OUT_NO',
				'SAP_OUT_ITEM_NO','MO_NO','MO_ITEM_NO','RSNUM','RSPOS','COST_CENTER','IO_NO','WBS','INBOUND_NO',
				'INBOUND_ITEM_NO'];
			DynamicColume(hideCol,"列选择","#dataGrid","#example-getting-started");
		},
		close: function(){
			console.log("----> close return : " + $("#returnNo").val())
			js.confirm('确定要关闭此退货单？', function(){
				$.ajax({
					type: "POST",
				    url: baseURL+"query/returngoodsQuery/close",
				    dataType : "json",
					data : {
						RETURN_NO:$("#returnNo").val()
					},
				    success: function(r){
				    	if(r.code === 0){
				        	vm.saveFlag= true;
				        	js.showMessage(r.msg+"：操作成功！");
							$("#dataGrid").jqGrid("clearGridData", true);
						}else{
							js.showMessage(r.msg);
						}
					}
				});
			})
		},
		del: function(){
			var saveData=[];
			//获取选中表格数据
			var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");	

			if(ids.length == 0){
				alert("当前还没有勾选任何行项目数据！");
				return false;
			}
			for (var i = 0; i < ids.length; i++) {
				var data=$("#dataGrid").jqGrid('getRowData',ids[i]);
				if(data["ITEM_STATUS"]!='00' && data["ITEM_STATUS"]!='01'){
					js.showErrorMessage('第'+(i+1)+'行项目状态为'+data["ITEM_STATUS_DESC"]+",不允许关闭操作");
					save_flag=false;
					return false;
				}
				
				saveData[i] = $("#dataGrid").jqGrid('getRowData',ids[i]);
			}
			var url = baseURL+"query/returngoodsQuery/del";
			if(save_flag)
			$.ajax({
				type: "POST",
			    url: url,
			    dataType : "json",
				data : {
					saveData:JSON.stringify(saveData),
				},
			    success: function(r){
			    	if(r.code === 0){
			        	vm.saveFlag= true;
			        	js.showMessage(r.msg+"：保存成功！");
			        	//$("#dataGrid").dataGrid("clearGridData");
					}else{
						js.showMessage(r.msg);
					}
				}
			});
		},
		returnDocTypeChange :function(){
			getReturnType();
		},
		onPlantChange : function(event) {
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
		more: function(e){
			var options = {
		    		type: 2,
		    		maxmin: true,
		    		shadeClose: true,
		    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>选择更多',
		    		area: ["400px", "380px"],
		    		content: baseURL+"wms/query/conditionChoose.html",  
		    		btn: ['<i class="fa fa-check"></i> 确定'],
		    		success:function(layero, index){
		    			var win = layero.find('iframe')[0].contentWindow;
		    			win.vm.showTable($(e).val());
		    		},
		    		btn1:function(index,layero){
						var win = layero.find('iframe')[0].contentWindow;
						win.vm.getTableData();
						var returnValue= $("#returnValue", layero.find("iframe")[0].contentWindow.document);
						var retVal='';
						$(e).val($(returnValue).val());
						try {
							parent.layer.close(index);
				        } catch(e){
				        	layer.close(index);
				        }
						if(typeof listselectCallback == 'function'){
							try {
								parent.layer.close(index);
					        } catch(e){
					        	layer.close(index);
					        }
						}
		    		}
		    	};
		    	options.btn.push('<i class="fa fa-close"></i> 关闭');
		    	options['btn'+options.btn.length] = function(index, layero){
		     };
		     js.layer.open(options);
		}
	}
});
$(function(){
	var werks = getUrlKey("werks")
	console.log('-->werks : ' + werks + "|business_class : " + getUrlKey("bclass") + "|business_name : " + getUrlKey("bname"))
	$("#werks").val(werks)
	var returnNo=getUrlKey("returnNo");
	$("#returnNo").val(returnNo);
    getReturnDocType();
    $("#returnDocType").change(function () {
		console.log('-->returnDocTypeChange')
		getReturnType($("#returnDocType").val())
	});
    $.ajax({
		url:baseURL+"query/returngoodsQuery/detail",
		dataType : "json",
		type : "post",
		data : {
			WERKS:$("#werks").val(),
			WH_NUMBER:$("#whNumber").val(),
			RETURN_NO:$("#returnNo").val(),
			
		},
		async: false,
		success: function (response) { 
		    detail=response.list;
		    showTable(detail);
		}
	});
    
    $("#btnPrint1").click(function () {
    	if("04" === getUrlKey("bclass")){
    		window.open(baseURL+"docPrint/receiveRoomOutPrint?PageSize=1&outNo=" + returnNo);
    	}else if("05" === getUrlKey("bclass")){
    		window.open(baseURL+"docPrint/wareHouseOutPrint?PageSize=1&outNo=" + returnNo);
    	}else if("06" === getUrlKey("bclass")){
    		window.open(baseURL+"docPrint/workshopReturnOutPrint?PageSize=1&outNo=" + returnNo);
    	}
    })
    
    $("#btnPrint2").click(function () {
    	if("04" === getUrlKey("bclass")){
    		window.open(baseURL+"docPrint/receiveRoomOutPrint?PageSize=0&outNo=" + returnNo);
    	}else if("05" === getUrlKey("bclass")){
    		window.open(baseURL+"docPrint/wareHouseOutPrint?PageSize=0&outNo=" + returnNo);
    	}else if("06" === getUrlKey("bclass")){
    		window.open(baseURL+"docPrint/workshopReturnOutPrint?PageSize=0&outNo=" + returnNo);
    	}
    })
//	var now = new Date(); //当前日期
//	var startDate=new Date(now.getTime()-30*24*3600*1000);
//	$("#createDateStart").val(formatDate(startDate));
//	$("#createDateEnd").val(formatDate(now));
//	var grid = $("#dataGrid"); 
//	$("#dataGrid").dataGrid({
//		searchForm: $("#searchForm"),
//		columnModel: [
//			{header:'退货单号', name:'RETURN_NO', width:110, align:"center"},
//			{header:'行项目', name:'RETURN_ITEM_NO', width:60, align:"center"},
//			{header:'工厂代码', name:'WERKS', width:70, align:"center",sortable:false},
//			{header:'仓库号', name:'WH)NUMBER', width:80, align:"center",sortable:false},
//			{header:'库位', name:'LGORT', width:70, align:"center",sortable:false},
//			{header:'料号', name:'MATNR', width:90, align:"center"},
//			{header:'物料描述', name:'MAKTX', width:150, align:"center",sortable:false},
//			{header:'特殊库存类型', name:'SOBKZ', width:80, align:"center"},
//			{header:'单位', name:'UNIT', width:45, align:"center",sortable:false},
//			{header:'退货数量', name:'RETURN_QTY', width:70, align:"center",sortable:false},
//			{header:'过账数量', name:'REAL_RETURN_QTY', width:80, align:"center",sortable:false},
//			{header:'批次', name:'BATCH', width:100, align:"center"},
//			{header:'退货人', name:'RETURN_PEOPLE', width:60, align:"center",sortable:false},
//			{header:'退货原因', name:'RETURN_REASON_DESC', width:150, align:"center",sortable:false},
//			{header:'创建人', name:'CREATOR', width:120, align:"center",sortable:false},
//			{header:'创建时间', name:'CREATE_DATE', width:120, align:"center",sortable:false},
//			{header:'源批次', name:'F_BATCH', width:70, align:"center",sortable:false},
//			{header:'行项目文本', name:'ITEM_TEXT', width:90, align:"center",sortable:false},
//			{header:'状态', name:'ITEM_STATUS_DESC', width:70, align:"center",sortable:false},
//			{header:'收货单号', name:'RECEIPT_NO', width:60, align:"center",sortable:false},
//			{header:'收货单行项目', name:'RECEIPT_ITEM_NO', width:60, align:"center",sortable:false},
//			{header:'采购订单号', name:'PO_NO', width:60, align:"center",sortable:false},
//			{header:'行项目', name:'RECEIPT_ITEM_NO', width:60, align:"center",sortable:false},
//			{header:'SAP交货单号', name:'SAP_OUT_NO', width:60, align:"center",sortable:false},
//			{header:'行项目', name:'SAP_OUT_ITEM_NO', width:60, align:"center",sortable:false},
//			{header:'生产订单号', name:'MO_NO', width:60, align:"center",sortable:false},
//			{header:'行项目', name:'MO_ITEM_NO', width:60, align:"center",sortable:false},
//			{header:'预留号', name:'RSNUM', width:60, align:"center",sortable:false},
//			{header:'行项目', name:'RSPOS', width:60, align:"center",sortable:false},
//			{header:'成本中心', name:'COST_CENTER', width:60, align:"center",sortable:false},
//			{header:'内部/CO订单号', name:'IO_NO', width:60, align:"center",sortable:false},
//			{header:'WBS', name:'WBS', width:60, align:"center",sortable:false},
//			{header:'备注', name:'MEMO', width:80, align:"center",sortable:false},
//		],	
//		viewrecords: true,
//	    shrinkToFit:false,
//        rownumWidth: 35, 
//        showCheckbox:true,
//        multiselect: true,
//		onSelectRow:function(rowid,status){
//			var rowData = $("#dataGrid").jqGrid('getRowData',rowid);
//			var returnNo=rowData["RETURN_NO"];
//    		var ids = $("#dataGrid").jqGrid('getDataIDs');
//	    	if(status == true){
//	    		for(i = 1; i <= ids.length; i++){
//	    			if(rowid!=ids[i]){
//	    				var row = $("#dataGrid").jqGrid('getRowData',ids[i]);
//		    			if (row['RETURN_NO'] == returnNo){
//	                        console.log("returnNo",returnNo);
//	                  	    $("#dataGrid").jqGrid("setSelection",ids[i]); 
//	                    }
//	    			}
//	    		}
//	    	}else{
//	    		for(i = 1; i <= ids.length; i++){
//	    			if(rowid!=ids[i]){
//	    				var row = $("#dataGrid").jqGrid('getRowData',ids[i]);
//		    			if (row['RETURN_NO'] == returnNo){
//	                        console.log("returnNo",returnNo);
//	                  	    $("#dataGrid").jqGrid("setSelection",ids[i],false); 
//	                    }
//	    			}
//	    		}
//	    	}	
//	    }
//	});
	var hideCol=['F_BATCH','ITEM_TEXT','RECEIPT_NO','RECEIPT_ITEM_NO','PO_NO','PO_ITEM_NO','SAP_OUT_NO',
		'SAP_OUT_ITEM_NO','MO_NO','MO_ITEM_NO','MO_NO','MO_ITEM_NO','RSNUM','RSPOS','COST_CENTER','IO_NO','WBS','INBOUND_NO',
		'INBOUND_ITEM_NO'];
	DynamicColume(hideCol,"列选择","#dataGrid","#example-getting-started");
});
function showTable(data){
	console.log('showTable')
	$("#dataGrid").dataGrid({
		datatype: "local",
		data: data,
		columnModel: [
			{header:'退货单号', name:'RETURN_NO', width:110, align:"center"},
			{header:'行项目', name:'RETURN_ITEM_NO', width:60, align:"center"},
			{header:'工厂代码', name:'WERKS', width:70, align:"center",sortable:false},
			{header:'仓库号', name:'WH_NUMBER', width:80, align:"center",sortable:false},
			{header:'库位', name:'LGORT', width:70, align:"center",sortable:false},
			{header:'料号', name:'MATNR', width:90, align:"center"},
			{header:'物料描述', name:'MAKTX', width:150, align:"center",sortable:false},
			{header:'特殊库存类型', name:'SOBKZ', width:90, align:"center"},
			{header:'单位', name:'UNIT', width:45, align:"center",sortable:false},
			{header:'退货数量', name:'TOTAL_RETURN_QTY', width:70, align:"center",sortable:false},
			{header:'过账数量', name:'REAL_QTY', width:80, align:"center",sortable:false},
			{header:'批次', name:'BATCH', width:100, align:"center"},
			{header:'退货人', name:'RETURN_PEOPLE', width:60, align:"center",sortable:false},
			{header:'退货原因', name:'RETURN_REASON_DESC', width:150, align:"center",sortable:false},
			{header:'创建人', name:'CREATOR', width:120, align:"center",sortable:false},
			{header:'创建时间', name:'CREATE_DATE', width:120, align:"center",sortable:false},
			{header:'源批次', name:'F_BATCH', width:70, align:"center",sortable:false},
			{header:'行项目文本', name:'ITEM_TEXT', width:90, align:"center",sortable:false},
			{header:'状态', name:'ITEM_STATUS', width:70, align:"center",sortable:false,
				formatter:function(val){
					//退货单状态 字典定义：（00创建 01 部分下架 02下架  03完成）
					if("00"===val)return "创建"
					if("01"===val)return "部分下架"
					if("02"===val)return "下架"
					if("03"===val)return "完成"
				}
			},
			{header:'收货单号', name:'RECEIPT_NO', width:60, align:"center",sortable:false},
			{header:'收货单行项目', name:'RECEIPT_ITEM_NO', width:60, align:"center",sortable:false},
			{header:'采购订单号', name:'PO_NO', width:60, align:"center",sortable:false},
			{header:'行项目', name:'PO_ITEM_NO', width:60, align:"center",sortable:false},
			{header:'SAP交货单号', name:'SAP_OUT_NO', width:60, align:"center",sortable:false},
			{header:'行项目', name:'SAP_OUT_ITEM_NO', width:60, align:"center",sortable:false},
			{header:'生产订单号', name:'MO_NO', width:150, align:"center",sortable:false},
			{header:'行项目', name:'MO_ITEM_NO', width:80, align:"center",sortable:false},
			{header:'预留号', name:'RSNUM', width:60, align:"center",sortable:false},
			{header:'行项目', name:'RSPOS', width:60, align:"center",sortable:false},
			{header:'成本中心', name:'COST_CENTER', width:60, align:"center",sortable:false},
			{header:'内部/CO订单号', name:'IO_NO', width:60, align:"center",sortable:false},
			{header:'WBS', name:'WBS', width:60, align:"center",sortable:false},
			{header:'备注', name:'MEMO', width:80, align:"center",sortable:false},
		],	
		viewrecords: true,
	    shrinkToFit:false,
        rownumWidth: 35, 
        showCheckbox:true,
        multiselect: true,
		onSelectRow:function(rowid,status){
			var rowData = $("#dataGrid").jqGrid('getRowData',rowid);
			var returnNo=rowData["RETURN_NO"];
    		var ids = $("#dataGrid").jqGrid('getDataIDs');
	    	if(status == true){
	    		for(i = 1; i <= ids.length; i++){
	    			if(rowid!=ids[i]){
	    				var row = $("#dataGrid").jqGrid('getRowData',ids[i]);
		    			if (row['RETURN_NO'] == returnNo){
	                        console.log("returnNo",returnNo);
	                  	    $("#dataGrid").jqGrid("setSelection",ids[i]); 
	                    }
	    			}
	    		}
	    	}else{
	    		for(i = 1; i <= ids.length; i++){
	    			if(rowid!=ids[i]){
	    				var row = $("#dataGrid").jqGrid('getRowData',ids[i]);
		    			if (row['RETURN_NO'] == returnNo){
	                        console.log("returnNo",returnNo);
	                  	    $("#dataGrid").jqGrid("setSelection",ids[i],false); 
	                    }
	    			}
	    		}
	    	}	
	    }
	});
}

function getUrlKey(name){
	return decodeURIComponent((new RegExp('[?|&]'+name+'='+'([^&;]+?)(&|#|;|$)').exec(location.href)||[,""])[1].replace(/\+/g,'%20'))||null;
}
//退货单类型
function getReturnDocType(){
	$.ajax({
		url:baseURL+"query/returngoodsQuery/queryReturnDocTypeList",
		dataType : "json",
		type : "post",
		data : {},
		async: true,
		success: function (response) { 
			$("#returnDocType").empty();
			var strs = "<option value=''>全部</option>";
		    $.each(response.list, function(index, value) {
		    	strs += "<option value=" + value.CODE + ">" + value.VALUE + "</option>";
		    });
		    $("#returnDocType").append(strs);
		    getReturnType($("#returnDocType").val());
		}
	});
}
// 退货类型
function getReturnType(type){
	$.ajax({
		url:baseURL+"query/returngoodsQuery/queryReturnTypeList",
		dataType : "json",
		type : "post",
		data : {
			"type":type
		},
		async: true,
		success: function (response) { 
			$("#returnType").empty();
			var strs = "<option value=''>全部</option>";
		    $.each(response.list, function(index, value) {
		    	strs += "<option value=" + value.BUSINESS_NAME + ">" + value.BUSINESS_NAME_DESC + "</option>";
		    });
		    $("#returnType").append(strs);
		}
	});
}