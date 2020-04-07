function initGridTable(){
	$("#dataGrid").dataGrid({
    	url: baseUrl + 'qc/wmsqcinspectionitem/list',
        datatype: "json",
        searchForm:$("#searchForm"),
        colModel: [
        	{ label:'',name:'labelNo',index:'labelNo',hidden:true},
        	{ label:'',name:'effectDate',index:'effectDate',hidden:true},
            { label: 'receiptItemNo', name: 'receiptItemNo', index: 'RECEIPT_ITEM_NO',hidden:true },
            { label: 'receiptNo', name: 'receiptNo', index: 'RECEIPT_NO',hidden:true },
			{ label: 'id', name: 'id', index: 'ID', width: 100, key: true,hidden:true },
			{ label: 'urgentFlag', name: 'urgentFlag', index: 'urgent_Flag', width: 100,hidden:true },
			{ label: '批次', name: 'batch', index: 'BATCH', width: 90 ,align: 'center'}, 
			{ label: '需求跟踪号', name: 'bednr', index: 'BEDNR', width: 120 ,align: 'center'}, 
			{ label: '物料号', name: 'matnr', index: 'MATNR', width: 100 ,align: 'center'}, 	
			{ label: '物料描述', name: 'maktx', index: 'MAKTX', width: 250 ,align: 'center'}, 
			{ label: '送检数量', name: 'inspectionQty', index: 'INSPECTION_QTY', width: 65 ,align: 'center'},
			{ label: '批量检验', name: 'qcResultCodeSel', index: '', width: 80,formatter:function(val, obj, row, act){	
				var selectHtml = "" + $("#statusOps").html();
				var status = $("#inspectionItemStatus").val();
				if(status == '01'){
					/*val = row.qcResultCode
					if (val === '01') {
						return "让步接收";
					}
					if (val === "02" || val === '11') {
						return "合格"
					}
					if (val === "03") {
						return "不合格"
					}
					if (val === "04") {
						return "挑选使用"
					}
					if (val === "05") {
						return "紧急放行"
					}
					if (val === "06") {
						return "特采"
					}
					if (val === "07") {
						return "退货"
					}
					if (val === "08") {
						return "实验中"
					}
					if (val === "09") {
						return "评审中"
					}
					if (val === "10") {
						return "精测中"
					}*/
					
					//UPDATE: 质检中的，全部显示质检中状态
					return "质检中";
				}
				
				if(row.effectDate !== undefined && row.effectDate !== null){
					var effectDate = new Date(row.effectDate);
					var currentDate = new Date(formatDate(new Date()))
					if(effectDate < currentDate){
						selectHtml = "" + $("#statusOpsReturn").html();
					}
				}
				
				if(row.qcResultCode != null && row.qcResultCode != '' && row.qcResultCode != undefined){
					//设置默认选项
					var index_  =  selectHtml.indexOf("value=\"" + row.qcResultCode);
					if(index_ === -1) return selectHtml;
					var prePart = selectHtml.substring(0,index_) + " selected=\"selected\" ";
					var endPart = selectHtml.substring(index_);
					selectHtml = prePart + endPart;
				}
				selectHtml = selectHtml.replace('$rowid',obj.rowId);
				return  selectHtml;
			},align: 'center'}, 
			{   label: '不合格原因', name: 'returnreason', index: 'returnreason', width: 150,align:"center",
				formatter:function(val,obj,row){
				        var rowId = obj.rowId;
				        if(val == null){
				        	val = "";
				        }
				    return "<span style='color: rgb(233, 104, 107);cursor:pointer;' onclick='vm.onStatusChange(null,"+rowId+")'>"+val+" </span>"
			    },
			    unformat : function(cellVal,options,rowObj){
				    return cellVal
			    },align: 'center'
			},
			 
			{ label: '不合格原因代码', name: 'returnreasoncode', index: 'returnreasoncode',hidden:true}, 
			{ label: '不合格原因类别', name: 'returnreasontype', index: 'returnreasontype',hidden:true}, 
			{ label: '单批检验', name: 'singleBatchInspection', index: '', width: 70,formatter:function(val, obj, row, act){
				var type  = $("#inspectionItemStatus").val();
				var href = baseUrl + "qc/redirect/singlebatchinspection?inspectionNo="+row.inspectionNo+"&inspectionItemNo="+row.inspectionItemNo+"&type="+type+"&batch="+row.batch+"&werks="+row.werks;//跳转到单批质检页面
				
				var disabledFlag = false;
				if(row.effectDate !== undefined && row.effectDate !== null){
					var effectDate = new Date(row.effectDate);
					var currentDate = new Date(formatDate(new Date()))
					if(effectDate < currentDate){
						//有效期小于当天,且用红色标识
						disabledFlag = true;
					}
				}
				if(disabledFlag){
					return '<span title="超过有效期，不能单批质检"> <a  class="btn btn-default disabled" role="button">检验</a> </span>'
				}
				
				if(row.barcodeFlag == 'X'){
					return '<span title="'+row.whNumber+'仓库包含标签管理，不能单批质检"> <a  class="btn btn-default disabled" role="button">检验</a> </span>'
				}
				return '<span> <a class="btn btn-primary btnList" href="'+href+'" title="单批质检">检验</a></span>'
			},align: 'center'}, 
			{ label: '收料房存放区', name: 'binCode', index: 'BIN_CODE', width: 90, align: 'center'}, 		
			{ label: '库位', name: 'lgort', index: 'LGORT', width: 70 ,align: 'center'},
			{ label: '供应商代码', name: 'lifnr', index: 'LIFNR', width: 80 ,align: 'center'}, 			
			{ label: '供应商名称', name: 'liktx', index: 'LIKTX', width: 180 ,align: 'center'},
			{ label: '需质检数量', name: 'requiredInspectionQtyCount', index: 'INSPECTION_QTY', width: 80 ,align: 'center'},
			{ label: '需开箱数量', name: 'requiredBoxCount', index: 'BOX_COUNT', width: 80 ,align: 'center'},
			{ label: '试装数量', name: 'tryQty', index: 'TRY_QTY', width: 80 ,align: 'center'}, 	
			{ label: '库存类型', name: 'sobkz', index: 'SOBKZ', width: 80 ,align: 'center'}, 	
			{ label: '单位', name: 'unit', index: 'UNIT', width: 60 ,align: 'center'}, 		
			
			{ label: '批量检验', name: 'qcResultCode', index: '', width: 80,hidden:true}, 
			{ label: '批量检验文本', name: 'qcResultText', index: 'qcResultText',hidden:true}, 
			{ label: '工厂代码', name: 'werks', index: 'WERKS', width: 80 ,align: 'center'}, 
			{ label: '仓库号', name: 'whNumber', index: 'WH_NUMBER', width: 70 ,align: 'center'},
			{ label: '收货日期', name: 'receiptDate', index: 'RECEIPT_DATE', width: 80 ,align: 'center'}, 			
			{ label: '收料员', name: 'receiver', index: 'RECEIVER', width: 80,align: 'center' }, 			
			{ label: '申请人', name: 'afnam', index: 'AFNAM', width: 60 ,align: 'center'}, 
			{ label: '属于退货类型', name: 'needReturn', index: 'needReturn',hidden:true},
			{label:'有效日期', name: 'effectDate', index : 'effectDate', width : 80,align: 'center'},
			{ label: '送检单号', name: 'inspectionNo', index: 'INSPECTION_NO', width: 110 ,align: 'center'}, 			
			{ label: '行项目号', name: 'inspectionItemNo', index: 'INSPECTION_ITEM_NO', width: 68 ,align: 'center'}, 
        ],
        viewrecords: true,
        cellEdit:false,
        autowidth:true,
        shrinkToFit:false,  
        autoScroll: true, 
        multiselect: true,
        gridComplete:function(){
        	//紧急物料（urgentFlag不为空的）设置自定义样式
            var ids = $("#dataGrid").getDataIDs();
            for(var i=0;i<ids.length;i++){
                var rowData = $("#dataGrid").getRowData(ids[i]);
                if(rowData.urgentFlag != null && rowData.urgentFlag !='' && rowData.urgentFlag != undefined){
                	//如果是紧急物料设置自定义的颜色
                    $('#'+ids[i]).find("td").addClass("urgent-select");
                }
                //过了质检日期的用红色标识
                if(checkEffectDate(rowData.effectDate)){
                	$('#'+ids[i]).find("td").addClass("urgent-select");
                }
            }
            
            //如果质检中，设置批量检验的下拉框不可用。
            var status = $("#inspectionItemStatus").val();
            if(status === '01'){
            	$("select.status").prop("disabled",true);
            	$("#dataGrid").setGridParam().hideCol("returnreason").trigger("reloadGrid"); 
            	$("#dataGrid").jqGrid('setLabel','qcResultCodeSel','质检状态')
            }else {
            	$("select.status").prop("disabled",false);
            	$("#dataGrid").setGridParam().showCol("returnreason").trigger("reloadGrid");
            	$("#dataGrid").jqGrid('setLabel','qcResultCodeSel','批量检验')
            }

        }
    });

}
//保质期校验
//true 过了保质期 
//false 没有过保质期 
function checkEffectDate(effectDate){
	if(effectDate == null || effectDate == undefined || effectDate.trim() == '')
		return false;
	effectDate = new Date(effectDate);
	var currentDate = new Date(formatDate(new Date()));
	if(effectDate < currentDate){
		return true;
	}
	return false;
}		

var vm = new Vue({
	el:"#vue-app",
	data:{
		werks:'',
		whNumber:'',
		warehourse:[],
		qcResult:[],
		wmsReturnReasons:[],
		saveVisiable:true,
		reason_type: null
	},
	watch:{
		reason_type:{
			handler:function(newVal,oldVal){
		        $.ajax({
		        	url:baseUrl + "config/wmscqcreturnreasons/list",
		      	  	data:{"REASON_TYPE": newVal},
		        	success:function(resp){
		        		vm.wmsReturnReasons = resp.page.list;
		        	}
		        });
		        
			}
		}
	},
	created:function(){
		this.werks = $("#werks").val();
		this.reason_type = '';
		this.initDataGrid();
		//更新质检结果配置
        this.updateQcResultConfig();
		//初始化查询仓库
		var plantCode = $("#werks").val();
        
        //初始化仓库
        $.ajax({
      	  url:baseUrl + "common/getWhDataByWerks",
    	  data:{"WERKS":plantCode},
      	  success:function(resp){
      		 vm.warehourse = resp.data;
      		 vm.whNumber = resp.data[0].WH_NUMBER;
      		 initGridTable();//初始化后，才加载表格。
      	  }
        });
		if(!$("#receiptDateStart").val().length > 0){
			this.initData();	
		}
        this.setRefresh();
	},
	methods:{
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
		query:function(){
			
			
			if($("#inspectionItemStatus").val() === '02'){
				//已质检重定向到已质检页面,当前页面的查询参数
				var queryparams = $("#searchForm").serialize();
				window.location.href = baseUrl +  "qc/redirect/hasinspectedlist?"+queryparams;
			}else{
				//未质检和质检中
				$("#searchForm").submit();
			}
			
			//----------
			if($("#inspectionItemStatus").val() === '01'){//质检中
				vm.saveVisiable = false; //不能批量保存
				//隐藏字段
				$("#dataGrid").setGridParam().hideCol("requiredInspectionQtyCount");
				$("#dataGrid").setGridParam().hideCol("requiredBoxCount");
				$("#dataGrid").setGridParam().hideCol("tryQty").trigger("reloadGrid");;
				//修改 送检数量字段名字为，质检数量
				$("#dataGrid").jqGrid('setLabel','inspectionQty','质检数量');
			}
			if($("#inspectionItemStatus").val() === '00'){//未质检
				vm.saveVisiable = true;//显示保存按钮
				$("#dataGrid").setGridParam().showCol("requiredInspectionQtyCount");
				$("#dataGrid").setGridParam().showCol("requiredBoxCount");
				$("#dataGrid").setGridParam().showCol("tryQty").trigger("reloadGrid");
				$("#dataGrid").jqGrid('setLabel','inspectionQty','送检数量').trigger("reloadGrid");
			}
		},
		updateQcResultConfig:function(){
			
			//获取质检结果配置 
			var statusOps = '<option value="">请选择</option>';
	        $.ajax({
	        	url:baseUrl + "config/wmscqcresult/list2",
	        	data:{"werks":$("#werks").val()},
	        	success:function(resp){
	        		var qcResultList =resp.page.list;
	        		this.qcResult = resp.page.list;
	        		for(i=0;i<qcResultList.length;i++){
	        			statusOps += '<option value="'+qcResultList[i].qcResultCode+'" >' +qcResultList[i].qcResultName + '</option>';
	        		}
	        		$("#qcResultSelect").html(statusOps);
	        	}.bind(this)
	        });
		},
		save:function(){
			js.loading("处理中...");
			$("#saveOperation").html("处理中...");
			$("#saveOperation").attr("disabled","disabled");
			//从jqGrid获取选中的行数据
			var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
			var rows = [];
			for(var id in ids){
				var row = $("#dataGrid").jqGrid('getRowData',ids[id]);
				vm.onStatusChange(null,id);
				row.qcResultCodeSel = "";
				row.singleBatchInspection = "";
				//没有选择质检结果
				if(row.qcResultCode === '' ||  row.qcResultCode === null || row.qcResultCode === undefined){
					js.showMessage("请选择质检结果");
					$("#saveOperation").html("保存");	
					$("#saveOperation").removeAttr("disabled");
					js.closeLoading();
					return;
				}
				//选择了质检结果且为退货类型
				if(row.needReturn === 'X' && (row.returnreasoncode.trim() === '' || row.returnreasoncode === null || row.returnreasoncode === undefined)){
					js.showMessage("请选择不合格原因");
					$("#saveOperation").html("保存");	
					$("#saveOperation").removeAttr("disabled");
					js.closeLoading();
					return;
				}
				if(row.tryQty !== null && row.tryQty != undefined && row.tryQty !=='' && Number(row.tryQty)>0){
					//为不合格类型，且试装数量大于送检数量
					//update : 2019-05-31
					//如果属于过了保质期的物料，不用判断试装
					var isNotEffect = checkEffectDate(row.effectDate);
				
					if(row.needReturn === 'X' && !isNotEffect){
						js.showMessage("质检合格数量必须大于试装数量("+row.tryQty+")");
						$("#saveOperation").html("保存");	
						$("#saveOperation").removeAttr("disabled");
						js.closeLoading();
						return ;
					}
				}
				row.batchQcFlag = "batchQcFlag";
				rows[id] = row;
			}
			if(rows.length <=0){
				js.showMessage("没有可保存的数据!");
				$("#saveOperation").html("保存");	
				$("#saveOperation").removeAttr("disabled");
				js.closeLoading();
				return;
			}
			//
			var inspectionItemStatus = $("#inspectionItemStatus").val();
			var url = "";
			if(inspectionItemStatus === "00"){
				url = baseUrl + "qc/wmsqcinspectionhead/saveinspectionbatch";
			}
			if(inspectionItemStatus === "01"){
				url = baseUrl + "qc/wmsqcinspectionhead/saveOnInspect";
			}
			
			$.ajax({
				url:url,
				type:"post",
				contentType:"application/json",
				data:JSON.stringify(rows),
				success:function(resp){
					if(resp.code === 0){
						js.showMessage("保存成功");
						//刷新页面
						vm.query();
					}else{
						js.showMessage("保存失败," + resp.msg);
					}
					$("#saveOperation").html("保存");	
					$("#saveOperation").removeAttr("disabled");
					js.closeLoading();
				}
			});
			
		},
		onStatusChange:function(event,id){
			//选择不合格原因
			
			var statusCode = null;
			if(event != undefined && event != null){
				statusCode = event.target.value
				 //设置不合格原因code
				$("#dataGrid").dataGrid("setCell",id,"qcResultCode",statusCode);
			}else {
				statusCode =$("#dataGrid").dataGrid("getRowData",id).qcResultCode;
			}
			if(statusCode == null || statusCode == ""){
				//取消选中行
				$("#dataGrid").jqGrid('resetSelection',id);
			}else{
				//选中行
				$('#dataGrid').jqGrid('setSelection',id);
			}
			
			//判断是否是不合格或退货
			var needReturn = false;
			this.qcResult.map(function(val){
				//退货类型或者不可进仓
				//且不是质检中状态
				if(val.qcResultCode === statusCode  && (val.returnFlag == 'X' || val.whFlag == '0') && val.qcStatus !='01'){
					needReturn = true;
				}
			});
			$("#dataGrid").jqGrid("setCell",id,"needReturn","0");
		    //弹出选择不合格原因，弹出框。
			if(needReturn){
				//设置为退货类型
				$("#dataGrid").jqGrid("setCell",id,"needReturn","X");
				
				layer.open({
					  type: 1,
					  title:"选择不合格原因",
					  area: ['70%', '50%'],
					  closeBtn:2,
					  btn:['确定','取消'],
					  yes:function(index,layero){
						  var selectedVal =  $("#wmscqcreturnreasons-select").find("option:selected").text();
						  var selectedCode = $("#wmscqcreturnreasons-select").val();
						  var selectedType = $("#wmscqcreturnreasons-select").find("option:selected").attr("data-reasonType");
						   //设置选择的不合格原文本、code到 dataGrid
						   $("#dataGrid").dataGrid("setCell",id,"returnreason",selectedVal);
						   $("#dataGrid").dataGrid("setCell",id,"returnreasoncode",selectedCode);
						   $("#dataGrid").dataGrid("setCell",id,"returnreasontype",selectedType);
						  
					  },
					  no:function(){
						 
					  },
					  fixed: false, //不固定
					  maxmin: true,
					  content: $("#wmscqcreturnreasons")
				});
			}else{
				//取消退货类型标识
				$("#dataGrid").jqGrid("setCell",id,"needReturn","");
				//去掉不合格原因描述
				$("#dataGrid").dataGrid("setCell",id,"returnreason",' ');
				$("#dataGrid").dataGrid("setCell",id,"returnreasoncode",' ');
			}			
		},
		onPlantChange:function(event){
			  //工厂变化的时候，更新库存下拉框
	          var plantCode = event.target.value;
	          if(plantCode === null || plantCode === '' || plantCode === undefined){
	        	  return;
	          }
	          //查询工厂仓库
	          $.ajax({
	        	  url:baseUrl + "common/getWhDataByWerks",
	        	  data:{"WERKS":plantCode},
	        	  async:false,
	        	  success:function(resp){
	        		 vm.warehourse = resp.data;
	        		 vm.whNumber = resp.data[0].WH_NUMBER;
	        		 $("#whNumber").val(resp.data[0].WH_NUMBER);
	        	  }
	          })
	          //工厂变化同时更新质检结果配置
	          this.updateQcResultConfig();
	          vm.query();
		},
		initData:function(){
			//设置默认日期，当前日期往前一个月
			var now = new Date(); //当前日期
			var endDate = now.Format("yyyy-MM-dd hh:mm:ss");
			var startDate = GetDateStr(-2);//获取退后一个月的日期
			
			$(function(){
				
				var WERKS=getUrlKey("WERKS");
				var WH_NUMBER=getUrlKey("WH_NUMBER");
				var MATNR=getUrlKey("MATNR");
				var BATCH=getUrlKey("BATCH");
				
				if(WERKS !=null && WERKS!=''){
					vm.werks = WERKS;
					$("#werks option[value='"+WERKS+"']").prop("selected",true);
			        //初始化仓库
			        $.ajax({
			      	  url:baseUrl + "common/getWhDataByWerks",
			    	  data:{"WERKS":WERKS},
			      	  success:function(resp){
			      		 vm.warehourse = resp.data;
			      		 vm.whNumber = resp.data[0].WH_NUMBER;
			      	  }
			        });
				}else{
					$("#receiptDateStart").val(startDate+" 00:00:00");
					$("#receiptDateEnd").val(endDate+" 23:59:59");
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
				}
			});
		},
		initDataGrid:function(){},
		exp: function () {
			export2Excel();
		}
	}
})

function export2Excel(){
	var column=[      
		{"title":'送检单号', "data":'inspectionNo', "class":"center"},
		{"title":'行项目号', "data":'inspectionItemNo', "class":"center"},
		{"title":'料号', "data":'matnr', "class":"center"},
		{"title":'物料描述', "data":'maktx', "class":"center"},
		{"title":'送检数量', "data":'inspectionQty', "class":"center"},
		{"title":'需质检数量', "data":'requiredInspectionQtyCount', "class":"center"},
		{"title":'需开箱数量', "data":'requiredBoxCount', "class":"center"},
		{"title":'试装数量', "data":'tryQty', "class":"center"},
		{"title":'库位', "data":'lgort',"class":"center"},
		{"title":'特殊库存类型', "data":'sobkz',"class":"center"},
		{"title":'仓库号', "data":'whNumber', "class":"center"},
		{"title":'批次', "data":'batch',"class":"center"},
		{"title":'单位', "data":'unit', "class":"center"},
		{"title":'供应商代码', "data":'lifnr', "class":"center"},
		{"title":'供应商名称', "data":'liktx',  "class":"center"},
		{"title":'需求跟踪号', "data":'bednr', "class":"center"},
		{"title":'工厂代码', "data":'werks', "class":"center"},
		{"title":'收料房存放区', "data":'binCode', "class":"center"},
		{"title":'收货日期', "data":'receiptDate', "class":"center"},
		{"title":'申请人', "data":'afnam',"class":"center"},
		{"title":'收料员', "data":'receiver', "class":"center"},
		{"title":'退货类型', "data":'needReturn', "class":"center"},
		{"title":'有效日期', "data":'effectDate', "class":"center"},
      ]	;
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+'qc/wmsqcinspectionitem/list',
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) { 
		    results=response.page.list;
		}
	});
	var rowGroups=[];
	getMergeTable(results,column,rowGroups,"来料质检清单");	
}

function GetDateStr(AddDayCount) { 
	   var dd = new Date();
	   dd.setDate(dd.getDate()+AddDayCount);//获取AddDayCount天后的日期
	   var y = dd.getFullYear(); 
	   var m = (dd.getMonth()+1)<10?"0"+(dd.getMonth()+1):(dd.getMonth()+1);//获取当前月份的日期，不足10补0
	   var d = dd.getDate()<10?"0"+dd.getDate():dd.getDate();//获取当前几号，不足10补0
	   return y+"-"+m+"-"+d; 
}

function getUrlKey(name){
	return decodeURIComponent((new RegExp('[?|&]'+name+'='+'([^&;]+?)(&|#|;|$)').exec(location.href)||[,""])[1].replace(/\+/g,'%20'))||null;
}