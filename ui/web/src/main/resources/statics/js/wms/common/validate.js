/**
 * JS验证
 */
var validateFun = function () { 
	return {
		
		/**
		 * 验证仓库是否存在
		 */
		validateWhNumber:function(whNumber) {
			var results = false;
			var data = {
				"whNumber": whNumber
			};
			$.ajax({
				url: baseURL + "common/getWhList",
				async: false,
				dataType: "json",
				type: "post",
				data: data,
				success:function(response){
					var whlist = response.list;
					$.each(whlist, function (index, value) {
					    if (value.WHNUMBER == whNumber){
							results = true;
						}
					})
				}
			})
			return results;
		},
		
		/**
		 * 验证工厂是否存在
		 */
		validateWerks:function(werks) {
			var results = false;
			var data = {
				"werks": werks
			};
			$.ajax({
				url: baseURL + "common/getPlantList",
				async: false,
				dataType: "json",
				type: "post",
				data: data,
				success:function(response){
					var werklist = response.list;
					$.each(werklist, function (index, value) {
					    if (value.WERKS == werks){
							results = true;
						}
					})
				}
			})
			return results;
		},
	};
}; 