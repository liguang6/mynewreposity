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

@TableName("SYS_GRID_TEMPLET")
@KeySequence("SYS_GRID_TEMPLET_AUTH")
public class SysTableConfigurationEntity implements Serializable {

    @TableId(value = "grid_Id", type = IdType.INPUT)
    private Long gridId;
    /**
     * 角色ID
     */
    @NotBlank(message = "表格编号不能为空")
    private Long gridNo;

    @NotBlank(message = "表格名字不能为空")
    private String gridName;

    private String remark;



	public Long getGridId() {
		return gridId;
	}



	public void setGridId(Long gridId) {
		this.gridId = gridId;
	}



	public Long getGridNo() {
		return gridNo;
	}



	public void setGridNo(Long gridNo) {
		this.gridNo = gridNo;
	}



	public String getGridName() {
		return gridName;
	}



	public void setGridName(String gridName) {
		this.gridName = gridName;
	}



	public String getRemark() {
		return remark;
	}


	public void setRemark(String remark) {
		this.remark = remark;
	}

}
