var vm ;
//-- menu
var setting = {
	view : {
		dblClickExpand : false
	},
	data : {
		simpleData : {
			enable : true,
			idKey : "deptId",
			pIdKey : "parentId",
			rootPId : "0"
		}
	},
	callback : {
		beforeClick : beforeClick,
		onClick : onClick
	}
};

var zNodes = [];

function beforeClick(treeId, treeNode) {

	var check = (treeNode && !treeNode.isParent); if (!check)
	alert("只能选择生产线..."); return check;
	 
	return true;
}

function onClick(e, treeId, treeNode) {
	vm.onClickDeptMenu();
	
}

function showMenu() {
	var cityObj = $("#deptSel");
	var cityOffset = $("#deptSel").offset();
	$("#menuContent").css({
		left : cityOffset.left + "px",
		top : cityOffset.top + cityObj.outerHeight() + "px"
	}).slideDown("fast");

	$("body").bind("mousedown", onBodyDown);
}
function hideMenu() {
	$("#menuContent").fadeOut("fast");
	$("body").unbind("mousedown", onBodyDown);
}
function onBodyDown(event) {
	if (!(event.target.id == "menuBtn" || event.target.id == "menuContent" || $(
			event.target).parents("#menuContent").length > 0)) {
		hideMenu();
	}
}

/**
 * 新增或者编辑车型工序
 */
var compare = function (prop) {
    return function (obj1, obj2) {
        var val1 = obj1[prop];
        var val2 = obj2[prop];if (val1 < val2) {
            return -1;
        } else if (val1 > val2) {
            return 1;
        } else {
            return 0;
        }            
    } 
}

var opindex;
//VueJS code
vm = new Vue({
	el : "#vue-app",
	data : {
		deptId:"",
		factoryName:"",
		workshopName:"",
		busTypeName:"",
		lineName:"",
		vehicleType:"",
		vehicleTypeId:"",
		busTypeCode:"",
		busTypeId:"",
		processFlows:[],
		processList:[],
		vehicleTypeDict:[],
		busTypeDict:[],
		url:baseURL + 'setting/settingprocessflow/save',
		pos:0
	},
	watch : {
		processFlows : function(){
			this.pos = $("html").scrollTop();//记录滚动条垂直位置
		}
	},
	updated:function(){
		var p = this.pos;
		setTimeout(function(){$("html").scrollTop(p)},60);//设置滚动条位置
	},
	methods : {
		getProcess:function(event,index,a){
			//alert(event.target.id);
			opindex = index;
			getProcessNoSelect('.process',event.target,a);
		},

		checkSeactionOverlapping:function(){
			/*
			          检查工段不能交叉 
			   1.加一个栈
               2.遍历所有工序，如果工段名字与栈顶元素不同，入栈
               3.判断栈内元素是否有重复，有重复则工段交叉，没有重复则工段没有交叉
			*/
			var sectionStack = new Array();
			for(var index in this.processFlows){//index 从1开始
				var topElement = sectionStack[sectionStack.length - 1];
				if(topElement !== this.processFlows[index].sectionName){
					sectionStack.push(this.processFlows[index].sectionName);
				}
			}
			//检查重复
			Array.sort(sectionStack);
			for(var i in sectionStack){
				var current = parseInt(i)-1;
				//存在重复
				if(i < sectionStack.length && sectionStack[current] === sectionStack[i]){
					return true;
				}
			}
			return false;
		},
		insert:function(sortNo){
			if(vm.lineName==''){
				layer.msg('请先选择线别');
				return false;
			}
			//insert 一个元素
			this.processFlows.splice(sortNo+1,0,{"sortNo":sortNo+1});
			this.refreshSort();
		},
		remove:function(sortNo){
			this.pos = window.scrollY;
			if(this.processFlows.length <= 1)
				return;
			//记录当前滚动条位置
			
			//删除一个元素
			this.processFlows.splice(sortNo,1);
			this.refreshSort();
		},
		refreshSort:function(){
			//重新计算sortNo
			this.processFlows = this.processFlows.map(function(val,index,arr){
				val.sortNo = index;
				return val;
			});
		},
		moveUp:function(sortNo){
			if(sortNo<=0)
				return;
			this.processFlows[sortNo].sortNo -=1;
			this.processFlows[sortNo - 1].sortNo +=1;
			//对工序进行排序,上移下移 插入 需要更新工序
			this.processFlows = this.processFlows.sort(compare('sortNo'));
			if(this.checkSeactionOverlapping()){
				alert("工序不能交叉");
				//上移还原
				if(sortNo<=0)
					return;
				this.processFlows[sortNo].sortNo -=1;
				this.processFlows[sortNo - 1].sortNo +=1;
				//对工序进行排序,上移下移 插入 需要更新工序
				this.processFlows = this.processFlows.sort(compare('sortNo'));
			}
		},
		moveDowm:function(sortNo){
			//最后一个元素不能下移
			if(sortNo >= (this.processFlows.length -1))
				return;
			this.processFlows[sortNo].sortNo +=1;
			this.processFlows[sortNo+1].sortNo -=1;
			//对工序进行排序,上移下移 插入 需要更新工序
			this.processFlows = this.processFlows.sort(compare('sortNo'));
			
			if(this.checkSeactionOverlapping()){
				alert("工序不能交叉");
				//下移还原
				if(sortNo >= (this.processFlows.length -1))
					return;
				this.processFlows[sortNo].sortNo +=1;
				this.processFlows[sortNo+1].sortNo -=1;
				//对工序进行排序,上移下移 插入 需要更新工序
				this.processFlows = this.processFlows.sort(compare('sortNo'));
			}
		},
		saveOrUpdate:function(event){
			event.preventDefault();
			
			
			//根据车型Code获取车型ID
			var busTypeCode = this.busTypeCode;
			var busTypeDict =  this.busTypeDict.filter(function(value){
				return value.busTypeCode === busTypeCode;
			});
			if(busTypeDict.length >=1)
			    this.busTypeId = busTypeDict[0].id;
			
			//获取车辆类型名字
			var vehicleType = this.vehicleType;
			var vehicleTypeDict = this.vehicleTypeDict.filter(function(value){
				return value.value === vehicleType;
			});
			if(vehicleTypeDict.length >= 1)
			    this.vehicleTypeId = vehicleTypeDict[0].id;
			if( vm.factoryName==''){
                 alert('请选择工厂、车间、线别信息');
                 return false;
            }
            if( vm.busTypeCode==''){
                 alert('请选择车型');
                 return false;
            }
            if( vm.vehicleType==''){
                 alert('请选择车辆类型');
                 return false;
            }
            
			var processFlows =  this.processFlows.map(function(value,index,arr){
				value.deptId = vm.deptId;
                value.factoryName= vm.factoryName;
                value.workshopName= vm.workshopName;
                value.lineName= vm.lineName;
				value.vehicleType = vm.vehicleType;
				value.vehicleTypeId = vm.vehicleTypeId;
				value.busTypeCode = vm.busTypeCode;
				value.busTypeId = vm.busTypeId;
				return value;
			});
		
			$.ajax({
				url:this.url,
				contentType: "application/json;charset=UTF-8",
				type:"POST",
				dataType:"json",
				data:JSON.stringify(processFlows),
				success:function(response){
					if(response.code === 0){
						this.close();//保存成功直接关闭
					}else{
						alert("保存失败,"+response.msg)
					}
				}.bind(this)
			});
		},
		
		updateProcessFlow(sortNo,id,processCode){
			if(id === undefined) return;
			//检查工序是否重复
			var existedProcessFlows = this.processFlows.filter(function(val,index,arr){
				return val.processCode === processCode;
			});
			
			
			
			if(existedProcessFlows.length >=2){
				//长度要判断>=2 其中有一个是当前选择的工序, Vue 双向绑定
			    alert("工序已经存在！");	
			    //重新初始化
			    Vue.set(vm.processFlows, sortNo, {sortNo:sortNo,processCode:""});
			    return ;
			}
			
			var p = this.processList.filter(function(val,index,arr){
				return val.id === id;
			});			
			if(p.length !== 1)
				return;
			//根据选择的工序更新 车型工序
		    var	newValue = {};
		    newValue.sortNo = sortNo;
		    newValue.processName = p[0].processName;
		    newValue.planNodeName = p[0].planNodeName;
		    newValue.planNodeCode = p[0].planNodeCode;//add
		    newValue.sectionName = p[0].sectionName;
		    newValue.monitoryPointFlag = p[0].monitoryPointFlag;
		    newValue.processCode = p[0].processCode;
		    newValue.processId = p[0].id;
		    //更新车型工序 & 更新界面
			Vue.set(vm.processFlows, sortNo, newValue);
			//检查工序交叉
			if(this.checkSeactionOverlapping()){
				 alert("工序不能交叉");
				 Vue.set(vm.processFlows, sortNo, {sortNo:sortNo,processId:""});
			}
		},
		showDeptMenu:function(){
			showMenu();
		},
		onClickDeptMenu:function(){
			var zTree = $.fn.zTree.getZTreeObj("treeDemo"),
			nodes = zTree.getSelectedNodes(),
			v = "";
			nodes.sort(function compare(a,b){return a.id-b.id;});
			for (var i=0, l=nodes.length; i<l; i++) {
				v += nodes[i].name + ",";
			}
			if (v.length > 0 ) v = v.substring(0, v.length-1);
			this.lineName = v;
			//线别Id
			this.deptId = nodes[0].deptId;
			//车间名
			var workShopDeptId = nodes[0].parentId;
			var workShopNode = zTree.getNodeByParam("deptId",workShopDeptId,null);
			this.workshopName = workShopNode.name;
			var factoryDeptId = workShopNode.parentId;
			var factoryNode = zTree.getNodeByParam("deptId",factoryDeptId,null);
			this.factoryName = factoryNode.name;
			hideMenu();
			//更新工序信息 ,根据线别
			$.ajax({
				url:baseURL+"setting/settingprocess/list",
				data:{
					pageSize:10000,
		    		pageNo:1,
		    		orderBy:"",
		    		lineName:this.lineName,
		    		factoryName:this.factoryName,
		    		workshopName:this.workshopName
				},
				success:function(data){
					this.processList = data.page.list;
				}.bind(this)
			});
		},
		close:function(){
			//假设这是iframe页
			var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
			parent.layer.close(index); //再执行关闭 
		}
	},
	created:function(){
		
		var deptId = $("#deptId").val();
		var busTypeCode = $("#busTypeCode").val();
		var vehicleType = $("#vehicleType").val();
		if(deptId !== '' && busTypeCode!='' && vehicleType !=''){
			this.url=baseURL + 'setting/settingprocessflow/update';
			//加载车型工序信息
			$.ajax({
				url: baseURL + "setting/settingprocessflow/info/"+deptId+"/"+busTypeCode+"/"+vehicleType+"/",
				data:{
					
				},
				success:function(data){
					//工序信息
					this.processFlows = data.processFlow;
					//车型工序信息
					this.deptId = data.processFlow[0].deptId;
					this.factoryName= data.processFlow[0].factoryName;
					this.workshopName = data.processFlow[0].workshopName;
					this.lineName = data.processFlow[0].lineName;
					this.vehicleType = data.processFlow[0].vehicleType;
					this.vehicleTypeId = data.processFlow[0].vehicleTypeId;
					this.busTypeCode = data.processFlow[0].busTypeCode;
					this.busTypeId = data.processFlow[0].busTypeId;
				}.bind(this)//bind(this) 是必须要加的, this代表当前vue对象
			});
		}else{
			this.processFlows = [{"sortNo":0}];
			//加载树信息,线别只有新增才能改变.
			$.ajax({
		    	url:baseURL + "masterdata/dept/ztreeDepts",
		    	success: function(data){
		    		zNodes = data;
		    		$.fn.zTree.init($("#treeDemo"), setting, zNodes);
		    	}
		    })
		}
		
		
		//设置工序信息
		$.ajax({
			url:baseURL+"setting/settingprocess/list",
			data:{
				pageSize:10000,
	    		pageNo:1,
	    		orderBy:"",
	    		lineName:this.lineName
			},
			success:function(data){
				this.processList = data.page.list;
			}.bind(this)
		});
		
	    //加载车辆类型字典
	    $.ajax({
	    	url:baseURL + "masterdata/dict/list",
	    	data:{
	    		pageSize:10000,
	    		pageNo:1,
	    		orderBy:"",
	    		type:"vehicle_type"
	    	},
	    	success:function(data){
	    		this.vehicleTypeDict = data.page.list;
	    	}.bind(this)
	    });
	    
	    //加载车型信息
	    $.ajax({
	    	url:baseURL + "setting/settingbustype/list",
	    	data:{
	    		pageSize:10000,
	    		pageNo:1,
	    		orderBy:"",
	    	},
	    	success:function(data){
	    		this.busTypeDict = data.page.list;
	    	}.bind(this)
	    });
	    
	}
})
function callback(event){
	//alert(event.process_name);
	//$(event).parent().next().text($(event).attr("process_name"));
}
/*
 * 工序模糊查询 submitId： 用于提交的元素的id
 */
function getProcessNoSelect(elementId,submitId, fn_backcall) {

	var processentity={};
	var processlist;
	$(elementId).typeahead({		
		source : function(input, process) {
			var factory=$('#factoryName').html()!=undefined ? trim($('#factoryName').html()) : '';
			var lineName=vm.lineName;
			var data={
					"process":input,
					"factory_name":factory,
					"line_name":$('#deptSel').val()
			};
			$.ajax({
				url:baseURL+"setting/settingprocess/getProcessList",
				dataType : "json",
				type : "post",
				data : data,
				async: false,
				success: function (response) { 
					processlist = response.data;
					
					var results = new Array();
					$.each(processlist, function(index, value) {
						results.push(value.process_code+" "+value.process_name);
					})
					return process(results);
				}
			});
		},
		items : 20,
		highlighter : function(item) {
			var process_name = "";
			var process_code = "";
			$.each(processlist, function(index, value) {
				if (value.process_code == item.split(" ")[0] &&
						value.process_name == item.split(" ")[1]) {
					process_name = value.process_name;
					process_code = value.process_code;
				}
			})
			return process_code + "  " + process_name;
		},
		matcher : function(item) {
			return true;
		},
		updater : function(item) {
			return item;
		},
		afterSelect:function(item){
			$.each(processlist, function(index, value) {
				if (value.process_code == item.split(" ")[0] &&
						value.process_name == item.split(" ")[1]) {
					processentity=value;
					
			}
			})
			if (typeof (fn_backcall) == "function") {
				processentity.index_ = opindex;
				fn_backcall(processentity);
			}
		}
	});
}
function trim(str){ 
	return str.replace(/(^\s*)|(\s*$)/g, ""); 
}