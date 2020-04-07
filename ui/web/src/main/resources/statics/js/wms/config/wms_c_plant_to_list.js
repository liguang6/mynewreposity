//国际化取值
var smallUtil=new smallTools();
var array = new Array("FACTORYCODE", "WAREHOUSECODE", "MAKE_SURE_DEL",
		"DELETE_SUCCESS", "SELECT_DELETE", "PROMPT_MESSAGE", "CONFIRM", "ADD",
		"CLOSE", "UPDATE", "SELECT_EDIT", "CREATOR", "CREATE_DATE", "EDITOR",
		"EDIT_DATE","STATUS", "CONTROL_FLAG","CONTROL_FLAG","DESCS","CONTROL_FLAG_TYPE");
var languageObj = smallUtil.loadlanguageByPKeyAndLKey(array, "M");
var vm = new Vue({
	el:'#rrapp',
	data:{
		lgortlist:[],
		WERKS:"",
		LGORT:"",
		WMS_MOVE_TYPE:"",
		SAP_MOVE_TYPE:"",
		SOBKZ:"",
	},
	methods:{
		reload: function (event) {
			$("#searchForm").submit();
		},
	},
	watch:{
		WERKS:function(newVal,oldVal){
			this.$nextTick(function(){
				getLgortList(newVal)
			})
			
		}
	},
	created:function(){
		this.WERKS=$("#werks").val()
		//getLgortList(this.WERKS)
	}
})

$(function(){
	$("#dataGrid").dataGrid({
    	searchForm: $("#searchForm"),
    	
        columnModel: [			
			{ label: '调出工厂', name: 'WERKS_F', index: 'WERKS_F', width: 60,align:'center' },
			{ label: '调出库位', name: 'LGORT_F', index: 'LGORT_F', width: 80,align:'center'  }, 			
			{ label: '接收工厂', name: 'WERKS_T', index: 'WERKS_T', width: 90,align:'center'  }, 			
			{ label: '接收库位', name: 'LGORT_T', index: 'LGORT_T', width: 90,align:'center'  }, 			
			{ label: 'WMS移动类型', name: 'WMS_MOVE_TYPE', index: 'WMS_MOVE_TYPE', width: 80,align:'center'  }, 			
			{ label: 'SAP移动类型', name: 'SAP_MOVE_TYPE', index: 'SAP_MOVE_TYPE', width: 80,align:'center'  }, 			
			{ label: '特殊库存类型', name: 'SOBKZ', index: 'SOBKZ', width: 80,align:'center', }, 
			{ label: '创建人', name: 'UPDATER', index: 'UPDATER', width: 60,align:'center'  }, 			
			{ label: '创建时间', name: 'UPDAT_DATE', index: 'UPDAT_DATE', width: 90,align:'center'  },
			{ label: 'ID', name: 'ID', index: 'ID', hidden:true },
		],
		viewrecords: true,
        rowNum: 15,
        rownumWidth: 25, 
        showCheckbox:true,
       // autowidth:true,
        //shrinkToFit:false,  
        autoScroll: true
    });
	

	//------新增操作---------
	$("#newOperation").click(function(){
			js.layer.open({
				type: 2,
				title: "新增配置",
				area: ["780px", "320px"],
				content: baseUrl + "wms/config/wms_c_plant_to_edit.html",
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
				title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>编辑配置',
				area: ["780px", "320px"],
				content: baseURL + 'wms/config/wms_c_plant_to_edit.html',
				btn: ["确定","取消"],
				success:function(layero, index){
					var win = layero.find('iframe')[0].contentWindow;
					win.vm.saveFlag = false;
					//win.vm.getInfo(grData.ID);
					win.vm.WERKS_F=grData.WERKS_F;
					win.vm.LGORT_F=grData.LGORT_F;
					win.vm.WERKS_T=grData.WERKS_T;
					win.vm.LGORT_T=grData.LGORT_T;
					win.vm.WMS_MOVE_TYPE=grData.WMS_MOVE_TYPE;
					win.vm.SAP_MOVE_TYPE=grData.SAP_MOVE_TYPE;
					win.vm.SOBKZ=grData.SOBKZ;
					win.vm.ID=grData.ID;
				},
				yes:function(index,layero){
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
			};
			//options.btn.push('<i class="fa fa-close"></i> 关闭');
			js.layer.open(options);
		 }else{
			 layer.msg("请选择要编辑的行",{time:1000});
		 }
	});

	//------删除操作---------
	$("#deleteOperation").click(function(){
		  var gr = $("#dataGrid").jqGrid('getGridParam', 'selarrrow');//获取选中的行号
		  var ids=[];
		  
		  for(var item in gr){
				var row = $("#dataGrid").jqGrid('getRowData',gr[item]);
				ids.push(row.ID);
			}
		  
		  if (ids != "") {
			  layer.confirm('确定删除选中的记录?', {icon: 3, title:'提示'}, function(index){
				  //do something
				  var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
				  $.ajax({
					  url:baseUrl + "config/cPlantTo/dels",
					  type:"post",
					  data:{"ids":ids.join(",")},
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

	
})

function getLgortList(WERKS){
    //查询库位
  	$.ajax({
      	url:baseUrl + "common/getLoList",
      	data:{
      		"WERKS":WERKS
      		},
      	success:function(resp){
      		vm.lgortlist = resp.data;    		
      	}
      });
  	
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