/*
 * 订单编号模糊查询 submitId： 用于提交的元素的id
 * werks：为元素ID，只能动态获取值，否则调用多次时都是取第一次传入的固定值
 */
function getBjOrderNoSelect(elementId, submitId, fn_backcall, werks) {
	$(submitId).val("");
	var order={};
	var orderlist;
	// alert($(elementId).next().html())
	$(elementId).typeahead({		
		source : function(input, process) {
			var data={
					"search_order":input,
					"search_werks":$(werks).val()||''
			};
			
			$.ajax({
				url:baseURL+"bjmes/common/getOrderList",
				dataType : "json",
				type : "post",
				data : data,
				async: false,
				success: function (response) { 
					orderlist = response.page.list;
					var results = new Array();
					$.each(orderlist, function(index, value) {
						//console.log(value.order_no);
						results.push(value.order_no);
					})
					return process(results);
				}
			});
		},
		items : 20,
		highlighter : function(item) {
			var order_name = "";
			var bus_type = "";
			var order_qty = "";
			$.each(orderlist, function(index, value) {
				if (value.order_no == item) {
					order_name = value.order_name;
					bus_type = value.internal_name;
					order_qty = value.order_qty + "台";
				}
			})
			return item + "  " + order_name + " " + bus_type + order_qty;
		},
		matcher : function(item) {
			return true;
		},
		updater : function(item) {
			return item;
		},
		afterSelect:function(item){
			$.each(orderlist, function(index, value) {
				if (value.order_no == item) {
					order=value;
					selectId = value.id;
					$(elementId).attr("orderId", selectId);
					$(submitId).val(value.order_no)
					$(elementId).attr("orderQty", value.order_qty);
					$(elementId).attr("busTypeCode",value.bus_type_code);				
				}
			})
			if (typeof (fn_backcall) == "function") {
				fn_backcall(order);
			}
		}
	});
}
//产品库
function getBjProductNoSelect(submitId, fn_backcall) {
	$(submitId).val("");
	var product={};
	var productlist;
	
	$(submitId).typeahead({		
		source : function(input, process) {
			var data={
					"product_code":input
			};
			
			$.ajax({
				url:baseURL+"config/bjMesProducts/getList",
				dataType : "json",
				type : "post",
				data : data,
				async: false,
				success: function (response) { 
					productlist = response.data;
					var results = new Array();
					$.each(productlist, function(index, value) {
						results.push(value.product_code);
					})
					return process(results);
				}
			});
		},
		items : 20,
		highlighter : function(item) {
			var product_name = "";
			$.each(productlist, function(index, value) {
				if (value.product_code == item) {
					product_name = value.product_name;
				}
			})
			return item + "  " + product_name;
		},
		matcher : function(item) {
			return true;
		},
		updater : function(item) {
			return item;
		},
		afterSelect:function(item){
			$.each(productlist, function(index, value) {
				if (value.product_code == item) {
					product=value;
					$(submitId).val(value.product_code)				
				}
			})
			if (typeof (fn_backcall) == "function") {
				fn_backcall(product);
			}
		}
	});
}

//生产产品信息列表
function getBjPDProductSelect(submitId, fn_backcall) {
	$(submitId).val("");
	var product={};
	var productlist;
	
	$(submitId).typeahead({		
		source : function(input, process) {
			var data={
					"product_no":input
			};
			
			$.ajax({
				url:baseURL+"bjmes/product/pe/getPDProductList",
				dataType : "json",
				type : "post",
				data : data,
				async: false,
				success: function (response) { 
					productlist = response.data;
					var results = new Array();
					$.each(productlist, function(index, value) {
						results.push(value.product_no);
					})
					return process(results);
				}
			});
		},
		items : 20,
		highlighter : function(item) {
			var product_name = "";
			$.each(productlist, function(index, value) {
				if (value.product_no == item) {
					product_name = value.product_name;
				}
			})
			return item + "  " + product_name;
		},
		matcher : function(item) {
			return true;
		},
		updater : function(item) {
			return item;
		},
		afterSelect:function(item){
			$.each(productlist, function(index, value) {
				if (value.product_no == item) {
					product=value;
					$(submitId).val(value.product_no)				
				}
			})
			if (typeof (fn_backcall) == "function") {
				fn_backcall(product);
			}
		}
	});
}