elData = {}; 
var vm = new Vue({
    el:'#rrapp',
    data:{
		saveFlag:true,
		wh_list:[],
		werks:'',
		whNumber:'',
		type:'00',
		BARCODE_FLAG: '0',
		showList:false
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
					vm.wh_list=response.data;
					vm.whNumber=response.data[0]['WH_NUMBER'];
				}
			});	
		  }
		},
		whNumber: {
			handler:function(newVal,oldVal){
				var setting=getPlantSetting(newVal);
				console.log("setting.BARCODE_FLAG",setting.BARCODE_FLAG+"-"+vm.type);
				if(setting.BARCODE_FLAG =='X'){
					vm.BARCODE_FLAG = "X";
					// 库存修改  如果仓库启用条码管理，需要修改标签数据才能保存，所以显示”保存“按钮
					if(vm.type=='01'){
						vm.showList=true;
					}
				}else{
					vm.showList=false;
				}
			}
		}
	},
	created: function(){
		this.werks=$("#werks").find("option").first().val();
	},
	methods: {
		save:function(){
		   saveUpload();
		},
		typeChange:function(){
			vm.type=$("#type").val();
			if(vm.type=='00'){
				// 库存新增
				vm.showList=false;
				$("#template").attr("href",baseUrl+"statics/excel/import-wms-stock-add.xlsx");
				$("#uploadForm").attr("href",baseUrl+"kn/stockModify/previewExcel");
				$("#template").html("库存新增导入模板");
				showAddTable();
			}
			if(vm.type=='01'){
				// 库存修改
				if(vm.BARCODE_FLAG =='X'){
					vm.showList=true;
				}
				$("#template").attr("href",baseUrl+"statics/excel/import-wms-stock-modify.xlsx");
				$("#uploadForm").attr("href",baseUrl+"kn/stockModify/previewModifyExcel");
				showModifyTable();
				$("#template").html("库存修改导入模板");
			}
			if(vm.type=='02'){
				// 条码新增库存
				vm.showList=false;
				$("#template").attr("href",baseUrl+"statics/excel/import-wms-stock-label.xlsx");
				$("#uploadForm").attr("href",baseUrl+"kn/stockModify/previewLabelExcel");
				showLabelTable();
				$("#template").html("条码新增库存导入模板");
			}
		},
		exp:function(){
			$("#entityList").val(JSON.stringify(excelData));
			$("#exportForm").submit();
		}
	}
});
$(function () {
	// 显示库存新增table
	showAddTable();

});
$("#uploadForm").validate({
	submitHandler:function(e){
		if($('#type').val()=='00'){
			showAddTable();
		}
		if($('#type').val()=='01'){
			showModifyTable();
		}
		if($('#type').val()=='02'){
			showLabelTable();
		}
	}
});

function getPlantSetting(WH_NUMBER){
	var list=[];
	$.ajax({
		url:baseUrl+'common/getPlantSetting',
		type:'post',
		async:false,
		dataType:'json',
		data:{
			WH_NUMBER:WH_NUMBER
		},
		success:function(resp){
			if(resp.msg=='success'){	
				list=resp.data;
			}
		}			
	})
	return list;
}

function showAddTable(){
	jQuery('#dataGrid').GridUnload();  
	var form = new FormData(document.getElementById("uploadForm"));
	form.append("barcodeFlag",vm.BARCODE_FLAG);
	form.append("showList",vm.showList);
	console.log("form",form);
	js.loading("正在导入,请稍候...");
	$("#btnImport").val("正在导入...");	
	$("#btnImport").attr("disabled","disabled");
	$.ajax({
		url:baseUrl + "kn/stockModify/previewExcel",
		data:form,
		type:"post",
		contentType:false,
		processData:false,
		success:function(response){
			js.closeLoading();
			//加载jqgrid
			$("#dataGrid").dataGrid({
			 	datatype: 'local', 
				colNames:['公司','工厂','仓库号','库位','物料号','物料描述','供应商代码','单位','批次','生产日期','库存数量','每箱数量','储位','储位名称','库存类型','非限制/冻结','备注','销售订单','销售订单行项目','校验提示信息','行号'],
				colModel:[
				  {name:"COMPANY",index:'COMPANY',align:'center',width:"60"},
		          {name:"WERKS",index:'WERKS',align:'center',width:"50"},
		          {name:"WH_NUMBER",index:'WH_NUMBER',align:'center',width:"60"},
		          {name:"LGORT",index:'LGORT',align:'center',width:"50"},
		          {name:"MATNR",index:'MATNR',align:'center',width:"100"},
		          {name:"MAKTX",index:'MAKTX',align:'center',width:"160"},
		          {name:"LIFNR",index:'LIFNR',align:'center',width:"80"},
		          {name:"MEINS",index:'MEINS',align:'center',width:"50"},
		          {name:"BATCH",index:'BATCH',align:'center',width:"100"},
		          {name:"PRODUCTION_DATE",index:'PRODUCTION_DATE',align:'center',width:"90"},
		          {name:"QTY",index:'QTY',align:'center',width:"70"},
		          {name:"BOX_QTY",index:'BOX_QTY',align:'center',width:"70"},
		          {name:"BIN_CODE",index:'BIN_CODE',align:'center',width:"70"},
		          {name:"BIN_NAME",index:'BIN_NAME',align:'center',width:"70"},
		          {name:"SOBKZ",index:'SOBKZ',align:'center',width:"70"},
		          {name:"QTY_TYPE",index:'QTY_TYPE',align:'center',width:"80"},
		          {name:"MEMO",index:'MEMO',align:'center',width:"70"},  
		          {name:"SO_NO",index:'SO_NO',align:'center',width:"80"},
				  {name:"SO_ITEM_NO",index:'SO_ITEM_NO',align:'center',width:100},
		          {name:"MSG",index:'MSG',align:'center',classes:"msg-font",width:"120"},  
		          {name:"ROW_NO",index:'ROW_NO',hidden:true},
		        ],
		        viewrecords: true,
		        shrinkToFit:false,
		        width:1100,
			});
			//清空数据
			$("#dataGrid").dataGrid("clearGridData");
			//加入数据
			for(dIndex in response.data){
				$("#dataGrid").dataGrid("addRowData",dIndex+1,response.data[dIndex]);
			}
			excelData = response.data;
			console.log("excelData",excelData);
			if(response.code == 0){
				/**************
				 * 保存成功后,弹出打印标签DIV,把校验出错的记录显示出来，tangj 2019-08-27
				 * 
				 **************/
				
				if(response.saveCount!=undefined && response.errorCount!=undefined){
				  // 库存新增，弹出标签打印DIV
				  if($('#type').val()=='00' || $('#type').val()=='02'){
					var msg=response.saveCount+'条记录保存成功;'+response.errorCount+'条记录校验错误;';
                    if(response.wmsNo!=undefined){
                    	msg+="WMS凭证号："+response.wmsNo+"!";
                    }else{
                    	msg+="未产生WMS凭证号!";
                    }
					$("#resultMsg").html(msg);
					$("#labelList").val(JSON.stringify(response.labelList));
					$("#labelListA4").val(JSON.stringify(response.labelList));
					layer.open({
						type : 1,
						offset : '50px',
						skin : 'layui-layer-molv',
						title : "保存提示",
						area : [ '500px', '250px' ],
						shade : 0,
						shadeClose : false,
						content : jQuery("#resultLayer"),
						btn : [ '确定'],
						btn1 : function(index) {
							layer.close(index);
						}
					});	
				 }
			  }
			}else{
				alert("上传失败," + response.msg);
			} 
		},
		complete:function(XMLHttpRequest, textStatus){
			$("#btnImport").val("导入");	
			$("#btnImport").removeAttr("disabled");
			js.closeLoading();
		}
	});
}

function showModifyTable(){
	jQuery('#dataGrid').GridUnload();  
	//提交表单 & 加载数据
	var form = new FormData(document.getElementById("uploadForm"));
	form.append("BARCODE_FLAG",vm.BARCODE_FLAG);
	form.append("showList",vm.showList);
	js.loading("正在导入,请稍候...");
	$("#btnImport").val("正在导入,请稍候...");	
	$("#btnImport").attr("disabled","disabled");
	$.ajax({
		url:baseUrl + "kn/stockModify/previewModifyExcel",
		data:form,
		type:"post",
		contentType:false,
		processData:false,
		success:function(response){
			js.closeLoading();
			$("#btnImport").val("导入");	
			$("#btnImport").removeAttr("disabled");
			//加载jqgrid
			$("#dataGrid").jqGrid({
			 	datatype: 'local', 
				colNames:['公司','工厂','仓库号','库位','物料号','物料描述','供应商代码','单位','批次','差异数量','储位','储位名称','库存类型','非限制/冻结','销售订单','销售订单行项目','条码修改','条码','','校验信息','',''],
				colModel:[
					  {name:"COMPANY",index:'COMPANY',align:'center',width:80},
			          {name:"WERKS",index:'WERKS',align:'center',width:60},
			          {name:"WH_NUMBER",index:'WH_NUMBER',align:'center',width:70},
			          {name:"LGORT",index:'LGORT',align:'center',width:60},
			          {name:"MATNR",index:'MATNR',align:'center',width:100},
			          {name:"MAKTX",index:'MAKTX',align:'center',width:120},
			          {name:"LIFNR",index:'LIFNR',align:'center',width:90},
			          {name:"MEINS",index:'MEINS',align:'center',width:60},
			          {name:"BATCH",index:'BATCH',align:'center',width:100},
			          {name:"QTY",index:'QTY',align:'center',width:100},
			          {name:"BIN_CODE",index:'BIN_CODE',align:'center',width:60},
			          {name:"BIN_NAME",index:'BIN_NAME',align:'center',width:100},
			          {name:"SOBKZ",index:'SOBKZ',align:'center',width:100},
					  {name:"QTY_TYPE",index:'QTY_TYPE',align:'center',width:100},
					  {name:"SO_NO",index:'SO_NO',align:'center',width:100},
					  {name:"SO_ITEM_NO",index:'SO_ITEM_NO',align:'center',width:100},
					  {name:'actions',align:"center", width:80, sortable:false, title:false, formatter: function(val, obj, row, act){
							var actions = [];
							console.log("row",row);
							delete row["EDIT_DATE"]; 
							if(vm.BARCODE_FLAG == "X"){
							   actions.push("<a title='条码修改' onClick=modify(\""+obj.rowId+"\","+JSON.stringify(row)+")><i class='fa fa-pencil' style='color:blue;cursor: pointer;'></i></a>&nbsp;");
							}
							return actions.join('');
					  }},
					{name:"LABEL_NO",index:'LABEL_NO',align:'center',classes:"msg-font",width:100},  
					{name:"MSG",index:'MSG',align:'center',classes:"msg-font",width:100},  
					{name:"ROW_NO",index:'ROW_NO',hidden:true},
					{name:"ID",index:'ID',hidden:true},
					{name:"UNIT",index:'UNIT',hidden:true},
		        ],
		        viewrecords: true,  
		        shrinkToFit:false,
		        width:1200,
			});
			//清空数据
			$("#dataGrid").dataGrid("clearGridData");
			//加入数据
			for(dIndex in response.data){
				$("#dataGrid").dataGrid("addRowData",dIndex+1,response.data[dIndex]);
			}
			excelData = response.data;
			console.log("excelData",excelData);
			if(response.code == 0){
				if(response.saveCount!=undefined && response.errorCount!=undefined){
					var msg=response.saveCount+'条记录保存成功;'+response.errorCount+'条记录校验错误;';
                    if(response.wmsNo!=undefined){
                    	msg+="WMS凭证号："+response.wmsNo+"!";
                    }else{
                    	msg+="未产生WMS凭证号!";
                    }
					$("#resultMsg").html(msg);
					$("#labelList").val(JSON.stringify(response.labelList));
					$("#labelListA4").val(JSON.stringify(response.labelList));
					layer.open({
						type : 1,
						offset : '50px',
						skin : 'layui-layer-molv',
						title : "保存提示",
						area : [ '500px', '250px' ],
						shade : 0,
						shadeClose : false,
						content : jQuery("#resultLayer"),
						btn : [ '确定'],
						btn1 : function(index) {
							layer.close(index);
						}
					});	
				 }
			}
			else
			  alert("上传失败," + response.msg);
		}.bind(this)
	});
	
}
function showLabelTable(){
	jQuery('#dataGrid').GridUnload();  
	//提交表单 & 加载数据
	var form = new FormData(document.getElementById("uploadForm"));
	form.append("BARCODE_FLAG",vm.BARCODE_FLAG);
	form.append("showList",vm.showList);
	js.loading("正在导入,请稍候...");
	$("#btnImport").val("正在导入,请稍候...");	
	$("#btnImport").attr("disabled","disabled");
	$.ajax({
		url:baseUrl + "kn/stockModify/previewLabelExcel",
		data:form,
		type:"post",
		contentType:false,
		processData:false,
		success:function(response){
			js.closeLoading();
			$("#btnImport").val("导入");	
			$("#btnImport").removeAttr("disabled");
			//加载jqgrid
			$("#dataGrid").jqGrid({
			 	datatype: 'local', 
				colNames:['标签号','标签状态','箱序号','满箱数量','装箱数量','尾箱标识','工厂','仓库号','库位','物料号','储位','特殊库存类型','源批次','WMS批次','供应商代码','生产日期','备注','有效期','校验信息'],
				colModel:[
				  {name:"LABEL_NO",index:'LABEL_NO',align:'center',width:160},
		          {name:"LABEL_STATUS",index:'LABEL_STATUS',align:'center',width:60},
		          {name:"BOX_SN",index:'BOX_SN',align:'center',width:60},
		          {name:"FULL_BOX_QTY",index:'FULL_BOX_QTY',align:'center',width:60},
		          {name:"BOX_QTY",index:'BOX_QTY',align:'center',width:100},
		          {name:"END_FLAG",index:'END_FLAG',align:'center',width:100},
		          {name:"WERKS",index:'WERKS',align:'center',width:80},
		          {name:"WH_NUMBER",index:'WH_NUMBER',align:'center',width:80},
		          {name:"LGORT",index:'LGORT',align:'center',width:100},
		          {name:"MATNR",index:'MATNR',align:'center',width:100},
		          {name:"BIN_CODE",index:'BIN_CODE',align:'center',width:60},
		          {name:"SOBKZ",index:'SOBKZ',align:'center',width:100},
		          {name:"F_BATCH",index:'F_BATCH',align:'center',width:100},
				  {name:"BATCH",index:'BATCH',align:'center',width:100},
				  {name:"LIFNR",index:'LIFNR',align:'center',width:100}, 
				  {name:"PRODUCTION_DATE",index:'PRODUCTION_DATE',align:'center',width:100},
				  {name:"REMARK",index:'REMARK',align:'center',width:100},  
				  {name:"EFFECT_DATE",index:'EFFECT_DATE',align:'center',width:100},   
				  {name:"MSG",index:'MSG',align:'center',classes:"msg-font",width:150},
		        ],
		        viewrecords: true,  
		        shrinkToFit:false,
		        width:1200,
			});
			//清空数据
			$("#dataGrid").dataGrid("clearGridData");
			//加入数据
			for(dIndex in response.data){
				$("#dataGrid").dataGrid("addRowData",dIndex+1,response.data[dIndex]);
			}
			excelData = response.data;
			console.log("excelData",excelData);
			if(response.code == 0){
				if(response.saveCount!=undefined && response.errorCount!=undefined){
					var msg=response.saveCount+'条记录保存成功;'+response.errorCount+'条记录校验错误;';
                    if(response.wmsNo!=undefined){
                    	msg+="WMS凭证号："+response.wmsNo+"!";
                    }else{
                    	msg+="未产生WMS凭证号!";
                    }
					$("#resultMsg").html(msg);
					$("#labelList").val(JSON.stringify(response.labelList));
					$("#labelListA4").val(JSON.stringify(response.labelList));
					layer.open({
						type : 1,
						offset : '50px',
						skin : 'layui-layer-molv',
						title : "保存提示",
						area : [ '500px', '250px' ],
						shade : 0,
						shadeClose : false,
						content : jQuery("#resultLayer"),
						btn : [ '确定'],
						btn1 : function(index) {
							layer.close(index);
						}
					});	
				 }
			}else{
			  alert("上传失败," + data.msg);
			}
		}.bind(this)
	});
}

function saveUpload(){
	 if(excelData === null){
		 alert("需要上传的数据为空");
		 vm.saveFlag=false;
		 return false;	
	 }
	 
	 for(var dIndex in excelData){
		 console.log("excelData[dIndex].MSG",excelData[dIndex].MSG);
		 if(excelData[dIndex].MSG!=undefined && excelData[dIndex].MSG!=''){
			 alert("第"+excelData[dIndex].ROW_NO+"行数据校验未通过!");
			 vm.saveFlag=false;
			 return false;
		 }
	 }
	 
	 if($("#type").val()=='01'){
		var rows=$("#dataGrid").jqGrid('getRowData')
		if(vm.BARCODE_FLAG == "X"){
			 for(var dIndex in rows){
				 if(rows[dIndex].LABEL_NO==''){
					 alert("第"+excelData[dIndex].ROW_NO+"行未修改标签数据!");
					 vm.saveFlag=false;
					 return false;
				 }
			 }
		}
		excelData=rows;
	 }
	 if(excelData.length<=0){
		 alert("需要保存的数据为空！");
		 vm.saveFlag=false;
		 return false;	
	 }
	 
	 js.loading("保存中...");
	$("#btnSave").html("保存中...");
	$("#btnSave").attr("disabled","disabled");
	 //提交数据 & 关闭窗口
	 var jsondata = JSON.stringify(excelData);
	 $.ajax({
		 url:baseUrl + "kn/stockModify/save",
		 type:"post",
		 dataType:"json",
		 data:{
			 SAVE_DATA:jsondata,
			 werks:$('#werks').val(),
			 whNumber:$('#whNumber').val(),
			 type:$('#type').val(),
			 BARCODE_FLAG: vm.BARCODE_FLAG,
		 },
		 success:function(resp){
			 $("#btnSave").html("保存");	
			 $("#btnSave").removeAttr("disabled");
			 js.closeLoading();
			 if(resp.code === 0){
				js.showMessage('保存成功,事务凭证号：'+resp.wmsNo);
				$("#dataGrid").dataGrid("clearGridData");
				// 库存新增，弹出标签打印DIV
				if($('#type').val()=='00' || $('#type').val()=='02'){
					console.log("resp.lableList",resp.labelList);
                    $("#resultMsg").html(resp.wmsNo);
					$("#labelList").val(JSON.stringify(resp.labelList));
					$("#labelListA4").val(JSON.stringify(resp.labelList));
					layer.open({
						type : 1,
						offset : '50px',
						skin : 'layui-layer-molv',
						title : "库存新增成功",
						area : [ '500px', '250px' ],
						shade : 0,
						shadeClose : false,
						content : jQuery("#resultLayer"),
						btn : [ '确定'],
						btn1 : function(index) {
							layer.close(index);
						}
					});
				}
			 }else{
				 alert("导入失败：" + resp.msg);
			 }
		 }
	 });
}
function modify(rowid,rowData){
	console.log("rowData",rowData);
	$('#needModifyQty').html(rowData.QTY);
	$.ajax({
		url:baseUrl+"kn/stockModify/getLabelList",
		type:"post",
		async:false,
		dataType:"json",
		data:{
			WERKS : rowData.WERKS,
			WH_NUMBER : rowData.WH_NUMBER,
			LGORT : rowData.LGORT,
			MATNR : rowData.MATNR,
			LIFNR : rowData.LIFNR,
			BIN_CODE : rowData.BIN_CODE,
			BATCH : rowData.BATCH,
			SOBKZ : rowData.SOBKZ
		},
		success:function(response){
				var list=response.labelList;
				layer.open({
				type : 1,
				offset : '50px',
				skin : 'layui-layer-molv',
				title : "标签修改",
				area : [ '820px', '350px' ],
				shade : 0,
				shadeClose : false,
				content : jQuery("#labelLayer"),
				btn: ['<i class="fa fa-check"></i> 确定','<i class="fa fa-close"></i> 关闭'],
				btn1:function(index,layero){
					$('#dataGrid1').jqGrid("saveCell", lastrow, lastcell);
					if($("#needModifyQty").html()==$("#modifyQty").html()){
							var rows = $('#dataGrid1').jqGrid('getRowData');
							var labelStr=[];
							$.each(rows,function(index,row){
								if(row.QTY!=''){
									var str=row.LABEL_NO+":"+row.QTY; 
									labelStr.push(str);                              
								}
							});		
							console.log("labelStr",labelStr+"-"+rowid);
							$("#dataGrid").jqGrid('setCell',rowid,"LABEL_NO",labelStr);					 
					}else{
						console.log("cancel");
					}
	    		},
				btn0 : function(index) {
					layer.close(index);
				}
					
			});
			showTable(list);
		}
	})
}
function showTable (list){
	jQuery('#dataGrid1').GridUnload();
	$("#modifyQty").html("");
	console.log("list",list);
	$("#dataGrid1").dataGrid({
		datatype: "local",
		data: list,
        colModel: [			
            {label: '条码', name: 'LABEL_NO',index:"LABEL_NO", width: "110",align:"center",sortable:false}, 	
            {label: '料号', name: 'MATNR',index:"MATNR", width: "100",align:"center",sortable:false}, 
            {label: '物料描述', name: 'MAKTX',index:"MAKTX", width: "120",align:"center",sortable:false}, 	
            {label: '批次', name: 'BATCH',index:"vendor", width: "100",align:"center",sortable:false}, 	
            {label: '储位', name: 'BIN_CODE',index:"BIN_CODE", width: "70",align:"center",sortable:false},
            {label: '基本数量', name: 'BOX_QTY',index:"BOX_QTY", width: "70",align:"center",sortable:false}, 	
            {label: '单位', name: 'UNIT',index:"UNIT", width: "50",align:"center",sortable:false}, 	
            {label: '修改数量', name: 'QTY',index:"QTY", width: "70",align:"center",sortable:false,editable:true},
            {label: '工厂', name: 'WERKS',index:"WERKS", width: "60",align:"center",sortable:false}, 	
			{label: '仓库号', name: 'WH_NUMBER',index:"WH_NUMBER", width: "60",align:"center",sortable:false},
			{label: '库位', name: 'LGORT',index:"LGORT", width: "60",align:"center",sortable:false},
			{label: '库存类型', name: 'SOBKZ',index:"SOBKZ", width: "70",align:"center",sortable:false},
        ],
		beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
		},
		afterSaveCell:function(rowid, cellname, value, iRow, iCol){
			  // $('#dataGrid1').jqGrid("saveCell", lastrow, lastcell);
                if(cellname=='QTY'){
					var rows=$("#dataGrid1").jqGrid('getRowData');
					var modifyQtySum=0;
					$.each(rows,function(i,row){
						console.info("row.QTY",row.QTY);
						modifyQtySum+=parseInt(row.QTY!='' ? row.QTY  : 0);
					})
				}
				console.info("modifyQtySum",modifyQtySum);
				$("#modifyQty").html(modifyQtySum);
		},
		shrinkToFit: false,
        cellEdit:true,
        cellurl:'#',
		cellsubmit:'clientArray',
    });
}