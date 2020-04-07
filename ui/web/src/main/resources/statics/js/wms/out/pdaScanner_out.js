var vm = new Vue({
	el:'#vue-app',
	data:{
		werks:'',
		whNumber:'',
		warehourse:[],
		scanerTypes:[],//扫描枪出库类型集合-businessName
		type:'',//'41',扫描枪出库类型
		certificateDate:'',//凭证日期
		postingDate:'',//过账日期
		qty:0,//数量,
		labelNo:"",//标签号码
		lastrow:null,
		lastcell:null,
		lastcellname:null,
		sendLgort:'',//发出库位
		businessType:'',//'10',业务类型
		jsgc:'',//接收工厂
		reciveLgort:'',//接收库位
		zzkm:'',//总账科目
		jsf:'',//接收方
		cbzx:'',//成本中心
		nbdd:'',//内部订单
	},
	created:function(){
		this.onPlantChange();
		this.queryBusinessNames();
		this.initGrid();
		this.disableSort();
		//日期
		var date = new Date();
		var today = formatDate(date);
		this.certificateDate = today;
		this.postingDate = today;

	},
	watch:{
		werks:function(){
			this.onPlantChange();//加载仓库
			this.queryBusinessNames();//更新业务类型
		},
		type:function(){
			//businessName变化时更新businessType
			if(this.type === '41'){
				this.businessType = '10';
			}
			if(this.type==='42'){
				this.businessType = '10';
			}
			if(this.type==='43'){
				this.businessType = '13';
			}
			if(this.type==='44'){
				this.businessType = '14';
			}
			if(this.type==='45'){
				this.businessType = '15';
			}
			if(this.type==='46'){
				this.businessType = '00';
			}
			if(this.type==='47'){
				this.businessType = '00';
			}
			if(this.type==='48'){
				this.businessType = '00';
			}
			if(this.type==='49'){
				this.businessType = '02';
			}
			if(this.type==='50'){
				this.businessType = '06';
			}
			if(this.type==='51'){
				this.businessType = '16';
			}
			if(this.type==='52'){
				this.businessType = '00';
			}
			if(this.type==='53'){
				this.businessType = '17';
			}
			if(this.type==='54'){
				this.businessType = '00';
			}
			if(this.type==='64'){
				this.businessType = '02';
			}
			vm.queryBarcode();
		}
	},
	methods:{
		queryBarcode:function(){
			var data = {WERKS:this.werks,WH_NUMBER:this.whNumber,BUSINESS_NAME:this.type};
			$.ajax({
				url : baseUrl + "out/scanner/queryBarcodeCache",
				contentType:"application/json",
				type:"post",
				data:JSON.stringify(data),
				success : function(resp) {
					vm.items = resp.data;
					vm.refresh();
				}
			});
		},
		onPlantChange : function() {
			//初始化查询仓库
			var plantCode = $("#werks").val();
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
			this.queryBusinessNames();
		},
		initGrid:function(){
			$("#dataGrid").dataGrid(
				{
					datatype : "local",
					colNames : [
						"标签号",
						"批次",
						"料号",
						"物料描述",
						"数量",
						"单位",
						"物料器具标识"
					],
					colModel : [ {
						name : "LABEL_NO",
						index : "LABEL_NO",
						width:140,
						align:'center'
					}, {
						name : "BATCH",
						index : "BATCH",
						width:120,
						align:'center'
					}, {
						name : "MATNR",
						index : "MATNR",
						width:120,
						align:'center'
					}, {
						name : "MAKTX",
						index : "MAKTX",
						width:150,
						align:'center'
					},{
						name : "QTY",
						index : "QTY",
						width:100,
						align:'center'
					}, {
						name : "UNIT",
						index : "UNIT",
						width:100,
						align:'center'
					}, {
						name:"POSTVEHICLEID",
						index:"POSTVEHICLEID",
						width:100,
						align:"center",
					}
					],
					viewrecords : true,
					cellEdit : true,
					cellurl : '#',
					cellsubmit : 'clientArray',
					multiselect : true,
					autowidth:true,
					shrinkToFit:false,
					autoScroll: true,
					beforeSelectRow : function(rowid, e) {
						// 点击复选框才选中行
						var $myGrid = $(this),
							i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),
							cm = $myGrid.jqGrid('getGridParam', 'colModel');
						if (cm[i].name == 'cb') {
							$('#dataGrid').jqGrid('setSelection', rowid);
							//更新选择的行项目数量
							vm.qty = vm.getGridSelectedItems().length;
						}
						return (cm[i].name == 'cb');
					},
					beforeEditCell:function(rowid, cellname, v, iRow, iCol){
						//编辑之前先保存当前编辑的行号列号
						vm.lastrow=iRow;
						vm.lastcell=iCol;
						vm.lastcellname=cellname;
					}
				});
		},
		refresh:function(_items,append){
			var items = this.items;
			if(_items != undefined && _items != null)
				items = _items;
			if(append){
				//默认追加
				var oldList = vm.getGridSelectedItems(true);
				//过滤掉已经存在的记录
				items  = items.filter(function(val){
					for(var index in oldList){
						if(oldList[index].LABEL_NO === val.LABEL_NO){
							return false;
						}
					}
					return true;
				});

				var records = $('#dataGrid').jqGrid('getGridParam','records');
				//追加到后面 rowId 从records开始
				for (var i = 0; i < items.length; i++) {
					$("#dataGrid").dataGrid('addRowData', records+i, items[i]);
				}
			}else{
				//重新加载
				$("#dataGrid").dataGrid("clearGridData");
				for (var i = 0; i < items.length; i++) {
					$("#dataGrid").dataGrid('addRowData', i, items[i]);
				}
			}
		},
		queryBusinessNames:function(){
			$.ajax({
				url:baseUrl +"out/scanner/queryBusinessName",
				contentType:"application/json",
				type:"post",
				data:JSON.stringify({werks:$("#werks").val(),businessClass:'07'}),
				success:function(resp){
					vm.scanerTypes = resp.data;
				}
			})
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
		matUnion:function(){
			//料号汇总
			var items = this.getGridSelectedItems(true);
			//如果有选择的行项目，只汇总选择的行
			var selectedItems = this.getGridSelectedItems();
			if(selectedItems.length > 0){
				items = selectedItems;
			}
			var m = new Map();
			for(var index in items){
				var matnr = items[index].MATNR;
				var maktx = items[index].MAKTX;
				var qty = items[index].BOX_QTY;
				if(qty === undefined || qty === null || qty === ''){
					qty = 0;
				}
				var liktx = items[index].LIKTX;
				if(m.get(matnr) === undefined){
					m.set(matnr,{MATNR:matnr,MAKTX:maktx,TOTAOL_QTY:qty,LIKTX:liktx});
				}else{
					qty = parseFloat(m.get(matnr).TOTAOL_QTY) + parseFloat(qty);
					m.set(matnr,{MATNR:matnr,MAKTX:maktx,TOTAOL_QTY:qty,LIKTX:liktx});
				}
			}

			var matUnionList = Array.from(m.values());
			var width ="80%";
			var height = "50%";

			js.layer.open({
				type : 2,
				title : "料号汇总  <span style='background-color:orange;padding: 2px 16px;font-size: x-large;color:white;margin-left: 16px;'>"+matUnionList.length+"</span>",
				area : [ width, height],
				content : baseUrl+ "wms/out/scanner_out_mat_union.html",
				btn : [ "取消" ],
				success : function(layero, index) {
					var win = layero.find('iframe')[0].contentWindow;
					//初始化参数
					win.vm.init(matUnionList);
				},
				no : function(index, layero) {
				}
			});

		},
		openLgort:function(){
			//选择出库库位

			// 库位选择框
			js.layer.open({
				type : 2,
				title : "库位",
				area : [ "40%", "60%" ],
				content : baseUrl + "wms/out/stock_mutiselect.html",
				btn : [ "确定", "取消" ],
				success : function(layero, index_) {
					// 加载完成后，初始化
					//
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
					vm.sendLgort = lgortList;
					win.vm.close();
				},
				no : function(index, layero) {
				}
			});
		},
		handoverComfirm:function(){
			//验证交接模式
			var business_rows=[];
			var rows = [];
			if(vm.type =='43'){
				if(!vm.nbdd){
					js.showErrorMessage("内部订单号不能为空");
					return;
				}
			} 
			if(vm.type =='44'){
				if(!vm.cbzx){
					js.showErrorMessage("成本中心不能为空");
					return;
				}
			} 
			if(vm.type =='48'){
				if(!vm.reciveLgort){
					js.showErrorMessage("接收库位不能为空");
					return;
				}
			} 
			if(vm.type =='46'){
				if(!vm.jsgc){
					js.showErrorMessage("接收工厂不能为空");
					return;
				}
				if(!vm.reciveLgort){
					js.showErrorMessage("接收库位不能为空");
					return;
				}
			} 
			
			var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
			if(ids.length<1){
				js.alert("请选择数据！");
				return
			}
			for(var id in ids){
				var row = $("#dataGrid").jqGrid('getRowData',ids[id]);
				rows[id] = row;
				business_rows[id]=this.type;
			}
			
			$.ajax({
	        	  url:baseUrl + "config/handoverType/wmsCHandoverTypelist",
	        	  data:{
	        		  "WERKS":$("#werks").val(),
	        		  "WH_NUMBER":$("#whNumber").val(),
	        		  "BUSINESS_CODE_LIST":JSON.stringify(business_rows)
	        		  },
	        	  success:function(resp){
	        		  if(resp.handover_type_flag!=""){//存在交接类型不一致的情况
	        			  js.alert("存在多个业务交接模式不一致！");
	        		  }else{
	        			
	        		  if(resp.business_strName!=""){//存在 找不到的业务类型
	        			  js.alert("交接业务类型"+resp.business_strName+"没有配置交接权限！");
	        			  $(".btn").attr("disabled", false);
						  return false;
	        		  }else{
	        			  if(resp.result.length>0){
			        		  if(resp.result[0].HANDOVER_TYPE=='00'){//无交接过账
			        			  var user="";
			        			  var username=""
			        			  handoverSave(user,username);
			        		  }else{
			        			  commonHandover(validateUser,validateUser,resp.result[0].HANDOVER_TYPE);
			        		  }
			         	  }else{
			         		 js.alert("交接模式没有配置！");
			         		 $(".btn").attr("disabled", false);
							 return false;
			         	  }
	        		  }
	        	  }
        		  }
	          })
		},	
		remove:function(){
			var ids=$("#dataGrid").getGridParam("selarrrow");
			if(ids.length===0){
				alert('请勾选后再删除！');
				return;
			}
			$("#dataGrid").jqGrid("delGridRow",ids);
		},
	}
});
function handoverSave(user,username){
	var handover="";
	// 需交接过账的记录交接人员信息
	if(user!=""){
		handover=user+":"+username;
	}
	var items = vm.getGridSelectedItems(true);
	var selectedItems = vm.getGridSelectedItems();
	if(selectedItems.length > 0){
		items = selectedItems;
	}
	//设置公共字段，工厂，仓库，业务类型等
	var items = items.map(function(val){
		val.key = "pda";
		val.BOX_QTY = val.QTY;
		val.WERKS = $("#werks").val();
		val.WH_NUMBER = $("#whNumber").val();
		val.BUSINESS_NAME = vm.type;
		val.BUSINESS_TYPE = vm.businessType;
		val.POSTVEHICLEID = vm.POSTVEHICLEID;
		val.certificateDate = vm.certificateDate;
		val.postingDate = vm.postingDate;
		val.sendLgort = vm.sendLgort;
		val.jsgc = vm.jsgc;
		val.reciveLgort = vm.reciveLgort;
		val.zzkm = vm.zzkm;
		val.jsf = vm.jsf;
		val.cbzx = vm.cbzx;
		val.nbdd = vm.nbdd;
		return val;
	});
	
		$.ajax({
			url:baseUrl+"out/scanner/handoverComfirm",
			contentType:"application/json",
			type:"post",
			data:JSON.stringify(items),
			success:function(resp){
				if(resp.code === 0){
					js.showMessage(resp.msg);
					this.qty = 0;
					vm.queryBarcode();
					try {
						parent.layer.closeAll();
					} catch(e){
						layer.closeAll();
					}
					//win.vm.close();
				}else{
					vm.queryBarcode();
					js.showMessage(resp.msg);
					try {
						parent.layer.closeAll();
					} catch(e){
						layer.closeAll();
					}
					//parent.layer.closeAll();
				}
			}
		});
}

function validateUser(user,username){
	var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');	
	if(ids.length==0){
		js.alert("请选择要提交的数据!");
		return;
	}
	var rows = [];
	var business_rows=[];
	for(var id in ids){
		var row = $("#dataGrid").jqGrid('getRowData',ids[id]);
		rows[id] = row;
		business_rows[id]=this.type;
	}

	//验证交接人员信息
	if(user==""||username==""){
		js.alert("交接人员不能为空！");		
		return ;
	}
	
	$.ajax({
  	  url:baseUrl + "config/handover/wmsCHandoverlist",
  	  data:{
  		  "WERKS":$("#werks").val(),
  		  "WH_NUMBER":$("#whNumber").val(),
  		  "BUSINESS_CODE_LIST":JSON.stringify(business_rows),
  		  "STAFF_NUMBER":user
  		  },
  	  success:function(resp){
      	  if(resp.result.length>0){
      		handoverSave(user,username);
       	  }else{
       		 js.alert("交接人员没有对应权限！");       		 
			 return ;
       	  }
  	  }
    });		
}