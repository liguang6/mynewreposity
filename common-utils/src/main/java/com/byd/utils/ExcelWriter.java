package com.byd.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.baomidou.mybatisplus.toolkit.CollectionUtils;


public class ExcelWriter {

	/**
	 * 写入数据到Excel文件
	 * @param rows 表格内容
	 * @param titles 标题
	 * @param outputStream 输出流
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public static void writeToExcel(List<String[]> rows,String[] titles,OutputStream outputstream) throws IOException{
		Workbook workbook = null;
		if(rows.size()<10000) {
			workbook = new XSSFWorkbook();
		}else {
			workbook = new SXSSFWorkbook();
		}	
		
	    Sheet sheet = workbook.createSheet("default");
	    
	    //创建一个标题的字体
	    Font font = workbook.createFont();
	    font.setColor(IndexedColors.BROWN.getIndex());//color
	    font.setFontHeightInPoints((short)12);//font size
	    CellStyle cellstyle =  workbook.createCellStyle();
	    cellstyle.setFont(font);
	    
	    //写入标题
	    Row titleRow = sheet.createRow(0);
	    for(int i=0;i<titles.length;i++){
	    	Cell titleCell = titleRow.createCell(i);
	    	titleCell.setCellValue(titles[i]);
	    	titleCell.setCellStyle(cellstyle);
	    }
	    
	    //写入表格内容,从第二行开始写入
	    for(int i=0;i< rows.size();i++){
	    	Row dataRow = sheet.createRow(i+1);
	    	for(int j=0;j<rows.get(i).length;j++){
	    		dataRow.createCell(j).setCellValue(rows.get(i)[j]);
	    	}
	    }
	    
	    workbook.write(outputstream);
	}
	
	public static <T> void writeRecordToFile(OutputStream outputstream,List<T> records,Map<String,String> fieldMap) throws Exception{
		if(CollectionUtils.isEmpty(records) || fieldMap.isEmpty()){
			throw new IllegalArgumentException();
		}
		List<String[]> rows = new ArrayList<String[]>();
		Class<? extends Object> clazz = records.get(0).getClass();
		Field[] fields =  clazz.getDeclaredFields();
		String[] titles = new String[fieldMap.size()];
		
		//获取标题
		int index = 0;
		for(Entry<String,String> entry:fieldMap.entrySet()){
			titles[index] = entry.getValue();
			index ++;
		}	
		
		for(int e = 0;e < records.size();e++){
			//反射的方式生成数据-全部转换成字符串
			String[] row = new String[fieldMap.size()];
			int mapIndex = 0;
			for(Entry<String,String> entry:fieldMap.entrySet()){
				for(int i=0;i<fields.length;i++){
					Field field = fields[i];
					if(field.getName().equals(entry.getKey())){
						field.setAccessible(true);
						Object val = field.get(records.get(e));
						row[mapIndex] = String.valueOf(val);
						if(row[mapIndex].equals("null")){
							row[mapIndex] = "";
						}
						mapIndex ++;
						break;
					}			
				}
			}			
			rows.add(row);
		}
		writeToExcel(rows, titles,outputstream);
	}
	
	public static <T> void writeRecordToFile(OutputStream outputstream,List<T> records,List<String> fieldList) throws Exception{
//		if(CollectionUtils.isEmpty(records) || fieldList.isEmpty()){
//			throw new IllegalArgumentException();
//		}
		List<String[]> rows = new ArrayList<String[]>();
		String[] titles = new String[fieldList.size()];
		Field[] fields =new Field[0];
		// 默认records存放的Map对象
		boolean isMap=true;
		if(!CollectionUtils.isEmpty(records)) {
			if(!(records.get(0) instanceof Map)) {
				isMap=false;
			}
			Class<? extends Object> clazz = records.get(0).getClass();
			fields =clazz.getDeclaredFields();
		}
		//获取标题
		int index = 0;
		for(String entry:fieldList){
			titles[index] = entry.split("-")[1];
			index ++;
		}	
		for(int m = 0;m < records.size();m++){
			/*********start:将Map对象存放的key-value添加到Cell*********/
			if(isMap) {
				Map<String,Object> record=(Map<String, Object>) records.get(m);
				String[] row = new String[fieldList.size()];
				int i = 0;
				for(String entry:fieldList){		
					Object val =record.get(entry.split("-")[0]);
					row[i] = String.valueOf(val);
					if(row[i].equals("null")){
						row[i] = "";
					}
					i ++;	
				}	
				rows.add(row);
			}
			/******end:将Map对象存放的key-value添加到Cell*********/		
			/******start:将实体对象setting的值添加到Cell*********/
			if(!isMap){
				//反射的方式生成数据-全部转换成字符串
				String[] row = new String[fieldList.size()];
				int mapIndex = 0;
				for(String entry:fieldList){
					for(int i=0;i<fields.length;i++){
						Field field = fields[i];
						if(field.getName().equals(entry.split("-")[0])){
							field.setAccessible(true);
							Object val = field.get(records.get(m));
							row[mapIndex] = String.valueOf(val);
							if(row[mapIndex].equals("null")){
								row[mapIndex] = "";
							}
							mapIndex ++;
							break;
						}			
					}
				}			
				rows.add(row);
			}
			/******end:将实体对象setting的值添加到Cell*********/
		}
		writeToExcel(rows, titles,outputstream);
	}
	//生成返回的字节流响应
	public static <T> ResponseEntity<byte[]>  generateBytesResponse(List<T> entitys,Map<String,String> filedTitleMap) throws Exception{
			HttpHeaders header = new HttpHeaders();
			String filepath ="export-excel-"+ DateUtils.format(new Date(),"yyyyMMddHHmmss")+".xlsx";
			header.setContentDispositionFormData("attachment", filepath);
			header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			File tmpExcel = new File(filepath);
			try {
				tmpExcel.createNewFile();
				// write records to file --
				OutputStream outputstream = new FileOutputStream(tmpExcel);
				writeRecordToFile(outputstream,entitys,filedTitleMap);		
				outputstream.close();//写完后,关闭输出流
				ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(
						FileUtils.readFileToByteArray(tmpExcel), header,
						HttpStatus.CREATED);
				
				return responseEntity;
			} catch (IOException e) {
				FileUtils.deleteQuietly(tmpExcel);//抛出异常时，先删除临时文件
				throw e;// 重新抛出异
			} finally {
				FileUtils.deleteQuietly(tmpExcel);// 删除临时文件
			}
		}
	
}
