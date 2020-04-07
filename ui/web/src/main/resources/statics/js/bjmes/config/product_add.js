
var vm = new Vue({
    el:'#rrapp',
    data:{
    	product:{},
    	productarray:[{'id':'','process_code':'','process_name':''}],
    	processflowlist:[],
    	saveFlag:false,
    	id:''
    },
    created: function(){
    	this.productarray.length=0;
    },
    watch:{
    	
    },
    methods:{
    
      getInfo:function(id){
    	  
    	//页面赋值
			$.ajax({
	            type: "GET",
	            url: baseURL + "config/bjMesProducts/infoByParentId/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	if(r.data!=null){
	            		vm.productarray=r.data;
	            	}else{
	            		
	            	}
	            	
	            }
			});
	
		
  		},
  		getProcessNoFuzzy:function(event,process_flow_code){
  			//console.info("id",event.target.id);
  			getProcessByflowcodeNoSelect("#"+event.target.id,process_flow_code,fn_backcall_process);
  			
  			
		},
		insert:function(index,product_code,product_name,id){
			
			//insert 一个元素
			this.productarray.splice(index+1,0,{"product_code":product_code,"product_name":product_name,"id":id,"process_code":'',"process_name":''});
			
		},
		remove:function(index){
			this.pos = window.scrollY;
			if(this.productarray.length <= 1)
				return;
			//记录当前滚动条位置
			
			//删除一个元素
			this.productarray.splice(index,1);
		},
		saveOrUpdate: function (event) {
			js.loading("正在保存,请稍候...");
			console.info("length",this.productarray.length);
			//
			if(this.productarray.length>0){
				$.ajax({
					type: "POST",
				    url: baseURL + "config/bjMesProducts/deleteByParentId/"+this.productarray[0].id,
				    contentType: "application/json",
				    success: function(r){
						if(r.code == 0){
							
						}else{
							//alert(r.msg);
						}
					}
				});
			}
			
			//
			var productarraylist=[];
			for(var i=0;i<this.productarray.length;i++){
				//console.info("chid",this.productarray[i].chid);
				var p={};
				console.info("process_code",this.productarray[i].process_code);
				//
				if(this.productarray[i].process_code==''||this.productarray[i].process_code==null){
					js.alert("安装节点编码不能为空!");
					return false;
				}
				
				
				//
				
				p.parent_process_code=this.productarray[i].process_code;
				p.parent_process_name=this.productarray[i].process_name;
				p.parent_product_id=this.productarray[i].id;
				productarraylist[i]=p;
				
			}
			console.info("arrylist",productarraylist[0].parent_process_code);
			console.info("arrylist",productarraylist[1].parent_process_code);
			$.ajax({
		        type: "POST",
		        url: baseURL + "config/bjMesProducts/saveComp",
		        //contentType: "application/json",
		        dataType : "json",
		        data: {"ARRLIST":JSON.stringify(productarraylist)},
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
    }
});


function fn_backcall_process(process){
	$(process.dizhi).val(process.node_code);//安装节点编码
	
	$(process.dizhi).parent("td").next().html(process.node_name);//安装节点名称
	//给数组中的process_code,process_name赋值
	vm.productarray[process.dizhi.substr(1)-1].process_code=process.node_code;
	vm.productarray[process.dizhi.substr(1)-1].process_name=process.node_name;
	
}