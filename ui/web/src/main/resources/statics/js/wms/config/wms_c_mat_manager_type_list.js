$(function () {
    $("#dataGrid").dataGrid({
    	searchForm: $("#searchForm"),
        columnModel: [			
			{ label: '工厂代码', name: 'WERKS', index: 'WERKS', width: 80,align:'center'},
			{ label: '仓库号', name: 'WH_NUMBER', index: 'WH_NUMBER', width: 80,align:'center'}, 			
			{ label: '物料管理方式', name: 'MAT_MANAGER_TYPE_DESC', index: 'MAT_MANAGER_TYPE_DESC', width: 80,align:'center'}, 
			{ label: '授权码', name: 'AUTHORIZE_CODE', index: 'AUTHORIZE_CODE', width: 80,align:'center'},
			{ label: '授权码名称', name: 'AUTHORIZE_NAME', index: 'AUTHORIZE_NAME', width: 80,align:'center'},		
			{ label: '类型', name: 'MANAGER_TYPE_DESC', index: 'MANAGER_TYPE_DESC', width: 80,align:'center'},		
			{ label: '管理员工号', name: 'MANAGER_STAFF', index: 'MANAGER_STAFF', width: 80,align:'center'},	
			{ label: '管理员姓名', name: 'MANAGER', index: 'MANAGER', width: 80,align:'center'},	
			{ label: '主管工号', name: 'LEADER_STAFF', index: 'LEADER_STAFF', width: 80,align:'center'},
			{ label: '主管姓名', name: 'LEADER', index: 'LEADER', width: 80,align:'center'},
			{ label: '录入人员', name: 'CREATOR', index: 'CREATOR', width: 80,align:'center'}, 			
			{ label: '录入时间', name: 'CREATE_DATE', index: 'CREATE_DATE', width: 80,align:'center'},
			{ label: 'ID', name: 'ID', index: 'ID', hidden:true },
			{ label: 'MANAGER_TYPE', name: 'MANAGER_TYPE', index: 'MANAGER_TYPE', hidden:true },
			{ label: 'MAT_MANAGER_TYPE', name: 'MAT_MANAGER_TYPE', index: 'MAT_MANAGER_TYPE', hidden:true },
		],
		rowNum : 15,
    });
  //------删除操作---------
	  $("#deleteOperation").click(function(){
		  var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
		  if (gr != null  && gr!==undefined) {
			  layer.confirm('确定删除选中的记录?', {icon: 3, title:'提示'}, function(index){
				  var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
				  
				  $.ajax({
					type: "POST",
				    url: baseURL + "config/matmanagertype/delById?id="+grData.ID,
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
	    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>新增人料关系模式',
	    		area: ["770px", "320px"],
	    		content: baseURL+"wms/config/wms_c_mat_manager_type_edit.html",  
	    		btn: ['<i class="fa fa-check"></i> 确定'],
	    		success:function(layero, index){
	    			var win = layero.find('iframe')[0].contentWindow;
	    			win.vm.saveFlag = false;
	    		},
	    		btn1:function(index,layero){
	    			var win = layero.find('iframe')[0].contentWindow;
					var flag=false;
					
					var btnSubmit= $("#btnSubmit", layero.find("iframe")[0].contentWindow.document);
					$(btnSubmit).click();
					if(typeof listselectCallback == 'function'){
						listselectCallback(index,win.vm.saveFlag);
					}
	    		}
	    	};
	    	options.btn.push('<i class="fa fa-close"></i> 关闭');
	    	options['btn'+options.btn.length] = function(index, layero){
         };
         js.layer.open(options);
	  });
	  //-------编辑操作--------
	  $("#editOperation").click(function(){
		  var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
		 if(gr !== null && gr!==undefined){
			 var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
			 console.log('grData',grData);
			 var options = {
				type: 2,
				maxmin: true,
				shadeClose: true,
				title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>修改人料关系模式',
				area: ["770px", "320px"],
				content: baseURL+'wms/config/wms_c_mat_manager_type_edit.html',
				btn: ['<i class="fa fa-check"></i> 确定'],
				success:function(layero, index){
					var win = layero.find('iframe')[0].contentWindow;
					win.vm.saveFlag = false;
					win.vm.getInfo(grData.ID);
					$(win.document.body).find("#werks").attr("readonly",true);
					//$(win.document.body).find("#areaCode").attr("readonly",true);
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
			options['btn'+options.btn.length] = function(index, layero){
				if(typeof listselectCallback == 'function'){
				}         
			};
			js.layer.open(options);
		 }else{
			 layer.msg("请选择要编辑的行",{time:1000});
		 }
	  });
	  //-------导入操作--------
	  $("#importOperation").click(function(){
		  var options = {
				type: 2,
				maxmin: true,
				shadeClose: true,
				title: "<i class='fa fa-upload' aria-hidden='true'>&nbsp;</i> 批量新增仓库人料关系模式",
				area: ["820px", "520px"],
				content: baseUrl + "wms/config/wms_c_mat_manager_type_upload.html",
				btn: ['<i class="fa fa-check"></i> 保存'],
				
				btn1:function(index,layero){
					var win = layero.find('iframe')[0].contentWindow;
					win.vm.saveUpload();
					// 保存成功 关闭窗口
					if(win.vm.saveFlag){
						if(typeof listselectCallback == 'function'){
							listselectCallback(index,true);
						}
					}
				}
			};
			options.btn.push('<i class="fa fa-close"></i> 关闭');
			js.layer.open(options);
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
		getPlantNoFuzzy:function(){
			getPlantNoSelect("#werks",null,null);
		},
	}
});
function export2Excel(){
	var column=[
      	{ "title": '工厂代码', "data": 'WERKS', "class":'center' },
      	{ "title": '仓库号', "data": 'WH_NUMBER', "class":'center' },
      	{ "title": '物料管理方式', "data": 'MAT_MANAGER_TYPE_DESC', align:'center'}, 
		{ "title": '授权码', "data": 'AUTHORIZE_CODE',"class":'center'},
		{ "title": '授权码名称', "data": 'AUTHORIZE_NAME',"class":'center'},		
		{ "title": '类型', "data": 'MANAGER_TYPE_DESC',"class":'center'},		
		{ "title": '管理员工号', "data": 'MANAGER_STAFF',"class":'center'},	
		{ "title": '管理员姓名', "data": 'MANAGER',"class":'center'},	
		{ "title": '主管工号', "data": 'LEADER_STAFF',"class":'center'},
		{ "title": '主管姓名', "data": 'LEADER',"class":'center'},
		{ "title": '录入人员', "data": 'CREATOR', "class":'center'  }, 			
		{ "title": '录入时间', "data": 'CREATE_DATE',"class":'center'  },
      ]	;
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"config/matmanagertype/list",
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) { 
		    results=response.page.list;		    
		}
	})
	var rowGroups=[];
//	alert(JSON.stringify(results));
//	alert(JSON.stringify(column));
	getMergeTable(results,column,rowGroups,"仓库人料关系模式");	
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