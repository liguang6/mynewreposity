$(function () {
    $("#dataGrid").dataGrid({
    	searchForm: $("#searchForm"),
        columnModel: [			
			{ label: '字典名称', name: 'name', index: 'name', width: 80 },
			{ label: '字典类型', name: 'type', index: 'type', width: 80 }, 			
			{ label: '字典码', name: 'code', index: 'code', width: 80 }, 			
			{ label: '字典值', name: 'value', index: 'value', width: 80 }, 			
			{ label: '排序', name: 'orderNum', index: 'order_num', width: 80 }, 			
			{ label: '备注', name: 'remark', index: 'remark', width: 80 },
			{ label: '操作', name: 'id', index: 'id', formatter: function(item, index){
	        		if(item != ""){
	        			return '<i class="fa fa-pencil" title="编辑" onclick="fun_edit('+item+');" style="font-size:16px;color:green;cursor: pointer;"></i> '
	            		+'&nbsp;<i class="fa fa-trash-o" onclick="fun_del('+item+');" title="删除" style="font-size:16px;color:red;cursor: pointer;"></i>';
	        		}else{
	        			return "-";
	        		}
        		}
		    },
		],
		rowNum: 15,
		rowList : [15,30,50]
    });
});

var vm = new Vue({
	el:'#rrapp',
	data:{
	},
	methods: {
		refresh:function(){
			$("#searchForm").submit();
        },
        add: function(){
        	js.addTabPage(null, '<i class="fa fa-plus" style="font-size:12px"> 增加数据字典</i>', baseURL + 'sys/masterdata/dict_addform.html', true, true)
        },
		reload: function (event) {
			$("#searchForm").submit();
		}
	}
});


function fun_edit(id){
	js.addTabPage(null, '<i class="fa fa-plus" style="font-size:12px"> 编辑数据字典</i>', baseURL + 'sys/masterdata/dict_addform.html?id='+id, true, true)
}

function fun_del(id){
	js.confirm('确定要删除选中的记录？', function(){
		$.ajax({
			type: "POST",
		    url: baseURL + "masterdata/dict/deleteById?id="+id,
            contentType: "application/json",
		    success: function(r){
				if(r.code == 0){
					alert('操作成功', function(index){
						$("#jqGrid").trigger("reloadGrid");
					});
				}else{
					alert(r.msg);
				}
			}
		});
	});
}
