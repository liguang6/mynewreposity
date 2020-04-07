package com.byd.wms.business.modules.timejob.service.impl;

import com.byd.utils.mail.MailUtils;
import com.byd.utils.mail.entity.Mail;
import com.byd.wms.business.modules.timejob.dao.EmailSendDao;
import com.byd.wms.business.modules.timejob.entity.WmsEmailAddresseeEntity;
import com.byd.wms.business.modules.timejob.entity.WmsEmailMessageEntity;
import com.byd.wms.business.modules.timejob.service.EmailSendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("EmailSendService")
public class EmailSendServiceImpl implements EmailSendService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private EmailSendDao EmailSendDao;
    /*
      已收货未质检物料通知
         */
	@Override
	public void sendEmail01() {
		List<WmsEmailAddresseeEntity> WmsEmailAddresseeEntitys=EmailSendDao.queryUnQualityMessage();
        try{
            for(WmsEmailAddresseeEntity wmsEmailAddresseeEntity:WmsEmailAddresseeEntitys) {
                //收件人
                String[] ttAddr = {wmsEmailAddresseeEntity.getAddressee()};
                String noticeType = wmsEmailAddresseeEntity.getNoticeType();
                if(noticeType.equals("2")){
                //抄送
                    String[] ccAddr = {};
                    StringBuilder content = new StringBuilder("<html><head></head><body><h3>各位好，<h3>\n" );
                    content.append("<h3>以下物料已收货，请IQC及时处理，谢谢！<h3>\n");
                    content.append("<h3>【系统邮件请勿回复】</h3>\n");
                    content.append("<table border=\"1\" style=\"border:solid 1px #E8F2F9;font-size=14px;;font-size:18px;\">");
                    content.append("<tr style=\"background-color: #428BCA; color:#ffffff\"><th>工厂</th><th>仓库号</th><th>物料</th><th>物料描述</th><th>批次</th><th>数量</th><th>单位</th><th>供应商</th><th>供应商名称</th><th>收货日期</th></tr>");
                    for (WmsEmailMessageEntity wmsEmailMessageEntity:wmsEmailAddresseeEntity.getWmsEmailMessageEntity()) {
                        content.append("<tr>");
                        content.append("<td>" + wmsEmailMessageEntity.getWerks() + "</td>"); //工厂
                        content.append("<td>" + wmsEmailMessageEntity.getWhNumber() + "</td>"); //仓库号
                        content.append("<td>" + wmsEmailMessageEntity.getMatnr() + "</td>"); //物料
                        content.append("<td>" + wmsEmailMessageEntity.getMaktx() + "</td>"); //物料描述
                        content.append("<td>" + wmsEmailMessageEntity.getBatch() + "</td>"); //批次
                        content.append("<td>" + wmsEmailMessageEntity.getQty() + "</td>"); //数量
                        content.append("<td>" + wmsEmailMessageEntity.getUnit() + "</td>"); //单位
                        content.append("<td>" + wmsEmailMessageEntity.getLifnr() + "</td>"); //供应商
                        content.append("<td>" + wmsEmailMessageEntity.getLiktx() + "</td>"); //供应商名称
                        content.append("<td>" + wmsEmailMessageEntity.getReceiptDate() + "</td>"); //收货日期
                        content.append("</tr>");
                    }
                    content.append("</table>");
                    content.append("</body></html>");
                    Mail mail = new Mail("已收货未质检物料通知", ttAddr, ccAddr, content.toString());
                    MailUtils.mainSend(mail);
                }

            }
        }catch(Exception e){
            e.printStackTrace();
        }
	}
    /*
        已质检未进仓物料通知
         */
    @Override
    public void sendEmail02() {
        List<WmsEmailAddresseeEntity> WmsEmailAddresseeEntitys=EmailSendDao.queryUnStorageMessage();
        try{
            for(WmsEmailAddresseeEntity wmsEmailAddresseeEntity:WmsEmailAddresseeEntitys) {
                //收件人
                String[] ttAddr = {wmsEmailAddresseeEntity.getAddressee()};
                String noticeType = wmsEmailAddresseeEntity.getNoticeType();
                if(noticeType.equals("1")){
                    //抄送
                    String[] ccAddr = {};
                    StringBuilder content = new StringBuilder("<html><head></head><body><h3>各位好，<h3>\n" );
                    content.append("<h3>以下物料来料已检验合格，请根据实际需求及时处理，谢谢！<h3>\n");
                    content.append("<h3>【系统邮件请勿回复】</h3>\n");
                    content.append("<table border=\"1\" style=\"border:solid 1px #E8F2F9;font-size=14px;;font-size:18px;\">");
                    content.append("<tr style=\"background-color: #428BCA; color:#ffffff\"><th>工厂</th><th>仓库号</th><th>物料</th><th>物料描述</th><th>批次</th><th>数量</th><th>单位</th><th>供应商</th><th>供应商名称</th><th>收货日期</th><th>质检时间</th><th>质检结果</th></tr>");
                    for (WmsEmailMessageEntity wmsEmailMessageEntity:wmsEmailAddresseeEntity.getWmsEmailMessageEntity()) {
                        content.append("<tr>");
                        content.append("<td>" + wmsEmailMessageEntity.getWerks() + "</td>"); //工厂
                        content.append("<td>" + wmsEmailMessageEntity.getWhNumber() + "</td>"); //仓库号
                        content.append("<td>" + wmsEmailMessageEntity.getMatnr() + "</td>"); //物料
                        content.append("<td>" + wmsEmailMessageEntity.getMaktx() + "</td>"); //物料描述
                        content.append("<td>" + wmsEmailMessageEntity.getBatch() + "</td>"); //批次
                        content.append("<td>" + wmsEmailMessageEntity.getQty() + "</td>"); //数量
                        content.append("<td>" + wmsEmailMessageEntity.getUnit() + "</td>"); //单位
                        content.append("<td>" + wmsEmailMessageEntity.getLifnr() + "</td>"); //供应商
                        content.append("<td>" + wmsEmailMessageEntity.getLiktx() + "</td>"); //供应商名称
                        content.append("<td>" + wmsEmailMessageEntity.getReceiptDate() + "</td>"); //收货日期
                        content.append("<td>" + wmsEmailMessageEntity.getQcDate() + "</td>"); //质检日期
                        content.append("<td>" + wmsEmailMessageEntity.getQcResultText() + "</td>"); //质检结果
                        content.append("</tr>");
                    }
                    content.append("</table>");
                    content.append("</body></html>");
                    Mail mail = new Mail("已质检未进仓物料通知", ttAddr, ccAddr, content.toString());
                    MailUtils.mainSend(mail);
                }

            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void sendEmail03() {
        List<WmsEmailAddresseeEntity> WmsEmailAddresseeEntitys=EmailSendDao.queryUnQualifiedMessage ();
        try{
            for(WmsEmailAddresseeEntity wmsEmailAddresseeEntity:WmsEmailAddresseeEntitys) {
                //收件人
                String[] ttAddr = {wmsEmailAddresseeEntity.getAddressee()};
                String noticeType = wmsEmailAddresseeEntity.getNoticeType();
                if(noticeType.equals("3")){
                    //抄送
                    String[] ccAddr = {};
                    StringBuilder content = new StringBuilder("<html><head></head><body><h3>各位好，<h3>\n" );
                    content.append("<h3>以下物料来料检验不合格，请资源、采购、计划等部门及时处理，勿影响生产作业，谢谢！<h3>\n");
                    content.append("<h3>【系统邮件请勿回复】</h3>\n");
                    content.append("<table border=\"1\" style=\"border:solid 1px #E8F2F9;font-size=14px;;font-size:18px;\">");
                    content.append("<tr style=\"background-color: #428BCA; color:#ffffff\"><th>工厂</th><th>仓库号</th><th>物料</th><th>物料描述</th><th>批次</th><th>数量</th><th>单位</th><th>供应商</th><th>供应商名称</th><th>收货日期</th><th>质检时间</th><th>质检结果</th><th>原因</th></tr>");
                    for (WmsEmailMessageEntity wmsEmailMessageEntity:wmsEmailAddresseeEntity.getWmsEmailMessageEntity()) {
                        content.append("<tr>");
                        content.append("<td>" + wmsEmailMessageEntity.getWerks() + "</td>"); //工厂
                        content.append("<td>" + wmsEmailMessageEntity.getWhNumber() + "</td>"); //仓库号
                        content.append("<td>" + wmsEmailMessageEntity.getMatnr() + "</td>"); //物料
                        content.append("<td>" + wmsEmailMessageEntity.getMaktx() + "</td>"); //物料描述
                        content.append("<td>" + wmsEmailMessageEntity.getBatch() + "</td>"); //批次
                        content.append("<td>" + wmsEmailMessageEntity.getQty() + "</td>"); //数量
                        content.append("<td>" + wmsEmailMessageEntity.getUnit() + "</td>"); //单位
                        content.append("<td>" + wmsEmailMessageEntity.getLifnr() + "</td>"); //供应商
                        content.append("<td>" + wmsEmailMessageEntity.getLiktx() + "</td>"); //供应商名称
                        content.append("<td>" + wmsEmailMessageEntity.getReceiptDate() + "</td>"); //收货日期
                        content.append("<td>" + wmsEmailMessageEntity.getQcDate() + "</td>"); //质检日期
                        content.append("<td>" + wmsEmailMessageEntity.getQcResultText() + "</td>"); //质检结果
                        content.append("<td>" + wmsEmailMessageEntity.getQcResult() + "</td>"); //原因
                        content.append("</tr>");
                    }
                    content.append("</table>");
                    content.append("</body></html>");
                    Mail mail = new Mail("IQC来料检验不合格物料通知", ttAddr, ccAddr, content.toString());
                    MailUtils.mainSend(mail);
                }

            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void sendEmail04() {
        List<WmsEmailAddresseeEntity> WmsEmailAddresseeEntitys=EmailSendDao.queryOverDueMessage ();
        try{
            for(WmsEmailAddresseeEntity wmsEmailAddresseeEntity:WmsEmailAddresseeEntitys) {
                //收件人
                String[] ttAddr = {wmsEmailAddresseeEntity.getAddressee()};
                String noticeType = wmsEmailAddresseeEntity.getNoticeType();
                if(noticeType.equals("4")){
                    //抄送
                    String[] ccAddr = {};
                    StringBuilder content = new StringBuilder("<html><head></head><body><h3>各位好，<h3>\n" );
                    content.append("<h3>以下物料即将过期，请计划同事确认是否有使用需求，如有使用需求请及时通知IQC进行复检，谢谢！<h3>\n");
                    content.append("<h3>【系统邮件请勿回复】</h3>\n");
                    content.append("<table border=\"1\" style=\"border:solid 1px #E8F2F9;font-size=14px;;font-size:18px;\">");
                    content.append("<tr style=\"background-color: #428BCA; color:#ffffff\"><th>工厂</th><th>仓库号</th><th>物料</th><th>物料描述</th><th>批次</th><th>数量</th><th>单位</th><th>供应商</th><th>供应商名称</th><th>到期日期</th></tr>");
                    for (WmsEmailMessageEntity wmsEmailMessageEntity:wmsEmailAddresseeEntity.getWmsEmailMessageEntity()) {
                        content.append("<tr>");
                        content.append("<td>" + wmsEmailMessageEntity.getWerks() + "</td>"); //工厂
                        content.append("<td>" + wmsEmailMessageEntity.getWhNumber() + "</td>"); //仓库号
                        content.append("<td>" + wmsEmailMessageEntity.getMatnr() + "</td>"); //物料
                        content.append("<td>" + wmsEmailMessageEntity.getMaktx() + "</td>"); //物料描述
                        content.append("<td>" + wmsEmailMessageEntity.getBatch() + "</td>"); //批次
                        content.append("<td>" + wmsEmailMessageEntity.getQty() + "</td>"); //数量
                        content.append("<td>" + wmsEmailMessageEntity.getUnit() + "</td>"); //单位
                        content.append("<td>" + wmsEmailMessageEntity.getLifnr() + "</td>"); //供应商
                        content.append("<td>" + wmsEmailMessageEntity.getLiktx() + "</td>"); //供应商名称
                        content.append("<td>" + wmsEmailMessageEntity.getEffectDate() + "</td>"); //收货日期
                        content.append("</tr>");
                    }
                    content.append("</table>");
                    content.append("</body></html>");
                    Mail mail = new Mail("待过期物料复检通知", ttAddr, ccAddr, content.toString());
                    MailUtils.mainSend(mail);
                }

            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
