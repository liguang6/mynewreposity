/*
 * 本软件为比亚迪股份有限公司开发研制。未经本公司正式书面授权，其他任何个人、团体不得使用、复制、修改或发布本软件。
 * CopyRight © BYD Company Limited. All rights reserved.
 */

package com.byd.utils.mail.entity;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 邮件参数
 * @author user
 */
public class Mail {
    
    private String head;//标题 [必须]
    private String sendAddr;//发件人邮箱 [可选]
    private String[] toAddr;//收件人邮箱 [必须]
    private String[] ccAddr;//抄送 [可选]
    private String message;//正文内容(支持HTML) [必须]
    
    private List<Map<String,InputStream>> fileList;//附件  文件名=文件流 [可选]
    
    public Mail(){
    
    }
    
    public Mail(String head,String[] toAddr,String[] ccAddr,String message){
        this.head = head;
        this.toAddr = toAddr;
        this.ccAddr = ccAddr;
        this.message = message;
    }
    
    public Mail(String head,String sendAddr,String[] toAddr,String[] ccAddr,String message){
        this.head = head;
        this.sendAddr = sendAddr;
        this.toAddr = toAddr;
        this.ccAddr = ccAddr;
        this.message = message;
    }
    
    public Mail(String head,String sendAddr,String[] toAddr,String[] ccAddr,String message,List<Map<String,InputStream>> fileList){
        this.head = head;
        this.sendAddr = sendAddr;
        this.toAddr = toAddr;
        this.ccAddr = ccAddr;
        this.message = message;
        this.fileList = fileList;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getSendAddr() {
        return sendAddr;
    }

    public void setSendAddr(String sendAddr) {
        this.sendAddr = sendAddr;
    }

    public String[] getToAddr() {
        return toAddr;
    }

    public void setToAddr(String[] toAddr) {
        this.toAddr = toAddr;
    }

    public String[] getCcAddr() {
        return ccAddr;
    }

    public void setCcAddr(String[] ccAddr) {
        this.ccAddr = ccAddr;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Map<String, InputStream>> getFileList() {
        return fileList;
    }

    public void setFileList(List<Map<String, InputStream>> fileList) {
        this.fileList = fileList;
    }
    
    
}
