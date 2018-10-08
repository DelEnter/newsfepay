package com.ecpss.action;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import com.ecpss.model.shop.InternationalMerchant;
import com.ecpss.service.common.CommonService;

public class ExpressUserThread extends Thread {
	public volatile boolean exit = false;
	Logger logger = Logger.getLogger(ExpressUserThread.class.getName());	
	
	private CommonService commonService;
	private InternationalMerchant mer;
	
	private String pt_code;//ƽ̨��ʶ
	private String customercode;//ƽ̨�̻���
	private String address;//	��ַ
	private String true_name;//��ʵ����
	private String phone;//�绰
	private String postUrl;
		
	int i=0;		
	
	public ExpressUserThread(CommonService commonService,InternationalMerchant mer,String pt_code, String customercode,
			String address, String true_name, String phone, String postUrl) {
		super();
		this.commonService = commonService;
		this.mer = mer;
		this.pt_code = pt_code;
		this.customercode = customercode;
		this.address = address;
		this.true_name = true_name;
		this.phone = phone;
		this.postUrl = postUrl;
	}
	
	public void run() {
		 while (!exit){
			 HttpClient client = new HttpClient(); 
			 client.setConnectionTimeout((int)20*1000);
			 client.setTimeout((int)20*1000);
			 PostMethod method = new UTF8PostMethod(postUrl); 
			 method.setRequestHeader("authenticate", "123456");
			 i++;
			 if(i<=10){
				try {
					 logger.info("ͬ���̻���Ϣ����ȴ�������");
					 Thread.sleep(6*1000);
					 logger.info("��"+i+"��ͬ���̻���Ϣ��ʼ������");
		             NameValuePair[] query_data = new NameValuePair[5];
		             query_data[0]=new NameValuePair("pt_code",pt_code);		             
		             query_data[1]=new NameValuePair("customercode",customercode);
		             query_data[2]=new NameValuePair("address",address);
		             query_data[3]=new NameValuePair("phone",phone);
		             query_data[4]=new NameValuePair("true_name",true_name);
		             method.setRequestBody(query_data);
		             client.executeMethod(method);
		           
		             String content = method.getResponseBodyAsString();
		             
		             JSONObject json=JSONObject.fromObject(content);

		             if("0".equals(json.get("status"))){
		            	 logger.info("********���ע���û�״̬************,����̻���"+json.getJSONObject("data").getString("customercode")+"   ͬ���̻���Ϣ״̬��" +json.get("status")+"   ����û�����" +json.getJSONObject("data").getString("username")+"   ������룺" +json.getJSONObject("data").getString("pwd"));
		            	 logger.info("ͬ���̻���Ϣ�ɹ�����"+i+"��");
		            	 exit=true;
		             }else{
		            	 logger.info("ͬ���̻���Ϣ״̬��" +json.get("status")+"   ����û�����" +json.get("message"));
		             }
		           
		            mer.setExpmerchantno(Long.parseLong(json.getJSONObject("data").getString("customercode")));			            
		     		mer.setExpuername(json.getJSONObject("data").getString("username"));
		     		mer.setExppassword(json.getJSONObject("data").getString("pwd"));
		     		mer.setExpopenstatus("1");
		     		commonService.update(mer);
		     		
		             method.releaseConnection();
				} catch (Exception e) {
					// TODO: handle exception					
					e.printStackTrace();
					method.releaseConnection();
				}
			 }else{
				 exit=true;
				 logger.info("��10��ͬ���̻���Ϣʧ�ܣ�");
			 }
		}
		 
		
	}
	public static class UTF8PostMethod extends PostMethod{     
	    public UTF8PostMethod(String url){     
	    super(url);     
	    }     
	    @Override     
	    public String getRequestCharSet() {     
	        //return super.getRequestCharSet();     
	        return "UTF-8";     
	    }  
	}

}
