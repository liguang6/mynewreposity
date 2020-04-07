var listdata = [];
var lastrow,lastcell,lastcellname; 
var lastrow501,lastcell501,lastcellname501; 
var trindex = 1;

var trindex_product=1;

var productdata = [{ id: "1",AUFNR: ""},{ id: "2",AUFNR: ""}];		//初始化批量订单数据
var productmarntdata = [{ id: "1",AUFNR: "",MATNR:""},{ id: "2",AUFNR: "",MATNR:""}];		//初始化批量订单数据

var yf262importdata = [{ id: "1",IO_NO: "",MATERIAL:"",MATERIALDESC:"",IN_QTY:"",UNIT:"",FULL_BOX_QTY:"",
	BOX_COUNT:"",BIN_CODE:"",BEDNR:"",PRDDT:"",LT_WARE:"",WORKSHOP:"",CAR_TYPE:"",MOULD_NO:"",CPTH:"",
	ZYY:"",DIS_STATION:"",WORKGROUP_NO:"",WH_MANAGER:"",REMARK:"",RECWERKS:"",WHNUMBER:"",RLGORT:"",
	INBOUND_TYPE:"",urgentFlag:""}];
			
var vm = new Vue({
	el:"#vue",
	data:{
		werks:"",
		warehourse:[],
		whNumber:"",//
		lgort:"",
		lgortlist:[],//目标库位
		PRDDT:getCurDate(),
		PZYEAR:getCurDate().substring(0,4),
		inbound_type:"",
		showDiv:false,
        rowid:""
	},
	watch:{
		//业务类型变化，切换表头
		inbound_type:{
			handler:function(newVal,oldVal){
				showOrHideDataGrid(newVal);
			}
		},
		werks:{
			handler:function(newVal,oldVal){
				 vm.warehourse = [];
				 vm.lgortList = [];
		         this.$nextTick(function(){
		        	 
					 $.ajax({
						    url:baseURL+"common/getWhDataByWerks",
							dataType : "json",
							type : "post",
							data : {
								"WERKS":newVal
							},
							async: true,
				        	  success:function(resp){
				        		  $("#whNumber").empty();
				        		 if(resp && resp.data.length>0){
					        		 /*vm.warehourse = resp.data;
					        		 vm.whNumber = resp.data[0].WH_NUMBER;*/
				        			//20190713 注释上面的内容改为下面的逻辑，html也做了相应的修改
				 					var strs = "";
				 				    $.each(resp.data, function(index, value) {
				 				    	strs += "<option value=" + value.WH_NUMBER + ">" + value.WH_NUMBER + "</option>";
				 				    });
				 				    $("#whNumber").append(strs);
				 				    //向vm中的仓库号赋值，因为在html没有关联vm.whNumber
				 				   vm.whNumber=$("#whNumber").val();
				        		 }
				        	  }
			          })
	        		  vm.lgortList = getLgortList(newVal)
				})
				
				//替换行中的工厂
				var rows=$("#dataGrid").jqGrid('getRowData');
					$.each(rows,function(i,row){	
						
					  $("#dataGrid").jqGrid('setCell', i+1, "RECWERKS", newVal);//第二个参数代表行 id，第三个代表 列名称，第四个可以是值或者属性
						
					});
			}
		},
		lgort:{
			handler:function(newVal,oldVal){
				var rows=$("#dataGrid").jqGrid('getRowData');
				$.each(rows,function(i,row){	
					
				  $("#dataGrid").jqGrid('setCell', i+1, "RLGORT", newVal);//第二个参数代表行 id，第三个代表 列名称，第四个可以是值或者属性
					
				});
			}
		},
		
		whNumber:{
			handler:function(newVal,oldVal){
				var rows=$("#dataGrid").jqGrid('getRowData');
				$.each(rows,function(i,row){	
					
				  $("#dataGrid").jqGrid('setCell', i+1, "WHNUMBER", newVal);//第二个参数代表行 id，第三个代表 列名称，第四个可以是值或者属性
					
				});
			}
		}
	},
	created:function(){
		this.businessList = getBusinessList("03");
		if(undefined == this.businessList || this.businessList.length<=0){
			js.showErrorMessage("工厂未配置自制进仓业务类型！");	
		}else{
			this.inbound_type=this.businessList[0].CODE;
			this.werks=$("#werks").find("option").first().val();
		}

        //TIP: JqGrid必须要在页面加载完成后才初始化。
//		$(function(){
//			//页面加载时，默认为生产订单领料。
//			vm.init1();
//		});


        //绑定粘贴事件
        $(function(){

            $("#dataGridDiv").bind("paste",function(e){
                e.preventDefault();
                // 获取粘贴板数据
                var data = null;
                var clipboardData = window.clipboardData || e.originalEvent.clipboardData;
                data = clipboardData.getData('Text');

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

                var inboundType = $('#inboundType').val();

				//如果是指定输入框，只粘贴数据到当前输入框。
                if("INPUT" === e.target.tagName){
                    if(tdList.length === 1 && tdList[0].length === 1){
                        e.target.value = tdList[0][0];
                        var MATERIAL = $("#dataGrid").getCell(rowInx, "MATERIAL");
                        if(MATERIAL != '' && MATERIAL.indexOf("<input") < 0) {
                            vm.queryMatInfo(MATERIAL, function (matinfo) {
                                //设置物料描述，物料单位
                                $("#dataGrid").jqGrid('setCell', rowInx, "MATERIALDESC", matinfo.MAKTX);
                                $("#dataGrid").jqGrid('setCell', rowInx, "UNIT", matinfo.MEINH);
                            });
                        }
                        return;
                    }
                    $.ajaxSettings.async = false;

                    var records =$("#dataGrid").children("tbody").children("tr").length -1;
                    var rowid = vm.rowid;

                    for(var i in tdList){
                        var rowInx = parseInt(rowid) + parseInt(i);
                        var rowData = {};
                        if(inboundType ==='12'){
                            rowData= {
                                IO_NO: tdList[i][0],
                                MATERIAL: tdList[i][1],
                                IN_QTY: tdList[i][2],
                                FULL_BOX_QTY: tdList[i][3],
                                BEDNR: tdList[i][4],
                                PRDDT: tdList[i][5],
                                LT_WARE: tdList[i][6],
                                WORKSHOP: tdList[i][7],
                                CAR_TYPE: tdList[i][8],
                                MOULD_NO: tdList[i][9],
                                ZYY: tdList[i][10],
                                DIS_STATION: tdList[i][11],
                                WORKGROUP_NO: tdList[i][12],
                                REMARK: tdList[i][13],
                                RECWERKS: vm.werks,
                                WHNUMBER: vm.whNumber,
                                LIFNR: vm.lgort,
                                INBOUND_TYPE: vm.inbound_type

                            }
                        }
                        if(inboundType ==='14' || inboundType ==='15'|| inboundType ==='16' || inboundType ==='17'){
                            var no = '';
                            e.target.value = tdList[0][0];
                            if(inboundType ==='14') {

                                no = $('#co_no101').val();
                                rowData.IO_NO = no;
                            }

                            if(inboundType ==='15'){
                                no = $('#yf_262').val();
                                rowData.IO_NO = no;
                            }
                            if(inboundType ==='16') {
                                no = $('#cb_202').val();
                                rowData.COST_CENTER = no;
                            }
                            if(inboundType ==='17') {
                                no = $('#wbs_222').val();
                                rowData.WBS = no;
                            }

                            rowData.MATERIAL = tdList[i][0];
                            rowData.IN_QTY = tdList[i][1];
                            rowData.FULL_BOX_QTY = tdList[i][2];
                            rowData.BEDNR = tdList[i][3];
                            rowData.PRDDT = tdList[i][4];
                            rowData.LT_WARE = tdList[i][5];
                            rowData.WORKSHOP = tdList[i][6];
                            rowData.CAR_TYPE = tdList[i][7];
                            rowData.MOULD_NO = tdList[i][8];
                            rowData.ZYY = tdList[i][9];
                            rowData.DIS_STATION = tdList[i][10];
                            rowData.WORKGROUP_NO = tdList[i][11];
                            rowData.REMARK = tdList[i][12];
                            rowData.RECWERKS = vm.werks;
                            rowData.WHNUMBER = vm.whNumber;
                            rowData.LIFNR = vm.lgort;
                            rowData.INBOUND_TYPE = vm.inbound_type;

                        }
                        if(inboundType ==='18') {

                            rowData.CUSTOMER_MACD = tdList[i][0];
                            rowData.MATERIAL = tdList[i][1];
                            rowData.IN_QTY = tdList[i][2];
                            rowData.FULL_BOX_QTY = tdList[i][3];
                            rowData.BEDNR = tdList[i][4];
                            rowData.PRDDT = tdList[i][5];
                            rowData.REMARK = tdList[i][6];
                            rowData.RECWERKS = vm.werks;
                            rowData.WHNUMBER = vm.whNumber;
                            rowData.RLGORT = vm.lgort;
                            rowData.LIFNR = $("#vendor_501").val();
                            rowData.LIKTX = $("#vendorName_501").val();
                            rowData.INBOUND_TYPE =$("#inboundType").val();
                        }
                        if(inboundType ==='19') {
                            rowData.MATERIAL = tdList[i][0];
                            rowData.IN_QTY = tdList[i][1];
                            rowData.FULL_BOX_QTY = tdList[i][2];
                            rowData.BEDNR = tdList[i][3];
                            rowData.PRDDT = tdList[i][4];
                            rowData.REMARK = tdList[i][5];
                            rowData.RECWERKS = vm.werks;
                            rowData.WHNUMBER = vm.whNumber;
                            rowData.RLGORT = vm.lgort;
                            rowData.LIFNR = $("#vendor_511").val();
                            rowData.LIKTX = $("#vendorName_511").val();
                            rowData.INBOUND_TYPE =$("#inboundType").val();
                        }
                        if(inboundType ==='20') {
                            rowData.MATERIAL = tdList[i][0];
                            rowData.IN_QTY = tdList[i][1];
                            rowData.FULL_BOX_QTY = tdList[i][2];
                            rowData.BEDNR = tdList[i][3];
                            rowData.PRDDT = tdList[i][4];
                            rowData.REMARK = tdList[i][5];
                            rowData.RECWERKS = vm.werks;
                            rowData.WHNUMBER = vm.whNumber;
                            rowData.RLGORT = vm.lgort;
                            rowData.LIFNR = $("#vendor_903").val();
                            rowData.LIKTX = $("#vendorName_903").val();
                            rowData.INBOUND_TYPE =$("#inboundType").val();
                        }
                        if(inboundType ==='74') {
                            rowData.MATERIAL = tdList[i][0];
                            rowData.IN_QTY = tdList[i][1];
                            rowData.FULL_BOX_QTY = tdList[i][2];
                            rowData.BEDNR = tdList[i][3];
                            rowData.PRDDT = tdList[i][4];
                            rowData.REMARK = tdList[i][5];
                            rowData.RLGORT =vm.lgort;
                            /*rowData.RECWERKS = vm.werks;
                            rowData.WHNUMBER = vm.whNumber;
                            rowData.MO_NO = $("#AUFNR_Mo262").val();*/


                            rowData.INBOUND_TYPE =vm.inbound_type;
                        }
                        if (parseInt(i) === 0) {
                            $("#dataGrid").jqGrid('setRowData',rowInx,rowData);
                        }
                        else if (parseInt(rowInx) <= parseInt(records)) {
                            $("#dataGrid").jqGrid('setRowData',rowInx,rowData);
                        } else {
                            $("#dataGrid").jqGrid('addRowData',rowInx,rowData);
                        }
                        var MATERIAL = $("#dataGrid").getCell(rowInx, "MATERIAL");
                        //<input：前段异常，input的默认值有时会变成其dom结构
                        if(MATERIAL != '' && MATERIAL.indexOf("<input") < 0) {
                            vm.queryMatInfo(MATERIAL, function (matinfo) {
                                //设置物料描述，物料单位
                                $("#dataGrid").jqGrid('setCell', rowInx, "MATERIALDESC", matinfo.MAKTX);
                                $("#dataGrid").jqGrid('setCell', rowInx, "UNIT", matinfo.MEINH);
                            });
                        }
                    }


                    e.target.value = tdList[0][0];
                    $.ajaxSettings.async = true;//恢复ajax异步
                    return;
                }
                /*if("INPUT" === e.target.tagName){
                    e.target.value = tdList[0][0];
                    var lastrow = vm.lastrow;
                    var lastcell = vm.lastcell;
                    var lastcellname = vm.lastcellname;
                    $.ajaxSettings.async = false;
                    var records = $('#dataGrid').jqGrid('getGridParam','records');
                    for(var i in tdList) {
                        if (parseInt(i) == 0) {
                            continue;
                        }
                        var rowId = parseInt(lastrow) + parseInt(i) - 1;
                        var rowData;
                        if (inboundType === '12') {
                            rowData = {
                                IO_NO: tdList[i][0],
                                MATERIAL: tdList[i][1],
                                IN_QTY: tdList[i][2],
                                FULL_BOX_QTY: tdList[i][3],
                                BEDNR: tdList[i][4],
                                PRDDT: tdList[i][5],
                                LT_WARE: tdList[i][6],
                                WORKSHOP: tdList[i][7],
                                CAR_TYPE: tdList[i][8],
                                MOULD_NO: tdList[i][9],
                                ZYY: tdList[i][10],
                                DIS_STATION: tdList[i][11],
                                WORKGROUP_NO: tdList[i][12],
                                REMARK: tdList[i][13]
                            }

                            if (parseInt(rowId) < parseInt(records)) {
                                $("#dataGrid").jqGrid('setRowData', rowId, rowData);
                            } else {
                                $("#dataGrid").jqGrid('addRowData', rowId, rowData);
                            }
                            var MATERIAL = $("#dataGrid").getCell(rowId, "MATERIAL");
                            vm.queryMatInfo(MATERIAL, function (matinfo) {
                                //设置物料描述，物料单位
                                $("#dataGrid").jqGrid('setCell', rowId, "MATERIALDESC", matinfo.MAKTX);
                                $("#dataGrid").jqGrid('setCell', rowId, "UNIT", matinfo.MEINH);
                            });
                        }
                    }
                    $.ajaxSettings.async = true;//恢复ajax异步
                    return;
                }*/

            });
        });
	},
	methods:{
		query:function(){
			$("#searchForm").submit();
		},
		
		save:function(){
			$(".btn").attr("disabled", true);
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			//从jqGrid获取选中的行数据
			var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
			
			if(ids.length==0){
				js.alert("请选择要提交的数据!");
				$(".btn").attr("disabled", false);
				return;
			}
			var rows = [];
			for(var id in ids){
				var row = $("#dataGrid").jqGrid('getRowData',ids[id]);
				
				row.RN= $("#dataGrid").jqGrid('getGridParam','selrow');
				
				rows[id] = row;
				
				if(row.IN_QTY-row.ENTRY_QNT>0){
					js.alert("进仓数量不能大于调入数量!");
					return;
				}
			}
			$.ajax({
				url:baseUrl + "in/wmsinternalbound/save",
				dataType : "json",
				type : "post",
				data : {
					"ARRLIST":JSON.stringify(rows)
				},
				async: false,
				success:function(resp){
					if(resp.code == '0'){
						//js.showMessage("保存成功! 进仓单号  "+resp.ininternetbountNo,null,null,1000*60000);
						$("#inboundNoLayer").html(resp.ininternetbountNo);
						$("#inboundNo").val(resp.ininternetbountNo);
						$("#inboundNo1").val(resp.ininternetbountNo);
						$("#inboundNo2").val(resp.ininternetbountNo);
						layer.open({
		            		type : 1,
		            		offset : '50px',
		            		skin : 'layui-layer-molv',
		            		title : "进仓单创建成功",
		            		area : [ '600px', '200px' ],
		            		shade : 0,
		            		shadeClose : false,
		            		content : jQuery("#resultLayer"),
		            		btn : [ '确定'],
		            		btn1 : function(index) {
		            			layer.close(index);
		            		}
		            	});
						if($("#pz303Z25").val()!=""){
							queryListZ25();
						}else{
							queryList();
						}
						
						$(".btn").attr("disabled", false);
					}else{
						js.alert("保存失败");
						js.showErrorMessage(resp.msg,1000*100);
						$(".btn").attr("disabled", false);
					}
				}
			});
		},
		onStatusChange:function(event,id){},
		getVendorNoFuzzy:function(){
			getVendorNoSelect("#vendor",null,null,$("werks").val());
		},
		getVendorNoFuzzy101:function(){
			getVendorNoSelect("#vendor_101",null,null,$("werks").val());
		},
		getVendorNoFuzzy501:function(){
			getVendorNoSelect("#vendor_501",null,null,$("werks_501").val());
		},
		getVendorNoFuzzy903:function(){
			getVendorNoSelect("#vendor_903",null,null,$("werks_903").val());
		},
		getVendorNoFuzzy511:function(){
			getVendorNoSelect("#vendor_511",null,null,$("werks_511").val());
		},
		getVerdorName101:function(){
			mapcond={lifnr:$("#vendor_101").val()};
			$.ajax({
		        url: baseURL + "config/CVendor/listVendortName",
		        dataType : "json",
				type : "post",
		        data: mapcond,
		        async:false,
		        success: function(data){
		        	if(data.code == 0){
		        		$("#vendorName_101").val(data.vendorName);
		        	}
		        }
			});
			//填充列表中的供应商编码和名称
			var LIFNR = $("#vendor_101").val();
			var LIKTX = $("#vendorName_101").val();
			var rows=$("#dataGrid").jqGrid('getRowData');
			$.each(rows,function(i,row){	
				
				  $("#dataGrid").jqGrid('setCell', i+1, "LIFNR", LIFNR);//第二个参数代表行 id，第三个代表 列名称，第四个可以是值或者属性
				  $("#dataGrid").jqGrid('setCell', i+1, "LIKTX", LIKTX);
				});
			
		},
		getVerdorName:function(){
			mapcond={lifnr:$("#vendor_501").val()};
			$.ajax({
		        url: baseURL + "config/CVendor/listVendortName",
		        dataType : "json",
				type : "post",
		        data: mapcond,
		        async:false,
		        success: function(data){
		        	if(data.code == 0){
		        		$("#vendorName_501").val(data.vendorName);
		        	}
		        }
			});
			//填充列表中的供应商编码和名称
			var LIFNR = $("#vendor_501").val();
			var LIKTX = $("#vendorName_501").val();
			var rows=$("#dataGrid").jqGrid('getRowData');
			$.each(rows,function(i,row){	
				
				  $("#dataGrid").jqGrid('setCell', i+1, "LIFNR", LIFNR);//第二个参数代表行 id，第三个代表 列名称，第四个可以是值或者属性
				  $("#dataGrid").jqGrid('setCell', i+1, "LIKTX", LIKTX);
				});
			
		},
		getVerdorName903:function(){
			mapcond={lifnr:$("#vendor_903").val()};
			$.ajax({
		        url: baseURL + "config/CVendor/listVendortName",
		        dataType : "json",
				type : "post",
		        data: mapcond,
		        async:false,
		        success: function(data){
		        	if(data.code == 0){
		        		$("#vendorName_903").val(data.vendorName);
		        	}
		        }
			});
			//填充列表中的供应商编码和名称
			var LIFNR = $("#vendor_903").val();
			var LIKTX = $("#vendorName_903").val();
			var rows=$("#dataGrid").jqGrid('getRowData');
			$.each(rows,function(i,row){	
				
				  $("#dataGrid").jqGrid('setCell', i+1, "LIFNR", LIFNR);//第二个参数代表行 id，第三个代表 列名称，第四个可以是值或者属性
				  $("#dataGrid").jqGrid('setCell', i+1, "LIKTX", LIKTX);
				});
			
		},
		getVerdorName511:function(){
			mapcond={lifnr:$("#vendor_511").val()};
			$.ajax({
		        url: baseURL + "config/CVendor/listVendortName",
		        dataType : "json",
				type : "post",
		        data: mapcond,
		        async:false,
		        success: function(data){
		        	if(data.code == 0){
		        		$("#vendorName_511").val(data.vendorName);
		        	}
		        }
			});
			//填充列表中的供应商编码和名称
			var LIFNR = $("#vendor_511").val();
			var LIKTX = $("#vendorName_511").val();
			var rows=$("#dataGrid").jqGrid('getRowData');
			$.each(rows,function(i,row){	
				
				  $("#dataGrid").jqGrid('setCell', i+1, "LIFNR", LIFNR);//第二个参数代表行 id，第三个代表 列名称，第四个可以是值或者属性
				  $("#dataGrid").jqGrid('setCell', i+1, "LIKTX", LIKTX);
				});
			
		},
		show:function(){
			vm.showDiv=!vm.showDiv;
			//$('#dataGrid').trigger('reloadGrid');
		},
		onltwareChange:function(){
			var ltware = $("#LT_WARE").val();
			var rows=$("#dataGrid").jqGrid('getRowData');
			$.each(rows,function(i,row){	
				
			  $("#dataGrid").jqGrid('setCell', i+1, "LT_WARE", ltware);//第二个参数代表行 id，第三个代表 列名称，第四个可以是值或者属性
				
			});
		},
		onworkshopChange:function(){
			var workshop = $("#WORKSHOP").val();
			var rows=$("#dataGrid").jqGrid('getRowData');
			$.each(rows,function(i,row){	
				
			  $("#dataGrid").jqGrid('setCell', i+1, "WORKSHOP", workshop);//第二个参数代表行 id，第三个代表 列名称，第四个可以是值或者属性
				
			});
		},
		oncartypeChange:function(){
			var cartype = $("#CAR_TYPE").val();
			var rows=$("#dataGrid").jqGrid('getRowData');
			$.each(rows,function(i,row){	
				
			  $("#dataGrid").jqGrid('setCell', i+1, "CAR_TYPE", cartype);//第二个参数代表行 id，第三个代表 列名称，第四个可以是值或者属性
				
			});
		},
		onmouldnoChange:function(){
			var mouldno = $("#MOULD_NO").val();
			var rows=$("#dataGrid").jqGrid('getRowData');
			$.each(rows,function(i,row){	
				
			  $("#dataGrid").jqGrid('setCell', i+1, "MOULD_NO", mouldno);//第二个参数代表行 id，第三个代表 列名称，第四个可以是值或者属性
				
			});
		},
		onzyyChange:function(){
			var zyy = $("#ZYY").val();
			var rows=$("#dataGrid").jqGrid('getRowData');
			$.each(rows,function(i,row){	
				
			  $("#dataGrid").jqGrid('setCell', i+1, "ZYY", zyy);//第二个参数代表行 id，第三个代表 列名称，第四个可以是值或者属性
				
			});
		},
		ondisstationChange:function(){
			var disstation = $("#DIS_STATION").val();
			var rows=$("#dataGrid").jqGrid('getRowData');
			$.each(rows,function(i,row){	
				
			  $("#dataGrid").jqGrid('setCell', i+1, "DIS_STATION", disstation);//第二个参数代表行 id，第三个代表 列名称，第四个可以是值或者属性
				
			});
		},
		onworkgroupnoChange:function(){
			var workgroupno = $("#WORKGROUP_NO").val();
			var rows=$("#dataGrid").jqGrid('getRowData');
			$.each(rows,function(i,row){	
				
			  $("#dataGrid").jqGrid('setCell', i+1, "WORKGROUP_NO", workgroupno);//第二个参数代表行 id，第三个代表 列名称，第四个可以是值或者属性
				
			});
		},
		onremarkChange:function(){
			var remark = $("#REMARK").val();
			var rows=$("#dataGrid").jqGrid('getRowData');
			$.each(rows,function(i,row){	
				
			  $("#dataGrid").jqGrid('setCell', i+1, "REMARK", remark);//第二个参数代表行 id，第三个代表 列名称，第四个可以是值或者属性
				
			});
		},
		//需求跟踪号修改 
		onXqggzChange:function(){
			var xqggh = $("#xqgzh_501").val();
			var rows=$("#dataGrid").jqGrid('getRowData');
			$.each(rows,function(i,row){	
				
			  $("#dataGrid").jqGrid('setCell', i+1, "BEDNR", xqggh);//第二个参数代表行 id，第三个代表 列名称，第四个可以是值或者属性
				
			});
		},
		onXqggzChange903:function(){
			var xqggh = $("#xqgzh_903").val();
			var rows=$("#dataGrid").jqGrid('getRowData');
			$.each(rows,function(i,row){	
				
			  $("#dataGrid").jqGrid('setCell', i+1, "BEDNR", xqggh);//第二个参数代表行 id，第三个代表 列名称，第四个可以是值或者属性
				
			});
		},
		onXqggzChange511:function(){
			var xqggh = $("#xqgzh_511").val();
			var rows=$("#dataGrid").jqGrid('getRowData');
			$.each(rows,function(i,row){	
				
			  $("#dataGrid").jqGrid('setCell', i+1, "BEDNR", xqggh);//第二个参数代表行 id，第三个代表 列名称，第四个可以是值或者属性
				
			});
		},
		
		save501:function(){
			$(".btn").attr("disabled", true);
			$('#dataGrid').jqGrid("saveCell", lastrow501, lastcell501);
			//从jqGrid获取选中的行数据
			var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
			
			if(ids.length==0){
				js.alert("请选择要提交的数据!");
				$(".btn").attr("disabled", false);
				return;
			}
			
			if($("#vendor_501").val()==""){
				js.alert("供应商不能为空!");
				$(".btn").attr("disabled", false);
				return;
			}
			
			
			var rows_=[];
			var trs=$("#dataGrid").children("tbody").children("tr");
			//$.each(trs,function(index,tr){
				for(var m=1;m<trs.length;m++){
				var cbx=$(trs[m]).find("td").find("input").attr("type");
				if(cbx!=undefined){	
					var c_checkbox=$(trs[m]).find('input[type=checkbox]');
					var ischecked=$(c_checkbox).is(":checked");
					if(ischecked){
						var RN= $("#dataGrid").jqGrid('getGridParam','selrow');
						var CUSTOMER_MACD ="";
						if($(trs[m]).find("td").eq(2).find("input").val()!=undefined){
							CUSTOMER_MACD = $(trs[m]).find("td").eq(2).find("input").val();
						}else{
							CUSTOMER_MACD = $(trs[m]).find("td").eq(2).html();
						}
						var MATERIAL ="";
						if($(trs[m]).find("td").eq(3).find("input").val()!=undefined){
							MATERIAL = $(trs[m]).find("td").eq(3).find("input").val();
						}else{
							MATERIAL = $(trs[m]).find("td").eq(3).html();
						}
						
						var MATERIALDESC = $(trs[m]).find("td").eq(4).html();
						
						var IN_QTY =0;
						if($(trs[m]).find("td").eq(5).find("input").val()!=undefined){
							IN_QTY = $(trs[m]).find("td").eq(5).find("input").val();
						}else{
							IN_QTY = $(trs[m]).find("td").eq(5).html();
						}
						
						var UNIT = $(trs[m]).find("td").eq(6).html();
						
						var FULL_BOX_QTY =0;
						if($(trs[m]).find("td").eq(7).find("input").val()!=undefined){
							FULL_BOX_QTY = $(trs[m]).find("td").eq(7).find("input").val();
						}else{
							FULL_BOX_QTY = $(trs[m]).find("td").eq(7).html();
						}
						
						var BOX_COUNT = $(trs[m]).find("td").eq(8).html();
						var BIN_CODE = $(trs[m]).find("td").eq(9).html();
						
						var BEDNR ="";
						if($(trs[m]).find("td").eq(10).find("input").val()!=undefined){
							BEDNR = $(trs[m]).find("td").eq(10).find("input").val();
						}else{
							BEDNR = $(trs[m]).find("td").eq(10).html();
						}
						
						var PRDDT ="";
						if($(trs[m]).find("td").eq(11).find("input").val()!=undefined){
							PRDDT = $(trs[m]).find("td").eq(11).find("input").val();
						}else{
							PRDDT = $(trs[m]).find("td").eq(11).html();
						}
						var WH_MANAGER = $(trs[m]).find("td").eq(12).html();
						var REMARK =""
						if($(trs[m]).find("td").eq(13).find("input").val()!=undefined){
							REMARK = $(trs[m]).find("td").eq(13).find("input").val();
						}else{
							REMARK = $(trs[m]).find("td").eq(13).html();
						}
						var RECWERKS = $(trs[m]).find("td").eq(14).html();
						var WHNUMBER = $(trs[m]).find("td").eq(15).html();
						var RLGORT = $(trs[m]).find("td").eq(16).html();
						var LIFNR = $(trs[m]).find("td").eq(17).html();
						var LIKTX = $(trs[m]).find("td").eq(18).html();
						var INBOUND_TYPE = $(trs[m]).find("td").eq(19).html();
						
						rows_[m-1]="{"+"'CUSTOMER_MACD':'"+CUSTOMER_MACD+"',"+"'MATERIAL':'"+MATERIAL+"',"+"'MATERIALDESC':'"+MATERIALDESC+
						"',"+"'IN_QTY':'"+IN_QTY+"',"+"'UNIT':'"+UNIT+"',"+"'FULL_BOX_QTY':'"+FULL_BOX_QTY+
						"',"+"'BOX_COUNT':'"+BOX_COUNT+"',"+"'BIN_CODE':'"+BIN_CODE+"',"+"'BEDNR':'"+BEDNR+
						"',"+"'PRDDT':'"+PRDDT+"',"+"'WH_MANAGER':'"+WH_MANAGER+"',"+"'REMARK':'"+REMARK+"',"+"'RECWERKS':'"+RECWERKS+
						"',"+"'WHNUMBER':'"+WHNUMBER+"',"+"'RLGORT':'"+RLGORT+"',"+"'LIFNR':'"+LIFNR+"',"+"'LIKTX':'"+LIKTX+"',"+
						"'INBOUND_TYPE':'"+INBOUND_TYPE+"'," +"'RN':'"+RN+"'"+
								"}";
					}
				}
				}
			//});
			$.ajax({
				url:baseUrl + "in/wmsinternalbound/save501",
				dataType : "json",
				type : "post",
				data : {
					"ARRLIST":JSON.stringify(rows_)
				},
				async: false,
				success:function(resp){
					if(resp.code == '0'){
						//js.showMessage("保存成功! 进仓单号  "+resp.ininternetbountNo,null,null,1000*60000);
						$("#inboundNoLayer").html(resp.ininternetbountNo);
						$("#inboundNo").val(resp.ininternetbountNo);
						$("#inboundNo1").val(resp.ininternetbountNo);
						$("#inboundNo2").val(resp.ininternetbountNo);
						layer.open({
		            		type : 1,
		            		offset : '50px',
		            		skin : 'layui-layer-molv',
		            		title : "进仓单创建成功",
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
					}else{
						js.alert("保存失败");
						js.showErrorMessage(resp.msg,1000*100);
						$(".btn").attr("disabled", false);
					}
				}
			});
		},
		
		save903:function(){
			$(".btn").attr("disabled", true);
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			//从jqGrid获取选中的行数据
			var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
			
			if(ids.length==0){
				js.alert("请选择要提交的数据!");
				$(".btn").attr("disabled", false);
				return;
			}
			
			if($("#vendor_903").val()==""){
				js.alert("供应商不能为空!");
				$(".btn").attr("disabled", false);
				return;
			}
			
			
			var rows_=[];
			var trs=$("#dataGrid").children("tbody").children("tr");
			//$.each(trs,function(index,tr){
				for(var m=1;m<trs.length;m++){
				var cbx=$(trs[m]).find("td").find("input").attr("type");
				if(cbx!=undefined){	
					var c_checkbox=$(trs[m]).find('input[type=checkbox]');
					var ischecked=$(c_checkbox).is(":checked");
					if(ischecked){
						var RN= $("#dataGrid").jqGrid('getGridParam','selrow');
						var MATERIAL ="";
						if($(trs[m]).find("td").eq(2).find("input").val()!=undefined){
							MATERIAL = $(trs[m]).find("td").eq(2).find("input").val();
						}else{
							MATERIAL = $(trs[m]).find("td").eq(2).html();
						}
						
						var MATERIALDESC = $(trs[m]).find("td").eq(3).html();
						
						var IN_QTY =0;
						if($(trs[m]).find("td").eq(4).find("input").val()!=undefined){
							IN_QTY = $(trs[m]).find("td").eq(4).find("input").val();
						}else{
							IN_QTY = $(trs[m]).find("td").eq(4).html();
						}
						
						var UNIT = $(trs[m]).find("td").eq(5).html();
						
						var FULL_BOX_QTY =0;
						if($(trs[m]).find("td").eq(6).find("input").val()!=undefined){
							FULL_BOX_QTY = $(trs[m]).find("td").eq(6).find("input").val();
						}else{
							FULL_BOX_QTY = $(trs[m]).find("td").eq(6).html();
						}
						
						var BOX_COUNT = $(trs[m]).find("td").eq(7).html();
						var BIN_CODE = $(trs[m]).find("td").eq(8).html();
						
						var BEDNR =""
						if($(trs[m]).find("td").eq(9).find("input").val()!=undefined){
						   BEDNR = $(trs[m]).find("td").eq(9).find("input").val();
						}else{
						   BEDNR=$(trs[m]).find("td").eq(9).html();
						}
						
						var PRDDT ="";
						if($(trs[m]).find("td").eq(10).find("input").val()!=undefined){
							PRDDT =$(trs[m]).find("td").eq(10).find("input").val();
						}else{
							PRDDT = $(trs[m]).find("td").eq(10).html();
						}
						var WH_MANAGER = $(trs[m]).find("td").eq(11).html();
						
						var REMARK ="";
						if($(trs[m]).find("td").eq(12).find("input").val()!=undefined){
							REMARK = $(trs[m]).find("td").eq(12).find("input").val();
						}else{
							REMARK = $(trs[m]).find("td").eq(12).html();
						}
						var RECWERKS = $(trs[m]).find("td").eq(13).html();
						var WHNUMBER = $(trs[m]).find("td").eq(14).html();
						var RLGORT = $(trs[m]).find("td").eq(15).html();
						var LIFNR = $(trs[m]).find("td").eq(16).html();
						var LIKTX = $(trs[m]).find("td").eq(17).html();
						var INBOUND_TYPE = $(trs[m]).find("td").eq(18).html();
						
						rows_[m-1]="{"+"'MATERIAL':'"+MATERIAL+"',"+"'MATERIALDESC':'"+MATERIALDESC+
						"',"+"'IN_QTY':'"+IN_QTY+"',"+"'UNIT':'"+UNIT+"',"+"'FULL_BOX_QTY':'"+FULL_BOX_QTY+
						"',"+"'BOX_COUNT':'"+BOX_COUNT+"',"+"'BIN_CODE':'"+BIN_CODE+"',"+"'BEDNR':'"+BEDNR+
						"',"+"'PRDDT':'"+PRDDT+"',"+"'WH_MANAGER':'"+WH_MANAGER+"',"+"'REMARK':'"+REMARK+"',"+"'RECWERKS':'"+RECWERKS+
						"',"+"'WHNUMBER':'"+WHNUMBER+"',"+"'RLGORT':'"+RLGORT+"',"+"'LIFNR':'"+LIFNR+"',"+"'LIKTX':'"+LIKTX+"',"+
						"'INBOUND_TYPE':'"+INBOUND_TYPE+"'," +"'RN':'"+RN+"'"+
								"}";
					}
				}
				}
			//});
			$.ajax({
				url:baseUrl + "in/wmsinternalbound/save903",
				dataType : "json",
				type : "post",
				data : {
					"ARRLIST":JSON.stringify(rows_)
				},
				async: false,
				success:function(resp){
					if(resp.code == '0'){
						//js.showMessage("保存成功! 进仓单号  "+resp.ininternetbountNo,null,null,1000*60000);
						$("#inboundNoLayer").html(resp.ininternetbountNo);
						$("#inboundNo").val(resp.ininternetbountNo);
						$("#inboundNo1").val(resp.ininternetbountNo);
						$("#inboundNo2").val(resp.ininternetbountNo);
						layer.open({
		            		type : 1,
		            		offset : '50px',
		            		skin : 'layui-layer-molv',
		            		title : "进仓单创建成功",
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
					}else{
						js.alert("保存失败");
						js.showErrorMessage(resp.msg,1000*100);
						$(".btn").attr("disabled", false);
					}
				}
			});
		},
		save511:function(){
			$(".btn").attr("disabled", true);
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			//从jqGrid获取选中的行数据
			var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
			
			if(ids.length==0){
				js.alert("请选择要提交的数据!");
				$(".btn").attr("disabled", false);
				return;
			}
			
			if($("#vendor_511").val()==""){
				js.alert("供应商不能为空!");
				$(".btn").attr("disabled", false);
				return;
			}
			
			
			var rows_=[];
			var trs=$("#dataGrid").children("tbody").children("tr");
			//$.each(trs,function(index,tr){
				for(var m=1;m<trs.length;m++){
				var cbx=$(trs[m]).find("td").find("input").attr("type");
				if(cbx!=undefined){	
					var c_checkbox=$(trs[m]).find('input[type=checkbox]');
					var ischecked=$(c_checkbox).is(":checked");
					if(ischecked){
						var RN= $("#dataGrid").jqGrid('getGridParam','selrow');
						
						var MATERIAL ="";
						if($(trs[m]).find("td").eq(2).find("input").val()!=undefined){
							MATERIAL = $(trs[m]).find("td").eq(2).find("input").val();
						}else{
							MATERIAL = $(trs[m]).find("td").eq(2).html();
						}
						
						var MATERIALDESC = $(trs[m]).find("td").eq(3).html();
						
						var IN_QTY =0;
						if($(trs[m]).find("td").eq(4).find("input").val()!=undefined){
							IN_QTY = $(trs[m]).find("td").eq(4).find("input").val();
						}else{
							IN_QTY = $(trs[m]).find("td").eq(4).html();
						}
						
						var UNIT = $(trs[m]).find("td").eq(5).html();
						
						var FULL_BOX_QTY =0;
						if($(trs[m]).find("td").eq(6).find("input").val()!=undefined){
							FULL_BOX_QTY = $(trs[m]).find("td").eq(6).find("input").val();
						}else{
							FULL_BOX_QTY = $(trs[m]).find("td").eq(6).html();
						}
						
						var BOX_COUNT = $(trs[m]).find("td").eq(7).html();
						var BIN_CODE = $(trs[m]).find("td").eq(8).html();
						
						var BEDNR =""
						if($(trs[m]).find("td").eq(9).find("input").val()!=undefined){
						   BEDNR = $(trs[m]).find("td").eq(9).find("input").val();
						}else{
						   BEDNR=$(trs[m]).find("td").eq(9).html();
						}
						
						var PRDDT ="";
						if($(trs[m]).find("td").eq(10).find("input").val()!=undefined){
							PRDDT =$(trs[m]).find("td").eq(10).find("input").val();
						}else{
							PRDDT = $(trs[m]).find("td").eq(10).html();
						}
						var WH_MANAGER = $(trs[m]).find("td").eq(11).html();
						
						var REMARK ="";
						if($(trs[m]).find("td").eq(12).find("input").val()!=undefined){
							REMARK = $(trs[m]).find("td").eq(12).find("input").val();
						}else{
							REMARK = $(trs[m]).find("td").eq(12).html();
						}
						var RECWERKS = $(trs[m]).find("td").eq(13).html();
						var WHNUMBER = $(trs[m]).find("td").eq(14).html();
						var RLGORT = $(trs[m]).find("td").eq(15).html();
						var LIFNR = $(trs[m]).find("td").eq(16).html();
						var LIKTX = $(trs[m]).find("td").eq(17).html();
						var INBOUND_TYPE = $(trs[m]).find("td").eq(18).html();
						
						rows_[m-1]="{"+"'MATERIAL':'"+MATERIAL+"',"+"'MATERIALDESC':'"+MATERIALDESC+
						"',"+"'IN_QTY':'"+IN_QTY+"',"+"'UNIT':'"+UNIT+"',"+"'FULL_BOX_QTY':'"+FULL_BOX_QTY+
						"',"+"'BOX_COUNT':'"+BOX_COUNT+"',"+"'BIN_CODE':'"+BIN_CODE+"',"+"'BEDNR':'"+BEDNR+
						"',"+"'PRDDT':'"+PRDDT+"',"+"'WH_MANAGER':'"+WH_MANAGER+"',"+"'REMARK':'"+REMARK+"',"+"'RECWERKS':'"+RECWERKS+
						"',"+"'WHNUMBER':'"+WHNUMBER+"',"+"'RLGORT':'"+RLGORT+"',"+"'LIFNR':'"+LIFNR+"',"+"'LIKTX':'"+LIKTX+"',"+
						"'INBOUND_TYPE':'"+INBOUND_TYPE+"'," +"'RN':'"+RN+"'"+
								"}";
					}
				}
				}
			//});
			$.ajax({
				url:baseUrl + "in/wmsinternalbound/save511",
				dataType : "json",
				type : "post",
				data : {
					"ARRLIST":JSON.stringify(rows_)
				},
				async: false,
				success:function(resp){
					if(resp.code == '0'){
						//js.showMessage("保存成功! 进仓单号  "+resp.ininternetbountNo,null,null,1000*60000);
						$("#inboundNoLayer").html(resp.ininternetbountNo);
						$("#inboundNo").val(resp.ininternetbountNo);
						$("#inboundNo1").val(resp.ininternetbountNo);
						$("#inboundNo2").val(resp.ininternetbountNo);
						layer.open({
		            		type : 1,
		            		offset : '50px',
		            		skin : 'layui-layer-molv',
		            		title : "进仓单创建成功",
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
					}else{
						js.alert("保存失败");
						js.showErrorMessage(resp.msg,1000*100);
						$(".btn").attr("disabled", false);
					}
				}
			});
		},
		importOperation511:function(){//免费品进仓511 导入
			$("#werks_511_upload").val(vm.werks);//赋值到页面的form中去，才方便传到后台
			$("#whNumber_511_upload").val(vm.whNumber);
			$("#vendor_511_upload").val($("#vendor_511").val());
			layer.open({
        		type : 1,
        		offset : '50px',
        		skin : 'layui-layer-molv',
        		title : "导入免费品进仓511",
        		area : [ '600px', '250px' ],
        		shade : 0,
        		shadeClose : false,
        		content : jQuery("#import511Layer"),
        		btn : [ '确定','关闭'],
        		btn1 : function(index) {
        			//查询导入的数据并返回
        			var form = new FormData(document.getElementById("uploadForm511"));
        			$.ajax({
        				url:baseUrl + "in/wmsinternalbound/preview511",
        				data:form,
        				type:"post",
        				contentType:false,
        				processData:false,
        				success:function(data){
        					if(data.code == 0){
        						//
        						var currentDate = formatDate(new Date());
        						var werks=vm.werks;
        						var whnumber=vm.whNumber;
        						var inboundType=$("#inboundType").val();
        						var lgort=vm.lgort;
        						var lifnr=$("#vendor_511").val();
        						var liktx=$("#vendorName_511").val();
        						for(var m=0;m<data.data.length;m++){
        						var addtr = '<tr id = "'+trindex+'" role="row" tabindex="-1" class="ui-widget-content jqgrow ui-row-ltr '+((trindex%2 == 0)?"ui-priority-secondary":"")+'" aria-selected = "false">'+
        						'<td role="gridcell" class="ui-state-default jqgrid-rownum" style="text-align:center;width: 30px;" title="1" aria-describedby="dataGrid_rn">' + trindex + '</td>'+
        						'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_cb"><input role="checkbox" type="checkbox" id="jqg_dataGrid_' + trindex +'" class="cbox noselect2" name="jqg_dataGrid_' + trindex +'"></td>'+
        						'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_MATERIAL"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="MATERIAL" id="'+trindex+'_MATERIAL" value="'+data.data[m].MATERIAL+'" type="text" onblur="queryMads511(this)"></td>'+
        						'<td role="gridcell" style="" title="" aria-describedby="dataGrid_MATERIALDESC" >'+data.data[m].MATERIALDESC+'</td>'+
        						'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_IN_QTY"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="IN_QTY" id="'+trindex+'"_IN_QTY value="'+data.data[m].IN_QTY+'" type="text" onblur="queryfullboxqty511(this)"></td>'+
        						'<td role="gridcell" style="" title="" aria-describedby="dataGrid_UNIT">'+data.data[m].UNIT+'</td>'+
        						'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_FULL_BOX_QTY"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="FULL_BOX_QTY" id="'+trindex+'_FULL_BOX_QTY" value="'+data.data[m].FULL_BOX_QTY+'" type="text" onblur="iceil_add511(this)"></td>'+
        						'<td role="gridcell" style="" title="" aria-describedby="dataGrid_BOX_COUNT">'+data.data[m].BOX_COUNT+'</td>'+
        						'<td role="gridcell" style="" title="" aria-describedby="dataGrid_BIN_CODE">'+data.data[m].BIN_CODE+'</td>'+
        						'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_BEDNR"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="BEDNR" id="'+trindex+'_BEDNR" value="'+data.data[m].BEDNR+'" type="text"></td>'+
        						'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_PRDDT"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="PRDDT" id="'+trindex+'_PRDDT" type="text" value="'+currentDate+'" onclick="WdatePicker({dateFmt:\'yyyy-MM-dd\'})"></td>'+
        						'<td role="gridcell" style="" title="" aria-describedby="dataGrid_WH_MANAGER">'+data.data[m].WH_MANAGER+'</td>'+
        						'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_REMARK"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="REMARK" id="'+trindex+'_REMARK" value="'+data.data[m].REMARK+'" type="text"></td>'+
        						'<td role="gridcell" style="" title="" aria-describedby="dataGrid_RECWERKS">'+werks+'</td>'+
        						'<td role="gridcell" style="" title="" aria-describedby="dataGrid_WHNUMBER">'+whnumber+'</td>'+
        						'<td role="gridcell" style="" title="" aria-describedby="dataGrid_RLGORT">'+lgort+'</td>'+
        						'<td role="gridcell" style="" title="" aria-describedby="dataGrid_LIFNR">'+lifnr+'</td>'+
        						'<td role="gridcell" style="" title="" aria-describedby="dataGrid_LIKTX">'+liktx+'</td>'+
        						'<td role="gridcell" style="" title="" aria-describedby="dataGrid_INBOUND_TYPE">'+inboundType+'</td>'+
        						
        						'</tr>'
        						$("#dataGrid tbody").append(addtr);
        						trindex++;
        						}
        						
        						
        						
        					}
        					else{
        					  alert("上传失败," + data.msg);
        					}
        				}.bind(this)
        			});
        			//添加到列表中
        		},
        		btn2 : function(index) {
        			layer.close(index);
        		}
        	});
		},
		onXqggzChange101:function(){
			var xqggh = $("#xqgzh_101").val();
			var rows=$("#dataGrid").jqGrid('getRowData');
			$.each(rows,function(i,row){	
				
			  $("#dataGrid").jqGrid('setCell', i+1, "BEDNR", xqggh);//第二个参数代表行 id，第三个代表 列名称，第四个可以是值或者属性
				
			});
		},
		onXqggzChangeA101:function(){
			var xqggh = $("#xqgzh_A101").val();
			var rows=$("#dataGrid").jqGrid('getRowData');
			$.each(rows,function(i,row){	
				
			  $("#dataGrid").jqGrid('setCell', i+1, "BEDNR", xqggh);//第二个参数代表行 id，第三个代表 列名称，第四个可以是值或者属性
				
			});
		},
		onXqggzChangeA531:function(){
			var xqggh = $("#xqgzh_A531").val();
			var rows=$("#dataGrid").jqGrid('getRowData');
			$.each(rows,function(i,row){	
				
			  $("#dataGrid").jqGrid('setCell', i+1, "BEDNR", xqggh);//第二个参数代表行 id，第三个代表 列名称，第四个可以是值或者属性
				
			});
		},
		save101:function(){
			$(".btn").attr("disabled", true);
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			//从jqGrid获取选中的行数据
			var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
			
			if(ids.length==0){
				js.alert("请选择要提交的数据!");
				$(".btn").attr("disabled", false);
				return;
			}
			var rows = [];
			for(var id in ids){
				var row = $("#dataGrid").jqGrid('getRowData',ids[id]);
				row.RN= $("#dataGrid").jqGrid('getGridParam','selrow');
				rows[id] = row;
				if(row.IN_QTY-row.KJCSL>0){
					js.alert("进仓数量不能大于可进仓数量!");
					$(".btn").attr("disabled", false);
					return;
				}
			}
			$.ajax({
				url:baseUrl + "in/wmsinternalbound/save",
				dataType : "json",
				type : "post",
				data : {
					"ARRLIST":JSON.stringify(rows)
				},
				async: false,
				success:function(resp){
					if(resp.code == '0'){
						//js.showMessage("保存成功! 进仓单号  "+resp.ininternetbountNo,null,null,1000*60000);
						$("#inboundNoLayer").html(resp.ininternetbountNo);
						$("#inboundNo").val(resp.ininternetbountNo);
						$("#inboundNo1").val(resp.ininternetbountNo);
						$("#inboundNo2").val(resp.ininternetbountNo);
						layer.open({
		            		type : 1,
		            		offset : '50px',
		            		skin : 'layui-layer-molv',
		            		title : "进仓单创建成功",
		            		area : [ '600px', '200px' ],
		            		shade : 0,
		            		shadeClose : false,
		            		content : jQuery("#resultLayer"),
		            		btn : [ '确定'],
		            		btn1 : function(index) {
		            			layer.close(index);
		            		}
		            	});
						queryList101();
						$(".btn").attr("disabled", false);
					}else{
						js.showErrorMessage(resp.msg,1000*100);
						$(".btn").attr("disabled", false);
					}
				}
			});
		},
		onXqggzChange531:function(){
			var xqggh = $("#xqgzh_531").val();
			var rows=$("#dataGrid").jqGrid('getRowData');
			$.each(rows,function(i,row){	
				
			  $("#dataGrid").jqGrid('setCell', i+1, "BEDNR", xqggh);//第二个参数代表行 id，第三个代表 列名称，第四个可以是值或者属性
				
			});
		},
		save531:function(){
			$(".btn").attr("disabled", true);
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			//从jqGrid获取选中的行数据
			var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
			
			if(ids.length==0){
				js.alert("请选择要提交的数据!");
				$(".btn").attr("disabled", false);
				return;
			}
			var rows = [];
			for(var id in ids){
				var row = $("#dataGrid").jqGrid('getRowData',ids[id]);
				row.RN= $("#dataGrid").jqGrid('getGridParam','selrow');
				rows[id] = row;
				
				if(row.IN_QTY-row.KJCSL>0){
					js.alert("进仓数量不能大于可进仓数量!");
					$(".btn").attr("disabled", false);
					return;
				}
			}
			$.ajax({
				url:baseUrl + "in/wmsinternalbound/save",
				dataType : "json",
				type : "post",
				data : {
					"ARRLIST":JSON.stringify(rows)
				},
				async: false,
				success:function(resp){
					if(resp.code == '0'){
						//js.showMessage("保存成功! 进仓单号  "+resp.ininternetbountNo,null,null,1000*60000);
						$("#inboundNoLayer").html(resp.ininternetbountNo);
						$("#inboundNo").val(resp.ininternetbountNo);
						$("#inboundNo1").val(resp.ininternetbountNo);
						$("#inboundNo2").val(resp.ininternetbountNo);
						layer.open({
		            		type : 1,
		            		offset : '50px',
		            		skin : 'layui-layer-molv',
		            		title : "进仓单创建成功",
		            		area : [ '600px', '200px' ],
		            		shade : 0,
		            		shadeClose : false,
		            		content : jQuery("#resultLayer"),
		            		btn : [ '确定'],
		            		btn1 : function(index) {
		            			layer.close(index);
		            		}
		            	});
						//queryList531();
						$("#dataGrid").jqGrid("clearGridData", true);
						$(".btn").attr("disabled", false);
					}else{
						js.alert("保存失败");
						js.showErrorMessage(resp.msg,1000*100);
						$(".btn").attr("disabled", false);
					}
				}
			});
		},
		onXqggzChange521:function(){
			var xqggh = $("#xqgzh_521").val();
			var rows=$("#dataGrid").jqGrid('getRowData');
			$.each(rows,function(i,row){	
				
			  $("#dataGrid").jqGrid('setCell', i+1, "BEDNR", xqggh);//第二个参数代表行 id，第三个代表 列名称，第四个可以是值或者属性
				
			});
		},
		save521:function(){
			$(".btn").attr("disabled", true);
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			//从jqGrid获取选中的行数据
			var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
			
			if(ids.length==0){
				js.alert("请选择要提交的数据!");
				$(".btn").attr("disabled", false);
				return;
			}
			
			
			var rows_=[];
			var trs=$("#dataGrid").children("tbody").children("tr");
			//$.each(trs,function(index,tr){
				for(var m=1;m<trs.length;m++){
				var cbx=$(trs[m]).find("td").find("input").attr("type");
				if(cbx!=undefined){	
					var c_checkbox=$(trs[m]).find('input[type=checkbox]');
					var ischecked=$(c_checkbox).is(":checked");
					if(ischecked){
						var RN= $("#dataGrid").jqGrid('getGridParam','selrow');
						var IO_NO ="";
						if($(trs[m]).find("td").eq(2).find("input").val()!=undefined){
							IO_NO = $(trs[m]).find("td").eq(2).find("input").val();
						}else{
							IO_NO = $(trs[m]).find("td").eq(2).html();
						}
						
						if(IO_NO==""){//订单号为空 不能创建
							js.alert("订单号不能为空!");
							$(".btn").attr("disabled", false);
							return false;
						}
						
						var MATERIAL ="";
						if($(trs[m]).find("td").eq(3).find("input").val()!=undefined){
							MATERIAL = $(trs[m]).find("td").eq(3).find("input").val();
						}else{
							MATERIAL = $(trs[m]).find("td").eq(3).html();
						}
						
						var MATERIALDESC = $(trs[m]).find("td").eq(4).html();
						
						var IN_QTY =0;
						if($(trs[m]).find("td").eq(5).find("input").val()!=undefined){
							IN_QTY = $(trs[m]).find("td").eq(5).find("input").val();
						}else{
							IN_QTY = $(trs[m]).find("td").eq(5).html();
						}
						
						var UNIT = $(trs[m]).find("td").eq(6).html();
						
						var FULL_BOX_QTY =0;
						if($(trs[m]).find("td").eq(7).find("input").val()!=undefined){
							FULL_BOX_QTY = $(trs[m]).find("td").eq(7).find("input").val();
						}else{
							FULL_BOX_QTY = $(trs[m]).find("td").eq(7).html();
						}
						
						var BOX_COUNT = $(trs[m]).find("td").eq(8).html();
						var BIN_CODE = $(trs[m]).find("td").eq(9).html();
						
						var BEDNR =""
						if($(trs[m]).find("td").eq(10).find("input").val()!=undefined){
						   BEDNR = $(trs[m]).find("td").eq(10).find("input").val();
						}else{
						   BEDNR=$(trs[m]).find("td").eq(10).html();
						}
						
						var PRDDT ="";
						if($(trs[m]).find("td").eq(11).find("input").val()!=undefined){
							PRDDT =$(trs[m]).find("td").eq(11).find("input").val();
						}else{
							PRDDT = $(trs[m]).find("td").eq(11).html();
						}
						
						var LT_WARE ="";
						if($(trs[m]).find("td").eq(12).find("input").val()!=undefined){
							LT_WARE =$(trs[m]).find("td").eq(12).find("input").val();
						}else{
							LT_WARE = $(trs[m]).find("td").eq(12).html();
						}
						
						var WORKSHOP ="";
						if($(trs[m]).find("td").eq(13).find("input").val()!=undefined){
							WORKSHOP =$(trs[m]).find("td").eq(13).find("input").val();
						}else{
							WORKSHOP = $(trs[m]).find("td").eq(13).html();
						}
						
						var CAR_TYPE ="";
						if($(trs[m]).find("td").eq(14).find("input").val()!=undefined){
							CAR_TYPE =$(trs[m]).find("td").eq(14).find("input").val();
						}else{
							CAR_TYPE = $(trs[m]).find("td").eq(14).html();
						}
						
						var MOULD_NO ="";
						if($(trs[m]).find("td").eq(15).find("input").val()!=undefined){
							MOULD_NO =$(trs[m]).find("td").eq(15).find("input").val();
						}else{
							MOULD_NO = $(trs[m]).find("td").eq(15).html();
						}
						
						var CPTH = $(trs[m]).find("td").eq(16).html();
						
						var ZYY ="";
						if($(trs[m]).find("td").eq(17).find("input").val()!=undefined){
							ZYY =$(trs[m]).find("td").eq(17).find("input").val();
						}else{
							ZYY = $(trs[m]).find("td").eq(17).html();
						}
						
						var DIS_STATION ="";
						if($(trs[m]).find("td").eq(18).find("input").val()!=undefined){
							DIS_STATION =$(trs[m]).find("td").eq(18).find("input").val();
						}else{
							DIS_STATION = $(trs[m]).find("td").eq(18).html();
						}
						
						var WORKGROUP_NO ="";
						if($(trs[m]).find("td").eq(19).find("input").val()!=undefined){
							WORKGROUP_NO =$(trs[m]).find("td").eq(19).find("input").val();
						}else{
							WORKGROUP_NO = $(trs[m]).find("td").eq(19).html();
						}
						
						var WH_MANAGER = $(trs[m]).find("td").eq(20).html();
						
						var REMARK ="";
						if($(trs[m]).find("td").eq(21).find("input").val()!=undefined){
							REMARK = $(trs[m]).find("td").eq(21).find("input").val();
						}else{
							REMARK = $(trs[m]).find("td").eq(21).html();
						}
						var RECWERKS = $(trs[m]).find("td").eq(22).html();
						var WHNUMBER = $(trs[m]).find("td").eq(23).html();
						var RLGORT = $(trs[m]).find("td").eq(24).html();
						var INBOUND_TYPE = $(trs[m]).find("td").eq(25).html();
						
						rows_[m-1]="{"+"'IO_NO':'"+IO_NO+"',"+"'MATERIAL':'"+MATERIAL+"',"+"'MATERIALDESC':'"+MATERIALDESC+
						"',"+"'IN_QTY':'"+IN_QTY+"',"+"'UNIT':'"+UNIT+"',"+"'FULL_BOX_QTY':'"+FULL_BOX_QTY+
						"',"+"'BOX_COUNT':'"+BOX_COUNT+"',"+"'BIN_CODE':'"+BIN_CODE+"',"+"'BEDNR':'"+BEDNR+
						"',"+"'PRDDT':'"+PRDDT+"',"+"'LT_WARE':'"+LT_WARE+"',"+"'WORKSHOP':'"+WORKSHOP+
						"',"+"'CAR_TYPE':'"+CAR_TYPE+"',"+"'MOULD_NO':'"+MOULD_NO+"',"+"'CPTH':'"+CPTH+
						"',"+"'ZYY':'"+ZYY+"',"+"'DIS_STATION':'"+DIS_STATION+"',"+"'WORKGROUP_NO':'"+WORKGROUP_NO+
						"',"+"'WH_MANAGER':'"+WH_MANAGER+"',"+"'REMARK':'"+REMARK+"',"+"'RECWERKS':'"+RECWERKS+
						"',"+"'WHNUMBER':'"+WHNUMBER+"',"+"'RLGORT':'"+RLGORT+"',"+
						"'INBOUND_TYPE':'"+INBOUND_TYPE+"'," +"'RN':'"+RN+"'"+
								"}";
					}
				}
				}
			//});
			$.ajax({
				url:baseUrl + "in/wmsinternalbound/save521",
				dataType : "json",
				type : "post",
				data : {
					"ARRLIST":JSON.stringify(rows_)
				},
				async: false,
				success:function(resp){
					if(resp.code == '0'){
						//js.showMessage("保存成功! 进仓单号  "+resp.ininternetbountNo,null,null,1000*60000);
						$("#inboundNoLayer").html(resp.ininternetbountNo);
						$("#inboundNo").val(resp.ininternetbountNo);
						$("#inboundNo1").val(resp.ininternetbountNo);
						$("#inboundNo2").val(resp.ininternetbountNo);
						layer.open({
		            		type : 1,
		            		offset : '50px',
		            		skin : 'layui-layer-molv',
		            		title : "进仓单创建成功",
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
					}else{
						js.alert(resp.msg);
						$(".btn").attr("disabled", false);
					}
				}
			});
		},
		saveCo101:function(){
			$(".btn").attr("disabled", true);
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			//从jqGrid获取选中的行数据
			var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
			
			if(ids.length==0){
				js.alert("请选择要提交的数据!");
				$(".btn").attr("disabled", false);
				return;
			}
			
			
			var rows_=[];
			var trs=$("#dataGrid").children("tbody").children("tr");
			//$.each(trs,function(index,tr){
				for(var m=1;m<trs.length;m++){
				var cbx=$(trs[m]).find("td").find("input").attr("type");
				if(cbx!=undefined){	
					var c_checkbox=$(trs[m]).find('input[type=checkbox]');
					var ischecked=$(c_checkbox).is(":checked");
					if(ischecked){
						var RN= $("#dataGrid").jqGrid('getGridParam','selrow');
						var IO_NO  = $(trs[m]).find("td").eq(2).html();
						
						var MATERIAL ="";
						if($(trs[m]).find("td").eq(3).find("input").val()!=undefined){
							MATERIAL = $(trs[m]).find("td").eq(3).find("input").val();
						}else{
							MATERIAL = $(trs[m]).find("td").eq(3).html();
						}
						
						var MATERIALDESC = $(trs[m]).find("td").eq(4).html();
						
						var IN_QTY =0;
						if($(trs[m]).find("td").eq(5).find("input").val()!=undefined){
							IN_QTY = $(trs[m]).find("td").eq(5).find("input").val();
						}else{
							IN_QTY = $(trs[m]).find("td").eq(5).html();
						}
						
						var UNIT = $(trs[m]).find("td").eq(6).html();
						
						var FULL_BOX_QTY =0;
						if($(trs[m]).find("td").eq(7).find("input").val()!=undefined){
							FULL_BOX_QTY = $(trs[m]).find("td").eq(7).find("input").val();
						}else{
							FULL_BOX_QTY = $(trs[m]).find("td").eq(7).html();
						}
						
						var BOX_COUNT = $(trs[m]).find("td").eq(8).html();
						var BIN_CODE = $(trs[m]).find("td").eq(9).html();
						
						var BEDNR =""
						if($(trs[m]).find("td").eq(10).find("input").val()!=undefined){
						   BEDNR = $(trs[m]).find("td").eq(10).find("input").val();
						}else{
						   BEDNR=$(trs[m]).find("td").eq(10).html();
						}
						
						var PRDDT ="";
						if($(trs[m]).find("td").eq(11).find("input").val()!=undefined){
							PRDDT =$(trs[m]).find("td").eq(11).find("input").val();
						}else{
							PRDDT = $(trs[m]).find("td").eq(11).html();
						}
						
						var LT_WARE ="";
						if($(trs[m]).find("td").eq(12).find("input").val()!=undefined){
							LT_WARE =$(trs[m]).find("td").eq(12).find("input").val();
						}else{
							LT_WARE = $(trs[m]).find("td").eq(12).html();
						}
						
						var WORKSHOP ="";
						if($(trs[m]).find("td").eq(13).find("input").val()!=undefined){
							WORKSHOP =$(trs[m]).find("td").eq(13).find("input").val();
						}else{
							WORKSHOP = $(trs[m]).find("td").eq(13).html();
						}
						
						var CAR_TYPE ="";
						if($(trs[m]).find("td").eq(14).find("input").val()!=undefined){
							CAR_TYPE =$(trs[m]).find("td").eq(14).find("input").val();
						}else{
							CAR_TYPE = $(trs[m]).find("td").eq(14).html();
						}
						
						var MOULD_NO ="";
						if($(trs[m]).find("td").eq(15).find("input").val()!=undefined){
							MOULD_NO =$(trs[m]).find("td").eq(15).find("input").val();
						}else{
							MOULD_NO = $(trs[m]).find("td").eq(15).html();
						}
						
						var CPTH = $(trs[m]).find("td").eq(16).html();
						
						var ZYY ="";
						if($(trs[m]).find("td").eq(17).find("input").val()!=undefined){
							ZYY =$(trs[m]).find("td").eq(17).find("input").val();
						}else{
							ZYY = $(trs[m]).find("td").eq(17).html();
						}
						
						var DIS_STATION ="";
						if($(trs[m]).find("td").eq(18).find("input").val()!=undefined){
							DIS_STATION =$(trs[m]).find("td").eq(18).find("input").val();
						}else{
							DIS_STATION = $(trs[m]).find("td").eq(18).html();
						}
						
						var WORKGROUP_NO ="";
						if($(trs[m]).find("td").eq(19).find("input").val()!=undefined){
							WORKGROUP_NO =$(trs[m]).find("td").eq(19).find("input").val();
						}else{
							WORKGROUP_NO = $(trs[m]).find("td").eq(19).html();
						}
						
						var WH_MANAGER = $(trs[m]).find("td").eq(20).html();
						
						var REMARK ="";
						if($(trs[m]).find("td").eq(21).find("input").val()!=undefined){
							REMARK = $(trs[m]).find("td").eq(21).find("input").val();
						}else{
							REMARK = $(trs[m]).find("td").eq(21).html();
						}
						var RECWERKS = $(trs[m]).find("td").eq(22).html();
						var WHNUMBER = $(trs[m]).find("td").eq(23).html();
						var RLGORT = $(trs[m]).find("td").eq(24).html();
						var INBOUND_TYPE = $(trs[m]).find("td").eq(25).html();
						
						rows_[m-1]="{"+"'IO_NO':'"+IO_NO+"',"+"'MATERIAL':'"+MATERIAL+"',"+"'MATERIALDESC':'"+MATERIALDESC+
						"',"+"'IN_QTY':'"+IN_QTY+"',"+"'UNIT':'"+UNIT+"',"+"'FULL_BOX_QTY':'"+FULL_BOX_QTY+
						"',"+"'BOX_COUNT':'"+BOX_COUNT+"',"+"'BIN_CODE':'"+BIN_CODE+"',"+"'BEDNR':'"+BEDNR+
						"',"+"'PRDDT':'"+PRDDT+"',"+"'LT_WARE':'"+LT_WARE+"',"+"'WORKSHOP':'"+WORKSHOP+
						"',"+"'CAR_TYPE':'"+CAR_TYPE+"',"+"'MOULD_NO':'"+MOULD_NO+"',"+"'CPTH':'"+CPTH+
						"',"+"'ZYY':'"+ZYY+"',"+"'DIS_STATION':'"+DIS_STATION+"',"+"'WORKGROUP_NO':'"+WORKGROUP_NO+
						"',"+"'WH_MANAGER':'"+WH_MANAGER+"',"+"'REMARK':'"+REMARK+"',"+"'RECWERKS':'"+RECWERKS+
						"',"+"'WHNUMBER':'"+WHNUMBER+"',"+"'RLGORT':'"+RLGORT+"',"+
						"'INBOUND_TYPE':'"+INBOUND_TYPE+"'," +"'RN':'"+RN+"'"+
								"}";
					}
				}
				}
			//});
			$.ajax({
				url:baseUrl + "in/wmsinternalbound/save521",
				dataType : "json",
				type : "post",
				data : {
					"ARRLIST":JSON.stringify(rows_)
				},
				async: false,
				success:function(resp){
					if(resp.code == '0'){
						//js.showMessage("保存成功! 进仓单号  "+resp.ininternetbountNo,null,null,1000*60000);
						$("#inboundNoLayer").html(resp.ininternetbountNo);
						$("#inboundNo").val(resp.ininternetbountNo);
						$("#inboundNo1").val(resp.ininternetbountNo);
						$("#inboundNo2").val(resp.ininternetbountNo);
						layer.open({
		            		type : 1,
		            		offset : '50px',
		            		skin : 'layui-layer-molv',
		            		title : "进仓单创建成功",
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
					}else{
						js.alert("保存失败");
						js.showErrorMessage(resp.msg,1000*100);
						$(".btn").attr("disabled", false);
					}
				}
			});
		},
		save202:function(){
			$(".btn").attr("disabled", true);
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			//从jqGrid获取选中的行数据
			var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
			
			if(ids.length==0){
				js.alert("请选择要提交的数据!");
				$(".btn").attr("disabled", false);
				return;
			}
			
			
			var rows_=[];
			var trs=$("#dataGrid").children("tbody").children("tr");
			//$.each(trs,function(index,tr){
				for(var m=1;m<trs.length;m++){
				var cbx=$(trs[m]).find("td").find("input").attr("type");
				if(cbx!=undefined){	
					var c_checkbox=$(trs[m]).find('input[type=checkbox]');
					var ischecked=$(c_checkbox).is(":checked");
					if(ischecked){
						var RN= $("#dataGrid").jqGrid('getGridParam','selrow');
						
						var COST_CENTER  = $(trs[m]).find("td").eq(2).html();
						
						var MATERIAL ="";
						if($(trs[m]).find("td").eq(3).find("input").val()!=undefined){
							MATERIAL = $(trs[m]).find("td").eq(3).find("input").val();
						}else{
							MATERIAL = $(trs[m]).find("td").eq(3).html();
						}
						
						var MATERIALDESC = $(trs[m]).find("td").eq(4).html();
						
						var IN_QTY =0;
						if($(trs[m]).find("td").eq(5).find("input").val()!=undefined){
							IN_QTY = $(trs[m]).find("td").eq(5).find("input").val();
						}else{
							IN_QTY = $(trs[m]).find("td").eq(5).html();
						}
						
						var UNIT = $(trs[m]).find("td").eq(6).html();
						
						var FULL_BOX_QTY =0;
						if($(trs[m]).find("td").eq(7).find("input").val()!=undefined){
							FULL_BOX_QTY = $(trs[m]).find("td").eq(7).find("input").val();
						}else{
							FULL_BOX_QTY = $(trs[m]).find("td").eq(7).html();
						}
						
						var BOX_COUNT = $(trs[m]).find("td").eq(8).html();
						var BIN_CODE = $(trs[m]).find("td").eq(9).html();
						
						var BEDNR =""
						if($(trs[m]).find("td").eq(10).find("input").val()!=undefined){
						   BEDNR = $(trs[m]).find("td").eq(10).find("input").val();
						}else{
						   BEDNR=$(trs[m]).find("td").eq(10).html();
						}
						
						var PRDDT ="";
						if($(trs[m]).find("td").eq(11).find("input").val()!=undefined){
							PRDDT =$(trs[m]).find("td").eq(11).find("input").val();
						}else{
							PRDDT = $(trs[m]).find("td").eq(11).html();
						}
						
						var LT_WARE ="";
						if($(trs[m]).find("td").eq(12).find("input").val()!=undefined){
							LT_WARE =$(trs[m]).find("td").eq(12).find("input").val();
						}else{
							LT_WARE = $(trs[m]).find("td").eq(12).html();
						}
						
						var WORKSHOP ="";
						if($(trs[m]).find("td").eq(13).find("input").val()!=undefined){
							WORKSHOP =$(trs[m]).find("td").eq(13).find("input").val();
						}else{
							WORKSHOP = $(trs[m]).find("td").eq(13).html();
						}
						
						var CAR_TYPE ="";
						if($(trs[m]).find("td").eq(14).find("input").val()!=undefined){
							CAR_TYPE =$(trs[m]).find("td").eq(14).find("input").val();
						}else{
							CAR_TYPE = $(trs[m]).find("td").eq(14).html();
						}
						
						var MOULD_NO ="";
						if($(trs[m]).find("td").eq(15).find("input").val()!=undefined){
							MOULD_NO =$(trs[m]).find("td").eq(15).find("input").val();
						}else{
							MOULD_NO = $(trs[m]).find("td").eq(15).html();
						}
						
						var CPTH = $(trs[m]).find("td").eq(16).html();
						
						var ZYY ="";
						if($(trs[m]).find("td").eq(17).find("input").val()!=undefined){
							ZYY =$(trs[m]).find("td").eq(17).find("input").val();
						}else{
							ZYY = $(trs[m]).find("td").eq(17).html();
						}
						
						var DIS_STATION ="";
						if($(trs[m]).find("td").eq(18).find("input").val()!=undefined){
							DIS_STATION =$(trs[m]).find("td").eq(18).find("input").val();
						}else{
							DIS_STATION = $(trs[m]).find("td").eq(18).html();
						}
						
						var WORKGROUP_NO ="";
						if($(trs[m]).find("td").eq(19).find("input").val()!=undefined){
							WORKGROUP_NO =$(trs[m]).find("td").eq(19).find("input").val();
						}else{
							WORKGROUP_NO = $(trs[m]).find("td").eq(19).html();
						}
						
						var WH_MANAGER = $(trs[m]).find("td").eq(20).html();
						
						var REMARK ="";
						if($(trs[m]).find("td").eq(21).find("input").val()!=undefined){
							REMARK = $(trs[m]).find("td").eq(21).find("input").val();
						}else{
							REMARK = $(trs[m]).find("td").eq(21).html();
						}
						var RECWERKS = $(trs[m]).find("td").eq(22).html();
						var WHNUMBER = $(trs[m]).find("td").eq(23).html();
						var RLGORT = $(trs[m]).find("td").eq(24).html();
						var INBOUND_TYPE = $(trs[m]).find("td").eq(25).html();
						
						rows_[m-1]="{"+"'COST_CENTER':'"+COST_CENTER+"',"+"'MATERIAL':'"+MATERIAL+"',"+"'MATERIALDESC':'"+MATERIALDESC+
						"',"+"'IN_QTY':'"+IN_QTY+"',"+"'UNIT':'"+UNIT+"',"+"'FULL_BOX_QTY':'"+FULL_BOX_QTY+
						"',"+"'BOX_COUNT':'"+BOX_COUNT+"',"+"'BIN_CODE':'"+BIN_CODE+"',"+"'BEDNR':'"+BEDNR+
						"',"+"'PRDDT':'"+PRDDT+"',"+"'LT_WARE':'"+LT_WARE+"',"+"'WORKSHOP':'"+WORKSHOP+
						"',"+"'CAR_TYPE':'"+CAR_TYPE+"',"+"'MOULD_NO':'"+MOULD_NO+"',"+"'CPTH':'"+CPTH+
						"',"+"'ZYY':'"+ZYY+"',"+"'DIS_STATION':'"+DIS_STATION+"',"+"'WORKGROUP_NO':'"+WORKGROUP_NO+
						"',"+"'WH_MANAGER':'"+WH_MANAGER+"',"+"'REMARK':'"+REMARK+"',"+"'RECWERKS':'"+RECWERKS+
						"',"+"'WHNUMBER':'"+WHNUMBER+"',"+"'RLGORT':'"+RLGORT+"',"+
						"'INBOUND_TYPE':'"+INBOUND_TYPE+"'," +"'RN':'"+RN+"'"+
								"}";
					}
				}
				}
			//});
			$.ajax({
				url:baseUrl + "in/wmsinternalbound/save202",
				dataType : "json",
				type : "post",
				data : {
					"ARRLIST":JSON.stringify(rows_)
				},
				async: false,
				success:function(resp){
					if(resp.code == '0'){
						//js.showMessage("保存成功! 进仓单号  "+resp.ininternetbountNo,null,null,1000*60000);
						$("#inboundNoLayer").html(resp.ininternetbountNo);
						$("#inboundNo").val(resp.ininternetbountNo);
						$("#inboundNo1").val(resp.ininternetbountNo);
						$("#inboundNo2").val(resp.ininternetbountNo);
						layer.open({
		            		type : 1,
		            		offset : '50px',
		            		skin : 'layui-layer-molv',
		            		title : "进仓单创建成功",
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
					}else{
						js.alert("保存失败");
						js.showErrorMessage(resp.msg,1000*100);
						$(".btn").attr("disabled", false);
					}
				}
			});
		},
		save222:function(){

			$(".btn").attr("disabled", true);
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			//从jqGrid获取选中的行数据
			var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
			
			if(ids.length==0){
				js.alert("请选择要提交的数据!");
				$(".btn").attr("disabled", false);
				return;
			}
			
			
			var rows_=[];
			var trs=$("#dataGrid").children("tbody").children("tr");
			//$.each(trs,function(index,tr){
				for(var m=1;m<trs.length;m++){
				var cbx=$(trs[m]).find("td").find("input").attr("type");
				if(cbx!=undefined){	
					var c_checkbox=$(trs[m]).find('input[type=checkbox]');
					var ischecked=$(c_checkbox).is(":checked");
					if(ischecked){
						var RN= $("#dataGrid").jqGrid('getGridParam','selrow');
						var WBS  = $(trs[m]).find("td").eq(2).html();
						
						var MATERIAL ="";
						if($(trs[m]).find("td").eq(3).find("input").val()!=undefined){
							MATERIAL = $(trs[m]).find("td").eq(3).find("input").val();
						}else{
							MATERIAL = $(trs[m]).find("td").eq(3).html();
						}
						
						var MATERIALDESC = $(trs[m]).find("td").eq(4).html();
						
						var IN_QTY =0;
						if($(trs[m]).find("td").eq(5).find("input").val()!=undefined){
							IN_QTY = $(trs[m]).find("td").eq(5).find("input").val();
						}else{
							IN_QTY = $(trs[m]).find("td").eq(5).html();
						}
						
						var UNIT = $(trs[m]).find("td").eq(6).html();
						
						var FULL_BOX_QTY =0;
						if($(trs[m]).find("td").eq(7).find("input").val()!=undefined){
							FULL_BOX_QTY = $(trs[m]).find("td").eq(7).find("input").val();
						}else{
							FULL_BOX_QTY = $(trs[m]).find("td").eq(7).html();
						}
						
						var BOX_COUNT = $(trs[m]).find("td").eq(8).html();
						var BIN_CODE = $(trs[m]).find("td").eq(9).html();
						
						var BEDNR =""
						if($(trs[m]).find("td").eq(10).find("input").val()!=undefined){
						   BEDNR = $(trs[m]).find("td").eq(10).find("input").val();
						}else{
						   BEDNR=$(trs[m]).find("td").eq(10).html();
						}
						
						var PRDDT ="";
						if($(trs[m]).find("td").eq(11).find("input").val()!=undefined){
							PRDDT =$(trs[m]).find("td").eq(11).find("input").val();
						}else{
							PRDDT = $(trs[m]).find("td").eq(11).html();
						}
						
						var LT_WARE ="";
						if($(trs[m]).find("td").eq(12).find("input").val()!=undefined){
							LT_WARE =$(trs[m]).find("td").eq(12).find("input").val();
						}else{
							LT_WARE = $(trs[m]).find("td").eq(12).html();
						}
						
						var WORKSHOP ="";
						if($(trs[m]).find("td").eq(13).find("input").val()!=undefined){
							WORKSHOP =$(trs[m]).find("td").eq(13).find("input").val();
						}else{
							WORKSHOP = $(trs[m]).find("td").eq(13).html();
						}
						
						var CAR_TYPE ="";
						if($(trs[m]).find("td").eq(14).find("input").val()!=undefined){
							CAR_TYPE =$(trs[m]).find("td").eq(14).find("input").val();
						}else{
							CAR_TYPE = $(trs[m]).find("td").eq(14).html();
						}
						
						var MOULD_NO ="";
						if($(trs[m]).find("td").eq(15).find("input").val()!=undefined){
							MOULD_NO =$(trs[m]).find("td").eq(15).find("input").val();
						}else{
							MOULD_NO = $(trs[m]).find("td").eq(15).html();
						}
						
						var CPTH = $(trs[m]).find("td").eq(16).html();
						
						var ZYY ="";
						if($(trs[m]).find("td").eq(17).find("input").val()!=undefined){
							ZYY =$(trs[m]).find("td").eq(17).find("input").val();
						}else{
							ZYY = $(trs[m]).find("td").eq(17).html();
						}
						
						var DIS_STATION ="";
						if($(trs[m]).find("td").eq(18).find("input").val()!=undefined){
							DIS_STATION =$(trs[m]).find("td").eq(18).find("input").val();
						}else{
							DIS_STATION = $(trs[m]).find("td").eq(18).html();
						}
						
						var WORKGROUP_NO ="";
						if($(trs[m]).find("td").eq(19).find("input").val()!=undefined){
							WORKGROUP_NO =$(trs[m]).find("td").eq(19).find("input").val();
						}else{
							WORKGROUP_NO = $(trs[m]).find("td").eq(19).html();
						}
						
						var WH_MANAGER = $(trs[m]).find("td").eq(20).html();
						
						var REMARK ="";
						if($(trs[m]).find("td").eq(21).find("input").val()!=undefined){
							REMARK = $(trs[m]).find("td").eq(21).find("input").val();
						}else{
							REMARK = $(trs[m]).find("td").eq(21).html();
						}
						var RECWERKS = $(trs[m]).find("td").eq(22).html();
						var WHNUMBER = $(trs[m]).find("td").eq(23).html();
						var RLGORT = $(trs[m]).find("td").eq(24).html();
						var INBOUND_TYPE = $(trs[m]).find("td").eq(25).html();
						
						rows_[m-1]="{"+"'WBS':'"+WBS+"',"+"'MATERIAL':'"+MATERIAL+"',"+"'MATERIALDESC':'"+MATERIALDESC+
						"',"+"'IN_QTY':'"+IN_QTY+"',"+"'UNIT':'"+UNIT+"',"+"'FULL_BOX_QTY':'"+FULL_BOX_QTY+
						"',"+"'BOX_COUNT':'"+BOX_COUNT+"',"+"'BIN_CODE':'"+BIN_CODE+"',"+"'BEDNR':'"+BEDNR+
						"',"+"'PRDDT':'"+PRDDT+"',"+"'LT_WARE':'"+LT_WARE+"',"+"'WORKSHOP':'"+WORKSHOP+
						"',"+"'CAR_TYPE':'"+CAR_TYPE+"',"+"'MOULD_NO':'"+MOULD_NO+"',"+"'CPTH':'"+CPTH+
						"',"+"'ZYY':'"+ZYY+"',"+"'DIS_STATION':'"+DIS_STATION+"',"+"'WORKGROUP_NO':'"+WORKGROUP_NO+
						"',"+"'WH_MANAGER':'"+WH_MANAGER+"',"+"'REMARK':'"+REMARK+"',"+"'RECWERKS':'"+RECWERKS+
						"',"+"'WHNUMBER':'"+WHNUMBER+"',"+"'RLGORT':'"+RLGORT+"',"+
						"'INBOUND_TYPE':'"+INBOUND_TYPE+"'," +"'RN':'"+RN+"'"+
								"}";
					}
				}
				}
			//});
			$.ajax({
				url:baseUrl + "in/wmsinternalbound/save222",
				dataType : "json",
				type : "post",
				data : {
					"ARRLIST":JSON.stringify(rows_)
				},
				async: false,
				success:function(resp){
					if(resp.code == '0'){
						//js.showMessage("保存成功! 进仓单号  "+resp.ininternetbountNo,null,null,1000*60000);
						$("#inboundNoLayer").html(resp.ininternetbountNo);
						$("#inboundNo").val(resp.ininternetbountNo);
						$("#inboundNo1").val(resp.ininternetbountNo);
						$("#inboundNo2").val(resp.ininternetbountNo);
						layer.open({
		            		type : 1,
		            		offset : '50px',
		            		skin : 'layui-layer-molv',
		            		title : "进仓单创建成功",
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
					}else{
						js.alert("保存失败");
						js.showErrorMessage(resp.msg,1000*100);
						$(".btn").attr("disabled", false);
					}
				}
			});
		
		},
		save262:function(){
			$(".btn").attr("disabled", true);
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			//从jqGrid获取选中的行数据
			var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
			
			if(ids.length==0){
				js.alert("请选择要提交的数据!");
				$(".btn").attr("disabled", false);
				return;
			}
			
			
			var rows_=[];
			var trs=$("#dataGrid").children("tbody").children("tr");
			//$.each(trs,function(index,tr){
				for(var m=1;m<trs.length;m++){
				var cbx=$(trs[m]).find("td").find("input").attr("type");
				if(cbx!=undefined){	
					var c_checkbox=$(trs[m]).find('input[type=checkbox]');
					var ischecked=$(c_checkbox).is(":checked");
					if(ischecked){
						var RN= $("#dataGrid").jqGrid('getGridParam','selrow');
						var IO_NO  = $(trs[m]).find("td").eq(2).html();
						
						var MATERIAL ="";
						if($(trs[m]).find("td").eq(3).find("input").val()!=undefined){
							MATERIAL = $(trs[m]).find("td").eq(3).find("input").val();
						}else{
							MATERIAL = $(trs[m]).find("td").eq(3).html();
						}
						
						var MATERIALDESC = $(trs[m]).find("td").eq(4).html();
						
						var IN_QTY =0;
						if($(trs[m]).find("td").eq(5).find("input").val()!=undefined){
							IN_QTY = $(trs[m]).find("td").eq(5).find("input").val();
						}else{
							IN_QTY = $(trs[m]).find("td").eq(5).html();
						}
						
						var UNIT = $(trs[m]).find("td").eq(6).html();
						
						var FULL_BOX_QTY =0;
						if($(trs[m]).find("td").eq(7).find("input").val()!=undefined){
							FULL_BOX_QTY = $(trs[m]).find("td").eq(7).find("input").val();
						}else{
							FULL_BOX_QTY = $(trs[m]).find("td").eq(7).html();
						}
						
						var BOX_COUNT = $(trs[m]).find("td").eq(8).html();
						var BIN_CODE = $(trs[m]).find("td").eq(9).html();
						
						var BEDNR =""
						if($(trs[m]).find("td").eq(10).find("input").val()!=undefined){
						   BEDNR = $(trs[m]).find("td").eq(10).find("input").val();
						}else{
						   BEDNR=$(trs[m]).find("td").eq(10).html();
						}
						
						var PRDDT ="";
						if($(trs[m]).find("td").eq(11).find("input").val()!=undefined){
							PRDDT =$(trs[m]).find("td").eq(11).find("input").val();
						}else{
							PRDDT = $(trs[m]).find("td").eq(11).html();
						}
						
						var LT_WARE ="";
						if($(trs[m]).find("td").eq(12).find("input").val()!=undefined){
							LT_WARE =$(trs[m]).find("td").eq(12).find("input").val();
						}else{
							LT_WARE = $(trs[m]).find("td").eq(12).html();
						}
						
						var WORKSHOP ="";
						if($(trs[m]).find("td").eq(13).find("input").val()!=undefined){
							WORKSHOP =$(trs[m]).find("td").eq(13).find("input").val();
						}else{
							WORKSHOP = $(trs[m]).find("td").eq(13).html();
						}
						
						var CAR_TYPE ="";
						if($(trs[m]).find("td").eq(14).find("input").val()!=undefined){
							CAR_TYPE =$(trs[m]).find("td").eq(14).find("input").val();
						}else{
							CAR_TYPE = $(trs[m]).find("td").eq(14).html();
						}
						
						var MOULD_NO ="";
						if($(trs[m]).find("td").eq(15).find("input").val()!=undefined){
							MOULD_NO =$(trs[m]).find("td").eq(15).find("input").val();
						}else{
							MOULD_NO = $(trs[m]).find("td").eq(15).html();
						}
						
						var CPTH = $(trs[m]).find("td").eq(16).html();
						
						var ZYY ="";
						if($(trs[m]).find("td").eq(17).find("input").val()!=undefined){
							ZYY =$(trs[m]).find("td").eq(17).find("input").val();
						}else{
							ZYY = $(trs[m]).find("td").eq(17).html();
						}
						
						var DIS_STATION ="";
						if($(trs[m]).find("td").eq(18).find("input").val()!=undefined){
							DIS_STATION =$(trs[m]).find("td").eq(18).find("input").val();
						}else{
							DIS_STATION = $(trs[m]).find("td").eq(18).html();
						}
						
						var WORKGROUP_NO ="";
						if($(trs[m]).find("td").eq(19).find("input").val()!=undefined){
							WORKGROUP_NO =$(trs[m]).find("td").eq(19).find("input").val();
						}else{
							WORKGROUP_NO = $(trs[m]).find("td").eq(19).html();
						}
						
						var WH_MANAGER = $(trs[m]).find("td").eq(20).html();
						
						var REMARK ="";
						if($(trs[m]).find("td").eq(21).find("input").val()!=undefined){
							REMARK = $(trs[m]).find("td").eq(21).find("input").val();
						}else{
							REMARK = $(trs[m]).find("td").eq(21).html();
						}
						var RECWERKS = vm.werks;//$(trs[m]).find("td").eq(22).html();
						var WHNUMBER = vm.whNumber;//$(trs[m]).find("td").eq(23).html();
						var RLGORT = vm.lgort;//$(trs[m]).find("td").eq(24).html();
						var INBOUND_TYPE = $(trs[m]).find("td").eq(25).html();
						
						rows_[m-1]="{"+"'IO_NO':'"+IO_NO+"',"+"'MATERIAL':'"+MATERIAL+"',"+"'MATERIALDESC':'"+MATERIALDESC+
						"',"+"'IN_QTY':'"+IN_QTY+"',"+"'UNIT':'"+UNIT+"',"+"'FULL_BOX_QTY':'"+FULL_BOX_QTY+
						"',"+"'BOX_COUNT':'"+BOX_COUNT+"',"+"'BIN_CODE':'"+BIN_CODE+"',"+"'BEDNR':'"+BEDNR+
						"',"+"'PRDDT':'"+PRDDT+"',"+"'LT_WARE':'"+LT_WARE+"',"+"'WORKSHOP':'"+WORKSHOP+
						"',"+"'CAR_TYPE':'"+CAR_TYPE+"',"+"'MOULD_NO':'"+MOULD_NO+"',"+"'CPTH':'"+CPTH+
						"',"+"'ZYY':'"+ZYY+"',"+"'DIS_STATION':'"+DIS_STATION+"',"+"'WORKGROUP_NO':'"+WORKGROUP_NO+
						"',"+"'WH_MANAGER':'"+WH_MANAGER+"',"+"'REMARK':'"+REMARK+"',"+"'RECWERKS':'"+RECWERKS+
						"',"+"'WHNUMBER':'"+WHNUMBER+"',"+"'RLGORT':'"+RLGORT+"',"+
						"'INBOUND_TYPE':'"+INBOUND_TYPE+"'," +"'RN':'"+RN+"'"+
								"}";
					}
				}
				}
			//});
			$.ajax({
				url:baseUrl + "in/wmsinternalbound/save262",
				dataType : "json",
				type : "post",
				data : {
					"ARRLIST":JSON.stringify(rows_)
				},
				async: false,
				success:function(resp){
					if(resp.code == '0'){
						//js.showMessage("保存成功! 进仓单号  "+resp.ininternetbountNo,null,null,1000*60000);
						$("#inboundNoLayer").html(resp.ininternetbountNo);
						$("#inboundNo").val(resp.ininternetbountNo);
						$("#inboundNo1").val(resp.ininternetbountNo);
						$("#inboundNo2").val(resp.ininternetbountNo);
						layer.open({
		            		type : 1,
		            		offset : '50px',
		            		skin : 'layui-layer-molv',
		            		title : "进仓单创建成功",
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
					}else{
						js.alert("保存失败");
						js.showErrorMessage(resp.msg,1000*100);
						$(".btn").attr("disabled", false);
					}
				}
			});
		
		
		},
		saveA101:function(){
			$(".btn").attr("disabled", true);
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			//从jqGrid获取选中的行数据
			var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
			
			if(ids.length==0){
				js.alert("请选择要提交的数据!");
				$(".btn").attr("disabled", false);
				return;
			}
			var rows = [];
			for(var id in ids){
				var row = $("#dataGrid").jqGrid('getRowData',ids[id]);
				row.RN= $("#dataGrid").jqGrid('getGridParam','selrow');
				rows[id] = row;
				
				if(row.IN_QTY-row.HX_QTY>0){
					js.alert("进仓数量不能大于还需核销数量!");
					$(".btn").attr("disabled", false);
					return;
				}
			}
			$.ajax({
				url:baseUrl + "in/wmsinternalbound/save",
				dataType : "json",
				type : "post",
				data : {
					"ARRLIST":JSON.stringify(rows)
				},
				async: false,
				success:function(resp){
					if(resp.code == '0'){
						//js.showMessage("保存成功! 进仓单号  "+resp.ininternetbountNo,null,null,1000*60000);
						$("#inboundNoLayer").html(resp.ininternetbountNo);
						$("#inboundNo").val(resp.ininternetbountNo);
						$("#inboundNo1").val(resp.ininternetbountNo);
						$("#inboundNo2").val(resp.ininternetbountNo);
						layer.open({
		            		type : 1,
		            		offset : '50px',
		            		skin : 'layui-layer-molv',
		            		title : "进仓单创建成功",
		            		area : [ '600px', '200px' ],
		            		shade : 0,
		            		shadeClose : false,
		            		content : jQuery("#resultLayer"),
		            		btn : [ '确定'],
		            		btn1 : function(index) {
		            			layer.close(index);
		            		}
		            	});
						queryListA101();
						$(".btn").attr("disabled", false);
					}else{
						js.alert("保存失败");
						js.showErrorMessage(resp.msg,1000*100);
						$(".btn").attr("disabled", false);
					}
				}
			});
		},
		saveA531:function(){
			$(".btn").attr("disabled", true);
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			//从jqGrid获取选中的行数据
			var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
			
			if(ids.length==0){
				js.alert("请选择要提交的数据!");
				$(".btn").attr("disabled", false);
				return;
			}
			var rows = [];
			for(var id in ids){
				var row = $("#dataGrid").jqGrid('getRowData',ids[id]);
				row.RN= $("#dataGrid").jqGrid('getGridParam','selrow');
				rows[id] = row;
				
				if(row.IN_QTY-row.HX_QTY>0){
					js.alert("进仓数量不能大于还需核销数量!");
					$(".btn").attr("disabled", false);
					return;
				}
			}
			$.ajax({
				url:baseUrl + "in/wmsinternalbound/save",
				dataType : "json",
				type : "post",
				data : {
					"ARRLIST":JSON.stringify(rows)
				},
				async: false,
				success:function(resp){
					if(resp.code == '0'){
						//js.showMessage("保存成功! 进仓单号  "+resp.ininternetbountNo,null,null,1000*60000);
						$("#inboundNoLayer").html(resp.ininternetbountNo);
						$("#inboundNo").val(resp.ininternetbountNo);
						$("#inboundNo1").val(resp.ininternetbountNo);
						$("#inboundNo2").val(resp.ininternetbountNo);
						layer.open({
		            		type : 1,
		            		offset : '50px',
		            		skin : 'layui-layer-molv',
		            		title : "进仓单创建成功",
		            		area : [ '600px', '200px' ],
		            		shade : 0,
		            		shadeClose : false,
		            		content : jQuery("#resultLayer"),
		            		btn : [ '确定'],
		            		btn1 : function(index) {
		            			layer.close(index);
		            		}
		            	});
						queryListA531();
						$(".btn").attr("disabled", false);
					}else{
						js.alert("保存失败");
						js.showErrorMessage(resp.msg,1000*100);
						$(".btn").attr("disabled", false);
					}
				}
			});
		},
		savepo101:function(){

			$(".btn").attr("disabled", true);
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			//从jqGrid获取选中的行数据
			var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
			
			if(ids.length==0){
				js.alert("请选择要提交的数据!");
				$(".btn").attr("disabled", false);
				return;
			}
			var rows = [];
			for(var id in ids){
				var row = $("#dataGrid").jqGrid('getRowData',ids[id]);
				row.RN= $("#dataGrid").jqGrid('getGridParam','selrow');
				rows[id] = row;
				
			}
			$.ajax({
				url:baseUrl + "in/wmsinternalbound/save",
				dataType : "json",
				type : "post",
				data : {
					"ARRLIST":JSON.stringify(rows)
				},
				async: false,
				success:function(resp){
					if(resp.code == '0'){
						//js.showMessage("保存成功! 进仓单号  "+resp.ininternetbountNo,null,null,1000*60000);
						$("#inboundNoLayer").html(resp.ininternetbountNo);
						$("#inboundNo").val(resp.ininternetbountNo);
						$("#inboundNo1").val(resp.ininternetbountNo);
						$("#inboundNo2").val(resp.ininternetbountNo);
						layer.open({
		            		type : 1,
		            		offset : '50px',
		            		skin : 'layui-layer-molv',
		            		title : "进仓单创建成功",
		            		area : [ '600px', '200px' ],
		            		shade : 0,
		            		shadeClose : false,
		            		content : jQuery("#resultLayer"),
		            		btn : [ '确定'],
		            		btn1 : function(index) {
		            			layer.close(index);
		            		}
		            	});
						queryListpo101();
						$(".btn").attr("disabled", false);
					}else{
						js.alert("保存失败");
						js.showErrorMessage(resp.msg,1000*100);
						$(".btn").attr("disabled", false);
					}
				}
			});
		
		},
		saveMo262:function(){

			$(".btn").attr("disabled", true);
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			//从jqGrid获取选中的行数据
			var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
			
			if(ids.length==0){
				js.alert("请选择要提交的数据!");
				$(".btn").attr("disabled", false);
				return;
			}
			
			
			var rows_=[];
			var trs=$("#dataGrid").children("tbody").children("tr");
				for(var m=1;m<trs.length;m++){
				var cbx=$(trs[m]).find("td").find("input").attr("type");
				if(cbx!=undefined){	
					var c_checkbox=$(trs[m]).find('input[type=checkbox]');
					var ischecked=$(c_checkbox).is(":checked");
					if(ischecked){
						var RN= $("#dataGrid").jqGrid('getGridParam','selrow');
						var MATERIAL ="";
						if($(trs[m]).find("td").eq(2).find("input").val()!=undefined){
							MATERIAL = $(trs[m]).find("td").eq(2).find("input").val();
						}else{
							MATERIAL = $(trs[m]).find("td").eq(2).html();
						}
						var MATERIALDESC = $(trs[m]).find("td").eq(3).html();
						
						var IN_QTY =0;
						if($(trs[m]).find("td").eq(4).find("input").val()!=undefined){
							IN_QTY = $(trs[m]).find("td").eq(4).find("input").val();
						}else{
							IN_QTY = $(trs[m]).find("td").eq(4).html();
						}
						
						var UNIT = $(trs[m]).find("td").eq(5).html();
						
						var FULL_BOX_QTY =0;
						if($(trs[m]).find("td").eq(6).find("input").val()!=undefined){
							FULL_BOX_QTY = $(trs[m]).find("td").eq(6).find("input").val();
						}else{
							FULL_BOX_QTY = $(trs[m]).find("td").eq(6).html();
						}
						
						var BOX_COUNT = $(trs[m]).find("td").eq(7).html();
						var BIN_CODE = $(trs[m]).find("td").eq(8).html();
						
						var  LGORT=""
						if($(trs[m]).find("td").eq(9).find("input").val()!=undefined){
							LGORT = $(trs[m]).find("td").eq(9).find("input").val();
						}else{
							LGORT=$(trs[m]).find("td").eq(9).html();
						}
						
						var BEDNR =""
						if($(trs[m]).find("td").eq(10).find("input").val()!=undefined){
						   BEDNR = $(trs[m]).find("td").eq(10).find("input").val();
						}else{
						   BEDNR=$(trs[m]).find("td").eq(10).html();
						}
						
						var PRDDT ="";
						if($(trs[m]).find("td").eq(11).find("input").val()!=undefined){
							PRDDT =$(trs[m]).find("td").eq(11).find("input").val();
						}else{
							PRDDT = $(trs[m]).find("td").eq(11).html();
						}
						
						
						
						var WH_MANAGER = $(trs[m]).find("td").eq(12).html();
						
						var REMARK ="";
						if($(trs[m]).find("td").eq(13).find("input").val()!=undefined){
							REMARK = $(trs[m]).find("td").eq(13).find("input").val();
						}else{
							REMARK = $(trs[m]).find("td").eq(13).html();
						}
						
						var RECWERKS = $(trs[m]).find("td").eq(14).html();
						var WHNUMBER = $(trs[m]).find("td").eq(15).html();
						var INBOUND_TYPE = $(trs[m]).find("td").eq(16).html();
						var MO_NO = $(trs[m]).find("td").eq(17).html();
						
						rows_[m-1]="{"+"'MATERIAL':'"+MATERIAL+"',"+"'MATERIALDESC':'"+MATERIALDESC+
						"',"+"'IN_QTY':'"+IN_QTY+"',"+"'UNIT':'"+UNIT+"',"+"'FULL_BOX_QTY':'"+FULL_BOX_QTY+
						"',"+"'BOX_COUNT':'"+BOX_COUNT+"',"+"'BIN_CODE':'"+BIN_CODE+"',"+"'BEDNR':'"+BEDNR+
						"',"+"'PRDDT':'"+PRDDT+
						"',"+"'WH_MANAGER':'"+WH_MANAGER+"',"+"'REMARK':'"+REMARK+"',"+"'RECWERKS':'"+RECWERKS+
						"',"+"'WHNUMBER':'"+WHNUMBER+"',"+"'RLGORT':'"+LGORT+"',"+
						"'INBOUND_TYPE':'"+INBOUND_TYPE+"'," +"'MO_NO':'"+MO_NO+"'," +"'RN':'"+RN+"'"+
								"}";
					}
				}
				}
			$.ajax({
				url:baseUrl + "in/wmsinternalbound/saveMo262",
				dataType : "json",
				type : "post",
				data : {
					"ARRLIST":JSON.stringify(rows_)
				},
				async: false,
				success:function(resp){
					if(resp.code == '0'){
						//js.showMessage("保存成功! 进仓单号  "+resp.ininternetbountNo,null,null,1000*60000);
						$("#inboundNoLayer").html(resp.ininternetbountNo);
						$("#inboundNo").val(resp.ininternetbountNo);
						$("#inboundNo1").val(resp.ininternetbountNo);
						$("#inboundNo2").val(resp.ininternetbountNo);
						layer.open({
		            		type : 1,
		            		offset : '50px',
		            		skin : 'layui-layer-molv',
		            		title : "进仓单创建成功",
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
					}else{
						js.alert(resp.msg);
						$(".btn").attr("disabled", false);
					}
				}
			});
		
		},
		queryCODescByCoNo101:function(){//查询Co订单
			var co_no = $("#co_no101").val();
			if(co_no!=""){
				$.ajax({
					url:baseUrl + "in/wmsinternalbound/listCo101",
					dataType : "json",
					type : "post",
					data : {
						"CO_NO":co_no,
						"WERKS":vm.werks
					},
					async: false,
					success:function(resp){
						if(resp.code == '0'){
							if(resp.dd_type=='04'){//co 订单
							$("#co_desc").val(resp.desc);
							$("#is_co101").val("0");//订单号存在
							}else{
								js.alert("该订单不是CO订单");
								$("#is_co101").val("1");//订单号不存在
							}
						}else{
							js.alert(resp.msg);
							$("#is_co101").val("1");//订单号不存在
						}
					}
				});
			}
		},
		queryCcDescByCostCenter202:function(){//查询成本中心描述
			var cb_no = $("#cb_202").val();
			if(cb_no!=""){
				$.ajax({
					url:baseUrl + "in/wmsinternalbound/listCostCenter202",
					dataType : "json",
					type : "post",
					data : {
						"COST_CENTER":cb_no,
						"WERKS":vm.werks
					},
					async: false,
					success:function(resp){
						if(resp.code == '0'){
							$("#cb_desc").val(resp.desc);
							$("#is_cb202").val("0");//订单号存在
						}else{
							js.alert(resp.msg);
							$("#is_cb202").val("1");//订单号不存在
						}
					}
				});
			}
		},
		queryWBS222:function(){//查询wbs描述
			var wbs_222 = $("#wbs_222").val();
			if(wbs_222!=""){
				$.ajax({
					url:baseUrl + "in/wmsinternalbound/listWBS222",
					dataType : "json",
					type : "post",
					data : {
						"WBS":wbs_222,
						"WERKS":vm.werks
					},
					async: false,
					success:function(resp){
						if(resp.code == '0'){
							$("#wbs_desc").val(resp.desc);
							$("#is_wbs222").val("0");//wbs存在
						}else{
							js.alert(resp.msg);
							$("#is_wbs222").val("1");//wbs不存在
						}
					}
				});
			}
		},
		queryYF262:function(){//查询研发订单描述
			var yf_262 = $("#yf_262").val();
			if(yf_262!=""){
				$.ajax({
					url:baseUrl + "in/wmsinternalbound/listyf262",
					dataType : "json",
					type : "post",
					data : {
						//"IO_NO":yf_262,
						"CO_NO":yf_262,
						"WERKS":vm.werks
					},
					async: false,
					success:function(resp){
						if(resp.code == '0'){
							//if(resp.dd_type=='01'){//研发内部订单
							$("#yf_desc").val(resp.desc);
							$("#is_yf262").val("0");//存在
							/*}else{
								js.alert("该订单不是研发/内部订单");
								$("#is_yf262").val("1");//不存在
							}*/
						}else{
							js.alert(resp.msg);
							$("#is_yf262").val("1");//不存在
						}
					}
				});
			}
		},
		validMo262:function(){//校验生产订单
			var AUFNR_Mo262 = $("#AUFNR_Mo262").val();
			if(AUFNR_Mo262!=""){
				$.ajax({
					url:baseUrl + "in/wmsinternalbound/listMo262",
					dataType : "json",
					type : "post",
					data : {
						"AUFNR":AUFNR_Mo262,
						"WERKS":vm.werks
					},
					async: false,
					success:function(resp){
						if(resp.code == '0'){
							$("#is_mo262").val("1");
						}else{
							js.alert(resp.msg);
							$("#is_mo262").val("0");
						}
					}
				});
			}
		},
		onXqggzChangeCo101:function(){
			var xqggh = $("#xqgzh_co101").val();
			var rows=$("#dataGrid").jqGrid('getRowData');
			$.each(rows,function(i,row){	
				
			  $("#dataGrid").jqGrid('setCell', i+1, "BEDNR", xqggh);//第二个参数代表行 id，第三个代表 列名称，第四个可以是值或者属性
				
			});
		},
		onXqggzChange202:function(){
			var xqggh = $("#xqgzh_202").val();
			var rows=$("#dataGrid").jqGrid('getRowData');
			$.each(rows,function(i,row){	
				
			  $("#dataGrid").jqGrid('setCell', i+1, "BEDNR", xqggh);//第二个参数代表行 id，第三个代表 列名称，第四个可以是值或者属性
				
			});
		},
		onXqggzChange222:function(){
			var xqggh = $("#xqgzh_222").val();
			var rows=$("#dataGrid").jqGrid('getRowData');
			$.each(rows,function(i,row){	
				
			  $("#dataGrid").jqGrid('setCell', i+1, "BEDNR", xqggh);//第二个参数代表行 id，第三个代表 列名称，第四个可以是值或者属性
				
			});
		},
		onXqggzChange262:function(){
			var xqggh = $("#xqgzh_262").val();
			var rows=$("#dataGrid").jqGrid('getRowData');
			$.each(rows,function(i,row){	
				
			  $("#dataGrid").jqGrid('setCell', i+1, "BEDNR", xqggh);//第二个参数代表行 id，第三个代表 列名称，第四个可以是值或者属性
				
			});
		},
		onXqggzChangeMo262:function(){
			var xqggh = $("#xqgzhMo_262").val();
			var rows=$("#dataGrid").jqGrid('getRowData');
			$.each(rows,function(i,row){	
				
			  $("#dataGrid").jqGrid('setCell', i+1, "BEDNR", xqggh);//第二个参数代表行 id，第三个代表 列名称，第四个可以是值或者属性
				
			});
		},
		//添加方法
		newOperation501:function(){

			var currentDate = formatDate(new Date());
			var werks=vm.werks;
			var whnumber=vm.whNumber;
			var inboundType=$("#inboundType").val();
			var lgort=vm.lgort;
			var lifnr=$("#vendor_501").val();
			var liktx=$("#vendorName_501").val();
			var xqgzh=$("#xqgzh_501").val();
            trindex =$("#dataGrid").children("tbody").children("tr").length ;

			var addtr = '<tr id = "'+trindex+'" role="row" tabindex="-1" class="ui-widget-content jqgrow ui-row-ltr '+((trindex%2 == 0)?"ui-priority-secondary":"")+'" aria-selected = "false">'+
			'<td role="gridcell" class="ui-state-default jqgrid-rownum" style="text-align:center;width: 30px;" title="1" aria-describedby="dataGrid_rn">' + trindex + '</td>'+
			'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_cb"><input role="checkbox" type="checkbox" id="jqg_dataGrid_' + trindex +'" class="cbox noselect2" name="jqg_dataGrid_' + trindex +'"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_CUSTOMER_MACD"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="CUSTOMER_MACD" id="'+trindex+'_CUSTOMER_MACD" type="text"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_MATERIAL"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="MATERIAL" id="'+trindex+'_MATERIAL" type="text" onblur="queryMads501(this)"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_MATERIALDESC"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_IN_QTY"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="IN_QTY" id="'+trindex+'_IN_QTY" type="text" onblur="queryfullboxqty501(this)"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_UNIT"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_FULL_BOX_QTY"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="FULL_BOX_QTY" id="'+trindex+'_FULL_BOX_QTY" type="text" onblur="iceil_add501(this)"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_BOX_COUNT"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_BIN_CODE"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_BEDNR"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="BEDNR" id="'+trindex+'_BEDNR" type="text" value="'+xqgzh+'"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_PRDDT"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="PRDDT" id="'+trindex+'_PRDDT" type="text" value="'+currentDate+'" onclick="WdatePicker({dateFmt:\'yyyy-MM-dd\'})"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_WH_MANAGER"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_REMARK"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="REMARK" id="'+trindex+'_REMARK" type="text"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_RECWERKS">'+werks+'</td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_WHNUMBER">'+whnumber+'</td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_RLGORT">'+lgort+'</td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_LIFNR">'+lifnr+'</td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_LIKTX">'+liktx+'</td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_INBOUND_TYPE">'+inboundType+'</td>'+
			
			'</tr>'
			$("#dataGrid tbody").append(addtr);

			

		},
		newOperation903:function () {
			var currentDate = formatDate(new Date());
			var werks=vm.werks;
			var whnumber=vm.whNumber;
			var inboundType=$("#inboundType").val();
			var lgort=vm.lgort;
			var lifnr=$("#vendor_903").val();
			var liktx=$("#vendorName_903").val();
			var xqgzh = $("#xqgzh_903").val();
            trindex =$("#dataGrid").children("tbody").children("tr").length ;

            var addtr = '<tr id = "'+trindex+'" role="row" tabindex="-1" class="ui-widget-content jqgrow ui-row-ltr '+((trindex%2 == 0)?"ui-priority-secondary":"")+'" aria-selected = "false">'+
			'<td role="gridcell" class="ui-state-default jqgrid-rownum" style="text-align:center;width: 30px;" title="1" aria-describedby="dataGrid_rn">' + trindex + '</td>'+
			'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_cb"><input role="checkbox" type="checkbox" id="jqg_dataGrid_' + trindex +'" class="cbox noselect2" name="jqg_dataGrid_' + trindex +'"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_MATERIAL"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="MATERIAL" id="'+trindex+'_MATERIAL" type="text" onblur="queryMads903(this)"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_MATERIALDESC"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_IN_QTY"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="IN_QTY" id="'+trindex+'_IN_QTY" type="text" onblur="queryfullboxqty903(this)"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_UNIT"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_FULL_BOX_QTY"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="FULL_BOX_QTY" id="'+trindex+'_FULL_BOX_QTY" type="text" onblur="iceil_add903(this)"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_BOX_COUNT"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_BIN_CODE"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_BEDNR"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="BEDNR" id="'+trindex+'_BEDNR" type="text" value="'+xqgzh+'"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_PRDDT"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="PRDDT" id="'+trindex+'_PRDDT" type="text" value="'+currentDate+'" onclick="WdatePicker({dateFmt:\'yyyy-MM-dd\'})"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_WH_MANAGER"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_REMARK"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="REMARK" id="'+trindex+'_REMARK" type="text"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_RECWERKS">'+werks+'</td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_WHNUMBER">'+whnumber+'</td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_RLGORT">'+lgort+'</td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_LIFNR">'+lifnr+'</td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_LIKTX">'+liktx+'</td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_INBOUND_TYPE">'+inboundType+'</td>'+
			
			'</tr>'
			$("#dataGrid tbody").append(addtr);
			
		},
		newOperation511:function () {
			var currentDate = formatDate(new Date());
			var werks=vm.werks;
			var whnumber=vm.whNumber;
			var inboundType=$("#inboundType").val();
			var lgort=vm.lgort;
			var lifnr=$("#vendor_511").val();
			var liktx=$("#vendorName_511").val();

            trindex =$("#dataGrid").children("tbody").children("tr").length ;

			var xqgzh_511=$("#xqgzh_511").val();
			
			var addtr = '<tr id = "'+trindex+'" role="row" tabindex="-1" class="ui-widget-content jqgrow ui-row-ltr '+((trindex%2 == 0)?"ui-priority-secondary":"")+'" aria-selected = "false">'+
			'<td role="gridcell" class="ui-state-default jqgrid-rownum" style="text-align:center;width: 30px;" title="1" aria-describedby="dataGrid_rn">' + trindex + '</td>'+
			'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_cb"><input role="checkbox" type="checkbox" id="jqg_dataGrid_' + trindex +'" class="cbox noselect2" name="jqg_dataGrid_' + trindex +'"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_MATERIAL"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="MATERIAL" id="'+trindex+'_MATERIAL" type="text" onblur="queryMads511(this)"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_MATERIALDESC"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_IN_QTY"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="IN_QTY" id="'+trindex+'_IN_QTY" type="text" onblur="queryfullboxqty511(this)"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_UNIT"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_FULL_BOX_QTY"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="FULL_BOX_QTY" id="'+trindex+'_FULL_BOX_QTY" type="text" onblur="iceil_add511(this)"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_BOX_COUNT"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_BIN_CODE"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_BEDNR"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="BEDNR" id="'+trindex+'_BEDNR" type="text" value="'+xqgzh_511+'"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_PRDDT"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="PRDDT" id="'+trindex+'_PRDDT" type="text" value="'+currentDate+'" onclick="WdatePicker({dateFmt:\'yyyy-MM-dd\'})"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_WH_MANAGER"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_REMARK"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="REMARK" id="'+trindex+'_REMARK" type="text"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_RECWERKS">'+werks+'</td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_WHNUMBER">'+whnumber+'</td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_RLGORT">'+lgort+'</td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_LIFNR">'+lifnr+'</td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_LIKTX">'+liktx+'</td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_INBOUND_TYPE">'+inboundType+'</td>'+
			
			'</tr>'
			$("#dataGrid tbody").append(addtr);

			
		},
		newOperation521:function () {
			var currentDate = formatDate(new Date());
			var werks= vm.werks;//$("#werks_521").val();
			var whnumber= vm.whNumber;//$("#whNumber_521").val();
			var inboundType= vm.inbound_type;//$("#inboundType").val();
			var lgort=vm.lgort;//$("#rLgort_521").val();
			var xqgzh=$("#xqgzh_521").val();
            trindex =$("#dataGrid").children("tbody").children("tr").length ;

			var addtr = '<tr id = "'+trindex+'" role="row" tabindex="-1" class="ui-widget-content jqgrow ui-row-ltr '+((trindex%2 == 0)?"ui-priority-secondary":"")+'" aria-selected = "false">'+
			'<td role="gridcell" class="ui-state-default jqgrid-rownum" style="text-align:center;width: 30px;" title="1" aria-describedby="dataGrid_rn">' + trindex + '</td>'+
			'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_cb"><input role="checkbox" type="checkbox" id="jqg_dataGrid_' + trindex +'" class="cbox noselect2" name="jqg_dataGrid_' + trindex +'"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_IO_NO"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="IO_NO" id="'+trindex+'_IO_NO" type="text"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_MATERIAL"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="MATERIAL" id="'+trindex+'_MATERIAL" type="text" onblur="queryMads521(this)"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_MATERIALDESC"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_IN_QTY"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="IN_QTY" id="'+trindex+'_IN_QTY" type="text" onblur="queryfullboxqty521(this)"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_UNIT"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_FULL_BOX_QTY"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="FULL_BOX_QTY" id="'+trindex+'_FULL_BOX_QTY" type="text" onblur="iceil_add521(this)"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_BOX_COUNT"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_BIN_CODE"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_BEDNR"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="BEDNR" id="'+trindex+'_BEDNR" type="text" value="'+xqgzh+'"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_PRDDT"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="PRDDT" id="'+trindex+'_PRDDT" type="text" value="'+currentDate+'" onclick="WdatePicker({dateFmt:\'yyyy-MM-dd\'})"></td>'+

			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_LT_WARE"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="LT_WARE" id="'+trindex+'_LT_WARE" type="text"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_WORKSHOP"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="WORKSHOP" id="'+trindex+'_WORKSHOP" type="text"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_CAR_TYPE"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="CAR_TYPE" id="'+trindex+'_CAR_TYPE" type="text"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_MOULD_NO"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="MOULD_NO" id="'+trindex+'_MOULD_NO" type="text"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_CPTH"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_ZYY"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="ZYY" id="'+trindex+'_ZYY" type="text"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_DIS_STATION"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="DIS_STATION" id="'+trindex+'_DIS_STATION" type="text"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_WORKGROUP_NO"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="WORKGROUP_NO" id="'+trindex+'_WORKGROUP_NO" type="text"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_WH_MANAGER"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_REMARK"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="REMARK" id="'+trindex+'_REMARK" type="text"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_RECWERKS">'+werks+'</td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_WHNUMBER">'+whnumber+'</td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_RLGORT">'+lgort+'</td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_INBOUND_TYPE">'+inboundType+'</td>'+

			'</tr>'
			$("#dataGrid tbody").append(addtr);

		},
		newOperationco101:function () {
			var currentDate = formatDate(new Date());
			var werks=vm.werks;
			var whnumber=vm.whNumber;
			var inboundType=$("#inboundType").val();
			var lgort=vm.lgort;
			var xqgzh=$("#xqgzh_co101").val();
			var co_no101=$("#co_no101").val();//co订单号

            trindex =$("#dataGrid").children("tbody").children("tr").length ;
			if($("#is_co101").val()=="1"){//订单号不存在
				js.alert("该订单号不存在！不能添加");
				return false;
			}

			$("#co_no101").attr("readonly","readonly");//co订单号 设为只读
			//$("#werks_co101").attr("disabled","disabled");//设为不可用
			//$("#whNumber_co101").attr("disabled","disabled");//设为不可用
			//$("#rLgort_co101").attr("disabled","disabled");//设为不可用

			
			var addtr = '<tr id = "'+trindex+'" role="row" tabindex="-1" class="ui-widget-content jqgrow ui-row-ltr '+((trindex%2 == 0)?"ui-priority-secondary":"")+'" aria-selected = "false">'+
			'<td role="gridcell" class="ui-state-default jqgrid-rownum" style="text-align:center;width: 30px;" title="1" aria-describedby="dataGrid_rn">' + trindex + '</td>'+
			'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_cb"><input role="checkbox" type="checkbox" id="jqg_dataGrid_' + trindex +'" class="cbox noselect2" name="jqg_dataGrid_' + trindex +'"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_CO_NO">'+co_no101+'</td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_MATERIAL"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="MATERIAL" id="'+trindex+'_MATERIAL" type="text" onblur="queryMadsCo101(this)"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_MATERIALDESC"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_IN_QTY"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="IN_QTY" id="'+trindex+'_IN_QTY" type="text" onblur="queryfullboxqtyCo101(this)"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_UNIT"></td>'+
            '<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_FULL_BOX_QTY"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="FULL_BOX_QTY" id="'+trindex+'_FULL_BOX_QTY" type="text" onblur="iceil_addCo101(this)"></td>'+
            '<td role="gridcell" style="" title="" aria-describedby="dataGrid_BOX_COUNT"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_BIN_CODE"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_BEDNR"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="BEDNR" id="'+trindex+'_BEDNR" type="text" value="'+xqgzh+'"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_PRDDT"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="PRDDT" id="'+trindex+'_PRDDT" type="text" value="'+currentDate+'" onclick="WdatePicker({dateFmt:\'yyyy-MM-dd\'})"></td>'+
			
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_LT_WARE"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="LT_WARE" id="'+trindex+'_LT_WARE" type="text"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_WORKSHOP"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="WORKSHOP" id="'+trindex+'_WORKSHOP" type="text"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_CAR_TYPE"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="CAR_TYPE" id="'+trindex+'_CAR_TYPE" type="text"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_MOULD_NO"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="MOULD_NO" id="'+trindex+'_MOULD_NO" type="text"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_CPTH"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_ZYY"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="ZYY" id="'+trindex+'_ZYY" type="text"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_DIS_STATION"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="DIS_STATION" id="'+trindex+'_DIS_STATION" type="text"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_WORKGROUP_NO"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="WORKGROUP_NO" id="'+trindex+'_WORKGROUP_NO" type="text"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_WH_MANAGER"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_REMARK"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="REMARK" id="'+trindex+'_REMARK" type="text"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_RECWERKS">'+werks+'</td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_WHNUMBER">'+whnumber+'</td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_RLGORT">'+lgort+'</td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_INBOUND_TYPE">'+inboundType+'</td>'+
			
			'</tr>'
			$("#dataGrid tbody").append(addtr);

			
		},
		newOperation202:function () {
			var currentDate = formatDate(new Date());
			var werks=vm.werks;
			var whnumber=vm.whNumber;
			var inboundType=$("#inboundType").val();
			var lgort=vm.lgort;
			var xqgzh=$("#xqgzh_202").val();
			var cb_202=$("#cb_202").val();//成本中心号

            trindex =$("#dataGrid").children("tbody").children("tr").length ;
			if($("#is_cb202").val()=="1"){//订单号不存在
				js.alert("该订单号不存在！不能添加");
				return false;
			}

			
			$("#cb_202").attr("readonly","readonly");//co订单号 设为只读
			//$("#werks_202").attr("disabled","disabled");//设为不可用
			//$("#whNumber_202").attr("disabled","disabled");//设为不可用
			//$("#rLgort_202").attr("disabled","disabled");//设为不可用

			
			var addtr = '<tr id = "'+trindex+'" role="row" tabindex="-1" class="ui-widget-content jqgrow ui-row-ltr '+((trindex%2 == 0)?"ui-priority-secondary":"")+'" aria-selected = "false">'+
			'<td role="gridcell" class="ui-state-default jqgrid-rownum" style="text-align:center;width: 30px;" title="1" aria-describedby="dataGrid_rn">' + trindex + '</td>'+
			'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_cb"><input role="checkbox" type="checkbox" id="jqg_dataGrid_' + trindex +'" class="cbox noselect2" name="jqg_dataGrid_' + trindex +'"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_COST_CENTER">'+cb_202+'</td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_MATERIAL"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="MATERIAL" id="'+trindex+'_MATERIAL" type="text" onblur="queryMads202(this)"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_MATERIALDESC"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_IN_QTY"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="IN_QTY" id="'+trindex+'_IN_QTY" type="text" onblur="queryfullboxqty202(this)"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_UNIT"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_FULL_BOX_QTY"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="FULL_BOX_QTY" id="'+trindex+'_FULL_BOX_QTY" type="text" onblur="iceil_addCo202(this)"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_BOX_COUNT"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_BIN_CODE"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_BEDNR"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="BEDNR" id="'+trindex+'_BEDNR" type="text" value="'+xqgzh+'"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_PRDDT"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="PRDDT" id="'+trindex+'_PRDDT" type="text" value="'+currentDate+'" onclick="WdatePicker({dateFmt:\'yyyy-MM-dd\'})"></td>'+
			
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_LT_WARE"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="LT_WARE" id="'+trindex+'_LT_WARE" type="text"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_WORKSHOP"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="WORKSHOP" id="'+trindex+'_WORKSHOP" type="text"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_CAR_TYPE"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="CAR_TYPE" id="'+trindex+'_CAR_TYPE" type="text"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_MOULD_NO"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="MOULD_NO" id="'+trindex+'_MOULD_NO" type="text"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_CPTH"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_ZYY"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="ZYY" id="'+trindex+'_ZYY" type="text"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_DIS_STATION"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="DIS_STATION" id="'+trindex+'_DIS_STATION" type="text"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_WORKGROUP_NO"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="WORKGROUP_NO" id="'+trindex+'_WORKGROUP_NO" type="text"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_WH_MANAGER"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_REMARK"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="REMARK" id="'+trindex+'_REMARK" type="text"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_RECWERKS">'+werks+'</td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_WHNUMBER">'+whnumber+'</td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_RLGORT">'+lgort+'</td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_INBOUND_TYPE">'+inboundType+'</td>'+
			
			'</tr>'
			$("#dataGrid tbody").append(addtr);

			
		},
		newOperation222:function(){
			var currentDate = formatDate(new Date());
			var werks=vm.werks;
			var whnumber=vm.whNumber;
			var inboundType=$("#inboundType").val();
			var lgort=vm.lgort;
			var xqgzh=$("#xqgzh_222").val();
			var wbs_222=$("#wbs_222").val();//wbs元素号
            trindex =$("#dataGrid").children("tbody").children("tr").length ;
            if($("#is_wbs222").val()=="1"){//wbs元素号不存在
				js.alert("元素号不存在！不能添加");
				return false;
			}

			
			$("#wbs_222").attr("readonly","readonly");//wbs元素号 设为只读
			//$("#werks_222").attr("disabled","disabled");//设为不可用
			//$("#whNumber_222").attr("disabled","disabled");//设为不可用
			//$("#rLgort_222").attr("disabled","disabled");//设为不可用

			
			var addtr = '<tr id = "'+trindex+'" role="row" tabindex="-1" class="ui-widget-content jqgrow ui-row-ltr '+((trindex%2 == 0)?"ui-priority-secondary":"")+'" aria-selected = "false">'+
			'<td role="gridcell" class="ui-state-default jqgrid-rownum" style="text-align:center;width: 30px;" title="1" aria-describedby="dataGrid_rn">' + trindex + '</td>'+
			'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_cb"><input role="checkbox" type="checkbox" id="jqg_dataGrid_' + trindex +'" class="cbox noselect2" name="jqg_dataGrid_' + trindex +'"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_WBS">'+wbs_222+'</td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_MATERIAL"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="MATERIAL" id="'+trindex+'_MATERIAL" type="text" onblur="queryMads222(this)"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_MATERIALDESC"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_IN_QTY"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="IN_QTY" id="'+trindex+'_IN_QTY" type="text" onblur="queryfullboxqty222(this)"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_UNIT"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_FULL_BOX_QTY"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="FULL_BOX_QTY" id="'+trindex+'_FULL_BOX_QTY" type="text" onblur="iceil_addCo222(this)"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_BOX_COUNT"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_BIN_CODE"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_BEDNR"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="BEDNR" id="'+trindex+'_BEDNR" type="text" value="'+xqgzh+'"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_PRDDT"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="PRDDT" id="'+trindex+'_PRDDT" type="text" value="'+currentDate+'" onclick="WdatePicker({dateFmt:\'yyyy-MM-dd\'})"></td>'+
			
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_LT_WARE"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="LT_WARE" id="'+trindex+'_LT_WARE" type="text"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_WORKSHOP"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="WORKSHOP" id="'+trindex+'_WORKSHOP" type="text"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_CAR_TYPE"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="CAR_TYPE" id="'+trindex+'_CAR_TYPE" type="text"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_MOULD_NO"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="MOULD_NO" id="'+trindex+'_MOULD_NO" type="text"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_CPTH"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_ZYY"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="ZYY" id="'+trindex+'_ZYY" type="text"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_DIS_STATION"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="DIS_STATION" id="'+trindex+'_DIS_STATION" type="text"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_WORKGROUP_NO"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="WORKGROUP_NO" id="'+trindex+'_WORKGROUP_NO" type="text"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_WH_MANAGER"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_REMARK"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="REMARK" id="'+trindex+'_REMARK" type="text"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_RECWERKS">'+werks+'</td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_WHNUMBER">'+whnumber+'</td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_RLGORT">'+lgort+'</td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_INBOUND_TYPE">'+inboundType+'</td>'+
			
			'</tr>'
			$("#dataGrid tbody").append(addtr);

			
		},
		newOperation262:function(){
			var currentDate = formatDate(new Date());
			var werks=vm.werks;
			var whnumber=vm.whNumber;
			var inboundType=$("#inboundType").val();
			var lgort=vm.lgort;
			var xqgzh=$("#xqgzh_262").val();
			var yf_262=$("#yf_262").val();//研发订单

            trindex =$("#dataGrid").children("tbody").children("tr").length ;
            if($("#is_yf262").val()=="1"){//研发订单号不存在
				js.alert("研发订单号不存在！不能添加");
				return false;
			}

			
			$("#yf_262").attr("readonly","readonly");//研发订单号 设为只读
			//$("#werks_262").attr("disabled","disabled");//设为不可用
			//$("#whNumber_262").attr("disabled","disabled");//设为不可用
			//$("#rLgort_262").attr("disabled","disabled");//设为不可用

			
			var addtr = '<tr id = "'+trindex+'" role="row" tabindex="-1" class="ui-widget-content jqgrow ui-row-ltr '+((trindex%2 == 0)?"ui-priority-secondary":"")+'" aria-selected = "false">'+
			'<td role="gridcell" class="ui-state-default jqgrid-rownum" style="text-align:center;width: 30px;" title="1" aria-describedby="dataGrid_rn">' + trindex + '</td>'+
			'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_cb"><input role="checkbox" type="checkbox" id="jqg_dataGrid_' + trindex +'" class="cbox noselect2" name="jqg_dataGrid_' + trindex +'"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_IO_NO">'+yf_262+'</td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_MATERIAL"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="MATERIAL" id="'+trindex+'_MATERIAL" type="text" onblur="queryMads262(this)"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_MATERIALDESC"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_IN_QTY"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="IN_QTY" id="'+trindex+'_IN_QTY" type="text" onblur="queryfullboxqty262(this)"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_UNIT"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_FULL_BOX_QTY"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="FULL_BOX_QTY" id="'+trindex+'_FULL_BOX_QTY" type="text" onblur="iceil_addCo262(this)"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_BOX_COUNT"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_BIN_CODE"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_BEDNR"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="BEDNR" id="'+trindex+'_BEDNR" type="text" value="'+xqgzh+'"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_PRDDT"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="PRDDT" id="'+trindex+'_PRDDT" type="text" value="'+currentDate+'" onclick="WdatePicker({dateFmt:\'yyyy-MM-dd\'})"></td>'+
			
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_LT_WARE"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="LT_WARE" id="'+trindex+'_LT_WARE" type="text"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_WORKSHOP"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="WORKSHOP" id="'+trindex+'_WORKSHOP" type="text"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_CAR_TYPE"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="CAR_TYPE" id="'+trindex+'_CAR_TYPE" type="text"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_MOULD_NO"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="MOULD_NO" id="'+trindex+'_MOULD_NO" type="text"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_CPTH"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_ZYY"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="ZYY" id="'+trindex+'_ZYY" type="text"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_DIS_STATION"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="DIS_STATION" id="'+trindex+'_DIS_STATION" type="text"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_WORKGROUP_NO"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="WORKGROUP_NO" id="'+trindex+'_WORKGROUP_NO" type="text"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_WH_MANAGER"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_REMARK"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="REMARK" id="'+trindex+'_REMARK" type="text"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_RECWERKS">'+werks+'</td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_WHNUMBER">'+whnumber+'</td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_RLGORT">'+lgort+'</td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_INBOUND_TYPE">'+inboundType+'</td>'+
			
			'</tr>'
			$("#dataGrid tbody").append(addtr);

			
		},
		//生产订单原材料进仓(262)
		newOperationMo262:function () {

			var currentDate = formatDate(new Date());
			var werks= vm.werks;//$("#werks_521").val();
			var whnumber= vm.whNumber;//$("#whNumber_521").val();
			var inboundType= vm.inbound_type;//$("#inboundType").val();
			var lgort=vm.lgort;//$("#rLgort_521").val();
			var xqgzh=$("#xqgzhMo_262").val();
			
			var mo_no=$("#AUFNR_Mo262").val();
			if(mo_no==""){
				js.alert("生产订单号不能为空");
				return false;
			}
            trindex =$("#dataGrid").children("tbody").children("tr").length;

			if($("#is_mo262").val()!="1"){
				js.alert("生产订单号不正确！");
				return false;
			}
			
			var addtr = '<tr id = "'+trindex+'" role="row" tabindex="-1" class="ui-widget-content jqgrow ui-row-ltr '+((trindex%2 == 0)?"ui-priority-secondary":"")+'" aria-selected = "false">'+
			'<td role="gridcell" class="ui-state-default jqgrid-rownum" style="text-align:center;width: 30px;" title="1" aria-describedby="dataGrid_rn">' + trindex + '</td>'+
			'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_cb"><input role="checkbox" type="checkbox" id="jqg_dataGrid_' + trindex +'" class="cbox noselect2" name="jqg_dataGrid_' + trindex +'"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_MATERIAL"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="MATERIAL" id="'+trindex+'_MATERIAL" type="text" onblur="queryMadsMo262(this)"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_MATERIALDESC"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_IN_QTY"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="IN_QTY" id="'+trindex+'_IN_QTY" type="text" onblur="queryfullboxqtyMo262(this)"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_UNIT"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_FULL_BOX_QTY"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="FULL_BOX_QTY" id="'+trindex+'_FULL_BOX_QTY" type="text" onblur="iceil_addmo262(this)"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_BOX_COUNT"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_BIN_CODE"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_LGORT"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="LGORT" id="'+trindex+'_LGORT" type="text" value="'+lgort+'"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_BEDNR"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="BEDNR" id="'+trindex+'_BEDNR" type="text" value="'+xqgzh+'"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_PRDDT"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="PRDDT" id="'+trindex+'_PRDDT" type="text" value="'+currentDate+'" onclick="WdatePicker({dateFmt:\'yyyy-MM-dd\'})"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_WH_MANAGER"></td>'+
			'<td tabindex="0" class="edit-cell ui-state-highlight" role="gridcell" style="" title="" aria-describedby="dataGrid_REMARK"><input role="textbox" style="width: 99.9%;" rowid="'+trindex+'" name="REMARK" id="'+trindex+'_REMARK" type="text"></td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_RECWERKS">'+werks+'</td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_WHNUMBER">'+whnumber+'</td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_INBOUND_TYPE">'+inboundType+'</td>'+
			'<td role="gridcell" style="" title="" aria-describedby="dataGrid_MO_NO">'+mo_no+'</td>'+
			
			'</tr>'
			$("#dataGrid tbody").append(addtr);
			
		
		},
		//统一删除方法
		btn_delete_n:function(){

			var trs=$("#dataGrid").children("tbody").children("tr");
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

		},
		//查询方法
		queryinteralbound:function(){
			var pzh=$("#pz303").val();
			var pzhyear=$("#pzyear").val();
			if(pzh==""||pzhyear==""){
				js.alert("凭证号或者凭证年份不能为空！");
				return false;
			}
			queryList();
		},
		queryinteralboundZ25:function(){
			var pzh=$("#pz303Z25").val();
			var pzhyear=$("#prddtZ25").val();
			if(pzh==""||pzhyear==""){
				js.alert("凭证号或者凭证年份不能为空！");
				return false;
			}
			queryListZ25();
		},
		queryinteralbound531:function(){
			var aufnr531=$("#AUFNR_531").val();
			if(aufnr531==""){
				js.alert("生产订单号不能为空！");
				return false;
			}
			queryList531();
		},
		queryinteralbound101:function(){
			var aufnr101=$("#AUFNR_101").val();
			if(aufnr101==""){
				js.alert("生产订单号不能为空！");
				return false;
			}
			queryList101();

		},
		queryinteralboundA101:function(){
			var aufnrA101=$("#AUFNR_A101").val();
			if(aufnrA101==""){
				js.alert("生产订单号不能为空！");
				return false;
			}
			queryListA101();

		},
		queryinteralboundA531:function(){
			var aufnrA531=$("#AUFNR_A531").val();
			if(aufnrA531==""){
				js.alert("生产订单号不能为空！");
				return false;
			}
			queryListA531();

		},
		queryinteralboundpo101:function(){
			var pono101=$("#PONO_101").val();
			if(pono101==""){
				js.alert("采购订单号不能为空！");
				return false;
			}
			queryListpo101();

		},
		printInInternalbound:function(){
			$("#printButton").click();
		},
		printInInternalboundBigList:function(){
			$("#printButton1").click();
			},
		printInInternalboundSmallList:function(){//自制小letter打印
			$("#subInBoundType1").val("02");
			$("#printButton2").click();
			},
		queryDetail:function(){//
			js.addTabPage(null, '<i class="fa fa-plus" style="font-size:12px"> 进仓单明细</i>',
					'wms/query/inboundDetailQuery.html?inboundNo='+$("#inboundNo").val()+'&werks='+$("#werks").val()+'&whNumber='+$("#whNumber").val()+'&inBoundType=01', 
							true, true);
			},
		productNoQuery:function(){
			openProductNoWindow();
			},
		initDataGrid:function(){},
        queryMatInfo:function(mat,callback){
            var werks = $("#werks").val();
            var whNumber = $("#whNumber").val();
            /*console.info("werks--->" + werks);
            console.info("whNumber--->" + whNumber);
            console.info("mat===>>> "+mat);*/
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
        }
	}
});

function queryList(){
	$(".btn").attr("disabled", true);
	$.ajax({
		url:baseURL+"in/wmsinternalbound/list",
		dataType : "json",
		type : "post",
		data : {
			"whNumber":vm.whNumber,
			"pz303":$("#pz303").val(),
			"pzyear":$("#pzyear").val(),
			"werks":vm.werks,
			"rLgort":vm.lgort,
			"vendor":$("#vendor").val(),
			"xqgzh":$("#xqgzh").val(),
			"prddt":$("#prddt").val(),
			"inbound_type":$("#inboundType").val()
		},
		async: true,
		success: function (response) { 
			$("#dataGrid").jqGrid("clearGridData", true);
			listdata.length=0;
			
			if(response.code == "0"){
				
				if(response.retMsg.retMsg!=null){
					
					js.showMessage(response.retMsg.retMsg,null,null,1000*60000);
					
				}else{
					listdata = response.result;
					
					$('#dataGrid').jqGrid('setGridParam',{data: listdata}).trigger( 'reloadGrid' );
				}
				$(".btn").attr("disabled", false);
			}else{
				alert(response.retMsg)
				$(".btn").attr("disabled", false);
			}
		}
	});
	
	

}

function queryListZ25(){
	$(".btn").attr("disabled", true);
	$.ajax({
		url:baseURL+"in/wmsinternalbound/list",
		dataType : "json",
		type : "post",
		data : {
			"whNumber":vm.whNumber,
			"pz303":$("#pz303Z25").val(),
			"pzyear":$("#pzyearZ25").val(),
			"werks":vm.werks,
			"rLgort":vm.lgort,
			//"vendor":$("#vendor").val(),
			"xqgzh":$("#xqgzhZ25").val(),
			"prddt":$("#prddtZ25").val(),
			"inbound_type":$("#inboundType").val()
		},
		async: true,
		success: function (response) { 
			$("#dataGrid").jqGrid("clearGridData", true);
			listdata.length=0;
			
			if(response.code == "0"){
				
				if(response.retMsg.retMsg!=null){
					
					js.showMessage(response.retMsg.retMsg,null,null,1000*60000);
					
				}else{
					listdata = response.result;
					
					$('#dataGrid').jqGrid('setGridParam',{data: listdata}).trigger( 'reloadGrid' );
				}
				$(".btn").attr("disabled", false);
			}else{
				alert(response.retMsg)
				$(".btn").attr("disabled", false);
			}
			
		}
	});
	
	

}

function queryList101(){
	$(".btn").attr("disabled", true);
	$.ajax({
		url:baseURL+"in/wmsinternalbound/listMo101",
		dataType : "json",
		type : "post",
		data : {
			"AUFNR":$("#AUFNR_101").val(),
			"WHNUMBER":vm.whNumber, //$("#whNumber_101").val(),
			"WERKS":vm.werks,//$("#werks_101").val(),
			"rLgort":vm.lgort,//$("#rLgort_101").val(),
			"BEDNR":$("#xqgzh_101").val(),
			"inbound_type":$("#inboundType").val(),
			"LIFNR":$("#vendor_101").val(),
			"LIKTX":$("#vendorName_101").val()
		},
		async: true,
		success: function (response) { 
			$("#dataGrid").jqGrid("clearGridData", true);
			listdata.length=0;
			
			if(response.code == "0"){
				
					listdata = response.result;
					
				$('#dataGrid').jqGrid('setGridParam',{data: listdata}).trigger( 'reloadGrid' );
				
				$(".btn").attr("disabled", false);
			}else{
				alert(response.msg)
				$(".btn").attr("disabled", false);
			}
		}
	});
	
}


function queryList531(){
	$(".btn").attr("disabled", true);
	$.ajax({
		url:baseURL+"in/wmsinternalbound/listMo531",
		dataType : "json",
		type : "post",
		data : {
			"AUFNR":$("#AUFNR_531").val(),
			"WHNUMBER":vm.whNumber, //$("#whNumber_101").val(),
			"WERKS":vm.werks,//$("#werks_101").val(),
			"rLgort":vm.lgort,//$("#rLgort_101").val(),
			"BEDNR":$("#xqgzh_531").val(),
			"inbound_type":$("#inboundType").val()
		},
		async: true,
		success: function (response) { 
			$("#dataGrid").jqGrid("clearGridData", true);
			listdata.length=0;
			
			if(response.code == "0"){
				
					listdata = response.result;
					
				$('#dataGrid').jqGrid('setGridParam',{data: listdata}).trigger( 'reloadGrid' );
				
				$(".btn").attr("disabled", false);
			}else{
				alert(response.msg)
				$(".btn").attr("disabled", false);
			}
		}
	});
	
}

function queryListPo531(){
	$('#dataGrid_product_matnr').jqGrid("saveCell", lastrow, lastcell);
	var rows = $("#dataGrid_product_matnr").jqGrid('getRowData');
	
	$.ajax({
		url:baseURL+"in/wmsinternalbound/listMo531",
		dataType : "json",
		type : "post",
		data : {
			"ARRLIST":JSON.stringify(rows),
			"WHNUMBER":vm.whNumber, //$("#whNumber_101").val(),
			"WERKS":vm.werks,//$("#werks_101").val(),
			"rLgort":vm.lgort,//$("#rLgort_101").val(),
			"BEDNR":$("#xqgzh_531").val(),
			"inbound_type":$("#inboundType").val()
		},
		async: true,
		success: function (response) { 
			$("#dataGrid").jqGrid("clearGridData", true);
			listdata.length=0;
			
			if(response.code == "0"){
				
					listdata = response.result;
					
				$('#dataGrid').jqGrid('setGridParam',{data: listdata}).trigger( 'reloadGrid' );
				
				$(".btn").attr("disabled", false);
			}else{
				alert(response.msg)
				$(".btn").attr("disabled", false);
			}
		}
	});
	
}

function queryListA101(){
	$(".btn").attr("disabled", true);
	$.ajax({
		url:baseURL+"in/wmsinternalbound/listA101",
		dataType : "json",
		type : "post",
		data : {
			"AUFNR":$("#AUFNR_A101").val(),
			"WHNUMBER":vm.whNumber,
			"WERKS":vm.werks,
			"rLgort":vm.lgort,
			"BEDNR":$("#xqgzh_A101").val(),
			"inbound_type":$("#inboundType").val()
		},
		async: true,
		success: function (response) { 
			$("#dataGrid").jqGrid("clearGridData", true);
			listdata.length=0;
			
			if(response.code == "0"){
				
				listdata = response.result;
					
				$('#dataGrid').jqGrid('setGridParam',{data: listdata}).trigger( 'reloadGrid' );
				
				$(".btn").attr("disabled", false);
			}else{
				alert(response.msg)
				$(".btn").attr("disabled", false);
			}
		}
	});
	
}

function queryListA531(){
	$(".btn").attr("disabled", true);
	$.ajax({
		url:baseURL+"in/wmsinternalbound/listA531",
		dataType : "json",
		type : "post",
		data : {
			"AUFNR":$("#AUFNR_A531").val(),
			"WHNUMBER":vm.whNumber,
			"WERKS":vm.werks,
			"rLgort":vm.lgort,
			"BEDNR":$("#xqgzh_A531").val(),
			"inbound_type":$("#inboundType").val()
		},
		async: true,
		success: function (response) { 
			$("#dataGrid").jqGrid("clearGridData", true);
			listdata.length=0;
			
			if(response.code == "0"){
				
				listdata = response.result;
					
				$('#dataGrid').jqGrid('setGridParam',{data: listdata}).trigger( 'reloadGrid' );
				
				$(".btn").attr("disabled", false);
			}else{
				alert(response.msg)
				$(".btn").attr("disabled", false);
			}
		}
	});
	
}

function queryListpo101(){
	$(".btn").attr("disabled", true);
	$.ajax({
		url:baseURL+"in/wmsinternalbound/listpo101",
		dataType : "json",
		type : "post",
		data : {
			"PO_NO":$("#PONO_101").val(),
			"WHNUMBER":vm.whNumber,
			"PRDDT":$("#prddt_po101").val(),
			"WERKS":vm.werks,
			"inbound_type":$("#inboundType").val()
		},
		async: true,
		success: function (response) { 
			$("#dataGrid").jqGrid("clearGridData", true);
			listdata.length=0;
			
			if(response.code == "0"){
				
				listdata = response.result;
					
				$('#dataGrid').jqGrid('setGridParam',{data: listdata}).trigger( 'reloadGrid' );
				
				$(".btn").attr("disabled", false);
			}else{
				alert(response.msg)
				$(".btn").attr("disabled", false);
			}
		}
	});
	
}



//库位修改
$("#btnupdatelgort").click(function () {});

$("#btn_delete").click(function () {
	var trs=$("#dataGrid").children("tbody").children("tr");
	var reIndex = 1;
	$.each(trs,function(index,tr){
		var td = $(tr).find("td").eq(0);
		var td_check = $(tr).find("td").eq(1).find("input");
		if(index > 0){
			//if(tr.getAttribute("aria-selected")){
			var ischecked=$(td_check).is(":checked");
			if(ischecked){
				//$(td_check).attr('checked', false);
				//$('#dataGrid').jqGrid('delRowData',index);
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
});

$("#btn_delete903").click(function () {
	var trs=$("#dataGrid").children("tbody").children("tr");
	var reIndex = 1;
	$.each(trs,function(index,tr){
		var td = $(tr).find("td").eq(0);
		var td_check = $(tr).find("td").eq(1).find("input");
		if(index > 0){
			//if(tr.getAttribute("aria-selected")){
			var ischecked=$(td_check).is(":checked");
			if(ischecked){
				//$(td_check).attr('checked', false);
				//$('#dataGrid').jqGrid('delRowData',index);
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
});

$("#btn_delete511").click(function () {
	var trs=$("#dataGrid").children("tbody").children("tr");
	var reIndex = 1;
	$.each(trs,function(index,tr){
		var td = $(tr).find("td").eq(0);
		var td_check = $(tr).find("td").eq(1).find("input");
		if(index > 0){
			//if(tr.getAttribute("aria-selected")){
			var ischecked=$(td_check).is(":checked");
			if(ischecked){
				//$(td_check).attr('checked', false);
				//$('#dataGrid').jqGrid('delRowData',index);
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
});

$("#btn_delete101").click(function () {
	var trs=$("#dataGrid").children("tbody").children("tr");
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
});

$("#btn_delete531").click(function () {
	var trs=$("#dataGrid").children("tbody").children("tr");
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
});

$("#btn_delete521").click(function () {
	var trs=$("#dataGrid").children("tbody").children("tr");
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
});

$("#btn_deleteco101").click(function () {
	var trs=$("#dataGrid").children("tbody").children("tr");
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
});

$("#btn_delete202").click(function () {
	var trs=$("#dataGrid").children("tbody").children("tr");
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
});

$("#btn_delete222").click(function () {});

function iceil_add(e){
	//件数=进仓数量/装箱数量
	if($(e).val().trim()==""){
		js.alert("进仓数量不能为空！");
		return false;
	}
	if($(e).val().trim()==0){
		js.alert("进仓数量不能为0！");
		return false;
	}

	$(e).parent("td").next().next().next().html(Math.ceil($(e).val().trim()/$(e).parent("td").next().next().html()));
}

function iceil_add2(e){
	//件数=进仓数量/装箱数量
	if($(e).val().trim()==""){
		js.alert("装箱数量不能为空！");
		return false;
	}
	if($(e).val().trim()==0){
		js.alert("装箱数量不能为0！");
		return false;
	}
	$(e).parent("td").next().html(Math.ceil($(e).parent("td").prev().prev().html()/$(e).val().trim()));
}
/**
 * 查询物料描述和单位
 * @param e
 * @returns {Boolean}
 */
function queryMads501(e){
	if($(e).val().trim()==""){
		js.alert("物料号不能为空！");
		return false;
	}
	var werks=vm.werks;
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
        		$(e).parent("td").next().next().next().html(data.unit);
        		
        		$(e).parent("td").next().next().next().next().find("input").val(data.fullBoxQty_d_n);
        		
        		//装箱数量
        		if(data.fullBoxQty_d_n>0){//如果装箱数量大于0，则该框不允许修改
        			$(e).parent("td").next().next().next().next().find("input").attr("disabled","disabled");
        		}
        	}
        }
	});
	//查询储位
	var whNumber=vm.whNumber;
	$.ajax({
        url: baseURL + "in/wmsinternalbound/querybincode",
        dataType : "json",
		type : "post",
        data: {
        	"werks":werks,
        	"whNumber":whNumber,
			"material":$(e).val().trim()
        },
        async:false,
        success: function(data){
        	if(data.code == 0){
        		$(e).parent("td").next().next().next().next().next().next().html(data.bin_code);
        	}
        }
	});
	//查询仓管员
	var whNumber=vm.whNumber;
	$.ajax({
        url: baseURL + "in/wmsinternalbound/querywhmanager",
        dataType : "json",
		type : "post",
        data: {
        	"werks":werks,
        	"whNumber":whNumber,
			"material":$(e).val().trim()
        },
        async:false,
        success: function(data){
        	if(data.code == 0){
        		$(e).parent("td").next().next().next().next().next().next().next().next().next().html(data.wh_manager);
        	}
        }
	})
	
}


function queryMads903(e){
	if($(e).val().trim()==""){
		//js.alert("物料号不能为空！");
		return false;
	}
	var werks=vm.werks;
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
        		$(e).parent("td").next().next().next().html(data.unit);
        		
        		$(e).parent("td").next().next().next().next().find("input").val(data.fullBoxQty_d_n);
        	}
        }
	});
	//查询储位
	var whNumber=vm.whNumber;
	$.ajax({
        url: baseURL + "in/wmsinternalbound/querybincode",
        dataType : "json",
		type : "post",
        data: {
        	"werks":werks,
        	"whNumber":whNumber,
			"material":$(e).val().trim()
        },
        async:false,
        success: function(data){
        	if(data.code == 0){
        		$(e).parent("td").next().next().next().next().next().next().html(data.bin_code);
        	}
        }
	});
	//查询仓管员
	var whNumber=vm.whNumber;
	$.ajax({
        url: baseURL + "in/wmsinternalbound/querywhmanager",
        dataType : "json",
		type : "post",
        data: {
        	"werks":werks,
        	"whNumber":whNumber,
			"material":$(e).val().trim()
        },
        async:false,
        success: function(data){
        	if(data.code == 0){
        		$(e).parent("td").next().next().next().next().next().next().next().next().next().html(data.wh_manager);
        	}
        }
	})
	
}

function queryMads511(e){
	if($(e).val().trim()==""){
		//js.alert("物料号不能为空！");
		return false;
	}
	var werks=vm.werks;
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
        		$(e).parent("td").next().next().next().html(data.unit);
        		
        		$(e).parent("td").next().next().next().next().find("input").val(data.fullBoxQty_d_n);
        		//装箱数量
        		if(data.fullBoxQty_d_n>0){//如果装箱数量大于0，则该框不允许修改
        			$(e).parent("td").next().next().next().next().find("input").attr("disabled","disabled");
        		}
        	}
        }
	});
	//查询储位
	var whNumber=vm.whNumber;
	$.ajax({
        url: baseURL + "in/wmsinternalbound/querybincode",
        dataType : "json",
		type : "post",
        data: {
        	"werks":werks,
        	"whNumber":whNumber,
			"material":$(e).val().trim()
        },
        async:false,
        success: function(data){
        	if(data.code == 0){
        		$(e).parent("td").next().next().next().next().next().next().html(data.bin_code);
        	}
        }
	});
	//查询仓管员
	var whNumber=vm.whNumber;
	$.ajax({
        url: baseURL + "in/wmsinternalbound/querywhmanager",
        dataType : "json",
		type : "post",
        data: {
        	"werks":werks,
        	"whNumber":whNumber,
			"material":$(e).val().trim()
        },
        async:false,
        success: function(data){
        	if(data.code == 0){
        		$(e).parent("td").next().next().next().next().next().next().next().next().next().html(data.wh_manager);
        	}
        }
	})
	
}

function queryMads521(e){
	if($(e).val().trim()==""){
		js.alert("物料号不能为空！");
		return false;
	}
	var werks=vm.werks;
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
        		$(e).parent("td").next().next().next().html(data.unit);
        		//装箱数量
        		if(data.fullBoxQty_d>0){//如果装箱数量大于0，则该框不允许修改
        			$(e).parent("td").next().next().next().next().find("input").attr("disabled","disabled");
        		}
        		$(e).parent("td").next().next().next().next().find("input").val(data.fullBoxQty_d);
        		//物流器具
        		$(e).parent("td").next().next().next().next().next().next().next().next().next().find("input").val(data.LT_WARE);
        		//车型
        		$(e).parent("td").next().next().next().next().next().next().next().next().next().next().next().find("input").val(data.CAR_TYPE);
        		//模具编号
        		$(e).parent("td").next().next().next().next().next().next().next().next().next().next().next().next().find("input").val(data.MOULD_NO);
        		//工位
        		$(e).parent("td").next().next().next().next().next().next().next().next().next().next().next().next().next().next().next().find("input").val(data.DIS_STATION);
        		
        		$(e).parent("td").next().next().next().next().next().next().next().next().next().next().next().next().next()
        		.html(data.mads.substring(0,data.mads.indexOf("_")));
        	}
        }
	});
	//查询储位
	var whNumber= vm.whNumber;//$("#whNumber_512").val();
	$.ajax({
        url: baseURL + "in/wmsinternalbound/querybincode",
        dataType : "json",
		type : "post",
        data: {
        	"werks":werks,
        	"whNumber":whNumber,
			"material":$(e).val().trim()
        },
        async:false,
        success: function(data){
        	if(data.code == 0){
        		$(e).parent("td").next().next().next().next().next().next().html(data.bin_code);
        	}
        }
	});
	var whlgort= vm.lgort;
	$.ajax({
        url: baseURL + "in/wmsinternalbound/querywhmanager",
        dataType : "json",
		type : "post",
        data: {
        	"werks":werks,
        	"whNumber":whNumber,
        	"lgort":whlgort,
			"material":$(e).val().trim()
        },
        async:false,
        success: function(data){
        	if(data.code == 0){
        		$(e).parent("td").next().next().next().next().next().next().next().next().next().next().next().next().next().next().next().next().next()
        		.html(data.wh_manager);
        	}
        }
	})
	
}

function queryMadsCo101(e){
	if($(e).val().trim()==""){
		js.alert("物料号不能为空！");
		return false;
	}
	var werks=vm.werks;
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
        		$(e).parent("td").next().next().next().html(data.unit);
        		
        		//装箱数量
        		if(data.fullBoxQty_d>0){//如果装箱数量大于0，则该框不允许修改
        			$(e).parent("td").next().next().next().next().find("input").attr("disabled","disabled");
        		}
        		$(e).parent("td").next().next().next().next().find("input").val(data.fullBoxQty_d);
        		//物流器具
        		$(e).parent("td").next().next().next().next().next().next().next().next().next().find("input").val(data.LT_WARE);
        		//车型
        		$(e).parent("td").next().next().next().next().next().next().next().next().next().next().next().find("input").val(data.CAR_TYPE);
        		//模具编号
        		$(e).parent("td").next().next().next().next().next().next().next().next().next().next().next().next().find("input").val(data.MOULD_NO);
        		//工位
        		$(e).parent("td").next().next().next().next().next().next().next().next().next().next().next().next().next().next().next().find("input").val(data.DIS_STATION);
        		
        		
        		$(e).parent("td").next().next().next().next().next().next().next().next().next().next().next().next().next()
        		.html(data.mads.substring(0,data.mads.indexOf("_")));
        	}
        }
	});
	//查询储位
	var whNumber=vm.whNumber;
	$.ajax({
        url: baseURL + "in/wmsinternalbound/querybincode",
        dataType : "json",
		type : "post",
        data: {
        	"werks":werks,
        	"whNumber":whNumber,
			"material":$(e).val().trim()
        },
        async:false,
        success: function(data){
        	if(data.code == 0){
        		$(e).parent("td").next().next().next().next().next().next().html(data.bin_code);
        	}
        }
	});
	//查询仓管员
	var whNumber=vm.whNumber;
	$.ajax({
        url: baseURL + "in/wmsinternalbound/querywhmanager",
        dataType : "json",
		type : "post",
        data: {
        	"werks":werks,
        	"whNumber":whNumber,
			"material":$(e).val().trim()
        },
        async:false,
        success: function(data){
        	if(data.code == 0){
        		$(e).parent("td").next().next().next().next().next().next().next().next().next().next().next().next().next().next().next().next().next()
        		.html(data.wh_manager);
        	}
        }
	})
	
}

function queryMads202(e){
	if($(e).val().trim()==""){
		js.alert("物料号不能为空！");
		return false;
	}
	var werks=vm.werks;
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
        		$(e).parent("td").next().next().next().html(data.unit);
        		$(e).parent("td").next().next().next().next().next().next().next().next().next().next().next().next().next()
        		.html(data.mads.substring(0,data.mads.indexOf("_")));
        		
        		//装箱数量
        		if(data.fullBoxQty_d>0){//如果装箱数量大于0，则该框不允许修改
        			$(e).parent("td").next().next().next().next().find("input").attr("disabled","disabled");
        		}
        		$(e).parent("td").next().next().next().next().find("input").val(data.fullBoxQty_d);
        		//物流器具
        		$(e).parent("td").next().next().next().next().next().next().next().next().next().find("input").val(data.LT_WARE);
        		//车型
        		$(e).parent("td").next().next().next().next().next().next().next().next().next().next().next().find("input").val(data.CAR_TYPE);
        		//模具编号
        		$(e).parent("td").next().next().next().next().next().next().next().next().next().next().next().next().find("input").val(data.MOULD_NO);
        		//工位
        		$(e).parent("td").next().next().next().next().next().next().next().next().next().next().next().next().next().next().next().find("input").val(data.DIS_STATION);
        		
        	}
        }
	});
	//查询储位
	var whNumber=vm.whNumber;
	$.ajax({
        url: baseURL + "in/wmsinternalbound/querybincode",
        dataType : "json",
		type : "post",
        data: {
        	"werks":werks,
        	"whNumber":whNumber,
			"material":$(e).val().trim()
        },
        async:false,
        success: function(data){
        	if(data.code == 0){
        		$(e).parent("td").next().next().next().next().next().next().html(data.bin_code);
        	}
        }
	});
	//查询仓管员
	var whNumber=vm.whNumber;
	$.ajax({
        url: baseURL + "in/wmsinternalbound/querywhmanager",
        dataType : "json",
		type : "post",
        data: {
        	"werks":werks,
        	"whNumber":whNumber,
			"material":$(e).val().trim()
        },
        async:false,
        success: function(data){
        	if(data.code == 0){
        		$(e).parent("td").next().next().next().next().next().next().next().next().next().next().next().next().next().next().next().next().next()
        		.html(data.wh_manager);
        	}
        }
	})
	
}

function queryMads222(e){
	if($(e).val().trim()==""){
		js.alert("物料号不能为空！");
		return false;
	}
	var werks=vm.werks;
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
        		$(e).parent("td").next().next().next().html(data.unit);
        		$(e).parent("td").next().next().next().next().next().next().next().next().next().next().next().next().next()
        		.html(data.mads.substring(0,data.mads.indexOf("_")));
        		
        		//装箱数量
        		if(data.fullBoxQty_d>0){//如果装箱数量大于0，则该框不允许修改
        			$(e).parent("td").next().next().next().next().find("input").attr("disabled","disabled");
        		}
        		$(e).parent("td").next().next().next().next().find("input").val(data.fullBoxQty_d);
        		//物流器具
        		$(e).parent("td").next().next().next().next().next().next().next().next().next().find("input").val(data.LT_WARE);
        		//车型
        		$(e).parent("td").next().next().next().next().next().next().next().next().next().next().next().find("input").val(data.CAR_TYPE);
        		//模具编号
        		$(e).parent("td").next().next().next().next().next().next().next().next().next().next().next().next().find("input").val(data.MOULD_NO);
        		//工位
        		$(e).parent("td").next().next().next().next().next().next().next().next().next().next().next().next().next().next().next().find("input").val(data.DIS_STATION);
        		
        	}
        }
	});
	//查询储位
	var whNumber=vm.whNumber;
	$.ajax({
        url: baseURL + "in/wmsinternalbound/querybincode",
        dataType : "json",
		type : "post",
        data: {
        	"werks":werks,
        	"whNumber":whNumber,
			"material":$(e).val().trim()
        },
        async:false,
        success: function(data){
        	if(data.code == 0){
        		$(e).parent("td").next().next().next().next().next().next().html(data.bin_code);
        	}
        }
	});
	//查询仓管员
	var whNumber=vm.whNumber;
	$.ajax({
        url: baseURL + "in/wmsinternalbound/querywhmanager",
        dataType : "json",
		type : "post",
        data: {
        	"werks":werks,
        	"whNumber":whNumber,
			"material":$(e).val().trim()
        },
        async:false,
        success: function(data){
        	if(data.code == 0){
        		$(e).parent("td").next().next().next().next().next().next().next().next().next().next().next().next().next().next().next().next().next()
        		.html(data.wh_manager);
        	}
        }
	})
	
}

function queryMads262(e){
	if($(e).val().trim()==""){
		js.alert("物料号不能为空！");
		return false;
	}
	var werks=vm.werks;
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
        		$(e).parent("td").next().next().next().html(data.unit);
        		$(e).parent("td").next().next().next().next().next().next().next().next().next().next().next().next().next()
        		.html(data.mads.substring(0,data.mads.indexOf("_")));
        		
        		//装箱数量
        		
        		if(data.fullBoxQty_d>0){//如果装箱数量大于0，则该框不允许修改
        			$(e).parent("td").next().next().next().next().find("input").attr("disabled","disabled");
        		}
        		$(e).parent("td").next().next().next().next().find("input").val(data.fullBoxQty_d);
        		
        		//物流器具
        		$(e).parent("td").next().next().next().next().next().next().next().next().next().find("input").val(data.LT_WARE);
        		//车型
        		$(e).parent("td").next().next().next().next().next().next().next().next().next().next().next().find("input").val(data.CAR_TYPE);
        		//模具编号
        		$(e).parent("td").next().next().next().next().next().next().next().next().next().next().next().next().find("input").val(data.MOULD_NO);
        		//工位
        		$(e).parent("td").next().next().next().next().next().next().next().next().next().next().next().next().next().next().next().find("input").val(data.DIS_STATION);
        		
        	}
        }
	});
	//查询储位
	var whNumber=vm.whNumber;
	$.ajax({
        url: baseURL + "in/wmsinternalbound/querybincode",
        dataType : "json",
		type : "post",
        data: {
        	"werks":werks,
        	"whNumber":whNumber,
			"material":$(e).val().trim()
        },
        async:false,
        success: function(data){
        	if(data.code == 0){
        		$(e).parent("td").next().next().next().next().next().next().html(data.bin_code);
        	}
        }
	});
	//查询仓管员
	var whNumber=vm.whNumber;
	$.ajax({
        url: baseURL + "in/wmsinternalbound/querywhmanager",
        dataType : "json",
		type : "post",
        data: {
        	"werks":werks,
        	"whNumber":whNumber,
			"material":$(e).val().trim()
        },
        async:false,
        success: function(data){
        	if(data.code == 0){
        		$(e).parent("td").next().next().next().next().next().next().next().next().next().next().next().next().next().next().next().next().next()
        		.html(data.wh_manager);
        	}
        }
	})
	
}

function queryMadsMo262(e){
	if($(e).val().trim()==""){
		//js.alert("物料号不能为空！");
		return false;
	}
	var werks=vm.werks;
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
        		$(e).parent("td").next().next().next().html(data.unit);
        		
        		$(e).parent("td").next().next().next().next().find("input").val(data.fullBoxQty_d_n);
        		
        		//装箱数量
        		if(data.fullBoxQty_d_n>0){//如果装箱数量大于0，则该框不允许修改
        			$(e).parent("td").next().next().next().next().find("input").attr("disabled","disabled");
        		}
        	}
        }
	});
	//查询储位
	var whNumber= vm.whNumber;//$("#whNumber_512").val();
	$.ajax({
        url: baseURL + "in/wmsinternalbound/querybincode",
        dataType : "json",
		type : "post",
        data: {
        	"werks":werks,
        	"whNumber":whNumber,
			"material":$(e).val().trim()
        },
        async:false,
        success: function(data){
        	if(data.code == 0){
        		$(e).parent("td").next().next().next().next().next().next().html(data.bin_code);
        	}
        }
	});
	$.ajax({
        url: baseURL + "in/wmsinternalbound/querywhmanager",
        dataType : "json",
		type : "post",
        data: {
        	"werks":werks,
        	"whNumber":whNumber,
			"material":$(e).val().trim()
        },
        async:false,
        success: function(data){
        	if(data.code == 0){
        		$(e).parent("td").next().next().next().next().next().next().next().next().next().next()
        		.html(data.wh_manager);
        	}
        }
	})
	
}

function queryMadsBypaste262(material,cell){
	if(material.trim()==""){
		js.alert("物料号不能为空！");
		return false;
	}
	var werks=vm.werks;
	
	$.ajax({
        url: baseURL + "in/wmsinternalbound/querymaterial",
        dataType : "json",
		type : "post",
        data: {
        	"werks":werks,
			"material":material.trim()
        },
        async:false,
        success: function(data){
        	if(data.code == 0){
        		//alert(data.mads+"    "+data.unit);
        		$(cell).next().html(data.mads);
        		$(cell).next().next().next().html(data.unit);
        		$(cell).next().next().next().next().next().next().next().next().next().next().next().next().next()
        		.html(data.mads.substring(0,data.mads.indexOf("_")));
        		
        		//装箱数量
        		
        		$(cell).next().next().next().next().html(data.fullBoxQty_d);
        		
        		//物流器具
        		$(cell).next().next().next().next().next().next().next().next().next().html(data.LT_WARE);
        		//车型
        		$(cell).next().next().next().next().next().next().next().next().next().next().next().html(data.CAR_TYPE);
        		//模具编号
        		$(cell).next().next().next().next().next().next().next().next().next().next().next().next().html(data.MOULD_NO);
        		//工位
        		$(cell).next().next().next().next().next().next().next().next().next().next().next().next().next().next().next().html(data.DIS_STATION);
        		
        	}
        }
	});
	//查询储位
	var whNumber=vm.whNumber;
	$.ajax({
        url: baseURL + "in/wmsinternalbound/querybincode",
        dataType : "json",
		type : "post",
        data: {
        	"werks":werks,
        	"whNumber":whNumber,
			"material":material.trim()
        },
        async:false,
        success: function(data){
        	if(data.code == 0){
        		$(cell).next().next().next().next().next().next().html(data.bin_code);
        	}
        }
	});
	//查询仓管员
	var whNumber=vm.whNumber;
	$.ajax({
        url: baseURL + "in/wmsinternalbound/querywhmanager",
        dataType : "json",
		type : "post",
        data: {
        	"werks":werks,
        	"whNumber":whNumber,
			"material":material.trim()
        },
        async:false,
        success: function(data){
        	if(data.code == 0){
        		$(cell).next().next().next().next().next().next().next().next().next().next().next().next().next().next().next().next().next()
        		.html(data.wh_manager);
        	}
        }
	})
	
}

function queryMadsBypaste262sc(material,cell){
	if(material.trim()==""){
		js.alert("物料号不能为空！");
		return false;
	}
	var werks=vm.werks;
	$.ajax({
        url: baseURL + "in/wmsinternalbound/querymaterial",
        dataType : "json",
		type : "post",
        data: {
        	"werks":werks,
			"material":material.trim()
        },
        async:false,
        success: function(data){
        	if(data.code == 0){
        		//alert(data.mads+"    "+data.unit);
        		$(cell).next().html(data.mads);
        		$(cell).next().next().next().html(data.unit);
        		
        		$(cell).next().next().next().next().html(data.fullBoxQty_d_n);
        		
        	}
        }
	});
	//查询储位
	var whNumber= vm.whNumber;//$("#whNumber_512").val();
	$.ajax({
        url: baseURL + "in/wmsinternalbound/querybincode",
        dataType : "json",
		type : "post",
        data: {
        	"werks":werks,
        	"whNumber":whNumber,
			"material":material.trim()
        },
        async:false,
        success: function(data){
        	if(data.code == 0){
        		$(cell).next().next().next().next().next().next().html(data.bin_code);
        	}
        }
	});
	$.ajax({
        url: baseURL + "in/wmsinternalbound/querywhmanager",
        dataType : "json",
		type : "post",
        data: {
        	"werks":werks,
        	"whNumber":whNumber,
			"material":material.trim()
        },
        async:false,
        success: function(data){
        	if(data.code == 0){
        		$(cell).next().next().next().next().next().next().next().next().next().next()
        		.html(data.wh_manager);
        	}
        }
	})
	
}



/**
 * 根据进仓数量 更新 满箱数量 箱数
 * @param e
 * @returns {Boolean}
 */
function queryfullboxqty501(e){
	
	if($(e).val().trim()==""){
		js.alert("进仓数量不能为空！");
		return false;
	}
	if($(e).val().trim()=="0"){
		js.alert("进仓数量不能为0！");
		return false;
	}
	var werks=vm.werks;
	var material=$(e).parent("td").prev().prev().find("input").val();
	$.ajax({
        url: baseURL + "in/wmsinternalbound/queryfullbox",
        dataType : "json",
		type : "post",
        data: {
        	"werks":werks,
			"material":material,
			"entry_qty":$(e).val().trim()
        },
        async:false,
        success: function(data){
        	if(data.code == 0){
        		//alert(data.mads+"    "+data.unit);
        		//$(e).parent("td").next().next().find("input").val(data.fullBoxQty_d);
        		//$(e).parent("td").next().next().next().html(data.box_count);
        		
        		$(e).parent("td").next().next().next().html(Math.ceil($(e).val().trim()/$(e).parent("td").next().next().find("input").val()));
        	}
        }
	})
	
}

/**
 * 根据进仓数量 更新 满箱数量 箱数
 * @param e
 * @returns {Boolean}
 */
function queryfullboxqty903(e){
	
	if($(e).val().trim()==""){
		js.alert("进仓数量不能为空！");
		return false;
	}
	if($(e).val().trim()=="0"){
		js.alert("进仓数量不能为0！");
		return false;
	}
	var werks=vm.werks;
	var material=$(e).parent("td").prev().prev().find("input").val();
	$.ajax({
        url: baseURL + "in/wmsinternalbound/queryfullbox",
        dataType : "json",
		type : "post",
        data: {
        	"werks":werks,
			"material":material,
			"entry_qty":$(e).val().trim()
        },
        async:false,
        success: function(data){
        	if(data.code == 0){
        		//alert(data.mads+"    "+data.unit);
        		//$(e).parent("td").next().next().find("input").val(data.fullBoxQty_d);
        		//$(e).parent("td").next().next().next().html(data.box_count);
        		$(e).parent("td").next().next().next().html(Math.ceil($(e).val().trim()/$(e).parent("td").next().next().find("input").val()));
        	}
        }
	})
	
}

/**
 * 根据进仓数量 更新 满箱数量 箱数
 * @param e
 * @returns {Boolean}
 */
function queryfullboxqty511(e){
	
	if($(e).val().trim()==""){
		js.alert("进仓数量不能为空！");
		return false;
	}
	if($(e).val().trim()=="0"){
		js.alert("进仓数量不能为0！");
		return false;
	}
	var werks=vm.werks;
	var material=$(e).parent("td").prev().prev().find("input").val();
	$.ajax({
        url: baseURL + "in/wmsinternalbound/queryfullbox",
        dataType : "json",
		type : "post",
        data: {
        	"werks":werks,
			"material":material,
			"entry_qty":$(e).val().trim()
        },
        async:false,
        success: function(data){
        	if(data.code == 0){
        		//alert(data.mads+"    "+data.unit);
        		//$(e).parent("td").next().next().find("input").val(data.fullBoxQty_d);
        		//$(e).parent("td").next().next().next().html(data.box_count);
        		$(e).parent("td").next().next().next().html(Math.ceil($(e).val().trim()/$(e).parent("td").next().next().find("input").val()));
        	}
        }
	})
	
}

/**
 * 根据进仓数量 更新 满箱数量 箱数
 * @param e
 * @returns {Boolean}
 */
function queryfullboxqty521(e){
	
	if($(e).val().trim()==""){
		js.alert("进仓数量不能为空！");
		return false;
	}
	if($(e).val().trim()=="0"){
		js.alert("进仓数量不能为0！");
		return false;
	}
	var werks=vm.werks;
	var material=$(e).parent("td").prev().prev().find("input").val();
	$.ajax({
        url: baseURL + "in/wmsinternalbound/queryfullbox",
        dataType : "json",
		type : "post",
        data: {
        	"werks":werks,
			"material":material,
			"entry_qty":$(e).val().trim()
        },
        async:false,
        success: function(data){
        	if(data.code == 0){
        		//alert(data.mads+"    "+data.unit);
        		$(e).parent("td").next().next().find("input").val(data.fullBoxQty_d);
        		$(e).parent("td").next().next().next().html(data.box_count);
        		
        		//$(e).parent("td").next().next().next().next().next().next().next().find("input").val(data.LT_WARE);
        		
        		//$(e).parent("td").next().next().next().next().next().next().next()
        		//.next().next().find("input").val(data.CAR_TYPE);
        		
        		//$(e).parent("td").next().next().next().next().next().next().next()
        		//.next().next().next().find("input").val(data.MOULD_NO);
        		
        		//$(e).parent("td").next().next().next().next().next().next().next()
        		//.next().next().next().next().next().next().find("input").val(data.DIS_STATION);
        	}
        }
	})
	
}

/**
 * 根据进仓数量 更新 满箱数量 箱数
 * @param e
 * @returns {Boolean}
 */
function queryfullboxqtyCo101(e){
	
	if($(e).val().trim()==""){
		js.alert("进仓数量不能为空！");
		return false;
	}
	if($(e).val().trim()=="0"){
		js.alert("进仓数量不能为0！");
		return false;
	}
	var werks=vm.werks;
	var material=$(e).parent("td").prev().prev().find("input").val();
	$.ajax({
        url: baseURL + "in/wmsinternalbound/queryfullbox",
        dataType : "json",
		type : "post",
        data: {
        	"werks":werks,
			"material":material,
			"entry_qty":$(e).val().trim()
        },
        async:false,
        success: function(data){
        	if(data.code == 0){
        		//alert(data.mads+"    "+data.unit);
        		$(e).parent("td").next().next().find("input").val(data.fullBoxQty_d);
        		$(e).parent("td").next().next().next().html(data.box_count);
        		/*
        		$(e).parent("td").next().next().next().next().next().next().next().find("input").val(data.LT_WARE);
        		
        		$(e).parent("td").next().next().next().next().next().next().next()
        		.next().next().find("input").val(data.CAR_TYPE);
        		
        		$(e).parent("td").next().next().next().next().next().next().next()
        		.next().next().next().find("input").val(data.MOULD_NO);
        		
        		$(e).parent("td").next().next().next().next().next().next().next()
        		.next().next().next().next().next().next().find("input").val(data.DIS_STATION);
        		*/
        	}
        }
	})
	
}

/**
 * 根据进仓数量 更新 满箱数量 箱数
 * @param e
 * @returns {Boolean}
 */
function queryfullboxqty202(e){
	
	if($(e).val().trim()==""){
		js.alert("进仓数量不能为空！");
		return false;
	}
	if($(e).val().trim()=="0"){
		js.alert("进仓数量不能为0！");
		return false;
	}
	var werks=vm.werks;
	var material=$(e).parent("td").prev().prev().find("input").val();
	$.ajax({
        url: baseURL + "in/wmsinternalbound/queryfullbox",
        dataType : "json",
		type : "post",
        data: {
        	"werks":werks,
			"material":material,
			"entry_qty":$(e).val().trim()
        },
        async:false,
        success: function(data){
        	if(data.code == 0){
        		//alert(data.mads+"    "+data.unit);
        		//$(e).parent("td").next().next().find("input").val(data.fullBoxQty_d);
        		$(e).parent("td").next().next().next().html(data.box_count);
        		
        		//$(e).parent("td").next().next().next().next().next().next().next().find("input").val(data.LT_WARE);
        		
        		//$(e).parent("td").next().next().next().next().next().next().next()
        		//.next().next().find("input").val(data.CAR_TYPE);
        		
        		//$(e).parent("td").next().next().next().next().next().next().next()
        		//.next().next().next().find("input").val(data.MOULD_NO);
        		
        		//$(e).parent("td").next().next().next().next().next().next().next()
        		//.next().next().next().next().next().next().find("input").val(data.DIS_STATION);
        	}
        }
	})
	
}

/**
 * 根据进仓数量 更新 满箱数量 箱数
 * @param e
 * @returns {Boolean}
 */
function queryfullboxqty222(e){
	
	if($(e).val().trim()==""){
		js.alert("进仓数量不能为空！");
		return false;
	}
	if($(e).val().trim()=="0"){
		js.alert("进仓数量不能为0！");
		return false;
	}
	var werks=vm.werks;
	var material=$(e).parent("td").prev().prev().find("input").val();
	$.ajax({
        url: baseURL + "in/wmsinternalbound/queryfullbox",
        dataType : "json",
		type : "post",
        data: {
        	"werks":werks,
			"material":material,
			"entry_qty":$(e).val().trim()
        },
        async:false,
        success: function(data){
        	if(data.code == 0){
        		//alert(data.mads+"    "+data.unit);
        		//$(e).parent("td").next().next().find("input").val(data.fullBoxQty_d);
        		$(e).parent("td").next().next().next().html(data.box_count);
        		/*
        		$(e).parent("td").next().next().next().next().next().next().next().find("input").val(data.LT_WARE);
        		
        		$(e).parent("td").next().next().next().next().next().next().next()
        		.next().next().find("input").val(data.CAR_TYPE);
        		
        		$(e).parent("td").next().next().next().next().next().next().next()
        		.next().next().next().find("input").val(data.MOULD_NO);
        		
        		$(e).parent("td").next().next().next().next().next().next().next()
        		.next().next().next().next().next().next().find("input").val(data.DIS_STATION);
        		*/
        	}
        }
	})
	
}

/**
 * 根据进仓数量 更新 满箱数量 箱数
 * @param e
 * @returns {Boolean}
 */
function queryfullboxqty262(e){
	
	if($(e).val().trim()==""){
		js.alert("进仓数量不能为空！");
		return false;
	}
	if($(e).val().trim()=="0"){
		js.alert("进仓数量不能为0！");
		return false;
	}
	var werks=vm.werks;
	var material=$(e).parent("td").prev().prev().find("input").val();
	
	$.ajax({
        url: baseURL + "in/wmsinternalbound/queryfullbox",
        dataType : "json",
		type : "post",
        data: {
        	"werks":werks,
			"material":material,
			"entry_qty":$(e).val().trim()
        },
        async:false,
        success: function(data){
        	alert(data.fullBoxQty_d)
        	if(data.code == 0){
        		//alert(data.mads+"    "+data.unit);
        		//$(e).parent("td").next().next().find("input").val(data.fullBoxQty_d);
        		//$(e).parent("td").next().next().next().html(data.box_count);
        		
        		//$(e).parent("td").next().next().next().next().next().next().next().find("input").val(data.LT_WARE);
        		
        		//$(e).parent("td").next().next().next().next().next().next().next()
        		//.next().next().find("input").val(data.CAR_TYPE);
        		
        		//$(e).parent("td").next().next().next().next().next().next().next()
        		//.next().next().next().find("input").val(data.MOULD_NO);
        		
        		//$(e).parent("td").next().next().next().next().next().next().next()
        		//.next().next().next().next().next().next().find("input").val(data.DIS_STATION);
        		
        		$(e).parent("td").next().next().next().html(Math.ceil($(e).val().trim()/$(e).parent("td").next().next().find("input").val()));
        	}
        }
	})
	
}

function queryfullboxqtyMo262(e){
	
	if($(e).val().trim()==""){
		js.alert("进仓数量不能为空！");
		return false;
	}
	if($(e).val().trim()=="0"){
		js.alert("进仓数量不能为0！");
		return false;
	}
	var werks=vm.werks;
	var material=$(e).parent("td").prev().prev().find("input").val();
	$.ajax({
        url: baseURL + "in/wmsinternalbound/queryfullbox",
        dataType : "json",
		type : "post",
        data: {
        	"werks":werks,
			"material":material,
			"entry_qty":$(e).val().trim()
        },
        async:false,
        success: function(data){
        	if(data.code == 0){
        		//alert(data.mads+"    "+data.unit);
        		//$(e).parent("td").next().next().find("input").val(data.fullBoxQty_d);
        		//$(e).parent("td").next().next().next().html(data.box_count);
        		$(e).parent("td").next().next().next().html(Math.ceil($(e).val().trim()/$(e).parent("td").next().next().find("input").val()));
        	}
        }
	})
	
}



function iceil_add501(e){
	
	//件数=进仓数量/装箱数量
	if($(e).val().trim()==""){
		js.alert("装箱数量不能为空！");
		return false;
	}
	if($(e).val().trim()==0){
		js.alert("装箱数量不能为0！");
		return false;
	}
	$(e).parent("td").next().html(Math.ceil($(e).parent("td").prev().prev().find("input").val()/$(e).val().trim()));
}

function iceil_add903(e){
	
	//件数=进仓数量/装箱数量
	if($(e).val().trim()==""){
		js.alert("装箱数量不能为空！");
		return false;
	}
	if($(e).val().trim()==0){
		js.alert("装箱数量不能为0！");
		return false;
	}
	$(e).parent("td").next().html(Math.ceil($(e).parent("td").prev().prev().find("input").val()/$(e).val().trim()));
}

function iceil_add511(e){
	
	//件数=进仓数量/装箱数量
	if($(e).val().trim()==""){
		js.alert("装箱数量不能为空！");
		return false;
	}
	if($(e).val().trim()==0){
		js.alert("装箱数量不能为0！");
		return false;
	}
	$(e).parent("td").next().html(Math.ceil($(e).parent("td").prev().prev().find("input").val()/$(e).val().trim()));
}

function iceil_add521(e){
	
	//件数=进仓数量/装箱数量
	if($(e).val().trim()==""){
		js.alert("装箱数量不能为空！");
		return false;
	}
	if($(e).val().trim()==0){
		js.alert("装箱数量不能为0！");
		return false;
	}
	$(e).parent("td").next().html(Math.ceil($(e).parent("td").prev().prev().find("input").val()/$(e).val().trim()));
}

function iceil_addCo101(e){
	
	//件数=进仓数量/装箱数量
	if($(e).val().trim()==""){
		js.alert("装箱数量不能为空！");
		return false;
	}
	if($(e).val().trim()==0){
		js.alert("装箱数量不能为0！");
		return false;
	}
	$(e).parent("td").next().html(Math.ceil($(e).parent("td").prev().prev().find("input").val()/$(e).val().trim()));
}

function iceil_addCo202(e){
	
	//件数=进仓数量/装箱数量
	if($(e).val().trim()==""){
		js.alert("装箱数量不能为空！");
		return false;
	}
	if($(e).val().trim()==0){
		js.alert("装箱数量不能为0！");
		return false;
	}
	$(e).parent("td").next().html(Math.ceil($(e).parent("td").prev().prev().find("input").val()/$(e).val().trim()));
}

function iceil_addCo222(e){
	
	//件数=进仓数量/装箱数量
	if($(e).val().trim()==""){
		js.alert("装箱数量不能为空！");
		return false;
	}
	if($(e).val().trim()==0){
		js.alert("装箱数量不能为0！");
		return false;
	}
	$(e).parent("td").next().html(Math.ceil($(e).parent("td").prev().prev().find("input").val()/$(e).val().trim()));
}

function iceil_addCo262(e){
	
	//件数=进仓数量/装箱数量
	if($(e).val().trim()==""){
		js.alert("装箱数量不能为空！");
		return false;
	}
	if($(e).val().trim()==0){
		js.alert("装箱数量不能为0！");
		return false;
	}
	
	$(e).parent("td").next().html(Math.ceil($(e).parent("td").prev().prev().find("input").val()/$(e).val().trim()));
}

function iceil_addmo262(e){
	
	//件数=进仓数量/装箱数量
	if($(e).val().trim()==""){
		js.alert("装箱数量不能为空！");
		return false;
	}
	if($(e).val().trim()==0){
		js.alert("装箱数量不能为0！");
		return false;
	}
	$(e).parent("td").next().html(Math.ceil($(e).parent("td").prev().prev().find("input").val()/$(e).val().trim()));
}



$("#btn_delete501").click(function () {
	var trs=$("#dataGrid").children("tbody").children("tr");
	var reIndex = 1;
	$.each(trs,function(index,tr){
		var td = $(tr).find("td").eq(0);
		var td_check = $(tr).find("td").eq(1).find("input");
		if(index > 0){
			//if(tr.getAttribute("aria-selected")){
			var ischecked=$(td_check).is(":checked");
			if(ischecked){
				//$(td_check).attr('checked', false);
				//$('#dataGrid').jqGrid('delRowData',index);
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
});


function dataGrid305(){
	listdata.length=0;
	//$("#dataGrid").jqGrid("clearGridData", true);
	$("#dataGrid").jqGrid('GridUnload');
	$("#dataGrid").dataGrid({
    	url: baseUrl + 'in/wmsinternalbound/list',
    	datatype: "local",
		data: listdata,
        colModel: [			
			{ label: 'SAP凭证', name: 'MAT_DOC', index: 'MAT_DOC', width: 100,sortable:false }, 
			{ label: '行项目', name: 'MATDOC_ITM', index: 'MATDOC_ITM', width: 60,sortable:false },
			{ label: '物料号', name: 'MATERIAL', index: 'MATERIAL', width: 90,sortable:false }, 
			{ label: '物料描述', name: 'MATERIALDESC', index: 'MATERIALDESC', width: 200,sortable:false }, 
			{ label: '调入数量', name: 'ENTRY_QNT', index: 'ENTRY_QNT', width: 80,sortable:false }, 
			{ label: '<span style="color:blue">进仓数量</span>', name: 'IN_QTY', index: 'IN_QTY', width: 80,sortable:false,editable:true,editrules:{
   				number:true},editoptions:{
   	                dataInit:function(e){
   	                    $(e).blur(function(e){
   	                    	iceil_add(this)
   	                    });
   	                }
   	            } }, 
			{ label: '单位', name: 'UNIT', index: 'UNIT', width: 60,sortable:false },
			{ label: '<span style="color:blue">装箱数量</span>', name: 'FULL_BOX_QTY', index: 'FULL_BOX_QTY', width: 80,sortable:false,editable:true,editrules:{
   				number:true},editoptions:{
   	                dataInit:function(e){
   	                    $(e).blur(function(e){
   	                    	iceil_add2(this)
   	                    });
   	                }
   	            } },
			{ label: '件数', name: 'BOX_COUNT', index: 'BOX_COUNT', width: 60,sortable:false },
			//{ label: '调出数量', name: 'MAKTX', index: 'MAKTX', width: 160 },
			{ label: '调出工厂', name: 'PLANT', index: 'PLANT', width: 80,sortable:false },
			{ label: '调出库位', name: 'STGE_LOC', index: 'STGE_LOC', width: 80,sortable:false },
			{ label: '供应商代码', name: 'LIFNR', index: 'LIFNR', width: 100,sortable:false },
			{ label: '供应商名称', name: 'LIKTX', index: 'LIKTX', width: 160,sortable:false },
			{ label: '接收工厂', name: 'RECWERKS', index: 'RECWERKS', width: 100,sortable:false },
			{ label: '接收仓库号', name: 'WHNUMBER', index: 'WHNUMBER', width: 100,sortable:false },
			{ label: '<span style="color:blue">接收库位</span>', name: 'RLGORT', index: 'RLGORT', width: 100,editable:true,sortable:false },
			{ label: '储位', name: 'BIN_CODE', index: 'BIN_CODE', width: 80,sortable:false },
			{ label: '<span style="color:blue">需求跟踪号</span>', name: 'BEDNR', index: 'BEDNR', width: 120,editable:true,sortable:false},
			{ label: '<span style="color:blue">生产日期</span>', name: 'PRDDT', index: 'PRDDT', width: 100,sortable:false,editable:true,editoptions:{
                dataInit:function(e){
                    $(e).click(function(e){
                    	WdatePicker({dateFmt:'yyyy-MM-dd'})
                    });
                }
            }},
			{ label: '仓管员', name: 'WH_MANAGER', index: 'WH_MANAGER', width: 100,sortable:false },
			{ label: '<span style="color:blue">备注</span>', name: 'REMARK', index: 'REMARK', width: 160,editable:true,sortable:false},
			{ label: '核销虚收', name: 'HX_XS', index: 'HX_XS', width: 90,sortable:false },
			
			{ label: '', name: 'INBOUND_TYPE', index: 'INBOUND_TYPE', width: 160,hidden:true },
			{ label: '', name: 'BATCH', index: 'BATCH', width: 160,hidden:true },
			{ label: '', name: 'urgentFlag', index: 'urgentFlag', width: 160,hidden:true }
			
        ],
        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
		},
		onSelectAll:function(rowids,status){
			   if(status==true){
				   $('#dataGrid').jqGrid("saveCell", lastrow, lastcell);				   
				   $.each(rowids,function(i,rowid){					  
					  var row = $("#dataGrid").jqGrid("getRowData",rowid);
						if(row.HX_XS=='1') {
							$("#dataGrid").jqGrid("setSelection", rowid,false);//设置该行不能被选中。
						} 	
				   })
			   }
		   },
		   onSelectRow:function(rowid,status){
		    	if(status == true){
		    		var row = $("#dataGrid").jqGrid('getRowData',rowid);
		    		if(row.HX_XS=='1') {
						$("#dataGrid").jqGrid("setSelection", rowid,false);//设置该行不能被选中。
					} 
		    	}
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
        
        gridComplete:function(){
        	//紧急物料（urgentFlag不为空的）设置自定义样式
            var ids = $("#dataGrid").getDataIDs();
            for(var i=0;i<ids.length;i++){
                var rowData = $("#dataGrid").getRowData(ids[i]);
                if(rowData.urgentFlag != null && rowData.urgentFlag !='' && rowData.urgentFlag != undefined){
                	//如果是紧急物料设置自定义的颜色
                    $('#'+ids[i]).find("td").addClass("urgent-select");
                }
            }
            
            var rows=$("#dataGrid").jqGrid('getRowData');
            
            	$.each(rows,function(i,row){
            		
            		if(row.FULL_BOX_QTY>0){
            			$('#dataGrid').jqGrid('setColProp', 'FULL_BOX_QTY', { editable:false,editrules:{number:true}});

            		}
                	
                })
            
            
        },
        afterSaveCell:function(rowid, cellname, value, iRow, iCol){},
		beforeSelectRow: function (rowid, e) {  //点击复选框才选中行
			   var $myGrid = $(this),  
			   i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),  
			   cm = $myGrid.jqGrid('getGridParam', 'colModel'); 
			   if(cm[i].name == 'cb'){
				   $('#dataGrid').jqGrid('setSelection',rowid);
			   }			   		
			   return (cm[i].name == 'cb'); 
			   
			}
    });
}

function dataGridZ25(){
	listdata.length=0;
	//$("#dataGrid").jqGrid("clearGridData", true);
	$("#dataGrid").jqGrid('GridUnload');
	$("#dataGrid").dataGrid({
    	url: baseUrl + 'in/wmsinternalbound/list',
    	datatype: "local",
		data: listdata,
        colModel: [			
			{ label: 'SAP凭证', name: 'MAT_DOC', index: 'MAT_DOC', width: 100,sortable:false }, 
			{ label: '行项目', name: 'MATDOC_ITM', index: 'MATDOC_ITM', width: 60,sortable:false },
			{ label: '物料号', name: 'MATERIAL', index: 'MATERIAL', width: 90,sortable:false }, 
			{ label: '物料描述', name: 'MATERIALDESC', index: 'MATERIALDESC', width: 200,sortable:false }, 
			{ label: '调入数量', name: 'ENTRY_QNT', index: 'ENTRY_QNT', width: 80,sortable:false }, 
			{ label: '<span style="color:blue">进仓数量</span>', name: 'IN_QTY', index: 'IN_QTY', width: 80,sortable:false,editable:true,editrules:{
   				number:true},editoptions:{
   	                dataInit:function(e){
   	                    $(e).blur(function(e){
   	                    	iceil_add(this)
   	                    });
   	                }
   	            } }, 
			{ label: '单位', name: 'UNIT', index: 'UNIT', width: 60,sortable:false },
			{ label: '<span style="color:blue">装箱数量</span>', name: 'FULL_BOX_QTY', index: 'FULL_BOX_QTY', width: 80,sortable:false,editable:true,editrules:{
   				number:true},editoptions:{
   	                dataInit:function(e){
   	                    $(e).blur(function(e){
   	                    	iceil_add2(this)
   	                    });
   	                }
   	            } },
			{ label: '件数', name: 'BOX_COUNT', index: 'BOX_COUNT', width: 60,sortable:false },
			//{ label: '调出数量', name: 'MAKTX', index: 'MAKTX', width: 160 },
			{ label: '调出工厂', name: 'PLANT', index: 'PLANT', width: 80,sortable:false },
			{ label: '调出库位', name: 'STGE_LOC', index: 'STGE_LOC', width: 80,sortable:false },
			{ label: '供应商代码', name: 'LIFNR', index: 'LIFNR', width: 100,sortable:false },
			{ label: '供应商名称', name: 'LIKTX', index: 'LIKTX', width: 160,sortable:false },
			{ label: '接收工厂', name: 'RECWERKS', index: 'RECWERKS', width: 100,sortable:false },
			{ label: '接收仓库号', name: 'WHNUMBER', index: 'WHNUMBER', width: 100,sortable:false },
			{ label: '<span style="color:blue">接收库位</span>', name: 'RLGORT', index: 'RLGORT', width: 100,editable:true,sortable:false },
			{ label: '储位', name: 'BIN_CODE', index: 'BIN_CODE', width: 80,sortable:false },
			{ label: '<span style="color:blue">需求跟踪号</span>', name: 'BEDNR', index: 'BEDNR', width: 120,editable:true,sortable:false},
			{ label: '<span style="color:blue">生产日期</span>', name: 'PRDDT', index: 'PRDDT', width: 100,sortable:false,editable:true,editoptions:{
                dataInit:function(e){
                    $(e).click(function(e){
                    	WdatePicker({dateFmt:'yyyy-MM-dd'})
                    });
                }
            }},
			{ label: '仓管员', name: 'WH_MANAGER', index: 'WH_MANAGER', width: 100,sortable:false },
			{ label: '<span style="color:blue">备注</span>', name: 'REMARK', index: 'REMARK', width: 160,editable:true,sortable:false},
			{ label: '核销虚收', name: 'HX_XS', index: 'HX_XS', width: 90,sortable:false },
			
			{ label: '', name: 'INBOUND_TYPE', index: 'INBOUND_TYPE', width: 160,hidden:true },
			{ label: '', name: 'BATCH', index: 'BATCH', width: 160,hidden:true },
			{ label: '', name: 'urgentFlag', index: 'urgentFlag', width: 160,hidden:true }
			
        ],
        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
		},
		onSelectAll:function(rowids,status){
			   if(status==true){
				   $('#dataGrid').jqGrid("saveCell", lastrow, lastcell);				   
				   $.each(rowids,function(i,rowid){					  
					  var row = $("#dataGrid").jqGrid("getRowData",rowid);
						if(row.HX_XS=='1') {
							$("#dataGrid").jqGrid("setSelection", rowid,false);//设置该行不能被选中。
						} 	
				   })
			   }
		   },
		   onSelectRow:function(rowid,status){
		    	if(status == true){
		    		var row = $("#dataGrid").jqGrid('getRowData',rowid);
		    		if(row.HX_XS=='1') {
						$("#dataGrid").jqGrid("setSelection", rowid,false);//设置该行不能被选中。
					} 
		    	}
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
        
        gridComplete:function(){
        	//紧急物料（urgentFlag不为空的）设置自定义样式
            var ids = $("#dataGrid").getDataIDs();
            for(var i=0;i<ids.length;i++){
                var rowData = $("#dataGrid").getRowData(ids[i]);
                if(rowData.urgentFlag != null && rowData.urgentFlag !='' && rowData.urgentFlag != undefined){
                	//如果是紧急物料设置自定义的颜色
                    $('#'+ids[i]).find("td").addClass("urgent-select");
                }
            }
            
            var rows=$("#dataGrid").jqGrid('getRowData');
            
            	$.each(rows,function(i,row){
            		
            		if(row.FULL_BOX_QTY>0){
            			$('#dataGrid').jqGrid('setColProp', 'FULL_BOX_QTY', { editable:false,editrules:{number:true}});

            		}
                	
                })
            
            
        },
        afterSaveCell:function(rowid, cellname, value, iRow, iCol){},
		beforeSelectRow: function (rowid, e) {  //点击复选框才选中行
			   var $myGrid = $(this),  
			   i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),  
			   cm = $myGrid.jqGrid('getGridParam', 'colModel'); 
			   if(cm[i].name == 'cb'){
				   $('#dataGrid').jqGrid('setSelection',rowid);
			   }			   		
			   return (cm[i].name == 'cb'); 
			   
			}
    });
}

function dataGrid501(){
	listdata.length=0;
	//$("#dataGrid").jqGrid("clearGridData", true);
	$("#dataGrid").jqGrid('GridUnload');
	$("#dataGrid").dataGrid({
    	url: baseUrl + 'in/wmsinternalbound/list',
    	datatype: "local",
		data: listdata,
        colModel: [	
			{ label: '客户料号', name: 'CUSTOMER_MACD', index: 'CUSTOMER_MACD', width: 100,sortable:false,editable:true },
			{ label: '物料号', name: 'MATERIAL', index: 'MATERIAL', width: 100,sortable:false,editable:true },
			{ label: '物料描述', name: 'MATERIALDESC', index: 'MATERIALDESC', width: 200,sortable:false }, 
			{ label: '进仓数量', name: 'IN_QTY', index: 'IN_QTY', width: 80,editable:true,sortable:false}, 
			{ label: '单位', name: 'UNIT', index: 'UNIT', width: 60,sortable:false },
			{ label: '装箱数量', name: 'FULL_BOX_QTY', index: 'FULL_BOX_QTY', width: 80,editable:true,sortable:false},
			{ label: '件数', name: 'BOX_COUNT', index: 'BOX_COUNT', width: 60,sortable:false },
			{ label: '储位', name: 'BIN_CODE', index: 'BIN_CODE', width: 80,sortable:false },
			{ label: '需求跟踪号', name: 'BEDNR', index: 'BEDNR', width: 120,editable:true,sortable:false},
			{ label: '生产日期', name: 'PRDDT', index: 'PRDDT', width: 100,editable:true,sortable:false,editoptions:{
                dataInit:function(e){
                    $(e).click(function(e){
                    	WdatePicker({dateFmt:'yyyy-MM-dd'})
                    });
                }
            }},
			{ label: '仓管员', name: 'WH_MANAGER', index: 'WH_MANAGER', width: 100,sortable:false },
			{ label: '备注', name: 'REMARK', index: 'REMARK', width: 160,editable:true,sortable:false},
			
			{ label: '接收工厂', name: 'RECWERKS', index: 'RECWERKS', width: 100,sortable:false },
			{ label: '接收仓库号', name: 'WHNUMBER', index: 'WHNUMBER', width: 100,sortable:false },
			{ label: '接收库位', name: 'RLGORT', index: 'RLGORT', width: 100,sortable:false },
			{ label: '供应商代码', name: 'LIFNR', index: 'LIFNR', width: 100,sortable:false },
			{ label: '供应商名称', name: 'LIKTX', index: 'LIKTX', width: 160,sortable:false },
			{ label: '', name: 'INBOUND_TYPE', index: 'INBOUND_TYPE', width: 160,hidden:true },
			{ label: '', name: 'BATCH', index: 'BATCH', width: 160,hidden:true },
			{ label: '', name: 'urgentFlag', index: 'urgentFlag', width: 160,hidden:true }
			
        ],
        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow501=iRow;
			lastcell501=iCol;
			lastcellname501=cellname;
		},
		onSelectAll:function(rowids,status){},
		onSelectRow:function(rowid,status){},
        viewrecords: true,
        cellEdit:true,
        cellurl:'#',
        cellsubmit:'clientArray',
        autowidth:true,
        shrinkToFit:false,  
        autoScroll: true, 
        multiselect: true,
        showCheckbox:true,
        ajaxSuccess:function(){
        },
        
        gridComplete:function(){
        	//紧急物料（urgentFlag不为空的）设置自定义样式
            var ids = $("#dataGrid").getDataIDs();
            for(var i=0;i<ids.length;i++){
                var rowData = $("#dataGrid").getRowData(ids[i]);
                if(rowData.urgentFlag != null && rowData.urgentFlag !='' && rowData.urgentFlag != undefined){
                	//如果是紧急物料设置自定义的颜色
                    $('#'+ids[i]).find("td").addClass("urgent-select");
                }
				
            }
        },
        afterSaveCell:function(rowid, cellname, value, iRow, iCol){},
		beforeSelectRow: function (rowid, e) {  //点击复选框才选中行
           var $myGrid = $(this),
           i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),
           cm = $myGrid.jqGrid('getGridParam', 'colModel');
            vm.rowid = rowid;
           if(cm[i].name == 'cb'){
               $('#dataGrid').jqGrid('setSelection',rowid);
           }
           return (cm[i].name == 'cb');

        }
    });
}


function dataGrid903(){
	listdata.length=0;
	$("#dataGrid").jqGrid('GridUnload');
	$("#dataGrid").dataGrid({
    	url: baseUrl + 'in/wmsinternalbound/list',
    	datatype: "local",
		data: listdata,
        colModel: [
			{ label: '物料号', name: 'MATERIAL', index: 'MATERIAL', width: 100,sortable:false,editable:true },
			{ label: '物料描述', name: 'MATERIALDESC', index: 'MATERIALDESC', width: 200,sortable:false }, 
			{ label: '进仓数量', name: 'IN_QTY', index: 'IN_QTY', width: 80,editable:true,sortable:false}, 
			{ label: '单位', name: 'UNIT', index: 'UNIT', width: 60,sortable:false },
			{ label: '装箱数量', name: 'FULL_BOX_QTY', index: 'FULL_BOX_QTY', width: 80,editable:true,sortable:false},
			{ label: '件数', name: 'BOX_COUNT', index: 'BOX_COUNT', width: 60,sortable:false },
			{ label: '储位', name: 'BIN_CODE', index: 'BIN_CODE', width: 80,sortable:false },
			{ label: '需求跟踪号', name: 'BEDNR', index: 'BEDNR', width: 120,editable:true,sortable:false},
			{ label: '生产日期', name: 'PRDDT', index: 'PRDDT', width: 100,editable:true,sortable:false,editoptions:{
                dataInit:function(e){
                    $(e).click(function(e){
                    	WdatePicker({dateFmt:'yyyy-MM-dd'})
                    });
                }
            }},
			{ label: '仓管员', name: 'WH_MANAGER', index: 'WH_MANAGER', width: 100,sortable:false },
			{ label: '备注', name: 'REMARK', index: 'REMARK', width: 160,editable:true,sortable:false},
			
			{ label: '接收工厂', name: 'RECWERKS', index: 'RECWERKS', width: 100,sortable:false },
			{ label: '接收仓库号', name: 'WHNUMBER', index: 'WHNUMBER', width: 100,sortable:false },
			{ label: '接收库位', name: 'RLGORT', index: 'RLGORT', width: 100,sortable:false },
			{ label: '供应商代码', name: 'LIFNR', index: 'LIFNR', width: 100,sortable:false },
			{ label: '供应商名称', name: 'LIKTX', index: 'LIKTX', width: 160,sortable:false },
			{ label: '', name: 'INBOUND_TYPE', index: 'INBOUND_TYPE', width: 160,hidden:true },
			{ label: '', name: 'BATCH', index: 'BATCH', width: 160,hidden:true },
			{ label: '', name: 'urgentFlag', index: 'urgentFlag', width: 160,hidden:true }
			
        ],
        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
		},
		onSelectAll:function(rowids,status){},
		onSelectRow:function(rowid,status){},
        viewrecords: true,
        cellEdit:true,
        cellurl:'#',
        cellsubmit:'clientArray',
        autowidth:true,
        shrinkToFit:false,  
        autoScroll: true, 
        multiselect: true,
        showCheckbox:true,
        ajaxSuccess:function(){
        },
        
        gridComplete:function(){
        	//紧急物料（urgentFlag不为空的）设置自定义样式
            var ids = $("#dataGrid").getDataIDs();
            for(var i=0;i<ids.length;i++){
                var rowData = $("#dataGrid").getRowData(ids[i]);
                if(rowData.urgentFlag != null && rowData.urgentFlag !='' && rowData.urgentFlag != undefined){
                	//如果是紧急物料设置自定义的颜色
                    $('#'+ids[i]).find("td").addClass("urgent-select");
                }
            }
        },
        afterSaveCell:function(rowid, cellname, value, iRow, iCol){},
		beforeSelectRow: function (rowid, e) {  //点击复选框才选中行
           var $myGrid = $(this),
           i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),
           cm = $myGrid.jqGrid('getGridParam', 'colModel');
            vm.rowid = rowid;
           if(cm[i].name == 'cb'){
               $('#dataGrid').jqGrid('setSelection',rowid);
           }
           return (cm[i].name == 'cb');

        }
    });
}



function dataGrid511(){
	listdata.length=0;
	$("#dataGrid").jqGrid('GridUnload');
	$("#dataGrid").dataGrid({
    	url: baseUrl + 'in/wmsinternalbound/list',
    	datatype: "local",
		data: listdata,
        colModel: [
			{ label: '物料号', name: 'MATERIAL', index: 'MATERIAL', width: 100,sortable:false,editable:true },
			{ label: '物料描述', name: 'MATERIALDESC', index: 'MATERIALDESC', width: 200,sortable:false }, 
			{ label: '进仓数量', name: 'IN_QTY', index: 'IN_QTY', width: 80,editable:true,sortable:false}, 
			{ label: '单位', name: 'UNIT', index: 'UNIT', width: 60,sortable:false },
			{ label: '装箱数量', name: 'FULL_BOX_QTY', index: 'FULL_BOX_QTY', width: 80,editable:true,sortable:false},
			{ label: '件数', name: 'BOX_COUNT', index: 'BOX_COUNT', width: 60,sortable:false },
			{ label: '储位', name: 'BIN_CODE', index: 'BIN_CODE', width: 80,sortable:false },
			{ label: '需求跟踪号', name: 'BEDNR', index: 'BEDNR', width: 120,editable:true,sortable:false},
			{ label: '生产日期', name: 'PRDDT', index: 'PRDDT', width: 100,editable:true,sortable:false,editoptions:{
                dataInit:function(e){
                    $(e).click(function(e){
                    	WdatePicker({dateFmt:'yyyy-MM-dd'})
                    });
                }
            }},
			{ label: '仓管员', name: 'WH_MANAGER', index: 'WH_MANAGER', width: 100,sortable:false },
			{ label: '备注', name: 'REMARK', index: 'REMARK', width: 160,editable:true,sortable:false},
			
			{ label: '接收工厂', name: 'RECWERKS', index: 'RECWERKS', width: 100,hidden:true },
			{ label: '接收仓库号', name: 'WHNUMBER', index: 'WHNUMBER', width: 100,hidden:true },
			{ label: '接收库位', name: 'RLGORT', index: 'RLGORT', width: 100,hidden:true },
			{ label: '供应商代码', name: 'LIFNR', index: 'LIFNR', width: 100,hidden:true },
			{ label: '供应商名称', name: 'LIKTX', index: 'LIKTX', width: 160,hidden:true },
			{ label: '', name: 'INBOUND_TYPE', index: 'INBOUND_TYPE', width: 160,hidden:true },
			{ label: '', name: 'BATCH', index: 'BATCH', width: 160,hidden:true },
			{ label: '', name: 'urgentFlag', index: 'urgentFlag', width: 160,hidden:true }
			
        ],
        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
		},
		onSelectAll:function(rowids,status){},
		onSelectRow:function(rowid,status){},
        viewrecords: true,
        cellEdit:true,
        cellurl:'#',
        cellsubmit:'clientArray',
        autowidth:true,
        shrinkToFit:false,  
        autoScroll: true, 
        multiselect: true,
        showCheckbox:true,
        ajaxSuccess:function(){
        },
        
        gridComplete:function(){
        	//紧急物料（urgentFlag不为空的）设置自定义样式
            var ids = $("#dataGrid").getDataIDs();
            for(var i=0;i<ids.length;i++){
                var rowData = $("#dataGrid").getRowData(ids[i]);
                if(rowData.urgentFlag != null && rowData.urgentFlag !='' && rowData.urgentFlag != undefined){
                	//如果是紧急物料设置自定义的颜色
                    $('#'+ids[i]).find("td").addClass("urgent-select");
                }
            }
        },
        afterSaveCell:function(rowid, cellname, value, iRow, iCol){},
		beforeSelectRow: function (rowid, e) {  //点击复选框才选中行
           var $myGrid = $(this),
           i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),
           cm = $myGrid.jqGrid('getGridParam', 'colModel');
            vm.rowid = rowid;
           if(cm[i].name == 'cb'){
               $('#dataGrid').jqGrid('setSelection',rowid);
           }
           return (cm[i].name == 'cb');

        }
    });
}

function dataGrid101(){
	listdata.length=0;
	$("#dataGrid").jqGrid('GridUnload');
	$("#dataGrid").dataGrid({
    	url: baseUrl + 'in/wmsinternalbound/listMo101',
    	datatype: "local",
		data: listdata,
        colModel: [   
            { label: '生产订单', name: 'MO_NO', index: 'MO_NO', width: 100,sortable:false },
            { label: '行号', name: 'MO_ITEM_NO', index: 'MO_ITEM_NO', width: 100,sortable:false },
			{ label: '物料号', name: 'MATERIAL', index: 'MATERIAL', width: 100,sortable:false }, 
			{ label: '物料描述', name: 'MATERIALDESC', index: 'MATERIALDESC', width: 200,sortable:false }, 
			{ label: '订单数量', name: 'PSMNG', index: 'PSMNG', width: 80,sortable:false}, 
			{ label: '已过账数量', name: 'HASGZ', index: 'HASGZ', width: 80,sortable:false}, 
			{ label: '可进仓数量', name: 'KJCSL', index: 'KJCSL', width: 80,sortable:false},
			{ label: '<span style="color:blue">进仓数量</span>', name: 'IN_QTY', index: 'IN_QTY', width: 80,sortable:false,editable:true,editrules:{
   				number:true},editoptions:{
   	                dataInit:function(e){
   	                    $(e).blur(function(e){
   	                    	iceil_add(this)
   	                    });
   	                }
   	            }},
			{ label: '单位', name: 'UNIT', index: 'UNIT', width: 60,sortable:false },
			{ label: '<span style="color:blue">装箱数量</span>', name: 'FULL_BOX_QTY', index: 'FULL_BOX_QTY', width: 80,sortable:false,editable:true,editrules:{
   				number:true},editoptions:{
   	                dataInit:function(e){
   	                    $(e).blur(function(e){
   	                    	iceil_add2(this)
   	                    });
   	                }
   	            }},
			{ label: '件数', name: 'BOX_COUNT', index: 'BOX_COUNT', width: 60,sortable:false },
			{ label: '储位', name: 'BIN_CODE', index: 'BIN_CODE', width: 80,sortable:false },
			{ label: '<span style="color:blue">需求跟踪号</span>', name: 'BEDNR', index: 'BEDNR', width: 120,sortable:false,editable:true},
			{ label: '<span style="color:blue">生产日期</span>', name: 'PRDDT', index: 'PRDDT', width: 100,sortable:false,editable:true,editoptions:{
                dataInit:function(e){
                    $(e).click(function(e){
                    	WdatePicker({dateFmt:'yyyy-MM-dd'})
                    });
                }
            }},
			{ label: '<span style="color:blue">物流器具</span>', name: 'LT_WARE', index: 'LT_WARE', width: 80,editable:true,sortable:false },
			{ label: '<span style="color:blue">车间</span>', name: 'WORKSHOP', index: 'WORKSHOP', width: 80,editable:true,sortable:false },
			{ label: '<span style="color:blue">车型</span>', name: 'CAR_TYPE', index: 'CAR_TYPE', width: 80,editable:true,sortable:false },
			{ label: '<span style="color:blue">模具编号</span>', name: 'MOULD_NO', index: 'MOULD_NO', width: 80,editable:true,sortable:false },
			{ label: '产品图号', name: 'CPTH', index: 'CPTH', width: 80,sortable:false},
			{ label: '<span style="color:blue">作业员</span>', name: 'ZYY', index: 'ZYY', width: 80,editable:true,sortable:false },
			{ label: '<span style="color:blue">工位<span>', name: 'DIS_STATION', index: 'DIS_STATION', width: 80,editable:true,sortable:false },
			{ label: '<span style="color:blue">班次</span>', name: 'WORKGROUP_NO', index: 'WORKGROUP_NO', width: 80,editable:true,sortable:false },
			{ label: '仓管员', name: 'WH_MANAGER', index: 'WH_MANAGER', width: 100,sortable:false },
			{ label: '<span style="color:blue">备注</span>', name: 'REMARK', index: 'REMARK', width: 160,editable:true,sortable:false},
			{ label: '销售订单号', name: 'KDAUF', index: 'KDAUF', width: 100,sortable:false },
			{ label: '销售订单行项目号', name: 'KDPOS', index: 'KDPOS', width: 110,sortable:false },
			{ label: '接收工厂', name: 'RECWERKS', index: 'RECWERKS', width: 100,hidden:true },
			{ label: '接收仓库号', name: 'WHNUMBER', index: 'WHNUMBER', width: 100,hidden:true },
			{ label: '接收库位', name: 'RLGORT', index: 'RLGORT', width: 100,hidden:true },
			{ label: '供应商代码', name: 'LIFNR', index: 'LIFNR', width: 100,hidden:true },
			{ label: '供应商名称', name: 'LIKTX', index: 'LIKTX', width: 160,hidden:true },
			{ label: '', name: 'INBOUND_TYPE', index: 'INBOUND_TYPE', width: 160,hidden:true },
			{ label: '', name: 'BATCH', index: 'BATCH', width: 160,hidden:true },
			{ label: '', name: 'urgentFlag', index: 'urgentFlag', width: 160,hidden:true },
			{ label: '', name: 'FULL_BOX_QTY_FLAG', index: 'FULL_BOX_QTY_FLAG', width: 160,hidden:true }
			
        ],
        
        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
		},
		onSelectAll:function(rowids,status){},
		onSelectRow:function(rowid,status){},
        viewrecords: true,
        cellEdit:true,
        cellurl:'#',
        cellsubmit:'clientArray',
        autowidth:true,
        shrinkToFit:false,  
        autoScroll: true, 
        multiselect: true,
        showCheckbox:true,
        ajaxSuccess:function(){
        },
        
        gridComplete:function(){
        	//紧急物料（urgentFlag不为空的）设置自定义样式
            var ids = $("#dataGrid").getDataIDs();
            for(var i=0;i<ids.length;i++){
                var rowData = $("#dataGrid").getRowData(ids[i]);
                if(rowData.urgentFlag != null && rowData.urgentFlag !='' && rowData.urgentFlag != undefined){
                	//如果是紧急物料设置自定义的颜色
                    $('#'+ids[i]).find("td").addClass("urgent-select");
                }
                if(rowData.FULL_BOX_QTY_FLAG!='X'){//没有维护包装箱数量的，可以修改
                	$("#dataGrid").jqGrid('setCell', i+1, 'FULL_BOX_QTY', '', 'not-editable-cell');
                }
            }
        },
       
        afterSaveCell:function(rowid, cellname, value, iRow, iCol){},
        
		beforeSelectRow: function (rowid, e) {  //点击复选框才选中行
			   var $myGrid = $(this),  
			   i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),  
			   cm = $myGrid.jqGrid('getGridParam', 'colModel'); 
			   if(cm[i].name == 'cb'){
				   $('#dataGrid').jqGrid('setSelection',rowid);
			   }			   		
			   return (cm[i].name == 'cb'); 
			  
			}
    });
}

function dataGrid531(){
	listdata.length=0;
	$("#dataGrid").jqGrid('GridUnload');
	$("#dataGrid").dataGrid({
    	url: baseUrl + 'in/wmsinternalbound/listMo531',
    	datatype: "local",
		data: listdata,
        colModel: [
            { label: '生产订单', name: 'MO_NO', index: 'MO_NO', width: 100,sortable:false },
            { label: '行号', name: 'MO_ITEM_NO', index: 'MO_ITEM_NO', width: 100,sortable:false },
			{ label: '物料号', name: 'MATERIAL', index: 'MATERIAL', width: 100,sortable:false }, 
			{ label: '物料描述', name: 'MATERIALDESC', index: 'MATERIALDESC', width: 200,sortable:false }, 
			{ label: '订单数量', name: 'PSMNG', index: 'PSMNG', width: 80,sortable:false}, 
			{ label: '已过账数量', name: 'HASGZ', index: 'HASGZ', width: 80,sortable:false}, 
			{ label: '可进仓数量', name: 'KJCSL', index: 'KJCSL', width: 80,sortable:false},
			{ label: '<span style="color:blue">进仓数量</span>', name: 'IN_QTY', index: 'IN_QTY', width: 80,sortable:false,editable:true,editrules:{
   				number:true},editoptions:{
   	                dataInit:function(e){
   	                    $(e).blur(function(e){
   	                    	iceil_add(this)
   	                    });
   	                }
   	            }},
			{ label: '单位', name: 'UNIT', index: 'UNIT', width: 60,sortable:false },
			{ label: '<span style="color:blue">装箱数量</span>', name: 'FULL_BOX_QTY', index: 'FULL_BOX_QTY', width: 80,sortable:false,editable:true,editrules:{
   				number:true},editoptions:{
   	                dataInit:function(e){
   	                    $(e).blur(function(e){
   	                    	iceil_add2(this)
   	                    });
   	                }
   	            }},
			{ label: '件数', name: 'BOX_COUNT', index: 'BOX_COUNT', width: 60,sortable:false },
			{ label: '储位', name: 'BIN_CODE', index: 'BIN_CODE', width: 80,sortable:false },
			{ label: '<span style="color:blue">需求跟踪号</span>', name: 'BEDNR', index: 'BEDNR', width: 120,sortable:false,editable:true},
			{ label: '<span style="color:blue">生产日期</span>', name: 'PRDDT', index: 'PRDDT', width: 100,sortable:false,editable:true,editoptions:{
                dataInit:function(e){
                    $(e).click(function(e){
                    	WdatePicker({dateFmt:'yyyy-MM-dd'})
                    });
                }
            }},
			{ label: '<span style="color:blue">物流器具</span>', name: 'LT_WARE', index: 'LT_WARE', width: 80,editable:true,sortable:false },
			{ label: '<span style="color:blue">车间</span>', name: 'WORKSHOP', index: 'WORKSHOP', width: 80,editable:true,sortable:false },
			{ label: '<span style="color:blue">车型</span>', name: 'CAR_TYPE', index: 'CAR_TYPE', width: 80,editable:true,sortable:false },
			{ label: '<span style="color:blue">模具编号</span>', name: 'MOULD_NO', index: 'MOULD_NO', width: 80,editable:true,sortable:false },
			{ label: '产品图号', name: 'CPTH', index: 'CPTH', width: 80,sortable:false},
			{ label: '<span style="color:blue">作业员</span>', name: 'ZYY', index: 'ZYY', width: 80,editable:true,sortable:false },
			{ label: '<span style="color:blue">工位<span>', name: 'DIS_STATION', index: 'DIS_STATION', width: 80,editable:true,sortable:false },
			{ label: '<span style="color:blue">班次</span>', name: 'WORKGROUP_NO', index: 'WORKGROUP_NO', width: 80,editable:true,sortable:false },
			{ label: '仓管员', name: 'WH_MANAGER', index: 'WH_MANAGER', width: 100,sortable:false },
			{ label: '<span style="color:blue">备注</span>', name: 'REMARK', index: 'REMARK', width: 160,editable:true,sortable:false},
			{ label: '销售订单号', name: 'KDAUF', index: 'KDAUF', width: 100,sortable:false },
			{ label: '销售订单行项目号', name: 'KDPOS', index: 'KDPOS', width: 110,sortable:false },
			
			{ label: '接收工厂', name: 'RECWERKS', index: 'RECWERKS', width: 100,hidden:true },
			{ label: '接收仓库号', name: 'WHNUMBER', index: 'WHNUMBER', width: 100,hidden:true },
			{ label: '接收库位', name: 'RLGORT', index: 'RLGORT', width: 100,hidden:true },
			{ label: '供应商代码', name: 'LIFNR', index: 'LIFNR', width: 100,hidden:true },
			{ label: '供应商名称', name: 'LIKTX', index: 'LIKTX', width: 160 ,hidden:true},
			{ label: '', name: 'INBOUND_TYPE', index: 'INBOUND_TYPE', width: 160,hidden:true },
			{ label: '', name: 'BATCH', index: 'BATCH', width: 160,hidden:true },
			{ label: '', name: 'urgentFlag', index: 'urgentFlag', width: 160,hidden:true },
			{ label: '', name: 'FULL_BOX_QTY_FLAG', index: 'FULL_BOX_QTY_FLAG', width: 160,hidden:true }
			
        ],
        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
		},
		onSelectAll:function(rowids,status){},
		onSelectRow:function(rowid,status){},
        viewrecords: true,
        cellEdit:true,
        cellurl:'#',
        cellsubmit:'clientArray',
        autowidth:true,
        shrinkToFit:false,  
        autoScroll: true, 
        multiselect: true,
        showCheckbox:true,
        ajaxSuccess:function(){
        },
        
        gridComplete:function(){
        	//紧急物料（urgentFlag不为空的）设置自定义样式
            var ids = $("#dataGrid").getDataIDs();
            for(var i=0;i<ids.length;i++){
                var rowData = $("#dataGrid").getRowData(ids[i]);
                if(rowData.urgentFlag != null && rowData.urgentFlag !='' && rowData.urgentFlag != undefined){
                	//如果是紧急物料设置自定义的颜色
                    $('#'+ids[i]).find("td").addClass("urgent-select");
                }
                if(rowData.FULL_BOX_QTY_FLAG!='X'){//没有维护包装箱数量的，可以修改
                	$("#dataGrid").jqGrid('setCell', i+1, 'FULL_BOX_QTY', '', 'not-editable-cell');
                }
               
            }
        },
        
        afterSaveCell:function(rowid, cellname, value, iRow, iCol){},
		beforeSelectRow: function (rowid, e) {  //点击复选框才选中行
			   var $myGrid = $(this),  
			   i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),  
			   cm = $myGrid.jqGrid('getGridParam', 'colModel'); 
			   if(cm[i].name == 'cb'){
				   $('#dataGrid').jqGrid('setSelection',rowid);
			   }			   		
			   return (cm[i].name == 'cb'); 
			  
			}
    });
}


function dataGrid521(){
	listdata.length=0;
	$("#dataGrid").jqGrid('GridUnload');
	$("#dataGrid").dataGrid({
    	url: baseUrl + 'in/wmsinternalbound/listMo521',
    	datatype: "local",
		data: listdata,
        colModel: [
            { label: '<span style="color:blue">订单号</span>', name: 'IO_NO', index: 'IO_NO', width: 100,sortable:false,editable:true },
			{ label: '<span style="color:blue">物料号</span>', name: 'MATERIAL', index: 'MATERIAL', width: 100,sortable:false,editable:true },
			{ label: '物料描述', name: 'MATERIALDESC', index: 'MATERIALDESC', width: 200,sortable:false }, 
			{ label: '<span style="color:blue">进仓数量</span>', name: 'IN_QTY', index: 'IN_QTY', width: 80,editable:true,sortable:false,editrules:{
   				number:true},editoptions:{
   	                dataInit:function(e){
   	                    $(e).blur(function(e){
   	                    	iceil_add(this)
   	                    });
   	                }
   	            }},
			{ label: '单位', name: 'UNIT', index: 'UNIT', width: 60,sortable:false },
			{ label: '装箱数量', name: 'FULL_BOX_QTY', index: 'FULL_BOX_QTY', width: 80,sortable:false,editable:true,editrules:{
   				number:true},editoptions:{
   	                dataInit:function(e){
   	                    $(e).blur(function(e){
   	                    	iceil_add2(this)
   	                    });
   	                }
   	            }},
			{ label: '件数', name: 'BOX_COUNT', index: 'BOX_COUNT', width: 60,sortable:false },
			{ label: '储位', name: 'BIN_CODE', index: 'BIN_CODE', width: 80,sortable:false },
			{ label: '需求跟踪号', name: 'BEDNR', index: 'BEDNR', width: 120,editable:true,sortable:false},
			{ label: '生产日期', name: 'PRDDT', index: 'PRDDT', width: 100,sortable:false,editable:true,editoptions:{
                dataInit:function(e){
                    $(e).click(function(e){
                    	WdatePicker({dateFmt:'yyyy-MM-dd'})
                    });
                }
            }},
			{ label: '物流器具', name: 'LT_WARE', index: 'LT_WARE', width: 80,editable:true,sortable:false },
			{ label: '车间', name: 'WORKSHOP', index: 'WORKSHOP', width: 80,editable:true,sortable:false },
			{ label: '车型', name: 'CAR_TYPE', index: 'CAR_TYPE', width: 80,editable:true,sortable:false },
			{ label: '模具编号', name: 'MOULD_NO', index: 'MOULD_NO', width: 80,editable:true,sortable:false },
			{ label: '产品图号', name: 'CPTH', index: 'CPTH', width: 100,sortable:false},
			{ label: '作业员', name: 'ZYY', index: 'ZYY', width: 80,editable:true,sortable:false },
			{ label: '工位', name: 'DIS_STATION', index: 'DIS_STATION', width: 80,editable:true,sortable:false },
			{ label: '班次', name: 'WORKGROUP_NO', index: 'WORKGROUP_NO', width: 80,editable:true,sortable:false },
			{ label: '仓管员', name: 'WH_MANAGER', index: 'WH_MANAGER', width: 90,sortable:false },
			{ label: '备注', name: 'REMARK', index: 'REMARK', width: 160,editable:true,sortable:false},
			
			{ label: '接收工厂', name: 'RECWERKS', index: 'RECWERKS', width: 100,hidden:true },
			{ label: '接收仓库号', name: 'WHNUMBER', index: 'WHNUMBER', width: 100,hidden:true },
			{ label: '接收库位', name: 'RLGORT', index: 'RLGORT', width: 100,hidden:true },
			{ label: '', name: 'INBOUND_TYPE', index: 'INBOUND_TYPE', width: 160,hidden:true },
			{ label: '', name: 'urgentFlag', index: 'urgentFlag', width: 160,hidden:true }
			
        ],
        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
        	console.info(">>?Mmm");
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
		},
		onSelectAll:function(rowids,status){},
		onSelectRow:function(rowid,status){},
        viewrecords: true,
        cellEdit:true,
        cellurl:'#',
        cellsubmit:'clientArray',
        autowidth:true,
        shrinkToFit:false,  
        autoScroll: true, 
        multiselect: true,
        showCheckbox:true,
        ajaxSuccess:function(){
        },
        
        gridComplete:function(){
        	//紧急物料（urgentFlag不为空的）设置自定义样式
            var ids = $("#dataGrid").getDataIDs();
            for(var i=0;i<ids.length;i++){
                var rowData = $("#dataGrid").getRowData(ids[i]);
                if(rowData.urgentFlag != null && rowData.urgentFlag !='' && rowData.urgentFlag != undefined){
                	//如果是紧急物料设置自定义的颜色
                    $('#'+ids[i]).find("td").addClass("urgent-select");
                }
            }
        },
        afterSaveCell:function(rowid, cellname, value, iRow, iCol){
        	
        },
		beforeSelectRow: function (rowid, e) {  //点击复选框才选中行
			   var $myGrid = $(this),  
			   i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),  
			   cm = $myGrid.jqGrid('getGridParam', 'colModel');
			   vm.rowid = rowid;
			   if(cm[i].name == 'cb'){
				   $('#dataGrid').jqGrid('setSelection',rowid);
			   }			   		
			   return (cm[i].name == 'cb');
			  
			}
    });
}



function dataGridCo101(){
	listdata.length=0;
	$("#dataGrid").jqGrid('GridUnload');
	$("#dataGrid").jqGrid("clearGridData", true);
	$("#dataGrid").dataGrid({
    	url: baseUrl + 'in/wmsinternalbound/listCo101',
    	datatype: "local",
		data: listdata,
        colModel: [
            { label: '<span style="color:blue">订单号</span>', name: 'IO_NO', index: 'IO_NO', width: 100,sortable:false },
			{ label: '<span style="color:blue">物料号</span>', name: 'MATERIAL', index: 'MATERIAL', width: 100,sortable:false,editable:true },
			{ label: '物料描述', name: 'MATERIALDESC', index: 'MATERIALDESC', width: 200,sortable:false }, 
			{ label: '<span style="color:blue">进仓数量</span>', name: 'IN_QTY', index: 'IN_QTY', width: 80,editable:true,sortable:false,editrules:{
   				number:true},editoptions:{
   	                dataInit:function(e){
   	                    $(e).blur(function(e){
   	                    	iceil_add(this)
   	                    });
   	                }
   	            }},
			{ label: '单位', name: 'UNIT', index: 'UNIT', width: 60,sortable:false },
            { label: '装箱数量', name: 'FULL_BOX_QTY', index: 'FULL_BOX_QTY', width: 80,sortable:false,editable:true,editrules:{
                    number:true},editoptions:{
                    dataInit:function(e){
                        $(e).blur(function(e){
                            iceil_add2(this)
                        });
                    }
                }},
			{ label: '件数', name: 'BOX_COUNT', index: 'BOX_COUNT', width: 60,sortable:false },
			{ label: '储位', name: 'BIN_CODE', index: 'BIN_CODE', width: 80,sortable:false },
			{ label: '需求跟踪号', name: 'BEDNR', index: 'BEDNR', width: 120,editable:true,sortable:false},
			{ label: '生产日期', name: 'PRDDT', index: 'PRDDT', width: 100,editable:true,sortable:false,editoptions:{
                dataInit:function(e){
                    $(e).click(function(e){
                    	WdatePicker({dateFmt:'yyyy-MM-dd'})
                    });
                }
            }},
			{ label: '物流器具', name: 'LT_WARE', index: 'LT_WARE', width: 80,editable:true,sortable:false },
			{ label: '车间', name: 'WORKSHOP', index: 'WORKSHOP', width: 80,editable:true,sortable:false },
			{ label: '车型', name: 'CAR_TYPE', index: 'CAR_TYPE', width: 80,editable:true,sortable:false },
			{ label: '模具编号', name: 'MOULD_NO', index: 'MOULD_NO', width: 80,editable:true,sortable:false },
			{ label: '产品图号', name: 'CPTH', index: 'CPTH', width: 100,sortable:false},
			{ label: '作业员', name: 'ZYY', index: 'ZYY', width: 80,editable:true,sortable:false },
			{ label: '工位', name: 'DIS_STATION', index: 'DIS_STATION', width: 80,editable:true,sortable:false },
			{ label: '班次', name: 'WORKGROUP_NO', index: 'WORKGROUP_NO', width: 80,editable:true,sortable:false },
			{ label: '仓管员', name: 'WH_MANAGER', index: 'WH_MANAGER', width: 90,sortable:false },
			{ label: '备注', name: 'REMARK', index: 'REMARK', width: 160,editable:true,sortable:false},
			
			{ label: '接收工厂', name: 'RECWERKS', index: 'RECWERKS', width: 100,hidden:true },
			{ label: '接收仓库号', name: 'WHNUMBER', index: 'WHNUMBER', width: 100,hidden:true },
			{ label: '接收库位', name: 'RLGORT', index: 'RLGORT', width: 100,hidden:true },
			{ label: '', name: 'INBOUND_TYPE', index: 'INBOUND_TYPE', width: 160,hidden:true },
			{ label: '', name: 'urgentFlag', index: 'urgentFlag', width: 160,hidden:true }
			
        ],
        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
		},
		onSelectAll:function(rowids,status){},
		onSelectRow:function(rowid,status){},
        viewrecords: true,
        cellEdit:true,
        cellurl:'#',
        cellsubmit:'clientArray',
        autowidth:true,
        shrinkToFit:false,  
        autoScroll: true, 
        multiselect: true,
        showCheckbox:true,
        ajaxSuccess:function(){
        },
        
        gridComplete:function(){
        	//紧急物料（urgentFlag不为空的）设置自定义样式
            var ids = $("#dataGrid").getDataIDs();
            for(var i=0;i<ids.length;i++){
                var rowData = $("#dataGrid").getRowData(ids[i]);
                if(rowData.urgentFlag != null && rowData.urgentFlag !='' && rowData.urgentFlag != undefined){
                	//如果是紧急物料设置自定义的颜色
                    $('#'+ids[i]).find("td").addClass("urgent-select");
                }
            }
        },
        afterSaveCell:function(rowid, cellname, value, iRow, iCol){},
        onCellSelect : function(rowid,iCol,cellcontent,e){//如果装箱数量大于0，则不可编辑
   	 		var rec =  $("#dataGrid").jqGrid('getRowData', rowid);
	  	    if (rec['FULL_BOX_QTY']>0) {//过滤条件
	          	$("#dataGrid").jqGrid('setCell', rowid, 'FULL_BOX_QTY', '', 'not-editable-cell');
	          	
	        }
	   	},
		beforeSelectRow: function (rowid, e) {  //点击复选框才选中行
           var $myGrid = $(this),
           i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),
           cm = $myGrid.jqGrid('getGridParam', 'colModel');
            vm.rowid = rowid;
           if(cm[i].name == 'cb'){
               $('#dataGrid').jqGrid('setSelection',rowid);
           }
           return (cm[i].name == 'cb');

        }
    });
}


function dataGrid202(){
	listdata.length=0;
	$("#dataGrid").jqGrid('GridUnload');
	$("#dataGrid").jqGrid("clearGridData", true);
	$("#dataGrid").dataGrid({
    	url: baseUrl + 'in/wmsinternalbound/list202',
    	datatype: "local",
		data: listdata,
        colModel: [
            { label: '成本中心', name: 'COST_CENTER', index: 'COST_CENTER', width: 100,sortable:false },
			{ label: '物料号', name: 'MATERIAL', index: 'MATERIAL', width: 100,sortable:false,editable:true  },
			{ label: '物料描述', name: 'MATERIALDESC', index: 'MATERIALDESC', width: 200,sortable:false},
			{ label: '进仓数量', name: 'IN_QTY', index: 'IN_QTY', width: 80,editable:true,sortable:false,editrules:{
   				number:true},editoptions:{
   	                dataInit:function(e){
   	                    $(e).blur(function(e){
   	                    	iceil_add(this)
   	                    });
   	                }
   	            }},
			{ label: '单位', name: 'UNIT', index: 'UNIT', width: 60,sortable:false },
			{ label: '装箱数量', name: 'FULL_BOX_QTY', index: 'FULL_BOX_QTY', width: 80,sortable:false,editable:true,editrules:{
   				number:true},editoptions:{
   	                dataInit:function(e){
   	                    $(e).blur(function(e){
   	                    	iceil_add2(this)
   	                    });
   	                }
   	            }},
			{ label: '件数', name: 'BOX_COUNT', index: 'BOX_COUNT', width: 60,sortable:false },
			{ label: '储位', name: 'BIN_CODE', index: 'BIN_CODE', width: 80,sortable:false },
			{ label: '需求跟踪号', name: 'BEDNR', index: 'BEDNR', width: 120,editable:true,sortable:false},
			{ label: '生产日期', name: 'PRDDT', index: 'PRDDT', width: 100,sortable:false,editable:true,editoptions:{
                dataInit:function(e){
                    $(e).click(function(e){
                    	WdatePicker({dateFmt:'yyyy-MM-dd'})
                    });
                }
            }},
			{ label: '物流器具', name: 'LT_WARE', index: 'LT_WARE', width: 80,editable:true,sortable:false },
			{ label: '车间', name: 'WORKSHOP', index: 'WORKSHOP', width: 80,editable:true,sortable:false },
			{ label: '车型', name: 'CAR_TYPE', index: 'CAR_TYPE', width: 80,editable:true,sortable:false },
			{ label: '模具编号', name: 'MOULD_NO', index: 'MOULD_NO', width: 80,editable:true,sortable:false },
			{ label: '产品图号', name: 'CPTH', index: 'CPTH', width: 100,sortable:false},
			{ label: '作业员', name: 'ZYY', index: 'ZYY', width: 80,editable:true,sortable:false },
			{ label: '工位', name: 'DIS_STATION', index: 'DIS_STATION', width: 80,editable:true,sortable:false },
			{ label: '班次', name: 'WORKGROUP_NO', index: 'WORKGROUP_NO', width: 80,editable:true,sortable:false },
			{ label: '仓管员', name: 'WH_MANAGER', index: 'WH_MANAGER', width: 90,sortable:false },
			{ label: '备注', name: 'REMARK', index: 'REMARK', width: 160,editable:true,sortable:false},
			
			{ label: '接收工厂', name: 'RECWERKS', index: 'RECWERKS', width: 100,hidden:true },
			{ label: '接收仓库号', name: 'WHNUMBER', index: 'WHNUMBER', width: 100,hidden:true },
			{ label: '接收库位', name: 'RLGORT', index: 'RLGORT', width: 100,hidden:true },
			{ label: '', name: 'INBOUND_TYPE', index: 'INBOUND_TYPE', width: 160,hidden:true },
			{ label: '', name: 'urgentFlag', index: 'urgentFlag', width: 160,hidden:true }
			
        ],
        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
		},
		onSelectAll:function(rowids,status){},
		onSelectRow:function(rowid,status){},
        viewrecords: true,
        cellEdit:true,
        cellurl:'#',
        cellsubmit:'clientArray',
        autowidth:true,
        shrinkToFit:false,  
        autoScroll: true, 
        multiselect: true,
        showCheckbox:true,
        ajaxSuccess:function(){
        },
        
        gridComplete:function(){
        	//紧急物料（urgentFlag不为空的）设置自定义样式
            var ids = $("#dataGrid").getDataIDs();
            for(var i=0;i<ids.length;i++){
                var rowData = $("#dataGrid").getRowData(ids[i]);
                if(rowData.urgentFlag != null && rowData.urgentFlag !='' && rowData.urgentFlag != undefined){
                	//如果是紧急物料设置自定义的颜色
                    $('#'+ids[i]).find("td").addClass("urgent-select");
                }
            }
        },
        afterSaveCell:function(rowid, cellname, value, iRow, iCol){},
		beforeSelectRow: function (rowid, e) {  //点击复选框才选中行
           var $myGrid = $(this),
           i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),
           cm = $myGrid.jqGrid('getGridParam', 'colModel');
            vm.rowid = rowid;
           if(cm[i].name == 'cb'){
               $('#dataGrid').jqGrid('setSelection',rowid);
           }
           return (cm[i].name == 'cb');

        }
    });
}


function dataGrid222(){
	listdata.length=0;
	$("#dataGrid").jqGrid('GridUnload');
	$("#dataGrid").jqGrid("clearGridData", true);
	$("#dataGrid").dataGrid({
    	url: baseUrl + 'in/wmsinternalbound/list222',
    	datatype: "local",
		data: listdata,
        colModel: [
            { label: '元素号', name: 'WBS', index: 'WBS', width: 160,sortable:false },
			{ label: '物料号', name: 'MATERIAL', index: 'MATERIAL', width: 100,sortable:false,editable:true },
			{ label: '物料描述', name: 'MATERIALDESC', index: 'MATERIALDESC', width: 200,sortable:false }, 
			{ label: '进仓数量', name: 'IN_QTY', index: 'IN_QTY', width: 80,sortable:false,editable:true,editrules:{
   				number:true},editoptions:{
   	                dataInit:function(e){
   	                    $(e).blur(function(e){
   	                    	iceil_add(this)
   	                    });
   	                }
   	            }},
			{ label: '单位', name: 'UNIT', index: 'UNIT', width: 60,sortable:false },
			{ label: '装箱数量', name: 'FULL_BOX_QTY', index: 'FULL_BOX_QTY', width: 80,sortable:false,editable:true,editrules:{
   				number:true},editoptions:{
   	                dataInit:function(e){
   	                    $(e).blur(function(e){
   	                    	iceil_add2(this)
   	                    });
   	                }
   	            }},
			{ label: '件数', name: 'BOX_COUNT', index: 'BOX_COUNT', width: 60,sortable:false },
			{ label: '储位', name: 'BIN_CODE', index: 'BIN_CODE', width: 80,sortable:false },
			{ label: '需求跟踪号', name: 'BEDNR', index: 'BEDNR', width: 120,editable:true,sortable:false},
			{ label: '生产日期', name: 'PRDDT', index: 'PRDDT', width: 100,sortable:false,editable:true,editoptions:{
                dataInit:function(e){
                    $(e).click(function(e){
                    	WdatePicker({dateFmt:'yyyy-MM-dd'})
                    });
                }
            }},
			{ label: '物流器具', name: 'LT_WARE', index: 'LT_WARE', width: 80,editable:true,sortable:false },
			{ label: '车间', name: 'WORKSHOP', index: 'WORKSHOP', width: 80,editable:true,sortable:false },
			{ label: '车型', name: 'CAR_TYPE', index: 'CAR_TYPE', width: 80,editable:true,sortable:false },
			{ label: '模具编号', name: 'MOULD_NO', index: 'MOULD_NO', width: 80,editable:true,sortable:false },
			{ label: '产品图号', name: 'CPTH', index: 'CPTH', width: 100,sortable:false},
			{ label: '作业员', name: 'ZYY', index: 'ZYY', width: 80,editable:true,sortable:false },
			{ label: '工位', name: 'DIS_STATION', index: 'DIS_STATION', width: 80,editable:true,sortable:false },
			{ label: '班次', name: 'WORKGROUP_NO', index: 'WORKGROUP_NO', width: 80,editable:true,sortable:false },
			{ label: '仓管员', name: 'WH_MANAGER', index: 'WH_MANAGER', width: 90,sortable:false },
			{ label: '备注', name: 'REMARK', index: 'REMARK', width: 160,editable:true,sortable:false},
			
			{ label: '接收工厂', name: 'RECWERKS', index: 'RECWERKS', width: 100,hidden:true },
			{ label: '接收仓库号', name: 'WHNUMBER', index: 'WHNUMBER', width: 100,hidden:true },
			{ label: '接收库位', name: 'RLGORT', index: 'RLGORT', width: 100,hidden:true },
			{ label: '', name: 'INBOUND_TYPE', index: 'INBOUND_TYPE', width: 160,hidden:true },
			{ label: '', name: 'urgentFlag', index: 'urgentFlag', width: 160,hidden:true }
			
        ],
        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
		},
		onSelectAll:function(rowids,status){},
		onSelectRow:function(rowid,status){},
        viewrecords: true,
        cellEdit:true,
        cellurl:'#',
        cellsubmit:'clientArray',
        autowidth:true,
        shrinkToFit:false,  
        autoScroll: true, 
        multiselect: true,
        showCheckbox:true,
        ajaxSuccess:function(){
        },
        
        gridComplete:function(){
        	//紧急物料（urgentFlag不为空的）设置自定义样式
            var ids = $("#dataGrid").getDataIDs();
            for(var i=0;i<ids.length;i++){
                var rowData = $("#dataGrid").getRowData(ids[i]);
                if(rowData.urgentFlag != null && rowData.urgentFlag !='' && rowData.urgentFlag != undefined){
                	//如果是紧急物料设置自定义的颜色
                    $('#'+ids[i]).find("td").addClass("urgent-select");
                }
            }
        },
        afterSaveCell:function(rowid, cellname, value, iRow, iCol){},
		beforeSelectRow: function (rowid, e) {  //点击复选框才选中行
           var $myGrid = $(this),
           i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),
           cm = $myGrid.jqGrid('getGridParam', 'colModel');
            vm.rowid = rowid;
           if(cm[i].name == 'cb'){
               $('#dataGrid').jqGrid('setSelection',rowid);
           }
           return (cm[i].name == 'cb');

        }
    });
}

function dataGrid262(){
	listdata.length=0;
	$("#dataGrid").jqGrid('GridUnload');
	$("#dataGrid").jqGrid("clearGridData", true);
	$("#dataGrid").dataGrid({
    	url: baseUrl + 'in/wmsinternalbound/list262',
    	datatype: "local",
		data: listdata,
        colModel: [
            { label: '订单号', name: 'IO_NO', index: 'IO_NO', width: 160,sortable:false },
			{ label: '物料号', name: 'MATERIAL', index: 'MATERIAL', width: 100,sortable:false }, 
			{ label: '物料描述', name: 'MATERIALDESC', index: 'MATERIALDESC', width: 200,sortable:false }, 
			{ label: '进仓数量', name: 'IN_QTY', index: 'IN_QTY', width: 80,sortable:false,editable:true,editrules:{
   				number:true},editoptions:{
   	                dataInit:function(e){
   	                    $(e).blur(function(e){
   	                    	iceil_add(this)
   	                    });
   	                }
   	            }},
			{ label: '单位', name: 'UNIT', index: 'UNIT', width: 60,sortable:false },
			{ label: '装箱数量', name: 'FULL_BOX_QTY', index: 'FULL_BOX_QTY', width: 80,sortable:false,editable:true,editrules:{
   				number:true},editoptions:{
   	                dataInit:function(e){
   	                    $(e).blur(function(e){
   	                    	iceil_add2(this)
   	                    });
   	                }
   	            }},
			{ label: '件数', name: 'BOX_COUNT', index: 'BOX_COUNT', width: 60,sortable:false },
			{ label: '储位', name: 'BIN_CODE', index: 'BIN_CODE', width: 80,sortable:false },
			{ label: '需求跟踪号', name: 'BEDNR', index: 'BEDNR', width: 120,editable:true,sortable:false},
			{ label: '生产日期', name: 'PRDDT', index: 'PRDDT', width: 100,sortable:false,editable:true,editoptions:{
                dataInit:function(e){
                    $(e).click(function(e){
                    	WdatePicker({dateFmt:'yyyy-MM-dd'})
                    });
                }
            }},
			{ label: '物流器具', name: 'LT_WARE', index: 'LT_WARE', width: 80,editable:true,sortable:false },
			{ label: '车间', name: 'WORKSHOP', index: 'WORKSHOP', width: 80,editable:true,sortable:false },
			{ label: '车型', name: 'CAR_TYPE', index: 'CAR_TYPE', width: 80,editable:true,sortable:false },
			{ label: '模具编号', name: 'MOULD_NO', index: 'MOULD_NO', width: 80,editable:true,sortable:false },
			{ label: '产品图号', name: 'CPTH', index: 'CPTH', width: 100,sortable:false},
			{ label: '作业员', name: 'ZYY', index: 'ZYY', width: 80,editable:true,sortable:false },
			{ label: '工位', name: 'DIS_STATION', index: 'DIS_STATION', width: 80,editable:true,sortable:false },
			{ label: '班次', name: 'WORKGROUP_NO', index: 'WORKGROUP_NO', width: 80,editable:true,sortable:false },
			{ label: '仓管员', name: 'WH_MANAGER', index: 'WH_MANAGER', width: 90,sortable:false },
			{ label: '备注', name: 'REMARK', index: 'REMARK', width: 160,editable:true,sortable:false},
			
			{ label: '接收工厂', name: 'RECWERKS', index: 'RECWERKS', width: 100,hidden:true },
			{ label: '接收仓库号', name: 'WHNUMBER', index: 'WHNUMBER', width: 100,hidden:true },
			{ label: '接收库位', name: 'RLGORT', index: 'RLGORT', width: 100,hidden:true },
			{ label: '', name: 'INBOUND_TYPE', index: 'INBOUND_TYPE', width: 160,hidden:true },
			{ label: '', name: 'urgentFlag', index: 'urgentFlag', width: 160,hidden:true }
			
        ],
        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
		},
		onSelectAll:function(rowids,status){},
		onSelectRow:function(rowid,status){},
        viewrecords: true,
        cellEdit:true,
        cellurl:'#',
        cellsubmit:'clientArray',
        autowidth:true,
        shrinkToFit:false,  
        autoScroll: true, 
        multiselect: true,
        showCheckbox:true,
        ajaxSuccess:function(){
        },
        
        gridComplete:function(){
        	//紧急物料（urgentFlag不为空的）设置自定义样式
            var ids = $("#dataGrid").getDataIDs();
            for(var i=0;i<ids.length;i++){
                var rowData = $("#dataGrid").getRowData(ids[i]);
                if(rowData.urgentFlag != null && rowData.urgentFlag !='' && rowData.urgentFlag != undefined){
                	//如果是紧急物料设置自定义的颜色
                    $('#'+ids[i]).find("td").addClass("urgent-select");
                }
            }
        },
        afterSaveCell:function(rowid, cellname, value, iRow, iCol){},
		beforeSelectRow: function (rowid, e) {  //点击复选框才选中行
           var $myGrid = $(this),
           i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),
           cm = $myGrid.jqGrid('getGridParam', 'colModel');
            vm.rowid = rowid;
           if(cm[i].name == 'cb'){
               $('#dataGrid').jqGrid('setSelection',rowid);
           }
           return (cm[i].name == 'cb');

        }
    });
}

function dataGridA101(){
	listdata.length=0;
	$("#dataGrid").jqGrid('GridUnload');
	$("#dataGrid").dataGrid({
    	url: baseUrl + 'in/wmsinternalbound/listA101',
    	datatype: "local",
		data: listdata,
        colModel: [
            { label: '生产订单', name: 'IO_NO', index: 'IO_NO', width: 100,sortable:false },
			{ label: '物料号', name: 'MATERIAL', index: 'MATERIAL', width: 100,sortable:false }, 
			{ label: '物料描述', name: 'MATERIALDESC', index: 'MATERIALDESC', width: 200,sortable:false }, 
			{ label: '订单数量', name: 'DDSL', index: 'DDSL', width: 80,sortable:false}, 
			{ label: '还需核销数量', name: 'HX_QTY', index: 'HX_QTY', width: 100,sortable:false},
			{ label: '<span style="color:blue">进仓数量</span>', name: 'IN_QTY', index: 'IN_QTY', width: 80,sortable:false,editable:true,editrules:{
   				number:true},editoptions:{
   	                dataInit:function(e){
   	                    $(e).blur(function(e){
   	                    	iceil_add(this)
   	                    });
   	                }
   	            }},
			{ label: '单位', name: 'UNIT', index: 'UNIT', width: 60,sortable:false },
			{ label: '<span style="color:blue">装箱数量</span>', name: 'FULL_BOX_QTY', index: 'FULL_BOX_QTY', width: 80,sortable:false,editable:true,editrules:{
   				number:true},editoptions:{
   	                dataInit:function(e){
   	                    $(e).blur(function(e){
   	                    	iceil_add2(this)
   	                    });
   	                }
   	            }},
			{ label: '件数', name: 'BOX_COUNT', index: 'BOX_COUNT', width: 60,sortable:false },
			{ label: '储位', name: 'BIN_CODE', index: 'BIN_CODE', width: 80,sortable:false },
			{ label: '<span style="color:blue">需求跟踪号</span>', name: 'BEDNR', index: 'BEDNR', width: 120,sortable:false,editable:true},
			{ label: '<span style="color:blue">生产日期</span>', name: 'PRDDT', index: 'PRDDT', width: 100,sortable:false,editable:true,editoptions:{
                dataInit:function(e){
                    $(e).click(function(e){
                    	WdatePicker({dateFmt:'yyyy-MM-dd'})
                    });
                }
            }},
			{ label: '<span style="color:blue">物流器具</span>', name: 'LT_WARE', index: 'LT_WARE', width: 80,editable:true,sortable:false },
			{ label: '<span style="color:blue">车间</span>', name: 'WORKSHOP', index: 'WORKSHOP', width: 80,editable:true,sortable:false },
			{ label: '<span style="color:blue">车型</span>', name: 'CAR_TYPE', index: 'CAR_TYPE', width: 80,editable:true,sortable:false },
			{ label: '<span style="color:blue">模具编号</span>', name: 'MOULD_NO', index: 'MOULD_NO', width: 80,editable:true,sortable:false },
			{ label: '产品图号', name: 'CPTH', index: 'CPTH', width: 80,sortable:false},
			{ label: '<span style="color:blue">作业员</span>', name: 'ZYY', index: 'ZYY', width: 80,editable:true,sortable:false },
			{ label: '<span style="color:blue">工位<span>', name: 'DIS_STATION', index: 'DIS_STATION', width: 80,editable:true,sortable:false },
			{ label: '<span style="color:blue">班次</span>', name: 'WORKGROUP_NO', index: 'WORKGROUP_NO', width: 80,editable:true,sortable:false },
			{ label: '仓管员', name: 'WH_MANAGER', index: 'WH_MANAGER', width: 100,sortable:false },
			{ label: '<span style="color:blue">备注</span>', name: 'REMARK', index: 'REMARK', width: 160,editable:true,sortable:false},
			
			{ label: '接收工厂', name: 'RECWERKS', index: 'RECWERKS', width: 100,hidden:true },
			{ label: '接收仓库号', name: 'WHNUMBER', index: 'WHNUMBER', width: 100,hidden:true },
			{ label: '接收库位', name: 'RLGORT', index: 'RLGORT', width: 100,hidden:true },
			{ label: '行项目号', name: 'POSNR', index: 'POSNR', width: 100,hidden:true },
			{ label: '', name: 'INBOUND_TYPE', index: 'INBOUND_TYPE', width: 160,hidden:true },
			{ label: '', name: 'urgentFlag', index: 'urgentFlag', width: 160,hidden:true }
			
        ],
        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
		},
		onSelectAll:function(rowids,status){},
		onSelectRow:function(rowid,status){},
        viewrecords: true,
        cellEdit:true,
        cellurl:'#',
        cellsubmit:'clientArray',
        autowidth:true,
        shrinkToFit:false,  
        autoScroll: true, 
        multiselect: true,
        showCheckbox:true,
        ajaxSuccess:function(){
        },
        
        gridComplete:function(){
        	//紧急物料（urgentFlag不为空的）设置自定义样式
            var ids = $("#dataGrid").getDataIDs();
            for(var i=0;i<ids.length;i++){
                var rowData = $("#dataGrid").getRowData(ids[i]);
                if(rowData.urgentFlag != null && rowData.urgentFlag !='' && rowData.urgentFlag != undefined){
                	//如果是紧急物料设置自定义的颜色
                    $('#'+ids[i]).find("td").addClass("urgent-select");
                }
            }
        },
        afterSaveCell:function(rowid, cellname, value, iRow, iCol){},
		beforeSelectRow: function (rowid, e) {  //点击复选框才选中行
			   var $myGrid = $(this),  
			   i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),  
			   cm = $myGrid.jqGrid('getGridParam', 'colModel'); 
			   if(cm[i].name == 'cb'){
				   $('#dataGrid').jqGrid('setSelection',rowid);
			   }			   		
			   return (cm[i].name == 'cb'); 
			  
			}
    });
}

function dataGridA531(){
	listdata.length=0;
	$("#dataGrid").jqGrid('GridUnload');
	$("#dataGrid").dataGrid({
    	url: baseUrl + 'in/wmsinternalbound/listA531',
    	datatype: "local",
		data: listdata,
        colModel: [
            { label: '生产订单', name: 'IO_NO', index: 'IO_NO', width: 100,sortable:false },
			{ label: '物料号', name: 'MATERIAL', index: 'MATERIAL', width: 100,sortable:false }, 
			{ label: '物料描述', name: 'MATERIALDESC', index: 'MATERIALDESC', width: 200,sortable:false }, 
			{ label: '订单数量', name: 'DDSL', index: 'DDSL', width: 80,sortable:false}, 
			{ label: '还需核销数量', name: 'HX_QTY', index: 'HX_QTY', width: 100,sortable:false},
			{ label: '<span style="color:blue">进仓数量</span>', name: 'IN_QTY', index: 'IN_QTY', width: 80,sortable:false,editable:true,editrules:{
   				number:true},editoptions:{
   	                dataInit:function(e){
   	                    $(e).blur(function(e){
   	                    	iceil_add(this)
   	                    });
   	                }
   	            }},
			{ label: '单位', name: 'UNIT', index: 'UNIT', width: 60,sortable:false },
			{ label: '<span style="color:blue">装箱数量</span>', name: 'FULL_BOX_QTY', index: 'FULL_BOX_QTY', width: 80,sortable:false,editable:true,editrules:{
   				number:true},editoptions:{
   	                dataInit:function(e){
   	                    $(e).blur(function(e){
   	                    	iceil_add2(this)
   	                    });
   	                }
   	            }},
			{ label: '件数', name: 'BOX_COUNT', index: 'BOX_COUNT', width: 60,sortable:false },
			{ label: '储位', name: 'BIN_CODE', index: 'BIN_CODE', width: 80,sortable:false },
			{ label: '<span style="color:blue">需求跟踪号</span>', name: 'BEDNR', index: 'BEDNR', width: 120,sortable:false,editable:true},
			{ label: '<span style="color:blue">生产日期</span>', name: 'PRDDT', index: 'PRDDT', width: 100,sortable:false,editable:true,editoptions:{
                dataInit:function(e){
                    $(e).click(function(e){
                    	WdatePicker({dateFmt:'yyyy-MM-dd'})
                    });
                }
            }},
			{ label: '<span style="color:blue">物流器具</span>', name: 'LT_WARE', index: 'LT_WARE', width: 80,editable:true,sortable:false },
			{ label: '<span style="color:blue">车间</span>', name: 'WORKSHOP', index: 'WORKSHOP', width: 80,editable:true,sortable:false },
			{ label: '<span style="color:blue">车型</span>', name: 'CAR_TYPE', index: 'CAR_TYPE', width: 80,editable:true,sortable:false },
			{ label: '<span style="color:blue">模具编号</span>', name: 'MOULD_NO', index: 'MOULD_NO', width: 80,editable:true,sortable:false },
			{ label: '产品图号', name: 'CPTH', index: 'CPTH', width: 80,sortable:false},
			{ label: '<span style="color:blue">作业员</span>', name: 'ZYY', index: 'ZYY', width: 80,editable:true,sortable:false },
			{ label: '<span style="color:blue">工位<span>', name: 'DIS_STATION', index: 'DIS_STATION', width: 80,editable:true,sortable:false },
			{ label: '<span style="color:blue">班次</span>', name: 'WORKGROUP_NO', index: 'WORKGROUP_NO', width: 80,editable:true,sortable:false },
			{ label: '仓管员', name: 'WH_MANAGER', index: 'WH_MANAGER', width: 100,sortable:false },
			{ label: '<span style="color:blue">备注</span>', name: 'REMARK', index: 'REMARK', width: 160,editable:true,sortable:false},
			
			{ label: '接收工厂', name: 'RECWERKS', index: 'RECWERKS', width: 100,hidden:true },
			{ label: '接收仓库号', name: 'WHNUMBER', index: 'WHNUMBER', width: 100,hidden:true },
			{ label: '接收库位', name: 'RLGORT', index: 'RLGORT', width: 100,hidden:true },
			{ label: '', name: 'RSNUM', index: 'RSNUM', width: 100,hidden:true },
			{ label: '', name: 'RSPOS', index: 'RSPOS', width: 100,hidden:true },
			{ label: '', name: 'INBOUND_TYPE', index: 'INBOUND_TYPE', width: 160,hidden:true },
			{ label: '', name: 'urgentFlag', index: 'urgentFlag', width: 160,hidden:true }
			
        ],
        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
		},
		onSelectAll:function(rowids,status){},
		onSelectRow:function(rowid,status){},
        viewrecords: true,
        cellEdit:true,
        cellurl:'#',
        cellsubmit:'clientArray',
        autowidth:true,
        shrinkToFit:false,  
        autoScroll: true, 
        multiselect: true,
        showCheckbox:true,
        ajaxSuccess:function(){
        },
        
        gridComplete:function(){
        	//紧急物料（urgentFlag不为空的）设置自定义样式
            var ids = $("#dataGrid").getDataIDs();
            for(var i=0;i<ids.length;i++){
                var rowData = $("#dataGrid").getRowData(ids[i]);
                if(rowData.urgentFlag != null && rowData.urgentFlag !='' && rowData.urgentFlag != undefined){
                	//如果是紧急物料设置自定义的颜色
                    $('#'+ids[i]).find("td").addClass("urgent-select");
                }
            }
        },
        afterSaveCell:function(rowid, cellname, value, iRow, iCol){},
		beforeSelectRow: function (rowid, e) {  //点击复选框才选中行
			   var $myGrid = $(this),  
			   i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),  
			   cm = $myGrid.jqGrid('getGridParam', 'colModel'); 
			   if(cm[i].name == 'cb'){
				   $('#dataGrid').jqGrid('setSelection',rowid);
			   }			   		
			   return (cm[i].name == 'cb'); 
			  
			}
    });
}

function dataGridpo101(){
	listdata.length=0;
	$("#dataGrid").jqGrid('GridUnload');
	$("#dataGrid").dataGrid({
    	url: baseUrl + 'in/wmsinternalbound/listpo101',
    	datatype: "local",
		data: listdata,
        colModel: [
            { label: '订单号', name: 'PO_NO', index: 'PO_NO', width: 100,sortable:false },
			{ label: '行项目号', name: 'PO_ITEM_NO', index: 'PO_ITEM_NO', width: 100,sortable:false }, 
			{ label: '物料号', name: 'MATERIAL', index: 'MATERIAL', width: 100,sortable:false },
			{ label: '物料描述', name: 'MATERIALDESC', index: 'MATERIALDESC', width: 200,sortable:false }, 
			{ label: '订单数量', name: 'MENGE', index: 'MENGE', width: 80,sortable:false}, 
			{ label: '<span style="color:blue">进仓数量</span>', name: 'IN_QTY', index: 'IN_QTY', width: 80,sortable:false,editable:true,editrules:{
   				number:true},editoptions:{
   	                dataInit:function(e){
   	                    $(e).blur(function(e){
   	                    	iceil_add(this)
   	                    });
   	                }
   	            }},
			{ label: '单位', name: 'UNIT', index: 'UNIT', width: 60,sortable:false },
			{ label: '<span style="color:blue">装箱数量</span>', name: 'FULL_BOX_QTY', index: 'FULL_BOX_QTY', width: 80,sortable:false,editable:true,editrules:{
   				number:true},editoptions:{
   	                dataInit:function(e){
   	                    $(e).blur(function(e){
   	                    	iceil_add2(this)
   	                    });
   	                }
   	            }},
			{ label: '件数', name: 'BOX_COUNT', index: 'BOX_COUNT', width: 60,sortable:false },
			{ label: '供应商代码', name: 'LIFNR', index: 'LIFNR', width: 100 },
			{ label: '供应商名称', name: 'LIKTX', index: 'LIKTX', width: 160 },
			{ label: '工厂', name: 'RECWERKS', index: 'RECWERKS', width: 100 },
			{ label: '仓库号', name: 'WHNUMBER', index: 'WHNUMBER', width: 100},
			{ label: '库位', name: 'RLGORT', index: 'RLGORT', width: 100},
			{ label: '储位', name: 'BIN_CODE', index: 'BIN_CODE', width: 80,sortable:false },
			{ label: '<span style="color:blue">需求跟踪号</span>', name: 'BEDNR', index: 'BEDNR', width: 120,sortable:false,editable:true},
			{ label: '', name: 'INBOUND_TYPE', index: 'INBOUND_TYPE', width: 160,hidden:true },
			{ label: '<span style="color:blue">生产日期</span>', name: 'PRDDT', index: 'PRDDT', width: 100,sortable:false,editable:true,editoptions:{
                dataInit:function(e){
                    $(e).click(function(e){
                    	WdatePicker({dateFmt:'yyyy-MM-dd'})
                    });
                }
            }},
            
			
            { label: '仓管员', name: 'WH_MANAGER', index: 'WH_MANAGER', width: 100,sortable:false },
            { label: '', name: 'BATCH', index: 'BATCH', width: 160,hidden:true },
			{ label: '<span style="color:blue">备注</span>', name: 'REMARK', index: 'REMARK', width: 160,editable:true,sortable:false},
            { label: '', name: 'FULL_BOX_QTY_FLAG', index: 'FULL_BOX_QTY_FLAG', width: 160,hidden:true }
			
        ],
        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
		},
		onSelectAll:function(rowids,status){},
		onSelectRow:function(rowid,status){},
        viewrecords: true,
        cellEdit:true,
        cellurl:'#',
        cellsubmit:'clientArray',
        autowidth:true,
        shrinkToFit:false,  
        autoScroll: true, 
        multiselect: true,
        showCheckbox:true,
        ajaxSuccess:function(){
        },
        
        gridComplete:function(){
        	//紧急物料（urgentFlag不为空的）设置自定义样式
            var ids = $("#dataGrid").getDataIDs();
            for(var i=0;i<ids.length;i++){
                var rowData = $("#dataGrid").getRowData(ids[i]);
                if(rowData.urgentFlag != null && rowData.urgentFlag !='' && rowData.urgentFlag != undefined){
                	//如果是紧急物料设置自定义的颜色
                    $('#'+ids[i]).find("td").addClass("urgent-select");
                }
                if(rowData.FULL_BOX_QTY_FLAG!='X'){//没有维护包装箱数量的，可以修改
                	$("#dataGrid").jqGrid('setCell', i+1, 'FULL_BOX_QTY', '', 'not-editable-cell');
                }
            }
        },
        afterSaveCell:function(rowid, cellname, value, iRow, iCol){},
		beforeSelectRow: function (rowid, e) {  //点击复选框才选中行
			   var $myGrid = $(this),  
			   i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),  
			   cm = $myGrid.jqGrid('getGridParam', 'colModel'); 
			   if(cm[i].name == 'cb'){
				   $('#dataGrid').jqGrid('setSelection',rowid);
			   }			   		
			   return (cm[i].name == 'cb'); 
			  
			}
    });
}

function dataGridmo262(){
	listdata.length=0;
	$("#dataGrid").jqGrid('GridUnload');
	$("#dataGrid").dataGrid({
    	url: baseUrl + 'in/wmsinternalbound/listmo262',
    	datatype: "local",
		data: listdata,
        colModel: [
			{ label: '物料号', name: 'MATERIAL', index: 'MATERIAL', width: 100,sortable:false,editable:true },
			{ label: '物料描述', name: 'MATERIALDESC', index: 'MATERIALDESC', width: 200,sortable:false }, 
			{ label: '<span style="color:blue">进仓数量</span>', name: 'IN_QTY', index: 'IN_QTY', width: 80,sortable:false,editable:true},
			{ label: '单位', name: 'UNIT', index: 'UNIT', width: 60,sortable:false },
			{ label: '<span style="color:blue">装箱数量</span>', name: 'FULL_BOX_QTY', index: 'FULL_BOX_QTY', width: 80,sortable:false,editable:true},
			{ label: '件数', name: 'BOX_COUNT', index: 'BOX_COUNT', width: 60,sortable:false },
			{ label: '储位', name: 'BIN_CODE', index: 'BIN_CODE', width: 80,sortable:false },
			{ label: '库位', name: 'RLGORT', index: 'RLGORT', width: 100},
			
			
			{ label: '<span style="color:blue">需求跟踪号</span>', name: 'BEDNR', index: 'BEDNR', width: 120,sortable:false,editable:true},
			{ label: '<span style="color:blue">生产日期</span>', name: 'PRDDT', index: 'PRDDT', width: 100,sortable:false,editable:true,editoptions:{
                dataInit:function(e){
                    $(e).click(function(e){
                    	WdatePicker({dateFmt:'yyyy-MM-dd'})
                    });
                }
            }},
			
            { label: '仓管员', name: 'WH_MANAGER', index: 'WH_MANAGER', width: 100,sortable:false },
			{ label: '<span style="color:blue">备注</span>', name: 'REMARK', index: 'REMARK', width: 160,editable:true,sortable:false},
            { label: '接收工厂', name: 'RECWERKS', index: 'RECWERKS', width: 100,hidden:true },
            { label: '接收仓库号', name: 'WHNUMBER', index: 'WHNUMBER', width: 100,hidden:true },
            { label: '', name: 'INBOUND_TYPE', index: 'INBOUND_TYPE', width: 100,hidden:true },
            { label: '', name: 'MO_NO', index: 'MO_NO', width: 100,hidden:true }

			
        ],
        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
		},
		onSelectAll:function(rowids,status){},
		onSelectRow:function(rowid,status){},
        viewrecords: true,
        cellEdit:true,
        cellurl:'#',
        cellsubmit:'clientArray',
        autowidth:true,
        shrinkToFit:false,  
        autoScroll: true, 
        multiselect: true,
        showCheckbox:true,
        ajaxSuccess:function(){
        },
        
        gridComplete:function(){
        	//紧急物料（urgentFlag不为空的）设置自定义样式
            var ids = $("#dataGrid").getDataIDs();
            for(var i=0;i<ids.length;i++){
                var rowData = $("#dataGrid").getRowData(ids[i]);
                if(rowData.urgentFlag != null && rowData.urgentFlag !='' && rowData.urgentFlag != undefined){
                	//如果是紧急物料设置自定义的颜色
                    $('#'+ids[i]).find("td").addClass("urgent-select");
                }
            }
        },
        afterSaveCell:function(rowid, cellname, value, iRow, iCol){},
		beforeSelectRow: function (rowid, e) {  //点击复选框才选中行
           var $myGrid = $(this),
           i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),
           cm = $myGrid.jqGrid('getGridParam', 'colModel');
            vm.rowid = rowid;
           if(cm[i].name == 'cb'){
               $('#dataGrid').jqGrid('setSelection',rowid);
           }
           return (cm[i].name == 'cb');

        }
    });
}

//表头切换
function showOrHideDataGrid(inboundType){
	if(inboundType==='18'){//501
		dataGrid501();
	}else if(inboundType==='21'){//305
		dataGrid305();
	}else if(inboundType==='20'){//备品进仓903
		dataGrid903();
	}else if(inboundType==='19'){//免费品511
		dataGrid511();
	}else if(inboundType==='11'){//生产订单进仓101
		dataGrid101();
	}else if(inboundType==='13'){//生产订单副产品进仓531
		dataGrid531();
	}else if(inboundType==='12'){//半成品无订单进仓521
		dataGrid521();
	}else if(inboundType==='14'){//co订单进仓101
		dataGridCo101();
	}else if(inboundType==='16'){//成本中心202
		dataGrid202();
	}else if(inboundType==='17'){//wbs元素222
		dataGrid222();
	}else if(inboundType==='15'){//研发订单进仓262
		dataGrid262();
	}else if(inboundType==='61'){//半成品生产订单A101  有虚收的
		dataGridA101();
	}else if(inboundType==='63'){//副产品A531  有虚收的
		dataGridA531();
	}else if(inboundType==='67'){//采购订单101
		dataGridpo101();
	}
	else if(inboundType==='74'){//生产订单原材料进仓262
		dataGridmo262();
	}else if(inboundType==='79'){//Z25
		dataGridZ25();
	}
}

//根据工厂获取库位
function getLgortList(WERKS){
    //查询库位
  	$.ajax({
      	url:baseUrl + "in/wmsinbound/lgortlist",
      	data:{
      		"DEL":'0',
      		"WERKS":WERKS,
      		"SOBKZ":"Z"
      		//"BAD_FLAG":'0'
      		},
      	success:function(resp){
      		vm.lgortlist = resp.result;
      		if(vm.lgortlist.length>0){
      			vm.lgort=vm.lgortlist[0].LGORT;
      		}
      		
      	}
      });
  	
}

//生产订单弹出框


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

$("#newOperation_product_matnr").click(function (){
	var trMoreindex = $("#dataGrid_product_matnr").children("tbody").children("tr").length;
	trMoreindex++;
	productmarntdata.push({ id: trMoreindex,AUFNR: "",MATNR:""});
	
	var addtr = '<tr id = "'+trMoreindex+'"  role="row" tabindex="-1" class="ui-widget-content jqgrow jqgrow ui-row-ltr '+((trMoreindex%2 == 0)?"ui-priority-secondary":"")+'" aria-selected = "false">'+
	'<td role="gridcell" class="ui-state-default jqgrid-rownum" style="text-align:center;" title="2" aria-describedby="dataGrid_rn">' + trMoreindex + 
	'</td><td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_1_"></td>'+
	'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_1_"></td>'+
	'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_1_"><i title="删除" aria-hidden="true" onclick="$(this).parent().parent().remove();delMoreTr();" class="delMoreTr fa fa-times"></i></td><td></td></tr>'
	$("#dataGrid_product_matnr tbody").append(addtr);
});

function getProductNo(){
	var productNostr="";
	var trs=$("#dataGrid_product").children("tbody").children("tr");
	$.each(trs,function(index,tr){
		
		if(index>0&&($(tr).find("td").eq(1).text()!=" "&&$(tr).find("td").eq(1).text()!=""))
			productNostr += $(tr).find("td").eq(1).text() + ";";
		
	});
	if(vm.inbound_type==='11'){//生产订单进仓101
		$("#AUFNR_101").val(productNostr.substring(0,productNostr.length-1));
	}
	if(vm.inbound_type==='13'){//生产订单副产品进仓531
		$("#AUFNR_531").val(productNostr.substring(0,productNostr.length-1));
	}
	if(vm.inbound_type==='61'){//半成品生产订单A101  有虚收的
		$("#AUFNR_A101").val(productNostr.substring(0,productNostr.length-1));
	}
	if(vm.inbound_type==='67'){//采购订单101
		$("#PONO_101").val(productNostr.substring(0,productNostr.length-1));
	}
	if(vm.inbound_type==='74'){//生产订单原材料进仓262
		$("#AUFNR_Mo262").val(productNostr.substring(0,productNostr.length-1));
	}
	if(vm.inbound_type==='63'){//生产订单副产品进仓531
		$("#AUFNR_A531").val(productNostr.substring(0,productNostr.length-1));
	}
	
}


$("#productNoQuery").click(function () {
	showTable();
	datagridPaste();
	layer.open({
		  type: 1,
		  title:['批量输入生产订单','font-size:18px'],
		  btn:['确定'],
		  offset:'t',
		  area: ["500px", "400px"],
		  skin: 'layui-bg-green', //没有背景色
		  shadeClose: false,
		  content : jQuery("#productNoqueryLayer"),
		  btn1:function(index){
			  layer.close(index);
			  getProductNo();
		  },
		});
	
});
//
$("#productNoQuery531").click(function () {
	showTable();
	datagridPaste();
	layer.open({
		  type: 1,
		  title:['批量输入生产订单','font-size:18px'],
		  btn:['确定'],
		  offset:'t',
		  area: ["500px", "400px"],
		  skin: 'layui-bg-green', //没有背景色
		  shadeClose: false,
		  content : jQuery("#productNoqueryLayer"),
		  btn1:function(index){
			  layer.close(index);
			  getProductNo();
		  },
		});
	
});

$("#productNoMatnrQuery531").click(function () {
	showTable_matnr();
	datagridMatnrPaste();
	layer.open({
		  type: 1,
		  title:['批量输入生产订单','font-size:18px'],
		  btn:['确定'],
		  offset:'t',
		  area: ["500px", "400px"],
		  skin: 'layui-bg-green', //没有背景色
		  shadeClose: false,
		  content : jQuery("#productMatnrNoqueryLayer"),
		  btn1:function(index){
			  layer.close(index);
			  queryListPo531();
		  },
		});
	
});

//
$("#productNoQueryA101").click(function () {
	showTable();
	datagridPaste();
	layer.open({
		  type: 1,
		  title:['批量输入生产订单','font-size:18px'],
		  btn:['确定'],
		  offset:'t',
		  area: ["500px", "400px"],
		  skin: 'layui-bg-green', //没有背景色
		  shadeClose: false,
		  content : jQuery("#productNoqueryLayer"),
		  btn1:function(index){
			  layer.close(index);
			  getProductNo();
		  },
		});
	
});
//
$("#productNoQueryPO101").click(function () {
	showTable();
	datagridPaste();
	layer.open({
		  type: 1,
		  title:['批量输入生产订单','font-size:18px'],
		  btn:['确定'],
		  offset:'t',
		  area: ["500px", "400px"],
		  skin: 'layui-bg-green', //没有背景色
		  shadeClose: false,
		  content : jQuery("#productNoqueryLayer"),
		  btn1:function(index){
			  layer.close(index);
			  getProductNo();
		  },
		});
	
});
//
$("#productNoQueryMO262").click(function () {
	showTable();
	datagridPaste();
	layer.open({
		  type: 1,
		  title:['批量输入生产订单','font-size:18px'],
		  btn:['确定'],
		  offset:'t',
		  area: ["500px", "400px"],
		  skin: 'layui-bg-green', //没有背景色
		  shadeClose: false,
		  content : jQuery("#productNoqueryLayer"),
		  btn1:function(index){
			  layer.close(index);
			  getProductNo();
		  },
		});
	
});
//
$("#productNoQueryA531").click(function () {
	showTable();
	datagridPaste();
	layer.open({
		  type: 1,
		  title:['批量输入生产订单','font-size:18px'],
		  btn:['确定'],
		  offset:'t',
		  area: ["500px", "400px"],
		  skin: 'layui-bg-green', //没有背景色
		  shadeClose: false,
		  content : jQuery("#productNoqueryLayer"),
		  btn1:function(index){
			  layer.close(index);
			  getProductNo();
		  },
		});
	
});

$("#importyf262Operation").click(function () {
	$("#dataGrid_yf262_import").jqGrid("clearGridData", true);
	
	showTable_yf262_import();
	datagridyf262ImportPaste();
	layer.open({
		  type: 1,
		  title:['批量粘贴导入','font-size:18px'],
		  btn:['创建进仓单'],
		  offset:'t',
		  area: ["800px", "400px"],
		  skin: 'layui-bg-green', //没有背景色
		  shadeClose: false,
		  content : jQuery("#yf262importLayer"),
		  btn1:function(index){
			  layer.close(index);
			  save262_import();
		  }
		});
	
});

$("#importsc262Operation").click(function () {
	$("#dataGrid_sc262_import").jqGrid("clearGridData", true);
	
	var mo_no=$("#AUFNR_Mo262").val();
	if(mo_no==""){
		js.alert("生产订单号不能为空");
		return false;
	}
	
	/*if($("#is_mo262").val()!="1"){
		js.alert("生产订单号不正确！");
		return false;
	}*/
	
	showTable_sc262_import();
	datagridsc262ImportPaste();
	layer.open({
		  type: 1,
		  title:['批量粘贴导入','font-size:18px'],
		  btn:['创建进仓单'],
		  offset:'t',
		  area: ["800px", "400px"],
		  skin: 'layui-bg-green', //没有背景色
		  shadeClose: false,
		  content : jQuery("#sc262importLayer"),
		  btn1:function(index){
			  layer.close(index);
			  save262sc_import();
		  }
		});
	
});

function showTable(){
	$("#dataGrid_product").dataGrid({
		datatype: "local",
		data: productdata,
        colModel: [			
            {label: '<span style="color:red">*</span><span style="color:blue"><b>生产订单</b></span>', name: 'AUFNR',index:"AUFNR", width: "200",align:"center",sortable:false,editable:true}, 
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

function showTable_matnr(){
	$("#dataGrid_product_matnr").dataGrid({
		datatype: "local",
		data: productmarntdata,
        colModel: [			
            {label: '<span style="color:red">*</span><span style="color:blue"><b>生产订单</b></span>', name: 'AUFNR',index:"AUFNR", width: "160",align:"center",sortable:false,editable:true}, 
            {label: '物料号', name: 'MATNR',index:"MATNR", width: "120",align:"center",sortable:false,editable:true}, 
            {label: '删除', name: '',index:"", width: "80",align:"center",sortable:false,editable:false,
            	formatter:function(value, name, record){
            		return '<i title="删除" aria-hidden="true" onclick="$(this).parent().parent().remove();delMoreTr();" class="fa fa-times"></i>';
            	}
            },
            {label: '', name: '',index:"", width: "80",align:"center",sortable:false,editable:false} 
        ],
        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
		},
		shrinkToFit: false,
        width:600,
        cellEdit:true,
        cellurl:'#',
		cellsubmit:'clientArray',
        showCheckbox:false
    });
}

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

$("#newReset_product").click(function () {
	productdata = [{ id: "1",AUFNR: ""},{ id: "2",AUFNR: ""}];
	$("#dataGrid_product").jqGrid('GridUnload');
	showTable();
	datagridPaste();
});

$("#newReset_product_matnr").click(function () {
	productmarntdata = [{ id: "1",AUFNR: ""},{ id: "2",AUFNR: ""},{ id: "3",MATNR: ""}];
	$("#dataGrid_product_matnr").jqGrid('GridUnload');
	showTable_matnr();
	datagridMatnrPaste();
});

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


function datagridMatnrPaste(){
	$('#dataGrid_product_matnr').bind('paste', function(e) {
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
				$("#newOperation_product_matnr").click();
			}
			var cells = tab.rows[startRow + i].cells.length; // 该行总列数
			for (var j = 0; j < arr[i].length && startCell + j < cells; j++) {
				var cell = tab.rows[startRow + i].cells[startCell + j];
				$(cell).find(':text').val(arr[i][j]); //找到cell下的input:text，设置value
				$('#dataGrid_product_matnr').jqGrid("saveCell", startRow + i, 1);
				$(cell).html(arr[i][j]);
			}
		}
	});
};


function showTable_yf262_import(){
	$("#dataGrid_yf262_import").dataGrid({
		datatype: "local",
		//data: '',
        colModel: [
			{ label: '<i class=\"fa fa-plus\" style=\"color:blue\" onclick=\"addMat262()\" ></i>', name: 'EMPTY_COL', index: '', align:"center",width: 30, sortable:false,formatter:function(val, obj, row, act){
				var actions = [];
				actions.push('<a href="#" onClick="delMat262('+row.id+')" title="删除" data-confirm="确认要删除该行数据吗？"><i class="fa fa-times" style=\"color:red\"></i></a>');
				
				return actions.join(",");
			},unformat:function(cellvalue, options, rowObject){
			return "";
			}},
			{label:'序号',name:'ID', index: 'ID',key: true ,width: 40,formatter:function(val, obj, row, act){
				return obj.rowId
			}},
			{ label: '订单号', name: 'IO_NO', index: 'IO_NO', width: 160,sortable:false,editable:true },
            { label: '物料号', name: 'MATERIAL', index: 'MATERIAL', width: 100,sortable:false,editable:true}, 
			{ label: '物料描述', name: 'MATERIALDESC', index: 'MATERIALDESC', width: 200,sortable:false }, 
			{ label: '进仓数量', name: 'IN_QTY', index: 'IN_QTY', width: 80,sortable:false,editable:true,editrules:{
   				number:true},editoptions:{
   	                dataInit:function(e){
   	                    $(e).change(function(e){
   	                    	iceil_add(this)
   	                    });
   	                }
   	            }},
			{ label: '单位', name: 'UNIT', index: 'UNIT', width: 60,sortable:false },
			{ label: '装箱数量', name: 'FULL_BOX_QTY', index: 'FULL_BOX_QTY', width: 80,sortable:false,editable:true,editrules:{
   				number:true},editoptions:{
   	                dataInit:function(e){
   	                    $(e).blur(function(e){
   	                    	iceil_add2(this)
   	                    });
   	                }
   	            }},
			{ label: '件数', name: 'BOX_COUNT', index: 'BOX_COUNT', width: 60,sortable:false },
			{ label: '储位', name: 'BIN_CODE', index: 'BIN_CODE', width: 80,sortable:false },
			{ label: '需求跟踪号', name: 'BEDNR', index: 'BEDNR', width: 120,editable:true,sortable:false},
			{ label: '生产日期', name: 'PRDDT', index: 'PRDDT', width: 100,sortable:false,editable:true,editoptions:{
                dataInit:function(e){
                    $(e).click(function(e){
                    	WdatePicker({dateFmt:'yyyy-MM-dd'})
                    });
                }
            }},
			{ label: '物流器具', name: 'LT_WARE', index: 'LT_WARE', width: 80,editable:true,sortable:false },
			{ label: '车间', name: 'WORKSHOP', index: 'WORKSHOP', width: 80,editable:true,sortable:false },
			{ label: '车型', name: 'CAR_TYPE', index: 'CAR_TYPE', width: 80,editable:true,sortable:false },
			{ label: '模具编号', name: 'MOULD_NO', index: 'MOULD_NO', width: 80,editable:true,sortable:false },
			{ label: '产品图号', name: 'CPTH', index: 'CPTH', width: 100,sortable:false},
			{ label: '作业员', name: 'ZYY', index: 'ZYY', width: 80,editable:true,sortable:false },
			{ label: '工位', name: 'DIS_STATION', index: 'DIS_STATION', width: 80,editable:true,sortable:false },
			{ label: '班次', name: 'WORKGROUP_NO', index: 'WORKGROUP_NO', width: 80,editable:true,sortable:false },
			{ label: '仓管员', name: 'WH_MANAGER', index: 'WH_MANAGER', width: 90,sortable:false },
			{ label: '备注', name: 'REMARK', index: 'REMARK', width: 160,editable:true,sortable:false},
			
			{ label: '接收工厂', name: 'RECWERKS', index: 'RECWERKS', width: 100,hidden:true },
			{ label: '接收仓库号', name: 'WHNUMBER', index: 'WHNUMBER', width: 100,hidden:true },
			{ label: '接收库位', name: 'RLGORT', index: 'RLGORT', width: 100,hidden:true },
			{ label: '', name: 'INBOUND_TYPE', index: 'INBOUND_TYPE', width: 160,hidden:true }
			
            
        ],
        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
		},
		afterSaveCell:function(rowid, cellname, value, iRow, iCol){
			var row =  $("#dataGrid_yf262_import").jqGrid('getRowData', rowid);
			if(cellname=='MATERIAL'){//根据物料号列表查询物料信息	
				
			}
		},
		onCellSelect : function(rowid,iCol,cellcontent,e){//如果装箱数量大于0，则不可编辑
   	 		var rec =  $("#dataGrid_yf262_import").jqGrid('getRowData', rowid);
	  	    if (rec['FULL_BOX_QTY']>0) {//过滤条件
	          	$("#dataGrid_yf262_import").jqGrid('setCell', rowid, 'FULL_BOX_QTY', '', 'not-editable-cell');
	          	
	  	    }
	  	    
	  	    
	   	},
		shrinkToFit: false,
        width:600,
        cellEdit:true,
        cellurl:'#',
		cellsubmit:'clientArray',
		showRownum:false,
        showCheckbox:false
    });
};

function showTable_sc262_import(){
	
	$("#dataGrid_sc262_import").dataGrid({
		datatype: "local",
        colModel: [
			{ label: '<i class=\"fa fa-plus\" style=\"color:blue\" onclick=\"addMatsc262()\" ></i>', name: 'EMPTY_COL', index: '', align:"center",width: 30, sortable:false,formatter:function(val, obj, row, act){
				var actions = [];
				actions.push('<a href="#" onClick="delMatsc262('+row.id+')" title="删除" data-confirm="确认要删除该行数据吗？"><i class="fa fa-times" style=\"color:red\"></i></a>');
				
				return actions.join(",");
			},unformat:function(cellvalue, options, rowObject){
			return "";
			}},
			{label:'序号',name:'ID', index: 'ID',key: true ,width: 40,formatter:function(val, obj, row, act){
				return obj.rowId
			}},
			{ label: '物料号', name: 'MATERIAL', index: 'MATERIAL', width: 100,sortable:false,editable:true}, 
			{ label: '物料描述', name: 'MATERIALDESC', index: 'MATERIALDESC', width: 200,sortable:false }, 
			{ label: '进仓数量', name: 'IN_QTY', index: 'IN_QTY', width: 80,sortable:false,editable:true,editrules:{
   				number:true},editoptions:{
   	                dataInit:function(e){
   	                    $(e).change(function(e){
   	                    	iceil_add(this)
   	                    });
   	                }
   	            }},
			{ label: '单位', name: 'UNIT', index: 'UNIT', width: 60,sortable:false },
			{ label: '装箱数量', name: 'FULL_BOX_QTY', index: 'FULL_BOX_QTY', width: 80,sortable:false,editable:true,editrules:{
   				number:true},editoptions:{
   	                dataInit:function(e){
   	                    $(e).blur(function(e){
   	                    	iceil_add2(this)
   	                    });
   	                }
   	            }},
			{ label: '件数', name: 'BOX_COUNT', index: 'BOX_COUNT', width: 60,sortable:false },
			{ label: '储位', name: 'BIN_CODE', index: 'BIN_CODE', width: 80,sortable:false },
			{ label: '库位', name: 'RLGORT', index: 'RLGORT', width: 80,sortable:false },
			{ label: '需求跟踪号', name: 'BEDNR', index: 'BEDNR', width: 120,editable:true,sortable:false},
			{ label: '生产日期', name: 'PRDDT', index: 'PRDDT', width: 100,sortable:false,editable:true,editoptions:{
                dataInit:function(e){
                    $(e).click(function(e){
                    	WdatePicker({dateFmt:'yyyy-MM-dd'})
                    });
                }
            }},
			{ label: '仓管员', name: 'WH_MANAGER', index: 'WH_MANAGER', width: 90,sortable:false },
			{ label: '备注', name: 'REMARK', index: 'REMARK', width: 160,editable:true,sortable:false},
			
			{ label: '接收工厂', name: 'RECWERKS', index: 'RECWERKS', width: 100,hidden:true },
			{ label: '接收仓库号', name: 'WHNUMBER', index: 'WHNUMBER', width: 100,hidden:true },
			{ label: '', name: 'MO_NO', index: 'MO_NO', width: 100,hidden:true },
			{ label: '', name: 'INBOUND_TYPE', index: 'INBOUND_TYPE', width: 160,hidden:true }
			
            
        ],
        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
		},
		afterSaveCell:function(rowid, cellname, value, iRow, iCol){
			var row =  $("#dataGrid_sc262_import").jqGrid('getRowData', rowid);
			
		},
		onCellSelect : function(rowid,iCol,cellcontent,e){//如果装箱数量大于0，则不可编辑
   	 		var rec =  $("#dataGrid_sc262_import").jqGrid('getRowData', rowid);
	  	    if (rec['FULL_BOX_QTY']>0) {//过滤条件
	          	$("#dataGrid_sc262_import").jqGrid('setCell', rowid, 'FULL_BOX_QTY', '', 'not-editable-cell');
	          	
	  	    }
	  	    
	   	},
		shrinkToFit: false,
        width:600,
        cellEdit:true,
        cellurl:'#',
		cellsubmit:'clientArray',
		showRownum:false,
        showCheckbox:false
    });
};

$("#newReset_yf262_import").click(function () {
	$("#dataGrid_yf262_import").jqGrid("clearGridData", true);
});

$("#newReset_sc262_import").click(function () {
	$("#dataGrid_sc262_import").jqGrid("clearGridData", true);
});

function datagridyf262ImportPaste(){
	$('#dataGrid_yf262_import').bind('paste', function(e) {
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
				addMat262();
			}
			var cells = tab.rows[startRow + i].cells.length; // 该行总列数
			for (var j = 0; j < arr[i].length && startCell + j < cells; j++) {
				
				var cell = tab.rows[startRow + i].cells[startCell + j];
				if(j==1){//物料号的列数据
					//console.info(arr[i][j]);//物料号
					queryMadsBypaste262(arr[i][j],cell);
				}
				
				if(arr[i][j]!=""){//excel有数据
					$(cell).find(':text').val(arr[i][j]); //找到cell下的input:text，设置value
				}
				$('#dataGrid_yf262_import').jqGrid("saveCell", startRow + i, 1);
				if(arr[i][j]!=""){//excel有数据
					$(cell).html(arr[i][j]);
				}
				if(j==6){//计算件数 进仓数量/装箱数量
					if($(cell).prev().html()!="0"&&$(cell).prev().html()!=""){
						$(cell).html(Math.ceil($(cell).prev().prev().prev().html()/$(cell).prev().html()));
					}else{
						$(cell).html("1");
					}
					
					
				}
			}
		}
	});
};

function datagridsc262ImportPaste(){
	$('#dataGrid_sc262_import').bind('paste', function(e) {
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
				addMatsc262();
			}
			var cells = tab.rows[startRow + i].cells.length; // 该行总列数
			for (var j = 0; j < arr[i].length && startCell + j < cells; j++) {
				
				var cell = tab.rows[startRow + i].cells[startCell + j];
				if(j==0){//物料号的列数据
					//console.info(arr[i][j]);//物料号
					queryMadsBypaste262sc(arr[i][j],cell);
				}
				
				if(arr[i][j]!=""){//excel有数据
					$(cell).find(':text').val(arr[i][j]); //找到cell下的input:text，设置value
				}
				$('#dataGrid_sc262_import').jqGrid("saveCell", startRow + i, 1);
				if(arr[i][j]!=""){//excel有数据
					$(cell).html(arr[i][j]);
				}
				if(j==5){//计算件数 进仓数量/装箱数量
					if($(cell).prev().html()!="0"&&$(cell).prev().html()!=""){
						$(cell).html(Math.ceil($(cell).prev().prev().prev().html()/$(cell).prev().html()));
					}else{
						$(cell).html("1");
					}
					
					
				}
			}
		}
	});
};


function addMat262(){
	var xqgzh=$("#xqgzh_262").val();//需求跟踪号
	var currentDate = formatDate(new Date());
	
	var werks=vm.werks;
	var whnumber=vm.whNumber;
	var inboundType=$("#inboundType").val();
	var lgort=vm.lgort;
	
	var selectedId = $("#dataGrid_yf262_import").jqGrid("getGridParam", "selrow"); 
	var ids = $("#dataGrid_yf262_import").jqGrid('getDataIDs');

	//console.info(ids)
	if(ids.length==0){
		ids=[0]
	}
    var rowid = Math.max.apply(null,ids) + 1;
    var data={};
    data.id=rowid;
    data.BEDNR=xqgzh;
    data.PRDDT=currentDate;
    
    data.RECWERKS=werks;
    data.WHNUMBER=whnumber;
    data.RLGORT=lgort;
    data.INBOUND_TYPE=inboundType;
    
    $("#dataGrid_yf262_import").jqGrid("addRowData", rowid, data, "last"); 
    
};

function addMatsc262(){
	var xqgzh=$("#xqgzhMo_262").val();//需求跟踪号
	var mo_no=$("#AUFNR_Mo262").val();//
	var currentDate = formatDate(new Date());
	
	var werks=vm.werks;
	var whnumber=vm.whNumber;
	var inboundType=$("#inboundType").val();
	var lgort=vm.lgort;
	
	var selectedId = $("#dataGrid_sc262_import").jqGrid("getGridParam", "selrow"); 
	var ids = $("#dataGrid_sc262_import").jqGrid('getDataIDs');

	//console.info(ids)
	if(ids.length==0){
		ids=[0]
	}
    var rowid = Math.max.apply(null,ids) + 1;
    var data={};
    data.id=rowid;
    data.BEDNR=xqgzh;
    data.PRDDT=currentDate;
    
    data.RECWERKS=werks;
    data.WHNUMBER=whnumber;
    data.RLGORT=lgort;
    data.INBOUND_TYPE=inboundType;
    data.MO_NO=mo_no;
    
    $("#dataGrid_sc262_import").jqGrid("addRowData", rowid, data, "last"); 
    
};

function delMat262(id){
	//console.info(id)
	$("#dataGrid_yf262_import").delRowData(id)
};

function delMatsc262(id){
	//console.info(id)
	$("#dataGrid_sc262_import").delRowData(id)
};


function save262_import(){
	$(".btn").attr("disabled", true);
	$('#dataGrid_yf262_import').jqGrid("saveCell", lastrow, lastcell);
	var rows = $("#dataGrid_yf262_import").jqGrid('getRowData');
	$.ajax({
		url:baseUrl + "in/wmsinternalbound/save262import",
		dataType : "json",
		type : "post",
		data : {
			"ARRLIST":JSON.stringify(rows)
		},
		async: false,
		success:function(resp){
			if(resp.code == '0'){
				//js.showMessage("保存成功! 进仓单号  "+resp.ininternetbountNo,null,null,1000*60000);
				$("#inboundNoLayer").html(resp.ininternetbountNo);
				$("#inboundNo").val(resp.ininternetbountNo);
				$("#inboundNo1").val(resp.ininternetbountNo);
				$("#inboundNo2").val(resp.ininternetbountNo);
				layer.open({
            		type : 1,
            		offset : '50px',
            		skin : 'layui-layer-molv',
            		title : "进仓单创建成功",
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
			}else{
				js.alert("保存失败");
				js.showErrorMessage(resp.msg,1000*100);
				$(".btn").attr("disabled", false);
			}
		}
	});
}

function save262sc_import(){

	$(".btn").attr("disabled", true);
	$('#dataGrid_sc262_import').jqGrid("saveCell", lastrow, lastcell);
	var rows = $("#dataGrid_sc262_import").jqGrid('getRowData');
	//从jqGrid获取选中的行数据
	
	$.ajax({
		url:baseUrl + "in/wmsinternalbound/saveMo262import",
		dataType : "json",
		type : "post",
		data : {
			"ARRLIST":JSON.stringify(rows)
		},
		async: false,
		success:function(resp){
			if(resp.code == '0'){
				//js.showMessage("保存成功! 进仓单号  "+resp.ininternetbountNo,null,null,1000*60000);
				$("#inboundNoLayer").html(resp.ininternetbountNo);
				$("#inboundNo").val(resp.ininternetbountNo);
				$("#inboundNo1").val(resp.ininternetbountNo);
				$("#inboundNo2").val(resp.ininternetbountNo);
				layer.open({
            		type : 1,
            		offset : '50px',
            		skin : 'layui-layer-molv',
            		title : "进仓单创建成功",
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
			}else{
				js.alert(resp.msg);
				$(".btn").attr("disabled", false);
			}
		}
	});

}
