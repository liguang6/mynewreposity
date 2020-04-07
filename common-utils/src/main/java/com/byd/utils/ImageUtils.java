package com.byd.utils;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;


public class ImageUtils {
	private static Logger logger = LoggerFactory.getLogger(ImageUtils.class);
	
	/**
     * 按照宽度还是高度进行压缩
     * @param w int 最大宽度
     * @param h int 最大高度
     * @param descFileName String 目标文件路径+名称
     */
    public static void resizeFix(MultipartFile  srcFile,String descFileName,int w, int h) throws IOException {  	
    	//读取图片
    			BufferedInputStream in = new BufferedInputStream(srcFile.getInputStream());
    			//字节流转图片对象
    			Image bi =ImageIO.read(in);
    			int width = bi.getWidth(null);    // 得到源图宽
    	        int height = bi.getHeight(null);  // 得到源图长
    			if (width / height > w / h) {
    				h = (int) (height * w / width);
    	        } else {
    	        	w = (int) (width * h / height);
    	        }
    	        // SCALE_SMOOTH 的缩略算法 生成缩略图片的平滑度的 优先级比速度高 生成的图片质量比较好 但速度慢
    	        BufferedImage image = new BufferedImage(w, h,BufferedImage.TYPE_INT_RGB ); 
    	        image.getGraphics().drawImage(bi, 0, 0, w, h, null); // 绘制缩小后的图
    	        if(descFileName!=null){
    	            File destFile = new File(descFileName);
    	    		// 判断目标文件是否存在
    	    		if (destFile.exists()) {
    	    			// 如果目标文件存在，并且允许覆盖
    	    			if (!FileUtils.delFile(descFileName)) {
	    					logger.debug("删除目标文件 " + descFileName + " 失败!");
	    					throw new IOException("删除目标文件 " + descFileName + " 失败!");
	    				}
    	    		}
    	    		if (!destFile.getParentFile().exists()) {
        				// 如果目标文件所在的目录不存在，则创建目录
        				logger.debug("目标文件所在的目录不存在，创建目录!");
        				// 创建目标文件所在的目录
        				if (!destFile.getParentFile().mkdirs()) {
        					logger.debug("创建目标文件所在的目录失败!");
        					throw new IOException("创建目标文件所在的目录失败!");
        				}
        			}
    	    		
    	            FileOutputStream out = new FileOutputStream(destFile); // 输出到文件流
    	            // 可以正常实现bmp、png、gif转jpg
    	           /* JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
    	            encoder.encode(image); // JPEG编码
    	            */    	            
    	            ImageIO.write(image, "jpeg", out);
    	            out.flush(); 
    	            out.close();
    	        }else{
    	            System.out.println("无效输出路径");
    	        }
    	
    }
   
}
