//国际化取值
var smallUtil=new smallTools();
var array = new Array("COLUMNNAME","GRIDID","HIDERMK","DEFHIDE","IDXSEQ","COLUMNWIDTH","COLUMNNO");
var languageObj = smallUtil.loadlanguageByPKeyAndLKey(array,"M");
var lastrow,lastcell,lastcellname;
var maxlist;



$(function () {
    $(document).ready(function(){
		$("#dataGrid").tableDnD({
		scrollAmount : 1
		});
    });

    $("#dataGrid").dataGrid({
        searchForm: $("#searchForm"),
        datatype : "local",
        columnModel: [    	
        	{ label: '列编号', name: 'columnNo',index:'columnNo', width: 80,align:'center',editable:true  },
            { label: '列名称', name: 'columnName',index:'columnName', width: 80,align:'center'  ,editable:true },
            { label: '归属模板ID', name: 'gridId',index:'gridId', width: 80,align:'center',hidden:true  },
            { label: '是否允许隐藏', name: 'hideRmk',index:'hideRmk', width: 80,align:'center' ,editable:true, edittype:'select', formatter:function(val, obj, row, act){
				if(val == 0){
					return "否";
				} else {
					return "是";
				}
			} },
            { label: '默认隐藏', name: 'defHide',  index:'defHide', width: 80,align:'center',editable:true, edittype:'select', formatter:function(val, obj, row, act){
				if(val == 0){
					return "否";
				} else {
					return "是";
				}
			}},
            { label:'排列序号', name: 'idxSeq',  index:'idxSeq', width: 80,align:'center',hidden:true },
            { label: '列宽', name: 'columnWidth', index:'columnWidth', width: 80,align:'center'  ,editable:true,editrules: { required: true, number: true, custom: true, custom_func: ValidateTvalue }, },
            { label: 'columnId', name: 'columnId',index:'columnId',key: true ,hidden:true}
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
			if(cellname=='hideRmk'){					
	            $('#dataGrid').jqGrid('setColProp', 'hideRmk', { editoptions: {value:"0:否 ;1:是"}
	            });
			}
			if(cellname=='defHide'){					
	            $('#dataGrid').jqGrid('setColProp', 'defHide', { editoptions: {value:"0:否 ;1:是"}
	            });
			}
		},
	    gridComplete:function(){
	    	//隐藏grid底部滚动条
	    	$("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" }); 
	    	$("#_empty", "#dataGrid").addClass("nodrag nodrop");//样式
	    	$("#dataGrid").tableDnDUpdate();//更新jquery.tablednd.js插件的方法。
	    }
    });
//格式验证
    function ValidateTvalue(value,name) {
        //#region 验证分数是否为数值
        var regu = "^[0-9]+(.[0-9]{2})?$";
        //var regu = "/^\+?(\d*\.\d{2})$/";
        var re = new RegExp(regu);
        if (re.test(value)) {
            return [true, ""];
        }
        else {
            return [false, "分数【" + rowDatas.columnWidth + "】错误，请输入数值型.如：12或12.23"];
        }
        //#endregion
}
    
    //------删除操作---------
    $("#deleteOperation").click(function(){
        var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
        var grData = $("#dataGrid").jqGrid("getRowData",gr);//获取行数据
        if (gr != null  && gr!==undefined) {
            layer.confirm('确定删除选中的记录?', {icon: 3, title:'提示'}, function(index){
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/columnConfiguration/delete?id="+grData.ID,
                    // data: JSON.stringify(vm.data),
                    contentType: "application/json",
                    success: function(r){
                        if(r.code == 0){
                            js.showMessage('删除成功!');
                            vm.reload();
                        }else{
                            alert(r.msg);
                        }
                    }
                });
                layer.close(index);
            });
        }
        else layer.msg("请选择要删除的行",{time:1000});
    });


});



var vm = new Vue({
	el:'#rrapp',
	data:{
		items:[],
	},
	methods: {
		query: function () {
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

				if (rowData.id != ""){
					deleteRows.push(rowData);
					dflag = true;
				} else {
					$("#dataGrid").delRowData(rows[i]);

				}
				
			}
			
			if(dflag){
				var url = "sys/columnConfiguration/delete";
				if(confirm('确定要删除选中的记录？')){
					saveConfirm(deleteRows,url);
				}
				
			}
		},
		save:function()	{
			var flag=true;
			var selectedRows=[];
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			$("#cb_dataGrid").click()
			var cm = $('#dataGrid').jqGrid('getGridParam', 'colModel'); 
			var rows=getSelectedRows();
			if(rows.length==0){
				js.alert(languageObj.PLEASE_SELECT);
				return false;
			}
			$.each(rows,function(i,rowid){
				var row = $("#dataGrid").jqGrid("getRowData",rowid);
				row["gridId"]=$("#menuId").val();
				console.info(row);
				row["idxSeq"]=i+1;
				flag=checkRequiredCell(cm, row);
				if(flag){
					selectedRows.push(row);
				}
			})
			
			if(flag){
				var url = "sys/columnConfiguration/save";
				saveConfirm(selectedRows,url);
			}		
		},
		refresh:function(){
			$("#searchForm").submit();
        }
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
        	vm.refresh();
		}
	})
	
}