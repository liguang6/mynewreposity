var listdata = [];
$(function () {
	var now = new Date(); //当前日期
	var startDate=new Date(now.getTime()-7*24*3600*1000);
	startDate.setHours(0);
	startDate.setMinutes(0);
	startDate.setSeconds(0);
	var endDate=new Date(now.getTime());
	$("#ETA_DATE_START").val(startDate.Format("yyyy-MM-dd hh:mm:ss"));
	$("#ETA_DATE_END").val(now.Format("yyyy-MM-dd hh:mm:ss"));
	
	initGridTable();
	
});

function initGridTable(){
	$("#dataGrid").dataGrid({
		searchForm:$("#searchForm"),
		datatype: "json",
		colModel:[
					{ label: '凭证号', name: 'WMS_NO', index: 'WMS_NO', width: 150 }, 
					{ label: '行项目', name: 'WMS_ITEM_NO', index: 'WMS_ITEM_NO', width: 60, align:'center' }, 
					{ label: '物料号', name: 'MATNR', index: 'MATNR', width: 90 }, 
					{ label: '物料描述', name: 'MAKTX', index: 'MAKTX', width: 260 },
					{ label: '数量', name: 'QTY_WMS', index: 'QTY_WMS', width: 70, align:'center' }, 
					{ label: '单位', name: 'UNIT', index: 'UNIT', width: 55, align:'center'},
					{ label: '工厂', name: 'WERKS', index: 'WERKS', width: 60, align:'center' },
					{ label: '库位', name: 'LGORT', index: 'LGORT', width: 60, align:'center' },
					{ label: '供应商', name: 'LIFNR', index: 'LIFNR', width: 70, align:'center' }, 	
					{ label: '特殊库存', name: 'SOBKZ', index: 'SOBKZ', width: 65, align:'center' },
					{ label: '箱数', name: 'BOX_COUNT', index: 'BOX_COUNT', width: 55, align:'center' },
					{ label: '订单号', name: 'PO_NO', index: 'PO_NO', width: 90, align:'center' },
					{ label: '行项目', name: 'PO_ITEM_NO', index: 'PO_ITEM_NO', width: 60, align:'center' }, 
					{ label: '批次', name: 'BATCH', index: 'BATCH', width: 85, align:'center' },
					{ label: '成本中心', name: 'COST_CENTER', index: 'COST_CENTER', width: 65, align:'center' },
					{ label: '客户', name: 'CUSTOMER', index: 'CUSTOMER', width: 60, align:'center' },
					{ label: '总帐科目', name: 'SAKTO', index: 'SAKTO', width: 65, align:'center' },
					{ label: '凭证日期', name: 'PZ_DATE', index: 'PZ_DATE', width: 80, align:'center' },
					{ label: '凭证年份', name: 'PZ_YEAR', index: 'PZ_YEAR', width: 70, align:'center' },
					{ label: '操作终端', name: 'HANDLE_FTU', index: 'HANDLE_FTU', width: 70, align:'center' },
					{ label: '操作人', name: 'CREATOR', index: 'CREATOR', width: 120, align:'center' },
					{ label: '操作时间', name: 'CREATE_DATE', index: 'CREATE_DATE', width: 135, align:'center' },
					{ label: '移动原因', name: 'ITEM_TEXT', index: 'ITEM_TEXT', width: 160, align:'center' },
					{ label: '条码明细', name: 'ACTIONS', index: 'ACTIONS', width: 80,align:'center' ,formatter: function(val, obj, row, act){
						var actions = [];
		   				actions.push('<a href="#" title="明细" onclick=show("'+row.RECEIPT_NO+'","'+row.RECEIPT_ITEM_NO+'")><i class="glyphicon glyphicon-search"></i></a>');
						return actions.join(',');
				      },unformat:function(cellvalue, options, rowObject){
							return "";
						}
				    },
				    { name: 'RECEIPT_NO', index: 'RECEIPT_NO', hidden:true},
				    { name: 'RECEIPT_ITEM_NO', index: 'RECEIPT_ITEM_NO', hidden:true}
		        
		          ],
		rowNum: 15,
	    rownumWidth: 25,
	    multiselect:true,
	    shrinkToFit:false,
	    autoScroll: true, 
	    beforeSelectRow: function (rowid, e) {  //点击复选框才选中行
			   var $myGrid = $(this),  
			   i = $.jgrid.getCellIndex($(e.target).closest('td')[0]), 
			   cm = $myGrid.jqGrid('getGridParam', 'colModel'); 
			   
			   return (cm[i].name == 'cb'); 
		}
	});
}

var vm = new Vue({
	el:"#vue-app",
	data:{
		whNumber:"",//
		warehourse:[]
	},
	created:function(){
		//初始化查询仓库
		var plantCode = $("#WERKS").val();
		
        //初始化仓库
		$.ajax({
       	  url:baseUrl + "common/getWhDataByWerks",
       	  data:{"WERKS":plantCode/*,"MENU_KEY":"IN_RECEIPT"*/},
       	  success:function(resp){
       		 vm.warehourse = resp.data;
       		 if(resp.data.length>0){
       			 vm.whNumber=resp.data[0].WH_NUMBER
       		 }
       		 
       	  }
         });
        
	},
	methods: {
		onPlantChange:function(event){
			  //工厂变化的时候，更新仓库号下拉框
	          var plantCode = event.target.value;
	          if(plantCode === null || plantCode === '' || plantCode === undefined){
	        	  return;
	          }
	          //查询工厂仓库
	  		  $.ajax({
	         	  url:baseUrl + "common/getWhDataByWerks",
	         	  data:{"WERKS":plantCode/*,"MENU_KEY":"IN_RECEIPT"*/},
	         	  success:function(resp){
	         		 vm.warehourse = resp.data;
	         		 if(resp.data.length>0){
	         			 vm.whNumber=resp.data[0].WH_NUMBER
	         		 }
	         		 
	         	  }
	           });
	        
		}
	}
});
/**
 * 显示单据明细
 * @param asnno
 */
function show(receiptno,itemno){
	var options = {
    		type: 2,
    		maxmin: true,
    		shadeClose: true,
    		title: '<i aria-hidden="true" class="glyphicon glyphicon-search">&nbsp;</i>单据明细',
    		area: ["55%", "70%"],
    		content: baseUrl + "wms/query/receiveLogDetail.html",  
    		btn: [],
    		success:function(layero, index){
    			var win = layero.find('iframe')[0].contentWindow;
    			win.vm.getDetail(receiptno,itemno);
    		},
    	};
    	options.btn.push('<i class="fa fa-close"></i> 关闭');
    	options['btn'+options.btn.length] = function(index, layero){
    		js.layer.close(index);
    	};
     js.layer.open(options);
}