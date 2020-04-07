$(function () {
    $("#dataGrid").dataGrid({
    	searchForm: $("#searchForm"),
        columnModel: [			
			{ label: '工厂代码', name: 'WERKS', index: 'WERKS', width: 80,align:'center' },
			{ label: '仓库代码', name: 'WHNUMBER', index: 'WHNUMBER', width: 80,align:'center'  }, 			
			{ label: '仓库名称', name: 'WHNAME', index: 'WHNAME', width: 150,align:'center'  }, 
			{ label: '库位', name: 'LGORT_NO', index: 'LGORT_NO', width: 65,align:'center'  },
			{ label: '语言', name: 'LANGUAGENAME', index: 'LANGUAGENAME', width: 65,align:'center'  },
			{ label: '国家', name: 'COUNTRY', index: 'COUNTRY', width: 80,align:'center'  },
			{ label: '省份', name: 'PROVINCE', index: 'PROVINCE', width: 80,align:'center'  },
			{ label: '城市', name: 'CITY', index: 'CITY', width: 80,align:'center'  },
			{ label: '区/县', name: 'REGION', index: 'REGION', width: 80,align:'center'  }, 
			{ label: '街道', name: 'STREET', index: 'STREET', width: 80,align:'center'  },
			{ label: '联系人', name: 'CONTACTS', index: 'CONTACTS', width: 80,align:'center'  },
			{ label: '联系电话', name: 'TEL', index: 'TEL', width: 110,align:'center'  },
			{ label: '备注', name: 'MEMO', index: 'MEMO', width: 80,align:'center'  },
			{ label: '录入人员', name: 'EDITOR', index: 'EDITOR', width: 80,align:'center'  }, 			
			{ label: '录入时间', name: 'EDITDATE', index: 'EDITDATE', width: 80,align:'center'  },
			{ label: 'ID', name: 'ID', index: 'ID', hidden:true },
			{ label: '语言', name: 'LANGUAGE', index: 'LANGUAGE', hidden:true },
			{ label: 'ADDRESSID', name: 'ADDRESSID', index: 'ADDRESSID', hidden:true }
		],
		viewrecords: true,
		rowNum: 15
    });
  //------删除操作---------
	  $("#deleteOperation").click(function(){
		  var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
		  if (gr != null  && gr!==undefined) {
			  layer.confirm('确定删除选中的记录?', {icon: 3, title:'提示'}, function(index){
				  //do something
				  var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
				  $.ajax({
					type: "GET",
				    url: baseURL + "config/wh/delById",
				    data:{
		            	id:grData.ID,
		            	addrID:grData.ADDRESSID
		            },
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
	    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>新增仓库信息',
	    		area: ["560px", "450px"],
	    		content: baseURL + "wms/config/wms_core_wh_edit.html",  
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
				title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>修改仓库信息',
				area: ["560px", "450px"],
				content: baseURL + 'wms/config/wms_core_wh_edit.html',
				btn: ['<i class="fa fa-check"></i> 确定'],
				success:function(layero, index){
					var win = layero.find('iframe')[0].contentWindow;
					win.vm.saveFlag = false;
					win.vm.getInfo(grData.ID,grData.ADDRESSID);
					$(win.document.body).find("#werks").attr("readonly",true);
					//$(win.document.body).find("#whNumber").attr("readonly",true);
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
		getWhNoFuzzy:function(){
			getWhNoSelect("#whNumber",null,null);
		},
	}
});
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