var grid = $("#dataGrid");
var mydata=[];
var lastrow,lastcell;
var vm = new Vue({
	el:"#vue-app",
	data:{
		warehourse:[],
		whNumber:"",
		relatedareaname:[],//仓管员
		lgortList:''
	},
	watch:{
		whNumber:{

			handler:function(newVal,oldVal){

				$.ajax({
					url:baseUrl + "in/wmsinbound/relatedAreaNamelist",
					data:{"WERKS":$("#werks").val(),"WH_NUMBER":newVal},
					success:function(resp){
						vm.relatedareaname = resp.result;
					}
				})

			}

		}
	},
	created:function(){
// 		this.onPlantChange();
		//初始化查询仓库
		var plantCode = $("#werks").val();

		//初始化仓库
		$.ajax({
			url:baseUrl + "common/getWhDataByWerks",
			data:{"WERKS":plantCode},
			success:function(resp){
				vm.warehourse = resp.data;
				if(resp.data.length>0){
					vm.whNumber=resp.data[0].WH_NUMBER//方便 v-mode取值
				}

			}
		});
	},
	methods:{
		init:function(){
			//console.log(" ----> init");
// 			initGridTable();
		},
		// 查询
		query:function(){
			var requirementNo=$("#requirementNo").val();
			//var status = $("#WT_STATUS").val();
			//	alert(status);
			console.log("requirementNo",requirementNo);
			if(requirementNo==''){
				layer.msg("请输入需求号！",{time:1000});
				return false;
			}
			js.loading();
			$.ajax({
				url:baseUrl + "out/resersalPicking/list",
				data:{
					//"WERKS":$('#werks').val(),
					"WH_NUMBER":$("#whNumber").val(),
					"REFERENCE_DELIVERY_NO":requirementNo,
					"WT_STATUS":$("#WT_STATUS").val(),
				},
				success:function(resp){
					mydata = resp.page.list;
					$("#dataGrid").jqGrid('clearGridData');
					$("#dataGrid").jqGrid('setGridParam',{ // 重新加载数据
						data : resp.page.list, // newdata 是符合格式要求的需要重新加载的数据
					}).trigger("reloadGrid");
					js.closeLoading();
				}
			});
		},
		// 插入
		insert:function(){
			var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");
			if(ids.length==0){
				layer.msg("请勾选行项目！",{time:1000});
				return;
			}
			var rows = [];
			for(var i=0;i<ids.length;i++){
				var rowData = $("#dataGrid").jqGrid('getRowData',ids[i]);
				/*if (Number(rowData.RECEIVE_QTY) <= 0) {
					js.showErrorMessage("收货数量不能为0");
					return;
				}*/
//				console.info('PROCESS_TYPE'+rowData.PROCESS_TYPE.trim());
//				console.info('WT_STATUS'+rowData.WT_STATUS.trim());
				if(rowData.PROCESS_TYPE.trim() === "01" && (rowData.WT_STATUS.trim() === "未清" ||rowData.WT_STATUS.trim() === "部分确认" || rowData.WT_STATUS.trim() === "已确认" || rowData.WT_STATUS.trim() === "部分过账")) {

				}else {
					$("#dataGrid").jqGrid("setSelection", i+1,false);
					layer.msg("该数据无法取消下架！",{time:1500});
					return;
				}
				delete rowData.MAKTX;
				delete rowData.CONFIRMOR;
				delete rowData.CONFIRM_TIME;
				rows.push(rowData);
			}
			console.log(" ----> "+JSON.stringify(rows));
			js.loading();
			$.ajax({
				url:baseUrl + "out/resersalPicking/update",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":$("#werks").val(),
					"WH_NUMBER":$("#whNumber").val(),
					"requirementNo":$("#requirementNo").val(),
					"ITEMLIST":JSON.stringify(rows)
				},
//				async: false,
				success:function(resp){
					if(resp.code == '0'){
						//js.showMessage("操作成功!"+resp.msg,null,null,100);
						vm.query();
						//poFlag = true;
						//timestr = resp.timestr;
						//ebeln = resp.msg;
						//document.getElementById("createpo").style.background="GreenYellow";
						//document.getElementById("successpo").style.display="";
						//js.showErrorMessage("操作成功!"+resp.msg,1000*100);
						js.alert("操作成功!"+resp.msg,1000*100);
						js.closeLoading();
					}else{
						js.alert("操作失败!"+resp.msg,1000*100);
						//js.showErrorMessage("操作失败!"+resp.msg,1000*100);
						//js.showErrorMessage("操作失败!  "+resp.msg,null,null,1000);
						//$(".btn").attr("disabled", false);
						js.closeLoading();
					}
				}
			});

		},
		//显示列表
		showTable:function(){
			$("#dataGrid").dataGrid({
				datatype: 'local',
				data: mydata,
				cellEdit:true,
				cellurl:'#',
				cellsubmit:'clientArray',
				colNames: ['需求行项目','仓库任务', '状态', '料号', '物料描述', '数量','确认数量','已过账数量','单位', '库存类型', '批次', '供应商', '库位', '源存储区', '源仓位', '目标存储区', '目标仓位','确认者','确认时间','仓库处理类型','条码号','ID',''],
				colModel: [
					{ name: 'REFERENCE_DELIVERY_ITEM', index: 'REFERENCE_DELIVERY_ITEM', width: 100, sortable:false, align: 'center',
						/*cellattr: function(rowId, tv, rawObject, cm, rdata) {
                            //合并单元格
                            return 'id=\'REFERENCE_DELIVERY_ITEM' + rowId + "\'";
                        }*/
					},
					{ name: 'TASK_NUM', index: 'TASK_NUM', width: 100, sortable:false, align: 'center',

					},
					{ name: 'WT_STATUS', index: 'WT_STATUS', width: 55, sortable: false,align: 'center',
						formatter:function(val, obj, row, act){
							if(val == "00"){
								return "未清";
							} else if(val == "01"){
								return "部分确认";
							} else if(val == "02"){
								return "已确认";
							} else if(val == "03"){
								return "已取消";
							} else if(val == "04"){
								return "部分过账";
							} else if(val == "05"){
								return "已过账";
							} else{
								return val;
							}

						}
					},
					{ name: 'MATNR', index: 'MATNR', width: 100, sortable:false, align: 'center',

					},
					{ name: 'MAKTX', index: 'MAKTX', width: 70, sortable:false, align: 'center',

					},
					{ name: 'QUANTITY', index: 'QUANTITY', width: 55, sortable:false, align: 'center',

					},
					{ name: 'CONFIRM_QUANTITY', index: 'CONFIRM_QUANTITY', width: 65, sortable: false,align: 'center' },
					{ name: 'REAL_QUANTITY', index: 'REAL_QUANTITY',width: 75,},
					{ name: 'UNIT', index: 'UNIT', width: 55, sortable: false,editable:true,align: 'center' },
					{ name: 'SOBKZ', index: 'SOBKZ', width: 65, sortable: false,editable:true ,align: 'center'},
					{ name: 'BATCH', index: 'BATCH', width: 95, sortable: false,editable:true ,align: 'center'},
					{ name: 'LIFNR', index: 'LIFNR', width: 75, sortable: false,editable:true ,align: 'center'},
					{ name: 'LGORT', index: 'LGORT', width: 65, sortable: false,editable:true ,align: 'center'},
					{ name: 'FROM_STORAGE_AREA', index: 'FROM_STORAGE_AREA', width: 70, sortable: false,editable:true ,align: 'center'},
					{ name: 'FROM_BIN_CODE', index: 'FROM_BIN_CODE', width: 70, sortable: false,editable:true ,align: 'center'},
					{ name: 'TO_STORAGE_AREA', index: 'TO_STORAGE_AREA', width: 75, sortable: false,editable:true ,align: 'center'},
					{ name: 'TO_BIN_CODE', index: 'TO_BIN_CODE', width: 70, sortable: false,editable:true ,align: 'center'},

					{ name: 'CONFIRMOR', index: 'LGORT', width: 60, sortable: false,editable:true,align: 'center' },
					{ name: 'CONFIRM_TIME', index: 'SOBKZ', width: 100, sortable: false,editable:true,align: 'center' },
					{ name: 'PROCESS_TYPE', index: 'PROCESS_TYPE', hidden:true},
					{ name: 'LABEL_NO', index: 'LABEL_NO', hidden:true},
					{ name: 'ID', index: 'ID', hidden:true},
					{ name: 'REAL_QUANTITY', index: 'REAL_QUANTITY', hidden:true}
				],
				showCheckbox: true,
				rownumbers: true,
				//sortname: 'id',
				viewrecords: false,
				// sortorder: 'desc',
				height: '100%',
				multiselect: true,
				autowidth:true,
				shrinkToFit:false,
				autoScroll: true,

				beforeEditCell:function(rowid, cellname, v, iRow, iCol){
					lastrow=iRow;
					lastcell=iCol;
				},
				onCellSelect : function(rowid,iCol,cellcontent,e){
					var rec =  $("#dataGrid").jqGrid('getRowData', rowid);
// 			  	    if (rec['REQ_ITEM_STATUS']=='已推荐') {//过滤条件
// 			          	$("#dataGrid").jqGrid('setCell', rowid, 'BATCH', '', 'not-editable-cell');
// 			          	$("#dataGrid").jqGrid('setCell', rowid, 'RECOMMEND_QTY', '', 'not-editable-cell');
// 			          	$("#dataGrid").jqGrid('setCell', rowid, 'BIN_CODE', '', 'not-editable-cell');
// 			          	$("#dataGrid").jqGrid('setCell', rowid, 'LIFNR', '', 'not-editable-cell');
// 			          	$("#dataGrid").jqGrid('setCell', rowid, 'LGORT', '', 'not-editable-cell');
// 			          	$("#dataGrid").jqGrid('setCell', rowid, 'SOBKZ', '', 'not-editable-cell');
// 			        }

				},
				afterSaveCell:function(rowid, cellname, value, iRow, iCol){

					var rec =  $("#dataGrid").jqGrid('getRowData', rowid);
// 		        	alert(JSON.stringify(rec));
				},
				onSelectAll: function(aRowids,status) {

				},
				onSelectRow:function(rowid,e){
					var rec =  $("#dataGrid").jqGrid('getRowData', rowid);
					console.log(rec.PROCESS_TYPE.trim());
					console.log(rec.WT_STATUS.trim());
					if(rec.PROCESS_TYPE.trim() === "01" && (rec.WT_STATUS.trim() === "未清" ||rec.WT_STATUS.trim() === "部分确认" || rec.WT_STATUS.trim() === "已确认") || rowData.WT_STATUS.trim() === "部分过账") {

					}else {
						$("#dataGrid").jqGrid("setSelection", rowid,false);
						layer.msg("该数据无法取消下架！",{time:1500});
					}

				},
				beforeSelectRow:function(rowid,e){
					var $myGrid = $(this),
						i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),
						cm = $myGrid.jqGrid('getGridParam', 'colModel');
					if(cm[i].name == 'cb'){
						$('#dataGrid').jqGrid('setSelection',rowid);
					}
					return (cm[i].name == 'cb');
				},


			});
		},
		openLgortSelectWindow : function(call_back) {
			// 库位选择框
			js.layer.open({
				type : 2,
				title : "库位",
				area : [ "40%", "60%" ],
				content : baseUrl + "wms/out/stock_mutiselect.html",
				btn : [ "确定", "取消" ],
				success : function(layero, index_) {
					// 加载完成后，初始化
					var werks = $("#werks").val();
					var whNumber = $("#whNumber").val();
					var win = layero.find('iframe')[0].contentWindow;
					win.vm.init(werks, whNumber);
				},
				yes : function(index_, layero) {
					// 提交，调用生产订单页面的提交方法。
					var win = layero.find('iframe')[0].contentWindow;
					var lgortList = win.vm.getSelectedItems();
					var lgortItems = lgortList
						.map(function(val) {
							return val.LGORT;
						});
					var lgortList = lgortItems.join(",");
					if(lgortList.length > 0 && lgortList.charAt(lgortList.length-1) === ','){
						lgortList = lgortList.substring(0,lgortList.length-1);
					}
					if(call_back != null){
						call_back(lgortList);
					}
					else{
						//没有回调
						vm.lgortList = lgortList;
					}
					win.vm.close();
				},
				no : function(index, layero) {
				}
			});
		},
		onPlantChange:function(event){
			//工厂变化的时候，更新仓库号下拉框
			var plantCode = event.target.value;
			if(plantCode === null || plantCode === '' || plantCode === undefined){
				return;
			}
			//查询工厂仓库
			$.ajax({
				url:baseUrl + "common/getWhDataByWerks",
				data:{"WERKS":plantCode},
				success:function(resp){
					vm.warehourse = resp.data;
					if(resp.data.length>0){
						vm.whNumber=resp.data[0].WH_NUMBER//方便 v-mode取值
					}
				}
			})
		}
	}
});
$(document).ready(function() {
	vm.showTable();
//    var data = $("#dataGrid").jqGrid("getRowData");
//    console.log("data",data);

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
function getMaxId(ids){
	var maxId=0;
	for(var i=0;i<ids.length;i++){
		if(maxId<parseInt(ids[i])){
			maxId=ids[i];
		}
	}
	console.log("maxId",maxId);
	return maxId;
}

function isEmpty(obj){
	if(typeof obj == "undefined" || obj == null || obj == ""){
		return true;
	}else{
		return false;
	}
}
