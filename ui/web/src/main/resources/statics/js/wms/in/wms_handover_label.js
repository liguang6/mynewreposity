var listdata = [];
var lastrow,lastcell; 
var  labelnohandover="";
var last_scan_ele="";

$(function(){
	$("#dataGrid").dataGrid({
    	url: baseUrl + 'in/wmsinhandoverbound/labellist',
    	datatype: "local",
		data: listdata,
        colModel: [			
			{ label: '标签号', name: 'LABEL_NO', index: 'LABEL_NO', width: 120 }, 
			{ label: '批次', name: 'BATCH', index: 'BATCH', width: 90 },
			{ label: '物料号', name: 'MATNR', index: 'MATNR', width: 90 }, 
			{ label: '物料描述', name: 'MAKTX', index: 'MAKTX', width: 220 }, 
			{ label: '标签数量', name: 'IN_QTY', index: 'IN_QTY', width: 80 },
			{ label: '<span style="color:blue">可进仓数量</span>', name: 'MAY_IN_QTY', index: 'MAY_IN_QTY', width: 80 ,editable:true,editrules:{
   				number:true} },
			{ label: '单位', name: 'UNIT', index: 'UNIT', width: 60 },
			{ label: '工厂', name: 'WERKS', index: 'WERKS', width: 80 },
			{ label: '仓库号', name: 'WH_NUMBER', index: 'WH_NUMBER', width: 80 },
			{ label: '<span style="color:blue">库位</span>', name: 'LGORT', index: 'LGORT', width: 80,editable:true },
			{ label: '库存类型', name: 'SOBKZ', index: 'SOBKZ', width: 80 },
			{ label: '供应商代码', name: 'LIFNR', index: 'LIFNR', width: 100 },
			{ label: '供应商名称', name: 'LIKTX', index: 'LIKTX', width: 160 },
			{ label: '备注', name: 'REMARK', index: 'REMARK', width: 80 },
			{ label: '储位', name: 'BIN_CODE', index: 'BIN_CODE', width: 80 },
			{ label: '仓管员', name: 'WH_MANAGER', index: 'WH_MANAGER', width: 80 },
			{ label: '<span style="color:blue">行文本</span>', name: 'ITEM_TEXT', index: 'ITEM_TEXT', width: 150,editable:true },
			{ label: '', name: 'BUSINESS_NAME', index: 'BUSINESS_NAME', width: 160,hidden:true },//
			{ label: '', name: 'BUSINESS_TYPE', index: 'BUSINESS_TYPE', width: 160,hidden:true },
			{ label: '', name: 'RECEIPT_NO', index: 'RECEIPT_NO', width: 160,hidden:true },
			{ label: '', name: 'RECEIPT_ITEM_NO', index: 'RECEIPT_ITEM_NO', width: 160,hidden:true },
			{ label: '', name: 'INBOUND_NO', index: 'INBOUND_NO', width: 160,hidden:true },
			{ label: '', name: 'INBOUND_ITEM_NO', index: 'INBOUND_ITEM_NO', width: 160,hidden:true },
			{ label: '', name: 'LABEL_FALG', index: 'LABEL_FALG', width: 160,hidden:true },
			{ label: '', name: 'REF_SAP_MATDOC_YEAR', index: 'REF_SAP_MATDOC_YEAR', width: 160,hidden:true },
			{ label: '', name: 'REF_SAP_MATDOC_NO', index: 'REF_SAP_MATDOC_NO', width: 160,hidden:true },
			{ label: '', name: 'REF_SAP_MATDOC_ITEM_NO', index: 'REF_SAP_MATDOC_ITEM_NO', width: 160,hidden:true },
			{ label: '', name: 'PO_NO', index: 'PO_NO', width: 160,hidden:true },
			{ label: '', name: 'PO_ITEM_NO', index: 'PO_ITEM_NO', width: 160,hidden:true },
			{ label: '', name: 'MO_NO', index: 'MO_NO', width: 160,hidden:true },
			{ label: '', name: 'MO_ITEM_NO', index: 'MO_ITEM_NO', width: 160,hidden:true },
			{ label: '', name: 'SO_NO', index: 'SO_NO', width: 160,hidden:true },
			{ label: '', name: 'F_BATCH', index: 'F_BATCH', width: 90,hidden:true },
			{ label: '', name: 'SO_ITEM_NO', index: 'SO_ITEM_NO', width: 160,hidden:true }
			
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
            var ids = $("#dataGrid").getDataIDs();
            for(var i=0;i<ids.length;i++){
                var rowData = $("#dataGrid").getRowData(ids[i]);
                if(rowData.urgentFlag != null && rowData.urgentFlag !='' && rowData.urgentFlag != undefined){
                	//如果是紧急物料设置自定义的颜色
                    $('#'+ids[i]).find("td").addClass("urgent-select");
                }
            }
        },
        afterSaveCell:function(rowid, cellname, value, iRow, iCol){},
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
		warehourse:[],
		whNumber:"",//
		PZDDT:getCurDate(),
		JZDDT:getCurDate()
	},
	watch:{
		whNumber:{

			handler:function(newVal,oldVal){

				$.ajax({
		        	  url:baseUrl + "in/wmsinbound/relatedAreaNamelist",
		        	  data:{"WERKS":$("#werks").val(),"WH_NUMBER":newVal},
		        	  success:function(resp){
		        		 vm.relatedareaname = resp.result;
		        	  }
		          })
			
			}
		
		}
	},
	created:function(){
		//初始化查询仓库
		var plantCode = $("#werks").val();
		
        //初始化仓库
        $.ajax({
      	  url:baseUrl + "common/getWhDataByWerks",
      	  data:{"WERKS":plantCode},
      	  success:function(resp){
        		vm.warehourse = resp.data;
          		if(resp.data.length>0){
        			 vm.whNumber=resp.data[0].WH_NUMBER
        		 }
          		
          	  }
        });
        
      
        
	},
	methods:{
		query:function(){
			$("#searchForm").submit();
		},
		
		save:function(){
			var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
			
			if(ids.length==0){
				js.alert("请选择要交接的数据!");
				return;
			}
			//commonHandover(handoverSave,handoverSave);
			//验证交接模式
			var business_rows=[];
			var rows = [];
			for(var id in ids){
				var row = $("#dataGrid").jqGrid('getRowData',ids[id]);
				
				rows[id] = row;
				business_rows[id]=row.BUSINESS_NAME;
			}
			validHandover(business_rows);//先验证数据，再验证权限
			/*layer.open({
        		type : 1,
        		offset : '50px',
        		skin : 'layui-layer-molv',
        		title : "确认人信息",
        		area : [ '600px', '350px' ],
        		shade : 0,
        		shadeClose : false,
        		content : jQuery("#handoverConfirmLayer"),
        		btn : [ '交接并过帐','无交接并过帐','关闭'],
        		btn1 : function(index) {
        			layer.close(index);
        		},
        		btn2 : function(index) {
        			$(".btn").attr("disabled", false);
        			
        			layer.close(index);
        			handoverSave();
        		},
        		btn3 : function(index) {
        			layer.close(index);
        		}
        	});*/
			
			
		},
		onStatusChange:function(event,id){},
		onPlantChange:function(event){

			  //工厂变化的时候，更新仓库号下拉框
	          var plantCode = event.target.value;
	          if(plantCode === null || plantCode === '' || plantCode === undefined){
	        	  return;
	          }
	          //查询工厂仓库
	          $.ajax({
	        	  url:baseUrl + "common/getWhDataByWerks",
	        	  data:{"WERKS":plantCode},
	        	  success:function(resp){vm.warehourse = resp.data;
	        		if(resp.data.length>0){
	       			 vm.whNumber=resp.data[0].WH_NUMBER
	       		 }}
	          })
	         
		
		},
		
		getVendorNoFuzzy:function(){
			getVendorNoSelect("#lifnr",null,null,$("werks").val());
		},
		
		initDataGrid:function(){}
	}
});

function queryList(label_no){
	$(".btn").attr("disabled", true);
	//再查询列表数据,验证数量
	var rowsData = $('#dataGrid').jqGrid("getRowData");  
	
	$.ajax({
		url:baseURL+"in/wmsinhandoverbound/labellist",
		dataType : "json",
		type : "post",
		data : {
			"WERKS":$("#werks").val(),
			"WH_NUMBER":$("#whNumber").val(),
			"JZ_DATE":$("#jzddt").val(),
			"LABEL_NO":label_no
		},
		async: true,
		success: function (response) { 
			
			if(response.code == "0"){
				
					listdata = listdata.concat(response.result);
					
					$('#dataGrid').jqGrid('setGridParam',{data: listdata}).trigger( 'reloadGrid' );
				
					$(".btn").attr("disabled", false);
					
					labelnohandover=labelnohandover+","+$("#labelNo").val();//把进仓单号都保存起来
					$("#dhhidden").val(labelnohandover);
					$("#labelNo").val("");
			}else{
				alert(response.msg)
				$(".btn").attr("disabled", false);
				$("#werks").removeAttr("disabled");
				$("#whNumber").removeAttr("disabled");
				$("#labelNo").val("");
			}
		}
	});
	
}

function validQty(){
	//再查询列表数据,验证数量
	var label_no=$("#labelNo").val();
	var rowsData = $('#dataGrid').jqGrid("getRowData");  
	$.ajax({
		url:baseURL+"in/wmsinhandoverbound/ValidlabelQyt",
		dataType : "json",
		type : "post",
		data : {
			"WERKS":$("#werks").val(),
			"WH_NUMBER":$("#whNumber").val(),
			"LABEL_NO":label_no,
			"ARRLIST":JSON.stringify(rowsData)
		},
		async: true,
		success: function (response) { 
			
			if(response.code == "0"){
				queryList(label_no);
			}else{
				alert(response.msg)
				//$(".btn").attr("disabled", true);
			}
		}
	});
	//
}

function validHandover(business_rows){
	//再查询列表数据,验证数量
	var label_no=$("#labelNo").val();
	var rowsData = $('#dataGrid').jqGrid("getRowData");  
	$.ajax({
		url:baseURL+"in/wmsinhandoverbound/ValidlabelHandover",
		dataType : "json",
		type : "post",
		data : {
			"WERKS":$("#werks").val(),
			"WH_NUMBER":$("#whNumber").val(),
			"LABEL_NO":label_no,
			"ARRLIST":JSON.stringify(rowsData)
		},
		async: true,
		success: function (response) { 
			
			if(response.code == "0"){
				handoversubmit(business_rows);
			}else{
				alert(response.msg)
				//$(".btn").attr("disabled", true);
			}
		}
	});
	//
}

function handoversubmit(business_rows){
	$.ajax({
  	  url:baseUrl + "config/handoverType/wmsCHandoverTypelist",
  	  data:{
  		  "WERKS":$("#werks").val(),
  		  "WH_NUMBER":$("#whNumber").val(),
  		  "BUSINESS_CODE_LIST":JSON.stringify(business_rows)
  		  },
  	  success:function(resp){
  		  if(resp.handover_type_flag!=""){//存在交接类型不一致的情况
  			  js.alert("存在多个业务交接模式不一致！");
  		  }else{
  			
  		  if(resp.business_strName!=""){//存在 找不到的业务类型
  			  js.alert("交接业务类型"+resp.business_strName+"没有配置交接权限！");
					 return false;
  		  }else{
  			  if(resp.result.length>0){
	        		  if(resp.result[0].HANDOVER_TYPE=='00'){//无交接过账
	        			  var user="";
	        			  var username=""
	        			  handoverSave(user,username);
	        		  }else{
	        			  commonHandover(validateUser,validateUser,resp.result[0].HANDOVER_TYPE);
	        		  }
	        		  
	         	  }else{
	         		 js.alert("交接模式没有配置！");
					 return false;
	         	  }
  		  }
      	  
  	  }
		  }
    })
}

$("#btnqueryhandoverlabel").click(function () {
	if($("#labelNo").val()!=''){//如果单号不为空
		if($("#dhhidden").val()!=''){
			var arrayinbound=$("#dhhidden").val().split(",");
			for(var i=0;i<arrayinbound.length;i++){
				if($("#labelNo").val()==arrayinbound[i]){
					js.alert("标签号不能重复！");
					return false;
				}
			}
		}
		
		validQty();
		//验证成功后再查询出来
		//queryList();
		
		//$("#labelNo").val("");
		$("#werks").attr("disabled","disabled");//设为不可用
		$("#whNumber").attr("disabled","disabled");//设为不可用
	}else{
		js.alert("标签号不能为空！");
		return false;
	}
	
});

$("#btnReset").click(function () {
	$("#dataGrid").jqGrid("clearGridData", true);
	$("#werks").removeAttr("disabled");
	$("#whNumber").removeAttr("disabled");
	$("#labelNo").val("");
	listdata.length=0;
});

$("#btn_delete").click(function () {
	var trs=$("#dataGrid").children("tbody").children("tr");
	var reIndex = 1;
	$.each(trs,function(index,tr){
		var td = $(tr).find("td").eq(0);
		var td_check = $(tr).find("td").eq(1).find("input");
		if(index > 0){
			//if(tr.getAttribute("aria-selected")){
			var ischecked=$(td_check).is(":checked");
			if(ischecked){
				//$(td_check).attr('checked', false);
				//$('#dataGrid').jqGrid('delRowData',index);
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

function validateUser(user,username){
	var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
	
	if(ids.length==0){
		js.alert("请选择要提交的数据!");
		$(".btn").attr("disabled", false);
		return;
	}
	var rows = [];
	var business_rows=[];
	for(var id in ids){
		var row = $("#dataGrid").jqGrid('getRowData',ids[id]);
		
		rows[id] = row;
		business_rows[id]=row.BUSINESS_NAME;
		
	}
	
	//验证交接人员信息
	if(user==""||username==""){
		js.alert("交接人员不能为空！");
		return ;
	}
	$.ajax({
  	  url:baseUrl + "config/handover/wmsCHandoverlist",
  	  data:{
  		  "WERKS":$("#werks").val(),
  		  "WH_NUMBER":$("#whNumber").val(),
  		  "BUSINESS_CODE_LIST":JSON.stringify(business_rows),
  		  "STAFF_NUMBER":user
  		  },
  	  success:function(resp){
      	  if(resp.result.length>0){
      		handoverSave(user,username);
       	  }else{
       		 js.alert("交接人员没有对应权限！");
			 return ;
       	  }
  	  }
    });
}

function handoverSave(user,username){
	var handover="";
	if(user!=""){// 非 无交接过账 要验证
		handover=user+":"+username;
		
	}
	$(".btn").attr("disabled", true);
	$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
	//从jqGrid获取选中的行数据
	var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
	if(ids.length==0){
		js.alert("请选择要提交的数据!");
		$(".btn").attr("disabled", false);
		return false;
	}
	var rows = [];
	for(var id in ids){
		var row = $("#dataGrid").jqGrid('getRowData',ids[id]);
		
		rows[id] = row;
		
		if(row.MAY_IN_QTY-row.IN_QTY>0){
			js.alert("可进仓数量不能大于标签数量!");
			return;
		}
	}
	
	$("#btnsavehandoverlabel").html("交接中...");	
	$("#btnsavehandoverlabel").attr("disabled","disabled");
	js.loading();
	
	$.ajax({
		url:baseUrl + "in/wmsinhandoverbound/save",
		dataType : "json",
		type : "post",
		data : {
			"WERKS":$("#werks").val(),
			"WH_NUMBER":$("#whNumber").val(),
			"PZDDT":$("#pzddt").val(),
			"JZDDT":$("#jzddt").val(),
			"HANDOVER":handover,
			"ARRLIST":JSON.stringify(rows)
		},
		async: false,
		success:function(resp){
			if(resp.code == '0'){
				js.showMessage("交接成功!  "+resp.msg,null,null,10000*600000);
				//列表 单号缓存都置位空
				$("#dataGrid").jqGrid("clearGridData", true);
				labelnohandover="";
				$("#dhhidden").val("");
				listdata.length=0;
				//
				//$(".btn").attr("disabled", false);
				$("#btnqueryhandoverlabel").attr("disabled", false);
				$("#btnsavehandoverlabel").html("交接确认");
				js.closeLoading();
			}else{
				js.alert(resp.msg);
				$(".btn").attr("disabled", false);
				$("#btnsavehandoverlabel").html("交接确认");
				js.closeLoading();
			}
		}
	});
	$("#btnsavehandoverlabel").html("交接确认");
	js.closeLoading();
}

//用于点击其他地方保存正在编辑状态下的行
$('html').bind('click', function(e) { 
    if (!isEmpty(lastcell)) {
        if($(e.target).closest('#dataGrid').length == 0) {  
        	$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
        	lastrow="";
        	lastcell="";
        }
    }
});

$("#labelNo").keydown(function(e){
	if(e.keyCode===13){
		var labelobj=getLabelData($("#labelNo").val().trim());
		$("#labelNo").val(labelobj.LABEL_NO);
		
		if($("#labelNo").val()!=''){//如果单号不为空
			if($("#dhhidden").val()!=''){
				var arrayinbound=$("#dhhidden").val().split(",");
				for(var i=0;i<arrayinbound.length;i++){
					if($("#labelNo").val()==arrayinbound[i]){
						js.alert("标签号不能重复！");
						return false;
					}
				}
			}
			
			validQty();
			
			$("#werks").attr("disabled","disabled");//设为不可用
			$("#whNumber").attr("disabled","disabled");//设为不可用
		}
	}
});

function funFromjs(result) {
	var str_arr = result.split(";");
	if(str_arr.length>0){
		var matnr = "";
		for(var i=0;i<str_arr.length;i++){
			var str = str_arr[i];
			if(str.split(":")[0]=='S' || str.split(":")[0]=='PN'){
				matnr=str.split(":")[1];
			}
		}
		var e = $.Event('keydown');
		e.keyCode = 13;
		$(last_scan_ele).val(matnr);
		$("#btnqueryhandoverlabel").click();
	}
}

function doScan(ele) {
	var url = "../../doScan";
	last_scan_ele = "#" + ele;
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
