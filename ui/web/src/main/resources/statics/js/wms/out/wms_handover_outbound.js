var listdata = [];
var lastrow,lastcell,lastcellname;
var last_scan_ele;
//var  inboundhandover="";
$(function(){
	$("#dataGrid").dataGrid({
    	datatype: "local",
		data: listdata,
        colModel: [			
			{ label: '需求行项目', name: 'REFERENCE_DELIVERY_ITEM', index: 'REFERENCE_DELIVERY_ITEM', sortable:false, width: 80, align: 'center',
				cellattr: function(rowId, tv, rawObject, cm, rdata) {
	                    //合并单元格
	                    return 'id=\'REFERENCE_DELIVERY_ITEM' + rowId + "\'";
	                }
			},
			{ label: '料号', name: 'MATNR', index: 'MATNR', width: 110, sortable:false,
				cellattr: function(rowId, tv, rawObject, cm, rdata) {
	                    //合并单元格
	                    return 'id=\'MATNR' + rowId + "\'";
	                }
			}, 
			{ label: '物料描述', name: 'MAKTX', index: 'MAKTX', width: 260, sortable:false,
				cellattr: function(rowId, tv, rawObject, cm, rdata) {
	                    //合并单元格
	                    return 'id=\'MAKTX' + rowId + "\'";
	                }
			}, 
			{ label: '需求数量', name: 'QTY', index: 'QTY', width: 80, sortable:false,
				cellattr: function(rowId, tv, rawObject, cm, rdata) {
	                    //合并单元格
	                    return 'id=\'QTY' + rowId + "\'";
	                }
			},
			{ label: '已发数量', name: 'QTY_REAL', index: 'QTY_REAL', width: 80, sortable:false,
				cellattr: function(rowId, tv, rawObject, cm, rdata) {
	                    //合并单元格
	                    return 'id=\'QTY_REAL' + rowId + "\'";
	                }
			},
			{ label: '<span style="color:blue">数量</span>', name: 'GZ_QTY', index: 'GZ_QTY', width: 80, sortable:false, editable:true,editrules:{number:true} },
			{ label: '单位', name: 'UNIT', index: 'UNIT', width: 60, sortable:false },
			{ label: '库位', name: 'LGORT', index: 'LGORT', width: 60, sortable:false }, 
			{ label: '批次', name: 'BATCH', index: 'BATCH', width: 90, sortable:false },
			{ label: '供应商代码', name: 'LIFNR', index: 'LIFNR', width: 100, sortable:false },
			{ label: '供应商名称', name: 'LIKTX', index: 'LIKTX', width: 160, sortable:false },
			{ label: '订单号', name: 'MO_NO', index: 'MO_NO', width: 80, sortable:false},
			{ label: '库存类型', name: 'SOBKZ', index: 'SOBKZ', width: 80, sortable:false },
			{ label: '接收工厂', name: 'RECEIVE_WERKS', index: 'RECEIVE_WERKS', width: 80, sortable:false },
			{ label: '接收库位', name: 'RECEIVE_LGORT', index: 'RECEIVE_LGORT', width: 80, sortable:false },
			{ label: '车间', name: 'RECEIVER', index: 'RECEIVER', width: 80, sortable:false },
			{ label: '工位', name: 'STATION', index: 'STATION', width: 80, sortable:false },
			{ label: '仓管员', name: 'WH_MANAGER', index: 'WH_MANAGER', width: 80, sortable:false },
			{ label: '<span style="color:blue">行文本</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'ITEM_TEXT\')" title="向下填充" style="color:green" aria-hidden="true"></i>', 
   				name: 'ITEM_TEXT',index: 'ITEM_TEXT', width: 260,editable:true,sortable:false,},
			{ label: '', name: 'INDX', index: 'INDX', width: 160,hidden:true },
			{ label: '', name: 'ID', index: 'ID', width: 160,hidden:true },
			{ label: '', name: 'CONFIRM_QUANTITY', index: 'CONFIRM_QUANTITY', width: 160,hidden:true },
			{ label: '', name: 'GZ_QTY_HIDDEN', index: 'GZ_QTY_HIDDEN', width: 160,hidden:true },
			{ label: '', name: 'WERKS', index: 'WERKS', width: 160,hidden:true },
			{ label: '', name: 'WH_NUMBER', index: 'WH_NUMBER', width: 160,hidden:true },
			{ label: '', name: 'TASK_NUM', index: 'TASK_NUM', width: 160,hidden:true },
			{ label: '', name: 'FROM_BIN_CODE', index: 'FROM_BIN_CODE', width: 160,hidden:true },
			{ label: '', name: 'REFERENCE_DELIVERY_NO', index: 'REFERENCE_DELIVERY_NO', width: 160,hidden:true },
			{ label: '', name: 'BUSINESS_TYPE', index: 'BUSINESS_TYPE', width: 160,hidden:true },
			{ label: '', name: 'BUSINESS_NAME', index: 'BUSINESS_NAME', width: 160,hidden:true },
			{ label: '', name: 'MO_NO', index: 'MO_NO', width: 160,hidden:true },
			{ label: '', name: 'MO_ITEM_NO', index: 'MO_ITEM_NO', width: 160,hidden:true },
			{ label: '', name: 'SO_NO', index: 'SO_NO', width: 160,hidden:true },
			{ label: '', name: 'SO_ITEM_NO', index: 'SO_ITEM_NO', width: 160,hidden:true },
			{ label: '', name: 'PO_NO', index: 'PO_NO', width: 160,hidden:true },
			{ label: '', name: 'PO_ITEM_NO', index: 'PO_ITEM_NO', width: 160,hidden:true },
			{ label: '', name: 'SAP_OUT_NO', index: 'SAP_OUT_NO', width: 160,hidden:true },
			{ label: '', name: 'SAP_OUT_ITEM_NO', index: 'SAP_OUT_ITEM_NO', width: 160,hidden:true },
			{ label: '', name: 'COST_CENTER', index: 'COST_CENTER', width: 160,hidden:true },
			{ label: '', name: 'IO_NO', index: 'IO_NO', width: 160,hidden:true },
			{ label: '', name: 'WBS', index: 'WBS', width: 160,hidden:true },
			{ label: '', name: 'CUSTOMER', index: 'CUSTOMER', width: 160,hidden:true },
			{ label: '', name: 'SAP_MATDOC_NO', index: 'SAP_MATDOC_NO', width: 160,hidden:true },
			{ label: '', name: 'SAP_MATDOC_ITEM_NO', index: 'SAP_MATDOC_ITEM_NO', width: 160,hidden:true },
			{ label: '', name: 'TXTRULE', index: 'TXTRULE', width: 160,hidden:true },
			{ label: '', name: 'RSNUM', index: 'RSNUM', hidden:true },
			{ label: '', name: 'RSPOS', index: 'RSPOS', hidden:true },
			{ label: '', name: 'AUTYP', index: 'AUTYP', hidden:true }
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
        	Merger(gridName, 'REFERENCE_DELIVERY_ITEM');
	        Merger(gridName, 'MATNR');
	        Merger(gridName, 'MAKTX');
	        Merger(gridName, 'QTY','MATNR');
	        Merger(gridName, 'QTY_REAL','MATNR');
	            
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
		afterSaveCell:function(rowid, cellname, value, iRow, iCol){
	        	
			var rec =  $("#dataGrid").jqGrid('getRowData', rowid);

	        if(cellname == 'GZ_QTY'){
				if (Number(rec.GZ_QTY) <= 0) {
	  				js.showErrorMessage("数量不能为0");
	  				return;
	  			}
	  			if (Number(rec.GZ_QTY) > Number(rec.GZ_QTY_HIDDEN)) {
	  				js.showErrorMessage("数量不能超过可拣配数量");
	  				$("#dataGrid").jqGrid('setCell', rowid, cellname, null,'editable-cell');
	  				return;
	  			}
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
		relatedareaname:[],//仓管员
		PZDDT:getCurDate(),
		JZDDT:getCurDate(),
		inbound_type:"",
		lgortList:''
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
	  		 "MENU_KEY":"A45"
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
			$(".btn").attr("disabled", true);

			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
			
			if(ids.length==0){
				js.alert("请选择要交接的数据!");
				$(".btn").attr("disabled", false);
				return;
			}
			
			for(var i=0;i<ids.length;i++){
				var rowData = $("#dataGrid").jqGrid('getRowData',ids[i]);
				if (Number(rowData.GZ_QTY) <= 0) {
	  				js.showErrorMessage("数量不能为0");
	  				$(".btn").attr("disabled", false);
	  				return;
	  			}
	  			if (Number(rowData.GZ_QTY) > Number(rowData.GZ_QTY_HIDDEN)) {
	  				js.showErrorMessage("数量不能超过可拣配数量");
	  				$(".btn").attr("disabled", false);
	  				return;
	  			}
	  		}
			
			//验证交接模式
			var business_rows=[];
			var rows = [];
			for(var id in ids){
				var row = $("#dataGrid").jqGrid('getRowData',ids[id]);
				
				rows[id] = row;
				if(business_rows.indexOf(row.BUSINESS_NAME) < 0){
					business_rows[id]=row.BUSINESS_NAME;
				}
				
			}
			
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
	        			  $(".btn").attr("disabled", false);
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
			         		 $(".btn").attr("disabled", false);
							 return false;
			         	  }
	        		  }
	        	  }
        		  }
	          })
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
	        	data:{"WERKS":plantCode,"MENU_KEY":"A45"},
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
		
		initDataGrid:function(){},
		openLgortSelectWindow : function(call_back) {
			// 库位选择框
			js.layer.open({
				type : 2,
				title : "库位",
				area : [ "40%", "60%" ],
				content : baseUrl + "wms/out/stock_mutiselect.html",
				btn : [ "确定", "取消" ],
				success : function(layero, index_) {
					// 加载完成后，初始化
					//var werks = vm.werks;
					var werks = $("#werks").val();
					//var whNumber = vm.whNumber;
					var whNumber = $("#whNumber").val();
					var win = layero.find('iframe')[0].contentWindow;
					win.vm.init(werks, whNumber);

				},
				yes : function(index_, layero) {
					// 提交，调用生产订单页面的提交方法。
					var win = layero.find('iframe')[0].contentWindow;
					var lgortList = win.vm.getSelectedItems();
					var lgortItems = lgortList
						.map(function(val) {
							return val.LGORT;
						});
					var lgortList = lgortItems.join(",");
					if(lgortList.length > 0 && lgortList.charAt(lgortList.length-1) === ','){
						lgortList = lgortList.substring(0,lgortList.length-1);
					}
					if(call_back != null){
						call_back(lgortList);
					}
					else{
						//没有回调
						vm.lgortList = lgortList;
						//vm.lgortLd();
					}
					win.vm.close();
				},
				no : function(index, layero) {
				}
			});
		},
	}
});

function queryList(){
	$(".btn").attr("disabled", true);
	js.loading();
	$.ajax({
		url:baseURL+"out/handover/list",
		dataType : "json",
		type : "post",
		data : {
			"WERKS":$("#werks").val(),
			"WH_NUMBER":$("#whNumber").val(),
			"WH_MANAGER":$("#whManager").val(),
			"REQUIREMENT_NO":$("#outBoundNo").val(),
			"STATION":$("#station").val(),
			"LGORT":$("#lgortList").val(),
			"JZ_DATE":$("#jzddt").val(),
		},
		async: true,
		success: function (response) { 
			
			if(response.code == "0"){
				if(response.page.list.length === 0){
					layer.msg("单号无可交接数据！",{time:3000});
					$("#werks").attr("disabled",false);
					$("#whNumber").attr("disabled",false);
				}
				listdata = listdata.concat(response.page.list);
				$("#dataGrid").jqGrid('clearGridData');
				$("#dataGrid").jqGrid('setGridParam',{ // 重新加载数据 
					datatype:'local', data : response.page.list, // newdata 是符合格式要求的需要重新加载的数据
			    }).trigger("reloadGrid");
				$(".btn").attr("disabled", false);
				js.closeLoading();
			}else{
				alert(response.msg)
				$(".btn").attr("disabled", false);
				$("#werks").attr("disabled",false);
				$("#whNumber").attr("disabled",false);
				js.closeLoading();
			}
		}
	});
	
}

$("#btnqueryhandoverbound").click(function () {
	if($("#outBoundNo").val()!=''){//如果单号不为空
		
		queryList();
		$("#werks").attr("disabled","disabled");//设为不可用
		$("#whNumber").attr("disabled","disabled");//设为不可用
	}else{
		js.alert("单号不能为空！");
		return false;
	}
	
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
		
		if(business_rows.indexOf(row.BUSINESS_NAME) < 0){
			business_rows[id]=row.BUSINESS_NAME;
		}
		
	}
	
	//验证交接人员信息
	if(user==""||username==""){
		js.alert("交接人员不能为空！");
		$(".btn").attr("disabled", false);
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
       		 $(".btn").attr("disabled", false);
			 return ;
       	  }
  	  }
    });
	
	$(".btn").attr("disabled", false);
}

function handoverSave(user,username){
	
	var handover="";
	// 需交接过账的记录交接人员信息
	if(user!=""){
		handover=user+":"+username;
		
	}
	
	$(".btn").attr("disabled", true);
	$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
	//从jqGrid获取选中的行数据
	var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
	
	if(ids.length==0){
		js.alert("请选择要提交的数据!");
		$(".btn").attr("disabled", false);
		return;
	}
	var rows = [];
	for(var i=0;i<ids.length;i++){
		var rowData = $("#dataGrid").jqGrid('getRowData',ids[i]);
		rows.push(rowData);	
	}
	
	$("#btnsaveinteralbound").html("交接中...");	
	$("#btnsaveinteralbound").attr("disabled","disabled");
	js.loading();
	
	$.ajax({
		url:baseUrl + "out/handover/save",
		dataType : "json",
		type : "post",
		data : {
			"WERKS":$("#werks").val(),
			"WH_NUMBER":$("#whNumber").val(),
			"PZDDT":$("#pzddt").val(),
			"JZDDT":$("#jzddt").val(),
			"HANDOVER":handover,
			"REQUIREMENT_NO":$("#outBoundNo").val(),
			"ARRLIST":JSON.stringify(rows)
		},
		async: true,
		success:function(resp){
			if(resp.code == '0'){
//				js.showMessage("交接成功!  "+resp.msg,null,null,1000*600000);
				$("#outreqNoLayer").html(resp.msg);
				layer.open({
					type : 1,
					offset : '50px',
					skin : 'layui-layer-molv',
					title : "交接成功",
					area : [ '500px', '260px' ],
					shade : 0,
					shadeClose : false,
					content : jQuery("#resultLayer"),
					btn : [ '确定'],
					btn1 : function(index) {
						layer.close(index);
					}
				});
				
				//列表 单号缓存都置位空
				$("#dataGrid").jqGrid("clearGridData", true);
//				inboundhandover="";
				$("#dhhidden").val("");
				listdata.length=0;
				//
				$(".btn").attr("disabled", false);
				$("#btnConfirm").html("交接确认");
				js.closeLoading();
			}else{
//				js.alert(resp.msg);
				js.showErrorMessage("交接失败!  "+resp.msg,null,null,1000*600000);
				$(".btn").attr("disabled", false);
				$("#werks").attr("disabled",false);
				$("#whNumber").attr("disabled",false);
				$("#btnConfirm").html("交接确认");
				js.closeLoading();
			}
		}
	});
	$("#btnConfirm").html("交接确认");
	js.closeLoading();
}

function fillDown(e){
	$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
	var ids=$("#dataGrid").getGridParam("selarrrow");
	var rowid=lastrow;
	var rows=$("#dataGrid").jqGrid('getRowData');
/*	if(ids!=null){
		rowid=Math.min.apply(null,ids);
	}*/
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


function funFromjs(result) {
	result = result.replace("{","").replace("}","")
	var outbound_no = result.split(",")[0].split(":")[1];
	
	var e = $.Event('keydown');
	e.keyCode = 13;
	$(last_scan_ele).val(outbound_no).trigger(e);
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

