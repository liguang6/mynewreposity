var listdata = [];
var lastrow,lastcell; 
var total_count=0;
var mapnew={};
var maprecordNo={};
var maprecordhas={};//已选择的记录
var mapRecordV={}
var sy_count=0;
$(function(){
	
	$("#dataGrid").dataGrid({
    	url: baseUrl + 'cswlms/dispatchingBillPicking/listjiaojie',
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
			{ label: '到货口', name: 'WAITING_LOCATION', index: 'WAITING_LOCATION', width: 90 },
			{ label: '仓管员', name: '', index: '', width: 120 },
			{ label: '状态', name: 'STATUS', index: 'STATUS', width: 70,hidden:true},
			{ label: '线别', name: 'LINE_CATEGORY', index: 'LINE_CATEGORY', width: 80 },
			{ label: '剩余交接时间', name: 'LEFT_HANDOVER_TIMES', index: 'LEFT_HANDOVER_TIMES', width: 120 },
			{ label: '', name: 'XJ_QTY', index: 'XJ_QTY', width: 100 ,hidden:true},
			{ label: '', name: 'ITEM_NO', index: 'ITEM_NO', width: 100,hidden:true},
			{ label: '', name: 'COMPONENT_NO', index: 'COMPONENT_NO', width: 100,hidden:true},
			{ label: '', name: 'FROM_PLANT_CODE', index: 'FROM_PLANT_CODE', width: 100,hidden:true},
			{ label: '', name: 'PLANT_CODE', index: 'PLANT_CODE', width: 100,hidden:true},
			{ label: '', name: 'LGORT', index: 'LGORT', width: 100,hidden:true},
			{ label: '', name: 'TYPE', index: 'TYPE', width: 150 ,hidden:true},
			{ label: '', name: 'HANDOVER_USER_ID', index: 'HANDOVER_USER_ID', width: 150 ,hidden:true},
			{ label: '', name: 'ID', index: 'ID', width: 120 ,hidden:true},
			{ label: '', name: 'PICK_RECORD_NO', index: 'PICK_RECORD_NO', width: 120 ,hidden:true},
			{ label: '', name: 'PICK_RECORD_USER_ID', index: 'PICK_RECORD_USER_ID', width: 120 ,hidden:true}

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
			   
			},
		onSelectRow:function(rowid,status,e){
			//maprecordNo['拣配确认记录号':'总箱数']
			console.log("maprecordNo[]:",maprecordNo);
			var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
			
			var row = $("#dataGrid").jqGrid('getRowData',ids[0]);
			
			$("#pickRecordNoNum").html(row.PICK_RECORD_NO);//拣配确认记录号
			$("#pickRecordque").html(row.PICK_RECORD_USER_ID);//确认人
			
			var tot=maprecordNo[row.PICK_RECORD_NO];//当前拣配确认记录号对应的 总箱数
			$("#pickRecordTotal").html(maprecordNo[row.PICK_RECORD_NO]);
			
			if(ids.indexOf(rowid)>-1){//返回的下标,勾选
				sy_count++;
				console.log("sy_count++:",tot-sy_count);
				/*if(maprecordNo.containsKey(row.PICK_RECORD_NO)){//勾选的拣配确认记录号存在 map中
					var tots=maprecordNo.get(row.PICK_RECORD_NO);
				}*/
				
				$("#pickRecordshyTotal").html(tot-sy_count);
			}else{//取消
				sy_count--;
				console.log("sy_count--:",tot-sy_count);
				$("#pickRecordshyTotal").html(tot-sy_count);
				
			}
			
			
			}
		
    });

});
			
var vm = new Vue({
	el:"#vue",
	data:{
		sortTypeList:[],
		sortTypeName:"",
		sortType:"",
		PZDDT:getCurDate(),
		JZDDT:getCurDate()
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
		save:function(){
			
			var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
			
			if(ids.length==0){
				js.alert("请选择要交接确认的数据!");
				return;
			}
			//验证交接模式
			var business_rows=[];
			business_rows[0]="46";
			$.ajax({
	        	  url:baseUrl + "config/handoverType/wmsCHandoverTypelist",
	        	  data:{
	        		  "WERKS":$("#werks").val(),
	        		  "WH_NUMBER":"",
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
			        				  dispatchingHandover(user,username);
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
			
		},
		initData:function(){}
	}
});

function queryList(){
	$.ajax({
		url:baseURL+"cswlms/dispatchingBillPicking/listjiaojie",
		dataType : "json",
		type : "post",
		data : {
			"FROM_PLANT_CODE":$("#werks").val(),
			"DISPATCHING_NO":$("#dispatchingNo").val(),
			"LINE_CATEGORY":$("#lineCategory").val(),
			"PLANT_CODE":$("#plantCode").val(),
			"PZDDT":$("#pzddt").val(),
			"JZDDT":$("#jzddt").val(),
			"STATUS":'08'
		},
		async: true,
		success: function (response) { 
			$("#dataGrid").jqGrid("clearGridData", true);
			
			if(response.code == "0"){
				listdata = listdata.concat(response.result);
				$('#dataGrid').jqGrid('setGridParam',{data: listdata}).trigger( 'reloadGrid' );
				$(".btn").attr("disabled", false);
			}else{
				js.alert(response.msg)
			}
			total_count++;
			$("#jiaojie_count").html(total_count);
			$("#msg_audio").attr("src", baseURL+"statics/mp3/" + total_count + ".wav");
            $("#msg_audio").get(0).play();
		}
	});
	

}

$("#btnquerydispatchingJiaojie").click(function () {
	queryList();
});

$("#dispatchingNo").keydown(function(e){
	if(e.keyCode===13){
		
		if(!mapnew.hasOwnProperty($("#dispatchingNo").val())){
			mapnew[$("#dispatchingNo").val()]=$("#dispatchingNo").val();
			queryList();
		}else{//重复输入单据号
			$("#msg_audio").attr("src", baseURL+"statics/mp3/smcf.wav");
            $("#msg_audio").get(0).play();
            $(".btn").attr("disabled", false);
            $("#dispatchingNo").val("");
		}
		var pickRecordNo="";
		if(!maprecordNo.hasOwnProperty(pickRecordNo)){
			$.ajax({
				url:baseURL+"cswlms/dispatchingBillPicking/listPickRecordNoCount",
				dataType : "json",
				type : "post",
				data : {
					"BARCODE":$("#dispatchingNo").val()
				},
				async: true,
				success: function (response) { 
					
					if(response.code == "0"){
						var pickRecordNoMap = response.result;
						pickRecordNo=pickRecordNoMap.PICK_RECORD_NO;
						maprecordNo[pickRecordNo]=pickRecordNoMap.CNT;
						//alert(pickRecordNoMap.PICK_RECORD_NO);
						//alert(pickRecordNoMap.CNT);
					}else{
						js.alert(response.msg);
					}
					
				}
			});
		}
	}
});

function dispatchingHandover(user,username){
	var handover="";
	if(user!=""){// 非 无交接过账 要验证
		handover=user+":"+username;
	}
	
	$(".btn").attr("disabled", true);
	$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
	//从jqGrid获取选中的行数据
	var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
	
	if(ids.length==0){
		js.alert("请选择要交接的数据!");
		$(".btn").attr("disabled", false);
		return;
	}
	var rows = [];
	for(var id in ids){
		var row = $("#dataGrid").jqGrid('getRowData',ids[id]);
		rows[id] = row;
	}
	
	js.loading();
	
	$.ajax({
		url:baseUrl + "cswlms/dispatchingBillPicking/dispatchingHandover",
		dataType : "json",
		type : "post",
		data : {
			"ARRLIST":JSON.stringify(rows),
			"PZDDT":$("#pzddt").val(),
			"JZDDT":$("#jzddt").val(),
			"HANDOVER":handover
		},
		async: false,
		success:function(resp){
			if(resp.code == '0'){
				//queryList();
				js.closeLoading();
				$(".btn").attr("disabled", false);
				if(resp.jkinfo!=""){
					js.showErrorMessage("调用物流接口错误  "+resp.jkinfo,1000*10);
				}
				if(resp.retsap!=""){
					js.showMessage("交接成功!  "+resp.retsap,null,null,1000*600000);
				}
				$("#dataGrid").jqGrid("clearGridData", true);
				$("#jiaojie_count").html(0);
			}else{
				js.showErrorMessage(resp.msg,1000*10);
				js.closeLoading();
			}
		}
	});
	js.closeLoading();
}

function validateUser(user,username){
	var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
	
	if(ids.length==0){
		js.alert("请选择要提交的数据!");
		$(".btn").attr("disabled", false);
		return;
	}
	
	var business_rows=[];
	business_rows[0]="46";
	
	//验证交接人员信息
	if(user==""||username==""){
		js.alert("交接人员不能为空！");
		return ;
	}
	
	$.ajax({
  	  url:baseUrl + "config/handover/wmsCHandoverlist",
  	  data:{
  		  "WERKS":$("#werks").val(),
  		  "WH_NUMBER":"",
  		  "BUSINESS_CODE_LIST":JSON.stringify(business_rows),
  		  "STAFF_NUMBER":user
  		  },
  	  success:function(resp){
      	  if(resp.result.length>0){
      		dispatchingHandover(user,username);
       	  }else{
       		 js.alert("交接人员没有对应权限！");
			 return ;
       	  }
  	  }
    });
}








