package com.byd.wms.business.modules.out.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/**
 * @auther: peng.yang8@byd.com
 * @data: 2019/12/19
 * @description：
 */

@TableName("WMS_OUT_REQUIREMENT_LABEL")
@KeySequence("SEQ_WMS_OUT_REQUIREMENT_LABEL")
public class WmsOutRequirementLabelEntity {


    /**
     * ID
     */
    @TableId(value="ID",type= IdType.INPUT)
    private Long id;
    /**
     * 需求号
     */
    private String requirementNo;
    /**
     * 需求行项目号
     */
    private String requirementItemNo;
    /**
     * 标签号
     */
    private String labelNo;
    /**
     * 记录修改人
     */
    private String editor;
    /**
     * 记录修改时间
     */
    private String editDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequirementNo() {
        return requirementNo;
    }

    public void setRequirementNo(String requirementNo) {
        this.requirementNo = requirementNo;
    }

    public String getRequirementItemNo() {
        return requirementItemNo;
    }

    public void setRequirementItemNo(String requirementItemNo) {
        this.requirementItemNo = requirementItemNo;
    }

    public String getLabelNo() {
        return labelNo;
    }

    public void setLabelNo(String labelNo) {
        this.labelNo = labelNo;
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

}
