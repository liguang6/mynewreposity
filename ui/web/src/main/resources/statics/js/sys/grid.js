//国际化取值
var smallUtil=new smallTools();
var array = new Array("COLUMNNAME","GRIDID","HIDERMK","DEFHIDE","IDXSEQ","COLUMNWIDTH","COLUMNNO");
var languageObj = smallUtil.loadlanguageByPKeyAndLKey(array,"M");
var lastrow,lastcell,lastcellname;
var maxlist;

$(document).ready(function(){
	$("#dataGrid1").tableDnD({
		scrollAmount : 1
		});

});
function getGridNo(GridNo){
	$("#gridNo").val(GridNo);
}
function init(){
    $('#dataGrid1').dataGrid({
    	searchForm: $("#searchForm"),
       columnModel: [    	
           { label: '列名称', name: 'COLUMNNAME',index:'COLUMNNAME', width: 80,align:'center' },
           { label: '是否隐藏', name: 'HIDERMK',index:'HIDERMK', width: 80,align:'center' ,editable:true, edittype:'select', formatter:function(val, obj, row, act){
				if(val =='0'){
					return "否";
				} else if (val =='1'){
					return "是";
				}
			} },
    //       { label:'排列序号', name: 'IDXSEQ',  index:'IDXSEQ', width: 80,align:'center',editable:true },
           { label: '列宽', name: 'COLUMNWIDTH', index:'COLUMNWIDTH', width: 80,align:'center'  ,editable:true },
           { label: 'columnId', name: 'COLUMNID',index:'COLUMNID',key: true ,hidden:true}
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
				$('#dataGrid1').jqGrid('setSelection', rowid);
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
			var rec = $('#dataGrid1').jqGrid('getRowData', rowid);
			//下拉框值
			if(cellname=='HIDERMK'){					
	            $('#dataGrid1').jqGrid('setColProp', 'HIDERMK', { editoptions: {value:"0:否 ;1:是"} });
			}
		},
	    gridComplete:function(){
	    	//隐藏grid底部滚动条
	    	$("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" }); 
	    	$("#_empty", "#dataGrid1").addClass("nodrag nodrop");//样式
	    	$("#dataGrid1").tableDnDUpdate();//更新jquery.tablednd.js插件的方法。
	    }
   });
}

$("#btnSubmit").click(function()	{
	var flag=true;
	var selectedRows=[];
	$("#cb_dataGrid1").click()
	$('#dataGrid1').jqGrid("saveCell", lastrow, lastcell);
	var cm = $('#dataGrid1').jqGrid('getGridParam', 'colModel'); 
	var rows=$('#dataGrid1').getGridParam("selarrrow");
	if(rows.length==0){
		js.alert(languageObj.PLEASE_SELECT);
		return false;
	}
	$.each(rows,function(i,rowid){
		var row = $("#dataGrid1").jqGrid("getRowData",rowid);
		row["gridId"]=$("#gridId").val();
		console.info(row)
		row["IDXSEQ"]=i+1;
		flag=checkRequiredCell(cm, row);
		if(flag){
			selectedRows.push(row);
		}
	})
	
	if(flag){
		var url = "sys/columnConfiguration/userSave";
		saveConfirm(selectedRows,url);
	}	
});


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

function checkRequiredCell(cm,row){
	console.info("checkRequiredCell")
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
	console.info("saveConfirm")
	
	$.ajax({
		url:baseUrl+url,
		type:"post",
		async:false,
		dataType:"json",
		data:{
			columnList:JSON.stringify(selectedRows)
		},
		success:function(response){
			if(response.code==500){
				return false;
			}
			js.showMessage(response.msg);
        	$("#resultMsg").html(response.msg);
//        	layer.open({
//        		type : 1,
//        		offset : '30px',
//        		skin : 'layui-layer-molv',
//        		title : languageObj.PROMPT_MESSAGE,
//        		area : [ '500px', '250px' ],
//        		shade : 0,
//        		shadeClose : false,
//        		content : jQuery("#resultLayer"),
//        		btn : [languageObj.CONFIRM],
//        		btn1 : function(index) {
//        			layer.close(index);
////        			$("#dataGrid").jqGrid('setGridParam').trigger('reloadGrid');
//        			vm.refresh();
//        		}
//        	});
		}
	})
	
}