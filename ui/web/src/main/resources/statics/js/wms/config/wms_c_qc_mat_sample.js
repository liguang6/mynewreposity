/**
 * 
 */
$(function(){
	$("#dataGrid").dataGrid({
        url: baseUrl + 'config/wmscqcmatsample/list',
        datatype: "json",
        searchForm:$("#searchForm"),
        colModel: [			
       			{ label: 'id', name: 'id', index: 'ID', key: true,hidden:true },
       			{ label: '工厂代码', name: 'werks', index: 'WERKS', width: 60 ,align:"center"}, 			
       			{ label: '物料号', name: 'matnr', index: 'MATNR', width: 110 ,align:"center"}, 			
       			{ label: '物料描述', name: 'maktx', index: 'MAKTX', width: 150 ,align:"center"}, 			
       			{ label: '供应商代码', name: 'lifnr', index: 'LIFNR', width: 80,align:"center" }, 			
       			{ label: '供应商名称', name: 'liktx', index: 'LIKTX', width: 80 ,align:"center"}, 			
       			{ label: '抽样率(%)', name: 'sampling', index: 'SAMPLING', width: 70,align:"center" }, 			
       			{ label: '最小抽样数量', name: 'minSample', index: 'MIN_SAMPLE', width: 80,align:"center" }, 			
       			{ label: '最大抽样数量', name: 'maxSample', index: 'MAX_SAMPLE', width: 80,align:"center" }, 			
       			{ label: '开箱率(%)', name: 'unpacking', index: 'UNPACKING', width: 70 ,align:"center"}, 			
       			{ label: '最小开箱数', name: 'minUnpacking', index: 'MIN_UNPACKING', width: 80,align:"center" }, 			
       			{ label: '最大开箱数', name: 'maxUnpacking', index: 'MAX_UNPACKING', width: 80 ,align:"center"}, 			
       			{ label: '创建人', name: 'creator', index: 'CREATOR', width: 60,align:"center" }, 			
       			{ label: '创建时间', name: 'createDate', index: 'CREATE_DATE', width: 80,align:"center" }, 			
       			{ label: '编辑人', name: 'editor', index: 'EDITOR', width: 60 ,align:"center"}, 			
       			{ label: '编辑时间', name: 'editDate', index: 'EDIT_DATE', width: 80,align:"center" }, 			
       			{ label: '备注', name: 'memo', index: 'MEMO', width: 80,align:"center" }			
        ],
        viewrecords: true,
        rowNum: 15,
        rownumWidth: 25, 
    });
	

	$("#newOperation").click(function(){
//		newop("新增物料质检抽样配置","wms/config/wms_c_qc_mat_sample_edit.html");
		js.layer.open({
			type: 2,
			title: "新增物料质检抽样配置",
			area: ["80%", "70%"],
			content: baseUrl + "wms/config/wms_c_qc_mat_sample_edit.html",
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
			}
		});
	});
	$("#editOperation").click(function(){
//		editop("编辑物料质检抽样配置","wms/config/wms_c_qc_mat_sample_edit.html");
		var row = getSelRow("#dataGrid");
		if(row!==null){
			 js.layer.open({
					type: 2,
					title: "编辑物料质检抽样配置",
					area: ["80%", "70%"],
					content: baseUrl + "wms/config/wms_c_qc_mat_sample_edit.html",
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
	$("#deleteOperation").click(function(){
//		deleteop("config/wmscqcmatsample/delete");
		var row = getSelRow("#dataGrid");
		if(row != null){
			 layer.confirm('确定删除选中的记录?', {icon: 3, title:'提示'}, function(index){
				  $.ajax({
					  url:baseUrl + "config/wmscqcmatsample/delById?id="+row.id,
					  type:"post",
					  contentType:"application/json",
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
		 }else{
			layer.msg("请选择要删除的记录",{time:1000});
		}
	});
	$("#importOperation").click(function(){
		importop("导入物料质检抽样配置",baseURL + "wms/config/wms_c_qc_mat_sample_upload.html")
	});
});
function export2Excel(){
	var column=[
		{ "title": '工厂代码', "data": 'werks', align:"center"}, 			
		{ "title": '物料号', "data": 'matnr', align:"center"}, 			
		{ "title": '物料描述', "data": 'maktx', align:"center"}, 			
		{ "title": '供应商代码', "data": 'lifnr', align:"center" }, 			
		{ "title": '供应商名称', "data": 'liktx', align:"center"}, 			
		{ "title": '抽样率(%)', "data": 'sampling', align:"center" }, 			
		{ "title": '最小抽样数量', "data": 'minSample',align:"center" }, 			
		{ "title": '最大抽样数量', "data": 'maxSample', align:"center" }, 			
		{ "title": '开箱率(%)', "data": 'unpacking',align:"center"}, 			
		{ "title": '最小开箱数', "data": 'minUnpacking',align:"center" }, 			
		{ "title": '最大开箱数', "data": 'maxUnpacking',align:"center"}, 			
		{ "title": '备注', "data": 'memo',align:"center" }			
      ]	;
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"config/wmscqcmatsample/list",
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) { 
		    results=response.page.list;
		}
	})
	var rowGroups=[];
	getMergeTable(results,column,rowGroups,"物料质检抽样配置表");	
}