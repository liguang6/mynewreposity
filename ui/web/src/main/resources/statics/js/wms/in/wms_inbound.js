var listdata = [];
var listwwdata=[];
var lastrow,lastcell;
var trindex=1;

var rows_ww=[];

$(function(){
	initGrid();
});
			
var vm = new Vue({
	el:"#vue",
	data:{
		werks:"",
		warehourse:[],
		qcResult:[],
		whNumber:"",//
		relatedareaname:[],//仓管员
		lgortlist:[],//目标库位
		receiptDateStart: '',
		receiptDateEnd: '',
		BARCODE_FLAG: '0',
	},
	watch:{
		werks: {
			handler:function(newVal,oldVal){
				  //工厂变化的时候，更新仓库号下拉框
		          var plantCode = newVal;
		          
		          if(plantCode === null || plantCode === '' || plantCode === undefined){
		        	  return;
		          }
		          this.$nextTick(function(){
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
			          });
			      	//查询目标库位
			      	$.ajax({
			          	url:baseUrl + "in/wmsinbound/lgortlist",
			          	data:{
			          		"DEL":'0',
			          		"WERKS": plantCode,
			          		"SOBKZ":"Z",
			          		"BAD_FLAG":$("#qcResult").val()=='01'?'0':'1'
			          		},
			          	success:function(resp){
			          		vm.lgortlist = resp.result;
			          		vm.lgort=vm.lgortlist[0].LGORT;
			          	}
			        });
		        	  
				  })

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
	},
	created:function(){
		initQueryCondition();
	},
	methods:{
		query:function(){
			queryList();
		},
		save:function(){
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			//从jqGrid获取选中的行数据
			var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
			if(ids.length==0){
				js.alert("请选择至少一条记录");
				return false;
			}
			var rows = [];
			for(var id in ids){
				var row = $("#dataGrid").jqGrid('getRowData',ids[id]);
				
				row.RN= $("#dataGrid").jqGrid('getGridParam','selrow');
				
				if(row.IN_QTY-row.INABLE_QTY>0){
					js.alert("进仓数量不能大于可进仓数量!");
					return;
				}
				if((row.IN_QTY-row.TRY_QTY)<0){
					js.alert("进仓数量不能小于试装数量!");
					return;
				}
				if(row.IN_QTY<0){
					js.alert("进仓数量不能小于0!");
					return;
				}
				if(row.LGORT=='00ZJ'){
					js.alert("00ZJ库位不是有效的进仓存储库位!");
					return;
				}
				if(row.TRY_QTY>0&&row.TRY_BIN_CODE==''){
					js.alert("试装数量的创建进仓单，试装储位不能为空!");
					return;
				}
				//alert(row.LGORT);
				rows[id] = row;
			}
			js.loading("处理中...");
			$("#saveOperation").html("处理中...");
			$("#saveOperation").attr("disabled","disabled");
			$.ajax({
				url:baseUrl + "in/wmsinbound/save",
				dataType : "json",
				type : "post",
				async:true,
				data : {
					"ARRLIST":JSON.stringify(rows),
					"ARRLIST_WW":JSON.stringify(rows_ww), //委外的数据,
					"BARCODE_FLAG": vm.BARCODE_FLAG,
					//查询条件，再查询一次
					"WERKS":$("#werks").val(),
					"WH_NUMBER":$("#whNumber").val(),
					"LGORT":$("#lgort").val(),
					"BATCH":$("#batch").val(),
					"RELATED_AREA_NAME":$("#whManager").val(),
					"LIFNR":$("#lifnr").val(),
					"MATNR":$("#matnr").val(),
					"RECEIPT_DATE_START":$("#receiptDateStart").val(),
					"RECEIPT_DATE_END":$("#receiptDateEnd").val(),
					"ASNNO":$("#asnNo").val(),
					"QC_RESULT":$("#qcResult").val()
					
				},
				async: false,
				success:function(resp){
					if(resp.code == '0'){
						$("#saveOperation").html("创建进仓单");	
						//$("#saveOperation").removeAttr("disabled");
						js.closeLoading();
						
						//js.showMessage("保存成功! 进仓单号  "+resp.inbountNo,null,null,1000*50);
						$("#inboundNoLayer").html(resp.inbountNo);
						$("#inboundNo").val(resp.inbountNo);
						$("#inboundNo3").val(resp.inbountNo);
						layer.open({
		            		type : 1,
		            		offset : '50px',
		            		skin : 'layui-layer-molv',
		            		title : "进仓单创建成功",
		            		area : [ '500px', '200px' ],
		            		shade : 0,
		            		shadeClose : false,
		            		content : jQuery("#resultLayer"),
		            		btn : [ '确定'],
		            		btn1 : function(index) {
		            			layer.close(index);
		            		}
		            	});
						//queryList();
						
					}else{
						js.showErrorMessage(resp.msg,1000*10);
						
						$("#saveOperation").html("创建进仓单");	
						$("#saveOperation").removeAttr("disabled");
						js.closeLoading();
					}
					//清空列表
					$("#dataGrid").jqGrid("clearGridData", true);
				}
			});
			/*if(rows_ww.length>0){
				save_ww_n();
			}*/
			
			
		},
		//表格行项目库位批量修改
		btnupdatelgort: function(){
			var rows=$("#dataGrid").jqGrid('getRowData');
			$.each(rows,function(i,row){	
				//console.info(row)
				if(row.LGORT=="00ZJ"){
					$("#dataGrid").jqGrid('setCell', i+1, "LGORT", $("#dLgort").val());//第二个参数代表行 id，第三个代表 列名称，第四个可以是值或者属性
				}
			});
		},
		getVendorNoFuzzy:function(){
			getVendorNoSelect("#lifnr",null,null,$("werks").val());
		},
		queryDetail:function(){//
			js.addTabPage(null, '<i class="fa fa-plus" style="font-size:12px"> 进仓单明细</i>',
					'wms/query/inboundDetailQuery.html?inboundNo='+$("#inboundNo").val()+'&werks='+$("#werks").val()+'&whNumber='+$("#whNumber").val()+'&inBoundType=00', 
							true, true);
			},
		printInbound:function(){
			$("#printButton").click();
		},
		printSmallInbound:function(){//小letter
			$("#printButton3").click();
		},
		newOperation_ww:function(){}
	}
});

function initGrid(){
	$("#dataGrid").dataGrid({
    	url: '',
    	datatype: "local",
		data: listdata,
        colModel: [			
			{ label: '批次', name: 'BATCH', index: 'BATCH', width: 120 }, 
			{ label: '物料号', name: 'MATNR', index: 'MATNR', width: 90 }, 	
			{ label: '物料描述', name: 'MAKTX', index: 'MAKTX', width: 260 }, 
			{ label: '需求跟踪号', name: 'BEDNR', index: 'BEDNR', width: 120 },
			{ label: '<span style="color:blue">库位</span>', name: 'LGORT', index: 'LGORT', width: 60,editable:true,edittype:'select' }, 	
			{ label: '储位', name: 'BIN_CODE', index: 'BIN_CODE', width: 60,hidden:true },
			{ label: '收货数量', name: 'RECEIPT_QTY', index: 'RECEIPT_QTY', width: 70 },
			{ label: '可进仓数量', name: 'INABLE_QTY', index: 'INABLE_QTY', width: 80 },
			{ label: '<span style="color:blue">进仓数量</span>', name: 'IN_QTY', index: 'IN_QTY', width: 70,editable:true }, 	
			{ label: '单位', name: 'UNIT', index: 'UNIT', width: 60 },
			{ label: '试装数量', name: 'TRY_QTY', index: 'TRY_QTY', width: 70 }, 	
			{ label: '试装储位', name: 'TRY_BIN_CODE', index: 'TRY_BIN_CODE', width: 80 }, 	
			{ label: '库存类型', name: 'SOBKZ', index: 'SOBKZ', width: 70 }, 	
			{ label: '供应商代码', name: 'LIFNR', index: 'LIFNR', width: 80 }, 			
			{ label: '供应商名称', name: 'LIKTX', index: 'LIKTX', width: 180 },
			{ label: '收料方存放区', name: 'GR_AREA', index: 'GR_AREA', width: 100 },
			 
			{ label: '立库储位', name: 'LIKU_BIN_CODE', index: 'LIKU_BIN_CODE', width: 80,hidden:true },
			
			{ label: '仓管员', name: 'WH_MANAGER', index: 'WH_MANAGER', width: 80}, 
			{ label: '收货日期', name: 'RECEIPT_DATE', index: 'RECEIPT_DATE', width: 90}, 
			{ label: '', name: 'BUSINESS_NAME', index: 'BUSINESS_NAME', width: 80,hidden:true},
			{ label: '', name: 'BUSINESS_TYPE', index: 'BUSINESS_TYPE', width: 80,hidden:true},
			{ label: '', name: 'F_WERKS', index: 'F_WERKS', width: 80,hidden:true},
			{ label: '', name: 'F_WH_NUMBER', index: 'F_WH_NUMBER', width: 80,hidden:true},
			{ label: '', name: 'F_LGORT', index: 'F_LGORT', width: 80,hidden:true},
			{ label: '', name: 'F_BATCH', index: 'F_BATCH', width: 80,hidden:true},
			{ label: '', name: 'WERKS', index: 'WERKS', width: 80,hidden:true},
			{ label: '', name: 'WH_NUMBER', index: 'WH_NUMBER', width: 80,hidden:true},
			{ label: '', name: 'F_UNIT', index: 'F_UNIT', width: 80,hidden:true},
			{ label: '', name: 'F_QTY', index: 'F_QTY', width: 80,hidden:true},
			{ label: '', name: 'REAL_QTY', index: 'REAL_QTY', width: 80,hidden:true},
			{ label: '', name: 'QTY_CANCEL', index: 'QTY_CANCEL', width: 80,hidden:true},
			{ label: '', name: 'FULL_BOX_QTY', index: 'FULL_BOX_QTY', width: 80,hidden:true},
			{ label: '', name: 'BOX_COUNT', index: 'BOX_COUNT', width: 80,hidden:true},
			{ label: '', name: 'RECEIPT_NO', index: 'RECEIPT_NO', width: 80,hidden:true},
			{ label: '', name: 'RECEIPT_ITEM_NO', index: 'RECEIPT_ITEM_NO', width: 80,hidden:true},
			{ label: '', name: 'IO_NO', index: 'IO_NO', width: 80,hidden:true},
			{ label: '', name: 'WBS', index: 'WBS', width: 80,hidden:true},
			{ label: '', name: 'SAKTO', index: 'SAKTO', width: 80,hidden:true},
			{ label: '', name: 'ANLN1', index: 'ANLN1', width: 80,hidden:true},
			{ label: '', name: 'MO_NO', index: 'MO_NO', width: 80,hidden:true},
			{ label: '', name: 'MO_ITEM_NO', index: 'MO_ITEM_NO', width: 80,hidden:true},
			{ label: '', name: 'PO_NO', index: 'PO_NO', width: 80,hidden:true},
			{ label: '', name: 'PO_ITEM_NO', index: 'PO_ITEM_NO', width: 80,hidden:true},
			{ label: '', name: 'PSTYP', index: 'PSTYP', width: 80,hidden:true},
			{ label: '', name: 'PRODUCT_DATE', index: 'PRODUCT_DATE', width: 80,hidden:true},
			{ label: '', name: 'QC_RESULT', index: 'QC_RESULT', width: 80,hidden:true},
			{ label: '', name: 'REF_SAP_MATDOC_NO', index: 'REF_SAP_MATDOC_NO', width: 80,hidden:true},
			{ label: '', name: 'REF_SAP_MATDOC_ITEM_NO', index: 'REF_SAP_MATDOC_ITEM_NO', width: 80,hidden:true},
			{ label: '', name: 'REF_SAP_MATDOC_YEAR', index: 'REF_SAP_MATDOC_YEAR', width: 80,hidden:true},
			{ label: '', name: '', index: '', width: 80,align:'center' ,formatter: function(val, obj, row, act){
				var actions = [];
				if(row.PSTYP == "3")actions.push('<a href="#" title="委外原料消耗" onclick=show("'+row.RECEIPT_NO+'","'+row.RECEIPT_ITEM_NO+'","'+row.IN_QTY+'","'+row.WERKS+'","'+row.WH_NUMBER+'")>委外原料消耗</a>');
				return actions.join('');
		      }
		    },
		    { label: '', name: 'ID', index: 'ID', width: 80,hidden:true},

        ],
        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
		},
        viewrecords: true,
        cellEdit:true,
        cellurl:'#',
        cellsubmit:'clientArray',
        autowidth:true,
        shrinkToFit:false,  
        autoScroll: true, 
        multiselect: true,
        ajaxSuccess:function(){
        },
        formatCell:function(rowid, cellname, value, iRow, iCol){
			console.info(cellname)
			if(cellname=='LGORT'){
				var rec = $('#dataGrid').jqGrid('getRowData', rowid);
	            $('#dataGrid').jqGrid('setColProp', 'LGORT', { editoptions: {value:getLotOption(rec.SOBKZ)}
	            });
			}	
			
		},
		gridComplete:function(data){
        },
        afterSaveCell:function(rowid, cellname, value, iRow, iCol){
			var row =  $("#dataGrid").jqGrid('getRowData', rowid);
			if(cellname=='IN_QTY'||cellname=='INABLE_QTY'){//进仓数量,可进仓数量
				
				var able_qty=row.IN_QTY-row.INABLE_QTY;//进仓数量大于可进仓数量，报错
				if(able_qty>0){
					js.alert("进仓数量不能大于可进仓数量!");
				}
				if((row.IN_QTY-row.TRY_QTY)<0){
					js.alert("进仓数量不能小于试装数量!");
				}
				if(row.IN_QTY<0){
					js.alert("进仓数量不能小于0!");
				}
				if(row.TRY_QTY>0&&row.TRY_BIN_CODE==''){
					
					js.alert("试装数量的创建进仓单，试装储位不能为空!");
					return;
				}
			}
			if(cellname=='LGORT'){//库位
				if(row.LGORT=='00ZJ'){
					js.alert("00ZJ库位不是有效的进仓存储库位!");
				}
			}
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
			}
    });
}

function queryList(){
	$.ajax({
		url:baseURL+"in/wmsinbound/list",
		dataType : "json",
		type : "post",
		data : {
			"WERKS":$("#werks").val(),
			"WH_NUMBER":$("#whNumber").val(),
			"LGORT":$("#lgort").val(),
			"BATCH":$("#batch").val(),
			"RELATED_AREA_NAME":$("#whManager").val(),
			"LIFNR":$("#lifnr").val(),
			"MATNR":$("#matnr").val(),
			"RECEIPT_DATE_START":$("#receiptDateStart").val(),
			"RECEIPT_DATE_END":$("#receiptDateEnd").val(),
			"ASNNO":$("#asnNo").val(),
			"QC_RESULT":$("#qcResult").val()
		},
		async: true,
		success: function (response) { 
			$("#dataGrid").jqGrid("clearGridData", true);
			listdata.length=0;
			
			if(response.code == "0"){
				listdata = response.result;
				
				if(listdata.length==0){
					js.showMessage("未找到符合条件的数据！ ",null,null,1000*50000);
					return;
				}
				
				$('#dataGrid').jqGrid('setGridParam',{
						data: listdata,
						cellEdit:true, 
						cellsubmit : 'clientArray', 
						beforeEditCell : function(rowid,cellname,value,iRow,iCol){ 
							lastrow = iRow; lastcell = iCol; 
						}, 
						onCellSelect : function(rowid,iCol,cellcontent,e){ 
							var rec = $("#grid-table").jqGrid('getRowData', rowid); 
							if (response.BARCODE_FLAG =="X") {
								//启用条码的工厂
								vm.BARCODE_FLAG = response.BARCODE_FLAG;
								
								$("#dataGrid").jqGrid('setCell', rowid, 'IN_QTY', '', 'not-editable-cell'); 
								$("#dataGrid").jqGrid('setCell', rowid, 'INABLE_QTY', '', 'not-editable-cell');
							}else{
								//未启用条码的工厂
								vm.BARCODE_FLAG = response.BARCODE_FLAG;
								$("#dataGrid").jqGrid('setCell', rowid, 'IN_QTY', '', 'editable-cell');
								$("#dataGrid").jqGrid('setCell', rowid, 'INABLE_QTY', '', 'editable-cell');
							}
						}
					}).trigger( 'reloadGrid' );
			}else{
				alert(response.msg)
			}
			
		}
	});
	
}

function getLotOption(SOBKZ){
	var options="";
	$.ajax({
		url:baseUrl+'in/wmsinbound/lgortlist',
		type:'post',
		async:false,
		dataType:'json',
		data:{
			"DEL":'0',
    		"WERKS":$("#werks").val(),
    		"SOBKZ":SOBKZ,
    		"BAD_FLAG":$("#qcResult").val()=='01'?'0':'X'
		},
		success:function(resp){
			//alert(resp.msg)
			if(resp.msg=='success'){		
				for(i=0;i<resp.result.length;i++){
	 				if(i != resp.result.length - 1) {
	 					options += resp.result[i].LGORT + ":" +resp.result[i].LGORT + ";";
	 				} else {
	 					options += resp.result[i].LGORT + ":" + resp.result[i].LGORT;
	 				}
				}
				//alert(options)
				console.info(options)
				
			}
		}		
	})
	return options;
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
/**
 * 显示委外清单
 * @param 
 */

function show(receipt_no,receipt_item_no,in_qty,werks,wh_number){
	
	$("#werks_ww").val(werks);
	
	$("#inboundWeiWaiTable tbody").html("");
	
	//查询抬头并打开窗口
	queryReceiptInfo(receipt_no,receipt_item_no,in_qty);
	//查询列表
	queryPoCompoment(receipt_no,receipt_item_no,in_qty);
	
	
}

function queryReceiptInfo(receipt_no,receipt_item_no,in_qty){
	
	$.ajax({
			url : baseUrl + "in/wmsinbound/Receiptlist",
			dataType : "json",
			type : "post",
			async:false,
			data : {
				"RECEIPT_NO":receipt_no,
		  		"RECEIPT_ITEM_NO":receipt_item_no
			},
			success:function(resp){
			   if(resp.code==0){
				  
					 $("#poNo_ww").text(resp.result.PO_NO);
					 $("#poItemNO_ww").text(resp.result.PO_ITEM_NO);
					 $("#lifnr_ww").text(resp.result.LIFNR);
					 $("#liktx_ww").text(resp.result.LIKTX);
					 $("#matnr_ww").text(resp.result.MATNR);
					 $("#maktx_ww").text(resp.result.MAKTX);
					 /*if(resp.result.IN_QTY!=null){
						 $("#inQty_ww").text(resp.result.IN_QTY);
					 }else{
						 $("#inQty_ww").text(0);
					 }*/
					 
					 $("#inQty_ww").text(in_qty);
					 
					 
					 openWeiwaiWindow(receipt_no,receipt_item_no,resp.result.PO_NO,resp.result.PO_ITEM_NO);
			   }
			}
		});
}

function openWeiwaiWindow(receipt_no,receipt_item_no,poNo,poItemNo){
	
	layer.open({
		  type: 1,
		  title:['委外原料消耗','font-size:18px'],
		  closeBtn: 0,
		  btn:['确定','取消'],
		  offset:'t',
		  area: ["750px", "500px"],
		  skin: 'layui-bg-green', //没有背景色
		  shadeClose: false,
		  content: '<div id="layer_content">' +$('#inboundWeiwai').html()+'</div>',
		  btn1:function(index){
			  confirm_ww_n(receipt_no,receipt_item_no,poNo,poItemNo);
		  },
		  btn2:function(index){
			  $("#inboundWeiWaiTable tbody").html("");
			  layer.close(index);
		  }
		});
}
//查询列表
function queryPoCompoment(receipt_no,receipt_item_no,in_qty){
	$.ajax({
		url:baseURL+"in/wmsinbound/sapComponentlist",
		dataType : "json",
		type : "post",
		async:false,
		data : {
			"RECEIPT_NO":receipt_no,
			"RECEIPT_ITEM_NO":receipt_item_no,
			"IN_QTY":in_qty
		},
		//async: true,
		success: function (response) { 
			
			if(response.code == "0"){
				listwwdata=response.result;
				
				var listdata_ww = response.result;
				console.info(JSON.stringify(listdata_ww))
				$("#inboundWeiWaiTable tbody").html("");
				$.each(listdata_ww,function(i,row){
					 var tr=$("<tr/>");
					 //$('<td role="gridcell" class="ui-state-default jqgrid-rownum" style="text-align:center;width: 30px;" title="1" aria-describedby="dataGrid_rn">' + trindex + '</td>').appendTo(tr)+
						
					 //$('<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_cb"><input role="checkbox" type="checkbox" class="cbox noselect2"></td>').appendTo(tr);
					 //$("<td key='true'/>").html(trindex).appendTo(tr);
					 $("<td><a href='#' onClick='delRoWw(this)' title='删除' data-confirm='确认要删除该行数据吗？'><i class='fa fa-times' style='color:red'></i></a></td>").appendTo(tr);
					 $("<td/>").html(row.MATN2).appendTo(tr);
					 $("<td/>").html(row.MAKTX2).appendTo(tr);
					 $("<td><input style='width: 99.9%;'type='text'></>").appendTo(tr);
					 $("<td><input style='width: 99.9%;'type='text' value='"+row.MENG2+"'></>").appendTo(tr);
					 $("<td />").html(row.MEIN2).appendTo(tr);
					 $("<td style='display:none'/>").html(row.WERKS).appendTo(tr);

					 $(tr).appendTo($("#inboundWeiWaiTable tbody"));
				})	
			}else{
				alert(response.msg)
			}
		}
	});
}

function newoper_n(){
	var werks=$("#werks_ww").val();
	var tr=$("<tr />");
	/*$("<td />").html(trindex).appendTo(tr);*/
	$("<td />").html('<a href="#" onClick="delRoWw(this)" title="删除" data-confirm="确认要删除该行数据吗？"><i class="fa fa-times" style="color:red"></i></a>').appendTo(tr);
	$("<td />").html('<input rowid="'+trindex+'" name="MATN2" id="'+trindex+'_MATN2" type="text" onblur="queryMads(this)">').appendTo(tr);
	$("<td />").html('').appendTo(tr);
	$("<td />").html('<input rowid="'+trindex+'" name="BATCH" id="'+trindex+'_BATCH" type="text"></td>').appendTo(tr);
	$("<td />").html('<input  style="width: 99.9%;" rowid="'+trindex+'" name="MENG2" id="'+trindex+'_MENG2" type="text">').appendTo(tr);
	$("<td />").html('').appendTo(tr);
	$("<td style='display:none'/>").html(werks).appendTo(tr);
	
	$("#inboundWeiWaiTable tbody").append(tr);
	trindex++;
}

function btn_delete_n(){
	var trs=$("#inboundWeiWaiTable tbody").children("tr");
	var reIndex = 1;
	$.each(trs,function(index,tr){
		var td = $(tr).find("td").eq(0);
		var td_check = $(tr).find("td").eq(1).find("input");
		if(index > 0){
			var ischecked=$(td_check).is(":checked");
			if(ischecked){
				tr.remove();
			}else{
				td.text(reIndex);
				$(tr).attr('id',reIndex);
				$(td_check).attr('name','jqg_dataGrid_' + reIndex);
				$(td_check).attr('id','jqg_dataGrid_' + reIndex);
				reIndex++;
			}
		}
	});
	trindex = reIndex;

}


function queryMads(e){
	if($(e).val().trim()==""){
		js.alert("物料号不能为空！");
		return false;
	}
	var werks=$("#werks_ww").val();
	$.ajax({
        url: baseURL + "in/wmsinternalbound/querymaterial",
        dataType : "json",
		type : "post",
        data: {
        	"werks":werks,
			"material":$(e).val().trim()
        },
        async:false,
        success: function(data){
        	if(data.code == 0){
        		//alert(data.mads+"    "+data.unit);
        		$(e).parent("td").next().html(data.mads);
        		$(e).parent("td").next().next().next().next().html(data.unit);
        		
        	}
        }
	});
	
}

//确认委外
function confirm_ww_n(receipt_no,receipt_item_no,poNo,poItemNo){
	//弹出层的table获取必须要用这种方式，否则是两个tbody
	var table=$("#layer_content").find("table[id='inboundWeiWaiTable']")
	var trs=$(table).find("tbody").find("tr");
	//不能用这种方式，否则是两个tbody
	//var trs=$("#inboundWeiWaiTable tbody").find("tr");
	
	rows_ww=[];
	
	for(var m=0;m<trs.length;m++){
		var MATN2 ="";
		
		if($(trs[m]).find("td").eq(1).find("input").val()!=undefined){
			MATN2 = $(trs[m]).find("td").eq(1).find("input").val();
		}else{
			MATN2 = $(trs[m]).find("td").eq(1).html();
		}
		
		var MAKTX2 ="";
		if($(trs[m]).find("td").eq(2).find("input").val()!=undefined){
			MAKTX2 = $(trs[m]).find("td").eq(2).find("input").val();
		}else{
			MAKTX2 = $(trs[m]).find("td").eq(2).html();
		}
		
		var BATCH ="";
		if($(trs[m]).find("td").eq(3).find("input").val()!=undefined){
			BATCH = $(trs[m]).find("td").eq(3).find("input").val();
		}else{
			BATCH = $(trs[m]).find("td").eq(3).html();
		}
		
		var MENG2 ="";
		if($(trs[m]).find("td").eq(4).find("input").val()!=undefined){
			MENG2 = $(trs[m]).find("td").eq(4).find("input").val();
		}else{
			MENG2 = $(trs[m]).find("td").eq(4).html();
		}
		
		var MEIN2 ="";
		if($(trs[m]).find("td").eq(5).find("input").val()!=undefined){
			MEIN2 = $(trs[m]).find("td").eq(5).find("input").val();
		}else{
			MEIN2 = $(trs[m]).find("td").eq(5).html();
		}
		
		var WERKS ="";
		if($(trs[m]).find("td").eq(6).find("input").val()!=undefined){
			WERKS = $(trs[m]).find("td").eq(6).find("input").val();
		}else{
			WERKS = $(trs[m]).find("td").eq(6).html();
		}
		
		rows_ww[m]="{"+"'MATN2':'"+MATN2+"',"+"'MAKTX2':'"+MAKTX2+"',"+"'BATCH':'"+BATCH+
		"',"+"'MENG2':'"+MENG2+"',"+"'MEIN2':'"+MEIN2+"',"+"'WERKS':'"+WERKS+
		"',"+"'RECEIPT_NO':'"+receipt_no+"',"+"'RECEIPT_ITEM_NO':'"+receipt_item_no+
		"',"+"'EBELN':'"+poNo+"',"+"'EBELP':'"+poItemNo+
			"'}";
		
	}
	for(var m=0;m<rows_ww.length;m++){
		console.info(rows_ww[m]);
	}
	
}


function delRoWw(ele){
	var tr=$(ele).parent("td").parent("tr")
	$(tr).remove()
}

function selectAll(selectStatus){//传入参数（全选框的选中状态）
	  //根据name属性获取到单选框的input，使用each方法循环设置所有单选框的选中状态
	  if(selectStatus){
	    $("input[name='check']").each(function(i,n){
	      n.checked = true;
	    });
	  }else{
	    $("input[name='check']").each(function(i,n){
	      n.checked = false;
	    });
	  }
	  
	}  

$("#reset").click(function(){
	  reset();
});

function reset(){
	
	$("#dataGrid").jqGrid("clearGridData", true);
	
}

function getUrlKey(name){
	return decodeURIComponent((new RegExp('[?|&]'+name+'='+'([^&;]+?)(&|#|;|$)').exec(location.href)||[,""])[1].replace(/\+/g,'%20'))||null;
}

function initQueryCondition(){
	$(function(){
		
		var WERKS=getUrlKey("WERKS");
		var WH_NUMBER=getUrlKey("WH_NUMBER");
		var MATNR=getUrlKey("MATNR");
		var BATCH=getUrlKey("BATCH");
		
		if(WERKS !=null && WERKS!=''){
			vm.werks = WERKS;
			$("#werks option[value='"+WERKS+"']").prop("selected",true);
		}else{
			vm.werks=$("#werks").find("option").first().val();
			//设置默认日期，当前日期往前一个月
			var now = new Date(); //当前日期
			var endDate = now.Format("yyyy-MM-dd hh:mm:ss");
			var startDate = getLastMonthYestdy();//获取退后一个月的日期
			
			vm.receiptDateStart = startDate;
			vm.receiptDateEnd = endDate;
		}
		if(WH_NUMBER !=null && WH_NUMBER!=''){
			vm.whNumber = WH_NUMBER;
			$("#whNumber option[value='"+WH_NUMBER+"']").prop("selected",true);
		}
		
		if(MATNR !=null && MATNR!=''){
			$("#matnr").val(MATNR);
		}
		if(BATCH !=null && BATCH!=''){
			$("#batch").val(BATCH);
			queryList();
		}
	});
}
