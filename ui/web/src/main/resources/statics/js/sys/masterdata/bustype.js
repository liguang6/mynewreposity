$(function () {
    $("#dataGrid").dataGrid({
    	searchForm: $("#searchForm"),
        colModel: [			
			{ label: '车型代码', name: 'busTypeCode', index: 'bus_type_code', width: 80 }, 			
			{ label: '代号/内部名称', name: 'internalName', index: 'internal_name', width: 80 }, 	
			{ label: '平台', name: 'busSeries', index: 'bus_series', width: 80 }, 
			{ label: '品牌', name: 'brand', index: 'brand', width: 70 }, 			
			{ label: '制造商', name: 'manufacturer', index: 'manufacturer', width: 150 }, 			
			{ label: '车辆类型', name: 'vehicleType', index: 'vehicle_type', width: 70 }, 			
			{ label: '动力类型', name: 'powerType', index: 'power_type', width: 80 }, 			
			{ label: '备注', name: 'memo', index: 'memo', width: 80 }, 			
			{ label: '维护人', name: 'editor', index: 'editor', width: 100 }, 			
			{ label: '维护时间', name: 'editDate', index: 'edit_date', width: 150 }			
        ],
		viewrecords: true,
        rowNum: 10,
        rownumbers: true, 
        rownumWidth: 25, 
        autowidth:true,
        multiselect: true,
        prmNames : {
            page:"page", 
            rows:"limit", 
            order: "order"
        }
    });
});

var vm = new Vue({
	el:'#rrapp',
	data:{
		settingBusType: {}
	},
	methods: {
		query: function () {
			$("#searchForm").submit();
		},
		add: function(event){
			saveOrUpdate(event);
		},
		update: function (event) {
			var id = getSelectedRow();
			if(id == null){
				return ;
			}
			saveOrUpdate(event,id);
		},
		del: function (event) {
			var ids = getSelectedRows();
			if(ids == null){
				return ;
			}
			
			js.confirm('确定要删除选中的记录？', function(){
				$.ajax({
					type: "POST",
				    url: baseURL + "masterdata/bustype/delete",
                    contentType: "application/json",
				    data: JSON.stringify(ids),
				    success: function(r){
						if(r.code == 0){
							js.showMessage('操作成功！');
							$("#dataGrid").trigger("reloadGrid");
						}else{
							alert(r.msg);
						}
					}
				});
			});
		}
	}
});

function saveOrUpdate(event,bustTypeId){
	var options = {
			type: 2,
			maxmin: true,
			shadeClose: false,
			title: $(event.currentTarget).html()+'车型',
			area: ["800px", "500px"],
			content: 'sys/masterdata/bustype_form.html',
			btn: ['<i class="fa fa-check"></i> 确定'],
			success:function(layero, index){
				if(bustTypeId){
					var busTypeIdInput= $("#busTypeId", layero.find("iframe")[0].contentWindow.document);
					$(busTypeIdInput).val(bustTypeId);
					var win = top[layero.find('iframe')[0]['name']];
					win.vm.getSettingbustype();
				}
			},
			btn1:function(index,layero){
				var win = top[layero.find('iframe')[0]['name']];
				var rtn = win.vm.saveOrUpdate();
				if(typeof listselectCallback == 'function'){
					listselectCallback(index,rtn);
				}
				
			}
		};
		/*options.btn.push('<i class="fa fa-reply-all"></i> 重置');*/
		options.btn.push('<i class="fa fa-close"></i> 关闭');
		options['btn'+options.btn.length] = function(index, layero){
			parent.layer.close(index); 
		};
		
		js.layer.open(options);
}


function listselectCallback(index,rtn){
	rtn = rtn ||false;
	if(rtn){
		//操作成功，刷新表格
		vm.query();
		parent.layer.close(index);
	}
}