$(function(){
	  
	  $("#dataGrid").dataGrid({
	    	searchForm:$("#searchForm"),
	        datatype: "json",
	        colModel: [			
	            {label: '工厂代码', name: 'werks',index:"werks", width: 200,align:"center"},
	            {label: '工厂名称', name: 'werksName',index:"werks_name", width: 200,align:"center"},
				{label: '供应商代码', name: 'lifnr',index:"lifnr", width: 200,align:"center"},
	            {label: '供应商名称', name: 'name1',index:"name1", width: 200,align:"center"},
				{label: '供应商简称',name: 'shortName',index:"SHORT_NAME",width: 200,align:"center"},
	            {label: '是否已上SCM', name: 'isScm', index:"IS_SCM", width: 150,align:"center",formatter:function(cellval, obj, row){
					var html="";
					if(row.isScm=='0'){
						html= "<span>否</span>"
					}
					if(row.isScm=='X'){
						html= "<span>是</span>"
					}
					return html;
					
				}},
				{label: '供应商管理者', name: 'vendorManager',index:"VENDOR_MANAGER", width: 150,align:"center" },
				{label: '维护人', name: 'editor',index:"editor", width: 100,align:"center"},
				{label:'维护时间',name:'editDate',index:"edit_date",align:"center",width: 220},
				{label:'ID',width:75,name:'id',index:"id",align:"center",hidden:true}//隐藏此列
	        ],
			viewrecords: true,
	        rowNum: 15,
	        rownumWidth: 25, 
	        showCheckbox:true
	    });
	  
	
	//------删除操作---------
	  $("#deleteOperation").click(function(){
		  var gr = $("#dataGrid").jqGrid('getGridParam', 'selarrrow');//获取选中的行号
		  var ids="";
		  for(var i=0;i<gr.length;i++){
			  ids=ids+gr[i]+",";
		  }
		  if (gr != null) {
			  layer.confirm('确定删除选中的记录?', {icon: 3, title:'提示'}, function(index){
				  //do something
				  var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
				  $.ajax({
					  url:baseUrl + "config/CVendor/dels",
					  type:"post",
					  data:{"ids":ids},
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
		  } 
		  else layer.msg("请选择要删除的记录",{time:2000});
	  });
	  //------新增操作---------
	  $("#newOperation").click(function(){
			js.layer.open({
				type: 2,
				title: "新增供应商配置",
				area: ["600px", "260px"],
				content: baseUrl + "wms/config/wms_c_vendor_edit.html",
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
				title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>编辑供应商配置信息',
				area: ["600px", "260px"],
				content: baseUrl + 'wms/config/wms_c_vendor_edit.html',
				btn: ['<i class="fa fa-check"></i> 确定'],
				success:function(layero, index){
					var win = layero.find('iframe')[0].contentWindow;
					win.vm.saveFlag = false;
					win.vm.getInfo(grData.id);
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
					title: "<i class='fa fa-upload' aria-hidden='true'>&nbsp;</i> 导入供应商配置",
					area: ["900px", "500px"],
					content: baseUrl + "wms/config/wms_c_vendor_upload.html",
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
		},
		getVendorNoFuzzy:function(){
			getVendorNoSelect("#lifnr",null,null,$("werks").val());
		}
	}
});

function export2Excel(){
	var column=[
      	{"title":"工厂代码","class":"center","data":"werks"},
      	{"title":"工厂名称","class":"center","data":"werksName"},
      	{"title":"供应商代码","class":"center","data":"lifnr"},
      	{"title":"供应商名称","class":"center","data":"name1"},
      	{"title":"供应商简称","class":"center","data":"shortName"},
      	{"title":"供应商管理者","class":"center","data":"vendorManager"},
      	{"title":"是否已上SCM","class":"center","data":"isScm"}
      ]	;
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"config/CVendor/list",
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) { 
		    results=response.page.list;
		}
	})
	var rowGroups=[];
	for(var i=0;i<results.length;i++){
		if(results[i].isScm=="0"){
			results[i].isScm="是";
		}else if(results[i].isScm=="1"){
			results[i].isScm="否";
		}
		
	}
	getMergeTable(results,column,rowGroups,"供应商配置");	
}

function getSelRow(gridSelector){
	var gr = $(gridSelector).jqGrid('getGridParam', 'selrow');//获取选中的行号
	return gr != null?$(gridSelector).jqGrid("getRowData",gr):null;
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