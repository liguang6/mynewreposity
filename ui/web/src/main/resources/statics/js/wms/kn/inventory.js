
var vm = new Vue({
	el:'#rrapp',
	data:{
		WERKS:"",
		whNumber:"",
		warehourse:[],
		relatedareaname:[],
	},
	created:function(){
		this.WERKS = $("#werks").find("option").first().val();
		loadLgort();
	},
	watch:{
		WERKS:{
			handler:function(newVal,oldVal){
	          //查询工厂仓库
	          $.ajax({
	        	  url:baseUrl + "common/getWhDataByWerks",
	        	  data:{"WERKS":newVal},
	        	  success:function(resp){
	        		 vm.warehourse = resp.data;
	        		 vm.whNumber = resp.data[0].WH_NUMBER;
	        	  }
	          })
	          loadLgort();
			}
		},
		whNumber:{
			handler:function(newVal,oldVal){
				$.ajax({
					type:'post',
					datatype:'json',
					url:baseUrl + "kn/inventory/getWhManagerList",
					data:{"WERKS":$("#werks").val(),"WH_NUMBER":newVal},
					success:function(resp){
						vm.relatedareaname = resp.list;
					}
				})
			}
		},
	},
	methods: {
		refresh:function(){
			$("#searchForm").submit();
        },
		reload: function (event) {
			$("#searchForm").submit();
		},
	}
});
$(function () {
	var now = new Date(); //当前日期
	var startDate=new Date(now.getTime()-6*24*3600*1000);
	$("#startDate").val(formatDate(startDate));
	$("#endDate").val(formatDate(now));
    $("#dataGrid").dataGrid({
    	searchForm: $("#searchForm"),
        columnModel: [			
			{ label: '工厂代码', name: 'WERKS', index: 'WERKS', width: 70,align:'center' },
			{ label: '仓库号', name: 'WH_NUMBER', index: 'WH_NUMBER', width: 70,align:'center'  }, 			
			{ label: '盘点任务号', name: 'INVENTORY_NO', index: 'INVENTORY_NO', width: 100,align:'center'  }, 			
			{ label: '盘点方式', name: 'INVENTORY_TYPE', index: 'INVENTORY_TYPE', width: 80,align:'center' ,formatter: function(item, index){
    		    return item=='00' ? '明盘'  : '暗盘';
		    	}
		    }, 	 			
			{ label: '仓管员', name: 'WH_MANAGER', index: 'WH_MANAGER', width: 80,align:'center'  },
			{ label: '盘点库位', name: 'LGORT', index: 'LGORT', width: 80,align:'center'  },
			{ label: '盘点比例(%)', name: 'PROPORTION', index: 'PROPORTION', width: 80,align:'center'  },
			{ label: '盘点状态', name: 'STATUS_DESC', index: 'STATUS_DESC', width: 80,align:'center'  },
			{ label: '创建时间', name: 'CREATE_DATE', index: 'CREATE_DATE', width: 80,align:'center'  },
			{ label: '操作', name: 'ACTIONS', index: 'ACTIONS', width: 80,align:'center' ,formatter: function(val, obj, row, act){
				var actions = [];
				actions.push('<a title="查看" onclick=show("'+row.INVENTORY_NO+'","'+row.WERKS+'","'+row.WH_NUMBER+'","'+row.STATUS_DESC+'")><i class="glyphicon glyphicon-search"></i></a>&nbsp;&nbsp;');
				actions.push('<a title="打印" onclick=printPreview("'+row.INVENTORY_NO+'","'+row.WERKS+'","'+row.WH_NUMBER+'","'+row.STATUS+'")><i class="glyphicon glyphicon-print"></i></a>&nbsp;&nbsp;');
				actions.push('<a title="导出" onclick=exportExcel("'+row.INVENTORY_NO+'","'+row.STATUS+'")><i class="glyphicon glyphicon-export"></i></a>&nbsp;');
			    return actions.join(' ');
		      }
		    }, 	 			
			
			{ label: 'ID', name: 'ID', index: 'id', hidden:true },
		],
		rowNum : 15,
		orderBy : "INVENTORY_NO DESC"
    });
  
	  //-----创建盘点表操作---------
	  $("#btnAdd").click(function(){
		 var options = {
    		type: 2,
    		maxmin: true,
    		shadeClose: true,
    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>盘点表创建',
    		area: ["500px", "450px"],
    		content: baseURL + "wms/kn/inventoryCreate.html",  
    		btn: ['<i class="fa fa-check"></i> 确定'],
    		success:function(layero, index){
    			var win = layero.find('iframe')[0].contentWindow;
    			//win.vm.saveFlag = false;
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
     };
     js.layer.open(options);
   });
});
function printPreview(inventoryNo,werks,whNumber,status){
	var results=[];
	$.ajax({
		url:baseURL+"kn/inventory/getItemByInventoryNo",
		dataType : "json",
		type : "post",
		data : {
			INVENTORY_NO:inventoryNo
		},
		async: false,
		success: function (response) {
			if(response.code==0){
				results=response.list;
			
				for(var i=0;i<results.length;i++){
					if(status=='01'){
						results[i].STOCK_QTY='*';
					}
				}
				$("#printWerks").val(werks);
				$("#printWhNumber").val(whNumber);
				$("#printInventoryNo").val(inventoryNo);
				$("#printStatus").val(status);
				$("#dataList").val(JSON.stringify(results));
				$("#printButton").click();
			}
		}
	});
}
// 根据盘点任务号查询明细
function show(inventoryNo,werks,whNumber,status){
	var options = {
    		type: 2,
    		maxmin: true,
    		shadeClose: true,
    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>盘点明细',
    		area: ["960px", "500px"],
    		content: baseURL + "wms/kn/inventoryDetail.html",  
    		btn: [],
    		success:function(layero, index){
    			var win = layero.find('iframe')[0].contentWindow;
    			win.vm.getDetail(inventoryNo,werks,whNumber,status);
    		},
    	};
    	options.btn.push('<i class="fa fa-close"></i> 关闭');
    	options['btn'+options.btn.length] = function(index, layero){
     };
     js.layer.open(options);
}
function exportExcel(inventoryNo,status){
	var column=[
      	{"title":"盘点任务号","class":"center","data":"INVENTORY_NO"},
      	{"title":"行项目号","class":"center","data":"INVENTORY_ITEM_NO"},
      	{"title":"工厂","class":"center","data":"WERKS"},
      	{"title":"仓库号","class":"center","data":"WH_NUMBER"},
      	{"title":"物料号","class":"center","data":"MATNR"},
      	{"title":"物料描述","class":"center","data":"MAKTX"},
      	{"title":"库位号","class":"center","data":"LGORT"},
      	{"title":"仓管员","class":"center","data":"WH_MANAGER"},
      	{"title":"供应商代码","class":"center","data":"LIFNR"},
      	{"title":"供应商名称","class":"center","data":"LIKTX"},
      	{"title":"单位","class":"center","data":"MEINS"},
      	{"title":"库存数量","class":"center","data":"STOCK_QTY"},
      //	{"title":"冻结数量","class":"center","data":"FREEZE_QTY"},
      	{"title":"初盘数量","class":"center","data":"INVENTORY_QTY"},
      	{"title":"复盘数量","class":"center","data":"INVENTORY_QTY_REPEAT"},
      	{"title":"差异原因","class":"center","data":"DIFFERENCE_REASON"},
      	{"title":"初盘人","class":"center","data":"INVENTORY_PEOPLE"},
      	{"title":"初盘时间","class":"center","data":"INVENTORY_DATE"},
      	{"title":"复盘人","class":"center","data":"INVENTORY_PEOPLE_REPEAT"},
      	{"title":"复盘时间","class":"center","data":"INVENTORY_DATE_REPEAT"},
      	{"title":"差异确认人","class":"center","data":"CONFIRMOR"},
      	{"title":"差异确认时间","class":"center","data":"CONFIRM_DATE"},
      ]	;
	console.log("inventoryNo",inventoryNo);
	var results=[];
	$.ajax({
		url:baseURL+"kn/inventory/getItemByInventoryNo",
		dataType : "json",
		type : "post",
		data : {
			INVENTORY_NO:inventoryNo
		},
		async: false,
		success: function (response) {
			if(response.code==0){
				results=response.list;
			
				for(var i=0;i<results.length;i++){
					if(status=='01'){
						results[i].STOCK_QTY='*';
					}
					//防止'001'Excel导出后变成'1',在后端加上空格
					results[i].LGORT=results[i].LGORT+'&nbsp;';
				}
				
			}
		}
	})
	var rowGroups=[];
	getMergeTable(results,column,rowGroups,"盘点表");	
}

function loadLgort(){
	//查询库位
  	$.ajax({
		  type:'post',
		  datatype:'json',
      	url:baseUrl + "kn/inventory/lgortlist",
      	data:{
      		"DEL":'0',
      		"WERKS":$("#werks").val(),
      		},
      	success:function(resp){
      		var lgortlist = resp.list;
      		var str="<option value=''>全部</option>";
      		$.each(lgortlist,function(index,val){
      			str+="<option value='"+val.lgort+"'>"+val.lgort+"</option>";
      		});
      		$("#lgort").html(str);
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