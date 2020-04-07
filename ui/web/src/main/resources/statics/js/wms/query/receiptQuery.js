var last_scan_ele="";
var vm = new Vue({
	el:'#rrapp',
	data:{
		showList:false,
		businessList:[],
		receiptDateStart: '',
		receiptDateEnd: '',
		lgort:[],
		wh_list:[],
		werks:'',
		whNumber:'',
	},
	watch:{
		   werks : {
			  handler:function(newVal,oldVal){
				$.ajax({
					url:baseURL+"common/getWhDataByWerks",
					dataType : "json",
					type : "post",
					data : {
						"WERKS":newVal
					},
					async: true,
					success: function (response) { 
						vm.wh_list=response.data
						vm.whNumber=response.data[0]['WH_NUMBER']
					}
				});
					
				}
			}
	},
	created:function(){
		this.businessList=getBusinessList("01");
		this.werks=$("#werks").find("option").first().val();
	},
	methods: {
		query: function () {
			var receiptDateStart = $("#receiptDateStart").val();
			var receiptDateEnd = $("#receiptDateEnd").val();
			if(receiptDateStart=='' || receiptDateEnd==""){
				alert('必须选择收货起始日期！');
				return false;
			}
			$("#receiptDateStart").val(receiptDateStart +" 00:00:00");
			$("#receiptDateEnd").val(receiptDateEnd +" 23:59:59");
			
			$("#searchForm").submit();
		},
		exp: function () {
			export2Excel();
		},
		print: function (type) {
			var receiptNo='';
			var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");
			if(ids.length == 0){
				alert("当前还没有勾选任何行项目数据！");
				return false;
			}
			for (var i = 0; i < ids.length; i++) {
				var data=$("#dataGrid").jqGrid('getRowData',ids[i]);
				receiptNo+=data.RECEIPT_NO+',';
			}
			var url = baseURL+"query/receiptQuery/getLabelData";
			
			$.ajax({
				type: "POST",
			    url: url,
			    dataType : "json",
				data : {
					receiptNoList:receiptNo,
				},
			    success: function(r){
			    	if(r.code === 0){
			    		$("#labelList").val(JSON.stringify(r.data));
						$("#printButton").click();
					}else{
						js.showMessage(r.msg);
					}
				}
			});
		},
		printA4: function (type) {
			var receiptNo='';
			var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");
			if(ids.length == 0){
				alert("当前还没有勾选任何行项目数据！");
				return false;
			}
			for (var i = 0; i < ids.length; i++) {
				var data=$("#dataGrid").jqGrid('getRowData',ids[i]);
				receiptNo+=data.RECEIPT_NO+',';
			}
			var url = baseURL+"query/receiptQuery/getLabelData";
			
			$.ajax({
				type: "POST",
			    url: url,
			    dataType : "json",
				data : {
					receiptNoList:receiptNo,
				},
			    success: function(r){
			    	if(r.code === 0){
			    		$("#labelListA4").val(JSON.stringify(r.data));
						$("#printButtonA4").click();
					}else{
						js.showMessage(r.msg);
					}
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
	  },
	}
});
$(function () {
	
	var now = new Date(); //当前日期
	var startDate=new Date(now.getTime()-7*24*3600*1000);
	$("#receiptDateStart").val(formatDate(startDate));
	$("#receiptDateEnd").val(formatDate(now));
	
	vm.receiptDateStart = formatDate(startDate);
	vm.receiptDateEnd = formatDate(now);
	
	$('#dataGrid').dataGrid({
		searchForm: $("#searchForm"),
		columnModel: [
			{header:'工厂', name:'WERKS', width:45, align:"center",frozen: true},
			{header:'仓库号', name:'WH_NUMBER', width:55, align:"center",frozen: true},
			{header:'收货类型', name:'BUSINESS_NAME', width:90, align:"center",frozen: true},
			{header:'收货单号', name:'RECEIPT_NO', width:110, align:"center",frozen: true},
			{header:'行项目', name:'RECEIPT_ITEM_NO', width:55, align:"center",frozen: true},
			{header:'料号', name:'MATNR', width:90, align:"center",frozen: true},
			{header:'物料描述', name:'MAKTX', width:160, align:"center"},			
			{header:'单位', name:'UNIT', width:50, align:"center"},
			{header:'实收数量', name:'RECEIPT_QTY', width:65, align:"center"},
			{header:'可进仓数量', name:'INABLE_QTY', width:75, align:"center"},
			{header:'进仓数量', name:'IN_QTY', width:65, align:"center"},
			{header:'试装数量', name:'TRY_QTY', width:65, align:"center"},
			{header:'试装进仓数量', name:'TRY_IN_QTY', width:90, align:"center"},
			{header:'可退货数量', name:'RETURNABLE_QTY', width:80, align:"center"},
			{header:'退货冲销数量', name:'RETURN_QTY', width:90, align:"center"},
			{header:'质检破坏数量', name:'DESTROY_QTY', width:90, align:"center"},
			{header:'IQC成本中心', name:'IQC_COST_CENTER', width:100, align:"center"},
			{header:'批次', name:'BATCH', width:110, align:"center"},
			{header:'申请人', name:'AFNAM', width:70, align:"center"},
			{header:'需求跟踪号', name:'BEDNR', width:90, align:"center"},
			{header:'收料原因', name:'RECEIPT_REASON', width:90, align:"center"},
			{header:'收料员', name:'RECEIVER', width:80, align:"center"},
			{header:'发货工厂', name:'F_WERKS', width:80, align:"center"},
			{header:'供应商代码', name:'LIFNR', width:80, align:"center"},
			{header:'供应商名称', name:'LIKTX', width:80, align:"center"},
			{header:'操作人', name:'CREATOR', width:80, align:"center"},
			{header:'收货时间', name:'RECEIPT_DATE', width:80, align:"center"},
			{header:'采购订单号', name:'PO_NO', width:80, align:"center"},
			{header:'采购订单行项目', name:'PO_ITEM_NO', width:80, align:"center"},
			{header:'SCM送货单号', name:'ASNNO', width:80, align:"center"},
			{header:'SCM送货单行项目', name:'ASNITM', width:80, align:"center"},
			{header:'SAP交货单号', name:'SAP_OUT_NO', width:80, align:"center"},
			{header:'SAP交货单行项目', name:'SAP_OUT_ITEM_NO', width:80, align:"center"},
			{header:'303凭证号', name:'SAP_MATDOC_NO', width:80, align:"center"},
			{header:'303凭证行项目', name:'SAP_MATDOC_ITEM_NO', width:80, align:"center"},
		],	
		viewrecords: true,
	    shrinkToFit:false,
		rowNum: 15,  
		width:3100,
		showCheckbox:true,
		rowList : [  15,30 ,50],
        rownumWidth: 35, 
        multiselect: true,
	});
	$("#dataGrid").jqGrid("setFrozenColumns");
	var hideCol=['SOBKZ','ASNNO','ASNITM','PO_NO','PO_ITEM_NO','SAP_OUT_NO',
		'SAP_OUT_ITEM_NO','SAP_MATDOC_NO','SAP_MATDOC_ITEM_NO'];
	DynamicColume(hideCol,"更多列","#dataGrid","#example-getting-started");
});
function export2Excel(){
	var column=[      
		{"title":'工厂', "data":'WERKS', "class":"center"},
		{"title":'仓库号', "data":'WH_NUMBER', "class":"center"},
		{"title":'收货类型', "data":'BUSINESS_NAME',"class":"center"},
		{"title":'收货单号', "data":'RECEIPT_NO',"class":"center"},
		{"title":'行项目', "data":'RECEIPT_ITEM_NO',"class":"center"},
		{"title":'料号', "data":'MATNR', "class":"center"},
		{"title":'物料描述', "data":'MAKTX', "class":"center"},
		{"title":'单位', "data":'UNIT', "class":"center"},
		{"title":'实收数量', "data":'RECEIPT_QTY', "class":"center"},
		{"title":'可进仓数量', "data":'INABLE_QTY', "class":"center"},
		{"title":'进仓数量', "data":'IN_QTY', "class":"center"},
		{"title":'试装数量', "data":'TRY_QTY', "class":"center"},
		{"title":'试装进仓数量', "data":'TRY_IN_QTY', "class":"center"},
		{"title":'可退货数量', "data":'RETURNABLE_QTY', "class":"center"},
		{"title":'退货冲销数量', "data":'RETURN_QTY',  "class":"center"},
		{"title":'质检破坏数量', "data":'DESTROY_QTY', "class":"center"},
		{"title":'IQC成本中心', "data":'IQC_COST_CENTER', "class":"center"},
		{"title":'批次', "data":'BATCH', "class":"center"},
		{"title":'申请人', "data":'AFNAM',"class":"center"},
		{"title":'需求跟踪号', "data":'BEDNR', "class":"center"},
		{"title":'收料原因', "data":'RECEIPT_REASON', "class":"center"},
		{"title":'收料员', "data":'RECEIVER', "class":"center"},
		{"title":'采购订单号', "data":'PO_NO',  "class":"center"},
		{"title":'采购订单行项目', "data":'PO_ITEM_NO', "class":"center"},
		{"title":'SCM送货单号', "data":'ASNNO', "class":"center"},
		{"title":'SCM送货单行项目', "data":'ASNITM', "class":"center"},
		{"title":'SAP交货单号', "data":'SAP_OUT_NO',  "class":"center"},
		{"title":'SAP交货单行项目', "data":'SAP_OUT_ITEM_NO', "class":"center"},
		{"title":'303凭证号', "data":'SAP_MATDOC_NO', "class":"center"},
		{"title":'303凭证行项目', "data":'SAP_MATDOC_ITEM_NO', "class":"center"},
		{"title":'发货工厂', "data":'F_WERKS', "class":"center"},
		{"title":'供应商代码', "data":'LIFNR',  "class":"center"},
		{"title":'供应商名称', "data":'LIKTX', "class":"center"},
		{"title":'操作人', "data":'CREATOR',"class":"center"},
		{"title":'收货时间', "data":'RECEIPT_DATE', "class":"center"},
      ]	;
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"query/receiptQuery/list",
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
	getMergeTable(results,column,rowGroups,"收货单");	
}

function funFromjs(result) {
	var str_arr = result.split(";");
	if(str_arr.length>0){
		var matnr = "";
		for(var i=0;i<str_arr.length;i++){
			var str = str_arr[i];
			if(str.split(":")[0]=='M'){
				matnr=str.split(":")[1].split("/")[0];
			}
		}
		
		var e = $.Event('keydown');
		e.keyCode = 13;
		$(last_scan_ele).val(matnr).trigger(e);
		vm.query();
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

