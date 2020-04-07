var workshop_edit='';
var line_edit='';
var vm = new Vue({
    el:'#rrapp',
    data:{
    	deviceType:{
    		deviceTypeCode:'',deviceTypeName:''},
    		saveFlag:false,
    		id:''
    },
    created: function(){
    	 
	},
    watch:{},
    methods:{
    
      getInfo:function(id){
    	  
    	//页面赋值
			$.ajax({
	            type: "GET",
	            url: baseURL + "masterdata/deviceType/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	vm.deviceType = r.deviceType;
	            }
			});
			$("#deviceTypeCode").attr("readonly",true);
		
  		}
    }
});
$("#saveForm").validate({
	submitHandler: function(form){
		
		js.loading("正在保存,请稍候...");
		var url = vm.deviceType.id ==null ? "masterdata/deviceType/save" : "masterdata/deviceType/update";
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	        contentType: "application/json",
	        data: JSON.stringify(vm.deviceType),
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