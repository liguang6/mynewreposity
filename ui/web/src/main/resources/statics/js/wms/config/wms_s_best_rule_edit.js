//国际化取值
var smallUtil=new smallTools();
var array = new Array("DATA_EXISTS","SUCCESS","WH_NOT_FOUND");
var languageObj = smallUtil.loadlanguageByPKeyAndLKey(array,"M");
//验证仓库、工厂等是否存在
var validateFun=new validateFun();

var vm = new Vue({
    el:'#rrapp',
    data:{
    	ruleConfig:{},
    	saveFlag:false
    },
    methods:{
    	getInfo:function(id,maktx){
			$.ajax({
	            type: "GET",
	            url: baseURL + "config/ruleConfig/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	vm.ruleConfig = r.ruleConfig;
	            }
			});
    	},
    	getPlantNoFuzzy:function(){
		},
		getWhNoFuzzy:function(){
			getWhNoSelect("#whNumber",null,null);
		},
    }
});
$("#saveForm").validate({
	rules : {
		
	},
	submitHandler: function(form){
		// VUE绑定读取不到通过模糊查找选中的值，所以重新赋值
//		vm.ruleConfig.werks=$('#werks').val();
		vm.ruleConfig.whNumber=$('#whNumber').val().toUpperCase();
		if (!validateFun.validateWhNumber(vm.ruleConfig.whNumber)){
			layer.msg(languageObj.WH_NOT_FOUND,{time:2000});
			return false;
		}
//		if (!validateFun.validateWerks(vm.ruleConfig.werks)){
//			layer.msg(languageObj.WERKS_NOT_FOUND,{time:2000});
//			return false;
//		}
		var url = vm.ruleConfig.id == null ? "config/ruleConfig/save" : "config/ruleConfig/update";
//		alert(vm.ruleConfig.werks);
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	        contentType: "application/json",
	        dataType:"json",
	        data:JSON.stringify(vm.ruleConfig),
	        async:false,
	        success: function(data){
	        	if(data.code == 0){
                    js.showMessage(languageObj.SUCCESS);
                    vm.saveFlag= true;
                }else{
                	if (data.msg == "WH_NOT_FOUND") {
                		layer.msg(languageObj.WH_NOT_FOUND,{time:2000});
                	} else if (data.msg == "DATA_EXISTS") {
                		layer.msg(languageObj.DATA_EXISTS,{time:2000});
                	} else {
                		layer.msg(data.msg,{time:3000});
                	}
                    
                }
	        }
	    });
    }
});