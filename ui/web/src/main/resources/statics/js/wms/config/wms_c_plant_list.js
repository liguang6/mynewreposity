$(function(){
	  
	  $("#dataGrid").dataGrid({
	    	searchForm:$("#searchForm"),
	        datatype: "json",
	        colModel: [			
	            {label: '工厂代码', name: 'WERKS',index:"werks", width: 70,align:"center"},
				{label: '工厂名称', name: 'WERKS_NAME',index:"werksName", width: 120,align:"center"},
				{label: '仓库号', name: 'WH_NUMBER',index:"whNumber", width: 70,align:"center"},
				//{label: '库存地点', name: 'LGORT',index:"lgort", width: 80,align:"center"},
				{label: '启用供应商管理',name: 'VENDOR_FLAG',index:"vendorFlag",width: 100,align:"center",formatter:function(cellval, obj, row){
					var html="";
					if(row.VENDOR_FLAG=='X'){
						html= "<span>是</span>"
					}
					if(row.VENDOR_FLAG=='0'){
						html= "<span>否</span>"
					}
					return html;
					
				}},
	            {label: '启用智能储位', name: 'IG_FLAG', index:"igFlag", width: 90,align:"center",formatter:function(cellval, obj, row){
					var html="";
					if(row.IG_FLAG=='X'){
						html= "<span>是</span>"
					}
					if(row.IG_FLAG=='0'){
						html= "<span>否</span>"
					}
					return html;
					
				} },
				{label: '是否已上WMS', name: 'WMS_FLAG',index:"wmsFlag", width: 100,align:"center",formatter:function(cellval, obj, row){
					var html="";
					if(row.WMS_FLAG=='X'){
						html= "<span>是</span>"
					}
					if(row.WMS_FLAG=='0'){
						html= "<span>否</span>"
					}
					return html;
					
				} },
				{label: '启用核销业务', name: 'HX_FLAG',index:"hxFlag", width: 90,align:"center",formatter:function(cellval, obj, row){
					var html="";
					if(row.HX_FLAG=='X'){
						html= "<span>是</span>"
					}
					if(row.HX_FLAG=='0'){
						html= "<span>否</span>"
					}
					return html;
					
				} },
				{label: '启用最小包装', name: 'PACKAGE_FLAG',index:"packageFlag", width: 90,align:"center",formatter:function(cellval, obj, row){
					var html="";
					if(row.PACKAGE_FLAG=='X'){
						html= "<span>是</span>"
					}
					if(row.PACKAGE_FLAG=='0'){
						html= "<span>否</span>"
					}
					return html;
					
				} },
				/*{label: 'PDA拣配确认标识', name: 'PDA_PICK_FLAG',index:"pdaPickFlag", width: 100,align:"center",formatter:function(cellval, obj, row){
					var html="";
					if(row.PDA_PICK_FLAG=='X'){
						html= "<span>是</span>"
					}
					if(row.PDA_PICK_FLAG=='0'){
						html= "<span>否</span>"
					}
					return html;
					
				} },*/
				{label: '启用条码', name: 'BARCODE_FLAG',index:"barcodeFlag", width: 70,align:"center",formatter:function(cellval, obj, row){
					var html="";
					if(row.BARCODE_FLAG=='X'){
						html= "<span>是</span>"
					}
					if(row.BARCODE_FLAG=='0'){
						html= "<span>否</span>"
					}
					return html;
					
				} },
				{label: '启用预留', name: 'RESBD_FLAG',index:"resbdFlag", width: 70,align:"center",formatter:function(cellval, obj, row){
					var html="";
					if(row.RESBD_FLAG=='X'){
						html= "<span>是</span>"
					}
					if(row.RESBD_FLAG=='0'){
						html= "<span>否</span>"
					}
					return html;
					
				} },
				{label: '启用费用性订单库存管理', name: 'CMMS_FLAG',index:"cmmsFlag", width: 150,align:"center",formatter:function(cellval, obj, row){
					var html="";
					if(row.CMMS_FLAG=='X'){
						html= "<span>是</span>"
					}
					if(row.CMMS_FLAG=='0'){
						html= "<span>否</span>"
					}
					return html;
					
				} },
				{label: '校验人料关系', name: 'MAT_MANAGER_FLAG',index:"matManagerFlag", width: 90,align:"center",formatter:function(cellval, obj, row){
					var html="";
					if(row.MAT_MANAGER_FLAG=='X'){
						html= "<span>是</span>"
					}
					if(row.MAT_MANAGER_FLAG=='0'){
						html= "<span>否</span>"
					}
					return html;
					
				} },
				{label: '是否启用保质期', name: 'PRFRQFLAG', index: 'PRFRQFLAG', width: 120,align:'center',formatter:function(cellval, obj, row){
                        var html="";
                        if(row.PRFRQFLAG=='X'){
                            html= "<span>是</span>"
                        }
                        if(row.PRFRQFLAG=='0'){
                            html= "<span>否</span>"
                        }
                        return html;
                    }  },
                {label: '冻结/解冻是否启用SAP过账', name: 'FREEZEPOSTSAPFLAG', index: 'FREEZEPOSTSAPFLAG', width: 180,align:'center',formatter:function(cellval, obj, row){
                    var html="";
                    if(row.FREEZEPOSTSAPFLAG=='X'){
                        html= "<span>是</span>"
                    }
                    if(row.FREEZEPOSTSAPFLAG=='0'){
                        html= "<span>否</span>"
                    }
                    return html;
                }  },
				{label: '维护人', name: 'EDITOR',index:"editor", width: 90,align:"center" },
				{label:'维护时间',name:'EDITOR_DATE',index:"editorDate",width: 140,align:"center"},
				{label:'ID',width:75,name:'ID',index:"id",align:"center",hidden:"true"}//隐藏此列
	        ],
			viewrecords: true,
	        rowNum: 15,
	        rownumWidth: 25, 
	        showCheckbox:true,
	        autowidth:true,
	        shrinkToFit:false,  
	        autoScroll: true
	    });
	  
	
	  //------删除操作---------
	  $("#deleteOperation").click(function(){
		  var gr = $("#dataGrid").jqGrid('getGridParam', 'selarrrow');//获取选中的行号
		  var ids="";
		  
		  for(var item in gr){
				var row = $("#dataGrid").jqGrid('getRowData',gr[item]);
				ids=ids+row.ID+",";
			}
		  
		  if (gr != null) {
			  layer.confirm('确定删除选中的记录?', {icon: 3, title:'提示'}, function(index){
				  //do something
				  var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
				  $.ajax({
					  url:baseUrl + "config/CPlant/dels",
					  type:"post",
					  data:{"ids":ids},
					  success:function(resp){
						  if(resp.code === 0){
							  js.showMessage('删除成功');
							  $("#searchForm").submit();//重新查询数据
						  }else{
							 alert("删除失败,"+resp.msg);
						  }
						  layer.close(index);
					  }
				  }); 
				});
		  } 
		  else layer.msg("请选择要删除的记录",{time:2000});
	  });
	  //------新增操作---------
	  $("#newOperation").click(function(){
			js.layer.open({
				type: 2,
				title: "新增仓库配置",
				area: ["780px", "320px"],
				content: baseUrl + "wms/config/wms_c_plant_edit.html",
				closeBtn:1,
				end:function(){
					$("#searchForm").submit();
				},
				btn: ["确定","取消"],
				yes:function(index,layero){
					//提交表单
					var win = layero.find('iframe')[0].contentWindow;
					var btnSubmit= $("#btnSubmit", layero.find("iframe")[0].contentWindow.document);
					$(btnSubmit).click();
					if(typeof listselectCallback == 'function'){
						listselectCallback(index,win.vm.saveFlag);
					}
				},
				no:function(index,layero){
					layer.close(index);
				}
		});
	  })
	  //-------编辑操作--------
	  $("#editOperation").click(function(){
		  var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
		 if(gr !== null && gr!==undefined){
			 var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
			 var options = {
				type: 2,
				maxmin: true,
				shadeClose: true,
				title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>编辑仓库配置',
				area: ["780px", "320px"],
				content: baseUrl +'wms/config/wms_c_plant_edit.html',
				btn: ['<i class="fa fa-check"></i> 确定'],
				success:function(layero, index){
					var win = layero.find('iframe')[0].contentWindow;
					win.vm.saveFlag = false;
					win.vm.getInfo(grData.ID);
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
	  
	//-------上传操作--------
	  $("#importOperation").click(function(){
		  var options = {
					type: 2,
					maxmin: true,
					shadeClose: true,
					title: "<i class='fa fa-upload' aria-hidden='true'>&nbsp;</i> 导入仓库配置",
					area: ["70%", "60%"],
					content: baseUrl + "wms/config/wms_c_plant_upload.html",
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
	  })
	  
	
});

var vm = new Vue({
	el:'#rrapp',
	data:{},
	methods: {
		refresh:function(){
			$("#searchForm").submit();
        },
		reload: function (event) {
			$("#searchForm").submit();
		},
		getPlantNoFuzzy:function(){
			getPlantNoSelect("#werks",null,null);
		}
	}
});

function getSelRow(gridSelector){
	var gr = $(gridSelector).jqGrid('getGridParam', 'selrow');//获取选中的行号
	return gr != null?$(gridSelector).jqGrid("getRowData",gr):null;
}

function export2Excel(){
	var column=[
      	{"title":"工厂代码","class":"center","data":"WERKS"},
      	{"title":"工厂名称","class":"center","data":"WERKS_NAME"},
      	{"title":"仓库号","class":"center","data":"WH_NUMBER"},
      	//{"title":"库存地点","class":"center","data":"LGORT"},
      	{"title":"启用供应商管理","class":"center","data":"VENDOR_FLAG"},
      	{"title":"启用智能储位","class":"center","data":"IG_FLAG"},
      	{"title":"是否已上WMS","class":"center","data":"WMS_FLAG"},
      	{"title":"是否启用核销业务","class":"center","data":"HX_FLAG"},
      	{"title":"是否启用最小包装","class":"center","data":"PACKAGE_FLAG"},
      	//{"title":"PDA拣配确认标识","class":"center","data":"PDA_PICK_FLAG"},
      	{"title":"是否启用条码","class":"center","data":"BARCODE_FLAG"},
      	{"title":"是否启用预留","class":"center","data":"RESBD_FLAG"},
      	{"title":"是否启用费用性订单库存管理","class":"center","data":"CMMS_FLAG"},
        {"title":"是否启用保质期","class":"center","data":"PRFRQFLAG"},
        {"title":"冻结/解冻是否启用SAP过账","class":"center","data":"FREEZEPOSTSAPFLAG"},
      	{"title":"维护人","class":"center","data":"EDITOR"},
      	{"title":"维护时间","class":"center","data":"EDITOR_DATE"}
      ]	;
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"config/CPlant/list",
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) { 
		    results=response.page.list;
		}
	})
	var rowGroups=[0,1];
	for(var i=0;i<results.length;i++){
		if(results[i].VENDOR_FLAG=="X"){
			results[i].VENDOR_FLAG="是";
		}else if(results[i].VENDOR_FLAG=="0"){
			results[i].VENDOR_FLAG="否";
		}
		//
		
		if(results[i].IG_FLAG=="X"){
			results[i].IG_FLAG="是";
		}else if(results[i].IG_FLAG=="0"){
			results[i].IG_FLAG="否";
		}
		//
		if(results[i].WMS_FLAG=="X"){
			results[i].WMS_FLAG="是";
		}else if(results[i].WMS_FLAG=="0"){
			results[i].WMS_FLAG="否";
		}
		//
		if(results[i].HX_FLAG=="X"){
			results[i].HX_FLAG="是";
		}else if(results[i].HX_FLAG=="0"){
			results[i].HX_FLAG="否";
		}
		//
		if(results[i].PACKAGE_FLAG=="X"){
			results[i].PACKAGE_FLAG="是";
		}else if(results[i].PACKAGE_FLAG=="0"){
			results[i].PACKAGE_FLAG="否";
		}
		//
		
		if(results[i].BARCODE_FLAG=="X"){
			results[i].BARCODE_FLAG="是";
		}else if(results[i].BARCODE_FLAG=="0"){
			results[i].BARCODE_FLAG="否";
		}
		//
		if(results[i].RESBD_FLAG=="X"){
			results[i].RESBD_FLAG="是";
		}else if(results[i].RESBD_FLAG=="0"){
			results[i].RESBD_FLAG="否";
		}
		//
		if(results[i].CMMS_FLAG=="X"){
			results[i].CMMS_FLAG="是";
		}else if(results[i].CMMS_FLAG=="0"){
			results[i].CMMS_FLAG="否";
		}
        if(results[i].PRFRQFLAG=="X"){
            results[i].PRFRQFLAG="是";
        }else if(results[i].PRFRQFLAG=="0"){
            results[i].PRFRQFLAG="否";
        }
	}
	getMergeTable(results,column,rowGroups,"仓库配置");	
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