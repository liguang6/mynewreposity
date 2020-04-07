var mydata = [];
var lastrow,lastcell,lastcellname;  
var vm = new Vue({
	el : '#rrapp',
	data : {
		formUrl:baseUrl+"account/wmsAccountPostJob/listPostJob",
		WERKS:"",
		whList:[],
		WH_NUMBER:'',
		JOB_FLAG: '03',
		REF_WMS_NO: '',
		SAP_MOVE_TYPE:'',
		MATNR:'',
		REF_DOC_NO:'',
		WMS_CREATOR:'',
	},
	watch:{
		WERKS:{
			handler:function(newVal,oldVal){
					 vm.whList = [];
					 vm.lgortList = [];
			         this.$nextTick(function(){
			        	 
						 $.ajax({
							    url:baseURL+"common/getWhDataByWerks",
								dataType : "json",
								type : "post",
								data : {
									"WERKS":newVal
								},
								async: true,
					        	  success:function(resp){
					        		 if(resp && resp.data.length>0){
						        		 vm.whList = resp.data;
						        		 vm.WH_NUMBER = resp.data[0].WH_NUMBER;
					        		 }
					        	  }
				          })
			        	  
					})
				}
		},
		JOB_FLAG:{
			handler:function(newVal,oldVal){
				if(newVal =="03" || newVal =="05"){
					$("#pageSize").val("100000");
				}else{
					$("#pageSize").val("15");
				}
			}
		}
		
	},
	methods : {
		refresh : function() {
			$('#dataGrid').trigger('reloadGrid');
		},
		query: function () {
			vm.$nextTick(function(){//数据渲染后调用
				$("#searchForm").submit();
			})
	
		},
		post: function(){
			var selectedRows = [];
			var flag=true;
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			var rows = getSelectedRows();
			if(rows<=0){
				//js.showMessage("请选择需要保存的行项目数据！");	
				alert("请选择一条或多条记录！");
				flag = false;
				return false;
			}
			//过账数量必须大于0，否则不能选中
			$.each(rows,function(i,rowid){
				var row = $("#dataGrid").jqGrid("getRowData",rowid);
				if(row.ENTRY_QNT=="" || Number(row.ENTRY_QNT) <=0){
					js.showErrorMessage("第"+rowid+"行过账数量必须大于0！");	
					flag=false;
					return false;
				}
				selectedRows.push(row);
			});
			//var rowData = $("#dataGrid").jqGrid('getRowData'); 表格所有行项目数量
			if(flag && selectedRows.length>0){
				js.loading();
				$("#btnConfirm").val("过账中...");	
				$("#btnConfirm").attr("disabled","disabled");
				$("#btnSearchData").attr("disabled","disabled");	
				
				/**
				 * 异步过账任务处理
				 */
				$.ajax({
					url:baseUrl+"account/wmsAccountPostJob/post",
					type:"post",
					async:true,
					dataType:"json",
					data:{
						matList:JSON.stringify(selectedRows),
						WERKS:vm.WERKS,
						WH_NUMBER:vm.WH_NUMBER,
						PZ_DATE:$("#PZ_DATE").val(),
						JZ_DATE:$("#JZ_DATE").val(),
					},
					success:function(response){
						if(response.code==500){
							$("#btnConfirm").val("过账");	
							$("#btnConfirm").removeAttr("disabled");
							$("#btnSearchData").removeAttr("disabled");
							js.closeLoading();
							js.showErrorMessage(response.msg);
							return false;
						}
						
						if(response.code=='0'){
							js.showMessage(response.msg);
	/*		            	$("#rtnMst").html(response.msg);
			            	layer.open({
			            		type : 1,
			            		offset : '50px',
			            		skin : 'layui-layer-molv',
			            		title : "无PO收货账务处理成功！",
			            		area : [ '500px', '200px' ],
			            		shade : 0,
			            		shadeClose : false,
			            		content : jQuery("#resultLayer"),
			            		btn : [ '确定'],
			            		btn1 : function(index) {
			            			layer.close(index);
			    					$("#btnCreat").removeAttr("disabled");
			            		}
			            	});*/
						}
						
						$('#dataGrid').jqGrid("clearGridData");
						$("#btnConfirm").val("过账");	
						$("#btnConfirm").removeAttr("disabled");
						$("#btnSearchData").removeAttr("disabled");
						js.closeLoading();
						
						fun_tab();
					}
				});
				js.closeLoading();
				$("#btnConfirm").html("过账");	
				$("#btnConfirm").removeAttr("disabled");
				$("#btnSearchData").removeAttr("disabled");
			}
		},
		cancel: function(){
			var selectedRows = [];
			var flag=true;
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			var rows = getSelectedRows();
			if(rows<=0){
				//js.showMessage("请选择需要保存的行项目数据！");	
				alert("请选择一条或多条记录！");
				flag = false;
				return false;
			}
			//过账数量必须大于0，否则不能选中
			$.each(rows,function(i,rowid){
				var row = $("#dataGrid").jqGrid("getRowData",rowid);
				selectedRows.push(row);
			});
			
			if(flag && selectedRows.length>0){
				js.loading();
				$("#btnCancel").val("执行中...");	
				$("#btnCancel").attr("disabled","disabled");
				$("#btnSearchData").attr("disabled","disabled");	
				
				/**
				 * 异步过账任务处理
				 */
				$.ajax({
					url:baseUrl+"account/wmsAccountPostJob/closePostJob",
					type:"post",
					async:true,
					dataType:"json",
					data:{
						matList:JSON.stringify(selectedRows),
						WERKS:vm.WERKS,
						WH_NUMBER:vm.WH_NUMBER,
					},
					success:function(response){
						if(response.code==500){
							$("#btnCancel").val("屏蔽");	
							$("#btnCancel").removeAttr("disabled");
							$("#btnSearchData").removeAttr("disabled");
							js.closeLoading();
							js.showErrorMessage(response.msg);
							return false;
						}
						
						if(response.code=='0'){
							js.showMessage(response.msg);
						}
						
						$('#dataGrid').jqGrid("clearGridData");
						$("#btnCancel").val("屏蔽");	
						$("#btnCancel").removeAttr("disabled");
						$("#btnSearchData").removeAttr("disabled");
						js.closeLoading();
						
						fun_tab();
					}
				});
				js.closeLoading();
				$("#btnCancel").html("屏蔽");	
				$("#btnCancel").removeAttr("disabled");
				$("#btnSearchData").removeAttr("disabled");
			}			
		}
	},
	created:function(){
		this.WERKS=$("#WERKS").find("option").first().val();
	}
});

$(function(){
	var now = new Date(); //当前日期
	var curDate = formatDate(now);
	var startDate=new Date(now.getTime()-6*24*3600*1000);
	$("#CREATE_DATE_S").val(formatDate(startDate));//收货日期-开始
	$("#CREATE_DATE_E").val(curDate);
	$("#PZ_DATE").val(curDate);
	$("#JZ_DATE").val(curDate);
	fun_tab();
	
});

function fun_tab(){
	$("#dataGrid").dataGrid({
		searchForm: $("#searchForm"),
		columnModel: [
			{label: '工厂', name: 'WERKS',index:"WERKS", width: "65",align:"center",sortable:false,frozen: true,hidden:true},
			{label: '仓库号', name: 'WH_NUMBER',index:"WH_NUMBER", width: "75",align:"center",sortable:false,frozen: true,hidden:true},
            {label: '参考WMS凭证号', name: 'REF_WMS_NO',index:"REF_WMS_NO", width: "130",align:"center",sortable:false,frozen: true,},
            {label: '行项目号', name: 'REF_WMS_ITEM_NO',index:"REF_WMS_ITEM_NO", width: "80",align:"center",sortable:false,frozen: true,},
            {label: 'WMS移动类型', name: 'WMS_MOVE_TYPE',index:"WMS_MOVE_TYPE", width: "100",align:"center",sortable:false,frozen: true,},
            {label: 'SAP移动类型', name: 'MOVE_TYPE',index:"MOVE_TYPE", width: "100",align:"center",sortable:false,frozen: true,},
            {label: '料号', name: 'MATNR',index:"MATNR", width: "100",align:"center",sortable:false,frozen: true,},
            {label: '批次', name: 'BATCH',index:"BATCH", width: "100",align:"center",sortable:false,frozen: true,},
            {label: '待过账数量', name: 'ALL_ENTRY_QNT',index:"ALL_ENTRY_QNT", width: "85",align:"center",sortable:false,frozen: false,},
            {label: '已过账数量', name: 'POST_QTY',index:"POST_QTY", width: "85",align:"center",sortable:false,frozen: false,},
   			{ display:'过账数量',label: '<span style="color:red">*</span><span style="color:blue">过账数量</span>', name: 'ENTRY_QNT', align:"center",index: 'ENTRY_QNT', width: 70,editable:true,editrules:{
   				required:true,number:true},  formatter:'number',formatoptions:{
   				decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}}	,	
            {label: '单位', name: 'ENTRY_UOM',index:"ENTRY_UOM", width: "45",align:"center",sortable:false,frozen: false,},
            {label: 'WMS过账日期', name: 'WMS_JZ_DATE',index:"WMS_JZ_DATE", width: "100",align:"center",sortable:true,frozen: false},
            {label: 'WMS操作人', name: 'WMS_CREATOR',index:"WMS_CREATOR", width: "90",align:"center",sortable:false},
            {label: 'SAP凭证日期', name: 'DOC_DATE',index:"DOC_DATE", width: "90",align:"center",sortable:true,frozen: false},
            {label: 'SAP过账日期', name: 'PSTNG_DATE',index:"PSTNG_DATE", width: "90",align:"center",sortable:true,frozen: false},
            {label: 'SAP过账失败原因', name: 'ERROR',index:"ERROR", width: "250",align:"center",sortable:false},
            {label: '行文本', name: 'ITEM_TEXT',index:"ITEM_TEXT", width: "250",align:"center",sortable:false,frozen: true},
            {label: '抬头文本', name: 'HEADER_TXT',index:"HEADER_TXT", width: "250",align:"center",sortable:false},            
            {label: '采购订单号', name: 'PO_NUMBER',index:"PO_NUMBER", width: "90",align:"center",sortable:false},
            {label: '行项目号', name: 'PO_ITEM',index:"PO_ITEM", width: "80",align:"center",sortable:false},
            {label: '供应商', name: 'VENDOR',index:"VENDOR", width: "90",align:"center",sortable:false},
            {label: '生产/内部订单号', name: 'ORDERID',index:"ORDERID", width: "120",align:"center",sortable:false},
            {label: '行项目号', name: 'ORDER_ITNO',index:"ORDER_ITNO", width: "80",align:"center",sortable:false},
            {label: '预留号', name: 'RESERV_NO',index:"RESERV_NO", width: "120",align:"center",sortable:false},
            {label: '行项目号', name: 'RES_ITEM',index:"RES_ITEM", width: "90",align:"center",sortable:false},
            {label: '交货单号', name: 'VBELN_VL',index:"VBELN_VL", width: "90",align:"center",sortable:false},
            {label: '行项目号', name: 'POSNR_VL',index:"POSNR_VL", width: "90",align:"center",sortable:false},
            {label: '成本中心', name: 'COSTCENTER',index:"COSTCENTER", width: "90",align:"center",sortable:false},
            {label: 'WBS元素', name: 'WBS_ELEM',index:"WBS_ELEM", width: "150",align:"center",sortable:false},
            {label:'SAP_JOB_NO',name:'SAP_JOB_NO',hidden:true},
            {label:'SAP_JOB_ITEM_NO',name:'SAP_JOB_ITEM_NO',hidden:true},
            {label:'PARENT_JOB_ITEM_NO',name:'PARENT_JOB_ITEM_NO',hidden:true},
            {label:'REF_DOC_NO',name:'REF_DOC_NO',hidden:true},
            {label:'PSTNG_DATE',name:'PSTNG_DATE',hidden:true},
            {label:'DOC_DATE',name:'DOC_DATE',hidden:true},
            {label:'LGORT_CONFIG',name:'LGORT_CONFIG',hidden:true},
            {label:'STGE_LOC',name:'STGE_LOC',hidden:true},
            {label:'LGORT',name:'LGORT',hidden:true},
            {label:'GM_CODE',name:'GM_CODE',hidden:true},
            {label:'JOB_SEQ',name:'JOB_SEQ',hidden:true},
            {label:'MOVE_MAT',name:'MOVE_MAT',hidden:true},
            {label:'MOVE_PLANT',name:'MOVE_PLANT',hidden:true},
            {label:'MOVE_STLOC',name:'MOVE_STLOC',hidden:true},
            {label:'MOVE_BATCH',name:'MOVE_BATCH',hidden:true},
            {label:'JOB_FLAG',name:'JOB_FLAG',hidden:true},
            {label:'ID',name:'ID',hidden:true},
        ],
		viewrecords: true,
	    shrinkToFit:false,
        width:1300,
        rowNum: 15, 
        showRownum:true,
	    autowidth:true,
        cellEdit:true,
        cellurl:'#',
		cellsubmit:'clientArray',
        showCheckbox:true,
        rowList : [15,30,50],
        multiselect: true,
/*		loadComplete:function(data){
		   if(data.code==500){
				js.showErrorMessage(data.msg,1000*10);
			    //js.alert(data.msg);
				js.closeLoading();
				$(".btn").attr("disabled", false);
				return false;
			}
		   if(data.rows==""){
			   js.closeLoading();
				$(".btn").attr("disabled", false);
			   return false;
		   }
			var rows=$("#dataGrid").jqGrid('getRowData');
			$.each(rows,function(i,row){	
				console.info(row.MOVE_TYPE.indexOf("411") == -1);
				  if(row.MOVE_TYPE.indexOf("411") == -1){
					  $("#dataGrid").jqGrid('setCell', row.ID, 'ENTRY_QNT', '', 'not-editable-cell');
					  $("#dataGrid").jqGrid('setCell', row.ID, 'ENTRY_QNT', '', {background : '#A9A9A9'});
				  }
				  
			});
			
			js.closeLoading();
			$(".btn").attr("disabled", false);
		},*/
		formatCell:function(rowid, cellname, value, iRow, iCol){
		},
	    beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
		},
		beforeSelectRow : function(rowid, e) { // 点击复选框才选中行
			var row = $("#dataGrid").jqGrid("getRowData",rowid);
			var ids=$("#dataGrid").getGridParam("selarrrow");
			
			if(ids.indexOf(rowid) <0 ){
				//未选中->选中
				var REF_WMS_NO = row.REF_WMS_NO;
				if(row.WMS_MOVE_TYPE ==='103T' || row.WMS_MOVE_TYPE ==='XS101T' 
					|| row.WMS_MOVE_TYPE ==='124T' || row.WMS_MOVE_TYPE ==='161T'){
					//把同类型的都勾选上
					var rows=$("#dataGrid").jqGrid('getRowData');
					for(var i=0;i<rows.length;i++){
						var x = i +1;
						if(rows[i].REF_WMS_NO == REF_WMS_NO && ids.indexOf(x) <0){
							$('#dataGrid').jqGrid('setSelection', x, false);
						}
					}
				}else{
					$('#dataGrid').jqGrid('setSelection', rowid);
				}
			}else{
				$('#dataGrid').jqGrid('setSelection', rowid);
			}
			
			var $myGrid = $(this), i = $.jgrid.getCellIndex($(e.target).closest('td')[0]), cm = $myGrid.jqGrid('getGridParam', 'colModel');
/*			if (cm[i].name == 'cb') {
				if(ids.indexOf(rowid) <0){
					$('#dataGrid').jqGrid('setSelection', rowid);
				}
			}*/
			return (cm[i].name == 'cb');
		},
		onSelectRow:function(rowid,status,e){
			console.log("-->rowid : " + rowid);
		},
		afterSaveCell:function(rowid, cellname, value, iRow, iCol){

		},

    });
	
	$("#dataGrid").jqGrid("setFrozenColumns");
}