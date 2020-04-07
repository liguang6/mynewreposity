var mydata = [];
var mydata_mat = [{ id: "1",MAKTX: ""},{ id: "2",MAKTX: ""}];	//初始化批量物料数据
var check = true;
var lastrow,lastcell;  
var vm = new Vue({
	el : '#rrapp',
	data:{
		warehourse:[],
		businessList:[],
		lgort:'',
		lgortlist:[],//目标库位
		},
	created: function(){
		this.businessList = getBusinessList("05","RG_WHO",$("#werks").val());
		this.lgortList = getLgortList($("#werks").val())
		
    },
	methods : {
		refresh : function() {
			$('#dataGrid').trigger('reloadGrid');
		},
		onPlantChange : function(event) {
			console.log("-->onPlantChange werks = " + $("#werks").val());			
			// 工厂变化的时候，更新库存下拉框BUSINESS_NAME
			var werks = event.target.value;
			if (werks === null || werks === '' || werks === undefined) {
				return;
			}
			$.ajax({
				url:baseURL+"common/getWhDataByWerks",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":werks
				},
				async: true,
				success: function (response) { 
					$("#wh").empty();
					var strs = "";
				    $.each(response.data, function(index, value) {
				    	strs += "<option value=" + value.WH_NUMBER + ">" + value.WH_NUMBER + "</option>";
				    });
				    $("#wh").append(strs);
				}
			});
			this.businessList = getBusinessList("05","RG_WHO",$("#werks").val());
			
			this.lgortList = getLgortList(werks)
			this.lgort = ''
			$("#LGORT").find("option:contains('全部')").attr("selected",true);
		},
		onBusinessChange : function(event) {
			var business = event.target.value;
			console.log('business = ' + business)
			if("27" == business){
				$("#lable1").hide();
				$("#lable2").show();
				$("#lable3").hide();
				$("#lable4").hide();
				$("#lable5").hide();
				$("#lable10").hide();
				$("#lable11").hide();
				$("#div_mat").hide();
				$("#btnMore").hide();
			}else if("28" == business || "29" == business){
				$("#lable1").show();
				$("#lable2").hide();
				$("#lable3").hide();
				$("#lable4").hide();
				$("#lable5").hide();
				$("#lable10").hide();
				$("#lable11").hide();
				$("#div_mat").hide();
				$("#btnMore").hide();
			}else if("30" == business){
				$("#lable1").hide();
				$("#lable2").hide();
				$("#lable3").show();
				$("#btnMore").show();
				$("#lable4").hide();
				$("#lable5").hide();
				$("#lable10").hide();
				$("#lable11").hide();
				$("#div_mat").hide();
			}else if("31" == business || "32" == business){
				$("#lable1").hide();
				$("#lable2").hide();
				$("#lable3").hide();
				$("#lable4").hide();
				$("#lable5").show();
				$("#lable10").hide();
				$("#lable11").show();
				$("#div_mat").show();
				$("#btnMore").hide();
			}else if("33" == business){
				$("#lable1").hide();
				$("#lable2").hide();
				$("#lable3").hide();
				$("#lable4").hide();
				$("#lable5").show();
				$("#lable10").show();
				$("#lable11").hide();
				$("#div_mat").show();
				$("#btnMore").hide();
			}else if("34" == business){
				$("#lable1").hide();
				$("#lable2").hide();
				$("#lable3").hide();
				$("#lable4").show();
				$("#lable5").show();
				$("#lable10").hide();
				$("#lable11").hide();
				$("#div_mat").show();
				$("#btnMore").hide();
			}else if("37" == business){
				$("#lable1").hide();
				$("#lable2").hide();
				$("#lable3").hide();
				$("#lable4").hide();
				$("#lable5").show();
				$("#lable10").hide();
				$("#lable11").show();
				$("#div_mat").show();
				$("#btnMore").hide();
			}
		}
	}
});

$(function(){
	$("#inOrderNo").focus();
	$("#sap_no").hide();
	
	var business = $("#business_name").val()
	console.log("-->business : " + business)
	
	if("27" == business){
		$("#lable1").hide();
		$("#lable2").show();
		$("#lable3").hide();
		$("#lable4").hide();
		$("#lable5").hide();
		$("#lable10").hide();
		$("#lable11").hide();
		$("#div_mat").hide();
		$("#btnMore").hide();
	}else if("28" == business || "29" == business){
		$("#lable1").show();
		$("#lable2").hide();
		$("#lable3").hide();
		$("#lable4").hide();
		$("#lable5").hide();
		$("#lable10").hide();
		$("#lable11").hide();
		$("#div_mat").hide();
		$("#btnMore").hide();
	}else if("30" == business){
		$("#lable1").hide();
		$("#lable2").hide();
		$("#lable3").show();
		$("#btnMore").show();
		$("#lable4").hide();
		$("#lable5").hide();
		$("#lable10").hide();
		$("#lable11").hide();
		$("#div_mat").hide();
	}else if("31" == business || "32" == business){
		$("#lable1").hide();
		$("#lable2").hide();
		$("#lable3").hide();
		$("#lable4").hide();
		$("#lable5").show();
		$("#lable10").hide();
		$("#lable11").show();
		$("#div_mat").show();
		$("#btnMore").hide();
	}else if("33" == business){
		$("#lable1").hide();
		$("#lable2").hide();
		$("#lable3").hide();
		$("#lable4").hide();
		$("#lable5").show();
		$("#lable10").show();
		$("#lable11").hide();
		$("#div_mat").show();
		$("#btnMore").hide();
	}else if("34" == business){
		$("#lable1").hide();
		$("#lable2").hide();
		$("#lable3").hide();
		$("#lable4").show();
		$("#lable5").show();
		$("#lable10").hide();
		$("#lable11").hide();
		$("#div_mat").show();
		$("#btnMore").hide();
	}else if("37" == business){
		$("#lable1").hide();
		$("#lable2").hide();
		$("#lable3").hide();
		$("#lable4").hide();
		$("#lable5").show();
		$("#lable10").hide();
		$("#lable11").show();
		$("#div_mat").show();
		$("#btnMore").hide();
	}else{
		$("#lable1").hide();
		$("#lable2").show();
		$("#lable3").hide();
		$("#lable4").hide();
		$("#lable5").hide();
		$("#lable10").hide();
		$("#lable11").hide();
		$("#div_mat").hide();
		$("#btnMore").hide();
	}
	
	var now = new Date(); //当前日期
	var startDate=new Date(now.getTime()-6*24*3600*1000);
	$("#dateStart").val(formatDate(startDate));
	$("#dateEnd").val(formatDate(now));
	
	$.ajax({
		url:baseURL+"common/getWhDataByWerks",
		dataType : "json",
		type : "post",
		data : {
			"WERKS":$("#werks").val()
		},
		async: true,
		success: function (response) { 
			$("#wh").empty();
			var strs = "";
		    $.each(response.data, function(index, value) {
		    	strs += "<option value=" + value.WH_NUMBER + ">" + value.WH_NUMBER + "</option>";
		    });
		    $("#wh").append(strs);
		}
	});
	
	fun_tab29();
	
	$("#btnSearchData").click(function () {
		if("29"==$("#business_name").val()){
			if(""==$("#pono").val()){
				alert("采购订单不能为空！");
				return false;
			}
			$("#btnSearchData").val("查询中...");
			$("#btnSearchData").attr("disabled","disabled");	
			
			$.ajax({
				url:baseURL+"returngoods/getWareHouseOutData29",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":$("#werks").val(),
					"WH_NUMBER":$("#wh").val(),
					"LGORT":$("#LGORT").val(),
					"BUSINESS_NAME":$("#business_name").find("option:selected").text(),
					"BUSINESS_CODE":$("#business_name").val(),
					"PONO":$("#pono").val()
				},
				async: true,
				success: function (response) { 
					$("#dataGrid").jqGrid("clearGridData", true);
					mydata.length=0;
					if(response.code == "0"){
						mydata = response.result;
						if(mydata.length > 0){
							//2.3.2.7	系统根据“工厂+仓库号’ 【仓库配置表-WMS_C_WH】中是否启用条码管理-BARCODE_FLAG ，
							//启用条码 =X 时不允许使用PC端操作，并报错：‘该仓库号启用条码管理，请选择PDA扫描条码退货。’ 
							if("X" == mydata[0].BARCODE_FLAG){
								alert("该仓库号启用条码管理，请选择PDA扫描条码退货!");
								$("#btnCreat").attr("disabled","disabled");	
							}else{
								$("#dataGrid").jqGrid('GridUnload');	
								fun_tab29();
								//if(mydata.length=="")js.showMessage("未查询到符合条件的数据！");
								//BUG #488 增加工厂业务类型是否配置的判断【WMS_C_PLANT_BUSINESS】！未找到配置时，报错“***工厂 未配置***业务类型！”
								if(0 == mydata[0].PC_COUNT){
									$("#btnCreat").attr("disabled","disabled");
									alert($("#werks").val() + "工厂未配置库房退货业务类型！")
								}
							}
						}else{
							js.showMessage("未查询到符合条件的数据！");
						}
						
					}else{
						js.showMessage(response.msg)
					}
					$("#btnSearchData").val("查询");
					$("#btnSearchData").removeAttr("disabled");
				}
			});
			
		}else if("28"==$("#business_name").val()){

			if(""==$("#pono").val()){
				alert("采购订单不能为空！");
				return false;
			}
			$("#btnSearchData").val("查询中...");
			$("#btnSearchData").attr("disabled","disabled");	
			
			$.ajax({
				url:baseURL+"returngoods/getWareHouseOutData28",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":$("#werks").val(),
					"WH_NUMBER":$("#wh").val(),
					"LGORT":$("#LGORT").val(),
					"BUSINESS_NAME":$("#business_name").find("option:selected").text(),
					"BUSINESS_CODE":$("#business_name").val(),
					"PONO":$("#pono").val()
				},
				async: true,
				success: function (response) { 
					$("#dataGrid").jqGrid("clearGridData", true);
					mydata.length=0;
					if(response.code == "0"){
						mydata = response.result;
						if(mydata.length > 0){
							//2.3.2.7	系统根据“工厂+仓库号’ 【仓库配置表-WMS_C_WH】中是否启用条码管理-BARCODE_FLAG ，
							//启用条码 =X 时不允许使用PC端操作，并报错：‘该仓库号启用条码管理，请选择PDA扫描条码退货。’ 
							if("X" == mydata[0].BARCODE_FLAG){
								alert("该仓库号启用条码管理，请选择PDA扫描条码退货!");
								$("#btnCreat").attr("disabled","disabled");	
							}else{
								$("#dataGrid").jqGrid('GridUnload');						
								fun_tab28();
								if(mydata.length=="")js.showMessage("未查询到符合条件的数据！");
								//BUG #488 增加工厂业务类型是否配置的判断【WMS_C_PLANT_BUSINESS】！未找到配置时，报错“***工厂 未配置***业务类型！”
								if(0 == mydata[0].PC_COUNT){
									$("#btnCreat").attr("disabled","disabled");
									alert($("#werks").val() + "工厂未配置收料房退货业务类型！")
								}
							}
						}else{
							js.showMessage("未查询到符合条件的数据！");
						}
						
					}else{
						js.showMessage(response.msg)
					}
					$("#btnSearchData").val("查询");
					$("#btnSearchData").removeAttr("disabled");
				}
			});
		}else if("27"==$("#business_name").val()){	//STO退货
			if(""==$("#pono").val()){
				alert("SAP退货交货单不能为空！");
				return false;
			}
			$("#btnSearchData").val("查询中...");
			$("#btnSearchData").attr("disabled","disabled");	
			$.ajax({
				url:baseURL+"returngoods/getWareHouseOutData27",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":$("#werks").val(),
					"WH_NUMBER":$("#wh").val(),
					"LGORT":$("#LGORT").val(),
					"BUSINESS_NAME":$("#business_name").find("option:selected").text(),
					"BUSINESS_CODE":$("#business_name").val(),
					"SAP_OUT_NO":$("#pono").val()	//187055027 84077983
				},
				async: true,
				success: function (response) { 
					$("#dataGrid").jqGrid("clearGridData", true);
					mydata.length=0;
					if(response.code == "0"){
						mydata = response.result;
						if(mydata.length > 0){
							if("X" == mydata[0].BARCODE_FLAG){
								alert("该仓库号启用条码管理，请选择PDA扫描条码退货!");
								$("#btnCreat").attr("disabled","disabled");	
							}else{
								$("#dataGrid").jqGrid('GridUnload');	
								fun_tab27();
								if(mydata.length=="")js.showMessage("未查询到符合条件的数据！");
								//BUG #488 增加工厂业务类型是否配置的判断【WMS_C_PLANT_BUSINESS】！未找到配置时，报错“***工厂 未配置***业务类型！”
								if(0 == mydata[0].PC_COUNT){
									$("#btnCreat").attr("disabled","disabled");
									alert($("#werks").val() + "工厂未配置收料房退货业务类型！")
								}
							}
						}else{
							js.showMessage("未查询到符合条件的数据！");
						}
						
					}else{
						js.showMessage(response.msg)
					}
					$("#btnSearchData").val("查询");
					$("#btnSearchData").removeAttr("disabled");
				}
			});
		}else if("30"==$("#business_name").val()){	//生产订单半成品退货
			if(""==$("#pono").val()){
				alert("生产订单不能为空！");
				return false;
			}
			console.log($("#pono").val());
			$("#btnSearchData").val("查询中...");
			$("#btnSearchData").attr("disabled","disabled");
			
			$.ajax({
				url:baseURL+"returngoods/getWareHouseOutData30",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":$("#werks").val(),
					"WH_NUMBER":$("#wh").val(),
					"LGORT":$("#LGORT").val(),
					"BUSINESS_NAME":$("#business_name").find("option:selected").text(),
					"BUSINESS_CODE":$("#business_name").val(),
					"PO_NO":$("#pono").val()	//187055027 84077983
				},
				async: true,
				success: function (response) { 
					$("#dataGrid").jqGrid("clearGridData", true);
					mydata.length=0;
					if(response.code == "0"){
						mydata = response.result;
						if(mydata.length > 0){
							if("X" == mydata[0].BARCODE_FLAG){
								alert("该仓库号启用条码管理，请选择PDA扫描条码退货!");
								$("#btnCreat").attr("disabled","disabled");	
							}else{
								$("#dataGrid").jqGrid('GridUnload');	
								fun_tab30();
								if(mydata.length=="")js.showMessage("未查询到符合条件的数据！");
								//BUG #488 增加工厂业务类型是否配置的判断【WMS_C_PLANT_BUSINESS】！未找到配置时，报错“***工厂 未配置***业务类型！”
								if(0 == mydata[0].PC_COUNT){
									$("#btnCreat").attr("disabled","disabled");
									alert($("#werks").val() + "工厂未配置收料房退货业务类型！")
								}
							}
						}else{
							js.showMessage("未查询到符合条件的数据！");
						}
						
					}else{
						js.showMessage(response.msg)
					}
					$("#btnSearchData").val("查询");
					$("#btnSearchData").removeAttr("disabled");
				}
			});
		}else if("31"==$("#business_name").val()||"32"==$("#business_name").val()){
			if(""==$("#pono").val()){
				alert("订单不能为空！");
				return false;
			}
			$("#btnSearchData").val("查询中...");
			$("#btnSearchData").attr("disabled","disabled");
			$.ajax({
				url:baseURL+"returngoods/getWareHouseOutData31",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":$("#werks").val(),
					"WH_NUMBER":$("#wh").val(),
					"LGORT":$("#LGORT").val(),
					"BUSINESS_NAME":$("#business_name").find("option:selected").text(),
					"BUSINESS_CODE":$("#business_name").val(),
					"IO_NO":$("#pono").val(),
					"MATNR":$("#matnr").val()
				},
				async: true,
				success: function (response) { 
					$("#dataGrid").jqGrid("clearGridData", true);
					mydata.length=0;
					if(response.code == "0"){
						mydata = response.result;
						if(mydata.length > 0){
							if("X" == mydata[0].BARCODE_FLAG){
								alert("该仓库号启用条码管理，请选择PDA扫描条码退货!");
								$("#btnCreat").attr("disabled","disabled");	
							}else{
								$("#dataGrid").jqGrid('GridUnload');	
								fun_tab31();
								if(mydata.length=="")js.showMessage("未查询到符合条件的数据！");
								//BUG #488 增加工厂业务类型是否配置的判断【WMS_C_PLANT_BUSINESS】！未找到配置时，报错“***工厂 未配置***业务类型！”
								if(0 == mydata[0].PC_COUNT){
									$("#btnCreat").attr("disabled","disabled");
									alert($("#werks").val() + "工厂未配置收料房退货业务类型！")
								}
							}
						}else{
							js.showMessage("未查询到符合条件的数据！");
						}
						
					}else{
						js.showMessage(response.msg)
					}
					$("#btnSearchData").val("查询");
					$("#btnSearchData").removeAttr("disabled");
				}
			})
		}else if("34"==$("#business_name").val()){	//WBS元素成品退货
			if(""==$("#pono").val()){
				alert("WBS元素不能为空！");
				return false;
			}
			if(""==$("#matnr").val()){
				alert("料号不能为空！");
				return false;
			}
			$("#btnSearchData").val("查询中...");
			$("#btnSearchData").attr("disabled","disabled");
			$.ajax({
				url:baseURL+"returngoods/getWareHouseOutData34",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":$("#werks").val(),
					"WH_NUMBER":$("#wh").val(),
					"LGORT":$("#LGORT").val(),
					"BUSINESS_NAME":$("#business_name").find("option:selected").text(),
					"BUSINESS_CODE":$("#business_name").val(),
					"WBS":$("#pono").val(),	//AU019CS13007-01-0001 {CODE=0, WBS_ELEMENT=AU019CS13007-01-0001, DESCRIPTION=CAN盒, COMP_CODE=C190}
					"MATNR":$("#matnr").val()
				},
				async: true,
				success: function (response) { 
					$("#dataGrid").jqGrid("clearGridData", true);
					mydata.length=0;
					if(response.code == "0"){
						mydata = response.result;
						if(mydata.length > 0){
							if("X" == mydata[0].BARCODE_FLAG){
								alert("该仓库号启用条码管理，请选择PDA扫描条码退货!");
								$("#btnCreat").attr("disabled","disabled");	
							}else{
								$("#dataGrid").jqGrid('GridUnload');	
								fun_tab34();
								if(mydata.length=="")js.showMessage("未查询到符合条件的数据！");
								//BUG #488 增加工厂业务类型是否配置的判断【WMS_C_PLANT_BUSINESS】！未找到配置时，报错“***工厂 未配置***业务类型！”
								if(0 == mydata[0].PC_COUNT){
									$("#btnCreat").attr("disabled","disabled");
									alert($("#werks").val() + "工厂未配置收料房退货业务类型！")
								}
							}
						}else{
							js.showMessage("未查询到符合条件的数据！");
						}
						
					}else{
						js.showMessage(response.msg)
					}
					$("#btnSearchData").val("查询");
					$("#btnSearchData").removeAttr("disabled");
				}
			});
		}else if("33"==$("#business_name").val()){	//成本中心成品退货201
			if(""==$("#pono").val()){
				alert("成本中心不能为空！");
				return false;
			}
			if(""==$("#matnr").val()){
				alert("料号不能为空！");
				return false;
			}
			$("#btnSearchData").val("查询中...");
			$("#btnSearchData").attr("disabled","disabled");
			$.ajax({
				url:baseURL+"returngoods/getWareHouseOutData33",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":$("#werks").val(),
					"WH_NUMBER":$("#wh").val(),
					"LGORT":$("#LGORT").val(),
					"BUSINESS_NAME":$("#business_name").find("option:selected").text(),
					"BUSINESS_CODE":$("#business_name").val(),
					"COSTCENTER":$("#pono").val(),
					"MATNR":$("#matnr").val()
				},
				async: true,
				success: function (response) { 
					$("#dataGrid").jqGrid("clearGridData", true);
					mydata.length=0;
					if(response.code == "0"){
						mydata = response.result;
						if(mydata.length > 0){
							if("X" == mydata[0].BARCODE_FLAG){
								alert("该仓库号启用条码管理，请选择PDA扫描条码退货!");
								$("#btnCreat").attr("disabled","disabled");	
							}else{
								$("#dataGrid").jqGrid('GridUnload');	
								fun_tab33();
								if(mydata.length=="")js.showMessage("未查询到符合条件的数据！");
								//BUG #488 增加工厂业务类型是否配置的判断【WMS_C_PLANT_BUSINESS】！未找到配置时，报错“***工厂 未配置***业务类型！”
								if(0 == mydata[0].PC_COUNT){
									$("#btnCreat").attr("disabled","disabled");
									alert($("#werks").val() + "工厂未配置收料房退货业务类型！")
								}
							}
						}else{
							js.showMessage("未查询到符合条件的数据！");
						}
						
					}else{
						js.showMessage(response.msg)
					}
					$("#btnSearchData").val("查询");
					$("#btnSearchData").removeAttr("disabled");
				}
			});
		}else if("37"==$("#business_name").val()){	//内部/CO订单退料
			if(""==$("#pono").val()){
				alert("内部/CO订单不能为空！");
				return false;
			}
			if(""==$("#matnr").val()){
				alert("料号不能为空！");
				return false;
			}
			$("#btnSearchData").val("查询中...");
			$("#btnSearchData").attr("disabled","disabled");
			$.ajax({
				url:baseURL+"returngoods/getWareHouseOutData37",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":$("#werks").val(),
					"WH_NUMBER":$("#wh").val(),
					"LGORT":$("#LGORT").val(),
					"BUSINESS_NAME":$("#business_name").find("option:selected").text(),
					"BUSINESS_CODE":$("#business_name").val(),
					"IO_NO":$("#pono").val(),
					"MATNR":$("#matnr").val()
				},
				async: true,
				success: function (response) { 
					$("#dataGrid").jqGrid("clearGridData", true);
					mydata.length=0;
					if(response.code == "0"){
						mydata = response.result;
						if(mydata.length > 0){
							if("X" == mydata[0].BARCODE_FLAG){
								alert("该仓库号启用条码管理，请选择PDA扫描条码退货!");
								$("#btnCreat").attr("disabled","disabled");	
							}else{
								$("#dataGrid").jqGrid('GridUnload');	
								fun_tab37();
								if(mydata.length=="")js.showMessage("未查询到符合条件的数据！");
								//BUG #488 增加工厂业务类型是否配置的判断【WMS_C_PLANT_BUSINESS】！未找到配置时，报错“***工厂 未配置***业务类型！”
								if(0 == mydata[0].PC_COUNT){
									$("#btnCreat").attr("disabled","disabled");
									alert($("#werks").val() + "工厂未配置收料房退货业务类型！")
								}
							}
						}else{
							js.showMessage("未查询到符合条件的数据！");
						}
						
					}else{
						js.showMessage(response.msg)
					}
					$("#btnSearchData").val("查询");
					$("#btnSearchData").removeAttr("disabled");
				}
			});
		}
	});
	
	$("#btnMore").click(function () {
		fun_ShowMoreTable1();
		dataGridMorePaste1();
		var mats = "";
		layer.open({
    		type : 1,
    		offset : '50px',
    		title : "批量输入订单号：",
    		area : [ '500px', '350px' ],
    		shadeClose : false,
    		content : jQuery("#moreLayer1"),
    		btn : [ '确定'],
    		btn1 : function(index) {
    			layer.close(index);
    			var trs=$("#dataGrid_1").children("tbody").children("tr");
    			$.each(trs,function(index,tr){
    				console.log("==>" + $(tr).find("td").eq(1).text() + "<==");
    				if(index>0&&($(tr).find("td").eq(1).text()!=" "&&$(tr).find("td").eq(1).text()!=""))mats += $(tr).find("td").eq(1).text() + ",";
    			});
    			$("#pono").val(mats.substring(0,mats.length-1));
    		}
    	});
	});
	
	$("#btnMatMore").click(function () {
		fun_ShowMoreTable2();
		dataGridMorePaste2();
		var mats = "";
		layer.open({
    		type : 1,
    		offset : '50px',
    		title : "批量输入物料号：",
    		area : [ '500px', '350px' ],
    		shadeClose : false,
    		content : jQuery("#moreLayer2"),
    		btn : [ '确定'],
    		btn1 : function(index) {
    			layer.close(index);
    			var trs=$("#dataGrid_2").children("tbody").children("tr");
    			$.each(trs,function(index,tr){
    				console.log("==>" + $(tr).find("td").eq(1).text() + "<==");
    				if(index>0&&($(tr).find("td").eq(1).text()!=" "&&$(tr).find("td").eq(1).text()!=""))mats += $(tr).find("td").eq(1).text() + ",";
    			});
    			$("#matnr").val(mats.substring(0,mats.length-1));
    		}
    	});
	});

	$("#newOperation_1").click(function () {
		var trMoreindex = $("#dataGrid_1").children("tbody").children("tr").length;
		mydata_mat.push({ id: trMoreindex,MAKTX: ""});
		var addtr = '<tr id = "'+trMoreindex+'"  role="row" tabindex="-1" class="ui-widget-content jqgrow jqgrow ui-row-ltr '+((trMoreindex%2 == 0)?"ui-priority-secondary":"")+'" aria-selected = "false">'+
		'<td role="gridcell" class="ui-state-default jqgrid-rownum" style="text-align:center;" title="2" aria-describedby="dataGrid_rn">' + trMoreindex + 
		'</td><td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_1_"></td>'+
		'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_1_"><i title="删除" aria-hidden="true" onclick="$(this).parent().parent().remove();delMoreTr2();" class="delMoreTr fa fa-times"></i></td><td></td></tr>'
		$("#dataGrid_1 tbody").append(addtr);
	});
	$("#newReset_1").click(function () {
		mydata_mat = [{ id: "1",MAKTX: ""},{ id: "2",MAKTX: ""}]
		$("#dataGrid_1").jqGrid('GridUnload');
		fun_ShowMoreTable1();
		dataGridMorePaste1();
	});
	
	$("#newOperation_2").click(function () {
		var trMoreindex = $("#dataGrid_2").children("tbody").children("tr").length;
		mydata_mat.push({ id: trMoreindex,MAKTX: ""});
		var addtr = '<tr id = "'+trMoreindex+'"  role="row" tabindex="-1" class="ui-widget-content jqgrow jqgrow ui-row-ltr '+((trMoreindex%2 == 0)?"ui-priority-secondary":"")+'" aria-selected = "false">'+
		'<td role="gridcell" class="ui-state-default jqgrid-rownum" style="text-align:center;" title="2" aria-describedby="dataGrid_rn">' + trMoreindex + 
		'</td><td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_1_"></td>'+
		'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_1_"><i title="删除" aria-hidden="true" onclick="$(this).parent().parent().remove();delMoreTr2();" class="delMoreTr fa fa-times"></i></td><td></td></tr>'
		$("#dataGrid_2 tbody").append(addtr);
	});
	$("#newReset_2").click(function () {
		mydata_mat = [{ id: "1",MAKTX: ""},{ id: "2",MAKTX: ""}]
		$("#dataGrid_2").jqGrid('GridUnload');
		fun_ShowMoreTable2();
		dataGridMorePaste2();
	});
	
	$("#btnReset").click(function () {
		$("#dataGrid").jqGrid("clearGridData", true);
		$("#dataGrid").jqGrid("setFrozenColumns");
		$("#pono").val("");
		$("#btnCreat").removeAttr("disabled");
		//console.log(document.getElementById("sp1").innerHTML);
		//document.getElementById("sp1").innerHTML="111111";
	});
	$("#btnPrint1").click(function () {
		window.open(baseURL+"docPrint/wareHouseOutPrint?PageSize=0&outNo=" + document.getElementById("outNo").innerHTML);
	});
	$("#btnPrint2").click(function () {
		window.open(baseURL+"docPrint/wareHouseOutPrint?PageSize=1&outNo=" + document.getElementById("outNo").innerHTML);
	});
	$("#btnCreat").click(function () {
		//校验数据
		var mydata2 = [];
		var trs=$("#dataGrid").children("tbody").children("tr");
		var total = 0;var freeze = 0;
		var total2 = 0;var freeze2 = 0; var matnr="";
		if("27"==$("#business_name").val() || "28"==$("#business_name").val() || "29"==$("#business_name").val()){
			$.each(trs,function(index,tr){
				var cbx=$(tr).find("td").find("input").attr("type");
				
				if(cbx!=undefined){	
					var c_checkbox=$(tr).find('input[type=checkbox]');
					var ischecked=$(c_checkbox).is(":checked");
					
					var trindex = mydata[$(tr).find("td").eq(0).html()-1].INDEX;
					if(1==trindex){
						if(ischecked){
							total = parseFloat(mydata[$(tr).find("td").eq(0).html()-1].QTY1);
							freeze = parseFloat(mydata[$(tr).find("td").eq(0).html()-1].KETUI);
						}else{
							total = 0;
							freeze = parseFloat(mydata[$(tr).find("td").eq(0).html()-1].KETUI);
						}
					}else{
						if(ischecked) total += parseFloat(mydata[$(tr).find("td").eq(0).html()-1].QTY1);
					}
					console.log("-->" + trindex + "|total=" + total + "|freeze=" + freeze);
					if(total > freeze){
						alert("退货数量不能超过可退数量！");
						check = false;
						return false;
					}
					
					//增加针对料号相同行项目不同的情况的校验
					if(ischecked){
						if(matnr == ""){
							total2 = parseFloat(mydata[$(tr).find("td").eq(0).html()-1].QTY1);
							freeze2 = parseFloat(mydata[$(tr).find("td").eq(0).html()-1].KETUI);
						}else{
							if($(tr).find("td").eq(3).html() == matnr){
								total2 += parseFloat(mydata[$(tr).find("td").eq(0).html()-1].QTY1);
							}else{
								total2 = parseFloat(mydata[$(tr).find("td").eq(0).html()-1].QTY1);
								freeze2 = parseFloat(mydata[$(tr).find("td").eq(0).html()-1].KETUI);
							}
						}
						console.log(index + " matnr : " + matnr + " " + total2 + "|" + freeze2)
						if(total2 > freeze2){
							alert("相同物料退货数量不能超过该物料可退数量！");
							check = false;
							return false;
						}
						matnr = $(tr).find("td").eq(2).html();
						console.log("-->" + matnr + "|total2=" + total2 + "|freeze2=" + freeze2);
					}
				}
				
			});
		}else if("30"==$("#business_name").val()){
			console.log("-->business_name 30");
			//相同物料退货数量不能超过该物料可退数量！
			
			//check = false;
		}else if("31"==$("#business_name").val() || "32"==$("#business_name").val()){
			console.log("-->business_name 31、32");
			//相同物料退货数量不能超过该物料可退数量！
			$.each(trs,function(index,tr){
				var cbx=$(tr).find("td").find("input").attr("type");
				if(cbx!=undefined){	
					var c_checkbox=$(tr).find('input[type=checkbox]');
					var ischecked=$(c_checkbox).is(":checked");
					if(ischecked){
						total = parseFloat(mydata[$(tr).find("td").eq(0).html()-1].QTY1);
						freeze = parseFloat(mydata[$(tr).find("td").eq(0).html()-1].KETUI);
						if(total > freeze){
							alert("退货数量不能超过该物料可退数量！");
							check = false;
							return false;
						}
					}
				}
			})
			//check = false;
		}else if("34"==$("#business_name").val()){
			console.log("-->business_name 34");
			
		}
		
		
		$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
		if(check){	//防止用户在输入数量的文本框没有消失就直接点创建
			var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");	
			var BUSINESS_TYPE = "";
			if("29"==$("#business_name").val()){
				BUSINESS_TYPE = "02";
			}else if("28"==$("#business_name").val()){ 
				BUSINESS_TYPE = "02";
			}else if("27"==$("#business_name").val()){
				BUSINESS_TYPE = "06";
			}else if("30"==$("#business_name").val()){
				BUSINESS_TYPE = "10";
			}else if("31"==$("#business_name").val()){
				BUSINESS_TYPE = "13";
			}else if("32"==$("#business_name").val()){
				BUSINESS_TYPE = "12";
			}else if("34"==$("#business_name").val()){
				BUSINESS_TYPE = "15";
			}else if("33"==$("#business_name").val()){
				BUSINESS_TYPE = "14";
			}else if("37"==$("#business_name").val()){
				BUSINESS_TYPE = "13";
			}
			var arrList = new Array();
			if(ids.length == 0){
				alert("当前还没有勾选任何行项目数据！");
				return false;
			}
			for (var i = 0; i < ids.length; i++) {
				arrList[i] = $("#dataGrid").jqGrid('getRowData',ids[i]);
			}
			
			//console.log(ids);
			console.log(arrList);
			
			$("#btnCreat").val("正在操作,请稍候...");	
			$("#btnCreat").attr("disabled","disabled");
			
			$.ajax({
				url:baseURL+"returngoods/createWareHouseOutReturn",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":$("#werks").val(),
					"WH_NUMBER":$("#wh").val(),
					"BUSINESS_TYPE":BUSINESS_TYPE,
					"BUSINESS_NAME":$("#business_name").val(),
					"ARRLIST":JSON.stringify(arrList)
				},
				async: true,
				success: function (response) {
					if(response.code=='0'){
						if("27"==$("#business_name").val()){
							$("#dataGrid").jqGrid("clearGridData", true);
							$("#dataGrid").jqGrid("setFrozenColumns");
						}else{
							$("#btnSearchData").click();
						}
						
						js.showMessage("退货单创建成功");
		            	$("#btnCreat").val("创建退货单");
		            	$("#outNo").html(response.outNo);
		            	layer.open({
		            		type : 1,
		            		offset : '50px',
		            		skin : 'layui-layer-molv',
		            		title : "退货单创建成功",
		            		area : [ '500px', '200px' ],
		            		shade : 0,
		            		shadeClose : false,
		            		content : jQuery("#resultLayer"),
		            		btn : [ '确定'],
		            		btn1 : function(index) {
		            			layer.close(index);
		    					$("#btnCreat").removeAttr("disabled");
		            		}
		            	});
		            	$("#btnSearchData").click();
					}else{
						js.showMessage(response.msg);
		            	$("#btnCreat").val("创建退货单");
    					$("#btnCreat").removeAttr("disabled");
					}
				}
			});
		}
		check = true;
	});

	$(document).bind("click",function(e){
		if("searchForm" == $(e.target).attr("id")||"ui-jqgrid-bdiv" == $(e.target).attr("class")){
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
		}
	});
	
	function fun_tab37(){
		$("#dataGrid").dataGrid({
			datatype: "local",
			data: mydata,
	        colModel: [			
	        	{label: '工厂', name: 'WERKS',index:"WERKS", width: "100",align:"center",sortable:false,frozen: true,
	            	formatter:function(val){
	            		return '<p style="height:20px;padding-top:7px">'+$("#werks").val()+'</p>';
	            	}
	            },
	            {label: '料号', name: 'MATNR',index:"MATNR", width: "100",align:"center",sortable:false,frozen: true,},
	            {label: '物料描述', name: 'MAKTX',index:"MAKTX", width: "200",align:"center",sortable:false,frozen: true,
	            	formatter:function(val){
	            		//return '<p style="height:20px;padding-top:7px">'+val+'</p>';
	            		return val
	            	}
	            },
	            {label: '入库数量', name: 'IN_QTY',index:"IN_QTY", width: "80",align:"center",sortable:true,frozen: true},  
	            {label: '冻结数量', name: 'FREEZE_QTY',index:"FREEZE_QTY", width: "80",align:"center",sortable:true,frozen: true},  
	            {label: '<span style="color:red">*</span><span style="color:blue"><b>退货数量</b></span>', name: 'QTY1',index:"QTY1", width: "100",align:"center",sortable:false,editable:true,
	            	formatter:'number',editrules:{number:true},
	            	formatoptions:{decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}
	            },     
	            {label: '批次', name: 'BATCH',index:"BATCH", width: "100",align:"center",sortable:true,frozen: true},     
	            {label: '储位', name: 'BIN_CODE',index:"BIN_CODE", width: "60",align:"center",sortable:true,hidden:true},       
	            {label: '库位', name: 'LGORT',index:"LGORT", width: "60",align:"center",sortable:true},          
	            {label: '备注', name: 'MEMO',index:"MEMO", width: "100",align:"center",sortable:true,editable:true},      
	            {label: '单位', name: 'UNIT',index:"UNIT", width: "80",align:"center",sortable:true},      
	            {label: '内部/CO订单', name: 'IO_NO',index:"IO_NO", width: "100",align:"center",sortable:true},      
	            {label: '入库日期', name: 'CREATE_DATE',index:"CREATE_DATE", width: "100",align:"center",sortable:true},
	            {label: 'KETUI', name: 'KETUI',index:"KETUI", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'LIFNR', name: 'LIFNR',index:"LIFNR", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'LIKTX', name: 'LIKTX',index:"LIKTX", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'SOBKZ', name: 'SOBKZ',index:"SOBKZ", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'F_BATCH', name: 'F_BATCH',index:"F_BATCH", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'BATCH', name: 'BATCH',index:"BATCH", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'PONO', name: 'PONO',index:"PONO", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'EBELP', name: 'EBELP',index:"EBELP", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'MO_NO', name: 'MO_NO',index:"MO_NO", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'MO_ITEM_NO', name: 'MO_ITEM_NO',index:"MO_ITEM_NO", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'INBOUND_NO', name: 'INBOUND_NO',index:"INBOUND_NO", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'INBOUND_ITEM_NO', name: 'INBOUND_ITEM_NO',index:"INBOUND_ITEM_NO", width: "50",align:"center",sortable:false,hidden:true},
	            ],
	        viewrecords: true,
			beforeEditCell:function(rowid, cellname, v, iRow, iCol){
				lastrow=iRow;
				lastcell=iCol;
			},
			beforeSelectRow : function(rowid, e) { // 点击复选框才选中行
				var $myGrid = $(this), i = $.jgrid.getCellIndex($(e.target).closest('td')[0]), cm = $myGrid.jqGrid('getGridParam', 'colModel');
				if (cm[i].name == 'cb') {
					$('#dataGrid').jqGrid('setSelection', rowid);
				}
				return (cm[i].name == 'cb');
			},
			onSelectRow:function(rowid,status,e){
			},
			afterSaveCell:function(rowid, cellname, value, iRow, iCol){
				var row =  $("#dataGrid").jqGrid('getRowData', rowid);
				if(cellname=='QTY1'){
					if("" == value){
						alert("退货数量不能为空！");
						$("#dataGrid").jqGrid('setCell', rowid, cellname, mydata[rowid-1].QTY1,'editable-cell');
						mydata[rowid-1].QTY3 = row.QTY2;
						check = false;
						return false;
					}
					if(parseFloat(value) > parseFloat(row.KETUI)){
						alert("退货数量不能大于可退数量！");
						$("#dataGrid").jqGrid('setCell', rowid, cellname, mydata[rowid-1].QTY1,'editable-cell');
						mydata[rowid-1].QTY1 = row.KETUI;
						check = false;
					}else{
						console.log("-->rowid-1 : " + (rowid-1));
						mydata[rowid-1].QTY1 = value;
						check = true;
					}
					
				}
				if(cellname=='REASON'){
					mydata[iRow-1].REASON = value;
				}
				$('#dataGrid').jqGrid('setGridParam',{data: mydata}).trigger( 'reloadGrid' );
			},
			shrinkToFit: false,
	        width:1900,
	        cellEdit:true,
	        cellurl:'#',
			cellsubmit:'clientArray',
	        showCheckbox:true
	    });
		
		$("#dataGrid").jqGrid("setFrozenColumns");
	}
	
	function fun_tab33(){
		$("#dataGrid").dataGrid({
			datatype: "local",
			data: mydata,
	        colModel: [			
	        	{label: '工厂', name: 'WERKS',index:"WERKS", width: "100",align:"center",sortable:false,frozen: true,
	            	formatter:function(val){
	            		return '<p style="height:20px;padding-top:7px">'+$("#werks").val()+'</p>';
	            	}
	            },
	            {label: '料号', name: 'MATNR',index:"MATNR", width: "100",align:"center",sortable:false,frozen: true,},
	            {label: '物料描述', name: 'MAKTX',index:"MAKTX", width: "200",align:"center",sortable:false,frozen: true,
	            	formatter:function(val){
	            		return val
	            	}
	            },
	            {label: '入库数量', name: 'IN_QTY',index:"IN_QTY", width: "80",align:"center",sortable:true,frozen: true},  
	            {label: '冻结数量', name: 'FREEZE_QTY',index:"FREEZE_QTY", width: "80",align:"center",sortable:true,frozen: true},  
	            {label: '<span style="color:red">*</span><span style="color:blue"><b>退货数量</b></span>', name: 'QTY1',index:"QTY1", width: "100",align:"center",sortable:false,editable:true,
	            	formatter:'number',editrules:{number:true},
	            	formatoptions:{decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}
	            },     
	            {label: '批次', name: 'BATCH',index:"BATCH", width: "100",align:"center",sortable:true,frozen: true},     
	            {label: '储位', name: 'BIN_CODE',index:"BIN_CODE", width: "60",align:"center",sortable:true,hidden:true},       
	            {label: '库位', name: 'LGORT',index:"LGORT", width: "60",align:"center",sortable:true},          
	            {label: '备注', name: 'MEMO',index:"MEMO", width: "100",align:"center",sortable:true,editable:true},      
	            {label: '单位', name: 'UNIT',index:"UNIT", width: "80",align:"center",sortable:true},      
	            {label: '成本中心', name: 'COST_CENTER',index:"COST_CENTER", width: "100",align:"center",sortable:true},      
	            {label: '入库日期', name: 'CREATE_DATE',index:"CREATE_DATE", width: "100",align:"center",sortable:true},
	            {label: 'KETUI', name: 'KETUI',index:"KETUI", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'LIFNR', name: 'LIFNR',index:"LIFNR", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'LIKTX', name: 'LIKTX',index:"LIKTX", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'SOBKZ', name: 'SOBKZ',index:"SOBKZ", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'F_BATCH', name: 'F_BATCH',index:"F_BATCH", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'BATCH', name: 'BATCH',index:"BATCH", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'PONO', name: 'PONO',index:"PONO", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'EBELP', name: 'EBELP',index:"EBELP", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'MO_NO', name: 'MO_NO',index:"MO_NO", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'MO_ITEM_NO', name: 'MO_ITEM_NO',index:"MO_ITEM_NO", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'INBOUND_NO', name: 'INBOUND_NO',index:"INBOUND_NO", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'INBOUND_ITEM_NO', name: 'INBOUND_ITEM_NO',index:"INBOUND_ITEM_NO", width: "50",align:"center",sortable:false,hidden:true},
	            ],
	        viewrecords: true,
			beforeEditCell:function(rowid, cellname, v, iRow, iCol){
				lastrow=iRow;
				lastcell=iCol;
			},
			beforeSelectRow : function(rowid, e) { // 点击复选框才选中行
				var $myGrid = $(this), i = $.jgrid.getCellIndex($(e.target).closest('td')[0]), cm = $myGrid.jqGrid('getGridParam', 'colModel');
				if (cm[i].name == 'cb') {
					$('#dataGrid').jqGrid('setSelection', rowid);
				}
				return (cm[i].name == 'cb');
			},
			onSelectRow:function(rowid,status,e){
			},
			afterSaveCell:function(rowid, cellname, value, iRow, iCol){
				var row =  $("#dataGrid").jqGrid('getRowData', rowid);
				if(cellname=='QTY1'){
					if("" == value){
						alert("退货数量不能为空！");
						$("#dataGrid").jqGrid('setCell', rowid, cellname, mydata[rowid-1].QTY1,'editable-cell');
						mydata[rowid-1].QTY3 = row.QTY2;
						check = false;
						return false;
					}
					if(parseFloat(value) > parseFloat(row.KETUI)){
						alert("退货数量不能大于可退数量！");
						$("#dataGrid").jqGrid('setCell', rowid, cellname, mydata[rowid-1].QTY1,'editable-cell');
						mydata[rowid-1].QTY1 = row.KETUI;
						check = false;
					}else{
						console.log("-->rowid-1 : " + (rowid-1));
						mydata[rowid-1].QTY1 = value;
						check = true;
					}
					
				}
				if(cellname=='REASON'){
					mydata[iRow-1].REASON = value;
				}
				$('#dataGrid').jqGrid('setGridParam',{data: mydata}).trigger( 'reloadGrid' );
			},
			shrinkToFit: false,
	        width:1900,
	        cellEdit:true,
	        cellurl:'#',
			cellsubmit:'clientArray',
	        showCheckbox:true
	    });
		
		$("#dataGrid").jqGrid("setFrozenColumns");
	}
	
	function fun_tab34(){
		$("#dataGrid").dataGrid({
			datatype: "local",
			data: mydata,
	        colModel: [			
	        	{label: '工厂', name: 'WERKS',index:"WERKS", width: "100",align:"center",sortable:false,frozen: true,
	            	formatter:function(val){
	            		return '<p style="height:20px;padding-top:7px">'+$("#werks").val()+'</p>';
	            	}
	            },
	            {label: '料号', name: 'MATNR',index:"MATNR", width: "100",align:"center",sortable:false,frozen: true,},
	            {label: '物料描述', name: 'MAKTX',index:"MAKTX", width: "200",align:"center",sortable:false,frozen: true,
	            	formatter:function(val){
	            		return val
	            	}
	            },
	            {label: '入库数量', name: 'IN_QTY',index:"IN_QTY", width: "80",align:"center",sortable:true,frozen: true},  
	            {label: '冻结数量', name: 'FREEZE_QTY',index:"FREEZE_QTY", width: "80",align:"center",sortable:true,frozen: true},  
	            {label: '<span style="color:red">*</span><span style="color:blue"><b>退货数量</b></span>', name: 'QTY1',index:"QTY1", width: "100",align:"center",sortable:false,editable:true,
	            	formatter:'number',editrules:{number:true},
	            	formatoptions:{decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}
	            },     
	            {label: '批次', name: 'BATCH',index:"BATCH", width: "100",align:"center",sortable:true,frozen: true},     
	            {label: '储位', name: 'BIN_CODE',index:"BIN_CODE", width: "60",align:"center",sortable:true,hidden:true},       
	            {label: '库位', name: 'LGORT',index:"LGORT", width: "60",align:"center",sortable:true},          
	            {label: '备注', name: 'MEMO',index:"MEMO", width: "100",align:"center",sortable:true,editable:true},      
	            {label: '单位', name: 'UNIT',index:"UNIT", width: "80",align:"center",sortable:true},      
	            {label: 'WBS', name: 'WBS',index:"WBS", width: "150",align:"center",sortable:true},      
	            {label: '入库日期', name: 'CREATE_DATE',index:"CREATE_DATE", width: "100",align:"center",sortable:true},
	            {label: 'KETUI', name: 'KETUI',index:"KETUI", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'LIFNR', name: 'LIFNR',index:"LIFNR", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'LIKTX', name: 'LIKTX',index:"LIKTX", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'SOBKZ', name: 'SOBKZ',index:"SOBKZ", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'F_BATCH', name: 'F_BATCH',index:"F_BATCH", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'BATCH', name: 'BATCH',index:"BATCH", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'PONO', name: 'PONO',index:"PONO", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'EBELP', name: 'EBELP',index:"EBELP", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'MO_NO', name: 'MO_NO',index:"MO_NO", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'MO_ITEM_NO', name: 'MO_ITEM_NO',index:"MO_ITEM_NO", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'INBOUND_NO', name: 'INBOUND_NO',index:"INBOUND_NO", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'INBOUND_ITEM_NO', name: 'INBOUND_ITEM_NO',index:"INBOUND_ITEM_NO", width: "50",align:"center",sortable:false,hidden:true},
	            ],
	        viewrecords: true,
			beforeEditCell:function(rowid, cellname, v, iRow, iCol){
				lastrow=iRow;
				lastcell=iCol;
			},
			beforeSelectRow : function(rowid, e) { // 点击复选框才选中行
				var $myGrid = $(this), i = $.jgrid.getCellIndex($(e.target).closest('td')[0]), cm = $myGrid.jqGrid('getGridParam', 'colModel');
				if (cm[i].name == 'cb') {
					$('#dataGrid').jqGrid('setSelection', rowid);
				}
				return (cm[i].name == 'cb');
			},
			onSelectRow:function(rowid,status,e){
			},
			afterSaveCell:function(rowid, cellname, value, iRow, iCol){
				var row =  $("#dataGrid").jqGrid('getRowData', rowid);
				if(cellname=='QTY1'){
					if("" == value){
						alert("退货数量不能为空！");
						$("#dataGrid").jqGrid('setCell', rowid, cellname, mydata[rowid-1].QTY1,'editable-cell');
						mydata[rowid-1].QTY3 = row.QTY2;
						check = false;
						return false;
					}
					if(parseFloat(value) > parseFloat(row.KETUI)){
						alert("退货数量不能大于可退数量！");
						$("#dataGrid").jqGrid('setCell', rowid, cellname, mydata[rowid-1].QTY1,'editable-cell');
						mydata[rowid-1].QTY1 = row.KETUI;
						check = false;
					}else{
						console.log("-->rowid-1 : " + (rowid-1));
						mydata[rowid-1].QTY1 = value;
						check = true;
					}
					
				}
				if(cellname=='REASON'){
					mydata[iRow-1].REASON = value;
				}
				$('#dataGrid').jqGrid('setGridParam',{data: mydata}).trigger( 'reloadGrid' );
			},
			shrinkToFit: false,
	        width:1900,
	        cellEdit:true,
	        cellurl:'#',
			cellsubmit:'clientArray',
	        showCheckbox:true
	    });
		
		$("#dataGrid").jqGrid("setFrozenColumns");
	}
	
	function fun_tab31(){
		$("#dataGrid").dataGrid({
			datatype: "local",
			data: mydata,
	        colModel: [			
	        	{label: '工厂', name: 'WERKS',index:"WERKS", width: "100",align:"center",sortable:false,frozen: true,
	            	formatter:function(val){
	            		return '<p style="height:20px;padding-top:7px">'+$("#werks").val()+'</p>';
	            	}
	            },
	            {label: '料号', name: 'MATNR',index:"MATNR", width: "100",align:"center",sortable:false,frozen: true,},
	            {label: '物料描述', name: 'MAKTX',index:"MAKTX", width: "200",align:"center",sortable:false,frozen: true,
	            	formatter:function(val){
	            		return val
	            	}
	            },
	            {label: '入库数量', name: 'IN_QTY',index:"IN_QTY", width: "80",align:"center",sortable:true,frozen: true},  
	            {label: '冻结数量', name: 'FREEZE_QTY',index:"FREEZE_QTY", width: "80",align:"center",sortable:true,frozen: true}, 
	            {label: '可退数量', name: 'KETUI',index:"KETUI", width: "80",align:"center",sortable:false,hidden:false}, 
	            {label: '<span style="color:red">*</span><span style="color:blue"><b>退货数量</b></span>', name: 'QTY1',index:"QTY1", width: "100",align:"center",sortable:false,editable:true,
	            	formatter:'number',editrules:{number:true},
	            	formatoptions:{decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}
	            },     
	            {label: '批次', name: 'BATCH',index:"BATCH", width: "100",align:"center",sortable:true,frozen: true},     
	            {label: '储位', name: 'BIN_CODE',index:"BIN_CODE", width: "60",align:"center",sortable:true,hidden:true},       
	            {label: '库位', name: 'LGORT',index:"LGORT", width: "60",align:"center",sortable:true},          
	            {label: '备注', name: 'MEMO',index:"MEMO", width: "100",align:"center",sortable:true,editable:true},      
	            {label: '单位', name: 'UNIT',index:"UNIT", width: "80",align:"center",sortable:true},      
	            {label: 'IO_NO', name: 'IO_NO',index:"IO_NO", width: "150",align:"center",sortable:true},      
	            {label: '入库日期', name: 'CREATE_DATE',index:"CREATE_DATE", width: "100",align:"center",sortable:true},
	            {label: 'LIFNR', name: 'LIFNR',index:"LIFNR", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'LIKTX', name: 'LIKTX',index:"LIKTX", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'SOBKZ', name: 'SOBKZ',index:"SOBKZ", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'F_BATCH', name: 'F_BATCH',index:"F_BATCH", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'BATCH', name: 'BATCH',index:"BATCH", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'PONO', name: 'PONO',index:"PONO", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'EBELP', name: 'EBELP',index:"EBELP", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'MO_NO', name: 'MO_NO',index:"MO_NO", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'MO_ITEM_NO', name: 'MO_ITEM_NO',index:"MO_ITEM_NO", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'INBOUND_NO', name: 'INBOUND_NO',index:"INBOUND_NO", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'INBOUND_ITEM_NO', name: 'INBOUND_ITEM_NO',index:"INBOUND_ITEM_NO", width: "50",align:"center",sortable:false,hidden:true},
	            ],
	        viewrecords: true,
			beforeEditCell:function(rowid, cellname, v, iRow, iCol){
				lastrow=iRow;
				lastcell=iCol;
			},
			beforeSelectRow : function(rowid, e) { // 点击复选框才选中行
				var $myGrid = $(this), i = $.jgrid.getCellIndex($(e.target).closest('td')[0]), cm = $myGrid.jqGrid('getGridParam', 'colModel');
				if (cm[i].name == 'cb') {
					$('#dataGrid').jqGrid('setSelection', rowid);
				}
				return (cm[i].name == 'cb');
			},
			onSelectRow:function(rowid,status,e){
			},
			afterSaveCell:function(rowid, cellname, value, iRow, iCol){
				var row =  $("#dataGrid").jqGrid('getRowData', rowid);
				if(cellname=='QTY1'){
					if("" == value){
						alert("退货数量不能为空！");
						$("#dataGrid").jqGrid('setCell', rowid, cellname, mydata[rowid-1].QTY1,'editable-cell');
						mydata[rowid-1].QTY3 = row.QTY2;
						check = false;
						return false;
					}
					if(parseFloat(value) > parseFloat(row.KETUI)){
						alert("退货数量不能大于可退数量！");
						$("#dataGrid").jqGrid('setCell', rowid, cellname, mydata[rowid-1].QTY1,'editable-cell');
						mydata[rowid-1].QTY1 = row.KETUI;
						check = false;
					}else{
						console.log("-->rowid-1 : " + (rowid-1));
						mydata[rowid-1].QTY1 = value;
						check = true;
					}
					
				}
				if(cellname=='REASON'){
					mydata[iRow-1].REASON = value;
				}
				$('#dataGrid').jqGrid('setGridParam',{data: mydata}).trigger( 'reloadGrid' );
			},
			shrinkToFit: false,
	        width:1900,
	        cellEdit:true,
	        cellurl:'#',
			cellsubmit:'clientArray',
	        showCheckbox:true
	    });
		
		$("#dataGrid").jqGrid("setFrozenColumns");
	}
	
	function fun_tab30(){
		$("#dataGrid").dataGrid({
			datatype: "local",
			data: mydata,
	        colModel: [		
	        	{label: '工厂', name: 'WERKS',index:"WERKS", width: "100",align:"center",sortable:false,frozen: true,
	            	formatter:function(val){
	            		return '<p style="height:20px;padding-top:7px">'+$("#werks").val()+'</p>';
	            	}
	            },	
	            {label: '料号', name: 'MATNR',index:"MATNR", width: "100",align:"center",sortable:false,frozen: true,},
	            {label: '物料描述', name: 'MAKTX',index:"MAKTX", width: "200",align:"center",sortable:false,frozen: true,
	            	formatter:function(val){
	            		return val
	            	}
	            },
	            {label: '入库数量', name: 'IN_QTY',index:"IN_QTY", width: "80",align:"center",sortable:true,frozen: true},  
	            {label: '冻结数量', name: 'FREEZE_QTY',index:"FREEZE_QTY", width: "80",align:"center",sortable:true,frozen: true}, 
	            {label: '可退数量', name: 'KETUI',index:"KETUI", width: "80",align:"center",sortable:false,hidden:false}, 
	            {label: '<span style="color:red">*</span><span style="color:blue"><b>退货数量</b></span>', name: 'QTY1',index:"QTY1", width: "100",align:"center",sortable:false,editable:true,
	            	formatter:'number',editrules:{number:true},
	            	formatoptions:{decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}
	            },     
	            {label: '批次', name: 'BATCH',index:"BATCH", width: "100",align:"center",sortable:true,frozen: true},     
	            {label: '储位', name: 'BIN_CODE',index:"BIN_CODE", width: "60",align:"center",sortable:true,hidden:true},       
	            {label: '库位', name: 'LGORT',index:"LGORT", width: "60",align:"center",sortable:true},          
	            {label: '备注', name: 'MEMO',index:"MEMO", width: "100",align:"center",sortable:true,editable:true},      
	            {label: '单位', name: 'UNIT',index:"UNIT", width: "80",align:"center",sortable:true},      
	            {label: '生产订单', name: 'MO_NO',index:"MO_NO", width: "100",align:"center",sortable:true},      
	            {label: '订单行项目', name: 'MO_ITEM_NO',index:"MO_ITEM_NO", width: "100",align:"center",sortable:true},      
	            {label: '入库日期', name: 'CREATE_DATE',index:"CREATE_DATE", width: "100",align:"center",sortable:true},
	            {label: 'LIFNR', name: 'LIFNR',index:"LIFNR", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'LIKTX', name: 'LIKTX',index:"LIKTX", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'SOBKZ', name: 'SOBKZ',index:"SOBKZ", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'F_BATCH', name: 'F_BATCH',index:"F_BATCH", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'BATCH', name: 'BATCH',index:"BATCH", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'PONO', name: 'PONO',index:"PONO", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'EBELP', name: 'EBELP',index:"EBELP", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'MO_NO', name: 'MO_NO',index:"MO_NO", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'MO_ITEM_NO', name: 'MO_ITEM_NO',index:"MO_ITEM_NO", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'INBOUND_NO', name: 'INBOUND_NO',index:"INBOUND_NO", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'INBOUND_ITEM_NO', name: 'INBOUND_ITEM_NO',index:"INBOUND_ITEM_NO", width: "50",align:"center",sortable:false,hidden:true},
	            ],
	        viewrecords: true,
			beforeEditCell:function(rowid, cellname, v, iRow, iCol){
				lastrow=iRow;
				lastcell=iCol;
			},
			beforeSelectRow : function(rowid, e) { // 点击复选框才选中行
				var $myGrid = $(this), i = $.jgrid.getCellIndex($(e.target).closest('td')[0]), cm = $myGrid.jqGrid('getGridParam', 'colModel');
				if (cm[i].name == 'cb') {
					$('#dataGrid').jqGrid('setSelection', rowid);
				}
				return (cm[i].name == 'cb');
			},
			onSelectRow:function(rowid,status,e){
			},
			afterSaveCell:function(rowid, cellname, value, iRow, iCol){
				var row =  $("#dataGrid").jqGrid('getRowData', rowid);
				if(cellname=='QTY1'){
					if("" == value){
						alert("退货数量不能为空！");
						$("#dataGrid").jqGrid('setCell', rowid, cellname, mydata[rowid-1].QTY1,'editable-cell');
						mydata[rowid-1].QTY3 = row.QTY2;
						check = false;
						return false;
					}
					if(parseFloat(value) > parseFloat(row.KETUI)){
						alert("退货数量不能大于可退数量！");
						$("#dataGrid").jqGrid('setCell', rowid, cellname, mydata[rowid-1].QTY1,'editable-cell');
						mydata[rowid-1].QTY1 = row.KETUI;
						check = false;
					}else{
						console.log("-->rowid-1 : " + (rowid-1));
						mydata[rowid-1].QTY1 = value;
						check = true;
					}
					
				}
				if(cellname=='REASON'){
					mydata[iRow-1].REASON = value;
				}
				$('#dataGrid').jqGrid('setGridParam',{data: mydata}).trigger( 'reloadGrid' );
			},
			shrinkToFit: false,
	        width:1900,
	        cellEdit:true,
	        cellurl:'#',
			cellsubmit:'clientArray',
	        showCheckbox:true
	    });
		
		$("#dataGrid").jqGrid("setFrozenColumns");
	}
	
	function fun_tab29(){
		$("#dataGrid").dataGrid({
			datatype: "local",
			data: mydata,
	        colModel: [	
	        	{label: '工厂', name: 'WERKS',index:"WERKS", width: "100",align:"center",sortable:false,frozen: true,
	            	formatter:function(val){
	            		return '<p style="height:20px;padding-top:7px">'+$("#werks").val()+'</p>';
	            	}
	            },		
	            {label: '料号', name: 'MATNR',index:"MATNR",height:"20", width: "100",align:"center",sortable:false,frozen: true,},
	            {label: '物料描述', name: 'MAKTX',index:"MAKTX", width: "200",align:"center",sortable:false,frozen: true,
	            	formatter:function(val){
	            		return val
	            	}
	            },
	            {label: '冻结数量', name: 'FREEZE_QTY',index:"FREEZE_QTY", width: "80",align:"center",sortable:true,frozen: true},  
	            {label: '批次', name: 'BATCH',index:"BATCH", width: "100",align:"center",sortable:true,frozen: true},     
	            {label: '<span style="color:red">*</span><span style="color:blue"><b>退货数量</b></span>', name: 'QTY1',index:"QTY1", width: "100",align:"center",sortable:false,editable:true,
	            	formatter:'number',editrules:{number:true},
	            	formatoptions:{decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}
	            },     
	            {label: '储位', name: 'BIN_CODE',index:"BIN_CODE", width: "60",align:"center",sortable:true,hidden:true},       
	            {label: '库位', name: 'LGORT',index:"LGORT", width: "60",align:"center",sortable:true},          
	            {label: '备注', name: 'MEMO',index:"MEMO", width: "100",align:"center",sortable:true},      
	            {label: '单位', name: 'UNIT',index:"UNIT", width: "80",align:"center",sortable:true},      
	            {label: '库存类型', name: 'SOBKZ',index:"SOBKZ", width: "80",align:"center",sortable:true},      
	            {label: '采购订单', name: 'PONO',index:"PONO", width: "100",align:"center",sortable:true},      
	            {label: '需求跟踪号', name: 'BEDNR',index:"BEDNR", width: "100",align:"center",sortable:true},      
	            {label: '供应商代码', name: 'LIFNR',index:"LIFNR", width: "100",align:"center",sortable:true},      
	            {label: '供应商名称', name: 'LIKTX',index:"LIKTX", width: "100",align:"center",sortable:true},
	            {label: 'group', name: 'group',index:"group", width: "50",align:"center",sortable:false},
	            {label: 'TRID', name: 'TRID',index:"TRID", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'F_BATCH', name: 'F_BATCH',index:"F_BATCH", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'PONO', name: 'PONO',index:"PONO", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'EBELP', name: 'EBELP',index:"EBELP", width: "50",align:"center",sortable:false,hidden:true},
	            
	            ],
	        viewrecords: true,
			grouping: true,
			groupingView: {
				groupField: ["group"],
				groupColumnShow: [false],
				groupText: ["<b>{0}</b>"],
				groupOrder: ["asc"],
				groupSummary: [true],
				groupCollapse: false
			},
			beforeEditCell:function(rowid, cellname, v, iRow, iCol){
				lastrow=iRow;
				lastcell=iCol;
			},
			beforeSelectRow : function(rowid, e) { // 点击复选框才选中行
				var $myGrid = $(this), i = $.jgrid.getCellIndex($(e.target).closest('td')[0]), cm = $myGrid.jqGrid('getGridParam', 'colModel');
				if (cm[i].name == 'cb') {
					$('#dataGrid').jqGrid('setSelection', rowid);
				}
				return (cm[i].name == 'cb');
			},
			onSelectRow:function(rowid,status,e){
			},
			afterSaveCell:function(rowid, cellname, value, iRow, iCol){
				var row =  $("#dataGrid").jqGrid('getRowData', rowid);
				console.log('-->row.FREEZE_QTY = ' + row.FREEZE_QTY)
				if(cellname=='QTY1'){
					if("" == value){
						alert("退货数量不能为空！");
						$("#dataGrid").jqGrid('setCell', rowid, cellname, mydata[rowid-1].QTY1,'editable-cell');
						mydata[rowid-1].QTY3 = row.QTY2;
						check = false;
						return false;
					}
					if(parseFloat(value) > parseFloat(row.FREEZE_QTY)){
						alert("退货数量不能大于冻结数量！");
						$("#dataGrid").jqGrid('setCell', rowid, cellname, mydata[rowid-1].QTY1,'editable-cell');
						mydata[rowid-1].QTY1 = row.FREEZE_QTY;
						check = false;
					}else{
						console.log("-->rowid-1 : " + (rowid-1));
						mydata[rowid-1].QTY1 = value;
						check = true;
					}
					
				}
				if(cellname=='REASON'){
					mydata[iRow-1].REASON = value;
				}
				//$('#dataGrid').jqGrid('setGridParam',{data: mydata}).trigger( 'reloadGrid' );
			},
			shrinkToFit: false,
	        width:1900,
	        cellEdit:true,
	        cellurl:'#',
			cellsubmit:'clientArray',
	        showCheckbox:true
	    });
		
		$("#dataGrid").jqGrid("setFrozenColumns");
	}
	
	function fun_tab28(){

		$("#dataGrid").dataGrid({
			datatype: "local",
			data: mydata,
	        colModel: [			
	        	{label: '工厂', name: 'WERKS',index:"WERKS", width: "80",align:"center",sortable:false,frozen: true,
	            	formatter:function(val){
	            		return '<p style="height:20px;padding-top:7px">'+$("#werks").val()+'</p>';
	            	}
	            },
	            {label: '料号', name: 'MATNR',index:"MATNR", width: "100",align:"center",sortable:false,frozen: true,},
	            {label: '物料描述', name: 'MAKTX',index:"MAKTX", width: "200",align:"center",sortable:false,frozen: true,
	            	formatter:function(val){
	            		return val
	            	}
	            },
	            {label: '进仓数量', name: 'IN_QTY',index:"IN_QTY", width: "80",align:"center",sortable:true,frozen: true},  
	            {label: '冻结数量', name: 'FREEZE_QTY',index:"FREEZE_QTY", width: "80",align:"center",sortable:true,frozen: true},  
	            {label: '批次', name: 'BATCH',index:"BATCH", width: "100",align:"center",sortable:true,frozen: true},     
	            {label: '<span style="color:red">*</span><span style="color:blue"><b>退货数量</b></span>', name: 'QTY1',index:"QTY1", width: "100",align:"center",sortable:false,editable:true,
	            	formatter:'number',editrules:{number:true},
	            	formatoptions:{decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}
	            },     
	            {label: '收货单号', name: 'RECEIPT_NO',index:"RECEIPT_NO", width: "100",align:"center",sortable:true},  
	            {label: '行项目号', name: 'RECEIPT_ITEM_NO',index:"RECEIPT_ITEM_NO", width: "100",align:"center",sortable:true},
	            {label: '<span style="color:red">*</span><span style="color:blue"><b>库位</b></span>', name: 'LGORT',index:"LGORT", width: "60",align:"center",sortable:true,editable:true,},
	            {label: '备注', name: 'MEMO',index:"MEMO", width: "100",align:"center",sortable:true},      
	            {label: '单位', name: 'UNIT',index:"UNIT", width: "80",align:"center",sortable:true},      
	            {label: '库存类型', name: 'SOBKZ',index:"SOBKZ", width: "80",align:"center",sortable:true},      
	            {label: '采购订单', name: 'PONO',index:"PONO", width: "100",align:"center",sortable:true},      
	            {label: '需求跟踪号', name: 'BEDNR',index:"BEDNR", width: "100",align:"center",sortable:true},      
	            {label: '供应商代码', name: 'LIFNR',index:"LIFNR", width: "100",align:"center",sortable:true},      
	            {label: '供应商名称', name: 'LIKTX',index:"LIKTX", width: "100",align:"center",sortable:true},
	            {label: 'group', name: 'group',index:"group", width: "50",align:"center",sortable:false},
	            {label: 'TRID', name: 'TRID',index:"TRID", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'F_BATCH', name: 'F_BATCH',index:"F_BATCH", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'PONO', name: 'PONO',index:"PONO", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'EBELP', name: 'EBELP',index:"EBELP", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'ASNNO', name: 'ASNNO',index:"ASNNO", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'ASNITM', name: 'ASNITM',index:"ASNITM", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'BUSINESS_TYPE', name: 'BUSINESS_TYPE',index:"BUSINESS_TYPE", width: "50",align:"center",sortable:false,hidden:true},
	            ],
	        viewrecords: true,
			grouping: true,
			groupingView: {
				groupField: ["group"],
				groupColumnShow: [false],
				groupText: ["<b>{0}</b>"],
				groupOrder: ["asc"],
				groupSummary: [true],
				groupCollapse: false
			},
			beforeEditCell:function(rowid, cellname, v, iRow, iCol){
				lastrow=iRow;
				lastcell=iCol;
			},
			beforeSelectRow : function(rowid, e) { // 点击复选框才选中行
				var $myGrid = $(this), i = $.jgrid.getCellIndex($(e.target).closest('td')[0]), cm = $myGrid.jqGrid('getGridParam', 'colModel');
				if (cm[i].name == 'cb') {
					$('#dataGrid').jqGrid('setSelection', rowid);
				}
				return (cm[i].name == 'cb');
			},
			onSelectRow:function(rowid,status,e){
				console.log("-->rowid : " + rowid);
			},
			afterSaveCell:function(rowid, cellname, value, iRow, iCol){
				var row =  $("#dataGrid").jqGrid('getRowData', rowid);
				if(cellname=='QTY1'){
					console.log("-->rowid-1 : " + (rowid-1));
					if("" == value){
						alert("退货数量不能为空！");
						$("#dataGrid").jqGrid('setCell', rowid, cellname, mydata[rowid-1].QTY1,'editable-cell');
						mydata[rowid-1].QTY3 = row.QTY2;
						check = false;
						return false;
					}
					if(parseFloat(value) > parseFloat(row.FREEZE_QTY)){
						alert("退货数量不能大于冻结数量！");
						$("#dataGrid").jqGrid('setCell', rowid, cellname, mydata[rowid-1].QTY1,'editable-cell');
						mydata[rowid-1].QTY1 = row.FREEZE_QTY;
						check = false;
					}else{
						mydata[rowid-1].QTY1 = value;
						check = true;
					}
				}
				if(cellname=='REASON'){
					mydata[iRow-1].REASON = value;
				}
				$('#dataGrid').jqGrid('setGridParam',{data: mydata}).trigger( 'reloadGrid' );
			},
			shrinkToFit: false,
	        width:1900,
	        cellEdit:true,
	        cellurl:'#',
			cellsubmit:'clientArray',
	        showCheckbox:true
	    });
		
		$("#dataGrid").jqGrid("setFrozenColumns");
	}
	
	function fun_tab27(){
		$("#dataGrid").dataGrid({
			datatype: "local",
			data: mydata,
	        colModel: [			
	        	{label: '工厂', name: 'WERKS',index:"WERKS", width: "100",align:"center",sortable:false,frozen: true,
	            	formatter:function(val){
	            		return '<p style="height:20px;padding-top:7px">'+$("#werks").val()+'</p>';
	            	}
	            },
	            {label: '料号', name: 'MATNR',index:"MATNR", width: "100",align:"center",sortable:false,frozen: true,},
	            {label: '物料描述', name: 'MAKTX',index:"MAKTX", width: "200",align:"center",sortable:false,frozen: true,
	            	formatter:function(val){
	            		return val
	            	}
	            },
	            {label: '冻结数量', name: 'FREEZE_QTY',index:"FREEZE_QTY", width: "80",align:"center",sortable:true,frozen: true},  
	            {label: '批次', name: 'BATCH',index:"BATCH", width: "100",align:"center",sortable:true,frozen: true},     
	            {label: '<span style="color:red">*</span><span style="color:blue"><b>退货数量</b></span>', name: 'QTY1',index:"QTY1", width: "100",align:"center",sortable:false,editable:true,
	            	formatter:'number',editrules:{number:true},
	            	formatoptions:{decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}
	            },     
	            {label: '库位', name: 'LGORT',index:"LGORT", width: "60",align:"center",sortable:true},
	            {label: '备注', name: 'MEMO',index:"MEMO", width: "100",align:"center",sortable:true},      
	            {label: '基本单位', name: 'MEINS',index:"MEINS", width: "80",align:"center",sortable:true},      
	            {label: '库存类型', name: 'SOBKZ',index:"SOBKZ", width: "80",align:"center",sortable:true},      
	            {label: 'SAP交货单', name: 'VBELN',index:"VBELN", width: "100",align:"center",sortable:true},     
	            {label: '行项目', name: 'POSNR',index:"POSNR", width: "80",align:"center",sortable:true},     
	            {label: '采购订单', name: 'VGBEL',index:"VGBEL", width: "100",align:"center",sortable:true},      
	            {label: '行项目', name: 'VGPOS',index:"VGPOS", width: "100",align:"center",sortable:true},      
	            {label: '需求跟踪号', name: 'BEDNR',index:"BEDNR", width: "100",align:"center",sortable:true},
	            {label: '供应商代码', name: 'LIFNR',index:"LIFNR", width: "100",align:"center",sortable:true},      
	            {label: '供应商名称', name: 'LIKTX',index:"LIKTX", width: "100",align:"center",sortable:true},
	            {label: 'DA_GROUP', name: 'DA_GROUP',index:"DA_GROUP", width: "50",align:"center",sortable:false},
	            {label: 'TRID', name: 'TRID',index:"TRID", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'SAP_OUT_NO', name: 'SAP_OUT_NO',index:"SAP_OUT_NO", width: "50",align:"center",sortable:false,hidden:true},
	            {label: 'F_BATCH', name: 'F_BATCH',index:"F_BATCH", width: "50",align:"center",sortable:false,hidden:true},
	            
	            ],
	        viewrecords: true,
			grouping: true,
			groupingView: {
				groupField: ["DA_GROUP"],
				groupColumnShow: [false],
				groupText: ["<b>{0}</b>"],
				groupOrder: ["asc"],
				groupSummary: [true],
				groupCollapse: false
			},
			beforeEditCell:function(rowid, cellname, v, iRow, iCol){
				lastrow=iRow;
				lastcell=iCol;
			},
			beforeSelectRow : function(rowid, e) { // 点击复选框才选中行
				var $myGrid = $(this), i = $.jgrid.getCellIndex($(e.target).closest('td')[0]), cm = $myGrid.jqGrid('getGridParam', 'colModel');
				if (cm[i].name == 'cb') {
					$('#dataGrid').jqGrid('setSelection', rowid);
				}
				return (cm[i].name == 'cb');
			},
			onSelectRow:function(rowid,status,e){
				console.log("-->rowid : " + rowid);
			},
			afterSaveCell:function(rowid, cellname, value, iRow, iCol){
				var row =  $("#dataGrid").jqGrid('getRowData', rowid);
				if(cellname=='QTY1'){
					if("" == value){
						alert("退货数量不能为空！");
						$("#dataGrid").jqGrid('setCell', rowid, cellname, mydata[rowid-1].QTY1,'editable-cell');
						mydata[rowid-1].QTY3 = row.QTY2;
						check = false;
						return false;
					}
					if(parseFloat(value) > parseFloat(row.FREEZE_QTY)){
						alert("退货数量不能大于冻结数量！");
						$("#dataGrid").jqGrid('setCell', rowid, cellname, mydata[rowid-1].QTY1,'editable-cell');
						mydata[rowid-1].QTY1 = row.FREEZE_QTY;
						check = false;
					}else{
						mydata[rowid-1].QTY1 = value;
						check = true;
					}
				}
				if(cellname=='REASON'){
					mydata[iRow-1].REASON = value;
				}
				$('#dataGrid').jqGrid('setGridParam',{data: mydata}).trigger( 'reloadGrid' );
			},
			shrinkToFit: false,
	        width:1900,
	        cellEdit:true,
	        cellurl:'#',
			cellsubmit:'clientArray',
	        showCheckbox:true
	    });
		
		$("#dataGrid").jqGrid("setFrozenColumns");
	}
	
	
});
function freason(){
	console.log("freason");
	var reason = "";
	var tdIndex = 0;
	var trs=$("#dataGrid").children("tbody").children("tr");
	$.each(trs,function(index,tr){
		var cbx=$(tr).find("td").find("input").attr("type");
		if(cbx!=undefined){	
			var c_checkbox=$(tr).find('input[type=checkbox]');
			var ischecked=$(c_checkbox).is(":checked");
			if(ischecked){
				if(reason == ""){
					reason = $(tr).find("td").eq(20).html();
					var che =$(tr).find("td").eq(20).find("input").attr("type");
					if(che!=undefined){	
						tdIndex = index;
					}else{
						return false;
					}
					
				}
			}
		}
	});
	if(reason == ""){
		if("25"==$("#business_name").val()){
			reason = mydata[0].REASON;
		}else if("26"==$("#business_name").val()){
			reason = mydata[0].REASON;
		}
		else if("27"==$("#business_name").val()){
			reason = mydata2[0].REASON;
		}
	}
	
	var trs=$("#dataGrid").children("tbody").children("tr");
	$.each(trs,function(index,tr){
		if(index >tdIndex){
			if("undefined" != typeof($(tr).attr("id"))){
				$(tr).find("td").eq(20).html(reason);
			}
		}
	});
}

$('body').on('DOMNodeInserted', 'input', function () {
	
	$("input[name='REASON']").typeahead({
		source : function(input, process) {
			$.get(baseURL+"common/getReasonData", {"REASON_DESC" : input}, function(response) {
				var data=response.reason;
				busNumberlist = data;
				var results = new Array();
				$.each(data, function(index, value) {
					results.push(value.REASON_DESC);
				})
				return process(results);
			}, 'json');
		},
		items : 15,
		matcher : function(item) {				
			return true;
		},
		updater : function(item) {				
			return item;
		}
	});
});

function fun_ShowMoreTable1(){
	$("#dataGrid_1").dataGrid({
		datatype: "local",
		data: mydata_mat,
        colModel: [			
            {label: '<span style="color:red">*</span><span style="color:blue"><b>订单号</b></span>', name: 'AUFNR',index:"AUFNR", width: "200",align:"center",sortable:false,editable:true}, 
            {label: '删除', name: 'AUFNR',index:"AUFNR", width: "80",align:"center",sortable:false,editable:false,
            	formatter:function(value, name, record){
            		return '<i title="删除" aria-hidden="true" onclick="$(this).parent().parent().remove();delMoreTr2();" class="fa fa-times"></i>';
            	}, 
            },
            {label: '', name: '',index:"AUFNR", width: "150",align:"center",sortable:false,editable:false} 
        ],
		shrinkToFit: false,
        width:600,
        cellEdit:true,
        cellurl:'#',
		cellsubmit:'clientArray',
        showCheckbox:false
    });
}
function dataGridMorePaste1(){
	$('#dataGrid_1').bind('paste', function(e) {
		e.preventDefault(); // 消除默认粘贴
		// 获取粘贴板数据
		var data = null;
		var clipboardData = window.clipboardData || e.originalEvent.clipboardData; // IE || chrome
		data = clipboardData.getData('Text');
		// 解析数据
		var arr = data.split('\n').filter(function(item) { // 兼容Excel行末\n，防止出现多余空行
			return (item !== "")
		}).map(function(item) {
			return item.split("\t");
		});
		// 输出至网页表格
		var tab = this; // 表格DOM
		var td = $(e.target).parents('td');
		var startRow = td.parents('tr')[0].rowIndex;
		var startCell = td[0].cellIndex;
		var rows = tab.rows.length; // 总行数
		for (var i = 0; i < arr.length; i++) {
			if(startRow + i >= rows){
				$("#newOperation_1").click();
			}
			var cells = tab.rows[startRow + i].cells.length; // 该行总列数
			for (var j = 0; j < arr[i].length && startCell + j < cells; j++) {
				var cell = tab.rows[startRow + i].cells[startCell + j];
				$(cell).find(':text').val(arr[i][j]); //找到cell下的input:text，设置value
				$('#dataGrid_1').jqGrid("saveCell", startRow + i, 1);
				$(cell).html(arr[i][j]);
			}
		}
	});
}

function fun_ShowMoreTable2(){
	$("#dataGrid_2").dataGrid({
		datatype: "local",
		data: mydata_mat,
        colModel: [			
            {label: '<span style="color:red">*</span><span style="color:blue"><b>物料号</b></span>', name: 'AUFNR',index:"AUFNR", width: "200",align:"center",sortable:false,editable:true}, 
            {label: '删除', name: 'AUFNR',index:"AUFNR", width: "80",align:"center",sortable:false,editable:false,
            	formatter:function(value, name, record){
            		return '<i title="删除" aria-hidden="true" onclick="$(this).parent().parent().remove();delMoreTr2();" class="fa fa-times"></i>';
            	}, 
            },
            {label: '', name: '',index:"AUFNR", width: "150",align:"center",sortable:false,editable:false} 
        ],
		shrinkToFit: false,
        width:600,
        cellEdit:true,
        cellurl:'#',
		cellsubmit:'clientArray',
        showCheckbox:false
    });
}
function dataGridMorePaste2(){
	$('#dataGrid_2').bind('paste', function(e) {
		e.preventDefault(); // 消除默认粘贴
		// 获取粘贴板数据
		var data = null;
		var clipboardData = window.clipboardData || e.originalEvent.clipboardData; // IE || chrome
		data = clipboardData.getData('Text');
		// 解析数据
		var arr = data.split('\n').filter(function(item) { // 兼容Excel行末\n，防止出现多余空行
			return (item !== "")
		}).map(function(item) {
			return item.split("\t");
		});
		// 输出至网页表格
		var tab = this; // 表格DOM
		var td = $(e.target).parents('td');
		var startRow = td.parents('tr')[0].rowIndex;
		var startCell = td[0].cellIndex;
		var rows = tab.rows.length; // 总行数
		for (var i = 0; i < arr.length; i++) {
			if(startRow + i >= rows){
				$("#newOperation_2").click();
			}
			var cells = tab.rows[startRow + i].cells.length; // 该行总列数
			for (var j = 0; j < arr[i].length && startCell + j < cells; j++) {
				var cell = tab.rows[startRow + i].cells[startCell + j];
				$(cell).find(':text').val(arr[i][j]); //找到cell下的input:text，设置value
				$('#dataGrid_2').jqGrid("saveCell", startRow + i, 1);
				$(cell).html(arr[i][j]);
			}
		}
	});
}

function delMoreTr1(){
	var trs=$("#dataGrid_1").children("tbody").children("tr");
	var reIndex = 1;
	$.each(trs,function(index,tr){
		var td = $(tr).find("td").eq(0);
		if(index > 0){
			td.text(reIndex);
			$(tr).attr('id',reIndex);
			reIndex++;
		}
	});
}
function delMoreTr2(){
	var trs=$("#dataGrid_2").children("tbody").children("tr");
	var reIndex = 1;
	$.each(trs,function(index,tr){
		var td = $(tr).find("td").eq(0);
		if(index > 0){
			td.text(reIndex);
			$(tr).attr('id',reIndex);
			reIndex++;
		}
	});
}

function getLgortList(WERKS) {
	//查询库位
	$.ajax({
		url : baseUrl + "in/wmsinbound/lgortlist",
		data : {
			"DEL" : '0',
			"WERKS" : WERKS,
			"SOBKZ" : "Z",
			"BAD_FLAG" : '0'
		},
		success : function(resp) {
			vm.lgortlist = resp.result;
		}
	});
}