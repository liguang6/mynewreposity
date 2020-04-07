var listdata = [];
$(function () {
	var now = new Date(); //当前日期
	var startDate=new Date(now.getTime()-7*24*3600*1000);
	var endDate=new Date(now.getTime()+7*24*3600*1000);
	startDate.setHours(0);
	startDate.setMinutes(0);
	startDate.setSeconds(0);
	endDate.setHours(0);
	endDate.setMinutes(0);
	endDate.setSeconds(0);
	$("#createDateStart").val(startDate.Format("yyyy-MM-dd hh:mm:ss"));
	$("#createDateEnd").val(now.Format("yyyy-MM-dd hh:mm:ss"));
	$("#ETA_DATE_START").val(startDate.Format("yyyy-MM-dd hh:mm:ss"));
	$("#ETA_DATE_END").val(endDate.Format("yyyy-MM-dd hh:mm:ss"));

	initGridTable();

});

function initGridTable(){
	$("#dataGrid").dataGrid({
		searchForm:$("#searchForm"),
		datatype: "json",
		colModel:[
					{ label: '送货单号', name: 'ASNNO', index: 'ASNNO', width: 120 },
					{ label: '状态', name: 'ST', index: 'ST', width: 90,
						formatter:function(val){	//(状态:1.创建,2.待确认,3.驳回,4.待打印,5.已发货,6.已收货,7.取消,8.已送检,9.冲销送检,A.冲销收货)
							if("1"==val)return "创建";
							if("2"==val)return "待确认";
							if("3"==val)return "驳回";
							if("4"==val)return "待打印";
							if("5"==val)return "已发货";
							if("6"==val)return "已收货";
							if("7"==val)return "取消";
							if("8"==val)return "已送检";
							if("9"==val)return "冲销送检";
							if("A"==val)return "冲销收货";
						}
					},
					{ label: '供应商', name: 'VDCD', index: 'VDCD', width: 160 },
					{ label: '供应商名称', name: 'VDCN', index: 'VDCN', width: 260 },
					{ label: '送达仓库', name: 'WHNO', index: 'WHNO', width: 160 },
					{ label: '交货计划', name: 'ETA', index: 'ETA', width: 170 },
					{ label: '操作', name: 'ACTIONS', index: 'ACTIONS', width: 80,align:'center' ,formatter: function(val, obj, row, act){
						var actions = [];
		   				actions.push('<a href="#" title="明细" onclick=showItems("'+row.ASNNO+'")><i class="glyphicon glyphicon-search"></i></a>');
		   				actions.push('<a href="#" title="条码明细" onclick=showBarInfos("'+row.ASNNO+'")><i class="ace-icon fa fa-barcode black bigger-160 btn_scan" style="cursor: pointer"></i></a>');
		   				// actions.push(<i class="ace-icon fa fa-barcode black bigger-160 btn_scan" style="cursor: pointer" onclick="doScan('matnr')"> </i>);
						return actions.join('  ');
				      },unformat:function(cellvalue, options, rowObject){
							return "";
						}
				    }

		          ],
		rowNum: 15,
	    rownumWidth: 25,
	    multiselect:true,
	    beforeSelectRow: function (rowid, e) {  //点击复选框才选中行
			   var $myGrid = $(this),
			   i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),
			   cm = $myGrid.jqGrid('getGridParam', 'colModel');
			   /*if(cm[i].name == 'cb'){
				   console.info(">>>");
				   $('#dataGrid').jqGrid('setSelection',rowid);
			   }*/

			   return (cm[i].name == 'cb');
			}
	});
}

var vm = new Vue({
	el:"#vue-app",
	data:{
		whNumber:"",
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
       			 vm.whNumber=resp.data[0].WH_NUMBER;
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
	         			 vm.whNumber=resp.data[0].WH_NUMBER;
	         		 }

	         	  }
	           });

		}
		// update:function(){
		// 	var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
		// 	$(".btn").attr("disabled", true);
		// 	if(ids.length==0){
		// 		js.alert("请选择要关闭的行!");
		// 		$(".btn").attr("disabled", false);
		// 		return;
		// 	}
		//
		// 	var rows = [];
		// 	for(var id in ids){
		// 		var row = $("#dataGrid").jqGrid('getRowData',ids[id]);
		//
		// 		rows[id] = row;
		//
		// 		if("部分收货"!=row.ST){
		// 			js.alert("只有状态为 部分收货 的才能进行关闭!");
		// 			$(".btn").attr("disabled", false);
		// 			return;
		// 		}
		// 	}
		//
		// 	layer.confirm('确定关闭选中的行?', {icon: 3, title:'提示'}, function(index){
		// 		$.ajax({
		// 			url:baseUrl + "query/scmquery/update",
		// 			dataType : "json",
		// 			type : "post",
		// 			data : {
		// 				"ARRLIST":JSON.stringify(rows)
		// 			},
		// 			async: false,
		// 			success:function(resp){
		// 				if(resp.code == '0'){
		// 					js.showMessage("关闭成功!  "+resp.msg,null,null,1000*600000);
		// 					//
		// 					$("#searchForm").submit();
		// 					$(".btn").attr("disabled", false);
		// 				}else{
		// 					js.alert(resp.msg);
		// 					$(".btn").attr("disabled", false);
		// 				}
		// 			}
		// 		});
		// 	});
		//
		//
		// 	$(".btn").attr("disabled", false);
		// }
	}
});
/**
 * 点击放大镜，显示送货单行明细信息
 * @param asnno
 */
function showItems(asnno){
	var options = {
    		type: 2,
    		maxmin: true,
    		shadeClose: true,
    		title: '<i aria-hidden="true" class="glyphicon glyphicon-search">&nbsp;</i>云平台送货单单据明细',
    		area: ["1060px", "500px"],
    		content: "wms/query/YPTDeliveryDetail.html",
    		btn: [],
    		success:function(layero, index){
    			var win = top[layero.find('iframe')[0]['name']];
    			win.vm.getDetail(asnno);
    		},
    	};
    	options.btn.push('<i class="fa fa-close"></i> 关闭');
    	options['btn'+options.btn.length] = function(index, layero){
    		js.layer.close(index);
    	};
     js.layer.open(options);
}
/**
 * 点击条码操作，显示送货单关联的条码信息
 * @param asnno
 */
function showBarInfos(asnno){
	var options = {
		type: 2,
		maxmin: true,
		shadeClose: true,
		title: '<i aria-hidden="true" class="glyphicon glyphicon-search">&nbsp;</i>条码明细',
		area: ["1060px", "500px"],
		content: "wms/query/YPTDeliverBarCodeQuery.html",
		btn: [],
		success:function(layero, index){
			var win = top[layero.find('iframe')[0]['name']];
			console.log("asnno:"+asnno);
			win.vm.getBarDetail(asnno);
		},
	};
	options.btn.push('<i class="fa fa-close"></i> 关闭');
	options['btn'+options.btn.length] = function(index, layero){
		js.layer.close(index);
	};
	js.layer.open(options);
}






