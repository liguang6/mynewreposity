package com.byd.admin.modules.sys.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@TableName("SYS_AUTH_OBJECT")
@KeySequence("SEQ_SYS_AUTH_OBJECT")
public class SysDataPermissionObjectEntity implements Serializable {

    @TableId(value = "ID", type = IdType.INPUT)
    private Long id;
    /**
     * 菜单ID
     */
    @NotBlank(message = "菜单id不能为空")
    private Long menuId;

    @NotBlank(message = "权限字段不能为空")
    private String authFields;

    @NotBlank(message = "权限字段名不能为空")
    private String authName;


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

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public String getAuthFields() {
        return authFields;
    }

    public void setAuthFields(String authFields) {
        this.authFields = authFields;
    }


    public String getAuthName() {
        return authName;
    }

    public void setAuthName(String authName) {
        this.authName = authName;
    }

    @Override
    public String toString() {
        return "SysDataPermissionObjectEntity{" +
                "id=" + id +
                ", menuId=" + menuId +
                ", authFields='" + authFields + '\'' +
                ", authName='" + authName + '\'' +
                ", del='" + del + '\'' +
                '}';
    }
}
