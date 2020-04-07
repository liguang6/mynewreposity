
var smallUtil=new smallTools();
var array = new Array("WAREHOUSECODE","FACTORYCODE","STORAGEAREASEARCH","SEARCHSEQUENCETYPE",
	"DESCS","STATUS","CREATOR","CREATEDATE","EDITOR","EDITDATE");
var languageObj = smallUtil.loadlanguageByPKeyAndLKey(array,"M");

$(function () {
    $("#dataGrid").dataGrid({
    	searchForm: $("#searchForm"),
        columnModel: [
			{ label: languageObj.WAREHOUSECODE, name: 'warehouseCode', index: 'warehouseCode', width: 80,align:'center'  },
			// { label: languageObj.FACTORYCODE, name: 'factoryCode', index: 'factoryCode', width: 80,align:'center'  },
			{ label: languageObj.STORAGEAREASEARCH, name: 'storageAreaSearch', index: 'storageAreaSearch', width: 90,align:'center'  },
			{ label: languageObj.SEARCHSEQUENCETYPE, name: '', index: '', width: 80,align:'center' ,formatter:function(val, obj, row, act){
				return row.searchSequenceType=='00' ? '入库' : '出库';
			}}, 			
			{ label: languageObj.DESCS, name: 'descs', index: 'descs', width: 80,align:'center'  },
			// { label: languageObj.STATUS, name: 'status', index: 'status', width: 80,align:'center' ,formatter:function(val, obj, row, act){
			// 		return row.status=='0' ? '启用' : '禁用';
			// 	} },
			// { label:languageObj.CREATOR, name: 'creator', index: 'creator', width: 80,align:'center'  },
			// { label: languageObj.CREATEDATE, name: 'createDate', index: 'createDate', width: 80,align:'center'  },
			{ label: languageObj.EDITOR, name: 'editor', index: 'editor', width: 80,align:'center'  },
			{ label: languageObj.EDITDATE, name: 'editDate', index: 'editDate', width: 80,align:'center'  },
			{ label: 'ID', name: 'id', index: 'id', hidden:true },

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
				    url: baseURL + "config/wmsCoreSearchSequence/delById?id="+grData.id,
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
	    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>新增存储类型搜索顺序配置',
	    		area: ["500px", "300px"],
	    		content: baseURL + "wms/config/wms_core_search_sequence_edit.html",
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
				title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>修改存储类型搜索顺序配置',
				area: ["500px", "300px"],
				content: baseURL + 'wms/config/wms_core_search_sequence_edit.html',
				btn: ['<i class="fa fa-check"></i> 确定'],
				success:function(layero, index){
					var win = layero.find('iframe')[0].contentWindow;
					win.vm.saveFlag = false;
					win.vm.getInfo(grData.id);
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
		},getWhNoFuzzy:function(){
			getWhNoSelect("#warehouseCode",null,null);
//			getWhNoSelectForRedis("#warehouseCode",null,null);
		},/*getFactoryNoFuzzy:function(){
			getFactorySelectForRedis("#factoryCode",null,null);
		},*/
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