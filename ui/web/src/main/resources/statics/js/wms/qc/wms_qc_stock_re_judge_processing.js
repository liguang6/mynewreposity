$(function(){
	//点击按钮，弹出到新的标签页（框架内的）
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
})

var vm = new Vue(
		{
			el : "#vue-app",
			data : {
				items : [],
				warehourse : [],
				returnreasons : [],
				wmsCQcResultList :[],
				qcresults :[]	
			},
			created : function() {
				$(function(){
					$.post(baseUrl + "config/wmscqcresult/list2",{post:$("#WERKS").val()},
					function(resp) {
							vm.qcresults = resp.page.list;
							vm.initJqgrid();
							vm.updateWoreHourse(vm.query);
					});
					$.post(baseUrl+"config/wmscqcreturnreasons/list",{},function(resp){
						vm.returnreasons = resp.page.list;
					});
				});
				this.initData();
				this.setRefresh();
			},
			methods : {
				setRefresh: function() {
					//页面刷新定时任务
					//HTML5的 Web存储特性，【Cookie也可以解决问题】
					//需求：
					//在其他页面返回的时候，需要刷新本页面
					//设置refreshPage为true即可
					setInterval(function() {
						if(localStorage.getItem("refreshPage") == "true"){
							vm.query();
							localStorage.setItem("refreshPage","false");
						}
					}, 500);
					
				},
				initData:function(){
					
					//设置默认日期，当前日期往前一个月
					var endDate = new Date().toLocaleDateString().replace(/\//g,"-");
					var startDate = getLastMonthYestdy();//获取退后一个月的日期
					
					$(function(){
						if($("#receiptDateStart").val().length == 0)
							$("#receiptDateStart").val(startDate);
						if($("#receiptDateEnd").val().length == 0)
							$("#receiptDateEnd").val(endDate);
					});
				},
				updateCQcResult:function(){
					var werks = $("#werks").val();
					$.post(baseUrl +"config/wmscqcresult/list2",{"werks":werks},function(resp){
						this.wmsCQcResultList = resp.page.list;
					}.bind(this));
				},
				initJqgrid : function(returnreasons) {
					//初始化JqGrid表单
					$("#dataGrid").dataGrid(
									{
										datatype : "local",
										colNames : ['复检单号', '行项目', '批次', '料号',
												'物料描述',  '仓库号', '库位',
												 '送检数量', '质检数量', 
												'质检状态',
												 '单批检验',
												 '工厂','储位',
												'单位',
												'供应商代码', '供应商名称',  '复检单创建日期',
												'备注','紧急物料' ],
										colModel : [
												{
													name : "INSPECTION_NO",
													index : "INSPECTION_NO",
													align : "center"
												},
												{
													name : "INSPECTION_ITEM_NO",
													index : "INSPECTION_ITEM_NO",
													width: 70,
													align : "center"
												},
												{
													name : "BATCH",
													index : "BATCH",
													align : "center"
												},
												{
													name : "MATNR",
													index : "MATNR",
													width: 100,
													align : "center"
												},
												{
													name : "MAKTX",
													index : "MAKTX",
													align : "center"
												},
												
												{
													name : "WH_NUMBER",
													index : "WH_NUMBER",
													width : 70,
													align : "center"
												},
												
												{
													name : "BIN_CODE",
													index : "BIN_CODE",
													width : 70,
													align : "center"
												},
												{
													name : "INSPECTION_QTY",
													index : "INSPECTION_QTY",
													width : 70,
													align : "center"
												},
												{
													name : "RESULT_QTY",
													index : "RESULT_QTY",
													width : 70,
													align : "center"
												},
												{
													name : "QC_RESULT_CODE",
													index : "QC_RESULT_CODE",
													width : 100,
													align : "center",
													formatter:function(val,obj,row){
														/*row.QC_RESULT_CODE = row.QC_RESULT_CODE === null || row.QC_RESULT_CODE === undefined ? "" : row.QC_RESULT_CODE;
														var selectStr = "<select class='form-control' disabled='disabled' onchange='vm.onqcStatusChange(event)'>";
														selectStr += "<option>请选择</option>";
														for(var i in vm.qcresults){
															if(row.QC_RESULT_CODE === vm.qcresults[i].code){//如果有初始值
																selectStr += "<option selected='selected' value='"+vm.qcresults[i].code+"'>"+vm.qcresults[i].value +"</option>";
															}else{
																selectStr += "<option value='"+vm.qcresults[i].code+"'>"+vm.qcresults[i].value +"</option>";
															}
														}
														selectStr += "<select>"
														return selectStr;*/
														return '质检中'
													}
												},
												
												{
													name : "SINGLE_BATCH_INSPECTION",// 这是一个按钮
													index : "SINGLE_BATCH_INSPECTION",
													align : "center",
													width : 70,
													formatter : function(val,obj, row, act) {
														if(row.BARCODE_FLAG == 'X'){
															return '<span title="'+row.WH_NUMBER+'仓库号已启用条码管理，请使用PDA功能维护改判结果"> <a class="btn btn-primary disabled" role="button">检验</a> </span>'
														}
														var url = baseUrl
																+ "qc/redirect/to_stoke_rejudge_on_inspect_batch?inspectionNo="
																+ row.INSPECTION_NO
																+ "&inspectionItemNo="
																+ row.INSPECTION_ITEM_NO
																+ "&type=00"
														return '<span><a class="btn btn-primary btnList toNewPage" title="单批质检" href="'
																+ url+ '">检验</a></span>';
													}
												}, 
												{
													name : "WERKS",
													index : "WERKS",
													align : "center"
												},
												{
													name : "LGORT",
													index : "LGORT",
													align : "center"
												},
												{
													name : "UNIT",
													index : "UNIT",
													align : "center"
												},
												{
													name : "LIFNR",
													index : "LIFNR",
													align : "center"
												},
												{
													name : "LIKTX",
													index : "LIKTX",
													align : "center"
												},
												{
													name : "CREATE_DATE",
													index : "CREATE_DATE",
													align : "center"
												}, {
													name : "MEMO",
													index : "MEMO",
													align : "center"
												},{
													name : "URGENT_FLAG",
													index : "URGENT_FLAG",
													hidden : true
												} ],
										multiselect : true,
										autowidth : true,
										shrinkToFit : false,
										autoScroll : true,
										multiselect : true,
										gridComplete:function(){
								        	//紧急物料（urgentFlag不为空的）设置自定义样式
								            var ids = $("#dataGrid").getDataIDs();
								            for(var i=0;i<ids.length;i++){
								                var rowData = $("#dataGrid").getRowData(ids[i]);
								                if(rowData.URGENT_FLAG != null && rowData.URGENT_FLAG !='' && rowData.URGENT_FLAG != undefined){
								                	//如果是紧急物料设置自定义的颜色
								                    $('#'+ids[i]).find("td").addClass("urgent-select");
								                }
								            }
								        }
						});
				},
				memoChange:function(e,rowId){
					//备注信息改变事件
					this.items[rowId].MEMO = e.target.value;
				},
				onqcStatusChange:function(e){
					//同时改变数据 (items) 和 视图 (jqgrid)
					var code =  e.target.value;
					var rowId = $(e.target).closest("tr").attr("id");
					this.items[rowId].INSPECTION = code;
					//如果是退货类型，弹出框。
					var resultConfig = null;
					vm.wmsCQcResultList.map(function(val){
						if(val.qcResultCode === code){
							resultConfig = val;
						}
					});
					if(resultConfig.returnFlag === "X" || resultConfig.goodFlag === "X"){
						//弹出框
						layer.open({
							  type: 1,
							  title:"选择不合格原因",
							  area: ['70%', '50%'],
							  closeBtn:2,
							  btn:['确定','取消'],
							  yes:function(index,layero){
								 $("#dataGrid").dataGrid("setCell",rowId,"QC_RESULT",$("#returnReason").val());
								 vm.items[rowId].QC_RESULT = $("#returnReason").val();
							  },
							  no:function(){
								 
							  },
							  fixed: false, //不固定
							  maxmin: true,
							  content: $("#wmscqcreturnreasons")
						});
					}else{
						$("#dataGrid").dataGrid("setCell",rowId,"QC_RESULT",null);
						vm.items[rowId].QC_RESULT = null;
					}
				},
				save : function() {
					//计算 不合格数量变化  合格数量变化
					

				},
				refresh : function() {
					// 刷新表单
					$("#dataGrid").dataGrid("clearGridData");
					for (var i = 0; i < this.items.length; i++) {
						$("#dataGrid").dataGrid("addRowData", i, this.items[i]);
					}
				},
				query : function(event) {
					var inspectionStatus = $("#inspectionStatus").val();
					
					if(inspectionStatus == '00'){
						//跳转到未质检
						var params = $("#searchForm").serialize();
						window.location.href = baseUrl + "qc/redirect/to_stock_rejudge_not_inspect?"+params;
					}
					
					var params = $("#searchForm").serialize();
					//查询库存复检-未质检的数据
					$.post(baseUrl+ "qc/wmsqcinspectionitem/stoke_rejuge_on_inspect?"+ params, {}, function(resp) {
										this.items = resp.list;
										this.refresh();
									}.bind(this));
				},
				updateWoreHourse : function(callBack) {
					var werks = $("#WERKS").val();
					// 工厂变化的时候，更新库存下拉框
					if (werks === null || werks === '' || werks === undefined) {
						return;
					}
					// 查询工厂仓库
					$.ajax({
				      	url:baseUrl + "common/getWhDataByWerks",
				    	data:{"WERKS":werks},
						success : function(resp) {
							vm.warehourse = resp.data;
							if (callBack != undefined
									&& typeof (callBack) === 'function') {
								callBack();
							}
							//更新质检配置
							this.updateCQcResult();
						}.bind(this)
					})
				}
			}
		})