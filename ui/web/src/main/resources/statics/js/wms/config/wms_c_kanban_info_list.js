$(function(){  
	  $("#dataGrid").dataGrid({
	    	searchForm:$("#searchForm"),
	    	datatype: "json",
	        colModel: [
	            {label: '工厂', name: 'WERKS',index:"WERKS", width: 70,align:"center"},
				{label: '仓库号', name: 'WH_NUMBER',index:"WH_NUMBER", width: 70,align:"center"},
				{label: '存储类型代码', name: 'STORAGE_AREA_CODE',index:"STORAGE_AREA_CODE", width: 70,align:"center",hidden:true},
				{label: '类型', name: 'KANBAN_TYPE',index:"KANBAN_TYPE", width: 80,align:"center"},
				{label: '消息', name: 'KANBAN_MSG',index:"KANBAN_MSG", width: 80,align:"center"},
				{label: '信息开始显示时间', name: 'START_DATE',index:"START_DATE", width: 120,align:"center" },
				{label:'信息结束显示时间',name:'END_DATE',index:"END_DATE",width: 120,align:"center"},
				{label:'最后更新人',name:'EDITOR',index:"EDITOR",width: 80,align:"center"},
				{label:'最后更新时间',name:'EDIT_DATE',index:"EDIT_DATE",width: 80,align:"center"},
				{label:'ID',width:75,name:'ID',index:"ID",align:"center",hidden:true},
	        ],
	        viewrecords: true,
	        rowNum: 15,
	        rownumWidth: 25, 
	        showCheckbox:true
	    });

	  //------删除操作---------
	  $("#deleteOperation").click(function(){
		  var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
		  if (gr != null) {
			  layer.confirm('确定删除选中的记录?', {icon: 3, title:'提示'}, function(index){
				  //do something
				  var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
				  $.ajax({
					  url:baseUrl + "config/KanbanInfo/delById",
					  type:"post",
					  data:{"id":grData.ID},
					  success:function(resp){
						  if(resp.code === 0){
							  js.showMessage('删除成功!');
							  $("#searchForm").submit();//重新查询数据
						  }else{
							 alert("删除失败,"+resp.msg);
						  }
						  layer.close(index);
					  }
				  }); 
				});
		  } 
		  else layer.msg("请选择要删除的记录",{time:2000});
	  });
	  //------新增操作---------
	  $("#newOperation").click(function(){
			js.layer.open({
				type: 2,
				title: "新增看板类型",
				area: ["500px", "360px"],
				content: baseUrl + "wms/config/wms_c_kanban_info_edit.html",
				closeBtn:1,
				end:function(){
					$("#searchForm").submit();
				},
				btn: ["确定","取消"],
				yes:function(index,layero){
					//提交表单
					var win = layero.find('iframe')[0].contentWindow;
					var btnSubmit= $("#btnSubmit", layero.find("iframe")[0].contentWindow.document);
					$(btnSubmit).click();
					if(typeof listselectCallback == 'function'){
						listselectCallback(index,win.vm.saveFlag);
					}
				},
				no:function(index,layero){
					layer.close(index);
				}
		});
	  })
	  //-------编辑操作--------
	  $("#editOperation").click(function(){
		  var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
		 if(gr !== null && gr!==undefined){
			 var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
			 var options = {
				type: 2,
				maxmin: true,
				shadeClose: true,
				title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>编辑看板类型',
				area: ["500px", "360px"],
				content: baseUrl + 'wms/config/wms_c_kanban_info_edit.html',
				btn: ['<i class="fa fa-check"></i> 确定'],
				success:function(layero, index){
					var win = layero.find('iframe')[0].contentWindow;
					win.vm.saveFlag = false;
					win.vm.getInfo(grData.ID);
				},
				btn1:function(index,layero){
					var win = layero.find('iframe')[0].contentWindow;
					var btnSubmit= $("#btnSubmit", layero.find("iframe")[0].contentWindow.document);
					$(btnSubmit).click();
					if(typeof listselectCallback == 'function'){
						listselectCallback(index,win.vm.saveFlag);
					}
					
				}
			};
			options.btn.push('<i class="fa fa-close"></i> 关闭');
			js.layer.open(options);
		 }else{
			 layer.msg("请选择要编辑的行",{time:1000});
		 }
	  });
	  
});

var vm = new Vue({
	el:'#rrapp',
	data:{},
	methods: {
		refresh:function(){
			$("#searchForm").submit();
        },
		reload: function (event) {
			$("#searchForm").submit();
		},
		getPlantNoFuzzy:function(){
			getPlantNoSelect("#werks",null,null);
		}
	}
});

function getSelRow(gridSelector){
	var gr = $(gridSelector).jqGrid('getGridParam', 'selrow');//获取选中的行号
	return gr != null?$(gridSelector).jqGrid("getRowData",gr):null;
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