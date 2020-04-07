var mydata = [];
var lastrow,lastcell;
var vm = new Vue({
	el:'#rrapp',
	data:{
		WERKS:"",
		whNumber:"",
		warehourse:[],
		relatedareaname:[],
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
		},
		whNumber:{
			handler:function(newVal,oldVal){
				$.ajax({
					type:'post',
					datatype:'json',
					url:baseUrl + "kn/inventory/getWhManagerList",
					data:{"WERKS":$("#werks").val(),"WH_NUMBER":newVal},
					success:function(resp){
						vm.relatedareaname = resp.list;
					}
				})
			}
		},
	},
	methods: {
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
		// 保存确认结果
		saveConfirm: function (event) {
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
                if(data["DIFFERENCE_REASON"]=='' || data["DIFFERENCE_REASON"]==''){
					js.showErrorMessage('第'+(ids[i])+'行差异原因不能为空');
					save_flag=false;
					return false;
				}
				saveData.push(data);
			}
			console.log("saveData",saveData);
			var url = baseURL+"kn/inventory/saveConfirm";
			if(save_flag)
			$.ajax({
				type: "POST",
			    url: url,
			    dataType : "json",
			    async: false,
				data : {
					SAVE_DATA:JSON.stringify(saveData),
					TYPE:'02',
					INVENTORY_NO:$("#inventoryNo").val(),
				},
			    success: function(r){
			    	if(r.code === 0){
			        	js.showMessage(r.msg+"：保存成功！");
					}else{
						js.showMessage(r.msg);
					}
				}
			});
		},
	}
});
$(function () {
	var now = new Date(); //当前日期
	var startDate=new Date(now.getTime()-6*24*3600*1000);
	$("#startDate").val(formatDate(startDate));
	$("#endDate").val(formatDate(now));
	showTable();
	$("#btnQuery").click(function () {
		$("#btnQuery").val("查询中...");
		$("#btnQuery").attr("disabled","disabled");	
		
		$.ajax({
			url:baseURL+"kn/inventory/getInventoryConfirm",
			dataType : "json",
			type : "post",
			data : {
				"WERKS":$("#werks").val(),
				"WH_NUMBER":$("#whNumber").val(),
				"INVENTORY_NO":$("#inventoryNo").val(),
				"WH_MANAGER":$("#whManager").val(),
				"START_DATE":$("#startDate").val(),
				"END_DATE":$("#endDate").val()
			},
			async: true,
			success: function (response) { 
				$("#dataGrid").jqGrid("clearGridData", true);
				mydata.length=0;
				$("#btnQuery").removeAttr("disabled");
				$("#btnQuery").val("查询");
				if(response.code == "0"){
					mydata = response.list;
					$("#dataGrid").jqGrid('GridUnload');	
					showTable();
					if(mydata.length=="")alert("未查询到符合条件的数据！");
				}else{
					alert(response.msg);
				}				
			}
		});
	});	
});
function showTable(){
	$("#dataGrid").dataGrid({
		datatype: "local",
		data: mydata,
        colModel: [		
        	{label: '盘点任务号', name: 'INVENTORY_NO',index:"INVENTORY_NO", width: "120",align:"center",frozen: true},
        	{label: '行项目', name: 'INVENTORY_ITEM_NO',index:"INVENTORY_ITEM_NO", width: "55",align:"center",sortable:false,frozen: true},
            {label: '工厂', name: 'WERKS',index:"WERKS", width: "60",align:"center",sortable:false,frozen: true,},
            {label: '仓库号', name: 'WH_NUMBER',index:"WH_NUMBER", width: "60",align:"center",sortable:false,frozen: true},
            {label: '料号', name: 'MATNR',index:"MATNR", width: "100",align:"center",sortable:true,frozen: true},
            {label: '物料描述', name: 'MAKTX',index:"MAKTX", width: "150",align:"center",sortable:false,frozen: true},
            {label: '仓管员', name: 'WH_MANAGER',index:"WH_MANAGER", width: "70",align:"center",sortable:true},
            {label: '供应商代码', name: 'LIFNR',index:"LIFNR", width: "90",align:"center",sortable:false},
            {label: '供应商名称', name: 'LIKTX',index:"LIKTX", width: "130",align:"center",sortable:false},
            {label: '库位', name: 'LGORT',index:"LGORT", width: "50",align:"center",sortable:false},
            {label: '单位', name: 'MEINS',index:"MEINS", width: "50",align:"center",sortable:false},
            {label: '账面数量', name: 'STOCK_QTY',index:"STOCK_QTY", width: "70",align:"center",sortable:false},
        	{label: '复盘数量', name: 'INVENTORY_QTY_REPEAT',index:"INVENTORY_QTY_REPEAT", width: "70",align:"center",sortable:false},
            {label: '差异数量', name: 'DIF_QTY',index:"DIF_QTY", width: "80",align:"center",sortable:false},
            {label: '<SPAN STYLE="COLOR:RED">*</SPAN><SPAN STYLE="COLOR:BLUE"><B>差异原因</B></SPAN>', name: 'DIFFERENCE_REASON',index:"DIFFERENCE_REASON", width: "100",align:"center",sortable:false,editable:true,
            	formatter:function(item, index){
        		    return item==null ? '': item;
        	},formatoptions:{defaultValue:''}},
            {label: '确认人', name: 'CONFIRMOR',index:"CONFIRMOR", width: "80",align:"center",sortable:false},         
            {label: 'ID', name: 'ID',index:"ID", width: "0",align:"center",hidden:true},
        ],
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
			// enter键 移动到下一行单元格
			if(cellname=='DIFFERENCE_REASON'){
				window.setTimeout(function(){ 
               	   $("#dataGrid").jqGrid("editCell",iRow+1,iCol,true); 
               	},100);
			}
		},
		shrinkToFit: false,
        cellEdit:true,
        rownumbers:false,
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