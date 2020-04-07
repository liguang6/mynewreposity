var listdata = [];
$(function () {
	var now = new Date(); //当前日期
	var startDate=new Date(now.getTime()-7*24*3600*1000);
	var endDate=new Date(now.getTime()+7*24*3600*1000);
	$("#ETA_DATE_START").val(formatDate(startDate));
	$("#ETA_DATE_END").val(formatDate(endDate));
	
	initGridTable();
	
});

function initGridTable(){
	$("#dataGrid").dataGrid({
		searchForm:$("#searchForm"),
		datatype: "json",
		colModel:[
					
					{ label: '送货单号', name: 'ASNNO', index: 'ASNNO', width: 120 }, 
					{ label: '状态', name: 'ST', index: 'ST', width: 90, 
						formatter:function(val){	// 1 已创建  2送货中  3已送货 4 取消 5删除 8部分收货 9关闭
		            		if("1"==val)return "已创建";
		            		if("2"==val)return "送货中";
		            		if("3"==val)return "已送货";
		            		if("4"==val)return "取消";
		            		if("5"==val)return "删除";
		            		if("8"==val)return "部分收货";
		            		if("9"==val)return "关闭";
		            	}	
					}, 	
					{ label: '供应商', name: 'VDCD', index: 'VDCD', width: 160 }, 
					{ label: '供应商名称', name: 'VDCN', index: 'VDCN', width: 260 }, 	
					{ label: '送达仓库', name: 'WHNO', index: 'WHNO', width: 160 },
					{ label: '交货计划', name: 'ETA', index: 'ETA', width: 170 },
					{ label: '操作', name: 'ACTIONS', index: 'ACTIONS', width: 80,align:'center' ,formatter: function(val, obj, row, act){
						var actions = [];
		   				actions.push('<a href="#" title="明细" onclick=show("'+row.ASNNO+'")><i class="glyphicon glyphicon-search"></i></a>');
						return actions.join(',');
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
	        
		},
		update:function(){
			var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
			$(".btn").attr("disabled", true);
			if(ids.length==0){
				js.alert("请选择要关闭的行!");
				$(".btn").attr("disabled", false);
				return;
			}
			
			var rows = [];
			for(var id in ids){
				var row = $("#dataGrid").jqGrid('getRowData',ids[id]);
				
				rows[id] = row;
				
				if("部分收货"!=row.ST){
					js.alert("只有状态为 部分收货 的才能进行关闭!");
					$(".btn").attr("disabled", false);
					return;
				}
			}
			
			layer.confirm('确定关闭选中的行?', {icon: 3, title:'提示'}, function(index){
				$.ajax({
					url:baseUrl + "query/scmquery/update",
					dataType : "json",
					type : "post",
					data : {
						"ARRLIST":JSON.stringify(rows)
					},
					async: false,
					success:function(resp){
						if(resp.code == '0'){
							js.showMessage("关闭成功!  "+resp.msg,null,null,1000*600000);
							//
							$("#searchForm").submit();
							$(".btn").attr("disabled", false);
						}else{
							js.alert(resp.msg);
							$(".btn").attr("disabled", false);
						}
					}
				});
			});
			
			
			$(".btn").attr("disabled", false);
		}
	}
});
/**
 * 显示单据明细
 * @param asnno
 */
function show(asnno){
	var options = {
    		type: 2,
    		maxmin: true,
    		shadeClose: true,
    		title: '<i aria-hidden="true" class="glyphicon glyphicon-search">&nbsp;</i>单据明细',
    		area: ["1060px", "500px"],
    		content: baseUrl + "wms/query/SCMDeliveryDetail.html",  
    		btn: [],
    		success:function(layero, index){
    			var win = layero.find('iframe')[0].contentWindow;
    			win.vm.getDetail(asnno);
    		},
    	};
    	options.btn.push('<i class="fa fa-close"></i> 关闭');
    	options['btn'+options.btn.length] = function(index, layero){
    		js.layer.close(index);
    	};
     js.layer.open(options);
}






