
var smallUtil=new smallTools();
var array = new Array("WAREHOUSECODE","FACTORYCODE","STORAGEAREASEARCH","STORAGEAREACODE",
	"PRIORITY","STATUS","CREATOR","CREATEDATE","EDITOR","EDITDATE","AREANAME","CONTROL_FLAG","BUSINESS_NAME","SOBKZ","LGORT");
var languageObj = smallUtil.loadlanguageByPKeyAndLKey(array,"M");

$(function () {
    $("#dataGrid").dataGrid({
    	searchForm: $("#searchForm"),
        columnModel: [
			{ label: languageObj.WAREHOUSECODE, name: 'WAREHOUSECODE', index: 'WAREHOUSECODE', width: 80,align:'center'  },
			// { label: languageObj.FACTORYCODE, name: 'FACTORYCODE', index: 'FACTORYCODE', width: 80,align:'center'  },
			// { label: languageObj.STATUS, name: 'STATUS', index: 'STATUS', width: 80,align:'center' ,formatter:function(val, obj, row, act){
			// 		return row.STATUS=='0' ? '启用' : '禁用';
			// 	} },storageAreaSearch STORAGE_AREA_CODE
			{ label: languageObj.STORAGEAREASEARCH, name: 'STORAGEAREASEARCH', index: 'STORAGEAREASEARCH', width: 80,align:'center'},
			{ label: languageObj.STORAGEAREACODE, name: 'STORAGEAREACODE', index: 'STORAGEAREACODE', width: 80,align:'center'},
				// formatter:function(val, obj, row, act){
				// 	if(!/^[0-9]*$/.test(val) ){
				// 		return languageObj.NUMBERREGULAR;
				// 	}
				// } },


				{ label: languageObj.AREANAME, name: 'AREANAME', index: 'AREANAME', width: 80,align:'center'  },
			{ label: languageObj.PRIORITY, name: 'PRIORITY', index: 'PRIORITY', width: 80,align:'center'  },

			// { label:languageObj.CREATOR, name: 'CREATOR', index: 'CREATOR', width: 80,align:'center'  },
			// { label: languageObj.CREATEDATE, name: 'CREATEDATE', index: 'CREATEDATE', width: 80,align:'center'  },
			{ label: languageObj.EDITOR, name: 'EDITOR', index: 'EDITOR', width: 80,align:'center'  },
			{ label: languageObj.EDITDATE, name: 'EDITDATE', index: 'EDITDATE', width: 80,align:'center'  },
			{ label: 'ID', name: 'ID', index: 'ID',hidden:true},

		],
		viewrecords: true,
		rowNum: 15
    });
  //------删除操作---------
	  $("#deleteOperation").click(function(){
		  var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
		  if (gr != null  && gr!==undefined) {
			  layer.confirm('确定删除选中的记录?', {icon: 3, title:'提示'}, function(index){
				  var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
				  $.ajax({
					type: "POST",
				    url: baseURL + "config/wmsCoreStorageSearch/delById?id="+grData.ID,
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
	    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>新增分配存储类型至搜索顺序配置',
	    		area: ["500px", "330px"],
	    		content: baseURL + "wms/config/wms_core_storage_search_edit.html",
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
				title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>修改分配存储类型至搜索顺序配置',
				area: ["500px", "330px"],
				content: baseURL + 'wms/config/wms_core_storage_search_edit.html',
				btn: ['<i class="fa fa-check"></i> 确定'],
				success:function(layero, index){
					var win = layero.find('iframe')[0].contentWindow;
					win.vm.saveFlag = false;
					win.vm.getInfo(grData.ID);
					win.vm.getInfoName(grData.WAREHOUSECODE,grData.STORAGEAREACODE);
					//$(win.document.body).find("#storageTypeCode").attr("readonly",true);
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
		getWhNoFuzzy:function(){
			getWhNoSelect("#warehouseCode",null,null);
//			getWhNoSelectForRedis("#warehouseCode",null,null);
		},getFactoryNoFuzzy:function(){
			getFactorySelectForRedis("#factoryCode",null,null);
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