$(function () {
    $("#dataGrid").dataGrid({
    	searchForm: $("#searchForm"),
        columnModel: [		
            { label: '车间', name: 'WORKSHOP_NAME', index: 'WORKSHOP_NAME', width: 100,align:'center'  },
			{ label: '班组类别', name: 'TYPE', index: 'TYPE', width: 100,align:'center'  ,formatter:function(cellval, obj, row){
				var html="";
				if(row.TYPE=='0'){
					html= "<span>标准班组</span>"
				}
				if(row.TYPE=='1'){
					html= "<span>标准小班组</span>"
				}
				
				return html;
				
			}},
			{ label: '班组代码', name: 'WORKGROUP_NO', index: 'WORKGROUP_NO', width: 100,align:'center'  },
			{ label: '班组名称', name: 'WORKGROUP_NAME', index: 'WORKGROUP_NAME', width: 100,align:'center'  },
			{ label: '班组职责', name: 'RESPONSIBILITY', index: 'RESPONSIBILITY', width: 100,align:'center'  },
			{ label: '备注', name: 'MEMO', index: 'MEMO', width: 100,align:'center'  },
			{ label: '编辑人', name: 'EDITOR', index: 'EDITOR', width: 80,align:'center'  }, 			
			{ label: '编辑时间', name: 'EDIT_DATE', index: 'EDIT_DATE', width: 80,align:'center'  },	
			{ label: 'ID', name: 'ID', index: 'ID', hidden:true }
		],
		rowNum : 15
		
    });
  //------删除操作---------
	  $("#deleteOperation").click(function(){
		  var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
		  if (gr != null  && gr!==undefined) {
			  layer.confirm('确定删除选中的记录?', {icon: 3, title:'提示'}, function(index){
				  var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
				  $.ajax({
					type: "POST",
				    url: baseURL + "masterdata/StandardWorkgroup/deleteById?id="+grData.ID,
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
	    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>新增标准班组维护',
	    		area: ["500px", "300px"],
	    		content: "sys/masterdata/standardworkgroup_edit.html",  
	    		btn: ['<i class="fa fa-check"></i> 确定'],
	    		success:function(layero, index){
	    			var win = top[layero.find('iframe')[0]['name']];
	    			win.vm.saveFlag = false;
	    		},
	    		btn1:function(index,layero){
					var win = top[layero.find('iframe')[0]['name']];
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
				title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>修改标准班组维护',
				area: ["500px", "300px"],
				content: 'sys/masterdata/standardworkgroup_edit.html',
				btn: ['<i class="fa fa-check"></i> 确定'],
				success:function(layero, index){
					var win = top[layero.find('iframe')[0]['name']];
					win.vm.saveFlag = false;
					win.vm.getInfo(grData.ID);
				},
				btn1:function(index,layero){
					var win = top[layero.find('iframe')[0]['name']];
					var btnSubmit= $("#btnSubmit", layero.find("iframe")[0].contentWindow.document);
					$(btnSubmit).click();
					if(typeof listselectCallback == 'function'){
						console.log("win.vm.saveFlag",win.vm.saveFlag);
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
});

var vm = new Vue({
	el:'#rrapp',
    data:{
    	WORKGROUP_NO:'',
		WORKSHOP_CODE:'',
		WORKGROUP_NAME:''
    },
    created: function(){},
	watch:{},
	methods: {
		refresh:function(){
			$("#searchForm").submit();
        },
		reload: function (event) {
			$("#searchForm").submit();
		},
		
	}
});
function export2Excel(){}
function listselectCallback(index,rtn){
	rtn = rtn ||false;
	if(rtn){
		//操作成功，刷新表格
		vm.reload();
		parent.layer.close(index);
	}
}