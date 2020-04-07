var vm = new Vue(
			{
				el : "#vue-app",
				data : {
					items : [{}],
					referOrderLgort : "",
					filterZeroRequireLine : '',
					werks:"",
					whNumber:"",
					resultItems:[],//根据查询条件，的到的生产订单，返回到创建生产订单页面
					type:'1',//查询类型  1.生产订单 2.SAP采购订单 ...
					vendor:'',//委外单位
					acceptWerks:'',//接收工厂 从父页面传入
					deliveryType:"",//配送模式从父页面传入
					lgortList:""//发出库位
				},
				
				created:function(){
					$(function(){
						//绑定粘贴事件,把粘贴数据放到HTML
						$("body").bind("paste",function(e){
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
							//转换成Vue的数据对象
							var items  = vm.convertToTable(tdList);
				
							//如果是指定输入框，只粘贴数据到当前输入框。 
							if("INPUT" === e.target.tagName){
								e.target.value = tdList[0][0];
								//触发组件的input事件，Vue根据input事件来改变数据
								vm.simulateEvent(e.target,"input");
								return true;
							}
							vm.items = items;
						});
						
					});
				},
				methods : {
					simulateEvent:function(dom,event){
						//使用JS来触发事件
						var e = document.createEvent("Event");
						e.initEvent(event,true,true);
						dom.dispatchEvent(e);
					},
					add : function() {
						//新增一个元素
						this.items.splice(this.items.length, 0, {});
					},
					remove : function(index) {
						if (this.items.length <= 1)
							return false;
						this.items.splice(index, 1);
					},
					submit : function(callback,_url) {
						var url = baseUrl+ "out/createRequirement/processProduceOrder";
						if(_url != undefined && _url != null && _url != ''){
							url = _url;
						}
						//入参校验
						for(var i=0;i<vm.items.length;i++){
							if(vm.type === '1'){
								if(vm.items[i].orderNo === undefined){
									alert("生产订单号不能为空")
									return;
								}
							}
							if(vm.type === '2'){
								if(vm.items[i].EBELN === undefined){
									alert("SAP采购订单号不能为空")
									return;
								}
							}
							if(vm.type === '6'){
								if(vm.items[i].distribution === undefined){
									alert("配送单号不能为空")
									return;
								}
							}
						}
						
						//公共字段
						if(this.type === '3'){
							for(var idx in vm.items){
								vm.items[idx].werks = vm.werks;
							}
						}
						
						if(this.type === '4'){
							//如果是UB转储业务类型，入参加入接收工厂
							for(var index in this.items){
								this.items[index].acceptWerks = this.acceptWerks;
							}
						}
						if(this.type === '5'){
							for(var index in this.items){
								this.items[index].acceptWerks = this.acceptWerks;
								this.items[index].werks = this.werks;
							}
						}
						
						if(this.type === '6'){
							for(var index in this.items){
								this.items[index].deliveryType = this.deliveryType;
							}
						}
						
						$.ajax({
									url : url,
									contentType : "application/json",
									data : JSON.stringify(vm.items),
									type : "post",
									success : function(resp) {
										if (resp.code !== 0) {
											callback(false, resp.msg);
										} else {
											callback(true, resp.msg);
										}
									}
								})
					},
					getData : function() {
						return {
							'items' : vm.items,
							'referOrderLgort' : $("#referOrderLgort").val(),
							'filterZeroRequireLine' : $("#filterZeroRequireLine").val()
						};
					},
					close : function() {
						close();
					},
					convertToTable : function(items) {
						//粘贴的数组，转换为对象
						var excelDatas = [];
						if(this.type === '1'){
							for(var index in items){
								excelDatas[index] = {};
								excelDatas[index].orderNo = items[index][0];
								excelDatas[index].requireSuitQty = items[index][1];
								excelDatas[index].mat = items[index][2];
								excelDatas[index].qty = items[index][3];
								excelDatas[index].location = items[index][4];
								excelDatas[index].station = items[index][5];
							}
						}
						if(this.type === '2'){
							for(var index in items){
								excelDatas[index] = {};
								excelDatas[index].EBELN = items[index][0];
								excelDatas[index].EBELP = items[index][1];
								excelDatas[index].qty = items[index][2];
							}
						}
						if(this.type === '3'){
							for(var index in items){
								excelDatas[index] = {};
								excelDatas[index].deliveryNO = items[index][0];
							}
						}
						
						if(this.type === '4'){
							for(var index in items){
								excelDatas[index] = {};
								excelDatas[index].EBELN = items[index][0];
								excelDatas[index].MATNR = items[index][1];
								excelDatas[index].qty = items[index][2];
							}
						}
						return excelDatas
					},
					init:function(werks,whNumber,type,acceptWerks,deliveryType,lgortList){
						//初始化，传递参数
						this.werks = werks;
						this.whNumber = whNumber;
						this.type = type;
						this.acceptWerks = acceptWerks;
						this.deliveryType = deliveryType;
						this.lgortList = lgortList;
					},
					query : function(callback,queryurl){
						var postObj;
						var items = this.items;
						var werks = this.werks;
						var vendor = this.vendor;
						var acceptWerks = this.acceptWerks;
						var whNumber = this.whNumber;
	                    //查询生产订单
						if(this.type === '1'){
							postObj = {
									"referOrderLgort" : $("#referOrderLgort").val(),
									"filterZeroRequireLine" : $("#filterZeroRequireLine").val(),
									"items" : vm.items,
									"werks" : vm.werks,
									"whNumber" : vm.whNumber
							};
						}else if(this.type === '2'){
							//填充，工厂和供应商信息
							for(var i=0;i<items.length;i++){
								items[i].WERKS = werks;
								items[i].LIFNR = vendor;
							}
							postObj = items;
						}else if(this.type === '3'){
							//SAP交货单类型
							for(var i=0;i<items.length;i++){
								items[i].lgortList = this.lgortList;
							}
							postObj = items;
						}else if(this.type === '4' || this.type === '5'){
							//填充接收工厂,工厂，仓库
							for(var i=0;i<items.length;i++){
								items[i].acceptWerks = acceptWerks;
								items[i].werks = werks;
								items[i].whNumber = whNumber;
							}
							postObj = items;
						}else if(this.type === '6'){
							for(var i=0;i<items.length;i++){
								items[i].deliveryType = this.deliveryType;
							}
							postObj = items;
						}
	                    var url = baseUrl + "out/createRequirement/producerOrders";
	                    if(queryurl != undefined && queryurl !== null && queryurl !== ''){
	                    	//如果传入了服务端API,使用从父页面传入的.
	                    	url = queryurl;
	                    }
	                    
						$.ajax({
							url : url,
							data : JSON.stringify(postObj),
							contentType : "application/json",
							type : "post",
							success : function(response) {
								if(response.code === 0){
									vm.resultItems = response.data;
									//回调
									callback(vm.resultItems);
								}
								else {
									alert(response.msg);
								}
							}
						});
					}
				}
			})