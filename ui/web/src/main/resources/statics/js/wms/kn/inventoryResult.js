var mydata = [];
var type='';  // 初盘或复盘
var inventoryType='';// 明盘或暗盘
var inventoryNo='';// 盘点任务号
var lastrow,lastcell;
var last_scan_ele;
var vm = new Vue({
	el:'#rrapp',
	data:{
		WERKS:"",
		whNumber:"",
		warehourse:[]
    },
	created:function(){
		this.WERKS = $("#werks").find("option").first().val();
	},
	watch:{
		WERKS:{
			handler:function(newVal,oldVal){
	          //查询工厂仓库
	          $.ajax({
	        	  url:baseUrl + "common/getWhDataByWerks",
	        	  data:{"WERKS":newVal},
	        	  success:function(resp){
	        		 vm.warehourse = resp.data;
	        		 vm.whNumber = resp.data[0].WH_NUMBER;
	        	  }
	          })
			}
		}
	},
	methods: {
		refresh : function() {
			$('#dataGrid').trigger('reloadGrid');
		},
		reload: function (event) {
			$("#searchForm").submit();
		},
		getPlantNoFuzzy:function(){
			getPlantNoSelect("#werks",null,null);
		},
		enter:function(e){
			$("#btnQuery").trigger("click");
		},
		onPlantChange:function(){
			loadWhNumber();
		},
		saveResult: function (event) {
			var save_flag=true;
			//保存编辑的最后一个单元格
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			var saveData=[];
			//获取选中表格数据
			var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");	

			if(ids.length == 0){
				alert("当前还没有勾选任何行项目数据！");
				return false;
			}
			for (var i = 0; i < ids.length; i++) {
				var data=$("#dataGrid").jqGrid('getRowData',ids[i]);
				if(type=='00'){
					if(data["INVENTORY_QTY"]=='' || data["INVENTORY_QTY"]==''){
						js.showErrorMessage('第'+(ids[i])+'行初盘数量不能为空');
						save_flag=false;
						return false;
					}
				}
				if(type=='01'){
					if(data["INVENTORY_QTY_REPEAT"]=='' || data["INVENTORY_QTY_REPEAT"]==''){
						js.showErrorMessage('第'+(ids[i])+'行复盘数量不能为空');
						save_flag=false;
						return false;
					}
				}
				saveData.push(data);
			}
			console.log("saveData",saveData);
			var url = baseURL+"kn/inventory/saveResult";
			if(save_flag)
			$.ajax({
				type: "POST",
			    url: url,
			    dataType : "json",
			    async: false,
				data : {
					SAVE_DATA:JSON.stringify(saveData),
					TYPE:type, // 初盘或复盘
					INVENTORY_NO:inventoryNo
				},
			    success: function(r){
			    	if(r.code === 0){
			        	js.showMessage(r.msg+"：保存成功！");
			        	$("#dataGrid").dataGrid("clearGridData");
			        	reset();
					}else{
						js.showMessage(r.msg);
					}
				}
			});
		},
	}
});
$(function () {
	type=$("#type").val();
	showTable();
	$("#btnQuery").click(function () {
		if($("#inventoryNo").val()==''){
			alert("请输入盘点任务号！");
			return false;
		}
		$("#btnQuery").val("查询中...");
		$("#btnQuery").attr("disabled","disabled");	
		
		$.ajax({
			url:baseURL+"kn/inventory/getInventoryResult",
			dataType : "json",
			type : "post",
			data : {
				"WERKS":$("#werks").val(),
				"WH_NUMBER":$("#whNumber").val(),
				"INVENTORY_NO":$("#inventoryNo").val(),
				"TYPE":$("#type").val()
			},
			async: true,
			success: function (response) { 
				$("#dataGrid").jqGrid("clearGridData", true);
				mydata.length=0;
				if(response.code == "0"){
					type=$("#type").val();
					inventoryNo=$("#inventoryNo").val();
					mydata = response.map.list;
					inventoryType=response.map.INVENTORY_TYPE;
					$("#dataGrid").jqGrid('GridUnload');	
					showTable();
					if(mydata.length=="")alert("未查询到符合条件的数据！");
					$("#btnQuery").removeAttr("disabled");
					$("#btnQuery").val("查询");
					$("#werks").attr("disabled",true);
					$("#whNumber").attr("disabled",true);
					$("#inventoryNo").attr("disabled",true);
					$("#type").attr("disabled",true);
					$("#btnQuery").attr("disabled",true);
				}else{
					alert(response.msg);
					$("#btnQuery").val("查询");
					$("#btnQuery").removeAttr("disabled");
				}				
			}
		});
	});	
  //-----导入盘点结果操作---------
  $("#btnImp").click(function(){
	 var options = {
		type: 2,
		maxmin: true,
		shadeClose: true,
		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>盘点结果导入',
		area: ["1060px", "560px"],
		content: baseURL+"wms/kn/inventoryResultImport.html",  
		btn: ['<i class="fa fa-check"></i> 保存'],
		success:function(layero, index){
			var win = layero.find('iframe')[0].contentWindow;
			win.vm.saveFlag = false;
		},
		btn1:function(index,layero){
			var win = layero.find('iframe')[0].contentWindow;
			// 调用弹出层页面vue的save函数
			win.vm.save();
			if(typeof listselectCallback == 'function'){
				listselectCallback(index,win.vm.saveFlag);
			}
		}
	};
	options.btn.push('<i class="fa fa-close"></i> 关闭');
	options['btn'+options.btn.length] = function(index, layero){
    };
	js.layer.open(options);
  });
  $("#reset").click(function(){
	  reset();
  });
});
function showTable(){
	$("#dataGrid").dataGrid({
		datatype: "local",
		data: mydata,
        colModel: [		
        	{label: '行项目', name: 'INVENTORY_ITEM_NO',index:"INVENTORY_ITEM_NO", width: "55",align:"center",sortable:false,frozen: true},
            {label: '工厂', name: 'WERKS',index:"WERKS", width: "60",align:"center",sortable:false,frozen: true,},
            {label: '仓库号', name: 'WH_NUMBER',index:"WH_NUMBER", width: "60",align:"center",sortable:false,frozen: true},
            {label: '料号', name: 'MATNR',index:"MATNR", width: "100",align:"center",sortable:true,frozen: true},
            {label: '物料描述', name: 'MAKTX',index:"MAKTX", width: "150",align:"center",sortable:false,frozen: true},
            {label: '仓管员', name: 'WH_MANAGER',index:"WH_MANAGER", width: "70",align:"center",sortable:true},
            {label: '供应商代码', name: 'LIFNR',index:"LIFNR", width: "90",align:"center",sortable:false},
            {label: '供应商名称', name: 'LIKTX',index:"LIKTX", width: "150",align:"center",sortable:false},
            {label: '库位', name: 'LGORT',index:"LGORT", width: "50",align:"center",sortable:false},
            {label: '单位', name: 'MEINS',index:"MEINS", width: "50",align:"center",sortable:false},
            {label: '账面数量', name: 'STOCK_QTY',index:"STOCK_QTY", width: "75",align:"center",sortable:false,formatter: function(val, obj, row, act){
               if(inventoryType=='00'){
            	   return val;
               }
               if(inventoryType=='01'){
            	   return '*';
               }
            }},
        	{label: '<SPAN STYLE="COLOR:RED">*</SPAN><SPAN STYLE="COLOR:BLUE"><B>初盘数量</B></SPAN>', name: 'INVENTORY_QTY',index:"INVENTORY_QTY", width: "100",align:"center",sortable:false,editable:true,
            	formatter:'integer',editrules:{number:true},
            	formatoptions:{defaultValue:''}},
        	{label: '<SPAN STYLE="COLOR:RED">*</SPAN><SPAN STYLE="COLOR:BLUE"><B>复盘数量</B></SPAN>', name: 'INVENTORY_QTY_REPEAT',index:"INVENTORY_QTY_REPEAT", width: "100",align:"center",sortable:false,editable:true,
            	formatter:'integer',editrules:{number:true},
            	formatoptions:{defaultValue:''}},
            {label: '差异数量', name: 'DIF_QTY',index:"DIF_QTY", width: "80",align:"center",sortable:false},
            {label: '盘点任务号', name: 'INVENTORY_NO',index:"INVENTORY_NO", width: "0",align:"center",hidden:true},
            {label: 'ID', name: 'ID',index:"ID", width: "0",align:"center",hidden:true},
        ],
        loadComplete:function(){
			js.closeLoading();
			$(".btn").attr("disabled", false);
			// 初盘：隐藏”复盘数量“列
        	if(type=='00'){
        		$("#dataGrid").setGridParam().hideCol("INVENTORY_QTY_REPEAT");
        	}
        	// 复盘：设置”初盘数量“列不可编辑
        	if(type=='01'){
        		$("#dataGrid").setGridParam().hideCol("INVENTORY_QTY");
        	}
		},
		beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
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
			if(value!=''){
				var STOCK_QTY=$("#dataGrid").jqGrid("getCell",rowid,"STOCK_QTY");
				// 盘点方式为” 暗盘“ or 差异数量为0；不计算差异数
				if(STOCK_QTY!='*'){
					var DIF_QTY=parseInt(value)-parseInt(STOCK_QTY);
					if(DIF_QTY!=0){
					   $("#dataGrid").setCell(rowid,"DIF_QTY",DIF_QTY,{color:'red'});
					}
				}
			}
			// enter键 移动到下一行单元格
			if(cellname=='INVENTORY_QTY' || cellname=='INVENTORY_QTY_REPEAT'){
				window.setTimeout(function(){ 
               	   $("#dataGrid").jqGrid("editCell",iRow+1,iCol,true); 
               	},100);
			}
		},
		shrinkToFit: false,
        cellEdit:true,
        //rownumbers:false,
		rownumbers: false,
        cellurl:'#',
		cellsubmit:'clientArray',
        showCheckbox:true
    });
	
	$("#dataGrid").jqGrid("setFrozenColumns");
}
function loadWhNumber(){
	var plantCode = $("#werks").val();
   //初始化仓库
    $.ajax({
        url:baseUrl + "common/getWhDataByWerks",
        data:{"WERKS":plantCode},
        success:function(resp){
            vm.warehourse = resp.data;
        }
    });
}
function reset(){
	$("#btnQuery").removeAttr("disabled");
	$("#werks").removeAttr("disabled");
	$("#whNumber").removeAttr("disabled");
	$("#inventoryNo").removeAttr("disabled");
	$("#type").removeAttr("disabled");
	$("#inventoryNo").val("");
	$("#dataGrid").jqGrid("clearGridData", true);
	type='';
	inventoryType='';
	inventoryNo='';
}
function listselectCallback(index,rtn){
	rtn = rtn ||false;
	if(rtn){
		//操作成功，刷新表格
		vm.reload();
		try {
			parent.layer.close(index);
        } catch(e){
        	layer.close(index);
        }
	}
}

function funFromjs(result) {
	result = result.replace("{","").replace("}","")
	var inventoryNo = result.split(",")[0].split(":")[1];

	var e = $.Event('keydown');
	e.keyCode = 13;
	$(last_scan_ele).val(inventoryNo).trigger(e);
	vm.TEST_NODE=""
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
