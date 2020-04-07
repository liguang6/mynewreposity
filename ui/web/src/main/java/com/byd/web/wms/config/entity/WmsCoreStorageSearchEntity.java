package com.byd.web.wms.config.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;


/**
 * @ClassName WmsCSearchSequenceEntity
 * @Description  分配存储类型至搜索顺序配置实体类
 * @Author qiu.jiaming1
 * @Date 2019/2/27
 */
@TableName("WMS_C_STORAGE_SEARCH")
@KeySequence("SEQ_WMS_C_STORAGE_SEARCH")//使用oracle 注解自增
public class WmsCoreStorageSearchEntity implements Serializable {
	@TableId(value="ID",type= IdType.INPUT)
	private Long id;
	@NotBlank(message = "仓库编号不能为空")
	private String warehouseCode;
	private String factoryCode;
	@NotBlank(message = "存储类型搜索顺序不能为空")
	private String storageAreaSearch;
	private String priority;
	@NotBlank(message = "存储类型代码不能为空")
	private String storageAreaCode;
	private String status;
	private String creator;
	private String createDate;
	private String editor;
	private String editDate;
	private String del;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getWarehouseCode() {
		return warehouseCode;
	}

	public void setWarehouseCode(String warehouseCode) {
		this.warehouseCode = warehouseCode;
	}

	public String getFactoryCode() {
		return factoryCode;
	}

	public void setFactoryCode(String factoryCode) {
		this.factoryCode = factoryCode;
	}

	public String getStorageAreaSearch() {
		return storageAreaSearch;
	}

	public void setStorageAreaSearch(String storageAreaSearch) {
		this.storageAreaSearch = storageAreaSearch;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getStorageAreaCode() {
		return storageAreaCode;
	}

	public void setStorageAreaCode(String storageAreaCode) {
		this.storageAreaCode = storageAreaCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getEditor() {
		return editor;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}

	public String getEditDate() {
		return editDate;
	}

	public void setEditDate(String editDate) {
		this.editDate = editDate;
	}

	public String getDel() {
		return del;
	}

	public void setDel(String del) {
		this.del = del;
	}

	@Override
	public String toString() {
		return "WmsCoreStorageSearchEntity{" +
				"id=" + id +
				", warehouseCode='" + warehouseCode + '\'' +
				", factoryCode='" + factoryCode + '\'' +
				", storageAreaSearch='" + storageAreaSearch + '\'' +
				", priority='" + priority + '\'' +
				", storageAreaCode='" + storageAreaCode + '\'' +
				", status='" + status + '\'' +
				", creator='" + creator + '\'' +
				", createDate='" + createDate + '\'' +
				", editor='" + editor + '\'' +
				", editDate='" + editDate + '\'' +
				", del='" + del + '\'' +
				'}';
	}
}
