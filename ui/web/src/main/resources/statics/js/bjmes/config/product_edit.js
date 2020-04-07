
var vm = new Vue({
    el:'#rrapp',
    data:{
    	product:{},
    	processflowlist:[],
    	saveFlag:false,
    	process_flow_code_edit:'',
    	product_type_code_edit:'',
    	id:''
    },
    created: function(){
    	$.ajax({
      	  url:baseUrl + "config/bjMesProcessFlow/getList",
      	  data:{
      		 
      	  },
      	  success:function(resp){
      		 vm.processflowlist = resp.data;
      		 if(resp.data.length>0){
      			 vm.product.process_flow_code=resp.data[0].PROCESS_FLOW_CODE;
      			 if(vm.process_flow_code_edit!=''){
      				 vm.product.process_flow_code=vm.process_flow_code_edit;
      			 }
      		 }
      		 
      	  }
        })
        
    	this.product.process_flow_code = $("#process_flow_code").find("option").first().val();
	},
    watch:{
    	
    },
    methods:{
    
      getInfo:function(id){
    	  
    	//页面赋值
			$.ajax({
	            type: "GET",
	            url: baseURL + "config/bjMesProducts/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	vm.product = r.product;
	            	vm.id=r.product.id;
	            	vm.process_flow_code_edit=r.product.process_flow_code;
	            }
			});
	
		
  		},
  		getProcessNoFuzzy:function(){
  			
  			//模糊查找设备类型
  			getProcessNoSelect("#process_code",fn_getProcessCode);
  			
  			
		}
    }
});
$("#saveForm").validate({
	submitHandler: function(form){
		vm.product.product_type_code=$("#product_type_code").val();
		vm.product.product_type_name=$("#product_type_code").find("option:selected").text();
		
		js.loading("正在保存,请稍候...");
		var url = vm.id =='' ? "config/bjMesProducts/save" : "config/bjMesProducts/update";
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	        contentType: "application/json",
	        data: JSON.stringify(vm.product),
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

function fn_getProcessCode(process){
	$("#process_code").val(process.PROCESS_CODE);
	$("#process_name").val(process.PROCESS_NAME);
	
	vm.product.process_code=process.PROCESS_CODE;
	vm.product.process_name=process.PROCESS_NAME;
}