var vm = new Vue({
    el:'#rrapp',
    data:{
    	matstorage:{"werks":"","storageModel":"00","mpqFlag":"0","externalBatchFlag":"0","whNumber":""},
		saveFlag:false,
		warehourse:[],
		inList:[],
		outList:[]
		
    },
    created: function(){
    	// 加载存储类型代码select
    	//getStorageTypeCodeSelect();
    	this.matstorage.werks = $("#werks").find("option").first().val();
	},
	 watch:{
		 'matstorage.werks':{
				handler:function(newVal,oldVal){					
					//查询工厂仓库
			          $.ajax({
			        	  url:baseUrl + "common/getWhDataByWerks",
			        	  data:{"WERKS":vm.matstorage.werks},
			        	  success:function(resp){
			        		 vm.warehourse = resp.data;
			        		 vm.matstorage.whNumber = resp.data[0].WH_NUMBER;
			        	  }
			          })
				}
		  },
		 'matstorage.whNumber':{
				handler:function(newVal,oldVal){					
					this.queryCtrFlag(vm.matstorage.whNumber)
				}
		  }
		},
    methods:{
    	getInfo:function(id){
			$.ajax({
	            type: "GET",
	            url: baseURL + "config/matstorage/info/"+id,
	            contentType: "application/json",
	            success: function(r){
					vm.matstorage = r.wmsCMatStorage;
					vm.warehourse.push({WH_NUMBER:r.wmsCMatStorage.whNumber});					 
                    $('#werks').attr("disabled",true);
                    $('#whNumber').attr("disabled",true);
                    $('#matnr').attr("readonly",true);
	            }
			});
    	},
    	getPlantNoFuzzy:function(){
			getPlantNoSelect("#werks",null,null);
		},
    	getWhNoFuzzy:function(){
			getWhNoSelect("#whNumber",null,null);
		},
		getMaterialNoFuzzy:function(){
			/*
			 * $("#werks").val() : 工厂代码
			 * "#matnr" ： 物料号ID
			 * "#maktx" : 物料描述ID ;根据料号带出描述
			 */
			getMaterialNoSelect($("#werks").val(),"#matnr","#maktx",null,null);
		},
		getControlFlagInNoFuzzy:function(){//入库标识
			getControlFlagNoSelect($("#whNumber").val(),"00","#inControlFlag",null,null);
		},
		getControlFlagOutFuzzy:function(){//出库标识
			getControlFlagNoSelect($("#whNumber").val(),"01","#outControlFlag",null,null);
		},
		// 仓库代码失去焦点，重新加载存储区代码
		areaCodeChange: function(){
			$.ajax({
				type: "POST",
		        url: baseURL + "config/matstorage/queryAreaCode",
		        dataType : "json",
		        data: {
		        	"whNumber":whNumber,
                    "areaCode":areaCode
                },
		        success: function(resp){
		        	var data=resp.area;
		        	$('#areaName').val(data.areaName);
		        }
			});
		},
		binCodeBlur:function(){
			var whNumber=$("#whNumber").val();
			var binCode=$("#binCode").val().toUpperCase();
			$("#binCode").val(binCode);
			$.ajax({
				type: "POST",
		        url: baseURL + "config/matstorage/queryBinCode",
		        dataType : "json",
		        data: {
		        	"whNumber":whNumber,
                    "binCode":binCode
                },
		        success: function(resp){
		        	var data=resp.bin;
		        	vm.matstorage.binName=data.binName;
		        	$('#binName').val(data.binName);
		        	$('#storageType').val(data.storageTypeCode);
		        	$('#storageModel').val(data.storageModel=='00' ? '固定存储' : (data.storageModel=='01' ?'随机存储' :''));
		        	$('#areaCode').val(data.areaCode);
		        	$('#binRow').val(data.binRow);
		        	$('#binColumn').val(data.binColumn);
		        	$('#binFloor').val(data.binFloor);
		        	$.ajax({
						type: "POST",
				        url: baseURL + "config/matstorage/queryAreaCode",
				        dataType : "json",
				        data: {
				        	"whNumber":data.whNumber,
		                    "areaCode":data.areaCode
		                },
				        success: function(resp){
				        	var data=resp.area;
				        	$('#areaName').val(data.areaName);
				        }
					});
		        }
			});
		},
		queryCtrFlag : function(whNumber){
			$.ajax({
				type: "POST",
		        url: baseURL + "config/matstorage/queryCtrFlag",
		        dataType : "json",
		        data: {
		        	"whNumber":whNumber,
                },
		        success: function(resp){
		        	vm.inList = resp.inList
		        	vm.outList = resp.outList
		        	
		        }
				
			})
		}
    }
});
$("#saveForm").validate({
	submitHandler: function(form){
		// 物料号字段存在小写的转化成大写
		//vm.matstorage.matstorageCode=vm.matstorage.matstorageCode.toUpperCase();
		var url = vm.matstorage.id == null ? "config/matstorage/save" : "config/matstorage/update";
		vm.matstorage.werks=$("#werks").val();
		vm.matstorage.whNumber=$("#whNumber").val();
		vm.matstorage.matnr=$("#matnr").val();
		vm.matstorage.maktx=$("#maktx").val();
		
		vm.matstorage.correlationMaterial=$("#correlationMaterial").val();
		vm.matstorage.repulsiveMaterial=$("#repulsiveMaterial").val();
        vm.matstorage.prfrqFlag =$("#prfrqFlag").val();
		vm.matstorage.inControlFlag=$("#inControlFlag").val();
		vm.matstorage.outControlFlag=$("#outControlFlag").val();
		$.ajax({
	        type: "POST",
	        url: baseURL + url,
	        contentType: "application/json",
	        data: JSON.stringify(vm.matstorage),
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
