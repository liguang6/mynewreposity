$(function () {
    $("#dataGrid").dataGrid({
    	searchForm: $("#searchForm"),
        columnModel: [			
			{ label: '检验类别', name: 'TEST_TYPE', index: 'TEST_TYPE', width: 80,align:'center' },
			{ label: '检验节点', name: 'TEST_NODE', index: 'TEST_NODE', width: 80,align:'center'  }, 	
			{ label: '按车号/VIN号录入', name: 'VIN_FLAG', index: 'VIN_FLAG', width: 80,align:'center'  }, 
			{ label: '是否自编号', name: 'CUSTOM_NO_FLAG', index: 'CUSTOM_NO_FLAG', width: 80,align:'center'  }, 
			{ label: '创建人员', name: 'CREATOR', index: 'CREATOR', width: 80,align:'center'  }, 			
			{ label: '创建时间', name: 'CREATE_DATE', index: 'CREATE_DATE', width: 80,align:'center'  },
			{ label: '更新人员', name: 'EDITOR', index: 'EDITOR', width: 80,align:'center'  }, 			
			{ label: '更新时间', name: 'EDIT_DATE', index: 'EDIT_DATE', width: 80,align:'center'  },
			{ label: 'ID', name: 'ID', index: 'ID', hidden:true },
		],
		viewrecords: true,
		rowNum: 15
    });
  //------删除操作---------
	  $("#deleteOperation").click(function(){
		  var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
		  if (gr != null  && gr!==undefined) {
			  layer.confirm('确定删除选中的记录?', {icon: 3, title:'提示'}, function(index){
				  var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
				  $.ajax({
					type: "POST",
				    url: baseURL + "qms/config/checkNode/delById/"+grData.ID,
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
	  //------新增操作---------
	  $("#newOperation").click(function(){
		   var options = {
	    		type: 2,
	    		maxmin: true,
	    		shadeClose: true,
	    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>新增检验节点',
	    		area: ["560px", "260px"],
	    		content: "qms/config/qms_check_node_edit.html",  
	    		btn: ['<i class="fa fa-check"></i> 确定'],
	    		success:function(layero, index){
	    			var win = top[layero.find('iframe')[0]['name']];
	    			win.vm.saveFlag = false;
	    		},
	    		btn1:function(index,layero){
					var win = top[layero.find('iframe')[0]['name']];
					var flag=false;
					
					var btnSubmit= $("#btnSubmit", layero.find("iframe")[0].contentWindow.document);
					$(btnSubmit).click();
					if(typeof listselectCallback == 'function'){
						listselectCallback(index,win.vm.saveFlag);
					}
	    		}
	    	};
	    	options.btn.push('<i class="fa fa-close"></i> 关闭');
	    	js.layer.open(options);
	  });
	  //-------编辑操作--------
	  $("#editOperation").click(function(){
		  var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
		 if(gr !== null && gr!==undefined){
			 var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
			 var options = {
				type: 2,
				maxmin: true,
				shadeClose: true,
				title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>修改检验节点',
				area: ["560px", "260px"],
				content: 'qms/config/qms_check_node_edit.html',
				btn: ['<i class="fa fa-check"></i> 确定'],
				success:function(layero, index){
					var win = top[layero.find('iframe')[0]['name']];
					win.vm.saveFlag = false;
					win.vm.getInfo(grData.ID);
					//$(win.document.body).find("#werks").attr("readonly",true);
					//$(win.document.body).find("#whNumber").attr("readonly",true);
				},
				btn1:function(index,layero){
					var win = top[layero.find('iframe')[0]['name']];
					var btnSubmit= $("#btnSubmit", layero.find("iframe")[0].contentWindow.document);
					$(btnSubmit).click();
					if(typeof listselectCallback == 'function'){
						listselectCallback(index,win.vm.saveFlag);
					}
				}
			};
			options.btn.push('<i class="fa fa-close"></i> 关闭');
			options['btn'+options.btn.length] = function(index, layero){
				if(typeof listselectCallback == 'function'){
				}          
			};
			js.layer.open(options);
		 }else{
			 layer.msg("请选择要编辑的行",{time:1000});
		 }
	  });
});

var vm = new Vue({
	el:'#rrapp',
	data:{
	},
	methods: {
		refresh:function(){
			$("#searchForm").submit();
        },
		reload: function (event) {
			$("#searchForm").submit();
		},
	}
});
function export2Excel(){
	var column=[
		{ "title": '订单类型', "data": 'TEST_TYPE', "class":'center' },
		{ "title": '检验节点', "data": 'TEST_NODE', "class":'center'  }, 	
		{ "title": '按车号/VIN号录入', "data": 'VIN_FLAG', "class":'center'  }, 
		{ "title": '是否自编号', "data": 'CUSTOM_NO_FLAG', "class":'center'  }, 
		{ "title": '创建人员', "data": 'CREATOR', "class":'center'  }, 			
		{ "title": '创建时间', "data": 'CREATE_DATE', "class":'center'  },
		{ "title": '更新人员', "data": 'EDITOR', "class":'center'  }, 			
		{ "title": '更新时间', "data": 'EDIT_DATE', "class":'center'  },
      ]	;
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"qms/config/checkNode/list",
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) { 
		    results=response.page.list;		    
		}
	})
	var rowGroups=[];
	getMergeTable(results,column,rowGroups,"品质检验节点");	
}
function listselectCallback(index,rtn){
	rtn = rtn ||false;
	if(rtn){
		//操作成功，刷新表格
		vm.reload();
		parent.layer.close(index);
	}
}