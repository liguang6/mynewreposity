package com.byd.web.sys.entity;


import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import net.bytebuddy.implementation.bind.annotation.Default;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

@TableName("SYS_GRID_COLUMN")
@KeySequence("SYS_GRID_COLUMN_AUTH")
public class SysColumnConfigurationEntity implements Serializable {

    @TableId(value = "column_Id", type = IdType.INPUT)
    private Long columnId;
    /**
     * 角色ID
     */ 
    @NotBlank(message = "列编号不能为空")
    private String columnNo;

    @NotBlank(message = "列名称不能为空")
    private String columnName;
    
    @NotBlank(message = "归属表不能为空")
    private Long  gridId;

//    @NotBlank(message = "是否可以隐藏不能为空")
    private String hideRmk;
    
//    @NotBlank(message = "默认是否隐藏不能为空")
    private String defHide;
    
    @NotBlank(message = "默认顺序不能为空")
    private Long idxSeq;
    
    @NotBlank(message = "宽度不能为空")
    private Long columnWidth;

   
	public Long getcolumnId() {
		return columnId;
	}


	public void setcolumnId(Long columnId) {
		this.columnId = columnId;
	}




	public String getColumnNo() {
		return columnNo;
	}


	public void setColumnNo(String columnNo) {
		this.columnNo = columnNo;
	}


	public String getcolumnName() {
		return columnName;
	}


	public void setcolumnName(String columnName) {
		this.columnName = columnName;
	}


	public String getHideRmk() {
		return hideRmk;
	}


	public void setHideRmk(String hideRmk) {
		this.hideRmk = hideRmk;
	}


	public String getDefHide() {
		return defHide;
	}


	public void setDefHide(String defHide) {
		this.defHide = defHide;
	}




	public Long getGridId() {
		return gridId;
	}


	public void setGridId(Long gridId) {
		this.gridId = gridId;
	}


	public Long getIdxSeq() {
		return idxSeq;
	}


	public void setIdxSeq(Long idxSeq) {
		this.idxSeq = idxSeq;
	}


	public Long getColumnWidth() {
		return columnWidth;
	}


	public void setColumnWidth(Long columnWidth) {
		this.columnWidth = columnWidth;
	}



}
