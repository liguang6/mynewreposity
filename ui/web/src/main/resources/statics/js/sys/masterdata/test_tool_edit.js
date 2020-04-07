var workshop_edit='';
var line_edit='';
var vm = new Vue({
    el:'#rrapp',
    data:{
    	tool:{
    		werks:'',
    		test_tool_no:'',
    		test_tool_name:'',
    		specification:'',
    		memo:''
    			},
    		saveFlag:false,
    		id:''
    },
    created: function(){
    	this.tool.werks = $("#werks").find("option").first().val();
	},
    watch:{},
    methods:{
    
      getInfo:function(id){
    	  
    	//页面赋值
			$.ajax({
	            type: "GET",
	            url: baseURL + "masterdata/testTool/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	vm.tool.id = r.result.ID;
	            	vm.tool.werks = r.result.WERKS;
	            	vm.tool.test_tool_no = r.result.TEST_TOOL_NO;
	            	vm.tool.test_tool_name = r.result.TEST_TOOL_NAME;
	            	vm.tool.specification = r.result.SPECIFICATION;
	            	vm.tool.memo = r.result.MEMO;
	            }
			});
	
			$('#werks').attr("disabled",true);
			$('#test_tool_no').attr("disabled",true);
		
  		}
    }
});
$("#saveForm").validate({
	submitHandler: function(form){
		
		js.loading("正在保存,请稍候...");
		var url = vm.tool.id ==null ? "masterdata/testTool/save" : "masterdata/testTool/update";
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	        dataType : "json",
	        data: vm.tool,
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