$(function(){
	$.ajax({
        type: "POST",
        url: baseURL + "common/batchRules/querybatchcode",
        dataType : "json",
        data: {},
        success: function(resp){
        	var data=resp.retlist;
        	var element=$("#batchRuleCode");
        	$.each(data,function(index,val){
        		element.append("<option value='"+val.BATCH_RULE_CODE+"'>"+val.BATCH_RULE_CODE+" "+val.BATCH_RULE_TEXT+"</option>");
        		
        	});
        }
    });
});

var vm = new Vue({
	el:'#rrapp',
    data:{
    	cbatchplantrules:{},
    	saveFlag:false
    },
    methods:{
    	getInfo:function(id){
    		
			$.ajax({
	            type: "GET",
	            url: baseURL + "common/batchRules/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	vm.cbatchplantrules = r.wmscbatchplanrules;
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
		vm.cbatchplantrules.werks=$("#werks").val();
		vm.cbatchplantrules.businessName = $("#businessName").val();
		vm.cbatchplantrules.batchRuleCode=$("#batchRuleCode").val();
		vm.cbatchplantrules.businessNameText = $("#businessName").find("option:selected").text(); 
		vm.cbatchplantrules.batchRuleText= $("#batchRuleCode").find("option:selected").text(); 
		vm.cbatchplantrules.lgort=$("#lgort").val();
		vm.cbatchplantrules.dangerFlag=$("#dangerFlag").val();
		vm.cbatchplantrules.fBatchFlag = $("#fBatchFlag").val();
		//判断 该条件下的批次规则  是否存在，不存在才可以添加
		var mapcond={werks:	vm.cbatchplantrules.werks,
				     business_name: vm.cbatchplantrules.businessName,
					 batch_rule_code:	vm.cbatchplantrules.batchRuleCode,
					 lgort:	vm.cbatchplantrules.lgort,
					 danger_flag:	vm.cbatchplantrules.dangerFlag};
		$.ajax({
	        url: baseURL + "common/batchRules/listBatchOnly",
	        dataType : "json",
			type : "post",
	        data: mapcond,
	        async:false,
	        success: function(data){
	        	if(data.code == 0){
	                	   if(data.onlyOne=='true'&&vm.cbatchplantrules.id==null){//如果有记录并且是插入，则报错
	                		   alert("该配置已经存在！不能再添加!");
	                		   return false;
	                	   }
	                	   if(vm.cbatchplantrules.id!=null){//如果有记录并且是修改，则报错
	                		   if(data.onlyId!=""&&data.onlyId!=vm.cbatchplantrules.id){//比较id不是同一条记录
	                		   alert("该配置已经存在！");
	                		   return false;
	                		   }
	                	   }
	                	   var url = vm.cbatchplantrules.id == null ? "common/batchRules/save" : "common/batchRules/update";
	               	    $.ajax({
	               	        type: "POST",
	               	        url: baseURL + url,
	               	        dataType: "json",
	               	         data: {
                                    id: vm.cbatchplantrules.id,                                   
                                    werks:	vm.cbatchplantrules.werks,
                                    businessName: vm.cbatchplantrules.businessName,
                                    businessNameText: vm.cbatchplantrules.businessNameText,
                                    batchRuleCode:	vm.cbatchplantrules.batchRuleCode,
                                    batchRuleText:  vm.cbatchplantrules.batchRuleText,
                                    lgort:	vm.cbatchplantrules.lgort,
                                    dangerFlag:	vm.cbatchplantrules.dangerFlag,
                                    fBatchFlag: vm.cbatchplantrules.fBatchFlag,
                            },
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
