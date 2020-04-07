var vm = new Vue({
    el:'#rrapp',
    data:{
        handoverType:{
        	whNumber:"",
        	werks:"",
        	businessName:"",
        	handoverType:""
        },
    	saveFlag:false,
    	warehourse:[]
    },
    created: function(){
    	this.handoverType.werks = $("#werks").find("option").first().val();
	},
    watch:{
    	'handoverType.werks': {
		　　　　handler(newValue, oldValue) {
		　　　　　　          //查询工厂仓库
		          $.ajax({
		        	  url:baseUrl + "common/getWhDataByWerks",
		        	  data:{"WERKS": newValue},
		        	  success:function(resp){
		        		 vm.warehourse = resp.data;
		        		 vm.handoverType.whNumber = resp.data[0].WH_NUMBER;
		        	  }
		          })
		          
		　　　　}
		}
	},
    methods:{
    	getInfo:function(id){
			$.ajax({
	            type: "GET",
	            url: baseURL + "config/handoverType/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	vm.handoverType = r.handoverType;
	            	$("#werks").attr("disabled",true);
	            	$("#whNumber").attr("disabled",true);
	            	$("#businessName").attr("disabled",true);     	
	            }
			});
    	},
    }
});
$("#saveForm").validate({
	submitHandler: function(form){
        vm.handoverType.werks=$("#werks").val();
        vm.handoverType.whNumber=$("#whNumber").val();
		var url = vm.handoverType.id == null ? "config/handoverType/save" : "config/handoverType/update";
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	        contentType: "application/json",
	        data: JSON.stringify(vm.handoverType),
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