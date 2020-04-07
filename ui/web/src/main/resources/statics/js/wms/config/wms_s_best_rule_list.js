//国际化取值
var smallUtil=new smallTools();
var array = new Array("WH_NUMBER","PLANT","PRIORITY","TYPE","CONTROL_FLAG","WH_BUSINESS_TYPE",
		"LGORT","STOCK_TYPE","EDITOR","EDITDATE","DELETE_SUCCESS","ADD","UPDATE",
		"MAKE_SURE_DEL","SELECT_DELETE","PROMPT_MESSAGE","CONFIRM","CLOSE","SAVE","SELECT_EDIT");
var languageObj = smallUtil.loadlanguageByPKeyAndLKey(array,"M");

$(function () {
    $("#dataGrid").dataGrid({
    	searchForm: $("#searchForm"),
        columnModel: [			
			{ label: languageObj.WH_NUMBER, name: 'whNumber', index: 'whNumber', width: 70,align:'center' },
//			{ label: languageObj.PLANT, name: 'werks', index: 'werks', width: 50,align:'center'  }, 			
			{ label: languageObj.TYPE, name: 'ruleType', index: 'ruleType', width: 55,align:'center'  }, 
			{ label: languageObj.PRIORITY, name: 'seqno', index: 'seqno', width: 50,align:'center'  }, 
			{ label: languageObj.CONTROL_FLAG, name: 'controlFlag', index: 'controlFlag', width: 60,align:'center'  }, 
			{ label: languageObj.WH_BUSINESS_TYPE, name: 'businessTypeFlag', index: 'businessTypeFlag', width: 80,align:'center'  }, 
			{ label: languageObj.LGORT, name: 'lgortFlag', index: 'lgortFlag', width: 50,align:'center'  }, 
			{ label: languageObj.STOCK_TYPE, name: 'stockTypeFlag', index: 'stockTypeFlag', width: 60,align:'center'  }, 
			{ label: languageObj.EDITOR, name: 'editor', index: 'editor', width: 50,align:'center'  }, 
			{ label: languageObj.EDITDATE, name: 'editDate', index: 'editDate', width: 90,align:'center'  },
			{ label: 'ID', name: 'id', index: 'id', hidden:true },
		],
		rowNum : 15
    });
  //------删除操作---------
	  $("#deleteOperation").click(function(){
		  var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
		  if (gr != null  && gr!==undefined) {
			  layer.confirm(languageObj.MAKE_SURE_DEL, {icon: 3, title:languageObj.PROMPT_MESSAGE}, function(index){
				  var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
				  $.ajax({
					type: "POST",
				    url: baseURL + "config/ruleConfig/delById?id="+grData.id,
				    contentType: "application/json",
				    success: function(r){
						if(r.code == 0){
							js.showMessage(languageObj.DELETE_SUCCESS);
							vm.reload();
						}else{
							alert(r.msg);
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
	    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>'+languageObj.ADD,
	    		area: ["480px", "390px"],
	    		content: baseURL + "wms/config/wms_s_best_rule_edit.html",  
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
				title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>'+languageObj.UPDATE,
				area: ["480px", "390px"],
				content: baseURL + 'wms/config/wms_s_best_rule_edit.html',
				btn: ['<i class="fa fa-check"></i> '+languageObj.CONFIRM],
				success:function(layero, index){
					var win = layero.find('iframe')[0].contentWindow;
					win.vm.saveFlag = false;
					win.vm.getInfo(grData.id,grData.maktx);
//					$(win.document.body).find("#werks").attr("readonly",true);
					$(win.document.body).find("#whNumber").attr("readonly",true);
					$(win.document.body).find("#ruleType").attr("readonly",true);
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
//			getPlantNoSelect("#werks",null,null);
//			getFactorySelectForRedis("#werks",null,null);
		},
		getWhNoFuzzy:function(){
			getWhNoSelect("#whNumber",null,null);
//			getWhNoSelectForRedis("#whNumber",null,null);
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