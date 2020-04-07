/*
 * 本软件为比亚迪股份有限公司开发研制。未经本公司正式书面授权，其他任何个人、团体不得使用、复制、修改或发布本软件。
 * CopyRight © BYD Company Limited. All rights reserved.
 */
package com.byd.utils.mail;

import com.byd.utils.mail.entity.Mail;
import org.apache.commons.lang.StringUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 邮件相关工具类
 *
 * @author user
 */
public class MailUtils {

    /**
     * 获取邮件属性
     *
     * @return
     */
    public final static Properties getProperties() throws IOException {
        Properties prop = new Properties();
        //读取邮件配置属性，如需修改，请修改MAIL.properties文件
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("MAIL.properties");
        prop.load(is);
        return prop;
    }

    /**
     * 创建邮件
     *
     * @param session
     * @param mail 邮件属性
     * @return 邮件
     * @throws Exception
     */
    public final static MimeMessage createMai(Session session, Mail mail) throws Exception {
        //创建邮件
        MimeMessage message = new MimeMessage(session);
        //指明发件人
        message.setFrom(new InternetAddress(mail.getSendAddr()));
        //接收人
        if(mail.getToAddr() == null || mail.getToAddr().length == 0){
            throw new Exception("收件人邮箱为空");
        }
        String[] toAddr = mail.getToAddr();
        if (toAddr != null && toAddr.length > 0) {
            Address[] toAddress = new Address[toAddr.length];
            for (int i = 0; i < toAddr.length; i++) {
                toAddress[i] = new InternetAddress(toAddr[i]);
            }
            //指明接收人
            message.setRecipients(Message.RecipientType.TO, toAddress);
        }
        //抄送
        String[] ccAddr = mail.getCcAddr();
        if (ccAddr != null && ccAddr.length > 0) {
            Address[] ccAddress = new Address[ccAddr.length];
            for (int i = 0; i < ccAddr.length; i++) {
                ccAddress[i] = new InternetAddress(ccAddr[i]);
            }
            //指明抄送
            message.setRecipients(Message.RecipientType.CC, ccAddress);
        }
        //邮件标题
        message.setSubject(mail.getHead());
        //描述数据关系
        MimeMultipart mmp = new MimeMultipart();
        //准备邮件正文数据
        MimeBodyPart mbp = new MimeBodyPart();
        mbp.setContent(mail.getMessage(), "text/html;charset=utf-8");
        mmp.addBodyPart(mbp);
        //文件数据
        List<Map<String, InputStream>> fileList = mail.getFileList();
        if (fileList != null && fileList.size() > 0) {
            for (int i = 0; i < fileList.size(); i++) {
                Map.Entry<String, InputStream> fileMap = fileList.get(i).entrySet().iterator().next();
                mbp = new MimeBodyPart();
                DataSource dataSource = new ByteArrayDataSource(fileMap.getValue(), "application/octet-stream");
                DataHandler dh = new DataHandler(dataSource);
                mbp.setDataHandler(dh);
                //加上这句将作为附件发送，否则将作为邮件的正文发送（必须）
                mbp.setFileName(fileMap.getKey());
                mmp.addBodyPart(mbp);
            }
        }
        //这据很重要，不要忘了
        mmp.setSubType("related");
        message.setContent(mmp);
        message.saveChanges();
        return message;
    }

    /**
     * 邮件发送
     * @param mail 邮件内容
     * @throws Exception  异常
     */
    public final static void mainSend(Mail mail) throws Exception {
        Properties prop = MailUtils.getProperties();
        //1、创建Session
        Session session = Session.getInstance(prop);
        //session的Debug模式 开发环境设置为true，正式环境为false
        session.setDebug(false);
        //发件人邮箱
        String sendAddr = prop.getProperty("sendEmial");
        //发件人密码
        String password = prop.getProperty("password");
        //2、通过session创建
        Transport ts = session.getTransport();
        //3、用户认证 邮件服务器IP 端口 用户名 密码
        ts.connect(prop.getProperty("mail.service.ip"), Integer.parseInt(prop.getProperty("mail.service.port")), sendAddr, password);
        //判断发件人邮箱是否为空
        if(StringUtils.isEmpty(mail.getSendAddr())){
            mail.setSendAddr(sendAddr);
        }
        if(mail.getToAddr() == null || mail.getToAddr().length == 0){
            throw new Exception("收件人邮箱为空");
        }
        //4、创建邮件
        MimeMessage mm = MailUtils.createMai(session, mail);
        //发送
        ts.sendMessage(mm, mm.getAllRecipients());
        ts.close();
    }
    
/*    *//**
     * 邮件测试
     * @param args 
     *//*
    public static void main(String[] args){
        try{
            //收件人
            String[] ttAddr = {"zhao.zhengwen@byd.com"};
            //抄送
            String[] ccAddr = {};
            Mail mail = new Mail("邮件标题",ttAddr,ccAddr,"邮件内容");
            mainSend(mail);
        }catch(Exception e){
            e.printStackTrace();
        }
    }*/
}
