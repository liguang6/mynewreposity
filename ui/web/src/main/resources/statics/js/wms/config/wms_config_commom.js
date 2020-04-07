/**
 * 封装基础数据模块的通用方法
 *
 * 新增
 * 编辑
 * 删除
 * 导入
 * 导出
 */


function editop(title,htmlUrl){
	var row = getSelRow("#dataGrid");
	if(row!==null){
		 js.layer.open({
				type: 2,
				title: title,
				area: ["80%", "70%"],
				content: baseUrl + htmlUrl,
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
}

function newop(title,htmlUrl){
	
	js.layer.open({
		type: 2,
		title: title,
		area: ["80%", "70%"],
		content: baseUrl + htmlUrl,
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
	
}

//删除操作
function deleteop(url){
	var row = getSelRow("#dataGrid");
	if(row != null){
		 //del
		 layer.confirm('确定删除选中的记录?', {icon: 3, title:'提示'}, function(index){
			  $.ajax({
				  url:baseUrl + url,
				  type:"post",
				  contentType:"application/json",
				  data:JSON.stringify([row.id]),
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
		layer.msg("请选择要删除的记录",{time:1000});
	}
}

function importop(title,url){
	js.layer.open({
		type: 2,
		title: title,
		area: ["80%", "80%"],
		content: baseUrl + url,
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
}