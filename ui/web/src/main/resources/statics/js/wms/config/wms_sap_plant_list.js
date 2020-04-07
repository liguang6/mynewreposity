$(function(){
	  
	  $("#dataGrid").dataGrid({
	    	searchForm:$("#searchForm"),
	        datatype: "json",
	        colModel: [			
				{label: '工厂名称', name: 'werksName',index:"werks_name", width: 200,align:"center"},
				{label: '工厂代码', name: 'werks',index:"werks", width: 200,align:"center"},
				{label:"工厂简称",name: 'shortName',index:"Short_Name",width: 200,align:"center"},
	            {label: '公司名称', name: 'bukrsName', index:"bukrs_name", width: 200,align:"center" },
	            {label: '公司代码', name: 'bukrs', index:"bukrs", width: 200,align:"center" },
				{label: '公司简称', name: 'bukrsShortName',index:"bukrs_Short_Name", width: 200,align:"center" },
				{label: '维护人', name: 'editor',index:"editor", width: 100,align:"center" },
				{label:'维护时间',name:'editDate',index:"edit_date",align:"center"},
				{label:'ID',width:75,name:'id',index:"id",align:"center",hidden:true}//隐藏此列
	        ],
			viewrecords: true,
	        rowNum: 15,
	        rownumWidth: 25, 
	    });
	  
	
	  //------删除操作---------
	  $("#deleteOperation").click(function(){
		  var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
		  if (gr != null) {
			  layer.confirm('确定删除选中的记录?', {icon: 3, title:'提示'}, function(index){
				  //do something
				  var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
				  $.ajax({
					  url:baseUrl + "config/sapPlant/del",
					  type:"post",
					  data:{"id":grData.id},
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
		  else layer.msg("请选择要删除的记录",{time:1000});
	  });
	  //------新增操作---------
	  $("#newOperation").click(function(){
			js.layer.open({
				type: 2,
				title: "新增工厂",
				area: ["70%", "50%"],
				content: baseUrl + "wms/config/wms_sap_plant_new.html",
				closeBtn:1,
				end:function(){
					$("#searchForm").submit();
				},
				btn: ["确定","取消"],
				yes:function(index,layero){
					//提交表单
					var win = layero.find('iframe')[0].contentWindow;
					win.submit();
					layer.close(index);
				},
				no:function(index,layero){
					layer.close(index);
				}
		});
	  })
	  //-------编辑操作--------
	  $("#editOperation").click(function(){
		     var rowData =  getSelRow("#dataGrid");
			 if(rowData !== null){
				 var options = {
							type: 2,
							title: "编辑工厂",
							area: ["70%", "50%"],
							content: baseUrl + "wms/config/wms_sap_plant_edit.html",
							closeBtn:1,
							btn: ["确定","取消"],
							yes:function(index,layero){
								var win = layero.find('iframe')[0].contentWindow;
								win.vm.submit();//提交更新
								$("#searchForm").submit();//更新数据
								layer.close(index);
							},
							no:function(index,layero){
								layer.close(index);
							},
							success:function(layero,index){
								var win = layero.find('iframe')[0].contentWindow;
								win.vm.init(rowData.id);
							}
					};
					js.layer.open(options);
			 }else{
				 layer.msg("请选择要编辑的行",{time:1000});
			 }
	  })
	  //-------上传操作--------
	  $("#importOperation").click(function(){
		  var options = {
					type: 2,
					title: "导入工厂",
					area: ["80%", "80%"],
					content: baseUrl + "wms/config/wms_sap_plant_upload.html",
					closeBtn:1,
					btn: ["取消"],
					no:function(index,layero){
						layer.close(index);
					},
					end:function(){
						$("#searchForm").submit();
					}
			};
			js.layer.open(options);
	  })
	
});

function getSelRow(gridSelector){
	var gr = $(gridSelector).jqGrid('getGridParam', 'selrow');//获取选中的行号
	return gr != null?$(gridSelector).jqGrid("getRowData",gr):null;
}

var exportHref = $("#exportOperation").attr("href");
function appendExportUrl(){
	var werksName = $("#werksName").val();
	$("#exportOperation").attr("href",exportHref+"?werksName="+werksName);
}