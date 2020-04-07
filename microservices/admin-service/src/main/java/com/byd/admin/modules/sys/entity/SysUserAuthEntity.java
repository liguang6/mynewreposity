package com.byd.admin.modules.sys.entity;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import io.swagger.annotations.ApiModelProperty;

@TableName("SYS_USER_AUTH")
@KeySequence("SEQ_SYS_USER_AUTH")
public class SysUserAuthEntity implements Serializable {

	@TableId(value = "ID", type = IdType.INPUT)
    private Long id;
	
	@NotBlank(message = "权限字段不能为空")
    private String authFields;

    @NotBlank(message = "权限值不能为空")
    private String authValue;


    @ApiModelProperty(value = "用户id")
    @TableField("user_id")
    private String userId;

    @NotBlank(message = "菜单id不能为空")
    @ApiModelProperty(value = "菜单id")
    @TableField("menu_id")
    private Long menuId;
    
  //@Value("0")
    private String del;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAuthFields() {
		return authFields;
	}

	public void setAuthFields(String authFields) {
		this.authFields = authFields;
	}

	public String getAuthValue() {
		return authValue;
	}

	public void setAuthValue(String authValue) {
		this.authValue = authValue;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Long getMenuId() {
		return menuId;
	}

	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}

	public String getDel() {
		return del;
	}

	public void setDel(String del) {
		this.del = del;
	}
    
    
}
