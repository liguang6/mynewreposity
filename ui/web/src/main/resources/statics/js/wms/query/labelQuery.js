var lastrow,lastcell,last_scan_ele;
var vm = new Vue({
	el:'#rrapp',
	data:{
	},
	created:function(){
		$(".poNo").hide();
		$(".productNo").hide();
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
	},
	methods: {
		query: function () {
			$("#searchForm").submit();
		},
		exp: function () {
			export2Excel();
		},
		onPlantChange:function(event){
		  //工厂变化的时候，更新库存下拉框
          var werks = event.target.value;
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
		labelTypeChange: function(){
			var labelType=$('#labelType').val();
			console.log("labelType",labelType);
			if(labelType!=''){
				if(labelType=='00'){
					$(".poNo").show();
					$(".productNo").hide();
				}
                if(labelType=='01'){
                	$(".poNo").hide();
					$(".productNo").show();
				}
			}
		},
		print: function () {
			//获取选中表格数据
			var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");
			if(ids.length == 0){
				alert("当前还没有勾选任何行项目数据！");
				return false;
			}
			var saveData = [];
			var labelType = null;
			var WERKS = null;
			for (var i = 0; i < ids.length; i++) {
				var data=$("#dataGrid").jqGrid('getRowData',ids[i]);
				if(WERKS == null){
					WERKS = data.WERKS;
				}
				if(undefined != data.RECEIPT_ITEM_NO && null != data.RECEIPT_ITEM_NO && 'undefined' != data.RECEIPT_ITEM_NO
						&& "" != data.RECEIPT_ITEM_NO){
					if(labelType==null){
						labelType = "外购";
					}else{
						if("外购" != labelType){
							alert("不能同时勾选外购和自制标签！");
							return false;
						}
					}
				}
				if(undefined != data.INBOUND_NO && null != data.INBOUND_NO && 'undefined' != data.INBOUND_NO
						&& "" != data.INBOUND_NO){
					if(labelType==null){
						labelType = "自制";
					}else{
						if("自制" != labelType){
							alert("不能同时勾选外购和自制标签！");
							return false;
						}
					}
				}
				
				saveData[i] = $("#dataGrid").jqGrid('getRowData',ids[i]);
			}
			if(saveData.length>0){
				if(labelType == "自制"){
					//将打印form action地址修改为自制标签打印路径
					$("#print").attr('action',baseUrl+"docPrint/inInternalBoundPreview"); 
				}else{
					//将打印from action地址修改为默认的外购标签打印路径
					$("#print").attr('action',baseUrl+"docPrint/labelLabelPreview"); 
				}
				$("#WERKS").val(WERKS);
				$("#labelList").val(JSON.stringify(saveData));
				$("#printButton").click();
			}

		},
		printA4: function () {
			//获取选中表格数据
			var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");
			if(ids.length == 0){
				alert("当前还没有勾选任何行项目数据！");
				return false;
			}
			var saveData = [];
			var WERKS = null;
			var labelType = null;
			for (var i = 0; i < ids.length; i++) {
				var data=$("#dataGrid").jqGrid('getRowData',ids[i]);
				if(WERKS == null){
					WERKS = data.WERKS;
				}
				
				if(undefined != data.RECEIPT_ITEM_NO && null != data.RECEIPT_ITEM_NO && 'undefined' != data.RECEIPT_ITEM_NO
						&& "" != data.RECEIPT_ITEM_NO){
					if(labelType==null){
						labelType = "外购";
					}else{
						if("外购" != labelType){
							alert("不能同时勾选外购和自制标签！");
							return false;
						}
					}
				}
				if(undefined != data.INBOUND_NO && null != data.INBOUND_NO && 'undefined' != data.INBOUND_NO
						&& "" != data.INBOUND_NO){
					if(labelType==null){
						labelType = "自制";
					}else{
						if("自制" != labelType){
							alert("不能同时勾选外购和自制标签！");
							return false;
						}
					}
				}
				
				saveData[i] = $("#dataGrid").jqGrid('getRowData',ids[i]);
			}
			if(saveData.length>0){
				if(labelType == "自制"){
					//将打印form action地址修改为自制标签打印路径
					$("#printA4").attr('action',baseUrl+"docPrint/inInternalBoundPreview"); 
				}else{
					//将打印from action地址修改为默认的外购标签打印路径
					$("#print").attr('action',baseUrl+"docPrint/labelPreview_A4"); 
				}
				$("#WERKSA4").val(WERKS);
				$("#labelListA4").val(JSON.stringify(saveData));
				$("#printButtonA4").click();
			}
		},
		keyPartsPrint: function () {
			//获取选中表格数据
			//保存编辑的最后一个单元格
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");
			if(ids.length == 0){
				js.showErrorMessage('当前还没有勾选任何行项目数据！');
				return false;
			}
			var saveData = [];
			for (var i = 0; i < ids.length; i++) {
				var data=$("#dataGrid").jqGrid('getRowData',ids[i]);
				if(data.QUANTITY!=''){
					var reg=/^[0-9]+.?[0-9]*$/;
					if(!reg.test(data.QUANTITY)){
						js.showErrorMessage('标签号：'+data.LABEL_NO+' 关键零部件打印数量请输入数字！');
                        return false;
				    }
					
					saveData[i] = $("#dataGrid").jqGrid('getRowData',ids[i]);
				}else if(data.QUANTITY=='0'){
					js.showErrorMessage('标签号：'+data.LABEL_NO+' 关键零部件打印数量不能为0！');
                    return false;
				}else{
					js.showErrorMessage('标签号：'+data.LABEL_NO+' 关键零部件打印数量不能为空！');
					return false;
				}
			}
			if(saveData.length>0)
			$("#keyPartsList").val(JSON.stringify(saveData));
			$("#keyPartsButton").click();
		}
	}
});
$(function () {
	var now = new Date(); //当前日期
	var startDate=new Date(now.getTime()-6*24*3600*1000);
	$("#createDateStart").val(formatDate(startDate));
	$("#createDateEnd").val(formatDate(now));
	$('#dataGrid').dataGrid({
		searchForm: $("#searchForm"),
		cellEdit:true,
		cellurl:'#',
		cellsubmit:'clientArray',
		columnModel: [
			{header:'批次', name:'BATCH', width:110, align:"center"},
			{header:'标签号', name:'LABEL_NO', width:120, align:"center"},
			{header:'工厂', name:'WERKS', width:50, align:"center",sortable:false},
			{header:'仓库号', name:'WH_NUMBER', width:60, align:"center",sortable:false},
			{header:'供应商代码', name:'LIFNR', width:80, align:"center"},
			{header:'供应商描述', name:'LIKTX', width:200, align:"center"},
			{header:'料号', name:'MATNR', width:90, align:"center"},
			{header:'物料描述', name:'MAKTX', width:160, align:"center",sortable:false},	
			{header:'数量', name:'BOX_QTY', width:60, align:"center",sortable:false},
			{header:'单位', name:'UNIT', width:50, align:"center",sortable:false},
			{header:'库位', name:'LGORT', width:60, align:"center",sortable:false},
			{header:'储位代码', name:'BIN_CODE', width:70, align:"center",sortable:false},
			{header:'生产日期', name:'PRODUCT_DATE', width:90, align:"center",sortable:false},
			{header:'有效期', name:'EFFECT_DATE', width:90, align:"center",sortable:false},
			{header:'状态', name:'LABEL_STATUS', width:110, align:"center",sortable:false,formatter: function(item, index){
    		    	//00创建，01已收料（待质检），02已收料（无需质检）03待进仓(已质检)，04待退货(已质检)，05收料房退货，06库房退货，07已进仓，08已上架，09已下架，10已出库，11已冻结，12已锁定，20关闭
					var st = "创建";
	    		    if(item=='01'){
	    		    	st = "已收料（待质检）";
	    		    }
	    		    if(item=='02'){
	    		    	st = "已收料（无需质检）";
	    		    }
	    		    if(item=='03'){
	    		    	st = "待进仓(已质检)";
	    		    }
	    		    if(item=='04'){
	    		    	st = "待退货(已质检)";
	    		    }
	    		    if(item=='05'){
	    		    	st = "收料房退货";
	    		    }
	    		    if(item=='06'){
	    		    	st = "库房退货";
	    		    }
	    		    if(item=='07'){
	    		    	st = "已进仓";
	    		    }
	    		    if(item=='08'){
	    		    	st = "已上架";
	    		    }
	    		    if(item=='09'){
	    		    	st = "已下架";
	    		    }
	    		    if(item=='10'){
	    		    	st = "已出库";
	    		    }
	    		    if(item=='11'){
	    		    	st = "已冻结";
	    		    }
	    		    if(item=='12'){
	    		    	st = "已锁定";
	    		    }
	    		    if(item=='20'){
	    		    	st = "关闭";
	    		    }
    		    
					return st;
	    		}
			},
			{header:'质检结果', name:'QC_RESULT_CODE', width:80, align:"center",sortable:false},
			{header:'<span style="color:blue">关键零部件打印数量</span>', name:'QUANTITY', width:80, align:"center",editable:true,sortable:false,editrules:{number:true}},
			{header:'采购订单号', name:'PO_NO', width:80, align:"center"},
			{header:'行项目', name:'PO_ITEM_NO', width:80, align:"center"},
			{header:'需求跟踪号', name:'BEDNR', width:80, align:"center"},
			{header:'收货单号', name:'RECEIPT_NO', width:110, align:"center"},
			{header:'行项目', name:'RECEIPT_ITEM_NO', width:55, align:"center"},
			{header:'进仓单号', name:'INBOUND_NO', width:110, align:"center"},
			{header:'行项目', name:'INBOUND_ITEM_NO', width:55, align:"center"},
			{header:'箱序号', name:'BOX_SN', width:55, align:"center"},
			{header:'备注', name:'SAP_MATDOC_NO', width:80, align:"center",sortable:false},
			{label:'DRAWING_NO',name:'DRAWING_NO',hidden:true},
			{label:'PRO_STATION',name:'PRO_STATION',hidden:true},
			{label:'CAR_TYPE',name:'CAR_TYPE',hidden:true},
			{label:'WORKGROUP_NO',name:'WORKGROUP_NO',hidden:true},
			{label:'MOULD_NO',name:'MOULD_NO',hidden:true},
			{label:'OPERATOR',name:'OPERATOR',hidden:true},
			{label:'SO_NO',name:'SO_NO',hidden:true},
			{label:'SO_ITEM_NO',name:'SO_ITEM_NO',hidden:true},
			{label:'MO_NO',name:'MO_NO',hidden:true},
			{label:'MO_ITEM_NO',name:'MO_ITEM_NO',hidden:true},
			{label:'IO_NO',name:'IO_NO',hidden:true},
			{label:'COST_CENTER',name:'COST_CENTER',hidden:true},
			{label:'WBS',name:'WBS',hidden:true},
			{label:'PO_NO_E',name:'PO_NO_E',hidden:true},
			{label:'PO_ITEM_NO_E',name:'PO_ITEM_NO_E',hidden:true},
		],	
		viewrecords: true,
	    shrinkToFit:false,
		rowNum: 15,  
		width:3100,
		showCheckbox:true,
		rowList : [  15,30 ,50],
        rownumWidth: 35, 
        multiselect: true,
        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
		},
        beforeSelectRow:function(rowid,e){
        	var $myGrid = $(this),  
			   i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),  
			   cm = $myGrid.jqGrid('getGridParam', 'colModel'); 
			   if(cm[i].name == 'cb'){
				   $('#dataGrid').jqGrid('setSelection',rowid);
			   }   		
			   return (cm[i].name == 'cb'); 
        },
        afterSaveCell:function(rowid, cellname, value, iRow, iCol){
        	var rec =  $("#dataGrid").jqGrid('getRowData', rowid);
        	if(cellname == 'QUANTITY'){
        		//校验数量是否超出
        		var row=$('#dataGrid').jqGrid('getRowData',rowid);
        		if(parseInt(value)>parseInt(row.QTY)){
        			js.showErrorMessage('本次输入的配置数量不能超过:'+row.QTY);
        			$("#dataGrid").jqGrid('setCell', rowid, cellname, ' ','editable-cell');
        			return ;
        		}
        	}
        }
	});

	$("#labelType").change(function(){
		var labelType=$('#labelType').val();
		if(labelType!=''){
			if(labelType=='00'){
				$(".poNo").show();
				$(".productNo").hide();
				$("#productNo").val("");
			}
            if(labelType=='01'){
            	$(".poNo").hide();
            	$("#poNo").val("");
				$(".productNo").show();
			}
		}else{
			$(".poNo").hide();
			$(".productNo").hide();
			$("#poNo").val("");
			$("#productNo").val("");
		}
	});
});
function export2Excel(){
	var column=[   
		{"title":'批次', "data":'BATCH', "class":"center"},
		{"title":'标签号', "data":'LABEL_NO', "class":"center"},
		{"title":'工厂', "data":'WERKS',  "class":"center"},
		{"title":'仓库号', "data":'WH_NUMBER',  "class":"center"},
		{"title":'供应商代码', "data":'LIFNR', "class":"center"},
		{"title":'供应商描述', "data":'LIKTX', "class":"center"},
		{"title":'料号', "data":'MATNR', "class":"center"},
		{"title":'物料描述', "data":'MAKTX',  "class":"center"},	
		{"title":'数量', "data":'BOX_QTY', "class":"center"},
		{"title":'单位', "data":'UNIT', "class":"center"},
		{"title":'库位', "data":'LGORT', "class":"center"},
		{"title":'储位代码', "data":'BIN_CODE', "class":"center"},
		{"title":'生产日期', "data":'PRODUCT_DATE', "class":"center"},
		{"title":'有效期', "data":'EFFECT_DATE', "class":"center"},
		{"title":'状态', "data":'LABEL_STATUS', "class":"center"},
		{"title":'质检结果', "data":'QC_RESULT_CODE', "class":"center"},
		{"title":'关键零部件打印数量', "data":'QUANTITY', "class":"center"},
		{"title":'采购订单号', "data":'PO_NO', "class":"center"},
		{"title":'行项目', "data":'PO_ITEM_NO', "class":"center"},
		{"title":'需求跟踪号', "data":'BEDNR', "class":"center"},
		{"title":'收货单号', "data":'RECEIPT_NO', "class":"center"},
		{"title":'行项目', "data":'RECEIPT_ITEM_NO', "class":"center"},
		{"title":'进仓单号', "data":'INBOUND_NO', "class":"center"},
		{"title":'行项目', "data":'INBOUND_ITEM_NO',  "class":"center"},
		{"title":'备注', "data":'SAP_MATDOC_NO', "class":"center"},
      ]	;
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"query/labelQuery/list",
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) { 
		    results=response.page.list;
		    console.log("results",results);
		}
	});
	var rowGroups=[];
	getMergeTable(results,column,rowGroups,"标签记录");	
}

function funFromjs(result) {
	var str_arr = result.split(";");
	if(str_arr.length>0){
		var label_no = "";
		for(var i=0;i<str_arr.length;i++){
			var str = str_arr[i];
			if(str.split(":")[0]=='S'){
				label_no=str.split(":")[1];
			}
			if(str.split(":")[0]=='PN'){
				label_no=str.split(":")[1];
			}
		}
		
		var e = $.Event('keydown');
		e.keyCode = 13;
		$(last_scan_ele).val(label_no).trigger(e);
		$("#searchForm").submit();
	}
	
}

function doScan(ele) {
	var url = "../../doScan";
	last_scan_ele = $("#" + ele);
	if(IsPC() ){
		$(last_scan_ele).focus();
		return false;
	}
	var iframe_id=self.frameElement.getAttribute('id');
	$(window.parent.document).find("#activeIframe").val(iframe_id)
	var a = document.createElement("a");
	a.target="_parent";
	a.href = url;
	a.click();
}