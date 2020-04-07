$(function () {
    $("#dataGrid").dataGrid({
    	searchForm: $("#searchForm"),
        columnModel: [			
			{ label: '工厂代码', name: 'WERKS', index: 'werks', width: 80,align:'center' },
			{ label: '仓库号', name: 'WH_NUMBER', index: 'whNumber', width: 80,align:'center' },
			{ label: '包规类型', name: 'PACKAGE_TYPE_DESC', index: 'packageType', width: 80,align:'center'  }, 			
			{ label: '物料号', name: 'MATNR', index: 'matnr', width: 80,align:'center' },
			{ label: '供应商', name: 'LIFNR', index: 'lifnr', width: 80,align:'center'  },
			{ label: '满箱数量', name: 'FULL_BOX_QTY', index: 'fullBoxQty', width: 80,align:'center'  },
			{ label: '状态', name: 'STATUS_DESC', index: 'status', width: 80,align:'center'  },
			{ label: '创建人', name: 'CREATOR', index: 'creator', width: 80,align:'center'  }, 			
			{ label: '创建时间', name: 'CREATE_DATE', index: 'createDate', width: 80,align:'center'  },
			{ label: '编辑人', name: 'EDITOR', index: 'editor', width: 80,align:'center'  }, 			
            { label: '编辑时间', name: 'EDIT_DATE', index: 'editDate', width: 80,align:'center'  },	
            {label:'查看明细', name:'actions',align:"center", width:80, sortable:false, title:false, formatter: function(val, obj, row, act){
				var actions = [];
				actions.push("<a title='查看明细' onClick='vm.detail("+obj.rowId+")'><i class='fa fa-search' style='color:blue;cursor: pointer;'></i></a>&nbsp;");
				return actions.join('');
			}},
			{ label: 'ID', name: 'ID', index: 'id', hidden:true },
			{ label: 'PACKAGE_TYPE',name:'PACKAGE_TYPE',index:'id',hidden:true},
            { label: 'STATUS',name:'STATUS',index:'id',hidden:true},
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
				    url: baseURL + "config/matPackage/delById",
				    dataType: "json",
				    data:{
				    	headId:grData.ID
				    },
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
      		shadeClose: false,
      		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>新增物料包装规格',
      		area: ["780px", "500px"],
      		content: baseURL + 'wms/config/wms_c_mat_package_copy.html',
      		btn: ['<i class="fa fa-check"></i> 确定'],
      		success:function(layero, index){
      			var win = layero.find('iframe')[0].contentWindow;
      			win.vm.saveFlag = false;
      			win.vm.headId = 0;
      		},
      		btn1:function(index,layero){
      			var win = layero.find('iframe')[0].contentWindow;
  				win.vm.saveOrUpdate();
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
	  });
	  //-------编辑操作--------
	  $("#editOperation").click(function(){
		  var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
		  if(gr !== null && gr!==undefined){
			 var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
			 var options = {
		      		type: 2,
		      		maxmin: true,
		      		shadeClose: false,
		      		title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>编辑物料包装规格',
		      		area: ["780px", "500px"],
		      		content: baseURL + 'wms/config/wms_c_mat_package_edit.html',
		      		btn: ['<i class="fa fa-check"></i> 确定'],
		      		success:function(layero, index){
		      			console.log("grData",grData);
		      			var win = layero.find('iframe')[0].contentWindow;
		      			win.vm.headId = grData.ID;
                        win.vm.matPackage=grData;
                        win.vm.warehourse.push({WH_NUMBER:grData.WH_NUMBER});
                        $(win.document.body).find("#werks").attr("disabled",true);
                        $(win.document.body).find('#whNumber').attr("disabled",true);
                        $(win.document.body).find('#matnr').attr("readonly",true);
		      		},
		      		btn1:function(index,layero){
		      			var win = layero.find('iframe')[0].contentWindow;
		  				win.vm.saveOrUpdate();
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
    	warehourse:[],
    	werks:"",
    	whNumber:""
    },
    created: function(){
		this.werks=$("#werks").find("option").first().val();
	},
	watch:{
		werks:{
			handler:function(newVal,oldVal){
				  //工厂变化的时候，更新库存下拉框
		          //查询工厂仓库
		          $.ajax({
		        	  url:baseUrl + "common/getWhDataByWerks",
		        	  data:{"WERKS":newVal},
		        	  success:function(resp){
		        		 vm.warehourse = resp.data;
		        		 vm.whNumber = resp.data[0].WH_NUMBER;
		        	  }
		          })			
			}
		}
	},
	methods: {
		refresh:function(){
			$("#searchForm").submit();
        },
		reload: function (event) {
			$("#searchForm").submit();
		},
        detail:function(rowid){
            var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
            console.log("rowid",rowid);
			 var grData = $("#dataGrid").jqGrid("getRowData",rowid)//获取选中的行数据
			 var options = {
		      		type: 2,
		      		maxmin: true,
		      		shadeClose: false,
		      		title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>物料包装规格明细',
		      		area: ["780px", "500px"],
		      		content: baseURL + 'wms/config/wms_c_mat_package_edit.html',
		      		btn: [],
		      		success:function(layero, index){
		      			console.log("grData",grData);
		      			var win = layero.find('iframe')[0].contentWindow;
		      			win.vm.headId = grData.ID;
                        win.vm.matPackage=grData;
                        win.vm.type="detail";
                        win.vm.warehourse.push({WH_NUMBER:grData.WH_NUMBER});
                        $(win.document.body).find("#werks").attr("disabled",true);
                        $(win.document.body).find('#whNumber').attr("disabled",true);
                        $(win.document.body).find('#matnr').attr("readonly",true);
		      		},
		      	};
		      	options.btn.push('<i class="fa fa-close"></i> 关闭');
		      	options['btn'+options.btn.length] = function(index, layero){
		      		if(typeof listselectCallback == 'function'){
		      			try {
		      				parent.layer.close(index);
		      	        } catch(e){
		      	        	layer.close(index);
		      	        }
		      		}
		      	};
		      	js.layer.open(options);
        }
	}
});
function export2Excel(){
	var column=[
      	{"title":"工厂代码","class":"center","data":"WERKS"},
      	{"title":"仓库号","class":"center","data":"WH_NUMBER"},
      	{"title":"业务类型名称","class":"center","data":"BUSINESS_NAME_DESC"},
      	{"title":"部门","class":"center","data":"DEPARTMENT"},
      	{"title":"工号","class":"center","data":"STAFF_NUMBER"},
      	{"title":"姓名","class":"center","data":"USER_NAME"},
      	{"title":"状态","class":"center","data":"STATUS"},
      	{"title":'创建人', "data": 'CREATOR',"class":'center'}, 			
		{"title":'创建时间', "data": 'CREATE_DATE',"class":'center'},
		{"title":'最近更新人', "data": 'EDITOR', "class":'center'}, 			
		{"title":'最近更新时间', "data": 'EDIT_DATE',"class":'center'},	
      ]	;
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"config/matPackage/list",
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) { 
		    results=response.page.list;
		    for(var i=0;i<results.length;i++){
		    	var status=results[i].status;
		    	if(status=='X'){results[i].status='锁定';}
			    if(status=='0'){results[i].status='开通';}
		    }	    
		}
	})
	var rowGroups=[];
	getMergeTable(results,column,rowGroups,"物料包装规格配置");	
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