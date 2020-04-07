var vm = new Vue({
	el:'#rrapp',
    data:{
    	cvendor:{},
    	saveFlag:false
    },
    methods:{
    	getInfo:function(id){
    		
			$.ajax({
	            type: "GET",
	            url: baseURL + "config/CVendor/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	vm.cvendor = r.cvendor;
	            	$("#werks").attr("disabled",true);
	            	$("#lifnr").attr("readonly",true);
	            }
			});
    	},
    	getPlantNoFuzzy:function(){
			getPlantNoSelect("#werks",null,null);
		},
		getVendorNoFuzzy:function(){
			getVendorNoSelect("#lifnr","#name1",null,$("#werks").val());
		},
		getWerksName:function(){
			mapcond={werks:$("#werks").val()};
			$.ajax({
		        url: baseURL + "config/CVendor/listPlantName",
		        dataType : "json",
				type : "post",
		        data: mapcond,
		        async:false,
		        success: function(data){
		        	if(data.code == 0){
		        		vm.cvendor.werksName=data.werksName;
		        		$("#werksName").val(data.werksName);
		        	}
		        }
			})
		},
		getVerdorName:function(){
			mapcond={lifnr:$("#lifnr").val()};
			$.ajax({
		        url: baseURL + "config/CVendor/listVendortName",
		        dataType : "json",
				type : "post",
		        data: mapcond,
		        async:false,
		        success: function(data){
		        	if(data.code == 0){
		        		vm.cvendor.name1=data.vendorName;
		        		$("#name1").val(data.vendorName);
		        	}
		        }
			})
		}

    }
});

$("#saveForm").validate({
	submitHandler: function(form){
	   vm.cvendor.werks=$("#werks").val();
 	   vm.cvendor.lifnr=$("#lifnr").val();
	   vm.cvendor.name1=$("#name1").val();
		//判断 工厂代码  供应商代码，是否存在，不存在才可以添加
		var mapcond={werks:vm.cvendor.werks,lifnr:vm.cvendor.lifnr};
		
		$.ajax({
	        url: baseURL + "config/CVendor/listEntity",
	        dataType : "json",
			type : "post",
	        data: mapcond,
	        async:false,
	        success: function(data){
	        	if(data.code == 0){
                   
                	   if(data.onlyOne=='true'&&vm.cvendor.id==null){//如果有记录并且是插入，则报错
                		   alert("工厂号:"+vm.cvendor.werks+"  供应商代码:"+vm.cvendor.lifnr+" 已经存在！增加失败！");
                		   return false;
                	   }   
                	   
                	   if(vm.cvendor.id!=null){//如果有记录并且是修改，则报错
                		   if(data.onlyId!=""&&data.onlyId!=vm.cvendor.id){//比较id不是同一条记录
                		   alert("该工厂 和工厂代码 已经存在！");
                		   return false;
                		   }
                	   }
                	   
                	var url = vm.cvendor.id == null ? "config/CVendor/save" : "config/CVendor/update";
               	    $.ajax({
               	        type: "POST",
               	        url: baseURL + url,
               	        contentType: "application/json",
               	        data: JSON.stringify(vm.cvendor),
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
                   
                }else{
                    
                }
	        }
	    });
		
		
    }
});
