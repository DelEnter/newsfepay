package com.ecpss.action.filemanager;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;

import com.ecpss.action.BaseAction;
import com.ecpss.model.FileManager;

public class FileDownloadAction extends BaseAction{
	private String inputPath; // ָ��Ҫ�����ص��ļ�·�� 
	private InputStream inputStream;
	private Long fid;
	private String fileName;
	
	public String downLoad() throws Exception{
		 //System.out.println("�ļ���id��:++"+fid);
		 FileManager fm = (FileManager) this.commonService.load(FileManager.class, fid);
		 InputStream stream = FileUpLoadAction.class.getClassLoader().getResourceAsStream("/ecpss.properties");
		 Properties p = new Properties();
		 p.load(stream);
		 inputPath=p.getProperty("upload_dir")+"/"+fm.getFileroute();
		 String orgName = FilenameUtils.getBaseName(fm.getFilename())+"."+FilenameUtils.getExtension(fm.getFileroute());
		 fileName = new String(orgName.getBytes("GBK"),"iso-8859-1");
	     return SUCCESS;
	 }
	 public InputStream getInputStream() throws Exception {
		 inputStream = new FileInputStream(inputPath);
		 // ͨ�� ServletContext��Ҳ����application ����ȡ���� 
		 return inputStream ; 
	 }
	public String getInputPath() {
		return inputPath;
	}
	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	public Long getFid() {
		return fid;
	}
	public void setFid(Long fid) {
		this.fid = fid;
	} 
	 
}
