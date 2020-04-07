$(function(){
	  
	  $("#dataGrid").dataGrid({
	    	searchForm:$("#searchForm"),
	        datatype: "json",
	        colModel: [			
	            {label: '工厂代码', name: 'werks',index:"werks", width: 70,align:"center"},
	            {label: '业务类型名称', name: 'businessNameText',index:"businessNameText", width: 180,align:"center"},
				{label: '库位', name: 'lgort',index:"lgort", width: 70,align:"center"},
				{label: '批次规则代码', name: 'batchRuleCode',index:"batchRuleCode", width: 80,align:"center"},
				{label: '批次规则描述', name: 'batchRuleText',index:"batchRuleText", width: 200,align:"center"},
				{label: '是否危化品', name: 'dangerFlag',index:"dangerFlag", width: 80,align:"center",formatter: function(item, index){
		 		       var result='';
				       if(item=='0'){result='<span class="label label-danger">否</span>';}
				       if(item=='X'){result='<span class="label label-success">是</span>';}
			    	   return result;
			    }},
				{label: '沿用源批次', name: 'fBatchFlag',index:"fBatchFlag", width: 80,align:"center",formatter: function(item, index){
		 		       var result='';
				       if(item=='0'){result='<span class="label label-danger">否</span>';}
				       if(item=='X'){result='<span class="label label-success">是</span>';}
			    	   return result;
			    }},				
				{label: '维护人', name: 'editor',index:"editor", width: 80,align:"center" },
				{label:'维护时间',name:'editDate',index:"editDate",width: 90,align:"center"},
				{label:'ID',width:75,name:'id',index:"id",align:"center",hidden:true}//隐藏此列
	        ],
			viewrecords: true,
	        rowNum: 15,
	        rownumWidth: 25, 
	        showCheckbox:true
	    });
	  
	
	  //------删除操作---------
	  $("#deleteOperation").click(function(){
		  var gr = $("#dataGrid").jqGrid('getGridParam', 'selarrrow');//获取选中的行号
		  var ids="";
		  for(var i=0;i<gr.length;i++){
			  ids=ids+gr[i]+",";
		  }
		  if (gr != null) {
			  layer.confirm('确定删除选中的记录?', {icon: 3, title:'提示'}, function(index){
				  //do something
				  var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
				  $.ajax({
					  url:baseUrl + "common/batchRules/dels",
					  type:"post",
					  data:{"ids":ids},
					  success:function(resp){
						  if(resp.code === 0){
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
				title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>新增工厂批次规则',
				area: ["450px", "380px"],
				content: baseUrl + "wms/common/wms_c_batchplant_rules_edit.html",
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
				title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>编辑工厂批次规则',
				area: ["450px", "380px"],
				content: baseURL+'wms/common/wms_c_batchplant_rules_edit.html',
				btn: ['<i class="fa fa-check"></i> 确定'],
				success:function(layero, index){
					var win = layero.find('iframe')[0].contentWindow;
					win.vm.saveFlag = false;
					win.vm.getInfo(grData.id);
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
	  
	  $("#copyOperation").click(function(){
			var options = {
	    		type: 2,
	    		maxmin: true,
	    		shadeClose: true,
	    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>复制批次规则',
	    		area: ["860px", "500px"],
	    		content: baseURL+"wms/common/wms_c_batchplant_rules_copy.html",  
	    		btn: ['<i class="fa fa-check"></i> 确定'],
	    		success:function(layero, index){

	    		},
	    		btn1:function(index,layero){
					var win = layero.find('iframe')[0].contentWindow;
					var flag=false;
					
					var btnSave= $("#save", layero.find("iframe")[0].contentWindow.document);
					$(btnSave).click();
					if(typeof listselectCallback == 'function'){
						console.log("win.vm.saveFlag",win.vm.saveFlag);
						listselectCallback(index,win.vm.saveFlag);
					}
	    		}
	    	};
	    	options.btn.push('<i class="fa fa-close"></i> 关闭');
	    	options['btn'+options.btn.length] = function(index, layero){
       };
       js.layer.open(options);
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