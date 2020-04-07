var vm = new Vue({
	el:'#rrapp',
    data:{
    	cplant:{
    		"vendorFlag":"0","igFlag":"0","wmsFlag":"0","hxFlag":"0",
    		"packageFlag":"0","pdaPickFlag":"0","barcodeFlag":"0","resbdFlag":"0",
    		"cmmsFlag":"0","matManagerFlag":"0","prfrqflag":"0","freezepostsapflag":"0"
    	},
    	saveFlag:false
    },
    methods:{
    	getInfo:function(id){
    		
			$.ajax({
	            type: "GET",
	            url: baseURL + "config/CPlant/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	vm.cplant = r.cplant;
	            	$("#werks").attr("readonly",true);
	            	$("#whNumber").attr("readonly",true);
	            }
			});
    	},
    	getPlantNoFuzzy:function(){
			getPlantNoSelect("#werks",null,function(){vm.cplant.werks=$("#werks").val();});
		},
		getWerksName:function(){
			mapcond={werks:$("#werks").val()};
			$.ajax({
		        url: baseURL + "config/CPlant/listPlantName",
		        dataType : "json",
				type : "post",
		        data: mapcond,
		        async:false,
		        success: function(data){
		        	if(data.code == 0){
		        		vm.cplant.werksName=data.werksName;
		        		$("#werksName").val(data.werksName);
		        	}
		        }
			})
		}

    }
});

$("#saveForm").validate({
	submitHandler: function(form){	
		vm.cplant.werks=$("#werks").val();
		//判断 仓库号  是否存在，不存在才可以添加
		var mapcond={werks:vm.cplant.werks,wh_Number:vm.cplant.whNumber};
		$.ajax({
	        url: baseURL + "config/CPlant/listEntity",
	        dataType : "json",
			type : "post",
	        data: mapcond,
	        async:false,
	        success: function(data){
	        	if(data.code == 0){
	                   
	                	   if(data.onlyOne=='true'&&vm.cplant.id==null){//如果有记录并且是插入，则报错
	                		   alert("该仓库号已经存在！不能再添加!");
	                		   return false;
	                	   }
	                	   
	                	   if(vm.cplant.id!=null){//如果有记录并且是修改，则报错
	                		   if(data.onlyId!=""&&data.onlyId!=vm.cplant.id){//比较id不是同一条记录
	                		   alert("该仓库号已经存在！不能修改成此工厂!");
	                		   return false;
	                		   }
	                	   }
	                	   
	                	   var url = vm.cplant.id == null ? "config/CPlant/save" : "config/CPlant/update";
	               	    $.ajax({
	               	        type: "POST",
	               	        url: baseURL + url,
	               	        contentType: "application/json",
	               	        data: JSON.stringify(vm.cplant),
	               	        async:false,
	               	        success: function(data){
	               	        	if(data.code == 0){
	                                   js.showMessage('操作成功');
	                                   vm.saveFlag= true;
	                               }else{
	                                   alert(data.msg);
	                               }
	               	        }
	               	    });
	                	
	        	}
	        }
		});
		
		
    }
});
