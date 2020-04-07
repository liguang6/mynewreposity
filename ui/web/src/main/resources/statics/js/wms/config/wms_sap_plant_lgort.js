
function getSelRow(gridSelector){
	var gr = $(gridSelector).jqGrid('getGridParam', 'selrow');//获取选中的行号
	return gr != null?$(gridSelector).jqGrid("getRowData",gr):null;
}

function getPlantNoFuzzy(){
	getPlantNoSelect("#werks",null,null);
}

$(function(){
	
	
	 $("#dataGrid").dataGrid({
	    	searchForm:$("#searchForm"),
	        datatype: "json",
	        colModel: [			
				{label: '工厂代码', name: 'werks',index:"werks", width: 200,align:"center"},
				{label:"库存地点代码",name: 'lgort',index:"lgort",width: 200,align:"center"},
	            {label: '库存地点名称', name: 'lgortName', index:"lgort_name", width: 200,align:"center" },
				{label: '库存类型', name: 'sobkz',index:"sobkz", width: 200,align:"center"},
				{label: '不良库', name: 'badFlag',index:"bad_flag", width: 100,align:"center",formatter: function(value, options, row){
					return value === '0' ?
							'<span class="label label-danger">否</span>':
							'<span class="label label-success">是</span>';
				}},
				{label:'维护人',name:'editor',index:"editor",align:"center"},
				{label:'维护时间',name:'editDate',index:"edit_date",align:"center"},
				{label:'ID',width:75,name:'id',index:"id",align:"center",hidden:true}//隐藏此列
	        ],
			viewrecords: true,
	        rowNum: 15,
	        rownumWidth: 25, 
	    });
	 
	 
	 
	 $("#newOperation").click(function(){
		 js.layer.open({
				type: 2,
				title: "新增工厂库存地点",
				area: ["780px", "300px"],
				content: baseUrl + "wms/config/wms_sap_plant_lgort_edit.html",
				closeBtn:1,
				end:function(){
					$("#searchForm").submit();
				},
				btn: ["确定","取消"],
				yes:function(index,layero){
					//提交表单
					var win = layero.find('iframe')[0].contentWindow;
					win.vm.save();
					layer.close(index);
				},
				no:function(index,layero){
					layer.close(index);
				}
		}); 
	 });
	 
	 $("#deleteOperation").click(function(){
		 if(getSelRow("#dataGrid") !== null){
			 //del
			 layer.confirm('确定删除选中的记录?', {icon: 3, title:'提示'}, function(index){
				  //do something
				  var grData = getSelRow("#dataGrid");
				  $.ajax({
					  url:baseUrl + "config/sapPlantLgort/del",
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
		 }else{
			 layer.msg("请选择要删除的记录");
		 }
	 });
	 
	 $("#editOperation").click(function(){
		 
		 if(getSelRow("#dataGrid") !== null){
			 
			 
			 js.layer.open({
					type: 2,
					title: "编辑工厂库存地点",
					area: ["780px", "300px"],
					content: baseUrl + "wms/config/wms_sap_plant_lgort_edit.html",
					closeBtn:1,
					end:function(){
						$("#searchForm").submit();
					},
					btn: ["确定","取消"],
					yes:function(index,layero){
						//提交表单
						var win = layero.find('iframe')[0].contentWindow;
						win.vm.save();
						layer.close(index);
					},
					no:function(index,layero){
						layer.close(index);
					},
					success:function(layero,index){
						var row =  getSelRow("#dataGrid");
						var win = layero.find('iframe')[0].contentWindow;
						win.vm.init(row.id);
					}
			});
			 
		 }else{
			 layer.msg("请选择要编辑的行",{time:1000});
		 }
		 
		 
	 });
	 
	 $("#importOperation").click(function(){
		 var options = {
					type: 2,
					title: "导入仓库库存地点",
					area: ["780px", "560px"],
					content: baseUrl + "wms/config/wms_sap_plant_lgort_upload.html",
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
	 });
});