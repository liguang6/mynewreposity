//国际化取值
var smallUtil=new smallTools();
var array = new Array("WH_NUMBER","PLANT","MATNR","MATNR_DESC","STORAGE_TYPE","BIN_CODE","MAX_QTY","QUANTITY",
		"MIN_QTY","LAST_EDIT_DATE","LAST_EDIT","ADD_FIXED_STORAGE","MODIFY_FIXED_STORAGE","DELETE_SUCCESS",
		"MAKE_SURE_DEL","SELECT_DELETE","PROMPT_MESSAGE","CONFIRM","CLOSE","SAVE","SELECT_EDIT","IMPORT","LGORT",
		"STOCK_TYPE","LIFNR","PRIORITY");
var languageObj = smallUtil.loadlanguageByPKeyAndLKey(array,"M");

$(function () {
    $("#dataGrid").dataGrid({
    	searchForm: $("#searchForm"),
        columnModel: [			
			{ label: languageObj.WH_NUMBER, name: 'whNumber', index: 'whNumber', width: 65,align:'center' },
			{ label: languageObj.PLANT, name: 'werks', index: 'werks', width: 55,align:'center'  }, 			
			{ label: languageObj.MATNR, name: 'matnr', index: 'matnr', width: 95,align:'center'  }, 
			{ label: languageObj.MATNR_DESC, name: 'maktx', index: 'maktx', width: 160,align:'center'  }, 
			{ label: languageObj.STORAGE_TYPE, name: 'storageAreaCode', index: 'storageAreaCode', width: 72,align:'center'  }, 
			{ label: languageObj.BIN_CODE, name: 'binCode', index: 'binCode', width: 85,align:'center'  }, 
			{ label: languageObj.PRIORITY, name: 'seqno', index: 'seqno', width: 55,align:'center'  }, 
			//{ label: languageObj.QUANTITY, name: 'qty', index: 'qty', width: 55,align:'center'  }, 
			{ label: languageObj.MIN_QTY, name: 'stockM', index: 'stockM', width: 70,align:'center'  }, 
			{ label: languageObj.MAX_QTY, name: 'stockL', index: 'stockL', width: 70,align:'center'  }, 
			{ label: languageObj.LGORT, name: 'lgort', index: 'lgort', width: 55,align:'center'  }, 
			{ label: languageObj.STOCK_TYPE, name: 'sobkz', index: 'sobkz', width: 70,align:'center'  }, 
			{ label: languageObj.LIFNR, name: 'lifnr', index: 'lifnr', width: 80,align:'center'  }, 
			{ label: languageObj.LAST_EDIT, name: 'editor', index: 'editor', width: 75,align:'center'  }, 
			{ label: languageObj.LAST_EDIT_DATE, name: 'editDate', index: 'editDate', width: 80,align:'center'  },
			{ label: 'ID', name: 'id', index: 'id', hidden:true },
		],
		rowNum : 15,
		showCheckbox:true,
    });
  //------删除操作---------
	  $("#deleteOperation").click(function(){
//		  var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
		  var gr = $("#dataGrid").jqGrid('getGridParam', 'selarrrow');//获取选中的行号
		  var ids="";
		  for(var i=0;i<gr.length;i++){
			  ids=ids+gr[i]+",";
		  }
		  var selectedRows=[];
		  $.each(gr,function(i,rowid){
			 var row = $("#dataGrid").jqGrid("getRowData",rowid);
			 selectedRows.push(row);
		  })
		  
//		  if (gr != null  && gr!==undefined) {
		  if (ids != "") {
			  layer.confirm(languageObj.MAKE_SURE_DEL, {icon: 3, title:languageObj.PROMPT_MESSAGE}, function(index){
				  var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
				  $.ajax({
					type: "POST",
				    url: baseURL + "config/fixedStorage/delById",
				    async:false,
					dataType:"json",
//		            data:{"ids":ids},
					data:{
						deleteList:JSON.stringify(selectedRows)
					},
				    success: function(r){
						if(r.code == 0){
							js.showMessage(languageObj.DELETE_SUCCESS);
							vm.reload();
						}else{
//							alert(r.msg);
							layer.msg(r.msg,{time:2000});
						}
					}
				});
				layer.close(index);
			});
		  } 
		  else layer.msg(languageObj.SELECT_DELETE,{time:1000});
	  });
	  //------新增操作---------
	  $("#newOperation").click(function(){
		   var options = {
	    		type: 2,
	    		maxmin: true,
	    		shadeClose: true,
	    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>'+languageObj.ADD_FIXED_STORAGE,
	    		area: ["500px", "560px"],
	    		content: baseURL+"wms/config/wms_c_mat_fixed_storage_add.html",  
	    		btn: ['<i class="fa fa-check"></i> '+languageObj.CONFIRM],
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
	    	options.btn.push('<i class="fa fa-close"></i> '+languageObj.CLOSE);
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
				title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>'+languageObj.MODIFY_FIXED_STORAGE,
				area: ["500px", "560px"],
				content: baseURL+'wms/config/wms_c_mat_fixed_storage_edit.html',
				btn: ['<i class="fa fa-check"></i> '+languageObj.CONFIRM],
				success:function(layero, index){
					var win = layero.find('iframe')[0].contentWindow;
					win.vm.saveFlag = false;
					win.vm.getInfo(grData.id,grData.maktx);
					$(win.document.body).find("#werks").attr("readonly",true);
					$(win.document.body).find("#whNumber").attr("readonly",true);
					$(win.document.body).find("#matnr").attr("readonly",true);
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
			options.btn.push('<i class="fa fa-close"></i> '+languageObj.CLOSE);
			options['btn'+options.btn.length] = function(index, layero){
				if(typeof listselectCallback == 'function'){
				}         
			};
			js.layer.open(options);
		 }else{
			 layer.msg(languageObj.SELECT_EDIT,{time:1000});
		 }
	  });
	//-------导入操作--------
	  $("#importOperation").click(function(){
		  var options = {
				type: 2,
				maxmin: true,
				shadeClose: true,
				title: "<i class='fa fa-upload' aria-hidden='true'>&nbsp;</i> "+languageObj.IMPORT,
				area: ["880px", "520px"],
				content: baseUrl + "wms/config/wms_c_mat_fixed_storage_upload.html",
				btn: ['<i class="fa fa-check"></i> '+languageObj.SAVE],
				
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
				}
			};
			options.btn.push('<i class="fa fa-close"></i> '+languageObj.CLOSE);
			js.layer.open(options);
	  });
});

var vm = new Vue({
	el:'#rrapp',
	data:{
		warehourse:[]
	},
	created:function(){
		//初始化查询仓库
		var plantCode = $("#werks").val();
		
        //初始化仓库
        $.ajax({
      	  url:baseUrl + "common/getWhDataByWerks",
      	  data:{
	  		 "WERKS":plantCode,
	  		 "MENU_KEY":"FIXED_STORAGE"
	  	  },
      	  success:function(resp){
      		 vm.warehourse = resp.data;
      		if(resp.data.length>0){
    			 vm.whNumber=resp.data[0].WH_NUMBER//方便 v-mode取值
    		 }
      		
      	  }
        });
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
//			getFactorySelectForRedis("#werks",null,null);
		},
		getWhNoFuzzy:function(){
			getWhNoSelect("#whNumber",null,null);
//			getWhNoSelectForRedis("#whNumber",null,null);
		},
		onPlantChange:function(event){
			//工厂变化的时候，更新仓库号下拉框
	        var plantCode = event.target.value;
	        if(plantCode === null || plantCode === '' || plantCode === undefined){
	        	return;
	        }
	        //查询工厂仓库
	        $.ajax({
	        	url:baseUrl + "common/getWhDataByWerks",
	        	data:{"WERKS":plantCode,"MENU_KEY":"FIXED_STORAGE"},
	        	success:function(resp){
	        		vm.warehourse = resp.data;
	        		if(resp.data.length>0){
	        			vm.whNumber=resp.data[0].WH_NUMBER//方便 v-mode取值
	         		}
	        	}
	          })
		},
	}
});

function export2Excel(){
	var column=[
		{ "title": '序号', "class": 'center', "data": 'id'},
      	{ "title": '仓库号', "class": 'center', "data": 'whNumber'},
      	{ "title": '工厂', "class": 'center', "data": 'werks'},
      	{ "title": '物料号', "class": 'center', "data": 'matnr'},
      	{ "title": '物料描述', "class":'center', "data": 'maktx'},
      	{ "title": '存储类型', "class":'center', "data": 'storageAreaCode'},
      	{ "title": '储位', "class": 'center', "data": 'binCode'},
      	{ "title": '优先级', "class": 'center', "data": 'seqno'},
      	//{ "title": '存储单位数量', "class": 'center', "data": 'qty'},
      	{ "title": '最小数量', "class": 'center', "data": 'stockM'},
      	{ "title": '最大数量', "class": 'center', "data": 'stockL'},
      	{ "title": '库位', "class": 'center', "data": 'lgort'},
      	{ "title": '库存类型', "class": 'center', "data": 'sobkz'},
      	{ "title": '供应商', "class": 'center', "data": 'lifnr'},
      	{ "title": '最后更新人', "class": 'center', "data": 'editor'},
      	{ "title": '最后更新时间', "class": 'center', "data": 'editDate'}
      ];
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"config/fixedStorage/list",
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) { 
		    results=response.page.list;
		}
	})
	var rowGroups=[];
	getMergeTable(results,column,rowGroups,"物料固定储位");
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