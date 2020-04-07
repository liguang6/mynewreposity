
$(function(){
	$("[data-toggle='tooltip']").tooltip();
})
var vm = new Vue({
	el : "#vue-app",
	data : {
		receipt : {},
		items : [ {} ],
		returnreasons : [],
		qcResult : [],
		reasonCode:"01",//不合格原因
		notDisplayItems : [],//不显示的，保存时合并。
		plant:{},
		costcenter:"",
		costcenterValid:false,
		costcenterName:"",
	},
	created : function() {
		// 初始化参数
		var id = $("#receiptId").val();
		$.post(baseUrl + "in/wmsinreceipt/info/" + id, {}, function(resp) {
			vm.receipt = resp.wmsInReceipt;
			vm.queryPlant();

			//可改判数量  =  可退货数量 + 可进仓数量
			vm.receipt.updateable = 0;
			if(vm.receipt.inableQty !== null && vm.receipt.inableQty != undefined){
				vm.receipt.updateable += parseFloat(vm.receipt.inableQty);
			}
			if(vm.receipt.returnableQty !== null && vm.receipt.returnableQty !== undefined){
				vm.receipt.updateable += parseFloat(vm.receipt.returnableQty);
			}
				//查询质检重判信息
				$.post(baseUrl + "qc/wmsqcresult/rejudgeitems", {
					"receiptNo" : vm.receipt.receiptNo,
					"receiptItemNo" : vm.receipt.receiptItemNo
				}, function(resp) {
					var items = resp.list;
					var displayItems = [];
					var unDisplayItems = [];
					
					
					//查询质检结果配置
					$.ajax({
						url : baseUrl + "config/wmscqcresult/list2",
						type : "post",
						data : {
							"werks" : vm.receipt.werks
						},
						success : function(resp) {
							vm.qcResult = resp.page.list;							
							//过滤操作，把不属于 可退货类型 和 可进仓类型的数据过滤掉。
							for(var i=0;i<items.length;i++){
								var results = vm.qcResult.filter(function(val){
									if(items[i].qcResultCode === val.qcResultCode){
										return true;
									}
								});
								var result = results[0];
								if((result != undefined &&  result.returnFlag !== undefined) && (result.returnFlag === 'X' || result.whFlag === 'X')){
									displayItems.push(items[i]);
									if(result.whFlag === 'X'){
										items[i].destoryEditable = true;
									}
								}else{
									unDisplayItems.push(items[i]);
								}
							}
							vm.items = displayItems;
							vm.notDisplayItems = unDisplayItems;
							for ( var i in vm.items) {
								vm.items[i].updateable = vm.receipt.updateable;
								vm.items[i].lastDestroyQty = vm.items[i].destroyQty;
							}
						}.bind(this)
					})
				});
		}.bind(this));

		//查询退货原因信息
		$.post(baseUrl + "config/wmscqcreturnreasons/list", {}, function(resp) {
			this.returnreasons = resp.page.list;
		}.bind(this));
		this.templateItem = this.items[0];

	},
	methods : {
		queryPlant:function(){
			   //查询工厂信息
			   $.post(baseUrl + "config/sapPlant/query?WERKS=" + vm.receipt.werks,function(resp){
				   if(resp.list === undefined ||  resp.list.length === 0){
					   js.showMessage("工厂信息不存在")
				   }else{
					   vm.plant = resp.list[0];
				   }
			   });
		},
		add : function(index) {
			var tpl = {};
			// 对象复制
			$.extend(tpl, this.items[0]);
			tpl.qcResultCode = "02";
			tpl.qcResult = "";
			tpl.destroyQty = 0;
			// 已改判数量
			var hasQty = 0;
			this.items.map(function(val) {
				hasQty += parseFloat(val.resultQty);
			})
			var leftNumber = tpl.updateable - hasQty;
			if (leftNumber <= 0) {
				js.showMessage("改判之和不能大于可改判数量");
				return;
			}
			tpl.resultQty = leftNumber;
			this.items.splice(index + 1, 0, tpl);
		},
		remove : function(index) {
			if (this.items.length <= 1) {//至少有一行数据
				return;
			}
			// 更新前一条质检记录的质检数量 新质检数量 = 质检数量 + 删除项的质检数量
			// 前一项：如果删除的项是第一项则前一项为第二项
			var nextIndex = index - 1;
			if (index === 0)
				nextIndex = 1;
			var totalQty = parseFloat(this.items[nextIndex].resultQty) + parseFloat(this.items[index].resultQty);
			this.items[nextIndex].resultQty = totalQty;
			this.items.splice(index, 1);
		},
		openReturnReason:function(index_){
			var code = this.items[index_].qcResultCode;
			//不合格类型标识
			var returnTypeFlag = false;
			this.qcResult.map(function(val){
				if(val.qcResultCode === code){
					//根据品质判定代码，设置品质判定文本。
					vm.items[index_].qcResultText = val.qcResultName;
				}
				//属于可退货或者不可进仓
				if(val.qcResultCode === code && val.qcStatus !== '01' && (val.whFlag === '0' || val.returnFlag === 'X')){
					returnTypeFlag = true;
				}
			});
			if(returnTypeFlag === false){
				//不是退货类型，不需要弹窗 & 清空不合格原因
				vm.items[index_].qcResult = null ;
				vm.items[index_].destoryEditable = true;
				return;
			}
			vm.items[index_].destoryEditable = false;//破坏类型不能编辑
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
					  vm.items[index_].qcResult = reasonText ;
					  vm.items[index_].returnreasoncode = reasonCode;
				  }.bind(this),
				  no:function(){
					  //取消
				  },
				  fixed: false, //不固定
				  maxmin: true,
				  content: $("#returnreason")
			});
		},
		//测试是否是不合格类型
		isBadType : function(code){
			var returnTypeFlag = false;
			this.qcResult.map(function(val){
				//属于可退货或者不可进仓
				if(val.qcResultCode === code && val.qcStatus !== '01' && (val.whFlag === '0' || val.returnFlag === 'X')){
					returnTypeFlag = true;
				}
			});
			return returnTypeFlag;
		},
		save:function(){
			//校验页面的改判数量，是否大于可改判数量
			var t = 0;
			var u = this.receipt.updateable;
			for(var index in this.items){
				t += parseInt(this.items[index].resultQty);
			}
			if(t != u){
				js.showMessage("改判数量之("+t+")和不等于可改判数量("+u+")");
				return 
			}
			//2 校验合格数量必须大于试装数量加破坏数量
			var destroyQty = this.receipt.destroyQty;//破坏数量
			if(destroyQty == null || destroyQty == undefined || destroyQty ==''){
				destroyQty = 0;
			}
			var tryQty = this.receipt.tryQty;//试装数量
			tryQty = tryQty == undefined && tryQty == null?0:tryQty;
			var totalQualifiedQty = 0;//总合格数量
			for(var i in this.items){
				var val = this.items[i];
				//是否是属于合格的质检结果
				var whTypeFlag = false;
				vm.qcResult.map(function(val_){
					//遍历质检结果配置。    
					if(val_.qcResultCode === val.qcResultCode && val_.returnFlag === '0' && val_.whFlag === 'X' && val_.qcStatus == '02'){
						whTypeFlag = true;
					}
				});
				
				if(whTypeFlag){
					totalQualifiedQty += parseFloat(this.items[i].resultQty);
				}
			}
			if(parseFloat(destroyQty)+parseFloat(tryQty) > totalQualifiedQty){
				js.showMessage("合格数量("+totalQualifiedQty+")必须大于试装数量("+tryQty+")加破坏数量("+destroyQty+")");
				return;
			}
			//1 校验成本中心
			if(this.costcenterValid === false){
				var desItems = vm.items.filter(function(val){
					//返回存在破坏数量的项目
					return (val.destroyQty !== undefined && val.destroyQty !== '' && val.destroyQty !== null && val.destroyQty !== 0);
				});
				if(desItems.length > 0){
					js.showMessage("成本中心为空，或不正确");
					return ;
				}
			}
			//保存
			var items = vm.items.concat(vm.notDisplayItems);			
			//设置成本中心
			for(index in vm.items){
				vm.items[index].costcenter = vm.costcenter;
			}
			//调用保存单批质检结果方法
			$.ajax({
				url:baseUrl + "qc/wmsqcinspectionhead/hasInspectedRejudgeSave",
				type:"post",
				contentType:"application/json",
				data:JSON.stringify(vm.items),
				success:function(resp){
					if(resp.code === 0){
						js.showMessage("保存成功");
						js.closeCurrentTabPage();
					}else{
						alset("保存失败，"+resp.msg);
						close();
					}
				}
			})
		},
		checkCostCenter:function(){
			var costcenter = this.costcenter;
			var werks = this.receipt.werks;
			if(costcenter.trim() ==""){
				return false;
			}
			$.ajax({
				url:baseUrl + "common/sapCostcenter/"+costcenter,
				success:function(resp){
					if(resp.COSTCENTER === ''){
						js.showMessage(costcenter+"成本中心不存在")
						vm.costcenterValid = false;
						vm.costcenterName = "";
					}
					else if(resp.COMP_CODE !== vm.plant.bukrs){
						js.showMessage(costcenter+"成本中心不属于"+vm.plant.bukrs+"公司");
						vm.costcenterValid = false;
						vm.costcenterName = "";
					}else {
						vm.costcenterValid = true;
						vm.costcenterName = resp.DESCRIPT;
					}
				}
			})
		
		},
		returnAndInBoundAlertWord : function(record) {
			//生成进仓和退货的提示语句
			var qcResultCode = record.qcResultCode;
			var hasOutReturn = record.hasOutReturn;
			var hasInInbound = record.hasInInbound;
			if(this.isBadType(qcResultCode)){
				if(hasOutReturn == true)
					return "已经创建了退货单，不允许改判";
			}else {
				if(hasInInbound == true){
					return "已经创建了进仓单，不允许改判";
				}
			}
			return "";
		}
	}
});