/**
 * 
 */
$(function(){
	$("#dataGrid").dataGrid({
        url: baseUrl + 'config/wmscqcplant/list',
        datatype: "json",
        searchForm:$("#searchForm"),
        colModel: [			
       			{ label: 'id', name: 'id', index: 'ID', width: 50, key: true ,hidden:true},
       			{ label: '工厂代码', name: 'werks', index: 'WERKS', width: 80,align:'center' }, 			
       			{ label: 'WMS业务类型代码', name: 'businessName', index: 'BUSINESS_NAME_DESC',align:'center', width: 100 }, 			
       			{ label: '质检标识 ', name: 'testFlag', index: 'TEST_FLAG_DESC', width: 80,align:'center'}, 			
       			{ label: '有效开始日期', name: 'startDate', index: 'START_DATE', width: 80,align:'center' }, 			
       			{ label: '有效截止日期', name: 'endDate', index: 'END_DATE', width: 80,align:'center' }, 			
       			{ label: '创建人', name: 'creator', index: 'CREATOR', width: 80,align:'center' }, 			
       			{ label: '创建时间', name: 'createDate', index: 'CREATE_DATE', width: 80,align:'center' }, 			
       			{ label: '编辑人', name: 'editor', index: 'EDITOR', width: 80,align:'center' }, 			
       			{ label: '编辑时间', name: 'editDate', index: 'EDIT_DATE', width: 80,align:'center' }, 			
       			{ label: '备注', name: 'memo', index: 'MEMO', width: 80,align:'center' }			
        ],
//        showCheckbox:true,
		viewrecords: true,	
        rowNum: 15,
        rownumWidth: 25, 
    });
	
	
	
	$("#newOperation").click(function(){
//		newop("新增工厂质检配置","wms/config/wms_c_qc_plant_edit.html");
		js.layer.open({
			type: 2,
			title: "新增工厂质检配置",
			area: ["80%", "70%"],
			content: baseUrl + "wms/config/wms_c_qc_plant_edit.html",
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
//		editop("编辑工厂质检配置","wms/config/wms_c_qc_plant_edit.html");
		var row = getSelRow("#dataGrid");
		if(row!==null){
			 js.layer.open({
					type: 2,
					title: "编辑工厂质检配置",
					area: ["80%", "70%"],
					content: baseUrl + "wms/config/wms_c_qc_plant_edit.html",
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
	})
	$("#deleteOperation").click(function(){
//		deleteop("config/wmscqcplant/delete");
		var row = getSelRow("#dataGrid");
		if(row != null){
			 layer.confirm('确定删除选中的记录?', {icon: 3, title:'提示'}, function(index){
				  $.ajax({
					  url:baseUrl + "config/wmscqcplant/delById?id="+row.id,
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
		importop("导入工厂质检配置",baseURL + "wms/config/wms_c_qc_plant_upload.html")
	});
});