
var vm = new Vue({
    el:'#rrapp',
    data:{
    	device:{
    		/*werks:'',
    		deviceCode:'',deviceName:'',machineCode:'',machineName:'',
    		specificationModel:'',
    		status:'',memo:''*/
    			},
    	
    	saveFlag:false,
    	id:''
    },
    created: function(){
    	this.device.werks = $("#werks").find("option").first().val();
    	this.device.status = $("#status").find("option").first().val();
	},
    watch:{
    	
    },
    methods:{
    
      getInfo:function(id){
    	  
    	//页面赋值
			$.ajax({
	            type: "GET",
	            url: baseURL + "masterdata/device/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	vm.device = r.device;
	            }
			});
	
		
  		},
  		getDeviceTypeNoFuzzy:function(){
  			
  			//模糊查找设备类型
  			getDeviceTypeNoSelect("#deviceCode","#deviceName",null,fn_getDeviceMachineCode);
  			
  			
		},
		getdeviceCode:function(){//工厂改变则再次生成机台编码
			console.info(">>"+vm.device.deviceCode);
			if(vm.device.deviceCode==''||vm.device.deviceCode==undefined){
  				return false;
  			}
  			
			vm.getDeviceMachineCode();
	          
		},
		getDeviceMachineCode:function(){
			
			//生成机台编码 工厂_设备类型编码_三位流水号
  			//1 判断 工厂_设备类型编码在表中的 machine_code是否存在，
  			//2 不存在，则返回工厂_设备类型编码_000
  			//3 存在，则取出三位流水码，取最大的值加1，返回
			vm.$nextTick(function(){//数据渲染后调用	
  			$.ajax({
	        	  url:baseUrl + "masterdata/device/getMaxMachineCode",
	        	  data:{
	        		  "WERKS_MACHINE": vm.device.werks+"_"+$("#deviceCode").val()
	        	  },
	        	  success:function(resp){
	        		 vm.device.machineCode = resp.result;
	        		 $("#machineCode").val(vm.device.machineCode);
	        		 
	        	  }
	          })
			})
		}
    }
});
$("#saveForm").validate({
	submitHandler: function(form){
		
		$("#werksName").val($("#werks").find("option:selected").text());
		vm.device.werksName=$("#werks").find("option:selected").text();
		
		vm.device.deviceCode=$("#deviceCode").val();
		vm.device.deviceName=$("#deviceName").val();
		
		js.loading("正在保存,请稍候...");
		var url = vm.device.id ==null ? "masterdata/device/save" : "masterdata/device/update";
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	        contentType: "application/json",
	        data: JSON.stringify(vm.device),
	        async:false,
	        success: function(data){
	        	js.closeLoading();
	        	if(data.code == 0){
                    js.showMessage('操作成功');
                    vm.saveFlag= true;
                }else{
                    alert(data.msg);
                }
	        }
	    }); 
//	  }
//  }); 
 }
});

function fn_getDeviceMachineCode(devicecode){
	vm.device.deviceCode=devicecode.DEVICECODE;
	vm.getDeviceMachineCode();
}