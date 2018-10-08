package com.ecpss.util;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

/**
 * ��DownloadUtils.java��ʵ�����������ط���
 * @author xurong Aug 26, 2008 4:24:53 PM
 */
public class DownloadUtils {
	/**
	 * ���response���������������
	 * @param fileName ���ؿ���ʾ���ļ���
	 * @return
	 */
	public static OutputStream getResponseOutput(String fileName){
		 HttpServletResponse oResponse = ServletActionContext.getResponse();
		 // Set the content type
         oResponse.setContentType("application/x-msdownload");
         try {
			//Set the content-disposition
			 oResponse.addHeader("Content-disposition", "attachment;filename="+new String(fileName.getBytes("GBK"),"iso-8859-1"));
			 //// Get the outputstream
			 return oResponse.getOutputStream();
		}catch (Exception e) {
			new RuntimeException(e.getMessage(),e);
		}
		return null;
	}
	/**
	 * �ر�response�����
	 */
	public static void closeResponseOutput(){
		HttpServletResponse oResponse = ServletActionContext.getResponse();
		try {
			OutputStream os = oResponse.getOutputStream();
			os.flush();
			os.close();
		} catch (IOException e) {
			new RuntimeException(e.getMessage(),e);
		}
	}
}
