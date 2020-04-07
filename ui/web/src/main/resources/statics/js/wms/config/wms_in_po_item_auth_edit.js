var vm = new Vue({
	el:'#rrapp',
    data:{
    	poAuth:{},
    	saveFlag:false
    },
    methods:{
    	getInfo:function(id){
    		
			$.ajax({
	            type: "GET",
	            url: baseURL + "config/poAuth/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	vm.poAuth = r.wmsInPoItemAuth;
	            }
			});
    	},
    	getPlantNoFuzzy:function(){
			getPlantNoSelect("#werks",null,null);
		},
		getVendorNoFuzzy:function(){
			getVendorNoSelect("#lifnr","#name1",null,null);
		},
		getWerksName:function(){},
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
		        		vm.poAuth.name1=data.vendorName;
		        		$("#name1").val(data.vendorName);
		        	}
		        }
			})
		},
		getMaterialNoFuzzy:function(){
			/*
			 * $("#werks").val() : 工厂代码
			 * "#matnr" ： 物料号ID
			 * "#maktx" : 物料描述ID ;根据料号带出描述
			 */
			getMaterialNoSelect($("#werks").val(),"#matnr","#txz01",null,null);
		}

    }
});

$("#saveForm").validate({
	submitHandler: function(form){
		var url = vm.poAuth.id == null ? "config/poAuth/save" : "config/poAuth/update";
		if(vm.poAuth.authWerks == undefined || vm.poAuth.authWerks.trim() == ''){
			alert("授权工厂不能为空！");
			return false;
		}
	    $.ajax({
   	        type: "POST",
   	        url: baseURL + url,
   	        contentType: "application/json",
   	        data: JSON.stringify(vm.poAuth),
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
});
