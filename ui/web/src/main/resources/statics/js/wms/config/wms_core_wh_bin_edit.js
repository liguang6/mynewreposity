var vm = new Vue({
    el:'#rrapp',
    data:{
    	bin:{storageAreaCode:'',binType:'',binStatus:'',whNumber:''},
    	saveFlag: false,
    },
    watch: {
    	'bin.whNumber':{
			deep: true,
			handler:function(newVal,oldVal){
		    	// 加载存储类型代码select
				getAreaCodeSelect(newVal);
			}
    	}
    },
    created: function(){
    	// 加载存储类型代码select
	},
    methods:{
    	getInfo:function(id){
			$.ajax({
	            type: "GET",
	            url: baseURL + "config/corewhbin/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	getAreaCodeSelect(r.bin.whNumber,r.bin.areaCode);
	            	vm.bin = r.bin;
					console.info(r.bin);
					vm.showStorgeAreaName(r.bin.whNumber,r.bin.storageAreaCode);
	            	//vm.storageAreaCode = r.storageAreaCode;
	            }
			});
    	},
		// 仓库代码失去焦点，重新加载存储区代码
		whNumerBlur: function(){
			var whNumber=$("#whNumber").val();
			var storageTypeCode=$("#storageTypeCode").val();
			if(whNumber!=''){
				getAreaCodeSelect(whNumber);
			}
		},
		showStorgeAreaName(whNumbers,areaCodes){
    		var storageAreaCode  = $("#storageAreaCode").val();
    		var whNumber  = $("#whNumber").val();
    		if(whNumbers!=null && typeof(whNumbers) != "undefined"){
				whNumber = whNumbers;
				console.info('whNumber1=== '+whNumber);
			}
			if(areaCodes!=null && !typeof(areaCodes) != "undefined"){
				storageAreaCode = areaCodes;
				console.info('storageAreaCode1=== '+storageAreaCode);
			}


    		var areaName;
			$.ajax({
				type: "GET",
				url: baseURL + "config/wharea/queryAreaName",
				data: {
					"whNumber":whNumber,
					"storageAreaCode":storageAreaCode,
				},
				contentType: "application/json",
				success: function(r){
					console.info(r.list);
					var data=r.list;
					areaName = r.list[0].areaName;
					console.info("areaName "+areaName);
/*					$.each(data,function(index,val){
						areaName = val.areaName;
					});
					console.info("areaName2 "+areaName);*/
					$("#areaName").val(areaName);
				}
			});

		}
    }
});
$("#saveForm").validate({
	submitHandler: function(form){
		$("#storageAreaCode").removeAttr("disabled");
		$("#areaCode").removeAttr("disabled");
		// 物料号字段存在小写的转化成大写
		vm.bin.binCode=vm.bin.binCode.toUpperCase();
		vm.bin.whNumber=$("#whNumber").val();
		vm.bin.storageAreaCode=$("#storageAreaCode").val();
		var url = vm.bin.id == null ? "config/corewhbin/save" : "config/corewhbin/update";
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	        contentType: "application/json",
	        data: JSON.stringify(vm.bin),
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
function initWhSelect(werks){
	$.ajax({
		url:baseUrl + "common/getWhDataByWerks",
		data:{"WERKS":werks},
        dataType : "json",
        success: function(resp){
			var data=resp.data;
			console.log('data',$("#whNumber").html());
        	$.each(data,function(index,val){
        		$("#whNumber").append("<option value='"+val.WH_NUMBER+">"+val.WH_NUMBER+"</option>");
        	});
        }
    });
}
function getAreaCodeSelect(whNumber,selectVal){
	$("#storageAreaCode").html("");
	$.ajax({
        type: "POST",
        url: baseURL + "config/wharea/queryAll",
        dataType : "json",
        data: {
        	"whNumber":whNumber.toUpperCase(),
        },
        async:false,
        success: function(resp){
        	var data=resp.list;
        	var element=$("#storageAreaCode");
        	element.append("<option value='' storagetypecode=''>请选择</option>");
        	$.each(data,function(index,val){
        		if(selectVal==val.areaCode){
        			element.append("<option value='"+val.storageAreaCode+"'" + " selected>"+val.storageAreaCode+"</option>");
        			//$("#areaName").val(val.areaName);
				}else{
        			element.append("<option value='"+val.storageAreaCode+"'" + ">"+val.storageAreaCode+"</option>");
        		}
        	});
        }
    });
}