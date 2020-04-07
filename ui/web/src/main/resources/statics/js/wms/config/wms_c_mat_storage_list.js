//国际化取值
var smallUtil=new smallTools();
var array = new Array("WH_NUMBER","PLANT","MATNR","MATNR_DESC","STORAGE_TYPE","IMPORT");
var languageObj = smallUtil.loadlanguageByPKeyAndLKey(array,"M");

$(function () {
    $("#dataGrid").dataGrid({
    	searchForm: $("#searchForm"),
        columnModel: [	
        	{label: languageObj.PLANT, name: 'werks', index: 'werks', width: 55,align:'center' },
			{label: languageObj.WH_NUMBER, name: 'whNumber', index: 'whNumber', width: 55,align:'center' },
        	{label: languageObj.MATNR, name: 'matnr', index: 'matnr', width: 90,align:'center' },
        	{label: languageObj.MATNR_DESC, name: 'maktx', index: 'maktx', width: 180,align:'center' },
//			{label: '存储类型代码', name: 'storageAreaCode', index: 'storageAreaCode', width: 100,align:'center'  },
//			{label: '存储模式', name: 'storageModel', index: 'storageModel', width: 80,align:'center' ,formatter: function(item, index){
//    		    return item=='00' ? "固定存储" : (item=='01' ? '随机存储' : '');
//		    	}
//		    },
		    {label: '入库控制标识', name: 'inControlFlag', index: 'inControlFlag', width: 90,align:'center'  },
		    {label: '出库控制标识', name: 'outControlFlag', index: 'outControlFlag', width: 90,align:'center'  },
		    {label: '长', name: 'length', index: 'length', width: 50,align:'center'  }, 			
		    {label: '宽', name: 'width', index: 'width', width: 50,align:'center'  }, 			
		    {label: '高', name: 'height', index: 'height', width: 50,align:'center'  },
		    {label: '长宽高单位', name: 'sizeUnit', index: 'sizeUnit', width: 80,align:'center'  },
		    {label: '体积', name: 'volum', index: 'volum', width: 50,align:'center'  },
		    {label: '体积单位', name: 'volumUnit', index: 'volumUnit', width: 70,align:'center'  },
		    {label: '重量', name: 'weight', index: 'weight', width: 50,align:'center'  },
		    {label: '重量单位', name: 'weightUnit', index: 'weightUnit', width: 70,align:'center'  },
		    {label: '最小包装单位', name: 'storageUnit', index: 'storageUnit', width: 100,align:'center'  },
		    {label: '单存储单元数量', name: 'qty', index: 'qty', width: 100,align:'center'  },
		    {label: '最大库存', name: 'stockL', index: 'stockL', width: 70,align:'center'  }, 
		    {label: '最小库存', name: 'stockM', index: 'stockM', width: 70,align:'center'  },
		    {label: '是否启用最小包装', name: 'mpqFlag', index: 'mpqFlag', width: 120,align:'center',formatter: function(item, index){
    		    return item=='0' ? "否" : (item=='X' ? '是' : '');
	    	}  },
	    	{label: '是否启用外部批次', name: 'externalBatchFlag', index: 'externalBatchFlag', width: 120,align:'center',formatter: function(item, index){
    		    return item=='0' ? "否" : (item=='X' ? '是' : '');
	    	}  },
	    	{label: '相关联物料', name: 'correlationMaterial', index: 'correlationMaterial', width: 100,align:'center'},
	    	{label: '排斥物料', name: 'repulsiveMaterial', index: 'repulsiveMaterial', width: 100,align:'center'},
	    	//{label: '单存储单元数量', name: 'storageUnitQty', index: 'storageUnitQty', width: 80,align:'center'},
	    	{label: '创建人', name: 'editor', index: 'editor', width: 80,align:'center'  }, 			
			{label: '创建时间', name: 'editDate', index: 'editDate', width: 80,align:'center'  },
			{"label": 'ID', "name": 'id', index: 'id', hidden:true }
		],
		rowNum : 15,
		autowidth:true,
        shrinkToFit:false,  
        autoScroll: true
    });
  //------删除操作---------
	  $("#deleteOperation").click(function(){
		  var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
		  if (gr != null  && gr!==undefined) {
			  layer.confirm('确定删除选中的记录?', {icon: 3, title:'提示'}, function(index){
				  var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
				  $.ajax({
					type: "POST",
				    url: baseURL + "config/matstorage/delById?id="+grData.id,
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
	    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>新增物料存储配置',
	    		area: ["800px", "500px"],
	    		content: baseURL + "wms/config/wms_c_mat_storage_edit.html",  
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
				title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>修改物料存储配置',
				area: ["800px", "500px"],
				content: baseURL + 'wms/config/wms_c_mat_storage_edit.html',
				btn: ['<i class="fa fa-check"></i> 确定'],
				success:function(layero, index){
					var win = layero.find('iframe')[0].contentWindow;
					win.vm.saveFlag = false;
					win.vm.getInfo(grData.id);
					$(win.document.body).find("#werks").attr("readonly",true);
					$(win.document.body).find("#whNumber").attr("readonly",true);
					//$(win.document.body).find("#matnr").attr("readonly",true);
//					$(win.document.body).find("#storageTypeCode").attr("disabled",true);
//					$(win.document.body).find("#areaCode").attr("disabled",true);
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
				title: "<i class='fa fa-upload' aria-hidden='true'>&nbsp;</i> 导入物料存储配置",
				area: ["1120px", "560px"],
				content: baseUrl + "wms/config/wms_c_mat_storage_upload.html",
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
				}
			};
			options.btn.push('<i class="fa fa-close"></i> 关闭');
			js.layer.open(options);
	  });
	  
});

var vm = new Vue({
	el:'#rrapp',
	data:{ warehourse:[]},
	created: function(){
    	// 加载存储类型代码select
		//getStorageTypeCodeSelect();
		//查询工厂仓库
		$.ajax({
			url:baseUrl + "common/getWhDataByWerks",
			data:{"WERKS":$("#werks").val()},
			success:function(resp){
				vm.warehourse = resp.data;
			}
		})
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
		getMaterialNoFuzzy:function(){
			/*
			 * $("#werks").val() : 工厂代码
			 * "#matnr" ： 物料号ID
			 * "#maktx" : 物料描述ID ;根据料号带出描述
			 */
			getMaterialNoSelect($("#werks").val(),"#matnr","#maktx",null,null);
		},
		// 仓库代码失去焦点，重新加载存储区代码
		whNumerBlur: function(){
			var whNumber=$("#whNumber").val();
			if(whNumber!=''){
				getAreaCodeSelect(whNumber);
			}
		},
		onPlantChange:function(event){
			  //工厂变化的时候，更新库存下拉框
	          var plantCode = event.target.value;
	          if(plantCode === null || plantCode === '' || plantCode === undefined){
	        	  return;
	          }
	          //查询工厂仓库
	          $.ajax({
	        	  url:baseUrl + "common/getWhDataByWerks",
	        	  data:{"WERKS":plantCode},
	        	  success:function(resp){
	        		 vm.warehourse = resp.data;
	        	  }
	          })
		},
	}
});
function export2Excel(){
	var column=[
		{"title":"工厂代码","class":"center","data":"werks"},
      	{"title":"仓库代码","class":"center","data":"whNumber"},
      	{"title":"物料号","class":"center","data":"matnr"},
	    {"title": "入库控制标识","class":"center", "data": 'inControlFlag'},
	    {"title":"出库控制标识","class":"center","data":"outControlFlag"},
	    {"title":"长","class":"center","data":"length"},
	    {"title": '宽', "class":"center","data": 'width' },
	    {"title": '高',"class":"center", "data": 'height'},
	    {"title": '长宽高单位', "class":"center","data": 'sizeUnit' },
	    {"title": '体积',"class":'center', "data": 'volum'  },
	    {"title": '体积单位',"class":'center', "data": 'volumUnit'  },
	    {"title": '重量',"class":'center', "data": 'weight'  },
	    {"title": '重量单位',"class":'center', "data": 'weightUnit'  },
	    {"title": '最小包装单位',align:'center', "data": 'storageUnit'}, 
	    {"title": '数量',align:'center', "data": 'qty'}, 
	    {"title": '最大库存',align:'center', "data": 'stockL'}, 
	    {"title": '最小库存',align:'center', "data": 'stockM'},
	    {"title": '是否启用最小包装',align:'center', "data": 'mpqFlag'},
	    {"title": '是否启用外部批次',align:'center', "data": 'externalBatchFlag'},
	    {"title": '相关联物料',align:'center', "data": 'correlationMaterial'},
	    {"title": '排斥物料',align:'center', "data": 'repulsiveMaterial'},
	    {"title": '创建人',align:'center', "data": 'editor'},
	    {"title": '创建时间',align:'center', "data": 'editDate'}
      ]	;
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"config/matstorage/list",
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) { 
		    results=response.page.list;
		    for(var i=0;i<results.length;i++){
			    var mpqFlag=results[i].mpqFlag;
			    if(mpqFlag=='0'){results[i].mpqFlag='否';}
			    if(mpqFlag=='X'){results[i].mpqFlag='是';}
			    
			    var externalBatchFlag=results[i].externalBatchFlag;
			    if(externalBatchFlag=='0'){results[i].externalBatchFlag='否';}
			    if(externalBatchFlag=='X'){results[i].externalBatchFlag='是';}
		    }
		    
		}
	})
	var rowGroups=[0,1];
	getMergeTable(results,column,rowGroups,"物料存储配置");	
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
function getStorageTypeCodeSelect(){
	$.ajax({
        type: "POST",
        url: baseURL + "config/storagetype/queryAll",
        dataType : "json",
        data: {},
        success: function(resp){
        	var data=resp.list;
        	var element=$("#storageTypeCode");
        	$.each(data,function(index,val){
        		element.append("<option value='"+val.storageTypeCode+"'>"+val.storageTypeName+"</option>");
        	});
        }
    });
}
function getAreaCodeSelect(whNumber,selectVal){
	$("#areaCode").html("");
	$.ajax({
        type: "POST",
        url: baseURL + "config/wharea/queryAll",
        dataType : "json",
        data: {
        	"whNumber":whNumber.toUpperCase(),
        },
        success: function(resp){
        	var data=resp.list;
        	var element=$("#areaCode");
        	element.append("<option value='' storagetypecode=''>请选择</option>");
        	$.each(data,function(index,val){
        		if(selectVal==val.areaCode){
        			element.append("<option value='"+val.areaCode+"' storagetypecode='"+val.storageTypeCode+"'" +
        			" selected>"+val.areaName+"</option>");
        		}else{
        			element.append("<option value='"+val.areaCode+"' storagetypecode='"+val.storageTypeCode+"'" +
        			">"+val.areaName+"</option>");
        		}
        	});
        }
    });
}