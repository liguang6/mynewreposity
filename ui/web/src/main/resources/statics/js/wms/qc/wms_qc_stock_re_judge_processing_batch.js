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
		$.post(baseUrl + "qc/wmsqcinspectionitem/stoke_rejuge_on_inspect/",{"INSPECTION_NO":inspectionNo,"INSPECTION_ITEM_NO":inspectionItemNo,"getAll":"getAll"},function(resp){
				this.itemTemplate = resp.list[0];
				this.items = resp.list;
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
					 data:{"werks":this.itemTemplate.werks},
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
		back:function(){
			window.location.href = baseUrl + "qc/redirect/to_stock_rejudge_on_inspect"; 
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
			this.items[index_].QC_RESULT_TEXT = text;
			
			//不合格类型标识
			var returnTypeFlag = false;
			this.qcResult.map(function(val){
				//属于可退货或者不可进仓
				if(val.qcResultCode === code && (val.returnFlag === 'X' || val.whFlag === '0') && val.qcStatus != '01'){
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
				if(val.qcResultCode === code && val.qcStatus !== '01' && (val.returnFlag === 'X' || val.whFlag === '0' || val.reviewFlag === 'X' || val.goodFlag === 'X')){
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
			var newCheckedQty = parseFloat(this.items[nextIndex].RESULT_QTY) + parseFloat(this.items[index].RESULT_QTY);
			this.items[nextIndex].RESULT_QTY = newCheckedQty;
			this.items.splice(index,1);
		},
		add:function(index){
			var tpl = {};
			//对象复制
			$.extend(tpl,this.itemTemplate);
			tpl.QC_RESULT = "";
			tpl.returnreasoncode = "";
			tpl.QC_RESULT_CODE = "";
			tpl.QC_RESULT_TEXT = "";
			//计算质检数量 (新增项的质检数量 = 送检数量 - 已有项的质检数量之和)
			var hasCheckedQty = 0;
			this.items.map(function(val){
				hasCheckedQty += parseFloat(val.RESULT_QTY);
			})
			var checkedQty = tpl.INSPECTION_QTY - hasCheckedQty;
			if(checkedQty <= 0){
				alert("质检数量之和不能大于送检数量");
				return;
			}
			tpl.RESULT_QTY = checkedQty;
			
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

				//调用保存
				$.ajax({
					url:baseUrl + "qc/wmsqcinspectionhead/saveStockRejudgeOnInspect",
					type:"post",
					contentType:"application/json",
					data:JSON.stringify(this.items),
					success:function(resp){
						if(resp.code === 0){
							js.showMessage("操作成功");
							localStorage.setItem("refreshPage","true");//刷新查询页面，防止重复操作
							js.closeCurrentTabPage();
						}else{
							js.showErrorMessage("保存失败，"+resp.msg);
						}
					}
				});
			
		},
		
	}
})