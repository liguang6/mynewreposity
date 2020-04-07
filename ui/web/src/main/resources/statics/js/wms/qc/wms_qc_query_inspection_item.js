	var vm;

	var inspectionNo = $("#inspectionNo").val();
	vm = new Vue({
		el:"#vue-app",
		data:{tpl:{},inspectionItems:[],inspectionList:''},
		created:function(){
			/*$.post(baseUrl +"qcQuery/inspectionItem",{"inspectionNo":inspectionNo},function(resp){
				this.inspectionItems = resp.list;			
			});*/
			this.inspectionItems=ajaxGetInspectionItem(inspectionNo);
			this.tpl =ajaxGetInspectionTpl(inspectionNo);
			var tpl=this.tpl;
			console.info(JSON.stringify(tpl))
			if(this.tpl.INSPECTION_STATUS === '00'){
				this.tpl.inspectionStatusName =  "创建";
			}
			if(this.tpl.INSPECTION_STATUS === '01'){
				this.tpl.inspectionStatusName =  "部分完成";
			}
			if(this.tpl.INSPECTION_STATUS === '02'){
				this.tpl.inspectionStatusName =  "全部完成";
			}
			if(this.tpl.INSPECTION_STATUS === '03'){
				this.tpl.inspectionStatusName =  "关闭";
			}	
			var INSPECTION_TYPE=this.tpl.INSPECTION_TYPE;	
		    
			var inspectionList=[];
			$.each(this.inspectionItems,function(i,v){
				var item={};
				item.INSPECTION_NO=inspectionNo;
				item.WERKS=v.werks;
				item.INSPECTION_TYPE=INSPECTION_TYPE;
				item.CREATOR=v.creator;
				item.CREATE_DATE=v.createDate;
				item.BATCH=v.batch;
				item.MATNR=v.matnr;
				item.MAKTX=v.maktx;
				item.INSPECTION_QTY=v.inspectionQty;
				item.BEDNR=v.bednr;
				item.LIFNR=v.lifnr;
				item.LIKTX=v.liktx;
				item.UNIT=v.unit;
				
				inspectionList.push(item);
			})
			this.inspectionList=JSON.stringify(inspectionList);
		},
		methods:{
			print : function() {
				
			}
		}
	});

	function ajaxGetInspectionItem(inspectionNo){
		var list=[];
		$.ajax({
			url:baseUrl +"qcQuery/inspectionItem",
			data:{"inspectionNo":inspectionNo},
			dataType:'json',
			method:'post',
			async:false,
			success:function(resp){
				 list=resp.list;
			}			
		})
		return list;
	}
	
	function ajaxGetInspectionTpl(inspectionNo){
		var value={};
		$.ajax({
			url:baseUrl +"qcQuery/inspectionList",
			data:{"inspectionNo":inspectionNo},
			dataType:'json',
			method:'post',
			async:false,
			success:function(resp){
				 value=resp.page.list[0];
			}			
		})
		return value;
	}
