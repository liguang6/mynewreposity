var mydata = [];
var lastrow,lastcell,lastcellname;  
var vm = new Vue({
	el : '#rrapp',
	data : {
		businessList:[],
		formUrl:baseUrl+"account/wmsAccountOutV/listMOVMat",
		WERKS:"",
		whList:[],
		WH_NUMBER:'',
		BUSINESS_NAME:'',
		OUT_NO:'',
		PZ_DATE:getCurDate(),//凭证日期
		JZ_DATE:getCurDate(),//记账日期
		lgortList:[],
		CUSTOMER:'',
		CUSTOMER_ID:'',
		CUSTOMER_NAME:'',
		WERKS_TO:'',
		LGORT_TO:'',
		lgortList_To:[],
		HX_FLAG:true,
	},
	watch:{
		BUSINESS_NAME:{
			handler:function(newVal,oldVal){
				console.info(newVal)
				$("#dataGrid").jqGrid("clearGridData");
				mydata=[];
				//$("#dataGrid").jqGrid('setGridParam',{datatype:'local'}).trigger('reloadGrid'); 
				vm.OUT_NO="";
				//55 生产订单领料(V261)
				if(newVal=='55'){
					vm.formUrl=baseUrl+"account/wmsAccountOutV/listMOVMat";
					fun_tab55();
				}
				if(newVal=='56'){
					vm.formUrl=baseUrl+"account/wmsAccountOutV/getSTOVMatList";
					fun_tab56();
				}
				if(newVal=='57'){
					fun_tab57();
				}
			}
		},
		WERKS:{
			handler:function(newVal,oldVal){
					console.info(newVal);
					 vm.whList = [];
					 vm.lgortList = [];
			         this.$nextTick(function(){		        	 
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
		        		  //generateLgortTable();
					})
					
					var setting=getPlantSetting(vm.WERKS,wm.WH_NUMBER);
			         vm.HX_FLAG=setting.HX_FLAG=='X'?true:false;
				}
		},
	},
	methods : {
		refresh : function() {
			$('#dataGrid').trigger('reloadGrid');
		},
		query: function () {
			//55 生产订单领料(V261)
			if(vm.BUSINESS_NAME=='55'){
				if(vm.OUT_NO.trim().length==0){
					js.showErrorMessage("请输入SAP生产订单号！");
					return false;
				}
				//$("#LGORT_NAME").val($("#LGORT").find('option:selected').attr("logrt_name"));
			}
			vm.$nextTick(function(){//数据渲染后调用
				ajaxQuery();
			})
	
		},
		post: function(){
			var selectedRows = [];
			var flag=true;
			if(vm.BUSINESS_NAME=='57'){
				if(($("#WERKS_TO").val()==undefined||$("#WERKS_TO").val()=="")){
					js.alert("请填写接收工厂！")
					flag=false;
					return false;
				}
				if(($("#LGORT_TO").val()==undefined||$("#LGORT_TO").val()=="")){
					js.alert("请填写接收库位！")
					flag=false;
					return false;
				}
			}
			
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			var rows = $('#dataGrid').getGridParam("selarrrow");
			
			if(rows==undefined||rows.length<=0){
				//js.showMessage("请选择需要保存的行项目数据！");	
				alert("请选择一条或多条记录！");
				flag=false;
				return false;
			}
			//必填字段判断，未填不能选中
			$.each(rows,function(i,rowid){
				var row = $("#dataGrid").jqGrid("getRowData",rowid);
				console.info(JSON.stringify(row))
				if(vm.BUSINESS_NAME=='55'){
					if((row.LGORT==undefined||row.LGORT=="")&&row.VENDOR_FLAG!='X'){
						js.showErrorMessage("第"+rowid+"行必填字段（库位）未填写完整！");	
						flag=false;
						return false;
					}
				}
				if(vm.BUSINESS_NAME=='57'){
					row.LGORT=vm.LGORT_TO;
					row.WERKS=vm.WERKS_TO;
					if((row.MATNR==undefined||row.MATNR=="")){
						flag=false;
						return false;
					}
				}
				
				if(Number(row.QTY) <=0){
					js.showErrorMessage("第"+rowid+"行过账数量必须大于0！");	
					flag=false;
					return false;
				}
				selectedRows.push(row);
			});
			if(flag==false){
				return false;
			}
			//var rowData = $("#dataGrid").jqGrid('getRowData'); //表格所有行数据
			$("#btnConfirm").html("过账中...");	
			$("#btnConfirm").attr("disabled","disabled");
			$("#btnSearchData").attr("disabled","disabled");	
			js.loading();
			
			/**
			 * 虚发确认后台逻辑（质检逻辑，SAP过账逻辑，输出标签列表）
			 */
			$.ajax({
				url:baseUrl+"account/wmsAccountOutV/postGI",
				type:"post",
				async:false,
				dataType:"json",
				data:{
					matList:JSON.stringify(selectedRows),
					WERKS:vm.WERKS,
					WH_NUMBER:vm.WH_NUMBER,
					BUSINESS_NAME:vm.BUSINESS_NAME,	
					PZ_DATE:vm.PZ_DATE,
					JZ_DATE:vm.JZ_DATE,
				},
				success:function(response){
					if(response.code==500){
						$("#btnConfirm").html("过账发货");	
						$("#btnConfirm").removeAttr("disabled");
						$("#btnSearchData").removeAttr("disabled");
						js.closeLoading();
						js.showErrorMessage(response.msg);
						return false;
					}
					
					$('#dataGrid').jqGrid("clearGridData");
					js.showMessage(response.msg+"&nbsp;&nbsp;<a href='#' onclick='showLablePrint("+response.msg+response.lableList+")'>来料标签打印"+"</a>",null,null,1000*50);
					
					$("#btnConfirm").html("过账发货");	
					$("#btnConfirm").removeAttr("disabled");
					$("#btnSearchData").removeAttr("disabled");
					js.closeLoading();
					
					if(vm.BUSINESS_NAME=='55'||vm.BUSINESS_NAME=='56'){
						ajaxQuery();
					}
					if(vm.BUSINESS_NAME=='57'){
						fun_tab57();
					}
				}
			});
			js.closeLoading();
			$("#btnConfirm").html("过账发货");	
			$("#btnConfirm").removeAttr("disabled");
			$("#btnSearchData").removeAttr("disabled");
		}
	},
	created:function(){
		this.businessList=getBusinessList("09","ACCOUNT_OUT_V");
		if(this.businessList.length>0){
			this.BUSINESS_NAME=this.businessList[0].CODE;
		}else{
			js.showErrorMessage("工厂未配置发货（V）类型的业务类型！");
		}
		
		this.WERKS=$("#WERKS").find("option").first().val();
		this.WERKS_TO='';
	}
});

$(function(){
	$("#OUT_NO").focus();
	fun_tab55();
	
	$(document).on("paste","input[name='MATNR']",function(e){
		setTimeout(function(){
			var rowid=Number($(e.target).attr("rowid"));
			var row_c =  $("#dataGrid").jqGrid('getRowData', rowid);
			row_c.WERKS=vm.WERKS;
			row_c.WH_NUMBER=vm.WH_NUMBER;
			row_c.MATNR='';
			row_c.MAKTX='';
			row_c.LGORT='';
			row_c.SOBKZ='Z';
			row_c.MEINS="";
			row_c.QTY="";
			row_c.QTY_ABLE="0";
			row_c.VIRTUAL_QTY="";
			row_c.STOCK_QTY="";
			row_c.ITEM_TEXT="";
			row_c.HEADER_TXT="";
			 
			 var copy_text=$(e.target).val();		 
			 var dist_list=copy_text.split(" ");
			 //根据物料号列表查询物料信息
			 var ctmap={};
			 ctmap.WERKS=vm.WERKS;
			 ctmap.WH_NUMBER=vm.WH_NUMBER;
			 ctmap.BUSINESS_NAME=vm.BUSINESS_NAME;
			 ctmap.JZ_DATE=vm.JZ_DATE;
			 ctmap.matnr_list=dist_list.join(",");
			 var mat_list=getMatInfo(ctmap);
			 
			 $.each(mat_list,function(i,mat){
				 $("#dataGrid").jqGrid('setCell',lastrow+i, "MATNR", mat_list[i]['MATNR'],'editable-cell');
				 $('#dataGrid').jqGrid("saveCell", lastrow+i, 2);
				 $("#dataGrid").jqGrid('setCell',lastrow+i, "MAKTX", mat_list[i]['MAKTX'],'not-editable-cell');
				 $("#dataGrid").jqGrid('setCell',lastrow+i, "MEINS", mat_list[i]['MEINS'],'not-editable-cell');
				 $("#dataGrid").jqGrid('setCell',lastrow+i, "ITEM_TEXT", mat_list[i]['ITEM_TEXT'],'editable-cell');
				 $("#dataGrid").jqGrid('setCell',lastrow+i, "QTY", mat_list[i].QTY_ABLE,'editable-cell');
				 $("#dataGrid").jqGrid('setCell',lastrow+i, "QTY_ABLE", mat_list[i].QTY_ABLE,'not-editable-cell');
				 $("#dataGrid").jqGrid('setCell',lastrow+i, "VIRTUAL_QTY", mat_list[i].VIRTUAL_QTY,'not-editable-cell');
				 $("#dataGrid").jqGrid('setCell',lastrow+i, "STOCK_QTY", mat_list[i].STOCK_QTY,'not-editable-cell');
				 row_c.HEADER_TXT=mat_list[i]['HEADER_TXT'];
				 row_c.MEINS=mat_list[i]['MEINS'];
				 row_c.ITEM_TEXT=mat_list[i]['ITEM_TEXT'];
				 row_c.MATNR=mat_list[i]['MATNR'];
				 row_c.MAKTX=mat_list[i]['MAKTX'];
				 row_c.QTY_ABLE=mat_list[i]['QTY_ABLE'];
				 row_c =  $("#dataGrid").jqGrid('getRowData',rowid+i);
				 if(i>0&&row_c.ID==undefined){
					 addMat(mat);	
				 }
			 });
		},10);
		
	})
});


function ajaxQuery(){
	
	$("#btnSearchData").val("查询中...");
	$("#btnSearchData").attr("disabled","disabled");	
	
	$.ajax({
		url:vm.formUrl,
		dataType : "json",
		type : "post",
		data : {
			"WERKS":vm.WERKS,
			"WH_NUMBER":vm.WH_NUMBER,
			"BUSINESS_NAME":vm.BUSINESS_NAME,
			"OUT_NO":vm.OUT_NO,
			"PZ_DATE":vm.PZ_DATE,
			"JZ_DATE":vm.JZ_DATE,
		},
		async: true,
		success: function (response) { 
			$("#dataGrid").jqGrid("clearGridData", true);
			mydata.length=0;
			if(response.code == "0"){
				mydata = response.result;
				$("#dataGrid").jqGrid('GridUnload');	
				if(mydata.length=="")
					alert("未查询到符合条件的数据！");
				if(vm.BUSINESS_NAME=='55'){
					fun_tab55();
				}
				if(vm.BUSINESS_NAME=='56'){
					fun_tab56();
				}
				if(vm.BUSINESS_NAME=='57'){
					fun_tab57();
				}
			}else{
				alert(response.msg)
			}
			$("#btnSearchData").val("查询");
			$("#btnSearchData").removeAttr("disabled");
		}
	});

}


function fun_tab55(){
	var data = $('#dataGrid').data("dataGrid");

	if(data){
		$("#dataGrid").jqGrid('GridUnload');
	}
	$("#dataGrid").dataGrid({
		datatype: "local",
		data: mydata,
        colModel: [
			{label: '序号', name: 'ID', index: 'ID', align:"left",width: 45, key: true ,sortable:false,frozen: true,},
            {label: '单据号', name: 'AUFNR',index:"AUFNR", width: "90",align:"center",sortable:false,frozen: true,
            },
            {label: '行项目', name: 'POSNR',index:"POSNR", width: "60",align:"center",sortable:false,frozen: true},
            {label: '料号', name: 'MATNR',index:"MATNR", width: "90",align:"center",sortable:true,frozen: true},
            {label: '物料描述', name: 'MAKTX',index:"MAKTX", width: "280",align:"center",sortable:false},
            { label: '<span style="color:blue">库位 </span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'LGORT\')" title="向下填充" style="color:green" aria-hidden="true"></i> ',
            	name: 'LGORT',align:"center", index: 'LGORT', width: "120" ,sortable:false,editable:true,edittype:'text',editoptions:{
            		readonly:true,dataEvents:[{type:'focus',fn:function(e){
            			showLOGRTSelect(e);
            		}}]
            	}}, 	
            {label: '订单需求', name: 'BDMNG',index:"BDMNG", width: "80",align:"center",sortable:true},
            {label: '已发数量', name: 'QTY_XF',index:"QTY_XF", width: "80",align:"center",sortable:true},
            {label: '可发数量', name: 'QTY_ABLE',index:"QTY_ABLE", width: "80",align:"center",sortable:true},
            {label: '<span style="color:red">*</span><span style="color:blue"><b>过账数量</b></span>', name: 'QTY',index:"QTY", width: "90",align:"center",sortable:false,editable:true,
            	formatter:'number',editrules:{number:true},
            	formatoptions:{decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}
            },
            {label: '单位', name: 'MEINS',index:"MEINS", width: "50",align:"center",sortable:false}, 
            {label: '库存（V）', name: 'VIRTUAL_QTY',index:"VIRTUAL_QTY", width: "90",align:"center",sortable:false,},
            {label: '库存（非限制）', name: 'STOCK_QTY',index:"STOCK_QTY", width: "100",align:"center",sortable:false,},
         
			{label: '<span style="color:blue">行文本 </span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'ITEM_TEXT\')" title="向下填充" style="color:green" aria-hidden="true"></i> ', name: 'ITEM_TEXT',index:"ITEM_TEXT", width: "250",sortable:false,align:"center",
            	editable:true},			  
            {label: '预留号', name: 'RSNUM',index:"RSNUM", width: "100",align:"center",sortable:true}, 
            {label: '预留行项目', name: 'RSPOS',index:"RSPOS", width: "90",align:"center",sortable:false}, 
            {label:'HEADER_TXT',name:'HEADER_TXT',hidden:true},
            {label:'XF261',name:'XF261',hidden:true},
            {label:'XF262',name:'XF262',hidden:true},
            {label:'OVERSTEP_HX_FLAG',name:'OVERSTEP_HX_FLAG',hidden:true},
            {label:'QTY_ABLE',name:'QTY_ABLE',hidden:true},
            {label:'XCHPF',name:'XCHPF',hidden:true},/*物料在SAP系统是否启用批次管理标识 空 否 X是 */
        ],
		formatCell:function(rowid, cellname, value, iRow, iCol){
			console.info(cellname)
		/*	if(cellname=='LGORT'){
	            $('#dataGrid').jqGrid('setColProp', 'LGORT', { editoptions: {value:getLotOption(vm.WERKS)}
	            });
			}		*/
			
		},
	/*	onCellSelect:function(rowid,iCol,cellcontent,e){
			showLOGRTSelect(rowid,iCol);
		},*/
	    beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
		},
		beforeSelectRow : function(rowid, e) { // 点击复选框才选中行
			var $myGrid = $(this), i = $.jgrid.getCellIndex($(e.target).closest('td')[0]), cm = $myGrid.jqGrid('getGridParam', 'colModel');
			if (cm[i].name == 'cb') {
				$('#dataGrid').jqGrid('setSelection', rowid);
			}
			return (cm[i].name == 'cb');
		},
		onSelectRow:function(rowid,status,e){
			console.log("-->rowid : " + rowid);
		},
		afterSaveCell:function(rowid, cellname, value, iRow, iCol){
			var row =  $("#dataGrid").jqGrid('getRowData', rowid);
			if(cellname=='QTY'){
				if("" == value){
					alert("过账数量不能为空！");
					$("#dataGrid").jqGrid('setCell', rowid, cellname, row.QTY_ABLE,'editable-cell');
					return false;
				}
				if(parseFloat(value) > parseFloat(row.QTY_ABLE)){
					alert("过账数量不能大于可过账数量（"+row.QTY_ABLE+"）！");
					$("#dataGrid").jqGrid('setCell', rowid, cellname, row.QTY_ABLE,'editable-cell');
					return false;
				}
			}
			//$('#dataGrid').jqGrid('setGridParam',{data: mydata}).trigger( 'reloadGrid' );
		},
		shrinkToFit: false,
        width:1900,
        showRownum:false,
        cellEdit:true,
        cellurl:'#',
		cellsubmit:'clientArray',
        showCheckbox:true
    });
	
	$("#dataGrid").jqGrid("setFrozenColumns");
}

function fun_tab56(){
	var data = $('#dataGrid').data("dataGrid");

	if(data){
		$("#dataGrid").jqGrid('GridUnload');
	}
	$("#dataGrid").dataGrid({
		datatype: "local",
		data: mydata,
        colModel: [
			{label: '序号', name: 'ID', index: 'ID', align:"left",width: 45, key: true ,sortable:false,frozen: true,},
            {label: '单据号', name: 'VBELN',index:"VBELN", width: "90",align:"center",sortable:false,frozen: true,
            },
            {label: '行项目', name: 'POSNR',index:"POSNR", width: "60",align:"center",sortable:false,frozen: true},
            {label: '料号', name: 'MATNR',index:"MATNR", width: "90",align:"center",sortable:true,frozen: true},
            {label: '物料描述', name: 'MAKTX',index:"MAKTX", width: "280",align:"center",sortable:false},
            { label: '<span style="color:blue">库位 </span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'LGORT\')" title="向下填充" style="color:green" aria-hidden="true"></i> ',
            	name: 'LGORT',align:"center", index: 'LGORT', width: "120" ,sortable:false,editable:true,edittype:'text',editoptions:{
            		readonly:true,dataEvents:[{type:'focus',fn:function(e){
            			showLOGRTSelect(e);
            		}}]
            	}}, 	
            {label: '交货数量', name: 'JH_QTY',index:"JH_QTY", width: "80",align:"center",sortable:true},
            {label: '<span style="color:red">*</span><span style="color:blue"><b>过账数量</b></span>', name: 'QTY',index:"QTY", width: "90",align:"center",sortable:false,editable:true,
            	formatter:'number',editrules:{number:true},
            	formatoptions:{decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}
            },
            {label: '单位', name: 'MEINS',index:"MEINS", width: "50",align:"center",sortable:false}, 
            {label: '库存（V）', name: 'VIRTUAL_QTY',index:"VIRTUAL_QTY", width: "90",align:"center",sortable:false,},
            {label: '库存（非限制）', name: 'STOCK_QTY',index:"STOCK_QTY", width: "100",align:"center",sortable:false,},        
			{label: '<span style="color:blue">行文本 </span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'ITEM_TEXT\')" title="向下填充" style="color:green" aria-hidden="true"></i> ', name: 'ITEM_TEXT',index:"ITEM_TEXT", width: "250",sortable:false,align:"center",
            	editable:true},			  
            {label: '订单号', name: 'PO_NO',index:"PO_NO", width: "100",align:"center",sortable:true}, 
            {label: '行项目', name: 'PO_ITEM_NO',index:"PO_ITEM_NO", width: "90",align:"center",sortable:false}, 
            {label:'HEADER_TXT',name:'HEADER_TXT',hidden:true},
            {label:'OVERSTEP_HX_FLAG',name:'OVERSTEP_HX_FLAG',hidden:true},
            {label:'QTY_ABLE',name:'QTY_ABLE',hidden:true},
            {label:'XCHPF',name:'XCHPF',hidden:true},/*物料在SAP系统是否启用批次管理标识 空 否 X是 */
        ],
		formatCell:function(rowid, cellname, value, iRow, iCol){
			console.info(cellname)
		/*	if(cellname=='LGORT'){
	            $('#dataGrid').jqGrid('setColProp', 'LGORT', { editoptions: {value:getLotOption(vm.WERKS)}
	            });
			}		*/
			
		},
	/*	onCellSelect:function(rowid,iCol,cellcontent,e){
			showLOGRTSelect(rowid,iCol);
		},*/
	    beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
		},
		beforeSelectRow : function(rowid, e) { // 点击复选框才选中行
			var $myGrid = $(this), i = $.jgrid.getCellIndex($(e.target).closest('td')[0]), cm = $myGrid.jqGrid('getGridParam', 'colModel');
			if (cm[i].name == 'cb') {
				$('#dataGrid').jqGrid('setSelection', rowid);
			}
			return (cm[i].name == 'cb');
		},
		onSelectRow:function(rowid,status,e){
			console.log("-->rowid : " + rowid);
		},
		afterSaveCell:function(rowid, cellname, value, iRow, iCol){
			var row =  $("#dataGrid").jqGrid('getRowData', rowid);
			if(cellname=='QTY'){
				if("" == value){
					alert("过账数量不能为空！");
					$("#dataGrid").jqGrid('setCell', rowid, cellname, row.QTY_ABLE,'editable-cell');
					return false;
				}
				if(parseFloat(value) > parseFloat(row.QTY_ABLE)){
					alert("过账数量不能大于可过账数量（"+row.QTY_ABLE+"）！");
					$("#dataGrid").jqGrid('setCell', rowid, cellname, row.QTY_ABLE,'editable-cell');
					return false;
				}
			}
			//$('#dataGrid').jqGrid('setGridParam',{data: mydata}).trigger( 'reloadGrid' );
		},
		shrinkToFit: false,
        width:1900,
        showRownum:false,
        cellEdit:true,
        cellurl:'#',
		cellsubmit:'clientArray',
        showCheckbox:true
    });
	
	$("#dataGrid").jqGrid("setFrozenColumns");
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

function fillDown(e){
	$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
	var ids=$("#dataGrid").getGridParam("selarrrow");
	var rowid=lastrow;
	var rows=$("#dataGrid").jqGrid('getRowData');
	var last_row=$("#dataGrid").jqGrid('getRowData',rowid);
	var cellvalue=last_row[lastcellname];
	if(lastcellname==e){
		$.each(rows,function(i,row){
			if(Number(row.ID)>=Number(rowid)&&cellvalue){
				$("#dataGrid").jqGrid('setCell',Number(row.ID), e, cellvalue,'editable-cell');
			}
		})
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

function showLOGRTSelect(e){
	var default_list=$(e.target).val();
	//alert(default_list)
	// 库位选择框
	js.layer.open({
				type : 2,
				title : "库位",
				area : [ "40%", "60%" ],
				content : baseUrl + "wms/out/stock_mutiselect.html",
				btn : [ "确定", "取消" ],
				success : function(layero, index_) {
					// 加载完成后，初始化
					var werks = vm.WERKS;
					var whNumber = vm.WH_NUMBER;
					var win = layero.find('iframe')[0].contentWindow;
					win.vm.init(werks, whNumber);
					win.vm.default_list=default_list;
				},
				yes : function(index_, layero) {
					// 提交，调用生产订单页面的提交方法。
					var win = layero.find('iframe')[0].contentWindow;
					var lgortList = win.vm.getSelectedItems();
					var lgortItems = lgortList.map(function(val) {
								return val.LGORT;
							});
					var lgortList = lgortItems.join(",");
					if(lgortList.length > 0 && lgortList.charAt(lgortList.length-1) === ','){
						lgortList = lgortList.substring(0,lgortList.length-1);
					}
					//$("#dataGrid").jqGrid("setCell",rowid,'LGORT',lgortList,'editable-cell')
					$(e.target).val(lgortList)
					win.vm.close();
				},
				no : function(index, layero) {
				}
			});
}

function generateLgortTable(){
	var data = $('#lgortTable').data("dataGrid");

	if(data){
		$("#lgortTable").jqGrid('GridUnload');
	}
	$("#lgortTable").dataGrid({
		datatype: "local",
		data: vm.lgortList,
        colModel: [
            {label: '库位', name: 'LGORT',index:"LGORT",align:"center",sortable:false,},
        ],
        shrinkToFit: false,
        width:250,
        showRownum:false,
        cellEdit:true,
        autoWidth:true,
        cellurl:'#',
		cellsubmit:'clientArray',
        showCheckbox:true,
        id:'LGORT'
	})
}

function fun_tab57(){
	var data = $('#dataGrid').data("dataGrid");

	if(data){
		$("#dataGrid").jqGrid('GridUnload');
	}
	$("#dataGrid").dataGrid({
		datatype: "local",
		data: mydata,
        colModel: [
        	{label:'ID',name:'ID', index: 'ID',key: true ,hidden:true,formatter:function(val, obj, row, act){
				return obj.rowId
			}},
        	{ label: '<i class=\"fa fa-plus\" style=\"color:blue\" onclick=\"addMat()\" ></i>', name: 'EMPTY_COL', index: '', align:"center",width: 30, sortable:false,formatter:function(val, obj, row, act){
   				var actions = [];
   				actions.push('<a href="#" onClick="delMat('+obj.rowId+')" title="删除" data-confirm="确认要删除该行数据吗？"><i class="fa fa-times" style=\"color:red\"></i></a>');
   				
   				return actions.join(",");
   			},unformat:function(cellvalue, options, rowObject){
				return "";
			}}, 
            {label: '料号', name: 'MATNR',index:"MATNR", width: 107,align:"center",sortable:true,frozen: true,sortable:false,editable:true,edittype:'text',},
            {label: '物料描述', name: 'MAKTX',index:"MAKTX", width: 280,align:"center",sortable:false},
            { label: '<span style="color:blue">库位 </span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'F_LGORT\')" title="向下填充" style="color:green" aria-hidden="true"></i> ',
            	name: 'F_LGORT',align:"center", index: 'F_LGORT', width: 120 ,sortable:false,editable:true,edittype:'text',editoptions:{
            		readonly:true,dataEvents:[{type:'focus',fn:function(e){
            			showLOGRTSelect(e);
            		}}]
            	}}, 	
            {label: '<span style="color:red">*</span><span style="color:blue"><b>过账数量</b></span>', name: 'QTY',index:"QTY", width: 90,align:"center",sortable:false,editable:true,
            	formatter:'number',editrules:{number:true},
            	formatoptions:{decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}
            },
            {label: '单位', name: 'MEINS',index:"MEINS", width: 55,align:"center",sortable:false}, 
            {label: '库存（V）', name: 'VIRTUAL_QTY',index:"VIRTUAL_QTY", width: 90,align:"center",sortable:false,},
            {label: '库存（非限制）', name: 'STOCK_QTY',index:"STOCK_QTY", width: 100,align:"center",sortable:false,},
         
			{label: '<span style="color:blue">行文本 </span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'ITEM_TEXT\')" title="向下填充" style="color:green" aria-hidden="true"></i> ', 
            	name: 'ITEM_TEXT',index:"ITEM_TEXT", width: 260,sortable:false,align:"center",
            	editable:true},			  
           /* {label: '预留号', name: 'RSNUM',index:"RSNUM", width: "100",align:"center",sortable:true}, 
            {label: '预留行项目', name: 'RSPOS',index:"RSPOS", width: "90",align:"center",sortable:false}, */
            {label:'HEADER_TXT',name:'HEADER_TXT',hidden:true},
            {label:'F_WERKS',name:'F_WERKS',hidden:true},
            {label:'F_WH_NUMBER',name:'F_WH_NUMBER',hidden:true},
            {label:'XF261',name:'XF261',hidden:true},
            {label:'XF262',name:'XF262',hidden:true},
            {label:'OVERSTEP_HX_FLAG',name:'OVERSTEP_HX_FLAG',hidden:true},
            {label:'QTY_ABLE',name:'QTY_ABLE',hidden:true},
            {label:'WERKS',name:'WERKS',hidden:true},
            {label:'LGORT',name:'LGORT',hidden:true},
            {label:'XCHPF',name:'XCHPF',hidden:true},/*物料在SAP系统是否启用批次管理标识 空 否 X是 */
        ],
		formatCell:function(rowid, cellname, value, iRow, iCol){
			
		},
	/*	onCellSelect:function(rowid,iCol,cellcontent,e){
			showLOGRTSelect(rowid,iCol);
		},*/
	    beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
		},
		beforeSelectRow : function(rowid, e) { // 点击复选框才选中行
			var $myGrid = $(this), i = $.jgrid.getCellIndex($(e.target).closest('td')[0]), cm = $myGrid.jqGrid('getGridParam', 'colModel');
			if (cm[i].name == 'cb') {
				$('#dataGrid').jqGrid('setSelection', rowid);
			}
			return (cm[i].name == 'cb');
		},
/*		onSelectRow:function(rowid,status,e){
			console.log("-->rowid : " + rowid);
		},*/
		afterSaveCell:function(rowid, cellname, value, iRow, iCol){
			var row =  $("#dataGrid").jqGrid('getRowData', rowid);
			if(cellname=='QTY'){
				if("" == value){
					alert("过账数量不能为空！");
					$("#dataGrid").jqGrid('setCell', rowid, cellname, row.QTY_ABLE,'editable-cell');
					return false;
				}
				if(parseFloat(value) > parseFloat(row.QTY_ABLE)){
					alert("过账数量不能大于可过账数量（"+row.QTY_ABLE+"）！");
					$("#dataGrid").jqGrid('setCell', rowid, cellname, row.QTY_ABLE,'editable-cell');
					return false;
				}
			}
			//$('#dataGrid').jqGrid('setGridParam',{data: mydata}).trigger( 'reloadGrid' );
		},
		shrinkToFit: false,
        showRownum:false,
        autowidth:true,
        cellEdit:true,
        cellurl:'#',
		cellsubmit:'clientArray',
        showCheckbox:true
    });
	
	$("#dataGrid").jqGrid("setFrozenColumns");
}

function addMat(mat){
	/**
	 * 303调拨，先判断工厂是否启用核销业务，没有启用，提示‘XXx工厂未启用该类业务
	 */
	if(!vm.HX_FLAG){
		js.showErrorMessage("工厂"+vm.WERKS+"未启用该业务!");
		return false;
	}
	
	$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
	var selectedId = $("#dataGrid").jqGrid("getGridParam", "selrow"); 
	var ids = $("#dataGrid").jqGrid('getDataIDs');

	console.info(ids)
	if(ids.length==0){
		ids=[0]
	}
    var rowid = Math.max.apply(null,ids) + 1;

    var data={};
    data.id=rowid;
    data.F_WERKS=vm.WERKS;
    data.F_WH_NUMBER=vm.WH_NUMBER;
    data.MATNR='';
    data.MAKTX='';
    data.F_LGORT='';
    data.SOBKZ='Z';
    data.MEINS="";
    data.QTY="";
    data.QTY_ABLE="0";
	data.VIRTUAL_QTY="";
	data.STOCK_QTY="";
	data.ITEM_TEXT="";
	data.HEADER_TXT="";

    if(mat){
    	data.MAKTX=mat.MAKTX;
    	data.MEINS=mat.MEINS;
    	data.HEADER_TXT=mat.HEADER_TXT;
    	data.ITEM_TEXT=mat.ITEM_TEXT;
    	data.QTY=mat.VIRTUAL_QTY;
    	data.MATNR=mat.MATNR;
    	data.VIRTUAL_QTY=mat.VIRTUAL_QTY;
    	data.STOCK_QTY=mat.STOCK_QTY;
    	data.QTY_ABLE=mat.QTY_ABLE;
    }
    $("#dataGrid").jqGrid("addRowData", rowid, data, "last"); 
    
}

function delMat(id){
	//console.info(id)
	$("#dataGrid").delRowData(id)
}

function getPlantSetting(WERKS,WH_NUMBER){
	var list=[];
	$.ajax({
		url:baseUrl+'common/getPlantSetting',
		type:'post',
		async:false,
		dataType:'json',
		data:{
			WERKS:WERKS,
			WH_NUMBER: WH_NUMBER
		},
		success:function(resp){
			if(resp.msg=='success'){	
				list=resp.data;
			}
		}			
	})
	return list;
}

function getMatInfo(ctmap){
	var list=[];
	$.ajax({
		url:baseUrl+'account/wmsAccountOutV/getMatInfo',
		type:'post',
		async:false,
		dataType:'json',
		data:ctmap,
		success:function(resp){
			if(resp.msg=='success'){	
				list=resp.matList;
			}
			if(resp.error_msg){
				js.showErrorMessage(resp.error_msg)
			}
		}			
	})
	return list;
}

function customerFuzzy(){
	vm.CUSTOMER_NAME="";
	getCustomerSelect('#CUSTOMER' ,'#CUSTOMER_ID',function(o){vm.CUSTOMER=o.kunnr;vm.CUSTOMER_NAME=o.name1;})
}