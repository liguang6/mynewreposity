var vm = new Vue({
	el:'#rrapp',
    data:{
    	cdocno:{},
    	saveFlag:false
    },
    methods:{
    	getInfo:function(id){
    		
			$.ajax({
	            type: "GET",
	            url: baseURL + "common/docNo/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	vm.cdocno = r.cdocno;
	            }
			});
    	},
    	getPlantNoFuzzy:function(){
			getPlantNoSelect("#werks",null,null);
		}

    }
});

$("#saveForm").validate({
	submitHandler: function(form){
		if($("#noLength").val()<=0){
			alert("编号长度必须大于0！");
			return false;
		}
		
		vm.cdocno.werks=$("#werks").val();
		vm.cdocno.sys=$("#sys").val();
		vm.cdocno.docType=$("#docType").val();
		//判断 工厂代码  是否存在，不存在才可以添加
		var mapcond={werks:vm.cdocno.werks,
					 sys:vm.cdocno.sys,
					 doc_Type:vm.cdocno.docType};
		$.ajax({
	        url: baseURL + "common/docNo/listEntity",
	        dataType : "json",
			type : "post",
	        data: mapcond,
	        async:false,
	        success: function(data){
	        	if(data.code == 0){
	                   
	                	   if(data.onlyOne=='true'&&vm.cdocno.id==null){//如果有记录并且是插入，则报错
	                		   alert("该配置已经存在！不能再添加!");
	                		   return false;
	                	   }
	                	   var url = vm.cdocno.id == null ? "common/docNo/save" : "common/docNo/update";
	               	    $.ajax({
	               	        type: "POST",
	               	        url: baseURL + url,
	               	        contentType: "application/json",
	               	        data: JSON.stringify(vm.cdocno),
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
