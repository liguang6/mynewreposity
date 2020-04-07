var last_scan_ele;
var lastrow,lastcell,lastcellname;
var material_no="";
var vm = new Vue({
	el:'#rrapp',
	data:{
		machine_code : '', //机台
		order_no : '', //订单
		process : '' ,//工序
		batch : '', //批次
		batch_list : [],
		zzj_no : '', //零部件号
		bind_machine_list :[],
		bind_machine : {},
		tab_id : '',
		formUrl : '',
		werks : '',
		count_info_1 : "",
		count_info_2 : "",
		count_info_3 : 0,
		count_info_4 : "",
		table_id :"",
		process_type :'',
		accuracy_demand:"",      //精度
		specification:"", //材料规格
		subcontracting_type_list:[],
		productor:'',
	},
	watch : {
		tab_id : {
			handler : function(newVal,oldVal){
				//$("#subcontracting_type").multipleSelect('destroy');
				vm.table_id = "#dataGrid"+newVal.replace("#","_");
				$("#gbox_dataGrid"+oldVal.replace("#","_")).css("display","none");
				$("#gbox_dataGrid"+newVal.replace("#","_")).css("display","");
				if(newVal == "#div_1"){// 清单					
					vm.formUrl = baseUrl+"zzjmes/jtOperation/getJTPlan";
					vm.$nextTick(function(){
						$("#subcontracting_type").multipleSelect('destroy').multipleSelect({
							selectAll: true,
						}).multipleSelect('uncheckAll');
					})	
					
					return false;					
				}
				if(newVal == "#div_2"){// 绑定
					$("#zzj_no").select();
					vm.formUrl = "#";
				}
				if(newVal == "#div_3"){// 录入
					$("#zzj_no").select();
					vm.formUrl = "#";
				}
				if(newVal == "#div_4"){// 自检
					vm.formUrl = "#";
				}
				vm.$nextTick(function(){
					vm.query();
				})
				
			}
		},
		order_no : {
			handler : function(newVal,oldVal){
				getBatchSelects(vm.werks,vm.order_no);	
				if(vm.tab_id == '#div_1' ){
					vm.$nextTick(function(){
						vm.query();
					})
				}
				if(vm.tab_id == '#div_2' ){
					vm.$nextTick(function(){
						ajaxGetMachineAchieve();
					})
				}
		
			}
		},
		batch : {
			handler : function(newVal,oldVal){
				if(vm.tab_id == '#div_1' ){
					vm.$nextTick(function(){
						vm.query();
					})
				}				
			}
		},
		productor :{
			handler : function(newVal,oldVal){
				var rows = $(vm.table_id).jqGrid("getRowData");
				$.each(rows,function(i,row) {
					$(vm.table_id).jqGrid('setCell',Number(row.ID), "productor", newVal,'editable-cell');
				})
			}
		},
	/*	zzj_no : {
			handler : function(newVal,oldVal){
				if(vm.tab_id == '#div_1' ){
					vm.$nextTick(function(){
						vm.query();
					})	
				}			
			}
		},*/
		/*machine_code : {
			handler : function(newVal,oldVal){
				//校验机台编码是否有效
				checkJT(newVal);		
				if(vm.tab_id == '#div_3' ){					
					$(vm.table_id).jqGrid("clearGridData");
					$(vm.table_id).jqGrid('setGridParam',{datatype:'local'}).trigger('reloadGrid'); 							
					//获取工序是否为计划外工序
					var process_info = getStandardProcess();
					if(process_info.length>0){
						process_info = process_info[0];
						vm.process_type = process_info.PROCESS_TYPE;
					}
					console.info("PROCESS_TYPE："+process_type);
					vm.$nextTick(function(){
						vm.query();
					})
				}			
			}
		},*/
	},
	methods : {
		query : function(){
			if(vm.machine_code == ""|| vm.machine_code == undefined){
				js.showErrorMessage("请输入机台编码！",1000 * 10);
				return false;
			}
		
			if(vm.tab_id == "#div_1"){// 清单
				
				var subcontracting_type= $("#subcontracting_type").multipleSelect('getSelects').join(",")
	
				$("#subcontracting_type_submit").val(subcontracting_type);
				queryJTPlan();
			}
			if(vm.tab_id == "#div_2"){// 绑定			
				showJTBindTable();
			}
			if(vm.tab_id == "#div_3"){// 录入
				showJTOutTable();
			}
			if(vm.tab_id == "#div_4" && (vm.zzj_no==null || vm.zzj_no=="")){// 自检
				queryTestRecord([]);
			}
			if(vm.zzj_no!=""&&vm.zzj_no!=null && vm.tab_id != "#div_1" ){
				var e = $.Event('keydown');
			    e.keyCode = 13; 	
			    $("#zzj_no").trigger(e);
			}	
		},
		getJTProcess : function(){  // 根据账号获取权限机台列表
			var process=[];
			$.ajax({
				url : baseURL+"zzjmes/common/getJTProcess",
				method:'get',
				dataType:'json',
				async:false,
				data:{
				},
				success:function(response){
					if(response.msg="success"){
						process=response.process;
					}
				}
			});
			return process;
		},
		printLable : function(){//条码打印
			var rows=getSelectedRows(vm.table_id);
			if(rows.length==0){
				return false;
			}
			var data_list = [];
			rows.forEach(function(value, index, arr){
				var data = $(vm.table_id).jqGrid("getRowData",value);
				data_list.push(data)
			})
			
			var url = baseURL+"zzjmes/jtOperation/labelPreview"
			 var form = $('<form />', {action : url, method:"post", style:"display:none;"}).appendTo('body');
			$(form).attr("target","_blank");
			$(form).append("<input type='hidden' name='label_list' value='"+JSON.stringify(data_list)+"'>")
			form.submit();
		},
		/**
		 * 保存机台绑定数据
		 */
		saveJTBindData : function(){		
			$(vm.table_id).jqGrid("saveCell", lastrow, lastcell);
			var cm = $(vm.table_id).jqGrid('getGridParam', 'colModel'); 
			var rows=$(vm.table_id).jqGrid('getRowData');
			if(rows.length <= 0){
				js.showErrorMessage("无需要保存的数据！",1000 * 10);
				return false;
			}
			var save_flag=true;
			rows.forEach(function(row,index,arr){
				save_flag=checkRequiredCell(cm,row);
			})
			if(!save_flag){
				return false;
			}	
			
			js.loading("处理中...");
			$("#btnSave").attr("disabled","disabled");			
			$.ajax({
				type : "post",
				dataType : "json",// 返回json格式的数据
				async : false,
				url : baseUrl+"zzjmes/jtOperation/saveJTBindData",
				data : {
					dataList : JSON.stringify($(vm.table_id).jqGrid("getRowData"))
				},
				success:function(response){
					js.closeLoading();
					js.showMessage(response.msg);
					if(response.code=='0'){
						$(vm.table_id).jqGrid("clearGridData");
						$(vm.table_id).jqGrid('setGridParam',{datatype:'local'}).trigger('reloadGrid'); 
					}					
				}	
			});
			$("#btnSave").removeAttr("disabled");
		},
		saveJTOutputData : function(){
			$(vm.table_id).jqGrid("saveCell", lastrow, lastcell);
			var cm = $(vm.table_id).jqGrid('getGridParam', 'colModel'); 
			var rows=$(vm.table_id).jqGrid('getRowData');
			if(rows.length <= 0){
				js.showErrorMessage("无需要保存的数据！",1000 * 10);
				return false;
			}
			var save_flag=true;
			rows.forEach(function(row,index,arr){
				save_flag=checkRequiredCell(cm,row);
			})
			if(!save_flag){
				return false;
			}
			
			js.loading("处理中...");
			$("#btnSave").attr("disabled","disabled");	
			$.ajax({
				type : "post",
				dataType : "json",// 返回json格式的数据
				async : false,
				url : baseUrl+"zzjmes/jtOperation/saveJTOutputData",
				data : {
					dataList : JSON.stringify($(vm.table_id).jqGrid("getRowData")),
					process_type : vm.process_type,
				},
				success:function(response){
					js.showErrorMessage(response.msg,1000 * 10);
					if(response.code=='0'){
						$(vm.table_id).jqGrid("clearGridData");
						$(vm.table_id).jqGrid('setGridParam',{datatype:'local'}).trigger('reloadGrid'); 
						vm.count_info_3=0;
					}				
				}	
			});
			js.closeLoading();
			$("#btnSave").removeAttr("disabled");
		},
		// 保存生产自检
		saveTestRecord:function(){
			$(vm.table_id).jqGrid("saveCell", lastrow, lastcell);
        	var rows=$(vm.table_id).jqGrid('getRowData');
        	if(rows.length == 0){
				alert("当前还没有数据！");
				return false;
			}
        	for(var i in rows){
        		var data=rows[i];
        		if(data.test_tool_no=='' || data.outward_standard=='' || 
        			data.size_standard=='' || data.outward_result=='' ||
        			data.size_result=='' || data.test_result=='' ||
        			data.product_test=='' || data.product_test_date==''){
        			js.showErrorMessage("第"+(i+1)+"行：数据录入不完整！");
        			return;
        		}
        	}
        	js.loading("正在保存数据,请您耐心等待...");
			$("#btnSave").attr("disabled","disabled");
			$.ajax({
				url:baseUrl + "zzjmes/qmTestRecord/save",
				data:{
					werks:vm.bind_machine.werks,
					werks_name:vm.bind_machine.werks_name,
					workshop:vm.bind_machine.workshop,
					workshop_name:vm.bind_machine.workshop_name,
					order_no:vm.order_no,
					zzj_no:vm.zzj_no,
					zzj_plan_batch:vm.batch,
					zzj_name:rows[0].zzj_name,
					material_no:rows[0].material_no,
					test_tool_no:rows[0].test_tool_no,
					outward_standard:rows[0].outward_standard,
					size_standard:rows[0].size_standard,
					product_test:rows[0].product_test,
					product_test_date:rows[0].product_test_date,
					pro_detail_list:JSON.stringify(rows),
					result_type:'0'
				},
				type:"post",
				dataType:"json",
				success:function(resp){
					js.closeLoading();
					if(resp.code == 0){
						js.showMessage("保存成功!");
						$(vm.table_id).jqGrid('GridUnload');
						vm.batch_list=[];
						vm.order_no='';
						vm.zzj_no='';
					}else{
						alert("保存失败：" + resp.msg);
					} 
				},
				complete:function(XMLHttpRequest, textStatus){
					$("#btnSave").removeAttr("disabled");
				}
			});
		},
		getTestRules:function(item){
			console.log("params",item.werks+"-"+item.workshop+"-"+item.line+"-"+item.zzj_plan_batch+"-"+item.zzj_pmd_items_id);
			$.ajax({
				type : "post",
				dataType : "json",
				async : false,
				url : baseURL+"/zzjmes/qmTestRecord/getTestRules",
				data : {
					"werks" : item.werks,
					"workshop" : item.workshop,
					"line" : item.line,
					"order_no":item.order_no,
					"zzj_plan_batch":item.zzj_plan_batch,
					"zzj_no":item.zzj_no,
					"pmd_item_id":item.zzj_pmd_items_id,
				},
				success:function(response){
					if(response.code==0){
                       var rules=response.data;
                       var demand_quantity=0;
                       var prducu=0; // 自检抽样数
                       var zzj_name="";
                       var material_no="";
                       var pmdList=response.pmdList;
                       if(pmdList!=null && pmdList.length>0){
                    	   demand_quantity=pmdList[0].demand_quantity;
                    	   zzj_name=pmdList[0].zzj_name;
                    	   material_no=pmdList[0].material_no;
                       }else{
                    	   js.showErrorMessage("下料明细不存在！");
                    	   return;
                       }
                       if(rules!=null && rules.length>0){
                    	  var rule=rules[0];
                    	  if(rule.TEST_RULES.indexOf('[{')<0 && rule.TEST_RULES.indexOf('}]')<0){
                    		  js.showErrorMessage("检验规则录入格式错误！");
                    		  return false;
                    	  }
                    	  var jsonArr = jQuery.parseJSON(rule.TEST_RULES);
                    	  for(var i in jsonArr){
                    		  var from=parseInt(jsonArr[i].from);
                    		  var to=parseInt(jsonArr[i].to);
                    		  if(demand_quantity>=from && demand_quantity<=to){
                    			  prducu=parseInt(jsonArr[i].prducu!='全检' ? jsonArr[i].prducu : demand_quantity);
                    			  break;
                    		  }
                    	  }
                       }else{
                    	   js.showErrorMessage("该工厂、车间未配置检验规则！");
                    	   return;
                       }
                       vm.count_info_4 = prducu + "/" +demand_quantity;
                       console.log(prducu+"-"+zzj_name);
                       var mydata=[];
                       while(prducu>0){
                    	   var testRecord={};
                    	   testRecord.test_result="OK";
                    	   testRecord.product_test_date=formatDate(new Date());
                    	   testRecord.zzj_name=zzj_name;
                    	   testRecord.material_no=material_no;
                    	   mydata.push(testRecord);
                    	   prducu--;
                       }
                       console.log("mydata",mydata);
                       queryTestRecord(mydata);
					}else{
						js.showErrorMessage(response.msg);
					}
				}	
			});
		},
		startOpera : function(){//开始加工,记录开始时间
			var rows=getSelectedRows(vm.table_id);
			if(rows.length==0){
				return false;
			}
			var data_list = [];
			rows.forEach(function(value, index, arr){
				var data = $(vm.table_id).jqGrid("getRowData",value);
				data_list.push(data)
			})
			
			$.ajax({
				type : "post",
				dataType : "json",// 返回json格式的数据
				async : false,
				url : baseUrl+"zzjmes/jtOperation/startOpera",
				data : {
					dataList : JSON.stringify(data_list)
				},
				success:function(response){
					if(response.code=="500"){
						js.showErrorMessage(response.msg,1000 * 10);
					}else{
						js.showMessage("操作成功！");
					}					
					
				}	
			});
		},
		getMasterdataDictList : function(){
			var list=[];
			$.ajax({
				type : "post",
				dataType : "json",// 返回json格式的数据
				async : false,
				url : baseUrl+"masterdata/getMasterdataDictList",
				data : {
					type:'ZZJ_SUB_TYPE'
				},
				success:function(response){
					if(response.code=="500"){
						js.showErrorMessage(response.msg,1000 * 10);
					}else{
						list=response.data;
					}										
				}	
			});
			return list;
		},
		
	},
	created:function(){
		var list = this.getMasterdataDictList();
		console.info(list)
		this.subcontracting_type_list=list;
/*		this.$nextTick(function(){
			$("#subcontracting_type").multipleSelect('destroy').multipleSelect({
		        selectAll: true,
		    }).multipleSelect('uncheckAll');
		})*/
		
		/**
		 * 获取账号绑定的机台、工序、班组信息
		 */
		this.bind_machine_list=this.getJTProcess();
		this.bind_machine=this.bind_machine_list[0];
		if(this.bind_machine_list.length<1 || this.bind_machine==undefined){
			js.showErrorMessage("该账号未绑定机台!");
			return false;
			//this.bind_machine={};
		}
		this.werks = this.bind_machine.werks;
		this.process = this.bind_machine.process_name;
		this.machine_code =  this.bind_machine.machine_code;
		this.tab_id="#div_1";
	},

})	

$(function () {
	console.info("初始化")
	
	/**
	 * 订单模糊查询
	 * @param order
	 * @returns
	 */
	getZZJOrderNoSelect("#order_no", null, function(order){
		console.info(JSON.stringify(order))
		vm.order_no = order.order_no;
	})
	
	/**
	 * 切换Tab
	 */
	$(document).on("click","#myTab>li",function(e){
		var tag_name=$(e.target).prop("tagName");
		console.info("点击的元素:"+tag_name)
		var _tab_id ="";
		if(tag_name=="I"){
			return false;
		}
		
		if(tag_name=="A"){
			_tab_id=$(e.target).attr("href")
		}else{
			_tab_id=$(e.target).children("a").attr("href")
		}
		
		vm.tab_id =_tab_id;
	})
	
/*	$("#machine_code").change(function(e){
		checkJT($(e.target).val());
		vm.query();
	})
*/
	/**
	 * 扫描零部件二维码,解析二维码
	 */
	$(document).on("keydown","#zzj_no",function(e){		
		if(e.keyCode == 13){
			if($(e.target).val() ==null || $(e.target).val().trim().length==0){
				return false;
			}
			if($(e.target).val().indexOf("{")<0  && vm.tab_id !='#div_1' ){
				if(vm.order_no==null || vm.order_no.trim() ==""){
					js.showErrorMessage("请输入有效订单！");
					//vm.zzj_no="";
					return false;
				}
				if(vm.batch==null || vm.batch.trim() ==""){
					js.showErrorMessage("请选择批次！");
					//vm.zzj_no="";
					return false;
				}
			}
			var bind_machine = vm.bind_machine;
			var val_json = {};
			//扫描零部件条码
			if($(e.target).val()!=null && $(e.target).val().trim().length>0 && $(e.target).val().indexOf("{")>=0){
				val_json =  JSON.parse($(e.target).val());
				//alert($(e.target).val())
				vm.order_no=val_json.order_no;
				vm.batch = val_json.zzj_plan_batch;
				// $("#batch").val(vm.batch);
				vm.zzj_no=val_json.zzj_no;
				$(e.target).val(vm.zzj_no);
			}else{
				val_json.zzj_no=$(e.target).val();
				val_json.werks = bind_machine.werks;
				val_json.order_no = vm.order_no;
				val_json.zzj_plan_batch = vm.batch;
				val_json.workshop = bind_machine.workshop;
				val_json.line = bind_machine.line;
			}
			/*val_json.zzj_no=$(e.target).val();
			val_json.werks = bind_machine.werks;
			val_json.order_no = vm.order_no;
			val_json.zzj_plan_batch = vm.batch;
			val_json.workshop = bind_machine.workshop;
			val_json.line = bind_machine.line;*/
			
			var item = {};
			item.zzj_no=val_json.zzj_no;
			item.werks = val_json.werks;
			item.order_no = val_json.order_no;
			item.zzj_plan_batch = val_json.zzj_plan_batch;
			item.workshop = val_json.workshop;
			item.line = val_json.line;
			item.zzj_pmd_items_id = val_json.zzj_pmd_items_id||0;
			item.machine = bind_machine.machine_code;
			item.process_name = bind_machine.process_name;
			item.process_type = vm.process_type;
			//权限校验
			if(item.werks != bind_machine.werks || item.workshop !=bind_machine.workshop || item.line !=bind_machine.line){
				js.showErrorMessage("该零部件不属于工厂：("+ bind_machine.werks_name+") 车间：("+ bind_machine.workshop_name+") 线别：("+ bind_machine.line_name+")",1000 * 10);
				//vm.zzj_no="";
				return false;
			}
			//机台清单
			if(vm.tab_id.indexOf("div_1")>=0){
				var info = ajaxGetPmdBaseInfo(item);
				material_no=info.material_no||"";
				//机台清单,打开图纸
				if(material_no!=""){
					getPdf(item.werks,material_no);
				}

				var info_list = ajaxGetPmdItems(item);
				if(info_list == null || info_list.length==0){
					//js.showErrorMessage("零部件"+item.zzj_no+"未绑定该机台计划！",1000 * 10);
					return false;
				}
				
				info_list.forEach(detail=>{
					/**
					 * 查询上一道工序的产量，录入数量不能超出上一工序产量，上工序在工艺流程中存在多个时，不做判断
					 * @returns
					 */
					var last_process = detail.last_process
					console.info("last_process:"+last_process);		
					var left_qty=0; //欠产数量
					var count_p =0;
					detail.process_flow.split("-").forEach(function(value,index,arr){
						if(value==last_process){
							count_p++;
						}
					})
					if(count_p==1){
						left_qty=Math.min((detail.last_prod_quantity- detail.prod_quantity),(detail.plan_quantity - detail.prod_quantity));
					}else{
						left_qty = detail.plan_quantity - detail.prod_quantity;
					}
					
					var msg = detail.zzj_name+"("+detail.assembly_position+")";
					if(detail.plan_item_id==null||detail.plan_item_id=="0"){
						msg +="未绑定机台计划！<br/>";
					}
					if(detail.prod_quantity==null||detail.prod_quantity==0){
						msg +="未录入产量！";
					} 
					if (left_qty > 0){
						msg +="欠产数量："+left_qty+"<br/>";
					}else if(detail.qm_test_id ==null || detail.qm_test_id==0){
						msg +="未自检！<br/>";
					}
					
					js.showMessage(msg);
				})
				
				vm.query();
			}
			
			//机台绑定
			if(vm.tab_id.indexOf("div_2")>=0){
				$("#zzj_no").select();
				//校验零部件在工厂、车间、线别、订单、工序下是否存在计划
				if(!checkBindPlan(item)){
					js.showErrorMessage("已存在机台计划！",10*1000);
					return false;
				}
				//查询机台绑定表格其他信息
				var info_list = ajaxGetPmdItems(item);
				/**
				 * 校验零部件编号信息是否存在
				 */
				if(info_list==null || info_list.length==0){
					//js.showErrorMessage("未找到有效零部件信息！");
					//vm.zzj_no="";
					//js.showErrorMessage("未找到有效零部件信息，请确认是否导入下料明细！",1000 * 10);
					return false;
				}
				var rows=$(vm.table_id).jqGrid('getRowData');
				
				info_list.forEach(detail=>{
					var exist_flag=false;//未重复
					var _index=null;
					rows.forEach(function(row, index, arr){
						if(row.zzj_plan_batch==item.zzj_plan_batch && row.zzj_pmd_items_id==detail.zzj_pmd_items_id){
							exist_flag=true;
							_index=index;
							return false;
						}
					})
					if(exist_flag){
						vm.zzj_no="";
						js.showErrorMessage("抱歉，第"+(_index+1)+"行存在重复数据!",1000 * 10);
						return true;
					}
					var process_arr = detail.process_flow.split("-");
					var repeat_process_flag = false; //工艺流程是否包含重复工序,包含：不允许绑定
					process_arr.forEach(function(value, index, arr){
						var _first_index = process_arr.indexOf(value);
						if(_first_index!=index){
							repeat_process_flag=true;
							return ;
						}
					})
					
					if(repeat_process_flag){
						//vm.zzj_no="";
						js.showErrorMessage(item.zzj_no+"零件号工艺流程: "+detail.process_flow+" 出现重复工序，请前往机台计划导入功能操作",1000 * 10);
						return true;
					}
					
					detail.zzj_no=item.zzj_no;
					detail.werks = item.werks;
					detail.order_no = item.order_no;
					detail.zzj_plan_batch = item.zzj_plan_batch;
					detail.workshop = item.workshop;
					detail.line = item.line;
					//detail.zzj_pmd_items_id = item.pmd_item_id||0;
					detail.machine = bind_machine.machine_code;
					detail.process_name = bind_machine.process_name;
					detail.plan_process = bind_machine.process_name;
					detail.plan_quantity = detail.ecn_qty||Number(detail.batch_quantity)*Number(detail.quantity);
					detail.plan_date = formatDate(new Date());
					detail.werks_name = bind_machine.werks_name;
					detail.workshop_name = bind_machine.workshop_name;
					detail.line_name = bind_machine.line_name;
					detail.workgroup = bind_machine.workgroup;
					detail.workgroup_name = bind_machine.workgroup_name;
					detail.team = bind_machine.team;
					detail.team_name = bind_machine.team_name;
					
					var ids = $(vm.table_id).jqGrid('getDataIDs');
					if(ids.length==0){
						ids=[0]
					}
				    var rowid = Math.max.apply(null,ids) + 1;
				    detail.ID = rowid;
				    
				    $(vm.table_id).jqGrid("addRowData", rowid, detail, "last"); 
				   				    
				})
				  vm.saveJTBindData();
			}  
			
			//产量录入
			if(vm.tab_id.indexOf("div_3")>=0){		
				$("#zzj_no").select();		
				//查询产量录入表格其他信息
				var info_list = ajaxGetPmdOutItems(item);
				/**
				 * 校验零部件编号信息是否存在
				 */
				if(info_list==null || info_list.length==0){
					//vm.zzj_no="";
					return false;
				}
				var rows=$(vm.table_id).jqGrid('getRowData');
				
				var finished_flag=false;
				info_list.forEach(detail=>{
					var exist_flag=false;//未重复
					finished_flag=false;
					var _index=null;
					rows.forEach(function(row, index, arr){
						if(row.zzj_plan_batch==item.zzj_plan_batch && row.zzj_pmd_items_id==detail.zzj_pmd_items_id){
							exist_flag=true;
							_index=index;
							return false;
						}
					})
					if(exist_flag){
						//vm.zzj_no="";					
						js.showErrorMessage("抱歉，第"+(_index+1)+"行存在重复数据!",1000 * 10);
						return false;
					}	
					/**
					 * 计划外工序，无 机台计划，计划数量plan_quantity默认为上工序产量last_prod_quantity
					 */
					if(vm.process_type == "02"){
						detail.plan_quantity=detail.last_prod_quantity;
					}
					
					if(detail.plan_quantity - detail.prod_quantity <=0){
						vm.zzj_no="";
						finished_flag=true;
						return true;
					}
					
					detail.zzj_no=item.zzj_no;
					detail.werks = item.werks;
					detail.order_no = item.order_no;
					detail.zzj_plan_batch = item.zzj_plan_batch;
					detail.workshop = item.workshop;
					detail.line = item.line;
					detail.machine = bind_machine.machine_code;
					detail.process_name = bind_machine.process_name;
					detail.process = bind_machine.process_name;
					detail.process_code = bind_machine.process_code;
					//detail.quantity = detail.plan_quantity - detail.prod_quantity; 
					detail.werks_name = bind_machine.werks_name;
					detail.workshop_name = bind_machine.workshop_name;
					detail.line_name = bind_machine.line_name;
					detail.workgroup = bind_machine.workgroup;
					detail.workgroup_name = bind_machine.workgroup_name;
					detail.team = bind_machine.team;
					detail.team_name = bind_machine.team_name;
					detail.product_date = formatDate(new Date());
					detail.productor = $("#productor").val();
					
					/**
					 * 查询上一道工序的产量，录入数量不能超出上一工序产量，上工序在工艺流程中存在多个时，不做判断
					 * @returns
					 */
					var last_process = detail.last_process
					console.info("last_process:"+last_process);			
					var count_p =0;
					detail.process_flow.split("-").forEach(function(value,index,arr){
						if(value==last_process){
							count_p++;
						}
					})
					if(count_p==1){
						detail.able_qty=Math.min((detail.last_prod_quantity- detail.prod_quantity),(detail.plan_quantity - detail.prod_quantity));
						detail.quantity = detail.able_qty;
					}else{
						detail.quantity = detail.plan_quantity - detail.prod_quantity;
						detail.able_qty = detail.quantity;
					}
			
					var ids = $(vm.table_id).jqGrid('getDataIDs');
					if(ids.length==0){
						ids=[0]
					}
				    var rowid = Math.max.apply(null,ids) + 1;
				    detail.ID = rowid;
				    
					$(vm.table_id).jqGrid("addRowData", rowid, detail, "last"); 
					vm.count_info_3++;
					
				})
				
				//零部件所有计划都已达成，提示信息：计划已完成，无须录入产量
				if(finished_flag){
					js.showErrorMessage("该零部件："+item.zzj_no+" 的计划已达成，无须录入产量",1000 * 10);
				}
			} 
			vm.zzj_no=item.zzj_no;
			//生产自检
			if(vm.tab_id.indexOf("div_4")>=0){			
				vm.getTestRules(item);
			} 
	
		}

	})
	
	/**
	 * 扫描机台
	 */
	$(document).on("keydown","#machine_code",function(e){
		if(e.keyCode == 13){
			//校验机台编码是否有效
			checkJT(vm.machine_code);		
			if(vm.tab_id == '#div_1' ){
				vm.$nextTick(function(){
					vm.query();
				})
			}
			if(vm.tab_id == '#div_3' ){					
				$(vm.table_id).jqGrid("clearGridData");
				$(vm.table_id).jqGrid('setGridParam',{datatype:'local'}).trigger('reloadGrid'); 							
				//获取工序是否为计划外工序
				var process_info = getStandardProcess();
				if(process_info.length>0){
					process_info = process_info[0];
					vm.process_type = process_info.PROCESS_TYPE;
				}
				console.info("PROCESS_TYPE："+vm.process_type);
				vm.$nextTick(function(){
					vm.query();
				})
			}
		}
		
	})

	
})


var getBatchSelects=(werks,order_no)=>{
	$.ajax({
		type : "post",
		dataType : "json",// 返回json格式的数据
		async : false,
		url : baseURL+"/zzjmes/common/getPlanBatchList",
		data : {
			"werks" : werks,
			"order_no":order_no
		},
		success:function(response){
			if(response.data==null){
				vm.batch_list=[];
			}else
				vm.batch_list=response.data;
				
				vm.batch=(vm.batch_list==undefined||vm.batch_list.length==0)?vm.batch:vm.batch_list[0].batch;
				$("#batch").val(vm.batch);
			/*if(vm.batch_list.length==1){
				vm.batch = vm.batch_list[0].batch
			}*/
		}	
	});
}

/**
 * 校验机台是否有效
 */
var checkJT = jt_no=>{
	var match_jt=null;
	if(jt_no != null && jt_no.trim().length>0){
		$.each(vm.bind_machine_list,function(i,jt){
			if(jt.machine_code==jt_no){
				match_jt=jt.machine_code;
				vm.bind_machine=jt;
				return false;
			}
		})
		if(match_jt==null){
			match_jt=vm.bind_machine.machine_code;
			js.showErrorMessage("抱歉，您没有操作机台"+jt_no+"的权限!");
		}
		vm.machine_code=match_jt;
		vm.process =vm.bind_machine.process_name; 
		vm.werks = vm.bind_machine.werks;
	}
}

var queryJTPlan =()=>{
	var data = $(vm.table_id).data("dataGrid");
	if (data) {
		$(vm.table_id).jqGrid('GridUnload');
		js.closeLoading();
		$(".btn").attr("disabled", false);
		$(vm.table_id).jqGrid("clearGridData");
		$(vm.table_id).jqGrid('setGridParam',{datatype:'local'}).trigger('reloadGrid'); 
	}
	var columns = [ {
		label : '图号',
		name : 'material_no',
		align : "center",
		index : 'material_no',
		width : 150,
		formatter:function(val, obj, row, act){
        	return "<a href='javascript:void(0);' style='text-align:center;width:100%;' onclick=getPdf('"+row.werks+"','"+val+"')>"+val+"</a>";
		},
		unformat:function(cellvalue, options, rowObject){
			return cellvalue;
		}
	}, {
		label : '零部件号',
		name : 'zzj_no',
		align : "center",
		index : 'zzj_no',
		width : 150,
		formatter:function(val, obj, row, act){
        	return "<a href='javascript:void(0);' style='text-align:center;width:100%;' onclick=getException('"+row.werks+"','"+row.plan_item_id+"')>"+val+"</a>";
		},
		unformat:function(cellvalue, options, rowObject){
			return cellvalue;
		}
	}, {
		label : '零部件名称',
		name : 'zzj_name',
		align : "center",
		index : 'zzj_name',
		width : 200
	},  {
		label : '状态',
		name : 'status',
		align : "center",
		index : 'status',
		width : 60
	},{
		label : '下料尺寸',
		name : 'filling_size',
		align : "center",
		index : 'filling_size',
		width : 90
	},{
		label : '孔特征',
		name : 'aperture',
		align : "center",
		index : 'aperture',
		width : 80
	},{
		label : '单车用量',
		name : 'single_qty',
		align : "center",
		index : 'single_qty',
		width : 80
	},{
		label : '批次',
		name : 'zzj_plan_batch',
		align : "center",
		index : 'zzj_plan_batch',
		width : 60,
	},{
		label : '计划数量',
		name : 'plan_quantity',
		align : "center",
		index : 'plan_quantity',
		width : 80
	},{
		label : '计划日期',
		name : 'plan_date',
		align : "center",
		index : 'plan_date',
		width : 100
	},{
		label : '材料规格',
		name : 'specification',
		align : "center",
		index : 'specification',
		width : 130
	},{
		label : '精度',
		name : 'accuracy_demand',
		align : "center",
		index : 'accuracy_demand',
		width : 90
	},{
		label : '分包类型',
		name : 'subcontracting_type',
		align : "center",
		index : 'subcontracting_type',
		width : 90
	},{
		label : '工艺流程',
		name : 'process_flow',
		align : "center",
		index : 'process_flow',
		width : 160
	},{
		label : '加工顺序',
		name : 'process_sequence',
		align : "center",
		index : 'process_sequence',
		width : 70
	},{
		label : '装配位置 ',
		name : 'assembly_position',
		align : "center",
		index : 'assembly_position',
		width : 100
	},{
		label : '使用工序 ',
		name : 'process',
		align : "center",
		index : 'process',
		width : 100
	},{
		label : '订单 ',
		name : 'order_desc',
		align : "center",
		index : 'order_desc',
		width : 180
	},{
		label : '加工设备 ',
		name : 'process_machine',
		align : "center",
		index : 'process_machine',
		width : 100
	},{
		label : '计划工序',
		name : 'plan_process',
		align : "center",
		index : 'plan_process',
		width : 100
	},{
		label : '使用车间',
		name : 'use_workshop',
		hidden : true,
		index : 'use_workshop',
	},{
		label : 'plan_item_id',
		name : 'plan_item_id',
		hidden : true,
		index : 'plan_item_id',
	},{
		label : 'zzj_pmd_items_id',
		name : 'zzj_pmd_items_id',
		hidden : true,
		index : 'zzj_pmd_items_id',
	},{
		label : 'order_no',
		name : 'order_no',
		hidden : true,
		index : 'order_no',
	},{
		label : 'werks',
		name : 'werks',
		hidden : true,
		index : 'werks',
	},{
		label : 'werks_name',
		name : 'werks_name',
		hidden : true,
		index : 'werks_name',
	},{
		label : 'machine',
		name : 'machine',
		hidden : true,
		index : 'machine',
	},{
		label : 'workshop',
		name : 'workshop',
		hidden : true,
		index : 'workshop',
	},{
		label : 'workshop_name',
		name : 'workshop_name',
		hidden : true,
		index : 'workshop_name',
	},{
		label : 'line',
		name : 'line',
		hidden : true,
		index : 'line',
	},{
		label : 'line_name',
		name : 'line_name',
		hidden : true,
		index : 'line_name',
	},{
		label : 'exception_str',
		name : 'exception_str',
		hidden : true,
		index : 'exception_str',
	},{
		label : 'id',
		name : 'id',
		hidden : true,
		index : 'id',
	},{
		label : 'plan_batch_qty',
		name : 'plan_batch_qty',
		hidden : true,
		index : 'plan_batch_qty',
	},
	];
	
	dataGrid = $(vm.table_id).dataGrid({
		//datatype:'local',
		searchForm : $("#searchForm"),
		colModel : columns,
		cellEdit:true,
		cellurl:'#',
		cellsubmit:'clientArray',
	    viewrecords: false,
	    showRownum:false,
	    autowidth:true,
        shrinkToFit:false,  
        autoScroll: true, 
		//dataid:'id',
		multiselect: true,
		loadComplete : function(data) {
			js.closeLoading();
			$(".btn").attr("disabled", false);
			if (data.code == 500) {
				js.showErrorMessage(data.msg, 1000 * 10);
				return false;
			}
			var rows=$(vm.table_id).jqGrid('getRowData');
			var count_list =[];
			var zzj_no_list = [];
			var total_count = 0;
			rows.map(function(e,i,self){
				zzj_no_list.push(e.zzj_no)
				total_count+= Number(e.plan_quantity);
				if ( e.exception_str.indexOf("已处理") == -1 && e.exception_str.indexOf("未处理")  != -1) {//过滤条件  
					$(vm.table_id).jqGrid('setRowData', e.id, '', {'color':'red'});  
				}
				if ( e.exception_str.indexOf("已处理") != -1 && e.exception_str.indexOf("未处理")  != -1) {//过滤条件  
					$(vm.table_id).jqGrid('setRowData', e.id, '', {'color':'red'});  
				}
				if ( e.exception_str.indexOf("已处理") != -1 && e.exception_str.indexOf("未处理")  == -1) {//过滤条件  
					$(vm.table_id).jqGrid('setRowData', e.id, '', {'color':'green'});  
				}

			})
			/*count_list = zzj_no_list.filter(function(e, i, self) {
				console.info(i+":  "+e)
				console.info(i+":  "+self[i])
				return self.indexOf(e) == i;
			});*/
			console.info(count_list);
			vm.count_info_1 = total_count + "/" +zzj_no_list.length;
			
		},	
		beforeSelectRow: function (rowid, e) {  //点击复选框才选中行
			   var $myGrid = $(this),  
			   i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),  
			   cm = $myGrid.jqGrid('getGridParam', 'colModel'); 
			  // alert(JSON.stringify(cm))
			   if(cm[i].name == 'cb'){
				   console.info(rowid)
				   $myGrid.jqGrid('setSelection',rowid);
			   }
			   		
			   return (cm[i].name == 'cb');  
			  /* $('#dataGrid').jqGrid('setSelection',rowid);
			   return false;*/
			}, 
	});

}

var showJTBindTable =(gridData)=>{
	
	gridData = $(vm.table_id).data("dataGrid")||[];
	/*if (gridData) {
		$(vm.table_id).jqGrid('GridUnload');
		js.closeLoading();
		$(".btn").attr("disabled", false);
		//return false;
	}*/
	var columns = [ {
		label:'ID',name:'ID', index: 'ID',key: true ,hidden:true,formatter:function(val, obj, row, act){
		//console.info(row)
		return row.ID
	}},{ 
		label: '<i class=\"fa fa-times fa-lg\" style=\"color:red\" onclick=\"delAllBindItem()\" ></i>', name: 'EMPTY_COL', index: '', align:"center",width: 70, sortable:false,formatter:function(val, obj, row, act){
			var actions = [];
			actions.push('<a href="#" onClick="delBindItem('+row.ID+')" title="删除" data-confirm="确认要删除该行数据吗？"><i class="fa fa-times fa-lg" style=\"color:red\"></i></a>');
			/*actions.push('<a href="#" onClick="copyBindItem('+row.ID+')" title="复制" ><i class="fa fa-clone " style=\"color:green\"></i></a>');*/
			return actions.join("&nbsp;&nbsp;&nbsp;&nbsp;");
		},unformat:function(cellvalue, options, rowObject){
		return "";
	}}, {
		label : '零部件号',
		name : 'zzj_no',
		align : "center",
		index : 'zzj_no',
		sortable:false,
		width : 150
	}, {
		label : '零部件名称 ',
		name : 'zzj_name',
		align : "center",
		index : 'zzj_name',
		sortable:false,
		width : 200
	}, {
		label : '装配位置 ',
		name : 'assembly_position',
		align : "center",
		index : 'assembly_position',
		sortable:false,
		width : 100
	},{
		label : '使用工序 ',
		name : 'process',
		align : "center",
		index : 'process',
		sortable:false,
		width : 100
	},{
		label : '工艺流程 ',
		name : 'process_flow',
		align : "center",
		index : 'process_flow',
		sortable:false,
		width : 160
	},{
		label : '计划数量 ',
		name : 'plan_quantity',
		align : "center",
		index : 'plan_quantity',
		sortable:false,
		width : 80
	},{ label: '<span style="color:blue">计划日期</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'plan_date\')" title="向下填充" style="color:green" aria-hidden="true"></i>', 
		name: 'plan_date', align:"center",index: 'plan_date', sortable:false,width: 100,editable:true,editoptions:{
        dataInit:function(e){
            $(e).click(function(e){
            	WdatePicker({dateFmt:'yyyy-MM-dd'})
            });
        }
    } },{
		label : '批次车付数 ',
		name : 'batch_quantity',
		align : "center",
		index : 'batch_quantity',
		sortable:false,
		width : 130
	},{
		label : '生产工单 ',
		name : 'product_order',
		align : "center",
		index : 'product_order',
		sortable:false,
		width : 130
	},{
		label : '计划工序',
		name : 'plan_process',
		align : "center",
		index : 'plan_process',
		sortable:false,
		width : 100
	},{
		label : '班组 ',
		name : 'workgroup_name',
		align : "center",
		index : 'workgroup_name',
		sortable:false,
		width : 100
	},{
		label : '小班组',
		name : 'team_name',
		hidden : true,
		index : 'team_name',
	},{
		label : '订单 ',
		name : 'order_desc',
		align : "center",
		index : 'order_desc',
		width : 180
	},{
		label : 'no',
		name : 'no',
		hidden : true,
		index : 'no',
	},{
		label : 'zzj_pmd_items_id',
		name : 'zzj_pmd_items_id',
		hidden : true,
		index : 'zzj_pmd_items_id',
	},{
		label : 'order_no',
		name : 'order_no',
		hidden : true,
		index : 'order_no',
	},{
		label : 'werks',
		name : 'werks',
		hidden : true,
		index : 'werks',
	},{
		label : 'machine',
		name : 'machine',
		hidden : true,
		index : 'machine',
	},{
		label : 'zzj_plan_batch',
		name : 'zzj_plan_batch',
		hidden : true,
		index : 'zzj_plan_batch',
	},{
		label : 'process_code',
		name : 'process_code',
		hidden : true,
		index : 'process_code',
	},{
		label : 'workshop',
		name : 'workshop',
		hidden : true,
		index : 'workshop',
	},{
		label : 'line',
		name : 'line',
		hidden : true,
		index : 'line',
	},
	
	];
	
	dataGrid = $(vm.table_id).dataGrid({
		datatype:'local',
		searchForm : $("#searchForm"),
		data:gridData,
		colModel : columns,
		cellEdit:true,
		cellurl:'#',
		cellsubmit:'clientArray',
	    viewrecords: false,
	    showRownum:false,
	    autowidth:true,
        shrinkToFit:false,  
        autoScroll: true, 
		//dataid:'id',
		multiselect: false,
		loadComplete : function(data) {
			js.closeLoading();
			$(".btn").attr("disabled", false);
			if (data.code == 500) {
				js.showErrorMessage(data.msg, 1000 * 10);
				return false;
			}
			var rows=$(vm.table_id).jqGrid('getRowData');
			var count_list =[];
			var zzj_no_list = [];
			var total_count = 0;
			rows.map(function(e,i,self){
				zzj_no_list.push(e.zzj_no)
				total_count+= Number(e.plan_quantity);
			})
			count_list = zzj_no_list.filter(function(e, i, self) {
				/*console.info(i+":  "+e)
				console.info(i+":  "+self[i])*/
				return self.indexOf(e) == i;
			});
			console.info(count_list);
			vm.count_info_1 = total_count + "/" +count_list.length;
			
		},	
		beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
		},
		
	});
}

var showJTOutTable = (gridData)=>{

	gridData= $(vm.table_id).data("dataGrid");
	/*if (gridData) {
		$(vm.table_id).jqGrid('GridUnload');
		js.closeLoading();
		$(".btn").attr("disabled", false);
		return false;
	}*/
	var columns = [ {
		label:'ID',name:'ID', index: 'ID',key: true ,hidden:true,formatter:function(val, obj, row, act){
		//console.info(row)
		return row.ID
	}},{ 
		label: '<i class=\"fa fa-times fa-lg\" style=\"color:red\" onclick=\"delAllBindItem()\" ></i>', name: 'EMPTY_COL', index: '', align:"center",width: 70, sortable:false,formatter:function(val, obj, row, act){
			var actions = [];
			actions.push('<a href="#" onClick="delBindItem('+row.ID+')" title="删除" data-confirm="确认要删除该行数据吗？"><i class="fa fa-times fa-lg" style=\"color:red\"></i></a>');
			/*actions.push('<a href="#" onClick="copyBindItem('+row.ID+')" title="复制" ><i class="fa fa-clone " style=\"color:green\"></i></a>');*/
			return actions.join("&nbsp;&nbsp;&nbsp;&nbsp;");
		},unformat:function(cellvalue, options, rowObject){
		return "";
	}}, {
		label : '零部件号',
		name : 'zzj_no',
		align : "center",
		index : 'zzj_no',
		sortable:false,
		width : 150
	}, {
		label : '零部件名称 ',
		name : 'zzj_name',
		align : "center",
		index : 'zzj_name',
		sortable:false,
		width : 200
	}, {
		label : '计划批次 ',
		name : 'zzj_plan_batch',
		align : "center",
		index : 'zzj_plan_batch',
		sortable:false,
		width : 80
	},{
		label : '计划数量 ',
		name : 'plan_quantity',
		align : "center",
		index : 'plan_quantity',
		sortable:false,
		width : 80
	},{display:'生产数量',label : '<span style="color:red">*</span><span style="color:blue">生产数量</span> ',
		name : 'quantity',
		align : "center",
		index : 'quantity',
		sortable:false,
		editable:true,
		editrules:{ required:true,number:true},  formatter:'integer',
		width : 80
	},{ display:'生产日期',label: '<span style="color:red">*</span><span style="color:blue">生产日期</span>&nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'product_date\')" title="向下填充" style="color:green" aria-hidden="true"></i>', 
		name: 'product_date', align:"center",index: 'product_date', sortable:false,width: 100,editable:true,editrules:{ required:true,date:true}, editoptions:{
	        dataInit:function(e){
	            $(e).click(function(e){
	            	WdatePicker({dateFmt:'yyyy-MM-dd'})
	            });
	        }
	    } },{display:'加工者',label : '<span style="color:red">*</span><span style="color:blue">加工者</span> &nbsp;&nbsp;<i class="fa fa-clone fill-down" onclick="fillDown(\'productor\')" title="向下填充" style="color:green" aria-hidden="true"></i>',
			name : 'productor',
			align : "center",
			index : 'productor',
			sortable:false,
			editable:true,
			editrules:{ required:true}, 
			width : 80
		},{
		label : '装配位置 ',
		name : 'assembly_position',
		align : "center",
		index : 'assembly_position',
		sortable:false,
		width : 100
	},{
		label : '使用工序 ',
		name : 'use_process',
		align : "center",
		index : 'use_process',
		sortable:false,
		width : 100
	},{
		label : '工艺流程 ',
		name : 'process_flow',
		align : "center",
		index : 'process_flow',
		sortable:false,
		width : 160
	},{
		label : '计划日期 ',
		name : 'plan_date',
		align : "center",
		index : 'plan_date',
		sortable:false,
		width : 80
	},{
		label : '生产工序',
		name : 'process',
		align : "center",
		index : 'process',
		sortable:false,
		width : 100
	},{
		label : '班组 ',
		name : 'workgroup_name',
		align : "center",
		index : 'workgroup_name',
		sortable:false,
		width : 100
	},{
		label : '小班组',
		name : 'team_name',
		hidden : true,
		index : 'team_name',
	},{
		label : '订单 ',
		name : 'order_desc',
		align : "center",
		index : 'order_desc',
		width : 180
	},{
		label : 'zzj_pmd_items_id',
		name : 'zzj_pmd_items_id',
		hidden : true,
		index : 'zzj_pmd_items_id',
	},{
		label : 'order_no',
		name : 'order_no',
		hidden : true,
		index : 'order_no',
	},{
		label : 'werks',
		name : 'werks',
		hidden : true,
		index : 'werks',
	},{
		label : 'machine',
		name : 'machine',
		hidden : true,
		index : 'machine',
	},{
		label : 'process_code',
		name : 'process_code',
		hidden : true,
		index : 'process_code',
	},{
		label : 'workshop',
		name : 'workshop',
		hidden : true,
		index : 'workshop',
	},{
		label : 'workshop_name',
		name : 'workshop_name',
		hidden : true,
		index : 'workshop_name',
	},{
		label : 'line',
		name : 'line',
		hidden : true,
		index : 'line',
	},{
		label : 'line_name',
		name : 'line_name',
		hidden : true,
		index : 'line_name',
	},{
		label : 'plan_item_id',
		name : 'plan_item_id',
		hidden : true,
		index : 'plan_item_id',
	},{
		label : 'prod_quantity',
		name : 'prod_quantity',
		hidden : true,
		index : 'prod_quantity',
	},{
		label : 'werks_name',
		name : 'werks_name',
		hidden : true,
		index : 'werks_name',
	},{
		label : 'able_qty',
		name : 'able_qty',
		hidden : true,
		index : 'able_qty',
	},{
		label : 'last_prod_quantity',
		name : 'last_prod_quantity',
		hidden : true,
		index : 'last_prod_quantity',
	},
	{
		label : 'last_plan_head_id',
		name : 'last_plan_head_id',
		hidden : true,
		index : 'last_plan_head_id',
	},
	];
	
	dataGrid = $(vm.table_id).dataGrid({
		datatype:'local',
		searchForm : $("#searchForm"),
		data:gridData,
		colModel : columns,
		cellEdit:true,
		cellurl:'#',
		cellsubmit:'clientArray',
	    viewrecords: false,
	    showRownum:false,
	    autowidth:true,
        shrinkToFit:false,  
        autoScroll: true, 
		//dataid:'id',
		multiselect: false,
		loadComplete : function(data) {
			js.closeLoading();
			$(".btn").attr("disabled", false);
			if (data.code == 500) {
				js.showErrorMessage(data.msg, 1000 * 10);
				return false;
			}
			var rows=$(vm.table_id).jqGrid('getRowData');
			var count_list =[];
			var zzj_no_list = [];
			var total_count = 0;
			rows.map(function(e,i,self){
				zzj_no_list.push(e.zzj_no)
				total_count+= Number(e.plan_quantity);	
			})
			count_list = zzj_no_list.filter(function(e, i, self) {
				/*console.info(i+":  "+e)
				console.info(i+":  "+self[i])*/
				return self.indexOf(e) == i;
			});
			console.info(count_list);
			vm.count_info_1 = total_count + "/" +count_list.length;
	
		},	
		beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
		},
		afterSaveCell: function(rowid, cellname, value, iRow, iCol){
			var row =  $(vm.table_id).jqGrid('getRowData', rowid);
			if(cellname=='quantity'){
				var able_qty =row.able_qty;
				if(Number(row.quantity)> Number(able_qty)){
					js.alert("生产数量不能大于可录入数量："+able_qty);
					$(vm.table_id).jqGrid('setCell', rowid, cellname, able_qty,'editable-cell');
				}		
				
			}			
			
		},
		
	});
}

function delAllBindItem(){
	if(confirm("确认删除所有数据？")){
		$(vm.table_id).jqGrid('clearGridData');
	}
}

function delBindItem(rowid){	
	$(vm.table_id).delRowData(rowid)
}


function doScan(ele){
	var url = "../../doScan";
	last_scan_ele = ele;
	var iframe_id=self.frameElement.getAttribute('id');
	$(window.parent.document).find("#activeIframe").val(iframe_id)
	var a = document.createElement("a");
	a.target="_parent";
	a.href = url;
	a.click();
}

function funFromjs(result){ 
	var e = $.Event('keydown');
    e.keyCode = 13; 	
	if(last_scan_ele=="machine_code"){
		var rs_arr = result.split(";");
		$.each(rs_arr,function(i,rs){
			var _arr = rs.split(":")
			if(_arr[0]=="m"){
				vm.machine_code = _arr[1];
			}
		})
		$("#"+last_scan_ele).val(vm.machine_code);
		$("#"+last_scan_ele).trigger(e);
	}else if (last_scan_ele == "productor") {//扫描加工者一维码
		$("#"+last_scan_ele).val(result);		
	}
	else{
		var mat = JSON.parse(result);	
		vm.batch = mat.zzj_plan_batch;
		vm.order_no= mat.order_no;
		vm.zzj_no = mat.zzj_no;	
	    $("#"+last_scan_ele).val(mat.zzj_no);
	    $("#"+last_scan_ele).trigger(e);
	}

}

function ajaxGetPmdItems(item){
	var list=[];
	$.ajax({
		type : "post",
		dataType : "json",// 返回json格式的数据
		async : false,
		url : baseUrl+"zzjmes/jtOperation/getPmdItems",
		data : item,
		success:function(response){
			list = response.data;
			material_no = response.material_no;
			if(response.msg!="success"){
				if(vm.tab_id.indexOf("div_2")>=0){
					list=[];
					js.showErrorMessage(response.msg,1000 * 10);
				}
			}
		}	
	});
	
	return list;
}

function ajaxGetMachineAchieve(){
	$.ajax({
		type : "post",
		dataType : "json",// 返回json格式的数据
		async : false,
		url : baseUrl+"zzjmes/jtOperation/getMachineAchieve",
		data : {
			order_no : vm.order_no,
			machine : vm.bind_machine.machine_code,
			werks : vm.bind_machine.werks,
			process : vm.process,
		},
		success:function(response){
			if(response.msg=="success"){
				var data = response.data;
				vm.count_info_2 = data.done_qty+"/"+data.need_qty;
			}else{
				js.showErrorMessage(response.msg,1000 * 10);
			}
		}	
	});

}

function fillDown(e){
	console.info(lastcellname)
	$(vm.table_id).jqGrid("saveCell", lastrow, lastcell);
	var rowid=lastrow;
	var rows=$(vm.table_id).jqGrid('getRowData');
/*	if(ids!=null){
		rowid=Math.min.apply(null,ids);
	}*/
	var last_row=$(vm.table_id).jqGrid('getRowData',rowid);
	var cellvalue=last_row[lastcellname];
	
	if(lastcellname==e){
		$.each(rows,function(i,row){
			console.info(Number(row.ID)+"/"+rowid+"/"+cellvalue)
			if(Number(row.ID)>=Number(rowid)&&cellvalue){
				$(vm.table_id).jqGrid('setCell',Number(row.ID), e, cellvalue,'editable-cell');
			}
		})
	}
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

/**
 * 输入零部件号查询产量录入表格数据，
 * @param item
 * @returns
 */
function ajaxGetPmdOutItems(item){
	var list=[];
	$.ajax({
		type : "post",
		dataType : "json",// 返回json格式的数据
		async : false,
		url : baseUrl+"zzjmes/jtOperation/getPmdOutputItems",
		data : item,
		success:function(response){
			if(response.msg=="success"){
				list = response.data;
			}else{
				js.showErrorMessage(response.msg,1000 * 10);
			}
		}	
	});
	return list;
}

function getStandardProcess(){
	var list =[];
	$.ajax({
		type : "post",
		dataType : "json",// 返回json格式的数据
		async : false,
		url : baseUrl+"masterdata/getWorkshopProcessList",
		data : {
			WERKS : vm.werks,
			WORKSHOP : vm.bind_machine.workshop,
			PROCESS : vm.process
		},
		success:function(response){
			if(response.msg=="success"){
				list = response.data;
			}
		}	
	});
	return list;
}

function queryTestRecord(mydata){
	var batchSelectArr="OK:OK;NG:NG";
	console.log("vm.table_id",vm.table_id);
	$(vm.table_id).jqGrid('GridUnload');
	$(vm.table_id).dataGrid({
		datatype: "local",
		data: mydata,
        colModel: [		
            {label: '<SPAN STYLE="COLOR:RED">*</SPAN>检具编号', name: 'test_tool_no',index:"test_tool_no", width: "150",align:"center",sortable:false,editable:true,
            	editoptions:{
            		dataInit:function(el){
            		　 $(el).keydown(function(){
            		　         getTestToolNoSelect(el,null);
            		　 });
            	},}
            },
            {label: '<SPAN STYLE="COLOR:RED">*</SPAN>外观标准', name: 'outward_standard',index:"outward_standard", width: "160",align:"center",editable:true,editoptions:{
        		dataInit:function(el){
          		　 $(el).keydown(function(){
          		　         getTestStandardNoSelect(el,null);
          		　 });
          	}}  // ,dataEvents:[{type:"change",fn:function(e){}},]
          },
            {label: '<SPAN STYLE="COLOR:RED">*</SPAN>尺寸标准', name: 'size_standard',index:"size_standard", width: "120",align:"center",sortable:false,editable:true,},
            {label: '<SPAN STYLE="COLOR:RED">*</SPAN>外观检验结果', name: 'outward_result',index:"outward_result", width: "180",align:"center",sortable:false,editable:true,},
            {label: '<SPAN STYLE="COLOR:RED">*</SPAN>尺寸检验结果', name: 'size_result',index:"size_result", width: "180",align:"center",sortable:false,editable:true,},
            {label: '<SPAN STYLE="COLOR:RED">*</SPAN>判定', name: 'test_result',index:"test_result", width: "70",align:"center",sortable:false,editable:true,edittype:"select",
				editrules:{required: true},editoptions: {value:batchSelectArr,dataEvents:[
					{type:"click",fn:function(e){}},]},
			},
            {label: '<SPAN STYLE="COLOR:RED">*</SPAN>检验员', name: 'product_test',index:"product_test", width: "90",align:"center",editable:true,sortable:false},
            {label: '<SPAN STYLE="COLOR:RED">*</SPAN>检验日期', name: 'product_test_date',index:"product_test_date", width: "120",align:"center",editable:true,editrules:{required: true},
            	editoptions:{
            		dataInit:function(el){
            		　 $(el).click(function(){
            		　         WdatePicker();
            		　 });
            	}}
            },
            {label: '零部件名称', name: 'zzj_name',index:"zzj_name", width: "100",align:"center",sortable:false},
            {label: '图号', name: 'material_no',index:"material_no", hidden:true},
        ],
        loadComplete:function(){
			js.closeLoading();
			$(".btn").attr("disabled", false);
		},
		beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
		},
		afterSaveCell:function(rowid, cellname, value, iRow, iCol){
			console.log("iRow-iCol",cellname+":"+iRow+"-"+iCol+":"+value);
			var grid=$(vm.table_id);
			var rowData = $(grid).jqGrid('getRowData',rowid);
			// 第一行录入数据  自动向下填充（除判定）
			if(iRow==1 && cellname!='test_result'){
				var ids=  $(grid).jqGrid("getDataIDs");
				for(var i in ids){
					$(grid).jqGrid("setCell",ids[i],cellname,value);
				}
			}
				    // enter键 移动到下一行单元格
//				window.setTimeout(function(){ 
//               	   $("#dataGrid").jqGrid("editCell",iRow,iCol+1,true); 
//               	},100);
//			}
		},
		shrinkToFit: false,
		rownumbers: false,
        cellurl:'#',
		cellsubmit:'clientArray',
		cellEdit:true,
    });	
}

function getException(werks,plan_item_id){
	console.log("-->getException werks:" + werks + "|plan_item_id:" + plan_item_id)
	//默认通过plan_item_id查询工厂车零部件号等信息。没有计划时plan_item_id取0，通过pmd_item_id查询工厂车零部件号等信息
	var options = {
		type: 2,
		maxmin: true,
		shadeClose: true,
		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>异常录入',
		area: ["600px", "400px"],
		content: "/web/zzjmes/product/productionException.html?plan_item_id="+plan_item_id + "&plan_item_id=0",  
		btn : [ '确定' ,	'取消'],
		btn1 : function(index,layero) {
			console.log('btn1 ' + index)
			var win = top[layero.find('iframe')[0]['name']];
			var btnSubmit= $("#btnSubmit", layero.find("iframe")[0].contentWindow.document);
			$(btnSubmit).click();
			js.layer.close(index);
		},
		btn2 : function(index) {
			console.log('btn2 ' + index)
			js.layer.close(index);
			$("#btnConfirm").removeAttr("disabled");
		}
	};
	js.layer.open(options);
}

function ajaxGetPmdBaseInfo(item){
	var info={};
	$.ajax({
		type : "post",
		dataType : "json",// 返回json格式的数据
		async : false,
		url : baseUrl+"zzjmes/jtOperation/getPmdBaseInfo",
		data : item,
		success:function(response){
			info = response.data;
			
		}	
	});
	
	return info;
}

function checkBindPlan(item) {
	var flag = true;
	$.ajax({
		type : "post",
		dataType : "json",// 返回json格式的数据
		async : false,
		url : baseUrl+"zzjmes/jtOperation/checkBindPlan",
		data : item,
		success:function(response){
			if (response.data !=null && response.data.plan_item_id>0) {
				flag=false;
			}
			
		}	
	});
	return flag;
}
