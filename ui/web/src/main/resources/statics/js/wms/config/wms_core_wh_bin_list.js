$(function () {
    $("#dataGrid").dataGrid({
    	searchForm: $("#searchForm"),
        columnModel: [			
			{"label": '仓库代码', "name": 'whNumber', index: 'whNumber', width: 80,align:'center' },
			{"label": '存储区代码', "name": 'storageAreaCode', index: 'storageAreaCode', width: 100,align:'center'  }, 			
			{"label": '储位代码', "name": 'binCode', index: 'binCode', width: 110,align:'center'  },
		    {"label": '储位名称', "name": 'binName', index: 'binName', width: 110,align:'center'  },
		    {"label": '排', "name": 'binRow', index: 'binRow', width: 40,align:'center'  },
		    {"label": '列', "name": 'binColumn', index: 'binColumn', width: 40,align:'center'  },
		    {"label": '层', "name": 'binFloor', index: 'binFloor', width: 40,align:'center'  },
		    {"label": '储位类型', "name": 'binType', index: 'binType', width: 80,align:'center'  ,formatter: function(item, index){
	 		       var result='';
	 		       if(item=='00'){result='虚拟储位';}
			       if(item=='01'){result='进仓位';}
			       if(item=='02'){result='存储位';}
			       if(item=='03'){result='拣配位';}
			       if(item=='04'){result='试装位';}
			       if(item=='05'){result='立库位';}
		    	   return result;
		    	}
		    }, 	
		    {"label": '储位状态', "name": 'binStatus', index: 'binStatus', width: 80,align:'center' ,formatter: function(item, index){
    		       var result='';
    		       if(item=='00'){result='未启用';}
    		       if(item=='01'){result='可用';}
    		       if(item=='02'){result='不可用';}
    		       if(item=='03'){result='锁定';}
		    	   return result;
		    	}
		    },	
		    {"label": '容积', "name": 'vl', index: 'vl', width: 50,align:'center'  },
		    {"label": '容积单位', "name": 'vlUnit', index: 'vlUnit', width: 50,align:'center'  },
		    {"label": '承重', "name": 'wt', index: 'wt', width: 50,align:'center'  },
		    {"label": '承重单位', "name": 'wtUnit', index: 'wtUnit', width: 50,align:'center'  },
		    {"label": '容积使用', "name": 'vlUse', index: 'vtUse', width: 50,align:'center'  },
		    {"label": '重量使用', "name": 'wtUse', index: 'wtUse', width: 50,align:'center'  },
		    {"label": '周转率', "name": 'turnoverRate', index: 'turnoverRate', width: 50,align:'center'  },
		    {"label": '可容存储单元', "name": 'aStorageUnit', index: 'aStorageUnit', width: 50,align:'center'  },
		    {"label": '占用存储单元', "name": 'uStorageUnit', index: 'uStorageUnit', width: 50,align:'center'  },
		    {"label": 'X轴', "name": 'x', index: 'x', width: 50,align:'center'  },
		    {"label": 'Y轴', "name": 'y', index: 'y', width: 50,align:'center'  },
		    {"label": 'Z轴', "name": 'z', index: 'z', width: 50,align:'center'  },
			{"label": '创建人', "name": 'editor', index: 'editor', width: 80,align:'center'  }, 			
			{"label": '创建时间', "name": 'editDate', index: 'editDate', width: 80,align:'center'  },
			{"label": 'ID', "name": 'id', index: 'id', hidden:true },
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
				    url: baseURL + "config/corewhbin/delById?id="+grData.id,
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
	    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>新增仓库储位',
	    		area: ["800px", "480px"],
	    		content: baseURL + "wms/config/wms_core_wh_bin_edit.html",  
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
			 var options = {
				type: 2,
				maxmin: true,
				shadeClose: true,
				title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>修改仓库储位',
				area: ["800px", "480px"],
				content: baseURL + 'wms/config/wms_core_wh_bin_edit.html',
				btn: ['<i class="fa fa-check"></i> 确定'],
				success:function(layero, index){
					var win = layero.find('iframe')[0].contentWindow;
					win.vm.saveFlag = false;
					win.vm.getInfo(grData.id);
					$(win.document.body).find("#whNumber").attr("readonly",true);
					$(win.document.body).find("#binCode").attr("readonly",true);
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
	//-------导入操作--------
	  $("#importOperation").click(function(){
		  var options = {
				type: 2,
				maxmin: true,
				shadeClose: true,
				title: "<i class='fa fa-upload' aria-hidden='true'>&nbsp;</i> 导入仓库储位",
				area: ["1020px", "560px"],
				content: baseUrl + "wms/config/wms_core_wh_bin_upload.html",
				btn: ['<i class="fa fa-check"></i> 保存'],
				
				btn1:function(index,layero){
					var win = layero.find('iframe')[0].contentWindow;
					var saveUpload= $("#saveUpload", layero.find("iframe")[0].contentWindow.document);
					$(saveUpload).click();
					// 保存成功 关闭窗口
					if(win.vm.saveFlag){
						if(typeof listselectCallback == 'function'){
							listselectCallback(index,true);
						}
					}
					vm.reload();
				}
			};
			options.btn.push('<i class="fa fa-close"></i> 关闭');
			js.layer.open(options);
	  });
	  
});

var vm = new Vue({
	el:'#rrapp',
	data:{
		whNumber: ''
	},
	created: function(){
    	// 加载存储类型代码select
		//getStorageTypeCodeSelect();
		this.whNumber=$("#whNumber").find("option").first().val();
	},
	watch: {
		whNumber: {
			handler:function(newVal,oldVal){
		    	// 加载存储类型代码select
				console.info('仓库号：'+newVal);
				getAreaCodeSelect();
			}
		}
	},
	methods: {
		refresh:function(){
			$("#searchForm").submit();
        },
		reload: function (event) {
			$("#searchForm").submit();
		}
	}
});
function export2Excel(){
	var column=[
            {"title": '仓库代码', "data": 'whNumber', "class":'center' },
			{"title": '存储类型代码', "data": 'storageAreaCode',"class":'center'  }, 			
			{"title": '储位代码', "data": 'binCode',"class":'center'  },
		    {"title": '储位名称', "data": 'binName', "class":'center'  },
		    {"title": '排', "data": 'binRow',"class":'center'  },
		    {"title": '列', "data": 'binColumn',"class":'center'  },
		    {"title": '层', "data": 'binFloor',"class":'center'  },
		    {"title": '储位类型', "data": 'binType', "class":'center' }, 	
		    {"title": '储位状态', "data": 'binStatus', "class":'center' },	
		    {"title": '容积', "data": 'vl', "class":'center'  },
		    {"title": '容积单位', "data": 'vlUnit',"class":'center'  },
		    {"title": '承重', "data": 'wt', "class":'center'  },
		    {"title": '承重单位', "data": 'wtUnit',"class":'center'  },
		    {"title": '容积使用', "data": 'vtUse', "class":'center'  },
		    {"title": '重量使用', "data": 'wtUse', "class":'center'  },
		    {"title": '周转率', "data": 'turnoverRate',"class":'center'  },
		    {"title": '可容存储单元', "data": 'aStorageUnit',"class": 'center'  },
		    {"title": '占用存储单元', "data": 'uStorageUnit',"class":'center'  },
		    {"title": 'X轴', "data": 'x', "class":'center'  },
		    {"title": 'Y轴', "data": 'y', "class":'center'  },
		    {"title": 'Z轴', "data": 'z',"class":'center'  },
			{"title": '创建人', "data": 'editor', "class":'center'  }, 			
			{"title": '创建时间', "data": 'editDate',"class":'center'  },
      ]	;
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"config/corewhbin/list",
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) { 
		    results=response.page.list;
		    for(var i=0;i<results.length;i++){
		    	var binType=results[i].binType;
		    	if(binType=='00'){results[i].binType='虚拟储位';}
			    if(binType=='01'){results[i].binType='出仓位';}
			    if(binType=='02'){results[i].binType='存储位';}
			    if(binType=='03'){results[i].binType='拣配位';}
			    if(binType=='03'){results[i].binType='试装位';}
			    if(binType=='05'){results[i].binType='立体库';}
			    var binStatus=results[i].binStatus;
			    if(binStatus=='00'){results[i].binStatus='未启用';}
			    if(binStatus=='01'){results[i].binStatus='可用';}
			    if(binStatus=='02'){results[i].binStatus='不可用';}
			    if(binStatus=='03'){results[i].binStatus='锁定';}
			    var storageModel=results[i].storageModel;
			    if(storageModel=='00'){results[i].storageModel='固定存储';}
			    if(storageModel=='01'){results[i].storageModel='随机存储';}
		    }
		    
		}
	})
	var rowGroups=[0,1];
	getMergeTable(results,column,rowGroups,"仓库储位");	
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
/*function getStorageTypeCodeSelect(){
	$.ajax({
        type: "POST",
        url: baseURL + "config/storagetype/queryAll",
        dataType : "json",
        data: {},
        success: function(resp){
        	var data=resp.list;
        	var element=$("#storageAreaCode");
        	$.each(data,function(index,val){
        		element.append("<option value='"+val.storageAreaCode+"'>"+val.storageTypeCode+"</option>");
        	});
        }
    });
}*/
function getAreaCodeSelect(){
	$("#storageAreaCode").html("");
	$.ajax({
        type: "POST",
        url: baseURL + "config/wharea/queryAll",
        dataType : "json",
        data: {
        	"whNumber":vm.whNumber.toUpperCase(),
        },
        async:false,
        success: function(resp){
        	var data=resp.list;
        	var element=$("#storageAreaCode");
        	element.append("<option value='' storagetypecode=''>全部</option>");
        	$.each(data,function(index,val){
        		element.append("<option value='"+val.storageAreaCode+"'" + ">"+val.storageAreaCode+"</option>");
        	});
        }
    });
}