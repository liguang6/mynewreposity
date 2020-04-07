$(function() {
	$("#dataGrid").dataGrid(
			{
				datatype : "local",
				colNames : [ '批次', '料号', '物料描述', '工厂', '仓库号', '库位', '储位代码',
						'库存数量', '<span style="color:blue">送检数量</span>','库存类型',
						'有效日期','单位', '供应商代码', '供应商名称', '<span style="color:blue">备注</span>','','','',''],
				colModel : [ {
					name : "BATCH",
					index : "BATCH",
					width : 92
				}, {
					name : "MATNR",
					index : "MATNR",
					width : 90
				}, {
					name : "MAKTX",
					index : "MAKTX",
					width : 114
				}, {
					name : "WERKS",
					index : "WERKS",
					width : 44
				}, {
					name : "WH_NUMBER",
					index : "WH_NUMBER",
					width : 54
				}, {
					name : "LGORT",
					index : "LGORT",
					width : 54
				}, {
					name : "BIN_CODE",
					index : "BIN_CODE",
					width : 75
				}, {
					name : "STOCK_FREEZE_QTY",//库存数量 + 冻结数量
					index : "STOCK_FREEZE_QTY",
					width : 65
				},
				{
					name : "INSPECT_QTY",
					index : "INSPECT_QTY",
					editable : true,
					width : 67
				},
				{
					name : "SOBKZ",
					index : "SOBKZ",
					editable : true,
					width : 68
				},
				{
					name : 'EFFECT_DATE',
					index : 'EFFECT_DATE',
					width : 67
				},
				{
					name : "MEINS",
					index : "MEINS",
					width : 66
				}, {
					name : "LIFNR",
					index : "LIFNR",
					width : 80
				}, {
					name : "LIKTX",
					index : "LIKTX",
					width : 80
				}, {
					name : "MEMO",
					index : "MEMO",
					editable: true,
					width : 100
				},{
					name : "EFFECT_DATE",
					index : "EFFECT_DATE",
					hidden : true
				},{
					name : "BARCODE_FLAG",
					index : "BARCODE_FLAG",
					hidden : true
				},{
					name : "GOOD_DATES",
					index : "GOOD_DATES",
					hidden : true
				},{
					name : "MIN_GOOD_DATES",
					index : "MIN_GOOD_DATES",
					hidden : true
				}],
				multiselect : true,
				//配置可编辑
				cellEdit: true,
				cellsubmit: 'clientArray',
				autowidth : true,
				shrinkToFit : false,
				autoScroll : true,
				multiselect : true,
				beforeEditCell:function(rowid, cellname, v, iRow, iCol){
		        	//编辑之前先保存当前编辑的行号列号
					vm.lastrow=iRow;
					vm.lastcell=iCol;
					vm.lastcellname=cellname;
				},
				beforeSelectRow : function(rowid, e) { // 点击复选框才选中行
					var $myGrid = $(this),
					i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),
					cm = $myGrid.jqGrid('getGridParam', 'colModel');
					if (cm[i].name == 'cb') {
						$('#dataGrid').jqGrid('setSelection', rowid);
					}
					return (cm[i].name == 'cb');
				},
				gridComplete: function(){
					var ids = $("#dataGrid").getDataIDs();
					for(var i=0;i<ids.length;i++){
						var rowData = $("#dataGrid").getRowData(ids[i]);

						//合格日期
						if(rowData.EFFECT_DATE != null && rowData.EFFECT_DATE != undefined ){
							var currentDate = new Date(formatDate(new Date()));
							var effectDate = new Date(rowData.EFFECT_DATE);
							var minGoodsDate = rowData.MIN_GOOD_DATES;
							var minMilims =  0;
							if(minGoodsDate != undefined && minGoodsDate != null && minGoodsDate != ""){
								minMilims = 1000 * 60 * 60 * 24 * parseInt(minGoodsDate)
							}
							if(effectDate - currentDate < minMilims){
								//合格日期小于当前日期的，用红色标识
								$('#'+ids[i]).find("td").css({"color":"#f03f29"});
							}
						}

						//启用了条码管理的，送检数量不可编辑
						if(rowData.BARCODE_FLAG == 'X')
							$("#dataGrid").jqGrid('setCell',ids[i],'INSPECT_QTY','','not-editable-cell');
					}
				}
			});
});

var vm = new Vue({
	el : "#vue-app",
	data : {
		werks:"",
		whNumber: "",
		warehourse : [],
		lgort: '',
		items : [],
		queryParams:{},
		lastrow:null,
		lastcell:null,
		lastcellname:null,
		lgortList:[],
	},
	created : function() {
		this.werks=$("#werks").find("option").first().val();
	},
	watch:{
		werks:{
			handler:function(newVal,oldVal){
				 $.ajax({
		        	  url:baseUrl + "common/getWhDataByWerks",
		        	  data:{"WERKS":newVal/*,"MENU_KEY":"IN_RECEIPT"*/},
		        	  success:function(resp){
		        		 vm.warehourse = resp.data;
		        		 if(resp.data.length>0){
		        			 vm.whNumber=resp.data[0].WH_NUMBER
		        		 }

		        	  }
		         });

				 this.$nextTick(function(){
					    //查询库位
					  	$.ajax({
					      	url:baseUrl + "common/getLoList",
					      	async:false,
					      	data:{
					      		"WERKS":newVal
					      		},
					      	success:function(resp){
					      		vm.lgortList = resp.data;
					      		vm.lgort = '';
					      	}
					    });
				 })
			}
		}
	},
	methods : {
		selectedItem:function(muti){
			//选中的数据项
			var ids=$("#dataGrid").getGridParam("selarrrow");
			var grData = [];
			for(var i in ids){
				grData[i] =  $("#dataGrid").jqGrid("getRowData",ids[i])//获取选中的行数据
			}
			return grData;
		},
		closeEditCell : function(){

		},
		create:function(){
			//创建前，先把编辑状态的单元格关闭.FIX BUG#624-2
			$('#dataGrid').jqGrid("saveCell", vm.lastrow, vm.lastcell);
			//创建复检单
			var items = this.selectedItem();
			if(items === undefined || items === null || items.length === 0)//空对象
			{
				 layer.msg("请选择需要创建复检单的库存数据",{time:1000});
				 return ;
			}


			//不能大于库存数量
			//FIX BUG#676
			var flag = true;
			for(var i of items){
				if(parseInt(i.STOCK_FREEZE_QTY) < parseInt(i.INSPECT_QTY)){
					flag = false;
					break;
				}
			}
			if(!flag){
				layer.msg("送检数量不能大于库存数量，请修改",{time:1000});
				return ;
			}

			$.ajax({
				url:baseUrl +"qc/wmsqcinspectionhead/add_stock_rejudge_inspect",
				contentType:"application/json",
				type:"post",
				data:JSON.stringify(items),
				success:function(resp){
					if(resp.code === 0){
						//alert("复检单 " + resp.inspectNo + " 创建成功");
						$.each(items,function(i,item){
							item.INSPECTION_NO=resp.inspectNo;
							item.INSPECTION_TYPE='库存复检';
							item.CREATE_DATE=resp.CREATE_DATE;
							item.CREATOR=resp.CREATOR;
							item.INSPECTION_QTY=item.INSPECT_QTY;
							item.UNIT=item.MEINS;
						})
						$("#resultMsg").html("复检单 " + resp.inspectNo + " 创建成功");
						$("#inspectionList").val(JSON.stringify(items));
						layer.open({
				        	type : 1,
				        	offset : '50px',
				        	skin : 'layui-layer-molv',
				        	title : "创建成功",
				        	area : [ '500px', '300px' ],
				        	shade : 0,
				        	shadeClose : false,
				        	content : jQuery("#resultLayer"),
				        	btn : [ '确定'],
				        	btn1 : function(index) {
				        		layer.close(index);
								//$("#btnCreat").removeAttr("disabled");
				        	}
				        });
						vm.query();
					}else{
						alert("创建失败, " + resp.msg);
					}
				}
			});
		},
		refreshJqGrid : function() {
			$("#dataGrid").dataGrid("clearGridData");
			for (var i = 0; i < this.items.length; i++) {
				$("#dataGrid").dataGrid('addRowData', i, this.items[i]);
			}
		},
		query : function() {
			var werks = $("#werks").val();
			var whNumber = $("#whNumber").val();
			var batch = $("#batch").val();
			var matnr = $("#matnr").val();
			var lifnr = $("#lifnr").val();
			var effectDateFlag = $("#effectDateFlag").val();
			var lgort = $("#lgort").val();
			var status = $("#status").val();//bug1444

			if(batch == "" && matnr == "" && lifnr =="" && lgort =="" && status == ""){
				alert("查询条件：库位、批次、料号、供应商，库存状态不能都为空！");
				return false;
			}

			$.post(baseUrl + "qc/wmsqcinspectionitem/stokerejudgeitems", {
				"WERKS" : werks,
				"WH_NUMBER" : whNumber,
				"BATCH" : batch,
				"MATNR" : matnr,
				"LIFNR" : lifnr,
				"EFFECT_DATE_FLAG":effectDateFlag,
				"LGORT" : lgort,
				"status": status,
			}, function(resp) {
				if (resp.code != 0) {
					alert("查询数据失败! ", resp.msg);
					return;
				}
				for(var i=0;i<resp.page.list.length;i++){
					resp.page.list[i].INSPECT_QTY = resp.page.list[i].STOCK_FREEZE_QTY;//送检数量默认等于库存数量
				}
				this.items = resp.page.list;
				this.refreshJqGrid();
			}.bind(this));
		}
	}
});
