package com.ecpss.action.pay;

import java.text.DecimalFormat;
import java.util.List;

import com.ecpss.action.BaseAction;
import com.ecpss.action.pay.dcc.DCCMessage;
import com.ecpss.action.pay.dcc.DccUtil;
import com.ecpss.model.channel.InternationalChannels;
import com.ecpss.model.payment.InternationalCardholdersInfo;
import com.ecpss.model.payment.InternationalTradeinfo;
import com.ecpss.model.shop.InternationalMerchant;
import com.ecpss.model.shop.InternationalTerminalManager;
import com.ecpss.util.MD5;

public class PayByDCCAction extends BaseAction {
    private String  amount_Transaction_Foreign;
    private String  conversion_Rate;//����
    private String currency_Code_Foreign;
	private String tradeType;   //�������� 
	private String rorderno;//������ˮ��
	private String MD5info;
	private String MD5key;	
	private String md5Value;   //֧�����ض��̻���Ϣ���м���	
	private int responseCode; 	//���ز���  	
	private String message;
	
	private String ReturnURL;
	private String merchantOrderNo;
	private String tradeMoneyType;
	

	private Double ordercount;   //�������	
	
	private String checktrade;


	public String paybydcc(){
		try{
		List listtrade=this.commonService.list(" select t from InternationalTradeinfo t where  t.orderNo='"+this.rorderno+"'");
		List listcarinfo=this.commonService.list(" select f from InternationalTradeinfo t, InternationalCardholdersInfo f where t.id=f.tradeId and t.orderNo='"+this.rorderno+"'");

		InternationalTradeinfo trade=new InternationalTradeinfo();
		InternationalCardholdersInfo card=new InternationalCardholdersInfo();
		InternationalChannels chnal= new InternationalChannels();
		MD5 md5 = new MD5();
	    
		if(listtrade.size()==1){
			trade=(InternationalTradeinfo)listtrade.get(0);	
		}if(listcarinfo.size()==1){
			card=(InternationalCardholdersInfo)listcarinfo.get(0);
		}
		InternationalMerchant im=(InternationalMerchant)this.commonService.load(InternationalMerchant.class, trade.getMerchantId());	
		//ת���ɱ�׼���
		ordercount = trade.getTradeAmount();

		DecimalFormat decimalFormat = new DecimalFormat("##############0.00");
		String ordercountValue = decimalFormat.format(ordercount);	
		
		this.ReturnURL=trade.getReturnUrl();
		this.merchantOrderNo=trade.getMerchantOrderNo();
		this.tradeMoneyType=trade.getMoneyType()+"";
		this.MD5key=im.getMd5key();
		
	if(this.checktrade.equals("1")){
		this.message="Payment Declined!";	
	    this.responseCode=29;
	    this.commonService.deleteBySql("update  international_tradeinfo t  set t.tradestate='0'||substr(t.tradestate,2,(length(t.tradestate)-1)),t.remark='"+message+"' where t.id='"+trade.getId()+"'");
		MD5info = trade.getMerchantOrderNo() + trade.getMoneyType() + ordercountValue + responseCode + MD5key;
		md5Value = md5.getMD5ofStr(MD5info);	
		return SUCCESS;
	}	
	List<InternationalChannels> ic=this.commonService.list(" select t from InternationalChannels t,InternationalMerchantChannels f where t.id=f.channelId and f.id='"+trade.getTradeChannel()+"'"); 
	if(ic.size()>0){
		chnal=ic.get(0);	
		}	
    List<InternationalTerminalManager>  timlist=this.commonService.list(" from InternationalTerminalManager t where t.terminalNo='"+trade.getVIPTerminalNo()+"'");
    InternationalTerminalManager tim=timlist.get(0);
    if(tradeType.equals("YY")){		
    	//����

     	 DCCMessage msg2=new DCCMessage();
   	 msg2.setMessageType("0200");//����
   	 msg2.setPrimary_Account_Number(card.getCardNo());//�˺�2
   	 msg2.setProcessing_Code("000000");//�������3
   	 msg2.setAmount_Transaction_Local(this.buzero(trade.getRmbAmount()+""));//4 ���ؽ��׽��
   	 msg2.setAmount_Transaction_Foreign(this.amount_Transaction_Foreign);//5  0810
   	 msg2.setConversion_Rate(this.conversion_Rate);//9    0810
   	 msg2.setSYSTEMS_TRACE_AUDIT_NUMBER(trade.getOrderNo().substring(trade.getOrderNo().length()-6,trade.getOrderNo().length()));//11  ������ˮ��
   	 msg2.setDATE_EXPIRATION(card.getExpiryDate());//14   ��Ч��
   	 msg2.setPOINT_OF_SERVICE_ENTRY_CODE("0012");//22 POS����ģʽ
   	 msg2.setNETWORK_INTL_IDENTIFIER("0017");//24  �յ��̻���
   	 msg2.setPOS_CONDITION_CODE("00");//25 �̻�����
//   	 msg2.setRETRIEVAL_REFERENCE_NUMBER("");//37
   	 msg2.setCARD_ACCEPTOR_TERMINAL_ID(trade.getVIPTerminalNo());//41  �̻��ն˺�
   	 msg2.setCARD_ACCEPTOR_ID_CODE(tim.getMerchantNo());//42 �̻���� 
   	 msg2.setCurrency_Code_Foreign("970");//49 ���Ҵ���-----0810
   	 msg2.setCVV2_OR_CVC2(card.getCvv2());//cv2
   	 msg2.setInvoice_Number(trade.getOrderNo().substring(trade.getOrderNo().length()-6,trade.getOrderNo().length()));//62	
	 DccUtil dc=new DccUtil();
	 dc.setDccMessage(msg2);
	 try{
	 msg2=dc.getDccMessage();
	 }
	 catch(Exception ex){
    	 //���ݵĲ���    
		    responseCode=19;
			message = "Your payment is being processed";
			MD5info = trade.getMerchantOrderNo() + trade.getMoneyType() + ordercountValue + responseCode + MD5key;
			md5Value = md5.getMD5ofStr(MD5info);  		
			this.commonService.deleteBySql("update  international_tradeinfo t  set t.tradestate='2'||substr(t.tradestate,2,(length(t.tradestate)-1)) where t.id='"+trade.getId()+"'");
	        return SUCCESS;     		 		 
	 }
	 if(msg2.getRESPONSE_CODE().equals("00")){
	this.message="Payment Success!";	 
    this.responseCode=88;
    this.commonService.deleteBySql("update  international_tradeinfo t  set t.tradestate='1'||substr(t.tradestate,2,(length(t.tradestate)-1)),t.remark='"+message+"' where t.id='"+trade.getId()+"'");
	
	MD5info = trade.getMerchantOrderNo() + trade.getMoneyType() + ordercountValue + responseCode + MD5key;
	md5Value = md5.getMD5ofStr(MD5info);	
	return SUCCESS;
	 }
	 else{
		    this.responseCode=0;
			this.message="Payment Declined!";
		    this.commonService.deleteBySql("update  international_tradeinfo t  set t.tradestate='0'||substr(t.tradestate,2,(length(t.tradestate)-1)),t.remark='"+message+"' where t.id='"+trade.getId()+"'");
			MD5info = trade.getMerchantOrderNo() + trade.getMoneyType() + ordercountValue + responseCode + MD5key;
			md5Value = md5.getMD5ofStr(MD5info);	
			return SUCCESS;
	 }
   	 
    }
    if(tradeType.equals("YX")){
     	 DCCMessage dcc3=new DCCMessage();
    	 dcc3.setMessageType("0200");//����
    	 dcc3.setPrimary_Account_Number(card.getCardNo());//�˺�2
    	 dcc3.setProcessing_Code("000000");//�������3
    	 dcc3.setAmount_Transaction_Local(this.buzero(trade.getRmbAmount()+""));//4 ���ؽ��׽��
    	 dcc3.setSYSTEMS_TRACE_AUDIT_NUMBER("000006");//11  ������ˮ��
    	 dcc3.setDATE_EXPIRATION(card.getExpiryDate());//14   ��Ч��
    	 dcc3.setPOINT_OF_SERVICE_ENTRY_CODE("0012");//22 POS����ģʽ
    	 dcc3.setNETWORK_INTL_IDENTIFIER("0017");//24  �յ��̻���
         dcc3.setCARD_ACCEPTOR_TERMINAL_ID(trade.getVIPTerminalNo());//41  �̻��ն˺�
    	 dcc3.setCARD_ACCEPTOR_ID_CODE(tim.getMerchantNo());//42 �̻���� 
    	 dcc3.setCVV2_OR_CVC2(card.getCvv2());//cv2 61
    	 dcc3.setInvoice_Number("000006");//62	
    	 DccUtil dc=new DccUtil();
    	 dc.setDccMessage(dcc3);
    	 try{
    	 dcc3=dc.getDccMessage();  
    	 }
    	 catch(Exception ex){
        	 //���ݵĲ���    
 		    responseCode=19;
 			message = "Your payment is being processed";
 			MD5info = trade.getMerchantOrderNo() + trade.getMoneyType() + ordercountValue + responseCode + MD5key;
 			md5Value = md5.getMD5ofStr(MD5info);  		
 			this.commonService.deleteBySql("update  international_tradeinfo t  set t.tradestate='2'||substr(t.tradestate,2,(length(t.tradestate)-1)) where t.id='"+trade.getId()+"'");
 	        return SUCCESS;    		 
    	 }
    	 if(dcc3.getRESPONSE_CODE().equals("00")){
    			this.message="Payment Success!";	 
    		    this.responseCode=88;
    		    this.commonService.deleteBySql("update  international_tradeinfo t  set t.tradestate='1'||substr(t.tradestate,2,(length(t.tradestate)-1)),t.remark='"+message+"' where t.id='"+trade.getId()+"'");
    			
    			MD5info = trade.getMerchantOrderNo() + trade.getMoneyType() + ordercountValue + responseCode + MD5key;
    			md5Value = md5.getMD5ofStr(MD5info);	
    			return SUCCESS;	
    			 }
    			 else{
    					this.message="Payment Declined!";	
    				    this.responseCode=0;
    				    this.commonService.deleteBySql("update  international_tradeinfo t  set t.tradestate='0'||substr(t.tradestate,2,(length(t.tradestate)-1)),t.remark='"+message+"' where t.id='"+trade.getId()+"'");
    					MD5info = trade.getMerchantOrderNo() + trade.getMoneyType() + ordercountValue + responseCode + MD5key;
    					md5Value = md5.getMD5ofStr(MD5info);		 
    					return SUCCESS;
    			 }   	
    }

   	return this.SUCCESS;
		}catch(Exception ex){
			return ERROR;
		}
	}


	public String getAmount_Transaction_Foreign() {
		return amount_Transaction_Foreign;
	}


	public void setAmount_Transaction_Foreign(String amount_Transaction_Foreign) {
		this.amount_Transaction_Foreign = amount_Transaction_Foreign;
	}


	public String getConversion_Rate() {
		return conversion_Rate;
	}


	public void setConversion_Rate(String conversion_Rate) {
		this.conversion_Rate = conversion_Rate;
	}


	public String getCurrency_Code_Foreign() {
		return currency_Code_Foreign;
	}


	public void setCurrency_Code_Foreign(String currency_Code_Foreign) {
		this.currency_Code_Foreign = currency_Code_Foreign;
	}


	public String getTradeType() {
		return tradeType;
	}


	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}


	public String getRorderno() {
		return rorderno;
	}


	public void setRorderno(String rorderno) {
		this.rorderno = rorderno;
	}


	public String getMd5Value() {
		return md5Value;
	}


	public void setMd5Value(String md5Value) {
		this.md5Value = md5Value;
	}


	public int getResponseCode() {
		return responseCode;
	}


	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	public String getReturnURL() {
		return ReturnURL;
	}


	public void setReturnURL(String returnURL) {
		ReturnURL = returnURL;
	}


	public String getMerchantOrderNo() {
		return merchantOrderNo;
	}


	public void setMerchantOrderNo(String merchantOrderNo) {
		this.merchantOrderNo = merchantOrderNo;
	}


	public String getTradeMoneyType() {
		return tradeMoneyType;
	}


	public void setTradeMoneyType(String tradeMoneyType) {
		this.tradeMoneyType = tradeMoneyType;
	}


	public Double getOrdercount() {
		return ordercount;
	}


	public void setOrdercount(Double ordercount) {
		this.ordercount = ordercount;
	}
	
	public String getChecktrade() {
		return checktrade;
	}


	public void setChecktrade(String checktrade) {
		this.checktrade = checktrade;
	}
	public String buzero(String len){
		String tem="";
		if(len.indexOf(".")==-1){
			int lenght=len.length();
           for(int i=0;i<10-lenght;i++){
        	   tem="0"+tem;
           }
           tem=tem+"00";
		}else{
		    tem=len.substring(0,len.indexOf("."));
			String tem2=len.substring(len.indexOf(".")+1,len.length());
			tem=tem+tem2;
			int lenght=tem.length();
	        for(int i=0;i<12-lenght;i++){
	     	   tem="0"+tem;
	        }				
		}
		return tem;
	}
}
