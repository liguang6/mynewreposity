var lastrow,lastcell;
var qcStatusSelectArr = "00:未质检;01:质检中;02:已质检";
var data={};
var vm = new Vue({
	el:'#rrapp',
	data:{
		saveFlag:false
	},
	created:function(){
	},
	methods: {
		 save: function () {
			if($("#werks").val()==''){
			   alert("请选择源工厂");
			   return false;
			}
			if($("#targetWerks").val()==''){
			   alert("请选择目标工厂");
			   return false;
			}
			if($("#werks").val()!='' && $("#targetWerks").val()!=''){
				if($("#werks").val()==$("#targetWerks").val()){
					alert("目标工厂不能与源工厂相同");
					return false;
				}
			}
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
//				if(data["QUANTITY"]==null || data["QUANTITY"]==''){
//					js.showErrorMessage('第'+(i+1)+'行操作数量不能为空');
//					save_flag=false;
//					return false;
//				}
//				// 冻结时， 冻结原因必填
//				if(type=='00'){
//					if(data["REASON"]==null || data["REASON"]==''){
//						js.showErrorMessage('第'+(i+1)+'行冻结原因不能为空！');
//						save_flag=false;
//						return false;
//					}
//				}
				saveData[i] = $("#dataGrid").jqGrid('getRowData',ids[i]);
				//delete saveData[i]["actions"]; 
			}
			var url = baseURL+"config/wmscqcresult/batchSave";
			if(save_flag)
			$.ajax({
				type: "POST",
			    url: url,
			    dataType: "json",
			    async:false,
				data : {
					saveData:JSON.stringify(saveData)
				},
			    success: function(r){
			    	if(r.code === 0){
			        	vm.saveFlag= true;
			        	js.showMessage(r.msg+"：保存成功！");
					}else{
						js.showMessage(r.msg);
					}
				}
			});
		},
		query: function () {
		   showData();
		},
	}
});
$(function(){
	showData();
});
function showData () {
	$("#dataGrid").jqGrid('GridUnload');
	jQuery("#dataGrid").trigger("reloadGrid"); 
	$('#dataGrid').dataGrid({
        url : "config/wmscqcresult/list",
        dataType : "json",
		type : "post",
        postData : {},
		cellEdit:true,
		cellurl:'#',
		cellsubmit:'clientArray',
		columnModel: [
			{ label: '工厂代码', name: 'WERKS', index: 'WERKS', width: 80,align:"center",editable:true}, 			
   			{ label: '质检结果代码', name: 'QC_RESULT_CODE', index: 'QC_RESULT_CODE',align:"center", width: 80}, 
   			{ label: '质检结果类型', name: 'QC_RESULT_NAME', index: 'QC_RESULT_NAME',align:"center", width: 80}, 
   			{ label: '质检状态', name: 'QC_STATUS_DESC', width: 80,align:"center",
//   			editable:true,edittype:"select",
//		          editoptions: {value:qcStatusSelectArr,dataEvents:[
//					{type:"click",fn:function(e){
//						var val = this.value;
//						var rowId= $("#dataGrid").jqGrid('getGridParam','selrow');
//						$("#dataGrid").jqGrid('setCell',rowId,"QC_STATUS",val); 
//					}},
//				]},
			},			
   			{ label: '可进仓标识', name: 'WH_FLAG_DESC', index: 'WH_FLAG_DESC', width: 80,align:"center",
   			  editable:true,edittype:"select",
	          editoptions: {value:'0:不能进仓;X:可进仓',dataEvents:[
					{type:"click",fn:function(e){
						var val = this.value;
						var rowId= $("#dataGrid").jqGrid('getGridParam','selrow');
						$("#dataGrid").jqGrid('setCell',rowId,"WH_FLAG",val);  
					}},
				]},
			},			
   			{ label: '可退货标识', name: 'RETURN_FLAG_DESC', index: 'RETURN_FLAG_DESC', width: 80,align:"center" ,
   			  editable:true,edittype:"select",
	          editoptions: {value:'0:不能退货;X:可退货',dataEvents:[
					{type:"click",fn:function(e){
						var val = this.value;
						var rowId= $("#dataGrid").jqGrid('getGridParam','selrow');
						$("#dataGrid").jqGrid('setCell',rowId,"RETURN_FLAG",val);  
					}},
				]},
			},					
   			{ label: '评审标识', name: 'REVIEW_FLAG_DESC', index: 'REVIEW_FLAG_DESC', width: 80,align:"center",
   			  editable:true,edittype:"select",
	          editoptions: {value:'0:不需评审;X:需评审',dataEvents:[
					{type:"click",fn:function(e){
						var val = this.value;
						var rowId= $("#dataGrid").jqGrid('getGridParam','selrow');
						$("#dataGrid").jqGrid('setCell',rowId,"REVIEW_FLAG",val);  
					}},
				]},
			},		 			
   			{ label: '良品进仓标识', name: 'GOOD_FLAG_DESC', index: 'GOOD_FLAG_DESC', width: 80,align:"center",
   			  editable:true,edittype:"select",
	          editoptions: {value:'0:良品;X:不良品',dataEvents:[
					{type:"click",fn:function(e){
						var val = this.value;
						var rowId= $("#dataGrid").jqGrid('getGridParam','selrow');
						$("#dataGrid").jqGrid('setCell',rowId,"GOOD_FLAG",val);
					}},
				]},
			},		 		
			{ label: '良品进仓标识代码', name: 'GOOD_FLAG', index: 'GOOD_FLAG',hidden:true,formatter: function(val, obj, row, act){
				var result = '';
				if(val!=null){
					result=val;
				}
				return result;
			}},
   			{ label: '质检状态代码', name: 'QC_STATUS', index: 'QC_STATUS',hidden:true,formatter: function(val, obj, row, act){
				var result = '';
				if(val!=null){
					result=val;
				}
				return result;
			}},
   			{ label: '可进仓标识代码', name: 'WH_FLAG', index: 'WH_FLAG',hidden:true,formatter: function(val, obj, row, act){
				var result = '';
				console.log('val',val);
				if(val!=null){
					result=val;
				}
				return result;
			}},
   			{ label: '可退货标识代码', name: 'RETURN_FLAG', index: 'RETURN_FLAG',hidden:true,formatter: function(val, obj, row, act){
				var result = '';
				if(val!=null){
					result=val;
				}
				return result;
			}},
   			{ label: '评审标识代码', name: 'REVIEW_FLAG', index: 'REVIEW_FLAG',hidden:true,formatter: function(val, obj, row, act){
				var result = '';
				if(val!=null){
					result=val;
				}
				return result;
			}},
   			{ label: '质检结果类型', name: 'QC_RESULT_CODE', index: 'QC_RESULT_CODE',hidden:true,formatter: function(val, obj, row, act){
				var result = '';
				if(val!=null){
					result=val;
				}
				return result;
			}},
		],
        jsonReader : {  
        	root : "page.list" 
        },  
		showCheckbox:true,
		viewrecords: true,
        rownumbers: true, 
        rownumWidth: 25, 
        autowidth:true,
        multiselect: true,
        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
		},
        beforeSelectRow:function(rowid,e){
        	var $myGrid = $(this),  
			   i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),  
			   cm = $myGrid.jqGrid('getGridParam', 'colModel'); 
			   if(cm[i].name == 'cb'){
				   $('#dataGrid').jqGrid('setSelection',rowid);
			   }   		
			   return (cm[i].name == 'cb'); 
        },
        afterSaveCell:function(rowid, cellname, value, iRow, iCol){
        }
	});
}
function onPlantChange() {
	var targetWerks=$("#targetWerks").val();
	var ids = $("#dataGrid").jqGrid("getDataIDs");
	for (var i = 0; i < ids.length; i++) {
		$("#dataGrid").jqGrid('setCell', ids[i], "WERKS", targetWerks,'not-editable-cell');
	}
}