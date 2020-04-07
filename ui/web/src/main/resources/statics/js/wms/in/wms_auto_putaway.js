var listdata = [];
var lastrow,lastcell,lastcellname;
$(function(){
	$("#dataGrid").dataGrid({
    	datatype: "local",
		data: listdata,
        colModel: [			
			{ label: '行项目', name: 'WMS_ITEM_NO', index: 'WMS_ITEM_NO', sortable:false, width: 80, align: 'center',
				cellattr: function(rowId, tv, rawObject, cm, rdata) {
	                    //合并单元格
	                    return 'id=\'WMS_ITEM_NO' + rowId + "\'";
	                }
			},
			{ label: '料号', name: 'MATNR', index: 'MATNR', width: 90, sortable:false,
				cellattr: function(rowId, tv, rawObject, cm, rdata) {
	                    //合并单元格
	                    return 'id=\'MATNR' + rowId + "\'";
	                }
			}, 
			{ label: '物料描述', name: 'MAKTX', index: 'MAKTX', width: 220, sortable:false,
				cellattr: function(rowId, tv, rawObject, cm, rdata) {
	                    //合并单元格
	                    return 'id=\'MAKTX' + rowId + "\'";
	                }
			}, 
			{ label: '发货工厂', name: 'FROM_WERKS', index: 'FROM_WERKS', width: 80, align: 'center', sortable:false }, 
			{ label: '发货库位', name: 'FROM_LGORT', index: 'FROM_LGORT', width: 80, align: 'center', sortable:false }, 
			{ label: '数量', name: 'QTY_WMS', index: 'QTY_WMS', width: 80, sortable:false, align: 'center'},
			{ label: '已收数量', name: 'RECEIVED_QTY', index: 'RECEIVED_QTY', width: 80, sortable:false, align: 'center'},
			{ label: '<span style="color:red">*</span><span style="color:blue">收货数量</span>', name: 'RECEIVE_QTY', index: 'RECEIVE_QTY', width: 80, sortable:false, align: 'center', editable:true,editrules:{number:true} },
			{ label: '单位', name: 'UNIT', index: 'UNIT', width: 60, align:'center', sortable:false },
			{ label: '批次', name: 'BATCH', index: 'BATCH', width: 95, align:'center', sortable:false },
			{display:'满箱数量',  label: '<span style="color:red">*</span><span style="color:blue">满箱数量</span>', name: 'FULL_BOX_QTY', align:"center",index: 'FULL_BOX_QTY', width: 80 ,editable:true,editrules:{
	   				required:true,number:true}, sortable:false, formatter:'number',formatoptions:{
	   	   				decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}},
			{ label: '<span style="color:red">*</span><span style="color:blue">生产日期</span>', name: 'PRODUCT_DATE', align:"center",sortable:false,index: 'PRODUCT_DATE', width: 90,editable:true,editoptions:{
                dataInit:function(e){
                    $(e).click(function(e){
                    	WdatePicker({dateFmt:'yyyy-MM-dd'})
                    });
                }
            } },
            { label: '<span style="color:blue">收料房存放区</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'BIN_CODE\')" title="向下填充" style="color:green" aria-hidden="true"></i>', 
            	name: 'BIN_CODE', align:"center",index: 'BIN_CODE', width: 110,sortable:false,editable:true,edittype:'select'},
            { label: '<span style="color:blue">收料员</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'RECEIVER\')" title="向下填充" style="color:green" aria-hidden="true"></i>', 
       			name: 'RECEIVER', align:"center",index: 'RECEIVER', width: 80,editable:true, sortable:false,  },
			{ label: '<span style="color:blue">行文本</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'ITEM_TEXT\')" title="向下填充" style="color:green" aria-hidden="true"></i>', 
   				name: 'ITEM_TEXT',index: 'ITEM_TEXT', width: 260,editable:true,sortable:false,},
			{ label: '', name: 'INDX', index: 'INDX', width: 160,hidden:true }
        ],
		viewrecords: true,
		showRownum:false,
        cellEdit:true,
        cellurl:'#',
        cellsubmit:'clientArray',
        autowidth:true,
        shrinkToFit:false,  
        autoScroll: true, 
        multiselect: true,
        
        gridComplete:function(){
        	var gridName = "dataGrid";
        	Merger(gridName, 'WMS_ITEM_NO');
	        Merger(gridName, 'MATNR');
	        Merger(gridName, 'MAKTX');
	            
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
        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
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
			
		formatCell:function(rowid, cellname, value, iRow, iCol){
			var rec = $('#dataGrid').jqGrid('getRowData', rowid);	
			if(cellname=='BIN_CODE'){
		           $('#dataGrid').jqGrid('setColProp', 'BIN_CODE', { editoptions: {value:getGrAreaList($("#werks").val())}
		           });
			}	
		}
    });

});
			
var vm = new Vue({
	el:"#vue",
	data:{
		businessList:[],
		warehourse:[],
		whNumber:"",//
		PZDDT:getCurDate(),
		JZDDT:getCurDate(),
		inbound_type:""
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
      	  data:{
    		  "WERKS":plantCode,
    		  "MENU_KEY":"AUTO_PUTAWAY"
    	  },
      	  success:function(resp){
      		 vm.warehourse = resp.data;
      		if(resp.data.length>0){
    			 vm.whNumber=resp.data[0].WH_NUMBER//方便 v-mode取值
    		 }
      		
      	  }
        });
	},
	methods:{
		query:function(){
			$("#searchForm").submit();
		},
		save:function(){
			var timestr;
			var ebeln;
			var poFlag = false;
			var dnFlag = false;
			var vbeln;
			var createDNFlag = false;
			var postDNFlag = false;
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
			if(ids.length==0){
				js.alert("请选择要交接的数据!");
				return;
			}
			
			var rows = [];
			for(var i=0;i<ids.length;i++){
				var rowData = $("#dataGrid").jqGrid('getRowData',ids[i]);
				if (Number(rowData.RECEIVE_QTY) <= 0) {
	  				js.showErrorMessage("收货数量不能为0");
	  				return;
	  			}
	  			if (Number(rowData.RECEIVE_QTY) > Number(rowData.QTY_WMS) - Number(rowData.RECEIVED_QTY)) {
	  				js.showErrorMessage("收货数量不能超过可收货货数量");
	  				return;
	  			}
	  			if (Number(rowData.FULL_BOX_QTY) <= 0) {
	  				js.showErrorMessage("满箱数量不能为空");
	  				return;
	  			}
	  			if (Number(rowData.FULL_BOX_QTY) > Number(rowData.RECEIVE_QTY)) {
	  				js.showErrorMessage("满箱数量不能超过收货数量");
	  				return;
	  			}
	  			if (isEmpty(rowData.PRODUCT_DATE)) {
	  				js.showErrorMessage("生产日期不能为空");
	  				return;
	  			}
	  			rows.push(rowData);	
	  		}

			$(".btn").attr("disabled", true);
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			
			
			$("#btnsaveinteralbound").html("收货中...");	
			$("#btnsaveinteralbound").attr("disabled","disabled");
			js.loading();
			
			
			$.ajax({
				url:baseUrl + "in/autoPutaway/createPO",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":$("#werks").val(),
					"WH_NUMBER":$("#whNumber").val(),
					"MAT_DOC":$("#matDoc").val(),
//					"LGORT":$("#lgort").val(),
					"FROMWERKS":$("#fromWerks").val(),
					"PZDDT":$("#pzddt").val(),
					"JZDDT":$("#jzddt").val(),
					"ITEMLIST":JSON.stringify(rows)
				},
				async: true,
				success:function(resp){
					if(resp.code == '0'){
						var rmsg;
						poFlag = true;
						timestr = resp.timestr;
						ebeln = resp.msg;
//						js.closeLoading();
						$("#dataGrid").jqGrid('clearGridData');
						if (!isEmpty(resp.postr)){
//							js.showMessage("成功! 订单号： "+resp.postr,null,null,1000*600000);
							rmsg = "成功! 订单号： "+resp.postr;
							document.getElementById("createpo").style.background="GreenYellow";
							document.getElementById("successpo").style.display="";
						}
						if (!isEmpty(resp.dnstr)){
//							js.showMessage("成功! 交货单 ："+resp.dnstr,null,null,1000*600000);
							rmsg = rmsg + "<br/>成功! 交货单： "+resp.dnstr;
							document.getElementById("createdn").style.background="GreenYellow";
							document.getElementById("successdn").style.display="";
						} else {
							rmsg = rmsg + "<br/>创建交货单失败!";
							document.getElementById("createdn").style.background="Red";
							document.getElementById("faildn").style.display="";
						}
						
						if (!isEmpty(resp.wmsnostr)){
//							js.showMessage("成功!  WMS凭证："+resp.wmsnostr,null,null,1000*600000);
							rmsg = rmsg + "<br/>成功! WMS凭证： "+resp.wmsnostr;
							document.getElementById("postdn").style.background="GreenYellow";
							document.getElementById("successpost").style.display="";
							
							$("#labelList").val(JSON.stringify(resp.lableList));
				        	$("#labelListA4").val(JSON.stringify(resp.lableList));
				        	$("#inspectionList").val(JSON.stringify(resp.inspectionList))
						} else {
							rmsg = rmsg + "<br/>交货单过账失败!";
							document.getElementById("postdn").style.background="Red";
							document.getElementById("failpost").style.display="";
						}
						
						if (!isEmpty(resp.sapdoc)){
							rmsg = rmsg + "<br/>成功! SAP凭证： "+resp.sapdoc;
						} 
						
						$("#dataGrid").jqGrid('setGridParam',{datatype:'local'}).trigger('reloadGrid'); 
						$("#resultMsg").html(rmsg);
						layer.open({
							type : 1,
							offset : '50px',
							skin : 'layui-layer-molv',
							title : "成功",
							area : [ '500px', '300px' ],
							shade : 0,
							shadeClose : false,
							content : jQuery("#resultLayer"),
							btn : [ '确定'],
							btn1 : function(index) {
								layer.close(index);
							}
						});
						
						js.closeLoading();
					}else{
						js.alert(resp.msg);
						$("#dataGrid").jqGrid('clearGridData');
						js.showErrorMessage("失败!  "+resp.msg,null,null,1000*600000);
						$(".btn").attr("disabled", false);
						js.closeLoading();
					}
				}
			});
			
//			if (poFlag) {
//				$.ajax({
//					url:baseUrl + "in/autoPutaway/createDN",
//					dataType : "json",
//					type : "post",
//					data : {
//						"WERKS":$("#werks").val(),
//						"WH_NUMBER":$("#whNumber").val(),
//						"TIME_STAMP_STR":timestr,
//						"EBELN":ebeln
//					},
//					async: false,
//					success:function(resp){
//						if(resp.code == '0'){
//							js.showMessage("成功! 交货单 ："+resp.msg,null,null,1000*600000);
//							dnFlag = true;
//							vbeln = resp.msg;
//							document.getElementById("createdn").style.background="GreenYellow";
//							document.getElementById("successdn").style.display="";
//							js.closeLoading();
//						}else{
//							js.alert(resp.msg);
//							js.showErrorMessage("失败!  "+resp.msg,null,null,1000*600000);
//							$(".btn").attr("disabled", false);
//							js.closeLoading();
//						}
//					}
//				});
//			}
//			
//			if (dnFlag) {
//				$.ajax({
//					url:baseUrl + "in/autoPutaway/postDN",
//					dataType : "json",
//					type : "post",
//					data : {
//						"WERKS":$("#werks").val(),
//						"WH_NUMBER":$("#whNumber").val(),
//						"TIME_STAMP_STR":timestr,
//					},
//					async: false,
//					success:function(resp){
//						if(resp.code == '0'){
//							js.showMessage("成功!  WMS凭证："+resp.msg,null,null,1000*600000);
//							document.getElementById("postdn").style.background="GreenYellow";
//							document.getElementById("successpost").style.display="";
//							js.closeLoading();
//						}else{
//							js.alert(resp.msg);
//							js.showErrorMessage("失败!  "+resp.msg,null,null,1000*600000);
//							$(".btn").attr("disabled", false);
//							js.closeLoading();
//						}
//					}
//				});
//			}
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
	        	  data:{"WERKS":plantCode,"MENU_KEY":"AUTO_PUTAWAY"},
	        	  success:function(resp){
	        		 vm.warehourse = resp.data;
	        		 if(resp.data.length>0){
	         			 vm.whNumber=resp.data[0].WH_NUMBER//方便 v-mode取值
	         		 }
	        		 
	        	  }
	          })
		},
		
		getVendorNoFuzzy:function(){
			getVendorNoSelect("#lifnr",null,null,$("werks").val());
		},
		
		initDataGrid:function(){}
	}
});

function queryList(){
	$(".btn").attr("disabled", true);
	$.ajax({
		url:baseURL+"in/autoPutaway/list",
		dataType : "json",
		type : "post",
		data : {
			"WERKS":$("#werks").val(),
			"WH_NUMBER":$("#whNumber").val(),
			"MAT_DOC":$("#matDoc").val(),
			"DOC_YEAR":$("#docYear").val(),
//			"LGORT":$("#lgort").val(),
			"JZDDT":$("#jzddt").val(),
			"FROMWERKS":$("#fromWerks").val()
		},
		async: true,
		success: function (response) { 
			if(response.code == "0"){
				listdata = response.list;
				$.each(listdata,function(i){
					listdata[i].PRODUCT_DATE = getCurDate();
				})
				$("#dataGrid").jqGrid('clearGridData');
				$("#dataGrid").jqGrid('setGridParam',{ // 重新加载数据 
					datatype:'local', data : listdata, // newdata 是符合格式要求的需要重新加载的数据
			    }).trigger("reloadGrid");
				$(".btn").attr("disabled", false);
			}else{
				listdata = [];
				$("#dataGrid").jqGrid('clearGridData');
				js.alert(response.msg);
				$(".btn").attr("disabled", false);
				$("#werks").attr("disabled",false);//设为不可用
				$("#whNumber").attr("disabled",false);//设为不可用
			}
		}
	});
}

$("#btnqueryhandoverbound").click(function () {

	if($("#matDoc").val() ===''){//如果单号不为空
		js.alert("单号不能为空！");
		return false;
	}
	
//	if($("#lgort").val() ===''){
//		js.alert("接收库位不能为空！");
//		return false;
//	}
	
	if($("#fromWerks").val() ===''){
		js.alert("发货工厂不能为空！");
		return false;
	}
	
	$("#werks").attr("disabled","disabled");//设为不可用
	$("#whNumber").attr("disabled","disabled");//设为不可用
	queryList();
});

function fillDown(e){
	$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
	var ids=$("#dataGrid").getGridParam("selarrrow");
	var rowid=lastrow;
	var rows=$("#dataGrid").jqGrid('getRowData');

	var last_row=$("#dataGrid").jqGrid('getRowData',rowid);
	var cellvalue=last_row[lastcellname];
	
	if(lastcellname==e){
		$.each(rows,function(i,row){
			console.info(Number(row.INDX)+"/"+rowid+"/"+cellvalue)
			if(Number(row.INDX)>=Number(rowid)&&cellvalue){
				$("#dataGrid").jqGrid('setCell',Number(row.INDX), e, cellvalue,'editable-cell');
			}
		})
	}
}

//公共调用方法
function Merger(gridName, CellName,referCellName) {
    //得到显示到界面的id集合
    var mya = $("#" + gridName + "").getDataIDs();
    //当前显示多少条
    var length = mya.length;
    for (var i = 0; i < length; i++) {
        //从上到下获取一条信息
        var before = $("#" + gridName + "").jqGrid('getRowData', mya[i]);
        //定义合并行数
        var rowSpanTaxCount = 1;
        for (j = i + 1; j <= length; j++) {
            //和上边的信息对比 如果值一样就合并行数+1 然后设置rowspan 让当前单元格隐藏
            var end = $("#" + gridName + "").jqGrid('getRowData', mya[j]);
            if(referCellName=='' || referCellName==undefined){
	            if (before[CellName] == end[CellName]) {
	                rowSpanTaxCount++;
	                $("#" + gridName + "").setCell(mya[j], CellName, '', { display: 'none' });
	            } else {
	                rowSpanTaxCount = 1;
	                break;
	            }
            }else{
            	if (before[referCellName] == end[referCellName]) {
	                rowSpanTaxCount++;
	                $("#" + gridName + "").setCell(mya[j], CellName, '', { display: 'none' });
	            } else {
	                rowSpanTaxCount = 1;
	                break;
	            }
            }
            $("#" + CellName + "" + mya[i] + "").attr("rowspan", rowSpanTaxCount);
        }
    }
}

function getGrAreaList(WERKS){
	var options="";
	$.ajax({
		url:baseUrl+'common/getGrAreaList',
		type:'post',
		async:false,
		dataType:'json',
		data:{
			WERKS:WERKS,
		},
		success:function(resp){
			//alert(resp.msg)
			if(resp.msg=='success'){		
				for(i=0;i<resp.data.length;i++){
	 				if(i != resp.data.length - 1) {
	 					options += resp.data[i].AREA_CODE + ":" +resp.data[i].AREA_CODE + ";";
	 				} else {
	 					options += resp.data[i].AREA_CODE + ":" + resp.data[i].AREA_CODE;
	 				}
				}
			}
		}		
	})
	return options;
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