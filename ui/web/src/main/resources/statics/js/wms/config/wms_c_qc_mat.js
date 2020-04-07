$(function(){
	$("#dataGrid").dataGrid({
        url: baseUrl + 'config/wmscqcmat/list',
        datatype: "json",
        searchForm:$("#searchForm"),
        colModel: [			
            { label: 'ID', name: 'id', index: 'id', width: 80,hidden:true },//不显示 	
			{ label: '工厂代码', name: 'werks', index: 'WERKS', width: 80,align:"center" }, 			
			{ label: '物料号', name: 'matnr', index: 'MATNR', width: 80 ,align:"center"}, 			
			{ label: '物料描述', name: 'maktx', index: 'MAKTX', width: 80 ,align:"center"}, 			
			{ label: '供应商代码', name: 'lifnr', index: 'LIFNR', width: 80 ,align:"center"}, 			
			{ label: '供应商名称', name: 'liktx', index: 'LIKTX', width: 80 ,align:"center"}, 			
			{ label: '质检标识 ', name: 'testFlag', index: 'TEST_FLAG', width: 80 ,align:"center",formatter:function(val){
				return val === '00'?'质检':'免检';
			}}, 						
			{ label: '有效开始日期', name: 'startDate', index: 'START_DATE', width: 80,align:"center" }, 			
			{ label: '有效截止日期', name: 'endDate', index: 'END_DATE', width: 80 ,align:"center"}, 			
			{ label: '创建人', name: 'creator', index: 'CREATOR', width: 80,align:"center" }, 			
			{ label: '创建时间', name: 'createDate', index: 'CREATE_DATE', width: 80,align:"center" }, 			
			{ label: '编辑人', name: 'editor', index: 'EDITOR', width: 80,align:"center" }, 			
			{ label: '编辑时间', name: 'editDate', index: 'EDIT_DATE', width: 80,align:"center" }, 			
			{ label: '备注', name: 'memo', index: 'MEMO', width: 80 ,align:"center"}			
        ],
        viewrecords: true,
        rowNum: 15,
        rownumWidth: 25, 
    });
	
	$("#newOperation").click(function(){
		 js.layer.open({
				type: 2,
				title: "新增物料质检配置",
				area: ["80%", "70%"],
				content: baseUrl + "wms/config/wms_c_qc_mat_edit.html",
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
	
	$("#editOperation").click(function(){
		var row = getSelRow("#dataGrid");
		if(row!==null){
			 
			 js.layer.open({
					type: 2,
					title: "编辑物料质检配置",
					area: ["80%", "70%"],
					content: baseUrl + "wms/config/wms_c_qc_mat_edit.html",
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
						var win = layero.find('iframe')[0].contentWindow;
						win.vm.init(row.id);
					}
			});
			 
		 }else{
			layer.msg("请选择要编辑的记录",{time:1000});
		}
	});
	
	/**
	 * 删除操作
	 */
	$("#deleteOperation").click(function(){
		var row = getSelRow("#dataGrid");
		if(row != null){
			 //del
			 layer.confirm('确定删除选中的记录?', {icon: 3, title:'提示'}, function(index){
				  $.ajax({
					  url:baseUrl + "config/wmscqcmat/delete",
					  type:"post",
					  data:{"id":row.id},
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
			layer.msg("请选择要编辑的记录",{time:1000});
		}
	});
	
	$("#importOperation").click(function(){

		js.layer.open({
			type: 2,
			title: "导入物料质检配置",
			area: ["80%", "80%"],
			content: baseUrl + "wms/config/wms_c_qc_mat_upload.html",
			closeBtn:1,
			end:function(){
				$("#searchForm").submit();
			},
			btn: ["保存","取消"],
			yes:function(index,layero){
				var win = layero.find('iframe')[0].contentWindow;
				win.saveUpload();//保存导入数据
			},
			no:function(index,layero){
				layer.close(index);
			}
	    });
	
	});
});
function export2Excel(){
	var column=[	
		{ "title": '工厂代码', "data": 'werks', index: 'WERKS',"class":"center" }, 			
		{ "title": '物料号', "data": 'matnr', index: 'MATNR',"class":"center" }, 			
		{ "title": '物料描述', "data": 'maktx', index: 'MAKTX', "class":"center" }, 			
		{ "title": '供应商代码', "data": 'lifnr', index: 'LIFNR', "class":"center"}, 			
		{ "title": '供应商名称', "data": 'liktx', index: 'LIKTX', "class":"center" }, 			
		{ "title": '质检标识 ', "data": 'testFlag', index: 'TEST_FLAG', "class":"center"}, 						
		{ "title": '有效开始日期', "data": 'startDate', index: 'START_DATE',"class":"center" }, 			
		{ "title": '有效截止日期', "data": 'endDate', index: 'END_DATE',"class":"center" }, 			 			
		{ "title": '备注', "data": 'memo', index: 'MEMO', "class":"center" }	
      ]	;
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"config/wmscqcmat/list",
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) { 
		    results=response.page.list;
		    for(var i=0;i<results.length;i++){
		    	var testFlag=results[i].testFlag;
		    	if(testFlag=='00') results[i].testFlag="质检";
		    	if(testFlag=='01') results[i].testFlag="免检";
		    }
		}
	})
	var rowGroups=[];
	getMergeTable(results,column,rowGroups,"物料质检配置");	
}