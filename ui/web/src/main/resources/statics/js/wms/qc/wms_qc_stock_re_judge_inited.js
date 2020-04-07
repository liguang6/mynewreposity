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
			},
			created : function() {
				$(function(){
					$.post(baseUrl + "config/wmscqcresult/list2",{post:$("#WERKS").val()},
					function(resp) {
							vm.wmsCQcResultList = resp.page.list;
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
				initData:function(){
					//设置默认日期，当前日期往前一个月
					var now = new Date(); //当前日期
					var endDate = now.Format("yyyy-MM-dd");
					//var endDate = new Date().toLocaleDateString().replace(/\//g,"-");
					var startDate = GetDateStr(-7);//获取退后一个月的日期
					
					$(function(){
						if($("#receiptDateStart").val().length == 0)
							$("#receiptDateStart").val(startDate);
						if($("#receiptDateEnd").val().length == 0)
							$("#receiptDateEnd").val(endDate);
					});
				},
				
				updateCQcResult:function(){
					var werks = $("#WERKS").val();
					$.post(baseUrl +"config/wmscqcresult/list2",{"werks":werks},function(resp){
						this.wmsCQcResultList = resp.page.list;
					}.bind(this));
				},
				initJqgrid : function(returnreasons) {
					//初始化JqGrid表单
					$("#dataGrid").dataGrid(
									{
										datatype : "local",
										colNames : ['复检单号',
												'行项目', '批次', '料号',
												'物料描述',  '送检数量', '需质检数量',
												'批量检验',
												'不合格原因', '单批检验',
												'单位','工厂', '仓库号', '库位','库存类型',
												'储位',
												'供应商代码', '供应商名称','复检单创建日期','有效日期',
												'备注','紧急物料','库存数量','冻结数量' ],
										colModel : [
												{
													name : "INSPECTION_NO",
													index : "INSPECTION_NO",
													align : "center",
													width : 106
												},
												{
													name : "INSPECTION_ITEM_NO",
													index : "INSPECTION_ITEM_NO",
													width : 56,
													align : "center"
												},
												{
													name : "BATCH",
													index : "BATCH",
													align : "center",
													width : 95
												},
												{
													name : "MATNR",
													index : "MATNR",
													align : "center",
													width : 98
												},
												{
													name : "MAKTX",
													index : "MAKTX",
													align : "center",
													width : 153
												},
												
												{
													name : "INSPECTION_QTY",
													index : "INSPECTION_QTY",
													width : 69,
													align : "center"
												},
												{
													name : "REQUIRED_INSPECTION_QTY_COUNT",
													index : "REQUIRED_INSPECTION_QTY_COUNT",
													width : 80,
													align : "center"
												},
												{
													name : "QC_RESULT_CODE",// 这里是一个下拉框
													index : "QC_RESULT_CODE",
													align : "center",
													width : 103 ,
													formatter:function(val,obj,row){
														if(row.BARCODE_FLAG == 'X' && row.INSPECTION_QTY <= row.ALL_STOCK_QTY){
															return '<span title="'+row.WH_NUMBER+'仓库号已启用条码管理，且送检数量'+row.INSPECTION_QTY+'小于非限制库存数量与冻结库存数量之和'+row.ALL_STOCK_QTY+'，请重新创建复检单！"> <a  class="btn btn-default disabled" role="button">检验</a> </span>';
														}
														
														var EXTENDED_EFFECT_DATE = row.EXTENDED_EFFECT_DATE == 'X' ? true : false;//延长有效期配置
														row.INSPECTION = row.INSPECTION === null || row.INSPECTION === undefined ? "" : row.INSPECTION;
														var selectStr = "<select class='form-control' onchange='vm.onqcStatusChange(event)'>";
														selectStr += "<option>请选择</option>";
														
														if(row.EFFECT_DATE != null && row.EFFECT_DATE != undefined && !EXTENDED_EFFECT_DATE){
															//过了有效期，而且没有配置延长有效期标识
															//质检结果只能选择退货
															var effectDate = new Date(row.EFFECT_DATE);
															var currentDate = new Date(formatDate(new Date()));
															if(effectDate - currentDate < 0){
																selectStr += "<option value='07'>退货 </option><select>"
																return selectStr;
															}
														}
														
														for(var i in vm.wmsCQcResultList){
															if(row.INSPECTION === vm.wmsCQcResultList[i].qcResultCode){//如果有初始值
																selectStr += "<option selected='selected' value='"+vm.wmsCQcResultList[i].qcResultCode+"'>"+vm.wmsCQcResultList[i].qcResultName +"</option>";
															}else{
																selectStr += "<option value='"+vm.wmsCQcResultList[i].qcResultCode+"'>"+vm.wmsCQcResultList[i].qcResultName +"</option>";
															}
														}
														selectStr += "<select>"
														return selectStr;
													}
												},
												{
													name : "QC_RESULT",
													index : "QC_RESULT",
													align : "center",
													width : 116
												},
												{
													name : "SINGLE_BATCH_INSPECTION",// 这是一个按钮
													index : "SINGLE_BATCH_INSPECTION",
													align : "center",
													width : 78,
													formatter : function(val,obj, row, act) {
														var EXTENDED_EFFECT_DATE = row.EXTENDED_EFFECT_DATE == 'X' ? true : false;//延长有效期配置
														
														if(row.EFFECT_DATE != null && row.EFFECT_DATE != undefined){
															//已经过了有效期的，而且没有配置延长有效期的 不能单批质检
															var effectDate = new Date(row.EFFECT_DATE);
															var currentDate = new Date(formatDate(new Date()));
															if(effectDate - currentDate < 0){
																return '<span title="此料已过有效期，不允许单批质检！"> <a  class="btn btn-default disabled" role="button">检验</a> </span>';
															}
														}
														
														
														if(row.BARCODE_FLAG == 'X'){
															return '<span title="'+row.WH_NUMBER+'仓库号已启用条码管理，如需单批维护检验结果请使用PDA功能"> <a  class="btn btn-default disabled" role="button">检验</a> </span>';
														}
														
														var url = baseUrl
																+ "qc/redirect/to_stock_rejudge_sbatch?inspectionNo="
																+ row.INSPECTION_NO
																+ "&inspectionItemNo="
																+ row.INSPECTION_ITEM_NO
																+ "&type=00"
																+ "&batch=" + row.BATCH
										
														return '<span> <a class="btn btn-primary btnList toNewPage" title="单批复检" href="'
																+ url
																+ '">检验</a></span>';
													}
												},
												{
													name : "UNIT",
													index : "UNIT",
													width : 59,
													align : "center"
												},
												{
													name : "WERKS",
													index : "WERKS",
													width : 49,
													align : "center"
												},
												{
													name : "WH_NUMBER",
													index : "WH_NUMBER",
													width : 55,
													align : "center"
												},
												{
													name : "LGORT",
													index : "LGORT",
													width : 49,
													align : "center"
												},
												{
													name : "SOBKZ",
													index : "SOBKZ",
													width : 65,
													align : "center"
												},
												{
													name : "BIN_CODE",
													index : "BIN_CODE",
													width : 77,
													align : "center"
												},
												{
													name : "LIFNR",
													index : "LIFNR",
													align : "center",
													width : 91
												},
												{
													name : "LIKTX",
													index : "LIKTX",
													align : "center",
													width : 121
												},
												 {
													name : "CREATE_DATE",
													index : "CREATE_DATE",
													align : "center",
													width : 135
												}, 
												{
													name : "EFFECT_DATE",
													index : "EFFECT_DATE",
													align : "center",
													width : 153,
													formatter : function(vl,obj,row){
														if(row.EXTENDED_EFFECT_DATE != 'X'){
															return vl == null ? '' : vl
														}
														if(vl == null)
															vl = ""
														return "<input value='"+vl+"'onchange='vm.effectDateChange(event,"+obj.rowId+")' class='form-control'></input>"
													},
													unformat : function(v,r) {
														return null;
													}
												},
												{
													name : "MEMO",
													index : "MEMO",
													align : "center",
													width : 153,
													formatter:function(vl,obj,row){
														if(row.MEMO === null || row.MEMO === undefined)
															row.MEMO = "";
														return "<input value='"+row.MEMO+"'  onchange='vm.memoChange(event,"+obj.rowId+")' class='form-control'></input>"
													}
												}, 
												{
													name:"URGENT_FLAG",
													index:"URGENT_FLAG",
													hidden:true
												},
												{
													name:"STOCK_QTY",
													index:"STOCK_QTY",
													hidden:true
												},
												{
													name:"FREEZE_QTY",
													index:"FREEZE_QTY",
													hidden:true
												}
												],
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
									                    $('#'+ids[i]).find("td").css("color","red")
									                }
									                //有效期
									                //取vm.item jqgrid表格的数据为html元素不是原始的值
									                var item =  vm.items[i];
									                 if(item.EFFECT_DATE != null && item.EFFECT_DATE != undefined){
									                	 var effectDate = new Date(item.EFFECT_DATE)
									                	 var currentDate = new Date(formatDate(new Date()))
									                	 if(effectDate < currentDate){
									                		 $('#'+ids[i]).find("td").css("color","red")
									                	 }
									                 }
									            }
										}
						});
				},
				memoChange:function(e,rowId){
					//备注信息改变事件
					this.items[rowId].MEMO = e.target.value;
				},
				effectDateChange: function(e,rowId) {
					//有效日期编辑事件
					this.items[rowId].EFFECT_DATE = e.target.value;
					//如果修改后的有效日期大于当前日期，修改样式
					 var effectDate = new Date(e.target.value)
                	 var currentDate = new Date(formatDate(new Date()))
					 if(effectDate - currentDate > 0)
						 $('#'+rowId).find("td").css("color","black");
				},
				onqcStatusChange:function(e){
					//同时改变数据 (items) 和 视图 (jqgrid)
					var code =  e.target.value;
					var rowId = $(e.target).closest("tr").attr("id");
					//更新质检结果和代码
					//vue 更新属性,需要用$set的方式
					this.$set(this.items[rowId],"QC_RESULT_CODE",code);
					//this.items[rowId].QC_RESULT_CODE = code;
					for(var i=0;i<vm.wmsCQcResultList.length;i++){
						if(vm.wmsCQcResultList[i].qcResultCode === code){
							this.$set(this.items[rowId],"QC_RESULT_TEXT",vm.wmsCQcResultList[i].qcResultName);
							//vm.items[rowId].QC_RESULT_TEXT = vm.qcresults[i].value;
						}
					}
						
					//如果是退货类型，弹出框。
					var resultConfig = null;
					vm.wmsCQcResultList.map(function(val){
						if(val.qcResultCode === code){
							resultConfig = val;
						}
					});
					this.$set(this.items[rowId],"QC_FAIL_FLAG","0");
					if((resultConfig.whFlag == "0" || resultConfig.returnFlag == "X") && resultConfig.qcStatus != '01'){
						this.$set(this.items[rowId],"QC_FAIL_FLAG","X");//不合格标识
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
					
					//获取选中的数据ID
					var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
					if(ids.length === 0){
						alert("请选择需要保存的数据");
						return ;
					}
					//获取选中的vue date items
					var selectedItms = this.items.filter(function(val,index,arr){
						for(var id of ids){
							if(id == index)
								return true;
						}
						return false;
					});
					//判断不合格类型必须选择，不合格原因
					for(var i=0;i<selectedItms.length;i++){
						if(selectedItms[i].QC_FAIL_FLAG == 'X' && selectedItms[i].QC_RESULT_CODE != '' && (selectedItms[i].QC_RESULT == null || selectedItms[i].QC_RESULT == undefined || selectedItms[i].QC_RESULT.trim() == '')){
							js.showMessage("请选择不合格原因");
							return ;
						}
					}
					//保存
					$.ajax({
						url:baseUrl +"qc/wmsqcinspectionhead/saveStockRejudgeNotInspect",
						data:JSON.stringify(selectedItms),
						type:"post",
						contentType:"application/json",
						success:function(resp){
							if(resp.code === 0){
								js.showMessage("保存成功")
								vm.query();
							}else{
								alert("保存失败，"+resp.msg);
							}
						}
					})
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
					var params = $("#searchForm").serialize();
					if(inspectionStatus === '01'){
						window.location.href = baseUrl + "qc/redirect/to_stock_rejudge_on_inspect?" + params;
					}
					
					$.post(baseUrl+ "qc/wmsqcinspectionitem/stock_rejudge_not_inspected?"+ params, {}, function(resp) {
										this.items = resp.page.list;
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
					});
				},
				exportExcel:function(){
					
				},
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
					
				}
			}
		})


function GetDateStr(AddDayCount) { 
	   var dd = new Date();
	   dd.setDate(dd.getDate()+AddDayCount);//获取AddDayCount天后的日期
	   var y = dd.getFullYear(); 
	   var m = (dd.getMonth()+1)<10?"0"+(dd.getMonth()+1):(dd.getMonth()+1);//获取当前月份的日期，不足10补0
	   var d = dd.getDate()<10?"0"+dd.getDate():dd.getDate();//获取当前几号，不足10补0
	   return y+"-"+m+"-"+d; 
}