var listdata = [];
var lastrow,lastcell; 
$(function(){
	
	$("#dataGrid").dataGrid({
    	url: baseUrl + 'cswlms/dispatchingBillPicking/listfabu',
    	datatype: "local",
		data: listdata,
        colModel: [		
			{ label: '包装号', name: 'BARCODE', index: 'BARCODE', width: 160 },
			{ label: '拣配单号', name: 'DISPATCHING_NO', index: 'DISPATCHING_NO', width: 120 },
			{ label: '状态', name: 'STATUS', index: 'STATUSNAME', width: 70,
				formatter:function(val){	// 
            		if("01"==val)return "已发布";
            		if("02"==val)return "待打印";
            		if("03"==val)return "拣配中";
            		if("08"==val)return "拣配完成";
            		if("04"==val)return "已交接";
            	}	
			},
			{ label: '物料号', name: 'MATERIAL_CODE', index: 'MATERIAL_CODE', width: 100 },
			{ label: '物料描述', name: 'MATERIAL_DESC', index: 'MATERIAL_DESC', width: 200 },
			{ label: '批次', name: 'BATCH', index: 'BATCH', width: 100 },
			{ label: '供应商代码', name: 'VENDOR_CODE', index: 'VENDOR_CODE', width: 100 },
			{ label: '供应商简称', name: 'VENDOR_NAME', index: 'VENDOR_NAME', width: 150 },
			{ label: '上线模式', name: 'TYPE', index: 'TYPENAME', width: 70,
				formatter:function(val){	// 
            		if("01"==val)return "JIT";
            		if("02"==val)return "JIS";
            		if("03"==val)return "EKANBAN";
            	}	
			},
			{ label: '数量', name: 'QUANTITY', index: 'QUANTITY', width: 90 },
			{ label: '仓管员', name: '', index: '', width: 120 },
			{ label: '状态', name: 'STATUS', index: 'STATUS', width: 70,hidden:true},
			{ label: '线别', name: 'LINE_CATEGORY', index: 'LINE_CATEGORY', width: 80 },
			{ label: '剩余交接时间', name: 'LEFT_HANDOVER_TIMES', index: 'LEFT_HANDOVER_TIMES', width: 120 },
			{ label: '', name: 'XJ_QTY', index: 'XJ_QTY', width: 100 ,hidden:true},
			{ label: '', name: 'ITEM_NO', index: 'ITEM_NO', width: 100,hidden:true},
			{ label: '', name: 'COMPONENT_NO', index: 'COMPONENT_NO', width: 100,hidden:true},
			{ label: '', name: 'FROM_PLANT_CODE', index: 'FROM_PLANT_CODE', width: 100,hidden:true},
			{ label: '', name: 'LGORT', index: 'LGORT', width: 100,hidden:true},
			{ label: '', name: 'TYPE', index: 'TYPE', width: 150 ,hidden:true},
			{ label: '', name: 'ID', index: 'ID', width: 120 ,hidden:true}

        ],
        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
		},
        viewrecords: true,
        cellEdit:true,
        cellurl:'#',
        cellsubmit:'clientArray',
        autowidth:true,
        shrinkToFit:false,  
        autoScroll: true, 
        multiselect: true,
        ajaxSuccess:function(){
        },
        
        gridComplete:function(){
        	//紧急物料（urgentFlag不为空的）设置自定义样式
            /*var ids = $("#dataGrid").getDataIDs();
            for(var i=0;i<ids.length;i++){
                var rowData = $("#dataGrid").getRowData(ids[i]);
                if(rowData.urgentFlag != null && rowData.urgentFlag !='' && rowData.urgentFlag != undefined){
                	//如果是紧急物料设置自定义的颜色
                    $('#'+ids[i]).find("td").addClass("urgent-select");
                }
            }*/
        },
        beforeSelectRow: function (rowid, e) {  //点击复选框才选中行
			   var $myGrid = $(this),  
			   i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),  
			   cm = $myGrid.jqGrid('getGridParam', 'colModel'); 
			   if(cm[i].name == 'cb'){
				   $('#dataGrid').jqGrid('setSelection',rowid);
			   }			   		
			   return (cm[i].name == 'cb'); 
			   
			}
        
		
    });

});
			
var vm = new Vue({
	el:"#vue",
	data:{
		sortTypeList:[],
		sortTypeName:"",
		sortType:""
	},
	
	created:function(){
		var werks = $("#werks").val();
		
      	$.ajax({
        	  url:baseUrl + "cswlms/dispatchingBillPicking/listAssembly",
        	  data:{F_WERKS:werks},
        	  success:function(resp){
        		  vm.sortTypeList =resp.data;
        		  vm.sortType=vm.sortTypeList[0].SORT_NUM;
        	  }
          });
		
	},
	methods:{
		query:function(){
			$("#searchForm").submit();
		},
		
		
		onStatusChange:function(event,id){},
		onPlantChange:function(event){
			
	          var werks = event.target.value;
	          $.ajax({
	        	  url:baseUrl + "cswlms/dispatchingBillPicking/listAssembly",
	        	  data:{F_WERKS:werks},
	        	  success:function(resp){
	        		  vm.sortTypeList =resp.data;
	        		  
	        		  vm.sortType=vm.sortTypeList[0].SORT_NUM;
	        		 
	        		
	        	  }
	          });
	          
		},
		save:function(){},
		querylistbydanhao:function(){
			queryList();
		},
		calchaifen:function(){
			$("#c_chai_s_qty").val($("#c_qty").val()-$("#c_chai_qty").val());
		},
		dispatchingprint:function(){
			var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
			
			if(ids.length==0){
				js.alert("请选择要打印的数据!");
				$(".btn").attr("disabled", false);
				return;
			}
			var dis_type="";
			for(var id in ids){
				var row = $("#dataGrid").jqGrid('getRowData',ids[id]);
				if(dis_type!=""&&dis_type!=row.TYPE){
					js.alert("请选择同一个类型的上线模式!");
					return;
				}
				dis_type=row.TYPE;
				
			}
			
			var row = $("#dataGrid").jqGrid('getRowData',ids[0]);
			if(row.TYPE=='02'){
				printjisAjax();
			}else{
				printfeijisAjax();
			}
		},
		initData:function(){}
	}
});

function export2Excel(){
	var column=[
      	{ "title": '包装号', "data": 'BARCODE', "class":'center' },
		{ "title": '拣配单号', "data": 'DISPATCHING_NO', "class":'center'  },			
		{ "title": '状态', "data": 'STATUS', "class":'center'  },
		{ "title": '物料号', "data": 'MATERIAL_CODE',"class":'center'  }, 			
		{ "title": '物料描述', "data": 'MATERIAL_DESC',"class":'center'  },
		{ "title": '批次', "data": 'BATCH',"class":'center'  },
		{ "title": '供应商编码', "data": 'VENDOR_CODE',"class":'center'  },
		{ "title": '供应商名称', "data": 'VENDOR_NAME',"class":'center'  },
		{ "title": '上线模式', "data": 'TYPE',"class":'center'  }, 			
		{ "title": '数量', "data": 'QUANTITY',"class":'center'  },
		{ "title": '线别', "data": 'LINE_CATEGORY',"class":'center'  },
		{ "title": '剩余交接时间', "data": 'LEFT_HANDOVER_TIMES',"class":'center'  }
      ]	;
	var results=[];
	var params={};
	params["FROM_PLANT_CODE"]=$("#werks").val();
	params["BARCODE"]=$("#dispatchingNo").val();
	params["LINE_CATEGORY"]=$("#lineCategory").val();
	params["PLANT_CODE"]=$("#plantCode").val();
	params["STATUS"]=$("#dispatching_status").val();
	params["TYPE"]=$("#dispatching_type").val();
	params["SORT_TYPE"]=$("#sortType").val();
	$.ajax({
		url:baseURL+"cswlms/dispatchingBillPicking/listfabu",
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) { 
		    results=response.result;
		    for(var i=0;i<results.length;i++){
		    	if(results[i].STATUS=='01'){
		    		results[i].STATUS='已发布';
		    	}else if(results[i].STATUS=='02'){
		    		results[i].STATUS='待打印';
		    	}else if(results[i].STATUS=='03'){
		    		results[i].STATUS='拣配中';
		    	}else if(results[i].STATUS=='08'){
		    		results[i].STATUS='拣配完成';
		    	}else if(results[i].STATUS=='04'){
		    		results[i].STATUS='已交接';
		    	}
		    	
		    	if(results[i].TYPE=='01'){
		    		results[i].TYPE='JIT';
		    	}else if(results[i].TYPE=='02'){
		    		results[i].TYPE='JIS';
		    	}else if(results[i].TYPE=='03'){
		    		results[i].TYPE='EKANBAN';
		    	}
		    }
		}
	})
	var rowGroups=[];
	getMergeTable(results,column,rowGroups,"总装物流需求");	
}

function queryList(){
	$.ajax({
		url:baseURL+"cswlms/dispatchingBillPicking/listfabu",
		dataType : "json",
		type : "post",
		data : {
			"FROM_PLANT_CODE":$("#werks").val(),
			"BARCODE":$("#dispatchingNo").val(),
			"LINE_CATEGORY":$("#lineCategory").val(),
			"PLANT_CODE":$("#plantCode").val(),
			"STATUS":$("#dispatching_status").val(),
			"TYPE":$("#dispatching_type").val(),
			"SORT_TYPE":$("#sortType").val()
		},
		async: true,
		success: function (response) { 
			$("#dataGrid").jqGrid("clearGridData", true);
			listdata.length=0;
			
			if(response.code == "0"){
				listdata = response.result;
				$('#dataGrid').jqGrid('setGridParam',{data: listdata}).trigger( 'reloadGrid' );
			}else{
				alert(response.msg)
			}
			$(".btn").attr("disabled", false);
		}
	});
	

}

$("#btnquerydispatchingquery").click(function () {
	queryList();
});

$("#btnJISDispatchingfabu").click(function () {
	dispatchingupdate();
});


function dispatchingupdate(){
	
	$(".btn").attr("disabled", true);
	$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
	//从jqGrid获取选中的行数据
	var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
	
	if(ids.length==0){
		js.alert("请选择要重新发布的数据!");
		$(".btn").attr("disabled", false);
		return;
	}
	var rows = [];
	for(var id in ids){
		var row = $("#dataGrid").jqGrid('getRowData',ids[id]);
		rows[id] = row;
		
		if(row.TYPE=='02'){
			js.alert("JIS需求不允许重新发布!");
			$(".btn").attr("disabled", false);
			return;
		}
		
		if(row.STATUS=='01'||row.STATUS=='04'){
			js.alert("不允许对已发布/已交接的单据重新发布!");
			$(".btn").attr("disabled", false);
			return;
		}
		
		
	}
	
	js.loading();
	
	$.ajax({
		url:baseUrl + "cswlms/dispatchingBillPicking/updatefabu",
		dataType : "json",
		type : "post",
		data : {
			"ARRLIST":JSON.stringify(rows)
		},
		async: false,
		success:function(resp){
			if(resp.code == '0'){
				js.showMessage("重新发布成功!  "+resp.msg,null,null,1000*600000);
				if(resp.jkinfo!=""){
					js.showErrorMessage("调用物流接口错误  "+resp.jkinfo,1000*100);
				}
				queryList();
				$(".btn").attr("disabled", false);
				js.closeLoading();
			}else{
				alert(resp.msg);
				$(".btn").attr("disabled", false);
				js.closeLoading();
			}
		}
	});
	js.closeLoading();

}

$("#btnJISDispatchingchaifen").click(function () {
	
var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
	
	if(ids.length==0){
		js.alert("请选择要拆分的数据!");
		return;
	}
	var rows = [];
	
		var row = $("#dataGrid").jqGrid('getRowData',ids[0]);
		$("#c_from_plant_code").val(row.FROM_PLANT_CODE);
		$("#c_barcode").val(row.BARCODE);
		$("#c_dispatching_no").val(row.DISPATCHING_NO);
		$("#c_material_no").val(row.MATERIAL_CODE);
		$("#c_material_desc").val(row.MATERIAL_DESC);
		$("#c_qty").val(row.QUANTITY);
	
    if(row.STATUS!="01"){
    	js.alert("只有已发布状态的单据才能进行需求拆分!");
		return;
    }
		
	layer.open({
		type : 1,
		offset : '50px',
		skin : 'layui-layer-molv',
		title : "需求拆分",
		area : [ '600px', '450px' ],
		shade : 0,
		shadeClose : false,
		content : jQuery("#dispatchingchaifenLayer"),
		btn : [ '确认','关闭'],
		btn1 : function(index) {
			$(".btn").attr("disabled", false);
			layer.close(index);
			dispatchinchaifen();
		},
		btn2 : function(index) {
			layer.close(index);
		}
	});
});

function dispatchinchaifen(){
	if($("#c_chai_qty").val()==""){
		alert("拆分数量不能为空！");
		return;
	}
	js.loading();
	
	$.ajax({
		url:baseUrl + "cswlms/dispatchingBillPicking/dispatchingchaif",
		dataType : "json",
		type : "post",
		data : {
			"CHAIFQTY":$("#c_chai_qty").val(),
			"BARCODE":$("#c_barcode").val()
		},
		async: false,
		success:function(resp){
			if(resp.code == '0'){
				js.showMessage("拆分成功!  "+resp.msg,null,null,1000*600000);
				if(resp.jkinfo!=""){
					js.showErrorMessage("调用物流接口错误  "+resp.jkinfo,1000*100);
				}
				$(".btn").attr("disabled", false);
				js.closeLoading();
			}else{
				alert(resp.msg);
				$(".btn").attr("disabled", false);
				js.closeLoading();
			}
		}
	});
	js.closeLoading();

}

function printfeijisAjax(){
	$(".btn").attr("disabled", true);
	$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
	//从jqGrid获取选中的行数据
	var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
	
	if(ids.length==0){
		js.alert("请选择要打印的数据!");
		$(".btn").attr("disabled", false);
		return;
	}
	
	var barcodelist="";
	var rows = [];
	for(var id in ids){
		var row = $("#dataGrid").jqGrid('getRowData',ids[id]);
		rows[id] = row;
		if(row.STATUS!="03"&&row.STATUS!="08"){
			if(barcodelist==""){
				barcodelist=row.BARCODE;
			}else{
				barcodelist=barcodelist+","+row.BARCODE;
			}
			
		}
	}
	
	if(barcodelist!==""){
		js.alert("包装号为"+barcodelist+"的状态，不能进行补打印");
		$(".btn").attr("disabled", false);
		return;
	}
	
	feiJISPrint(rows);//
	
	/*$.ajax({
		url:baseUrl + "cswlms/dispatchingBillPicking/printfeijis",
		dataType : "json",
		type : "post",
		data : {
			"ARRLIST":JSON.stringify(rows)
		},
		async: false,
		success:function(resp){
			if(resp.code == '0'){
				js.showMessage("打印成功!  "+resp.msg,null,null,1000*600000);
				//列表 单号缓存都置位空
				//$("#dataGrid").jqGrid("clearGridData", true);
				
				//queryList("02");
				//
				$(".btn").attr("disabled", false);
				js.closeLoading();
			}else{
				js.alert(resp.msg);
				js.showErrorMessage("打印成功!  "+resp.msg,null,null,1000*600000);
				$(".btn").attr("disabled", false);
				js.closeLoading();
			}
		}
	});*/
}

function feiJISPrint(rows){
	js.loading();
	
	var barcode_str="";
	$.each(rows,function(i,row){
		if(""==barcode_str){
			barcode_str=row.BARCODE;
		}else{
			barcode_str=barcode_str+","+row.BARCODE;
		}
		
	});
	
	$("#BARCODE").val(barcode_str);
	$("#printButton_feijis").click();
	
	$(".btn").attr("disabled", false);
	js.closeLoading();
}



function printjisAjax(){
	$(".btn").attr("disabled", true);
	$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
	//从jqGrid获取选中的行数据
	var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
	
	if(ids.length==0){
		js.alert("请选择要打印的数据!");
		$(".btn").attr("disabled", false);
		return;
	}
	
	var barcodelist="";
	var rows = [];
	for(var id in ids){
		var row = $("#dataGrid").jqGrid('getRowData',ids[0]);
		rows[id] = row;
		if(row.STATUS!="03"&&row.STATUS!="08"){
			if(barcodelist==""){
				barcodelist=row.BARCODE;
			}else{
				barcodelist=barcodelist+","+row.BARCODE;
			}
		}
	}
	
	if(barcodelist!==""){
		js.alert("包装号为"+barcodelist+"的状态，不能进行补打印");
		$(".btn").attr("disabled", false);
		return;
	}
	
	JISPrint(rows);//弹出打印列表
	
	/*$.ajax({
		url:baseUrl + "cswlms/dispatchingBillPicking/printjis",
		dataType : "json",
		type : "post",
		data : {
			"ARRLIST":JSON.stringify(rows)
		},
		async: false,
		success:function(resp){
			if(resp.code == '0'){
				js.showMessage("打印成功!  "+resp.msg,null,null,1000*600000);
				//列表 单号缓存都置位空
				//$("#dataGrid").jqGrid("clearGridData", true);
				
				//queryList("02");
				//
				$(".btn").attr("disabled", false);
				js.closeLoading();
			}else{
				js.alert(resp.msg);
				js.showErrorMessage("打印成功!  "+resp.msg,null,null,1000*600000);
				$(".btn").attr("disabled", false);
				js.closeLoading();
			}
		}
	});*/
}

function JISPrint(rows){
	
	js.loading();
	
	var dispatching_str="";
	$.each(rows,function(i,row){
		if(""==dispatching_str){
			dispatching_str=row.DISPATCHING_NO;
		}else{
			dispatching_str=dispatching_str+","+row.DISPATCHING_NO;
		}
		
	});
	
	$("#DISPATCHING_NO").val(dispatching_str);
	$("#printButton_jis").click();
	
	$(".btn").attr("disabled", false);
	js.closeLoading();
}







