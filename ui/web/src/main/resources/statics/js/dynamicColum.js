/**
 * 列动态显示 defaultHideColums : 默认隐藏的列 tableSelector: jqGrid 表格选择器 mutiSelector:
 * mutiselect 下拉框选择器 title： 下拉菜单标题
 * 
 * useage: 
 * 控件：Bootstrap Multiselect
 * 
 * 1.导入依赖 ，js,css 放到最后导入
 * 
 * <link rel="stylesheet"
 * href="${request.contextPath}/statics/js/plugins/bootstrap-multiselect-master/dist/css/bootstrap-multiselect.css"
 * type="text/css"/> <script
 * src="${request.contextPath}/statics/js/dynamicColum.js?_${.now?long}"></script>
 * <script
 * src="${request.contextPath}/statics/js/plugins/bootstrap-multiselect-master/dist/js/bootstrap-multiselect.js?_${.now?long}"></script>
 * 
 * 2.在页面上创建一个下拉框，value对应jqgrid字段名 
 * 
 * <!-- Build your select: --> 
 * <select
 * id="example-getting-started" multiple="multiple"> <option
 * value="cheese">Cheese</option> <option value="tomatoes">Tomatoes</option>
 * <option value="mozarella">Mozzarella</option> <option
 * value="mushrooms">Mushrooms</option> <option value="pepperoni">Pepperoni</option>
 * <option value="onions">Onions</option> </select>
 * 
 * 3.页面加载完后调用 DynamicColume()函数
 * 
 * 
 * TIP: DynamicColume 需要在 JQuery加载完后调, $(fucntion(){DynamicColume()});
 */


function DynamicColume(defaultHideColums,title="表格列",tableSelector = '#dataGrid',mutiSelector  = '#example-getting-started'){
	var _dynamicColume = new Object();
	_dynamicColume.hideColumes;
	_dynamicColume.showCol;
	_dynamicColume.allShowColumes;
	//设置隐藏 显示字段
	_dynamicColume.setingHiddenColume = function (){
		for(var i in _dynamicColume.hideColumes)
			$(tableSelector).setGridParam().hideCol(_dynamicColume.hideColumes[i]);
		
		if(_dynamicColume.showCol !== null)
			$(tableSelector).setGridParam().showCol(_dynamicColume.showCol);
    };
    //下拉多选插件点击事件
    _dynamicColume.onColumeHideClick = function (_hideColms, _showCol) {
    	// 找出没有被选中的列名
    	_dynamicColume.hideColumes = _dynamicColume.allShowColumes.filter(function(val) {
    		for (var i = 0; _hideColms !== null && i < _hideColms.length; i++) {
    			if (val === _hideColms[i])
    				return false;
    		}
    		return true;
    	});
    	_dynamicColume.showCol = _showCol;
    	_dynamicColume.setingHiddenColume();
    };
    //设置下拉菜单，设置dataGrid加载数据回调函数
    _dynamicColume.setDynamicColume = function (){
		$(mutiSelector).multiselect({
	        onChange: function(option, checked, select) {
	            var _cols = $(mutiSelector).val();
	            var _showCol = checked?$(option).val():null;//如果列从没被选中到被选中，那这一列要从隐藏变成显示
	            _dynamicColume.onColumeHideClick(_cols,_showCol);
	        },
	        buttonText: function(options, select) {
	            return title;
	        },
	        buttonWidth: '80px',
	        selectedClass: 'multiselect-selected',
	        buttonClass: 'btn btn-primary btn-sm'
	    });
		//--调用bootstrap-mutiselect插件实现列动态显示 ,默认选中所有 .
		_dynamicColume.allShowColumes = $(mutiSelector).multiselect("selectAll",false).val();
		//设置默认隐藏的列
		if(defaultHideColums != undefined){
			$(mutiSelector).multiselect('deselect',defaultHideColums);
			_dynamicColume.hideColumes = defaultHideColums;
			_dynamicColume.setingHiddenColume();
		}
		$(tableSelector).dataGrid({ajaxSuccess: function(data){_dynamicColume.setingHiddenColume();}});//jqGrid请求服务器数据成功后执行，隐藏列
    };
    _dynamicColume.setDynamicColume();
    return _dynamicColume;
}
