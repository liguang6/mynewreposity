var vm = new Vue({
    el:'#rrapp',
    data:{
    	targetParam:{},
    	saveFlag:false,
    	testNodeList:[],
    },
    watch:{
		'targetParam.testType' : {
		  handler:function(newVal,oldVal){
			  if(newVal==''){
				  vm.testNodeList=[];
				  return;
			  }
			  this.getTestNodeList();
		  }
		},
	},
    methods:{
    	getInfo:function(id){
			$.ajax({
	            type: "GET",
	            url: baseURL + "qms/config/targetParamter/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	console.info(r.entity)
	            	vm.targetParam = r.entity;
	            	if(vm.targetParam.targetType=='01'){
	    				$("#werks option[value='']").prop("selected",true);
	    				$("#startDate").val('');
	    				$("#endDate").val('');
	    				$('#werks').attr("disabled",true);
	    				$('#startDate').attr("disabled",true);
	    				$('#endDate').attr("disabled",true);
	    			}
	            }
			});
    	},
    	getTestNodeList:function(){
			console.log("getTestNodeList ");
			$.ajax({
				url:baseURL+"qms/config/checkNode/getList",
				dataType : "json",
				type : "post",
				data : {
					"testType":$("#testType").val()
				},
				async: false,
				success: function (response) { 
					vm.testNodeList=response.data;
				}
			});	
		},
		targetTypeChange:function(){
			var targetType=vm.targetParam.targetType;
			if(targetType=='01'){
				$("#werks option[value='']").prop("selected",true);
				$("#startDate").val('');
				$("#endDate").val('');
				$('#werks').attr("disabled",true);
				$('#startDate').attr("disabled",true);
				$('#endDate').attr("disabled",true);
			}else{
				$('#werks').removeAttr("disabled");
				$('#startDate').removeAttr("disabled");
				$('#endDate').removeAttr("disabled");
			}
		}
    }
});
$("#saveForm").validate({
	
	submitHandler: function(form){

		if($("#testType").val()==''){
			js.showErrorMessage("'检测节点不能为空！");
			return ;
		}
		if($("#targetValue").val()==''){
			js.showErrorMessage("'目标值不能为空！");
			return ;
		}
		vm.targetParam.targetType=$("#targetType").val();
		vm.targetParam.testType=$("#testType").val();
		vm.targetParam.werks=$("#werks").val();
		vm.targetParam.startDate=$("#startDate").val();
		vm.targetParam.endDate=$("#endDate").val();
		var url = vm.targetParam.id == null ? "qms/config/targetParamter/save" : "qms/config/targetParamter/update";
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	        contentType: "application/json",
	        data: JSON.stringify(vm.targetParam),
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
