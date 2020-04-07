var listdata = [];
var lastrow,lastcell; 
var  inboundhandover="";
var rows_ww=[];
var last_scan_ele;
$(function(){
	$("#dataGrid").dataGrid({
    	datatype: "local",
		data: listdata,
        colModel: [	
            { label: '序号', name: 'ID', index: 'ID', align:"left",width: 60, key: true ,sortable:false,hidden:true},       
			{ label: '单号', name: 'INBOUND_NO', index: 'INBOUND_NO', width: 120 }, 
			{ label: '行项目', name: 'INBOUND_ITEM_NO', index: 'INBOUND_ITEM_NO', width: 60 },
			{ label: '批次', name: 'BATCH', index: 'BATCH', width: 90 },
			{ label: '物料号', name: 'MATNR', index: 'MATNR', width: 90 }, 
			{ label: '物料描述', name: 'MAKTX', index: 'MAKTX', width: 220 }, 
			{ label: '需求跟踪号', name: 'BEDNR', index: 'BEDNR', width: 90 }, 
			{ label: '<span style="color:blue">库位</span>', name: 'LGORT', index: 'LGORT', width: 60,editable:true,edittype:'select'}, 
			{ label: '<span style="color:blue">储位</span>', name: 'BIN_CODE', index: 'BIN_CODE', width: 80 },
			{ label: '单据数量', name: 'IN_QTY', index: 'IN_QTY', width: 80 },
			{ label: '已进仓数量', name: 'REAL_QTY', index: 'REAL_QTY', width: 80},
			{ label: '<span style="color:blue">可进仓数量</span>', name: 'MAY_IN_QTY', index: 'MAY_IN_QTY', width: 80,editable:true },
			{ label: '单位', name: 'UNIT', index: 'UNIT', width: 60 },
			{ label: '库存类型', name: 'SOBKZ', index: 'SOBKZ', width: 80 },
			{ label: '供应商代码', name: 'LIFNR', index: 'LIFNR', width: 100 },
			{ label: '供应商名称', name: 'LIKTX', index: 'LIKTX', width: 160 },
			{ label: '仓管员', name: 'WH_MANAGER', index: 'WH_MANAGER', width: 80 },
			{ label: '<span style="color:blue">行文本</span>', name: 'ITEM_TEXT', index: 'ITEM_TEXT', width: 200,editable:true },
			{ label: '', name: 'urgentFlag', index: 'urgentFlag', width: 160,hidden:true },
			{ label: '', name: 'WERKS', index: 'WERKS', width: 160,hidden:true },
			{ label: '', name: 'WH_NUMBER', index: 'WH_NUMBER', width: 160,hidden:true },
			{ label: '', name: 'RECEIPT_NO', index: 'RECEIPT_NO', width: 160,hidden:true },
			{ label: '', name: 'RECEIPT_ITEM_NO', index: 'RECEIPT_ITEM_NO', width: 160,hidden:true },
			{ label: '', name: 'MO_NO', index: 'MO_NO', width: 160,hidden:true },
			{ label: '', name: 'MO_ITEM_NO', index: 'MO_ITEM_NO', width: 160,hidden:true },
			{ label: '', name: 'IO_NO', index: 'IO_NO', width: 160,hidden:true },
			{ label: '', name: 'COST_CENTER', index: 'COST_CENTER', width: 160,hidden:true },
			{ label: '', name: 'SAKTO', index: 'SAKTO', width: 160,hidden:true },
			{ label: '', name: 'ANLN1', index: 'ANLN1', width: 160,hidden:true },
			{ label: '', name: 'WBS', index: 'WBS', width: 160,hidden:true },
			{ label: '', name: 'F_BATCH', index: 'F_BATCH', width: 160,hidden:true },
			{ label: '', name: 'BIN_NAME', index: 'BIN_NAME', width: 160,hidden:true },
			{ label: '', name: 'TXTRULE', index: 'TXTRULE', width: 160,hidden:true },
			{ label: '', name: 'BUSINESS_NAME', index: 'BUSINESS_NAME', width: 160,hidden:true },
			{ label: '', name: 'BUSINESS_TYPE', index: 'BUSINESS_TYPE', width: 160,hidden:true },
			{ label: '', name: 'LABEL_FALG', index: 'LABEL_FALG', width: 160,hidden:true },
			{ label: '', name: 'REF_SAP_MATDOC_YEAR', index: 'REF_SAP_MATDOC_YEAR', width: 160,hidden:true },
			{ label: '', name: 'REF_SAP_MATDOC_NO', index: 'REF_SAP_MATDOC_NO', width: 160,hidden:true },
			{ label: '', name: 'REF_SAP_MATDOC_ITEM_NO', index: 'REF_SAP_MATDOC_ITEM_NO', width: 160,hidden:true },
			{ label: '', name: 'LABEL_NO', index: 'LABEL_NO', width: 160,hidden:true },
			{ label: '', name: 'BIN_CODE_SHELF', index: 'BIN_CODE_SHELF', width: 160,hidden:true },
			{ label: '', name: 'AUTO_PUTAWAY_FLAG', index: 'AUTO_PUTAWAY_FLAG', width: 160,hidden:true },
			{ label: '', name: 'LGORT_311', index: 'LGORT_311', width: 60,hidden:true },
			{ label: '', name: 'WERKS_311', index: 'WERKS_311', width: 60,hidden:true },
			{ label: '', name: 'WH_NUMBER_311', index: 'WH_NUMBER_311', width: 60,hidden:true },
			{ label: '', name: 'F_LGORT', index: 'F_LGORT', width: 60,hidden:true },
			{ label: '', name: 'F_WERKS', index: 'F_WERKS', width: 60,hidden:true },
			{ label: '', name: 'F_WH_NUMBER', index: 'F_WH_NUMBER', width: 60,hidden:true },
			{ label: '', name: 'DESTROY_QTY', index: 'DESTROY_QTY', width: 60,hidden:true },
			{ label: '', name: 'DESTROY_GZ_QTY', index: 'DESTROY_GZ_QTY', width: 60,hidden:true },
			{ label: '', name: 'IQC_COST_CENTER', index: 'IQC_COST_CENTER', width: 60,hidden:true },
			{ label: '', name: 'PSTYP', index: 'PSTYP', width: 80,hidden:true},
			{ label: '', name: 'ITEM_STATUS', index: 'ITEM_STATUS', width: 80,hidden:true},
			{ label: '', name: 'PO_NO', index: 'PO_NO', width: 80,hidden:true},
			{ label: '', name: 'PO_ITEM_NO', index: 'PO_ITEM_NO', width: 80,hidden:true},
			{ label: '', name: 'UMREZ', index: 'UMREZ', width: 80,hidden:true},
			{ label: '', name: 'UMREN', index: 'UMREN', width: 80,hidden:true},
			{ label: '', name: 'SO_NO', index: 'SO_NO', width: 80,hidden:true},
			{ label: '', name: 'SO_ITEM_NO', index: 'SO_ITEM_NO', width: 80,hidden:true},
			
			{ label: '', name: 'ASNNO', index: 'ASNNO', width: 80,hidden:true},
			{ label: '', name: 'ASNITM', index: 'ASNITM', width: 80,hidden:true},
			{ label: '', name: 'F_B_BIN_CODE', index: 'F_B_BIN_CODE', width: 80,hidden:true},
			
			{ label: '', name: '', index: '', width: 80,align:'center' ,formatter: function(val, obj, row, act){
				var actions = [];
				if(row.PSTYP == "3")actions.push('<a href="#" title="委外原料消耗" onclick=show("'+row.INBOUND_NO+'","'+row.INBOUND_ITEM_NO+'","'+row.RECEIPT_NO+'","'+row.RECEIPT_ITEM_NO+'")>委外原料消耗</a>');
				return actions.join('');
		      }
		    }
			
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
        loadComplete:function(data){
	    	if(data.code==500){
				js.showErrorMessage(data.msg,1000*10);
				js.closeLoading();
				$(".btn").attr("disabled", false);
				return false;
			}
		   var rows=$("#dataGrid").jqGrid('getRowData');
		   if(rows.length <= 0){
			   js.closeLoading();
				$(".btn").attr("disabled", false);
				if(data.code == 0){
					js.showErrorMessage("未查询到有效数据！");
				}
			   return false;
		   }
		   
		   $.each(rows,function(i,row){
				
				if(row.PSTYP!='3'){//不带委外清单的，才可以编辑可进仓数量
					
					$("#dataGrid").jqGrid('setCell',row.ID, 'MAY_IN_QTY', '', 'editable-cell');
				}else{
					
					$("#dataGrid").jqGrid('setCell',row.ID, "MAY_IN_QTY", '','not-editable-cell');
				}
			});
		   
		   
	    	js.closeLoading();
			$(".btn").attr("disabled", false);
	    },
        formatCell:function(rowid, cellname, value, iRow, iCol){
        	if(cellname=='LGORT'){
				var rec = $('#dataGrid').jqGrid('getRowData', rowid);
	            $('#dataGrid').jqGrid('setColProp', 'LGORT', { editoptions: {value:getLotOption(rec.SOBKZ)}
	            });
			}
        	//在编辑前缓存到F_B_BIN_CODE这个字段
        	if(cellname=='BIN_CODE'){
        		$("#dataGrid").jqGrid('setCell', rowid, 'F_B_BIN_CODE',value,'editable-cell');
			}
        	
        	
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
        afterSaveCell:function(rowid, cellname, value, iRow, iCol){
        	//BIN_CODE
        	
        	if(cellname=='BIN_CODE'){
        		var rec = $('#dataGrid').jqGrid('getRowData', rowid);
        		
        		$.ajax({
		        	  url:baseUrl + "config/corewhbin/queryBinCode",
		        	  data:{
		        		  "WH_NUMBER":rec.WH_NUMBER,
		        		  "BIN_CODE":rec.BIN_CODE,
		        		  "DEL":"0"
		        		  },
		        	  success:function(resp){
		        		 if(resp.result.length>0){
		        			 //储位存在
		        			 $("#dataGrid").jqGrid('setCell', rowid, "BIN_CODE_SHELF", rec.BIN_CODE,'editable-cell');
		        		 }else{
		        			 js.alert("储位"+value+"不存在");
		        			 //写入修改前的储位
		         			$("#dataGrid").jqGrid('setCell', rowid, cellname, rec.F_B_BIN_CODE,'editable-cell');
		        		 }
		        	  }
		          })
		        
        	}
        },
		beforeSelectRow: function (rowid, e) {  //点击复选框才选中行
			   var $myGrid = $(this),  
			   i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),  
			   cm = $myGrid.jqGrid('getGridParam', 'colModel'); 
			   if(cm[i].name == 'cb'){
				   $('#dataGrid').jqGrid('setSelection',rowid);
			   }			   		
			   return (cm[i].name == 'cb'); 
			   /*$('#dataGrid').jqGrid('setSelection',rowid);

			   return false;*/
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
		this.businessList=getBusinessList("02");
		this.businessList.concat(getBusinessList("03"));
		
		if(undefined == this.businessList || this.businessList.length<=0){
			$("#btnsaveinteralbound").attr("disabled","disabled");
			js.showErrorMessage("工厂未配置收货进仓业务类型！");	
		}else{
			this.inbound_type=this.businessList[0].CODE;
		}
		//初始化查询仓库
		var plantCode = $("#werks").val();
		
        //初始化仓库
        $.ajax({
      	  url:baseUrl + "common/getWhDataByWerks",
      	  data:{"WERKS":plantCode},
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
			if(vm.inbound_type==""){
				js.showErrorMessage("工厂未配置收货进仓业务类型，不能执行进仓交接！");
				return false;
			}
			
			var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
			
			if(ids.length==0){
				js.alert("请选择要交接的数据!");
				return;
			}
			//验证交接模式
			var business_rows=[];
			var rows = [];
			for(var id in ids){
				var row = $("#dataGrid").jqGrid('getRowData',ids[id]);
				
				rows[id] = row;
				business_rows[id]=row.BUSINESS_NAME;
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
        	});
			*/
			
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
	        	  success:function(resp){
	        		  vm.warehourse = resp.data;
		        	  if(resp.data.length>0){
		         			 vm.whNumber=resp.data[0].WH_NUMBER
		         	  }
	        	  }
	          })
	         
		
		},
		
		getVendorNoFuzzy:function(){
			getVendorNoSelect("#lifnr",null,null,$("werks").val());
		},
		onChangeItemText:function(){
			var itemText = $("#item_text").val();
			var rows=$("#dataGrid").jqGrid('getRowData');
			$.each(rows,function(i,row){
			  $("#dataGrid").jqGrid('setCell', row.ID, "ITEM_TEXT", itemText);//第二个参数代表行 id，第三个代表 列名称，第四个可以是值或者属性
				
			});
		},
		initDataGrid:function(){}
	}
});

function queryList(){
	$(".btn").attr("disabled", true);
	$.ajax({
		url:baseURL+"in/wmsinhandoverbound/list",
		dataType : "json",
		type : "post",
		data : {
			"WERKS":$("#werks").val(),
			"WH_NUMBER":$("#whNumber").val(),
			"WH_MANAGER":$("#whManager").val(),
			"INBOUND_NO":$("#inBoundNo").val(),
			"LIFNR":$("#vendor").val(),
			"MATNR":$("#matnr").val(),
			"BATCH":$("#batch").val()
		},
		async: true,
		success: function (response) { 
			
				//$("#dataGrid").jqGrid("clearGridData", true);
				//listdata.length=0;
			if(response.code == "0"){
				if(response.retMsg.retMsg!=null){
					
					js.alert(response.retMsg.retMsg);
					$(".btn").attr("disabled", false);
				}else{
					listdata = listdata.concat(response.result);
					
					$('#dataGrid').jqGrid('setGridParam',{data: listdata}).trigger( 'reloadGrid' );
				
					$(".btn").attr("disabled", false);
				}
				
				
				if(response.BARCODE_FLAG=="0"){//不启用条码的工厂
					$('#dataGrid').jqGrid('setColProp', 'MAY_IN_QTY', { editable:true,editrules:{number:true}});
					
					$('#dataGrid').jqGrid('setColProp', 'BIN_CODE', { editable:true});
				}else{
					$('#dataGrid').jqGrid('setColProp', 'MAY_IN_QTY', { editable:false});
				}
				
			}else{
				alert(response.msg)
				$(".btn").attr("disabled", false);
			}
		}
	});
	
	

}

$("#btnqueryhandoverbound").click(function () {
	if(vm.inbound_type==""){
		js.showErrorMessage("工厂未配置收货进仓业务类型，不能执行进仓交接！");
		return false;
	}
	//whManager  vendor matnr batch
	if(($("#inBoundNo").val()!='')&&($("#whManager").val()=='')
			&&($("#vendor").val()=='')&&($("#matnr").val()=='')
			&&($("#batch").val()=='')){//如果单号不为空,其它条件为空的，才能累加到列表数据
		if($("#dhhidden").val()!=''){
			var arrayinbound=$("#dhhidden").val().split(",");
			for(var i=0;i<arrayinbound.length;i++){
				if($("#inBoundNo").val()==arrayinbound[i]){
					js.alert("单号不能重复！");
					return false;
				}
			}
		}
		
		inboundhandover=inboundhandover+","+$("#inBoundNo").val();//把进仓单号都保存起来
		$("#dhhidden").val(inboundhandover);
		
		queryList();
		
		$("#werks").attr("disabled","disabled");//设为不可用
		$("#whNumber").attr("disabled","disabled");//设为不可用
	}else if(($("#inBoundNo").val()!='')&&(($("#whManager").val()!='')||
			($("#vendor").val()!='')||($("#matnr").val()!='')||
			($("#batch").val()!=''))){//除了进仓单 条件以外的 查询，清空列表，再查询
		
		$("#dataGrid").jqGrid("clearGridData", true);
		listdata = [];
		//$("#dhhidden").val("");
		
		queryList();
		
	}else if($("#inBoundNo").val()==''){
		js.alert("单号不能为空！");
		return false;
	}
	
});
//库位修改
$("#btnupdatelgort").click(function () {});

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

function iceil_add(e){
	//件数=进仓数量/装箱数量
	if($(e).val().trim()==""){
		js.alert("进仓数量不能为空！");
		return false;
	}
	if($(e).val().trim()==0){
		js.alert("进仓数量不能为0！");
		return false;
	}

	$(e).parent("td").next().next().next().html(Math.ceil($(e).val().trim()/$(e).parent("td").next().next().html()));
}

function iceil_add2(e){
	//件数=进仓数量/装箱数量
	if($(e).val().trim()==""){
		js.alert("装箱数量不能为空！");
		return false;
	}
	if($(e).val().trim()==0){
		js.alert("装箱数量不能为0！");
		return false;
	}
	$(e).parent("td").next().html(Math.ceil($(e).parent("td").prev().prev().html()/$(e).val().trim()));
}

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
	
	if($("#pzddt").val()==""){
		js.alert("凭证日期不能为空!");
		$(".btn").attr("disabled", false);
		return;
	}
	if($("#jzddt").val()==""){
		js.alert("记账日期不能为空!");
		$(".btn").attr("disabled", false);
		return;
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
	for(var id in ids){
		var row = $("#dataGrid").jqGrid('getRowData',ids[id]);
		
		rows[id] = row;
		
		if(row.MAY_IN_QTY-(row.IN_QTY-row.REAL_QTY)>0){
			js.alert("可进仓数量不能大于单据数量减去已进仓数量!");
			return;
		}
	}
	
	$("#btnsaveinteralbound").val("交接中...");	
	$("#btnsaveinteralbound").attr("disabled","disabled");
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
		async: true,
		success:function(resp){
			if(resp.code == '0'){
				js.alert("交接成功!  "+resp.msg);
				//列表 单号缓存都置位空
				$("#dataGrid").jqGrid("clearGridData", true);
				inboundhandover="";
				$("#dhhidden").val("");
				listdata.length=0;
				//
				$("#btnqueryhandoverbound").attr("disabled", false);
				$("#btnsaveinteralbound").val("交接确认");
				js.closeLoading();
			}else{
				js.alert(resp.msg);
				js.showErrorMessage("交接失败!  "+resp.msg,null,null,1000*600000);
				$(".btn").attr("disabled", false);
				$("#btnsaveinteralbound").val("交接确认");
				js.closeLoading();
			}
		}
	});
	$("#btnsaveinteralbound").val("交接确认");
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


/**
 * 显示委外清单
 * @param 
 */

function show(inbound_no,inbound_item_no,receipt_no,receipt_item_no){
	//打开弹出窗口并 查询表头
	queryReceiptInfo(inbound_no,inbound_item_no,receipt_no,receipt_item_no);
	//查询列表
	queryInPoCptConsume(inbound_no,inbound_item_no);
}

function queryReceiptInfo(inbound_no,inbound_item_no,receipt_no,receipt_item_no){
	
	$.ajax({
			url : baseUrl + "in/wmsinbound/Receiptlist",
			dataType : "json",
			type : "post",
			async:false,
			data : {
				"RECEIPT_NO":receipt_no,
		  		"RECEIPT_ITEM_NO":receipt_item_no
			},
			success:function(resp){
			   if(resp.code==0){
				  
					 $("#poNo_ww").text(resp.result.PO_NO);
					 $("#poItemNO_ww").text(resp.result.PO_ITEM_NO);
					 $("#lifnr_ww").text(resp.result.LIFNR);
					 $("#liktx_ww").text(resp.result.LIKTX);
					 $("#matnr_ww").text(resp.result.MATNR);
					 $("#maktx_ww").text(resp.result.MAKTX);
					 if(resp.result.IN_QTY!=null){
						 $("#inQty_ww").text(resp.result.IN_QTY);
					 }else{
						 $("#inQty_ww").text(0);
					 }
					 
					 
					 openWeiwaiWindow(receipt_no,receipt_item_no,inbound_no,inbound_item_no);
			   }
			}
		});
}

function openWeiwaiWindow(receipt_no,receipt_item_no,inbound_no,inbound_item_no){
	
	layer.open({
		  type: 1,
		  title:['委外原料消耗','font-size:18px'],
		  closeBtn: 0,
		  btn:['确定','取消'],
		  offset:'t',
		  area: ["750px", "500px"],
		  skin: 'layui-bg-green', //没有背景色
		  shadeClose: false,
		  content: '<div id="layer_content">' +$('#inboundWeiwai').html()+'</div>',
		  btn1:function(index){
			  confirm_ww_n(receipt_no,receipt_item_no,inbound_no,inbound_item_no);
		  },
		  btn2:function(index){
			  $("#inboundWeiWaiTable tbody").html("");
			  layer.close(index);
		  }
		});
}

//查询列表
function queryInPoCptConsume(inbound_no,inbound_item_no){
	$.ajax({
		url:baseURL+"in/wmsinhandoverbound/inPoCptConsumelist",
		dataType : "json",
		type : "post",
		async:false,
		data : {
			"INBOUND_NO":inbound_no,
			"INBOUND_ITEM_NO":inbound_item_no
		},
		//async: true,
		success: function (response) { 
			
			if(response.code == "0"){
				listwwdata=response.result;
				
				var listdata_ww = response.result;
				console.info(JSON.stringify(listdata_ww))
				$("#inboundWeiWaiTable tbody").html("");
				$.each(listdata_ww,function(i,row){
					 var tr=$("<tr/>");
					 $("<td/>").html(row.MATN2).appendTo(tr);
					 $("<td/>").html(row.MAKTX2).appendTo(tr);
					 $("<td/>").html(row.BATCH).appendTo(tr);
					 $("<td/>").html(row.MENG2).appendTo(tr);
					 $("<td />").html(row.MEIN2).appendTo(tr);
					 $("<td style='display:none'/>").html(row.WERKS).appendTo(tr);

					 $(tr).appendTo($("#inboundWeiWaiTable tbody"));
				})	
			}else{
				alert(response.msg)
			}
		}
	});
}

//确认委外
function confirm_ww_n(receipt_no,receipt_item_no,poNo,poItemNo){
	//弹出层的table获取必须要用这种方式，否则是两个tbody
	var table=$("#layer_content").find("table[id='inboundWeiWaiTable']")
	var trs=$(table).find("tbody").find("tr");
	//不能用这种方式，否则是两个tbody
	//var trs=$("#inboundWeiWaiTable tbody").find("tr");
	
	rows_ww=[];
	
	for(var m=0;m<trs.length;m++){
		var MATN2 ="";
		
		if($(trs[m]).find("td").eq(0).find("input").val()!=undefined){
			MATN2 = $(trs[m]).find("td").eq(0).find("input").val();
		}else{
			MATN2 = $(trs[m]).find("td").eq(0).html();
		}
		
		var MAKTX2 ="";
		if($(trs[m]).find("td").eq(1).find("input").val()!=undefined){
			MAKTX2 = $(trs[m]).find("td").eq(1).find("input").val();
		}else{
			MAKTX2 = $(trs[m]).find("td").eq(1).html();
		}
		
		var BATCH ="";
		if($(trs[m]).find("td").eq(2).find("input").val()!=undefined){
			BATCH = $(trs[m]).find("td").eq(2).find("input").val();
		}else{
			BATCH = $(trs[m]).find("td").eq(2).html();
		}
		
		var MENG2 ="";
		if($(trs[m]).find("td").eq(3).find("input").val()!=undefined){
			MENG2 = $(trs[m]).find("td").eq(3).find("input").val();
		}else{
			MENG2 = $(trs[m]).find("td").eq(3).html();
		}
		
		var MEIN2 ="";
		if($(trs[m]).find("td").eq(4).find("input").val()!=undefined){
			MEIN2 = $(trs[m]).find("td").eq(4).find("input").val();
		}else{
			MEIN2 = $(trs[m]).find("td").eq(4).html();
		}
		
		var WERKS ="";
		if($(trs[m]).find("td").eq(5).find("input").val()!=undefined){
			WERKS = $(trs[m]).find("td").eq(5).find("input").val();
		}else{
			WERKS = $(trs[m]).find("td").eq(5).html();
		}
		
		rows_ww[m]="{"+"'MATN2':'"+MATN2+"',"+"'MAKTX2':'"+MAKTX2+"',"+"'BATCH':'"+BATCH+
		"',"+"'MENG2':'"+MENG2+"',"+"'MEIN2':'"+MEIN2+"',"+"'WERKS':'"+WERKS+
		"',"+"'RECEIPT_NO':'"+receipt_no+"',"+"'RECEIPT_ITEM_NO':'"+receipt_item_no+
		"',"+"'EBELN':'"+poNo+"',"+"'EBELP':'"+poItemNo+
			"'}";
		
	}
	for(var m=0;m<rows_ww.length;m++){
		console.info("|||:"+rows_ww[m]);
	}
	
}

$("#reset").click(function(){
	listdata = [];
	$("#dataGrid").jqGrid("clearGridData", true);
	$("#werks").attr("disabled", false);
	$("#whNumber").attr("disabled", false);
});


function getLotOption(SOBKZ){
	var options="";
	$.ajax({
		url:baseUrl+'in/wmsinbound/lgortlist',
		type:'post',
		async:false,
		dataType:'json',
		data:{
			"DEL":'0',
    		"WERKS":$("#werks").val(),
    		"SOBKZ":SOBKZ
    		//"BAD_FLAG":'0'
		},
		success:function(resp){
			//alert(resp.msg)
			if(resp.msg=='success'){		
				for(i=0;i<resp.result.length;i++){
	 				if(i != resp.result.length - 1) {
	 					options += resp.result[i].LGORT + ":" +resp.result[i].LGORT + ";";
	 				} else {
	 					options += resp.result[i].LGORT + ":" + resp.result[i].LGORT;
	 				}
				}
				//alert(options)
				console.info(options)
				
			}
		}		
	})
	return options;
}

function funFromjs(result) {
	var str_list = result.split(";");
	var inbound_no = str_list[0].split(":")[1];
	//alert(result)
	var e = $.Event('keydown');
	e.keyCode = 13;
	$(last_scan_ele).val(inbound_no).trigger(e);
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
