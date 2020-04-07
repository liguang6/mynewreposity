var dataGrid;
var darows=[];
var lastrow,lastcell,lastcellname;
 
var vm = new Vue({
		el:'#page_div',
		data:{
			showList:true,
			formUrl:baseUrl+"account/wmsAccountReceiptV/listPOVMat",
			receipt_type:'',//收货类型
			WERKS:"",
			WH_NUMBER:"",//仓库号
			businessList:[],
			PZ_DATE:getCurDate(),//凭证日期
			JZ_DATE:getCurDate(),//记账日期
			PO_NO:"",//SAP采购订单号,
			MAT_DOC_YEAR:new Date().getFullYear(),
			MAT_DOC:"",
			lgortList:[],//库位列表
			whList:[],
			LGORT:"",//选中的库位
		},
		watch:{
			receipt_type:{
				handler:function(newVal,oldVal){
					vm.showList=false;
					//SAP采购订单收货（V)
					if(newVal=='22'){
						vm.formUrl=baseUrl+"account/wmsAccountReceiptV/listPOVMat";
					}
					//SAP交货单收货（V)
					if(newVal=='23'){
						vm.formUrl=baseUrl+"account/wmsAccountReceiptV/listDNVMat";
					}
					//SAP303调拨收货（V)
					if(newVal=='24'){
						vm.formUrl=baseUrl+"account/wmsAccountReceiptV/listTOVMat";
					}
					
					//SAP生产订单收货（V)
					if(newVal=='60' || newVal=='62'){
						vm.formUrl=baseUrl+"account/wmsAccountReceiptV/listMOVMat";
					}
					
				}
			},
			WERKS:{
				handler:function(newVal,oldVal){
						 vm.whList = [];
						 vm.lgortList = [];
				         this.$nextTick(function(){
				        	 if(vm.receipt_type=='60' || vm.receipt_type=='62'){
				        		 setWerks();
				        	 }			        	 
							 $.ajax({
								    url:baseURL+"common/getWhDataByWerks",
									dataType : "json",
									type : "post",
									data : {
										"WERKS":newVal
									},
									async: true,
						        	  success:function(resp){
						        		 if(resp && resp.data.length>0){
							        		 vm.whList = resp.data;
							        		 vm.WH_NUMBER = resp.data[0].WH_NUMBER;
						        		 }
						        	  }
					          })
			        		  vm.lgortList=getLgortList(newVal,null,null,'Z')
				        	  
						})
					}
			},
			WH_NUMBER:{
				handler:function(newVal,oldVal){
					this.$nextTick(function(){
						setWH();
					})
					
				}
			},
			LGORT:{
				handler:function(newVal,oldVal){
					this.$nextTick(function(){
						setLGORT();
					})
					
				}
			}
		},
		methods: {
			query: function () {
				//SAP采购订单收货（V)
				if(vm.receipt_type=='22'){
					if(vm.PO_NO.trim().length==0){
						js.showErrorMessage("请输入SAP采购订单号！");
						return false;
					}
				}
				//SAP交货单收货（V)
				if(vm.receipt_type=='23'){
					if(vm.PO_NO.trim().length==0){
						js.showErrorMessage("请输入SAP交货单号！");
						return false;
					}
				}
				//303调拨收货（V)
				if(vm.receipt_type=='24'){
					if(vm.MAT_DOC.trim().length==0){
						js.showErrorMessage("请输入SAP303调拨凭证号！");
						return false;
					}
				}
				//生产订单收货（V)
				if(vm.receipt_type=='60' ||vm.receipt_type=='62'){
					if(vm.PO_NO.trim().length==0){
						js.showErrorMessage("请输入SAP生产订单号！");
						return false;
					}
					$("#LGORT_NAME").val($("#LGORT").find('option:selected').attr("logrt_name"));
				}
				vm.showList=true;
				vm.$nextTick(function(){//数据渲染后调用
					ajaxQuery();
				})
		
			},
			countMat:function(){
				$("#countMat tbody").html("");
				 var rows=$("#dataGrid").jqGrid('getRowData');
				 var last_mat=[];
				 var last_vdcd="";
				 var mat_id="";
				 $.each(rows,function(i,row){
					 var QTY=0;
					 QTY=Number(row.RECEIPT_QTY);
					 if(last_mat.indexOf(row.MATNR+row.LIFNR)>=0){//按供应商和物料号汇总						
						 var count_td=$("#"+mat_id).children("td").eq(1);
						 $(count_td).html(Number($(count_td).html())+QTY);
					 }else{
						 mat_id=row.MATNR+row.LIFNR;
						 last_mat.push(mat_id);
						 var tr=$("<tr id='"+mat_id+"' />");
						 $("<td />").html(row.MATNR).appendTo(tr);
						 $("<td />").html(QTY).appendTo(tr);
						 $("<td />").html(row.LIKTX).appendTo(tr);
						 $(tr).appendTo($("#countMat tbody"));
					 }					 
				 })		
				
				layer.open({
					  type: 1,
					  title:['料号汇总','font-size:18px'],
					  closeBtn: 1,
					  btn:['关闭'],
					  offset:'t',
					  area: '700px',
					  skin: 'layui-bg-green', //没有背景色
					  shadeClose: false,
					  content: $('#countMat').html()
					});
			},
			boundIn:function()	{
				var flag=true;
				$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
				var rows=getSelectedRows();
				if(rows<=0){
					//js.showMessage("请选择需要保存的行项目数据！");	
					alert("请选择一条或多条记录！");
					return false;
				}
				
				//必填字段判断，未填不能选中
				$.each(rows,function(i,rowid){
					var row = $("#dataGrid").jqGrid("getRowData",rowid);
					console.info(rowid+":"+row.VENDOR_MANAGER)
					if(vm.receipt_type=='22'){
						if((row.VENDOR_MANAGER==""||row.SHORT_NAME=="")&&row.VENDOR_FLAG!='X'){
							js.showErrorMessage("第"+rowid+"行必填字段（采购、供应商简称）未填写完整！");	
							flag=false;
							return false;
						}
					}
					if(Number(row.RECEIPT_QTY) <=0){
						js.showErrorMessage("第"+rowid+"行收货数量必须大于0！");	
						flag=false;
						return false;
					}
				})
					
				if(flag){
					showDangerMat(rows);	
				}		
			},
	
		},
		created:function(){
			this.businessList=getBusinessList("08","ACCOUNT_RECEIPT_V");
			if(this.businessList.length>0){
				this.receipt_type=this.businessList[0].CODE;
				this.SYN_FLAG=this.businessList[0].SYN_FLAG;
			}else{
				js.showErrorMessage("工厂未配置收货（A）类型的业务类型！");
			}
			this.WERKS=$("#WERKS").find("option").first().val();
		}
		
	});

function ajaxQuery(){
	var multiselect=true;
	var columns=[];
	var data = $('#dataGrid').data("dataGrid");
	$("#mat_count").html("");
	
	if(data){
		/*$("#searchForm").submit();
		return false;*/
		$("#dataGrid").jqGrid('GridUnload');
	}
	//SAP采购订单收料（V）
	if(vm.receipt_type=='22'){
		columns=[		
			{ label: '序号', name: 'ID', index: 'ID', align:"left",width: 45, key: true ,sortable:false,frozen: true,formatter:function(val, obj, row, act){
				var html=obj.rowId;
				if(row.DANGER_FLAG=='X'){
					html+="&nbsp;&nbsp;<i class='fa fa-exclamation-triangle fa-lg' aria-hidden='true' style='color:#e3ca12'></i>"
				} 
				return html;
			},unformat:function(cellvalue, options, rowObject){
				return cellvalue;
			}},
   			{ label: '收货工厂', name: 'WERKS',align:"center", index: 'WERKS', width: 70,frozen: true, }, 			
   			{ label: '仓库号', name: 'WH_NUMBER',align:"center", index: 'WH_NUMBER', width: 70,frozen: true, }, 			
   			{ label: '订单号 ', name: 'PO_NO', align:"center",index: 'PO_NO', width: 90,frozen: true,}, 			
   			{ label: '行号', name: 'PO_ITEM_NO',align:"center", index: 'PO_ITEM_NO', width: 60,frozen: true,}, 			
   			{ label: '料号', name: 'MATNR', align:"center",index: 'MATNR', width: 90,frozen: true, }, 			
   			{ label: '物料描述', name: 'MAKTX',align:"center", index: 'MAKTX', width: 180 }, 			
   			{ label: '<span style="color:blue">库位</span>', name: 'LGORT',align:"center", index: 'LGORT', width: 60 ,editable:true,edittype:'select'}, 			
   			{ label: '储位', name: 'BIN_CODE', align:"center",index: 'BIN_CODE', width: 60,sortable:false, }	,
   			{ label: '订单数量', name: 'MENGE',align:"center", index: 'MENGE', width: 70 }, 			
   			{ label: '最大可收数量', name: 'MAX_MENGE',align:"center", index: 'MAX_MENGE', width: 90 },
   			{ label: '已收数量', name: 'XSQTY',align:"center", index: 'XSQTY', width: 70 }, 			
   			{ label: '<span style="color:red">*</span><span style="color:blue">收货数量</span>', name: 'RECEIPT_QTY',sortable:false, align:"center",index: 'RECEIPT_QTY', width: 70,editable:true,editrules:{
   				required:true,number:true},  formatter:'number',formatoptions:{
   				decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}}	,
   			{ label: '采购单位', name: 'UNIT', align:"center",index: 'UNIT', width: 80 }	,
   			{ label: '<span style="color:blue">收料员</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'RECEIVER\')" title="向下填充" style="color:green" aria-hidden="true"></i>', name: 'RECEIVER', align:"center",index: 'RECEIVER', width: 90,editable:true, sortable:false, },
   			{ label: '<span style="color:red">*</span><span style="color:blue">采购</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'VENDOR_MANAGER\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
   				name: 'VENDOR_MANAGER', align:"center",index: 'VENDOR_MANAGER', width: 70,editable:true,  sortable:false, },
   			{ label: '<span style="color:red">*</span><span style="color:blue">供应商简称</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'SHORT_NAME\')" title="向下填充" style="color:green" aria-hidden="true"></i>', 
   					name: 'SHORT_NAME', align:"center",index: 'SHORT_NAME', width: 100,editable:true, sortable:false,  },
   			{ label: '供应商名称', name: 'LIKTX', align:"center",index: 'LIKTX', width: 150,sortable:false,},
   			{ label: '供应商代码', name: 'LIFNR', align:"center",index: 'LIFNR', width: 80,},
   			{ label: '需求跟踪号', name: 'BEDNR', align:"center",index: 'BEDNR', width: 80,sortable:false,},
   			{ label: '申请人', name: 'AFNAM', align:"center",index: 'AFNAM', width: 80,sortable:false,},
   			{ label: '<span style="color:blue">行文本</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'ITEM_TEXT\')" title="向下填充" style="color:green" aria-hidden="true"></i>', 
   				name: 'ITEM_TEXT', align:"center",index: 'ITEM_TEXT', width: 260,editable:true,sortable:false,},
   				{label:'DANGER_FLAG',name:'DANGER_FLAG',hidden:true},
   				{label:'VENDOR_FLAG',name:'VENDOR_FLAG',hidden:true},
   				{label:'HEADER_TXT',name:'HEADER_TXT',hidden:true},
   				{label:'SOBKZ',name:'SOBKZ',hidden:true},
   				{label:'GOOD_DATES',name:'GOOD_DATES',hidden:true},
   				{label:'MEINS',name:'MEINS',hidden:true},/* 物料基本计量单位 */
   				{label:'UMREZ',name:'UMREZ',hidden:true},/* 单位转换分子 */
   				{label:'UMREN',name:'UMREN',hidden:true},/* 单位转换分母 */
   				{label:'HXID',name:'HXID',hidden:true},
   				{label:'BIN_NAME',name:'BIN_NAME',hidden:true},
   			];
		
		dataGrid=$("#dataGrid").dataGrid({
		    datatype: "json",
		    searchForm:$("#searchForm"),
		    cellEdit:true,
			cellurl:'#',
			cellsubmit:'clientArray',
		    colModel: columns,
		    viewrecords: false,
		    showRownum:false,
		    autowidth:true,
	        shrinkToFit:false,  
	        autoScroll: true, 
		    rownumWidth: 25, 
		    rowNum:-1,
		    loadonce:true,
		    formatter:{number:{decimalSeparator:".", thousandsSeparator: " ", decimalPlaces: 3,defaultValue:''}},
		   /* dataId:'id',*/
		   multiselect: multiselect,
		   loadComplete:function(data){
			   if(data.code==500){
					js.showErrorMessage(data.msg,1000*10);
				    //js.alert(data.msg);
					js.closeLoading();
					$(".btn").attr("disabled", false);
					return false;
				}
			   var rows=$("#dataGrid").jqGrid('getRowData');
			   var WERKS=rows[0].WERKS;
			   vm.WERKS=WERKS;
			   
				$.each(rows,function(i,row){	
					$("#dataGrid").jqGrid('setCell', i+1, "WH_NUMBER", $("#WH_NUMBER").val(),'editable-cell');//仓库号默认为页面仓库下拉列表的值
					  if (row.URGENT_FLAG=='X') {//过滤条件  
				          $("#dataGrid").jqGrid('setRowData', i+1, '', {'color':'red'});  
				      }		
					  
					  if(row.VENDOR_FLAG!='X'){
						  $("#dataGrid").jqGrid('setCell', i+1, 'VENDOR_MANAGER', '', 'not-editable-cell');  
				          $("#dataGrid").jqGrid('setCell', i+1, 'SHORT_NAME', '', 'not-editable-cell'); 
				          $("#dataGrid").jqGrid('setLabel','VENDOR_MANAGER','采购');
				          $("#dataGrid").jqGrid('setLabel','SHORT_NAME','供应商简称');
					  }
					  
				});
	
				$("#mat_count").html(rows.length+"行");
				
				js.closeLoading();
				$(".btn").attr("disabled", false);
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
			   /*$('#dataGrid').jqGrid('setSelection',rowid);

			   return false;*/
			} ,
			formatCell:function(rowid, cellname, value, iRow, iCol){
				console.info(cellname)
				if(cellname=='LGORT'){
					var rec = $('#dataGrid').jqGrid('getRowData', rowid);
		            $('#dataGrid').jqGrid('setColProp', 'LGORT', { editoptions: {value:getLotOption(rec.WERKS)}
		            });
				}		
				
			},
			afterSaveCell:function(rowid, cellname, value, iRow, iCol){	
				if(value==""){
					$("#dataGrid").jqGrid('setCell', rowid, cellname, '&nbsp;','editable-cell');
				}
			
				var row =  $("#dataGrid").jqGrid('getRowData', rowid);
				if(cellname=='RECEIPT_QTY'){
					var field="收货数量";
					var able_qty=row.MAX_MENGE-row.XSQTY;
					if(value>able_qty){
						js.alert(field+"不能大于"+able_qty);
						$("#dataGrid").jqGrid('setCell', rowid, cellname, able_qty,'editable-cell');
					}
				}		
			},
		});
		$("#dataGrid").jqGrid("setFrozenColumns");
	}
	
	//SAP交货单收料（V）
	if(vm.receipt_type=='23'){
		columns=[		
			{ label: '序号', name: 'ID', index: 'ID', align:"left",width: 45, key: true ,sortable:false,frozen: true,formatter:function(val, obj, row, act){
				var html=obj.rowId;
				if(row.DANGER_FLAG=='X'){
					html+="&nbsp;&nbsp;<i class='fa fa-exclamation-triangle fa-lg' aria-hidden='true' style='color:#e3ca12'></i>"
				} 
				return html;
			},unformat:function(cellvalue, options, rowObject){
				return cellvalue;
			}},
   			{ label: '收货工厂', name: 'WERKS',align:"center", index: 'WERKS', width: 70,frozen: true, }, 			
   			{ label: '仓库号', name: 'WH_NUMBER',align:"center", index: 'WH_NUMBER', width: 70,frozen: true, }, 			
   			{ label: '交货单号 ', name: 'VBELN', align:"center",index: 'VBELN', width: 90,frozen: true,}, 			
   			{ label: '行号', name: 'POSNR',align:"center", index: 'POSNR', width: 60,frozen: true,}, 			
   			{ label: '料号', name: 'MATNR', align:"center",index: 'MATNR', width: 90,frozen: true, }, 			
   			{ label: '物料描述', name: 'MAKTX',align:"center", index: 'MAKTX', width: 180 }, 			
   			{ label: '<span style="color:blue">发货库位</span>', name: 'LGORT',align:"center", index: 'LGORT', width: 70 ,sortable:false}, 			
   			{ label: '收货库位', name: 'S_LGORT',align:"center", index: 'S_LGORT', width: 70 }, 
   			{ label: '储位', name: 'BIN_CODE', align:"center",index: 'BIN_CODE', width: 60,sortable:false, }	,
   			{ label: '送货数量', name: 'LFIMG',align:"center", index: 'LFIMG', width: 70 }, 			
   			/*{ label: '已收数量', name: 'XS101T',align:"center", index: 'XS101T', width: 70 }, 	*/		
   			{ label: '<span style="color:red">*</span><span style="color:blue">收货数量</span>', name: 'RECEIPT_QTY', align:"center",sortable:false,index: 'RECEIPT_QTY', width: 70,editable:true,editrules:{
   				required:true,number:true},  formatter:'number',formatoptions:{
   				decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}}	,
   			{ label: '单位', name: 'UNIT', align:"center",index: 'UNIT', width: 80}	,
   			{ label: '批次', name: 'CHARG', align:"center",index: 'CHARG', width: 100,sortable:false,}	,
   			{ label: '采购订单号 ', name: 'EBELN', align:"center",index: 'EBELN', width: 90}, 			
   			{ label: '行号', name: 'EBELP',align:"center", index: 'EBELP', width: 60,}, 
   			{ label: '供应商名称', name: 'LIKTX', align:"center",index: 'LIKTX', width: 150,sortable:false,},
   			{ label: '供应商代码', name: 'LIFNR', align:"center",index: 'LIFNR', width: 80,sortable:false,},
   			{ label: '需求跟踪号', name: 'BEDNR', align:"center",index: 'BEDNR', width: 80,sortable:false,},
   			{ label: '申请人', name: 'AFNAM', align:"center",index: 'AFNAM', width: 80,sortable:false,},
   			{ label: '<span style="color:blue">行文本</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'ITEM_TEXT\')" title="向下填充" style="color:green" aria-hidden="true"></i>', 
   				name: 'ITEM_TEXT', align:"center",index: 'ITEM_TEXT', width: 260,editable:true,sortable:false,},
   				{label:'DANGER_FLAG',name:'DANGER_FLAG',hidden:true},
   				{label:'HEADER_TXT',name:'HEADER_TXT',hidden:true},
   				{label:'SOBKZ',name:'SOBKZ',hidden:true},
   				{label:'GOOD_DATES',name:'GOOD_DATES',hidden:true},
   				{label:'MEINS',name:'MEINS',hidden:true},/* 物料基本计量单位 */
   				{label:'UMREZ',name:'UMREZ',hidden:true},/* 单位转换分子 */
   				{label:'UMREN',name:'UMREN',hidden:true},/* 单位转换分母 */
   				{label:'HXID',name:'HXID',hidden:true},
   				{label:'BIN_NAME',name:'BIN_NAME',hidden:true},
   				{label:'F_WERKS',name:'F_WERKS',hidden:true},
   				{label:'F_LGORT',name:'F_LGORT',hidden:true},
   				{label:'F_BATCH',name:'F_BATCH',hidden:true},
   				{label:'VGBEL',name:'VGBEL',hidden:true},
   				{label:'VGPOS',name:'VGPOS',hidden:true},
   			];
		
		dataGrid=$("#dataGrid").dataGrid({
		    datatype: "json",
		    searchForm:$("#searchForm"),
		    cellEdit:true,
			cellurl:'#',
			cellsubmit:'clientArray',
		    colModel: columns,
		    viewrecords: false,
		    showRownum:false,
		    autowidth:true,
	        shrinkToFit:false,  
	        autoScroll: true, 
		    rownumWidth: 25, 
		    loadonce:true,
		    rowNum:-1,
		    formatter:{number:{decimalSeparator:".", thousandsSeparator: " ", decimalPlaces: 3,defaultValue:''}},
		   /* dataId:'id',*/
		   multiselect: multiselect,
		   loadComplete:function(data){
			   if(data.code==500){
					js.showErrorMessage(data.msg,1000*10);
				    //js.alert(data.msg);
					js.closeLoading();
					$(".btn").attr("disabled", false);
					return false;
				}
			   var rows=$("#dataGrid").jqGrid('getRowData');
			   var WERKS=rows[0].WERKS;
			   vm.WERKS=WERKS;
			   
				$.each(rows,function(i,row){
					$("#dataGrid").jqGrid('setCell', i+1, "WH_NUMBER", $("#WH_NUMBER").val(),'editable-cell');//仓库号默认为页面仓库下拉列表的值
					  if (row.POSNR!=row.VGPOS) {// 批次拆分行，设置收货数量不能修改
						  $("#dataGrid").jqGrid('setCell', i+1, 'RECEIPT_QTY', '', 'not-editable-cell');  
					  }
					  
				});
	
				$("#mat_count").html(rows.length+"行");
				
				js.closeLoading();
				$(".btn").attr("disabled", false);
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
			   /*$('#dataGrid').jqGrid('setSelection',rowid);

			   return false;*/
			} ,
			formatCell:function(rowid, cellname, value, iRow, iCol){
				console.info(cellname)
				if(cellname=='F_LGORT'){
					var rec = $('#dataGrid').jqGrid('getRowData', rowid);
		            $('#dataGrid').jqGrid('setColProp', 'F_LGORT', { editoptions: {value:getLotOption(rec.F_WERKS)}
		            });
				}		
				
			},
			afterSaveCell:function(rowid, cellname, value, iRow, iCol){	
				if(value==""){
					$("#dataGrid").jqGrid('setCell', rowid, cellname, '&nbsp;','editable-cell');
				}
			
				var row =  $("#dataGrid").jqGrid('getRowData', rowid);
				if(cellname=='RECEIPT_QTY'){
					var field="收货数量";
					var able_qty=row.LFIMG-row.XS101T;
					if(value>able_qty){
						js.alert(field+"不能大于"+able_qty);
						$("#dataGrid").jqGrid('setCell', rowid, cellname, able_qty,'editable-cell');
					}
				}		
			},
		});
		//$("#dataGrid").jqGrid("setFrozenColumns");
	}
	
	//SAP303调拨收料（V）
	if(vm.receipt_type=='24'){
		columns=[		
			{ label: '序号', name: 'ID', index: 'ID', align:"left",width: 45, key: true ,sortable:false,frozen: true,formatter:function(val, obj, row, act){
				var html=obj.rowId;
				if(row.DANGER_FLAG=='X'){
					html+="&nbsp;&nbsp;<i class='fa fa-exclamation-triangle fa-lg' aria-hidden='true' style='color:#e3ca12'></i>"
				} 
				return html;
			},unformat:function(cellvalue, options, rowObject){
				return cellvalue;
			}},
   			{ label: '收货工厂', name: 'WERKS',align:"center", index: 'WERKS', width: 70,frozen: true, }, 			
   			{ label: '仓库号', name: 'WH_NUMBER',align:"center", index: 'WH_NUMBER', width: 70,frozen: true, }, 			
   			{ label: '303凭证号 ', name: 'SAP_MATDOC_NO', align:"center",index: 'SAP_MATDOC_NO', width: 90,frozen: true,}, 			
   			{ label: '行号', name: 'SAP_MATDOC_ITEM_NO',align:"center", index: 'SAP_MATDOC_ITEM_NO', width: 60,frozen: true,}, 			
   			{ label: '料号', name: 'MATNR', align:"center",index: 'MATNR', width: 90,frozen: true, }, 			
   			{ label: '物料描述', name: 'MAKTX',align:"center", index: 'MAKTX', width: 180 }, 			
   			{ label: '<span style="color:blue">收货库位</span>', name: 'LGORT',align:"center", index: 'LGORT', width: 80 ,editable:true,sortable:false,edittype:'select'}, 			
   			{ label: '储位', name: 'BIN_CODE', align:"center",index: 'BIN_CODE', width: 60,sortable:false, }	,
   			{ label: '调拨数量', name: 'ENTRY_QNT',align:"center", index: 'ENTRY_QNT', width: 70 }, 			
   			{ label: '已收数量', name: 'XS305',align:"center", index: 'XS305', width: 70 },	
   			{ label: '<span style="color:red">*</span><span style="color:blue">收货数量</span>', name: 'RECEIPT_QTY', align:"center",sortable:false,index: 'RECEIPT_QTY', width: 70,editable:true,editrules:{
   				required:true,number:true},  formatter:'number',formatoptions:{
   				decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}}	,
   			{ label: '调拨单位', name: 'UNIT', align:"center",index: 'UNIT', width: 80}	,
   			{ label: '发货工厂', name: 'F_WERKS',align:"center", index: 'F_WERKS', width: 70,frozen: true, }, 			
   			{ label: '发货仓库号', name: 'F_WH_NUMBER',align:"center", index: 'F_WH_NUMBER', width: 90,frozen: true, },
   			{ label: '发货库位', name: 'F_LGORT',align:"center", index: 'F_LGORT', width: 70,frozen: true, },
   			{ label: '发货批次', name: 'F_BATCH',align:"center", index: 'F_BATCH', width: 70,}, 
   			{ label: '<span style="color:blue">需求跟踪号</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'BEDNR\')" title="向下填充" style="color:green" aria-hidden="true"></i>', 
   				name: 'BEDNR', align:"center",index: 'BEDNR', width: 120,editable:true,sortable:false,},
   			/*{ label: '申请人', name: 'AFNAM', align:"center",index: 'AFNAM', width: 80,sortable:false,},*/
   			{ label: '<span style="color:blue">行文本</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'ITEM_TEXT\')" title="向下填充" style="color:green" aria-hidden="true"></i>', 
   				name: 'ITEM_TEXT', align:"center",index: 'ITEM_TEXT', width: 260,editable:true,sortable:false,},
   				{label:'DANGER_FLAG',name:'DANGER_FLAG',hidden:true},
   				{label:'HEADER_TXT',name:'HEADER_TXT',hidden:true},
   				{label:'SOBKZ',name:'SOBKZ',hidden:true},
   				{label:'GOOD_DATES',name:'GOOD_DATES',hidden:true},
   				{label:'MEINS',name:'MEINS',hidden:true},/* 物料基本计量单位 */
   				{label:'UMREZ',name:'UMREZ',hidden:true},/* 单位转换分子 */
   				{label:'UMREN',name:'UMREN',hidden:true},/* 单位转换分母 */
   				{label:'HX_TO_ID',name:'HX_TO_ID',hidden:true},
   				{label:'BIN_NAME',name:'BIN_NAME',hidden:true},
   				{label:'LIFNR',name:'LIFNR',hidden:true},
   				{label:'PRODUCT_DATE',name:'PRODUCT_DATE',hidden:true},
   				
   			];
		
		dataGrid=$("#dataGrid").dataGrid({
		    datatype: "json",
		    searchForm:$("#searchForm"),
		    cellEdit:true,
			cellurl:'#',
			cellsubmit:'clientArray',
		    colModel: columns,
		    viewrecords: false,
		    showRownum:false,
		    autowidth:true,
	        shrinkToFit:false,  
	        autoScroll: true, 
		    rownumWidth: 25, 
		    loadonce:true,
		    rowNum:-1,
		    formatter:{number:{decimalSeparator:".", thousandsSeparator: " ", decimalPlaces: 3,defaultValue:''}},
		   /* dataId:'id',*/
		   multiselect: multiselect,
		   loadComplete:function(data){
			   if(data.code==500){
					js.showErrorMessage(data.msg,1000*10);
				    //js.alert(data.msg);
					js.closeLoading();
					$(".btn").attr("disabled", false);
					return false;
				}
			   var rows=$("#dataGrid").jqGrid('getRowData');
			   var WERKS=rows[0].WERKS;
			   vm.WERKS=WERKS;
	
				$("#mat_count").html(rows.length+"行")
				
				js.closeLoading();
				$(".btn").attr("disabled", false);
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
			   /*$('#dataGrid').jqGrid('setSelection',rowid);

			   return false;*/
			} ,
			formatCell:function(rowid, cellname, value, iRow, iCol){
				if(cellname=='LGORT'){
					var rec = $('#dataGrid').jqGrid('getRowData', rowid);
		            $('#dataGrid').jqGrid('setColProp', 'LGORT', { editoptions: {value:getLotOption(rec.WERKS)}
		            });
				}		
				
			},
			afterSaveCell:function(rowid, cellname, value, iRow, iCol){	
				if(value==""){
					$("#dataGrid").jqGrid('setCell', rowid, cellname, '&nbsp;','editable-cell');
				}
			
				var row =  $("#dataGrid").jqGrid('getRowData', rowid);
				if(cellname=='RECEIPT_QTY'){
					var field="收货数量";
					var able_qty=row.ENTRY_QNT-row.XS305;
					if(value>able_qty){
						js.alert(field+"不能大于"+able_qty);
						$("#dataGrid").jqGrid('setCell', rowid, cellname, able_qty,'editable-cell');
					}
				}		
			},
		});
		//$("#dataGrid").jqGrid("setFrozenColumns");
	}
	
	//生产订单收货过账(V) 副产品收货过账(V)
	if(vm.receipt_type=='60' || vm.receipt_type=='62'){
		columns=[		
			{ label: '序号', name: 'ROWID', index: 'ROWID', align:"left",width: 45, key: true ,sortable:false,frozen: true,formatter:function(val, obj, row, act){
				var html=obj.rowId;
				if(row.DANGER_FLAG=='X'){
					html+="&nbsp;&nbsp;<i class='fa fa-exclamation-triangle fa-lg' aria-hidden='true' style='color:#e3ca12'></i>"
				} 
				return html;
			},unformat:function(cellvalue, options, rowObject){
				return cellvalue;
			}},
   			{ label: '收货工厂', name: 'WERKS',align:"center", index: 'WERKS', width: 65,frozen: true, }, 			
   			{ label: '仓库号', name: 'WH_NUMBER',align:"center", index: 'WH_NUMBER', width: 65,frozen: false, }, 	
   			{ label: '订单号 ', name: 'MO_NO', align:"center",index: 'PO_NO', width: 90,frozen: true,}, 
   			{ label: '行号', name: 'POSNR',align:"center", index: 'POSNR', width: 50,frozen: true,}, 			
   			{ label: '料号', name: 'MATNR', align:"center",index: 'MATNR', width: 90,frozen: true, }, 			
   			{ label: '物料描述', name: 'MAKTX',align:"center", index: 'MAKTX', width: 180 }, 			
   			{ label: '<span style="color:blue">库位 </span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'LGORT\')" title="向下填充" style="color:green" aria-hidden="true"></i> ', name: 'LGORT',align:"center", index: 'LGORT', width: 65 ,sortable:false,editable:true,edittype:'select'}, 			
   			{ label: '储位', name: 'BIN_CODE', align:"center",index: 'BIN_CODE', width: 60,sortable:false, }	,
   			{ label: '生产数量', name: 'BDMNG',align:"center", index: 'BDMNG', width: 65 }, 			
   			{ label: '已收数量', name: 'XSQTY',align:"center", index: 'XSQTY', width: 65 }, 			
   			{ label: '<span style="color:red">*</span><span style="color:blue">收货数量</span>', name: 'RECEIPT_QTY',sortable:false, align:"center",index: 'RECEIPT_QTY', width: 70,editable:true,editrules:{
   				required:true,number:true},  formatter:'number',formatoptions:{
   				decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}}	,
   			{ label: '单位', name: 'UNIT', align:"center",index: 'UNIT', width: 50 }	,
   			{ label: '生产日期', name: 'PRODUCT_DATE', align:"center",index: 'PRODUCT_DATE', width: 80 }	,
   			{ label: '<span style="color:blue">收料员</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'RECEIVER\')" title="向下填充" style="color:green" aria-hidden="true"></i>', name: 'RECEIVER', align:"center",index: 'RECEIVER', width: 90,editable:true, sortable:false, },
   			{ label: '<span style="color:blue">需求跟踪号</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'BEDNR\')" title="向下填充" style="color:green" aria-hidden="true"></i>', name: 'BEDNR', align:"center",index: 'BEDNR', width: 100,editable:true, sortable:false, },
   			{ label: '<span style="color:blue">行文本</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'ITEM_TEXT\')" title="向下填充" style="color:green" aria-hidden="true"></i>', 
   				name: 'ITEM_TEXT', align:"center",index: 'ITEM_TEXT', width: 260,editable:true,sortable:false,},
   				{label:'DANGER_FLAG',name:'DANGER_FLAG',hidden:true},
   				{label:'HEADER_TXT',name:'HEADER_TXT',hidden:true},
   				{label:'SOBKZ',name:'SOBKZ',hidden:true},
   				{label:'GOOD_DATES',name:'GOOD_DATES',hidden:true},
   				{label:'PSMNG',name:'PSMNG',hidden:true},
   				{label:'AUFNR',name:'AUFNR',hidden:true},
   				{label:'MO_ITEM_NO',name:'MO_ITEM_NO',hidden:true},
   				{label:'MEINS',name:'MEINS',hidden:true},
   				{label:'RSNUM',name:'RSNUM',hidden:true},/* 预留号 */
   				{label:'RSPOS',name:'RSPOS',hidden:true},/* 预留行项目号 */
   				{label:'HX_MO_ITEM_ID',name:'HX_MO_ITEM_ID',hidden:true},
   				{label:'HX_QTY',name:'HX_QTY',hidden:true},
   				{label:'HX_QTY_BY',name:'HX_QTY_BY',hidden:true},
   				{label:'HX_MO_COMPONENT_ID',name:'HX_MO_COMPONENT_ID',hidden:true},
   				{label:'BIN_NAME',name:'BIN_NAME',hidden:true},
   				
   			];
		
		dataGrid=$("#dataGrid").dataGrid({
		    datatype: "json",
		    searchForm:$("#searchForm"),
		    cellEdit:true,
			cellurl:'#',
			cellsubmit:'clientArray',
		    colModel: columns,
		    viewrecords: false,
		    showRownum:false,
		    autowidth:true,
	        shrinkToFit:false,  
	        autoScroll: true, 
		    rownumWidth: 25, 
		    rowNum:-1,
		    loadonce:true,
		    formatter:{number:{decimalSeparator:".", thousandsSeparator: " ", decimalPlaces: 3,defaultValue:''}},
		   /* dataId:'id',*/
		   multiselect: multiselect,
		   loadComplete:function(data){
			   if(data.code==500){
					js.showErrorMessage(data.msg,1000*10);
				    //js.alert(data.msg);
					js.closeLoading();
					$(".btn").attr("disabled", false);
					return false;
				}
			   var rows=$("#dataGrid").jqGrid('getRowData');
			   var WERKS=rows[0].WERKS;
			   vm.WERKS=WERKS;
			   
				$.each(rows,function(i,row){	
					$("#dataGrid").jqGrid('setCell', i+1, "WH_NUMBER", vm.WH_NUMBER,'editable-cell');//仓库号默认为页面仓库下拉列表的值
					  if (row.URGENT_FLAG=='X') {//过滤条件  
				          $("#dataGrid").jqGrid('setRowData', i+1, '', {'color':'red'});  
				      }		
					  
				});
	
				$("#mat_count").html(rows.length+"行");
				
				js.closeLoading();
				$(".btn").attr("disabled", false);
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
			   /*$('#dataGrid').jqGrid('setSelection',rowid);

			   return false;*/
			} ,
			formatCell:function(rowid, cellname, value, iRow, iCol){
				console.info(cellname)
				if(cellname=='LGORT'){
					var rec = $('#dataGrid').jqGrid('getRowData', rowid);
		            $('#dataGrid').jqGrid('setColProp', 'LGORT', { editoptions: {value:getLotOption(rec.WERKS)}
		            });
				}		
				
			},
			afterSaveCell:function(rowid, cellname, value, iRow, iCol){	
				if(value==""){
					$("#dataGrid").jqGrid('setCell', rowid, cellname, '&nbsp;','editable-cell');
				}
			
				var row =  $("#dataGrid").jqGrid('getRowData', rowid);
				if(cellname=='RECEIPT_QTY'){
					var field="收货数量";
					var able_qty=row.MAX_MENGE-row.XSQTY;
					if(value>able_qty){
						js.alert(field+"不能大于"+able_qty);
						$("#dataGrid").jqGrid('setCell', rowid, cellname, able_qty,'editable-cell');
					}
				}		
			},
		});
		//$("#dataGrid").jqGrid("setFrozenColumns");
	}
	
}

function getLotOption(WERKS,PO_NO,PO_ITEM_NO){
	var options="";
	$.ajax({
		url:baseUrl+'common/getLoList',
		type:'post',
		async:false,
		dataType:'json',
		data:{
			WERKS:WERKS,
			PO_NO:PO_NO||'',
			PO_ITEM_NO:PO_ITEM_NO||''
		},
		success:function(resp){
			//alert(resp.msg)
			if(resp.msg=='success'){		
				for(i=0;i<resp.data.length;i++){
	 				if(i != resp.data.length - 1) {
	 					options += resp.data[i].LGORT + ":" +resp.data[i].LGORT + ";";
	 				} else {
	 					options += resp.data[i].LGORT + ":" + resp.data[i].LGORT;
	 				}
				}
				//alert(options)
				console.info(options)
				
			}
		}		
	})
	return options;
}

function getLgortList(WERKS,PO_NO,PO_ITEM_NO,SOBKZ){
	var list=[];
	$.ajax({
		url:baseUrl+'common/getLoList',
		type:'post',
		async:false,
		dataType:'json',
		data:{
			WERKS:WERKS,
			PO_NO:PO_NO,
			PO_ITEM_NO:PO_ITEM_NO,
			SOBKZ:SOBKZ
		},
		success:function(resp){
			if(resp.msg=='success'){	
				list=resp.data;
			}
		}			
	})
	return list;
}


function addMat(){
	var selectedId = $("#dataGrid").jqGrid("getGridParam", "selrow"); 
	var ids = $("#dataGrid").jqGrid('getDataIDs');
	/*console.info(ids)*/
	if(ids.length==0){
		ids=[0]
	}
    var rowid = Math.max.apply(null,ids) + 1;
    var data={id:rowid}
    $("#dataGrid").jqGrid("addRowData", rowid, data, "last"); 
    //alert(rowid)
}

function delMat(id){
	console.info(id)
	$("#dataGrid").delRowData(id)
}

/**
 * 展示危化品保质期数据，审核保质期更新行项目保质期数据
 * @returns
 */
function showDangerMat(rows){
	darows=[];
	$("#dangerMat tbody").html("");
	var selectedRows=[];
	$.each(rows,function(i,rowid){
		var row=$("#dataGrid").jqGrid('getRowData',rowid);
		selectedRows.push(row);
		
		if(row.DANGER_FLAG=='X'){	
			darows.push(row);
		}
	})
	//危化品保质期确认
	if(darows.length>0){
		$.each(darows,function(i,row){
			//alert(row.ID);
			var tr=$("<tr />");
			$("<td />").html(row.WERKS).appendTo(tr);
			$("<td />").html(row.LIFNR).appendTo(tr);
			$("<td />").html(row.MATNR).appendTo(tr);
			$("<td />").html(row.MAKTX).appendTo(tr);
			$("<td />").html("<input id='prod_date_"+i+"' style='width:100%;border:0px;background-color: white;' value='"+row.PRODUCT_DATE +"' onclick=\"WdatePicker({onpicked:function(){changeProdDate("+i+")},dateFmt:'yyyy-MM-dd'})\">").appendTo(tr);
			
			$("<td />").html("<input id='good_dates_"+i+"'style='width:100%;border:0px;background-color: white;' value='"+row.GOOD_DATES +"' onchange='changeGoodDates("+i+")' >").appendTo(tr);
			
			$("<td />").html("<input id='effc_date_"+i+"'  style='width:100%;border:0px;background-color: white;' value='"+(dateAdd(row.PRODUCT_DATE,row.GOOD_DATES)) +"' onclick='WdatePicker({onpicked:function(){changeEffcDate("+i+")},dateFmt:\"yyyy-MM-dd\"})' >").appendTo(tr);
			
			$("#dangerMat tbody").append(tr);	
			
			darows[i].EFFECT_DATE=dateAdd(row.PRODUCT_DATE,row.GOOD_DATES);
			$.each(selectedRows,function(j,s_row){
				if(s_row.ID==row.ID){
					s_row.EFFECT_DATE=dateAdd(row.PRODUCT_DATE,row.GOOD_DATES);
					return ;
				}
			})
			
		})
			
		
		layer.open({
			  type: 1,
			  title:['危化品保质期核对','font-size:18px'],
			  closeBtn: 1,
			  btn:['确认','关闭'],
			  offset:['100px',''],
			  area: '950px',
			  skin: 'layui-bg-green', //没有背景色
			  shadeClose: false,
			  content: '<div id="layer_content">' +$('#dangerMat').html()+'</div>',//div 必须加上，否则无法改变layer中元素的值
			  btn1:function(index){//危化品确认生成危化品批次，并生成其他选中行项目的批次信息，调用后台收货服务
				  /*$.each(selectedRows,function(i,row){
					  $.each(darows,function(j,darow){
						  if(darow.ASNITM==row.ASNITM&&vm.receipt_type=='01'){
							  selectedRows[i]=darow;
							  return;
						  }
					  })
				  })*/
				  layer.close(index);
				  console.info(JSON.stringify(selectedRows))
				  boundInConfirm(selectedRows);
			  },
			  btn2:function(index){
				  layer.close(index)
			  }
			});	
		
	}else{
		boundInConfirm(selectedRows);
	}
	
	
}

//cdate增加(减少)n天
function dateAdd(cdate,n){
	var d = new Date(cdate); 
	d.setDate(d.getDate() + Number(n)); 
	var m = d.getMonth() + 1;
	if (m < 10) {
		m = "0" + m;
	}
	var day = d.getDate();
	if (d.getDate() < 10) {
		day = "0" + day;
	}
	return d.getFullYear() + '-' + m + '-' + day; 
}

function changeProdDate(e){
	var prod_date=$dp.cal.getDateStr("yyyy-MM-dd");	
	var good_dates=$("#layer_content").find("#good_dates_"+e).val();	
	$("#layer_content").find("#effc_date_"+e).val(dateAdd(prod_date,good_dates));
	darows[e].PRODUCT_DATE=prod_date;
	darows[e].EFFECT_DATE=dateAdd(prod_date,good_dates);
	darows[e].GOOD_DATES=good_dates;
}
function changeEffcDate(e){
	var good_dates=$("#layer_content").find("#good_dates_"+e).val();
	var effc_date=$dp.cal.getDateStr("yyyy-MM-dd");		
	$("#layer_content").find("#prod_date_"+e).val(dateAdd(effc_date,0-good_dates));
	darows[e].PRODUCT_DATE=dateAdd(effc_date,0-good_dates);
	darows[e].EFFECT_DATE=effc_date;
	darows[e].GOOD_DATES=good_dates;
}
function changeGoodDates(e){
	var prod_date=$("#layer_content").find("#prod_date_"+e).val();
	var good_dates=$("#layer_content").find("#good_dates_"+e).val();
	$("#layer_content").find("#effc_date_"+e).val(dateAdd(prod_date,good_dates));
	darows[e].PRODUCT_DATE=prod_date;
	darows[e].EFFECT_DATE=dateAdd(prod_date,good_dates);
	darows[e].GOOD_DATES=good_dates;
}

function setWH(){
	var rows=$("#dataGrid").jqGrid('getRowData');
	$.each(rows,function(i,row){
		$("#dataGrid").jqGrid('setCell', i+1, "WH_NUMBER", $("#WH_NUMBER").val(),'editable-cell');//仓库号默认为页面仓库下拉列表的值
	})
}
function setLGORT(){
	var rows=$("#dataGrid").jqGrid('getRowData');
	$.each(rows,function(i,row){
		$("#dataGrid").jqGrid('setCell', i+1, "LGORT", $("#LGORT").val(),'editable-cell');//库位默认为页面库位下拉列表的值
	})
}


function setWerks(){
	var rows=$("#dataGrid").jqGrid('getRowData');
	$.each(rows,function(i,row){
		$("#dataGrid").jqGrid('setCell', i+1, "WERKS", $("#WERKS").val(),'editable-cell');//收货工厂默认为页面工厂下拉列表的值
	})
}

function boundInConfirm(selectedRows){
	if(selectedRows.length<=0){
		return false;
	}
	$("#btnConfirm").html("保存中");	
	$("#btnConfirm").attr("disabled","disabled");
	js.loading();
	/**
	 * 收货确认后台逻辑（质检逻辑，SAP过账逻辑，输出标签列表）
	 */
	$.ajax({
		url:baseUrl+"account/wmsAccountReceiptV/boundIn",
		type:"post",
		async:true,
		dataType:"json",
		data:{
			matList:JSON.stringify(selectedRows),
			allDataList:JSON.stringify($("#dataGrid").jqGrid('getRowData')),
			WERKS:vm.WERKS,
			BUSINESS_NAME:vm.receipt_type,	
			PZ_DATE:vm.PZ_DATE,
			JZ_DATE:vm.JZ_DATE,
		},
		success:function(response){
			if(response.code==500){
				$("#btnConfirm").html("确认");
				$("#btnConfirm").removeAttr("disabled");
				js.closeLoading();
				js.showErrorMessage(response.msg);
				return false;
			}
			
			$('#dataGrid').jqGrid("clearGridData");
			//js.showMessage(response.msg+"&nbsp;&nbsp;<a href='#' onclick='showLablePrint("+response.msg+response.lableList+")'>来料标签打印"+"</a>",null,null,1000*50);
			js.showMessage(response.msg,null,null,1000*50);
			
	       	$("#btnConfirm").html("确认");
			$("#btnConfirm").removeAttr("disabled");
			js.closeLoading();
			if(vm.receipt_type!='23'){
				ajaxQuery();
			}
		}
	})
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
			//console.info(Number(row.ID)+"/"+rowid+"/"+cellvalue)
			if(Number(row.ID)>=Number(rowid)&&cellvalue){
				$("#dataGrid").jqGrid('setCell',Number(row.ID), e, cellvalue,'editable-cell');
			}
			if(Number(row.ROWID)>=Number(rowid)&&cellvalue){
				$("#dataGrid").jqGrid('setCell',Number(row.ROWID), e, cellvalue,'editable-cell');
			}
		})
	}
	
}

