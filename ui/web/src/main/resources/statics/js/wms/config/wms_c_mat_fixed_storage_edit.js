//国际化取值
var smallUtil=new smallTools();
var array = new Array("WERKS_NOT_FOUND","SUCCESS","WH_NOT_FOUND");
var languageObj = smallUtil.loadlanguageByPKeyAndLKey(array,"M");
//验证仓库、工厂等是否存在
var validateFun=new validateFun();

var vm = new Vue({
    el:'#rrapp',
    data:{
    	fixedStorage:{},
    	saveFlag:false,
    	whNumber:"",
    	werks:"",
    	warehourse:[]
    },
    watch:{
		whNumber:{
			handler:function(newVal,oldVal){
				getAreaCodeSelect(vm.whNumber);
			}
		}
	},
    created:function(){
		//初始化查询仓库
		var plantCode = $("#werks").val();
		
        //初始化仓库
        $.ajax({
      	  url:baseUrl + "common/getWhDataByWerks",
      	  data:{
	  		 "WERKS":plantCode,
	  		 "MENU_KEY":"FIXED_STORAGE"
	  	  },
      	  success:function(resp){
      		 vm.warehourse = resp.data;
      		if(resp.data.length>0){
    			 vm.whNumber=resp.data[0].WH_NUMBER//方便 v-mode取值
    		 }
      		
      	  }
        });
	},
    methods:{
    	getInfo:function(id,maktx){
			$.ajax({
	            type: "GET",
	            url: baseURL + "config/fixedStorage/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	vm.fixedStorage = r.fixedStorage;
	            	vm.fixedStorage.maktx = maktx;
	            	getAreaCodeSelect(r.fixedStorage.whNumber,r.fixedStorage.storageAreaCode);
	            }
			});
    	},
    	getPlantNoFuzzy:function(){
			getPlantNoSelect("#werks",null,null);
//			getFactorySelectForRedis("#werks",null,null);
		},
		getWhNoFuzzy:function(){
			getWhNoSelect("#whNumber",null,null);
//			getWhNoSelectForRedis("#whNumber",null,null);
		},
		getMaterialNoFuzzy:function(){
			getMaktxByMatnr("#werks","#matnr","#maktx");
			
		},
		// 仓库代码失去焦点，重新加载存储区代码
		whNumerBlur: function(){
			var whNumber=$("#whNumber").val();
			var storageTypeCode=$("#storageTypeCode").val();
			if(whNumber!=''){
				getAreaCodeSelect(whNumber);
			}
		},
		onPlantChange:function(event){
			//工厂变化的时候，更新仓库号下拉框
	        var plantCode = event.target.value;
	        if(plantCode === null || plantCode === '' || plantCode === undefined){
	        	return;
	        }
	        //查询工厂仓库
	        $.ajax({
	        	url:baseUrl + "common/getWhDataByWerks",
	        	data:{"WERKS":plantCode,"MENU_KEY":"FIXED_STORAGE"},
	        	success:function(resp){
	        		vm.warehourse = resp.data;
	        		if(resp.data.length>0){
	        			vm.whNumber=resp.data[0].WH_NUMBER//方便 v-mode取值
	         		}
	        	}
	          })
		},
    }
});
$("#saveForm").validate({
	rules : {
		
	},
	submitHandler: function(form){
		// VUE绑定读取不到通过模糊查找选中的值，所以重新赋值
		vm.fixedStorage.whNumber=$('#whNumber').val().toUpperCase();
		vm.fixedStorage.werks=$('#werks').val();
		vm.fixedStorage.matnr=$("#matnr").val();
		if(vm.fixedStorage.id == null){
			if (!validateFun.validateWhNumber(vm.fixedStorage.whNumber)){
				layer.msg(languageObj.WH_NOT_FOUND,{time:2000});
				return false;
			}
			if (!validateFun.validateWerks(vm.fixedStorage.werks)){
				layer.msg(languageObj.WERKS_NOT_FOUND,{time:2000});
				return false;
			}
		}

		var url = vm.fixedStorage.id == null ? "config/fixedStorage/save" : "config/fixedStorage/update";
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	        contentType: "application/json",
	        dataType:"json",
	        data:JSON.stringify(vm.fixedStorage),
	        async:false,
	        success: function(data){
	        	if(data.code == 0){
                    js.showMessage(languageObj.SUCCESS);
                    vm.saveFlag= true;
                }else{
                	if (data.msg == "WERKS_NOT_FOUND") {
                		alert(languageObj.WERKS_NOT_FOUND);
                	} else if (data.msg == "WH_NOT_FOUND") {
                		alert(languageObj.WH_NOT_FOUND);
                	} else {
                		layer.msg(data.msg,{time:2000});
                	}
                    
                }
	        }
	    });
    }
});

function getAreaCodeSelect(whNumber,selectVal){
	$("#storageAreaCode").html("");
	$.ajax({
        type: "POST",
        url: baseURL + "config/wharea/queryAll",
        dataType : "json",
        data: {
        	"whNumber":whNumber.toUpperCase(),
        },
        async:false,
        success: function(resp){
        	var data=resp.list;
        	var element=$("#storageAreaCode");
        	element.append("<option value='' storagetypecode=''>请选择</option>");
        	$.each(data,function(index,val){
        		if(selectVal==val.areaCode){
        			element.append("<option value='"+val.storageAreaCode+"'" + ">"+val.areaName+"</option>");
        		}else{
        			element.append("<option value='"+val.storageAreaCode+"'" + ">"+val.areaName+"</option>");
        		}
        	});
        }
    });
}