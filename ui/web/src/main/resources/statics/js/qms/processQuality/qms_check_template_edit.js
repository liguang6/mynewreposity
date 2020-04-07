var lastrow,lastcell;
var sourcedata=[];
var dataRow = {};
var rowIndex=10000;  // 为避免行号重复 新增行默认从10000开始
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
			var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");	

			if(ids.length == 0){
				alert("当前还没有勾选任何行项目数据！");
				return false;
			}
			for (var i = 0; i < ids.length; i++) {
				var data=$("#dataGrid").jqGrid('getRowData',ids[i]);
				if(data["testItem"]=='' || data["testStandard"]==''){
					js.showErrorMessage('第'+(i+1)+'行检验项目、检验要求不能为空');
					save_flag=false;
					return false;
				}
				if(data["testGroup"]==''){
					js.showErrorMessage('第'+(i+1)+'行检验分组不能为空');
					save_flag=false;
					return false;
				}
				saveData[i] = data;
				delete saveData[i]["actions"]; 
			}
			console.log("saveData",saveData);
			var url = baseURL+"qms/checkTemplate/saveOrUpdate";
			if(save_flag)
			$.ajax({
				type: "POST",
			    url: url,
			    dataType : "json",
			    async: false,
				data : {
					saveData:JSON.stringify(saveData),
					"tempNo":$("#tempNo").val(),
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
		addRow:function(jsondata){
		    var rows = $("#dataGrid").jqGrid('getRowData');
		    //获得当前最大行号（数据编号）
		    var rowid = rows.length;
		    //获得新添加行的行号（数据编号）
		    var newrowid = rowid+1;
		    //使用addRowData方法把dataRow添加到表格中
		    //$("#dataGrid").jqGrid("addRowData", newrowid, dataRow, "last");
	        $("#dataGrid").jqGrid('addRow',{  
	            rowID : rowIndex,  
	            initdata : jsondata,  
	            position :"first",  
	            useDefValues : true,  
	            useFormatter : true,  
	            addRowParams : {extraparam:{  
	            }}  
	        }); 
	        rowIndex++;
		},
		showEditTable: function (batchSelectArr) {
			var batchSelectArr="是:是;否:否";
			$('#dataGrid').dataGrid({
				searchForm: $("#searchForm"),
				cellEdit:true,
				cellurl:'#',
				cellsubmit:'clientArray',
				columnModel: [
					{header:"<a id='addRow' href='#' onClick='vm.addRow({})' title='新增'><i class='fa fa-plus-square'></i>&nbsp;</a>",width:50,align:"center",name:"actions", sortable:false, fixed:true, formatter: function(val, obj, row, act){
						var actions = [];
						actions.push('<a href="#" id=\'del_'+obj.rowId+'\' onclick="delRow(\''+row.id+'\',\''+obj.rowId+'\')"><i class="fa fa-trash-o"></i></a>&nbsp;');
						return actions.join('');
					}, editoptions: {defaultValue: 'new'}},
					{header: '工序名称', name: 'processName',index:"processName", width: "90",align:"center",sortable:false,editable:true}, 	
		            {header: '检验项目', name: 'testItem',index:"testItem", width: "180",align:"center",sortable:false,editable:true}, 
		            {header: '标准要求', name: 'testStandard',index:"testStandard", width: "260",align:"center",sortable:false,editable:true}, 	
		            {header: '检验分组',name:'testGroup',index:"testGroup", width: "80",align:"center",sortable:false,editable:true},
		            {header:'数值必填项', name:'numberFlagDesc', width:80, align:"center",editable:true,edittype:"select",
						editrules:{required: true},editoptions: {value:batchSelectArr,dataEvents:[]},
					},
					{header:'一次交检合格项', name:'onePassedFlagDesc', width:90, align:"center",editable:true,edittype:"select",
						editrules:{required: true},editoptions: {value:batchSelectArr,dataEvents:[]},
					},
					{header:'拍照必录项', name:'photoFlagDesc', width:80, align:"center",editable:true,edittype:"select",
						editrules:{required: true},editoptions: {value:batchSelectArr,dataEvents:[]},
					},
					{header:'巡检项', name:'patrolFlagDesc', width:70, align:"center",editable:true,edittype:"select",
						editrules:{required: true},editoptions: {value:batchSelectArr,dataEvents:[
							{type:"click",fn:function(e){
								var val = this.value;
								var rowId= $("#dataGrid").jqGrid('getGridParam','selrow');
								$("#dataGrid").jqGrid('setCell',rowId,"if_inspection",val);  
							}},
							
						]},
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
		   	 		// 编辑修改 不允许修改sap料号、零部件编号、零部件名称、供应商、车间、线别  字段
//			   	 	var data =  $("#dataGrid").jqGrid('getRowData', rowid);
//					if(data.id!='0'){
//					   $("#dataGrid").jqGrid('setCell', rowid, 'sapMat', '','not-editable-cell');
//					}
			   	},
			   	afterSaveCell:function(rowid, cellname, value, iRow, iCol){
			   		if(value!='' && value!=null){
			   			console.log("value",value);
			   			if(cellname=='check_item'){
//							dataRow=$("#dataGrid").jqGrid('getRowData',rowid);
//							var filtered = sourcedata.filter(dofilter);
//							if(filtered.length>0){
//								alert(value+":检验项目重复！");
//								$("#dataGrid").jqGrid('setCell',rowid,"check_item",' '); 
//								return false;
//							}
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
	            {header: '检验分组',name:'testGroup',index:"testGroup", width: "120",align:"center",sortable:false},
				{header: '数值必填项',name:'numberFlagDesc',index:"numberFlagDesc", width: "120",align:"center",sortable:false},
				{header: '一次交检合格项',name:'onePassedFlagDesc',index:"onePassedFlagDesc", width: "120",align:"center",sortable:false},
				{header: '拍照必录项',name:'photoFlagDesc',index:"photoFlagDesc", width: "120",align:"center",sortable:false},
				{header: '巡检项',name:'patrolFlagDesc',index:"patrolFlagDesc", width: "120",align:"center",sortable:false}
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
	$("#tempNo").val(getUrlKey("tempNo"));
	$("#busTypeCode").val(getUrlKey("busTypeCode"));
	$("#testNode").val(getUrlKey("testNode"));
	$("#testType").val(getUrlKey("testType"));
	$("#orderNo").val(getUrlKey("orderNo"));
	var operateType=getUrlKey("operateType");
	if(operateType=='info'){
		vm.showList=true; // hide保存按钮
		vm.showTable();
	}
	if(operateType=='edit'){
		vm.showList=false;  // show保存按钮
		vm.showEditTable();
	}
	$('#dataGrid').bind('paste', function(e) {
		console.log("-->paste");
		e.preventDefault(); // 消除默认粘贴
		// 获取粘贴板数据
		var data = null;
		var clipboardData = window.clipboardData || e.originalEvent.clipboardData; // IE || chrome
		data = clipboardData.getData('Text');
		// console.log(data.replace(/\t/g, '\\t').replace(/\n/g,'\\n')); //data转码
		// 解析数据
		var arr = data.split('\n').filter(function(item) { // 兼容Excel行末\n，防止出现多余空行
			return (item !== "");
		}).map(function(item) {
			return item.split("\t");
		});
		var checkdata=[];
		for(var i = 0; i < arr.length;i++){
			dataRow={};
			dataRow["processName"]=arr[i][0];
			dataRow["testItem"]=arr[i][1];
			dataRow["testStandard"]=arr[i][2];
			dataRow["testGroup"]=arr[i][3];
			dataRow["numberFlagDesc"]=arr[i][4];
			dataRow["onePassedFlagDesc"]=arr[i][5];
			dataRow["photoFlagDesc"]=arr[i][6];
			dataRow["patrolFlagDesc"]=arr[i][7];
			if(dataRow["testItem"]=='' || dataRow["testStandard"]==''){
				js.showErrorMessage("检验项目/检验要求不能为空");
				return;
			}
//			var filtered = sourcedata.filter(dofilter);
//			if(filtered.length>0){
//				alert(dataRow["testItem"]+":重复！");
//				return false;
//			}
			checkdata.push(dataRow);
		}
		for(var i=0;i<checkdata.length;i++){
			vm.addRow(checkdata[i]);
		}
		$(e.target).parents("tr").remove();
	});
});	

function delRow(id,rowId){
	console.log("id",id);
	// 手动新增行删除
	if(id=='undefined'){
        $('#dataGrid').dataGrid('delRowData',''+rowId+'');
	}else{ //导入记录数据删除 delTplById 
		js.confirm('你确认要删除这条数据吗？', 
				function(){
			$.ajax({
				type: "POST",
				url:baseURL + "qms/checkTemplate/deleteItem/"+id,
				contentType: "application/json",
			    success: function(r){
			    	if(r.code === 0){
			        	js.showMessage(r.msg+"：删除成功！");
			        	$("#dataGrid").jqGrid('delRowData',rowId);
					}else{
						js.showMessage(r.msg);
					}
				}
			});
		  }
		)
	}
}
//filter回调函数
function dofilter(element, index, array) {
	// 零部件名称过滤
	  if(dataRow.testItem && dataRow.testItem == element.testItem){ 
	    return true;
	  }
  return false;
}
function getUrlKey(name){
	return decodeURIComponent((new RegExp('[?|&]'+name+'='+'([^&;]+?)(&|#|;|$)').exec(location.href)||[,""])[1].replace(/\+/g,'%20'))||null;
}