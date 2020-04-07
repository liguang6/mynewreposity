var excelData = null; 
var vm = new Vue({
    el:'#rrapp',
    data:{
    	saveFlag:true
    },   
    methods: {
    	saveBtn:function(){

    		 if(excelData === null){
    			 alert("需要上传的数据为空");
    			 wm.saveFlag=false;
    			 return false;	
    		 }
    		 for(var dIndex in excelData){
    			 if(excelData[dIndex].msg!=''){
    				 alert("第"+excelData[dIndex].rowNo+"行数据校验未通过!");
    				 wm.saveFlag=false;
    				 return false;
    			 }
    		 }
    		 
    		 //
    		 var fgstr="";//是否有重复的行项目
    			$.ajax({
    				url:baseURL+"config/poAuth/queryByEbelnMin",
    				dataType : "json",
    				type : "post",
    				data : {
    					"ARRLIST":JSON.stringify(excelData)
    				},
    				async: false,
    				success: function (response) { 
    					if(response.code == "0"){
    						var retQuery=response.retQuery;
    						for(var i=0;i<retQuery.length;i++){
    							fgstr=fgstr+"订单:"+retQuery[i].ebeln+" 行号:"+retQuery[i].ebelp
    						}
    						
    					}
    	         	
    				}
    			});
    			
    			if(fgstr!=""){

    				//记录存在的显示框
    				$("#outNo1").html(fgstr+" 已经存在，是否覆盖？");
    				layer.open({
    	        		type : 1,
    	        		offset : '50px',
    	        		skin : 'layui-layer-molv',
    	        		title : "提示",
    	        		area : [ '500px', '200px' ],
    	        		shade : 0,
    	        		shadeClose : false,
    	        		content : jQuery("#resultLayer1"),
    	        		btn : [ '是','否'],
    	        		btn1 : function(index) {

    	        			$.ajax({
    	        				url:baseURL+"config/poAuth/import",
    	        				dataType : "json",
    	        				type : "post",
    	        				data : {
    	        					"ARRLIST":JSON.stringify(excelData),
    	        					"FG":"Y"
    	        				},
    	        				async: true,
    	        				success: function (response) { 
    	        					js.showMessage("保存成功");
    	        					close();
    	        				}
    	        			});
    	        			layer.close(index);
    	        		
    	        		},
    	        		btn2 : function(index) {

    	        			$.ajax({
    	        				url:baseURL+"config/poAuth/import",
    	        				dataType : "json",
    	        				type : "post",
    	        				data : {
    	        					"ARRLIST":JSON.stringify(excelData),
    	        					"FG":"N"
    	        				},
    	        				async: true,
    	        				success: function (response) { 
    	        					js.showMessage("保存成功");
    	        					close();
    	        				}
    	        			})
    	        			layer.close(index);
    	        		
    	        		}
    	        	});
    			
    			}else{
    				$.ajax({
    					url:baseURL+"config/poAuth/import",
    					dataType : "json",
    					type : "post",
    					data : {
    						"ARRLIST":JSON.stringify(excelData),
    						"FG":""
    					},
    					async: true,
    					success: function (response) { 
    						js.showMessage("保存成功");
    						close();
    					}
    				})
    			}
    		 //
    		 

    	}
    }
});
$("#uploadForm").validate({
	  submitHandler:function(e){
		  //提交表单 & 加载数据
		  var form = new FormData(document.getElementById("uploadForm"));
			$.ajax({
				url:baseUrl + "config/poAuth/preview",
				data:form,
				type:"post",
				contentType:false,
				processData:false,
				success:function(data){
					if(data.code == 0){
						//加载jqgrid
						$("#dataGrid").jqGrid({
						 	datatype: 'local', 
							colNames:['采购工厂','采购订单','行号','物料号','物料描述','供应商代码','最大可收货数量','授权工厂','校验消息',''],
							colModel:[
						          {name:"werks",index:'WERKS',align:'center',width:80},
						          {name:"ebeln",index:'EBELN',align:'center',width:80},
						          {name:"ebelp",index:'EBELP',align:'center',width:90},
						          {name:"matnr",index:'MATNR',align:'center',width:90},
						          {name:"txz01",index:'TXZ01',align:'center',width:90},
						          {name:"lifnr",index:'LIFNR',align:'center',width:90},
						          {name:"maxMenge",index:'MAX_MENGE',align:'center',width:90},
						          {name:"authWerks",index:'AUTHWERKS',align:'center',width:100},
						          {name:"msg",index:'msg',align:'center',width:100},
						          {name:"rowNo",index:'rowNo',align:'center',hidden:true}
					        ],
					        width:890
						});
						//清空数据
						$("#dataGrid").dataGrid("clearGridData");
						//加入数据
						for(dIndex in data.data){
							$("#dataGrid").dataGrid("addRowData",dIndex+1,data.data[dIndex]);
						}
						excelData = data.data;
						console.log("excelData",excelData);
					}
					else
					  alert("上传失败," + data.msg);
				}.bind(this)
			});
	  }
});


function saveUpload(){
	 if(excelData === null){
		 alert("需要上传的数据为空");
		 wm.saveFlag=false;
		 return false;	
	 }
	 for(var dIndex in excelData){
		 if(excelData[dIndex].msg!=''){
			 alert("第"+excelData[dIndex].rowNo+"行数据校验未通过!");
			 wm.saveFlag=false;
			 return false;
		 }
	 }
	 
	 //
	 var fgstr="";//是否有重复的行项目
		$.ajax({
			url:baseURL+"config/poAuth/queryByEbelnMin",
			dataType : "json",
			type : "post",
			data : {
				"ARRLIST":JSON.stringify(excelData)
			},
			async: false,
			success: function (response) { 
				if(response.code == "0"){
					var retQuery=response.retQuery;
					for(var i=0;i<retQuery.length;i++){
						fgstr=fgstr+"订单:"+retQuery[i].ebeln+" 行号:"+retQuery[i].ebelp
					}
					
				}
         	
			}
		});
		
		if(fgstr!=""){

			//记录存在的显示框
			$("#outNo1").html(fgstr+" 已经存在，是否覆盖？");
			layer.open({
        		type : 1,
        		offset : '50px',
        		skin : 'layui-layer-molv',
        		title : "提示",
        		area : [ '500px', '200px' ],
        		shade : 0,
        		shadeClose : false,
        		content : jQuery("#resultLayer1"),
        		btn : [ '是','否'],
        		btn1 : function(index) {

        			$.ajax({
        				url:baseURL+"config/poAuth/import",
        				dataType : "json",
        				type : "post",
        				data : {
        					"ARRLIST":JSON.stringify(excelData),
        					"FG":"Y"
        				},
        				async: true,
        				success: function (response) { 
        					js.showMessage("保存成功");
        					close();
        				}
        			});
        			layer.close(index);
        		
        		},
        		btn2 : function(index) {

        			$.ajax({
        				url:baseURL+"config/poAuth/import",
        				dataType : "json",
        				type : "post",
        				data : {
        					"ARRLIST":JSON.stringify(excelData),
        					"FG":"N"
        				},
        				async: true,
        				success: function (response) { 
        					js.showMessage("保存成功");
        					close();
        				}
        			})
        			layer.close(index);
        		
        		}
        	});
		
		}else{
			$.ajax({
				url:baseURL+"config/poAuth/import",
				dataType : "json",
				type : "post",
				data : {
					"ARRLIST":JSON.stringify(excelData),
					"FG":""
				},
				async: true,
				success: function (response) { 
					js.showMessage("保存成功");
					close();
				}
			})
		}
	 //
	 //提交数据 & 关闭窗口
	 /*var jsondata = JSON.stringify(excelData);
	 $.ajax({
		 url:baseUrl + "config/poAuth/import",
		 type:"post",
		 dataType:"json",
		 contentType: 'application/json',
		 data:jsondata,
		 success:function(resp){
			 if(resp.code === 0){
				 close();
				 js.showMessage('保存成功');
			 }else{
				 alert("上传失败," + resp.msg)
			 }
		 }
	 })*/
}