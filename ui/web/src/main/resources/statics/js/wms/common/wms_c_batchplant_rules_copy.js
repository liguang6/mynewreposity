var lastrow,lastcell;
var data={};
var vm = new Vue({
	el:'#rrapp',
	data:{
		saveFlag:false,
		lgortList:'',
		targetWerks:''
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
				saveData[i].werks=$("#targetWerks").val();
			}
			var url = baseURL+"common/batchRules/copy";
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
function showData (targetWerks,lgortList) {
	$("#dataGrid").jqGrid('GridUnload');
	jQuery("#dataGrid").trigger("reloadGrid"); 
	$('#dataGrid').dataGrid({
        url : baseURL+"common/batchRules/list",
        dataType : "json",
		type : "post",
        postData : {},
		cellEdit:true,
		cellurl:'#',
		cellsubmit:'clientArray',
		columnModel: [
			{label: '工厂代码', name: 'werks',index:"werks", width: 70,align:"center",formatter: function(item, index){
				if(targetWerks!='' && targetWerks!=undefined){
					return targetWerks;
				}else{
					return item;
				}
		    }},	
            {label: '业务类型名称', name: 'businessNameText',index:"businessNameText", width: 120,align:"center"},
			{label: '库位', name: 'lgort',index:"lgort", width: 70,align:"center",editable:true,edittype:"select",
			 editoptions: {value:lgortList,dataEvents:[
					{type:"click",fn:function(e){
						var val = this.value;
						var rowId= $("#dataGrid").jqGrid('getGridParam','selrow');
						//$("#dataGrid").jqGrid('setCell',rowId,"batch",val);  
					}},
					
				]},
			},
			{label: '规则代码', name: 'batchRuleCode',index:"batchRuleCode", width: 60,align:"center"},
			{label: '批次规则描述', name: 'batchRuleText',index:"batchRuleText", width: 290,align:"center"},
			{label: '是否危化品', name: 'dangerFlagText',index:"dangerFlagText", width: 75,align:"center",formatter: function(item, index,row,act){
	 		       var result='';
			       if(row.dangerFlag=='0'){result='<span class="label label-danger">否</span>';}
			       if(row.dangerFlag=='X'){result='<span class="label label-success">是</span>';}
		    	   return result;
		    }},
			{label: '沿用源批次', name: 'fBatchFlagText',index:"fBatchFlagText", width: 75,align:"center",formatter: function(item, index,row,act){
	 		       var result='';
			       if(row.fBatchFlag=='0'){result='<span class="label label-danger">否</span>';}
			       if(row.fBatchFlag=='X'){result='<span class="label label-success">是</span>';}
		    	   return result;
		    }},	
		    {header:'业务类型名称代码',name:'businessName',hidden:true,formatter: function(item, index){
		    	   return item!=null ? item : '' ;
		    }},	
		    {label: 'dangerFlag', name: 'dangerFlag',index:"dangerFlag", hidden:true},
		    {label: 'fBatchFlag', name: 'fBatchFlag',index:"fBatchFlag", hidden:true},	
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
	if(targetWerks!=''){
		var lgortList=getLgortList(targetWerks);
		showData(targetWerks,lgortList);
	}
//	console.log("targetWerks",targetWerks);
//	var ids = $("#dataGrid").jqGrid("getDataIDs");
//	console.log("ids",ids);
//	for (var i = 0; i < ids.length; i++) {
//		console.log("ids[i]",ids[i]);
//		$("#dataGrid").jqGrid('setCell', ids[i], "werks", targetWerks,'not-editable-cell');
//	}
}
function getLgortList(targetWerks){
	//查询库位
	var lgortList=":;";
  	$.ajax({
	  	url:baseUrl + "common/getLoList",
	  	async:false,
	  	data:{
	  		"WERKS":targetWerks,
	  	},
	  	success:function(resp){
	  		for(var i in resp.data){
	  			lgortList+=resp.data[i].LGORT+":"+resp.data[i].LGORT+";"
	  		}
	  		console.log("lgortList",lgortList);
	  	}
	 });
  	lgortList=lgortList.length>1 ? lgortList.substring(0,lgortList.length-1) : '';
  	return lgortList;
}