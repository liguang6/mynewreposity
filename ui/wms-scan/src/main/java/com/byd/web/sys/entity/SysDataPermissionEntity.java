package com.byd.web.sys.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@TableName("SYS_ROLE_AUTH")
@KeySequence("SEQ_SYS_ROLE_AUTH")
public class SysDataPermissionEntity implements Serializable {

    @TableId(value = "ID", type = IdType.INPUT)
    private Long id;
    /**
     * 角色ID
     */
    @NotBlank(message = "角色id不能为空")
    private Long roleId;

    @NotBlank(message = "权限字段不能为空")
    private String authFields;

    @NotBlank(message = "权限值不能为空")
    private String authValue;


    @ApiModelProperty(value = "用户id")
    @TableField("user_id")
    private Long userId;

    @NotBlank(message = "菜单id不能为空")
    @ApiModelProperty(value = "菜单id")
    @TableField("menu_id")
    private Long menuId;

    @TableField(exist=false)
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String key, String status) {
        this.status = status;
    }

    //@Value("0")
    private String del;

    public String getDel() {
        return del;
    }

    public void setDel(String del) {
        this.del = del;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    @Override
    public String toString() {
        return "SysDataPermissionEntity{" +
                "id=" + id +
                ", roleId=" + roleId +
                ", authFields='" + authFields + '\'' +
                ", authValue='" + authValue + '\'' +
                ", userId=" + userId +
                ", menuId=" + menuId +
                ", del='" + del + '\'' +
                '}';
    }
}
