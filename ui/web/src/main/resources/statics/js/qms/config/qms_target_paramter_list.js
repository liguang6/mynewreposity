$(function () {
    $("#dataGrid").dataGrid({
    	searchForm: $("#searchForm"),
        columnModel: [			
			{ label: '工厂', name: 'werks', index: 'werks', width: 80,align:'center' },
			{ label: '检验分类', name: 'testTypeDesc', index: 'testTypeDesc', width: 80,align:'center'  }, 
			{ label: '质量目标参数', name: 'testNode', index: 'testNode', width: 80,align:'center'  }, 	
			{ label: '目标类型', name: 'targetTypeDesc', index: 'targetTypeDesc', width: 80,align:'center'  }, 
			{ label: '目标值', name: 'targetValue', index: 'targetValue', width: 80,align:'center'  },
			{ label: '有效开始日期', name: 'startDate', index: 'startDate', width: 80,align:'center'  }, 
			{ label: '有效结束日期', name: 'endDate', index: 'endDate', width: 80,align:'center'  }, 
			{ label: '创建人员', name: 'creator', index: 'creator', width: 80,align:'center'  }, 			
			{ label: '创建时间', name: 'createDate', index: 'createDate', width: 80,align:'center'  },
			{ label: '更新人员', name: 'editor', index: 'editor', width: 80,align:'center'  }, 			
			{ label: '更新时间', name: 'editDate', index: 'editDate', width: 80,align:'center'  },
			{ label: 'ID', name: 'id', index: 'id', hidden:true },
			{ label: 'TARGET_TYPE', name: 'targetType', index: 'targetType', hidden:true },
			{ label: 'TEST_TYPE', name: 'testType', index: 'testType', hidden:true }, 
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
				    url: baseURL + "qms/config/targetParamter/delById/"+grData.id,
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
	    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>新增质量目标参数',
	    		area: ["560px", "360px"],
	    		content: "qms/config/qms_target_paramter_edit.html",  
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
				title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>修改质量目标参数',
				area: ["560px", "360px"],
				content: 'qms/config/qms_target_paramter_edit.html',
				btn: ['<i class="fa fa-check"></i> 确定'],
				success:function(layero, index){
					var win = top[layero.find('iframe')[0]['name']];
					win.vm.saveFlag = false;
					win.vm.getInfo(grData.id);
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
		testType:'',
		testNodeList:[],
		testNode:''
	},
	watch:{
		testType : {
		  handler:function(newVal,oldVal){
			  console.log("newVal",newVal);
			  if(newVal==''){
				  vm.testNodeList=[];
				  return;
			  }
			  this.getTestNodeList();
		  }
		},
	},
	methods: {
		refresh:function(){
			$("#searchForm").submit();
        },
		reload: function (event) {
			$("#searchForm").submit();
		},
		getTestNodeList:function(){
			$.ajax({
				url:baseURL+"qms/config/checkNode/getList",
				dataType : "json",
				type : "post",
				data : {
					"testType":$("#testType").val()
				},
				async: true,
				success: function (response) { 
					vm.testNodeList=response.data;
				}
			});	
		},
	}
});
function export2Excel(){
	var column=[
		{ "title": '工厂', "data": 'werks', "class":'center' },
		{ "title": '检验分类', "data": 'testTypeDesc', "class":'center'  }, 
		{ "title": '质量目标参数', "data": 'testNode', "class":'center'  }, 	
		{ "title": '目标类型', "data": 'targetTypeDesc', "class":'center'  }, 
		{ "title": '目标值', "data": 'targetValue', "class":'center'  },
		{ "title": '有效开始日期', "data": 'startDate', "class":'center'  }, 
		{ "title": '有效结束日期', "data": 'endDate',"class":'center'  }, 
		{ "title": '创建人员', "data": 'creator',"class":'center'  }, 			
		{ "title": '创建时间', "data": 'createDate', "class":'center'  },
		{ "title": '更新人员', "data": 'editor',"class":'center'  }, 			
		{ "title": '更新时间', "data": 'editDate', "class":'center'  },
      ]	;
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"qms/config/targetParamter/list",
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) { 
		    results=response.page.list;		    
		}
	})
	var rowGroups=[];
	getMergeTable(results,column,rowGroups,"质量目标参数");	
}
function listselectCallback(index,rtn){
	rtn = rtn ||false;
	if(rtn){
		//操作成功，刷新表格
		vm.reload();
		parent.layer.close(index);
	}
}