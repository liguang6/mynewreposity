/**
 * 车型工序细节
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

var vm = new Vue({
	el : "#vue-app",
	data : {
		processList:[{"sortNo":1,"processName":"工序名","monitoryPointFlag":"1","planNodeName":"计划节点名称"}],
		factoryName:"xx工厂",
		workshopName:"xx车间",
		lineName:"xx",
		vehicleType:"xx车辆类型",
		busTypeName:"xx车型名称",
		deptId:"",
		busTypeId:"",
		busTypeCode:"",
		vehicleTypeId:""
	},
	methods : {
		close:function(){
			var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
			parent.layer.close(index); //再执行关闭 
		}
	},
	created:function(){
		var deptId = $("#deptId").val();
		var busTypeCode = $("#busTypeCode").val();
		var vehicleType = $("#vehicleType").val();
		
		//加载数据
		$.ajax({
			url: baseURL + "setting/settingprocessflow/info/"+deptId+"/"+busTypeCode+"/"+vehicleType+"/",
			success:function(data){
				//processFlow 根据 sortNo 排序
				this.processList = data.processFlow.sort(compare('sortNo'));
				//设置车型工序其他信息
				this.factoryName=data.processFlow[0].factoryName;
				this.workshopName=data.processFlow[0].workshopName;
				this.lineName=data.processFlow[0].lineName;
				this.vehicleType=data.processFlow[0].vehicleType;
				this.deptId=data.processFlow[0].deptId;
				this.busTypeId=data.processFlow[0].busTypeId;
				this.busTypeCode=data.processFlow[0].busTypeCode;
				this.vehicleTypeId=data.processFlow[0].vehicleTypeId;
			}.bind(this)
		})
	}
})