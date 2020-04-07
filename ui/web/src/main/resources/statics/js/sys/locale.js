var lastrow,lastcell,lastcellname;
var maxlist;
//国际化取值
var smallUtil=new smallTools();
var array = new Array("KEY_VALUE","LANGUAGE","DEFAULT_LENGTH","ENGLISH","CHINESE","SHORT","LONG","MIDDLE",
		"MIDDLE_DESC","SHORT_DESC","LONG_DESC","PLEASE_SELECT","PLEASE_FILL_IN","CONFIRM","PROMPT_MESSAGE","ENTER_QUERY");
var languageObj = smallUtil.loadlanguageByPKeyAndLKey(array,"M");

$(function () {
    $("#dataGrid").dataGrid({
    	searchForm: $("#searchForm"),
    	datatype : "local",
        columnModel: [
        	{ label:'ID',name:'id', index: 'id',key: true ,hidden:true},
			{ label: '<span style="color:red">*</span><span style="color:blue">'+languageObj.KEY_VALUE+'</span>', display:languageObj.KEY_VALUE, name: 'pKey', index: 'pKey', width: 80, editable:true,editrules:{required:true} },
			{ label: '<span style="color:red">*</span><span style="color:blue">'+languageObj.LANGUAGE+'</span>', display:languageObj.LANGUAGE, name: 'lKey', index: 'lKey', width: 80, editable:true, edittype:'select', 
				formatter:function(val, obj, row, act){
				if(val == "EN_US"){
					return languageObj.ENGLISH;
				} else if(val == "ZH_CN"){
					return languageObj.CHINESE;
				} else {
					return languageObj.CHINESE;
				}
			},editrules:{required:true}}, 			
			{ label: '<span style="color:red">*</span><span style="color:blue">'+languageObj.DEFAULT_LENGTH+'</span>', display:languageObj.DEFAULT_LENGTH,name: 'descDef', index: 'descDef', width: 80, editable:true,edittype:'select', 
				editrules:{required:true},formatter:function(val, obj, row, act){
				if(val == "S"){
					return languageObj.SHORT;
				} else if(val == "L"){
					return languageObj.LONG;
				} else {
					return languageObj.MIDDLE;
				}
			}}, 
			{ label: languageObj.MIDDLE_DESC, name: 'middleDesc', index: 'middleDesc', width: 80, editable:true }, 
			{ label: languageObj.SHORT_DESC, name: 'shortDesc', index: 'shortDesc', width: 80, editable:true },
			{ label: languageObj.LONG_DESC, name: 'longDesc', index: 'longDesc', width: 80, editable:true }
			],
		    viewrecords: true,
		    cellEdit : true,
		    cellurl : '#',
		    cellsubmit : 'clientArray',
		    viewrecords: false,
		    rowNum: 15,
		    rownumbers: true, 
		    rownumWidth: 25, 
		    autowidth:true,
		    multiselect: true,
		    beforeSelectRow : function(rowid, e) { // 点击复选框才选中行
				var $myGrid = $(this), 
				i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),
				cm = $myGrid.jqGrid('getGridParam', 'colModel');
				if (cm[i].name == 'cb') {
					$('#dataGrid').jqGrid('setSelection', rowid);
				}
				return (cm[i].name == 'cb');
			},
			beforeEditCell:function(rowid, cellname, v, iRow, iCol){
	        	//编辑之前先保存当前编辑的行号列号
				lastrow=iRow;
				lastcell=iCol;
				lastcellname=cellname;
			},
			formatCell:function(rowid, cellname, value, iRow, iCol){
				var rec = $('#dataGrid').jqGrid('getRowData', rowid);
				//console.info(JSON.stringify(rec))
				//下拉框值
				if(cellname=='lKey'){					
		            $('#dataGrid').jqGrid('setColProp', 'lKey', { editoptions: {value:getLanguageOption()}
		            });
				}
				if(cellname=='descDef'){					
		            $('#dataGrid').jqGrid('setColProp', 'descDef', { editoptions: {value:getDescDefOption()}
		            });
				}
			},
		    gridComplete:function(){
		    	//隐藏grid底部滚动条
		    	$("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" }); 
		    }
    });
});

var vm = new Vue({
	el:'#rrapp',
	data:{
		items:[],
	},
	methods: {
		query: function () {
			if ($("input[name='pkey']").val() == '' && $("input[name='mdesc']").val() == '') {
//				alert(languageObj.ENTER_QUERY);
				js.showErrorMessage(languageObj.ENTER_QUERY);
				return false;
			}
			js.loading();
			vm.$nextTick(function(){//数据渲染后调用
				$("#dataGrid").jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid'); 
				js.closeLoading();
			})
	
		},
        add: function(){
        	var records = $('#dataGrid').jqGrid('getGridParam','records');
			var item = {};
			var ids=$("#dataGrid").jqGrid('getRowData');
			for ( var i = 0; i <ids.length; i++){
				if(ids[i].id =="") {
					records = maxlist;
				} else {
					records = ids[i].id;
				}
				
			}
			maxlist = Number(records) + Number(1);
			$("#dataGrid").jqGrid("addRowData",maxlist,item);
        },
        remove:function(){
        	var dflag= false;
        	var deleteRows=[];
        	var rows=getSelectedRows();
			if(rows.length==0){
				js.alert(languageObj.PLEASE_SELECT);
				return false;
			}
			for(i=0;i<rows.length;i++){
				var rowData = $("#dataGrid").jqGrid('getRowData',rows[i]);
//				alert(JSON.stringify(rowData));
//				alert(rowData.id);
				if (rowData.id != ""){
					deleteRows.push(rowData);
					dflag = true;
				} else {
					$("#dataGrid").delRowData(rows[i]);
//					$("#dataGrid").jqGrid("delGridRow",rows);
				}
				
			}
			
			if(dflag){
//				js.alert("不允许删除已保存到数据库的国际化KEY，只能更新！");
//				var url = "sys/language/delete";
//				if(confirm('确定要删除选中的记录？')){
//					saveConfirm(deleteRows,url);
//				}
				
			}
		},
		save:function()	{
			var flag=true;
			var selectedRows=[];
//			alert(JSON.stringify($("#dataGrid").jqGrid('getRowData')));
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			var cm = $('#dataGrid').jqGrid('getGridParam', 'colModel'); 
			var rows=getSelectedRows();
			if(rows.length==0){
				js.alert(languageObj.PLEASE_SELECT);
				return false;
			}
			//alert(rows.length)
			$.each(rows,function(i,rowid){
				var row = $("#dataGrid").jqGrid("getRowData",rowid);
				console.info(row)
				flag=checkRequiredCell(cm, row);
				if(flag){
					selectedRows.push(row);
				}
			})
			
			if(flag){
				var url = "sys/language/save";
				saveConfirm(selectedRows,url);
			}		
		},
		refresh:function(){
			$("#searchForm").submit();
        }
	}
});


function getLanguageOption(){
	var sobkz_list = getDictList("LANGUAGE");
	var options="";
	for(i=0;i<sobkz_list.length;i++){
			if(i != sobkz_list.length - 1) {
				options += sobkz_list[i].CODE + ":" +sobkz_list[i].VALUE + ";";
			} else {
				options += sobkz_list[i].CODE + ":" + sobkz_list[i].VALUE;
			}
	}
	return options;
//	return "ZH_CN:"+languageObj.CHINESE+" ;EN_US:"+languageObj.ENGLISH;
}

function getDescDefOption(){
	return "L:"+languageObj.LONG+" ;M:"+languageObj.MIDDLE+";S:"+languageObj.SHORT;
}

function checkRequiredCell(cm,row){
	   var flag=true;
	   $.each(cm,function(k,o){
		   if(o.editrules&&o.editable){
			   //console.info($("#dataGrid").jqGrid('getColProp',o.name))
			   if(o.editrules.required&&(row[o.name]==undefined||row[o.name].trim().length==0)){
				   flag=false;
				   js.showErrorMessage(smallUtil.getPKey("PLEASE_FILL_IN","M",o.display))
				   return false;
			   }
		   }
	   })
	   
	   return flag;
}

function saveConfirm(selectedRows,url){
	
	
	$.ajax({
		url:baseUrl+url,
		type:"post",
		async:false,
		dataType:"json",
		data:{
			languageList:JSON.stringify(selectedRows)
		},
		success:function(response){
			if(response.code==500){
				return false;
			}
			
//			$('#dataGrid').jqGrid("clearGridData");
			
			
        	$("#resultMsg").html(response.msg);
        	layer.open({
        		type : 1,
        		offset : '30px',
        		skin : 'layui-layer-molv',
        		title : languageObj.PROMPT_MESSAGE,
        		area : [ '500px', '250px' ],
        		shade : 0,
        		shadeClose : false,
        		content : jQuery("#resultLayer"),
        		btn : [languageObj.CONFIRM],
        		btn1 : function(index) {
        			layer.close(index);
//        			$("#dataGrid").jqGrid('setGridParam').trigger('reloadGrid');
        			vm.refresh();
        		}
        	});
		}
	})
	
}
