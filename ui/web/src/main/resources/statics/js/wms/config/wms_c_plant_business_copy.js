var lastrow,lastcell;
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
				saveData[i] = $("#dataGrid").jqGrid('getRowData',ids[i]);
				delete saveData[i]["BUSINESSCLASS"]; 
				delete saveData[i]["BUSINESSTYPE"];
				delete saveData[i]["BUSINESSNAME"];
				delete saveData[i]["SOBKZ"];
				saveData[i].WERKS=$("#targetWerks").val();
			}
			var url = baseURL+"config/plantbusiness/copy";
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
			    		js.showMessage('操作成功');
	                    vm.saveFlag= true;
	                    console.log("vm.saveFlag",vm.saveFlag);
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
        url : baseURL + "config/plantbusiness/list",
        dataType : "json",
		type : "post",
        postData : {},
		cellEdit:true,
		cellurl:'#',
		cellsubmit:'clientArray',
		columnModel: [
			{ label: '工厂代码', name: 'WERKS', index: 'WERKS', width: 65,align:"center",editable:true}, 			
   			{ label: '业务类型代码', name: 'BUSINESSCODE', index: 'BUSINESSCODE',align:"center", width: 95}, 
   			{ label: 'WMS业务分类', name: 'BUSINESSCLASS', index: 'BUSINESSCLASS',align:"center", width: 95}, 			
   			{ label: 'WMS业务类型', name: 'BUSINESSTYPE', index: 'BUSINESSTYPE',align:"center", width: 95}, 			
   			{ label: 'WMS业务类型名称', name: 'BUSINESSNAME', index: 'BUSINESSNAME',align:"center", width: 75}, 			
   			{ label: '仓库特殊类型', name: 'SOBKZ', index: 'SOBKZ',align:"center", width: 60}, 			
   			{ label: 'SAP同步过账标识', name: 'SYNFLAGDESC', index: 'SYNFLAGDESC',align:"center", width: 80,
   				editable:true,edittype:"select",
  	          editoptions: {value:'0:同步;X:异步',dataEvents:[
  					{type:"click",fn:function(e){
  						var val = this.value;
  						var rowId= $("#dataGrid").jqGrid('getGridParam','selrow');
  						$("#dataGrid").jqGrid('setCell',rowId,"SYNFLAG",val);  
  					}},
  				]},
   			}, 	
   			{ label: '是否需要审批', name: 'APPROVALFLAGDESC', index: 'APPROVALFLAGDESC',align:"center", width: 80,
              editable:true,edittype:"select",
  	          editoptions: {value:'0:否;X:是',dataEvents:[
  					{type:"click",fn:function(e){
  						var val = this.value;
  						var rowId= $("#dataGrid").jqGrid('getGridParam','selrow');
  						$("#dataGrid").jqGrid('setCell',rowId,"APPROVALFLAG",val);  
  					}},
  				]},
   			}, 			
   			{ label: '是否可超需求', name: 'OVERSTEPREQFLAGDESC', index: 'OVERSTEPREQFLAGDESC',align:"center", width: 80,
   			  editable:true,edittype:"select",
  	          editoptions: {value:'0:否;X:是',dataEvents:[
  					{type:"click",fn:function(e){
  						var val = this.value;
  						var rowId= $("#dataGrid").jqGrid('getGridParam','selrow');
  						$("#dataGrid").jqGrid('setCell',rowId,"OVERSTEPREQFLAG",val);  
  					}},
  				]},
   			}, 			
   			{ label: '是否可超虚拟库存', name: 'OVERSTEPHXFLAGDESC', index: 'OVERSTEPHXFLAGDESC',align:"center", width: 80,
   			  editable:true,edittype:"select",
  	          editoptions: {value:'0:否;X:是',dataEvents:[
  					{type:"click",fn:function(e){
  						var val = this.value;
  						var rowId= $("#dataGrid").jqGrid('getGridParam','selrow');
  						$("#dataGrid").jqGrid('setCell',rowId,"OVERSTEPHXFLAG",val);  
  					}},
  				]},
   			}, 	
   			{ label: '中转库', name: 'LGORT', index: 'LGORT',align:"center", width: 80,editable:true,edittype:"text",}, 	
   			{ label: '排序号', name: 'SORTNO', index: 'SORTNO',align:"center", width: 50}, 		
   			//{ label:'ID',name:'ID',hidden:true,formatter:"Long"},
   			{ label:'SAP同步过账标识代码',name:'SYNFLAG',hidden:true,formatter:"String"},
   			{ label:'是否需要审批代码',name:'APPROVALFLAG',hidden:true,formatter:"String"},
   			{ label:'是否可超需求代码',name:'OVERSTEPREQFLAG',hidden:true,formatter:"String"},
   			{ label:'是否可超虚拟库存代码',name:'OVERSTEPHXFLAG',hidden:true,formatter:"String"}
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