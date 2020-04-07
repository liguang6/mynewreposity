var dataGrid;
var darows=[];
var lastrow,lastcell,lastcellname;
var gridData=[];
var gr_area_list=[];
var sobkz_list=[];
var last_scan_ele="";
var productdata = [{ id: "1",AUFNR: ""},{ id: "2",AUFNR: ""}];		//初始化批量单据号数据
var vm = new Vue({
	el:'#page_div',
	data:{
		showList:true,
		formUrl:baseUrl+"in/wmsinreceipt/listScmMat",
		receipt_type:'',//收货类型
		WERKS:"",
		whList:[],
		WH_NUMBER:'',//仓库号
		businessList:[],
		PZ_DATE:getCurDate(),//凭证日期
		JZ_DATE:getCurDate(),//记账日期
		ASNNO:"",//送货单号（单据号）
		PO_NO:"",//SAP采购订单号
		SAP_OUT_NO:'',//SAP交货单号
		SAP_MATDOC_NO:'',//303凭证号
		skInfoList:[],//包装箱信息
		LIFNR:"",//供应商代码
		PZ_YEAR:new Date().getFullYear(),//凭证年度
		lgortList:[],//库位列表
		VENDOR_FLAG:'0',
		LIFNR_WPO: {},//无PO选择的供应商信息
	},
	watch:{
		receipt_type:{
			handler:function(newVal,oldVal){
				vm.showList=true;
				//alert(newVal)
				if(newVal=='01'){//SCM收料
					vm.formUrl=baseUrl+"in/wmsinreceipt/listScmMat";
				}
				if(newVal == '78' ){
					vm.formUrl=baseUrl+"in/wmsinreceipt/listCloudMat";
				}
				if(newVal=='02'){//SAP采购订单收料
					vm.formUrl=baseUrl+"in/wmsinreceipt/listPOMat";
				}
				if(newVal=='03'){
					vm.formUrl=baseUrl+"in/wmsinreceipt/listOutMat";
				}
				if(newVal=='04'){
					vm.formUrl=baseUrl+"in/wmsinreceipt/listPOCFMat";
				}
				if(newVal=='06'){//303调拨收料，获取接收库位列表
					vm.formUrl=baseUrl+"in/wmsinreceipt/list303Mat";
					vm.lgortList=getLgortList(vm.WERKS,null,null,'Z');
				}
				if(newVal=='09'){//303调拨收料(A)，获取接收库位列表
					vm.formUrl=baseUrl+"in/wmsinreceipt/list303AMat";
					vm.lgortList=getLgortList(vm.WERKS,null,null,'Z');
				}
				if(newVal=='08'){//SAP交货单收料(A)，获取接收库位列表
					vm.formUrl=baseUrl+"in/wmsinreceipt/listOutAMat";
					vm.lgortList=getLgortList(vm.WERKS,null,null,'Z');
				}

				if(newVal=='05'){
					vm.formUrl="#";
					var setting = getPlantSetting(vm.WH_NUMBER);
					vm.VENDOR_FLAG = setting.VENDOR_FLAG;
					gr_area_list=getGrAreaList(vm.WERKS);
					sobkz_list=getDictList('SOBKZ');
				}

				if(newVal=='07'){
					vm.formUrl=baseUrl+"in/wmsinreceipt/listPOAMat";
				}

				vm.$nextTick(function(){//数据渲染后调用
					vm.WH_NUMBER=$("#WH_NUMBER").find("option").first().val();

					showGrid();
				})
				vm.skInfoList="";

			}
		},
		WERKS:{
			handler:function(newVal,oldVal){
				$.ajax({
					url:baseUrl + "common/getWhDataByWerks",
					data:{"WERKS":newVal/*,"MENU_KEY":"IN_RECEIPT"*/},
					success:function(resp){
						vm.whList = resp.data;
						if(resp.data.length>0){
							vm.WH_NUMBER=resp.data[0].WH_NUMBER
						}

					}
				})

				this.$nextTick(function(){
					setWerks();

					if(vm.receipt_type=='06'){
						vm.lgortList=getLgortList(vm.WERKS,null,null,'Z')
					}
					if(vm.receipt_type=='05'){
						/*var BIN_CODE="";
                        gr_area_list=getGrAreaList(vm.WERKS);
                          if(gr_area_list.length>0){
                             BIN_CODE=gr_area_list.split(";")[0].split(":")[0];
                          }

                          var rows=$("#dataGrid").jqGrid('getRowData');
                          $.each(rows,function(i,row){
                              //console.info(i+":"+BIN_CODE+row.ID)
                              $("#dataGrid").jqGrid('setCell', row.ID, 'BIN_CODE', BIN_CODE||"&nbsp;", 'editable-cell');
                          });
                           //清除表格物料数据
                          $.each(rows,function(i,row){
                              $("#dataGrid").jqGrid('setCell', row.ID, 'MATNR', "&nbsp;", 'editable-cell');
                          });	*/
						/*layer.confirm("切换工厂会清除表格中物料数据，是否切换？",function(){

                        })*/
						var setting=getPlantSetting(vm.WH_NUMBER);
						vm.VENDOR_FLAG=setting.VENDOR_FLAG;
						gr_area_list=getGrAreaList(vm.WERKS);
						$('#dataGrid').jqGrid("clearGridData");
					}
				})
			}
		},
		WH_NUMBER:{
			handler:function(newVal,oldVal){
				if(vm.receipt_type=='09'){
					vm.query();
				}
				this.$nextTick(function(){
					setWH();
				})
			}
		},
	},
	methods: {
		query: function () {
			//SCM收货单
			if(vm.receipt_type=='01'){
				if(vm.asnno.trim().length==0){
					js.showErrorMessage("请输入单据号！");
					return false;
				}
			}
			//SCM收货单
			if(vm.receipt_type=='78'){
				if(vm.ASNNO.trim().length==0){
					js.showErrorMessage("请输入单据号！");
					return false;
				}
				//扫描枪扫描时截取单号P:C161;V:30024;VN:12345;DN:N190800000000139;
				if(vm.ASNNO.trim().indexOf("DN:") > 0 ){
					let scan_str = vm.ASNNO.trim()
					//console.log(scan_str.substring(scan_str.indexOf("DN:")).substring(3, scan_str.substring(scan_str.indexOf("DN:")).indexOf(";")))
					vm.ASNNO = scan_str.substring(scan_str.indexOf("DN:")).substring(3, scan_str.substring(scan_str.indexOf("DN:")).indexOf(";"))
				}
			}
			//SAP采购订单
			if(vm.receipt_type=='02'||vm.receipt_type=='04'){
				var PO_NO = $("#PO_NO").val();
				if(PO_NO ==""){
					js.showErrorMessage("请输入单据号！");
					return false;
				}
			}
			//SAP交货单
			if(vm.receipt_type=='03'){
				if(vm.SAP_OUT_NO.trim().length==0){
					js.showErrorMessage("请输入单据号！");
					return false;
				}
			}
			//303调拨
			if(vm.receipt_type=='06'){
				if(vm.SAP_MATDOC_NO.trim().length==0){
					js.showErrorMessage("请输入303凭证号！");
					return false;
				}
			}
			//303调拨（A）
			if(vm.receipt_type=='09'){
				if(vm.SAP_MATDOC_NO.trim().length==0){
					js.showErrorMessage("请输入303凭证号！");
					return false;
				}
				if(vm.WERKS.trim().length==0){
					js.showErrorMessage("请输入工厂！");
					return false;
				}
				if(vm.WH_NUMBER.trim().length==0){
					js.showErrorMessage("请输入仓库号！");
					return false;
				}
			}
			//SAP采购订单（A）
			if(vm.receipt_type=='07'){
				if(vm.PO_NO.trim().length==0){
					js.showErrorMessage("请输入单据号！");
					return false;
				}
				if(vm.WH_NUMBER.trim().length==0){
					js.showErrorMessage("请输入仓库号！");
					return false;
				}
			}
			//SAP交货单（A）
			if(vm.receipt_type=='08'){
				if(vm.SAP_OUT_NO.trim().length==0){
					js.showErrorMessage("请输入单据号！");
					return false;
				}
				if(vm.WH_NUMBER.trim().length==0){
					js.showErrorMessage("请输入仓库号！");
					return false;
				}
			}
			js.loading();
			vm.showList=true;

			vm.$nextTick(function(){//数据渲染后调用

				$("#dataGrid").jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
				js.closeLoading();
				if(vm.receipt_type=='01' || vm.receipt_type=='78' ){
					vm.skInfoList=getSKInfo();
				}
				//SAP采购订单收货（A），收货工厂取订单行项目中收货工厂
				if(vm.receipt_type=='07'){
					var rows=$("#dataGrid").jqGrid('getRowData');
					vm.WERKS=rows[0].WERKS;
				}
				//SAP交货单收货（A），收货工厂取订单行项目中收货工厂
				if(vm.receipt_type=='08'){
					var rows=$("#dataGrid").jqGrid('getRowData');
					vm.WERKS=rows[0].WERKS;
				}
			})

		},
		update: function (event,id) {
			//var id = getSelectedRow();
			if(id == null){
				return ;
			}
		},
		getReceiptList:function(){
			var docNo=$("#docNo").val();
			if(docNo.trim().length>0){
				vm.show=true;
			}
			if(docNo.trim().length==0){
				vm.show=false;
			}
		},
		countMat:function(){
			$("#countMat tbody").html("");
			var rows=$("#dataGrid").jqGrid('getRowData');
			var last_mat=[];
			var last_vdcd="";
			var mat_id="";
			$.each(rows,function(i,row){
				var QTY=Number(row.RECEIPT_QTY);
				if(vm.receipt_type=='01' || vm.receipt_type=='78'){
					QTY=Number(row.QTY);
				}

				if(last_mat.indexOf(row.MATNR+row.LIFNR)>=0){//按供应商和物料号汇总
					var count_td=$("#"+mat_id).children("td").eq(1);
					$(count_td).html(Number($(count_td).html())+QTY);
				}else{
					mat_id=row.MATNR+row.LIFNR;
					last_mat.push(mat_id);
					var tr=$("<tr id='"+mat_id+"' />");
					$("<td />").html(row.MATNR).appendTo(tr);
					$("<td />").html(QTY).appendTo(tr);
					$("<td />").html(row.LIKTX).appendTo(tr);
					$(tr).appendTo($("#countMat tbody"));
				}
			})

			layer.open({
				type: 1,
				title:['料号汇总','font-size:18px'],
				closeBtn: 1,
				btn:['关闭'],
				offset:'t',
				area: '700px',
				skin: 'layui-bg-green', //没有背景色
				shadeClose: false,
				content: $('#countMat').html()
			});
		},
		//SCM送货单显示标签信息
		showLabelInfo: function(){
			if(vm.skInfoList == null || vm.skInfoList == undefined || vm.skInfoList.length<=0){
				js.showErrorMessage("没有条码信息！");
				return false;
			}
			$("#labelInfoTable tbody").html("");
			$.each(vm.skInfoList,function(i,row){
				var tr=$("<tr id='"+row.SKUID+"' />");

				$("<td />").html(row.ASNNO).appendTo(tr);
				$("<td />").html(row.ASNITM).appendTo(tr);
				$("<td />").html(row.SKUID).appendTo(tr);
				$("<td />").html(row.MATNR).appendTo(tr);
				$("<td />").html(row.MAKTX).appendTo(tr);
				$("<td />").html(row.BATCH).appendTo(tr);
				$("<td />").html(row.PRDDT).appendTo(tr);
				$("<td />").html(row.VALID_DATE).appendTo(tr);
				$("<td />").html(row.BOX_QTY).appendTo(tr);
				$("<td />").html(row.FULL_BOX_QTY).appendTo(tr);
				$("<td />").html(row.BOX_SN).appendTo(tr);

				$(tr).appendTo($("#labelInfoTable tbody"));
			})
			layer.open({
				type: 1,
				title:['送货单条码信息','font-size:18px'],
				closeBtn: 1,
				btn:['关闭'],
				offset:'t',
				area: '800px',
				skin: 'layui-bg-green', //没有背景色
				shadeClose: false,
				content: $('#labelInfo').html()
			});
		},
		boundIn:function()	{
			var flag=true;
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			var cm = $('#dataGrid').jqGrid('getGridParam', 'colModel');
			var rows=getSelectedRows();
			if(rows.length==0){
				js.alert("请选择至少一条记录");
			}
			//alert(rows.length)
			$.each(rows,function(i,rowid){
				var row = $("#dataGrid").jqGrid("getRowData",rowid);
				if(row.WH_NUMBER==null || row.WH_NUMBER==undefined || row.WH_NUMBER.trim() == ""){
					js.showErrorMessage("第"+rowid+"行仓库号未填写完整！");
					flag=false;
					return false;
				}
				if((row.VENDOR_MANAGER.trim()==""||row.SHORT_NAME.trim()=="")&&row.VENDOR_FLAG=='X'){
					js.showErrorMessage("第"+rowid+"行“采购”和“供应商简称”未填写完整！");
					flag=false;
					return false;
				}

				flag=checkRequiredCell(cm, row);
			})

			if(flag){
				showDangerMat(rows);
			}
		},

	},
	created:function(){
		this.businessList=getBusinessList("01","IN_RECEIPT");
		if(undefined == this.businessList || this.businessList.length<=0){
			js.showErrorMessage("工厂未配置收货业务类型！");
		}else{
			this.receipt_type=this.businessList[0].CODE;
			this.SYN_FLAG=this.businessList[0].SYN_FLAG;
			this.WH_NUMBER=$("#WH_NUMBER").find("option").first().val();
			this.WERKS=$("#WERKS").find("option").first().val();
		}

	}

});

$(document).on("paste","input[name='MATNR']",function(e){
	setTimeout(function(){
		var rowid=Number($(e.target).attr("rowid"));
		var row_c =  $("#dataGrid").jqGrid('getRowData', rowid);
		row_c.LIKTX=$("#LIFNR").data("LIKTX");
		row_c.LIFNR=$("#LIFNR").data("LIFNR");
		row_c.SHORT_NAME=$("#LIFNR").data("SHORT_NAME");
		var copy_text=$(e.target).val();
		var dist_list=copy_text.split(" ");
		if(dist_list.length <=0){
			return false;
		}
		//根据物料号列表查询物料信息
		var ctmap={};
		ctmap.BUSINESS_NAME=vm.receipt_type;
		ctmap.JZ_DATE=$("#JZ_DATE").val();
		ctmap.WERKS=vm.WERKS;
		ctmap.LIFNR = vm.LIFNR_WPO.LIFNR;
		ctmap.LIKTX = vm.LIFNR_WPO.LIKTX;
		ctmap.WH_NUMBER = vm.WH_NUMBER;
		ctmap.matnr_list=dist_list.join(",");
		var mat_list=getMatInfo(ctmap);

		for(var i=0;i<mat_list.length;i++){
			var mat = mat_list[i];
			$("#dataGrid").jqGrid('setCell',lastrow+i, "MATNR", mat_list[i]['MATNR'],'editable-cell');
			$('#dataGrid').jqGrid("saveCell", lastrow+i, 5);
			var html="&nbsp;&nbsp;<i class='fa fa-exclamation-triangle fa-lg' aria-hidden='true' style='color:#e3ca12'></i>";
			if(mat.DANGER_FLAG=='X'){
				$("#dataGrid").jqGrid('setCell',lastrow+i, "DANGER_HTML", html,'not-editable-cell');
			}
			if(mat.URGENT_FLAG=='X'){
				$("#dataGrid").jqGrid('setRowData', lastrow+i, '', {'color':'red'});
			}
			if(mat.FULL_BOX_QTY!=null && mat.FULL_BOX_QTY !=undefined && Number(mat.FULL_BOX_QTY)>0){
				$("#dataGrid").jqGrid('setCell',lastrow+i, "FULL_BOX_QTY", mat.FULL_BOX_QTY,'not-editable-cell');
			}else{
				$("#dataGrid").jqGrid('setCell',lastrow+i, "FULL_BOX_QTY", '','editable-cell');
			}
			$("#dataGrid").jqGrid('setCell',lastrow+i, "MAKTX", mat_list[i]['MAKTX'],'editable-cell');
			$("#dataGrid").jqGrid('setCell',lastrow+i, "UNIT", mat_list[i]['UNIT'],'editable-cell');
			$("#dataGrid").jqGrid('setCell',lastrow+i, "ITEM_TEXT", mat_list[i]['ITEM_TEXT'],'editable-cell');
			$("#dataGrid").jqGrid('setCell',lastrow+i, "LIKTX", row_c.LIKTX,'not-editable-cell');
			$("#dataGrid").jqGrid('setCell',lastrow+i, "LIFNR", row_c.LIFNR,'not-editable-cell');
			$("#dataGrid").jqGrid('setCell',lastrow+i, "SHORT_NAME", row_c.SHORT_NAME,'editable-cell');

			$("#dataGrid").jqGrid('setCell',lastrow+i, "HEADER_TXT", mat_list[i]['HEADER_TXT'],'not-editable-cell');
			$("#dataGrid").jqGrid('setCell',lastrow+i, "DANGER_FLAG", mat_list[i]['DANGER_FLAG']||"0",'not-editable-cell');
			$("#dataGrid").jqGrid('setCell',lastrow+i, "GOOD_DATES", mat_list[i]['GOOD_DATES'],'not-editable-cell');
			$("#dataGrid").jqGrid('setCell',lastrow+i, "MIN_GOOD_DATES", mat_list[i]['MIN_GOOD_DATES'],'not-editable-cell');
			$("#dataGrid").jqGrid('setCell',lastrow+i, "STOCK_QTY", mat_list[i]['STOCK_QTY'],'not-editable-cell');
			$("#dataGrid").jqGrid('setCell',lastrow+i, "STOCK_L", mat_list[i]['STOCK_L'],'not-editable-cell');
			$("#dataGrid").jqGrid('setCell',lastrow+i, "STOCK_ALL", mat_list[i]['STOCK_ALL'],'not-editable-cell');

			row_c.HEADER_TXT=mat_list[i]['HEADER_TXT'];
			row_c.DANGER_FLAG=mat_list[i]['DANGER_FLAG']||"0";
			row_c.UNIT=mat_list[i]['UNIT'];
			row_c.ITEM_TEXT=mat_list[i]['ITEM_TEXT'];
			row_c.MATNR=mat_list[i]['MATNR'];
			row_c.MAKTX=mat_list[i]['MAKTX'];
			row_c.FULL_BOX_QTY = mat_list[i]['FULL_BOX_QTY'];
			row_c.STOCK_QTY = mat_list[i]['STOCK_QTY'];
			row_c.STOCK_L = mat_list[i]['STOCK_L'];
			row_c.STOCK_ALL = mat_list[i]['STOCK_ALL'];

			if(i>0&&row_c.ID==undefined){
				addMat(mat);
			}
		}
		/*		 $.each(rows,function(i,row){
                     console.info(row.DANGER_FLAG)
                     if(i<mat_list.length){
                         $("#dataGrid").jqGrid('setCell',lastrow+i, "MATNR", mat_list[i]['MATNR'],'editable-cell');
                         $('#dataGrid').jqGrid("saveCell", lastrow+i, 5);
                         $("#dataGrid").jqGrid('setCell',lastrow+i, "DANGER_FLAG", 'X','not-editable-cell');
                         var html="&nbsp;&nbsp;<i class='fa fa-exclamation-triangle fa-lg' aria-hidden='true' style='color:#e3ca12'></i>";
                         $("#dataGrid").jqGrid('setCell',lastrow+i, "DANGER_HTML", html,'not-editable-cell');
                         if(row.DANGER_FLAG=='X'){
                             $("#dataGrid").jqGrid('setRowData', lastrow+i, '', {'color':'red'});
                         }
                         //填充行文本

                         $("#dataGrid").jqGrid('setCell',lastrow+i, "HEADER_TXT", ctxt.HEADER_TXT,'editable-cell');
                         $("#dataGrid").jqGrid('setCell',lastrow+i, "ITEM_TEXT", ctxt.ITEM_TEXT,'editable-cell');
                     }
                 })*/

	},10);

})

function showGrid(){
	var multiselect=true;
	var columns=[];
	var data = $('#dataGrid').data("dataGrid");
	//$("#mat_count").html("");

	if(data){
		$("#dataGrid").jqGrid('GridUnload');
	}

	if(vm.receipt_type=='01' || vm.receipt_type=='78'){//SCM送货单
		columns=[
			{label:'URGENT_FLAG',name:'URGENT_FLAG',hidden:true},
			{label:'BATCH',name:'BATCH',hidden:true},
			{label:'PRODUCT_DATE',name:'PRODUCT_DATE',hidden:true},
			{label:'EFFECT_DATE',name:'EFFECT_DATE',hidden:true},
			{label:'DANGER_FLAG',name:'DANGER_FLAG',hidden:true},
			{label:'VENDOR_FLAG',name:'VENDOR_FLAG',hidden:true},
			{label:'HEADER_TXT',name:'HEADER_TXT',hidden:true},
			{label:'DELIVERY_DATE',name:'DELIVERY_DATE',hidden:true},
			{label:'SOBKZ',name:'SOBKZ',hidden:true},
			{label:'GOOD_DATES',name:'GOOD_DATES',hidden:true},
			{label:'MIN_GOOD_DATES',name:'MIN_GOOD_DATES',hidden:true},
			{label:'BARCODE_FLAG',name:'BARCODE_FLAG',hidden:true},
			{label:'FULL_BOX_ERROR',name:'FULL_BOX_ERROR',hidden:true},
			{label:'STOCK_L',name:'STOCK_L',hidden:true},
			{label:'STOCK_QTY',name:'STOCK_QTY',hidden:true},
			{label:'EFFECT_DATE',name:'EFFECT_DATE',hidden:true},
			{ label: '序号', name: 'ID', index: 'ID', align:"center",width: 48, key: true ,sortable:false,formatter:function(val, obj, row, act){
					var html="";
					if(row.DANGER_FLAG=='X'){
						html=obj.rowId+"&nbsp;&nbsp;<i class='fa fa-exclamation-triangle fa-lg' aria-hidden='true' style='color:#e3ca12'></i>"
					}else
						html=obj.rowId;

					return html;
				},unformat:function(cellvalue, options, rowObject){
					return Number(cellvalue);
				}},

			{ label: '工厂', name: 'WERKS',align:"center", index: 'WERKS', width: 60 },
			{ label: '仓库号', name: 'WH_NUMBER',align:"center", index: 'WH_NUMBER', width: 60 },
			{ label: '单据号 ', name: 'ASNNO', align:"center",index: 'ASNNO', width: 120},
			{ label: '行号', name: 'ASNITM',align:"center",/*formatter:'integer',sorttype:'integer',*/ index: 'ASNITM', width: 60,},
			{ label: '料号', name: 'MATNR', align:"center",index: 'MATNR', width: 110 },
			{ label: '物料描述', name: 'MAKTX',align:"center", index: 'MAKTX', width: 250 },
			{ label: '<span style="color:blue">库位</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'LGORT\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'LGORT',align:"center", index: 'LGORT', width: 80 ,editable:true,edittype:'select',sortable:false,},
			{ label: '送货数量', name: 'QTY',align:"center", index: 'QTY', width: 70 },
			{ label: '已收数量', name: 'FINISHED_QTY',align:"center", index: 'FINISHED_QTY', width: 70 },
			{ display:'收货数量',label: '<span style="color:red">*</span><span style="color:blue">收货数量</span>', name: 'RECEIPT_QTY', align:"center",index: 'RECEIPT_QTY', width: 80,editable:true,editrules:{
					required:true,number:true},  formatter:'number',formatoptions:{
					decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}}	,
			{ label: '库存/最大库存', name: 'STOCK_ALL', align:"center",index: 'STOCK_ALL', width: 100 },
			{ label: '采购单位', name: 'UNIT', align:"center",index: 'UNIT', width: 80 }	,
			{ label: '箱数', name: 'BOX_COUNT', align:"center",index: 'BOX_COUNT', width: 50 },
			{ label: '<span style="color:blue">试装数量</span>', name: 'TRY_QTY', align:"center",index: 'TRY_QTY', width: 70,editable:true,  editrules:{
					number:true},  formatter:'number',formatoptions:{
					decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}},
			{ label: '<span style="color:blue">收料房存放区</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'BIN_CODE\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'BIN_CODE', align:"center",index: 'BIN_CODE', width: 120,editable:true,edittype:'select',sortable:false,},
			{ label: '<span style="color:blue">收料员</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'RECEIVER\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'RECEIVER', align:"center",index: 'RECEIVER', width: 80,editable:true, sortable:false,  },
			{ label: '<span style="color:red">*</span><span style="color:blue">采购</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'VENDOR_MANAGER\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'VENDOR_MANAGER', align:"center",index: 'VENDOR_MANAGER', width: 70,editable:true,sortable:false,},
			{ label: '<span style="color:red">*</span><span style="color:blue">供应商简称</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'SHORT_NAME\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'SHORT_NAME', align:"center",index: 'SHORT_NAME', width: 100,editable:true,sortable:false,},
			{ label: '供应商名称', name: 'LIKTX', align:"center",index: 'LIKTX', width: 250 },
			{ label: '供应商代码', name: 'LIFNR', align:"center",index: 'LIFNR', width: 100,},
			{ label: '采购订单', name: 'PO_NO', align:"center",index: 'PO_NO', width: 90,},
			{ label: '订单行项目', name: 'PO_ITEM_NO', align:"center",index: 'PO_ITEM_NO', width: 80,},
			{ label: '需求跟踪号', name: 'BEDNR', align:"center",index: 'BEDNR', width: 150,},
			{ label: '申请人', name: 'AFNAM', align:"center",index: 'AFNAM', width: 80,},
			{ label: '<span style="color:blue">行文本</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'ITEM_TEXT\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'ITEM_TEXT', align:"center",index: 'ITEM_TEXT', width: 260,editable:true,sortable:false,},
		];

		dataGrid=$("#dataGrid").dataGrid({
			datatype: "local",
			searchForm:$("#searchForm"),
			data:gridData,
			cellEdit:true,
			cellurl:'#',
			cellsubmit:'clientArray',
			colModel: columns,
			viewrecords: false,
			showRownum:false,
			autowidth:true,
			shrinkToFit:false,
			autoScroll: true,
			rownumWidth: 25,
			rowNum:-1,
			formatter:{number:{decimalSeparator:".", thousandsSeparator: " ", decimalPlaces: 3,defaultValue:''}},
			/* dataId:'id',*/
			multiselect: multiselect,
			loadComplete:function(data){
				if(data.code==500){
					js.showErrorMessage(data.msg,1000*10);
					//js.alert(data.msg);
					js.closeLoading();
					$(".btn").attr("disabled", false);
					return false;
				}
				var rows=$("#dataGrid").jqGrid('getRowData');
				if(rows.length <= 0){
					js.closeLoading();
					$(".btn").attr("disabled", false);
					if(data.code == 0){
						js.showErrorMessage("未查询到有效数据，无收货可能！");
					}
					return false;
				}
				//查询收料房存放区，默认第一个值
				var BIN_CODE="";

				var WERKS=rows[0].WERKS;
				vm.WERKS=WERKS;
				vm.WH_NUMBER = rows[0].WH_NUMBER;

				gr_area_list=getGrAreaList(WERKS);
				if(gr_area_list.length>0){
					BIN_CODE=gr_area_list.split(";")[0].split(":")[0];
				}
				//alert(BIN_CODE)
				var bxtotal=0;
				$.each(rows,function(i,row){
					//console.info(row)
					$("#dataGrid").jqGrid('setCell',row.ID, "BIN_CODE", BIN_CODE,'editable-cell');//给收料房存放区赋默认值

					bxtotal+=Number(row.BOX_COUNT);
					if (row.URGENT_FLAG=='X') {//过滤条件
						$("#dataGrid").jqGrid('setRowData', row.ID, '', {'color':'red'});
					}

					if(row.VENDOR_FLAG!='X'){
						$("#dataGrid").jqGrid('setCell',row.ID, 'VENDOR_MANAGER', '', 'not-editable-cell');
						$("#dataGrid").jqGrid('setCell',row.ID, 'SHORT_NAME', '', 'not-editable-cell');
						$("#dataGrid").jqGrid('setLabel','VENDOR_MANAGER','采购');
						$("#dataGrid").jqGrid('setLabel','SHORT_NAME','供应商简称');
					}
					if(row.BARCODE_FLAG=='X'){
						//仓库启用了条码，SCM收料不允许修改收货数量
						$("#dataGrid").jqGrid('setCell', row.ID, 'RECEIPT_QTY', '', 'not-editable-cell');
					}

					//物料维护了最大库存，且本次收料数量+当前库存数量>大于物料最大库存，库存数量标红
					if(row.STOCK_L !='N' && Number(row.RECEIPT_QTY) + Number(row.STOCK_QTY) > Number(row.STOCK_L)){
						$("#dataGrid").jqGrid('setCell', row.ID, 'STOCK_ALL', '', {background : '#FFD700'});
					}

				});
				if(rows.length){
					$("#mat_count").html(bxtotal+"箱/"+rows.length+"行")
				}

				js.closeLoading();
				$(".btn").attr("disabled", false);
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
				/* $('#dataGrid').jqGrid('setSelection',rowid);
                 return false;*/
			},
			formatCell:function(rowid, cellname, value, iRow, iCol){
				//console.info(cellname)
				if(cellname=='LGORT'){
					var rec = $('#dataGrid').jqGrid('getRowData', rowid);
					$('#dataGrid').jqGrid('setColProp', 'LGORT', { editoptions: {value:getLotOption(rec.WERKS,null,null,rec.SOBKZ)}
					});
				}
				if(cellname=='BIN_CODE'){
					var rec = $('#dataGrid').jqGrid('getRowData', rowid);
					$('#dataGrid').jqGrid('setColProp', 'BIN_CODE', { editoptions: {value:getGrAreaList(rec.WERKS)}
					});
				}

			},
			afterSaveCell:function(rowid, cellname, value, iRow, iCol){
				var row =  $("#dataGrid").jqGrid('getRowData', rowid);
				if(cellname=='RECEIPT_QTY'||cellname=='TRY_QTY'){
					var field="收货数量";
					if(cellname=='TRY_QTY'){
						field="试装数量";
					}
					var able_qty=row.QTY-row.FINISHED_QTY;
					if(cellname=='TRY_QTY'){
						able_qty=row.RECEIPT_QTY;
					}

					if(Number(value)>Number(able_qty)){
						js.alert(field+"不能大于"+able_qty);
						$("#dataGrid").jqGrid('setCell', rowid, cellname, able_qty,'editable-cell');
					}
					/*					if(cellname=='RECEIPT_QTY'){
                                            $("#dataGrid").jqGrid('setCell', rowid, 'TRY_QTY', '0','editable-cell');
                                            js.alert("请重新填写试装数量!")
                                        }*/

				}
				if(cellname=='RECEIPT_QTY'){
					//物料维护了最大库存，且本次收料数量+当前库存数量>大于物料最大库存，库存数量标红
					if(row.STOCK_L !='N' && Number(row.RECEIPT_QTY) + Number(row.STOCK_QTY) > Number(row.STOCK_L)){
						$("#dataGrid").jqGrid('setCell', row.ID, 'STOCK_ALL', '', {background : '#FFD700'});
					}
					if(row.STOCK_L !='N' && Number(row.RECEIPT_QTY) + Number(row.STOCK_QTY) <= Number(row.STOCK_L)){
						$("#dataGrid").jqGrid('setCell', row.ID, 'STOCK_ALL', '', {background : '#FFFFFF'});
					}
				}

			},
		});
	}

	if(vm.receipt_type=='02' ||vm.receipt_type=='04' ){//SAP采购订单收料、跨工厂采购订单收料 ||vm.receipt_type=='04'
		columns=[
			{ label: '序号', name: 'ID', index: 'ID', align:"center",width: 48,frozen: true, key: true ,sortable:false,formatter:function(val, obj, row, act){
					var html=obj.rowId;
					if(row.DANGER_FLAG=='X'){
						html+="&nbsp;&nbsp;<i class='fa fa-exclamation-triangle fa-lg' aria-hidden='true' style='color:#e3ca12'></i>"
					}
					if(row.RECEIPT_FLAG=='X'){
						html+="&nbsp;&nbsp;<span style='color:red;font-size:13px;font-weight:bold'>HX</span>";
					}
					return html;
				},unformat:function(cellvalue, options, rowObject){
					return Number(cellvalue);
				}},
			{ label: '工厂', name: 'WERKS',align:"center", frozen: true,index: 'WERKS', width: 60 },
			{ label: '仓库号', name: 'WH_NUMBER',align:"center",frozen: true, index: 'WH_NUMBER', width: 60 },
			{ label: '订单号 ', name: 'PO_NO', align:"center",frozen: true,index: 'PO_NO', width: 100},
			{ label: '行号', name: 'PO_ITEM_NO',align:"center",frozen: true, index: 'PO_ITEM_NO', width: 60,},
			{ label: '料号', name: 'MATNR', align:"center",frozen: true,index: 'MATNR', width: 110 },
			{ label: '物料描述', name: 'MAKTX',align:"center",frozen: true, index: 'MAKTX', width: 250 },
			{ label: '<span style="color:blue">库位</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'LGORT\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'LGORT',align:"center", index: 'LGORT', width: 80 ,editable:true,edittype:'select',sortable:false,},
			{ label: '订单数量', name: 'MENGE',align:"center", index: 'MENGE', width: 70 },
			{ label: '最大可收数量', name: 'MAX_MENGE',align:"center", index: 'MAX_MENGE', width: 80 ,formatter:'number',formatoptions:{
					decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}}	,
			{ label: '已收数量', name: 'FINISHED_QTY',align:"center", index: 'FINISHED_QTY', width: 70 },
			{ display:'收货数量',label: '<span style="color:red">*</span><span style="color:blue">收货数量</span>', name: 'RECEIPT_QTY', align:"center",index: 'RECEIPT_QTY', width: 80,editable:true,editrules:{
					required:true,number:true},  formatter:'number',formatoptions:{
					decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}}	,
			{ label: '库存/最大库存', name: 'STOCK_ALL', align:"center",index: 'STOCK_ALL', width: 100 },
			{ label: '采购单位', name: 'UNIT', align:"center",index: 'UNIT', width: 70 }	,
			{ display:'满箱数量',label: '<span style="color:red">*</span><span style="color:blue">满箱数量</span>', name: 'FULL_BOX_QTY', align:"center",index: 'FULL_BOX_QTY', width: 70 ,editable:true,editrules:{
					required:true,number:true},  formatter:'number',formatoptions:{
					decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}},
			{ label: '<span style="color:blue">试装数量</span>', name: 'TRY_QTY', align:"center",index: 'TRY_QTY', width: 70,editable:true,  editrules:{
					number:true},  formatter:'number',formatoptions:{
					decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}},
			{ label: '<span style="color:blue">收料房存放区</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'BIN_CODE\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'BIN_CODE', align:"center",index: 'BIN_CODE', width: 120,editable:true,edittype:'select',sortable:false,},
			{ label: '<span style="color:blue">收料员</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'RECEIVER\')" title="向下填充" style="color:green" aria-hidden="true"></i>', name: 'RECEIVER', align:"center",index: 'RECEIVER', width: 90,editable:true, sortable:false, },
			{ label: '<span style="color:red">*</span><span style="color:blue">采购</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'VENDOR_MANAGER\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'VENDOR_MANAGER', align:"center",index: 'VENDOR_MANAGER', width: 70,editable:true,  sortable:false, },
			{ label: '<span style="color:red">*</span><span style="color:blue">供应商简称</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'SHORT_NAME\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'SHORT_NAME', align:"center",index: 'SHORT_NAME', width: 100,editable:true, sortable:false,  },
			{ label: '<span style="color:blue">生产日期</span>',
				name: 'PRODUCT_DATE', align:"center",index: 'PRODUCT_DATE', width: 90,editable:true,editoptions:{
					dataInit:function(e){
						$(e).click(function(e){
							WdatePicker({dateFmt:'yyyy-MM-dd'})
						});
					}
				} },
			{ label: '供应商名称', name: 'LIKTX', align:"center",index: 'LIKTX', width: 250 },
			{ label: '供应商代码', name: 'LIFNR', align:"center",index: 'LIFNR', width: 100,},
			{ label: '需求跟踪号', name: 'BEDNR', align:"center",index: 'BEDNR', width: 150,},
			{ label: '申请人', name: 'AFNAM', align:"center",index: 'AFNAM', width: 80,},
			{ label: '<span style="color:blue">行文本</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'ITEM_TEXT\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'ITEM_TEXT', align:"center",index: 'ITEM_TEXT', width: 260,editable:true,sortable:false,},
			{label:'URGENT_FLAG',name:'URGENT_FLAG',hidden:true},
			{label:'DANGER_FLAG',name:'DANGER_FLAG',hidden:true},
			{label:'VENDOR_FLAG',name:'VENDOR_FLAG',hidden:true},
			{label:'RECEIPT_FLAG',name:'RECEIPT_FLAG',hidden:true},//是否可收货标识，X 表示不能收货，存在还需核销数量
			{label:'HEADER_TXT',name:'HEADER_TXT',hidden:true},
			{label:'DELIVERY_DATE',name:'DELIVERY_DATE',hidden:true},//预计送货时间
			{label:'SOBKZ',name:'SOBKZ',hidden:true},
			{label:'GOOD_DATES',name:'GOOD_DATES',hidden:true},
			{label:'MIN_GOOD_DATES',name:'MIN_GOOD_DATES',hidden:true},
			{label:'FULL_BOX_FLAG',name:'FULL_BOX_FLAG',hidden:true},
			{label:'STOCK_L',name:'STOCK_L',hidden:true},
			{label:'STOCK_QTY',name:'STOCK_QTY',hidden:true},
		];

		if(vm.receipt_type=='04'){ //跨工厂收货
			columns=[
				{ label: '序号', name: 'ID', index: 'ID', align:"center",width: 48,frozen: true, key: true ,sortable:false,formatter:function(val, obj, row, act){
						var html=obj.rowId;
						if(row.DANGER_FLAG=='X'){
							html+="&nbsp;&nbsp;<i class='fa fa-exclamation-triangle fa-lg' aria-hidden='true' style='color:#e3ca12'></i>"
						}
						if(row.RECEIPT_FLAG=='X'){
							html+="&nbsp;&nbsp;<span style='color:red;font-size:13px;font-weight:bold'>HX</span>";
						}
						return html;
					},unformat:function(cellvalue, options, rowObject){
						return Number(cellvalue);
					}},
				{ label: '工厂', name: 'WERKS',align:"center", frozen: true,index: 'WERKS', width: 60 },
				{ label: '仓库号', name: 'WH_NUMBER',align:"center",frozen: true, index: 'WH_NUMBER', width: 60 },
				{ label: 'PO工厂', name: 'WERKS_PO',align:"center", frozen: true,index: 'WERKS_PO', width: 65 },
				{ label: '订单号 ', name: 'PO_NO', align:"center",frozen: true,index: 'PO_NO', width: 100},
				{ label: '行号', name: 'PO_ITEM_NO',align:"center",frozen: true, index: 'PO_ITEM_NO', width: 50,},
				{ label: '料号', name: 'MATNR', align:"center",frozen: true,index: 'MATNR', width: 110 },
				{ label: '物料描述', name: 'MAKTX',align:"center",frozen: true, index: 'MAKTX', width: 250 },
				{ label: '<span style="color:blue">库位</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'LGORT\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
					name: 'LGORT',align:"center", index: 'LGORT', width: 80 ,editable:true,edittype:'select',sortable:false,},
				{ label: '订单数量', name: 'MENGE',align:"center", index: 'MENGE', width: 70 },
				{ label: '最大可收数量', name: 'MAX_MENGE',align:"center", index: 'MAX_MENGE', width: 90 },
				{ label: '已收数量', name: 'FINISHED_QTY',align:"center", index: 'FINISHED_QTY', width: 70 },
				{ display:'收货数量',label: '<span style="color:red">*</span><span style="color:blue">收货数量</span>', name: 'RECEIPT_QTY', align:"center",index: 'RECEIPT_QTY', width: 70,editable:true,editrules:{
						required:true,number:true},  formatter:'number',formatoptions:{
						decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}}	,
				{ label: '库存/最大库存', name: 'STOCK_ALL', align:"center",index: 'STOCK_ALL', width: 100 },
				{ label: '采购单位', name: 'UNIT', align:"center",index: 'UNIT', width: 70 }	,
				{ display:'满箱数量',label: '<span style="color:red">*</span><span style="color:blue">满箱数量</span>', name: 'FULL_BOX_QTY', align:"center",index: 'FULL_BOX_QTY', width: 70 ,editable:true,editrules:{
						required:true,number:true},  formatter:'number',formatoptions:{
						decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}},
				{ label: '<span style="color:blue">试装数量</span>', name: 'TRY_QTY', align:"center",index: 'TRY_QTY', width: 70,editable:true,  editrules:{
						number:true},  formatter:'number',formatoptions:{
						decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}},
				{ label: '<span style="color:blue">收料房存放区</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'BIN_CODE\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
					name: 'BIN_CODE', align:"center",index: 'BIN_CODE', width: 120,editable:true,edittype:'select',sortable:false,},
				{ label: '<span style="color:blue">收料员</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'RECEIVER\')" title="向下填充" style="color:green" aria-hidden="true"></i>', name: 'RECEIVER', align:"center",index: 'RECEIVER', width: 90,editable:true, sortable:false, },
				{ label: '<span style="color:red">*</span><span style="color:blue">采购</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'VENDOR_MANAGER\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
					name: 'VENDOR_MANAGER', align:"center",index: 'VENDOR_MANAGER', width: 70,editable:true,  sortable:false, },
				{ label: '<span style="color:red">*</span><span style="color:blue">供应商简称</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'SHORT_NAME\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
					name: 'SHORT_NAME', align:"center",index: 'SHORT_NAME', width: 100,editable:true, sortable:false,  },
				{ label: '<span style="color:blue">生产日期</span>', name: 'PRODUCT_DATE', align:"center",index: 'PRODUCT_DATE', width: 90,editable:true,editoptions:{
						dataInit:function(e){
							$(e).click(function(e){
								WdatePicker({dateFmt:'yyyy-MM-dd'})
							});
						}
					} },
				{ label: '供应商名称', name: 'LIKTX', align:"center",index: 'LIKTX', width: 250 },
				{ label: '供应商代码', name: 'LIFNR', align:"center",index: 'LIFNR', width: 100,},
				{ label: '需求跟踪号', name: 'BEDNR', align:"center",index: 'BEDNR', width: 150,},
				{ label: '申请人', name: 'AFNAM', align:"center",index: 'AFNAM', width: 80,},
				{ label: '<span style="color:blue">行文本</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'ITEM_TEXT\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
					name: 'ITEM_TEXT', align:"center",index: 'ITEM_TEXT', width: 260,editable:true,sortable:false,},
				{label:'URGENT_FLAG',name:'URGENT_FLAG',hidden:true},
				{label:'DANGER_FLAG',name:'DANGER_FLAG',hidden:true},
				{label:'VENDOR_FLAG',name:'VENDOR_FLAG',hidden:true},
				{label:'RECEIPT_FLAG',name:'RECEIPT_FLAG',hidden:true},//是否可收货标识，X 表示不能收货，存在还需核销数量
				{label:'HEADER_TXT',name:'HEADER_TXT',hidden:true},
				{label:'DELIVERY_DATE',name:'DELIVERY_DATE',hidden:true},//预计送货时间
				{label:'SOBKZ',name:'SOBKZ',hidden:true},
				{label:'GOOD_DATES',name:'GOOD_DATES',hidden:true},
				{label:'MIN_GOOD_DATES',name:'MIN_GOOD_DATES',hidden:true},
				{label:'FULL_BOX_FLAG',name:'FULL_BOX_FLAG',hidden:true},
				{label:'STOCK_L',name:'STOCK_L',hidden:true},
				{label:'STOCK_QTY',name:'STOCK_QTY',hidden:true},
			];
		}

		dataGrid=$("#dataGrid").dataGrid({
			datatype: "local",
			searchForm:$("#searchForm"),
			cellEdit:true,
			cellurl:'#',
			cellsubmit:'clientArray',
			colModel: columns,
			viewrecords: false,
			showRownum:false,
			autowidth:true,
			shrinkToFit:false,
			autoScroll: true,
			rownumWidth: 25,
			rowNum:-1,
			formatter:{number:{decimalSeparator:".", thousandsSeparator: " ", decimalPlaces: 3,defaultValue:''}},
			/* dataId:'id',*/
			multiselect: multiselect,
			loadComplete:function(data){
				if(data.code==500){
					js.showErrorMessage(data.msg,1000*10);
					//js.alert(data.msg);
					js.closeLoading();
					$(".btn").attr("disabled", false);
					return false;
				}
				var rows=$("#dataGrid").jqGrid('getRowData');

				if(rows.length <=0){
					js.closeLoading();
					$(".btn").attr("disabled", false);
					if(data.code == 0){
						js.showErrorMessage("未查询到有效数据，无收货可能！");
					}
					return false;
				}

				//查询收料房存放区，默认第一个值
				var BIN_CODE="";
				var WERKS=rows[0].WERKS;
				if(vm.receipt_type=='04'){
					vm.WERKS=WERKS;
				}
				gr_area_list=getGrAreaList(WERKS);
				if(gr_area_list.length>0){
					BIN_CODE=gr_area_list.split(";")[0].split(":")[0];
				}
				//alert(BIN_CODE)
				var bxtotal=0;
				$.each(rows,function(i,row){
					//console.info(row)
					$("#dataGrid").jqGrid('setCell', row.ID, "BIN_CODE", BIN_CODE,'editable-cell');//给收料房存放区赋默认值
					$("#dataGrid").jqGrid('setCell', row.ID, "WH_NUMBER", $("#WH_NUMBER").val(),'editable-cell');//仓库号默认为页面仓库下拉列表的值
					bxtotal+=Number(row.BOX_COUNT);
					if (row.URGENT_FLAG=='X') {//过滤条件
						$("#dataGrid").jqGrid('setRowData', row.ID, '', {'color':'red'});
					}

					if(row.VENDOR_FLAG!='X'){
						$("#dataGrid").jqGrid('setCell', row.ID, 'VENDOR_MANAGER', '', 'not-editable-cell');
						$("#dataGrid").jqGrid('setCell', row.ID, 'SHORT_NAME', '', 'not-editable-cell');
						$("#dataGrid").jqGrid('setLabel','VENDOR_MANAGER','采购');
						$("#dataGrid").jqGrid('setLabel','SHORT_NAME','供应商简称');
					}
					if(row.RECEIPT_FLAG=='X'){
						//  alert($("#dataGrid tbody").children("tr").eq(row.ID).html())
						$("#dataGrid tbody").children("tr").eq(row.ID).children("td").find(".cbox").attr("disabled",true);
					}
					//系统维护了包规的物料，包规不允许修改
					if(row.FULL_BOX_FLAG=='X'){
						$("#dataGrid").jqGrid('setCell', row.ID, 'FULL_BOX_QTY', '', 'not-editable-cell');
						$("#dataGrid").jqGrid('setCell', row.ID, 'FULL_BOX_QTY', '', {background : '#A9A9A9'});
					}

					//物料维护了最大库存，且本次收料数量+当前库存数量>大于物料最大库存，库存数量标红
					if(row.STOCK_L !='N' && Number(row.RECEIPT_QTY) + Number(row.STOCK_QTY) > Number(row.STOCK_L)){
						$("#dataGrid").jqGrid('setCell', row.ID, 'STOCK_ALL', '', {background : '#FFD700'});
					}

				});
				if(rows.length){
					$("#mat_count").html(rows.length+"行")
				}

				js.closeLoading();
				$(".btn").attr("disabled", false);
			},
			onSelectAll:function(rowids,status){
				if(status==true){
					$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
					$.each(rowids,function(i,rowid){
						var row = $("#dataGrid").jqGrid("getRowData",rowid);
						if(row.RECEIPT_FLAG=='X') {
							$("#dataGrid").jqGrid("setSelection", rowid,false);//设置该行不能被选中。
						}
					})
				}
			},
			onSelectRow:function(rowid,status){
				if(status == true){
					var row = $("#dataGrid").jqGrid('getRowData',rowid);
					if(row.RECEIPT_FLAG=='X') {
						$("#dataGrid").jqGrid("setSelection", rowid,false);//设置该行不能被选中。
					}
				}
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
				/*$('#dataGrid').jqGrid('setSelection',rowid);

                return false;*/
			} ,
			formatCell:function(rowid, cellname, value, iRow, iCol){
				//console.info(cellname)
				if(cellname=='LGORT'){
					var rec = $('#dataGrid').jqGrid('getRowData', rowid);
					$('#dataGrid').jqGrid('setColProp', 'LGORT', { editoptions: {value:getLotOption(rec.WERKS,null,null)}
					});
				}
				if(cellname=='BIN_CODE'){
					var rec = $('#dataGrid').jqGrid('getRowData', rowid);
					$('#dataGrid').jqGrid('setColProp', 'BIN_CODE', { editoptions: {value:getGrAreaList(rec.WERKS)}
					});
				}

			},
			afterSaveCell:function(rowid, cellname, value, iRow, iCol){
				if(value==""){
					$("#dataGrid").jqGrid('setCell', rowid, cellname, '&nbsp;','editable-cell');
				}

				var row =  $("#dataGrid").jqGrid('getRowData', rowid);
				if(cellname=='RECEIPT_QTY'||cellname=='TRY_QTY'){
					var field="收货数量";
					var able_qty=row.MAX_MENGE-row.FINISHED_QTY;
					if(cellname=='TRY_QTY'){
						field="试装数量";
						able_qty=row.RECEIPT_QTY;
					}
					if(Number(value)>Number(able_qty)){
						js.alert(field+"不能大于"+able_qty);
						$("#dataGrid").jqGrid('setCell', rowid, cellname, able_qty,'editable-cell');
					}

					/*					if(cellname=='RECEIPT_QTY'){
                                            $("#dataGrid").jqGrid('setCell', rowid, 'TRY_QTY', '0','editable-cell');
                                            js.alert("请重新填写试装数量!")
                                        }*/
				}
				if(cellname=='RECEIPT_QTY'){
					//物料维护了最大库存，且本次收料数量+当前库存数量>大于物料最大库存，库存数量标红
					if(row.STOCK_L !='N' && Number(row.RECEIPT_QTY) + Number(row.STOCK_QTY) > Number(row.STOCK_L)){
						$("#dataGrid").jqGrid('setCell', row.ID, 'STOCK_ALL', '', {background : '#FFD700'});
					}
					if(row.STOCK_L !='N' && Number(row.RECEIPT_QTY) + Number(row.STOCK_QTY) <= Number(row.STOCK_L)){
						$("#dataGrid").jqGrid('setCell', row.ID, 'STOCK_ALL', '', {background : '#FFFFFF'});
					}
				}

			},
		});
		//alert("setFrozenColumns")
	}
	/**
	 * SAP交货单收料
	 */
	if(vm.receipt_type=='03'){
		columns=[
			{label:'URGENT_FLAG',name:'URGENT_FLAG',hidden:true},
			{label:'DANGER_FLAG',name:'DANGER_FLAG',hidden:true},
			{label:'VENDOR_FLAG',name:'VENDOR_FLAG',hidden:true},
			{label:'RECEIPT_FLAG',name:'RECEIPT_FLAG',hidden:true},//是否可收货标识，X 表示不能收货，存在还需核销数量
			{label:'HEADER_TXT',name:'HEADER_TXT',hidden:true},
			{label:'UMREZ',name:'HEADER_TXT',hidden:true},
			{label:'UMREN',name:'HEADER_TXT',hidden:true},
			{label:'SOBKZ',name:'SOBKZ',hidden:true},
			{label:'GOOD_DATES',name:'GOOD_DATES',hidden:true},
			{label:'MIN_GOOD_DATES',name:'MIN_GOOD_DATES',hidden:true},
			{label:'FULL_BOX_FLAG',name:'FULL_BOX_FLAG',hidden:true},
			{label:'PO_NO',name:'PO_NO',hidden:true},
			{label:'PO_ITEM_NO',name:'PO_ITEM_NO',hidden:true},
			{label:'STOCK_L',name:'STOCK_L',hidden:true},
			{label:'STOCK_QTY',name:'STOCK_QTY',hidden:true},
			{ label: '序号', name: 'ID', index: 'ID', align:"center",width: 48, key: true ,sortable:false,formatter:function(val, obj, row, act){
					var html=obj.rowId;
					if(row.DANGER_FLAG=='X'){
						html+="&nbsp;&nbsp;<i class='fa fa-exclamation-triangle fa-lg' aria-hidden='true' style='color:#e3ca12'></i>"
					}
					if(row.RECEIPT_FLAG=='X'){
						html+="&nbsp;&nbsp;<span style='color:red;font-size:13px;font-weight:bold'>HX</span>";
					}
					return html;
				},unformat:function(cellvalue, options, rowObject){
					return Number(cellvalue);
				}},
			{ label: '工厂', name: 'WERKS',align:"center", index: 'WERKS', width: 60 },
			{ label: '仓库号', name: 'WH_NUMBER',align:"center", index: 'WH_NUMBER', width: 60 },
			{ label: '交货号 ', name: 'SAP_OUT_NO', align:"center",index: 'SAP_OUT_NO', width: 100},
			{ label: '行号', name: 'SAP_OUT_ITEM_NO',align:"center", index: 'SAP_OUT_ITEM_NO', width: 80,},
			{ label: '料号', name: 'MATNR', align:"center",index: 'MATNR', width: 110 },
			{ label: '物料描述', name: 'MAKTX',align:"center", index: 'MAKTX', width: 250 },
			{ label: '收货库位', name: 'LGORT',align:"center", index: 'LGORT', width: 80 ,editable:false,},
			{ label: '简配库位', name: 'JP_LGORT',align:"center", index: 'JP_LGORT', width: 80 ,editable:false,},
			{ label: '送货数量', name: 'LFIMG',align:"center", index: 'MENGE', width: 80 },
			{ display:'收货数量',label: '<span style="color:red">*</span><span style="color:blue">收货数量</span>', name: 'RECEIPT_QTY', align:"center",index: 'RECEIPT_QTY', width: 80,editable:true,editrules:{
					required:true,number:true},  formatter:'number',formatoptions:{
					decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}}	,
			{ label: '库存/最大库存', name: 'STOCK_ALL', align:"center",index: 'STOCK_ALL', width: 100 },
			{ label: '单位', name: 'UNIT', align:"center",index: 'UNIT', width: 80 }	,
			{ display:'满箱数量',label: '<span style="color:red">*</span><span style="color:blue">满箱数量</span>', name: 'FULL_BOX_QTY', align:"center",index: 'FULL_BOX_QTY', width: 80 ,editable:true,editrules:{
					required:true,number:true},  formatter:'number',formatoptions:{
					decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}},
			{ label: '批次', name: 'F_BATCH',align:"center", index: 'F_BATCH', width: 80 },
			{ label: '<span style="color:blue">试装数量</span>', name: 'TRY_QTY', align:"center",index: 'TRY_QTY', width: 70,editable:true,  editrules:{
					number:true},  formatter:'number',formatoptions:{
					decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}},
			{ label: '<span style="color:blue">收料房存放区</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'BIN_CODE\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'BIN_CODE', align:"center",index: 'BIN_CODE', width: 120,editable:true,edittype:'select',  sortable:false,},
			{ label: '<span style="color:blue">收料员</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'RECEIVER\')" title="向下填充" style="color:green" aria-hidden="true"></i>', name: 'RECEIVER', align:"center",index: 'RECEIVER', width: 90,editable:true, sortable:false, },
			{ label: '<span style="color:red">*</span><span style="color:blue">采购</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'VENDOR_MANAGER\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'VENDOR_MANAGER', align:"center",index: 'VENDOR_MANAGER', width: 70,editable:true,  sortable:false, },
			{ label: '<span style="color:red">*</span><span style="color:blue">供应商简称</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'SHORT_NAME\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'SHORT_NAME', align:"center",index: 'SHORT_NAME', width: 100,editable:true, sortable:false,  },
			{ label: '<span style="color:blue">生产日期</span>', name: 'PRODUCT_DATE', align:"center",index: 'PRODUCT_DATE', width: 80,editable:true,editoptions:{
					dataInit:function(e){
						$(e).click(function(e){
							WdatePicker({dateFmt:'yyyy-MM-dd'})
						});
					}
				} },
			{ label: '供应商名称', name: 'LIKTX', align:"center",index: 'LIKTX', width: 250 },
			{ label: '供应商代码', name: 'LIFNR', align:"center",index: 'LIFNR', width: 100,},
			{ label: '采购订单', name: 'VGBEL', align:"center",index: 'VGBEL', width: 130 },
			{ label: '订单行项目', name: 'VGPOS', align:"center",index: 'VGPOS', width: 90,},
			{ label: '需求跟踪号', name: 'BEDNR', align:"center",index: 'BEDNR', width: 150,},
			{ label: '申请人', name: 'AFNAM', align:"center",index: 'AFNAM', width: 80,},
			{ label: '<span style="color:blue">行文本</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'ITEM_TEXT\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'ITEM_TEXT', align:"center",index: 'ITEM_TEXT', width: 260,editable:true,sortable:false,},
		];

		dataGrid=$("#dataGrid").dataGrid({
			datatype: "local",
			searchForm:$("#searchForm"),
			cellEdit:true,
			cellurl:'#',
			cellsubmit:'clientArray',
			colModel: columns,
			viewrecords: false,
			showRownum:false,
			autowidth:true,
			shrinkToFit:false,
			autoScroll: true,
			rownumWidth: 25,
			rowNum:-1,
			formatter:{number:{decimalSeparator:".", thousandsSeparator: " ", decimalPlaces: 3,defaultValue:''}},
			/* dataId:'id',*/
			multiselect: multiselect,
			loadComplete:function(data){
				if(data.code==500){
					js.showErrorMessage(data.msg,1000*10);
					//js.alert(data.msg);
					js.closeLoading();
					$(".btn").attr("disabled", false);
					return false;
				}
				var rows=$("#dataGrid").jqGrid('getRowData');
				if(rows.length <= 0){
					js.closeLoading();
					$(".btn").attr("disabled", false);
					if(data.code == 0){
						js.showErrorMessage("未查询到有效数据，无收货可能！");
					}
					return false;
				}

				//查询收料房存放区，默认第一个值
				var BIN_CODE="";
				var WERKS=rows[0].WERKS;
				vm.WERKS=WERKS;

				gr_area_list=getGrAreaList(WERKS);
				if(gr_area_list.length>0){
					BIN_CODE=gr_area_list.split(";")[0].split(":")[0];
				}
				//alert(BIN_CODE)
				var bxtotal=0;
				$.each(rows,function(i,row){
					//console.info(row)
					$("#dataGrid").jqGrid('setCell', row.ID, "BIN_CODE", BIN_CODE,'editable-cell');//给收料房存放区赋默认值
					$("#dataGrid").jqGrid('setCell', row.ID, "WH_NUMBER", $("#WH_NUMBER").val(),'editable-cell');//仓库号默认为页面仓库下拉列表的值
					bxtotal+=Number(row.BOX_COUNT);
					if (row.URGENT_FLAG=='X') {//过滤条件
						$("#dataGrid").jqGrid('setRowData', row.ID, '', {'color':'red'});
					}

					if(row.VENDOR_FLAG!='X'){
						$("#dataGrid").jqGrid('setCell', row.ID, 'VENDOR_MANAGER', '', 'not-editable-cell');
						$("#dataGrid").jqGrid('setCell', row.ID, 'SHORT_NAME', '', 'not-editable-cell');
						$("#dataGrid").jqGrid('setLabel','VENDOR_MANAGER','采购');
						$("#dataGrid").jqGrid('setLabel','SHORT_NAME','供应商简称');
					}
					if(row.RECEIPT_FLAG=='X'){
						//  alert($("#dataGrid tbody").children("tr").eq(row.ID).html())
						$("#dataGrid tbody").children("tr").eq(row.ID).children("td").find(".cbox").attr("disabled",true);
					}
					//系统维护了包规的物料，包规不允许修改
					if(row.FULL_BOX_FLAG=='X'){
						$("#dataGrid").jqGrid('setCell', row.ID, 'FULL_BOX_QTY', '', 'not-editable-cell');
						$("#dataGrid").jqGrid('setCell', row.ID, 'FULL_BOX_QTY', '', {background : '#A9A9A9'});
					}

					//物料维护了最大库存，且本次收料数量+当前库存数量>大于物料最大库存，库存数量标红
					if(row.STOCK_L !='N' && Number(row.RECEIPT_QTY) + Number(row.STOCK_QTY) > Number(row.STOCK_L)){
						$("#dataGrid").jqGrid('setCell', row.ID, 'STOCK_ALL', '', {background : '#FFD700'});
					}

				});

				if(rows.length){
					$("#mat_count").html(rows.length+"行")
				}

				js.closeLoading();
				$(".btn").attr("disabled", false);
			},
			onSelectAll:function(rowids,status){
				if(status==true){
					$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
					$.each(rowids,function(i,rowid){
						var row = $("#dataGrid").jqGrid("getRowData",rowid);
						if(row.RECEIPT_FLAG=='X') {
							$("#dataGrid").jqGrid("setSelection", rowid,false);//设置该行不能被选中。
						}
					})
				}
			},
			onSelectRow:function(rowid,status){
				if(status == true){
					var row = $("#dataGrid").jqGrid('getRowData',rowid);
					if(row.RECEIPT_FLAG=='X') {
						$("#dataGrid").jqGrid("setSelection", rowid,false);//设置该行不能被选中。
					}
				}
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
				/*$('#dataGrid').jqGrid('setSelection',rowid);

                return false;*/
			} ,
			formatCell:function(rowid, cellname, value, iRow, iCol){
				//console.info(cellname)
				/*	if(cellname=='LGORT'){
                        var rec = $('#dataGrid').jqGrid('getRowData', rowid);
                        $('#dataGrid').jqGrid('setColProp', 'LGORT', { editoptions: {value:getLotOption(rec.WERKS,rec.VGBEL,rec.VGPOS)}
                        });
                    }		*/
				if(cellname=='BIN_CODE'){
					var rec = $('#dataGrid').jqGrid('getRowData', rowid);
					$('#dataGrid').jqGrid('setColProp', 'BIN_CODE', { editoptions: {value:getGrAreaList(rec.WERKS)}
					});
				}

			},
			afterSaveCell:function(rowid, cellname, value, iRow, iCol){
				if(value==""){
					$("#dataGrid").jqGrid('setCell', rowid, cellname, '&nbsp;','editable-cell');
				}

				var row =  $("#dataGrid").jqGrid('getRowData', rowid);
				if(cellname=='RECEIPT_QTY'||cellname=='TRY_QTY'){
					var field="收货数量";
					var able_qty=row.LFIMG;
					if(cellname=='TRY_QTY'){
						field="试装数量";
						able_qty=row.RECEIPT_QTY;
					}
					if(Number(value)>Number(able_qty)){
						js.alert(field+"不能大于"+able_qty);
						$("#dataGrid").jqGrid('setCell', rowid, cellname, able_qty,'editable-cell');
					}

					if(cellname=='RECEIPT_QTY'){
						$("#dataGrid").jqGrid('setCell', rowid, 'TRY_QTY', '0','editable-cell');
						//js.alert("请重新填写试装数量!")
					}
				}
				if(cellname=='RECEIPT_QTY'){
					//物料维护了最大库存，且本次收料数量+当前库存数量>大于物料最大库存，库存数量标红
					if(row.STOCK_L !='N' && Number(row.RECEIPT_QTY) + Number(row.STOCK_QTY) > Number(row.STOCK_L)){
						$("#dataGrid").jqGrid('setCell', row.ID, 'STOCK_ALL', '', {background : '#FFD700'});
					}
					if(row.STOCK_L !='N' && Number(row.RECEIPT_QTY) + Number(row.STOCK_QTY) <= Number(row.STOCK_L)){
						$("#dataGrid").jqGrid('setCell', row.ID, 'STOCK_ALL', '', {background : '#FFFFFF'});
					}
				}
			},
		});
	}

	/**
	 * 303调拨收料
	 */
	if(vm.receipt_type=='06'){
		columns=[
			{label:'URGENT_FLAG',name:'URGENT_FLAG',hidden:true},
			{label:'DANGER_FLAG',name:'DANGER_FLAG',hidden:true},
			{label:'VENDOR_FLAG',name:'VENDOR_FLAG',hidden:true},
			{label:'RECEIPT_FLAG',name:'RECEIPT_FLAG',hidden:true},//是否可收货标识，X 表示不能收货，存在还需核销数量
			{label:'HEADER_TXT',name:'HEADER_TXT',hidden:true},
			{label:'SOBKZ',name:'SOBKZ',hidden:true},
			{label:'GOOD_DATES',name:'GOOD_DATES',hidden:true},
			{label:'MIN_GOOD_DATES',name:'MIN_GOOD_DATES',hidden:true},
			{label:'FULL_BOX_FLAG',name:'FULL_BOX_FLAG',hidden:true},
			{label:'LABEL_NO',name:'LABEL_NO',hidden:true},
			{label:'ITEM_LABEL_LIST',name:'ITEM_LABEL_LIST',hidden:true},
			{label:'BARCODE_FLAG',name:'BARCODE_FLAG',hidden:true},
			{label:'BATCH',name:'BATCH',hidden:true},
			{label:'F_BATCH',name:'F_BATCH',hidden:true},
			{label:'STOCK_L',name:'STOCK_L',hidden:true},
			{label:'STOCK_QTY',name:'STOCK_QTY',hidden:true},
			{ label: '序号', name: 'ID', index: 'ID', align:"center",width: 48, key: true ,sortable:false,formatter:function(val, obj, row, act){
					var html=obj.rowId;
					if(row.DANGER_FLAG=='X'){
						html+="&nbsp;&nbsp;<i class='fa fa-exclamation-triangle fa-lg' aria-hidden='true' style='color:#e3ca12'></i>"
					}
					if(row.RECEIPT_FLAG=='X'){
						html+="&nbsp;&nbsp;<span style='color:red;font-size:13px;font-weight:bold'>HX</span>";
					}
					return html;
				},unformat:function(cellvalue, options, rowObject){
					return Number(cellvalue);
				}},
			{ label: '工厂', name: 'WERKS',align:"center", index: 'WERKS', width: 70 },
			{ label: '仓库号', name: 'WH_NUMBER',align:"center", index: 'WH_NUMBER', width: 70 },
			{ label: '凭证号 ', name: 'SAP_MATDOC_NO', align:"center",index: 'SAP_MATDOC_NO', width: 100},
			{ label: '行号', name: 'SAP_MATDOC_ITEM_NO',align:"center", index: 'SAP_MATDOC_ITEM_NO', width: 80,},
			{ label: '料号', name: 'MATNR', align:"center",index: 'MATNR', width: 110 },
			{ label: '物料描述', name: 'MAKTX',align:"center", index: 'MAKTX', width: 250 },
			{  display:'接收库位',label: '<span style="color:red">*</span><span style="color:blue">接收库位</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'LGORT\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'LGORT',align:"center", index: 'LGORT', width: 100 ,editable:true,sortable:false,edittype:'select',editrules:{
					required:true}},
			{ label: '凭证数量', name: 'ENTRY_QNT',align:"center", index: 'ENTRY_QNT', width: 80 },
			{ label: '条码数量', name: 'LABEL_QTY',align:"center", index: 'LABEL_QTY', width: 80 },
			{  display:'收货数量',label: '<span style="color:red">*</span><span style="color:blue">收货数量</span>', name: 'RECEIPT_QTY', align:"center",index: 'RECEIPT_QTY', width: 80,editable:true,editrules:{
					required:true,number:true},  formatter:'number',formatoptions:{
					decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}}	,
			{ label: '库存/最大库存', name: 'STOCK_ALL', align:"center",index: 'STOCK_ALL', width: 100 },
			{ label: '单位', name: 'UNIT', align:"center",index: 'UNIT', width: 80 }	,
			{ display:'满箱数量',label: '<span style="color:red">*</span><span style="color:blue">满箱数量</span>', name: 'FULL_BOX_QTY', align:"center",index: 'FULL_BOX_QTY', width: 80 ,editable:true,editrules:{
					required:true,number:true},  formatter:'number',formatoptions:{
					decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}},
			{ label: '批次', name: 'F_BATCH',align:"center", index: 'F_BATCH', width: 80 },
			{ label: '<span style="color:blue">生产日期</span>', name: 'PRODUCT_DATE', align:"center",index: 'PRODUCT_DATE', width: 80,editable:true,editoptions:{
					dataInit:function(e){
						$(e).click(function(e){
							WdatePicker({dateFmt:'yyyy-MM-dd'})
						});
					}
				} },
			{ label: '<span style="color:blue">收料房存放区</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'BIN_CODE\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'BIN_CODE', align:"center",index: 'BIN_CODE', width: 120,editable:true,edittype:'select',  sortable:false,},
			{ label: '<span style="color:blue">收料员</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'RECEIVER\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'RECEIVER', align:"center",index: 'RECEIVER', width: 90,editable:true, sortable:false, },
			{ display:'采购',label: '<span style="color:red">*</span><span style="color:blue">采购</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'VENDOR_MANAGER\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'VENDOR_MANAGER', align:"center",editrules:{required:false},index: 'VENDOR_MANAGER', width: 70,editable:true, sortable:false, },
			{ display:'供应商简称',label: '<span style="color:red">*</span><span style="color:blue">供应商简称</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'SHORT_NAME\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'SHORT_NAME', align:"center",editrules:{required:false},index: 'SHORT_NAME', width: 100,editable:true, sortable:false,  },
			{ label: '供应商名称', name: 'LIKTX', align:"center",index: 'LIKTX', width: 250 },
			{ label: '供应商代码', name: 'LIFNR', align:"center",index: 'LIFNR', width: 100,},
			{ label: '<span style="color:blue">需求跟踪号</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'BEDNR\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'BEDNR', align:"center",index: 'BEDNR', width: 150,editable:true,sortable:false,},
			{ label: '<span style="color:blue">行文本</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'ITEM_TEXT\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'ITEM_TEXT', align:"center",index: 'ITEM_TEXT', width: 260,editable:true,sortable:false,},
		];

		dataGrid=$("#dataGrid").dataGrid({
			datatype: "local",
			searchForm:$("#searchForm"),
			cellEdit:true,
			cellurl:'#',
			cellsubmit:'clientArray',
			colModel: columns,
			viewrecords: false,
			showRownum:false,
			autowidth:true,
			shrinkToFit:false,
			autoScroll: true,
			rownumWidth: 25,
			rowNum:-1,
			formatter:{number:{decimalSeparator:".", thousandsSeparator: " ", decimalPlaces: 3,defaultValue:''}},
			/* dataId:'id',*/
			multiselect: multiselect,
			loadComplete:function(data){
				if(data.code==500){
					js.showErrorMessage(data.msg,1000*10);
					//js.alert(data.msg);
					js.closeLoading();
					$(".btn").attr("disabled", false);
					return false;
				}
				if(data.page.list ==undefined || data.page.list =='undefined' || data.page.list =="" || data.page.list.length<=0){
					//alert('未查询到符合要求的数据！');
					js.closeLoading();
					$(".btn").attr("disabled", false);
					return false;
				}
				var rows=$("#dataGrid").jqGrid('getRowData');
				//查询收料房存放区，默认第一个值
				var BIN_CODE="";
				var WERKS=rows[0].WERKS;
				vm.WERKS=WERKS;

				gr_area_list=getGrAreaList(WERKS);
				if(gr_area_list.length>0){
					BIN_CODE=gr_area_list.split(";")[0].split(":")[0];
				}
				//alert(BIN_CODE)

				var bxtotal=0;
				$.each(rows,function(i,row){
					//console.info(row)
					$("#dataGrid").jqGrid('setCell', row.ID, "BIN_CODE", BIN_CODE,'editable-cell');//给收料房存放区赋默认值
					$("#dataGrid").jqGrid('setCell', row.ID, "WH_NUMBER", $("#WH_NUMBER").val(),'editable-cell');//仓库号默认为页面仓库下拉列表的值
					bxtotal+=Number(row.BOX_COUNT);
					if (row.URGENT_FLAG=='X') {//过滤条件
						$("#dataGrid").jqGrid('setRowData', row.ID, '', {'color':'red'});
					}

					if(row.VENDOR_FLAG!='X'){
						$("#dataGrid").jqGrid('setCell', row.ID, 'VENDOR_MANAGER', '', 'not-editable-cell');
						$("#dataGrid").jqGrid('setCell', row.ID, 'SHORT_NAME', '', 'not-editable-cell');
						$("#dataGrid").jqGrid('setLabel','VENDOR_MANAGER','采购');
						$("#dataGrid").jqGrid('setLabel','SHORT_NAME','供应商简称');
					}
					if(row.RECEIPT_FLAG=='X'){
						//  alert($("#dataGrid tbody").children("tr").eq(row.ID).html())
						$("#dataGrid tbody").children("tr").eq(row.ID).children("td").find(".cbox").attr("disabled",true);
					}
					//系统维护了包规的物料，包规不允许修改
					if(row.FULL_BOX_FLAG=='X'){
						$("#dataGrid").jqGrid('setCell', row.ID, 'FULL_BOX_QTY', '', 'not-editable-cell');
						$("#dataGrid").jqGrid('setCell', row.ID, 'FULL_BOX_QTY', '', {background : '#A9A9A9'});
					}
					if(row.BARCODE_FLAG=='X' && row.LABEL_NO != undefined && row.LABEL_NO !=''){
						//仓库启用了条码，并且存在关联的标签
						$("#dataGrid").jqGrid('setCell', row.ID, 'RECEIPT_QTY', '', 'not-editable-cell');
					}
					//物料维护了最大库存，且本次收料数量+当前库存数量>大于物料最大库存，库存数量标红
					if(row.STOCK_L !='N' && Number(row.RECEIPT_QTY) + Number(row.STOCK_QTY) > Number(row.STOCK_L)){
						$("#dataGrid").jqGrid('setCell', row.ID, 'STOCK_ALL', '', {background : '#FFD700'});
					}
				});
				if(rows.length){
					$("#mat_count").html(rows.length+"行")
				}

				js.closeLoading();
				$(".btn").attr("disabled", false);
			},
			onSelectAll:function(rowids,status){
				if(status==true){
					$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
					$.each(rowids,function(i,rowid){
						var row = $("#dataGrid").jqGrid("getRowData",rowid);
						if(row.RECEIPT_FLAG=='X') {
							$("#dataGrid").jqGrid("setSelection", rowid,false);//设置该行不能被选中。
						}
					})
				}
			},
			onSelectRow:function(rowid,status){
				if(status == true){
					var row = $("#dataGrid").jqGrid('getRowData',rowid);
					if(row.RECEIPT_FLAG=='X') {
						$("#dataGrid").jqGrid("setSelection", rowid,false);//设置该行不能被选中。
					}
				}
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
				//console.info(cm);
				var row= $('#dataGrid').jqGrid('getRowData', rowid);

				if(cm[i].name == 'cb'){
					$('#dataGrid').jqGrid('setSelection',rowid);
				}
				return (cm[i].name == 'cb');

			} ,
			formatCell:function(rowid, cellname, value, iRow, iCol){
				//console.info(cellname)
				if(cellname=='LGORT'){
					var rec = $('#dataGrid').jqGrid('getRowData', rowid);
					$('#dataGrid').jqGrid('setColProp', 'LGORT', { editoptions: {value:getLotOption(rec.WERKS,rec.PO_NO,rec.PO_ITEM_NO,'Z')}
					});
				}
				if(cellname=='BIN_CODE'){
					var rec = $('#dataGrid').jqGrid('getRowData', rowid);
					$('#dataGrid').jqGrid('setColProp', 'BIN_CODE', { editoptions: {value:getGrAreaList(rec.WERKS)}
					});
				}

			},
			afterSaveCell:function(rowid, cellname, value, iRow, iCol){
				if(value==""){
					$("#dataGrid").jqGrid('setCell', rowid, cellname, '&nbsp;','editable-cell');
				}

				var row =  $("#dataGrid").jqGrid('getRowData', rowid);
				if(cellname=='RECEIPT_QTY'||cellname=='TRY_QTY'){
					var field="收货数量";
					var able_qty=row.QTY-row.FINISHED_QTY;
					if(cellname=='TRY_QTY'){
						field="试装数量";
						able_qty=row.RECEIPT_QTY;
					}
					if(Number(value)>Number(able_qty)){
						js.alert(field+"不能大于"+able_qty);
						$("#dataGrid").jqGrid('setCell', rowid, cellname, able_qty,'editable-cell');
					}

					/*					if(cellname=='RECEIPT_QTY'){
                                            $("#dataGrid").jqGrid('setCell', rowid, 'TRY_QTY', '0','editable-cell');
                                            js.alert("请重新填写试装数量!")
                                        }*/
				}

				if(cellname=='RECEIPT_QTY'){
					//物料维护了最大库存，且本次收料数量+当前库存数量>大于物料最大库存，库存数量标红
					if(row.STOCK_L !='N' && Number(row.RECEIPT_QTY) + Number(row.STOCK_QTY) > Number(row.STOCK_L)){
						$("#dataGrid").jqGrid('setCell', row.ID, 'STOCK_ALL', '', {background : '#FFD700'});
					}
					if(row.STOCK_L !='N' && Number(row.RECEIPT_QTY) + Number(row.STOCK_QTY) <= Number(row.STOCK_L)){
						$("#dataGrid").jqGrid('setCell', row.ID, 'STOCK_ALL', '', {background : '#FFFFFF'});
					}
				}

			},
		});
	}

	/**
	 * 无PO收货
	 */
	if(vm.receipt_type=='05'){//无PO收货
		columns=[
			{label:'URGENT_FLAG',name:'URGENT_FLAG',hidden:true},
			{label:'VENDOR_FLAG',name:'VENDOR_FLAG',hidden:true},
			{label:'HEADER_TXT',name:'HEADER_TXT',hidden:true},
			{label:'DELIVERY_DATE',name:'DELIVERY_DATE',hidden:true},
			{label:'GOOD_DATES',name:'GOOD_DATES',hidden:true},
			{label:'DANGER_FLAG',name:'DANGER_FLAG',hidden:true},
			{label:'MIN_GOOD_DATES',name:'MIN_GOOD_DATES',hidden:true},
			{label:'STOCK_L',name:'STOCK_L',hidden:true},
			{label:'STOCK_QTY',name:'STOCK_QTY',hidden:true},
			{label:'ID',name:'ID', index: 'ID',key: true ,hidden:true,formatter:function(val, obj, row, act){
					return obj.rowId
				}},
			{ label: '<i class=\"fa fa-plus\" style=\"color:blue\" onclick=\"addMat()\" ></i>', name: 'EMPTY_COL', index: '', align:"center",width: 30, sortable:false,formatter:function(val, obj, row, act){
					var actions = [];
					actions.push('<a href="#" onClick="delMat('+row.id+')" title="删除" data-confirm="确认要删除该行数据吗？"><i class="fa fa-times" style=\"color:red\"></i></a>');

					return actions.join(",");
				},unformat:function(cellvalue, options, rowObject){
					return "";
				}},
			{ label: '危化品', name: 'DANGER_HTML', index: 'DANGER_HTML', align:"left",width: 55, sortable:false,/*formatter:function(val, obj, row, act){
				var html="";
				if(row.DANGER_FLAG=='X'){
					html="&nbsp;&nbsp;<i class='fa fa-exclamation-triangle fa-lg' aria-hidden='true' style='color:#e3ca12'></i>"
				}else
					html='';

				return html;
			},*/},
			{ label: '收货工厂', name: 'WERKS',align:"center", index: 'WERKS', width: 65,sortable:false, },
			{ label: '仓库号', name: 'WH_NUMBER',align:"center", index: 'WH_NUMBER', width: 60,sortable:false, },
			{ label: '<span style="color:blue">料号</span>', name: 'MATNR', align:"center",index: 'MATNR', width: 110,editable:true,sortable:false,},
			{ label: '物料描述', name: 'MAKTX',align:"center", index: 'MAKTX', width: 250,sortable:false, },
			{display:'库存类型',label:'<span style="color:red">*</span><span style="color:blue">库存类型</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'SOBKZ\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name:'SOBKZ',index:'SOBKZ',align:'center',width:100,sortable:false,
				editable:true,edittype:'select',editrules:{required:true},},
			{display:'库位', label: '<span style="color:red">*</span><span style="color:blue">库位</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'LGORT\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'LGORT',align:"center", index: 'LGORT', width: 80 ,sortable:false,
				editable:true,edittype:'select',editrules:{required:true},},
			{display:'收货数量', label: '<span style="color:red">*</span><span style="color:blue">收货数量</span>', name: 'RECEIPT_QTY', align:"center",index: 'RECEIPT_QTY', width: 70,editable:true,editrules:{
					required:true,number:true}, sortable:false, formatter:'number',formatoptions:{
					decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}}	,
			{ label: '库存/最大库存', name: 'STOCK_ALL', align:"center",index: 'STOCK_ALL', width: 100,},
			{display:'满箱数量',  label: '<span style="color:red">*</span><span style="color:blue">满箱数量</span>', name: 'FULL_BOX_QTY', align:"center",index: 'FULL_BOX_QTY', width: 80 ,editable:true,editrules:{
					required:true,number:true}, sortable:false, formatter:'number',formatoptions:{
					decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}},
			{ label: '采购单位', name: 'UNIT', align:"center",index: 'UNIT', sortable:false,width: 80 }	,
			{ label: '<span style="color:red">*</span><span style="color:blue">生产日期</span>', name: 'PRODUCT_DATE', align:"center",sortable:false,index: 'PRODUCT_DATE', width: 90,editable:true,editoptions:{
					dataInit:function(e){
						$(e).click(function(e){
							WdatePicker({dateFmt:'yyyy-MM-dd'})
						});
					}
				} },
			{ label: '<span style="color:blue">收料房存放区</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'BIN_CODE\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'BIN_CODE', align:"center",index: 'BIN_CODE', width: 120,sortable:false,editable:true,edittype:'select'},
			{ label: '<span style="color:blue">收料员</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'RECEIVER\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'RECEIVER', align:"center",index: 'RECEIVER', width: 80,editable:true, sortable:false,  },
			{display:'采购',  label: '<span style="color:red">*</span><span style="color:blue">采购</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'VENDOR_MANAGER\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'VENDOR_MANAGER', align:"center",index: 'VENDOR_MANAGER', width: 70,editable:true,sortable:false,},
			{display:'供应商简称',  label: '<span style="color:red">*</span><span style="color:blue">供应商简称</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'SHORT_NAME\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'SHORT_NAME', align:"center",index: 'SHORT_NAME', width: 100,editable:true,sortable:false,},
			{ label: '供应商代码', name: 'LIFNR', align:"center",index: 'LIFNR', width: 100,sortable:false,},
			{ label: '供应商名称', name: 'LIKTX', align:"center",index: 'LIKTX', sortable:false,width: 250 },
			{ label: '<span style="color:red">*</span><span style="color:blue">需求跟踪号</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'BEDNR\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'BEDNR', align:"center",index: 'BEDNR',editable:true, width: 150,sortable:false,},
			{ label: '<span style="color:blue">收料原因</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'RECEIPT_REASON\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'RECEIPT_REASON', align:"center",index: 'RECEIPT_REASON',editable:true, width: 150,sortable:false,},
			{ label: '<span style="color:blue">行文本</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'ITEM_TEXT\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'ITEM_TEXT', align:"center",index: 'ITEM_TEXT', width: 260,editable:true,sortable:false,},
		];

		dataGrid=$("#dataGrid").dataGrid({
			datatype: "local",
			searchForm:$("#searchForm"),
			cellEdit:true,
			cellurl:'#',
			cellsubmit:'clientArray',
			colModel: columns,
			viewrecords: false,
			showRownum:false,
			autowidth:true,
			shrinkToFit:false,
			autoScroll: true,
			rownumWidth: 25,
			/*loadonce:true,*/
			sortable:false,
			rowNum:-1,
			formatter:{number:{decimalSeparator:".", thousandsSeparator: " ", decimalPlaces: 3,defaultValue:''}},
			dataId:'id',
			multiselect: multiselect,
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
				/* $('#dataGrid').jqGrid('setSelection',rowid);
                 return false;*/
			},
			formatCell:function(rowid, cellname, value, iRow, iCol){
				var rec = $('#dataGrid').jqGrid('getRowData', rowid);
				//console.info(JSON.stringify(rec))
				if(cellname=='LGORT'){
					$('#dataGrid').jqGrid('setColProp', 'LGORT', { editoptions: {value:getLotOption(rec.WERKS,null,null,rec.SOBKZ)}
					});
				}
				if(cellname=='BIN_CODE'){
					$('#dataGrid').jqGrid('setColProp', 'BIN_CODE', { editoptions: {value:getGrAreaList(rec.WERKS)}
					});
				}
				if(cellname=='SOBKZ'){
					$('#dataGrid').jqGrid('setColProp', 'SOBKZ', { editoptions: {value:getSobzkOption()}
					});
				}

			},
			afterSaveCell:function(rowid, cellname, value, iRow, iCol){
				var row =  $("#dataGrid").jqGrid('getRowData', rowid);
				console.info(row.MATNR)
				if(cellname=='RECEIPT_QTY'||cellname=='TRY_QTY'){
					var field="收货数量";
					if(cellname=='TRY_QTY'){
						field="试装数量";
					}
					var able_qty=row.QTY-row.FINISHED_QTY;
					if(cellname=='TRY_QTY'){
						able_qty=row.RECEIPT_QTY;
					}

					if(Number(value)>Number(able_qty)){
						js.alert(field+"不能大于"+able_qty);
						$("#dataGrid").jqGrid('setCell', rowid, cellname, able_qty,'editable-cell');
					}
				}

				if(cellname=='RECEIPT_QTY'){
					//物料维护了最大库存，且本次收料数量+当前库存数量>大于物料最大库存，库存数量标红
					if(row.STOCK_L !='N' && Number(row.RECEIPT_QTY) + Number(row.STOCK_QTY) > Number(row.STOCK_L)){
						$("#dataGrid").jqGrid('setCell', row.ID, 'STOCK_ALL', '', {background : '#FFD700'});
					}
					if(row.STOCK_L !='N' && Number(row.RECEIPT_QTY) + Number(row.STOCK_QTY) <= Number(row.STOCK_L)){
						$("#dataGrid").jqGrid('setCell', row.ID, 'STOCK_ALL', '', {background : '#FFFFFF'});
					}
				}

				if(cellname=='SOBKZ'){
					//根据工厂，库存类型查询库位列表
					var lgortList=getLgortList(vm.WERKS,null,null,value);
					var rows=$("#dataGrid").jqGrid('getRowData');
					$.each(rows,function(i,row){
						$("#dataGrid").jqGrid('setCell', row.ID, 'LGORT', lgortList[0].LGORT||"&nbsp;", 'editable-cell');
					});
				}

				if(cellname=='MATNR'){//根据物料号列表查询物料信息
					if(value.trim()==""){
						$("#dataGrid").jqGrid('setCell',rowid, "MATNR", '&nbsp;','editable-cell');
						$("#dataGrid").jqGrid('setCell',rowid, "MAKTX", '&nbsp;','editable-cell');
						$("#dataGrid").jqGrid('setCell',rowid, "UNIT", '&nbsp;','editable-cell');
						$("#dataGrid").jqGrid('setCell',rowid, "FULL_BOX_QTY", '0','editable-cell');
						$("#dataGrid").jqGrid('setCell',rowid, "DANGER_FLAG", '0','not-editable-cell');
						$("#dataGrid").jqGrid('setCell',rowid, "GOOD_DATES", '0','not-editable-cell');
						$("#dataGrid").jqGrid('setCell',rowid, "MIN_GOOD_DATES", '0','not-editable-cell');
						$("#dataGrid").jqGrid('setCell',rowid, "URGENT_FLAG", '0','not-editable-cell');
						$("#dataGrid").jqGrid('setCell',rowid, "STOCK_L", '0','not-editable-cell');
						$("#dataGrid").jqGrid('setCell',rowid, "STOCK_QTY", '0','not-editable-cell');
						$("#dataGrid").jqGrid('setCell',rowid, "STOCK_ALL", '0\0','not-editable-cell');

						return false;
					}
					var dist_list=[];
					dist_list.push(value)
					var ctmap={};
					ctmap.BUSINESS_NAME=vm.receipt_type;
					ctmap.JZ_DATE=$("#JZ_DATE").val();
					ctmap.LIFNR = vm.LIFNR_WPO.LIFNR;
					ctmap.LIKTX=vm.LIFNR_WPO.LIKTX;
					ctmap.WERKS=vm.WERKS;
					ctmap.WH_NUMBER=vm.WH_NUMBER;
					ctmap.matnr_list=dist_list.join(",");
					var mat_list=getMatInfo(ctmap);
					if(mat_list.length <=0){
						$("#dataGrid").jqGrid('setCell',rowid, "MATNR", '&nbsp;','editable-cell');
						$("#dataGrid").jqGrid('setCell',rowid, "MAKTX", '&nbsp;','editable-cell');
						$("#dataGrid").jqGrid('setCell',rowid, "UNIT", '&nbsp;','editable-cell');
						$("#dataGrid").jqGrid('setCell',rowid, "FULL_BOX_QTY", '0','editable-cell');
						$("#dataGrid").jqGrid('setCell',rowid, "DANGER_FLAG", '0','not-editable-cell');
						$("#dataGrid").jqGrid('setCell',rowid, "GOOD_DATES", '0','not-editable-cell');
						$("#dataGrid").jqGrid('setCell',rowid, "MIN_GOOD_DATES", '0','not-editable-cell');
						$("#dataGrid").jqGrid('setCell',rowid, "URGENT_FLAG", '0','not-editable-cell');
						$("#dataGrid").jqGrid('setCell',rowid, "STOCK_QTY", '0','not-editable-cell');
						$("#dataGrid").jqGrid('setCell',rowid, "STOCK_L", '0','not-editable-cell');
						$("#dataGrid").jqGrid('setCell',rowid, "STOCK_ALL", '0\0','not-editable-cell');
					}
					for(var i=0;i<mat_list.length;i++){
						var mat = mat_list[i];
						$("#dataGrid").jqGrid('setCell',lastrow+i, "MATNR", mat_list[i]['MATNR'],'editable-cell');
						$('#dataGrid').jqGrid("saveCell", lastrow+i, 5);
						var html="&nbsp;&nbsp;<i class='fa fa-exclamation-triangle fa-lg' aria-hidden='true' style='color:#e3ca12'></i>";
						if(mat.DANGER_FLAG=='X'){
							$("#dataGrid").jqGrid('setCell',lastrow+i, "DANGER_HTML", html,'not-editable-cell');
						}
						if(mat.URGENT_FLAG=='X'){
							$("#dataGrid").jqGrid('setRowData', lastrow+i, '', {'color':'red'});
						}
						if(mat.FULL_BOX_QTY!=null && mat.FULL_BOX_QTY !=undefined && Number(mat.FULL_BOX_QTY)>0){
							$("#dataGrid").jqGrid('setCell',lastrow+i, "FULL_BOX_QTY", mat.FULL_BOX_QTY,'not-editable-cell');
						}else{
							$("#dataGrid").jqGrid('setCell',lastrow+i, "FULL_BOX_QTY", '','editable-cell');
						}
						$("#dataGrid").jqGrid('setCell',lastrow+i, "MAKTX", mat_list[i]['MAKTX'],'editable-cell');
						$("#dataGrid").jqGrid('setCell',lastrow+i, "UNIT", mat_list[i]['UNIT'],'editable-cell');
						$("#dataGrid").jqGrid('setCell',lastrow+i, "ITEM_TEXT", mat_list[i]['ITEM_TEXT'],'editable-cell');

						$("#dataGrid").jqGrid('setCell',lastrow+i, "HEADER_TXT", mat_list[i]['HEADER_TXT'],'not-editable-cell');
						$("#dataGrid").jqGrid('setCell',lastrow+i, "DANGER_FLAG", mat_list[i]['DANGER_FLAG']||"0",'not-editable-cell');
						$("#dataGrid").jqGrid('setCell',lastrow+i, "GOOD_DATES", mat_list[i]['GOOD_DATES'],'not-editable-cell');
						$("#dataGrid").jqGrid('setCell',lastrow+i, "MIN_GOOD_DATES", mat_list[i]['MIN_GOOD_DATES'],'not-editable-cell');
						$("#dataGrid").jqGrid('setCell',lastrow+i, "STOCK_QTY", mat_list[i]['STOCK_QTY'],'not-editable-cell');
						$("#dataGrid").jqGrid('setCell',lastrow+i, "STOCK_L", mat_list[i]['STOCK_L'],'not-editable-cell');
						$("#dataGrid").jqGrid('setCell',lastrow+i, "STOCK_ALL", mat_list[i]['STOCK_ALL'],'not-editable-cell');
						row.HEADER_TXT=mat_list[i]['HEADER_TXT'];
						row.DANGER_FLAG=mat_list[i]['DANGER_FLAG']||"0";
						row.UNIT=mat_list[i]['UNIT'];
						row.ITEM_TEXT=mat_list[i]['ITEM_TEXT'];
						row.MATNR=mat_list[i]['MATNR'];
						row.MAKTX=mat_list[i]['MAKTX'];
						row.GOOD_DATES = mat_list[i]['GOOD_DATES'];
						row.MIN_GOOD_DATES = mat_list[i]['MIN_GOOD_DATES'];
						row.STOCK_QTY = mat_list[i]['STOCK_QTY'];
						row.STOCK_L = mat_list[i]['STOCK_L'];
						row.STOCK_ALL = mat_list[i]['STOCK_ALL'];
					}
				}
			},
		});
	}

	/**
	 * 303调拨收料(A)
	 */
	if(vm.receipt_type=='09'){
		columns=[
			{label:'URGENT_FLAG',name:'URGENT_FLAG',hidden:true},
			{label:'DANGER_FLAG',name:'DANGER_FLAG',hidden:true},
			{label:'VENDOR_FLAG',name:'VENDOR_FLAG',hidden:true},
			{label:'RECEIPT_FLAG',name:'RECEIPT_FLAG',hidden:true},//是否可收货标识，X 表示不能收货，存在还需核销数量
			{label:'HEADER_TXT',name:'HEADER_TXT',hidden:true},
			{label:'SOBKZ',name:'SOBKZ',hidden:true},
			{label:'GOOD_DATES',name:'GOOD_DATES',hidden:true},
			{ label: '序号', name: 'ID', index: 'ID', align:"left",width: 60, key: true ,sortable:false,formatter:function(val, obj, row, act){
					var html=obj.rowId;
					if(row.DANGER_FLAG=='X'){
						html+="&nbsp;&nbsp;<i class='fa fa-exclamation-triangle fa-lg' aria-hidden='true' style='color:#e3ca12'></i>"
					}
					if(row.RECEIPT_FLAG=='X'){
						html+="&nbsp;&nbsp;<span style='color:red;font-size:13px;font-weight:bold'>HX</span>";
					}
					return html;
				},unformat:function(cellvalue, options, rowObject){
					return Number(cellvalue);
				}},
			{ label: '收货工厂', name: 'WERKS',align:"center", index: 'WERKS', width: 80 },
			{ label: '仓库号', name: 'WH_NUMBER',align:"center", index: 'WH_NUMBER', width: 80 },
			{ label: '凭证号 ', name: 'SAP_MATDOC_NO', align:"center",index: 'SAP_MATDOC_NO', width: 100},
			{ label: '行号', name: 'SAP_MATDOC_ITEM_NO',align:"center", index: 'SAP_MATDOC_ITEM_NO', width: 80,},
			{ label: '料号', name: 'MATNR', align:"center",index: 'MATNR', width: 100 },
			{ label: '物料描述', name: 'MAKTX',align:"center", index: 'MAKTX', width: 180 },
			{  display:'接收库位',label: '<span style="color:red">*</span><span style="color:blue">接收库位</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'LGORT\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'LGORT',align:"center", index: 'LGORT', width: 100 ,editable:true,sortable:false,edittype:'select',editrules:{
					required:true}},
			{ label: '还需核销数量', name: 'HX_QTY_XS',align:"center", index: 'HX_QTY_XS', width: 100 },
			{  display:'收货数量',label: '<span style="color:red">*</span><span style="color:blue">收货数量</span>', name: 'RECEIPT_QTY', align:"center",index: 'RECEIPT_QTY', width: 80,editable:true,editrules:{
					required:true,number:true},  formatter:'number',formatoptions:{
					decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}}	,
			{ label: '单位', name: 'UNIT', align:"center",index: 'UNIT', width: 80 }	,
			{ label: '<span style="color:red">*</span><span style="color:blue">满箱数量</span>', name: 'FULL_BOX_QTY', align:"center",index: 'FULL_BOX_QTY', width: 80 ,editable:true,editrules:{
					required:true,number:true},  formatter:'number',formatoptions:{
					decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}},
			{ label: '<span style="color:blue">生产日期</span>', name: 'PRODUCT_DATE', align:"center",index: 'PRODUCT_DATE', width: 80,editable:true,editoptions:{
					dataInit:function(e){
						$(e).click(function(e){
							WdatePicker({dateFmt:'yyyy-MM-dd'})
						});
					}
				} },
			{ label: '<span style="color:blue">收料房存放区</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'BIN_CODE\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'BIN_CODE', align:"center",index: 'BIN_CODE', width: 110,editable:true,edittype:'select',  sortable:false,},
			{ label: '<span style="color:blue">收料员</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'RECEIVER\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'RECEIVER', align:"center",index: 'RECEIVER', width: 90,editable:true, sortable:false, },
			{ display:'采购',label: '<span style="color:red">*</span><span style="color:blue">采购</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'VENDOR_MANAGER\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'VENDOR_MANAGER', align:"center",editrules:{required:false},index: 'VENDOR_MANAGER', width: 70,editable:true, sortable:false, },
			{ display:'供应商简称',label: '<span style="color:red">*</span><span style="color:blue">供应商简称</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'SHORT_NAME\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'SHORT_NAME', align:"center",editrules:{required:false},index: 'SHORT_NAME', width: 100,editable:true, sortable:false,  },
			{ label: '供应商名称', name: 'LIKTX', align:"center",index: 'LIKTX', width: 80 },
			{ label: '供应商代码', name: 'LIFNR', align:"center",index: 'LIFNR', width: 80,},
			{ label: '<span style="color:blue">需求跟踪号</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'BEDNR\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'BEDNR', align:"center",index: 'BEDNR', width: 150,editable:true,sortable:false,},
			{ label: '<span style="color:blue">行文本</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'ITEM_TEXT\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'ITEM_TEXT', align:"center",index: 'ITEM_TEXT', width: 260,editable:true,sortable:false,},
		];

		dataGrid=$("#dataGrid").dataGrid({
			datatype: "local",
			searchForm:$("#searchForm"),
			cellEdit:true,
			cellurl:'#',
			cellsubmit:'clientArray',
			colModel: columns,
			viewrecords: false,
			showRownum:false,
			autowidth:true,
			shrinkToFit:false,
			autoScroll: true,
			rownumWidth: 25,
			rowNum:-1,
			formatter:{number:{decimalSeparator:".", thousandsSeparator: " ", decimalPlaces: 3,defaultValue:''}},
			/*  dataId:'ID',*/
			multiselect: multiselect,
			loadComplete:function(data){
				if(data.code==500){
					js.showErrorMessage(data.msg,1000*10);
					//js.alert(data.msg);
					js.closeLoading();
					$(".btn").attr("disabled", false);
					return false;
				}
				var rows=$("#dataGrid").jqGrid('getRowData');
				if(rows.length <=0){
					js.closeLoading();
					$(".btn").attr("disabled", false);
					if(data.code == 0){
						js.showErrorMessage("未查询到有效数据，无收货可能！");
					}
					return false;
				}

				//查询收料房存放区，默认第一个值
				var BIN_CODE="";
				var WERKS=rows[0].WERKS;
				vm.WERKS=WERKS;

				gr_area_list=getGrAreaList(WERKS);
				if(gr_area_list.length>0){
					BIN_CODE=gr_area_list.split(";")[0].split(":")[0];
				}
				//alert(BIN_CODE)

				var bxtotal=0;
				$.each(rows,function(i,row){
					//console.info(row)
					$("#dataGrid").jqGrid('setCell', row.ID, "BIN_CODE", BIN_CODE,'editable-cell');//给收料房存放区赋默认值
					$("#dataGrid").jqGrid('setCell', row.ID, "WH_NUMBER", $("#WH_NUMBER").val(),'editable-cell');//仓库号默认为页面仓库下拉列表的值
					bxtotal+=Number(row.BOX_COUNT);
					if (row.URGENT_FLAG=='X') {//过滤条件
						$("#dataGrid").jqGrid('setRowData', row.ID, '', {'color':'red'});
					}

					if(row.VENDOR_FLAG!='X'){
						$("#dataGrid").jqGrid('setCell', row.ID, 'VENDOR_MANAGER', '', 'not-editable-cell');
						$("#dataGrid").jqGrid('setCell', row.ID, 'SHORT_NAME', '', 'not-editable-cell');
						$("#dataGrid").jqGrid('setLabel','VENDOR_MANAGER','采购');
						$("#dataGrid").jqGrid('setLabel','SHORT_NAME','供应商简称');
					}
					if(row.RECEIPT_FLAG=='X'){
						//  alert($("#dataGrid tbody").children("tr").eq(row.ID).html())
						$("#dataGrid tbody").children("tr").eq(row.ID).children("td").find(".cbox").attr("disabled",true);
					}

				});

				if(rows.length){
					$("#mat_count").html(rows.length+"行")
				}

				js.closeLoading();
				$(".btn").attr("disabled", false);
			},
			onSelectAll:function(rowids,status){
				if(status==true){
					$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
					$.each(rowids,function(i,rowid){
						var row = $("#dataGrid").jqGrid("getRowData",rowid);
						if(row.RECEIPT_FLAG=='X') {
							$("#dataGrid").jqGrid("setSelection", rowid,false);//设置该行不能被选中。
						}
					})
				}
			},
			onSelectRow:function(rowid,status){
				if(status == true){
					var row = $("#dataGrid").jqGrid('getRowData',rowid);
					if(row.RECEIPT_FLAG=='X') {
						$("#dataGrid").jqGrid("setSelection", rowid,false);//设置该行不能被选中。
					}
				}
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
				//console.info(cm);
				var row= $('#dataGrid').jqGrid('getRowData', rowid);

				if(cm[i].name == 'cb'){
					$('#dataGrid').jqGrid('setSelection',rowid);
				}
				return (cm[i].name == 'cb');

			} ,
			formatCell:function(rowid, cellname, value, iRow, iCol){
				//console.info(cellname)
				if(cellname=='LGORT'){
					var rec = $('#dataGrid').jqGrid('getRowData', rowid);
					$('#dataGrid').jqGrid('setColProp', 'LGORT', { editoptions: {value:getLotOption(rec.WERKS,rec.PO_NO,rec.PO_ITEM_NO,'Z')}
					});
				}
				if(cellname=='BIN_CODE'){
					var rec = $('#dataGrid').jqGrid('getRowData', rowid);
					$('#dataGrid').jqGrid('setColProp', 'BIN_CODE', { editoptions: {value:getGrAreaList(rec.WERKS)}
					});
				}

			},
			afterSaveCell:function(rowid, cellname, value, iRow, iCol){
				if(value==""){
					$("#dataGrid").jqGrid('setCell', rowid, cellname, '&nbsp;','editable-cell');
				}

				var row =  $("#dataGrid").jqGrid('getRowData', rowid);
				if(cellname=='RECEIPT_QTY'||cellname=='TRY_QTY'){
					var field="收货数量";
					var able_qty=row.QTY-row.FINISHED_QTY;
					if(cellname=='TRY_QTY'){
						field="试装数量";
						able_qty=row.RECEIPT_QTY;
					}
					if(Number(value)>Number(able_qty)){
						js.alert(field+"不能大于"+able_qty);
						$("#dataGrid").jqGrid('setCell', rowid, cellname, able_qty,'editable-cell');
					}

					/*					if(cellname=='RECEIPT_QTY'){
                                            $("#dataGrid").jqGrid('setCell', rowid, 'TRY_QTY', '0','editable-cell');
                                            js.alert("请重新填写试装数量!")
                                        }*/
				}
			},
		});
	}

	if(vm.receipt_type=='07'){//SAP采购订单收料(A)
		columns=[
			{ label: '序号', name: 'ID', index: 'ID', align:"left",width: 60,frozen: true, key: true ,sortable:false,formatter:function(val, obj, row, act){
					var html=obj.rowId;
					if(row.DANGER_FLAG=='X'){
						html+="&nbsp;&nbsp;<i class='fa fa-exclamation-triangle fa-lg' aria-hidden='true' style='color:#e3ca12'></i>"
					}
					if(row.RECEIPT_FLAG=='X'){
						html+="&nbsp;&nbsp;<span style='color:red;font-size:13px;font-weight:bold'>HX</span>";
					}
					return html;
				},unformat:function(cellvalue, options, rowObject){
					return Number(cellvalue);
				}},
			{ label: '收货工厂', name: 'WERKS',align:"center", frozen: true,index: 'WERKS', width: 65 },
			{ label: '仓库号', name: 'WH_NUMBER',align:"center",frozen: true, index: 'WH_NUMBER', width: 60 },
			{ label: '订单号 ', name: 'PO_NO', align:"center",frozen: true,index: 'PO_NO', width: 100},
			{ label: '行号', name: 'PO_ITEM_NO',align:"center",frozen: true, index: 'PO_ITEM_NO', width: 50,},
			{ label: '料号', name: 'MATNR', align:"center",frozen: true,index: 'MATNR', width: 100 },
			{ label: '物料描述', name: 'MAKTX',align:"center",frozen: true, index: 'MAKTX', width: 220 },
			{ label: '<span style="color:blue">库位</span>', name: 'LGORT',align:"center", index: 'LGORT', width: 60 ,editable:true,edittype:'select'},
			{ label: '还需核销数量', name: 'HX_QTY',align:"center", index: 'HX_QTY', width: 110 },
			{ display:'收货数量',label: '<span style="color:red">*</span><span style="color:blue">收货数量</span>', name: 'RECEIPT_QTY', align:"center",index: 'RECEIPT_QTY', width: 70,editable:true,editrules:{
					required:true,number:true},  formatter:'number',formatoptions:{
					decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}}	,
			{ label: '单位', name: 'UNIT', align:"center",index: 'UNIT', width: 70 }	,
			{ display:'满箱数量',label: '<span style="color:red">*</span><span style="color:blue">满箱数量</span>', name: 'FULL_BOX_QTY', align:"center",index: 'FULL_BOX_QTY', width: 70 ,editable:true,editrules:{
					required:true,number:true},  formatter:'number',formatoptions:{
					decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}},
			{ label: '<span style="color:blue">收料房存放区</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'BIN_CODE\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'BIN_CODE', align:"center",index: 'BIN_CODE', width: 110,editable:true,edittype:'select',  sortable:false,},
			{ label: '<span style="color:blue">收料员</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'RECEIVER\')" title="向下填充" style="color:green" aria-hidden="true"></i>', name: 'RECEIVER', align:"center",index: 'RECEIVER', width: 90,editable:true, sortable:false, },
			{ label: '<span style="color:red"></span><span style="color:blue">采购</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'VENDOR_MANAGER\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'VENDOR_MANAGER', align:"center",index: 'VENDOR_MANAGER', width: 70,editable:true,  sortable:false, },
			{ label: '<span style="color:red"></span><span style="color:blue">供应商简称</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'SHORT_NAME\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'SHORT_NAME', align:"center",index: 'SHORT_NAME', width: 100,editable:true, sortable:false,  },
			{ display:'生产日期',label: '<span style="color:red">*</span><span style="color:blue">生产日期</span>', name: 'PRODUCT_DATE', align:"center",index: 'PRODUCT_DATE', width: 80,editable:true,editoptions:{
					dataInit:function(e){
						$(e).click(function(e){
							WdatePicker({dateFmt:'yyyy-MM-dd'})
						});
					}
				} },
			{ label: '供应商名称', name: 'LIKTX', align:"center",index: 'LIKTX', width: 80 },
			{ label: '供应商代码', name: 'LIFNR', align:"center",index: 'LIFNR', width: 80,},
			{ label: '需求跟踪号', name: 'BEDNR', align:"center",index: 'BEDNR', width: 80,},
			{ label: '申请人', name: 'AFNAM', align:"center",index: 'AFNAM', width: 80,},
			{ label: '<span style="color:blue">行文本</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'ITEM_TEXT\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'ITEM_TEXT', align:"center",index: 'ITEM_TEXT', width: 260,editable:true,sortable:false,},
			{label:'URGENT_FLAG',name:'URGENT_FLAG',hidden:true},
			{label:'DANGER_FLAG',name:'DANGER_FLAG',hidden:true},
			{label:'VENDOR_FLAG',name:'VENDOR_FLAG',hidden:true},
			{label:'RECEIPT_FLAG',name:'RECEIPT_FLAG',hidden:true},//是否可收货标识，X 表示不能收货，存在还需核销数量
			{label:'HEADER_TXT',name:'HEADER_TXT',hidden:true},
			{label:'DELIVERY_DATE',name:'DELIVERY_DATE',hidden:true},//预计送货时间
			{label:'SOBKZ',name:'SOBKZ',hidden:true},
			{label:'GOOD_DATES',name:'GOOD_DATES',hidden:true},
		];

		dataGrid=$("#dataGrid").dataGrid({
			datatype: "local",
			data:gridData,
			searchForm:$("#searchForm"),
			cellEdit:true,
			cellurl:'#',
			cellsubmit:'clientArray',
			colModel: columns,
			viewrecords: false,
			showRownum:false,
			autowidth:true,
			shrinkToFit:false,
			autoScroll: true,
			rownumWidth: 25,
			rowNum:-1,
			formatter:{number:{decimalSeparator:".", thousandsSeparator: " ", decimalPlaces: 3,defaultValue:''}},
			/* dataId:'id',*/
			multiselect: multiselect,
			loadComplete:function(data){
				if(data.code==500){
					js.showErrorMessage(data.msg,1000*10);
					//js.alert(data.msg);
					js.closeLoading();
					$(".btn").attr("disabled", false);
					return false;
				}
				var rows=$("#dataGrid").jqGrid('getRowData');
				if(rows.length <= 0){
					js.closeLoading();
					$(".btn").attr("disabled", false);
					if(data.code == 0){
						js.showErrorMessage("未查询到有效数据，无收货可能！");
					}
					return false;
				}

				//查询收料房存放区，默认第一个值
				var BIN_CODE="";
				var WERKS=rows[0].WERKS;
				vm.WERKS=WERKS;

				gr_area_list=getGrAreaList(WERKS);
				if(gr_area_list.length>0){
					BIN_CODE=gr_area_list.split(";")[0].split(":")[0];
				}
				//alert(BIN_CODE)
				var bxtotal=0;
				$.each(rows,function(i,row){
					//console.info(row)
					$("#dataGrid").jqGrid('setCell', row.ID, "BIN_CODE", BIN_CODE,'editable-cell');//给收料房存放区赋默认值
					$("#dataGrid").jqGrid('setCell', row.ID, "WH_NUMBER", $("#WH_NUMBER").val(),'editable-cell');//仓库号默认为页面仓库下拉列表的值
					bxtotal+=Number(row.BOX_COUNT);
					if (row.URGENT_FLAG=='X') {//过滤条件
						$("#dataGrid").jqGrid('setRowData', row.ID, '', {'color':'red'});
					}

					if(row.VENDOR_FLAG!='X'){
						$("#dataGrid").jqGrid('setCell', row.ID, 'VENDOR_MANAGER', '', 'not-editable-cell');
						$("#dataGrid").jqGrid('setCell', row.ID, 'SHORT_NAME', '', 'not-editable-cell');
						$("#dataGrid").jqGrid('setLabel','VENDOR_MANAGER','采购');
						$("#dataGrid").jqGrid('setLabel','SHORT_NAME','供应商简称');
					}
					if(row.RECEIPT_FLAG=='X'){
						//  alert($("#dataGrid tbody").children("tr").eq(row.ID).html())
						$("#dataGrid tbody").children("tr").eq(row.ID).children("td").find(".cbox").attr("disabled",true);
					}

				});
				if(rows.length){
					$("#mat_count").html(rows.length+"行")
				}

				js.closeLoading();
				$(".btn").attr("disabled", false);
			},
			onSelectAll:function(rowids,status){
				if(status==true){
					$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
					$.each(rowids,function(i,rowid){
						var row = $("#dataGrid").jqGrid("getRowData",rowid);
						if(row.RECEIPT_FLAG=='X') {
							$("#dataGrid").jqGrid("setSelection", rowid,false);//设置该行不能被选中。
						}
					})
				}
			},
			onSelectRow:function(rowid,status){
				if(status == true){
					var row = $("#dataGrid").jqGrid('getRowData',rowid);
					if(row.RECEIPT_FLAG=='X') {
						$("#dataGrid").jqGrid("setSelection", rowid,false);//设置该行不能被选中。
					}
				}
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
			} ,
			formatCell:function(rowid, cellname, value, iRow, iCol){
				//console.info(cellname)
				if(cellname=='LGORT'){
					var rec = $('#dataGrid').jqGrid('getRowData', rowid);
					$('#dataGrid').jqGrid('setColProp', 'LGORT', { editoptions: {value:getLotOption(rec.WERKS,rec.PO_NO,rec.PO_ITEM_NO)}
					});
				}
				if(cellname=='BIN_CODE'){
					var rec = $('#dataGrid').jqGrid('getRowData', rowid);
					$('#dataGrid').jqGrid('setColProp', 'BIN_CODE', { editoptions: {value:getGrAreaList(rec.WERKS)}
					});
				}

			},
			afterSaveCell:function(rowid, cellname, value, iRow, iCol){
				if(value==""){
					$("#dataGrid").jqGrid('setCell', rowid, cellname, '&nbsp;','editable-cell');
				}

				var row =  $("#dataGrid").jqGrid('getRowData', rowid);
				if(cellname=='RECEIPT_QTY'||cellname=='TRY_QTY'){
					var field="收货数量";
					var able_qty=row.QTY-row.FINISHED_QTY;
					if(cellname=='TRY_QTY'){
						field="试装数量";
						able_qty=row.RECEIPT_QTY;
					}
					if(Number(value)>Number(able_qty)){
						js.alert(field+"不能大于"+able_qty);
						$("#dataGrid").jqGrid('setCell', rowid, cellname, able_qty,'editable-cell');
					}
				}
			},
		});
		//alert("setFrozenColumns")
	}

	if(vm.receipt_type=='08'){//SAP采购订单收料(A)
		columns=[
			{ label: '序号', name: 'ID', index: 'ID', align:"left",width: 60,frozen: true, key: true ,sortable:false,formatter:function(val, obj, row, act){
					var html=obj.rowId;
					if(row.DANGER_FLAG=='X'){
						html+="&nbsp;&nbsp;<i class='fa fa-exclamation-triangle fa-lg' aria-hidden='true' style='color:#e3ca12'></i>"
					}
					if(row.RECEIPT_FLAG=='X'){
						html+="&nbsp;&nbsp;<span style='color:red;font-size:13px;font-weight:bold'>HX</span>";
					}
					return html;
				},unformat:function(cellvalue, options, rowObject){
					return Number(cellvalue);
				}},
			{ label: '收货工厂', name: 'WERKS',align:"center", frozen: true,index: 'WERKS', width: 65 },
			{ label: '仓库号', name: 'WH_NUMBER',align:"center",frozen: true, index: 'WH_NUMBER', width: 60 },
			{ label: '交货单号 ', name: 'SAP_OUT_NO', align:"center",frozen: true,index: 'SAP_OUT_NO', width: 100},
			{ label: '行号', name: 'SAP_OUT_ITEM_NO',align:"center",frozen: true, index: 'SAP_OUT_ITEM_NO', width: 50,},
			{ label: '料号', name: 'MATNR', align:"center",frozen: true,index: 'MATNR', width: 100 },
			{ label: '物料描述', name: 'MAKTX',align:"center",frozen: true, index: 'MAKTX', width: 220 },
			{ display:'库位', label: '<span style="color:red">*</span><span style="color:blue">库位</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'LGORT\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'LGORT',align:"center", index: 'LGORT', width: 80 ,editable:true,edittype:'select',sortable:false, editrules:{required:true}},
			{ label: '还需核销数量', name: 'HX_QTY_XS',align:"center", index: 'HX_QTY_XS', width: 110 },
			{ display:'收货数量',label: '<span style="color:red">*</span><span style="color:blue">收货数量</span>', name: 'RECEIPT_QTY', align:"center",index: 'RECEIPT_QTY', width: 70,editable:true,editrules:{
					required:true,number:true},  formatter:'number',formatoptions:{
					decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}}	,
			{ label: '单位', name: 'UNIT', align:"center",index: 'UNIT', width: 70 }	,
			{ display:'满箱数量',label: '<span style="color:red">*</span><span style="color:blue">满箱数量</span>', name: 'FULL_BOX_QTY', align:"center",index: 'FULL_BOX_QTY', width: 70 ,editable:true,editrules:{
					required:true,number:true},  formatter:'number',formatoptions:{
					decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 3,defaultValue:''}},
			{ label: '<span style="color:blue">收料房存放区</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'BIN_CODE\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'BIN_CODE', align:"center",index: 'BIN_CODE', width: 110,editable:true,edittype:'select',  sortable:false,},
			{ label: '<span style="color:blue">收料员</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'RECEIVER\')" title="向下填充" style="color:green" aria-hidden="true"></i>', name: 'RECEIVER', align:"center",index: 'RECEIVER', width: 90,editable:true, sortable:false, },
			{display:'采购', label: '<span style="color:red">*</span><span style="color:blue">采购</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'VENDOR_MANAGER\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'VENDOR_MANAGER', align:"center",index: 'VENDOR_MANAGER', width: 70,editable:true,  sortable:false,editrules:{required:false} },
			{display:'供应商简称', label: '<span style="color:red">*</span><span style="color:blue">供应商简称</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'SHORT_NAME\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'SHORT_NAME', align:"center",index: 'SHORT_NAME', width: 100,editable:true,editrules:{required:false} , sortable:false,  },
			{ display:'生产日期',label: '<span style="color:red">*</span><span style="color:blue">生产日期</span>', name: 'PRODUCT_DATE', align:"center",index: 'PRODUCT_DATE', width: 80,editable:true,editoptions:{
					dataInit:function(e){
						$(e).click(function(e){
							WdatePicker({dateFmt:'yyyy-MM-dd'})
						});
					}
				} },
			{ label: '供应商名称', name: 'LIKTX', align:"center",index: 'LIKTX', width: 80 },
			{ label: '供应商代码', name: 'LIFNR', align:"center",index: 'LIFNR', width: 80,},
			{ label: '采购订单', name: 'PO_NO', align:"center",index: 'PO_NO', width: 80 },
			{ label: '行号', name: 'PO_ITEM_NO', align:"center",index: 'PO_ITEM_NO', width: 50,},
			{ label: '需求跟踪号', name: 'BEDNR', align:"center",index: 'BEDNR', width: 80,},
			{ label: '申请人', name: 'AFNAM', align:"center",index: 'AFNAM', width: 80,},
			{ label: '<span style="color:blue">行文本</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'ITEM_TEXT\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
				name: 'ITEM_TEXT', align:"center",index: 'ITEM_TEXT', width: 260,editable:true,sortable:false,},
			{label:'URGENT_FLAG',name:'URGENT_FLAG',hidden:true},
			{label:'DANGER_FLAG',name:'DANGER_FLAG',hidden:true},
			{label:'VENDOR_FLAG',name:'VENDOR_FLAG',hidden:true},
			{label:'RECEIPT_FLAG',name:'RECEIPT_FLAG',hidden:true},//是否可收货标识，X 表示不能收货，存在还需核销数量
			{label:'HEADER_TXT',name:'HEADER_TXT',hidden:true},
			{label:'DELIVERY_DATE',name:'DELIVERY_DATE',hidden:true},//预计送货时间
			{label:'SOBKZ',name:'SOBKZ',hidden:true},
			{label:'GOOD_DATES',name:'GOOD_DATES',hidden:true},
		];

		dataGrid=$("#dataGrid").dataGrid({
			datatype: "local",
			data:gridData,
			searchForm:$("#searchForm"),
			cellEdit:true,
			cellurl:'#',
			cellsubmit:'clientArray',
			colModel: columns,
			viewrecords: false,
			showRownum:false,
			autowidth:true,
			shrinkToFit:false,
			autoScroll: true,
			rownumWidth: 25,
			rowNum:-1,
			formatter:{number:{decimalSeparator:".", thousandsSeparator: " ", decimalPlaces: 3,defaultValue:''}},
			/* dataId:'id',*/
			multiselect: multiselect,
			loadComplete:function(data){
				if(data.code==500){
					js.showErrorMessage(data.msg,1000*10);
					//js.alert(data.msg);
					js.closeLoading();
					$(".btn").attr("disabled", false);
					return false;
				}

				var rows=$("#dataGrid").jqGrid('getRowData');

				if(rows.length <= 0){
					js.closeLoading();
					$(".btn").attr("disabled", false);
					if(data.code == 0){
						js.showErrorMessage("未查询到有效数据，无收货可能！");
					}
					return false;
				}

				//查询收料房存放区，默认第一个值
				var BIN_CODE="";
				var WERKS=rows[0].WERKS;
				vm.WERKS=WERKS;

				gr_area_list=getGrAreaList(WERKS);
				if(gr_area_list.length>0){
					BIN_CODE=gr_area_list.split(";")[0].split(":")[0];
				}
				//alert(BIN_CODE)
				$.each(rows,function(i,row){
					//console.info(row)
					$("#dataGrid").jqGrid('setCell', row.ID, "BIN_CODE", BIN_CODE,'editable-cell');//给收料房存放区赋默认值
					$("#dataGrid").jqGrid('setCell', row.ID, "WH_NUMBER", $("#WH_NUMBER").val(),'editable-cell');//仓库号默认为页面仓库下拉列表的值
					if (row.URGENT_FLAG=='X') {//过滤条件
						$("#dataGrid").jqGrid('setRowData', row.ID, '', {'color':'red'});
					}

					if(row.VENDOR_FLAG!='X'){
						$("#dataGrid").jqGrid('setCell', row.ID, 'VENDOR_MANAGER', '', 'not-editable-cell');
						$("#dataGrid").jqGrid('setCell', row.ID, 'SHORT_NAME', '', 'not-editable-cell');
						$("#dataGrid").jqGrid('setLabel','VENDOR_MANAGER','采购');
						$("#dataGrid").jqGrid('setLabel','SHORT_NAME','供应商简称');
					}
					if(row.RECEIPT_FLAG=='X'){
						//  alert($("#dataGrid tbody").children("tr").eq(row.ID).html())
						$("#dataGrid tbody").children("tr").eq(row.ID).children("td").find(".cbox").attr("disabled",true);
					}

				});
				if(rows.length){
					$("#mat_count").html(rows.length+"行")
				}

				js.closeLoading();
				$(".btn").attr("disabled", false);
			},
			onSelectAll:function(rowids,status){
				if(status==true){
					$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
					$.each(rowids,function(i,rowid){
						var row = $("#dataGrid").jqGrid("getRowData",rowid);
						if(row.RECEIPT_FLAG=='X') {
							$("#dataGrid").jqGrid("setSelection", rowid,false);//设置该行不能被选中。
						}
					})
				}
			},
			onSelectRow:function(rowid,status){
				if(status == true){
					var row = $("#dataGrid").jqGrid('getRowData',rowid);
					if(row.RECEIPT_FLAG=='X') {
						$("#dataGrid").jqGrid("setSelection", rowid,false);//设置该行不能被选中。
					}
				}
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
			} ,
			formatCell:function(rowid, cellname, value, iRow, iCol){
				//console.info(cellname)
				if(cellname=='LGORT'){
					var rec = $('#dataGrid').jqGrid('getRowData', rowid);
					$('#dataGrid').jqGrid('setColProp', 'LGORT', { editoptions: {value:getLotOption(rec.WERKS,rec.PO_NO,rec.PO_ITEM_NO)}
					});
				}
				if(cellname=='BIN_CODE'){
					var rec = $('#dataGrid').jqGrid('getRowData', rowid);
					$('#dataGrid').jqGrid('setColProp', 'BIN_CODE', { editoptions: {value:getGrAreaList(rec.WERKS)}
					});
				}

			},
			afterSaveCell:function(rowid, cellname, value, iRow, iCol){
				if(value==""){
					$("#dataGrid").jqGrid('setCell', rowid, cellname, '&nbsp;','editable-cell');
				}

				var row =  $("#dataGrid").jqGrid('getRowData', rowid);
				if(cellname=='RECEIPT_QTY'||cellname=='TRY_QTY'){
					var field="收货数量";
					var able_qty=row.QTY-row.FINISHED_QTY;
					if(cellname=='TRY_QTY'){
						field="试装数量";
						able_qty=row.RECEIPT_QTY;
					}
					if(Number(value)>Number(able_qty)){
						js.alert(field+"不能大于"+able_qty);
						$("#dataGrid").jqGrid('setCell', rowid, cellname, able_qty,'editable-cell');
					}
				}
			},
		});
		//alert("setFrozenColumns")
	}
	/*$("#dataGrid").jqGrid('setLabel','rn', '序号', {'text-align':'center','width':'40px'},'');*/
	/*	$("#dataGrid").jqGrid('navGrid', "#dataGridPage", { edit : false, add : false, del : true });
        $("#dataGrid").jqGrid('inlineNav', "#dataGridPage")*/
}

function getLotOption(WERKS,PO_NO,PO_ITEM_NO,SOBKZ){
	var options="";
	var lgortList=getLgortList(WERKS,PO_NO,PO_ITEM_NO,SOBKZ);
	for(i=0;i<lgortList.length;i++){
		if(i != lgortList.length - 1) {
			options += lgortList[i].LGORT + ":" +lgortList[i].LGORT + ";";
		} else {
			options +=lgortList[i].LGORT + ":" + lgortList[i].LGORT;
		}
	}
	//alert(options)
	//console.info(options)

	return options;
}

function getGrAreaList(WERKS){
	var options="";
	$.ajax({
		url:baseUrl+'common/getGrAreaList',
		type:'post',
		async:false,
		dataType:'json',
		data:{
			WERKS:WERKS,
		},
		success:function(resp){
			//alert(resp.msg)
			if(resp.msg=='success'){
				for(i=0;i<resp.data.length;i++){
					if(i != resp.data.length - 1) {
						options += resp.data[i].AREA_CODE + ":" +resp.data[i].AREA_CODE + ";";
					} else {
						options += resp.data[i].AREA_CODE + ":" + resp.data[i].AREA_CODE;
					}
				}
				//alert(options)
				//console.info(options)

			}
		}
	})
	return options;
}

function addMat(mat){
	if(vm.LIFNR_WPO.LIFNR == undefined ||  vm.LIFNR_WPO.LIFNR == null){
		js.alert("请先输入供应商！");
		return false;
	}
	var selectedId = $("#dataGrid").jqGrid("getGridParam", "selrow");
	var ids = $("#dataGrid").jqGrid('getDataIDs');

	//console.info(ids)
	if(ids.length==0){
		ids=[0]
	}
	var rowid = Math.max.apply(null,ids) + 1;
	//查询收料房存放区，默认第一个值
	var BIN_CODE="";
	var WERKS=vm.WERKS;
	var SOBKZ=sobkz_list[1].CODE||"Z";
	if(gr_area_list.length>0){
		BIN_CODE=gr_area_list.split(";")[0].split(":")[0];
	}
	//根据工厂，库存类型查询库位列表
	var lgortList=getLgortList(WERKS,null,null,SOBKZ);
	if(lgortList == undefined || lgortList == null || lgortList.length <=0){
		js.showErrorMessage("工厂："+WERKS+"未配置库存地点！");
		return false;
	}
	//alert(BIN_CODE)
	var data={};
	data.id=rowid;
	data.WERKS=vm.WERKS;
	data.WH_NUMBER=vm.WH_NUMBER;
	data.VENDOR_FLAG=vm.VENDOR_FLAG;
	data.BIN_CODE=BIN_CODE;
	data.PRODUCT_DATE=getCurDate();
	data.SOBKZ=SOBKZ;
	data.LGORT=lgortList[0].LGORT||"";
	data.URGENT_FLAG="0";
	data.DANGER_FLAG="0";

	data.LIFNR = vm.LIFNR_WPO.LIFNR ;
	data.LIKTX = vm.LIFNR_WPO.LIKTX ;
	data.SHORT_NAME = vm.LIFNR_WPO.SHORT_NAME ;
	data.VENDOR_MANAGER = vm.LIFNR_WPO.VENDOR_MANAGER ;

	if(mat){
		data.MAKTX=mat.MAKTX;
		data.UNIT=mat.UNIT;
		data.HEADER_TXT=mat.HEADER_TXT;
		data.ITEM_TEXT=mat.ITEM_TEXT;
		data.URGENT_FLAG=mat.URGENT_FLAG;
		data.DANGER_FLAG=mat.DANGER_FLAG;
		data.GOOD_DATES=mat.GOOD_DATES;
		data.MIN_GOOD_DATES = mat.MIN_GOOD_DATES;
		data.MATNR=mat.MATNR;
		data.FULL_BOX_QTY = mat.FULL_BOX_QTY;
		data.STOCK_L = mat.STOCK_L;
		data.STOCK_QTY = mat.STOCK_QTY;
		data.STOCK_ALL = mat.STOCK_ALL;
	}
	$("#dataGrid").jqGrid("addRowData", rowid, data, "last");

	var rows=$("#dataGrid").jqGrid('getRowData');
	$.each(rows,function(i,row){

		if (row.URGENT_FLAG=='X') {//过滤条件
			$("#dataGrid").jqGrid('setRowData', row.ID, '', {'color':'red'});
		}
		if(row.VENDOR_FLAG!='X'){
			$("#dataGrid").jqGrid('setCell', row.ID, 'VENDOR_MANAGER', '', 'not-editable-cell');
			$("#dataGrid").jqGrid('setCell', row.ID, 'SHORT_NAME', '', 'not-editable-cell');
			$("#dataGrid").jqGrid('setLabel','VENDOR_MANAGER','采购');
			$("#dataGrid").jqGrid('setLabel','SHORT_NAME','供应商简称');
		}else{
			$("#dataGrid").jqGrid('setCell', row.ID, 'VENDOR_MANAGER', '', 'editable-cell');
			$("#dataGrid").jqGrid('setCell', row.ID, 'SHORT_NAME', '', 'editable-cell');
			$("#dataGrid").jqGrid('setLabel','<span style="color:red">*</span><span style="color:blue">采购</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'VENDOR_MANAGER\')" title="向下填充" style="color:green" aria-hidden="true"></i>','采购');
			$("#dataGrid").jqGrid('setLabel','<span style="color:red">*</span><span style="color:blue">供应商简称</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'SHORT_NAME\')" title="向下填充" style="color:green" aria-hidden="true"></i>','供应商简称');
		}
	});
	//alert(rowid)
}

function delMat(id){
	//console.info(id)
	$("#dataGrid").delRowData(id)
}

/**
 * 展示危化品保质期数据，审核保质期更新行项目保质期数据
 * @returns
 */
function showDangerMat(rows){
	darows=[];
	$("#dangerMat tbody").html("");
	var selectedRows=[];
	$.each(rows,function(i,rowid){
		var row=$("#dataGrid").jqGrid('getRowData',rowid);
		if(vm.receipt_type=='01' || vm.receipt_type=='78'){
			$.each(vm.skInfoList,function(i,sk){
				if(sk.ASNITM==row.ASNITM){
					row.PRODUCT_DATE=sk.PRDDT;
					row.F_BATCH=sk.BATCH;
					row.FULL_BOX_QTY=sk.FULL_BOX_QTY;
					row.VALID_DATE = sk.VALID_DATE;
					row.EFFECT_DATE = sk.VALID_DATE;
					return ;
				}
			});
		}

		selectedRows.push(row);

		if(row.DANGER_FLAG=='X'){
			darows.push(row);
		}
	})
	//危化品保质期确认
	if(darows.length>0){

		$.each(darows,function(i,row){
			//alert(row.ID);
			var tr=$("<tr />");
			$("<td />").html(row.WERKS).appendTo(tr);
			$("<td />").html(row.LIFNR).appendTo(tr);
			$("<td />").html(row.MATNR).appendTo(tr);
			$("<td />").html(row.MAKTX).appendTo(tr);
			$("<td />").html("<input id='prod_date_"+i+"' style='width:100%;border:0px;background-color: white;' value='"+row.PRODUCT_DATE +"' onclick=\"WdatePicker({onpicked:function(){changeProdDate("+i+")},dateFmt:'yyyy-MM-dd'})\">").appendTo(tr);

			$("<td />").html("<input readonly='readonly' id='good_dates_"+i+"'style='width:100%;border:0px;background-color: white;' value='"+row.GOOD_DATES +"' onchange='changeGoodDates("+i+")' >").appendTo(tr);

			$("<td />").html("<input readonly='readonly' id='min_good_dates_"+i+"'style='width:100%;border:0px;background-color: white;' value='"+row.MIN_GOOD_DATES +"' >").appendTo(tr);

			if(row.VALID_DATE ==null || row.VALID_DATE == undefined || row.VALID_DATE ==""){
				$("<td />").html("<input id='effc_date_"+i+"'  style='width:100%;border:0px;background-color: white;' value='"+(dateAdd(row.PRODUCT_DATE,row.GOOD_DATES)) +"' onclick='WdatePicker({onpicked:function(){changeEffcDate("+i+")},dateFmt:\"yyyy-MM-dd\"})' >").appendTo(tr);

				darows[i].EFFECT_DATE=dateAdd(row.PRODUCT_DATE,row.GOOD_DATES);
				$.each(selectedRows,function(j,s_row){
					if(s_row.ID==row.ID){
						s_row.EFFECT_DATE=dateAdd(row.PRODUCT_DATE,row.GOOD_DATES);
						return ;
					}
				})
			}else{
				$("<td />").html("<input id='effc_date_"+i+"'  style='width:100%;border:0px;background-color: white;' value='"+ row.VALID_DATE +"' onclick='WdatePicker({onpicked:function(){changeEffcDate("+i+")},dateFmt:\"yyyy-MM-dd\"})' >").appendTo(tr);
			}

			$("#dangerMat tbody").append(tr);

		})

		layer.open({
			type: 1,
			title:['危化品保质期核对','font-size:18px'],
			closeBtn: 1,
			btn:['确认','关闭'],
			offset:['100px',''],
			area: '950px',
			skin: 'layui-bg-green', //没有背景色
			shadeClose: false,
			content: '<div id="layer_content">' +$('#dangerMat').html()+'</div>',//div 必须加上，否则无法改变layer中元素的值
			btn1:function(index){//危化品确认生成危化品批次，并生成其他选中行项目的批次信息，调用后台收货服务
				/*$.each(selectedRows,function(i,row){
                    $.each(darows,function(j,darow){
                        if(darow.ASNITM==row.ASNITM&&vm.receipt_type=='01'){
                            selectedRows[i]=darow;
                            return;
                        }
                    })
                })*/
				var canCommit = "";
				$.each(darows,function(j,darow){
					var minEffectD = dateAdd(new Date(),darow.MIN_GOOD_DATES);
					var minEffectDate = new Date(minEffectD.replace(/\-/g, "\/"));
					var effectDate = new Date(darow.EFFECT_DATE.replace(/\-/g, "\/"));
					if(minEffectDate > effectDate){
						//不能小于 最小剩余保质时长
						canCommit += darow.MATNR+",";
					}
				})
				//console.info(JSON.stringify(selectedRows))
				if(canCommit !=""){
					js.showErrorMessage("物料："+canCommit+"有效期减去当天时间不能小于最小剩余保质时长！");
				}else{
					layer.close(index);
					boundInConfirm(selectedRows);
				}

			},
			btn2:function(index){
				layer.close(index)
			}
		});

	}else{
		boundInConfirm(selectedRows);
	}


}

function getSKInfo(){
	var list=[];
	$.ajax({
		url:baseUrl+'in/wmsinreceipt/listSKInfo',
		type:'post',
		async:false,
		dataType:'json',
		data:{
			ASNNO:vm.ASNNO,
			BUSINESS_NAME:vm.receipt_type
		},
		success:function(resp){
			//alert(resp.msg)
			if(resp.msg=='success'){
				list=resp.data;
			}
		}
	})
	return list;
}

//cdate增加(减少)n天
function dateAdd(cdate,n){
	var d = new Date(cdate);
	d.setDate(d.getDate() + Number(n));
	var m = d.getMonth() + 1;
	if (m < 10) {
		m = "0" + m;
	}
	var day = d.getDate();
	if (d.getDate() < 10) {
		day = "0" + day;
	}
	return d.getFullYear() + '-' + m + '-' + day;
}

function changeProdDate(e){
	var prod_date=$dp.cal.getDateStr("yyyy-MM-dd");
	var good_dates=$("#layer_content").find("#good_dates_"+e).val();
	$("#layer_content").find("#effc_date_"+e).val(dateAdd(prod_date,good_dates));
	darows[e].PRODUCT_DATE=prod_date;
	darows[e].EFFECT_DATE=dateAdd(prod_date,good_dates);
	darows[e].GOOD_DATES=good_dates;
}
function changeEffcDate(e){
	var good_dates=$("#layer_content").find("#good_dates_"+e).val();
	var effc_date=$dp.cal.getDateStr("yyyy-MM-dd");
	$("#layer_content").find("#prod_date_"+e).val(dateAdd(effc_date,0-good_dates));
	darows[e].PRODUCT_DATE=dateAdd(effc_date,0-good_dates);
	darows[e].EFFECT_DATE=effc_date;
	darows[e].GOOD_DATES=good_dates;
}
function changeGoodDates(e){
	var prod_date=$("#layer_content").find("#prod_date_"+e).val();
	var good_dates=$("#layer_content").find("#good_dates_"+e).val();
	$("#layer_content").find("#effc_date_"+e).val(dateAdd(prod_date,good_dates));
	darows[e].PRODUCT_DATE=prod_date;
	darows[e].EFFECT_DATE=dateAdd(prod_date,good_dates);
	darows[e].GOOD_DATES=good_dates;
}

function setWH(){
	var rows=$("#dataGrid").jqGrid('getRowData');
	$.each(rows,function(i,row){
		console.info("rowId:"+row.ID)
		$("#dataGrid").jqGrid('setCell', row.ID, "WH_NUMBER", vm.WH_NUMBER,'editable-cell');//仓库号默认为页面仓库下拉列表的值
	})
}

function setWerks(){
	var rows=$("#dataGrid").jqGrid('getRowData');
	$.each(rows,function(i,row){
		$("#dataGrid").jqGrid('setCell', Number(row.ID), "WERKS", vm.WERKS,'editable-cell');//收货工厂默认为页面工厂下拉列表的值
	})
}


function boundInConfirm(selectedRows){
	js.loading("处理中...");
	$("#btnBoundIn").html("处理中...");
	$("#btnBoundIn").attr("disabled","disabled");
	if(selectedRows.length<=0){
		$("#btnBoundIn").html("确认");
		$("#btnBoundIn").removeAttr("disabled");
		js.closeLoading();
		return false;
	}
	/**
	 * SCM收料校验送货单包规和WMS系统维护的包规是否一致
	 */
	if(vm.receipt_type=='01'){
		var FULL_BOX_ERROR = "";
		$.each(selectedRows,function(i,row){
			FULL_BOX_ERROR += row.FULL_BOX_ERROR;
		});
		console.info(FULL_BOX_ERROR);
		if(FULL_BOX_ERROR.length >0){
			if(!window.confirm("物料号："+FULL_BOX_ERROR+"实物装箱数量和WMS系统供应商维护的最小包装不一致，确定收货？")){
				$("#btnBoundIn").html("确认");
				$("#btnBoundIn").removeAttr("disabled");
				js.closeLoading();
				return false;
			}
		}
	}

	if(vm.receipt_type=='01' || vm.receipt_type=='78'){
		//SCM 云平台送货单 包装箱信息不能为空
		if( vm.skInfoList.length <=0 ){
			$("#btnBoundIn").html("确认");
			$("#btnBoundIn").removeAttr("disabled");
			js.closeLoading();
			js.showErrorMessage("未获取到包装箱信息，无法收货！");
			return false;
		}
	}

	/**
	 * 无PO收料不允许料号重复
	 */
	var confirm_flag=true;
	if(vm.receipt_type=='05'){
		var MATNR_L= [];
		$.each(selectedRows,function(i,row){
			var BEDNR = row.BEDNR||""; //需求跟踪号
			var PRODUCT_DATE = row.PRODUCT_DATE||"";
			var MATNR = row.MATNR||"";
			if(BEDNR.trim() ==""){
				confirm_flag=false;
				js.showErrorMessage("抱歉，物料号："+MATNR+"未维护需求跟踪号！");
				$("#btnBoundIn").html("确认");
				$("#btnBoundIn").removeAttr("disabled");
				js.closeLoading();
				return false;
			}
			if(PRODUCT_DATE.trim() ==""){
				confirm_flag=false;
				js.showErrorMessage("抱歉，物料号："+MATNR+"未维护生产日期！");
				$("#btnBoundIn").html("确认");
				$("#btnBoundIn").removeAttr("disabled");
				js.closeLoading();
				return false;
			}
			if(MATNR_L.indexOf(MATNR)>=0&&MATNR!=""){
				confirm_flag=false;
				js.showErrorMessage("抱歉，料号："+MATNR+"出现重复，不能收货！");
				$("#btnBoundIn").html("确认");
				$("#btnBoundIn").removeAttr("disabled");
				js.closeLoading();
				return false;
			}
			MATNR_L.push(MATNR);;
		})
	}

	/**
	 * 303调拨收货，校验启用了条码的工厂 条码数量是否和收货数量一致
	 */
	if(vm.receipt_type=='06'){
		$.each(selectedRows,function(i,row){
			var LABEL_QTY = row.LABEL_QTY||0;
			if(row.BARCODE_FLAG=='X' && row.LABEL_NO != undefined && row.LABEL_NO !=''){
				confirm_flag=false;
				if(Number(LABEL_QTY) != Number(row.RECEIPT_QTY)){
					js.showErrorMessage("抱歉，仓库启用了条码配置，料号："+row.MATNR+"("+row.MATDOC_ITM+")存在关联的标签，但是可收货数量不等于所有状态为已创建的标签!");
					$("#btnBoundIn").html("确认");
					$("#btnBoundIn").removeAttr("disabled");
					js.closeLoading();
					return false;
				}

			}
		})
	}

	if(!confirm_flag){
		$("#btnBoundIn").html("确认");
		$("#btnBoundIn").removeAttr("disabled");
		js.closeLoading();
		return false;
	}

	//解决确认时后台获取的工厂异常问题
	vm.WERKS = selectedRows[0].WERKS;

	/**
	 * 收货确认后台逻辑（质检逻辑，SAP过账逻辑，输出标签列表）
	 */
	debugger
	return;
	$.ajax({
		url:baseUrl+"in/wmsinreceipt/boundIn",
		type:"post",
		async:true,
		dataType:"json",
		data:{
			matList:JSON.stringify(selectedRows),
			allDataList:JSON.stringify($("#dataGrid").jqGrid('getRowData')),
			skList:JSON.stringify(vm.skInfoList),
			WERKS:vm.WERKS,
			BUSINESS_NAME:vm.receipt_type,
			ASNNO:vm.ASNNO,
			PZ_DATE:$("#PZ_DATE").val(),
			JZ_DATE:$("#JZ_DATE").val(),
		},
		success:function(response){
			if(response.code==500){
				js.showErrorMessage(response.msg);
				$("#btnBoundIn").html("确认");
				$("#btnBoundIn").removeAttr("disabled");
				js.closeLoading();

				return false;
			}
			$("#btnBoundIn").html("确认");
			$("#btnBoundIn").removeAttr("disabled");
			js.closeLoading();

			$('#dataGrid').jqGrid("clearGridData");
			//showGrid();
			$("#dataGrid").jqGrid('setGridParam',{datatype:'local'}).trigger('reloadGrid');

			$("#resultMsg").html(response.msg);
			$("#labelList").val(JSON.stringify(response.lableList));
			$("#labelListA4").val(JSON.stringify(response.lableList));
			$("#inspectionList").val(JSON.stringify(response.inspectionList))
			layer.open({
				type : 1,
				offset : '50px',
				skin : 'layui-layer-molv',
				title : "收货成功",
				area : [ '500px', '250px' ],
				shade : 0,
				shadeClose : false,
				content : jQuery("#resultLayer"),
				btn : [ '确定'],
				btn1 : function(index) {
					layer.close(index);
					//$("#btnCreat").removeAttr("disabled");
				}
			});

			//js.showMessage(response.msg+"&nbsp;&nbsp;<a href='#' onclick='showLablePrint("+response.lableList+")'>来料标签打印"+"</a>",null,null,1000*20);
		}
	})
}

function fillDown(e){
	$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
	var ids=$("#dataGrid").getGridParam("selarrrow");
	var rowid=lastrow;
	var rows=$("#dataGrid").jqGrid('getRowData');
	/*	if(ids!=null){
            rowid=Math.min.apply(null,ids);
        }*/
	var last_row=$("#dataGrid").jqGrid('getRowData',rowid);
	var cellvalue=last_row[lastcellname];

	if(lastcellname==e){
		$.each(rows,function(i,row){
			console.info(Number(row.ID)+"/"+rowid+"/"+cellvalue)
			if(Number(row.ID)>=Number(rowid)&&cellvalue){
				$("#dataGrid").jqGrid('setCell',Number(row.ID), e, cellvalue,'editable-cell');
			}
		})
	}
}

function getLgortList(WERKS,PO_NO,PO_ITEM_NO,SOBKZ){
	var list=[];
	$.ajax({
		url:baseUrl+'common/getLoList',
		type:'post',
		async:false,
		dataType:'json',
		data:{
			WERKS:WERKS,
			PO_NO:PO_NO,
			PO_ITEM_NO:PO_ITEM_NO,
			SOBKZ:SOBKZ
		},
		success:function(resp){
			if(resp.msg=='success'){
				list=resp.data;
			}
		}
	})
	return list;
}

function setBEDNR(){
	var rows=$("#dataGrid").jqGrid('getRowData');
	$.each(rows,function(i,row){
		$("#dataGrid").jqGrid('setCell', row.ID, "BEDNR", $("#BEDNR").val(),'editable-cell');//需求跟踪号
	})
}

function checkRequiredCell(cm,row){
	var flag=true;
	$.each(cm,function(k,o){
		if(o.editrules&&o.editable){
			//console.info($("#dataGrid").jqGrid('getColProp',o.name))
			if(o.editrules.required&&(row[o.name]==undefined||row[o.name].trim().length==0)){
				flag=false;
				js.showErrorMessage("请填写"+o.display)
				return false;
			}
		}
	})

	return flag;
}

function getVendorNoFuzzy(event){
	vm.LIFNR_WPO = {};
	/*	var rows=$("#dataGrid").jqGrid('getRowData');
        $.each(rows,function(i,row){
            $("#dataGrid").jqGrid('setCell',Number(row.ID), 'LIKTX', '&nbsp;','not-editable-cell');
            $("#dataGrid").jqGrid('setCell',Number(row.ID), 'LIFNR',  '&nbsp;','not-editable-cell');
            $("#dataGrid").jqGrid('setCell',Number(row.ID), 'SHORT_NAME',  '&nbsp;','not-editable-cell');
        })*/
	getVendorNoSelect(event.target,null,function(obj){
			$(event.target).data("LIKTX",obj.NAME1);
			$(event.target).data("LIFNR",obj.LIFNR);
			$(event.target).data("SHORT_NAME",obj.SHORT_NAME);
			$(event.target).data("VENDOR_MANAGER",obj.VENDOR_MANAGER);
			vm.LIFNR_WPO.LIFNR = obj.LIFNR;
			vm.LIFNR_WPO.LIKTX = obj.NAME1;
			vm.LIFNR_WPO.SHORT_NAME = obj.SHORT_NAME;
			vm.LIFNR_WPO.VENDOR_MANAGER = obj.VENDOR_MANAGER;
			var rows=$("#dataGrid").jqGrid('getRowData');
			$.each(rows,function(i,row){
				$("#dataGrid").jqGrid('setCell',Number(row.ID), 'LIKTX', obj.NAME1,'not-editable-cell');
				$("#dataGrid").jqGrid('setCell',Number(row.ID), 'LIFNR', obj.LIFNR,'not-editable-cell');
				$("#dataGrid").jqGrid('setCell',Number(row.ID), 'VENDOR_MANAGER', obj.VENDOR_MANAGER,'not-editable-cell');
				$("#dataGrid").jqGrid('setCell',Number(row.ID), 'SHORT_NAME', obj.SHORT_NAME,'not-editable-cell');
			})

		},vm.WERKS
	);
}

function getPlantSetting(WH_NUMBER){
	var list=[];
	$.ajax({
		url:baseUrl+'common/getPlantSetting',
		type:'post',
		async:false,
		dataType:'json',
		data:{
			WH_NUMBER:WH_NUMBER
		},
		success:function(resp){
			if(resp.msg=='success'){
				list=resp.data;
			}
		}
	})
	return list;
}

function getSobzkOption(){
	var options="";
	for(i=0;i<sobkz_list.length;i++){
		if(i != sobkz_list.length - 1) {
			options += sobkz_list[i].CODE + ":" +sobkz_list[i].CODE + ";";
		} else {
			options += sobkz_list[i].CODE + ":" + sobkz_list[i].CODE;
		}
	}
	return options;
}

function getMatInfo(ctmap){
	var list=[];
	$.ajax({
		url:baseUrl+'in/wmsinreceipt/getMatInfo',
		type:'post',
		async:false,
		dataType:'json',
		data:ctmap,
		success:function(resp){
			if(resp.msg=='success'){
				list=resp.matList;
			}
			if(resp.error_msg){
				js.showErrorMessage(resp.error_msg)
			}
		}
	})
	return list;
}

//用于点击其他地方保存正在编辑状态下的行
$('html').bind('click', function(e) {
	if (!isEmpty(lastcell)) {
		if($(e.target).closest('#dataGrid').length == 0) {
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			lastrow="";
			lastcell="";
		}
	}
});

function funFromjs(result) {
	var str_list = result.split(";");
	var inbound_no ="";
	str_list.forEach(str=>{
		if(str.split(":")[0]=='DN'){
		inbound_no=str.split(":")[1]
		return ;
	}
})
	vm.ASNNO=inbound_no;
	vm.query();
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

/*
SCM送货单收料，多个订单号查询
 */
$("#asnnoQuery").click(function(){
	showTable();
	datagridPaste();
	layer.open({
		type: 1,
		title:['批量输入订单号','font-size:18px'],
		btn:['确定'],
		offset:'t',
		area: ["500px", "400px"],
		skin: 'layui-bg-green', //没有背景色
		shadeClose: false,
		content : jQuery("#asnnoQeryLayer"),
		btn1:function(index){
			layer.close(index);
			getProductNo();
		},
	});
});

function showTable(){
	$("#dataGrid_product").dataGrid({
		datatype: "local",
		data: productdata,
		colModel: [
			{label: '<span style="color:red">*</span><span style="color:blue"><b>订单号</b></span>', name: 'AUFNR',index:"AUFNR", width: "200",align:"center",sortable:false,editable:true},
			{label: '删除', name: 'AUFNR',index:"AUFNR", width: "80",align:"center",sortable:false,editable:false,
				formatter:function(value, name, record){
					return '<i title="删除" aria-hidden="true" onclick="$(this).parent().parent().remove();delMoreTr();" class="fa fa-times"></i>';
				}
			},
			{label: '', name: '',index:"AUFNR", width: "80",align:"center",sortable:false,editable:false}
		],
		shrinkToFit: false,
		width:600,
		cellEdit:true,
		cellurl:'#',
		cellsubmit:'clientArray',
		showCheckbox:false
	});
}
function datagridPaste(){
	$('#dataGrid_product').bind('paste', function(e) {
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
				$("#newOperation_product").click();
			}
			var cells = tab.rows[startRow + i].cells.length; // 该行总列数
			for (var j = 0; j < arr[i].length && startCell + j < cells; j++) {
				var cell = tab.rows[startRow + i].cells[startCell + j];
				$(cell).find(':text').val(arr[i][j]); //找到cell下的input:text，设置value
				$('#dataGrid_product').jqGrid("saveCell", startRow + i, 1);
				$(cell).html(arr[i][j]);
			}
		}
	});
};
function delMoreTr(){
	var trs=$("#dataGrid_product").children("tbody").children("tr");
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
$("#newOperation_product").click(function (){
	var trMoreindex = $("#dataGrid_product").children("tbody").children("tr").length;
	trMoreindex++;
	productdata.push({ id: trMoreindex,AUFNR: ""});

	var addtr = '<tr id = "'+trMoreindex+'"  role="row" tabindex="-1" class="ui-widget-content jqgrow jqgrow ui-row-ltr '+((trMoreindex%2 == 0)?"ui-priority-secondary":"")+'" aria-selected = "false">'+
		'<td role="gridcell" class="ui-state-default jqgrid-rownum" style="text-align:center;" title="2" aria-describedby="dataGrid_rn">' + trMoreindex +
		'</td><td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_1_"></td>'+
		'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_1_"><i title="删除" aria-hidden="true" onclick="$(this).parent().parent().remove();delMoreTr();" class="delMoreTr fa fa-times"></i></td><td></td></tr>'
	$("#dataGrid_product tbody").append(addtr);
});
$("#newReset_product").click(function () {
	productdata = [{ id: "1",AUFNR: ""},{ id: "2",AUFNR: ""}];
	$("#dataGrid_product").jqGrid('GridUnload');
	showTable();
	datagridPaste();
});
function getProductNo(){
	var productNostr="";
	var trs=$("#dataGrid_product").children("tbody").children("tr");
	$.each(trs,function(index,tr){

		if(index>0&&($(tr).find("td").eq(1).text()!=" "&&$(tr).find("td").eq(1).text()!=""))
			productNostr += $(tr).find("td").eq(1).text() + ";";

	});

	$("#PO_NO").val(productNostr.substring(0,productNostr.length-1));


}
