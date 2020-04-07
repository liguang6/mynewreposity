var workshop_edit='';
var line_edit='';
var vm = new Vue({
    el:'#rrapp',
    data:{
    		STANDARD_TYPE:'',
    		STANDARD_CODE:'',
    		STANDARD_NAME:'',
    		saveFlag:false,
    		id:''
    },
    created: function(){
    	this.STANDARD_TYPE = $("#STANDARD_TYPE").find("option").first().val();
	},
    watch:{},
    methods:{
    
      getInfo:function(id){
    	  
    	//页面赋值
			$.ajax({
	            type: "GET",
	            url: baseURL + "masterdata/testStandard/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	vm.STANDARD_TYPE = r.result.STANDARD_TYPE;
	            	vm.STANDARD_CODE = r.result.STANDARD_CODE;
	            	vm.STANDARD_NAME = r.result.STANDARD_NAME;
	            	vm.id = r.result.ID;
	            }
			});
	
		
  		}
    }
});
$("#saveForm").validate({
	submitHandler: function(form){
		
		js.loading("正在保存,请稍候...");
		var url = vm.id =='' ? "masterdata/testStandard/save" : "masterdata/testStandard/update";
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	        //contentType: "application/json",
	        data: {
	        	STANDARD_TYPE:vm.STANDARD_TYPE,
	        	STANDARD_CODE:vm.STANDARD_CODE,
	        	STANDARD_NAME:vm.STANDARD_NAME,
	        	ID:vm.id
	        },
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

 }
});