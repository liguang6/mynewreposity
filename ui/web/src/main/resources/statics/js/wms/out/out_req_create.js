//all in one use vue.
var vm = new Vue({
	el:'#vue-app',

	data:{
		//数据定义businessList
		businessList:[],
		items:[],//创建领料需求数据行项目
		werks:'',//工厂
		whNumber:'',//仓库号
		requireTypes:'',//需求类型
		filterUseMaterials:'1',//过滤非上线物料
		requireDate:'',//需求日期
		lgortList:'',//库位
		summaryMode:'01',//汇总模式
		use:'',//领料用途
		requireTime:'00',//需求时间
		warehourse:[],//仓库列表
		werksList:[],//工厂列表
		vaildateUrl:'',//校验生产订单url
		queryUrl:'',//查询生产订单url
		createUrl:'',//创建需求url
		costcenter:"",//成本中心代码
		//bfyy:"",//报废原因
		costcenterName:"",//成本中心名称
		compcode:'',//接收公司代码
		all:false,//ignore this
		innerOrder:'',//内部订单号
		innerOrderName:'',//内部订单名字
		innerOrder_AUTYP:'',//内部订单类型
		wbsElementName:"",//wbs元素名字
		wbsElementNo:"",//wbs元素号
		creating:false,//是否在创建需求中
		w_vendor:'',//委外单位（供应商）
		costomerCode:'',//客户代码
		customerDesc:'',//客户描述
		transportDay:'',//预计运输天数
		transportType:'1',//运输方式
		shipArea:'1',//发货地区
		acceptArea:'2',//收货地区
		acceptPlant:"",//接收工厂
		barcode:"",//条码
		supermarket:'',
		distribution:"", //总装配送单号
		deliveryType:"JIT", //配送模式
		priority:"0", //优先级

		lastrow:null,//编辑的最后一行行号
		lastcell:null,//编辑的最后一行单元格
		lastcellname:null,//编辑的最后一行行名字

		aceptLgortList:"",//接收库位
		sendLgortList:"",//发送库位
		checkMaterialHxFlag:false,//是否检查物料的核销业务
		expect_box_qty:"",//预计箱数
		receiver:"",//接收方
	},
	watch:{

		lgortList:function () {
			//alert('vm.lgortList '+vm.lgortList);
			var ids = $("#dataGrid").getDataIDs();
			if(vm.lgortList==null ||vm.lgortList===''){
				for (var i = 0; i < ids.length; i++) {
					var gridData = $("#dataGrid").jqGrid("setCell", ids[i], 'LGORT', null);//这是获得 某一行的数据
				}
			}else {
				for (var i = 0; i < ids.length; i++) {
					var gridData = $("#dataGrid").jqGrid("setCell", ids[i], 'LGORT', vm.lgortList);//这是获得 某一行的数据
				}
			}


		},

		werks:{
			handler:function(newVal,oldVal){
				console.info(newVal);
				vm.warehourse = [];
				this.$nextTick(function(){

					$.ajax({
						url:baseURL+"common/getWhDataByWerks",
						dataType : "json",
						type : "post",
						data : {
							"WERKS":newVal,
							"MENU_KEY":"A91"
						},
						async: true,
						success:function(resp){
							if(resp && resp.data.length>0){
								vm.warehourse = resp.data;
								vm.whNumber = resp.data[0].WH_NUMBER;
							}
						}
					})
					//vm.lgortList = getLgortList(newVal)
				});

				if(this.requireTypes==='53'||this.requireTypes==='54'){
					vm.checkMaterialHx();
				}
			}
		},
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
		,
		requireTypes:function(){
			//出库需求业务类型
			vm.whNumber = $("#whNumber").val();
			switch(this.requireTypes){
				case '41':this.init1();break;//生产订单领料
				case '42':this.init2();break;//生产订单补料
				case '48':this.init3();break;//线边仓 工厂调拨类型转移
				case '44':this.init4();break;//成本中心
				case '43':this.init5();break;//内部订单

				case '49': this.init6();break;//委外订单
				case '50': this.init7();break;//STO销售发货(311)采购
				case '51': this.init7_1();break;//销售订单发货(601)销售
				case '64':this.init8();break;//UB转储单出库需求
				case '52':this.init10();break;//外部销售发货（251）

				case '46':this.init11();break;//工厂间调拨
				case '45':this.init12();break;//wbs元素
				case '47':this.init13();break;//库存地点调拨311—类型不转移
				case '53':this.init16();break;//工厂间调拨发货（A303）
				case '54':this.init17();break;//SAP交货单销售发货（A311T）
				case '73':this.init18();break;//报废（551）
				case '76':this.init19();break;//扫描看板卡领料311
				case '75':this.init20();break;//STO一步联动发货311
				case '77':this.init21();break;//工厂间调拨301（总装）
			}
		}
	},
	created:function(){
		var plantCode = $("#werks").val();
		this.businessList=getBusinessList("07","A91",plantCode);
		if(undefined == this.businessList || this.businessList.length<=0){
			js.showErrorMessage("工厂未配置需求业务类型！");
		}else{
			this.requireTypes=this.businessList[0].CODE;
		}
		//初始化需求日期
		var date = new Date();
		var today = date.toLocaleDateString().replace(/\//g, '-');
		this.requireDate = getCurDate();
		//this.werks=$("#werks").find("option").first().val();
		//加载仓库
		//var plantCode = vm.werks;

		//初始化仓库
		$.ajax({
			url:baseUrl + "common/getWhDataByWerks",
			data:{
				"WERKS":plantCode,
				"MENU_KEY":"A91"
			},
			success:function(resp){
				vm.warehourse = resp.data;
				vm.werks = plantCode;
				if(resp.data.length>0){
					vm.whNumber=resp.data[0].WH_NUMBER//方便 v-mode取值
				}

			}
		});

		//TIP: JqGrid必须要在页面加载完成后才初始化。
//		$(function(){
//			//页面加载时，默认为生产订单领料。
//			vm.init1();
//		});


		//..
		//绑定粘贴事件
		$(function(){

			$("#dataGridDiv").bind("paste",function(e){
				e.preventDefault();

				//js.loading();

				// 获取粘贴板数据
				var data = null;
				var clipboardData = window.clipboardData || e.originalEvent.clipboardData;
				data = clipboardData.getData('Text');

				//如果是指定输入框，只粘贴数据到当前输入框。
//				if("INPUT" === e.target.tagName){
//					e.target.value = data;
//					return true;
//				}


				//分行
				var trList = data.split("\n");
				//去掉空白行
				trList = trList.filter(function(val){
					return val.length > 0;
				});
				//分隔为二维数组
				var tdList = trList.map(function(val){
					return val.split("\t");
				});

				//如果是指定输入框，只粘贴数据到当前输入框。
				if("INPUT" === e.target.tagName){
					e.target.value = tdList[0][0];
					var lastrow = vm.lastrow;
					var lastcell = vm.lastcell;
					var lastcellname = vm.lastcellname;
					$.ajaxSettings.async = false;
					var records = $('#dataGrid').jqGrid('getGridParam','records');
					for(var i in tdList){
						if (parseInt(i) == 0) {
							continue;
						}
						var rowId = parseInt(lastrow) + parseInt(i) - 1;
						var rowData;
						if (lastcellname === 'MATNR') {
							rowData = {MATNR:tdList[i][0]};
						} else if (lastcellname === 'REQ_QTY') {
							rowData = {REQ_QTY:tdList[i][0]};
						} else if (lastcellname === 'LGORT') {
							rowData = {LGORT:tdList[i][0]};
						} else if (lastcellname === 'VENDOR') {
							rowData = {VENDOR:tdList[i][0]};
						} else if (lastcellname === 'MEMO') {
							rowData = {MEMO:tdList[i][0]};
						} else if (lastcellname === 'LIFNR') {
							rowData = {LIFNR:tdList[i][0]};
						}

						if (parseInt(rowId) < parseInt(records)) {
							$("#dataGrid").jqGrid('setRowData',rowId,rowData);
						} else {
							$("#dataGrid").jqGrid('addRowData',rowId,rowData);
						}

						if (lastcellname === 'MATNR') {
							var MATNR = $("#dataGrid").getCell(rowId,"MATNR");
							vm.queryMatInfo(MATNR,function(matinfo){
								//设置物料描述，物料单位
								$("#dataGrid").jqGrid('setCell',rowId,"MAKTX",matinfo.MAKTX);
								$("#dataGrid").jqGrid('setCell',rowId,"MEINS",matinfo.MEINH);
								//更新库存
								vm.reloadstock(null,null,rowId);
							});
						}
					}
					$.ajaxSettings.async = true;//恢复ajax异步
					return;
				}

				if(vm.requireTypes === '44' || vm.requireTypes === '43' || vm.requireTypes === '45' || vm.requireTypes === '46' || vm.requireTypes === '47' || vm.requireTypes === '48' || vm.requireTypes === '73' || vm.requireTypes === '75'){
					//料号 需求数量 备注
					//追加
					var records = $('#dataGrid').jqGrid('getGridParam','records');
					//不异步执行
					//这里需要更新多条数据，需要按循序
					//如果是异步的， 服务器返回值时 ,tdList已经遍历完了，rowId指向后一条记录，这样就只会更新最后一条记录
					$.ajaxSettings.async = false;
					for(var i in tdList){
						var rowId = parseInt(records) + parseInt(i);
						var rowData;
						if (vm.requireTypes === '48') {
							rowData = {MATNR:tdList[i][0],REQ_QTY:tdList[i][1],LGORT:tdList[i][2],VENDOR:tdList[i][3],MEMO:tdList[i][4]};
						} else {
							rowData = {MATNR:tdList[i][0],REQ_QTY:tdList[i][1],LIFNR:tdList[i][2],LGORT:tdList[i][3],MEMO:tdList[i][4]};
						}

						$("#dataGrid").jqGrid('addRowData',rowId,rowData);
						var MATNR = $("#dataGrid").getCell(rowId,"MATNR");
						vm.queryMatInfo(MATNR,function(matinfo){
							//设置物料描述，物料单位
							$("#dataGrid").jqGrid('setCell',rowId,"MAKTX",matinfo.MAKTX);
							$("#dataGrid").jqGrid('setCell',rowId,"MEINS",matinfo.MEINH);
							//更新库存
							vm.reloadstock(null,null,rowId);
						});
					}
					$.ajaxSettings.async = true;//恢复ajax异步
				}

			});

		});

	},


	methods:{

		importExcel:function(){
			//-------导入操作--------
			//$("#importOperation").click(function(){
				var options = {
					type: 2,
					maxmin: true,
					shadeClose: true,
					title: "<i class='fa fa-upload' aria-hidden='true'>&nbsp;</i> 批导操作",
					area: ["1020px", "560px"],
					content: baseUrl + "wms/out/out_req_create_upload.html",
					btn: ['<i class="fa fa-check"></i> 保存'],
                    success:function(layero, index){
                        var win = layero.find('iframe')[0].contentWindow;
                        win.vm.werks = $("#werks").val();
                        console.info("this.requireTypes "+vm.requireTypes);
                        win.vm.requireTypes = vm.requireTypes;
						win.vm.acceptPlant = vm.acceptPlant;
						win.vm.aceptLgortList = vm.aceptLgortList;
                        win.vm.whNumber = $("#whNumber").val();
                        win.vm.innerOrder = vm.innerOrder;
                        win.vm.innerOrder_AUTYP = vm.innerOrder_AUTYP;
                        // win.vm.saveFlag = false;
                        // win.vm.getInfo(grData.id);
                        // $(win.document.body).find("#factoryCode").attr("readonly",true);
                        // $(win.document.body).find("#warehouseCode").attr("readonly",true);
                        if(vm.acceptPlant ==='' && vm.acceptPlant === ''){
                            js.showErrorMessage("接收库位，接收库存不能为空！");
                        }
                    },
					btn1:function(index,layero){
						var win = layero.find('iframe')[0].contentWindow;
						var saveUpload= $("#saveUpload", layero.find("iframe")[0].contentWindow.document);
						$(saveUpload).click();
						// 保存成功 关闭窗口
						if(win.vm.saveFlag){
							if(typeof listselectCallback == 'function'){
								listselectCallback(index,true);
							}
						}
						//vm.reload();
					}
				};
				options.btn.push('<i class="fa fa-close"></i> 关闭');
				js.layer.open(options);
			//});
		},
		lgortLd:function (){
			var ids = $("#dataGrid").getDataIDs();
			for(var i=0;i<ids.length;i++){
				var gridData= $("#dataGrid").jqGrid("setCell",ids[i],'LGORT',vm.lgortList);//这是获得 某一行的数据
			}
			/*if(vm.requireTypes==='64'){
				var ids = $("#dataGrid").getDataIDs();
				for(var i=0;i<ids.length;i++){
					var gridData= $("#dataGrid").jqGrid("setCell",ids[i],'LGORT',vm.lgortList);//这是获得 某一行的数据
				}
			}*/


		},
		updateStr:function(){
			vm.costcenterName='';
			vm.innerOrderName='';
			vm.wbsElementName='';
			vm.customerDesc='';
			vm.aceptLgortList='';
		},
		init1_grid:function(){
			//生产订单领料261
			$("#dataGrid").dataGrid(
				{
					datatype : "local",
					colNames : [
						"<span style='color:red'>* </span> <span style='color:blue'>生产订单号<i class=\"fa fa-clone fill-down\" onclick=\"vm.fillDown('AUFNR')\" title=\"向下填充\" style=\"color:green\" aria-hidden=\"true\"></i></span>",
//						"生产订单号",
						"行项目",
						"预留号",
						"行项目",
						"<span style='color:red'>* </span> <span style='color:blue'>料号</span>",
						"物料描述",
						"单位",
						"订单需求",
						"已领数量",
						"可领数量",
						"<span style='color:red'>* </span><span style='color:blue'>需求数量  <i class=\"fa fa-clone fill-down\" onclick=\"vm.fillDown('REQ_QTY')\" title=\"向下填充\" style=\"color:green\" aria-hidden=\"true\"></i></span>",
						"<span style='color:blue'>库位  <i class=\"fa fa-clone fill-down\" onclick=\"vm.fillDown('LGORT')\" title=\"向下填充\" style=\"color:green\" aria-hidden=\"true\"></i></span>",
						"<span style='color:blue'>供应商代码  <i class=\"fa fa-clone fill-down\" onclick=\"vm.fillDown('VENDOR')\" title=\"向下填充\" style=\"color:green\" aria-hidden=\"true\"></i><span>",
						"特殊库存标识", "库存",
						"<span style='color:blue'>产线 <i class=\"fa fa-clone fill-down\" onclick=\"vm.fillDown('LINE')\" title=\"向下填充\" style=\"color:green\" aria-hidden=\"true\"></i><span>",
						"<span style='color:blue'>工位  <i class=\"fa fa-clone fill-down\" onclick=\"vm.fillDown('STATION')\" title=\"向下填充\" style=\"color:green\" aria-hidden=\"true\"></i><span>" ],
					colModel : [ {
						name : "AUFNR",
						index : "AUFNR",
						width:100,
						editable:true
					}, {
						name : "POSNR",
						index : "POSNR",
						width:60
					},{
						name : "RSNUM",
						index : "RSNUM",
						width:100
					},{
						name : "RSPOS",
						index : "RSPOS",
						width:60
					},{
						name : "MATNR",
						index : "MATNR",
						width:110,
						editable:true
					}, {
						name : "MAKTX",
						index : "MAKTX",
						width:300
					}, {
						name : "MEINS",
						index : "MEINS",
						width:65
					}, {
						name : "BDMNG",
						index : "BDMNG",
						width:65
					}, {
						name : "TL_QTY",
						inde : "TL_QTY",
						width:65,
                        formatter : function(val, obj, row, act) {
                            if(''+val ==="undefined"){
                                return 0;
                            } else {
                                return val;
                            }
                        }
					}, {
						name : "KL_QTY",
						index : "KL_QTY",
						width:65,
						formatter : function(val, obj, row, act) {
                            if(''+val ==="undefined"){
                                return 0;
                            } else {
                                return val;
                            }

							/*console.info(row.BDMNG);
							console.info(row.TL_QTY);
							console.info(row.OUT_QTY);
							var klQty =  (row.BDMNG- row.TL_QTY);
							console.info(klQty);
							return klQty === null || klQty === undefined ? 0 :klQty;
							*/
						}
					}, {
						name : "REQ_QTY",
						index : "REQ_QTY",
						formatter : function(val, obj, row, act) {
							if(''+val ==="undefined"){
								return 0;
							}
							if(val==null){
								var klQty =  (row.BDMNG- row.TL_QTY);
								return klQty === null || klQty === undefined ? 0 :klQty;

							}else {
								return val;
							}

						},
						width:100,
						editable : true
					}, {
						name : "LGORT",
						index : "LGORT",
						width:90,
						editable : true,
					}, {
						name : "VENDOR",// 可编辑，模糊查询，根据物料号。
						index : "VENDOR",
						width:130,
						editable : true
					}, {
						name : "SOBKZ",
						index : "SOBKZ",
						width:100,
					}, {
						name : "TOTAL_STOCK_QTY",
						index : "TOTAL_STOCK_QTY",
						width:65,
						formatter:function(val, obj, row, act){
							if(val === null || val === undefined || val === '')
								val = '0';

							/*if(val === '0' || val < row.REQ_QTY)
                               return "<div style='background-color:yellow;'>" + val +"</div>";

                            else */
							return val;
						}
					}, {
						name : "LINE",
						index : "LINE",
						width:90,
						editable : true
					}, {
						name : "STATION",
						index : "STATION",
						width:120,
						editable : true
					} ],
					viewrecords : true,
					cellEdit : true,
					cellurl : '#',
					cellsubmit : 'clientArray',
					autowidth:true,
					autoScroll: true,
					multiselect : true,
					shrinkToFit:false,
					beforeEditCell:function(rowid, cellname, v, iRow, iCol){
						//编辑之前先保存当前编辑的行号列号
						vm.lastrow=iRow;
						vm.lastcell=iCol;
						vm.lastcellname=cellname;
					},
					afterEditCell:function(rowid, cellname, v, iRow, iCol){
						var  VENDOR = $("#dataGrid").getCell(iRow-1,"VENDOR");
						var LGORT = $("#dataGrid").getCell(iRow-1,"LGORT");
						if(cellname === 'VENDOR'){
							getVendorNoSelect($("#"+iRow+"_"+cellname),null,function(rep){
								//供应商变化,更新库存
								vm.reloadstock(LGORT,rep.LIFNR);
							});
						}

						if(cellname === "LGORT"){
							vm.openLgortSelectWindow(function(lgortList){
								//更新库存单元格
								$('#dataGrid').jqGrid("saveCell", iRow, iCol);
								$("#dataGrid").jqGrid('setCell',rowid, cellname, lgortList,'editable-cell');
								//库存变化，更新库存
								vm.reloadstock(lgortList,VENDOR);
							});
						}
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
					beforeSaveCell:function(rowid, cellname, v, iRow, iCol){
						if(cellname === 'AUFNR'){
							//查询订单信息
							vm.querySapMoComponent(v,function(data){
								if(data.length < 1){
									alert("生产订单不存在");
									$("#dataGrid").jqGrid('setCell', rowid, cellname, null,'editable-cell');
								}
							});
						}
						if(cellname === 'MATNR'){
							vm.queryMatInfo(v,function(matinfo){
								$("#dataGrid").jqGrid('setCell',iRow-1,"MAKTX",matinfo.MAKTX);
								$("#dataGrid").jqGrid('setCell',iRow-1,"MEINS",matinfo.MEINH);
							});
						}
					}
				});
			this.disableSort();
		},
		init1:function(){
			this.updateStr();
			$("#links").attr("style","display:block;");//显示div
			//console.log("init1");
			this.vaildateUrl = baseUrl + "out/createRequirement/processProduceOrder";
			this.queryUrl = baseUrl +"out/createRequirement/producerOrders";
			this.createUrl = baseUrl + "out/createRequirement/create";
			this.reLoadGrid();
			this.init1_grid();
			this.initGrid();
		},
		init2:function(){
			this.updateStr();
			$("#links").attr("style","display:block;");//显示div
			//生产订单补料261
			//console.log("init2");
			this.reLoadGrid();
			this.init1_grid();
			this.initGrid();
			this.queryUrl =  baseUrl +"out/produceorderaddmat/producerOrders";
			this.vaildateUrl = baseUrl + "out/produceorderaddmat/processProduceOrder";
			this.createUrl = baseUrl + "out/produceorderaddmat/create";
		},
		init3:function(){
			this.updateStr();
			$("#links").attr("style","display:block;");//显示div
			//线边仓/库存地点调拨领料311
			//console.log("init3");
			this.reLoadGrid();
			this.init1_grid();
			this.initGrid();
			this.vaildateUrl = baseUrl + "out/producelinewarehouse/processProduceOrder";
			this.queryUrl = baseUrl +"out/producelinewarehouse/producerOrders";
			this.createUrl = baseUrl + "out/producelinewarehouse/create";
		},
		init4_grid:function(){
			$("#dataGrid").dataGrid(
				{
					datatype : "local",
					colNames : [
						"<span style='color:red'>* </span><span style='color:blue'>料号</span>",
						"物料描述",
						"单位",
						"<span style='color:red'>* </span><span style='color:blue'>需求数量</span>",
						"<span style='color:blue'>供应商代码 <i class=\"fa fa-clone fill-down\" onclick=\"vm.fillDown('LIFNR')\" title=\"向下填充\" style=\"color:green\" aria-hidden=\"true\"></i><span>",
						"<span style='color:blue'>库位 <i class=\"fa fa-clone fill-down\" onclick=\"vm.fillDown('LGORT')\" title=\"向下填充\" style=\"color:green\" aria-hidden=\"true\"></i></span>",
						"库存",
						'<span style="color:blue">预计箱数<span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="vm.fillDown(\'BOX_COUNT\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
						"<span style='color:blue'>备注<span>",
						"总账科目",
						"工位"
					],
					colModel : [ {
						name : "MATNR",
						index : "MATNR",
						width:110,
						editable:true,
						align:'center'
					}, {
						name : "MAKTX",
						index : "MAKTX",
						width:260,
						align:'center'
					}, {
						name : "MEINS",
						index : "MEINS",
						width:60,
						align:'center'
					}, {
						name : "REQ_QTY",
						index : "REQ_QTY",
						editable : true,
						width:80,
						align:'center'
					}, {
						name : "LIFNR",
						index : "LIFNR",
						editable : true,
						width:100,
						align:'center'
					}, {
						name : "LGORT",
						index : "LGORT",
						editable : true,
						width:100,
						align:'center'
					}, {
						name : "TOTAL_STOCK_QTY",
						index : "stock",
						width:60,
						align:'center'
					},{
						name:"BOX_COUNT",
						index:"BOX_COUNT",
						width:100,
						editable:true,
						align:"center",
						hidden:true,
						sortable:false
					}, {
						name : "MEMO",
						index : "MEMO",
						width:100,
						editable : true,
						align:'center'
					},{
						name:'ZZKM',
						index:'ZZKM',
						hidden:true,
						align:'center'
					},{
						name: 'STATION', index: 'STATION', width: 60,align:'center',editable : true,
					}
						,],
					viewrecords : true,
					cellEdit : true,
					cellurl : '#',
					cellsubmit : 'clientArray',
					multiselect : true,
					beforeSelectRow : function(rowid, e) { // 点击复选框才选中行
						var $myGrid = $(this),
							i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),
							cm = $myGrid.jqGrid('getGridParam', 'colModel');
						if (cm[i].name == 'cb') {
							$('#dataGrid').jqGrid('setSelection', rowid);
						}
						return (cm[i].name == 'cb');
					},
					beforeEditCell:function(rowid, cellname, v, iRow, iCol){
						//编辑之前先保存当前编辑的行号列号
						vm.lastrow=iRow;
						vm.lastcell=iCol;
						vm.lastcellname=cellname;
					},
					afterEditCell:function(rowid, cellname, v, iRow, iCol){
						//供应商模糊查询 & 库存选择弹出框
						var LIFNR = $("#dataGrid").getCell(iRow-1,"LIFNR");
						var LGORT =  $("#dataGrid").getCell(iRow-1,"LGORT");
						if(cellname === 'LIFNR'){
							getVendorNoSelect($("#"+iRow+"_"+cellname),null,function(rep){
								//供应商变化,更新库存
								vm.reloadstock(LGORT,rep.LIFNR);
							});
						}

						if(cellname === "LGORT"){
							vm.openLgortSelectWindow(function(lgortList){
								//更新库存单元格
								$('#dataGrid').jqGrid("saveCell", iRow, iCol);
								$("#dataGrid").jqGrid('setCell',rowid, cellname, lgortList,'editable-cell');
								//库存变化，更新库存
								vm.reloadstock(lgortList,LIFNR);
							});
						}

					},
					beforeSaveCell:function(rowid,celname,value,iRow,iCol){
						//输入值保存前
						//所有需要关联更新的代码 写在这里
						var LIFNR = $("#dataGrid").getCell(iRow-1,"LIFNR");
						var LGORT =  $("#dataGrid").getCell(iRow-1,"LGORT");

						if(celname === 'MATNR'){
							vm.queryMatInfo(value,function(matinfo){
								//iRow-1=rowId
								//设置物料描述，物料单位
								$("#dataGrid").jqGrid('setCell',iRow-1,"MAKTX",matinfo.MAKTX);
								$("#dataGrid").jqGrid('setCell',iRow-1,"MEINS",matinfo.MEINH);
								//更新库存
								vm.reloadstock(LGORT,LIFNR,iRow-1);

							});
						}
						else if(celname === "REQ_QTY"){
							//更新需求数量，更新库存的样式
							vm.stockStyle(iRow-1,value);
						}
						else if(celname === 'LIFNR' || celname === 'LGORT'){
							if(celname === 'LIFNR'){
								LIFNR = value;
							}
							if(celname === 'LGORT'){
								LGORT = value;
							}
							vm.reloadstock(LGORT,LIFNR,iRow-1);
						}
					}
				});
			$("#dataGrid").jqGrid('navGrid','#psetcols',{edit:false,add:false,del:false});
		},
		init4:function(){
			this.updateStr();
			$("#links").attr("style","display:block;");//显示div
			//成本中心领料
			//console.log("init4");
			this.createUrl = baseUrl + "out/createRequirement/costcenterReq";
			this.items = [];
			this.reLoadGrid();
			this.init4_grid();
		},
		init5:function(){
			this.updateStr();
			$("#links").attr("style","display:block;");//显示div
			//内部订单
			//console.log("init5");
			this.createUrl = baseUrl + "out/innerorder/createInternalOrder";
			this.items = [];
			this.reLoadGrid();
			this.init4_grid();//与成本中心一样
		},

		init6:function(){
			this.updateStr();
			$("#links").attr("style","display:block;");//显示div
			//委外订单
			//console.log("init6");
			this.reLoadGrid();
			this.vaildateUrl = baseUrl + "out/outreq/validateOutItems6";
			this.queryUrl = baseUrl + "out/outreq/queryOutItems6";
			this.createUrl = baseUrl + "out/outreq/outReqCreate6";
			$("#dataGrid").dataGrid(
				{
					datatype : "local",
					colNames : [
						"采购订单号",
						"行项目号",
						"<span style='color:red'>* </span> <span style='color:blue'>料号</span>",
						"物料描述",
						"组件数量",
						"单位",
						"<span style='color:red'>* </span><span style='color:blue'>需求数量</span>",
						"<span style='color:blue'>供应商代码<span>",
						"<span style='color:blue'>库位<span>",
						"库存",
						"基本单位",
						"<span style='color:blue'>备注<span>",
						"EBELN",
						"EBELP"
					],
					colModel : [
						{
							name : "EBELN",
							index : "EBELN",
							width:200,
						},{
							name : "EBELP",
							index : "EBELP",
							width:150,
						}, {
							name : "MATNR",
							index : "MATNR",
							width:200,
							editable:true
						}, {
							name : "TXZ01",
							index : "TXZ01",
							width:400
						}, {
							name : "MENGE",
							index : "MENGE"
						}, {
							name : "MEINS",
							index : "MEINS"
						}, {
							name : "REQ_QTY",
							index : "REQ_QTY",
							editable : true
						}, {
							name : "VENDOR",
							inde : "VENDOR",
							editable : true
						}, {
							name : "LGORT",
							index : "LGORT",
							editable : true
						}, {
							name : "TOTAL_STOCK_QTY",
							index : "TOTAL_STOCK_QTY"
						}, {
							name : "BASE_MEINS",
							index : "BASE_MEINS"
						}, {
							name : "MEMO",
							index : "MEMO",
							editable : true
						},{
							name:"EBELN",
							index:"EBELN",
							hidden:true
						},{
							name:"EBELP",
							index:"EBELP",
							hidden:true
						}],
					viewrecords : true,
					cellEdit : true,
					cellurl : '#',
					cellsubmit : 'clientArray',
					multiselect : true,
					beforeSelectRow : function(rowid, e) { // 点击复选框才选中行
						var $myGrid = $(this),
							i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),
							cm = $myGrid.jqGrid('getGridParam', 'colModel');
						if (cm[i].name == 'cb') {
							$('#dataGrid').jqGrid('setSelection', rowid);
						}
						return (cm[i].name == 'cb');
					},
					beforeEditCell:function(rowid, cellname, v, iRow, iCol){
						//编辑之前先保存当前编辑的行号列号
						vm.lastrow=iRow;
						vm.lastcell=iCol;
						vm.lastcellname=cellname;
					},
					afterEditCell:function(rowid, cellname, v, iRow, iCol){
						//供应商模糊查询 & 库存选择弹出框
						var LIFNR = $("#dataGrid").getCell(iRow-1,"VENDOR");
						var LGORT =  $("#dataGrid").getCell(iRow-1,"LGORT");
						if(cellname === 'VENDOR'){
							getVendorNoSelect($("#"+iRow+"_"+cellname),null,function(rep){
								//供应商变化,更新库存
								vm.reloadstock(LGORT,rep.LIFNR);
							});
						}

						if(cellname === "LGORT"){
							vm.openLgortSelectWindow(function(lgortList){
								//更新库存单元格
								$('#dataGrid').jqGrid("saveCell", iRow, iCol);
								$("#dataGrid").jqGrid('setCell',rowid, cellname, lgortList,'editable-cell');
								//库存变化，更新库存
								vm.reloadstock(lgortList,LIFNR);
							});
						}
					},
					beforeSaveCell:function(rowid,celname,value,iRow,iCol){
						//输入值保存前
						//所有需要关联更新的代码 写在这里
						var LIFNR = $("#dataGrid").getCell(iRow-1,"VENDOR");
						var LGORT =  $("#dataGrid").getCell(iRow-1,"LGORT");

						if(celname === 'MATNR'){
							vm.queryMatInfo(value,function(matinfo){
								//设置物料描述，物料单位
								$("#dataGrid").jqGrid('setCell',iRow-1,"TXZ01",matinfo.MAKTX);
								$("#dataGrid").jqGrid('setCell',iRow-1,"MEINS",matinfo.MEINH);
								//更新库存
								vm.reloadstock(LGORT,LIFNR,iRow-1);
							});
						}
						else if(celname === "REQ_QTY"){
							//更新需求数量，更新库存的样式
							vm.stockStyle(iRow-1,value);
						}

					}
				});
			this.initGrid();
		},
		init7:function(){//311
			this.updateStr();
			this.aceptLgortList = "00ZT";
			$("#links").attr("style","display:block;");//显示div
			//SAP销售订单发货
			//console.log("init7");
			this.vaildateUrl = baseUrl + "out/outreq/validateOutItems7";
			this.queryUrl = baseUrl + "out/outreq/queryOutItems7";
			this.createUrl = baseUrl + "out/outreq/outReqCreate7";
			this.reLoadGrid();
			$("#dataGrid").dataGrid(
				{
					datatype : "local",
					colNames : [
						"SAP交货单",
						"行项目",
						"移动类型",
						"<span style='color:red'>* </span>料号",
						"物料描述",
						"单位",
						"<span style='color:blue'>交货数量<span>",
						"<span style='color:blue'>需求数量<span>",
						"<span style='color:blue'>库位<span>",
						"<span style='color:blue'>供应商代码<span>",
						"特殊库存类型",
						"库存",
						"基本单位",
						'<span style="color:blue">预计箱数<span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="vm.fillDown(\'EXPECT_BOX_QTY\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
						"客户订单",
						"采购订单号",
						"行项目号",
						"客户料号",
						"参考单据的单据编号",
						"参考项目的项目号",
						"<span style='color:blue'>备注<span>"
					],
					colModel : [ {
						name : "VBELN",
						index : "VBELN",
						width: 100
					}, {
						name : "POSNR",
						index : "POSNR",
						width: 80
					}, {
						name : "BWART",
						index : "BWART",
						width: 80
					}, {
						name : "MATNR",
						index : "MATNR",
						width: 110
					}, {
						name : "ARKTX",
						index : "ARKTX",
						width: 250
					}, {
						name : "MEINS",
						inde : "MEINS",
						width: 80
					}, {
						name : "LFIMG",
						index : "LFIMG",
						editable : true,
						width: 80,
						editrules:{number:true}
					}, {
						name : "REQ_QTY",
						index : "REQ_QTY",
						width: 80,
						editable : true,
						editrules:{number:true}
					}, {
						name : "LGORT",
						index : "LGORT",
						editable : true,
						width: 80,
						formatter:function(val, obj, row, act) {
							if(vm.lgortList==null || vm.lgortList===''){
								return '';
							}else{
								return val;
							}
						}
					}, {
						name : "LIFNR",//供应商?
						index : "LIFNR",
						editable : true,
						width: 80
					},{
						name:"INSMK",//特殊库存类型?
						index:"INSMK",
						width: 100
					},{
						name:"TOTAL_STOCK_QTY",//库存?
						index:"TOTAL_STOCK_QTY",
						width: 80
					},{
						name:"MEINS",//基本单位
						index:"MEINS",
						width: 80
					},{
						name:"EXPECT_BOX_QTY",//预计箱数?
						index:"EXPECT_BOX_QTY",
						editable : true,
						sortable:false
					},{
						name:"",//客户订单?
						index:"",
						width: 80
					},{
						name:"VGBEL",//采购订单号
						index:"VGBEL",
						width: 100
					},{
						name:"VGPOS",//行项目号
						index:"VGPOS",
						width: 80,
						formatter:function(val, obj, row, act) {
							if (val == "000000") {
								return "";
							}else{
								return val;
							}

						}
					},{
						name:"",//客户料号?
						index:"",
						width: 80
					},
						{
							name : 'VGBEL',
							align : 'center',
							editable:true,
							width: 100
						},
						{
							name : 'VGPOS',
							align : 'center',
							editable:true,
							width: 100,
							formatter:function(val, obj, row, act) {
								if (val == "000000") {
									return "";
								}else{
									return val;
								}

							}
						},{
						name:"MEMO",//备注
						index:"MEMO",
						editable:true,
						width: 100
					}],
					cellEdit : true,
					cellurl : '#',
					cellsubmit : 'clientArray',
					viewrecords: true,
					autowidth:true,
					shrinkToFit:false,
					autoScroll: true,
					multiselect: true,
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
					}
				});
		},
		init7_1:function(){ //601
			this.updateStr();
			//this.aceptLgortList = "00ZT";
			$("#links").attr("style","display:block;");//显示div
			//SAP销售订单发货
			//console.log("init7");
			this.vaildateUrl = baseUrl + "out/outreq/validateOutItems7";
			this.queryUrl = baseUrl + "out/outreq/queryOutItems7";
			this.createUrl = baseUrl + "out/outreq/outReqCreate7";
			this.reLoadGrid();
			$("#dataGrid").dataGrid(
				{
					datatype : "local",
					colNames : [
						"SAP交货单",
						"行项目",
						"移动类型",
						"<span style='color:red'>* </span>料号",
						"物料描述",
						"单位",
						"<span style='color:blue'>交货数量<span>",
						"<span style='color:blue'>需求数量<span>",
						"<span style='color:blue'>库位<span>",
						"<span style='color:blue'>供应商代码<span>",
						"特殊库存类型",
						"库存",
						"基本单位",
						'<span style="color:blue">预计箱数<span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="vm.fillDown(\'EXPECT_BOX_QTY\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
						"客户订单",
						"销售订单号",
						"行项目号",
						"客户料号",
						"参考单据的单据编号",
						"参考项目的项目号",
						"<span style='color:blue'>备注<span>"
					],
					colModel : [ {
						name : "VBELN",
						index : "VBELN",
						width: 100
					}, {
						name : "POSNR",
						index : "POSNR",
						width: 80
					}, {
						name : "BWART",
						index : "BWART",
						width: 80
					}, {
						name : "MATNR",
						index : "MATNR",
						width: 110
					}, {
						name : "ARKTX",
						index : "ARKTX",
						width: 250
					}, {
						name : "MEINS",
						inde : "MEINS",
						width: 80
					}, {
						name : "LFIMG",
						index : "LFIMG",
						editable : true,
						width: 80,
						editrules:{number:true}
					}, {
						name : "REQ_QTY",
						index : "REQ_QTY",
						width: 80,
						editable : true,
						editrules:{number:true}
					}, {
						name : "LGORT",
						index : "LGORT",
						editable : true,
						width: 80,
						formatter:function(val, obj, row, act) {
							if(vm.lgortList==null || vm.lgortList===''){
								return val;
							}else{
								return vm.lgortList;
							}
						}
					}, {//fixbug1644 sjk 20191107
						name : "LIFNR",//供应商?
						index : "LIFNR",
						editable : true,
						width: 80
					},{
						name:"SOBKZ",//特殊库存类型?
						index:"SOBKZ",
						width: 100
					},{
						name:"TOTAL_STOCK_QTY",//库存?
						index:"TOTAL_STOCK_QTY",
						width: 80
					},{
						name:"MEINS",//基本单位
						index:"MEINS",
						width: 80
					},{
						name:"EXPECT_BOX_QTY",//预计箱数?
						index:"EXPECT_BOX_QTY",
						editable : true,
						sortable:false
					},{
						name:"",//客户订单?
						index:"",
						width: 80
					},{
						name:"VBELV",//销售订单号
						index:"VBELV",
						width: 100
					},{
						name:"POSNV",//行项目号
						index:"POSNV",
						width: 80,
						formatter:function(val, obj, row, act) {
							if (val === "000000") {
								return "";
							}else{
								return val;
							}

						}
					},{
						name:"",//客户料号?
						index:"",
						width: 80
					},
						{
							name : 'VGBEL',
							align : 'center',
							editable:true,
							width: 100
						},
						{
							name : 'VGPOS',
							align : 'center',
							editable:true,
							width: 100,
							formatter:function(val, obj, row, act) {
								if (val === "000000") {
									return "";
								}else{
									return val;
								}

							}
						},{
							name:"MEMO",//备注
							index:"MEMO",
							editable:true,
							width: 100
						}],
					cellEdit : true,
					cellurl : '#',
					cellsubmit : 'clientArray',
					viewrecords: true,
					autowidth:true,
					shrinkToFit:false,
					autoScroll: true,
					multiselect: true,
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
					}
				});
		},
		init8:function(){
			this.updateStr();
			$("#links").attr("style","display:block;");//显示div
			//UB转储单出库需求
			//console.log("init8");
			this.vaildateUrl = baseUrl +"out/outreq/validateOutItem8";
			this.queryUrl = baseUrl +"out/outreq/queryOutItem8";
			this.createUrl = baseUrl + "out/outreq/outReqCreate8";
			this.reLoadGrid();
			$("#dataGrid").dataGrid(
				{
					datatype : "local",
					colNames : [
						"UB转储单",
						"行项目",
						"料号",
						"物料描述",
						"采购单位",
						"订单数量",
						"已领数量",
						"可领数量",
						"<span style='color:blue'>需求数量<span>",
						"<span style='color:blue'>库位<span>",
						"<span style='color:blue'>供应商代码<span>",
						"库存",
						"基本单位",
					],
					colModel : [ {
						name : "EBELN",
						index : "EBELN",
						width: 100
					}, {
						name : "EBELP",
						index : "EBELP",
						width: 80
					}, {
						name : "MATNR",
						index : "MATNR",
						width: 110
					}, {
						name : "TXZ01",
						index : "TXZ01",
						width: 250
					}, {
						name : "MEINS",
						index : "MEINS",
						width: 80
					}, {
						name : "MENGE",
						inde : "MENGE",
						width: 80
					}, {
						name : "RECEIVED_QTY",//已领数量
						index : "RECEIVED_QTY",
						width: 80
					}, {
						name : "AVAILABLE_QTY",//可领数量
						index : "AVAILABLE_QTY",
						width: 80,
					}, {
						name : "REQ_QTY",//需求数量
						index : "REQ_QTY",
						editable : true,
						width: 80
					}, {
						name : "LGORT",//库位
						index : "LGORT",
						editable : true,
						width: 80,
						formatter:function(val, obj, row, act) {
							if(vm.lgortList==null || vm.lgortList===''){
								return '';
							}else{
								return val;
							}
						}
                        },{//fixbug1644 sjk 20191107
						name:"LIFNR",//供应商代码
						index:"LIFNR",
						editable : true,
						width: 100
					},{
						name:"TOTAL_STOCK_QTY",//库存
						index:"TOTAL_STOCK_QTY",
						width: 80
					},{
						name:"LMEIN",//基本单位
						index:"LMEIN",
						width: 80
					}],
					cellEdit : true,
					cellurl : '#',
					cellsubmit : 'clientArray',
					viewrecords: true,
					autowidth:true,
					shrinkToFit:false,
					autoScroll: true,
					multiselect: true,
					beforeSelectRow : function(rowid, e) { // 点击复选框才选中行
						var $myGrid = $(this),
							i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),
							cm = $myGrid.jqGrid('getGridParam', 'colModel');
						if (cm[i].name == 'cb') {
							$('#dataGrid').jqGrid('setSelection', rowid);
						}
						return (cm[i].name == 'cb');
					},beforeEditCell:function(rowid, cellname, v, iRow, iCol){
						//编辑之前先保存当前编辑的行号列号
						vm.lastrow=iRow;
						vm.lastcell=iCol;
						vm.lastcellname=cellname;
					},
					gridComplete: function() {
						var ids = $("#dataGrid").getDataIDs();
						for(var i=0;i<ids.length;i++){
							var gridData= $("#dataGrid").jqGrid("setCell",ids[i],'LGORT',vm.lgortList);//这是获得 某一行的数据
						}
                    }
				});
		},
		init10:function(){
			this.updateStr();
			$("#links").attr("style","display:block;");//显示div
			//外部销售发货
			//console.log("init10");
			this.vaildateUrl = baseUrl +"out/outreq/validateOutItem10";
			this.queryUrl = baseUrl +"out/outreq/queryOutItem10";
			this.createUrl = baseUrl + "out/outreq/outReqCreate10";
			this.reLoadGrid();
			$("#dataGrid").dataGrid(
				{
					datatype : "local",
					colNames : [
						"采购单号",
						"行项目",
						"料号",
						"物料描述",
						"采购单位",
						"订单数量",
						"已领数量",
						"可领数量",
						"<span style='color:blue'>需求数量<span>",
						'<span style="color:blue">预计箱数<span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="vm.fillDown(\'EXPECT_BOX_QTY\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
						"<span style='color:blue'>库位<span>",
						"<span style='color:blue'>供应商代码<span>",
						"库存",
						"基本单位",
						"<span style='color:blue'>备注<span>"
					],
					colModel : [ {
						name : "EBELN",
						index : "EBELN",
						width: 100,
						editable:true,
					}, {
						name : "EBELP",
						index : "EBELP",
						width: 80,
						editable:true,
					}, {
						name : "MATNR",
						index : "MATNR",
						width: 110,
						editable:true,
					}, {
						name : "TXZ01",
						index : "TXZ01",
						width: 250,
						editable:true,
					}, {
						name : "MEINS",
						index : "MEINS",
						width: 80,
						editable:true,
					}, {
						name : "MENGE",
						inde : "MENGE",
						width: 80,
						editable:true,
					}, {
						name : "RECEIVED_QTY",//已领数量
						index : "RECEIVED_QTY",
						width: 80,
						editable:true,
					}, {
						name : "AVAILABLE_QTY",//可领数量
						index : "AVAILABLE_QTY",
						width: 80,
						editable:true,
					}, {
						name : "REQ_QTY",//需求数量
						index : "REQ_QTY",
						editable : true,
						width: 80
					}, {
						name : "EXPECT_BOX_QTY",//预计箱数
						index : "EXPECT_BOX_QTY",
						editable : true,
						width: 90,
						sortable:false
						/*formatter:function(val, obj, row, act){
                            if(val === null || val === undefined || val === '')
                                val = '0';

                            /!*if(val === '0' || val < row.REQ_QTY)
                               return "<div style='background-color:yellow;'>" + val +"</div>";

                            else *!/
                            return val;
                        }*/

					}, {
						name : "LGORT",//库位
						index : "LGORT",
						editable : true,
						width: 80
					},{//fixbug1644 sjk 20191107
						name:"LIFNR",//供应商代码
						index:"LIFNR",
						editable : true,
						width: 100
					},{
						name:"TOTAL_STOCK_QTY",//库存
						index:"TOTAL_STOCK_QTY",
						width: 80,
						editable:true,
					},{
						name:"LMEIN",//基本单位
						index:"LMEIN",
						width: 80,
						editable:true,
					},{
						name:"MEMO",//备注
						index:"MEMO",
						width:150,
						editable : true,
					}],
					cellEdit : true,
					cellurl : '#',
					cellsubmit : 'clientArray',
					viewrecords: true,
					autowidth:true,
					shrinkToFit:false,
					autoScroll: true,
					multiselect: true,
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
					beforeSaveCell:function(rowid,celname,value,iRow,iCol){
						//输入值保存前
						//所有需要关联更新的代码 写在这里
						var LIFNR = $("#dataGrid").getCell(iRow-1,"LIFNR");
						var LGORT =  $("#dataGrid").getCell(iRow-1,"LGORT");

						if(celname === 'MATNR'){
							vm.queryMatInfo(value,function(matinfo){
								//iRow-1=rowId
								//设置物料描述，物料单位
								$("#dataGrid").jqGrid('setCell',iRow-1,"MAKTX",matinfo.MAKTX);
								$("#dataGrid").jqGrid('setCell',iRow-1,"TXZ01",matinfo.MAKTX);
								$("#dataGrid").jqGrid('setCell',iRow-1,"MEINS",matinfo.MEINH);
								//更新库存
								vm.reloadstock(LGORT,LIFNR,iRow-1);

							});
						}
						/*							if(celname==='EXPECT_BOX_QTY'){
                                                        alert('asdasdas '+value);
                                                    }*/
						/*else if(celname === "REQ_QTY"){
                            //更新需求数量，更新库存的样式
                            vm.stockStyle(iRow-1,value);
                        }
                        else if(celname === 'LIFNR' || celname === 'LGORT'){
                            if(celname === 'LIFNR'){
                                LIFNR = value;
                            }
                            if(celname === 'LGORT'){
                                LGORT = value;
                            }
                            vm.reloadstock(LGORT,LIFNR,iRow-1);
                        }*/
					}
				});
		},
		init11:function(){
			this.updateStr();
			$("#links").attr("style","display:block;");//显示div
			//工厂间调拨
			//console.log("init11");
			this.createUrl = baseUrl + "out/outreq/createOutReq11";
			//this.checkMaterialHxFlag = true;
			this.items = [];
			this.reLoadGrid();
			//与成本中心的表格差不多,可以重用
			this.init4_grid();
			//多了一个预计箱数默认是隐藏的，设置为可见
			$("#dataGrid").jqGrid('showCol', [ "BOX_COUNT"]);
		},
		init12:function(){
			this.updateStr();
			$("#links").attr("style","display:block;");//显示div
			//WBS元素发货（221）
			//console.log("init12");
			this.createUrl = baseUrl + "out/wbsElement/save";
			this.items = [];
			this.reLoadGrid();
			this.init4_grid();//与成本中心一样
		},
		init13:function(){
			this.updateStr();
			$("#links").attr("style","display:block;");//显示div
			//库存地点调拨311—类型不转移
			//console.log("init13");
			this.createUrl = baseUrl +"out/outreq/createOutReq13";
			this.items = [];
			//重新加载表格
			this.reLoadGrid();
			//初始化jqGrid,与成本中心一样
			this.init4_grid();
		},
		init20:function(){
			this.updateStr();
			this.aceptLgortList = "00ZT";
			$("#links").attr("style","display:block;");//显示div
			//
			//console.log("init20");
			this.createUrl = baseUrl +"out/outreq/createOutReq20";
			this.items = [];
			//重新加载表格
			this.reLoadGrid();
			//初始化jqGrid,与成本中心一样
			this.init4_grid();
		},
		init16:function(){
			this.updateStr();
			$("#links").attr("style","display:block;");//显示div
			//工厂间调拨发货（A303）
			//console.log("init16");
			vm.checkMaterialHx();
			/*this.checkMaterialHxFlag = true;
			if(vm.checkMaterialHxFlag)
				vm.checkMaterialHx(value);*/
			this.vaildateUrl = baseUrl + "out/outreq/validate16";
			this.queryUrl = baseUrl + "out/outreq/query16";
			this.createUrl = baseUrl + "out/outreq/create16";
			this.items = [];
			//重新加载表格
			this.reLoadGrid();
			$("#dataGrid").dataGrid(
				{
					datatype : "local",
					colNames : [
						"<span style='color:red'>* </span>凭证号",
						"行项目号",
						"<span style='color:red'>* </span>料号",
						"物料描述",
						"单位",
						"凭证数量",
						"可领数量",
						"<span style='color:red'>* </span><span style='color:blue'>需求数量<span>",
						"<span style='color:blue'>供应商代码<span>",
						"<span style='color:blue'>库位<span>",
						"库存",
						"接收工厂",
						"<span style='color:blue'>备注<span>"
					],
					colModel : [ {
						name : "SAP_MATDOC_NO",
						index : "SAP_MATDOC_NO",
						width: 100
					}, {
						name : "SAP_MATDOC_ITEM_NO",
						index : "SAP_MATDOC_ITEM_NO",
						width: 80
					}, {
						name : "MATNR",
						index : "MATNR",
						width: 110
					}, {
						name : "MAKTX",
						index : "MAKTX",
						width: 250
					}, {
						name : "MEINS",
						index : "MEINS",
						width: 80
					}, {
						name : "XF303",//凭证数量
						inde : "XF303",
						width: 80
					}, {
						name : "HX_QTY_XF",//可领数量
						index : "HX_QTY_XF",
						width: 80
					},  {
						name : "REQ_QTY",//需求数量
						index : "REQ_QTY",
						editable : true,
						width: 80
					},
						{
							name:"LIFNR",//供应商代码
							index:"LIFNR",
							editable : true,
							width: 100
						}, {
							name : "LGORT",//库位
							index : "LGORT",
							editable : true,
							width: 80
						},{
							name:"TOTAL_STOCK_QTY",//库存
							index:"TOTAL_STOCK_QTY",
							width: 80
						},{
							name:"WERKS",//接收工厂
							index:"WERKS",
							width: 80
						},{
							name:"MEMO",//备注
							index:"MEMO",
							width:150,
							editable : true,
						}],
					cellEdit : true,
					cellurl : '#',
					cellsubmit : 'clientArray',
					viewrecords: true,
					autowidth:true,
					shrinkToFit:false,
					autoScroll: true,
					multiselect: true,
					beforeSelectRow : function(rowid, e) { // 点击复选框才选中行
						var $myGrid = $(this),
							i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),
							cm = $myGrid.jqGrid('getGridParam', 'colModel');
						if (cm[i].name == 'cb') {
							$('#dataGrid').jqGrid('setSelection', rowid);
						}
						return (cm[i].name == 'cb');
					},
					afterEditCell:function(rowid, cellname, v, iRow, iCol){
						//供应商模糊查询 & 库存选择弹出框
						var LIFNR = $("#dataGrid").getCell(iRow-1,"LIFNR");
						var LGORT =  $("#dataGrid").getCell(iRow-1,"LGORT");
						if(cellname === 'LIFNR'){
							getVendorNoSelect($("#"+iRow+"_"+cellname),null,function(rep){
								//供应商变化,更新库存
								vm.reloadstock(LGORT,rep.LIFNR);
							});
						}

						if(cellname === "LGORT"){
							vm.openLgortSelectWindow(function(lgortList){
								//更新库存单元格
								$('#dataGrid').jqGrid("saveCell", iRow, iCol);
								$("#dataGrid").jqGrid('setCell',rowid, cellname, lgortList,'editable-cell');
								//库存变化，更新库存
								vm.reloadstock(lgortList,LIFNR);
							});
						}
					}

				});
		},
		init17:function(){
			this.updateStr();
			$("#links").attr("style","display:block;");//显示div
			//console.log("init17");
			vm.checkMaterialHx();
			//$("#links").attr("style","display:none;");
			//服务端地址
			this.vaildateUrl = baseUrl + "out/outreq/validate17";
			this.queryUrl = baseUrl + "out/outreq/query17";
			this.createUrl = baseUrl + "out/outreq/create17";
			this.items = [];
			//重新加载表格
			this.reLoadGrid();
			$("#dataGrid").dataGrid(
				{
					datatype : "local",
					colNames : [
						"<span style='color:red'>* </span>SAP交货单",
						"行项目号",
						"采购订单号",
						"<span style='color:red'>* </span>料号",
						"物料描述",
						"单位",
						"交货数量",
						"可领数量",
						"<span style='color:red'>* </span><span style='color:blue'>需求数量<span>",
						"<span style='color:blue'>供应商代码<span>",
						"<span style='color:blue'>库位<span>",
						"库存",
						"基本单位",
						'<span style="color:blue">预计箱数<span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="vm.fillDown(\'BOX_COUNT\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
						"<span style='color:blue'>备注<span>"
					],
					colModel : [ {
						name : "VBELN",
						index : "VBELN",
						width: 100,
						align:'center'
					}, {
						name : "POSNR",
						index : "POSNR",
						width: 80,
						align:'center'
					},
						{
							name : "EBELN",
							index : "EBELN",
							width: 80,
							align:'center'
						},
						{
							name : "MATNR",
							index : "MATNR",
							width: 100,
							align:'center'
						}, {
							name : "MAKTX",
							index : "MAKTX",
							width: 250,
							align:'center'
						}, {
							name : "UNIT",
							index : "UNIT",
							width: 80,
							align:'center'
						}, {
							name : "LFIMG",
							inde : "LFIMG",
							width: 80,
							align:'center'
						}, {
							name : "HX_QTY_XF",//可领数量
							index : "HX_QTY_XF",
							width: 80,
							align:'center'
						},  {
							name : "REQ_QTY",//需求数量
							index : "REQ_QTY",
							editable : true,
							width: 80,
							align:'center'
						},
						{
							name:"LIFNR",//供应商代码
							index:"LIFNR",
							editable : true,
							width: 100,
							align:'center'
						}, {
							name : "LGORT",//库位
							index : "LGORT",
							editable : true,
							width: 80,
							align:'center'
						},{
							name:"TOTAL_STOCK_QTY",//库存
							index:"TOTAL_STOCK_QTY",
							width: 80,
							align:'center'
						},
						{
							name:"MEINS",
							index:"MEINS",
							width: 80,
							align:'center'
						},
						{
							name:"BOX_COUNT",
							index:"BOX_COUNT",
							width: 90,
							editable:true,
							align:'center',
							sortable:false
						}
						,{
							name:"MEMO",//备注
							index:"MEMO",
							width:150,
							editable : true,
							align:'center'
						}],
					cellEdit : true,
					cellurl : '#',
					cellsubmit : 'clientArray',
					viewrecords: true,
					autowidth:true,
					shrinkToFit:false,
					autoScroll: true,
					multiselect: true,
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
					afterEditCell:function(rowid, cellname, v, iRow, iCol){
						//供应商模糊查询 & 库存选择弹出框
						var LIFNR = $("#dataGrid").getCell(iRow-1,"LIFNR");
						var LGORT =  $("#dataGrid").getCell(iRow-1,"LGORT");
						if(cellname === 'LIFNR'){
							getVendorNoSelect($("#"+iRow+"_"+cellname),null,function(rep){
								//供应商变化,更新库存
								vm.reloadstock(LGORT,rep.LIFNR);
							});
						}

						if(cellname === "LGORT"){
							vm.openLgortSelectWindow(function(lgortList){
								//更新库存单元格
								$('#dataGrid').jqGrid("saveCell", iRow, iCol);
								$("#dataGrid").jqGrid('setCell',rowid, cellname, lgortList,'editable-cell');
								//库存变化，更新库存
								vm.reloadstock(lgortList,LIFNR);
							});
						}
					}
				});
		},
		init18:function(){
			this.updateStr();
			$("#links").attr("style","display:block;");//显示div
			//511报废
			//console.log("init18");
			this.createUrl = baseUrl +"out/outreq/createOutReq18";
			//baseUrl + "/out/createRequirement/costcenterReq";
			this.items = [];
			//重新加载表格
			this.reLoadGrid();
			//初始化jqGrid,与成本中心一样
			this.init4_grid();
		},

		init19:function(){
			this.updateStr();
			$("#links").attr("style","display:block;");//显示div
			//扫描看板卡领料311
			//console.log("init19");
			this.queryUrl = baseUrl +"out/outreq/queryOutReqPda311";
			this.createUrl = baseUrl +"out/outreq/createOutReqPda311";
			this.items = [];
			//重新加载表格
			this.reLoadGrid();
			$("#dataGrid").dataGrid(
				{
					datatype : "local",
					colNames : [
						"物料号",
						"物料描述",
						"数量",
						"单位",
						"配送工位",
						"配送地址",
					],
					colModel : [ {
						name : "MATNR",
						index : "MATNR",
						width: 100,
						align:'center'
					}, {
						name : "MAKTX",
						index : "MAKTX",
						width: 250,
						align:'center'
					}, {
						name : "REQ_QTY",
						index : "REQ_QTY",
						width: 80,
						editable : true,
						align:'center'
					}, {
						name : "UNIT",
						index : "UNIT",
						width: 100,
						align:'center'
					}, {
						name : "DIS_STATION",
						index : "DIS_STATION",
						width: 150,
						align:'center'
					}, {
						name : "DIS_ADDRSS",
						inde : "DIS_ADDRSS",
						width: 80,
						align:'center'
					}],
					cellEdit : true,
					cellurl : '#',
					cellsubmit : 'clientArray',
					viewrecords: true,
					//autowidth:true,
					//shrinkToFit:false,
					// autoScroll: true,
					multiselect: true,
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
					}
				});
		},

		init21:function(){
			this.updateStr();
			$("#links").attr("style","display:block;");//显示div
			//工厂间调拨301（总装）
			//console.log("init21");
			this.vaildateUrl = baseUrl + "out/outreq/validate21";
			this.queryUrl = baseUrl +"out/outreq/query21";
			this.createUrl = baseUrl +"out/outreq/createOutReq21";
			this.items = [];
			//重新加载表格
			this.reLoadGrid();
			$("#dataGrid").dataGrid(
				{
					datatype : "local",
					colNames : [
						"物料号",
						"物料描述",
						"需求数量",
						"单位",
						"库位",
						"供应商",
						"配送单号",
						"行项目",
						"需求号",
						"配送模式",
						"排序号",
						"工位",
						"储位",
						"产线",
						"配送路线",
						"上线路线",
						"最晚配送时间",
						"最晚需求时间",
						"最小包装",
					],
					colModel : [ {
						name : "MATNR",
						index : "MATNR",
						width: 100,
						align:'center'
					}, {
						name : "MAKTX",
						index : "MAKTX",
						width: 250,
						align:'center'
					}, {
						name : "QTY",
						index : "QTY",
						width: 70,
//							editable : true,
						align:'center'
					}, {
						name : "MEINS",
						index : "MEINS",
						width: 60,
						align:'center'
					}, {
						name : "LGORT",
						index : "LGORT",
						width: 60,
						align:'center'
					}, {
						name : "LIFNR",
						inde : "LIFNR",
						width: 70,
						align:'center'
					}, {
						name : "DELIVERY_NO",
						inde : "DELIVERY_NO",
						width: 100,
						align:'center'
					}, {
						name : "DLV_ITEM",
						inde : "DLV_ITEM",
						width: 60,
						align:'center'
					}, {
						name : "KANBAN_NO",
						inde : "KANBAN_NO",
						width: 100,
						align:'center'
					}, {
						name : "DELIVERY_TYPE",
						inde : "DELIVERY_TYPE",
						width: 80,
						align:'center'
					}, {
						name : "SORT_VALUE",
						inde : "SORT_VALUE",
						width: 80,
						align:'center'
					}, {
						name : "POINT_OF_USE",
						inde : "POINT_OF_USE",
						width: 80,
						align:'center'
					}, {
						name : "POU",
						inde : "POU",
						width: 80,
						align:'center'
					}, {
						name : "LINE_NO",
						inde : "LINE_NO",
						width: 80,
						align:'center'
					}, {
						name : "DELIVERY_ROUTE",
						inde : "DELIVERY_ROUTE",
						width: 80,
						align:'center'
					}, {
						name : "LINE_FEEDING_ROUTE",
						inde : "LINE_FEEDING_ROUTE",
						width: 80,
						align:'center'
					}, {
						name : "START_SHIPPING_TIME",
						inde : "START_SHIPPING_TIME",
						width: 100,
						align:'center'
					}, {
						name : "DEMAND_TIME",
						inde : "DEMAND_TIME",
						width: 100,
						align:'center'
					}, {
						name : "DOSAGE",
						inde : "DOSAGE",
						width: 80,
						align:'center'
					}],
					cellEdit : true,
					cellurl : '#',
					cellsubmit : 'clientArray',
					viewrecords: true,
					autowidth:true,
					shrinkToFit:false,
					autoScroll: true,
					multiselect: true,
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
					}
				});
		},

		search:function(){
			$('#dataGrid').jqGrid("saveCell", vm.lastrow, vm.lastcell);
			$("#dataGrid").find("tbody tr").not(".jqgfirstrow").each(function (i) {
				var REQ_QTY = $(this).find('[aria-describedby="dataGrid_REQ_QTY"]').text();
				vm.items[i].REQ_QTY= REQ_QTY
			});
			if(this.barcode===''||this.barcode===undefined||this.barcode===null)
				return
			var data={
				'matnr' : this.barcode,
				'werks' : $("#werks").val()
			}
			var item = null
			$.ajax({
				async: false,
				url : vm.queryUrl,
				type : "POST",
				contentType: "application/json;charset=utf-8",
				data:JSON.stringify(data),
				success : function(resp) {
					if(resp.data.length>0)
						item = resp.data[0]
				}
			})
			if(item){
			var data1={
				'matnr' : item.MATNR,
			}
			$.ajax({
				url : baseUrl + "out/outreq/queryUNIT",
				type : "POST",
				contentType: "application/json;charset=utf-8",
				data:JSON.stringify(data1),
				success : function(resp) {
					if(resp.data[0]&&resp.data[0].MEINS)
						item.UNIT=resp.data[0].MEINS
					else
						item.UNIT=''
					if(vm.items.length===0){
						vm.items.push(JSON.parse(JSON.stringify(item)))
					}else{
						var indexList = [];
						vm.items.forEach(ele => {
							indexList.push(ele.MATNR);
					});
						var i = indexList.indexOf(item.MATNR);
						if (i == -1) {
							if(vm.items[0].DIS_ADDRSS===item.DIS_ADDRSS)
								vm.items.push(JSON.parse(JSON.stringify(item)));
							else
								alert('配送地址不一致')
						} else {
							var cellValue=$("#dataGrid").getCell(i, "REQ_QTY")
							vm.items[i].REQ_QTY = (Number)(cellValue)+(Number)(item.REQ_QTY)
						}
					}
					$("#dataGrid").dataGrid("clearGridData");
					for (var i = 0; i < vm.items.length; i++) {
						$("#dataGrid").dataGrid('addRowData', i, vm.items[i]);
					}
				}

			})}
		},
		removePda311:function(){
			var ids=$("#dataGrid").getGridParam("selarrrow");
			$("#dataGrid").jqGrid("delGridRow",ids);
			ids.sort(function(a, b) { return b - a});
			ids.forEach(function(index) { vm.items.splice(index, 1) })
		},
		getGridSelectedItems:function(all){
			//获取选中的数据
			var s  = $("#dataGrid").jqGrid('getGridParam','selarrrow');
			if(all){
				s = $("#dataGrid").getDataIDs();
			}
			var items = [];
			//获取行数据
			for(var i=0;i<s.length;i++){
				var rowData = $("#dataGrid").jqGrid('getRowData',s[i]);
				items[i] = rowData;
			}
			return items;
		},
		reLoadGrid:function(){
			//JqGrid重新加载

			//清空原来表格的数据
			//创建一个table元素，追加到div
			//重新生成表格
			this.items = [];
			$("#dataGridDiv").empty();
			var jqgrid = document.createElement('table');
			jqgrid.setAttribute('id','dataGrid');
			$("#dataGridDiv").append(jqgrid);
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
				data:{
					"WERKS":plantCode,
					"MENU_KEY":"A91"
				},
				success:function(resp){
					vm.warehourse = resp.data;
					if(resp.data.length>0){
						vm.whNumber=resp.data[0].WH_NUMBER
					}
				}
			})

			this.businessList=getBusinessList("07","A91",plantCode);
			if(undefined == this.businessList || this.businessList.length<=0){
				js.showErrorMessage("工厂未配置需求业务类型！");
			}else{
				this.requireTypes=this.businessList[0].CODE;
			}
		},
		OnRequireTypes:function(event){
			//出库需求业务类型
			var requireType = event.target.value;
			switch(requireType){
				case '41':this.init1();break;//生产订单领料
				case '42':this.init2();break;//生产订单补料
				case '48':this.init3();break;//线边仓
				case '44':this.init4();break;//成本中心
				case '43':this.init5();break;//内部订单
				case '49': this.init6();break;//委外订单
				case '50': this.init7();break;//STO销售发货(311)
				case '51': this.init7_1();break;//销售订单发货(601)
				case '64':this.init8();break;//UB转储单出库需求
				case '52':this.init10();break;//外部销售发货（251）
				case '46':this.init11();break;//工厂间调拨
				case '45':this.init12();break;//wbs元素
				case '47':this.init13();break;//库存地点调拨311—类型不转移
				case '53':this.init16();break;//工厂间调拨发货（A303）
				case '54':this.init17();break;//SAP交货单销售发货（A311T）
				case '73':this.init18();break;//报废（551）
				case '76':this.init19();break;//扫描看板卡领料311
				case '75':this.init20();break;//STO一步联动发货311
				case '77':this.init21();break;//工厂间调拨301（总装）
			}
		}

		,
		produceOrder : function() {
			//查询创建出库需求项目

			var title,type,vaildateUrl,queryUrl;//标题，业务类型,校验地址，查询地址
			vaildateUrl = this.vaildateUrl;
			queryUrl = this.queryUrl;
			var width ="60%";
			var height = "50%";
			var acceptPlant;
			var lgortList = this.lgortList;
			if(this.requireTypes == '41' || this.requireTypes == '42' || this.requireTypes == '48'){
				title = "生产订单";
				type='1';
			}
			if(this.requireTypes ==='49'){
				title = 'SAP采购订单';
				type = '2';
				//console.log("--->"+vaildateUrl);
			}

			if(this.requireTypes ==='50' || this.requireTypes ==='51'|| this.requireTypes ==='54'){
				title = 'SAP交货单';
				type = '3';
				width = '30%';
				//console.log("--->"+vaildateUrl);
			}

			if(this.requireTypes ==='50'){
				lgortList = this.lgortList;
			}

			if(this.requireTypes ==='64'){
				title = '采购订单';
				type = '4';
				acceptPlant = this.acceptPlant;//接收工厂

			}
			if(this.requireTypes ==='52'){
				title="采购订单";
				type='4';
				//...
			}

			if(this.requireTypes === '53'){
				title="凭证号";
				type='5';
				acceptPlant = this.acceptPlant;
			}

			if(this.requireTypes ==='77'){
//				if (isEmpty(vm.acceptPlant)) {
//					alert("接收工厂不能为空！")
//					return;
//				}
//				if (isEmpty(vm.aceptLgortList)) {
//					alert("接收库位不能为空！")
//					return;
//				}
				title = "总装配送单";
				type = '6';
				width = '30%'
			}

			js.layer.open({
				type : 2,
				title : title,
				area : [ width, height],
				content : baseUrl+ "wms/out/out_req_order_query.html",
				btn : [ "确定", "取消" ],
				success : function(layero, index) {
					var win = layero.find('iframe')[0].contentWindow;
					//初始化参数
					win.vm.init($("#werks").val(), $("#whNumber").val(),type,acceptPlant,vm.deliveryType,lgortList);
				},
				yes : function(index, layero) {
					var win = layero.find('iframe')[0].contentWindow;
					win.vm.submit(function(result, msg) {
						if (result) {
							// 校验成功，根据订单信息查询数据
							win.vm.query(function(response) {
								// 处理返回结果
								vm.items = response;
								//非上线物料过滤逻辑
								vm.filterMatUsing(function(filterItem){
									vm.initGrid(filterItem);
								});
								win.vm.close();
							},queryUrl);
						} else {
							//构建错误信息
							var msgList = msg.split('\n');
							var msgHtml = "";
							for ( var i in msgList) {
								msgHtml += "<p>" + msgList[i]
									+ "</p>";
							}
							alert(msgHtml);
						}
					},vaildateUrl);
				},
				no : function(index, layero) {
				}
			});
		},
		filterMatUsing:function(callback){
			//过滤非上线物料
			var items = this.items;
			if(this.filterUseMaterials === '1' && (this.requireTypes === '41' || this.requireTypes === '42' || this.requireTypes === '48')){
				//过滤
				var werks = $("#werks").val();
				//查询需要过滤的物料
				$.ajax({
					url:baseUrl +"out/createRequirement/matuseing/"+werks,
					success:function(resp){
						var matUseingList = resp.data;
						//过滤
						var filterItems = items.filter(function(val){
							for(var i=0;i<matUseingList.length;i++){
								if(matUseingList[i].MATNR === val.MATNR){
									return false
								}
							}
							return true;
						});
						callback(filterItems)
					}
				});
			}
			else{
				//不过滤，直接刷新表格
				callback(items);
			}
		},
		initGrid:function(_items,append){
			var items = this.items;
			if(_items != undefined && _items != null)
				items = _items;
			if(!append){
				//默认追加
				var oldList = vm.getGridSelectedItems(true);
				//过滤掉已经存在的记录
				items  = items.filter(function(val){
					for(var i in oldList){
						if(oldList[i].MATNR === val.MATNR && oldList[i].AUFNR === val.AUFNR && (vm.requireTypes === '41' || vm.requireTypes === '42' || vm.requireTypes === '48')){
							//生产订单 线边仓 生产订单补料
							return false;
						}
						else if(vm.requireTypes === '53' && oldList[i].MATNR === val.MATNR &&  oldList[i].SAP_MATDOC_NO === val.SAP_MATDOC_NO && oldList[i].SAP_MATDOC_ITEM_NO === val.SAP_MATDOC_ITEM_NO){
							//工厂间调拨发货（A303）
							return false;
						}
					}
					return true;
				});
				var records = $('#dataGrid').jqGrid('getGridParam','records');
				//追加到后面 rowId 从records开始
				for (var i = 0; i < items.length; i++) {
					$("#dataGrid").dataGrid('addRowData', records+i, items[i]);
					if(vm.requireTypes === '41' || vm.requireTypes === '42' || vm.requireTypes === '48'){
						vm.stockStyle(records+i);
					}
				}
			}else{
				//重新加载
				$("#dataGrid").dataGrid("clearGridData");
				for (var i = 0; i < items.length; i++) {
					$("#dataGrid").dataGrid('addRowData', i, items[i]);
					if(vm.requireTypes === '41' || vm.requireTypes === '42' || vm.requireTypes === '48'){
						vm.stockStyle(i);
					}
				}
			}
		},
		stockStyle:function(rowid,_REQ_QTY){
			//根据条件更新库存样式,如果需求数量大于库存数量，背景设置为黄色
			var REQ_QTY = $("#dataGrid").getCell(rowid,"REQ_QTY");
			if(_REQ_QTY != null && _REQ_QTY != undefined){
				REQ_QTY = _REQ_QTY;
			}
			var TOTAL_STOCK_QTY = $("#dataGrid").getCell(rowid,"TOTAL_STOCK_QTY");
			if(REQ_QTY === undefined || REQ_QTY === null || REQ_QTY === '' || parseFloat(TOTAL_STOCK_QTY) < parseFloat(REQ_QTY)){
				$("#dataGrid").setCell(rowid,"TOTAL_STOCK_QTY",TOTAL_STOCK_QTY,{background :"yellow"});
			}else{
				$("#dataGrid").setCell(rowid,"TOTAL_STOCK_QTY",TOTAL_STOCK_QTY,{background:"white"});
			}
		},
		create : function(){
//			alert(vm.createUrl);
			//先保存正在编辑的单元格
			$('#dataGrid').jqGrid("saveCell", vm.lastrow, vm.lastcell);
			//先检查必填项等条件...
			var checkResp = vm.checkBeforeSave();
			if(checkResp !== true){
				js.showErrorMessage(checkResp);
				return ;
			}
			//获取选中的行项目
			var postData = vm.getGridSelectedItems();
			if(postData.length === 0){
				js.showErrorMessage("没有选择数据");
				return ;
			}

			//设置公用的属性
			console.info("save--->>>"+($("#werks").val()));
			for(var i in postData){
				postData[i].werks = $("#werks").val();
				postData[i].whNumber = $("#whNumber").val();
				postData[i].requireDate = this.requireDate;
				postData[i].requireTime = this.requireTime;
				postData[i].summaryMode = this.summaryMode;
				postData[i].use = this.use;
				postData[i].acceptPlant = this.acceptPlant.toUpperCase();
				postData[i].companyCode = this.compcode.toUpperCase();
				postData[i].costcenterCode = this.costcenter;
				postData[i].companyCode = this.compcode;
				postData[i].internalOrder = this.innerOrder;
				postData[i].autyp = this.innerOrder_AUTYP;
				postData[i].wbsElementNo =this.wbsElementNo;
				postData[i].aceptLgortList = this.aceptLgortList.toUpperCase();
				postData[i].sendLgortList = this.sendLgortList;
				postData[i].transportDay = this.transportDay;
				//
				//postData[i].bfyy = this.bfyy;
				postData[i].lgortList = this.lgortList;
				postData[i].transportType = this.transportType;
				postData[i].shipArea = this.shipArea;
				postData[i].acceptArea = this.acceptArea;

				postData[i].transportDay = this.transportDay;
				postData[i].priority = this.priority;
				postData[i].supermarket = this.supermarket;
				postData[i].w_vendor = this.w_vendor;
				postData[i].requireTypes = this.requireTypes;
				postData[i].costomerCode = this.costomerCode;
				postData[i].receiver = this.receiver;

			}
			for(var i in postData){
				if(this.requireTypes ==='77'){
					if(isEmpty(postData[i].QTY)){
						js.showErrorMessage("需求数量不能为空");
						return;
					}
					if (postData[i].QTY <= "0") {
		  				js.showErrorMessage("需求数量不能为0");
		  				return;
		  			}
					if(isEmpty(this.requireDate)){
						js.showErrorMessage("需求日期不能为空");
						return;
					}
				} else {
					if(isEmpty(postData[i].REQ_QTY)){
						js.showErrorMessage("需求数量不能为空");
						return;
					}
					if (postData[i].REQ_QTY <= "0") {
		  				js.showErrorMessage("需求数量不能为0");
		  				return;
		  			}
				}

				if((this.lgortList==null || this.lgortList==='') && isEmpty(postData[i].LGORT)){
					//console.info('postData[i].LGORTpostData[i].LGORTpostData[i].LGORT');
					postData[i].LGORT = '';
				}

				if(this.requireTypes==='49'){
					//console.info("postData[i].VENDOR "+postData[i].VENDOR);
					//console.info("vm.w_vendor "+vm.w_vendor);
//					if(postData[i].EBELN==null || postData[i].EBELN===''){
//
//					}else {
//						if(postData[i].VENDOR !== vm.w_vendor){
//							js.showErrorMessage("委外单位与PO供应商代码不一致");
//							return ;
//						}
//					}



				}

				if(this.requireTypes === '50' || this.requireTypes === '51' || this.requireTypes === '52' ){
					if(isEmpty(postData[i].EXPECT_BOX_QTY)){
						js.showErrorMessage("预计箱数不能为空！");
						return ;
					}

				}
				if(this.requireTypes === '46'){
					if(isEmpty(postData[i].BOX_COUNT)){
						js.showErrorMessage("预计箱数不能为空！");
						return ;
					}
				}

				if(this.requireTypes === '42'){
					if(isEmpty(postData[i].AUFNR)){
						js.showErrorMessage("生产订单号不能为空！");
						return ;
					}
				}

			}

			js.loading();
			//创建生产订单需求
			$.ajax({
				url:vm.createUrl,
				data:JSON.stringify(postData),
				type:"post",
				contentType:"application/json",
				success:function(resp){
					if(resp.code == '0'){
						vm.initGrid([],true);
						vm.items = []
						//js.showMessage("保存成功! 进仓单号  "+resp.ininternetbountNo,null,null,1000*60000);
//						$("#outreqNoLayer").html(resp.data);
//						var url = baseUrl +  "out/requirement/info/" + resp.data +"/"+$("#werks").val()+"/"+$("#whNumber").val()
//						+"/"+"00";
//						$("#reqDetail").attr("href",url)
						
						var reshtml = "";
						var reqnos = new Array();
						reqnos = resp.data.split(",");
						for (i=0;i<reqnos.length;i++){
							var url = baseUrl +  "out/requirement/info/" + reqnos[i] +"/"+$("#werks").val()+"/"+$("#whNumber").val()
							+"/"+"00";
							reshtml = reshtml + '<a class="toNewPage" title="需求明细"   href="'+url+'"><span id="outreqNoLayer">'+reqnos[i]+'</span></a><br/>';
						}
						$("#resultMsg").html(reshtml);
						
						layer.open({
							type : 1,
							offset : '50px',
							skin : 'layui-layer-molv',
							title : "需求创建成功",
							area : [ '600px', '200px' ],
							shade : 0,
							shadeClose : false,
							content : jQuery("#resultLayer"),
							btn : [ '确定'],
							btn1 : function(index) {
								layer.close(index);
							}
						});
						$("#dataGrid").jqGrid("clearGridData", true);
						$(".btn").attr("disabled", false);
						js.closeLoading();
					}else{
						js.alert(resp.msg);
						js.showErrorMessage(resp.msg,1000*100);
						$(".btn").attr("disabled", false);
						js.closeLoading();
					}

					/*if(resp.code === 0){
						confirm("操作成功！需求号:"+resp.data);
						//alert("操作成功！需求号"+resp.data+" <a href='#'>大纸打印</a> <a href='#'>小纸打印</a>");
						//清空数据
						vm.initGrid([],true);
						vm.items = []
					}
					else
						alert(resp.msg)*/
				}
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
					//var werks = vm.werks;
					var werks = $("#werks").val();
					//var whNumber = vm.whNumber;
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
						//vm.lgortLd();
					}
					win.vm.close();
				},
				no : function(index, layero) {
				}
			});
		},
		validateCostcenter:function(){
			//校验成本中心
			if(this.costcenter === "" || this.compcode === ""){
				vm.costcenterName='';
				return ;
			}
			$.ajax({
				url:baseUrl + "out/createRequirement/sapCostcenter/" + this.costcenter,
				success:function(resp){
					if(resp.COSTCENTER === ""){
						alert("成本中心不存在");
						vm.costcenterName='';
						vm.costcenter = '';
					}else if(resp.VALID_TO < vm.today){
						alert("成本中心不在有效期内");
						vm.costcenterName='';
						vm.costcenter = '';
					}
					else if(resp.COMP_CODE !== vm.compcode.toUpperCase()){
						alert(vm.costcenter+"成本中心不属于"+vm.compcode+"公司代码");
						vm.costcenterName='';
						vm.costcenter = '';
						vm.compcode = '';
					}else {
						vm.costcenterName = resp.NAME;
					}
				}
			})
		},
		add:function(){
			/*if(vm.lgortList!=null && vm.lgortList!==''&& vm.lgortList !== undefined){
				var lastrow = this.lastrow;
				var lastcell = this.lastcell;
				var lastcellname = this.lastcellname;
				$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
				var ids=$("#dataGrid").getGridParam("selarrrow");
				var rowid=lastrow-1;
				var rows=$("#dataGrid").jqGrid('getRowData');
				var last_row=$("#dataGrid").jqGrid('getRowData',rowid);
				//var cellvalue=last_row[lastcellname];
				var cellvalue=vm.lgortList;
				$.each(rows,function(i,row){
					if(parseInt(i+1)>=lastrow && cellvalue){
						$("#dataGrid").jqGrid('setCell',i, 'LGORT', cellvalue,'editable-cell');
					}
				})

			}*/
			if(this.requireTypes ==='52'){
				if(isEmpty(vm.costomerCode)){
					js.showErrorMessage("请先输入客户！");
					return false;
				}
				$.ajax({
		        	  url:baseUrl +"out/outreq/validateCostomer",
		        	  data:{"COSTOMER":vm.costomerCode},
		        	  success:function(resp){
		        		  if(resp.code != '0'){
		        			  js.showErrorMessage("客户不存在！");
		        			  return false;
		        		  } else {
		        			  var records = $('#dataGrid').jqGrid('getGridParam','records');
		        			  //如果库位已经填了，默认取全局的库位
		        			  var item = {};
		        			  if(vm.lgortList !== '' && vm.lgortList !== null && vm.lgortList !== undefined && vm.requireTypes === '47'){
		        				item.LGORT = vm.lgortList;
		        			  }
		        			  $("#dataGrid").jqGrid("addRowData",records,item);
		        		  }
		        	  }
		        })
			} else {
				var records = $('#dataGrid').jqGrid('getGridParam','records');
				//如果库位已经填了，默认取全局的库位
				var item = {};
				if(vm.lgortList !== '' && vm.lgortList !== null && vm.lgortList !== undefined ){
					item.LGORT = vm.lgortList;
				}
				$("#dataGrid").jqGrid("addRowData",records,item);
			}
		},
		remove:function(){
			var ids=$("#dataGrid").getGridParam("selarrrow");
			if(ids.length===0){
				alert('请勾选后再删除！');
				return;
			}
			$("#dataGrid").jqGrid("delGridRow",ids);
		},
		queryMatInfo:function(mat,callback){
			var werks = $("#werks").val();
			var whNumber = $("#whNumber").val();
			console.info("werks--->" + werks);
			console.info("whNumber--->" + whNumber);
			console.info("mat===>>> "+mat);
			$.ajax({
				url:baseUrl +  "out/createRequirement/mat?mat=" + $.trim(mat) +"&werks="+werks,
				success:function(resp){
					if(resp.code === 0){
						var matinfo = resp.data;
						callback(matinfo);
					}else{
						js.showMessage(resp.msg);
					}
				}

			})
		},
		querySapMoComponent:function(poNo,callback){
			$.ajax({
				//查询生产订单信息
				url:baseUrl +  "out/createRequirement/sapPoItemHead/"+$("#werks").val()+"/"+poNo+"/",
				success:function(resp){
					if(resp.code  === 0){
						callback(resp.data);
					}else{
						alert(resp.msg);
					}
				}
			});
		},
		getVendorNoFuzzy:function(event,index){
			//模糊查询供应商
			getVendorNoSelect(event.target,null,function(){vm.items[index].vendorCode = event.target.value},$("#werks").val());
		},
		getVendor:function(event){
			getVendorNoSelect(event.target,null,function(){
				vm.w_vendor = event.target.value;
			},$("#werks").val());
		},
		openStockSelectWindow:function(index){
			//TODO:...
		},
		close:function(){
			layer.closeAll();
		},


		getInnerOrder:function(){
			console.info("innerOrder=== "+this.innerOrder);
			console.info("acceptPlant=== "+this.acceptPlant);
			console.info("acceptPlant2=== "+(vm.acceptPlant).toUpperCase() );
			//内部订单号,接收工厂
			if(this.innerOrder === ""){
				alert("请输入内部订单号！");
				vm.innerOrderName='';
				return ;
			}
			if(this.acceptPlant === ""){
				alert("请输入接收工厂！");
				vm.innerOrderName='';
				return ;
			}
			//alert('acceptPlant	'+this.acceptPlant);
			$.ajax({
				url:baseUrl + "out/innerorder/SAPInnerOrder/" + this.innerOrder,
				success:function(resp){
					resp = resp.data;
					console.info("data   "+resp.data);
					if(resp.AUFNR === "" || resp.AUFNR === undefined){
						alert("订单"+vm.innerOrder + "不存在");
						vm.innerOrderName='';
						vm.innerOrder = '';
						vm.innerOrder_AUTYP = '';
					}
					else if(resp.WERKS !==  (vm.acceptPlant).toUpperCase() ){
						alert(vm.innerOrder+"内部订单不属于"+vm.acceptPlant+"接收工厂");
						vm.innerOrderName='';
						vm.innerOrder_AUTYP = '';
					}
					//else if(resp.PHAS1 === 'X' && LOEKZ !== 'X') {
					else if(resp.PHAS1 === 'X') {
						//设置内部订单名字
						vm.innerOrderName = resp.KTEXT;
						vm.innerOrder_AUTYP = resp.AUTYP;
					}else{
						alert(vm.innerOrderName + "内部订单不允许货物移动");
					}
				}
			})
		},

		validateWbs:function(){
			//校验成本中心
			if(this.wbsElementNo === "" || this.compcode === ""){
				vm.wbsElementName='';
				return ;
			}
			$.ajax({
				url:baseUrl + "out/wbsElement/wbs_element/" + this.wbsElementNo,
				success:function(resp){
					if(resp.data.WBS_ELEMENT === "" || resp.data.WBS_ELEMENT === undefined){
						alert("成本中心不存在");
						vm.wbsElementName='';
						vm.wbsElementNo = '';
					}
					else if(resp.data.COMP_CODE !== vm.compcode){
						alert(vm.wbsElementNo+"WBS元素不属于"+vm.compcode+"公司代码");
						vm.wbsElementName='';
						vm.wbsElementNo = '';
						vm.compcode = '';
					}else {
						vm.wbsElementName = resp.data.DESCRIPTION;
					}
				}
			})
		},

		fillDown:function(e){
			//向下填充函数
			var lastrow = this.lastrow; //编辑的最后一行行号
			var lastcell = this.lastcell;//编辑的最后一行单元格
			var lastcellname = this.lastcellname;//编辑的最后一行行名字
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			var ids=$("#dataGrid").getGridParam("selarrrow");
			var rowid=lastrow-1;
			var rows=$("#dataGrid").jqGrid('getRowData');
			var last_row=$("#dataGrid").jqGrid('getRowData',rowid);
			var cellvalue=last_row[lastcellname];
			var logrt = rows[rowid].LGORT;//获取编辑后的行的库位
			if(lastcellname==e){
				$.each(rows,function(i,row){
					if(parseInt(i+1)>=lastrow && cellvalue){
						$("#dataGrid").jqGrid('setCell',i, e, cellvalue,'editable-cell');
						if(e == "LGORT"){//fixbug1597 20191111 sjk
							vm.fillDOwnReloadStock(i,logrt,rows[i].MATNR);//向下填充后重新查询库存
						}
					}
				})
			}
		},
		disableSort:function(){
			//去掉排序功能
			//disable sorting of grid
			var grid = $("#dataGrid");
			//get all column names
			var columnNames = grid.jqGrid('getGridParam','colModel');
			//iterate through each and disable
			for (i = 0; i < columnNames.length; i++) {
				grid.setColProp(columnNames[i].index, { sortable: false });
			}
		},
		//fixbug1597 20191111 sjk
		fillDOwnReloadStock:function(i,lgort,matnr,stockCellName){
			var werks = $("#werks").val();
			var whNumber = $("#whNumber").val();
			var lgort = lgort;
			var lgortList = [];
			var matnr = matnr.replace(/\s+/g,"");//物料号去空格
			if(lgort!== null && lgort !== undefined && lgort !== ''){
				lgortList = lgort.split(",");
			}
			var data = {"WERKS":werks,"WH_NUMBER":whNumber,"MATNR":matnr,"LGORT_LIST":lgortList};
			$.ajax({
				url: baseUrl + "out/outreq/queryTotalStockQty",
				contentType:"application/json",
				type:"post",
				data:JSON.stringify(data),
				success:function(data){
					console.info(data);
					if(data.code === 0){
						//更新库存
						var stockQty = data.data;
						if(stockQty === null){
							stockQty = 0;
						}
						if(stockCellName === null || stockCellName === undefined || stockCellName === ''){
							stockCellName = "TOTAL_STOCK_QTY";//库存列名
						}
						$("#dataGrid").jqGrid('setCell',i, stockCellName, stockQty);
						//如果库存小于需求数量，则更新样式，
						vm.stockStyle(i);
					}else{
						alert("库存查询失败!")
					}
				}
			})

		},
		reloadstock:function(lgort,lifnr,rowid,stockCellName,matCellName){
			//查询更新库存信息
			var werks = $("#werks").val();
			var whNumber = $("#whNumber").val();
			var updateRow = vm.lastrow-1;//更新的行
			if(rowid !== null && rowid != undefined) updateRow = rowid;
			if(matCellName == null || matCellName == undefined){
				matCellName = "MATNR";
			}
			var matnr = $("#dataGrid").getCell(updateRow,matCellName);
			var lgortList = [];
			var reqQty = $("#dataGrid").getCell(updateRow,"REQ_QTY");
			if(lgort!== null && lgort !== undefined && lgort !== ''){
				lgortList = lgort.split(",");
			}
			var data = {"WERKS":werks,"WH_NUMBER":whNumber,"MATNR":$.trim(matnr),"LIFNR":lifnr,"LGORT_LIST":lgortList};
			$.ajax({
				url: baseUrl + "out/outreq/queryTotalStockQty",
				contentType:"application/json",
				type:"post",
				data:JSON.stringify(data),
				success:function(data){
					console.info(data);
					if(data.code === 0){
						//更新库存
						var stockQty = data.data;
						if(stockQty === null){
							stockQty = 0;
						}
						if(stockCellName === null || stockCellName === undefined || stockCellName === ''){
							stockCellName = "TOTAL_STOCK_QTY";
						}
						$("#dataGrid").jqGrid('setCell',updateRow, stockCellName, stockQty);
						//样式更新，如果小于需求数量
						vm.stockStyle(updateRow);
					}else{
						alert("库存查询失败!")
					}
				}
			});
		},
		checkBeforeSave:function(items){
			//保存前的检测
			//返回错误信息，或者返回true
			if(this.requireTypes === '49'){
				if(vm.w_vendor === ''){
					return "委外单位不能为空";
				}
			}

			if(this.requireTypes ==='52'){
				if(isEmpty(this.costomerCode)){
					return "客户不能为空";
				}
			}

			if(this.requireTypes ==='47' || this.requireTypes ==='48' || this.requireTypes ==='50' || this.requireTypes ==='75'){
				if(isEmpty(this.aceptLgortList)){
					return "接收库位不能为空";
				}
			}
			if(this.requireTypes ==='46' || this.requireTypes ==='77'){
				if(isEmpty(this.acceptPlant)){
					return "接收工厂不能为空";
				}
				if(isEmpty(this.aceptLgortList)){
					return "接收库位不能为空";
				}
			}

			if(this.requireTypes === '44'){
				if(this.costcenterName ==null || this.costcenterName =='') {
					return "成本中心不能为空！";
				}
			}
			if(this.requireTypes === '43'){
				if(this.innerOrderName ==null || this.innerOrderName ==''){
					return "内部订单不能为空！";
				}
			}

			/*if(this.requireTypes === '7' || this.requireTypes === '52' || this.requireTypes === '46'){
				if( this.EXPECT_BOX_QTY ==null || this.EXPECT_BOX_QTY === ''){
					return "预计数量不能为空";
				}
			}*/
			//需求数量校验
			var items  = vm.getGridSelectedItems();
			var newitems = items.filter(function(val){
				if(vm.requireTypes != '77'){
					return val.REQ_QTY === null || val.REQ_QTY === undefined || val.REQ_QTY === '';
				}
			});
			if(newitems.length > 0){
				return "需求数量不能为空";
			}
			//TODO:...
			return true;
		},
		checkMaterialHx:function(){
			var werks = $("#werks").val();
			var whNumber = $("#whNumber").val();
			var type =$("#requireTypes").val() ;
			var post = [];
			post[0] = {"werks":werks,"whNumber":whNumber,"type":type};
			console.info("weak "+werks);
			console.info("whNumber "+whNumber);
			console.info("type "+type);
			$.ajax({
				url:baseUrl + "out/outreq/mathx",
				data:JSON.stringify(post),
				contentType:"application/json",
				type:"post",
				success:function(resp){
					if(resp.code !== 0){
						var msgs = resp.msg.split("\n");
						var msg = "";
						for(var i in msgs){
							msg+=msgs[i]+"</br>";
						}
						alert(msg);
						$("#links").attr("style","display:none;");
					}
				}
			});
			/*var post = [];
			var items = vm.getGridSelectedItems();
			for(var i in items){
				post[i] = {"MATNR":value,"werks":vm.werks,"acceptPlant":vm.acceptPlant};
			}

			$.ajax({
				url:baseUrl + "/out/outreq/mathx",
				data:JSON.stringify(post),
				contentType:"application/json",
				type:"post",
				success:function(resp){
					if(resp.code !== 0){
						var msgs = resp.msg.split("\n");
						var msg = "";
						for(var i in msgs){
							msg+=msgs[i]+"</br>";
						}
						alert(msg);
					}
				}
			})*/
		}
	}
});

/*
function getLgortList(WERKS){
	//查询库位
	$.ajax({
		url:baseUrl + "in/wmsinbound/lgortlist",
		data:{
			"DEL":'0',
			"WERKS":WERKS,
			"SOBKZ":"Z",
			"BAD_FLAG":'0'
		},
		success:function(resp){
			vm.lgortlist = resp.result;
			vm.lgort=vm.lgortlist[0].lgort;
		}
	});

}*/

//用于点击其他地方保存正在编辑状态下的行
$('html').bind('click', function(e) {
	if (!isEmpty(vm.lastcell)) {
		if($(e.target).closest('#dataGrid').length == 0) {
			$('#dataGrid').jqGrid("saveCell", vm.lastrow, vm.lastcell);
			vm.lastrow="";
			vm.lastcell="";
		}
	}
});
$(document).on("click", ".toNewPage", function(e) {
	var $this = $(this),
		href = $this.attr('href'),
		icon = $this.attr('icon')||$this.data("icon")||"",
		title = $this.data("title") || $this.attr("title") || $this.text();
	if(icon!=''){
		icon = '<i class="'+icon+'"></i> ';
	}else{
		icon = "<i class='fa fa-circle-o'></i>";
	}
	if (href && href != "" && href != "blank") {
		js.addTabPage($this,  icon+$.trim(title || js.text("tabpanel.newTabPage")), href, null, true, false);
		if ($this.parent().hasClass("treeview")) {
			window.location.hash = href.replace("#", "")
		}
		return false
	}
	return true
});
function valadateVendor(){
		if(isEmpty($('#w_vendor').val())){
			js.showErrorMessage("委外单位不能为空！")
			return
		}
		setTimeout(function(){
			$.getJSON(baseURL + "common/getVendor?lifnr="+$('#w_vendor').val(),function(result){
				//$.getJSON(baseURL + "common/getVendor?lifnr="+vm.w_vendor,function(result){
				if(result.code==500){
					 $('#w_vendor').val('')
					 return
				}
				if(result.code==0&&result.list.length==0){
					js.showErrorMessage("委外单位"+ $('#w_vendor').val()+"不存在！")
					$('#w_vendor').val('')
					 return
				}
			});
		},200)
}
