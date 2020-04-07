var vm = new Vue({
    el:'#rrapp',
    data:{
        handover:{werks:"",whNumber:"",businessName:"",status:""},
    	saveFlag:false,
    	warehourse:[]
    },
    created: function(){
    	this.handover.werks = $("#werks").find("option").first().val();
	},
    watch:{
    	'handover.werks': {
		　　　　handler(newValue, oldValue) {
		　　　　　　          //查询工厂仓库
		          $.ajax({
		        	  url:baseUrl + "common/getWhDataByWerks",
		        	  data:{"WERKS": newValue},
		        	  success:function(resp){
		        		 vm.warehourse = resp.data;
		        		 vm.handover.whNumber = resp.data[0].WH_NUMBER;
		        	  }
		          })
		          
		　　　　}
		}
	},
    methods:{
    	getInfo:function(id){
			$.ajax({
	            type: "GET",
	            url: baseURL + "config/handover/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	vm.handover = r.handover;
	            }
			});
    	},
    }
});
$("#saveForm").validate({
	submitHandler: function(form){
		js.loading("正在保存,请稍候...");
		// 先校验该工号在一卡通系统是否存在，存在才允许保存
        vm.handover.endDate=$("#endDate").val();
	    var url = vm.handover.id == null ? "config/handover/save" : "config/handover/update";
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	        contentType: "application/json",
	        data: JSON.stringify(vm.handover),
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