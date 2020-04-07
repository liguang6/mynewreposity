var selectData = {}, selectNum = 0,
dataGrid = $('#dataGrid').dataGrid({
	searchForm: $("#searchForm"),
	showRownum:false,
	showCheckbox:true,
	multiboxonly: false, // 单击复选框时再多选
	dataId:'userId',
	rowNum: 10000000,//ALL，显示所有带出已分配用户信息
	rowList : [15,30,50,100],
	columnModel: [
		{header:'登录账号', name:'staffNumber',  width:100, align:"center"},
		{header:'用户昵称', name:'username', width:100, align:"center"},
		{header:'部门', name:'deptName', width:100, align:"center"},
		{header:'电子邮箱', name:'email',width:100, align:"center"},
		{header:'手机号码', name:'mobile', width:100, align:"center"},
		{header:'更新时间', name:'updateDate', width:100, align:"center"},
		{header:'状态', name:'workingStatus', width:100, align:"center",formatter:function(val, obj, row, act){
			var html=val||"";
			if(val=="在职"){
				html="<span class=\"label label-success\">"+val+"</span>";				
			}
			if(val=="离职"){
				html="<span class=\"label label-danger\">"+val+"</span>";				
			}
			return html;
		}},
		{header:'行数据', name:'rowData', hidden:true, formatter: function(val, obj, row, act){
			return JSON.stringify(row);
		}}
	],
	autoGridHeight: function(){
		var height = $(window).height() - $('#searchForm').height() - $('#dataGridPage').height() - 73;
		$('.tags-input').height($('.ui-jqgrid').height() - 10);
		return height;
	},
	ajaxSuccess: function(data){
		//alert(data)
		if($("#selectUsers").val().trim().length>0){
			data=$("#selectUsers").val().split(",")
		}	
		//console.info(data)
		$.each(data, function(key, value){
			dataGrid.dataGrid('setSelectRow', value);

			//根据ID获取用户信息
			$.ajax({
				type: "POST",
				url: baseURL + "sys/user/info/"+value,
				contentType: "application/json",
				async: false,
				success: function(r){
					if(r.code == 0){
						var user = r.user;
						user['id'] = user.userId;
						selectData[user.id] = user;
					}
				}
			});

		});
		initSelectTag();
	},
	onSelectRow: function(id, isSelect, event){
		//console.info("isSelect:"+isSelect)
		//console.info("id:"+id)
		if(isSelect){
			selectData[id] = JSON.parse(dataGrid.dataGrid('getRowData', id).rowData);
		}else{
			 delete selectData[id]
		}
		initSelectTag();
	},
	onSelectAll: function(ids, isSelect){
		for (var i=0; i<ids.length; i++){
			if(isSelect){
				selectData[ids[i]] = JSON.parse(dataGrid.dataGrid('getRowData', ids[i]).rowData);
			}else{
				delete selectData[ids[i]];
			}
		}
		initSelectTag();
	}

});


function initSelectTag(){
	//console.info("selectData:"+JSON.stringify(selectData))
	selectNum = 0;
	var html = [];
	$.each(selectData, function(key, value){
		selectNum ++;
		html.push('<span class="tag" id="'+key+'_tags-input"><span>'+value.username+' </span>'
			+ '<a href="#" onclick="removeSelectTag(\''+key+'\');" title="取消选择">x</a></span>');
	});
	html.unshift('<div class="title">当前已选择<span id="selectNum">'+selectNum+'</span>项：</div>');
	$('#selectData').empty().append(html.join(''));
}
function removeSelectTag(key){
	delete selectData[key];
	dataGrid.dataGrid('resetSelection', key);
	$('#selectNum').html(--selectNum);
	$('#'+key+'_tags-input').remove();
}

function getSelectData(){
	return selectData;
}