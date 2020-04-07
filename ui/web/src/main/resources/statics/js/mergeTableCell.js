/**
 * @result 表格数据
 * @columns 列对象数组
 * @rowsGroup 需要合并单元格的列下标数组
 */
onmessage =function(event){
	var result=event.data.result;
	var columns=event.data.columns
	var rowsGroup=event.data.rowsGroup
	
	
	var ele_ids=[];
	//$(table_head).appendTo(table);
	/**
	 * 创建table body
	 */
	
	var trs=[];
	
	for(var i=0;i<result.length;i++){
		var tr=[];
		var data=result[i];
		for(var j=0;j<columns.length;j++){	
			var column=columns[j];
			var td={};
			td.id="td_"+i+"_"+j;				
			td.class=column.class;
			td.rowspan=1;
			td.hidden=false;
			td.width=column.width;
			td.html=data[column.data];
			tr.push(td);
		}
		
		trs.push(tr);
				
		
		if(i==0){
			for(var j=0;j<rowsGroup.length;j++){
				ele_ids.push("td_"+i+"_"+rowsGroup[j]);
			}
		}else{
			for(var j=0;j<rowsGroup.length;j++){	
				var index=rowsGroup[j];
				var column=columns[index];
				var cell_data=data[column.data];//需要合并的列当前行中的值
				var cell_data_lst=result[i-1][column.data];//需要合并的列上一行中的值
				
				if(cell_data==cell_data_lst){//相同值需要合并					
					var flag=true;
					
					for(var jj=j-1;jj>=0;jj--){
						var ii=rowsGroup[jj];
						var column_last=columns[ii]//前一个合并列
						var cell_data_clst=data[column_last.data];//前一个合并列单元格的值
						var cell_data_clst_lst=result[i-1][column_last.data];//前一个合并列上一行对应的单元格的值
						if(cell_data_clst!=cell_data_clst_lst){
							flag=false;
							break;
						}
					}
					
					if(flag){
						
						var tr_del=trs[i];
						var td_del=tr_del[index];
						//trs[i].splice(index,1)
						trs[i][index].hidden=true;
						
						var td_id=ele_ids[j];
						var i_merge=parseInt(td_id.split("_")[1]);
						var j_merge=parseInt(td_id.split("_")[2])
						var tr_merge=trs[i_merge];
						var td_merge=tr_merge[j_merge];
						var rowspan=parseInt(td_merge.rowspan);
						//alert($(td_merge).html())
						td_merge.rowspan=rowspan+1;
						//trs[i][j_merge].rowspan=rowspan+1;
					}else{
						if(index>=0){
							ele_ids[j]="td_"+i+"_"+rowsGroup[j];
						}						
					}
					
					
				}else{					
					if(index==0){
						ele_ids=[];
						for(var j=0;j<rowsGroup.length;j++){
							ele_ids.push("td_"+i+"_"+rowsGroup[j]);
						}
					}
					if(index>0){
						ele_ids[j]="td_"+i+"_"+rowsGroup[j];
					}
				}							
			}
			
			
		}

	}
	
	
	postMessage(trs);
	
	
}

