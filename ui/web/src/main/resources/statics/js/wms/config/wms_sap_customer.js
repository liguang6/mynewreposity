
$(function(){
	$("#dataGrid").dataGrid({
		searchForm:$("#searchForm"),
		datatype: "json",
		colModel: [			
					{label:"客户代码",name: 'kunnr',index:"kunnr",width: 100,align:"center"},
		            {label: '客户名称', name: 'name1', index:"name1", width: 300,align:"center" },
					{label: '客户简称', name: 'sortl',index:"sortl", width: 200,align:"center"},
					{label: '销售组织', name: 'vkorg',index:"vkorg", width: 100,align:"center"},
					{label: '分销渠道', name: 'vtweg',index:"vtweg", width: 100,align:"center"},
					{label: '公司代码', name: 'bukrs',index:"bukrs", width: 100,align:"center"},
					{label: '部门', name: 'spart',index:"spart", width: 100,align:"center"},
					{label: '地址', name: 'address',index:"address", width: 100,align:"center"},
					{label: '客户管理者', name: 'manager',index:"manager", width: 150,align:"center"},
					{label:'维护人',name:'editor',index:"editor",align:"center"},
					{label:'维护时间',name:'editDate',index:"edit_date",align:"center"},
					{label:'ID',width:75,name:'id',index:"id",align:"center",hidden:true}//隐藏此列
		     ],
		viewrecords: true,
		rowNum: 15,
		rownumWidth: 25, 
		autowidth:true
	});
	
	$("#editOperation").click(function(){
		  
		if(getSelRow("#dataGrid")===null){
			alert("请选择编辑的记录")
			return ;
		}
		
		 js.layer.open({
				type: 2,
				title: "编辑客户信息",
				area: ["780px", "300px"],
				content: baseUrl + "wms/config/wms_sap_customer_edit.html",
				closeBtn:1,
				end:function(){
					$("#searchForm").submit();
				},
				btn: ["确定","取消"],
				yes:function(index,layero){
					//提交表单
					var win = layero.find('iframe')[0].contentWindow;
					win.vm.save();//保存数据
					layer.close(index);
				},
				no:function(index,layero){
					layer.close(index);
				},
				success:function(layero,index){
					var win = layero.find('iframe')[0].contentWindow;
					var row = getSelRow("#dataGrid");
					win.vm.init(row.id);//查询信息
				}
		});
	});
	
	$("#importOperation").click(function(){
		js.layer.open({
			type: 2,
			title: "导入客户信息",
			area: ["80%", "80%"],
			content: baseUrl + "wms/config/wms_sap_customer_upload.html",
			closeBtn:1,
			end:function(){
				$("#searchForm").submit();
			},
			btn: ["取消"],
			no:function(index,layero){
				layer.close(index);
			}
	    });
	});
	
	$("#newOperation").click(function(){
		js.layer.open({
			type: 2,
			title: "新增客户信息",
			area: ["780px", "300px"],
			content: baseUrl + "wms/config/wms_sap_customer_edit.html",
			closeBtn:1,
			end:function(){
				$("#searchForm").submit();
			},
			btn: ["确定","取消"],
			yes:function(index,layero){
				//提交表单
				var win = layero.find('iframe')[0].contentWindow;
				win.vm.save();//保存数据
				layer.close(index);
			},
			no:function(index,layero){
				layer.close(index);
			}
	});
	});
});