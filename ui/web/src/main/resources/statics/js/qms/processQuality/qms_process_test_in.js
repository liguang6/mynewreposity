var last_scan_ele = ""
var lastrow, lastcell, lastcellname;
var last_upload_row = "";
var vm = new Vue({
	el : '#page_div',
	data : {
		showDefault : false,
		WERKS : '',
		VIN : '',
		BUS_NO:'',
		TEST_NODE : '', // 检验节点
		TEST_GROUP : '', // 检验类别
		TEST_TOOL_NO : '', // 检具编号
		ORDER_DESC : '', // 订单描述
		ORDER_NO : '',
		BUS_TYPE_CODE : '',
		TEST_TYPE : '', // 检验类别：01大巴 02 专业车
		formUrl : baseUrl + "processQuality/test/getProcessTestList",
		TEST_NODE_LIST : [],
		CONFIRM_FLAG : "N",
		FAULT_TYPE_LIST:[],
		FAULT_LIST:[],
		FAULT_LIST_ALL:[],
	},
	watch : {
		TEST_TYPE : {
			handler : function(newVal, oldVal) {
				$("#dataGrid").jqGrid('clearGridData');
				vm.TEST_NODE_LIST = vm.getTestNodes();
				//vm.query();
			}
		},
		TEST_NODE : {
			handler : function(newVal, oldVal) {
				$("#dataGrid").jqGrid('clearGridData');
				vm.$nextTick(function(){
					vm.query();
				})
				
			}
		},
		WERKS:{
			handler : function(newVal, oldVal){
				vm.ORDER_NO="";
				vm.BUS_NO="";
				vm.VIN="";
				vm.TEST_TYPE="";
				vm.ORDER_DESC="";
				vm.BUS_TYPE_CODE="";
				$("#dataGrid").jqGrid('clearGridData');
				$("#VIN").val("")
			}
		},
		ORDER_NO:{
			handler : function(newVal, oldVal) {
				$("#dataGrid").jqGrid('clearGridData');
				if(vm.ORDER_NO !=null || vm.ORDER_NO !="" ){
					vm.$nextTick(function(){
						vm.query();
					})
				}
				
			}
		}

	},
	methods : {
		query : function() {
			if(vm.TEST_TYPE==null||vm.TEST_TYPE.trim()==""){
				//js.showErrorMessage("请选择检验类别！")
				return false;
			}
			if(vm.TEST_NODE==null||vm.TEST_NODE.trim()==""){
				//js.showErrorMessage("请选择检验节点！")
				console.info("请选择检验节点")
				return false;
			}
			if (vm.ORDER_NO == null || vm.ORDER_NO.trim() == "") {		
				return false;
			}
			ajaxQuery();
		},
		getTestNodes : function() {
			let list = [];
			$.ajax({
				url : baseUrl + '/qms/common/getTestNodes',
				type : 'post',
				async : false,
				dataType : 'json',
				data : {
					testType : vm.TEST_TYPE,
					TEST_CLASS:'整车'
				},
				success : function(resp) {
					if (resp.msg == 'success') {
						list = resp.list;
					}
				}
			})
			return list;
		},
		saveRecord : function() {
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			if(($("#VIN").val()==null || ($("#VIN").val().trim()==""))){
				js.showErrorMessage("请输入车号/VIN!")
				return false;
			}
		/*	if((vm.VIN==null||vm.VIN.trim()=="")&&vm.TEST_TYPE=='02'){
				js.showErrorMessage("请输入VIN号!")
				return false;
			}*/
			var rows = $("#dataGrid").jqGrid('getRowData');
			if(rows.length<=0){
				js.showErrorMessage("无需要保存的数据！");
				return false;
			}
			var saveFlag=true;
			$.each(rows,function(i,row){
				if(row.JUDGE=='NG' && row.ABNORMAL_INFO==""){
					js.showErrorMessage("第"+(i+1)+"行未维护异常详情！")
					saveFlag=false;
					return false;
				}
				if(row.JUDGE=='NG' && row.TEST_RESULT==""){
					js.showErrorMessage("第"+(i+1)+"行未输入检验结果！")
					saveFlag=false;
					return false;
				}
				
				row.WERKS=vm.WERKS
				row.TEST_TYPE=vm.TEST_TYPE;
				if(vm.TEST_TYPE=='01'){
					row.BUS_NO=vm.BUS_NO
					row.VIN=vm.VIN
				}
				if(vm.TEST_TYPE=='02'){
					row.VIN=vm.VIN
				}
				row.ORDER_NO=vm.ORDER_NO
				row.TEST_NODE=vm.TEST_NODE
				row.TEST_GROUP=vm.TEST_GROUP
				if(row.UPDATE_FLAG =='X' ){
					row.TEST_TOOL_NO=vm.TEST_TOOL_NO
				}
				
				row.TEST_CLASS = '整车';
				//console.info(row);		
			})
			
			/**
				 * AJAX 保存检验记录
				 */
			if (saveFlag)
				$.ajax({
					url : baseUrl + "processQuality/test/saveTestRecord",
					type : 'post',
					async : false,
					dataType : 'json',
					data : {
						recordList:JSON.stringify(rows)
					},
					success : function(resp) {
						if (resp.msg == 'success') {
							js.showMessage("保存成功！");
							$('#dataGrid').trigger('reloadGrid');
							
							//$("#VIN").val("")
						}else{
							js.showErrorMessage("系统异常，保存失败!");
							return false;
						}
					}
				})
		},
		getFaultList:function(faultName){
			var _FAULT_TYPE_LIST=[];
			var _FAULT_LIST=[];
			var obj=ajaxGetFaultList();
			_FAULT_TYPE_LIST=obj.FAULT_TYPE_LIST;
			_FAULT_LIST=obj.FAULT_LIST;
			
			if(faultName==null||faultName==""){
				this.FAULT_TYPE_LIST=_FAULT_TYPE_LIST
				this.FAULT_LIST=_FAULT_LIST
			}			
			this.FAULT_LIST_ALL=_FAULT_LIST;
			//console.info(this.FAULT_TYPE_LIST)
		},

	},
	created : function() {
		this.WERKS = $("#WERKS").find("option").first().val();
		this.TEST_GROUP= $("#TEST_GROUP").find("option").first().val();
		if(this.TEST_GROUP==""){
			js.showErrorMessage("抱歉，您无录入权限!");
			return false;
		}
		this.getFaultList();
		// this.FAULT_TYPE_SELECTED=this.FAULT_TYPE_LIST[0]
		// this.getFaultList();
	},

})

$(function() {
	getBusSelect("#VIN", "#WERKS", "#TEST_TYPE", function(bus) {
		vm.ORDER_NO = bus.ORDER_NO;
		vm.ORDER_DESC = bus.ORDER_TEXT;
		vm.VIN = bus.VIN
		vm.BUS_NO=bus.BUS_NO
		vm.BUS_TYPE_CODE = bus.BUS_TYPE_CODE
		vm.TEST_TYPE=bus.TEST_TYPE;
	
	});

	getTestToolSelect("#TEST_TOOL_NO","#WERKS",function(obj){
		vm.TEST_TOOL_NO=obj.TEST_TOOL_NO
		//console.info(vm.TEST_TOOL_NO)
	});
	
	$(document).on("input", "#VIN", function() {
		vm.ORDER_NO = ""
		vm.BUS_TYPE_CODE = ""
		vm.ORDER_DESC = ""
		vm.VIN=""
		vm.TEST_NODE=""
		vm.BUS_NO=""
	})
	
	$(document).on("keydown","#VIN",function(e){	
		if(e.keyCode==13 && vm.ORDER_NO==""){
			//console.info("回车 : "+ vm.ORDER_NO)
			ajaxGetBusInfo();
		}
		
	})
	
	
	$("#uploadPhoto").bind('change', function () {
		var rowId=$("#uploadPhoto").attr("rowId");
		var row = $("#dataGrid").jqGrid("getRowData", rowId);
		console.info(JSON.stringify(row))
	    var file = $(this).val();
		if(file==null||file.length==0){
			js.showErrorMessage("请拍照或者选择图片！")
			return false;
		}
		/**
		 * 上传图片
		 */
		var formData = new FormData(); 
		formData.append('file', $('#uploadPhoto')[0].files[0]);  //添加图片信息的参数
		formData.append("file_name",$("#VIN").val()+"-"+row.TEMP_NO+"-"+row.TEMP_ITEM_NO)
		$.ajax({
		    url: baseUrl+'qms/common/uploadFile',
		    type: 'POST',
		    cache: false, //上传文件不需要缓存
		    data: formData,
		    processData: false, // 告诉jQuery不要去处理发送的数据
		    contentType: false, // 告诉jQuery不要去设置Content-Type请求头
		    async:false,
		    success: function (data) {
		    	if(data.msg=="success"){
		    		file=data.img_url;	
		    	}else{
		    		  js.showErrorMessage("上传失败")
		    	}		    		        
		    },
		    error: function (data) {
		        js.showErrorMessage("上传失败")
		    }
		})		
		
	    //alert(file);
	    if(file.length>0){
	    		var str_p = "";
	    		str_p += '<a href="'+ file+ '" class="addTabPage" title="图片查看">'+ '<i class="fa fa-search fa-lg" style="cursor:pointer" ></i>' + '</a>';
	    		//str_p += '<i class="fa fa-search fa-lg" style="cursor:pointer" onclick="showPhoto(\''+file+'\')"></i>' + '</a>';
	    		str_p += '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i class="fa fa-times fa-lg" style="cursor:pointer" onclick="delPhoto(\''
	    				+ row.ID + '\')"></i>';

	    		$("#dataGrid").jqGrid('setCell', rowId, "PHOTO", str_p, 'not-editable-cell');
	    		$("#dataGrid").jqGrid('setCell', rowId, "PHOTO_URL", file, 'not-editable-cell');
	    }
	});
	
})

function ajaxQuery() {
	
	var data = $('#dataGrid').data("dataGrid");
	
	if (data) {
		console.info(vm.BUS_NO+"    "+vm.VIN);
		$("#dataGrid").jqGrid('GridUnload');
	}
	var columns = [ {
		label : '序号',
		name : 'ID',
		index : 'ID',
		align : "left",
		width : 60,
		key : true,
		hidden : true
	}, {
		label : '',
		name : 'TEMP_ITEM_NO',
		align : "center",
		index : 'TEMP_ITEM_NO',
		hidden : true,
	}, {
		label : '异常信息',
		name : 'ABNORMAL_INFO',
		index : 'ABNORMAL_INFO',
		hidden : true
	}, {
		label : '工序名称',
		name : 'PROCESS_NAME',
		align : "center",
		index : 'PROCESS_NAME',
		width : '140'
	}, {
		label : '检验项目',
		name : 'TEST_ITEM',
		align : "center",
		index : 'TEST_ITEM',
		width : '160'
	}, {
		label : '标准要求 ',
		name : 'TEST_STANDARD',
		align : "center",
		index : 'TEST_STANDARD',
		width : '320'
	}, {
		label : '检验结果',
		name : 'TEST_RESULT',
		align : "center",
		index : 'TEST_RESULT',
		width : '150',
		editable : true,
	}, {
		label : '故障代码',
		name : 'FAULT_CODE',
		align : "center",
		index : 'FAULT_CODE',
		hidden : true,
	}, {
		label : '数值项',
		name : 'NUMBER_FLAG',
		align : "center",
		index : 'NUMBER_FLAG',
		width : '60',
		formatter : function(val, obj, row, act) {
			if (val == 'X') {
				return '是'
			} else
				return '否';
		},
		unformat : function(cellvalue, options, rowObject) {
			return cellvalue=='是'?"X":"0";
		}
	}, {
		label : '判定',
		name : 'JUDGE',
		align : "center",
		index : 'JUDGE',
		width : '80',
		editable : true,
		edittype : 'select',
	}, {
		label : '复检结果',
		name : 'RE_TEST_RESULT',
		align : "center",
		index : 'RE_TEST_RESULT',
		width : '80',
		editable : true,
	}, {
		label : '复判',
		name : 'RE_JUDGE',
		align : "center",
		index : 'RE_JUDGE',
		width : '80',
		sortable : false,
		editable : true,
		edittype : 'select',
	}, {
		label : '现场图片',
		name : 'PHOTO',
		align : "center",
		index : 'PHOTO',
		width : '100',
		editable : false,
	/* edittype:'file',editoptions:{enctype:"multipart/form-data"} */}, {
		label : '异常详情',
		name : 'ABNORMAL',
		align : "center",
		index : 'ABNORMAL',
		width : '100',
		unformat : function(cellvalue, options, rowObject) {
			return "";
		}
	}, {
		label : '检具编号',
		name : 'TEST_TOOL_NO',
		align : "center",
		index : 'TEST_TOOL_NO',
		width : '160'
	},{
		label : '图片地址',
		name : 'PHOTO_URL',
		index : 'PHOTO_URL',
		hidden : true
	},{
		label : 'RECORD_ID',
		name : 'RECORD_ID',
		index : 'RECORD_ID',
		hidden : true
	},{
		label : 'PHOTO_FLAG',
		name : 'PHOTO_FLAG',
		align : "center",
		index : 'PHOTO_FLAG',
		hidden : true
	},{
		label : 'ABNORMAL_ID',
		name : 'ABNORMAL_ID',
		index : 'ABNORMAL_ID',
		hidden : true,
	},{
		label : 'TEMP_NO',
		name : 'TEMP_NO',
		index : 'TEMP_NO',
		hidden : true,
	}, {
		label : 'ONE_PASSED_FLAG',
		name : 'ONE_PASSED_FLAG',
		index : 'ONE_PASSED_FLAG',
		hidden : true,
	}, {
		label : 'PATROL_FLAG',
		name : 'PATROL_FLAG',
		index : 'PATROL_FLAG',
		hidden : true,
	},{
		label : 'CONFIRMOR',
		name : 'CONFIRMOR',
		index : 'CONFIRMOR',
		hidden : true,
	}, {
		label : 'TEST_TOOL_NO',
		name : 'TEST_TOOL_NO',
		index : 'TEST_TOOL_NO',
		hidden : true,
	},{
		label : 'UPDATE_FLAG',
		name : 'UPDATE_FLAG',
		index : 'UPDATE_FLAG',
		hidden : true,
	},{
		label : 'FAULT_TYPE',
		name : 'FAULT_TYPE',
		index : 'FAULT_TYPE',
		hidden : true,
	},
	];

	dataGrid = $("#dataGrid") .dataGrid(
					{
						datatype : "json",
						searchForm : $("#searchForm"),
						cellEdit : true,
						cellurl : '#',
						cellsubmit : 'clientArray',
						colModel : columns,
						viewrecords : true,
						showRownum : true,
						width : "100%",
						autowidth : true,
						height : "100%",
						shrinkToFit : true,
						autoScroll : true,
						rownumWidth : 25,
						rowNum : -1,
						//loadonce : true,
						emptyrecords:"未找到匹配模板数据!",
						formatter : {
							number : {
								decimalSeparator : ".",
								thousandsSeparator : " ",
								decimalPlaces : 3,
								defaultValue : ''
							}
						},
						/* dataId:'ID', */
						multiselect : false,
						loadComplete : function(data) {
							js.closeLoading();
							$(".btn").attr("disabled", false);
							if (data.code == 500) {
								js.showErrorMessage(data.msg, 1000 * 10);
								// js.alert(data.msg);
								return false;
							}
							var rows = $("#dataGrid").jqGrid('getRowData');
							if(rows.length<1){
								js.showErrorMessage("模板未维护，请联系品质中心维护品质检验模板!");
								return false;
							}
							
							var confirmor="";
							if(rows.length>0){
								confirmor=rows[0].CONFIRMOR||"";
								//vm.TEST_TOOL_NO = rows[0].TEST_TOOL_NO;
							}							
							//已经QE确认，不允许保存
							if(confirmor.trim().length>0){
								//$("#btnSave").attr("disabled","disabled");
								vm.CONFIRM_FLAG="N";
								js.showMessage("该车辆已经QE确认过，不能修改！")
							}else{
								vm.CONFIRM_FLAG="Y";
							}
							
							$.each(
											rows,
											function(i, row) {
												// console.info(row)
												if (row.NUMBER_FLAG == 'X') {
													$("#dataGrid").jqGrid(
															'setCell', row.ID,
															"TEST_RESULT",
															row.TEST_RESULT||'&nbsp;',
															'editable-cell');
													$('#dataGrid')
															.jqGrid(
																	'setColProp',
																	'TEST_RESULT',
																	{
																		editrules : {
																			number : true
																		}
																	})
												} else {
													$("#dataGrid")
															.jqGrid(
																	'setCell',
																	row.ID,
																	"TEST_RESULT",
																	row.TEST_RESULT||'',
																	'not-editable-cell');
												}

												if (row.NUMBER_FLAG == 'X' ) {
													$("#dataGrid").jqGrid( 'setCell', row.ID, "JUDGE", row.JUDGE||'', 'editable-cell');
												} 
												if (row.NUMBER_FLAG != 'X' ) {
													if(row.TEST_RESULT != ""){
														$("#dataGrid").jqGrid('setCell', row.ID, "JUDGE", 'NG', 'not-editable-cell');
													}else
													 $("#dataGrid").jqGrid('setCell', row.ID, "JUDGE", row.JUDGE||'OK', 'editable-cell');
												} 
												/*if (row.NUMBER_FLAG == 'X' && row.JUDGE == "") {
													$("#dataGrid").jqGrid( 'setCell', row.ID, "JUDGE", '', 'editable-cell');
												} else if (row.NUMBER_FLAG != 'X' && row.TEST_RESULT != "") {
													$("#dataGrid") .jqGrid('setCell', row.ID, "JUDGE", 'NG', 'not-editable-cell');
												} else
													$("#dataGrid").jqGrid('setCell', row.ID,"JUDGE", 'OK','editable-cell');*/

												var str = "";
												if( row.ABNORMAL_INFO!=null && row.ABNORMAL_INFO.length>0){
													str += '<i class="fa fa-search fa-lg" style="cursor:pointer" onclick="showException(\''
														+ row.ID + '\')"></i>';
												}
												
												if(vm.CONFIRM_FLAG=='Y'  &&  row.ABNORMAL_INFO!=null && row.ABNORMAL_INFO.length>0){
													str += '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i class="fa fa-times fa-lg" style="cursor:pointer" onclick="delException(\''
														+ row.ID + '\')"></i>';
												}
												
												if ((row.ABNORMAL_INFO == null || row.ABNORMAL_INFO == "") && vm.CONFIRM_FLAG=='Y' ) {
													str = '<i class="fa  fa-plus fa-lg" style="cursor:pointer" onclick="addException(\''
															+ row.ID
															+ '\')"></i>'
												}
												$("#dataGrid").jqGrid('setCell', row.ID,"ABNORMAL", str,'not-editable-cell');

												/**
												 * 图片cell 初始化
												 */
												var str_p = "";
												if(row.PHOTO!=null && row.PHOTO.length>0){
													str_p += '<a href="'+ row.PHOTO+ '" class="addTabPage" title="图片查看">'+
													'<i class="fa fa-search fa-lg" style="cursor:pointer" ></i>' + '</a>';
												}
												if(vm.CONFIRM_FLAG=='Y'  &&  row.PHOTO!=null && row.PHOTO.length>0 ){
													str_p += '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i class="fa fa-times fa-lg" style="cursor:pointer" onclick="delPhoto(\''
														+ row.ID + '\')"></i>';
												}												
												//str_p += '<i class="fa fa-search fa-lg" style="cursor:pointer" onclick="showPhoto(\''+row.PHOTO+'\')"></i>' + '</a>';
												
												if ((row.PHOTO == null || row.PHOTO == "")&&row.PHOTO_FLAG=='X' && vm.CONFIRM_FLAG=='Y') {
													str_p = '<i class="fa  fa-camera fa-lg" style="cursor:pointer" onclick="uploadPhoto(\''
															+ row.ID
															+ '\')"></i>'
												}
												if(row.PHOTO_FLAG!='X'){
													str_p = "";
												}
												$("#dataGrid").jqGrid( 'setCell', row.ID, "PHOTO", str_p, 'not-editable-cell');									
												
												
												if(row.JUDGE=='OK' || row.JUDGE=='NA'||row.JUDGE==''){													
													$("#dataGrid").jqGrid('setCell', row.ID,"RE_JUDGE", row.RE_JUDGE||'','not-editable-cell');
													$("#dataGrid").jqGrid('setCell', row.ID,"RE_TEST_RESULT", row.RE_TEST_RESULT||'','not-editable-cell');
												}else{
													$("#dataGrid").jqGrid('setCell', row.ID,"RE_JUDGE",row.RE_JUDGE,'editable-cell');
													$("#dataGrid").jqGrid('setCell', row.ID,"RE_TEST_RESULT", row.RE_TEST_RESULT,'editable-cell');
												}
											})
											
							var rows = $("#dataGrid").jqGrid('getRowData');
							$.each(rows,function(i,row){
								/**
								 * added by xjw 190927
								 * JUDGE=NG且RE_JUDGE!=OK时，行背景颜色标记为红色；
								 * JUDGE为空时,行背景颜色标记为黄色；
								 */
								console.info(row.JUDGE)
								if(row.JUDGE=='NG' && row.RE_JUDGE!='OK' ){												
									$("#dataGrid tbody").find("tr[id='"+row.ID+"']").css("background-color","red")
								}
								if(row.JUDGE==''  ){												
									$("#dataGrid tbody").find("tr[id='"+row.ID+"']").css("background-color","yellow")
								}
							})
							
							js.closeLoading();
							$(".btn").attr("disabled", false);
						},
						onCellSelect : function(rowid, iCol, cellcontent, e) {
							var row = $("#dataGrid") .jqGrid('getRowData', rowid);
							if (iCol == 7 && row.NUMBER_FLAG != 'X') {
								showFaultLayer(rowid);
							}				
						
					
						},
						beforeEditCell : function(rowid, cellname, v, iRow,
								iCol) {
							lastrow = iRow;
							lastcell = iCol;
							lastcellname = cellname;
							
						},
						formatCell : function(rowid, cellname, value, iRow, iCol) {
							if (cellname == 'JUDGE') {
								var rec = $('#dataGrid').jqGrid('getRowData', rowid);
								$('#dataGrid').jqGrid('setColProp', 'JUDGE', {
									editoptions : {
										value : "OK:OK;NG:NG;NA:NA"
									}
								});
							}
							if (cellname == 'RE_JUDGE' ) {								
								$('#dataGrid').jqGrid('setColProp', 'RE_JUDGE',
										{
											editoptions : {
												value : ":;NG:NG;OK:OK"
											}
										});
							}

						},
						afterSaveCell : function(rowid, cellname, value, iRow, iCol) {
							var row = $('#dataGrid').jqGrid('getRowData', rowid);
							$("#dataGrid").jqGrid('setCell', rowid,"UPDATE_FLAG", 'X');
							//console.info(row.JUDGE)
							if(row.JUDGE=='OK' || row.JUDGE=='NA'){
								$("#dataGrid tbody").find("#"+rowid).find("td").eq(11).removeClass("editable-cell");
								$("#dataGrid tbody").find("#"+rowid).find("td").eq(12).removeClass("editable-cell");
								$("#dataGrid").jqGrid('setCell', rowid,"RE_JUDGE", '&nbsp;','not-editable-cell',{editable:false});
								$("#dataGrid").jqGrid('setCell', rowid,"RE_TEST_RESULT", '&nbsp;','not-editable-cell',{editable:false});								
							}else{
								$("#dataGrid tbody").find("#"+rowid).find("td").eq(11).removeClass("not-editable-cell");
								$("#dataGrid tbody").find("#"+rowid).find("td").eq(12).removeClass("not-editable-cell");
								$("#dataGrid").jqGrid('setCell', rowid,"RE_JUDGE", row.RE_JUDGE||'','editable-cell',{editable:true});
								$("#dataGrid").jqGrid('setCell', rowid,"RE_TEST_RESULT", row.RE_TEST_RESULT||'','editable-cell',{editable:true});
	
							}
						},
					});

}

function uploadPhoto(rowId) {
	$("#uploadPhoto").val("");
	$("#uploadPhoto").trigger("click");
	$("#uploadPhoto").attr("rowId",rowId);
	$("#dataGrid").jqGrid('setCell', rowId,"UPDATE_FLAG", 'X');
	//takePhoto(rowId);// 调用摄像头拍照
	
}
function showPhoto(url){
	$("#_photo").attr("src",url)
	layer.open({
		type : 1,
		title : [ '图片展示', 'font-size:18px' ],
		closeBtn : 1,
		btn : [ '确认' ],
		offset : [ '100px', '' ],
		area : [ '680px', '550px' ],
		skin : 'layui-bg-green', // 没有背景色
		maxmin : true,
		shadeClose : true,
		content : $("#photo_div"),
	})
}

function delPhoto(rowId) {
	var str = '<i class="fa  fa-camera fa-lg" style="cursor:pointer" onclick="uploadPhoto(\''
			+ rowId + '\')"></i>'
	$("#dataGrid").jqGrid('setCell', rowId, "PHOTO", str, "not-editable-cell");
	$("#dataGrid").jqGrid('setCell', rowId, "PHOTO_URL", '&nbsp;', 'not-editable-cell');
	$("#dataGrid").jqGrid('setCell', rowId,"UPDATE_FLAG", 'X');
}

function addException(rowId) {
	$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
	var row = $("#dataGrid").jqGrid("getRowData", rowId);
	var FAULT_TYPE = row.FAULT_TYPE;
	//console.info(row.TEST_RESULT)
	if(row.TEST_RESULT ==undefined || row.TEST_RESULT.trim()=="" ){
		js.showErrorMessage("请输入检验结果!");
		return false;
	}
	
	FAULT_TYPE = FAULT_TYPE||"尺寸类";
	
	var ABNORMAL_ID=row.ABNORMAL_ID;
	var RECORD_ID=row.RECORD_ID||"0";
	var ABNORMAL_INFO = {};
	if (row.ABNORMAL_INFO != null && row.ABNORMAL_INFO != "") {
		ABNORMAL_INFO = JSON.parse(ABNORMAL_INFO);
	}
	layer
			.open({
				type : 2,
				title : [ '异常添加', 'font-size:18px' ],
				closeBtn : 1,
				btn : [ '确认', '关闭' ],
				offset : [ '100px', '' ],
				area : [ '580px', '400px' ],
				skin : 'layui-bg-green', // 没有背景色
				maxmin : true,
				shadeClose : true,
				content : baseUrl
						+ "/qms/processQuality/qms_abnormal_info.html",
				success : function(layero, index) {
					var win = window[layero.find('iframe')[0]['name']];
					win.vm.FAULT_TYPE_LIST=vm.FAULT_TYPE_LIST||[];
					win.vm.BAD_CLASS=FAULT_TYPE;
					win.vm.WERKS =vm.WERKS;
				},
				btn1 : function(index, layero) {
					var win = window[layero.find('iframe')[0]['name']];
					/*var RESP_UNIT = $("#RESP_UNIT",
							layero.find("iframe")[0].contentWindow.document)
							.val();*/
					var RESP_WORKGROUP_LIST = win.vm.RESP_WORKGROUP_LIST;
					var RESP_UNIT=win.vm.RESP_UNIT;
					var RESP_WORKGROUP = win.vm.RESP_WORKGROUP;
					var RESP_WERKS = vm.WERKS;
					var REASON = win.vm.REASON;
					var SOLUTION = win.vm.SOLUTION;
					var SOLUTION_PERSON = win.vm.SOLUTION_PERSON;
					var BAD_SOURCE = win.vm.BAD_SOURCE;
					var BAD_CLASS = win.vm.BAD_CLASS;
					var MEMO = win.vm.MEMO;
					var CONFIRMOR= win.vm.CONFIRMOR;
					if (RESP_UNIT == null || RESP_UNIT == "") {
						js.showErrorMessage("请选择责任单位!")
						return false;
					}
					if ((RESP_WORKGROUP == null || RESP_WORKGROUP == "") && 
							(RESP_WORKGROUP_LIST!=undefined && RESP_WORKGROUP_LIST.length>0)) {
						js.showErrorMessage("请选择责任班组!")
						return false;
					}
					if (BAD_SOURCE == null || BAD_SOURCE.trim().length == 0) {
						js.showErrorMessage("请填写不良来源!")
						return false;
					}
					if (BAD_CLASS == null || BAD_CLASS.trim().length == 0) {
						js.showErrorMessage("请填写不良分类!")
						return false;
					}
					/*if (REASON == null || REASON.trim().length == 0) {
						js.showErrorMessage("请填写原因分析!")
						return false;
					}*/
					// 封装异常信息
					var _e = {};
					_e.RESP_UNIT = RESP_UNIT;
					_e.REASON = REASON;
					_e.SOLUTION = SOLUTION;
					_e.SOLUTION_PERSON = SOLUTION_PERSON;
					_e.BAD_SOURCE = BAD_SOURCE;
					_e.BAD_CLASS = BAD_CLASS;
					_e.MEMO = MEMO;
					_e.CONFIRMOR=CONFIRMOR;
					_e.RESP_WORKGROUP = RESP_WORKGROUP;

					var ABNORMAL_INFO = JSON.stringify(_e);
					/**
					 * AJAX 保存异常明细
					 */
					$.ajax({
						url : baseUrl + "processQuality/test/saveAbnormalInfo",
						type : 'post',
						async : false,
						dataType : 'json',
						data : {
							RESP_UNIT : RESP_UNIT,
							RESP_WORKGROUP : RESP_WORKGROUP,
							RESP_WERKS : RESP_WERKS,
							REASON:REASON,
							SOLUTION:SOLUTION,
							SOLUTION_PERSON:SOLUTION_PERSON,
							BAD_SOURCE:BAD_SOURCE,
							BAD_CLASS:BAD_CLASS,
							MEMO:MEMO,
							ABNORMAL_ID:ABNORMAL_ID||"",
							CONFIRMOR:CONFIRMOR,
							RECORD_ID:RECORD_ID,
						},
						success : function(resp) {
							if (resp.msg == 'success') {
								ABNORMAL_ID=resp.ABNORMAL_ID;
								$("#dataGrid").jqGrid('setCell', rowId, "ABNORMAL_ID", ABNORMAL_ID);
								$("#dataGrid").jqGrid('setCell', rowId, "ABNORMAL_INFO", ABNORMAL_INFO);
								var str = "";
								str += '<i class="fa fa-search fa-lg" style="cursor:pointer" onclick="showException(\''
										+ row.ID + '\')"></i>';
								str += '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i class="fa fa-times fa-lg" style="cursor:pointer" onclick="delException(\''
										+ row.ID + '\')"></i>';

								$("#dataGrid").jqGrid('setCell', rowId, "ABNORMAL", str,"not-editable-cell");
								$("#dataGrid").jqGrid('setCell', rowId,"UPDATE_FLAG", 'X');
							}else{
								js.showErrorMessage("系统异常，保存失败!");
								return false;
							}
						}
					})					
					
				},
				btn2 : function(index, layero) {
					layer.close(index)
				}
			});

}

function showException(rowId) {
	var row = $("#dataGrid").jqGrid("getRowData", rowId);
	var FAULT_TYPE = row.FAULT_TYPE;
	var ABNORMAL_ID=row.ABNORMAL_ID;
	var ABNORMAL_INFO = {};
	if (row.ABNORMAL_INFO != null && row.ABNORMAL_INFO != "") {
		ABNORMAL_INFO = JSON.parse(row.ABNORMAL_INFO.replace('///g', ''));
	}
	console.info(ABNORMAL_INFO)
	layer.open({
		type : 2,
		title : [ '异常查看', 'font-size:18px' ],
		closeBtn : 1,
		btn : [ '确认' ],
		offset : [ '100px', '' ],
		area : [ '580px', '400px' ],
		skin : 'layui-bg-green', // 没有背景色
		maxmin : true,
		shadeClose : true,
		content : baseUrl + "/qms/processQuality/qms_abnormal_info.html",
		success : function(layero, index) {
			var win = window[layero.find('iframe')[0]['name']];
			win.vm.FAULT_TYPE_LIST=vm.FAULT_TYPE_LIST||[];
			win.vm.BAD_CLASS=FAULT_TYPE;
			win.vm.WERKS =ABNORMAL_INFO.RESP_WERKS||vm.WERKS;
			win.vm.RESP_WORKGROUP =ABNORMAL_INFO.RESP_WORKGROUP||"";
			
			win.vm.RESP_UNIT=ABNORMAL_INFO.RESP_UNIT || "";
			win.vm.REASON=ABNORMAL_INFO.REASON || "";
			win.vm.SOLUTION=ABNORMAL_INFO.SOLUTION || "";
			win.vm.SOLUTION_PERSON=ABNORMAL_INFO.SOLUTION_PERSON || "";
			win.vm.BAD_CLASS=ABNORMAL_INFO.BAD_CLASS || "";
			win.vm.BAD_SOURCE=ABNORMAL_INFO.BAD_SOURCE || "";
			win.vm.MEMO=ABNORMAL_INFO.MEMO || "";
			//win.vm.CONFIRMOR=ABNORMAL_INFO.CONFIRMOR
			
		},
		btn1 : function(index, layero) {
			var win = window[layero.find('iframe')[0]['name']];
			var RESP_WORKGROUP_LIST = win.vm.RESP_WORKGROUP_LIST;
			var RESP_UNIT=win.vm.RESP_UNIT;
			var RESP_WORKGROUP = win.vm.RESP_WORKGROUP;
			var RESP_WERKS = win.vm.WERKS;
			var REASON = win.vm.REASON;
			var SOLUTION = win.vm.SOLUTION;
			var SOLUTION_PERSON = win.vm.SOLUTION_PERSON;
			var BAD_SOURCE = win.vm.BAD_SOURCE;
			var BAD_CLASS = win.vm.BAD_CLASS;
			var MEMO = win.vm.MEMO;
			var CONFIRMOR= win.vm.CONFIRMOR;
			if (RESP_UNIT == null || RESP_UNIT == "") {
				js.showErrorMessage("请选择责任单位!")
				return false;
			}
			if ((RESP_WORKGROUP == null || RESP_WORKGROUP == "") && 
					(RESP_WORKGROUP_LIST!=undefined && RESP_WORKGROUP_LIST.length>0)) {
				js.showErrorMessage("请选择责任班组!")
				return false;
			}
			if (BAD_SOURCE == null || BAD_SOURCE.trim().length == 0) {
				js.showErrorMessage("请填写不良来源!")
				return false;
			}
			if (BAD_CLASS == null || BAD_CLASS.trim().length == 0) {
				js.showErrorMessage("请填写不良分类!")
				return false;
			}
			/*if (REASON == null || REASON.trim().length == 0) {
				js.showErrorMessage("请填写原因分析!")
				return false;
			}*/
			// 封装异常信息
			var _e = {};
			_e.RESP_UNIT = RESP_UNIT;
			_e.REASON = REASON;
			_e.SOLUTION = SOLUTION;
			_e.SOLUTION_PERSON = SOLUTION_PERSON;
			_e.BAD_SOURCE = BAD_SOURCE;
			_e.BAD_CLASS = BAD_CLASS;
			_e.MEMO = MEMO;
			_e.CONFIRMOR=CONFIRMOR;
			_e.RESP_WORKGROUP = RESP_WORKGROUP;

			var ABNORMAL_INFO = JSON.stringify(_e);
			//未QE确认可以保存
			if(vm.CONFIRM_FLAG=='N' ){
				layer.close(index)
				return false;
			}
			/**
			 * AJAX 保存异常明细
			 */
			$.ajax({
				url : baseUrl + "processQuality/test/saveAbnormalInfo",
				type : 'post',
				async : false,
				dataType : 'json',
				data : {
					RESP_UNIT : RESP_UNIT,
					RESP_WORKGROUP : RESP_WORKGROUP,
					RESP_WERKS : RESP_WERKS,
					REASON:REASON,
					SOLUTION:SOLUTION,
					SOLUTION_PERSON:SOLUTION_PERSON,
					BAD_SOURCE:BAD_SOURCE,
					BAD_CLASS:BAD_CLASS,
					MEMO:MEMO,
					CONFIRMOR:CONFIRMOR,
					ABNORMAL_ID:ABNORMAL_ID||""
				},
				success : function(resp) {
					if (resp.msg == 'success') {
						ABNORMAL_ID=resp.ABNORMAL_ID;
						$("#dataGrid").jqGrid('setCell', rowId, "ABNORMAL_ID", ABNORMAL_ID);
						$("#dataGrid").jqGrid('setCell', rowId, "ABNORMAL_INFO", ABNORMAL_INFO);
						var str = "";
						str += '<i class="fa fa-search fa-lg" style="cursor:pointer" onclick="showException(\''
								+ row.ID + '\')"></i>';
						str += '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i class="fa fa-times fa-lg" style="cursor:pointer" onclick="delException(\''
								+ row.ID + '\')"></i>';

						$("#dataGrid").jqGrid('setCell', rowId, "ABNORMAL", str,"not-editable-cell");
						$("#dataGrid").jqGrid('setCell', rowId,"UPDATE_FLAG", 'X');
					}else{
						js.showErrorMessage("保存失败！")
						return false;
					}
				}
			})
			
			//console.info(ABNORMAL_INFO);
			
		},
	});

}

function delException(rowId) {
	var row = $("#dataGrid").jqGrid("getRowData", rowId);
	var ABNORMAL_ID=row.ABNORMAL_ID;
	var RECORD_ID=row.RECORD_ID||"0"
	/**
	 * AJAX 删除异常明细
	 */
	$.ajax({
		url : baseUrl + "processQuality/test/delAbnormalInfo",
		type : 'post',
		async : false,
		dataType : 'json',
		data : {
			ABNORMAL_ID:ABNORMAL_ID||"0",
			RECORD_ID:RECORD_ID,
			
		},
		success : function(resp) {
			if (resp.msg == 'success') {
				$("#dataGrid").jqGrid('setCell', rowId, "ABNORMAL_ID", "&nbsp;");
				$("#dataGrid").jqGrid('setCell', rowId, "ABNORMAL_INFO", "&nbsp;");
				var str = '<i class="fa  fa-plus fa-lg" style="cursor:pointer" onclick="addException(\''
						+ rowId + '\')"></i>'
				$("#dataGrid").jqGrid('setCell', rowId, "ABNORMAL", str,
						"not-editable-cell");
				$("#dataGrid").jqGrid('setCell', rowId,"UPDATE_FLAG", 'X');
			}
		}
	})
	
}

function showFaultLayer(rowId) {
	layer.open({
		type : 2,
		title : [ '故障选择', 'font-size:18px' ],
		closeBtn : 1,
		btn : [ '确认', '关闭' ],
		offset : [ '100px', '' ],
		area : [ '780px', '500px' ],
		skin : 'layui-bg-green', // 没有背景色
		maxmin : true,
		shadeClose : true,
		content : baseUrl + "/qms/processQuality/qms_fault_select.html",
		success : function(layero, index) {
			var win = window[layero.find('iframe')[0]['name']];
			win.vm.FAULT_TYPE_LIST = vm.FAULT_TYPE_LIST;
			win.vm.FAULT_LIST = vm.FAULT_LIST;
			win.vm.FAULT_LIST_ALL = vm.FAULT_LIST_ALL;
		},
		// content: '<div id="layer_content">'
		// +$('#layer_fault').html()+'</div>',//div 必须加上，否则无法改变layer中元素的值
		btn1 : function(index, layero) {
			var win = window[layero.find('iframe')[0]['name']];

			var type = win.vm.TYPE;
			if (type == 'other') {
				var fault_input = $("#fault_other",
						layero.find("iframe")[0].contentWindow.document);
				var fault_desc = $(fault_input).val()||""
				$("#dataGrid").jqGrid('setCell', rowId, "TEST_RESULT",
						fault_desc, 'not-editable-cell');
				$("#dataGrid").jqGrid('setCell', rowId, "FAULT_CODE",
						fault_code);
				$("#dataGrid").jqGrid('setCell', rowId, "FAULT_TYPE",
				"其他");
				/*if (fault_desc == null || fault_desc == "") {
					js.showErrorMessage("请输入故障描述！");
					return false;
				} else {
					$("#dataGrid").jqGrid('setCell', rowId, "TEST_RESULT",
							fault_desc, 'not-editable-cell');
				}*/
			} else {
				var fault_radio = $("input[name='fault_radio']:checked", layero
						.find("iframe")[0].contentWindow.document)
				//console.info(fault_radio)
				var fault_desc = $(fault_radio).val()||"&nbsp;"
				var fault_code = $("#SERIOUS_LEVEL", layero.find("iframe")[0].contentWindow.document).val();
				var fault_type =  $(fault_radio).attr("fault_type")||"&nbsp;"
				
				if ((fault_code == null || fault_code == "")&&fault_desc!="&nbsp;") {
					js.showErrorMessage("请选择故障等级！");
					return false;
				} else {
					if(fault_code==null||fault_code=="" ){
						fault_code="&nbsp;"
						fault_type="&nbsp;";
					}
					$("#dataGrid").jqGrid('setCell', rowId, "TEST_RESULT",
							fault_desc, 'not-editable-cell');
					$("#dataGrid").jqGrid('setCell', rowId, "FAULT_CODE",
							fault_code);
					$("#dataGrid").jqGrid('setCell', rowId, "FAULT_TYPE",
							fault_type);
				}
			}
			var row = $("#dataGrid").jqGrid("getRowData", rowId);
			if (row.TEST_RESULT != null && row.TEST_RESULT !="") {
				$("#dataGrid").jqGrid('setCell', rowId, "JUDGE", 'NG',
						'not-editable-cell');
				$("#dataGrid tbody").find("#"+rowId).find("td").eq(11).removeClass("not-editable-cell");
				$("#dataGrid tbody").find("#"+rowId).find("td").eq(12).removeClass("not-editable-cell");
				$("#dataGrid").jqGrid('setCell', rowId,"RE_JUDGE", row.RE_JUDGE||'','editable-cell',{editable:true});
				$("#dataGrid").jqGrid('setCell', rowId,"RE_TEST_RESULT", row.RE_TEST_RESULT||'','editable-cell',{editable:true});	
			}else{
				$("#dataGrid tbody").find("#"+rowId).find("td").eq(10).removeClass("not-editable-cell");
				$("#dataGrid").jqGrid('setCell', rowId, "JUDGE", 'OK', 'editable-cell');
				$("#dataGrid").jqGrid('setCell', rowId,"RE_JUDGE", '&nbsp;','not-editable-cell',{editable:false});
				$("#dataGrid").jqGrid('setCell', rowId,"RE_TEST_RESULT", '&nbsp;','not-editable-cell',{editable:false});	
			}
			
			$("#dataGrid").jqGrid('setCell', rowId,"UPDATE_FLAG", 'X');
		},
		btn2 : function(index, layero) {
			layer.close(index)
		}
	});
}

function funGetPhoto(result) {
	$("#dataGrid").jqGrid('setCell', last_upload_row, "PHOTO_URL", result);
	$("#dataGrid").jqGrid('setCell', last_upload_row,"UPDATE_FLAG", 'X');
}

function funFromjs(result) {
	//alert(result)
	var e = $.Event('keydown');
	e.keyCode = 13;
	$(last_scan_ele).val(result).trigger(e);
	vm.TEST_NODE=""
}

function doScan(ele) {
	var url = "../../doScan";
	last_scan_ele = $("#" + ele);
	if(IsPC() ){
		$(last_scan_ele).focus();
		return false;
	}
	var iframe_id=self.frameElement.getAttribute('id');
	$(window.parent.document).find("#activeIframe").val(iframe_id)
	var a = document.createElement("a");
	a.target="_parent";
	a.href = url;
	a.click();
}


function ajaxGetBusInfo(){
	var data={
			"busNo":$("#VIN").val(),
			"factory":$("#WERKS").val()||"",
			"busType":vm.TEST_TYPE
	};
	var bus=null;
	$.ajax({
		url:baseURL+"qms/common/getBusList",
		dataType : "json",
		type : "post",
		data : data,
		async: false,
		success: function (response) { 
			if(response.list==null|| response.list.length==0){
				//var bus_type=vm.TEST_TYPE=='01'?'大巴':'专用车';
				var msg="工厂“"+vm.WERKS+"”未找到车辆"+"“"+$("#VIN").val()+"”!";	
				js.showErrorMessage(msg);
			}else
				bus = response.list[0];		
		}
	});
	if(!bus){
		return false;
	}else{
		vm.ORDER_NO = bus.ORDER_NO;
		vm.ORDER_DESC = bus.ORDER_TEXT;
		vm.VIN = bus.VIN
		vm.BUS_NO=bus.BUS_NO
		vm.BUS_TYPE_CODE = bus.BUS_TYPE_CODE
		vm.TEST_TYPE=bus.TEST_TYPE;
	}

}

function ajaxGetFaultList(faultName){
	var _fault={};
	$.ajax({
		url:baseUrl+'/processQuality/test/getFaultList',
		type:'post',
		async:false,
		dataType:'json',
		data:{
			faultName:faultName,
		},
		success:function(resp){
			if(resp.msg=='success'){	
				_FAULT_TYPE_LIST=resp.faultTypeList;
				_FAULT_LIST=resp.data;
				_fault.FAULT_TYPE_LIST=_FAULT_TYPE_LIST;
				_fault.FAULT_LIST=_FAULT_LIST;
				//console.info(this.FAULT_TYPE_LIST)
			}
		}			
	})
	return _fault;
}
