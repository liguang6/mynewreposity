var vm = new Vue({
	el:'#rrapp',
	data:{
		q:{
		},
		settingBusType:{brand:'比亚迪',manufacturer:'比亚迪汽车工业有限公司',powerType:'纯电动'},
		title: '新增'
	},
	mounted: function(){
    	this.getSettingbustype();
    },
	methods: {
		getSettingbustype: function () {
        	if($("#busTypeId").val() != null && $("#busTypeId").val()!=''){
        		this.title = '修改';
	            $.getJSON(baseURL + "masterdata/bustype/info/"+ $("#busTypeId").val(), function (r) {
	                vm.settingBusType = r.settingBusType;
	            });
        	}
        },
		saveOrUpdate: function (event) {
			var rtn = false;
			var url = vm.settingBusType.id == null ? "masterdata/bustype/save" : "masterdata/bustype/update";
			console.info(";;"+vm.settingBusType.busTypeCode);
			if(vm.settingBusType.busTypeCode==''||vm.settingBusType.busTypeCode==undefined){
				js.alert("车型代码不能为空！");
				return false;
			}
			if(vm.settingBusType.internalName==''||vm.settingBusType.internalName==undefined){
				js.alert("内部名称（车系）不能为空！");
				return false;
			}
			if(vm.settingBusType.vehicleType==''||vm.settingBusType.vehicleType==undefined){
				js.alert("车辆类型不能为空！");
				return false;
			}
			$.ajax({
				type: "POST",
			    url: baseURL + url,
                contentType: "application/json",
                async:false,
			    data: JSON.stringify(vm.settingBusType),
			    success: function(r){
			    	if(r.code === 0){
			    		rtn = true;
			    		js.showMessage('操作成功！');
					}else{
						alert(r.msg);
					}
				}
			});
			return rtn;
		},
    	reset:function(){
    		vm.settingBusType ={brand:'比亚迪',manufacturer:'比亚迪汽车工业有限公司',powerType:'纯电动'}
    	}
	}
});