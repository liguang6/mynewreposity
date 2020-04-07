/**
单批质检 VueJS
**/
var vm = new Vue({
	el:"#vue-app",
	data:{
		items:[],//单批质检项列表
		qcResultCodes:[],//质检结果字典
		returnreasons:[],//不合格原因字典
		reasonCode:"01",//不合格原因
		currentUser:{},//当前登录用户信息
		itemTemplate:{},//单批质检项模板
		qcResult:[],//质检结果配置-那些质检结果需要退货
		type:"",
		costcenter:"",//成本中心
		costcenterValid:false,
		plant:{},
		costCenterName:"",//成本中心名字,
		reason_type: null
	},
	watch:{
		costcenter:{
			handler:function(newVal,oldVal){
				var costcenter = newVal;
				var werks = vm.itemTemplate.werks;
				if(costcenter.trim() !=''){
					$.ajax({
						url:baseUrl + "common/sapCostcenter/"+costcenter,
						success:function(resp){
							if(resp.COSTCENTER === ''){
								alert(costcenter+"成本中心不存在")
								vm.costcenterValid = false;
								vm.costCenterName = "";
							}
							else if(resp.COMP_CODE !== vm.plant.bukrs){
								alert(costcenter+"成本中心不属于"+vm.plant.bukrs+"公司");
								vm.costcenterValid = false;
								vm.costCenterName = "";
							}else {
								vm.costcenterValid = true;
								vm.costCenterName = resp.DESCRIPT;
							}
						}
					})
				}

			}
		},
		reason_type:{
			handler:function(newVal,oldVal){
				//查询退货原因信息
		        $.ajax({
		        	url:baseUrl + "config/wmscqcreturnreasons/list",
		      	  	data:{"REASON_TYPE": newVal},
		        	success:function(resp){
		        		vm.returnreasons = resp.page.list;
		        	}
		        });
			}
		}
	},
	created:function(){	
		this.reason_type = '';
		//获取质检项信息
		var inspectionNo = $("#inspectionNo").val();
		var inspectionItemNo = $("#inspectionItemNo").val();
		var batch = $("#batch").val();
		var werks = $("#werks").val();
		var type = $("#type").val();//质检状态 字典定义：00未质检 01质检中 02已质检
		this.type = type;
		$.post(baseUrl + "qc/wmsqcinspectionitem/list/",{"inspectionNo":inspectionNo,"inspectionItemNo":inspectionItemNo,"inspectionItemStatus":type,"batch":batch,"werks":werks},function(resp){
				this.itemTemplate = resp.page.list[0];
				this.items = resp.page.list;
				//1.未质检
				if(type === '00'){
					this.items[0].checkedQty = this.items[0].inspectionQty;
				}
				//质检中
				if(type === '01'){
					//送检数量 = 质检数量之和
					var  totalQty = 0;
					this.items.map(function(val){
						totalQty +=parseFloat(val.checkedQty);
					});
					for(var i in this.items){
						this.items[i].inspectionQty = totalQty;
					}
				}
				vm.queryPlant();
				//查询质检结果配置
			$.ajax({
					 url:baseUrl + "config/wmscqcresult/list2",
					 type:"post",
					 data:{"werks":this.itemTemplate.werks},
					 success:function(resp){
						   this.qcResult = resp.page.list;
						   this.qcResultCodes = resp.page.list;
					 }.bind(this)
			})
		}.bind(this));
		
	  
	   this.templateItem = this.items[0];
	  
	},
	methods:{
		queryPlant:function(){
			   //查询工厂信息
			   $.post(baseUrl + "config/sapPlant/query?WERKS=" + vm.itemTemplate.werks,function(resp){
				   if(resp.list === undefined ||  resp.list.length === 0){
					   alert("工厂信息不存在")
				   }else{
					   vm.plant = resp.list[0];
				   }
			   });
		},
		resultChange:function(event,index_){
			var code = event.target.value;
			//设置质检结果文本
			var text = "";
			this.qcResultCodes.map(function(val){
				if(val.qcResultCode === code){
					text = val.qcResultName;
				}
			});			
			this.items[index_].qcResultText = text;
			//不合格类型标识
			var returnTypeFlag = false;
			this.qcResult.map(function(val){
				//属于可退货或者不可进仓
				if(val.qcResultCode === code && val.qcStatus !== '01' && (val.whFlag === '0' || val.returnFlag === 'X')){
					returnTypeFlag = true;
				}
			});
			if(returnTypeFlag){
				//不合格的物料，破坏数量不可编辑，破坏数量设置为0
				
				this.items[index_].destoryVisiable = false;
				this.items[index_].destoryQty = 0;
				layer.open({
					  type: 1,
					  title:"选择不合格原因",
					  area: ['70%','70%'],
					  closeBtn:1,
					  btn:['确定','取消'],
					  yes:function(index,layero){
						  var reasonText = $("#reasons").find("option:selected").text();
						  var reasonCode = $("#reasons").val();
						  var selectedType = $("#reasons").find("option:selected").attr("data-reasonType");
						  vm.items[index_].returnreason = reasonText ;
						  vm.items[index_].returnreasoncode = reasonCode;
						  vm.items[index_].returnreasontype = selectedType;
					  }.bind(this),
					  no:function(){
						  
					  },
					  fixed: false, //不固定
					  maxmin: true,
					  content: $("#returnreason")
				})
			}else{
				//合格物料，不合格原因置空
				this.items[index_].destoryVisiable = true;
				this.items[index_].returnreason = null ;
				this.items[index_].returnreasoncode = null;
				this.items[index_].returnreasontype = null;
			}
		},
		openReturnReason:function(index_){
			var code = this.items[index_].qcResultCode;
			//不合格类型标识
			var returnTypeFlag = false;
			this.qcResult.map(function(val){
				//属于可退货或者不可进仓
				if(val.qcResultCode === code && (val.returnFlag === 'X' || val.whFlag === '0' || val.reviewFlag === 'X' || val.goodFlag === 'X')){
					returnTypeFlag = true;
				}
			});
			if(returnTypeFlag === false){
				//不是退货类型，不需要弹窗
				return;
			}
			//打开质检结果弹出窗
			layer.open({
				  type: 1,
				  title:"选择不合格原因",
				  area: ['70%','70%'],
				  closeBtn:1,
				  btn:['确定','取消'],
				  yes:function(index,layero){
					  var reasonText = $("#reasons").find("option:selected").text();
					  var reasonCode = $("#reasons").val();
					  var selectedType = $("#reasons").find("option:selected").attr("data-reasonType");
					  vm.items[index_].returnreason = reasonText ;
					  vm.items[index_].returnreasoncode = reasonCode;
					  vm.items[index_].returnreasontype = selectedType;
				  }.bind(this),
				  no:function(){
					  
				  },
				  fixed: false, //不固定
				  maxmin: true,
				  content: $("#returnreason")
			});
		},
		remove:function(index){
			if(this.items.length <= 1){//只有第一行数据
				return;
			}
			//更新前一条质检记录的质检数量           新质检数量  = 质检数量 + 删除项的质检数量
			//前一项：如果删除的项是第一项则前一项为第二项
			var nextIndex = index - 1;
			if(index === 0) nextIndex = 1;
			var newCheckedQty = parseFloat(this.items[nextIndex].checkedQty) + parseFloat(this.items[index].checkedQty);
			this.items[nextIndex].checkedQty = newCheckedQty;
			this.items.splice(index,1);
		},
		add:function(index){
			var tpl = {};
			//对象复制
			$.extend(tpl,this.itemTemplate);
			tpl.returnreason = "";
			tpl.returnreasoncode = "";
			tpl.returnreasontype = "";
			tpl.qcResultCode = "";
			tpl.qcResultText = "";
			//计算质检数量 (新增项的质检数量 = 送检数量 - 已有项的质检数量之和)
			var hasCheckedQty = 0;
			this.items.map(function(val){
				hasCheckedQty += parseFloat(val.checkedQty);
			})
			var checkedQty = tpl.inspectionQty - hasCheckedQty;
			if(checkedQty <= 0){
				alert("质检数量之和不能大于送检数量");
				return;
			}
			tpl.checkedQty = checkedQty;
			
			this.items.splice(index + 1,0,tpl);
		},
		save:function(){
			js.loading("处理中...");
			$("#saveOperation").html("处理中...");
			$("#saveOperation").attr("disabled","disabled");
			//质检总数需要等于送检数量
			var hasCheckedQty = 0;
			this.items.map(function(val){
				hasCheckedQty += parseFloat(val.checkedQty);
			})
			var checkedQty = this.itemTemplate.inspectionQty - hasCheckedQty;
			if(checkedQty > 0 || checkedQty < 0){
				alert("质检数量不等于送检数量");
				$("#saveOperation").html("保存");	
				$("#saveOperation").removeAttr("disabled");
				js.closeLoading();
				return;
			}
			
			//质检结果是否为空
			//质检数量之和需小等于送检数量
			var checkedQtyTotal = 0;
			
			//判断合格数量是否大于等于试装数量+破坏数量
			var tryQty = vm.itemTemplate.tryQty;//试装数量
			if(tryQty == undefined || tryQty == null || tryQty == '')
				tryQty = 0;
			var totalQualifiedQty  = 0;//总合格数量
			var destoryTotalQty = 0;//总破坏数量
			
			for(var i = 0;i<this.items.length;i++){
				var val = this.items[i];
				
				//文本或者编码不为空
				if(val.qcResultCode === "" || val.qcResultCode === null || val.qcResultCode === undefined){
					alert("质检结果不能为空");
					$("#saveOperation").html("保存");	
					$("#saveOperation").removeAttr("disabled");
					js.closeLoading();
					return ;
				}
				
				//是否是属于不合格的,校验是否填了不合格原因
				var returnTypeFlag = false;
				vm.qcResult.map(function(val_){
					//遍历质检结果配置。    可退货或者不可进仓 需要配置不合格原因
					if(val_.qcResultCode === val.qcResultCode && (val_.returnFlag === 'X' || val_.whFlag === '0') && val_.qcStatus != '01'){
						returnTypeFlag = true;
					}
	
				});
				
				if(returnTypeFlag && (val.returnreason === null || val.returnreason === '' || val.returnreason === undefined)){
					alert("请选择不合格原因");
					$("#saveOperation").html("保存");	
					$("#saveOperation").removeAttr("disabled");
					js.closeLoading();
					return ;
				}
				
				checkedQtyTotal += parseFloat(val.checkedQty);
				
				//是否是属于合格的质检结果
				var whTypeFlag = false;
				vm.qcResult.map(function(val_){
					//遍历质检结果配置。    
					if(val_.qcResultCode === val.qcResultCode && val_.returnFlag === '0' && val_.whFlag === 'X' && val_.qcStatus == '02'){
						whTypeFlag = true;
					}
				});
				
				if(whTypeFlag){
					totalQualifiedQty += parseFloat(vm.items[i].checkedQty);
					destoryTotalQty += parseFloat(vm.items[i].destoryQty);
				}
			}
			if(checkedQtyTotal > this.items[0].inspectionQty){
				alert("质检数量之和不能大于送检数量");
				$("#saveOperation").html("保存");	
				$("#saveOperation").removeAttr("disabled");
				js.closeLoading();
				return ;
			}
			if(totalQualifiedQty < (tryQty + destoryTotalQty)){
				alert("检验合格数量("+totalQualifiedQty+")需要大于试装数量("+tryQty+")加破坏数量("+destoryTotalQty+")");
				$("#saveOperation").html("保存");	
				$("#saveOperation").removeAttr("disabled");
				js.closeLoading();
				return ;
			}
			
			
			//成本中心校验
			if(this.costcenterValid === false){
				var desItems = vm.items.filter(function(val){
					return (val.destoryQty !== undefined && val.destoryQty !== '' && val.destoryQty !== null && val.destoryQty !== 0);
				});
				if(desItems.length > 0){
					//如果存在破坏数量
					$("#saveOperation").html("保存");	
					$("#saveOperation").removeAttr("disabled");
					js.closeLoading();
					alert("成本中心为空，或不正确");
					return ;
				}
			}
			
			//设置成本中心
			for(index in vm.items){
				vm.items[index].costcenter = vm.costcenter;
			}
			
			if(this.type === '00'){
				//调用保存-来料质检-未质检
				$.ajax({
					url:baseUrl + "qc/wmsqcinspectionhead/saveinspectionbatch",
					type:"post",
					contentType:"application/json",
					data:JSON.stringify(this.items),
					success:function(resp){
						$("#saveOperation").html("保存");	
						$("#saveOperation").removeAttr("disabled");
						js.closeLoading();
						if(resp.code === 0){
							js.showMessage("保存成功");
							localStorage.setItem("refreshPage","true");
							js.closeCurrentTabPage();
						}else{
							js.showMessage("保存失败，"+resp.msg);
						}
					}
				});
			}else{
				//质检中，评审中 ...保存逻辑
				//转换为质检结果
				for(var index in this.items){
					this.items[index].resultQty = this.items[index].checkedQty;
					this.items[index].qcResult = this.items[index].returnreason;
				}
				//调用保存-来料质检-未质检-单批质检
				$.ajax({
					url:baseUrl + "qc/wmsqcinspectionhead/saveOnInspectSingleBatch",
					type:"post",
					contentType:"application/json",
					data:JSON.stringify(this.items),
					success:function(resp){
						$("#saveOperation").html("保存");	
						$("#saveOperation").removeAttr("disabled");
						js.closeLoading();
						if(resp.code === 0){
							js.showMessage("保存成功");
							localStorage.setItem("refreshPage","true");
							js.closeCurrentTabPage();
						}else{
							js.showMessage("保存失败，"+resp.msg);
						}
					}
				});
			}
		},

	}
})