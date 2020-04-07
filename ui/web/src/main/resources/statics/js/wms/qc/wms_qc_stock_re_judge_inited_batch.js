/**
单批质检 VueJS
**/
var vm = new Vue({
	el:"#vue-app",
	data:{
		items:[{"QC_RESULT":""}],//单批质检项列表
		qcResultCodes:[],//质检结果字典
		returnreasons:[],//不合格原因字典
		reasonCode:"01",//不合格原因
		currentUser:{},//当前登录用户信息
		itemTemplate:{},//单批质检项模板
		qcResult:[],//质检结果配置-那些质检结果需要退货
		type:""
		},
	created:function(){	
		//获取质检项信息
		var inspectionNo = $("#inspectionNo").val();
		var inspectionItemNo = $("#inspectionItemNo").val();
		var type = $("#type").val();//质检状态 字典定义：00未质检 01质检中 02已质检
		this.type = type;
		var batch = $("#batch").val();
		$.post(baseUrl + "qc/wmsqcinspectionitem/stock_rejudge_not_inspected/",{"INSPECTION_NO":inspectionNo,"INSPECTION_ITEM_NO":inspectionItemNo,"BATCH":batch},function(resp){
				this.itemTemplate = resp.page.list[0];
				this.items = resp.page.list;
				this.items[0].QC_RESULT = ""
				//1.未质检
				if(type === '00'){
					this.items[0].QTY = this.items[0].INSPECTION_QTY;
				}
				//质检中
				if(type === '01'){
					//送检数量 = 质检数量之和
					var  totalQty = 0;
					this.items.map(function(val){
						totalQty += parseFloat(val.QTY);
					});
					for(var i in this.items){
						this.items[i].INSPECTION_QTY = totalQty;
					}
				}
				
				
				//查询当前登录用户信息
				$.post(baseUrl + "sys/user/info",{},function(resp){
					   this.currentUser = resp.user;
					   this.items[0].QC_PEOPLE = resp.user.staffNumber;
					   this.itemTemplate.QC_PEOPLE = resp.user.staffNumber;
				}.bind(this));
			
				   //查询质检结果配置
			       $.ajax({
					 url:baseUrl + "config/wmscqcresult/list2",
					 type:"post",
					 data:{"werks":this.itemTemplate.WERKS},//TIP: 大写的！！！
					 success:function(resp){
						   this.qcResult = resp.page.list;
						   this.qcResultCodes = resp.page.list;
					 }.bind(this)
			       })
		}.bind(this));
		
	   //查询退货原因信息
	   $.post(baseUrl+"config/wmscqcreturnreasons/list",{},function(resp){
		  this.returnreasons = resp.page.list;
	   }.bind(this));
	   //this.templateItem = this.items[0];
	   
	 
	},
	methods:{
		resultChange:function(event,index_){
			var code = event.target.value;
			//设置质检结果文本
			var text = "";
			this.qcResultCodes.map(function(val){
				if(val.qcResultCode === code){
					text = val.qcResultName;
				}
			});
			this.items[index_].QC_RESULT_TEXT = text;
			
			//不合格类型标识
			var returnTypeFlag = false;
			this.qcResult.map(function(val){
				//属于可退货或者不可进仓
				if(val.qcResultCode === code && val.qcStatus !== '01' && (val.whFlag === '0' || val.returnFlag === 'X') && val.qcStatus !='01'){
					returnTypeFlag = true;
				}
			});
			if(returnTypeFlag){
				//不合格的物料，破坏数量不可编辑，破坏数量设置为0
				
				this.items[index_].destoryVisiable = false;
				this.items[index_].DESTROY_QTY = 0;
				layer.open({
					  type: 1,
					  title:"选择不合格原因",
					  area: ['70%','70%'],
					  closeBtn:1,
					  btn:['确定','取消'],
					  yes:function(index,layero){
						  var reasonText = $("#reasons").find("option:selected").text();
						  vm.items[index_].QC_RESULT = reasonText;
						  Vue.set(vm.items,index_,vm.items[index_])
					  },
					  no:function(){
						  
					  },
					  fixed: false, //不固定
					  maxmin: true,
					  content: $("#returnreason")
				})
			}else{
				//合格物料，不合格原因置空
				this.items[index_].destoryVisiable = true;
				this.items[index_].QC_RESULT = null ;
				this.items[index_].returnreasonCode = null;
				
				Vue.set(vm.items,index_,vm.items[index_]);
			}
		},
		openReturnReason:function(index_){
			var code = this.items[index_].QC_RESULT_CODE;
			//不合格类型标识
			var returnTypeFlag = false;
			this.qcResult.map(function(val){
				//属于可退货或者不可进仓
				if(val.qcResultCode === code && (val.returnFlag === 'X' || val.whFlag === '0') && val.qcStatus != '01'){
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
					  vm.items[index_].QC_RESULT = reasonText ;
					  Vue.set(vm.items,index_,vm.items[index_]);
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
			var newCheckedQty = parseFloat(this.items[nextIndex].QTY) + parseFloat(this.items[index].QTY);
			this.items[nextIndex].QTY = newCheckedQty;
			this.items.splice(index,1);
		},
		add:function(index){
			var tpl = {};
			//对象复制
			$.extend(tpl,this.itemTemplate);
			tpl.returnreason = "";
			tpl.returnreasoncode = "";
			tpl.qcResultCode = "";
			tpl.qcResultText = "";
			//计算质检数量 (新增项的质检数量 = 送检数量 - 已有项的质检数量之和)
			var hasCheckedQty = 0;
			this.items.map(function(val){
				hasCheckedQty += parseFloat(val.QTY);
			})
			var checkedQty = tpl.INSPECTION_QTY - hasCheckedQty;
			if(checkedQty <= 0){
				alert("质检数量之和不能大于送检数量");
				return;
			}
			tpl.QTY = checkedQty;
			
			this.items.splice(index + 1,0,tpl);
		},
		save:function(){
			//质检结果是否为空
			//质检数量之和需小等于送检数量
			var checkedQtyTotal = 0;
			for(var i = 0;i<this.items.length;i++){
				var val = this.items[i];
				if(val.QC_RESULT_CODE === "" || val.QC_RESULT_CODE === null || val.QC_RESULT_CODE === undefined){
					alert("质检结果不能为空");
					return ;
				}
				checkedQtyTotal += parseFloat(val.QTY);
			}
			if(checkedQtyTotal > this.items[0].INSPECTION_QTY){
				alert("质检数量之和不能大于送检数量");
				return ;
			}
			if(this.type === '00'){
				//调用保存-来料质检-未质检
				$.ajax({
					url:baseUrl + "qc/wmsqcinspectionhead/saveStockRejudgeNotInspect",
					type:"post",
					contentType:"application/json",
					data:JSON.stringify(this.items),
					success:function(resp){
						if(resp.code === 0){
							js.showMessage("保存成功");
							localStorage.setItem("refreshPage","true");
							js.closeCurrentTabPage();
						}else{
							alert("保存失败，"+resp.msg);
						}
					}
				});
			}else{
				//质检中，评审中 ...保存逻辑
				//转换为质检结果
				for(var index in this.items){
					this.items[index].RESULT_QTY = this.items[index].QTY;
				}
				//调用保存-来料质检-未质检-单批质检
				$.ajax({
					url:baseUrl + "qc/wmsqcinspectionhead/saveStockRejudgeOnInspect",
					type:"post",
					contentType:"application/json",
					data:JSON.stringify(this.items),
					success:function(resp){
						if(resp.code === 0){
							js.showMessage("保存成功");
							localStorage.setItem("refreshPage","true");//保存后刷新查询页面
							js.closeCurrentTabPage();
						}else{
							alert("保存失败，"+resp.msg);
						}
					}
				});
			}
		}
	}
})