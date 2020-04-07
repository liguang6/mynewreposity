var listdata = [];
var lastrow,lastcell,lastcellname;
$(function(){
	var now = new Date(); //当前日期
	var startDate=new Date(now.getTime()-30*24*3600*1000);
	$("#startDate").val(formatDate(startDate));
	$("#endDate").val(formatDate(now));
	$("#dataGrid").dataGrid({
    	datatype: "local",
		data: listdata,
        colModel: [			
			{ label: '时间戳', name: 'TIME_STAMP_STR', index: 'TIME_STAMP_STR', sortable:false, width: 138, align: 'center',
				cellattr: function(rowId, tv, rawObject, cm, rdata) {
	                    //合并单元格
	                    return 'id=\'TIME_STAMP_STR' + rowId + "\'";
	                }
			},
			{ label: '调出工厂', name: 'WERKS_FROM', index: 'WERKS_FROM', width: 80, align: 'center', sortable:false }, 
			{ label: '调入工厂', name: 'WERKS', index: 'WERKS', width: 80, align: 'center', sortable:false }, 
			{ label: '调入库位', name: 'LGORT', index: 'LGORT', width: 80, align: 'center', sortable:false }, 
			{ label: '料号', name: 'MATNR', index: 'MATNR', width: 90, sortable:false,align: 'center' }, 
			{ label: '物料描述', name: 'MAKTX', index: 'MAKTX', width: 220, sortable:false,align: 'center'}, 
			{ label: '数量', name: 'QTY', index: 'QTY', width: 80, sortable:false, align: 'center'},
			{ label: '单位', name: 'UNIT', index: 'UNIT', width: 60, align:'center', sortable:false },
			{ label: '批次', name: 'BATCH', index: 'BATCH', width: 95, align:'center', sortable:false },
			{ label: '满箱数量', name: 'FULL_BOX_QTY', align:"center",index: 'FULL_BOX_QTY', width: 80 },
			{ label: '创建订单', name: 'STEP1_STATUS', align:"center",sortable:false,index: 'STEP1_STATUS', width: 70},
			{ label: '订单号', name: 'EBELN', align:"center",sortable:false,index: 'EBELN', width: 90},
			{ label: '消息', name: 'MSG1', align:"center",sortable:false,index: 'MSG1', width: 150},
			{ label: '创建交货单', name: 'STEP2_STATUS', align:"center",sortable:false,index: 'STEP2_STATUS', width: 80},
			{ label: '交货单号', name: 'VBELN', align:"center",sortable:false,index: 'VBELN', width: 90},
			{ label: '消息', name: 'MSG2', align:"center",sortable:false,index: 'MSG2', width: 150},
			{ label: 'STO收货', name: 'STEP3_STATUS', align:"center",sortable:false,index: 'STEP3_STATUS', width: 70},
			{ label: '收货单号', name: 'RECEIPT_NO', align:"center",sortable:false,index: 'RECEIPT_NO', width: 110},
			{ label: '送检单号', name: 'INSPECTION_NO', align:"center",sortable:false,index: 'INSPECTION_NO', width: 110},
			{ label: 'WMS凭证号', name: 'WMS_NO', align:"center",sortable:false,index: 'WMS_NO', width: 128},
			{ label: 'SAP凭证号', name: 'MAT_DOC', align:"center",sortable:false,index: 'MAT_DOC', width: 90},
			{ label: '消息', name: 'MSG3', align:"center",sortable:false,index: 'MSG3', width: 100},
			{ label: '发货方凭证', name: 'DOC_NO', align:"center",sortable:false,index: 'DOC_NO', width: 130},
			{ label: '凭证行', name: 'DOC_ITEM_NO', align:"center",sortable:false,index: 'DOC_ITEM_NO', width: 60},
			{ label: '', name: 'WH_NUMBER', index: 'WH_NUMBER', width: 160,hidden:true }
        ],
		viewrecords: true,
		showRownum:false,
        cellEdit:true,
        cellurl:'#',
        cellsubmit:'clientArray',
        autowidth:true,
        shrinkToFit:false,  
        autoScroll: true, 
        multiselect: true,
        rowNum: 15,  
        
        gridComplete:function(){
        	var gridName = "dataGrid";
        	Merger(gridName, 'TIME_STAMP_STR');
	        
        },
        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
		},
		beforeSelectRow: function (rowid, e) {  //点击复选框才选中行
			   var $myGrid = $(this),  
			   i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),  
			   cm = $myGrid.jqGrid('getGridParam', 'colModel'); 
			   if(cm[i].name == 'cb'){
				   $('#dataGrid').jqGrid('setSelection',rowid);
			   }			   		
			   return (cm[i].name == 'cb'); 

		},
		onSelectAll: function(aRowids,status) {
		      if (status) {
		         var rowIds = $("#dataGrid").jqGrid('getDataIDs');//获取jqgrid中所有数据行的id
			     for(var k=0; k<rowIds.length; k++) {
			       var curRowData = $("#dataGrid").jqGrid('getRowData', rowIds[k]);//获取指定id所在行的所有数据.
//			       if(curRowData.STEP3_STATUS === "S") {
			       if(!isEmpty(curRowData.MAT_DOC)) {
			          $("#dataGrid").jqGrid("setSelection", rowIds[k],false);
			       }   
			     }
		     }
		},
		onSelectRow:function(rowid,e){
		    var rec =  $("#dataGrid").jqGrid('getRowData', rowid);
//		    if(rec.STEP3_STATUS === "S") {
		    if(!isEmpty(rec.MAT_DOC)) {
	 			$("#dataGrid").jqGrid("setSelection", rowid,false);
	 			layer.msg("该数据已成功过账！",{time:1500});
		   	}
		}
    });

});
			
var vm = new Vue({
	el:"#vue",
	data:{
		warehourse:[],
		WH_NUMBER:""
	},
	watch:{
		WH_NUMBER:{

			handler:function(newVal,oldVal){
//				$.ajax({
//		        	  url:baseUrl + "in/wmsinbound/relatedAreaNamelist",
//		        	  data:{"WERKS":$("#WERKS").val(),"WH_NUMBER":newVal},
//		        	  success:function(resp){
//		        		 vm.relatedareaname = resp.result;
//		        	  }
//		          })
			}
		
		}
	},
	created:function(){
		//初始化查询仓库
		var plantCode = $("#WERKS").val();
		
        //初始化仓库
        $.ajax({
      	  url:baseUrl + "common/getWhDataByWerks",
      	  data:{
      		  "WERKS":plantCode,
      		  "MENU_KEY":"AUTO_PUTAWAY_PROCESS"
      	  },
      	  success:function(resp){
      		 vm.warehourse = resp.data;
      		if(resp.data.length>0){
    			 vm.WH_NUMBER=resp.data[0].WH_NUMBER//方便 v-mode取值
    		 }
      		
      	  }
        });
	},
	methods:{
		query:function(){
			if($("#startDate").val() ===''){
				js.alert("开始时间不能为空！");
				return false;
			}
			
			if($("#endDate").val() ===''){
				js.alert("结束时间不能为空！");
				return false;
			}
			js.loading();
			vm.$nextTick(function(){//数据渲染后调用
				$("#dataGrid").jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid'); 
				js.closeLoading();
			})
		},
		save:function(){
			
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
			if(ids.length==0){
				js.alert("请选择要交接的数据!");
				return;
			}
			
			var rows = [];
			for(var i=0;i<ids.length;i++){
				var rowData = $("#dataGrid").jqGrid('getRowData',ids[i]);
	  			rows.push(rowData);	
	  		}

			$(".btn").attr("disabled", true);
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			
			$("#btnsaveinteralbound").html("收货中...");	
			$("#btnsaveinteralbound").attr("disabled","disabled");
			
			js.loading();
			
			$.ajax({
				url:baseUrl + "in/autoPutaway/autoPutawayProcess",
				dataType : "json",
				type : "post",
				data : {
					"ITEMLIST":JSON.stringify(rows)
				},
				async: true,
				success:function(resp){
					if(resp.code == '0'){
						$("#dataGrid").jqGrid('clearGridData');
						$("#btnsaveinteralbound").html("执行");
						js.showMessage("执行成功! 请稍后查询日志记录 ",null,null,1000*600000);
						js.closeLoading();
					}else{
						$("#dataGrid").jqGrid('clearGridData');
						$("#btnsaveinteralbound").html("执行");
						js.showErrorMessage(resp.msg,null,null,1000*600000);
						js.closeLoading();
					}
				}
			});
			
			
			$(".btn").attr("disabled", false);
			
		},
		onStatusChange:function(event,id){},
		onPlantChange:function(event){

			  //工厂变化的时候，更新仓库号下拉框
	          var plantCode = event.target.value;
	          if(plantCode === null || plantCode === '' || plantCode === undefined){
	        	  return;
	          }
	          
	          //查询工厂仓库
	          $.ajax({
	        	  url:baseUrl + "common/getWhDataByWerks",
	        	  data:{"WERKS":plantCode,"MENU_KEY":"AUTO_PUTAWAY_PROCESS"},
	        	  success:function(resp){
	        		 vm.warehourse = resp.data;
	        		 if(resp.data.length>0){
	         			 vm.WH_NUMBER=resp.data[0].WH_NUMBER//方便 v-mode取值
	         		 }
	        		 
	        	  }
	          })
		}
	}
});

//公共调用方法
function Merger(gridName, CellName,referCellName) {
    //得到显示到界面的id集合
    var mya = $("#" + gridName + "").getDataIDs();
    //当前显示多少条
    var length = mya.length;
    for (var i = 0; i < length; i++) {
        //从上到下获取一条信息
        var before = $("#" + gridName + "").jqGrid('getRowData', mya[i]);
        //定义合并行数
        var rowSpanTaxCount = 1;
        for (j = i + 1; j <= length; j++) {
            //和上边的信息对比 如果值一样就合并行数+1 然后设置rowspan 让当前单元格隐藏
            var end = $("#" + gridName + "").jqGrid('getRowData', mya[j]);
            if(referCellName=='' || referCellName==undefined){
	            if (before[CellName] == end[CellName]) {
	                rowSpanTaxCount++;
	                $("#" + gridName + "").setCell(mya[j], CellName, '', { display: 'none' });
	            } else {
	                rowSpanTaxCount = 1;
	                break;
	            }
            }else{
            	if (before[referCellName] == end[referCellName]) {
	                rowSpanTaxCount++;
	                $("#" + gridName + "").setCell(mya[j], CellName, '', { display: 'none' });
	            } else {
	                rowSpanTaxCount = 1;
	                break;
	            }
            }
            $("#" + CellName + "" + mya[i] + "").attr("rowspan", rowSpanTaxCount);
        }
    }
}