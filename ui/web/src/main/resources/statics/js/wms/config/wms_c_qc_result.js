/**
 * 
 */
$(function(){
	$("#dataGrid").dataGrid({
        url: baseUrl + 'config/wmscqcresult/list',
        datatype: "json",
        searchForm:$("#searchForm"),
        colModel: [			
       			{ label: 'ID', name: 'ID', index: 'ID', width: 50, key: true,hidden:true },
       			{ label: '工厂代码', name: 'WERKS', index: 'WERKS', width: 80,align:"center" }, 			
       			{ label: '质检结果类型', name: 'QC_RESULT_NAME', index: 'QC_RESULT_NAME',align:"center", width: 80}, 			
       			{ label: '质检状态', name: 'QC_STATUS_DESC', index: 'QC_STATUS_DESC',align:"center", width: 80}, 			
       			{ label: '可进仓标识', name: 'WH_FLAG_DESC', index: 'WH_FLAG_DESC',align:"center", width: 80}, 			
       			{ label: '可退货标识', name: 'RETURN_FLAG_DESC', index: 'RETURN_FLAG_DESC',align:"center", width: 80 }, 			
       			{ label: '评审标识', name: 'REVIEW_FLAG_DESC', index: 'REVIEW_FLAG_DESC',align:"center", width: 80}, 			
       			{ label: '良品进仓标识', name: 'GOOD_FLAG_DESC', index: 'GOOD_FLAG_DESC',align:"center", width: 80}, 			
       			{ label: '创建人', name: 'CREATOR', index: 'CREATOR',align:"center", width: 80 }, 			
       			{ label: '创建时间', name: 'CREATE_DATE', index: 'CREATE_DATE',align:"center", width: 80 }, 			
       			{ label: '编辑人', name: 'EDITOR', index: 'EDITOR',align:"center", width: 80 }, 			
       			{ label: '编辑时间', name: 'EDIT_DATE', index: 'EDIT_DATE',align:"center", width: 80 },
       			{ label: '良品进仓标识代码', name: 'GOOD_FLAG', index: 'GOOD_FLAG',hidden:true,defaultValue:''},
       			{ label: '质检状态代码', name: 'QC_STATUS', index: 'QC_STATUS',hidden:true,defaultValue:'' },
       			{ label: '可进仓标识代码', name: 'WH_FLAG', index: 'WH_FLAG',hidden:true,defaultValue:'' },
       			{ label: '可退货标识代码', name: 'RETURN_FLAG', index: 'RETURN_FLAG',hidden:true,defaultValue:'' },
       			{ label: '评审标识代码', name: 'REVIEW_FLAG', index: 'REVIEW_FLAG',hidden:true,defaultValue:'' },
       			{ label: '质检结果类型', name: 'QC_RESULT_CODE', index: 'QC_RESULT_CODE',hidden:true,defaultValue:''}
        ],
        viewrecords: true,
        rowNum: 15,
        rownumWidth: 25, 
    });
	
	$("#newOperation").click(function(){
//		newop("新增质检结果配置","wms/config/wms_c_qc_result_edit.html");
		js.layer.open({
			type: 2,
			title: "新增质检结果配置",
			area: ["80%", "70%"],
			content: baseUrl + "wms/config/wms_c_qc_result_edit.html",
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
		var row = getSelRow("#dataGrid");
		if(row!==null){
			 js.layer.open({
					type: 2,
					title: "编辑质检结果配置",
					area: ["80%", "70%"],
					content: baseUrl + "wms/config/wms_c_qc_result_edit.html",
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
						win.vm.init(row.ID);
					}
			});
		 }else{
			layer.msg("请选择要编辑的记录",{time:1000});
		}
	});
	$("#deleteOperation").click(function(){
		//deleteop("config/wmscqcresult/delete");
		var row = getSelRow("#dataGrid");
		if(row != null){
			 layer.confirm('确定删除选中的记录?', {icon: 3, title:'提示'}, function(index){
				  $.ajax({
					  url:baseUrl + "config/wmscqcresult/delById?id="+row.ID,
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
	})
	$("#importOperation").click(function(){
		importop("导入质检结果配置",baseURL + "wms/config/wms_c_qc_result_upload.html")
	});
	$("#copyOperation").click(function(){
		var options = {
	    		type: 2,
	    		maxmin: true,
	    		shadeClose: true,
	    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>复制质检结果',
	    		area: ["860px", "540px"],
	    		content: baseURL + "wms/config/wms_c_qc_result_copy.html",  
	    		btn: ['<i class="fa fa-check"></i> 确定'],
	    		success:function(layero, index){
	    			var win = layero.find('iframe')[0].contentWindow;
	    			win.vm.saveFlag = false;
	    		},
	    		btn1:function(index,layero){
	    			var win = layero.find('iframe')[0].contentWindow;
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
	})
})
function listselectCallback(index,rtn){
	rtn = rtn ||false;
	if(rtn){
		try {
			parent.layer.close(index);
		} catch(e){
			layer.close(index);
		}
		//操作成功，刷新表格
		$("#searchForm").submit();
		
	}
}
function export2Excel(){
	var column=[	
		{ "title": '工厂代码', "data": 'WERKS', index: 'WERKS',"class":"center" }, 			
		{ "title": '质检结果类型', "data": 'QC_RESULT_CODE', index: 'QC_RESULT_CODE',"class":"center"}, 			
		{ "title": '质检状态', "data": 'QC_STATUS_DESC', index: 'QC_STATUS_DESC',"class":"center"}, 			
		{ "title": '可进仓标识', "data": 'WH_FLAG_DESC', index: 'WH_FLAG_DESC',"class":"center"}, 			
		{ "title": '可退货标识', "data": 'RETURN_FLAG_DESC', index: 'RETURN_FLAG_DESC',"class":"center"}, 			
		{ "title": '评审标识', "data": 'REVIEW_FLAG_DESC', index: 'REVIEW_FLAG_DESC',"class":"center"}, 			
		{ "title": '良品进仓标识', "data": 'GOOD_FLAG_DESC', index: 'GOOD_FLAG_DESC',"class":"center"}, 			
  ]	;
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"config/wmscqcresult/list",
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) { 
		    results=response.page.list;
		}
	})
	var rowGroups=[];
	getMergeTable(results,column,rowGroups,"物料质检结果配置");	
}