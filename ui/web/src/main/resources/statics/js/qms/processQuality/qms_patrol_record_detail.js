var lastrow,lastcell;
var sourcedata=[];
var dataRow = {};
var vm = new Vue({
	el:'#app-vue',
	data:{
		saveFlag:false,
		showList:true,
	},
	methods: {
		saveOrUpdate: function (event) {
			var save_flag=true;
			//保存编辑的最后一个单元格
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			var saveData=[];
			//获取选中表格数据
			var ids = $("#dataGrid").jqGrid("getGridParam","selarrrow");	

			if(ids.length == 0){
				js.showErrorMessage("当前还没有勾选任何行项目数据！");
				save_flag=false;
			}
			for (var i = 0; i < ids.length; i++) {
				var data=$("#dataGrid").jqGrid('getRowData',ids[i]);
//				if(data["testItem"]=='' || data["testStandard"]==''){
//					js.showErrorMessage('第'+(i+1)+'行检验项目、检验要求不能为空');
//					save_flag=false;
//					return false;
//				}
//				if(data["testGroup"]==''){
//					js.showErrorMessage('第'+(i+1)+'行检验分组不能为空');
//					save_flag=false;
//					return false;
//				}
				saveData[i] = data;
				delete saveData[i]["actions"]; 
			}
			console.log("saveData",saveData);
			var url = baseURL+"qms/patrolRecord/saveOrUpdate";
			if(save_flag)
			$.ajax({
				type: "POST",
			    url: url,
			    dataType : "json",
			    async: false,
				data : {
					saveData:JSON.stringify(saveData),
				},
			    success: function(r){
			    	if(r.code === 0){
			        	vm.saveFlag= true;
			        	js.showMessage(r.msg+"：保存成功！");
					}else{
						js.showErrorMessage(r.msg);
					}
				}
			});
		},
		
		showEditTable: function () {
			$('#dataGrid').dataGrid({
				searchForm: $("#searchForm"),
				cellEdit:true,
				cellurl:'#',
				cellsubmit:'clientArray',
				columnModel: [
					{header: '工序名称', name: 'processName',index:"processName", width: "90",align:"center",sortable:false,editable:false}, 	
		            {header: '检验项目', name: 'testItem',index:"testItem", width: "180",align:"center",sortable:false,editable:false}, 
		            {header: '标准要求', name: 'testStandard',index:"testStandard", width: "260",align:"center",sortable:false,editable:false}, 	
		            {header: '检验结果',name:'testResult',index:"testResult", width: "80",align:"center",sortable:false,editable:true},
		            {header:'判定', name:'judge', width:80, align:"center",editable:true,edittype:"select",
						editrules:{required: true},editoptions: {value:":;OK:OK;NG:NG;NA:NA",dataEvents:[]},
					},
					{header: '复检结果',name:'reTestResult',index:"reTestResult", width: "80",align:"center",sortable:false,editable:true},
					{header:'复判', name:'reJudge', width:90, align:"center",editable:true,edittype:"select",
						editrules:{required: true},editoptions: {value:":;OK:OK;NG:NG",dataEvents:[]},
					},
					{header:'ID',name:'id',hidden:true,formatter:"integer"},
				],
				gridComplete :function(){
					js.closeLoading();
					$(".btn").attr("disabled", false);
				},
				beforeEditCell:function(rowid, cellname, v, iRow, iCol){
					lastrow=iRow;
					lastcell=iCol;
				},
		   	 	onCellSelect : function(rowid,iCol,cellcontent,e){
		   	 	   var data =  $("#dataGrid").jqGrid('getRowData', rowid);
                   if(data.judge=='NG'){
                	   $("#dataGrid").jqGrid('setCell',rowid,"reTestResult",'',{background :'blue'});
                	   $("#dataGrid").jqGrid('setCell',rowid,"reJudge",'',{background :'blue'});
                	   $("#dataGrid").jqGrid('setCell', rowid, 'reTestResult', '','editable-cell');
                	   $("#dataGrid").jqGrid('setCell', rowid, 'reJudge', '','editable-cell');
                   }
                   if(data.judge=='OK' || data.judge=='NA'){
                	   $("#dataGrid").jqGrid('setCell', rowid, 'reTestResult', '','not-editable-cell');
                	   $("#dataGrid").jqGrid('setCell', rowid, 'reJudge', '','not-editable-cell');
                	   $("#dataGrid").jqGrid('setCell',rowid,"reTestResult",'',{background :''});
                	   $("#dataGrid").jqGrid('setCell',rowid,"reJudge",'',{background :''});
                   }
			   	},
			   	afterSaveCell:function(rowid, cellname, value, iRow, iCol){
			   		if(value!='' && value!=null){
			   			if(cellname=='judge'){
                           if(value=='NG'){
                        	   $("#dataGrid").jqGrid('setCell',rowid,"reTestResult",'',{background :'blue'});
                        	   $("#dataGrid").jqGrid('setCell',rowid,"reJudge",'',{background :'blue'});
                        	   $("#dataGrid").jqGrid('setCell', rowid, 'reTestResult', '','editable-cell');
                        	   $("#dataGrid").jqGrid('setCell', rowid, 'reJudge', '','editable-cell');
                        	  
                               $("#"+rowid).children().eq(iCol+1).removeClass('not-editable-cell');
                               $("#"+rowid).children().eq(iCol+2).removeClass('not-editable-cell');
                           }
                           if(value=='OK' || value=='NA'){
                        	   $("#dataGrid").jqGrid('setCell', rowid, 'reTestResult', '','not-editable-cell');
                        	   $("#dataGrid").jqGrid('setCell', rowid, 'reJudge', '','not-editable-cell');
                        	   $("#dataGrid").jqGrid('setCell',rowid,"reTestResult",'',{background :''});
                        	   $("#dataGrid").jqGrid('setCell',rowid,"reJudge",'',{background :''});
                           }
						}
			   			
			   		}
				},
				showCheckbox:true,
				viewrecords: true,
		        rownumbers: false, 
		        rownumWidth: 25, 
		        autowidth:true,
		        multiselect: true,
		        beforeSelectRow:function(rowid,e){
		        	var $myGrid = $(this),  
					   i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),  
					   cm = $myGrid.jqGrid('getGridParam', 'colModel'); 
					   if(cm[i].name == 'cb'){
						   $('#dataGrid').jqGrid('setSelection',rowid);
					   }
					   		
					   return (cm[i].name == 'cb'); 
		        },
			});
		},
		showTable:function (){
			$('#dataGrid').dataGrid({
			searchForm: $("#searchForm"),
			cellurl:'#',
			cellsubmit:'clientArray',
			columnModel: [
				//{header: '行项目', name: 'TEMP_ITEM_NO',index:"TEMP_ITEM_NO", width: "90",align:"center",sortable:false}, 	
				{header: '工序名称', name: 'processName',index:"processName", width: "90",align:"center",sortable:false}, 	
	            {header: '检验项目', name: 'testItem',index:"testItem", width: "100",align:"center",sortable:false}, 
	            {header: '标准要求', name: 'testStandard',index:"testStandard", width: "120",align:"center",sortable:false}, 
	            {header: '检验结果',name:'testResult',index:"testResult", width: "120",align:"center",sortable:false},
				{header: '判定',name:'judge',index:"judge", width: "120",align:"center",sortable:false},
				{header: '复检结果',name:'reTestResult',index:"reTestResult", width: "120",align:"center",sortable:false},
				{header: '复判',name:'reJudge',index:"reJudge", width: "120",align:"center",sortable:false},
			],
			gridComplete :function(){
				js.closeLoading();
				$(".btn").attr("disabled", false);
			},		
			viewrecords: true,
	        rownumbers: true, 
	        rownumWidth: 25, 
	        autowidth:true, 
		})
	  },
	}
});
$(function(){
	$("#patrolRecordNo").val(getUrlKey("patrolRecordNo"));
	$("#werks").val(getUrlKey("werks"));
	$("#orderNo").val(getUrlKey("orderNo"));
	$("#testNode").val(getUrlKey("testNode"));
	$("#testor").val(getUrlKey("testor"));
	$("#testDate").val(getUrlKey("testDate"));
	var operateType=getUrlKey("operateType");
	if(operateType=='info'){
		vm.showList=true; // hide保存按钮
		vm.showTable();
	}
	if(operateType=='edit'){
		vm.showList=false;  // show保存按钮
		vm.showEditTable();
	}
});	

function getUrlKey(name){
	return decodeURIComponent((new RegExp('[?|&]'+name+'='+'([^&;]+?)(&|#|;|$)').exec(location.href)||[,""])[1].replace(/\+/g,'%20'))||null;
}


function removeCellClass(rowid,grid,clumnName,className)
    {
        var iCol = getColumnIndexByName(grid, clumnName),
            tr = grid.rows.namedItem(rowid), // grid is defined as grid=$("#grid_id")
            td = tr.cells[iCol];
        $(td).removeClass(className);

    }

    var getColumnIndexByName = function (grid, columnName) {
        var cm = grid.jqGrid('getGridParam', 'colModel');
        var l=l = cm.length;
        for( var i=0; i < l; i++) {
            if (cm[i].name === columnName) {
                return i; // return the index
            }
        }
        ;
    }

